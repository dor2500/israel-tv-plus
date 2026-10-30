/**
 * Israel TV+ HTTPS relay (Cloudflare Worker).
 *
 * Browsers refuse http:// video on an https:// site, and many channels (stream.mcquack.net, servers with a bare IP
 * address) have no https at all. The web app asks this worker for  /?u=<the http url>  and gets the same bytes over
 * https, with CORS. HLS playlists are rewritten so every variant playlist, key and segment also goes through here.
 *
 * Also used for https streams whose server sends no CORS header (the browser can't read them directly).
 * Not an open proxy: only hosts that appear in the published channel list (relay-hosts.json on the site,
 * written by tools/make_sources.py) are relayed, and only GET / HEAD.
 */
const HOSTS_URL = 'https://dor2500.github.io/israel-tv-plus/relay-hosts.json';
const CORS = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Methods': 'GET, HEAD, OPTIONS',
  'Access-Control-Allow-Headers': 'Range, Content-Type',
  'Access-Control-Expose-Headers': 'Content-Length, Content-Range, Content-Type'
};
let hosts = null, hostsAt = 0;

async function allowedHosts(env) {
  if (hosts && Date.now() - hostsAt < 3600e3) return hosts;
  try {
    const r = await fetch((env && env.HOSTS_URL) || HOSTS_URL, { cf: { cacheTtl: 3600 } });
    if (r.ok) { hosts = new Set(await r.json()); hostsAt = Date.now(); }
  } catch (e) { /* keep the previous list */ }
  return hosts || new Set();
}

// links the relay writes into a playlist carry a signature (HMAC with the RELAY_KEY secret), so segments on other
// servers (CDNs, redirects) pass, but nobody can use the relay for arbitrary addresses
async function sign(env, u) {
  // no built-in fallback key: without the RELAY_KEY secret nothing is signed (and no signature is ever accepted)
  if (!env.RELAY_KEY) return '';
  const k = await crypto.subtle.importKey('raw', new TextEncoder().encode(env.RELAY_KEY), { name: 'HMAC', hash: 'SHA-256' }, false, ['sign']);
  const mac = new Uint8Array(await crypto.subtle.sign('HMAC', k, new TextEncoder().encode(u)));
  return btoa(String.fromCharCode.apply(null, mac.slice(0, 16))).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

function sameSig(a, b) {
  if (!a || !b || a.length !== b.length) return false;
  let d = 0;
  for (let i = 0; i < a.length; i++) d |= a.charCodeAt(i) ^ b.charCodeAt(i);
  return d === 0;
}

async function relayUrl(env, origin, abs) {
  return origin + '/?u=' + encodeURIComponent(abs) + '&t=' + (await sign(env, abs));
}

/** Every URI in an HLS playlist (lines and URI="..." attributes) -> absolute upstream URL -> through the relay. */
async function rewritePlaylist(env, text, base, origin) {
  const abs = (u) => { try { return new URL(u, base).href; } catch (e) { return u; } };
  const out = [];
  for (const line of text.split(/\r?\n/)) {
    const t = line.trim();
    if (!t) { out.push(line); continue; }
    if (t.startsWith('#')) {
      const m = /URI="([^"]+)"/.exec(line);
      out.push(m ? line.replace(m[0], 'URI="' + (await relayUrl(env, origin, abs(m[1]))) + '"') : line);
    } else out.push(await relayUrl(env, origin, abs(t)));
  }
  return out.join('\n');
}

export default {
  async fetch(req, env) {
    if (req.method === 'OPTIONS') return new Response(null, { headers: CORS });
    if (req.method !== 'GET' && req.method !== 'HEAD') return new Response('method', { status: 405, headers: CORS });
    const me = new URL(req.url);
    const target = me.searchParams.get('u');
    if (!target) return new Response('Israel TV+ relay', { headers: CORS });
    let url;
    try { url = new URL(target); } catch (e) { return new Response('bad url', { status: 400, headers: CORS }); }
    if (url.protocol !== 'http:' && url.protocol !== 'https:') return new Response('bad scheme', { status: 400, headers: CORS });
    const t = me.searchParams.get('t');
    const ok = (await allowedHosts(env)).has(url.host) || sameSig(t, await sign(env, url.href));
    if (!ok) return new Response('host not in the channel list', { status: 403, headers: CORS });

    const headers = { 'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Safari/537.36' };
    const range = req.headers.get('Range');
    if (range) headers.Range = range;
    // redirects are followed by hand: a few hops, web schemes only, no credentials in the address
    let up, finalUrl = url.href;
    try {
      for (let hop = 0; ; hop++) {
        up = await fetch(finalUrl, { method: req.method, headers, redirect: 'manual' });
        if (up.status < 300 || up.status >= 400 || !up.headers.get('Location')) break;
        if (hop >= 5) return new Response('too many redirects', { status: 502, headers: CORS });
        const next = new URL(up.headers.get('Location'), finalUrl);
        if ((next.protocol !== 'http:' && next.protocol !== 'https:') || next.username || next.password) {
          return new Response('bad redirect', { status: 502, headers: CORS });
        }
        finalUrl = next.href;
      }
    } catch (e) {
      return new Response('upstream unreachable', { status: 502, headers: CORS });
    }
    const type = up.headers.get('Content-Type') || '';
    const out = new Headers(CORS);
    for (const h of ['Content-Type', 'Content-Length', 'Content-Range', 'Accept-Ranges', 'Cache-Control', 'Last-Modified', 'ETag']) {
      const v = up.headers.get(h);
      if (v) out.set(h, v);
    }
    const isPlaylist = /mpegurl/i.test(type) || /\.m3u8?($|\?)/i.test(url.pathname);
    if (isPlaylist && up.ok && req.method === 'GET') {
      const text = await up.text();
      out.delete('Content-Length');
      out.set('Content-Type', 'application/vnd.apple.mpegurl');
      out.set('Cache-Control', 'no-cache');
      // the final URL after redirects is the base for relative URIs
      return new Response(await rewritePlaylist(env, text, finalUrl, me.origin), { status: up.status, headers: out });
    }
    return new Response(up.body, { status: up.status, headers: out });
  }
};
