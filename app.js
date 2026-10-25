/**
 * ISRAEL TV+ – the app (TV guide, player, settings, updates) for LG webOS, Samsung Tizen, Android / Android TV,
 * Windows, Mac / Linux and the web site. Hebrew live dubbing for foreign channels lives in dub.js.
 *
 * Written for old TV browsers too (webOS 4 = Chromium 53): no async/await, object spread, optional chaining,
 * CSS grid or flex gap. Remote navigation never measures the layout (fixed cards per row), focus moves only
 * toggle two classes, and the heavy parts (hero, channel logos) update after the key repeat settles.
 */
(function () {
  'use strict';

  window.CHANNELS_DATA = window.CHANNELS_DATA || window.ITV_CHANNELS || [];
  window.ITV_CHANNELS = window.CHANNELS_DATA;

  var REMOTE = 'https://dor2500.github.io/israel-tv-plus/';
  var RELEASES = 'https://github.com/dor2500/israel-tv-plus/releases/latest';
  var REPO = 'https://github.com/dor2500/israel-tv-plus';
  var BUILD = window.ITV_BUILD || { version: '0', code: 0, app: 0, shell: 0 };
  var LIVE = window.ITV_LIVE || null;
  var Dub = window.HebrewDub;
  var UA = navigator.userAgent;
  var IS_SITE = location.protocol === 'https:' || location.protocol === 'http:';

  // ================================================================== platform
  var PLATFORM = new URLSearchParams(location.search).get('platform') || (window.AndroidApp ? 'android'
    : (window.chrome && window.chrome.webview) ? 'windows'
    : (window.PalmSystem || window.PalmServiceBridge || /Web0S|webOS/i.test(UA)) ? 'webos'
    : (window.tizen || /Tizen/i.test(UA)) ? 'tizen'
    : /IsraelTVPlus\/\d+ Desktop/.test(UA) ? 'desktop'
    : location.protocol === 'https:' ? 'web' : 'local');
  var IS_WEB = PLATFORM === 'web';
  var IS_TV = PLATFORM === 'webos' || PLATFORM === 'tizen' || /AndroidTV/.test(UA) ||
    (PLATFORM === 'android' && window.AndroidApp && window.AndroidApp.isTv && window.AndroidApp.isTv());
  var TOUCH = !IS_TV && ('ontouchstart' in window) && window.matchMedia && matchMedia('(pointer: coarse)').matches;
  var PHONE = false;            // phone layout (Nuvio home + navigation bar), set by applyScale()
  var I = window.ITV_ICON || function () { return ''; };
  var NATIVE = window.AndroidApp || null;
  function native(fn) { try { return NATIVE && NATIVE[fn] ? NATIVE[fn].apply(NATIVE, [].slice.call(arguments, 1)) : undefined; } catch (e) { return undefined; } }
  var PLATFORM_NAMES = { webos: 'LG webOS', tizen: 'Samsung Tizen', android: IS_TV ? 'Android TV' : 'Android',
    windows: 'Windows', desktop: /Mac/.test(UA) ? 'macOS' : 'Linux', web: 'דפדפן (אתר)', local: 'דפדפן (מקומי)' };

  // ================================================================== settings
  var DEFAULTS = {
    theme: 'oled', accent: 'auto', uiScale: 1, cols: 0, hero: true, numbers: true, hints: true, perf: 'auto',
    startCat: 'ALL', autoplay: false, hideAppOnly: false, hiddenCats: [], hidden: [], showSource: true,
    pic: 'off', aspect: 'fit', sharpen: false, quality: 'auto', latency: 'stable', osdSec: 5, osdClock: true,
    retry: true, zap: 'category', upNext: false,
    remote: 'auto', keyRed: 'menu', keyGreen: 'aspect', keyYellow: 'fav', keyBlue: 'dub', longOk: 'menu', numWait: 1.2,
    saver: 2, kids: false, kidsPin: '', autoUpdate: true, liveUpdates: true, avatar: '1f60e',
    lastId: '', lastCat: 'ALL', seenColors: false
  };
  var S = loadSettings();

  function loadSettings() {
    var s = {};
    try { s = JSON.parse(localStorage.getItem('itv_settings') || '{}') || {}; } catch (e) { s = {}; }
    var out = {};
    Object.keys(DEFAULTS).forEach(function (k) { out[k] = s[k] !== undefined ? s[k] : DEFAULTS[k]; });
    // settings of the previous version
    try {
      if (s.theme === undefined && localStorage.getItem('israel_tv_theme') === 'light') out.theme = 'light';
    } catch (e) {}
    return out;
  }
  function saveSettings() { try { localStorage.setItem('itv_settings', JSON.stringify(S)); } catch (e) {} }
  function setSetting(k, v) { S[k] = v; saveSettings(); applySetting(k); }

  function lsGet(k, def) { try { var v = JSON.parse(localStorage.getItem(k)); return v == null ? def : v; } catch (e) { return def; } }
  function lsSet(k, v) { try { localStorage.setItem(k, JSON.stringify(v)); } catch (e) {} }

  // ================================================================== state
  var state = {
    all: [], list: [], cats: [], cat: 'ALL', query: '',
    favorites: lsGet('israel_tv_favorites', []), recents: lsGet('israel_tv_recents', []),
    zone: 'grid', gridIdx: 0, railIdx: 0, topIdx: 0,
    playing: null, playIdx: -1, isRadio: false, hls: null, retries: 0,
    overlay: null,            // 'player' | 'drawer' | 'menu' | 'settings' | 'dialog'
    stack: [],                // overlays under the current one
    drawerIdx: 0, menu: null, menuIdx: 0, dialog: null, dialogIdx: 0,
    setSection: 0, setIdx: 0, setZone: 'nav',
    digits: '', digitTimer: null, osdTimer: null, toastTimer: null, saverTimer: null, heroTimer: null,
    okTimer: null, okLong: false, okDown: false,
    device: { platform: PLATFORM_NAMES[PLATFORM] || PLATFORM },
    lowEnd: false, cols: 5, update: null, liveReady: null,
    tab: 'home', prevTab: 'home', openFrom: null   // phone: current tab, rect of the tapped poster (open motion)
  };
  var MAX_RECENTS = 12;
  var GRID_PAGE = 60;
  var cards = [];
  var rendered = 0;
  var el = {};

  // ================================================================== small helpers
  function $(id) { return document.getElementById(id); }
  function esc(s) {
    return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
  }
  function hueOf(id) { var h = 7; for (var i = 0; i < id.length; i++) h = (h * 31 + id.charCodeAt(i)) % 360; return h; }
  function clamp(v, a, b) { return Math.max(a, Math.min(b, v)); }
  function toggleClass(node, cls, on) { if (node) node.classList[on ? 'add' : 'remove'](cls); }
  function isApp(ch) { return ch && ch.kind === 'app'; }
  function isRadioCh(ch) { return ch && (ch.kind === 'radio' || ch.categoryEn === 'radio'); }
  // The web site can't play http streams (mixed content) or https ones without CORS: they go through the https relay
  // (relay/worker.js, a Cloudflare Worker) when one is configured (file relay.url → version.js); the apps play them directly.
  var RELAY = (new URLSearchParams(location.search).get('relay') || BUILD.relay || '').replace(/\/+$/, '');
  function needsRelay(ch) { return IS_WEB && !ch.webUrl && (ch.webOk === false || /^http:/i.test(ch.url)); }
  function streamUrl(ch) {
    if (IS_WEB && ch.webUrl) return ch.webUrl;
    if (RELAY && needsRelay(ch)) return RELAY + '/?u=' + encodeURIComponent(ch.url);
    return ch.url;
  }
  function appOnly(ch) { return IS_WEB && ch && !isApp(ch) && !RELAY && (ch.webOk === false || /^http:/i.test(streamUrl(ch))); }
  function isKidsCh(ch) { return /ילדים/.test(ch.category) || ch.id === 'radios_kids'; }
  function isFav(ch) { return ch && state.favorites.indexOf(ch.id) !== -1; }
  function foreign(ch) { return !!(Dub && ch && Dub.isForeign(ch)); }

  function monogramText(ch) {
    var latin = (ch.name.replace(/\(.*?\)/g, ' ').match(/[A-Za-z0-9][A-Za-z0-9!+&'.:-]*(?:\s+[A-Za-z0-9!+&'.:-]+)*/g) || [])
      .map(function (s) { return s.replace(/\b(HD|Live|TV)\b/g, '').trim(); })
      .filter(function (s) { return /[A-Za-z]{2,}/.test(s); })
      .sort(function (a, b) { return b.length - a.length; });
    if (latin.length) return latin[0].slice(0, 18);
    return ch.name.replace(/\bHD\b|\(.*?\)/g, '').trim().split(/\s+/).slice(0, 2).join(' ');
  }

  /** Logo with fallbacks: the local copy, then the published site (new channels of a live update), then a monogram. */
  function setLogo(img, mono, ch) {
    if (!img || !mono) return;
    var h = hueOf(ch.id);
    mono.textContent = monogramText(ch);
    mono.style.background = 'linear-gradient(135deg, hsl(' + h + ',70%,42%), hsl(' + ((h + 40) % 360) + ',75%,30%))';
    var urls = [];
    if (ch.logo) {
      if (/^https?:/i.test(ch.logo)) urls.push(ch.logo);
      else {
        urls.push((window.ITV_BASE || '') + ch.logo);
        if (!IS_SITE && !window.ITV_BASE) urls.push(REMOTE + ch.logo);
      }
    }
    var i = 0, box = img.parentNode;
    var loaded = function () { if (box && box.classList) box.classList.remove('loading'); };
    if (box && box.classList && urls.length) box.classList.add('loading');   // glass shimmer until the logo is in
    function showMono() { img.classList.add('hidden'); mono.classList.remove('hidden'); loaded(); }
    function tryNext() {
      if (i >= urls.length) { img.removeAttribute('src'); showMono(); return; }
      img.src = urls[i++];
    }
    img.onerror = tryNext;
    img.onload = function () { img.classList.remove('hidden'); mono.classList.add('hidden'); loaded(); };
    img.referrerPolicy = 'no-referrer';
    img.classList.remove('hidden');
    mono.classList.add('hidden');
    tryNext();
  }

  /** A soft wash in the channel's own colour behind its logo (Nuvio posters are never flat grey). */
  function tintOf(ch) {
    var h = hueOf(ch.id);
    return 'linear-gradient(135deg, hsla(' + h + ',65%,48%,.32), hsla(' + ((h + 40) % 360) + ',65%,40%,0) 72%)';
  }

  // Logos load only when their card comes near the screen (hundreds of channels, phones and TVs on slow Wi-Fi)
  var logoObserver = null;
  function lazyLogo(img, mono, ch) {
    if (!('IntersectionObserver' in window)) { setLogo(img, mono, ch); return; }
    if (!logoObserver) {
      logoObserver = new IntersectionObserver(function (entries) {
        entries.forEach(function (e) {
          if (!e.isIntersecting) return;
          var im = e.target;
          logoObserver.unobserve(im);
          if (im.__ch) { setLogo(im, im.__mono, im.__ch); im.__ch = null; im.__mono = null; }
        });
      }, { rootMargin: '400px 600px' });
    }
    img.__ch = ch;
    img.__mono = mono;
    if (ch.logo && img.parentNode) img.parentNode.classList.add('loading');
    var h = hueOf(ch.id);
    mono.textContent = monogramText(ch);
    mono.style.background = 'linear-gradient(135deg, hsl(' + h + ',70%,42%), hsl(' + ((h + 40) % 360) + ',75%,30%))';
    logoObserver.observe(img);
  }

  var CAT_SHORT = { 'ילדים ונוער (בעברית)': 'ילדים בעברית', 'מנויים ואפליקציות (TV Apps)': 'מנויים ואפליקציות' };
  function splitCat(name) {
    if (CAT_SHORT[name]) return CAT_SHORT[name];
    var m = name.match(/^(.*?)\s*\(([^)]*[A-Za-z][^)]*)\)\s*$/);
    var main = m ? m[1] : name;
    return main.replace(/\s*-\s*שידור חי\s*$/, '').replace(/\s*-\s*$/, '');
  }
  var CAT_ICONS = [[/אפליקציות|מנויים/, '📲'], [/רדיו/, '📻'], [/ישראל/, '📡'], [/ילדים/, '🧸'], [/סרטים|סדרות/, '🎬'],
    [/ספורט/, '⚽'], [/אוכל|בישול/, '🍳'], [/קומדיה/, '😂'], [/טיולים/, '✈️'], [/מדע/, '🔬'], [/כלכלה/, '📈'],
    [/מוזיקה/, '🎵'], [/לייף|בידור/, '🏖️'], [/דוקו|טבע/, '🌍'], [/חדשות|News/i, '📰'],
    [/מסורת|יהדות/, '🕍'], [/דת|אמונה/, '🙏'], [/בינלאומי|World/i, '🌐']];
  function catIcon(name) { for (var i = 0; i < CAT_ICONS.length; i++) if (CAT_ICONS[i][0].test(name)) return CAT_ICONS[i][1]; return '📺'; }
  var CAT_ORDER = ['ערוצי ישראל - שידור חי', 'ילדים ונוער (בעברית)', 'רדיו ישראלי (Israeli Radio)', 'מנויים ואפליקציות (TV Apps)',
    'ספורט (Sports)', 'חדשות מהעולם (News)', 'כלכלה ועסקים (Business)', 'סרטים וסדרות (Movies & Series)', 'קומדיה (Comedy)',
    'אוכל ובישול (Food)', 'ילדים מהעולם (Kids World)', 'מוזיקה (Music TV)', 'טבע ודוקו (Documentary & Nature)',
    'טיולים והרפתקאות (Travel)', 'מדע ולמידה (Science)', 'לייף סטייל ובידור', 'מסורת וקהילה', 'דת ואמונה (Religion)',
    'ערוצים בינלאומיים (World)'];

  var KIND_LABEL = { official: '✅ שידור רשמי', fast: '✅ ערוץ FAST חינמי (רשמי, עם פרסומות)', restream: '⚠️ ממסר של צד שלישי (לא השרת של הערוץ)' };
  function hostOf(url) { var m = /^[a-z]+:\/\/([^/:?#]+)/i.exec(url || ''); return m ? m[1] : ''; }
  function sourceOf(ch) {
    var s = ch.src || {};
    return { by: s.by || hostOf(ch.url), kind: s.kind || '', host: s.host || hostOf(ch.url), found: s.found || '', web: s.web || '' };
  }
  function sourceLine(ch) {
    if (isApp(ch)) return 'שירות מנויים – נפתח באפליקציה / באתר הרשמי';
    var s = sourceOf(ch);
    var k = s.kind === 'official' ? 'רשמי' : s.kind === 'fast' ? 'FAST רשמי' : s.kind === 'restream' ? 'ממסר צד שלישי' : '';
    return 'מקור: ' + s.by + (k ? ' · ' + k : '');
  }

  // ================================================================== start
  window.addEventListener('DOMContentLoaded', start);
  if (document.readyState !== 'loading') setTimeout(start, 0);
  var started = false;

  function start() {
    if (started) return;
    started = true;
    setupCrashLog();
    var root = $('root');
    root.innerHTML = window.ITV_UI || '';
    cacheEls();
    detectPerformance();
    applyAll();
    loadChannels();
    setupDub();
    setupInput();
    setupNativeBridge();
    setupPhone();
    setupClock();
    readDeviceInfo();
    resetSaver();
    var resizeTimer = null;
    window.addEventListener('resize', function () {
      clearTimeout(resizeTimer);
      resizeTimer = setTimeout(function () {
        var was = PHONE;
        applyScale();
        placeSearch();
        renderGridFresh();
        if (PHONE && !was) { buildHome(); setTab(state.tab, true); }
        else if (PHONE) movePill(false);
      }, 120);
    });

    var startCat = S.startCat === 'LAST' ? S.lastCat : S.startCat;
    if (S.kids) startCat = 'ALL';
    selectCategory(state.cats.some(function (c) { return c.id === startCat; }) ? startCat : 'ALL', true);
    focusGrid(0);
    if (PHONE) { buildHome(); setTab(S.startCat === 'FAVORITES' && state.favorites.length ? 'library' : 'home', true); }
    if (S.autoplay && S.lastId) {
      var idx = indexInList(S.lastId);
      if (idx !== -1) { focusGrid(idx); playIndex(idx); }
    }

    // the build started fine: a live update that got this far is good
    try {
      localStorage.removeItem('itv_pending');
      if (LIVE && LIVE.code <= BUILD.code) localStorage.removeItem('itv_live');
    } catch (e) {}
    // the Android app's splash screen (core-splashscreen) waits for this: the first screen is ready to draw
    setTimeout(function () { native('ready'); }, 30);
    if (S.autoUpdate) setTimeout(function () { checkUpdates(false); }, 4000);
    if (PLATFORM === 'webos') {
      // LG: TV apps stay open for days – check again every 6 hours, and apply a downloaded update on returning to the app
      setInterval(function () { if (S.autoUpdate) checkUpdates(false); }, 6 * 3600 * 1000);
      document.addEventListener('visibilitychange', function () { if (!document.hidden) applyLiveIfIdle(); });
    }
    if (Dub && Dub.settings.migratedFromLocal) setTimeout(explainTvDub, 1500);
    window.__itv = { state: state, S: S, playIndex: playIndex, openSettings: openSettings, checkUpdates: checkUpdates };
  }

  function cacheEls() {
    ['app', 'search-input', 'top-search', 'btn-update', 'btn-getapp', 'btn-kids-exit', 'btn-settings', 'clock', 'count-badge',
      'rail-list', 'hero', 'hero-logo', 'hero-mono', 'hero-num', 'hero-cat', 'hero-q', 'hero-lang', 'hero-fav', 'hero-title',
      'hero-desc', 'hero-src', 'grid-title', 'grid-count', 'grid', 'hints', 'numhud', 'numhud-digits', 'numhud-name',
      'radio-audio', 'player', 'video', 'radio-view', 'radio-logo', 'radio-mono', 'radio-name', 'spinner', 'spinner-text',
      'perror', 'perror-title', 'perror-desc', 'perror-retry', 'perror-next', 'perror-back', 'osd', 'osd-logo', 'osd-mono',
      'osd-num', 'osd-name', 'osd-cat', 'osd-quality', 'osd-pic', 'osd-clock', 'osd-dub', 'osd-dub-text', 'osd-buttons',
      'osd-hints', 'ob-play','drawer', 'drawer-list', 'drawer-cat', 'menu', 'menu-logo', 'menu-mono', 'menu-title',
      'menu-sub', 'menu-list', 'settings', 'settings-nav', 'settings-h', 'settings-p', 'settings-rows', 'dialog', 'dialog-box',
      'dialog-ico', 'dialog-title', 'dialog-body', 'dialog-actions', 'toast', 'saver', 'saver-in', 'saver-clock',
      'topbar', 'home', 'pager', 'pager-track', 'pager-dots', 'shelves', 'navbar', 'nb-pill', 'btn-search', 'hero-bg',
      'osd-center', 'ob-pip', 'guide-body', 'brand-tab', 'btn-avatar', 'avatar-img', 'avatar-dot', 'nb-search'
    ].forEach(function (id) {
      el[id.replace(/-([a-z])/g, function (m, c) { return c.toUpperCase(); })] = $(id);
    });
  }

  // ================================================================== crash log
  // A "crumb" (what the app was doing) is kept in localStorage while the app is on screen. If the next start finds
  // one that was not closed cleanly, the app was killed (e.g. out of memory) – recorded for Settings → About.
  var crashes = lsGet('itv_crashes', []);
  var errors = lsGet('itv_errors', []);
  function crumb(what) {
    lsSet('itv_crumb', { t: Date.now(), what: what, ch: state.playing ? state.playing.id : '',
      dub: Dub && Dub.settings.enabled ? Dub.settings.engine : '', v: (LIVE || BUILD).version });
  }
  function setupCrashLog() {
    var prev = lsGet('itv_crumb', null);
    var clean = false;
    try { clean = localStorage.getItem('itv_clean') === '1'; } catch (e) {}
    if (prev && !clean) {
      crashes.unshift(prev);
      crashes = crashes.slice(0, 10);
      lsSet('itv_crashes', crashes);
    }
    var mark = function (c) { try { localStorage.setItem('itv_clean', c ? '1' : ''); } catch (e) {} };
    mark(false);
    document.addEventListener('visibilitychange', function () { mark(document.hidden); });
    window.addEventListener('pagehide', function () { mark(true); });
    window.addEventListener('error', function (e) {
      errors.unshift({ t: Date.now(), m: String(e.message || 'error').slice(0, 160), at: (e.filename || '').split('/').pop() + ':' + (e.lineno || 0) });
      errors = errors.slice(0, 10);
      lsSet('itv_errors', errors);
    });
    crumb('start');
  }
  function when(t) {
    var d = new Date(t);
    return d.getDate() + '/' + (d.getMonth() + 1) + ' ' + d.getHours() + ':' + (d.getMinutes() < 10 ? '0' : '') + d.getMinutes();
  }
  function crashText() {
    if (!crashes.length) return 'אין';
    var c = crashes[0];
    var ch = c.ch ? (function () { var i = indexInAll(c.ch); return i === -1 ? c.ch : state.all[i].name; })() : '';
    return crashes.length + ' · אחרונה ' + when(c.t) + (ch ? ' בזמן ' + ch : '') + (c.dub ? ' (דיבוב: ' + c.dub + ')' : '');
  }

  // ================================================================== look & performance
  function detectPerformance() {
    var chrome = /Chrome\/(\d+)/.exec(UA);
    var cv = chrome ? parseInt(chrome[1], 10) : 99;
    var mem = navigator.deviceMemory || 0;
    var cores = navigator.hardwareConcurrency || 0;
    if (PLATFORM === 'webos' || PLATFORM === 'tizen') {
      // TVs report deviceMemory 2 even on strong models (LG C6): judge by the browser / webOS version instead
      state.lowEnd = cv < 80 || /SMART-TV.*Tizen [2-4]\./.test(UA);
    } else {
      state.lowEnd = cv < 70 || (mem && mem <= 2) || (cores && cores <= 2);
    }
  }
  function fastMode() { return S.perf === 'fast' || (S.perf === 'auto' && state.lowEnd); }

  function applyAll() {
    applyScale();
    ['theme', 'hero', 'numbers', 'hints', 'perf', 'osdClock', 'pic', 'aspect', 'sharpen', 'kids'].forEach(applySetting);
    document.body.classList.add('platform-' + PLATFORM);
    toggleClass(document.body, 'touch', TOUCH);
  }

  function applyScale() {
    var w = window.innerWidth, hgt = window.innerHeight;
    var compact = w < 900 || (TOUCH && w < 1100 && hgt > w * 0.9);
    // phones: portrait, or a touch screen whose short side is phone-sized (landscape phones too); never TVs
    PHONE = !IS_TV && (w < 700 || (TOUCH && (Math.min(w, hgt) < 600 || hgt > w)));
    var base = PHONE ? clamp(Math.min(w, hgt) / 40, 8.5, 11) : compact ? clamp(w / 40, 8.5, 11) : w / 120;
    document.documentElement.style.fontSize = (base * (compact || PHONE ? 1 : S.uiScale)) + 'px';
    toggleClass(document.body, 'compact', compact);
    toggleClass(document.body, 'phone', PHONE);
    var cols = S.cols || (PHONE ? (w < 600 ? 2 : w < 900 ? 3 : 4) : compact ? (w < 600 ? 2 : 3) : clamp(Math.round(5 / S.uiScale), 3, 7));
    if (PHONE && S.cols) cols = Math.min(S.cols, w < 600 ? 3 : 5);
    state.cols = cols;
    document.documentElement.style.setProperty('--card-cols', cols);
  }

  // ---- colours: Nuvio accent palettes, or Material You (the Android 12+ wallpaper colours from the native app)
  var PALETTES = [['auto', 'אוטומטי'], ['system', 'צבעי המערכת (Material You)'], ['white', 'לבן'], ['arctic', 'כחול ארקטי'],
    ['gold', 'זהב'], ['jade', 'ירקן'], ['rosegold', 'רוז גולד'], ['crimson', 'ארגמן'], ['ocean', 'אוקיינוס'], ['violet', 'סגול'],
    ['emerald', 'אמרלד'], ['amber', 'ענבר'], ['rose', 'ורוד'], ['graphite', 'גרפיט']];
  // accent, on accent, variant, focus ring, focus / container background, background, elevated, card (Nuvio ThemeColors.kt)
  var PAL = {
    white: ['#f5f5f5', '#111111', '#e0e0e0', '#ffffff', '#333333', '#0d0d0d', '#1a1a1a', '#242424'],
    arctic: ['#3185f5', '#ffffff', '#4d55e8', '#4de3ff', '#172844', '#0b0e14', '#141a24', '#161e2a'],
    gold: ['#e8a91c', '#111111', '#9a6200', '#ffd45c', '#3d2d1a', '#0f0e0b', '#1d1a14', '#262116'],
    jade: ['#22d37c', '#111111', '#0bbf9a', '#7bf08d', '#153a2c', '#0b0f0d', '#141d18', '#16251d'],
    rosegold: ['#ec70a9', '#111111', '#b75aff', '#ffb37a', '#442037', '#100c0f', '#1f161d', '#281a24'],
    crimson: ['#e53935', '#ffffff', '#c62828', '#ff5252', '#3d1a1a', '#0d0d0d', '#1a1a1a', '#241a1a'],
    ocean: ['#1e88e5', '#ffffff', '#1565c0', '#42a5f5', '#1a2d3d', '#0d0d0f', '#1a1a1e', '#1a1f24'],
    violet: ['#8e24aa', '#ffffff', '#6a1b9a', '#ab47bc', '#2d1a3d', '#0d0d0f', '#1a1a1e', '#1f1a24'],
    emerald: ['#43a047', '#ffffff', '#2e7d32', '#66bb6a', '#1a3d1e', '#0d0d0d', '#1a1a1a', '#1a241a'],
    amber: ['#fb8c00', '#111111', '#ef6c00', '#ffa726', '#3d2d1a', '#0f0d0d', '#1e1a1a', '#24201a'],
    rose: ['#d81b60', '#ffffff', '#c2185b', '#ec407a', '#3d1a2d', '#0d0d0d', '#1a1a1a', '#241a1f'],
    graphite: ['#aab2be', '#111111', '#687381', '#f3f5f7', '#30343a', '#0c0d0f', '#17191d', '#20242a']
  };
  var systemColors = null;
  function readSystemColors() {
    if (systemColors !== null) return systemColors;
    systemColors = false;
    var raw = native('dynamicColors');
    if (raw) { try { var c = JSON.parse(raw); if (c && c.a1_200) systemColors = c; } catch (e) {} }
    return systemColors;
  }
  function accentName() {
    if (S.accent === 'auto') return readSystemColors() ? 'system' : 'white';
    if (S.accent === 'system' && !readSystemColors()) return 'white';
    return (PAL[S.accent] || S.accent === 'system') ? S.accent : 'white';
  }
  function hexRgb(h) { h = h.replace('#', ''); if (h.length === 3) h = h.replace(/./g, '$&$&'); var n = parseInt(h, 16); return [n >> 16 & 255, n >> 8 & 255, n & 255]; }
  function mix(a, b, t) {
    var x = hexRgb(a), y = hexRgb(b), o = '#';
    for (var i = 0; i < 3; i++) { var v = Math.round(x[i] + (y[i] - x[i]) * t); o += (v < 16 ? '0' : '') + v.toString(16); }
    return o;
  }
  function rgba(h, a) { var c = hexRgb(h); return 'rgba(' + c[0] + ',' + c[1] + ',' + c[2] + ',' + a + ')'; }
  function applyColors() {
    var name = accentName(), sc = readSystemColors(), light = S.theme === 'light', oled = S.theme === 'oled';
    var p;
    if (name === 'system' && sc) {
      p = light ? [sc.a1_600, '#ffffff', sc.a2_600, sc.a1_700, sc.a1_100, sc.n1_50, sc.n1_100 || mix(sc.n1_50, sc.n1_200, .5), '#ffffff']
        : [sc.a1_200, sc.a1_800, sc.a2_200, sc.a1_100, sc.a1_700, sc.n1_900, mix(sc.n1_900, sc.n1_800, .5), sc.n1_800];
    } else {
      p = (PAL[name] || PAL.white).slice();
      if (light) {
        var acc = name === 'white' || name === 'graphite' ? '#202124' : mix(p[0], '#000000', .12);
        p = [acc, '#ffffff', p[2], mix(acc, '#000000', .2), mix(p[0], '#ffffff', .82), '#f6f6f8', '#ececf0', '#ffffff'];
      }
    }
    var r = document.documentElement.style;
    var bg = oled && !light ? '#000000' : p[5];
    var set = function (k, v) { r.setProperty(k, v); };
    set('--accent', p[0]); set('--on-accent', p[1]); set('--accent2', p[2]); set('--focus', p[3]);
    set('--focus-bg', p[4]); set('--container', p[4]);
    set('--on-container', light ? mix(p[0], '#000000', .5) : mix(p[3], '#ffffff', .55));
    set('--glow', rgba(p[3], light ? .25 : .35)); set('--pill', rgba(p[0], light ? .16 : .2));
    set('--bg', bg); set('--bg2', oled && !light ? mix(p[6], '#000000', .35) : p[6]); set('--surface', p[7]);
    set('--surface2', light ? '#f0f0f3' : mix(p[7], '#ffffff', .06)); set('--bg-a34', rgba(bg, .34));
    // Glass Expressive: tertiary accent, the glass tint (the background with a touch of the accent) and the ambient light
    var a3 = name === 'system' && sc && sc.a3_200 ? (light ? sc.a3_600 : sc.a3_200) : p[2];
    var tintBase = light ? '#ffffff' : (oled ? mix(p[6], '#000000', .3) : p[6]);
    set('--accent3', a3);
    set('--glass', rgba(mix(tintBase, p[0], light ? .05 : .1), light ? .64 : .58));
    set('--glass-strong', rgba(mix(tintBase, p[0], light ? .04 : .08), light ? .84 : .78));
    var grey = name === 'white' || name === 'graphite';
    set('--amb1', rgba(p[0], grey ? .06 : light ? .16 : .2)); set('--amb2', rgba(a3, grey ? .04 : light ? .12 : .14));
    var meta = document.querySelector('meta[name="theme-color"]');
    if (meta) meta.setAttribute('content', bg);
    native('setBarsLight', !!light);
  }

  function applySetting(k) {
    var b = document.body;
    if (k === 'theme' || k === 'accent') {
      b.classList.remove('oled', 'light', 'dark');
      b.classList.add(S.theme);
      applyColors();
    } else if (k === 'uiScale' || k === 'cols') { applyScale(); renderGridFresh(); }
    else if (k === 'hero') toggleClass(b, 'no-hero', !S.hero);
    else if (k === 'numbers') toggleClass(b, 'no-numbers', !S.numbers);
    else if (k === 'hints') toggleClass(b, 'no-hints', !S.hints);
    else if (k === 'perf') toggleClass(b, 'anim-off', fastMode());
    else if (k === 'osdClock') toggleClass(b, 'no-osd-clock', !S.osdClock);
    else if (k === 'pic' || k === 'aspect' || k === 'sharpen') applyPicture();
    else if (k === 'kids' || k === 'hiddenCats' || k === 'hidden' || k === 'hideAppOnly') {
      if (state.all.length) {
        var cat = state.cat, idx = state.gridIdx;
        loadChannels();
        selectCategory(k !== 'kids' && state.cats.some(function (c) { return c.id === cat; }) ? cat : 'ALL', true);
        focusGrid(k === 'kids' ? 0 : idx);
      }
      toggleClass(el.btnKidsExit, 'hidden', !S.kids);
    } else if (k === 'remote' || k.indexOf('key') === 0 || k === 'longOk') renderHints();
    else if (k === 'saver') resetSaver();
    else if (k === 'avatar') updateAvatar();
  }

  var PICS = [['off', 'רגיל (ללא שינוי)'], ['cinema', 'קולנוע'], ['vivid', 'צבעים חיים'], ['sports', 'ספורט'], ['warm', 'חם']];
  var ASPECTS = [['fit', 'מקורי (התאמה)'], ['zoom', 'מילוי מסך (חיתוך)'], ['stretch', 'מתיחה'], ['pillarbox', '4:3']];
  function label(list, v) { for (var i = 0; i < list.length; i++) if (list[i][0] === v) return list[i][1]; return v; }
  function cycle(list, v, step) {
    var i = 0;
    for (var j = 0; j < list.length; j++) if (list[j][0] === v) i = j;
    return list[(i + (step || 1) + list.length) % list.length][0];
  }
  function applyPicture() {
    var v = el.video;
    if (!v) return;
    v.className = '';
    if (S.pic !== 'off') v.classList.add('pic-' + S.pic);
    if (S.aspect !== 'fit') v.classList.add('aspect-' + S.aspect);
    if (S.sharpen) v.classList.add('sharpen');
    if (el.osdPic) el.osdPic.textContent = (S.pic !== 'off' ? '✨ ' + label(PICS, S.pic) + ' · ' : '') + label(ASPECTS, S.aspect);
  }

  // ================================================================== channels & categories
  function loadChannels() {
    var hidden = S.hidden || [];
    var hiddenCats = S.hiddenCats || [];
    state.all = (window.CHANNELS_DATA || []).filter(function (c) {
      if (hidden.indexOf(c.id) !== -1) return false;
      if (hiddenCats.indexOf(c.category) !== -1) return false;
      if (S.kids && !isKidsCh(c)) return false;
      if (IS_WEB && S.hideAppOnly && appOnly(c)) return false;
      return true;
    }).map(function (c, i) {
      var o = {};
      for (var k in c) o[k] = c[k];
      o.number = c.number || i + 1;
      o.category = c.category || 'ערוצים';
      o.desc = c.desc || '';
      o.lang = c.lang || 'he';
      return o;
    });
    if (el.countBadge) el.countBadge.textContent = state.all.length + ' ערוצים ותחנות' + (S.kids ? ' · מצב ילדים' : '');
    buildCategories();
  }

  function buildCategories() {
    var counts = {};
    state.all.forEach(function (c) { counts[c.category] = (counts[c.category] || 0) + 1; });
    var favs = state.all.filter(isFav).length;
    var recents = state.recents.filter(function (id) { return indexInAll(id) !== -1; }).length;
    var list = [{ id: 'ALL', name: S.kids ? 'כל ערוצי הילדים' : 'כל הערוצים', icon: '📺', count: state.all.length }];
    if (favs) list.push({ id: 'FAVORITES', name: 'מועדפים', icon: '⭐', count: favs });
    if (recents) list.push({ id: 'RECENT', name: 'צפית לאחרונה', icon: '🕘', count: recents });
    var dub = state.all.filter(foreign).length;
    if (dub && !S.kids) list.push({ id: 'DUB', name: 'דיבוב לעברית', icon: '🎙️', count: dub });
    var push = function (cat) { list.push({ id: cat, name: splitCat(cat), icon: catIcon(cat), count: counts[cat] }); delete counts[cat]; };
    CAT_ORDER.forEach(function (c) { if (counts[c]) push(c); });
    Object.keys(counts).forEach(push);
    state.cats = list;
    renderRail();
  }

  function renderRail() {
    el.railList.innerHTML = state.cats.map(function (c, i) {
      return '<div class="rail-item' + (c.id === state.cat ? ' active' : '') + '" data-i="' + i + '">' +
        '<span class="ri-ico">' + c.icon + '</span><span class="ri-name">' + esc(c.name) + '</span>' +
        '<span class="ri-count">' + c.count + '</span></div>';
    }).join('');
    if (state.zone === 'rail' && !state.overlay) markFocus();
  }

  function indexInAll(id) { for (var i = 0; i < state.all.length; i++) if (state.all[i].id === id) return i; return -1; }
  function indexInList(id) { for (var i = 0; i < state.list.length; i++) if (state.list[i].id === id) return i; return -1; }

  function selectCategory(id, quiet) {
    state.cat = id;
    for (var i = 0; i < state.cats.length; i++) if (state.cats[i].id === id) state.railIdx = i;
    if (!quiet && !S.kids) setSetting('lastCat', id);
    filterList();
    renderRail();
  }

  function filterList() {
    var q = state.query.trim().toLowerCase();
    var r = state.all;
    if (state.cat === 'FAVORITES') r = r.filter(isFav);
    else if (state.cat === 'RECENT') {
      r = state.recents.map(function (id) { var i = indexInAll(id); return i === -1 ? null : state.all[i]; }).filter(Boolean);
    } else if (state.cat === 'DUB') r = r.filter(foreign);
    else if (state.cat !== 'ALL') r = r.filter(function (c) { return c.category === state.cat; });
    if (q) {
      r = r.filter(function (c) {
        return c.name.toLowerCase().indexOf(q) !== -1 || (c.title && c.title.toLowerCase().indexOf(q) !== -1) ||
          (c.desc && c.desc.toLowerCase().indexOf(q) !== -1) || String(c.number) === q;
      });
    }
    state.list = r;
    var cat = null;
    for (var i = 0; i < state.cats.length; i++) if (state.cats[i].id === state.cat) cat = state.cats[i];
    el.gridTitle.textContent = q ? '🔍 "' + state.query.trim() + '"' : cat ? cat.icon + ' ' + cat.name : 'ערוצים';
    el.gridCount.textContent = r.length + ' ערוצים';
    state.playIdx = state.playing ? indexInList(state.playing.id) : -1;
    state.gridIdx = clamp(state.gridIdx, 0, Math.max(0, r.length - 1));
    renderGridFresh();
  }

  // ================================================================== grid
  function renderGridFresh() {
    if (!el.grid) return;
    el.grid.innerHTML = '';
    cards = [];
    rendered = 0;
    if (!state.list.length) {
      el.grid.innerHTML = '<div class="grid-empty">' + (state.query ? 'לא נמצאו ערוצים לחיפוש הזה' : 'אין כאן ערוצים עדיין') + '</div>';
      updateHero(null);
      return;
    }
    ensureRendered(Math.max(GRID_PAGE, state.gridIdx + GRID_PAGE / 2));
    if (state.zone === 'grid' && !state.overlay) markFocus();
    updateHero(state.list[state.gridIdx]);
  }

  function cardMeta(ch) {
    if (isApp(ch)) return 'אפליקציה / שירות רשמי';
    if (isRadioCh(ch)) return 'רדיו בשידור חי';
    var cat = splitCat(ch.category).replace(/^ערוצי ישראל$/, 'ישראל');
    var lang = foreign(ch) && ch.lang !== 'auto' ? Dub.langName(ch.lang) : '';
    return lang ? cat + ' · ' + lang : cat;
  }

  function ensureRendered(idx) {
    var total = state.list.length;
    var want = Math.min(total, Math.ceil((idx + 1) / GRID_PAGE) * GRID_PAGE);
    if (want <= rendered) return;
    var frag = document.createDocumentFragment();
    for (var i = rendered; i < want; i++) {
      var ch = state.list[i];
      var card = document.createElement('div');
      card.className = 'card' + (isFav(ch) ? ' is-fav' : '') + (state.playing && state.playing.id === ch.id ? ' is-playing' : '') +
        (appOnly(ch) ? ' is-locked' : '');
      card.setAttribute('data-i', i);
      var flags = '<span class="f-live">● משדר</span><span class="f-fav">⭐</span>' +
        (foreign(ch) ? '<span>🎙️</span>' : '') + (isApp(ch) ? '<span>↗</span>' : '') +
        (isRadioCh(ch) ? '<span>📻</span>' : '') + (appOnly(ch) ? '<span>📲</span>' : '');
      card.innerHTML = '<div class="card-logo" style="background-image:' + tintOf(ch) + '"><div class="card-num">' + ch.number + '</div>' +
        '<div class="card-flags">' + flags + '</div><img alt="" loading="lazy"><div class="mono hidden"></div></div>' +
        '<div class="card-body"><div class="card-name">' + esc(ch.name) + '</div><div class="card-meta">' + esc(cardMeta(ch)) + '</div></div>';
      lazyLogo(card.querySelector('img'), card.querySelector('.mono'), ch);
      frag.appendChild(card);
      cards.push(card);
    }
    el.grid.appendChild(frag);
    rendered = want;
  }

  // ================================================================== hero (debounced: holding an arrow stays smooth)
  function updateHero(ch) {
    if (state.heroTimer) clearTimeout(state.heroTimer);
    state.heroTimer = setTimeout(function () { drawHero(ch); }, fastMode() ? 140 : 60);
  }
  function drawHero(ch) {
    if (!S.hero || !el.hero) return;
    if (!ch) { el.hero.style.visibility = 'hidden'; return; }
    el.hero.style.visibility = '';
    el.heroNum.textContent = ch.number;
    el.heroCat.textContent = splitCat(ch.category);
    el.heroQ.textContent = isApp(ch) ? 'אפליקציה' : isRadioCh(ch) ? 'רדיו' : (ch.badge || 'HD');
    el.heroTitle.textContent = ch.name;
    el.heroDesc.textContent = ch.desc || '';
    var f = foreign(ch);
    toggleClass(el.heroLang, 'hidden', !f);
    if (f) el.heroLang.textContent = '🎙️ ' + Dub.langName(ch.lang) + ' – אפשר דיבוב לעברית';
    toggleClass(el.heroFav, 'hidden', !isFav(ch));
    el.heroSrc.textContent = S.showSource ? sourceLine(ch) + (appOnly(ch) ? ' · בדפדפן חסום – פועל באפליקציות' : '') : '';
    setLogo(el.heroLogo, el.heroMono, ch);
    if (el.heroBg) {
      var h = hueOf(ch.id);
      el.heroBg.style.background = 'radial-gradient(ellipse at 88% 40%, hsla(' + h + ',70%,45%,.55), hsla(' + h + ',70%,45%,0) 55%),' +
        'radial-gradient(ellipse at 55% 120%, hsla(' + ((h + 40) % 360) + ',70%,35%,.45), hsla(' + ((h + 40) % 360) + ',70%,35%,0) 60%)';
    }
  }

  // ================================================================== focus
  function markFocus() {
    var old = document.querySelectorAll('.focused');
    for (var i = 0; i < old.length; i++) old[i].classList.remove('focused');
    var t = null;
    if (state.overlay === 'player') {
      if (state.zone === 'perror') t = errorButtons()[state.perrIdx || 0];
    } else if (state.overlay === 'drawer') t = el.drawerList.children[state.drawerIdx];
    else if (state.overlay === 'menu') t = el.menuList.children[state.menuIdx];
    else if (state.overlay === 'dialog') t = dialogButtons()[state.dialogIdx];
    else if (state.overlay === 'settings') {
      t = state.setZone === 'nav' ? el.settingsNav.children[state.setSection] : el.settingsRows.querySelectorAll('.set-row')[state.setIdx];
    } else if (state.zone === 'grid') t = cards[state.gridIdx];
    else if (state.zone === 'rail') t = el.railList.children[state.railIdx];
    else if (state.zone === 'top') t = topItems()[state.topIdx];
    if (t) { t.classList.add('focused'); reveal(t); }
  }

  /** Keeps the focused item inside its scrolling box, without smooth scrolling (slow on TVs). */
  function reveal(node) {
    var box = node.parentNode;
    while (box && box !== document.body) {
      var oy = window.getComputedStyle(box).overflowY;
      if (oy === 'auto' || oy === 'scroll') break;
      box = box.parentNode;
    }
    if (!box || box === document.body) return;
    if (document.body.classList.contains('compact') && box.scrollWidth > box.clientWidth + 4) {
      try { node.scrollIntoView({ block: 'nearest', inline: 'nearest' }); } catch (e) {}
      return;
    }
    var top = node.offsetParent === box ? node.offsetTop
      : node.getBoundingClientRect().top - box.getBoundingClientRect().top + box.scrollTop;
    var margin = Math.min(node.offsetHeight * 0.35, box.clientHeight * 0.15);
    if (top - margin < box.scrollTop) box.scrollTop = Math.max(0, top - margin);
    else if (top + node.offsetHeight + margin > box.scrollTop + box.clientHeight) box.scrollTop = top + node.offsetHeight + margin - box.clientHeight;
  }

  function focusGrid(i) {
    state.zone = 'grid';
    if (!state.list.length) { markFocus(); return; }
    state.gridIdx = clamp(i, 0, state.list.length - 1);
    ensureRendered(state.gridIdx + GRID_PAGE / 2);
    markFocus();
    updateHero(state.list[state.gridIdx]);
  }
  function focusRail(i) { state.zone = 'rail'; state.railIdx = clamp(i, 0, state.cats.length - 1); markFocus(); }
  function topItems() {
    return Array.prototype.filter.call(document.querySelectorAll('.nav-top'), function (n) { return !n.classList.contains('hidden'); });
  }
  function focusTop(i) {
    var items = topItems();
    state.zone = 'top';
    state.topIdx = clamp(i, 0, items.length - 1);
    markFocus();
  }

  // ================================================================== favourites / recents / hidden
  function toggleFav(ch) {
    if (!ch) return;
    var i = state.favorites.indexOf(ch.id);
    if (i === -1) { state.favorites.push(ch.id); toast('⭐ ' + ch.name + ' נוסף למועדפים'); }
    else { state.favorites.splice(i, 1); toast('הוסר מהמועדפים'); }
    lsSet('israel_tv_favorites', state.favorites);
    homeDirty = true;
    var had = state.cats.length;
    buildCategories();
    if (state.cat === 'FAVORITES') filterList();
    else {
      var idx = indexInList(ch.id);
      if (idx !== -1 && cards[idx]) toggleClass(cards[idx], 'is-fav', isFav(ch));
      if (had !== state.cats.length && state.zone === 'rail') markFocus();
      updateHero(state.list[state.gridIdx]);
    }
  }
  function addRecent(ch) {
    state.recents = [ch.id].concat(state.recents.filter(function (id) { return id !== ch.id; })).slice(0, MAX_RECENTS);
    lsSet('israel_tv_recents', state.recents);
    homeDirty = true;
    S.lastId = ch.id;
    saveSettings();
    var has = state.cats.some(function (c) { return c.id === 'RECENT'; });
    if (!has) buildCategories();
  }
  // ================================================================== phone: tabs, home (hero pager + shelves), navigation bar
  var TABS = ['home', 'explore', 'library', 'settings'];
  var TAB_TITLES = { explore: 'ערוצים', library: 'שלי', settings: 'הגדרות' };
  var pagerTimer = null, pagerItems = [], homeDirty = true;

  function haptic(kind) { if (native('haptic', kind || 'tick') === undefined && navigator.vibrate && kind === 'long') { try { navigator.vibrate(12); } catch (e) {} } }

  /** Runs a DOM change as a View Transition where the browser has it (Chrome 111+), else straight away. */
  function withTransition(fn) {
    if (document.startViewTransition && !fastMode() && !document.hidden) {
      try { document.startViewTransition(fn); return; } catch (e) {}
    }
    fn();
  }

  function setTab(tab, instant) {
    if (!PHONE) return;
    if (TABS.indexOf(tab) === -1) tab = 'home';
    var prev = state.tab;
    var go = function () {
      if (tab !== 'settings' && state.overlay === 'settings') closeSettings(true);
      if (tab !== 'settings') state.prevTab = tab;
      state.tab = tab;
      TABS.forEach(function (t) { toggleClass(document.body, 'tab-' + t, t === tab || (tab === 'settings' && t === state.prevTab)); });
      var items = el.navbar.querySelectorAll('.nb-item');
      for (var i = 0; i < items.length; i++) toggleClass(items[i], 'on', items[i].getAttribute('data-tab') === tab);
      if (el.brandTab) el.brandTab.textContent = TAB_TITLES[tab] || '';
      if (tab !== 'explore' && el.navbar.classList.contains('searching')) setSearching(false);
      if (tab === 'home') { if (homeDirty) buildHome(); updateTopbar(); }
      else if (tab === 'library') {
        var target = state.cats.some(function (c) { return c.id === 'FAVORITES'; }) ? 'FAVORITES' : 'RECENT';
        if (state.query) { state.query = ''; el.searchInput.value = ''; }
        selectCategory(target, true);
        if (!state.list.length) el.grid.innerHTML = emptyState('star', 'עוד אין כאן ערוצים',
          'לחיצה ארוכה על ערוץ ← "הוספה למועדפים". ערוצים שצפיתם בהם מופיעים כאן ובמסך הבית.');
        el.gridTitle.textContent = target === 'FAVORITES' ? '★ המועדפים שלי' : '🕘 צפית לאחרונה';
      } else if (tab === 'explore') {
        if (prev === 'library' && (state.cat === 'FAVORITES' || state.cat === 'RECENT')) selectCategory('ALL', true);
        el.grid.scrollTop = el.grid.scrollTop;
      } else if (tab === 'settings' && state.overlay !== 'settings') openSettings();
      el.navbar.classList.remove('mini');
      movePill(!instant);
    };
    if (instant || prev === tab) go(); else { haptic('tick'); withTransition(go); }
  }

  function emptyState(icon, title, text) {
    return '<div class="grid-empty empty-state">' + I(icon) + '<b>' + esc(title) + '</b>' + esc(text) + '</div>';
  }

  /** Moves the selected pill under the active tab with a short "jelly" stretch (Nuvio JellyMotion). */
  var pillX = null;
  function movePill(animate) {
    if (!el.nbPill) return;
    var items = el.navbar.querySelectorAll('.nb-item'), item = null;
    for (var i = 0; i < items.length; i++) if (items[i].getAttribute('data-tab') === state.tab) item = items[i];
    if (!item) return;
    el.nbPill.style.width = item.offsetWidth + 'px';
    var x = item.offsetLeft - el.nbPill.offsetLeft;   // offsetLeft ignores the transform: distance from the pill's own spot
    var to = 'translateX(' + x + 'px)';
    if (animate && pillX !== null && pillX !== x && el.nbPill.animate && !fastMode()) {
      var mid = (pillX + x) / 2;
      el.nbPill.animate([
        { transform: 'translateX(' + pillX + 'px) scaleX(1)' },
        { transform: 'translateX(' + mid + 'px) scaleX(1.32) scaleY(.9)', offset: 0.45 },
        { transform: to + ' scaleX(1)' }
      ], { duration: 420, easing: 'cubic-bezier(.2, 0, 0, 1)' });
    }
    el.nbPill.style.transform = to;
    pillX = x;
  }

  function updateTopbar() {
    if (!PHONE || !el.topbar) return;
    var solid = state.tab !== 'home' || el.home.scrollTop > Math.max(60, el.pager.offsetHeight - el.topbar.offsetHeight - 40);
    toggleClass(el.topbar, 'solid', solid);
  }

  // ---- search: on phones the field lives in the floating glass bar and opens from its search button
  function placeSearch() {
    if (!el.topSearch || !el.navbar) return;
    if (PHONE && el.topSearch.parentNode !== el.navbar) el.navbar.insertBefore(el.topSearch, el.nbSearch);
    else if (!PHONE && el.topSearch.parentNode === el.navbar) {
      setSearching(false);
      el.topbar.insertBefore(el.topSearch, el.topbar.querySelector('.top-actions'));
    }
  }
  function setSearching(on) {
    if (!PHONE || !el.navbar) return;
    if (on === el.navbar.classList.contains('searching')) { if (on) el.searchInput.focus(); return; }
    haptic(on ? 'press' : 'tick');
    // Android builds that don't report the keyboard height: the field opens under the header, not behind the keyboard
    toggleClass(document.body, 'search-top', on && PLATFORM === 'android' && !state.kbAware);
    toggleClass(el.navbar, 'searching', on);
    el.navbar.classList.remove('mini');
    if (on) {
      if (state.tab !== 'explore') setTab('explore');
      el.searchInput.focus();
    } else {
      el.searchInput.blur();
      if (state.query) { el.searchInput.value = ''; state.query = ''; state.gridIdx = 0; filterList(); }
    }
  }

  // ---- profile: the 3D Noto Emoji avatar (assets/avatars, Apache 2.0) + the update card
  var AVATARS = ['1f60e', '1f981', '1f98a', '1f43c', '1f431', '1f436', '1f984', '1f47d', '1f916', '1f9b8', '1f9d1-1f680'];
  function avatarUrl(code) { return (window.ITV_BASE || '') + 'assets/avatars/' + code + '.webp'; }
  function updateAvatar() {
    if (!el.avatarImg) return;
    var code = AVATARS.indexOf(S.avatar) !== -1 ? S.avatar : AVATARS[0];
    el.avatarImg.src = avatarUrl(code);
  }
  function openProfile() {
    var html = '<div class="av-grid">' + AVATARS.map(function (c) {
      return '<button class="btn dlg-focus av-opt' + (c === S.avatar ? ' on' : '') + '" data-av="' + c + '"><img alt="" src="' + avatarUrl(c) + '"></button>';
    }).join('') + '</div>' + '<div class="upd-card dlg-focus ' + updateCardState() + '" id="profile-upd">' + updateCardHtml() + '</div>';
    openDialog({ ico: '', title: 'הפרופיל שלי', html: html, cls: 'profile',
      buttons: [{ label: 'סגירה', primary: true }, { label: 'הגדרות', act: function () { setTab('settings'); } }] });
    Array.prototype.forEach.call(el.dialogBody.querySelectorAll('.av-opt'), function (b) {
      b.onclickAction = function () {
        setSetting('avatar', b.getAttribute('data-av'));
        updateAvatar();
        Array.prototype.forEach.call(el.dialogBody.querySelectorAll('.av-opt'), function (x) { toggleClass(x, 'on', x === b); });
        haptic('confirm');
      };
      b.addEventListener('click', function (e) { e.stopPropagation(); b.onclickAction(); });
    });
    var card = $('profile-upd');
    card.onclickAction = function () { closeDialog(); updateCardAction(); };
    card.addEventListener('click', function (e) { e.stopPropagation(); card.onclickAction(); });
  }

  // ================================================================== phone: scroll physics (BitChord)
  /**
   * iOS-style rubber band + bounce for the phone's scroll containers: a port of BitChord's IosOverscroll.kt
   * (kushagrasinghx/BitChord, adapted there from Convx). While the finger pulls past an edge the content follows
   * UIKit's self-limiting rubber band (constant 0.55, sized by the container); pulling back pays the stretch off before
   * the list scrolls again. On release – or when a fling runs into the edge – it returns with a critically damped
   * spring: stiffness 247, or 130 above 5000 px/s, velocity capped at 10000 px/s (Compose's device pixels: converted with
   * devicePixelRatio). Flings are the WebView's own (Chromium on Android uses the platform's spline fling, like
   * Compose's default fling behaviour); a fling started from a stretched drag continues here with the same decay.
   */
  var Bounce = (function () {
    var BAND = 0.55, MAX_V = 10000, FAST_V = 5000, K_NORMAL = 247, K_FAST = 130, FLING_TAU = 0.325;
    function band(raw, dim) {
      var d = Math.abs(raw), b = (1 - 1 / (d * BAND / dim + 1)) * dim / BAND;
      return raw < 0 ? -b : b;
    }
    function attach(box, axis) {
      if (!box || box.__bounce) return;
      var o = box.__bounce = { pull: 0, raf: 0, quietUntil: 0 };
      var xAxis = axis === 'x', f = 1, dim = 2000, touching = false;
      // finger -> scroll position: forward = finger up (y) / left (x, LTR) / right (x, RTL)
      var sign = function () { f = xAxis && getComputedStyle(box).direction === 'rtl' ? -1 : 1; };
      var pos = function () { return xAxis ? Math.abs(box.scrollLeft) : box.scrollTop; };
      var max = function () { return Math.max(0, xAxis ? box.scrollWidth - box.clientWidth : box.scrollHeight - box.clientHeight); };
      var setPos = function (p) { if (xAxis) box.scrollLeft = p * f; else box.scrollTop = p; };
      var render = function () {
        var b = o.pull ? Math.round(band(o.pull, dim) * 10) / 10 : 0;
        box.style.transform = b ? (xAxis ? 'translate3d(' + b + 'px,0,0)' : 'translate3d(0,' + b + 'px,0)') : '';
      };
      var stop = function () { if (o.raf) cancelAnimationFrame(o.raf); o.raf = 0; };
      var measure = function () { dim = (xAxis ? box.clientWidth : box.clientHeight) || 2000; sign(); };
      // the spring back to rest, from the current stretch with the given velocity (screen px/s)
      var settle = function (v) {
        stop();
        var dpr = window.devicePixelRatio || 1;
        var vd = clamp(v * dpr, -MAX_V, MAX_V), w = Math.sqrt(Math.abs(vd) > FAST_V ? K_FAST : K_NORMAL);
        var x0 = o.pull, v0 = vd / dpr, b = v0 + w * x0, t0 = 0;
        if (!x0 && !v0) return;
        var step = function (now) {
          if (!t0) t0 = now;
          var t = (now - t0) / 1000, e = Math.exp(-w * t);
          o.pull = (x0 + b * t) * e;
          var vel = (v0 - w * b * t) * e;
          if ((Math.abs(o.pull) < 0.3 && Math.abs(vel) < 30) || t > 2.5) { o.pull = 0; o.raf = 0; render(); return; }
          render();
          o.raf = requestAnimationFrame(step);
        };
        o.raf = requestAnimationFrame(step);
      };
      // scroll the list by a finger delta; whatever runs past an edge becomes stretch
      var scrollBy = function (d) {
        var m = max(), target = pos() - d * f;
        if (target < 0) { setPos(0); o.pull += -target * f; }
        else if (target > m) { setPos(m); o.pull += -(target - m) * f; }
        else setPos(target);
      };
      // a fling that this code runs (the drag began as a stretch, so the WebView never started its own)
      var fling = function (v) {
        stop();
        var t0 = 0, p0 = pos(), m = max(), dir = -f;   // content moves with the finger: position changes by -v·f
        var step = function (now) {
          if (!t0) t0 = now;
          var t = (now - t0) / 1000, e = Math.exp(-t / FLING_TAU);
          var p = p0 + dir * v * FLING_TAU * (1 - e), vNow = v * e;
          if (p < 0 || p > m) { setPos(p < 0 ? 0 : m); o.raf = 0; settle(vNow); return; }
          setPos(p);
          if (Math.abs(vNow) < 20) { o.raf = 0; return; }
          o.raf = requestAnimationFrame(step);
        };
        o.raf = requestAnimationFrame(step);
      };

      var tracking = false, decided = false, owned = false, sx = 0, sy = 0, last = 0, lastT = 0, vel = 0;
      box.addEventListener('touchstart', function (e) {
        if (!PHONE || fastMode() || e.touches.length !== 1) { tracking = false; return; }
        stop();
        measure();
        var t = e.touches[0];
        tracking = true; decided = false; owned = false; touching = true;
        sx = t.clientX; sy = t.clientY; last = xAxis ? sx : sy; lastT = e.timeStamp; vel = 0;
      }, { passive: true });
      box.addEventListener('touchmove', function (e) {
        if (!tracking) return;
        var t = e.touches[0], c = xAxis ? t.clientX : t.clientY;
        if (!decided) {
          var dx = Math.abs(t.clientX - sx), dy = Math.abs(t.clientY - sy);
          if (dx < 3 && dy < 3) return;
          decided = true;
          if ((xAxis ? dx <= dy : dx > dy) && !o.pull) { tracking = false; touching = false; return; }   // the other axis' gesture
        }
        var d = c - last, dt = e.timeStamp - lastT;
        last = c; lastT = e.timeStamp;
        if (dt > 0) vel = 0.7 * (d / dt * 1000) + 0.3 * vel;
        if (!d) return;
        var p = pos(), m = max(), mine = false;
        if (o.pull) {
          var next = o.pull + d;
          if (o.pull > 0 !== next > 0 && next !== 0) { o.pull = 0; scrollBy(next); }   // paid off: the rest scrolls the list
          else o.pull = next;
          mine = true;
        } else if ((p <= 0 && d * f > 0) || (p >= m - 1 && d * f < 0)) { o.pull = d; mine = true; }
        else if (owned) { scrollBy(d); mine = true; }
        if (!mine) return;
        if (e.cancelable) { e.preventDefault(); owned = true; }
        else if (o.pull) setPos(o.pull * f > 0 ? 0 : m);   // the WebView is scrolling this drag: keep it at the edge
        render();
      }, { passive: false });
      var end = function (e) {
        if (!tracking) return;
        tracking = false; touching = false;
        if (e.timeStamp - lastT > 90) vel = 0;   // the finger stopped before lifting
        if (o.pull) settle(vel);
        else if (owned && Math.abs(vel) > 150) fling(vel);
      };
      box.addEventListener('touchend', end, { passive: true });
      box.addEventListener('touchcancel', end, { passive: true });

      // a WebView fling that runs into an edge hands its velocity (just before the edge) to the spring
      var sp = -1, st = 0, sv = 0;
      box.addEventListener('scroll', function (e) {
        var now = e.timeStamp || performance.now(), p = pos(), m = max();
        if (sp >= 0 && now - st < 70) {
          var hitStart = p <= 0 && sp > 0 && sv < -200, hitEnd = p >= m - 1 && sp < m - 1 && sv > 200;
          if ((hitStart || hitEnd) && !touching && !o.raf && !o.pull && now > o.quietUntil && PHONE && !fastMode()) {
            measure();
            settle(-sv * f);   // content velocity on screen = -(position velocity)·f
          }
          sv = (p - sp) / (now - st) * 1000;
        } else sv = 0;
        sp = p; st = now;
      }, { passive: true });
    }
    /** Programmatic scrolls (back to top) end without a bounce. */
    function quiet(box) { if (box && box.__bounce) box.__bounce.quietUntil = performance.now() + 1500; }
    return { attach: attach, quiet: quiet, band: band };
  })();

  // ================================================================== phone: Apple TV-style tilt + parallax + sheen on press
  function setupTilt() {
    var cur = null, layer = null, raf = 0, px = 0, py = 0, rect = null;
    var apply = function () {
      raf = 0;
      if (!cur) return;
      var nx = clamp((px - rect.left) / rect.width * 2 - 1, -1, 1), ny = clamp((py - rect.top) / rect.height * 2 - 1, -1, 1);
      // the pressed side sinks (Apple TV), the logo floats the other way (parallax), the light follows the finger
      cur.style.transform = 'perspective(60rem) rotateX(' + (-ny * 7).toFixed(2) + 'deg) rotateY(' + (nx * 9).toFixed(2) + 'deg) scale(.97)';
      cur.style.setProperty('--mx', ((nx + 1) * 50).toFixed(1) + '%');
      cur.style.setProperty('--my', ((ny + 1) * 50).toFixed(1) + '%');
      if (layer) layer.style.transform = 'translate3d(' + (-nx * 5).toFixed(1) + 'px,' + (-ny * 4).toFixed(1) + 'px,0) scale(1.05)';
    };
    var release = function () {
      if (!cur) return;
      cur.classList.remove('tilting');
      cur.style.transform = '';
      if (layer) layer.style.transform = '';
      cur = layer = null;
    };
    document.addEventListener('pointerdown', function (e) {
      if (!PHONE || fastMode() || e.pointerType === 'mouse' || !e.target.closest) return;
      var t = e.target.closest('.pc-media, .card-logo, .slide-logo');
      if (!t || t.classList.contains('skel')) return;
      release();
      cur = t;
      rect = t.getBoundingClientRect();
      layer = t.querySelector('img:not(.hidden)') || t.querySelector('.mono:not(.hidden)');
      if (!t.querySelector('.sheen') && !t.classList.contains('slide-logo')) {
        var s = document.createElement('span');
        s.className = 'sheen';
        t.appendChild(s);
      }
      t.classList.add('tilting');
      px = e.clientX; py = e.clientY;
      apply();
    }, { passive: true });
    document.addEventListener('pointermove', function (e) {
      if (!cur) return;
      px = e.clientX; py = e.clientY;
      if (!raf) raf = requestAnimationFrame(apply);
    }, { passive: true });
    ['pointerup', 'pointercancel', 'pointerleave'].forEach(function (n) { document.addEventListener(n, release, { passive: true }); });
  }

  /** Channels for the hero pager: favourites, recently watched, then the main Israeli channels. */
  function featured() {
    var out = [], seen = {};
    var add = function (ch) { if (ch && !seen[ch.id] && !isApp(ch) && !appOnly(ch) && out.length < 7) { seen[ch.id] = 1; out.push(ch); } };
    state.favorites.slice(0, 2).forEach(function (id) { add(state.all[indexInAll(id)]); });
    state.recents.slice(0, 2).forEach(function (id) { add(state.all[indexInAll(id)]); });
    state.all.forEach(function (c) { if (/ערוצי ישראל/.test(c.category) && c.logo) add(c); });
    state.all.forEach(function (c) { if (/חדשות|ספורט|ילדים/.test(c.category) && c.logo && !c.imported) add(c); });
    state.all.forEach(function (c) { if (c.logo) add(c); });
    return out;
  }

  function slideMeta(ch) {
    var parts = ['<span class="live">LIVE</span>', esc(splitCat(ch.category))];
    if (ch.countryHe) parts.push(esc(ch.countryHe));
    else if (foreign(ch) && ch.lang !== 'auto') parts.push(esc(Dub.langName(ch.lang)));
    if (ch.badge && !isRadioCh(ch)) parts.push(esc(ch.badge));
    return '<span>' + parts.join('</span><span>') + '</span>';
  }

  function buildHome() {
    if (!PHONE || !el.home) return;
    homeDirty = false;
    // ---- hero pager
    pagerItems = featured();
    el.pagerTrack.innerHTML = pagerItems.map(function (ch, i) {
      var h = hueOf(ch.id);
      return '<div class="slide" data-i="' + i + '"><div class="slide-bg" style="background:radial-gradient(ellipse at 50% 30%, hsla(' + h + ',70%,45%,.55), hsla(' + h + ',70%,30%,0) 70%),' +
        'radial-gradient(ellipse at 15% 85%, hsla(' + ((h + 50) % 360) + ',70%,40%,.4), hsla(0,0%,0%,0) 60%)"><img alt=""></div><div class="slide-shade"></div>' +
        '<div class="slide-body"><div class="slide-logo"><img alt=""><div class="mono hidden"></div></div>' +
        '<div class="slide-meta">' + slideMeta(ch) + '</div><h2 class="slide-title">' + esc(ch.name) + '</h2>' +
        '<div class="slide-actions"><button class="fab-play" data-act="play">' + I('play') + (isRadioCh(ch) ? 'האזנה' : 'צפייה') + '</button>' +
        '<button class="icon-tonal' + (isFav(ch) ? ' on' : '') + '" data-act="fav" aria-label="מועדף">' + I(isFav(ch) ? 'star' : 'starOutline') + '</button>' +
        '<button class="icon-tonal" data-act="info" aria-label="מידע">' + I('info') + '</button></div></div></div>';
    }).join('');
    Array.prototype.forEach.call(el.pagerTrack.children, function (slide, i) {
      var ch = pagerItems[i];
      setLogo(slide.querySelector('.slide-logo img'), slide.querySelector('.slide-logo .mono'), ch);
      var bg = slide.querySelector('.slide-bg img');
      var url = ch.logo ? (/^https?:/i.test(ch.logo) ? ch.logo : (window.ITV_BASE || '') + ch.logo) : '';
      if (url) { bg.referrerPolicy = 'no-referrer'; bg.onerror = function () { bg.remove(); }; bg.src = url; } else bg.remove();
    });
    el.pagerDots.innerHTML = pagerItems.map(function (c, i) { return '<i class="' + (i === 0 ? 'on' : '') + '"></i>'; }).join('');
    restartPager();

    // ---- shelves
    var shelves = [];
    var recents = state.recents.map(function (id) { return state.all[indexInAll(id)]; }).filter(Boolean);
    if (recents.length) shelves.push({ id: 'RECENT', title: 'המשך צפייה', list: recents, wide: true });
    var favs = state.all.filter(isFav);
    if (favs.length) shelves.push({ id: 'FAVORITES', title: 'המועדפים שלי', list: favs });
    var dubShelf = null, israeli = /ישראל|בעברית|מנויים|מסורת/;
    state.cats.forEach(function (c) {
      if (c.id === 'ALL' || c.id === 'FAVORITES' || c.id === 'RECENT') return;
      var list = c.id === 'DUB' ? state.all.filter(function (x) { return foreign(x) && !/ישראל/.test(x.category); })
        : state.all.filter(function (x) { return x.category === c.id; });
      if (IS_WEB) list = list.filter(function (x) { return !appOnly(x); }).concat(list.filter(appOnly));
      if (!list.length) return;
      var sh = { id: c.id, title: (c.id === 'DUB' ? 'מהעולם, עם דיבוב לעברית' : c.name), icon: c.icon, list: list, count: list.length };
      if (c.id === 'DUB') { dubShelf = sh; return; }
      if (dubShelf && !israeli.test(c.id)) { shelves.push(dubShelf); dubShelf = null; }   // after the Israeli rows
      shelves.push(sh);
    });
    if (dubShelf) shelves.push(dubShelf);
    el.shelves.innerHTML = '';
    var frag = document.createDocumentFragment();
    shelves.forEach(function (sh, si) {
      var sec = document.createElement('section');
      sec.className = 'shelf' + (sh.wide ? ' wide' : '');
      sec.style.animationDelay = Math.min(si, 6) * 45 + 'ms';
      sec.setAttribute('data-cat', sh.id);
      sec.innerHTML = '<div class="shelf-head"><h3>' + (sh.icon ? sh.icon + ' ' : '') + esc(sh.title) +
        (sh.count > 18 ? '<small>' + sh.count + '</small>' : '') + '</h3>' +
        (sh.list.length > 4 ? '<button class="view-all" data-cat="' + esc(sh.id) + '">הכל' + I('chevron') + '</button>' : '') + '</div>' +
        '<div class="shelf-row"></div>';
      var row = sec.querySelector('.shelf-row');
      sh.list.slice(0, 18).forEach(function (ch) { row.appendChild(posterCard(ch, sh)); });
      Bounce.attach(row, 'x');
      frag.appendChild(sec);
    });
    el.shelves.appendChild(frag);
    updateTopbar();
  }

  function posterCard(ch, shelf) {
    var d = document.createElement('div');
    d.className = 'pcard' + (appOnly(ch) ? ' is-locked' : '');
    d.setAttribute('data-id', ch.id);
    d.setAttribute('data-cat', shelf.id);
    var badges = (shelf.wide ? '' : '') + (foreign(ch) ? '<span>🎙️</span>' : '') + (isRadioCh(ch) ? '<span>📻</span>' : '') +
      (isApp(ch) ? '<span>↗</span>' : '') + (appOnly(ch) ? '<span>📲</span>' : '');
    d.innerHTML = '<div class="pc-media" style="--tint:' + tintOf(ch).replace(/"/g, '') + '"><div class="pc-badges">' + badges + '</div>' +
      '<img alt=""><div class="mono hidden"></div>' + (shelf.wide ? '<div class="pc-live-bar"></div>' : '') + '</div>' +
      '<div class="pc-name">' + esc(ch.name) + '</div><div class="pc-meta">' + esc(ch.countryHe ? ch.countryHe : cardMeta(ch)) + '</div>';
    lazyLogo(d.querySelector('img'), d.querySelector('.mono'), ch);
    return d;
  }

  function pagerIndex() {
    var t = el.pagerTrack;
    return t && t.clientWidth ? Math.round(Math.abs(t.scrollLeft) / t.clientWidth) : 0;
  }
  function restartPager() {
    clearInterval(pagerTimer);
    if (pagerItems.length < 2 || fastMode()) return;
    pagerTimer = setInterval(function () {
      if (!PHONE || state.tab !== 'home' || state.overlay || document.hidden || state.pagerTouch) return;
      if (el.home.scrollTop > el.pager.offsetHeight) return;
      var t = el.pagerTrack, n = (pagerIndex() + 1) % pagerItems.length;
      var left = (document.dir === 'rtl' || getComputedStyle(t).direction === 'rtl') ? -n * t.clientWidth : n * t.clientWidth;
      try { t.scrollTo({ left: left, behavior: 'smooth' }); } catch (e) { t.scrollLeft = left; }
    }, 6500);
  }

  /** Plays a channel from the home screen: zapping then stays inside that shelf's category. */
  function playFromHome(id, cat, from) {
    var ch = state.all[indexInAll(id)];
    if (!ch) return;
    var target = cat === 'RECENT' || cat === 'FAVORITES' || cat === 'DUB' || state.cats.some(function (c) { return c.id === cat; }) ? cat : ch.category;
    if (state.query) { state.query = ''; el.searchInput.value = ''; }
    if (state.cat !== target) selectCategory(target, true);
    var i = indexInList(id);
    if (i === -1) { selectCategory('ALL', true); i = indexInList(id); }
    state.gridIdx = i;
    state.openFrom = from ? from.getBoundingClientRect() : null;
    if (Dub) Dub.unlockAudio();
    playIndex(i);
  }

  function setupPhone() {
    // navigation bar
    el.navbar.addEventListener('click', function (e) {
      var b = e.target.closest('.nb-item');
      if (!b) return;
      var tab = b.getAttribute('data-tab');
      var box = tab === state.tab && tab === 'home' ? el.home : tab === state.tab && tab === 'explore' ? el.grid : null;
      if (box) { Bounce.quiet(box); try { box.scrollTo({ top: 0, behavior: 'smooth' }); } catch (x) { box.scrollTop = 0; } return; }
      setTab(tab);
    });
    el.btnSearch.addEventListener('click', function () { setSearching(true); });
    // the glass bar's search action: opens into the search field (and closes it again)
    placeSearch();
    el.nbSearch.addEventListener('click', function () { setSearching(!el.navbar.classList.contains('searching')); });
    el.searchInput.addEventListener('keydown', function (e) { if (PHONE && e.keyCode === 13) el.searchInput.blur(); });
    // the avatar: profile sheet (pick the 3D avatar) with the update card
    updateAvatar();
    el.btnAvatar.addEventListener('click', function () { haptic('press'); openProfile(); });
    // keyboard height: native inset from the Android app (__itvInsets k), else the visual viewport (browsers)
    if (!NATIVE && window.visualViewport) {
      var vv = window.visualViewport;
      var onVv = function () { document.documentElement.style.setProperty('--kb', Math.max(0, Math.round(window.innerHeight - vv.height - vv.offsetTop)) + 'px'); };
      vv.addEventListener('resize', onVv);
      vv.addEventListener('scroll', onVv);
    }
    // iOS-style rubber band + bounce (BitChord physics) on every phone scroller; tilt + parallax on press
    [el.home, el.grid, el.settingsRows, el.menuList, el.drawerList].forEach(function (b) { Bounce.attach(b, 'y'); });
    setupTilt();

    // home: posters, "view all", hero buttons
    el.home.addEventListener('click', function (e) {
      var all = e.target.closest('.view-all');
      if (all) {
        var cat = all.getAttribute('data-cat');
        setTab(cat === 'FAVORITES' || cat === 'RECENT' ? 'library' : 'explore');
        if (cat !== 'FAVORITES' && cat !== 'RECENT') { selectCategory(cat); focusGrid(0); }
        return;
      }
      var card = e.target.closest('.pcard');
      if (card) { playFromHome(card.getAttribute('data-id'), card.getAttribute('data-cat'), card.querySelector('.pc-media')); return; }
      var btn = e.target.closest('.slide [data-act]');
      if (btn) {
        var slide = btn.closest('.slide'), ch = pagerItems[parseInt(slide.getAttribute('data-i'), 10)];
        var act = btn.getAttribute('data-act');
        if (act === 'play') playFromHome(ch.id, ch.category, slide.querySelector('.slide-logo'));
        else if (act === 'fav') { toggleFav(ch); btn.classList.toggle('on', isFav(ch)); btn.innerHTML = I(isFav(ch) ? 'star' : 'starOutline'); haptic('tick'); }
        else if (act === 'info') showInfo(ch);
        return;
      }
      var s = e.target.closest('.slide');
      if (s) { var c2 = pagerItems[parseInt(s.getAttribute('data-i'), 10)]; playFromHome(c2.id, c2.category, s.querySelector('.slide-logo')); }
    });
    el.home.addEventListener('contextmenu', function (e) {
      var card = e.target.closest('.pcard') || e.target.closest('.slide');
      if (!card) return;
      e.preventDefault();
      var ch = card.classList.contains('slide') ? pagerItems[parseInt(card.getAttribute('data-i'), 10)] : state.all[indexInAll(card.getAttribute('data-id'))];
      if (ch) { haptic('long'); channelMenu(ch); }
    });
    // pager dots follow the swipe; touching the pager pauses auto-advance
    var dotsTimer = null;
    el.pagerTrack.addEventListener('scroll', function () {
      if (dotsTimer) return;
      dotsTimer = setTimeout(function () {
        dotsTimer = null;
        var n = pagerIndex(), dots = el.pagerDots.children;
        for (var i = 0; i < dots.length; i++) toggleClass(dots[i], 'on', i === n);
      }, 60);
    }, { passive: true });
    el.pagerTrack.addEventListener('touchstart', function () { state.pagerTouch = true; }, { passive: true });
    el.pagerTrack.addEventListener('touchend', function () { state.pagerTouch = false; restartPager(); }, { passive: true });

    // top bar turns solid over the content; the navigation bar shrinks while scrolling down (labels hide)
    var lastY = 0, ticking = false;
    var onScroll = function (box) {
      if (ticking) return;
      ticking = true;
      requestAnimationFrame(function () {
        ticking = false;
        var y = box.scrollTop;
        if (box === el.home) updateTopbar();
        if (Math.abs(y - lastY) > 12) { toggleClass(el.navbar, 'mini', y > lastY && y > 80); lastY = y; }
      });
    };
    el.home.addEventListener('scroll', function () { onScroll(el.home); }, { passive: true });
    el.grid.addEventListener('scroll', function () { if (PHONE) onScroll(el.grid); }, { passive: true });

    // ripple (M3 state layer) where the finger touches
    document.addEventListener('pointerdown', function (e) {
      if (!PHONE || fastMode() || e.pointerType === 'mouse') return;
      var t = e.target.closest && e.target.closest('.pc-media, .card-logo, .menu-item, .set-row, .drawer-item, .btn, .fab-play, .icon-tonal, .view-all, .rail-item, .nav-item, .nb-item, .top-btn');
      if (!t) return;
      var cs = getComputedStyle(t);
      if (cs.position === 'static') t.style.position = 'relative';
      if (cs.overflow !== 'hidden') t.style.overflow = 'hidden';
      var r = t.getBoundingClientRect(), rip = document.createElement('span');
      rip.className = 'rip';
      rip.style.left = (e.clientX - r.left) + 'px';
      rip.style.top = (e.clientY - r.top) + 'px';
      if (/fab-play|primary/.test(t.className)) rip.style.color = 'var(--on-accent)';
      else if (document.body.classList.contains('light')) rip.style.color = '#000';
      t.appendChild(rip);
      setTimeout(function () { if (rip.parentNode) rip.parentNode.removeChild(rip); }, 600);
    }, { passive: true });

    // player: swipe up / down = next / previous channel (like TV apps)
    var sx = 0, sy = 0, st = 0;
    el.player.addEventListener('touchstart', function (e) {
      if (e.touches.length !== 1) return;
      sx = e.touches[0].clientX; sy = e.touches[0].clientY; st = Date.now();
    }, { passive: true });
    el.player.addEventListener('touchend', function (e) {
      if (!PHONE || state.overlay !== 'player' || !e.changedTouches.length || e.target.closest('.drawer, .perror-box, .osd-buttons')) return;
      var dx = e.changedTouches[0].clientX - sx, dy = e.changedTouches[0].clientY - sy;
      if (Date.now() - st < 600 && Math.abs(dy) > 70 && Math.abs(dx) < Math.abs(dy) * 0.6) {
        haptic('tick');
        zap(dy < 0 ? 1 : -1);
        state.swiped = Date.now();
      }
    }, { passive: true });

    // bottom sheets: drag the handle / header down to close
    [el.menu, el.drawer].forEach(function (layer) {
      var sheet = layer === el.menu ? layer.querySelector('.sheet') : layer;
      var y0 = null, dy = 0, t0 = 0;
      sheet.addEventListener('touchstart', function (e) {
        if (!PHONE) return;
        var list = sheet.querySelector('.menu-list, .drawer-list');
        var onHead = !e.target.closest('.menu-list, .drawer-list') || (list && list.scrollTop <= 0 && e.target.closest('.menu-head, .drawer-head, .sheet-handle'));
        if (!onHead) return;
        y0 = e.touches[0].clientY; dy = 0; t0 = Date.now();
      }, { passive: true });
      sheet.addEventListener('touchmove', function (e) {
        if (y0 === null) return;
        dy = Math.max(0, e.touches[0].clientY - y0);
        sheet.classList.add('dragging');
        sheet.style.transform = 'translateY(' + dy + 'px)';
      }, { passive: true });
      sheet.addEventListener('touchend', function () {
        if (y0 === null) return;
        y0 = null;
        sheet.classList.remove('dragging');
        var fast = dy > 40 && Date.now() - t0 < 250;
        if (dy > sheet.offsetHeight * 0.3 || fast) {
          sheet.style.transform = '';
          sheet.classList.add('closing');
          setTimeout(function () { sheet.classList.remove('closing'); if (layer === el.menu) closeMenu(); else closeDrawer(); }, 200);
        } else {
          sheet.style.transition = 'transform .25s cubic-bezier(.2,0,0,1)';
          sheet.style.transform = '';
          setTimeout(function () { sheet.style.transition = ''; }, 260);
        }
      }, { passive: true });
    });
  }

  function hideChannel(ch) {
    var h = (S.hidden || []).slice();
    h.push(ch.id);
    setSetting('hidden', h);
    toast('🙈 ' + ch.name + ' הוסתר (אפשר להחזיר בהגדרות ← ערוצים)');
  }

  // ================================================================== playback
  function playIndex(i) {
    if (i < 0 || i >= state.list.length) return;
    var ch = state.list[i];
    if (isApp(ch)) { openServiceApp(ch); addRecent(ch); return; }
    if (appOnly(ch)) { showGetApp(ch); return; }
    state.playIdx = i;
    state.playing = ch;
    addRecent(ch);
    crumb('play');
    for (var k = 0; k < cards.length; k++) toggleClass(cards[k], 'is-playing', k === i);
    openPlayer();
    fillOsd(ch);
    showOsd();
    loadStream(ch);
  }

  function openPlayer() {
    if (state.overlay !== 'player') {
      if (state.overlay === 'settings') closeSettings(true);
      state.stack = [];
      state.overlay = 'player';
      state.zone = 'video';
      el.player.classList.remove('hidden');
      document.body.classList.add('player-open');
      renderOsdHints();
      // phones: the poster grows into the full-screen player (Nuvio PosterOpenMotion)
      var r = state.openFrom;
      state.openFrom = null;
      if (PHONE && r && el.player.animate && !fastMode()) {
        var W = window.innerWidth, H = window.innerHeight;
        el.player.animate([
          { clipPath: 'inset(' + r.top + 'px ' + (W - r.right) + 'px ' + (H - r.bottom) + 'px ' + r.left + 'px round 14px)', opacity: 0.6 },
          { clipPath: 'inset(0px 0px 0px 0px round 0px)', opacity: 1 }
        ], { duration: 420, easing: 'cubic-bezier(.2, 0, 0, 1)' });
      }
      if (PHONE) native('setImmersive', true);
    }
    native('setPlaying', true);
    toggleClass(el.obPip, 'hidden', !(native('canPip') || (document.pictureInPictureEnabled && !state.isRadio)));
  }

  function closePlayer(keepPlaying) {
    hideError();
    spinner(false);
    closeDrawer(true);
    var finish = function () {
      el.player.classList.add('hidden');
      document.body.classList.remove('player-open');
    };
    if (PHONE && el.player.animate && !fastMode()) {
      var a = el.player.animate([{ opacity: 1, transform: 'scale(1)' }, { opacity: 0, transform: 'scale(.94)' }],
        { duration: 200, easing: 'cubic-bezier(.3, 0, .8, .15)' });
      a.onfinish = finish;
    } else finish();
    if (PHONE) native('setImmersive', false);
    state.overlay = null;
    if (!keepPlaying) stopPlayback();
    native('setPlaying', false);   // picture-in-picture on "home" only while the player is on screen
    var idx = state.playing ? indexInList(state.playing.id) : -1;
    focusGrid(idx !== -1 ? idx : state.gridIdx);
    if (PHONE && state.tab === 'home' && homeDirty) { var y = el.home.scrollTop; buildHome(); el.home.scrollTop = y; }
    if (state.playing && keepPlaying !== false) toast(PHONE ? 'השידור ממשיך ברקע' : 'השידור ממשיך ברקע · OK על הערוץ לחזרה');
  }

  function stopPlayback() {
    if (Dub) Dub.detach();
    if (state.hls) { try { state.hls.destroy(); } catch (e) {} state.hls = null; }
    el.video.pause(); el.video.removeAttribute('src'); try { el.video.load(); } catch (e) {}
    el.radioAudio.pause(); el.radioAudio.removeAttribute('src');
    state.playing = null;
    for (var k = 0; k < cards.length; k++) cards[k].classList.remove('is-playing');
  }

  function isAudioUrl(url, ch) {
    return isRadioCh(ch) || /\.mp3|\.aac|_mp3|_aac|\.audio/i.test(url) ||
      (ch.categoryEn === 'music' && url.indexOf('.m3u8') === -1);
  }

  function spinner(on, text) {
    toggleClass(el.spinner, 'hidden', !on);
    if (on) el.spinnerText.textContent = text || 'מתחבר לשידור...';
  }

  // Keshet 12 / 24 play from mako's own CDN (the broadcaster, not a re-stream): the stream URL needs a short-lived
  // ticket from mako's entitlement service, fetched right before playing (it then holds for the whole session)
  var MAKO_ENTITLEMENT = 'https://mass.mako.co.il/ClicksStatistics/entitlementsServicesV2.jsp';
  function makoUrl(url, ok, fail) {
    var m = /^(https:\/\/[^/]+)(\/.*)$/.exec(url);
    if (!m) { fail(new Error('url')); return; }
    xhrJson(MAKO_ENTITLEMENT + '?et=ngt&rv=AKAMAI&lp=' + encodeURIComponent(m[2]), 10000, function (d) {
      var t = d && d.tickets && d.tickets[0] && d.tickets[0].ticket;
      if (!t) { fail(new Error('ticket')); return; }
      ok(url + (url.indexOf('?') === -1 ? '?' : '&') + decodeURIComponent(t.replace(/\+/g, ' ')));
    }, fail);
  }

  function loadStream(ch) {
    if (ch.auth === 'mako') {
      hideError();
      spinner(true);
      makoUrl(streamUrl(ch), function (url) { if (state.playing === ch) loadResolved(ch, url); },
        function () { if (state.playing === ch) onFatal(ch, 'לא התקבלה הרשאת צפייה מ-mako'); });
      return;
    }
    loadResolved(ch, streamUrl(ch));
  }

  function loadResolved(ch, url) {
    state.streamUrl = url;
    hideError();
    state.retries = 0;
    if (Dub) Dub.detach();
    if (state.hls) { try { state.hls.destroy(); } catch (e) {} state.hls = null; }
    spinner(true);

    if (isAudioUrl(url, ch)) {
      el.video.pause(); el.video.removeAttribute('src'); try { el.video.load(); } catch (e) {}
      state.isRadio = true;
      el.radioView.classList.remove('hidden');
      el.radioName.textContent = ch.name;
      setLogo(el.radioLogo, el.radioMono, ch);
      el.osdQuality.textContent = '📻 רדיו';
      el.radioAudio.src = url;
      el.radioAudio.onerror = function () { onFatal(ch, 'שגיאה בניגון השמע'); };
      startPlay(el.radioAudio, ch);
      return;
    }

    state.isRadio = false;
    el.radioAudio.pause(); el.radioAudio.removeAttribute('src');
    el.radioView.classList.add('hidden');
    el.osdQuality.textContent = 'HD';
    var v = el.video;

    if (window.Hls && Hls.isSupported()) {
      var delayed = Dub && Dub.wantsSync(ch);
      var low = S.latency === 'low' && !delayed;
      var hls = new Hls({
        enableWorker: true, lowLatencyMode: low, backBufferLength: fastMode() ? 15 : 45,
        liveSyncDurationCount: low ? 2 : 4, maxBufferLength: fastMode() ? 20 : 30,
        manifestLoadingMaxRetry: 2, levelLoadingMaxRetry: 2, fragLoadingMaxRetry: 3,
        capLevelToPlayerSize: S.quality === 'auto'
      });
      state.hls = hls;
      hls.loadSource(url);
      hls.attachMedia(v);
      var adjusted = false;
      hls.on(Hls.Events.LEVEL_LOADED, function (e, data) {
        if (!adjusted && Dub) { adjusted = true; Dub.adjustMainHls(hls, data.details, ch); }
      });
      hls.on(Hls.Events.MANIFEST_PARSED, function (e, data) {
        var levels = data.levels || [];
        if (levels.length > 1) {
          if (S.quality === 'max') hls.currentLevel = levels.length - 1;
          else if (S.quality === 'saver') {
            var cap = 0;
            for (var i = 0; i < levels.length; i++) if ((levels[i].height || 0) <= 576) cap = i;
            hls.autoLevelCapping = cap;
          }
        }
        if (delayed) spinner(true, 'מכין דיבוב מסונכרן… התמונה מושהית ב-' + Dub.settings.delay + ' שניות');
        startPlay(v, ch);
        if (Dub && state.playing === ch) Dub.attach({ video: v, hls: hls, channel: ch, url: url });
      });
      hls.on(Hls.Events.LEVEL_SWITCHED, function (e, data) {
        var l = hls.levels && hls.levels[data.level];
        if (l && l.height) el.osdQuality.textContent = l.height >= 1080 ? 'FHD ' + l.height + 'p' : l.height >= 720 ? 'HD ' + l.height + 'p' : 'SD ' + l.height + 'p';
      });
      hls.on(Hls.Events.ERROR, function (e, data) {
        if (!data.fatal) return;
        if (data.type === Hls.ErrorTypes.MEDIA_ERROR && state.retries < 2) { state.retries++; hls.recoverMediaError(); return; }
        if (data.type === Hls.ErrorTypes.NETWORK_ERROR && S.retry && state.retries < 3) {
          state.retries++;
          spinner(true, 'מתחבר מחדש... (' + state.retries + '/3)');
          setTimeout(function () { if (state.hls === hls) hls.startLoad(); }, 1500 * state.retries);
          return;
        }
        onFatal(ch, data.type === Hls.ErrorTypes.NETWORK_ERROR ? 'השרת לא עונה' : 'פורמט לא נתמך');
      });
    } else if (v.canPlayType('application/vnd.apple.mpegurl')) {
      v.src = url;
      v.onerror = function () { onFatal(ch, 'שגיאה בניגון'); };
      startPlay(v, ch);
    } else {
      v.src = url;
      startPlay(v, ch);
    }
  }

  // The app never mutes a channel: every channel starts with full sound, and only the TV's own mute key silences it.
  function startPlay(m, ch) {
    var done = function () { spinner(false); updatePlayIcon(); };
    m.muted = false;
    m.volume = 1;
    m.addEventListener('playing', function onp() { m.removeEventListener('playing', onp); done(); });
    var p = m.play();
    if (!p || !p.then) return;
    p.then(done).catch(function (e) {
      if (state.playing !== ch || (e && e.name === 'AbortError')) return;   // zapped on: the next channel's load took over
      if (e && e.name === 'NotAllowedError') {   // a browser without a tap yet: wait for ▶ instead of playing silently
        done();
        toast('▶ לחצו על נגן כדי להתחיל');
        return;
      }
      onFatal(ch, e && e.message);
    });
  }

  function onFatal(ch, reason) {
    if (state.playing !== ch) return;
    spinner(false);
    if (Dub) Dub.detach();
    if (state.hls) { try { state.hls.destroy(); } catch (e) {} state.hls = null; }
    var insecure = location.protocol === 'https:' && /^http:/i.test(streamUrl(ch));
    el.perrorTitle.textContent = insecure ? 'הערוץ פועל באפליקציה' : 'השידור לא זמין כרגע';
    el.perrorDesc.textContent = insecure
      ? 'הערוץ משדר ב-HTTP, ודפדפנים חוסמים שידור כזה מאתר מאובטח. באפליקציות (טלוויזיה, אנדרואיד, מחשב) הוא פועל.'
      : 'לא הצלחנו להתחבר ל"' + ch.name + '" (' + (reason || 'שגיאת רשת') + '). ייתכן שהשידור כבוי זמנית – נסו שוב או עברו ערוץ.';
    el.perror.classList.remove('hidden');
    state.zone = 'perror';
    state.perrIdx = 0;
    markFocus();
  }
  function hideError() { if (el.perror) el.perror.classList.add('hidden'); if (state.zone === 'perror') state.zone = 'video'; }
  function errorButtons() { return [el.perrorRetry, el.perrorNext, el.perrorBack]; }

  function media() { return state.isRadio ? el.radioAudio : el.video; }
  function togglePlay() {
    var m = media();
    if (!m || !state.playing) return;
    if (m.paused) { var p = m.play(); if (p && p.catch) p.catch(function () {}); toast('▶ ממשיך'); }
    else { m.pause(); toast('⏸ מושהה'); }
    updatePlayIcon();
  }
  function updatePlayIcon() {
    var m = media(), icon = I(m && m.paused ? 'play' : 'pause');
    var nodes = document.querySelectorAll('.ob-play');
    for (var i = 0; i < nodes.length; i++) nodes[i].innerHTML = icon;
  }

  function enterPip() {
    if (native('canPip')) { native('enterPip'); return; }
    if (document.pictureInPictureEnabled && el.video.requestPictureInPicture) {
      var p = el.video.requestPictureInPicture();
      if (p && p.catch) p.catch(function () { toast('תמונה בתוך תמונה לא זמינה כאן'); });
    }
  }

  function zap(dir) {
    var list = S.zap === 'all' ? state.all : state.list;
    if (!list.length) return;
    var cur = -1;
    if (state.playing) for (var i = 0; i < list.length; i++) if (list[i].id === state.playing.id) cur = i;
    var next = cur;
    for (var n = 0; n < list.length; n++) {
      next = (next + dir + list.length) % list.length;
      if (!isApp(list[next]) && !appOnly(list[next])) break;
    }
    if (S.zap === 'all' && list !== state.list) {
      if (state.cat !== 'ALL' || state.query) { state.query = ''; el.searchInput.value = ''; selectCategory('ALL', true); }
      next = indexInList(list[next].id);
    }
    state.gridIdx = next;
    playIndex(next);
  }

  function fillOsd(ch) {
    el.osdNum.textContent = ch.number;
    el.osdName.textContent = ch.name;
    el.osdCat.textContent = splitCat(ch.category) + (S.showSource ? ' · ' + sourceLine(ch).replace(/^מקור: /, '') : '');
    setLogo(el.osdLogo, el.osdMono, ch);
    applyPicture();
    refreshDubUi();
  }
  function showOsd() {
    el.osd.classList.remove('off');
    el.player.classList.remove('cursor-hidden');
    if (state.osdTimer) clearTimeout(state.osdTimer);
    state.osdTimer = setTimeout(function () {
      if (state.overlay !== 'player' || state.zone === 'osd' || state.zone === 'perror') return;
      el.osd.classList.add('off');
      el.player.classList.add('cursor-hidden');
    }, (S.osdSec || 5) * 1000);
  }

  // ---- channel list inside the player
  function openDrawer() {
    var playingId = state.playing && state.playing.id;
    el.drawerCat.textContent = (state.cats[state.railIdx] || {}).name || '';
    el.drawerList.innerHTML = state.list.map(function (ch, i) {
      return '<div class="drawer-item' + (ch.id === playingId ? ' is-playing' : '') + '" data-i="' + i + '">' +
        '<span class="d-num">' + ch.number + '</span><span class="d-name">' + esc(ch.name) + '</span>' +
        (isFav(ch) ? '<span>⭐</span>' : '') + '</div>';
    }).join('');
    el.drawer.classList.remove('hidden');
    state.stack.push(state.overlay);
    state.overlay = 'drawer';
    state.drawerIdx = Math.max(0, indexInList(playingId));
    markFocus();
  }
  function closeDrawer(silent) {
    if (state.overlay !== 'drawer') { el.drawer.classList.add('hidden'); return; }
    el.drawer.classList.add('hidden');
    state.overlay = state.stack.pop() || 'player';
    if (!silent) { markFocus(); showOsd(); }
  }

  // ================================================================== options menu
  /**
   * items: [{ ico, label, val: fn -> text, act: fn, stay: true (value changes, menu stays open), dot: colour }]
   */
  function openMenu(title, sub, ch, items) {
    state.menu = items;
    el.menuTitle.textContent = title;
    el.menuSub.textContent = sub || '';
    toggleClass(el.menuLogo.parentNode, 'hidden', !ch);
    if (ch) setLogo(el.menuLogo, el.menuMono, ch);
    renderMenu();
    el.menu.classList.remove('hidden');
    if (state.overlay) state.stack.push(state.overlay);
    state.overlay = 'menu';
    state.menuIdx = 0;
    markFocus();
  }
  function renderMenu() {
    el.menuList.innerHTML = state.menu.map(function (it, i) {
      return '<div class="menu-item" data-i="' + i + '"><span class="mi-ico">' + (it.ico || '') + '</span>' +
        '<span class="mi-label">' + esc(it.label) + '</span>' +
        (it.val ? '<span class="mi-val">' + esc(it.val()) + '</span>' : '') +
        (it.dot ? '<span class="cdot ' + it.dot + '"></span>' : '') + '</div>';
    }).join('');
  }
  function closeMenu() {
    el.menu.classList.add('hidden');
    state.overlay = state.stack.pop() || null;
    state.menu = null;
    if (state.overlay === 'player') showOsd();
    markFocus();
  }
  function runMenuItem(i, step) {
    var it = state.menu && state.menu[i];
    if (!it) return;
    if (it.stay) {
      it.act(step || 1);
      renderMenu();
      markFocus();
    } else {
      closeMenu();
      it.act(step || 1);
    }
  }

  function colourOf(action) {
    var map = { keyRed: 'red', keyGreen: 'green', keyYellow: 'yellow', keyBlue: 'blue' };
    for (var k in map) if (S[k] === action) return map[k];
    return '';
  }

  function channelMenu(ch) {
    if (!ch) return;
    var items = [{ ico: isApp(ch) ? '↗' : isRadioCh(ch) ? '🎧' : '▶', label: isApp(ch) ? 'פתיחת האפליקציה' : isRadioCh(ch) ? 'האזנה' : 'צפייה',
      act: function () { var i = indexInList(ch.id); if (i !== -1) playIndex(i); } }];
    if (foreign(ch) && !appOnly(ch)) items.push({ ico: '🎙️', label: 'צפייה עם דיבוב לעברית', dot: colourOf('dub'), act: function () { watchDubbed(ch); } });
    items.push({ ico: isFav(ch) ? '★' : '☆', label: isFav(ch) ? 'הסרה מהמועדפים' : 'הוספה למועדפים', dot: colourOf('fav'), act: function () { toggleFav(ch); } });
    items.push({ ico: 'ℹ️', label: 'מידע ומקור השידור', dot: colourOf('info'), act: function () { showInfo(ch); } });
    if (!S.kids) items.push({ ico: '🙈', label: 'הסתרת הערוץ', act: function () { hideChannel(ch); } });
    if (appOnly(ch)) items.push({ ico: '📲', label: 'הורדת האפליקציה', act: function () { showGetApp(ch); } });
    items.push({ ico: '⚙️', label: 'הגדרות', act: openSettings });
    openMenu(ch.name, '#' + ch.number + ' · ' + splitCat(ch.category), ch, items);
  }

  function playerMenu() {
    var ch = state.playing;
    if (!ch) return;
    var items = [];
    if (!state.isRadio) {
      items.push({ ico: '✨', label: 'מצב תמונה', stay: true, dot: colourOf('picture'), val: function () { return label(PICS, S.pic); },
        act: function (st) { setSetting('pic', cycle(PICS, S.pic, st)); } });
      items.push({ ico: '🖼️', label: 'גודל תמונה', stay: true, dot: colourOf('aspect'), val: function () { return label(ASPECTS, S.aspect); },
        act: function (st) { setSetting('aspect', cycle(ASPECTS, S.aspect, st)); } });
      items.push({ ico: '🔍', label: 'חידוד לשידורי SD ישנים', stay: true, val: function () { return S.sharpen ? 'פועל' : 'כבוי'; },
        act: function () { setSetting('sharpen', !S.sharpen); } });
      items.push({ ico: '📶', label: 'איכות', stay: true, val: function () { return label(QUALITIES, S.quality); },
        act: function (st) { setSetting('quality', cycle(QUALITIES, S.quality, st)); if (state.playing) loadStream(state.playing); } });
    }
    if (foreign(ch)) items.push({ ico: '🎙️', label: 'דיבוב לעברית', stay: true, dot: colourOf('dub'), val: function () { return Dub.settings.enabled ? 'פועל' : 'כבוי'; },
      act: function () { toggleDub(); } });
    items.push({ ico: isFav(ch) ? '★' : '☆', label: isFav(ch) ? 'הסרה מהמועדפים' : 'הוספה למועדפים', dot: colourOf('fav'), act: function () { toggleFav(ch); } });
    items.push({ ico: '☰', label: 'רשימת ערוצים', dot: colourOf('drawer'), act: openDrawer });
    items.push({ ico: 'ℹ️', label: 'מידע ומקור השידור', dot: colourOf('info'), act: function () { showInfo(ch); } });
    items.push({ ico: '⟳', label: 'טעינה מחדש של השידור', act: function () { loadStream(ch); } });
    items.push({ ico: '⚙️', label: 'הגדרות', act: openSettings });
    openMenu(ch.name, '#' + ch.number + ' · ' + el.osdQuality.textContent, ch, items);
  }

  // ================================================================== dialogs
  /** opts: { ico, title, html, buttons: [{ label, primary, act, keep }], onBack } */
  function openDialog(opts) {
    state.dialog = opts;
    el.dialogIco.textContent = opts.ico || '';
    el.dialogTitle.textContent = opts.title || '';
    el.dialogBody.innerHTML = opts.html || '';
    var btns = opts.buttons || [{ label: 'סגירה', primary: true }];
    el.dialogActions.innerHTML = btns.map(function (b, i) {
      return '<button class="btn' + (b.primary ? ' primary' : '') + '" data-i="' + i + '">' + b.label + '</button>';
    }).join('');
    el.dialogBox.className = 'dialog-box' + (opts.wide ? ' wide' : '') + (opts.cls ? ' ' + opts.cls : '');
    el.dialog.classList.remove('hidden');
    if (state.overlay && state.overlay !== 'dialog') state.stack.push(state.overlay);
    state.overlay = 'dialog';
    state.dialogIdx = opts.focus || 0;
    markFocus();
  }
  function dialogButtons() {
    var extra = el.dialogBody.querySelectorAll('.dlg-focus');
    return Array.prototype.slice.call(extra).concat(Array.prototype.slice.call(el.dialogActions.children));
  }
  function closeDialog() {
    if (state.overlay !== 'dialog') return;
    el.dialog.classList.add('hidden');
    var d = state.dialog;
    state.dialog = null;
    state.overlay = state.stack.pop() || null;
    if (state.overlay === 'player') showOsd();
    markFocus();
    if (d && d.onClose) d.onClose();
  }
  function pressDialog(i) {
    var nodes = dialogButtons();
    var n = nodes[i];
    if (!n) return;
    if (n.classList.contains('dlg-focus')) { if (n.onclickAction) n.onclickAction(); else n.click(); return; }
    var b = state.dialog.buttons ? state.dialog.buttons[parseInt(n.getAttribute('data-i'), 10)] : null;
    if (!b || !b.keep) closeDialog();
    if (b && b.act) b.act();
  }

  function showInfo(ch) {
    var s = sourceOf(ch);
    var rows = [['מספר', ch.number], ['קטגוריה', splitCat(ch.category)], ['שפה', ch.lang === 'he' ? 'עברית' : Dub ? Dub.langName(ch.lang) : ch.lang],
      ['איכות', ch.badge || '']];
    if (isApp(ch)) {
      rows.push(['סוג', 'שירות מנויים רשמי – נפתח באפליקציה שלו']);
      rows.push(['אתר', '<code>' + esc(ch.site || ch.url) + '</code>']);
    } else {
      rows.push(['מקור השידור', esc(s.by)]);
      if (s.kind) rows.push(['סוג המקור', KIND_LABEL[s.kind] || s.kind]);
      rows.push(['שרת', '<code>' + esc(s.host) + '</code>']);
      rows.push(['כתובת', '<code>' + esc(ch.url) + '</code>']);
      if (ch.webUrl) rows.push(['באתר (https)', '<code>' + esc(ch.webUrl) + '</code>']);
      if (s.web) rows.push(['בדפדפן', esc(s.web)]);
      if (s.found) rows.push(['נמצא ב', esc(s.found)]);
      if (foreign(ch)) rows.push(['דיבוב', '🎙️ אפשר לדבב לעברית']);
    }
    openDialog({
      ico: 'ℹ️', title: ch.name, wide: true,
      html: (ch.desc ? '<p>' + esc(ch.desc) + '</p>' : '') + rows.map(function (r) {
        return '<div class="kv"><b>' + r[0] + '</b><span>' + r[1] + '</span></div>';
      }).join('') + '<p style="margin-top:1.2rem">כל המקורות: ' + esc(REPO.replace('https://', '')) + '/blob/master/SOURCES.md</p>',
      buttons: [{ label: 'סגירה', primary: true }, { label: '📋 כל המקורות', act: function () { openUrl(REPO + '/blob/master/SOURCES.md'); } }]
    });
  }

  function showGetApp(ch) {
    var items = [['🤖', 'אנדרואיד', 'APK לטלפון וטאבלט', 'IsraelTVPlus-android.apk'], ['📺', 'Android TV / Google TV', 'Xiaomi, Sony, TCL, Fire TV', 'IsraelTVPlus-androidtv.apk'],
      ['🪟', 'Windows', 'התקנה', 'IsraelTVPlus-windows-setup.exe'], ['🖥️', 'LG webOS', 'IPK · Developer Mode', 'com.israeltv.plus_all.ipk'],
      ['🔷', 'Samsung Tizen', 'פרויקט לחתימה', 'IsraelTVPlus-tizen-project.zip'], ['🍏', 'Mac', 'Apple Silicon', 'IsraelTVPlus-macos-arm64.zip'],
      ['🐧', 'Linux', 'x64 · ZIP', 'IsraelTVPlus-linux-x64.zip'], ['🎬', 'Stremio', 'הרחבה (Addon)', 'stremio'], ['📋', 'נגני IPTV', 'פלייליסט M3U', 'm3u']];
    var html = '<p>' + (ch ? '"' + esc(ch.name) + '" משדר ב-HTTP, ודפדפנים חוסמים שידור כזה מאתר מאובטח. באפליקציות הוא פועל – וגם כל שאר הערוצים.'
      : 'באפליקציות פועלים כל הערוצים (גם מה שהדפדפן חוסם), עם שלט רחוק, מועדפים, דיבוב ועדכונים אוטומטיים.') + '</p>' +
      '<div class="getapp">' + items.map(function (it) {
        var href = it[3] === 'stremio' ? REMOTE + 'stremio/' : it[3] === 'm3u' ? REMOTE + 'playlist.m3u' : RELEASES + '/download/' + it[3];
        return '<a class="btn dlg-focus" target="_blank" rel="noopener" href="' + href + '">' + it[0] + ' ' + it[1] + '<small>' + it[2] + '</small></a>';
      }).join('') + '</div>';
    openDialog({ ico: '📲', title: ch ? 'הערוץ זמין באפליקציה' : 'ישראל TV+ לכל המכשירים', html: html, wide: true });
  }

  // ================================================================== dubbing
  var DUB_STATES = ['off', 'loading', 'listening', 'translating', 'speaking', 'error'];
  function setupDub() {
    if (!Dub) return;
    Dub.init({
      container: el.player,
      toast: toast,
      onStatus: function (st, text) {
        toggleClass(el.osdDub, 'hidden', st === 'off');
        DUB_STATES.forEach(function (c) { el.osdDub.classList.remove('st-' + c); });
        el.osdDub.classList.add('st-' + st);
        el.osdDubText.textContent = text;
        if (st === 'error') { showOsd(); toast('🎙️ ' + text); }
        if (st === 'listening' || st === 'speaking') spinner(false);
        if (st === 'loading' || st === 'error') crumb('dub-' + st);
      }
    });
  }
  function refreshDubUi() { /* the OSD status pill is driven by Dub's onStatus */ }
  function toggleDub() {
    if (!Dub) return;
    Dub.unlockAudio();
    var on = !Dub.settings.enabled;
    if (on && Dub.missingConfig()) { needKey(); return; }
    Dub.save({ enabled: on });
    var ch = state.playing;
    if (!on) { Dub.detach(); toast('🎙️ הדיבוב כובה'); return; }
    if (ch && !foreign(ch)) { toast('🎙️ הדיבוב פועל – יופעל אוטומטית בערוצים שאינם בעברית'); return; }
    toast('🎙️ דיבוב לעברית הופעל' + (Dub.settings.sync ? ' (מסונכרן)' : ''));
    if (ch && state.overlay && state.hls) {
      if (Dub.settings.sync) loadStream(ch);
      else Dub.attach({ video: el.video, hls: state.hls, channel: ch, url: state.streamUrl || streamUrl(ch) });
    }
  }
  function watchDubbed(ch) {
    if (!Dub) return;
    Dub.unlockAudio();
    if (Dub.missingConfig()) { needKey(ch); return; }
    Dub.save({ enabled: true });
    var i = indexInList(ch.id);
    if (i !== -1) playIndex(i);
  }

  /** Dubbing needs a key: on TVs (no keyboard) the easy way is to send it from a phone. */
  function needKey(ch) {
    if (IS_TV) { pairKey(ch); return; }   // TVs: straight to the QR – no typing with the remote
    openDialog({
      ico: '🎙️', title: 'לדיבוב צריך מפתח Gemini חינמי',
      html: '<p>' + esc(Dub.missingConfig()) + '</p><p>הכי קל: סורקים קוד בטלפון, מדביקים שם את המפתח והוא מגיע לכאן מוצפן. ' +
        'מפתח חינמי מקבלים תוך דקה ב-aistudio.google.com/apikey</p>',
      buttons: [{ label: '📱 חיבור מהטלפון (QR)', primary: true, act: function () { pairKey(ch); } },
        { label: '⌨️ הקלדה בהגדרות', act: function () { openSettings('dub'); } }, { label: 'ביטול' }]
    });
  }

  function explainTvDub() {
    Dub.save({ migratedFromLocal: false });
    openDialog({
      ico: '🎙️', title: 'שינוי בדיבוב בטלוויזיה',
      html: '<p>הדיבוב היה מוגדר למנוע "מקומי" (Whisper בתוך המכשיר). בטלוויזיה הוא כבד מדי – זה מה שגרם לאפליקציה להיסגר ולחזור למסך הפתיחה.</p>' +
        '<p>בטלוויזיה הדיבוב עובד דרך Gemini (מפתח חינמי). אפשר לחבר את המפתח מהטלפון בסריקת קוד.</p>',
      buttons: [{ label: '📱 חיבור מהטלפון (QR)', primary: true, act: function () { pairKey(); } }, { label: 'אחר כך' }]
    });
  }

  // ---- send the API key from a phone: QR → pair.html on the site → encrypted message via ntfy.sh → here
  // The AES key travels only inside the QR (URL #fragment, never sent to a server); ntfy.sh sees ciphertext only.
  var pairTimer = null;
  function b64(bytes) { var s = ''; for (var i = 0; i < bytes.length; i++) s += String.fromCharCode(bytes[i]); return btoa(s).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, ''); }
  function unb64(str) { var s = atob(str.replace(/-/g, '+').replace(/_/g, '/')); var a = new Uint8Array(s.length); for (var i = 0; i < s.length; i++) a[i] = s.charCodeAt(i); return a; }
  function loadQrLib(done) {
    if (window.qrcode) { done(); return; }
    var urls = [(window.ITV_BASE || '') + 'assets/qrcode.js', REMOTE + 'assets/qrcode.js'];
    var i = 0;
    (function next() {
      if (i >= urls.length) { done(); return; }
      var s = document.createElement('script');
      s.src = urls[i++];
      s.onload = done;
      s.onerror = next;
      document.head.appendChild(s);
    })();
  }
  function pairKey(ch) {
    if (!window.crypto || !crypto.subtle) { toast('המכשיר לא תומך בחיבור מוצפן – הקלידו את המפתח בהגדרות'); openSettings('dub'); return; }
    var rnd = new Uint8Array(16), tb = new Uint8Array(12);
    crypto.getRandomValues(rnd);
    crypto.getRandomValues(tb);
    var topic = 'itv' + b64(tb).replace(/[-_]/g, 'x');
    var url = REMOTE + 'pair.html#t=' + topic + '&k=' + b64(rnd);
    var since = Math.floor(Date.now() / 1000) - 5;
    loadQrLib(function () {
      var qr = '';
      try { var q = window.qrcode(0, 'M'); q.addData(url); q.make(); qr = q.createSvgTag(7, 3); } catch (e) {}
      openDialog({
        ico: '📱', title: 'חיבור מפתח הדיבוב מהטלפון',
        html: '<p>הדיבוב בטלוויזיה עובד עם מפתח Gemini חינמי – מגדירים פעם אחת, דקה מהטלפון.</p>' +
          '<div class="qr">' + qr + '</div><p>סרקו במצלמת הטלפון, צרו / הדביקו מפתח Gemini ולחצו "שליחה לטלוויזיה".</p>' +
          '<p class="small">או פתחו בטלפון: <code>' + esc(url) + '</code></p><p id="pair-status">⏳ ממתין לטלפון…</p>',
        buttons: [{ label: 'ביטול' }],
        onClose: function () { clearInterval(pairTimer); pairTimer = null; }
      });
    });
    var busy = false;
    clearInterval(pairTimer);
    pairTimer = setInterval(function () {
      if (busy) return;
      busy = true;
      var x = new XMLHttpRequest();
      x.open('GET', 'https://ntfy.sh/' + topic + '/json?poll=1&since=' + since, true);
      x.timeout = 10000;
      x.onload = function () {
        busy = false;
        var lines = (x.responseText || '').split('\n');
        for (var i = 0; i < lines.length; i++) {
          var m = null;
          try { m = JSON.parse(lines[i]); } catch (e) { continue; }
          if (m && m.event === 'message' && m.message) { clearInterval(pairTimer); pairTimer = null; receiveKey(m.message, rnd, ch); return; }
        }
      };
      x.onerror = x.ontimeout = function () { busy = false; };
      x.send();
    }, 2500);
    setTimeout(function () { if (pairTimer) { clearInterval(pairTimer); pairTimer = null; var st = $('pair-status'); if (st) st.textContent = 'הזמן עבר – פתחו שוב את החיבור'; } }, 15 * 60000);
  }
  function receiveKey(msg, raw, ch) {
    var data = unb64(msg);
    crypto.subtle.importKey('raw', raw, { name: 'AES-GCM' }, false, ['decrypt']).then(function (k) {
      return crypto.subtle.decrypt({ name: 'AES-GCM', iv: data.slice(0, 12) }, k, data.slice(12));
    }).then(function (plain) {
      var p = JSON.parse(new TextDecoder().decode(plain));
      var patch = { enabled: true };
      if (p.gemini) { patch.geminiKey = p.gemini; patch.engine = 'gemini'; }
      if (p.openai) { patch.openaiKey = p.openai; if (!p.gemini) patch.engine = 'openai'; }
      Dub.save(patch);
      closeDialog();
      toast('✅ המפתח התקבל – הדיבוב לעברית פועל');
      refreshSettingsIfOpen();
      if (ch) watchDubbed(ch);
      else if (state.playing && foreign(state.playing) && state.hls) Dub.attach({ video: el.video, hls: state.hls, channel: state.playing, url: streamUrl(state.playing) });
    }).catch(function () {
      var st = $('pair-status');
      if (st) st.textContent = '⚠️ ההודעה לא פוענחה – נסו שוב';
    });
  }

  // ================================================================== remote profiles & hints
  var ACTIONS = [['menu', 'תפריט אפשרויות'], ['fav', 'מועדף (הוספה/הסרה)'], ['favorites', 'מעבר למועדפים'], ['dub', 'דיבוב לעברית'],
    ['picture', 'מצב תמונה'], ['aspect', 'גודל תמונה'], ['drawer', 'רשימת ערוצים'], ['info', 'מידע ומקור'], ['search', 'חיפוש'],
    ['all', 'כל הערוצים'], ['recent', 'צפית לאחרונה'], ['settings', 'הגדרות'], ['none', 'ללא']];
  var REMOTES = [['auto', 'אוטומטי (לפי המכשיר)'], ['colors', 'שלט עם כפתורי צבע'], ['magic', 'LG Magic Remote'], ['android', 'שלט אנדרואיד / Google TV'],
    ['keyboard', 'מקלדת ועכבר'], ['touch', 'מסך מגע']];
  var SHORT = { menu: 'אפשרויות', fav: 'מועדף', favorites: 'מועדפים', dub: 'דיבוב', picture: 'תמונה', aspect: 'גודל תמונה', drawer: 'ערוצים',
    info: 'מידע', search: 'חיפוש', all: 'כל הערוצים', recent: 'אחרונים', settings: 'הגדרות' };

  function remoteProfile() {
    if (S.remote !== 'auto') return S.remote;
    if (PLATFORM === 'tizen') return 'colors';
    if (PLATFORM === 'webos') return S.seenColors ? 'colors' : 'magic';
    if (IS_TV) return S.seenColors ? 'colors' : 'android';
    if (TOUCH) return 'touch';
    return 'keyboard';
  }
  function remoteName() {
    var p = remoteProfile();
    return label(REMOTES, p) + (S.remote === 'auto' ? ' (זוהה אוטומטית)' : '');
  }

  function hintHtml(inPlayer) {
    var p = remoteProfile();
    var h = function (key, text, cls) { return '<span class="hint"><span class="key' + (cls ? ' ' + cls : '') + '">' + key + '</span>' + text + '</span>'; };
    var dot = function (c, act) { return S[act] !== 'none' ? '<span class="hint"><span class="cdot ' + c + '"></span>' + SHORT[S[act]] + '</span>' : ''; };
    var colours = dot('red', 'keyRed') + dot('green', 'keyGreen') + dot('yellow', 'keyYellow') + dot('blue', 'keyBlue');
    var longOk = h('OK ארוך', SHORT[S.longOk] || 'אפשרויות');
    if (p === 'touch') return '';
    if (p === 'keyboard') {
      return inPlayer
        ? h('▲▼', 'ערוץ') + h('◄', 'רשימת ערוצים') + h('Enter', 'אפשרויות') + h('F', 'מסך מלא') + h('D', 'דיבוב') + h('Esc', 'למדריך')
        : h('Enter', 'צפייה') + h('קליק ימני / O', 'אפשרויות') + h('/', 'חיפוש') + h('S', 'הגדרות') + h('F', 'מסך מלא') + h('0-9', 'מספר ערוץ');
    }
    var nav = inPlayer ? h(S.upNext ? '▲ הבא ▼ קודם' : '▲▼', 'החלפת ערוץ') + h('◄', 'רשימת ערוצים') + h('OK', 'אפשרויות')
      : h('OK', 'צפייה') + longOk;
    var base = nav + h('BACK', inPlayer ? 'למדריך' : 'חזרה') + (inPlayer ? '' : h('0-9', 'מספר ערוץ'));
    if (p === 'colors') return base + colours;
    if (p === 'magic') return base + (inPlayer ? '' : '<span class="hint">כפתורי צבע: גם דרך ··· בשלט</span>') + colours;
    return base + h('☰', 'הגדרות'); // android remotes: no colour keys
  }
  function renderHints() { if (el.hints) el.hints.innerHTML = hintHtml(false); renderOsdHints(); }
  function renderOsdHints() { if (el.osdHints) el.osdHints.innerHTML = hintHtml(true); }

  function runAction(action) {
    var inPlayer = state.overlay === 'player';
    var ch = inPlayer ? state.playing : state.list[state.gridIdx];
    switch (action) {
      case 'menu': if (inPlayer) playerMenu(); else channelMenu(ch); break;
      case 'fav': if (ch) toggleFav(ch); break;
      case 'favorites': if (inPlayer) closePlayer(true); if (state.cats.some(function (c) { return c.id === 'FAVORITES'; })) { selectCategory('FAVORITES'); focusGrid(0); } else toast('אין עדיין מועדפים – הוסיפו ⭐ מתפריט האפשרויות'); break;
      case 'dub': if (inPlayer) toggleDub(); else if (ch && foreign(ch)) watchDubbed(ch); else toggleDub(); break;
      case 'picture': setSetting('pic', cycle(PICS, S.pic)); toast('✨ מצב תמונה: ' + label(PICS, S.pic)); break;
      case 'aspect': setSetting('aspect', cycle(ASPECTS, S.aspect)); toast('🖼️ ' + label(ASPECTS, S.aspect)); break;
      case 'drawer': if (inPlayer) openDrawer(); else focusRail(state.railIdx); break;
      case 'info': if (ch) showInfo(ch); break;
      case 'search': if (inPlayer) closePlayer(true); focusSearch(); break;
      case 'all': if (inPlayer) closePlayer(true); selectCategory('ALL'); focusGrid(0); break;
      case 'recent': if (inPlayer) closePlayer(true); if (state.recents.length) { selectCategory('RECENT'); focusGrid(0); } break;
      case 'settings': openSettings(); break;
    }
  }

  // ================================================================== settings
  var QUALITIES = [['auto', 'אוטומטי (לפי הרשת)'], ['max', 'הכי גבוהה'], ['saver', 'חסכונית (SD)']];
  var THEMES = [['oled', 'שחור מלא (OLED)'], ['dark', 'כהה'], ['light', 'בהיר']];
  var SCALES = [[0.85, 'קטן'], [1, 'רגיל'], [1.15, 'גדול'], [1.3, 'גדול מאוד'], [1.5, 'ענק']];
  var COLS = [[0, 'אוטומטי'], [3, '3'], [4, '4'], [5, '5'], [6, '6'], [7, '7'], [8, '8']];
  var PERF = [['auto', 'אוטומטי'], ['fast', 'מהיר (בלי אנימציות)'], ['full', 'מלא']];
  var STARTS = [['ALL', 'כל הערוצים'], ['FAVORITES', 'מועדפים'], ['LAST', 'הקטגוריה האחרונה']];
  var LAT = [['stable', 'יציב (מומלץ)'], ['low', 'השהיה נמוכה']];
  var OSDS = [[3, '3 שניות'], [5, '5 שניות'], [8, '8 שניות'], [12, '12 שניות']];
  var SAVERS = [[0, 'כבוי'], [2, '2 דקות'], [5, '5 דקות'], [10, '10 דקות'], [30, '30 דקות']];
  var NUMW = [[0.8, 'מהיר'], [1.2, 'רגיל'], [2, 'איטי']];
  var LONGOK = [['menu', 'תפריט אפשרויות'], ['fav', 'מועדף'], ['info', 'מידע ומקור']];
  var ZAPS = [['category', 'בתוך הקטגוריה'], ['all', 'כל הערוצים']];

  function choice(id, labelText, list, help, after) {
    return { type: 'choice', id: id, label: labelText, list: list, help: help,
      get: function () { return label(list, S[id]); },
      step: function (st) { setSetting(id, cycle(list, S[id], st)); if (after) after(); } };
  }
  function toggle(id, labelText, help, after) {
    return { type: 'toggle', id: id, label: labelText, help: help, on: function () { return !!S[id]; },
      act: function () { setSetting(id, !S[id]); if (after) after(); } };
  }
  function dubChoice(key, labelText, list, help) {
    return { type: 'choice', label: labelText, help: help, get: function () { return label(list, Dub.settings[key]); },
      step: function (st) { var p = {}; p[key] = cycle(list, Dub.settings[key], st); Dub.save(p); } };
  }
  function dubToggle(key, labelText, help) {
    return { type: 'toggle', label: labelText, help: help, on: function () { return !!Dub.settings[key]; },
      act: function () { var p = {}; p[key] = !Dub.settings[key]; Dub.save(p); if (key === 'enabled' && !p[key]) Dub.detach(); } };
  }
  function dubText(key, labelText, help, placeholder) {
    return { type: 'text', label: labelText, help: help, placeholder: placeholder, secret: true,
      get: function () { return Dub.settings[key] || ''; }, set: function (v) { var p = {}; p[key] = v.trim(); Dub.save(p); } };
  }
  function action(labelText, act, help, danger) { return { type: 'action', label: labelText, act: act, help: help, danger: danger }; }
  function info(labelText, value) { return { type: 'info', label: labelText, get: typeof value === 'function' ? value : function () { return value; } }; }
  function sep(text) { return { type: 'sep', label: text }; }

  function settingsSections() {
    var cats = [];
    (window.CHANNELS_DATA || []).forEach(function (c) { if (cats.indexOf(c.category) === -1) cats.push(c.category); });
    var catRows = cats.map(function (c) {
      return { type: 'toggle', label: catIcon(c) + ' ' + splitCat(c), on: function () { return (S.hiddenCats || []).indexOf(c) === -1; },
        act: function () {
          var h = (S.hiddenCats || []).slice(), i = h.indexOf(c);
          if (i === -1) h.push(c); else h.splice(i, 1);
          setSetting('hiddenCats', h);
        } };
    });
    var d = state.device;
    var secs = [
      { id: 'display', ico: '🖥️', name: 'תצוגה', desc: 'גודל, צבעים ומה מופיע במסך', rows: [
        choice('theme', 'ערכת צבעים', THEMES),
        { type: 'choice', label: 'צבע הדגשה', help: readSystemColors() ? 'צבעי המערכת = הצבעים של הטפט (Material You)' : 'ערכות הצבע של Nuvio',
          get: function () { return label(PALETTES, S.accent) + (S.accent === 'auto' ? ' (' + label(PALETTES, accentName()) + ')' : ''); },
          step: function (st) {
            var list = PALETTES.filter(function (p) { return p[0] !== 'system' || readSystemColors(); });
            setSetting('accent', cycle(list, S.accent, st));
          } },
        { type: 'choice', tvOnly: true, label: 'גודל הממשק', help: 'הגדלה לצפייה מרחוק – משנה את כל הטקסטים והכרטיסים', get: function () { return label(SCALES, S.uiScale); },
          step: function (st) { setSetting('uiScale', cycle(SCALES, S.uiScale, st)); } },
        { type: 'choice', label: 'כרטיסים בשורה', get: function () { return S.cols ? String(S.cols) : 'אוטומטי (' + state.cols + ')'; },
          step: function (st) { setSetting('cols', cycle(COLS, S.cols, st)); } },
        tvRow(toggle('hero', 'הצגת פרטי הערוץ הנבחר (למעלה)')),
        toggle('numbers', 'מספרי ערוצים על הכרטיסים'),
        toggle('showSource', 'הצגת מקור השידור', 'שורת "מקור" בפרטי הערוץ ובנגן', function () { updateHero(state.list[state.gridIdx]); }),
        tvRow(toggle('hints', 'שורת עזרה לשלט בתחתית')),
        { type: 'choice', label: 'ביצועים', help: 'מהיר = בלי אנימציות, למכשירים חלשים. ' + (S.perf === 'auto' ? 'זוהה: ' + (state.lowEnd ? 'מכשיר חלש – מצב מהיר' : 'מצב מלא') : ''),
          get: function () { return label(PERF, S.perf); }, step: function (st) { setSetting('perf', cycle(PERF, S.perf, st)); } }
      ].concat(NATIVE && NATIVE.launchSound ? [
        { type: 'toggle', label: 'צליל פתיחה', help: 'צליל קצר כשהאפליקציה נפתחת (שקט כשהמכשיר על שקט או רטט)',
          on: function () { return native('launchSound') !== false; }, act: function () { native('setLaunchSound', native('launchSound') === false); } }
      ] : []) },
      { id: 'channels', ico: '📺', name: 'ערוצים', desc: 'מה מופיע במדריך ואיך האפליקציה נפתחת', rows: [
        choice('startCat', 'בפתיחת האפליקציה', STARTS),
        toggle('autoplay', 'לנגן את הערוץ האחרון בפתיחה'),
        info('ערוצים מוסתרים', function () { return (S.hidden || []).length ? (S.hidden || []).length + ' ערוצים' : 'אין'; }),
        action('החזרת כל הערוצים המוסתרים', function () { setSetting('hidden', []); toast('כל הערוצים חזרו'); }),
        action('ניקוי "צפית לאחרונה"', function () { state.recents = []; lsSet('israel_tv_recents', []); buildCategories(); toast('נוקה'); }),
        action('מחיקת כל המועדפים', function () { confirmBox('למחוק את כל המועדפים?', function () { state.favorites = []; lsSet('israel_tv_favorites', []); buildCategories(); filterList(); toast('המועדפים נמחקו'); }); }, '', true)
      ].concat(IS_WEB ? [toggle('hideAppOnly', 'הסתרת ערוצים שפועלים רק באפליקציה', 'ערוצי HTTP שהדפדפן חוסם')] : [])
        .concat([sep('קטגוריות שמוצגות')]).concat(catRows) },
      { id: 'player', ico: '▶️', name: 'נגן', desc: 'תמונה, איכות והחלפת ערוצים', rows: [
        choice('pic', 'מצב תמונה', PICS), choice('aspect', 'גודל תמונה', ASPECTS),
        toggle('sharpen', 'חידוד לשידורי SD ישנים', 'פילטר חידוד – כבד יותר על מעבדים חלשים'),
        choice('quality', 'איכות שידור', QUALITIES), choice('latency', 'סוג חיבור', LAT, 'השהיה נמוכה = קרוב יותר לשידור החי, אבל יותר עצירות'),
        toggle('retry', 'התחברות מחדש אוטומטית כשהשידור נקטע'),
        choice('zap', 'החלפת ערוצים ב-▲▼', ZAPS),
        { type: 'toggle', label: 'כיוון החצים בנגן', help: S.upNext ? '▲ = ערוץ הבא' : '▲ = ערוץ קודם (מספר נמוך)', on: function () { return S.upNext; },
          act: function () { setSetting('upNext', !S.upNext); } },
        choice('osdSec', 'משך הצגת פרטי הערוץ בנגן', OSDS),
        toggle('osdClock', 'שעון בנגן')
      ] },
      { id: 'remote', tvOnly: true, ico: '🎮', name: 'שלט רחוק', desc: 'זוהה: ' + remoteName(), rows: [
        { type: 'choice', label: 'סוג השלט', help: 'קובע את ההסברים במסך. אוטומטי: לפי סוג המכשיר, ועובר לכפתורי צבע ברגע שלוחצים על אחד',
          get: function () { return label(REMOTES, S.remote); }, step: function (st) { setSetting('remote', cycle(REMOTES, S.remote, st)); } },
        { type: 'choice', label: '🔴 כפתור אדום', get: function () { return label(ACTIONS, S.keyRed); }, step: function (st) { setSetting('keyRed', cycle(ACTIONS, S.keyRed, st)); } },
        { type: 'choice', label: '🟢 כפתור ירוק', get: function () { return label(ACTIONS, S.keyGreen); }, step: function (st) { setSetting('keyGreen', cycle(ACTIONS, S.keyGreen, st)); } },
        { type: 'choice', label: '🟡 כפתור צהוב', get: function () { return label(ACTIONS, S.keyYellow); }, step: function (st) { setSetting('keyYellow', cycle(ACTIONS, S.keyYellow, st)); } },
        { type: 'choice', label: '🔵 כפתור כחול', get: function () { return label(ACTIONS, S.keyBlue); }, step: function (st) { setSetting('keyBlue', cycle(ACTIONS, S.keyBlue, st)); } },
        choice('longOk', 'לחיצה ארוכה על OK', LONGOK, 'עובד בכל שלט – גם בלי כפתורי צבע'),
        choice('numWait', 'זמן המתנה בהקלדת מספר ערוץ', NUMW)
      ] },
      { id: 'dub', ico: '🎙️', name: 'דיבוב לעברית', desc: 'תרגום והקראה בעברית של ערוצים זרים', rows: Dub ? [
        dubToggle('enabled', 'דיבוב פעיל'),
        dubChoice('engine', 'מנוע תרגום', (Dub.NO_LOCAL ? [] : [['local', 'בלי מפתח – זיהוי דיבור במכשיר']]).concat([
          ['gemini', 'Google Gemini (מפתח חינמי, הכי מדויק)'], ['openai', 'OpenAI (בתשלום)']]),
          Dub.NO_LOCAL ? 'בטלוויזיה הדיבוב עובד דרך Gemini – זיהוי דיבור בתוך הטלוויזיה צורך יותר זיכרון ממה שיש לה'
            : 'בלי מפתח עובד מיד; עם מפתח Gemini התרגום מדויק יותר ומזהה דוברים'),
        info('מצב', function () { return Dub.missingConfig() || (Dub.settings.enabled ? '✅ מוכן' : 'כבוי'); }),
        action('📱 חיבור מפתח מהטלפון (QR)', function () { pairKey(); }, 'בלי הקלדה בשלט: סורקים, מדביקים בטלפון, והמפתח מגיע מוצפן'),
        dubText('geminiKey', 'מפתח Gemini API', 'מפתח חינמי: aistudio.google.com/apikey • נשמר רק במכשיר', 'AIza...'),
        dubText('openaiKey', 'מפתח OpenAI API', 'נשמר רק במכשיר', 'sk-...'),
        dubChoice('tts', 'קול הדיבוב', [['auto', 'אוטומטי'], ['browser', 'קול המכשיר'], ['google', 'Google'], ['openai', 'OpenAI'], ['none', 'כתוביות בלבד']]),
        { type: 'choice', label: 'מהירות דיבור', get: function () { return Number(Dub.settings.rate).toFixed(2); },
          step: function (st) { Dub.save({ rate: clamp(Math.round((Dub.settings.rate + 0.05 * st) * 100) / 100, 0.8, 1.6) }); } },
        { type: 'choice', label: 'עוצמת הסאונד המקורי בזמן דיבוב', get: function () { return Math.round(Dub.settings.duck * 100) + '%'; },
          step: function (st) { Dub.save({ duck: clamp(Math.round((Dub.settings.duck + 0.05 * st) * 100) / 100, 0.1, 1) }); } },
      ].concat(Dub.IS_TV ? [info('סנכרון לשפתיים', 'אוטומטי בטלוויזיה – כל משפט נאמר כשהתמונה מגיעה אליו')] : [
        dubToggle('sync', 'סנכרון לשפתיים', 'התמונה מושהית כדי שהדיבוב יגיע בזמן'),
        dubChoice('delay', 'השהיית תמונה', [[6, '6 שניות'], [8, '8 שניות'], [10, '10 שניות'], [12, '12 שניות'], [15, '15 שניות'], [20, '20 שניות']])
      ]).concat([
        dubToggle('subtitles', 'כתוביות בעברית'),
        dubToggle('showOriginal', 'גם הטקסט המקורי'),
        action('🔊 בדיקת קול', function () { Dub.unlockAudio(); Dub.testVoice(); })
      ]) : [info('דיבוב', 'לא זמין במכשיר הזה')] },
      { id: 'kids', ico: '🧸', name: 'מצב ילדים', desc: 'רק ערוצי ילדים, ויציאה עם קוד', rows: [
        { type: 'toggle', label: 'מצב ילדים', help: 'מציג רק ערוצי ילדים (בעברית ומהעולם) ותחנת רדיו לילדים', on: function () { return S.kids; },
          act: function () { if (S.kids) askPin(function () { setSetting('kids', false); renderSettings(); }); else { setSetting('kids', true); toast('🧸 מצב ילדים פועל'); } } },
        { type: 'action', label: S.kidsPin ? 'שינוי קוד יציאה (' + S.kidsPin.replace(/./g, '•') + ')' : 'הגדרת קוד יציאה (4 ספרות)', help: 'בלי קוד – כל אחד יכול לצאת ממצב ילדים',
          act: function () { askPin(function () { newPin(); }); } }
      ] },
      { id: 'saver', tvOnly: true, ico: '🌙', name: 'שומר מסך', desc: 'הגנה על מסכי OLED כשאין צפייה', rows: [
        choice('saver', 'הפעלה אחרי', SAVERS, 'רק במדריך או כשהשידור מושהה – לא בזמן צפייה')
      ] },
      { id: 'updates', ico: '⬆️', name: 'עדכונים', desc: 'עדכונים מגיעים ישירות מ-GitHub', rows: [
        { type: 'card', state: updateCardState, html: updateCardHtml, act: updateCardAction },
        info('גרסה מותקנת', (d.appVersion ? 'אפליקציה ' + d.appVersion + ' · ' : '') + BUILD.version + (BUILD.date ? ' (' + BUILD.date + ')' : '')),
        info('גרסה פעילה', function () { return LIVE ? LIVE.version + ' – עודכנה אונליין' : BUILD.version; }),
        toggle('autoUpdate', 'בדיקה אוטומטית בכל פתיחה'),
        toggle('liveUpdates', 'עדכון אונליין של המסך, הקוד והערוצים', 'עדכונים קטנים מגיעים לכל המכשירים בלי להתקין מחדש'),
        action('📋 מה חדש (GitHub)', function () { openUrl(RELEASES); })
      ] },
      { id: 'about', ico: 'ℹ️', name: 'אודות ומכשיר', desc: 'פרטי המכשיר שזוהה, מקורות ואיפוס', rows: [
        info('מכשיר', function () { return d.platform + (d.model ? ' · ' + d.model : ''); }),
        info('מערכת', function () { return d.os || '—'; }),
        info('מסך', function () { return screen.width + '×' + screen.height + ' (תצוגה ' + window.innerWidth + '×' + window.innerHeight + ')'; }),
        info('דפדפן המערכת', function () { var m = /Chrome\/([\d.]+)/.exec(UA); return m ? 'Chromium ' + m[1] : UA.slice(0, 60); }),
        info('שלט', remoteName),
        info('סגירות לא צפויות', crashText),
        info('שגיאה אחרונה', function () { return errors.length ? when(errors[0].t) + ' · ' + errors[0].m + ' (' + errors[0].at + ')' : 'אין'; }),
        action('ניקוי יומן התקלות', function () { crashes = []; errors = []; lsSet('itv_crashes', []); lsSet('itv_errors', []); toast('נוקה'); }),
        info('ערוצים', function () { return (window.CHANNELS_DATA || []).length + ' (מתוכם ' + state.all.length + ' מוצגים)'; }),
        action('📋 מאיפה מגיע כל ערוץ', function () { openUrl(REPO + '/blob/master/SOURCES.md'); }),
        action('🌐 הפרויקט ב-GitHub', function () { openUrl(REPO); }),
        action('📲 האפליקציה לשאר המכשירים', function () { showGetApp(); }),
        action('איפוס כל ההגדרות', function () { confirmBox('לאפס את כל ההגדרות? (מועדפים נשמרים)', function () { try { localStorage.removeItem('itv_settings'); } catch (e) {} location.reload(); }); }, '', true)
      ] }
    ];
    if (PHONE) {
      secs = secs.filter(function (s) { return !s.tvOnly; });
      secs.forEach(function (s) { s.rows = s.rows.filter(function (r) { return !r.tvOnly; }); });
    }
    return secs;
  }

  function tvRow(r) { r.tvOnly = true; return r; }

  var sections = [];
  function openSettings(sectionId) {
    if (S.kids && S.kidsPin && state.overlay !== 'settings') { askPin(function () { reallyOpenSettings(sectionId); }); return; }
    reallyOpenSettings(sectionId);
  }
  function reallyOpenSettings(sectionId) {
    if (state.overlay === 'menu') closeMenu();
    sections = settingsSections();
    state.setSection = 0;
    if (typeof sectionId === 'string') for (var i = 0; i < sections.length; i++) if (sections[i].id === sectionId) state.setSection = i;
    el.settingsNav.innerHTML = sections.map(function (s, i) {
      return '<div class="nav-item" data-i="' + i + '"><span class="ni-ico">' + s.ico + '</span>' + esc(s.name) + '</div>';
    }).join('');
    if (state.overlay && state.overlay !== 'settings') state.stack.push(state.overlay);
    state.overlay = 'settings';
    state.setZone = typeof sectionId === 'string' ? 'rows' : 'nav';
    state.setIdx = 0;
    el.settings.classList.remove('hidden');
    renderSettings();
    if (PHONE && state.tab !== 'settings' && state.overlay === 'settings' && !state.stack.length) setTab('settings', true);
  }
  function closeSettings(fromTab) {
    blurInputs();
    el.settings.classList.add('hidden');
    state.overlay = state.stack.pop() || null;
    if (state.overlay === 'player') { renderOsdHints(); showOsd(); }
    renderHints();
    markFocus();
    if (PHONE && state.tab === 'settings' && fromTab !== true) setTab(state.prevTab || 'home', true);
  }
  function rowsOf() { return (sections[state.setSection] || { rows: [] }).rows.filter(function (r) { return r.type !== 'sep'; }); }

  function renderSettings() {
    sections = settingsSections();
    var sec = sections[state.setSection];
    var navs = el.settingsNav.children;
    for (var i = 0; i < navs.length; i++) toggleClass(navs[i], 'active', i === state.setSection);
    el.settingsH.textContent = sec.ico + ' ' + sec.name;
    el.settingsP.textContent = sec.desc || '';
    var n = 0;
    el.settingsRows.innerHTML = sec.rows.map(function (r) {
      if (r.type === 'sep') return '<div class="set-sep">' + esc(r.label) + '</div>';
      if (r.type === 'card') return '<div class="set-row upd-row" data-i="' + (n++) + '"><div class="upd-card ' + r.state() + '">' + r.html() + '</div></div>';
      var val = '';
      if (r.type === 'choice') val = '<span class="arr">▶</span><span class="v">' + esc(r.get()) + '</span><span class="arr">◀</span>';
      else if (r.type === 'toggle') val = '<span class="switch' + (r.on() ? ' on' : '') + '"></span>';
      else if (r.type === 'info') val = '<span class="v">' + esc(r.get()) + '</span>';
      else if (r.type === 'text') val = '<input type="' + (r.secret ? 'password' : 'text') + '" placeholder="' + esc(r.placeholder || '') + '" value="' + esc(r.get()) + '">';
      return '<div class="set-row ' + r.type + (r.danger ? ' danger' : '') + '" data-i="' + (n++) + '"><div class="sr-text"><div class="sr-label">' + esc(r.label) + '</div>' +
        (r.help ? '<div class="sr-help">' + esc(r.help) + '</div>' : '') + '</div><div class="sr-val">' + val + '</div></div>';
    }).join('');
    state.setIdx = clamp(state.setIdx, 0, Math.max(0, n - 1));
    markFocus();
  }

  function settingsKey(code, e) {
    if (state.setZone === 'nav') {
      if (code === 38 || code === 40) {
        state.setSection = clamp(state.setSection + (code === 40 ? 1 : -1), 0, sections.length - 1);
        state.setIdx = 0;
        renderSettings();
      } else if (code === 37 || code === 13) { if (rowsOf().length) { state.setZone = 'rows'; markFocus(); } }
      else if (isBack(code)) closeSettings();
      return;
    }
    var rows = rowsOf();
    var r = rows[state.setIdx];
    var input = r && r.type === 'text' ? el.settingsRows.querySelectorAll('.set-row')[state.setIdx].querySelector('input') : null;
    if (input && document.activeElement === input) {
      if (code === 13 || isBack(code) || code === 38 || code === 40) {
        r.set(input.value);
        input.blur();
        toast('נשמר');
        if (code === 38 || code === 40) { state.setIdx = clamp(state.setIdx + (code === 40 ? 1 : -1), 0, rows.length - 1); markFocus(); }
        e.preventDefault();
      }
      return;
    }
    if (code === 38 || code === 40) { state.setIdx = clamp(state.setIdx + (code === 40 ? 1 : -1), 0, rows.length - 1); markFocus(); return; }
    if (isBack(code)) { state.setZone = 'nav'; markFocus(); return; }
    if (!r) return;
    if (r.type === 'choice' && (code === 37 || code === 39 || code === 13)) { r.step(code === 39 ? -1 : 1); renderSettings(); return; }
    if (code === 39) { state.setZone = 'nav'; markFocus(); return; }
    if (code === 13) {
      if (r.type === 'toggle' || r.type === 'action' || r.type === 'card') { r.act(); if (state.overlay === 'settings') renderSettings(); }
      else if (r.type === 'text' && input) { input.focus(); }
    }
  }
  function blurInputs() {
    var a = document.activeElement;
    if (a && a.tagName === 'INPUT' && el.settings.contains(a)) {
      var rows = rowsOf(), r = rows[state.setIdx];
      if (r && r.set) r.set(a.value);
      a.blur();
    }
  }

  function confirmBox(text, yes) {
    openDialog({ ico: '❓', title: text, buttons: [{ label: 'ביטול', primary: true }, { label: 'כן', act: yes }] });
  }

  // ---- kids mode PIN
  function askPin(ok) {
    if (!S.kidsPin) { ok(); return; }
    pinDialog('הקלידו את קוד היציאה', function (pin) {
      if (pin === S.kidsPin) ok(); else toast('❌ קוד שגוי');
    });
  }
  function newPin() {
    pinDialog('קוד חדש (4 ספרות)', function (pin) { setSetting('kidsPin', pin); toast('🔒 הקוד נשמר'); if (state.overlay === 'settings') renderSettings(); });
  }
  function pinDialog(title, done) {
    var pin = '';
    var draw = function () {
      var spans = el.dialogBody.querySelectorAll('.pin span');
      for (var i = 0; i < spans.length; i++) { spans[i].textContent = i < pin.length ? '•' : ''; toggleClass(spans[i], 'on', i === pin.length); }
    };
    var keys = '';
    for (var d = 1; d <= 10; d++) keys += '<button class="btn dlg-focus" data-d="' + (d % 10) + '">' + (d % 10) + '</button>';
    keys += '<button class="btn dlg-focus" data-d="del">⌫</button>';
    openDialog({ ico: '🔒', title: title, html: '<div class="pin"><span></span><span></span><span></span><span></span></div>' +
      '<p>מקשי הספרות בשלט, או בחירה כאן:</p><div>' + keys + '</div>', buttons: [{ label: 'ביטול' }] });
    state.pinInput = function (digit) {
      if (digit === 'del') pin = pin.slice(0, -1); else if (pin.length < 4) pin += digit;
      draw();
      if (pin.length === 4) { state.pinInput = null; closeDialog(); done(pin); }
    };
    Array.prototype.forEach.call(el.dialogBody.querySelectorAll('[data-d]'), function (b) {
      b.onclickAction = function () { if (state.pinInput) state.pinInput(b.getAttribute('data-d')); };
      b.addEventListener('click', function (e) { e.stopPropagation(); b.onclickAction(); });
    });
    state.dialog.onClose = function () { state.pinInput = null; };
    draw();
  }

  // ================================================================== updates
  function xhrJson(url, timeout, ok, fail) {
    var x = new XMLHttpRequest();
    x.open('GET', url, true);
    x.timeout = timeout || 10000;
    x.onload = function () {
      if (x.status >= 200 && x.status < 300) { try { ok(JSON.parse(x.responseText)); } catch (e) { fail && fail(e); } }
      else if (fail) fail(new Error('HTTP ' + x.status));
    };
    x.onerror = x.ontimeout = function () { if (fail) fail(new Error('network')); };
    x.send();
  }
  function prefetch(urls, done) {
    var left = urls.length, failed = false;
    urls.forEach(function (u) {
      var x = new XMLHttpRequest();
      x.open('GET', u, true);
      x.timeout = 60000;
      x.onload = function () { if (x.status !== 200 || x.responseText.length < 20) failed = true; if (--left === 0) done(!failed); };
      x.onerror = x.ontimeout = function () { failed = true; if (--left === 0) done(false); };
      x.send();
    });
  }
  // LG: no "download the IPK" prompts – the IPK only holds the web app, and the live update below brings all of it
  // (screen, code, channels) by itself; it is applied automatically (applyLiveIfIdle)
  function nativeKey() {
    if (PLATFORM === 'android') return 'android';
    if (PLATFORM === 'windows') return 'windows';
    if (PLATFORM === 'tizen') return 'tizen';
    if (PLATFORM === 'desktop') return /Mac/.test(UA) ? 'mac' : 'linux';
    return '';
  }
  function updateStatusText() {
    var u = state.update;
    if (state.checking) return 'בודק...';
    if (!u) return 'עוד לא נבדק';
    if (u.error) return 'לא הצלחנו לבדוק (' + u.error + ')';
    var parts = [];
    if (u.native) parts.push('גרסה חדשה של האפליקציה: ' + u.native.version);
    if (state.liveReady) parts.push('עדכון ' + state.liveReady.version + ' הורד – יופעל בפתיחה הבאה');
    else if (u.liveDownloading) parts.push('מוריד עדכון ' + u.data.version + '...');
    return parts.length ? parts.join(' · ') : '✅ הגרסה עדכנית (' + u.data.version + ')';
  }

  function checkUpdates(manual) {
    state.checking = true;
    if (manual) toast('בודק עדכונים...');
    xhrJson(REMOTE + 'version.json?t=' + Date.now(), 12000, function (data) {
      state.checking = false;
      var u = { data: data };
      state.update = u;
      var activeCode = LIVE ? LIVE.code : BUILD.code;
      // the web site: a newer build is published (an old copy may be cached)
      if (IS_SITE) {
        if (data.code > BUILD.code) { showUpdateButton(); if (manual) offerReload(data); }
        else if (manual) toast('✅ הגרסה עדכנית');
        refreshSettingsIfOpen();
        return;
      }
      var nk = nativeKey();
      var n = data.native && data.native[nk];
      if (n && n.app > (BUILD.app || 0)) { u.native = n; showUpdateButton(); }
      // the newest GitHub release too (it can be out before version.json): a new app version opens the update modal
      checkRelease(u, function () {
        state.lastCheck = Date.now();
        if (u.native) {
          showUpdateButton();
          if (!manual) offerOnce(u.native);
          else if (state.overlay !== 'dialog') showUpdateDialog();
        }
        refreshSettingsIfOpen();
      });
      if (data.code > activeCode && S.liveUpdates && (data.shell || 0) <= (BUILD.shell || 0)) {
        u.liveDownloading = true;
        var q = '?v=' + data.code;
        prefetch(['style.css', 'ui.js', 'channels.js', 'dub.js', 'app.js'].map(function (f) { return REMOTE + f + q; }), function (ok) {
          u.liveDownloading = false;
          if (ok) {
            state.liveReady = { code: data.code, version: data.version, shell: data.shell || 0 };
            try { localStorage.setItem('itv_live', JSON.stringify(state.liveReady)); localStorage.removeItem('itv_bad'); } catch (e) {}
            if (!applyLiveIfIdle()) {
              showUpdateButton();
              toast('⬆️ עדכון ' + data.version + ' מוכן – יופעל בפתיחה הבאה (או בהגדרות ← עדכונים)');
            }
          }
          refreshSettingsIfOpen();
        });
      } else if (manual && !u.native) toast('✅ הגרסה עדכנית (' + (LIVE ? LIVE.version : BUILD.version) + ')');
      if (manual && (u.native || data.code > activeCode)) showUpdateDialog();
      refreshSettingsIfOpen();
    }, function (err) {
      state.checking = false;
      state.update = { error: err && err.message === 'network' ? 'אין חיבור' : 'שגיאה' };
      if (manual) toast('לא הצלחנו לבדוק עדכונים');
      refreshSettingsIfOpen();
    });
  }
  /** LG: a downloaded live update is switched to at once when nothing is playing and no menu is open (else later). */
  function applyLiveIfIdle() {
    if (PLATFORM !== 'webos' || !state.liveReady || state.playing || state.overlay) return false;
    toast('⬆️ מתעדכן לגרסה ' + state.liveReady.version + '...');
    setTimeout(function () { try { sessionStorage.removeItem('itv_local'); } catch (e) {} location.reload(); }, 1500);
    return true;
  }
  function refreshSettingsIfOpen() { if (state.overlay === 'settings') renderSettings(); }
  function showUpdateButton() { el.btnUpdate.classList.remove('hidden'); toggleClass(el.avatarDot, 'hidden', false); }

  // ---- GitHub Releases (api.github.com, no key: 60 checks an hour per address – one per start is plenty)
  var RELEASES_API = 'https://api.github.com/repos/dor2500/israel-tv-plus/releases/latest';
  var RELEASE_ASSETS = { android: 'IsraelTVPlus-android.apk', windows: 'IsraelTVPlus-windows-setup.exe' };
  function verNum(v) { var p = String(v || '0').replace(/^v/i, '').split('.'); return (+p[0] || 0) * 1e6 + (+p[1] || 0) * 1e3 + (+p[2] || 0); }
  function installedAppVersion() { return state.device.appVersion || BUILD.version; }
  function releaseNotes(body) {
    return String(body || '').split(/\r?\n/).filter(function (l) { return /^\s*[-*•]\s+/.test(l); }).slice(0, 10).map(function (l) {
      return l.replace(/^\s*[-*•]\s+/, '').replace(/\*\*|__|`/g, '').replace(/\[([^\]]+)\]\([^)]*\)/g, '$1').trim();
    });
  }
  function checkRelease(u, done) {
    var file = RELEASE_ASSETS[nativeKey()];
    if (!file || IS_SITE) { done(); return; }
    xhrJson(RELEASES_API, 10000, function (r) {
      var v = String(r && r.tag_name || '').replace(/^v/i, '');
      var asset = ((r && r.assets) || []).filter(function (a) { return a.name === file; })[0];
      if (asset && !r.draft && !r.prerelease && verNum(v) > verNum(installedAppVersion()) && (!u.native || verNum(v) > verNum(u.native.version))) {
        u.native = { version: v, url: asset.browser_download_url, size: asset.size, notes: releaseNotes(r.body),
          date: String(r.published_at || '').slice(0, 10), release: true };
      }
      done();
    }, function () { done(); });
  }
  /** A new app version opens the update modal by itself once (per version), when nothing else is on screen. */
  function offerOnce(n) {
    if (lsGet('itv_upd_offered', '') === n.version || state.overlay || state.playing) return;
    lsSet('itv_upd_offered', n.version);
    showUpdateDialog();
  }

  // ---- the Pixel-style update card (Settings → Updates, profile sheet)
  function updateCardState() {
    var u = state.update;
    if (state.checking) return 'busy';
    return u && (u.native || state.liveReady) ? 'new' : 'ok';
  }
  function updateCardHtml() {
    var u = state.update, st = updateCardState(), title, sub, btn, icon;
    var ago = state.lastCheck ? 'נבדק לאחרונה ' + when(state.lastCheck).split(' ')[1] : 'עוד לא נבדק בהפעלה הזאת';
    if (st === 'busy') { icon = 'settings'; title = 'בודק עדכונים…'; sub = 'מתחבר ל-GitHub'; btn = ''; }
    else if (st === 'new') {
      icon = 'download';
      title = u.native ? 'גרסה ' + u.native.version + ' זמינה' : 'עדכון ' + state.liveReady.version + ' מוכן';
      sub = u.native ? 'גרסה חדשה של האפליקציה' + (u.native.size ? ' · ' + Math.round(u.native.size / 1048576 * 10) / 10 + 'MB' : '')
        : 'המסך, הקוד והערוצים – יופעל בהפעלה מחדש';
      btn = u.native ? 'עדכון עכשיו' : 'הפעלה מחדש';
    } else {
      icon = 'info'; title = u && u.error ? 'לא הצלחנו לבדוק' : 'המכשיר מעודכן';
      sub = (u && u.error ? u.error + ' · ' : 'גרסה ' + (LIVE ? LIVE.version : BUILD.version) + ' · ') + ago;
      btn = 'בדיקת עדכונים';
    }
    return '<div class="uc-ico">' + I(icon) + '</div><div class="uc-title">' + esc(title) + '</div><div class="uc-sub">' + esc(sub) + '</div>' +
      (btn ? '<div class="uc-btn">' + esc(btn) + '</div>' : '');
  }
  function updateCardAction() {
    var u = state.update;
    if (state.checking) return;
    if (u && (u.native || state.liveReady)) showUpdateDialog(); else checkUpdates(true);
  }
  function offerReload(data) {
    openDialog({ ico: '⬆️', title: 'גרסה ' + data.version + ' זמינה', html: notesHtml(data),
      buttons: [{ label: 'רענון עכשיו', primary: true, act: function () { location.reload(true); } }, { label: 'אחר כך' }] });
  }
  function notesHtml(data) {
    var notes = data.notes || [];
    return notes.length ? '<ul>' + notes.map(function (t) { return '<li>' + esc(t) + '</li>'; }).join('') + '</ul>' : '';
  }

  /**
   * The update modal (frosted glass on phones): the app icon, "installed → new" version chips and the release notes;
   * "update now" downloads in place – the notes fold away and an M3 Expressive linear progress bar takes their spot.
   */
  function showUpdateDialog() {
    var u = state.update;
    if (!u || !u.data) { checkUpdates(true); return; }
    if (IS_SITE) { offerReload(u.data); return; }
    // notes: version.json's list when it describes the same version (one line each), else the release's bullet lines
    var sameVer = !u.native || u.data.version === u.native.version;
    var buttons = [], notes = !sameVer && u.native.notes && u.native.notes.length ? u.native.notes : (sameVer ? u.data.notes || [] : []);
    var from = u.native ? installedAppVersion() : (LIVE ? LIVE.version : BUILD.version);
    var to = u.native ? u.native.version : state.liveReady ? state.liveReady.version : u.data.version;
    var html = '<div class="upd-hero"><img alt="" src="' + (window.ITV_BASE || '') + 'icon.png"><div><b>' +
      (u.native ? 'עדכון לאפליקציה' : 'עדכון מוכן') + '</b><div class="upd-ver"><span>' + esc(from) + '</span><i>←</i><span class="new">' + esc(to) + '</span>' +
      (u.native && u.native.size ? '<i>·</i>' + Math.round(u.native.size / 1048576 * 10) / 10 + 'MB' : '') + '</div></div></div>' +
      (notes.length ? '<div class="upd-notes"><ul>' + notes.map(function (t) { return '<li>' + esc(t) + '</li>'; }).join('') + '</ul></div>' : '') +
      '<div class="upd-dl"><div class="m3p indet" id="upd-prog"><i class="m3p-ind" id="upd-bar"></i><i class="m3p-track"></i></div>' +
      '<p id="upd-text">מתחבר...</p></div>';
    if (u.native) {
      if ((PLATFORM === 'android' && window.AndroidApp && window.AndroidApp.installUpdate) || PLATFORM === 'windows') {
        buttons.push({ label: 'עדכון עכשיו', primary: true, keep: true, act: function () { installNative(u.native); } });
      } else if (PLATFORM === 'webos') {
        html += '<p>בטלוויזיית LG: התקנה מחדש של קובץ ה-IPK (Developer Mode / Homebrew Channel). המסך, הקוד והערוצים מתעדכנים גם בלי זה.</p>';
        buttons.push({ label: '🌐 דף ההורדה', act: function () { openUrl(RELEASES); } });
      } else {
        buttons.push({ label: '🌐 דף ההורדה', primary: true, act: function () { openUrl(u.native.url || RELEASES); } });
      }
    }
    if (state.liveReady) {
      buttons.push({ label: u.native ? 'הפעלה מחדש עם עדכון המסך' : 'הפעלה מחדש עכשיו', primary: !buttons.length,
        act: function () { try { sessionStorage.removeItem('itv_local'); } catch (e) {} location.reload(); } });
    }
    if (!buttons.length) { toast('✅ הגרסה עדכנית'); return; }
    buttons.push({ label: 'אחר כך' });
    openDialog({ ico: '', title: 'עדכון זמין', html: html, buttons: buttons, cls: 'upd' });
  }

  function installNative(n) {
    if (state.overlay !== 'dialog' || !$('upd-prog')) showUpdateDialog();
    var box = el.dialogBox;
    box.classList.add('downloading');
    // the actions become a single "hide" (the download goes on; the system installer opens by itself)
    state.dialog.buttons = [{ label: 'הסתרה', primary: true }];
    el.dialogActions.innerHTML = '<button class="btn primary" data-i="0">הסתרה</button>';
    state.dialogIdx = 0;
    markFocus();
    haptic('start');
    window.__itvUpdate = function (st, v) {
      var prog = $('upd-prog'), bar = $('upd-bar'), txt = $('upd-text');
      if (st === 'progress') {
        if (prog) prog.classList.remove('indet');
        if (bar) bar.style.width = v + '%';
        if (txt) txt.textContent = 'מוריד את גרסה ' + n.version + ' · ' + v + '%';
      } else if (st === 'installing') {
        if (prog) { prog.classList.remove('indet'); prog.classList.add('done'); }
        if (txt) txt.textContent = 'פותח את ההתקנה – אשרו במסך שייפתח';
        haptic('confirm');
      } else if (st === 'error') {
        if (txt) txt.textContent = 'שגיאה: ' + v;
        if (prog) prog.classList.remove('indet');
        haptic('reject');
        toast('⚠️ ההורדה נכשלה');
      }
    };
    if (PLATFORM === 'android') window.AndroidApp.installUpdate(n.url);
    else if (PLATFORM === 'windows') window.chrome.webview.postMessage(JSON.stringify({ update: n.url }));
  }

  // ================================================================== native bridges
  function setupNativeBridge() {
    window.__itvKey = function (code) {
      if (code === 461) backReset(true);
      onKeyDown({ keyCode: code, which: code, target: document.body, preventDefault: function () {}, stopPropagation: function () {} });
      if (code === 13) onKeyUp({ keyCode: 13, which: 13, target: document.body, preventDefault: function () {} });
    };
    // edge-to-edge (Android 15+): the system bar / cutout sizes in CSS px, from the native app
    window.__itvInsets = function (t, r, b, l, k) {
      var s = document.documentElement.style;
      s.setProperty('--sat', (t || 0) + 'px'); s.setProperty('--sar', (r || 0) + 'px');
      s.setProperty('--sab', (b || 0) + 'px'); s.setProperty('--sal', (l || 0) + 'px');
      // k: the keyboard height (Android app 3.2+): the glass bar with the search field rides on top of it
      if (k !== undefined) { state.kbAware = true; s.setProperty('--kb', (k || 0) + 'px'); }
      if (PHONE) { movePill(false); updateTopbar(); }
    };
    // picture-in-picture: only the picture, no controls
    window.__itvPip = function (on) {
      toggleClass(document.body, 'in-pip', !!on);
      if (on) { el.osd.classList.add('off'); closeDrawer(true); if (state.overlay === 'menu') closeMenu(); }
    };
    // the wallpaper changed (Material You)
    window.__itvColors = function () { systemColors = null; applyColors(); };
    // predictive back (Android 14+): the top layer shrinks and follows the finger; the release sends key 461
    window.__itvBackProgress = function (p, fromLeft) {
      var t = backTarget();
      if (!t) return;
      t.classList.add('pb-target');
      var sheet = t.classList.contains('sheet') || t.classList.contains('menu-box');
      t.style.transform = sheet ? 'translateY(' + (p * 6) + 'rem) scale(' + (1 - p * 0.05) + ')'
        : 'translateX(' + ((fromLeft ? 1 : -1) * p * 3) + 'rem) scale(' + (1 - p * 0.1) + ')';
      t.style.borderRadius = (p * 3) + 'rem';
    };
    window.__itvBackCancel = function () { backReset(false); };
    var nativeInsets = native('insets');
    if (nativeInsets) { try { var ins = JSON.parse(nativeInsets); window.__itvInsets(ins.t, ins.r, ins.b, ins.l, ins.k); } catch (e) {} }
    if (PLATFORM === 'tizen' && window.tizen && window.tizen.tvinputdevice) {
      ['ColorF0Red', 'ColorF1Green', 'ColorF2Yellow', 'ColorF3Blue', 'ChannelUp', 'ChannelDown', 'MediaPlayPause', 'MediaPlay', 'MediaPause',
        'Info', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'].forEach(function (k) {
        try { window.tizen.tvinputdevice.registerKey(k); } catch (e) {}
      });
    }
    // LG Magic Remote: the pointer shows and hides (webOS event)
    document.addEventListener('cursorStateChange', function (e) {
      if (e.detail && e.detail.visibility) state.pointer = true;
    });
  }

  /** The layer the back gesture would close. */
  function backTarget() {
    if (state.overlay === 'dialog') return el.dialogBox;
    if (state.overlay === 'menu') return el.menu.querySelector('.menu-box');
    if (state.overlay === 'drawer') return el.drawer;
    if (state.overlay === 'settings') return el.settings.querySelector('.settings-box');
    if (state.overlay === 'player') return el.player;
    if (PHONE && state.tab !== 'home') return $(state.tab === 'settings' ? 'settings' : 'guide-body');
    return null;
  }
  function backReset(committed) {
    var nodes = document.querySelectorAll('.pb-target');
    for (var i = 0; i < nodes.length; i++) {
      var n = nodes[i];
      n.classList.remove('pb-target');
      if (committed) { n.style.transform = ''; n.style.borderRadius = ''; continue; }
      n.style.transition = 'transform .25s cubic-bezier(.2,0,0,1), border-radius .25s';
      n.style.transform = '';
      n.style.borderRadius = '';
      (function (x) { setTimeout(function () { x.style.transition = ''; }, 260); })(n);
    }
  }

  var lunaRefs = [];
  function luna(uri, params, cb) {
    try {
      var b = new window.PalmServiceBridge();
      lunaRefs.push(b);
      b.onservicecallback = function (msg) { try { cb(JSON.parse(msg)); } catch (e) { cb(null); } };
      b.call(uri, JSON.stringify(params || {}));
    } catch (e) { cb(null); }
  }
  function webosLaunch(id, params) {
    try { luna('luna://com.webos.applicationManager/launch', { id: id, params: params || {} }, function () {}); return true; } catch (e) { return false; }
  }

  /** Model / system of the device: shown in Settings → About, and used for the performance and remote defaults. */
  function readDeviceInfo() {
    var d = state.device;
    if (PLATFORM === 'webos' && window.PalmServiceBridge) {
      luna('luna://com.webos.service.tv.systemproperty/getSystemInfo', { keys: ['modelName', 'firmwareVersion', 'sdkVersion', 'UHD'] }, function (r) {
        if (!r || r.returnValue === false) return;
        d.model = r.modelName || '';
        d.os = 'webOS ' + (r.sdkVersion || '') + (r.firmwareVersion ? ' · קושחה ' + r.firmwareVersion : '') + (r.UHD === 'true' ? ' · 4K' : '');
        var major = parseInt(r.sdkVersion, 10);
        if (major && major < 5) { state.lowEnd = true; applySetting('perf'); }
        refreshSettingsIfOpen();
      });
    } else if (PLATFORM === 'android' && window.AndroidApp && window.AndroidApp.deviceInfo) {
      try {
        var a = JSON.parse(window.AndroidApp.deviceInfo());
        d.model = (a.manufacturer || '') + ' ' + (a.model || '');
        d.os = 'Android ' + (a.release || '') + ' (API ' + (a.sdk || '') + ')' + (a.ramMb ? ' · ' + Math.round(a.ramMb / 1024 * 10) / 10 + 'GB RAM' : '');
        d.appVersion = a.versionName || '';
        if (a.lowRam || (a.ramMb && a.ramMb < 2500)) { state.lowEnd = true; applySetting('perf'); }
      } catch (e) {}
    } else if (PLATFORM === 'tizen' && window.tizen && window.tizen.systeminfo) {
      try {
        d.model = window.tizen.systeminfo.getCapability('http://tizen.org/system/model_name');
        d.os = 'Tizen ' + window.tizen.systeminfo.getCapability('http://tizen.org/feature/platform.version');
      } catch (e) {}
    } else {
      var m = /\(([^)]+)\)/.exec(UA);
      d.os = m ? m[1] : '';
    }
    renderHints();
  }

  function openUrl(url) {
    if (!url) return;
    if (PLATFORM === 'android' && window.AndroidApp && window.AndroidApp.openUrl) window.AndroidApp.openUrl(url);
    else if (PLATFORM === 'windows') window.chrome.webview.postMessage(JSON.stringify({ open: url }));
    else if (PLATFORM === 'webos' && webosLaunch('com.webos.app.browser', { target: url })) toast('🌐 נפתח בדפדפן הטלוויזיה');
    else window.open(url, '_blank', 'noopener');
  }

  function openServiceApp(ch) {
    if (PLATFORM === 'android' && window.AndroidApp && window.AndroidApp.openApp) {
      window.AndroidApp.openApp(ch.androidTv || '', ch.android || '', ch.site || ch.url);
      toast('📲 פותח את ' + ch.name + '...');
      return;
    }
    toast('🌐 פותח את ' + ch.name);
    openUrl(ch.site || ch.url);
  }

  function exitApp() {
    toast('להתראות!');
    try {
      if (PLATFORM === 'android' && window.AndroidApp && window.AndroidApp.exit) window.AndroidApp.exit();
      else if (PLATFORM === 'windows') window.chrome.webview.postMessage(JSON.stringify({ exit: true }));
      else if (PLATFORM === 'tizen' && window.tizen) window.tizen.application.getCurrentApplication().exit();
      else if (window.webOS && window.webOS.platformBack) window.webOS.platformBack();
      else if (window.PalmSystem && window.PalmSystem.platformBack) window.PalmSystem.platformBack();
      else window.close();
    } catch (e) { window.close(); }
  }

  // ================================================================== clock / toast / screensaver / number entry
  function setupClock() {
    var tick = function () {
      var d = new Date();
      var t = (d.getHours() < 10 ? '0' : '') + d.getHours() + ':' + (d.getMinutes() < 10 ? '0' : '') + d.getMinutes();
      if (el.clock.textContent !== t) { el.clock.textContent = t; el.osdClock.textContent = t; el.saverClock.textContent = t; }
    };
    tick();
    setInterval(tick, 5000);
  }
  function toast(msg) {
    el.toast.textContent = msg;
    el.toast.classList.remove('hidden');
    if (state.toastTimer) clearTimeout(state.toastTimer);
    state.toastTimer = setTimeout(function () { el.toast.classList.add('hidden'); }, 3200);
  }
  var saverMove = null;
  function resetSaver() {
    if (state.saverOn) {
      state.saverOn = false;
      el.saver.classList.add('hidden');
      clearInterval(saverMove);
    }
    if (state.saverTimer) clearTimeout(state.saverTimer);
    if (!S.saver) return;
    state.saverTimer = setTimeout(function () {
      var m = media();
      if (state.overlay === 'player' && m && !m.paused) { resetSaver(); return; }
      state.saverOn = true;
      el.saver.classList.remove('hidden');
      var move = function () {
        el.saverIn.style.top = (10 + Math.random() * 60) + '%';
        el.saverIn.style.left = (10 + Math.random() * 50) + '%';
      };
      move();
      saverMove = setInterval(move, 20000);
    }, S.saver * 60000);
  }

  function digit(d) {
    if (state.pinInput) { state.pinInput(String(d)); return; }
    state.digits = (state.digits + d).slice(-4);
    var num = parseInt(state.digits, 10);
    var target = null;
    for (var i = 0; i < state.all.length; i++) if (state.all[i].number === num) target = state.all[i];
    el.numhud.classList.remove('hidden');
    el.numhudDigits.textContent = state.digits;
    el.numhudName.textContent = target ? target.name : '...';
    if (state.digitTimer) clearTimeout(state.digitTimer);
    state.digitTimer = setTimeout(function () {
      el.numhud.classList.add('hidden');
      state.digits = '';
      if (!target) { toast('אין ערוץ מספר ' + num); return; }
      var idx = indexInList(target.id);
      if (idx === -1) { state.query = ''; el.searchInput.value = ''; selectCategory('ALL', true); idx = indexInList(target.id); }
      if (state.overlay && state.overlay !== 'player') return;
      state.gridIdx = idx;
      if (!state.overlay) focusGrid(idx);
      playIndex(idx);
    }, (S.numWait || 1.2) * 1000);
  }

  // ================================================================== search
  function focusSearch() {
    if (state.overlay) return;
    var items = topItems();
    focusTop(items.indexOf(el.topSearch));
    el.searchInput.focus();
  }

  // ================================================================== input: keys, long OK, mouse, touch
  function isBack(code) { return code === 461 || code === 27 || code === 10009 || code === 8; }
  function typing(t) { return t && (t.tagName === 'INPUT' || t.tagName === 'TEXTAREA'); }

  function setupInput() {
    window.addEventListener('keydown', onKeyDown);
    window.addEventListener('keyup', onKeyUp);
    el.searchInput.addEventListener('input', function () {
      state.query = el.searchInput.value;
      state.gridIdx = 0;
      filterList();
    });
    el.searchInput.addEventListener('focus', function () { if (!state.overlay) { state.zone = 'top'; state.topIdx = topItems().indexOf(el.topSearch); markFocus(); } });

    // mouse / touch / Magic Remote pointer
    el.railList.addEventListener('click', function (e) {
      var it = e.target.closest('.rail-item');
      if (!it) return;
      var i = parseInt(it.getAttribute('data-i'), 10);
      selectCategory(state.cats[i].id);
      focusGrid(0);
    });
    el.grid.addEventListener('click', function (e) {
      var c = e.target.closest('.card');
      if (!c) return;
      var i = parseInt(c.getAttribute('data-i'), 10);
      focusGrid(i);
      if (Dub) Dub.unlockAudio();
      state.openFrom = PHONE ? c.querySelector('.card-logo').getBoundingClientRect() : null;
      playIndex(i);
    });
    el.grid.addEventListener('contextmenu', function (e) {
      var c = e.target.closest('.card');
      if (!c) return;
      e.preventDefault();
      var i = parseInt(c.getAttribute('data-i'), 10);
      focusGrid(i);
      if (PHONE) haptic('long');
      channelMenu(state.list[i]);
    });
    var lastMove = 0;
    el.grid.addEventListener('mousemove', function (e) {
      if (TOUCH || state.overlay) return;
      var c = e.target.closest('.card');
      if (!c) return;
      var i = parseInt(c.getAttribute('data-i'), 10);
      if (i !== state.gridIdx || state.zone !== 'grid') { state.zone = 'grid'; state.gridIdx = i; markFocusNoScroll(c); updateHero(state.list[i]); }
    });
    window.addEventListener('mousemove', function () {
      var now = Date.now();
      if (now - lastMove > 1000) resetSaver();
      lastMove = now;
      if (state.overlay === 'player') showOsd();
    });
    window.addEventListener('touchstart', resetSaver, { passive: true });
    el.grid.addEventListener('scroll', function () {
      if (el.grid.scrollHeight - el.grid.scrollTop - el.grid.clientHeight < 600) ensureRendered(rendered);
    }, { passive: true });

    el.btnSettings.addEventListener('click', function () { openSettings(); });
    el.btnUpdate.addEventListener('click', showUpdateDialog);
    el.btnGetapp.addEventListener('click', function () { showGetApp(); });
    el.btnKidsExit.addEventListener('click', function () { askPin(function () { setSetting('kids', false); toast('יצאתם ממצב ילדים'); }); });
    toggleClass(el.btnGetapp, 'hidden', !(PLATFORM === 'web' || PLATFORM === 'local'));
    toggleClass(el.btnKidsExit, 'hidden', !S.kids);

    el.osd.addEventListener('click', function (e) {
      var b = e.target.closest('.obtn');
      if (!b) return;
      e.stopPropagation();
      osdAction(b.getAttribute('data-act'));
    });
    el.player.addEventListener('click', function (e) {
      if (e.target.closest('.obtn') || e.target.closest('.drawer') || e.target.closest('.perror-box')) return;
      if (state.swiped && Date.now() - state.swiped < 500) return;
      if (el.osd.classList.contains('off')) showOsd(); else el.osd.classList.add('off');
    });
    el.player.addEventListener('dblclick', function (e) { if (!e.target.closest('.obtn')) toggleFullscreen(); });
    el.perrorRetry.addEventListener('click', function (e) { e.stopPropagation(); if (state.playing) loadStream(state.playing); });
    el.perrorNext.addEventListener('click', function (e) { e.stopPropagation(); zap(1); });
    el.perrorBack.addEventListener('click', function (e) { e.stopPropagation(); closePlayer(false); });
    el.drawerList.addEventListener('click', function (e) {
      var it = e.target.closest('.drawer-item');
      if (!it) return;
      e.stopPropagation();
      var i = parseInt(it.getAttribute('data-i'), 10);
      closeDrawer(true);
      playIndex(i);
    });
    el.menuList.addEventListener('click', function (e) {
      var it = e.target.closest('.menu-item');
      if (it) runMenuItem(parseInt(it.getAttribute('data-i'), 10));
    });
    el.menu.addEventListener('click', function (e) { if (e.target === el.menu) closeMenu(); });
    el.settingsNav.addEventListener('click', function (e) {
      var it = e.target.closest('.nav-item');
      if (!it) return;
      blurInputs();
      state.setSection = parseInt(it.getAttribute('data-i'), 10);
      state.setZone = 'nav';
      state.setIdx = 0;
      renderSettings();
    });
    el.settingsRows.addEventListener('click', function (e) {
      var row = e.target.closest('.set-row');
      if (!row) return;
      state.setZone = 'rows';
      state.setIdx = parseInt(row.getAttribute('data-i'), 10);
      var r = rowsOf()[state.setIdx];
      if (e.target.tagName === 'INPUT') { markFocus(); return; }
      if (r.type === 'choice') { r.step(1); if (PHONE) haptic('segment'); }
      else if (r.type === 'toggle') { r.act(); if (PHONE) haptic(r.on() ? 'on' : 'off'); }
      else if (r.type === 'action' || r.type === 'card') { if (PHONE) haptic('press'); r.act(); }
      if (state.overlay === 'settings') renderSettings();
    });
    el.settingsRows.addEventListener('change', function (e) {
      if (e.target.tagName !== 'INPUT') return;
      var row = e.target.closest('.set-row');
      var r = rowsOf()[parseInt(row.getAttribute('data-i'), 10)];
      if (r && r.set) { r.set(e.target.value); toast('נשמר'); }
    });
    el.settings.addEventListener('click', function (e) { if (e.target === el.settings) closeSettings(); });
    el.dialogActions.addEventListener('click', function (e) {
      var b = e.target.closest('.btn');
      if (!b) return;
      var nodes = dialogButtons();
      pressDialog(nodes.indexOf(b));
    });
    el.dialog.addEventListener('click', function (e) { if (e.target === el.dialog) closeDialog(); });
    [el.video, el.radioAudio].forEach(function (m) {
      m.addEventListener('play', updatePlayIcon);
      m.addEventListener('pause', updatePlayIcon);
    });
    renderHints();
  }

  function markFocusNoScroll(node) {
    var old = document.querySelectorAll('.focused');
    for (var i = 0; i < old.length; i++) old[i].classList.remove('focused');
    node.classList.add('focused');
  }

  function osdAction(a) {
    if (a === 'back') closePlayer(true);
    else if (a === 'prev') zap(-1);
    else if (a === 'next') zap(1);
    else if (a === 'playpause') togglePlay();
    else if (a === 'list') openDrawer();
    else if (a === 'menu') playerMenu();
    else if (a === 'fullscreen') toggleFullscreen();
    else if (a === 'dub') toggleDub();
    else if (a === 'pip') enterPip();
    if (PHONE) haptic('tick');
    showOsd();
  }

  function toggleFullscreen() {
    var d = document, de = d.documentElement;
    if (!d.fullscreenElement && !d.webkitFullscreenElement) {
      if (de.requestFullscreen) { var p = de.requestFullscreen(); if (p && p.catch) p.catch(function () {}); }
      else if (de.webkitRequestFullscreen) de.webkitRequestFullscreen();
    } else if (d.exitFullscreen) d.exitFullscreen();
    else if (d.webkitExitFullscreen) d.webkitExitFullscreen();
  }

  var COLOR_KEYS = { 403: 'keyRed', 404: 'keyGreen', 405: 'keyYellow', 406: 'keyBlue' };

  function onKeyUp(e) {
    var code = e.keyCode || e.which;
    if (code !== 13) return;
    // Long-press OK needs key-up events; until one is seen, OK acts on key-down (some remotes send no key-up).
    if (!state.okUpSeen) { state.okUpSeen = true; return; }
    if (!state.okDown) return;
    state.okDown = false;
    if (state.okTimer) { clearTimeout(state.okTimer); state.okTimer = null; }
    if (state.okLong) { state.okLong = false; return; }
    shortOk();
  }

  function onKeyDown(e) {
    var code = e.keyCode || e.which;
    if (state.saverOn) { resetSaver(); if (e.preventDefault) e.preventDefault(); return; }
    resetSaver();

    // text fields keep their keys (search box, API keys)
    var t = e.target;
    if (typing(t)) {
      if (state.overlay === 'settings') { settingsKey(code, e); return; }
      if (t === el.searchInput) {
        if (code === 40 || code === 13) { e.preventDefault(); t.blur(); focusGrid(0); }
        else if (isBack(code) && code !== 8) { e.preventDefault(); t.blur(); focusGrid(state.gridIdx); }
        else if (code === 8 && !t.value) { e.preventDefault(); t.blur(); focusGrid(state.gridIdx); }
        return;
      }
      return;
    }

    // OK / Enter: short press on release, long press = options
    if (code === 13) {
      if (e.preventDefault) e.preventDefault();
      if (!state.okUpSeen) { shortOk(); return; }
      if (state.okDown) return; // key repeat while held
      state.okDown = true;
      state.okLong = false;
      state.okTimer = setTimeout(function () {
        state.okTimer = null;
        state.okLong = true;
        longOk();
      }, 650);
      return;
    }

    // colour keys: the action chosen in Settings → Remote
    var ck = COLOR_KEYS[code];
    if (ck) {
      if (e.preventDefault) e.preventDefault();
      if (!S.seenColors) { S.seenColors = true; saveSettings(); renderHints(); }
      if (!state.overlay || state.overlay === 'player') runAction(S[ck]);
      else if (state.overlay === 'menu' || state.overlay === 'drawer') { (state.overlay === 'menu' ? closeMenu : closeDrawer)(); runAction(S[ck]); }
      return;
    }

    // digits
    var dgt = code >= 48 && code <= 57 ? code - 48 : code >= 96 && code <= 105 ? code - 96 : -1;
    if (dgt !== -1 && (!state.overlay || state.overlay === 'player' || state.pinInput)) { digit(dgt); return; }

    // media / channel keys
    if (code === 427 || code === 33) { if (e.preventDefault) e.preventDefault(); zapOrPage(1); return; }
    if (code === 428 || code === 34) { if (e.preventDefault) e.preventDefault(); zapOrPage(-1); return; }
    if (code === 415 || code === 19 || code === 179 || (code === 32 && state.overlay === 'player')) {
      if (e.preventDefault) e.preventDefault(); togglePlay(); return;
    }
    if (code === 457) { runAction('info'); return; } // Info key
    if (code === 10135 || code === 18) { runAction('menu'); return; } // Samsung Tools

    // keyboard shortcuts (computers); the Android TV ☰ / settings key arrives as S
    if (!state.overlay || state.overlay === 'player') {
      if (code === 70) { toggleFullscreen(); return; }       // F
      if (code === 68) { toggleDub(); return; }              // D
      if (code === 83) { openSettings(); return; }           // S
      if (code === 79 || code === 93) { runAction('menu'); return; } // O / context-menu key
      if (code === 191 && !state.overlay) { if (e.preventDefault) e.preventDefault(); focusSearch(); return; } // '/'
    }

    if ([37, 38, 39, 40].indexOf(code) !== -1 && e.preventDefault) e.preventDefault();
    if (isBack(code) && e.preventDefault) e.preventDefault();

    switch (state.overlay) {
      case 'settings': settingsKey(code, e); return;
      case 'dialog': dialogKey(code); return;
      case 'menu': menuKey(code); return;
      case 'drawer': drawerKey(code); return;
      case 'player': playerKey(code); return;
      default: guideKey(code);
    }
  }

  function zapOrPage(dir) {
    if (state.overlay === 'player') { zap(dir); return; }
    if (!state.overlay && state.zone === 'grid') focusGrid(state.gridIdx + (dir > 0 ? -1 : 1) * state.cols * 3);
  }

  function shortOk() {
    switch (state.overlay) {
      case 'settings': settingsKey(13, { preventDefault: function () {} }); return;
      case 'dialog': pressDialog(state.dialogIdx); return;
      case 'menu': runMenuItem(state.menuIdx); return;
      case 'drawer': var i = state.drawerIdx; closeDrawer(true); playIndex(i); return;
      case 'player':
        if (state.zone === 'perror') { errorButtons()[state.perrIdx || 0].click(); return; }
        if (el.osd.classList.contains('off')) showOsd(); else playerMenu();
        return;
    }
    if (state.zone === 'grid') { if (Dub) Dub.unlockAudio(); playIndex(state.gridIdx); }
    else if (state.zone === 'rail') { selectCategory(state.cats[state.railIdx].id); focusGrid(0); }
    else if (state.zone === 'top') {
      var it = topItems()[state.topIdx];
      if (it === el.topSearch) el.searchInput.focus();
      else if (it) it.click();
    }
  }

  function longOk() {
    if (state.overlay === 'player') { runAction(S.longOk === 'menu' ? 'menu' : S.longOk); return; }
    if (state.overlay) { shortOk(); return; }
    if (state.zone === 'grid') runAction(S.longOk);
    else shortOk();
  }

  function guideKey(code) {
    if (isBack(code)) { guideBack(); return; }
    var cols = state.cols;
    if (state.zone === 'grid') {
      var i = state.gridIdx, n = state.list.length;
      if (code === 37) { if (n && i % cols < cols - 1 && i < n - 1) focusGrid(i + 1); }
      else if (code === 39) { if (n && i % cols > 0) focusGrid(i - 1); else focusRail(state.railIdx); }
      else if (code === 38) { if (i - cols >= 0) focusGrid(i - cols); else focusTop(0); }
      else if (code === 40) { if (i + cols < n) focusGrid(i + cols); else if (Math.floor(i / cols) < Math.floor((n - 1) / cols)) focusGrid(n - 1); }
    } else if (state.zone === 'rail') {
      if (code === 38) { if (state.railIdx > 0) focusRail(state.railIdx - 1); else focusTop(0); }
      else if (code === 40) focusRail(state.railIdx + 1);
      else if (code === 37) {
        if (state.cats[state.railIdx].id !== state.cat) selectCategory(state.cats[state.railIdx].id);
        focusGrid(state.cat === state.cats[state.railIdx].id ? state.gridIdx : 0);
      }
    } else if (state.zone === 'top') {
      var items = topItems();
      if (code === 37) focusTop(state.topIdx + 1);
      else if (code === 39) focusTop(state.topIdx - 1);
      else if (code === 40) focusGrid(state.gridIdx);
    }
  }

  function guideBack() {
    if (PHONE && state.tab === 'explore' && !state.query && state.cat !== 'ALL') { selectCategory('ALL'); focusGrid(0); return; }
    if (PHONE && state.tab !== 'home' && !state.query) { setTab('home'); return; }
    if (PHONE && state.tab === 'home' && el.home.scrollTop > 200) { try { el.home.scrollTo({ top: 0, behavior: 'smooth' }); } catch (e) { el.home.scrollTop = 0; } return; }
    if (state.query) { state.query = ''; el.searchInput.value = ''; filterList(); focusGrid(0); toast('החיפוש נוקה'); return; }
    if (state.zone !== 'grid') { focusGrid(state.gridIdx); return; }
    if (state.cat !== 'ALL') { selectCategory('ALL'); focusGrid(0); return; }
    var now = Date.now();
    if (now - (state.lastBack || 0) < 2500) exitApp();
    else { state.lastBack = now; toast(PHONE ? 'חזרה נוספת ליציאה' : 'לחצו שוב BACK ליציאה'); }
  }

  function playerKey(code) {
    showOsd();
    if (state.zone === 'perror') {
      var b = errorButtons();
      if (code === 37) state.perrIdx = clamp((state.perrIdx || 0) + 1, 0, b.length - 1);
      else if (code === 39) state.perrIdx = clamp((state.perrIdx || 0) - 1, 0, b.length - 1);
      else if (code === 38 || code === 40) { zap(code === 40 ? 1 : -1); return; }
      else if (isBack(code)) { closePlayer(false); return; }
      markFocus();
      return;
    }
    if (isBack(code)) { closePlayer(true); return; }
    if (code === 38) zap(S.upNext ? 1 : -1);
    else if (code === 40) zap(S.upNext ? -1 : 1);
    else if (code === 37) openDrawer();
    else if (code === 39) playerMenu();
  }

  function drawerKey(code) {
    var n = state.list.length;
    if (code === 38) { state.drawerIdx = clamp(state.drawerIdx - 1, 0, n - 1); markFocus(); }
    else if (code === 40) { state.drawerIdx = clamp(state.drawerIdx + 1, 0, n - 1); markFocus(); }
    else if (code === 39 || isBack(code)) closeDrawer();
  }

  function menuKey(code) {
    var n = state.menu ? state.menu.length : 0;
    if (code === 38) { state.menuIdx = clamp(state.menuIdx - 1, 0, n - 1); markFocus(); }
    else if (code === 40) { state.menuIdx = clamp(state.menuIdx + 1, 0, n - 1); markFocus(); }
    else if (code === 37 || code === 39) { var it = state.menu[state.menuIdx]; if (it && it.stay) runMenuItem(state.menuIdx, code === 39 ? -1 : 1); }
    else if (isBack(code)) closeMenu();
  }

  function dialogKey(code) {
    var n = dialogButtons().length;
    if (code === 37 || code === 40) state.dialogIdx = clamp(state.dialogIdx + 1, 0, n - 1);
    else if (code === 39 || code === 38) state.dialogIdx = clamp(state.dialogIdx - 1, 0, n - 1);
    else if (isBack(code)) { closeDialog(); return; }
    markFocus();
  }
})();
