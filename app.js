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
  var PLATFORM_NAMES = { webos: 'LG webOS', tizen: 'Samsung Tizen', android: IS_TV ? 'Android TV' : 'Android',
    windows: 'Windows', desktop: /Mac/.test(UA) ? 'macOS' : 'Linux', web: 'דפדפן (אתר)', local: 'דפדפן (מקומי)' };

  // ================================================================== settings
  var DEFAULTS = {
    theme: 'oled', uiScale: 1, cols: 0, hero: true, numbers: true, hints: true, perf: 'auto',
    startCat: 'ALL', autoplay: false, hideAppOnly: false, hiddenCats: [], hidden: [], showSource: true,
    pic: 'off', aspect: 'fit', sharpen: false, quality: 'auto', latency: 'stable', osdSec: 5, osdClock: true,
    retry: true, zap: 'category', upNext: false,
    remote: 'auto', keyRed: 'menu', keyGreen: 'aspect', keyYellow: 'fav', keyBlue: 'dub', longOk: 'menu', numWait: 1.2,
    saver: 2, kids: false, kidsPin: '', autoUpdate: true, liveUpdates: true,
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
    lowEnd: false, cols: 5, update: null, liveReady: null
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
  function streamUrl(ch) { return (IS_WEB && ch.webUrl) || ch.url; }
  function appOnly(ch) { return IS_WEB && ch && !isApp(ch) && (ch.webOk === false || /^http:/i.test(streamUrl(ch))); }
  function isKidsCh(ch) { return /ילדים/.test(ch.category) || ch.id === 'radios_kids'; }
  function isFav(ch) { return ch && state.favorites.indexOf(ch.id) !== -1; }
  function foreign(ch) { return !!(Dub && ch && Dub.isForeign(ch)); }

  function monogramText(ch) {
    var latin = (ch.name.match(/[A-Za-z0-9][A-Za-z0-9!+&'.:-]*(?:\s+[A-Za-z0-9!+&'.:-]+)*/g) || [])
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
    var i = 0;
    function showMono() { img.classList.add('hidden'); mono.classList.remove('hidden'); }
    function tryNext() {
      if (i >= urls.length) { img.removeAttribute('src'); showMono(); return; }
      img.src = urls[i++];
    }
    img.onerror = tryNext;
    img.onload = function () { img.classList.remove('hidden'); mono.classList.add('hidden'); };
    img.referrerPolicy = 'no-referrer';
    img.classList.remove('hidden');
    mono.classList.add('hidden');
    tryNext();
  }

  var CAT_SHORT = { 'ילדים ונוער (בעברית)': 'ילדים בעברית', 'מנויים ואפליקציות (TV Apps)': 'מנויים ואפליקציות' };
  function splitCat(name) {
    if (CAT_SHORT[name]) return CAT_SHORT[name];
    var m = name.match(/^(.*?)\s*\(([^)]*[A-Za-z][^)]*)\)\s*$/);
    var main = m ? m[1] : name;
    return main.replace(/\s*-\s*שידור חי\s*$/, '').replace(/\s*-\s*$/, '');
  }
  var CAT_ICONS = [[/אפליקציות|מנויים/, '📲'], [/רדיו/, '📻'], [/ישראל/, '📡'], [/ילדים/, '🧸'], [/סרטים|סדרות/, '🎬'],
    [/ספורט/, '⚽'], [/מוזיקה/, '🎵'], [/לייף|בידור/, '🏖️'], [/דוקו|טבע/, '🌍'], [/חדשות|News/i, '📰'],
    [/מסורת|יהדות/, '🕍'], [/דת|אמונה/, '🙏'], [/בינלאומי|World/i, '🌐']];
  function catIcon(name) { for (var i = 0; i < CAT_ICONS.length; i++) if (CAT_ICONS[i][0].test(name)) return CAT_ICONS[i][1]; return '📺'; }
  var CAT_ORDER = ['ערוצי ישראל - שידור חי', 'ילדים ונוער (בעברית)', 'רדיו ישראלי (Israeli Radio)', 'מנויים ואפליקציות (TV Apps)',
    'חדשות מהעולם (News)', 'סרטים וסדרות (Movies & Series)', 'ספורט (Sports)', 'ילדים מהעולם (Kids World)', 'מוזיקה (Music TV)',
    'טבע ודוקו (Documentary & Nature)', 'לייף סטייל ובידור', 'מסורת וקהילה', 'ערוצים בינלאומיים (World)'];

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
    var root = $('root');
    root.innerHTML = window.ITV_UI || '';
    cacheEls();
    detectPerformance();
    applyAll();
    loadChannels();
    setupDub();
    setupInput();
    setupNativeBridge();
    setupClock();
    readDeviceInfo();
    resetSaver();
    window.addEventListener('resize', function () { applyScale(); renderGridFresh(); });

    var startCat = S.startCat === 'LAST' ? S.lastCat : S.startCat;
    if (S.kids) startCat = 'ALL';
    selectCategory(state.cats.some(function (c) { return c.id === startCat; }) ? startCat : 'ALL', true);
    focusGrid(0);
    if (S.autoplay && S.lastId) {
      var idx = indexInList(S.lastId);
      if (idx !== -1) { focusGrid(idx); playIndex(idx); }
    }

    // the build started fine: a live update that got this far is good
    try {
      localStorage.removeItem('itv_pending');
      if (LIVE && LIVE.code <= BUILD.code) localStorage.removeItem('itv_live');
    } catch (e) {}
    if (S.autoUpdate) setTimeout(function () { checkUpdates(false); }, 4000);
    window.__itv = { state: state, S: S, playIndex: playIndex, openSettings: openSettings, checkUpdates: checkUpdates };
  }

  function cacheEls() {
    ['app', 'search-input', 'top-search', 'btn-update', 'btn-getapp', 'btn-kids-exit', 'btn-settings', 'clock', 'count-badge',
      'rail-list', 'hero', 'hero-logo', 'hero-mono', 'hero-num', 'hero-cat', 'hero-q', 'hero-lang', 'hero-fav', 'hero-title',
      'hero-desc', 'hero-src', 'grid-title', 'grid-count', 'grid', 'hints', 'numhud', 'numhud-digits', 'numhud-name',
      'radio-audio', 'player', 'video', 'radio-view', 'radio-logo', 'radio-mono', 'radio-name', 'spinner', 'spinner-text',
      'perror', 'perror-title', 'perror-desc', 'perror-retry', 'perror-next', 'perror-back', 'osd', 'osd-logo', 'osd-mono',
      'osd-num', 'osd-name', 'osd-cat', 'osd-quality', 'osd-pic', 'osd-clock', 'osd-dub', 'osd-dub-text', 'osd-buttons',
      'osd-hints', 'ob-play', 'ob-mute', 'drawer', 'drawer-list', 'drawer-cat', 'menu', 'menu-logo', 'menu-mono', 'menu-title',
      'menu-sub', 'menu-list', 'settings', 'settings-nav', 'settings-h', 'settings-p', 'settings-rows', 'dialog', 'dialog-box',
      'dialog-ico', 'dialog-title', 'dialog-body', 'dialog-actions', 'toast', 'saver', 'saver-in', 'saver-clock'
    ].forEach(function (id) {
      el[id.replace(/-([a-z])/g, function (m, c) { return c.toUpperCase(); })] = $(id);
    });
  }

  // ================================================================== look & performance
  function detectPerformance() {
    var chrome = /Chrome\/(\d+)/.exec(UA);
    var cv = chrome ? parseInt(chrome[1], 10) : 99;
    var mem = navigator.deviceMemory || 0;
    var cores = navigator.hardwareConcurrency || 0;
    state.lowEnd = cv < 70 || (mem && mem <= 2) || (cores && cores <= 2) ||
      (PLATFORM === 'webos' && cv < 80) || /SMART-TV.*Tizen [2-4]\./.test(UA);
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
    var base = compact ? clamp(w / 40, 8.5, 11) : w / 120;
    document.documentElement.style.fontSize = (base * (compact ? 1 : S.uiScale)) + 'px';
    toggleClass(document.body, 'compact', compact);
    var cols = S.cols || (compact ? (w < 600 ? 2 : 3) : clamp(Math.round(5 / S.uiScale), 3, 7));
    state.cols = cols;
    document.documentElement.style.setProperty('--card-cols', cols);
  }

  function applySetting(k) {
    var b = document.body;
    if (k === 'theme') {
      b.classList.remove('oled', 'light', 'dark');
      b.classList.add(S.theme);
      var meta = document.querySelector('meta[name="theme-color"]');
      if (meta) meta.setAttribute('content', S.theme === 'light' ? '#e2e8f0' : '#05070d');
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
      card.innerHTML = '<div class="card-num">' + ch.number + '</div><div class="card-flags">' + flags + '</div>' +
        '<div class="card-logo"><img alt="" loading="lazy"><div class="mono hidden"></div></div>' +
        '<div class="card-body"><div class="card-name">' + esc(ch.name) + '</div><div class="card-meta">' + esc(cardMeta(ch)) + '</div></div>';
      setLogo(card.querySelector('img'), card.querySelector('.mono'), ch);
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
    S.lastId = ch.id;
    saveSettings();
    var has = state.cats.some(function (c) { return c.id === 'RECENT'; });
    if (!has) buildCategories();
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
    for (var k = 0; k < cards.length; k++) toggleClass(cards[k], 'is-playing', k === i);
    openPlayer();
    fillOsd(ch);
    showOsd();
    loadStream(ch);
  }

  function openPlayer() {
    if (state.overlay !== 'player') {
      state.stack = [];
      state.overlay = 'player';
      state.zone = 'video';
      el.player.classList.remove('hidden');
      document.body.classList.add('player-open');
      renderOsdHints();
    }
  }

  function closePlayer(keepPlaying) {
    hideError();
    spinner(false);
    closeDrawer(true);
    el.player.classList.add('hidden');
    document.body.classList.remove('player-open');
    state.overlay = null;
    if (!keepPlaying) stopPlayback();
    var idx = state.playing ? indexInList(state.playing.id) : -1;
    focusGrid(idx !== -1 ? idx : state.gridIdx);
    if (state.playing && keepPlaying !== false) toast('השידור ממשיך ברקע · OK על הערוץ לחזרה');
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

  function loadStream(ch) {
    var url = streamUrl(ch);
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

  function startPlay(m, ch) {
    var done = function () { spinner(false); updatePlayIcon(); };
    m.addEventListener('playing', function onp() { m.removeEventListener('playing', onp); done(); });
    var p = m.play();
    if (!p || !p.then) return;
    p.then(done).catch(function () {
      m.muted = true;
      var p2 = m.play();
      if (p2 && p2.then) p2.then(function () { done(); toast('🔇 השידור מושתק – לחצו על 🔊 להפעלת שמע'); updateMuteIcon(); })
        .catch(function (e) { if (state.playing === ch) onFatal(ch, e && e.message); });
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
  function updatePlayIcon() { var m = media(); if (el.obPlay) el.obPlay.textContent = m && m.paused ? '▶' : '⏸'; }
  function toggleMute() {
    var m = media();
    if (!m) return;
    m.muted = !m.muted;
    updateMuteIcon();
    toast(m.muted ? '🔇 מושתק' : '🔊 שמע פועל');
  }
  function updateMuteIcon() { var m = media(); if (el.obMute) el.obMute.textContent = m && m.muted ? '🔇' : '🔊'; }

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
    items.push({ ico: '🔊', label: 'השתקה', stay: true, val: function () { var m = media(); return m && m.muted ? 'מושתק' : 'שמע פועל'; }, act: toggleMute });
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
    el.dialogBox.className = 'dialog-box' + (opts.wide ? ' wide' : '');
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
      }
    });
  }
  function refreshDubUi() { /* the OSD status pill is driven by Dub's onStatus */ }
  function toggleDub() {
    if (!Dub) return;
    Dub.unlockAudio();
    var on = !Dub.settings.enabled;
    if (on && Dub.missingConfig()) { toast('🎙️ ' + Dub.missingConfig()); openSettings('dub'); return; }
    Dub.save({ enabled: on });
    var ch = state.playing;
    if (!on) { Dub.detach(); toast('🎙️ הדיבוב כובה'); return; }
    if (ch && !foreign(ch)) { toast('🎙️ הדיבוב פועל – יופעל אוטומטית בערוצים שאינם בעברית'); return; }
    toast('🎙️ דיבוב לעברית הופעל' + (Dub.settings.sync ? ' (מסונכרן)' : ''));
    if (ch && state.overlay && state.hls) {
      if (Dub.settings.sync) loadStream(ch);
      else Dub.attach({ video: el.video, hls: state.hls, channel: ch, url: streamUrl(ch) });
    }
  }
  function watchDubbed(ch) {
    if (!Dub) return;
    Dub.unlockAudio();
    if (Dub.missingConfig()) { toast('🎙️ ' + Dub.missingConfig()); openSettings('dub'); return; }
    Dub.save({ enabled: true });
    var i = indexInList(ch.id);
    if (i !== -1) playIndex(i);
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
        ? h('▲▼', 'ערוץ') + h('◄', 'רשימת ערוצים') + h('Enter', 'אפשרויות') + h('M', 'השתקה') + h('F', 'מסך מלא') + h('D', 'דיבוב') + h('Esc', 'למדריך')
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
        { type: 'choice', label: 'גודל הממשק', help: 'הגדלה לצפייה מרחוק – משנה את כל הטקסטים והכרטיסים', get: function () { return label(SCALES, S.uiScale); },
          step: function (st) { setSetting('uiScale', cycle(SCALES, S.uiScale, st)); } },
        { type: 'choice', label: 'כרטיסים בשורה', get: function () { return S.cols ? String(S.cols) : 'אוטומטי (' + state.cols + ')'; },
          step: function (st) { setSetting('cols', cycle(COLS, S.cols, st)); } },
        toggle('hero', 'הצגת פרטי הערוץ הנבחר (למעלה)'),
        toggle('numbers', 'מספרי ערוצים על הכרטיסים'),
        toggle('showSource', 'הצגת מקור השידור', 'שורת "מקור" בפרטי הערוץ ובנגן', function () { updateHero(state.list[state.gridIdx]); }),
        toggle('hints', 'שורת עזרה לשלט בתחתית'),
        { type: 'choice', label: 'ביצועים', help: 'מהיר = בלי אנימציות, למכשירים חלשים. ' + (S.perf === 'auto' ? 'זוהה: ' + (state.lowEnd ? 'מכשיר חלש – מצב מהיר' : 'מצב מלא') : ''),
          get: function () { return label(PERF, S.perf); }, step: function (st) { setSetting('perf', cycle(PERF, S.perf, st)); } }
      ] },
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
      { id: 'remote', ico: '🎮', name: 'שלט רחוק', desc: 'זוהה: ' + remoteName(), rows: [
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
        dubChoice('engine', 'מנוע תרגום', [['gemini', 'Google Gemini (מפתח חינמי)'], ['openai', 'OpenAI (בתשלום)'], ['local', 'מקומי – Whisper במכשיר']]),
        dubText('geminiKey', 'מפתח Gemini API', 'מפתח חינמי: aistudio.google.com/apikey • נשמר רק במכשיר', 'AIza...'),
        dubText('openaiKey', 'מפתח OpenAI API', 'נשמר רק במכשיר', 'sk-...'),
        dubChoice('tts', 'קול הדיבוב', [['auto', 'אוטומטי'], ['browser', 'קול המכשיר'], ['google', 'Google'], ['openai', 'OpenAI'], ['none', 'כתוביות בלבד']]),
        { type: 'choice', label: 'מהירות דיבור', get: function () { return Number(Dub.settings.rate).toFixed(2); },
          step: function (st) { Dub.save({ rate: clamp(Math.round((Dub.settings.rate + 0.05 * st) * 100) / 100, 0.8, 1.6) }); } },
        { type: 'choice', label: 'עוצמת הסאונד המקורי בזמן דיבוב', get: function () { return Math.round(Dub.settings.duck * 100) + '%'; },
          step: function (st) { Dub.save({ duck: clamp(Math.round((Dub.settings.duck + 0.05 * st) * 100) / 100, 0, 1) }); } },
        dubToggle('sync', 'סנכרון לשפתיים', 'התמונה מושהית כדי שהדיבוב יגיע בזמן'),
        dubChoice('delay', 'השהיית תמונה', [[6, '6 שניות'], [8, '8 שניות'], [10, '10 שניות'], [12, '12 שניות'], [15, '15 שניות'], [20, '20 שניות']]),
        dubToggle('subtitles', 'כתוביות בעברית'),
        dubToggle('showOriginal', 'גם הטקסט המקורי'),
        action('🔊 בדיקת קול', function () { Dub.unlockAudio(); Dub.testVoice(); })
      ] : [info('דיבוב', 'לא זמין במכשיר הזה')] },
      { id: 'kids', ico: '🧸', name: 'מצב ילדים', desc: 'רק ערוצי ילדים, ויציאה עם קוד', rows: [
        { type: 'toggle', label: 'מצב ילדים', help: 'מציג רק ערוצי ילדים (בעברית ומהעולם) ותחנת רדיו לילדים', on: function () { return S.kids; },
          act: function () { if (S.kids) askPin(function () { setSetting('kids', false); renderSettings(); }); else { setSetting('kids', true); toast('🧸 מצב ילדים פועל'); } } },
        { type: 'action', label: S.kidsPin ? 'שינוי קוד יציאה (' + S.kidsPin.replace(/./g, '•') + ')' : 'הגדרת קוד יציאה (4 ספרות)', help: 'בלי קוד – כל אחד יכול לצאת ממצב ילדים',
          act: function () { askPin(function () { newPin(); }); } }
      ] },
      { id: 'saver', ico: '🌙', name: 'שומר מסך', desc: 'הגנה על מסכי OLED כשאין צפייה', rows: [
        choice('saver', 'הפעלה אחרי', SAVERS, 'רק במדריך או כשהשידור מושהה – לא בזמן צפייה')
      ] },
      { id: 'updates', ico: '⬆️', name: 'עדכונים', desc: 'עדכונים מגיעים ישירות מ-GitHub', rows: [
        info('גרסה מותקנת', BUILD.version + (BUILD.date ? ' (' + BUILD.date + ')' : '')),
        info('גרסה פעילה', function () { return LIVE ? LIVE.version + ' – עודכנה אונליין' : BUILD.version; }),
        info('מצב', function () { return updateStatusText(); }),
        action('בדיקת עדכונים עכשיו', function () { checkUpdates(true); }),
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
        info('ערוצים', function () { return (window.CHANNELS_DATA || []).length + ' (מתוכם ' + state.all.length + ' מוצגים)'; }),
        action('📋 מאיפה מגיע כל ערוץ', function () { openUrl(REPO + '/blob/master/SOURCES.md'); }),
        action('🌐 הפרויקט ב-GitHub', function () { openUrl(REPO); }),
        action('📲 האפליקציה לשאר המכשירים', function () { showGetApp(); }),
        action('איפוס כל ההגדרות', function () { confirmBox('לאפס את כל ההגדרות? (מועדפים נשמרים)', function () { try { localStorage.removeItem('itv_settings'); } catch (e) {} location.reload(); }); }, '', true)
      ] }
    ];
    return secs;
  }

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
  }
  function closeSettings() {
    blurInputs();
    el.settings.classList.add('hidden');
    state.overlay = state.stack.pop() || null;
    if (state.overlay === 'player') { renderOsdHints(); showOsd(); }
    renderHints();
    markFocus();
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
      if (r.type === 'toggle' || r.type === 'action') { r.act(); if (state.overlay === 'settings') renderSettings(); }
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
  function nativeKey() {
    if (PLATFORM === 'android') return 'android';
    if (PLATFORM === 'windows') return 'windows';
    if (PLATFORM === 'webos') return 'webos';
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
      if (data.code > activeCode && S.liveUpdates && (data.shell || 0) <= (BUILD.shell || 0)) {
        u.liveDownloading = true;
        var q = '?v=' + data.code;
        prefetch(['style.css', 'ui.js', 'channels.js', 'dub.js', 'app.js'].map(function (f) { return REMOTE + f + q; }), function (ok) {
          u.liveDownloading = false;
          if (ok) {
            state.liveReady = { code: data.code, version: data.version, shell: data.shell || 0 };
            try { localStorage.setItem('itv_live', JSON.stringify(state.liveReady)); localStorage.removeItem('itv_bad'); } catch (e) {}
            showUpdateButton();
            toast('⬆️ עדכון ' + data.version + ' מוכן – יופעל בפתיחה הבאה (או בהגדרות ← עדכונים)');
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
  function refreshSettingsIfOpen() { if (state.overlay === 'settings') renderSettings(); }
  function showUpdateButton() { el.btnUpdate.classList.remove('hidden'); }
  function offerReload(data) {
    openDialog({ ico: '⬆️', title: 'גרסה ' + data.version + ' זמינה', html: notesHtml(data),
      buttons: [{ label: 'רענון עכשיו', primary: true, act: function () { location.reload(true); } }, { label: 'אחר כך' }] });
  }
  function notesHtml(data) {
    var notes = data.notes || [];
    return notes.length ? '<ul>' + notes.map(function (t) { return '<li>' + esc(t) + '</li>'; }).join('') + '</ul>' : '';
  }

  function showUpdateDialog() {
    var u = state.update;
    if (!u || !u.data) { checkUpdates(true); return; }
    if (IS_SITE) { offerReload(u.data); return; }
    var buttons = [], html = notesHtml(u.data);
    if (state.liveReady) {
      html = '<p>עדכון ' + esc(state.liveReady.version) + ' (מסך, קוד וערוצים) הורד ומוכן.</p>' + html;
      buttons.push({ label: '⟳ הפעלה מחדש עם העדכון', primary: true, act: function () { try { sessionStorage.removeItem('itv_local'); } catch (e) {} location.reload(); } });
    }
    if (u.native) {
      html += '<p>גרסה ' + esc(u.native.version) + ' של האפליקציה עצמה זמינה' + (u.native.note ? ' – ' + esc(u.native.note) : '') + '.</p>';
      if (PLATFORM === 'android' && window.AndroidApp && window.AndroidApp.installUpdate) {
        buttons.push({ label: '⬇️ הורדה והתקנה', primary: !buttons.length, act: function () { installNative(u.native); } });
      } else if (PLATFORM === 'windows') {
        buttons.push({ label: '⬇️ הורדה והתקנה', primary: !buttons.length, act: function () { installNative(u.native); } });
      } else if (PLATFORM === 'webos') {
        html += '<p>בטלוויזיית LG: התקנה מחדש של קובץ ה-IPK (Developer Mode / Homebrew Channel). המסך, הקוד והערוצים מתעדכנים גם בלי זה.</p>';
        buttons.push({ label: '🌐 דף ההורדה', act: function () { openUrl(RELEASES); } });
      } else {
        buttons.push({ label: '🌐 דף ההורדה', primary: !buttons.length, act: function () { openUrl(u.native.url || RELEASES); } });
      }
    }
    if (!buttons.length) { toast('✅ הגרסה עדכנית'); return; }
    buttons.push({ label: 'אחר כך' });
    openDialog({ ico: '⬆️', title: 'עדכון זמין', html: html, buttons: buttons });
  }

  function installNative(n) {
    openDialog({ ico: '⬇️', title: 'מוריד את גרסה ' + n.version, html: '<div class="progress"><i id="upd-bar"></i></div><p id="upd-text">מתחיל...</p>',
      buttons: [{ label: 'הסתרה' }] });
    window.__itvUpdate = function (st, v) {
      var bar = $('upd-bar'), txt = $('upd-text');
      if (st === 'progress') { if (bar) bar.style.width = v + '%'; if (txt) txt.textContent = v + '%'; }
      else if (st === 'installing') { if (txt) txt.textContent = 'פותח את ההתקנה – אשרו במסך שייפתח'; if (bar) bar.style.width = '100%'; }
      else if (st === 'error') { if (txt) txt.textContent = 'שגיאה: ' + v; toast('⚠️ ההורדה נכשלה'); }
    };
    if (PLATFORM === 'android') window.AndroidApp.installUpdate(n.url);
    else if (PLATFORM === 'windows') window.chrome.webview.postMessage(JSON.stringify({ update: n.url }));
  }

  // ================================================================== native bridges
  function setupNativeBridge() {
    window.__itvKey = function (code) {
      onKeyDown({ keyCode: code, which: code, target: document.body, preventDefault: function () {}, stopPropagation: function () {} });
      if (code === 13) onKeyUp({ keyCode: 13, which: 13, target: document.body, preventDefault: function () {} });
    };
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
      playIndex(i);
    });
    el.grid.addEventListener('contextmenu', function (e) {
      var c = e.target.closest('.card');
      if (!c) return;
      e.preventDefault();
      var i = parseInt(c.getAttribute('data-i'), 10);
      focusGrid(i);
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

    el.osdButtons.addEventListener('click', function (e) {
      var b = e.target.closest('.obtn');
      if (!b) return;
      e.stopPropagation();
      osdAction(b.getAttribute('data-act'));
    });
    el.player.addEventListener('click', function (e) {
      if (e.target.closest('.osd-buttons') || e.target.closest('.drawer') || e.target.closest('.perror-box')) return;
      if (el.osd.classList.contains('off')) showOsd(); else el.osd.classList.add('off');
    });
    el.player.addEventListener('dblclick', function (e) { if (!e.target.closest('.osd-buttons')) toggleFullscreen(); });
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
      if (r.type === 'choice') r.step(1);
      else if (r.type === 'toggle' || r.type === 'action') r.act();
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
      m.addEventListener('volumechange', updateMuteIcon);
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
    else if (a === 'mute') toggleMute();
    else if (a === 'list') openDrawer();
    else if (a === 'menu') playerMenu();
    else if (a === 'fullscreen') toggleFullscreen();
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
      if (code === 77) { toggleMute(); return; }             // M
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
    if (state.query) { state.query = ''; el.searchInput.value = ''; filterList(); focusGrid(0); toast('החיפוש נוקה'); return; }
    if (state.zone !== 'grid') { focusGrid(state.gridIdx); return; }
    if (state.cat !== 'ALL') { selectCategory('ALL'); focusGrid(0); return; }
    var now = Date.now();
    if (now - (state.lastBack || 0) < 2500) exitApp();
    else { state.lastBack = now; toast('לחצו שוב BACK ליציאה'); }
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
