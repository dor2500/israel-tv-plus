/**
 * Israel TV+ loader.
 *
 * Live updates: the installed apps (LG webOS, Samsung, Android, Windows, Mac/Linux) carry the whole app, but when
 * the published site (GitHub Pages) has a newer build, they load the screen, code and channel list from there,
 * so a new release reaches every TV without reinstalling. app.js checks version.json, pre-downloads the new
 * build and records it in localStorage ("itv_live"); the next start loads it from here.
 *
 * Safety: a build that fails to load or never finishes starting is marked bad and the installed copy is used.
 * The https site itself always loads its own files (they are the published build).
 */
(function () {
  'use strict';
  var REMOTE = 'https://dor2500.github.io/israel-tv-plus/';
  var BUILD = window.ITV_BUILD || { version: '0', code: 0, shell: 0 };
  var isSite = location.protocol === 'https:' || location.protocol === 'http:';
  var store = window.localStorage;
  var live = null;

  function get(k) { try { return store.getItem(k); } catch (e) { return null; } }
  function set(k, v) { try { if (v == null) store.removeItem(k); else store.setItem(k, v); } catch (e) {} }

  if (!isSite) {
    try { live = JSON.parse(get('itv_live') || 'null'); } catch (e) { live = null; }
    var settings = {};
    try { settings = JSON.parse(get('itv_settings') || '{}') || {}; } catch (e) {}
    var forcedLocal = false;
    try { forcedLocal = sessionStorage.getItem('itv_local') === '1'; } catch (e) {}
    if (live && get('itv_pending') === String(live.code)) set('itv_bad', String(live.code)); // last start never finished
    if (!live || !(live.code > BUILD.code) || settings.liveUpdates === false || forcedLocal ||
        (live.shell || 0) > (BUILD.shell || 0) || get('itv_bad') === String(live.code)) {
      live = null;
    }
  }

  var base = live ? REMOTE : '';
  var q = live ? '?v=' + live.code : '';
  window.ITV_BASE = base;       // where relative assets (channel logos) of this build live
  window.ITV_LIVE = live;       // the live-update build in use, or null for the installed one
  if (live) set('itv_pending', String(live.code));

  function fallback() {
    // A file of the live build could not load (offline, bad build): start again from the installed copy.
    if (!live) return;
    set('itv_bad', String(live.code));
    try { sessionStorage.setItem('itv_local', '1'); } catch (e) {}
    location.reload();
  }

  var css = document.createElement('link');
  css.rel = 'stylesheet';
  css.href = base + 'style.css' + q;
  document.head.appendChild(css);

  var files = ['ui.js', 'channels.js', 'dub.js', 'app.js'];
  var i = 0;
  (function next() {
    if (i >= files.length) return;
    var s = document.createElement('script');
    s.src = base + files[i++] + q;
    s.onload = next;
    s.onerror = fallback;
    document.body.appendChild(s);
  })();

  // The installable web app (PWA) on the https site only.
  if (location.protocol === 'https:' && 'serviceWorker' in navigator) {
    navigator.serviceWorker.register('sw.js').catch(function () {});
  }
})();
