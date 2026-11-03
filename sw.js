// Israel TV+ service worker: makes the site installable and opens it offline-fast.
// Same-origin files: network first (updates show up at once), cache as fallback. Streams are never touched.
const CACHE = 'itv-v4';
const SHELL = ['./', './index.html', './version.js', './boot.js', './ui.js', './fonts.css', './style.css', './app.js',
  './dub.js', './tvguide.js', './gdrive.js', './channels.js', './assets/hls.min.js', './assets/qrcode.js',
  './manifest.webmanifest', './icon.png', './assets/app/icon-192.png', './assets/app/icon-512.png'];

self.addEventListener('install', (e) => {
  e.waitUntil(caches.open(CACHE).then(c => c.addAll(SHELL)).then(() => self.skipWaiting()));
});

self.addEventListener('activate', (e) => {
  e.waitUntil(caches.keys()
    .then(keys => Promise.all(keys.filter(k => k !== CACHE).map(k => caches.delete(k))))
    .then(() => self.clients.claim()));
});

self.addEventListener('fetch', (e) => {
  const req = e.request;
  const url = new URL(req.url);
  if (req.method !== 'GET' || url.origin !== location.origin || url.pathname.endsWith('version.json')) return;
  e.respondWith(fetch(req).then(res => {
    if (res.ok) {
      const copy = res.clone();
      caches.open(CACHE).then(c => c.put(req, copy));
    }
    return res;
  }).catch(() => caches.match(req).then(r => {
    if (r) return r;
    if (req.mode === 'navigate' || req.destination === 'document') return caches.match('./index.html');
    return new Response('', { status: 504, statusText: 'Offline' });
  })));
});
