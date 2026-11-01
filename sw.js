// Israel TV+ service worker: makes the site installable and opens it offline-fast.
// Same-origin files: network first (updates show up at once), cache as fallback. Streams are never touched.
const CACHE = 'itv-v3';
const SHELL = ['./', './index.html', './version.js', './boot.js', './ui.js', './fonts.css', './style.css', './app.js',
  './dub.js', './channels.js', './assets/hls.min.js', './manifest.webmanifest', './icon.png',
  './assets/app/icon-192.png', './assets/app/icon-512.png'];

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
  }).catch(() => caches.match(req).then(r => r || caches.match('./index.html'))));
});
