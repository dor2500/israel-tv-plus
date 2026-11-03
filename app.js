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

  // channels.js exposes ITV_CHANNELS; keep the app's canonical alias available to all screens.
  window.CHANNELS_DATA = window.CHANNELS_DATA || window.ITV_CHANNELS || window.CHANNELS || [];
  window.ITV_CHANNELS = window.CHANNELS_DATA;

  var REMOTE = 'https://dor2500.github.io/israel-tv-plus/';
  var RELEASES = 'https://github.com/dor2500/israel-tv-plus/releases/latest';
  var REPO = 'https://github.com/dor2500/israel-tv-plus';
  // installers are only ever taken from this project's own GitHub releases (version.json and the API reply are remote data)
  function trustedReleaseUrl(u) { return typeof u === 'string' && u.indexOf(REPO + '/releases/download/') === 0; }
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
  var IS_TV = PLATFORM === 'webos' || PLATFORM === 'tizen' ||
    /Web0S|webOS|Tizen|SMART-TV|SmartTV|AndroidTV|GoogleTV|AppleTV|HbbTV|NetCast|VIDAA|AFT[BMT]|POV_TV|BRAVIA|MiTV|MiBOX/i.test(UA) ||
    (PLATFORM === 'android' && window.AndroidApp && window.AndroidApp.isTv && window.AndroidApp.isTv()) ||
    (new URLSearchParams(location.search).get('tv') === '1');
  var TOUCH = !IS_TV && ('ontouchstart' in window) && window.matchMedia && matchMedia('(pointer: coarse)').matches;
  var PHONE = false;            // phone layout (Nuvio home + navigation bar), set by applyScale()
  var I = window.ITV_ICON || function () { return ''; };
  var NATIVE = window.AndroidApp || null;
  function native(fn) { try { return NATIVE && NATIVE[fn] ? NATIVE[fn].apply(NATIVE, [].slice.call(arguments, 1)) : undefined; } catch (e) { return undefined; } }
  var PLATFORM_NAMES = { webos: 'LG webOS', tizen: 'Samsung Tizen', android: IS_TV ? 'Android TV' : 'Android',
    windows: 'Windows', desktop: /Mac/.test(UA) ? 'macOS' : 'Linux', web: 'דפדפן (אתר)', local: 'דפדפן (מקומי)' };

  // ================================================================== settings
  // A design replaces the whole interface: its look and colours (body.design-*, style.css) and the size of the home
  // screen's cards. pitch = card + gap, row = one shelf, in rem; vert = the tabs stand in a column.
  var DESIGNS = {
    classic: { name: 'קלאסי', help: 'המראה הרגיל: תצוגה מקדימה על כל המסך, סרגל זכוכית ומדפי ערוצים.', pitch: 19, row: 17.25 },
    cinema: { name: 'קולנוע', help: 'שחור וזהב: תמונה מלאה, פרטי הערוץ במרכז ושורת פוסטרים אחת בתחתית.', pitch: 12.5, row: 21,
      pal: ['#e8b44a', '#14100a', '#a87a1c', '#ffd98a', '#33270f', '#050403', '#100d08', '#191409'] },
    rail: { name: 'לוח שידורים', help: 'סרגל צד קבוע, חלון תצוגה מקדימה ומדפים צפופים שמראים הרבה ערוצים.', pitch: 14.5, row: 14, vert: true,
      pal: ['#2dd4bf', '#04211d', '#0e9f8e', '#7df3e3', '#0f3a38', '#081a24', '#0d2431', '#12303f'] },
    metro: { name: 'מטרו', help: 'משבצות צבעוניות וחדות, כותרות גדולות ובלי שקיפויות.', pitch: 18.5, row: 15,
      pal: ['#ff5a1f', '#ffffff', '#d9480f', '#ffffff', '#4a1c08', '#111214', '#1b1c20', '#25272c'] },
    paper: { name: 'מגזין', help: 'מסך בהיר כמו עיתון: נייר, דיו שחור, קווים דקים וכותרות גדולות.', pitch: 17.5, row: 16.5, light: true,
      pal: ['#b3261e', '#ffffff', '#7a1410', '#1a1a1a', '#efe6d4', '#f6f0e3', '#ece4d2', '#fffdf7'] }
  };
  var DEFAULTS = {
    theme: 'oled', accent: 'auto', design: 'classic', uiScale: 1, cols: 0, hero: true, numbers: true, hints: true, perf: 'auto',
    startCat: 'ALL', autoplay: false, hideAppOnly: false, hiddenCats: [], hidden: [], showSource: true,
    pic: 'off', aspect: 'fit', upscaleMode: 'off', sharpen: false, quality: 'max', latency: 'low', osdSec: 5, osdClock: true,
    retry: true, zap: 'category', upNext: true, upNextV2: true,
    remote: 'auto', keyRed: 'menu', keyGreen: 'aspect', keyYellow: 'fav', keyBlue: 'dub', longOk: 'menu', numWait: 1.2,
    saver: 2, kids: false, kidsPin: '', autoUpdate: true, liveUpdates: true, avatar: '1f60e', profileOnStart: true,
    lastId: '', lastCat: 'ALL', seenColors: false
  };
  var S = loadSettings();

  function loadSettings() {
    var s = {};
    try { s = JSON.parse(localStorage.getItem('itv_settings') || '{}') || {}; } catch (e) { s = {}; }
    var out = {};
    Object.keys(DEFAULTS).forEach(function (k) { out[k] = s[k] !== undefined ? s[k] : DEFAULTS[k]; });
    if (!s.upNextV2) { out.upNext = true; out.upNextV2 = true; }
    if (s.upscaleMode === undefined) {
      if (s.quality === 'upscale4k') out.upscaleMode = 'ai_4k';
      else if (s.sharpen) out.upscaleMode = 'crystal_hd';
    }
    // the "interface style" of 4.8.50 only had a real counterpart for cinema
    if (s.design === undefined && s.tvStyle === 'cinema') out.design = 'cinema';
    if (!DESIGNS[out.design]) out.design = 'classic';
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
  function isHevcSupported() {
    try {
      if (window.MediaSource && typeof MediaSource.isTypeSupported === 'function') {
        return MediaSource.isTypeSupported('video/mp4; codecs="hev1.1.2.L123.90"') ||
               MediaSource.isTypeSupported('video/mp4; codecs="hev1.1.6.L93.B0"') ||
               MediaSource.isTypeSupported('video/mp4; codecs="hvc1.1.6.L93.B0"');
      }
    } catch (e) {}
    return false;
  }
  function normalizeStreamUrl(u) {
    var s = String(u || '').trim();
    if (!s) return '';
    if (location.protocol === 'https:') {
      s = s.replace(/^http:\/\/(?:s2\.ottrast\.com|212\.80\.215\.72)(?::\d+)?\//i, 'https://s2.ottrast.com/');
      s = s.replace(/^http:\/\/stream\.mcquack\.net\//i, 'https://stream.mcquack.net/');
      s = s.replace(/^http:\/\/glzwizzlv\.bynetcdn\.com\//i, 'https://glzwizzlv.bynetcdn.com/');
    }
    return s;
  }
  function candidateUrlsOf(ch) {
    if (!ch) return [];
    var raw = [];
    if (IS_WEB && ch.webUrl) raw.push(ch.webUrl);
    if (ch.url) raw.push(ch.url);
    if (ch.backups && ch.backups.length) raw = raw.concat(ch.backups);
    if (ch.fallbackUrls && ch.fallbackUrls.length) raw = raw.concat(ch.fallbackUrls);
    var seen = {}, httpsList = [], httpList = [];
    for (var i = 0; i < raw.length; i++) {
      var u = normalizeStreamUrl(raw[i]);
      if (!u || seen[u]) continue;
      seen[u] = 1;
      if (/^http:/i.test(u)) httpList.push(u);
      else httpsList.push(u);
    }
    var list = httpsList.concat(httpList);
    if (location.protocol === 'https:') {
      var proxied = [];
      for (var j = 0; j < httpList.length; j++) {
        var hu = httpList[j];
        if (RELAY) proxied.push(RELAY + '/?u=' + encodeURIComponent(hu));
        // corsproxy removed due to 403 blocks
      }
      list = httpsList.concat(proxied).concat(httpList);
    }
    // Prioritize reliable FHD H.264 streams over 4K HEVC unless user explicitly selected 4K upscale mode
    if ((S.quality !== 'upscale4k' || !isHevcSupported()) && /_4k|sport_5_4k|54k/i.test((ch.id || '') + ' ' + (ch.url || '') + ' ' + (ch.name || ''))) {
      var non4k = [], is4k = [];
      for (var k = 0; k < list.length; k++) {
        if (/_4k|54k/i.test(list[k])) is4k.push(list[k]);
        else non4k.push(list[k]);
      }
      return non4k.concat(is4k);
    }
    return list;
  }
  function needsRelay(ch) { return IS_WEB && !ch.webUrl && (ch.webOk === false || /^http:/i.test(normalizeStreamUrl(ch.url))); }
  function streamUrl(ch) {
    var cands = candidateUrlsOf(ch);
    if (cands.length) return cands[0];
    if (IS_WEB && ch.webUrl) return ch.webUrl;
    var u = normalizeStreamUrl(ch && ch.url);
    if (RELAY && needsRelay(ch)) return RELAY + '/?u=' + encodeURIComponent(u);
    return u;
  }
  function appOnly(ch) { return false; }
  function isKidsCh(ch) {
    if (!ch) return false;
    var cat = (ch.category || '').toLowerCase();
    var en = (ch.categoryEn || '').toLowerCase();
    var name = (ch.name || '').toLowerCase();
    var id = (ch.id || '').toLowerCase();
    var full = cat + ' ' + en + ' ' + name + ' ' + id;
    if (/היפ הופ|hiphop|הופעות|אירוויזיון|יהודית|חדשות|ספורט|פוליטיקה|דת |כלכלה|ויוה|viva|טלנובלה|telenovela|אימה|horror|news|sport|finance|דרמה|drama|רכב|מרוצים|motor|cars|racing|אוכל|בישול|cooking|food|טבע|פרא|docu|דוקו|lifestyle|עיצוב|שיפוץ|האח הגדול|big brother|reality|ריאליטי|קומדיה|comedy|קולנוע ישראלי|סרטים|cinema|movies|hbo|hot 3|yes tv|action|אקשן|פוד|foody|סדרות|series|ערוץ 9|ערוץ 11|ערוץ 12|ערוץ 13|ערוץ 14|ערוץ 15|ערוץ 16|כאן 11|קשת 12|רשת 13|עכשיו 14|i24news|כנסת|knesset|103fm|100fm|eco99|גלגלצ|גלצ|קול ברמה|קול חי|מכאן/.test(full)) {
      if (!/ילדים|פמילי|kids|family|מצויר|cartoon|disney|דיסני|ניק|nick|ג'וניור|junior|הופ!|לולי|luli|חינוכית|hinuchit|ילדותי|yalduti|שירי ילדות|baby|duck|lego/.test(full)) {
        return false;
      }
    }
    if (ch.kind === 'radio' || /radio/.test(en) || ch.isRadio) {
      return id === 'radios_disney' || id === 'radios_kids' || id === 'radios_stars' || id === 'kan_kids_radio';
    }
    if (/ילדים/.test(cat) || /^(kids|kids_he|kids_world|cartoons)$/.test(en)) return true;
    if (/ילדים|נוער|פעוטות|קטנטנים|אנימציה|מצוירים|הופ!|בייבי|דיסני|ניקלודיאון|ניק ג'וניור|ג'וניור|קארטון נטוורק|בומרנג|שירי ילדותי|ילדותי|baby shark|ducktv|lego|transformers|cartoon network|boomerang|luli|לולי|זום|zoom|חינוכית|hinuchit|יויו|yoyo|וויז|wiz|באזז|fomo|stars_kids|a_plus_kids/.test(full)) return true;
    return false;
  }
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
    'טיולים והרפתקאות (Travel)', 'מדע ולמידה (Science)', 'לייף סטייל ובידור', 'מסורת וקהילה', 'דת ומסורת', 'דת ואמונה (Religion)',
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
    setupAtv();
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
        if (TVUI && !state.overlay) { atv.dirty = true; atvRestore(); }
      }, 120);
    });

    var startCat = S.startCat === 'LAST' ? S.lastCat : S.startCat;
    if (S.kids) startCat = 'ALL';
    selectCategory(state.cats.some(function (c) { return c.id === startCat; }) ? startCat : 'ALL', true);
    focusGrid(0);
    if (PHONE) { buildHome(); setTab(S.startCat === 'FAVORITES' && state.favorites.length ? 'library' : 'home', true); }
    if (TVUI) {
      atvBuild();
      atvLayout();
    }
    if (S.autoplay && S.lastId) {
      var idx = indexInList(S.lastId);
      if (idx !== -1) { focusGrid(idx); playIndex(idx); }
    }
    if (TVUI && (S.profileOnStart !== false)) {
      setTimeout(function () {
        openTvProfiles();
      }, 60);
    }

    // the build started fine: a live update that got this far is good
    try {
      localStorage.removeItem('itv_pending');
      if (LIVE && LIVE.code <= BUILD.code) localStorage.removeItem('itv_live');
    } catch (e) {}
    // the Android app's splash screen (core-splashscreen) waits for this: the first screen is ready to draw
    setTimeout(function () { native('ready'); }, 30);
    if (S.autoUpdate) setTimeout(function () { checkUpdates(false); }, 4000);
    setTimeout(loadVodData, 800);
    setTimeout(function () { ensureEpgData(); }, 1200);
    if (window.GDrive && window.GDrive.isSignedIn()) setTimeout(function () { triggerCloudSync(false); }, 3000);
    if (PLATFORM === 'webos') {
      // LG: TV apps stay open for days – check again every 6 hours, and apply a downloaded update on returning to the app
      setInterval(function () { if (S.autoUpdate) checkUpdates(false); }, 6 * 3600 * 1000);
      document.addEventListener('visibilitychange', function () { if (!document.hidden) applyLiveIfIdle(); });
      // Home / another app: webOS kills a backgrounded app that still holds the hardware decoder (it then never shows
      // in the recents bar). Release every media pipeline when hidden and bring the channel back when shown again.
      document.addEventListener('visibilitychange', function () {
        // at once, not on a timer: webOS freezes a hidden page's timers, so a delayed release never ran
        if (document.hidden) suspendMedia(); else resumeMedia();
      });
      // appinfo's handlesRelaunch leaves it to the app to come back: without activate() a second launch shows nothing
      document.addEventListener('webOSRelaunch', function () {
        try {
          var sys = window.webOSSystem || window.PalmSystem;
          if (sys && sys.activate) sys.activate();
          var v = document.querySelector('.video-box video');
          if (v && v.paused && state.overlay === 'player') { var p = v.play(); if (p && p.catch) p.catch(function () {}); }
        } catch (e) {}
      });
    }
    if (Dub && Dub.settings.migratedFromLocal) setTimeout(explainTvDub, 1500);
    window.__itv = { state: state, S: S, playIndex: playIndex, openSettings: openSettings, checkUpdates: checkUpdates };
  }

  // ---- LG: release media while the app is in the background
  var bgResume = null;
  function suspendMedia() {
    if (!document.hidden || bgResume) return;
    try {
      if (state.overlay === 'multiview') closeMultiView();
      if (state.playing || state.hls) {
        bgResume = state.overlay === 'player' ? state.playing : null;
        stopPlayback();
      }
      crumb('background');
    } catch (e) {}
  }
  function resumeMedia() {
    var ch = bgResume;
    bgResume = null;
    if (!ch || state.overlay !== 'player' || state.playing) return;
    try {
      var i = state.list.indexOf(ch);
      if (i === -1) for (var k = 0; k < state.list.length; k++) if (state.list[k].id === ch.id) { i = k; break; }
      if (i !== -1) playIndex(i); else closePlayer(false);
    } catch (e) {}
  }

  function cacheEls() {
    ['app', 'search-input', 'top-search', 'btn-update', 'btn-getapp', 'btn-kids-exit', 'btn-settings', 'btn-gdrive', 'clock', 'count-badge',
      'rail-list', 'hero', 'hero-logo', 'hero-mono', 'hero-num', 'hero-cat', 'hero-q', 'hero-lang', 'hero-fav', 'hero-title',
      'hero-desc', 'hero-src', 'grid-title', 'grid-count', 'grid', 'hints', 'numhud', 'numhud-digits', 'numhud-name',
      'radio-audio', 'player', 'video', 'radio-view', 'radio-logo', 'radio-mono', 'radio-name', 'spinner', 'spinner-text',
      'perror', 'perror-title', 'perror-desc', 'perror-retry', 'perror-next', 'perror-back', 'osd', 'osd-logo', 'osd-mono',
      'osd-num', 'osd-name', 'osd-cat', 'osd-quality', 'osd-pic', 'osd-clock', 'osd-dub', 'osd-dub-text', 'osd-buttons',
      'osd-hints', 'ob-play','drawer', 'drawer-list', 'drawer-cat', 'menu', 'menu-logo', 'menu-mono', 'menu-title',
      'menu-sub', 'menu-list', 'settings', 'settings-nav', 'settings-h', 'settings-p', 'settings-rows', 'dialog', 'dialog-box',
      'dialog-ico', 'dialog-title', 'dialog-body', 'dialog-actions', 'toast', 'saver', 'saver-in', 'saver-clock',
      'topbar', 'home', 'pager', 'pager-track', 'pager-dots', 'shelves', 'navbar', 'nb-pill', 'btn-search', 'hero-bg',
      'osd-center', 'ob-pip', 'ob-server', 'guide-body', 'brand-tab', 'btn-avatar', 'avatar-img', 'avatar-dot', 'nb-search', 'btn-multiview', 'btn-cast-top', 'multiview', 'mv-grid', 'mv-close', 'mv-picker', 'mv-picker-title', 'mv-picker-close', 'mv-picker-search', 'mv-picker-cats', 'mv-picker-grid', 'atv-kb', 'atv-colors'
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
    // LG: a hide is not a clean exit – if the system kills the app in the background it must show up as a crash
    if (PLATFORM !== 'webos') document.addEventListener('visibilitychange', function () { mark(document.hidden); });
    else document.addEventListener('visibilitychange', function () { if (!document.hidden) mark(false); });
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
    ['theme', 'design', 'hero', 'numbers', 'hints', 'perf', 'osdClock', 'pic', 'aspect', 'sharpen', 'upscaleMode', 'kids'].forEach(applySetting);
    document.body.classList.add('platform-' + PLATFORM);
    toggleClass(document.body, 'touch', TOUCH);
  }

  function applyScale() {
    var w = window.innerWidth, hgt = window.innerHeight, wasPhone = PHONE;
    var compact = w < 900 || (TOUCH && w < 1100 && hgt > w * 0.9);
    // phones: portrait, or a touch screen whose short side is phone-sized (landscape phones too); never TVs
    PHONE = !IS_TV && (window.ITV_IOS || w < 700 || (TOUCH && (Math.min(w, hgt) < 600 || hgt > w)));
    var base = PHONE ? clamp(Math.min(w, hgt) / 40, 8.5, 11) : compact ? clamp(w / 40, 8.5, 11) : w / 120;
    document.documentElement.style.fontSize = (base * (compact || PHONE ? 1 : S.uiScale)) + 'px';
    toggleClass(document.body, 'compact', compact);
    toggleClass(document.body, 'phone', PHONE);
    var scaled = !compact && !PHONE;
    toggleClass(document.body, 'ui-large', scaled && S.uiScale >= 1.15);
    toggleClass(document.body, 'ui-xl', scaled && S.uiScale === 1.3);
    toggleClass(document.body, 'ui-xxl', scaled && S.uiScale >= 1.5);
    TVUI = !PHONE;
    toggleClass(document.body, 'tvui', TVUI);
    var cols = S.cols || (PHONE ? (w < 600 ? 2 : w < 900 ? 3 : 4) : compact ? (w < 600 ? 2 : 3) : clamp(Math.round(5 / S.uiScale), 3, 7));
    if (PHONE && S.cols) cols = Math.min(S.cols, w < 600 ? 3 : 5);
    state.cols = cols;
    document.documentElement.style.setProperty('--card-cols', cols);
    if (wasPhone !== PHONE) applySetting('design');
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
  // phones keep their own interface whatever design is chosen on a big screen
  function activeDesign() { return (!PHONE && DESIGNS[S.design]) || DESIGNS.classic; }
  function applyColors() {
    var name = accentName(), sc = readSystemColors(), des = activeDesign();
    var light = des.pal ? !!des.light : S.theme === 'light', oled = !des.pal && S.theme === 'oled';
    var p;
    if (des.pal) { p = des.pal; name = 'design'; }
    else if (name === 'system' && sc) {
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
    if (k === 'theme' || k === 'accent' || k === 'design') {
      var des = activeDesign();
      b.classList.remove('oled', 'light', 'dark');
      b.classList.add(des.pal ? (des.light ? 'light' : 'dark') : S.theme);
      Object.keys(DESIGNS).forEach(function (d) { b.classList.remove('design-' + d); });
      b.classList.add('design-' + (!PHONE && DESIGNS[S.design] ? S.design : 'classic'));
      applyColors();
      if (k === 'design' && atv && atv.built) { atvLayout(); atvMark(); }
    } else if (k === 'uiScale' || k === 'cols') { applyScale(); renderGridFresh(); }
    else if (k === 'hero') toggleClass(b, 'no-hero', !S.hero);
    else if (k === 'numbers') toggleClass(b, 'no-numbers', !S.numbers);
    else if (k === 'hints') toggleClass(b, 'no-hints', !S.hints);
    else if (k === 'perf') toggleClass(b, 'anim-off', fastMode());
    else if (k === 'osdClock') toggleClass(b, 'no-osd-clock', !S.osdClock);
    else if (k === 'pic' || k === 'aspect' || k === 'sharpen' || k === 'upscaleMode') applyPicture();
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

  var DESIGN_LIST = Object.keys(DESIGNS).map(function (d) { return [d, DESIGNS[d].name]; });
  var UPSCALE_MODES = [
    ['off', 'כבוי (תמונה מקורית ללא עיבוד)'],
    ['ai_4k', 'השבחת AI 4K Ultra Sharp (חדות דינמית ושיפור רזולוציה)'],
    ['crystal_hd', 'מנוע Crystal Clear HD (חידוד מוגבר לשידורי SD/HD)'],
    ['cinema_hdr', 'קולנוע HDR Dynamic (טווח דינמי רחב ושחורים עמוקים)'],
    ['vivid_oled', 'צבעים חיים OLED Pop (גוונים עשירים ותמונה בוהקת)'],
    ['sports_pro', 'ספורט Pro Ultra (תנועה חלקה ומשטחים חיים)'],
    ['smooth_denoise', 'החלקה והפחתת רעשים (De-Noise עדין לשידורים מגורענים)'],
    ['retro_crt', 'אפקט שידור קלאסי / רטרו (חמימות נוסטלגית)']
  ];
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
    if (S.aspect && S.aspect !== 'fit') v.classList.add('aspect-' + S.aspect);
    if (S.upscaleMode && S.upscaleMode !== 'off') {
      v.classList.add('upscale-' + S.upscaleMode);
    } else if (S.pic && S.pic !== 'off') {
      v.classList.add('pic-' + S.pic);
    }
    if (S.sharpen && (!S.upscaleMode || S.upscaleMode === 'off')) v.classList.add('upscale-crystal_hd');
    if (el.osdPic) {
      var upL = (S.upscaleMode && S.upscaleMode !== 'off') ? label(UPSCALE_MODES, S.upscaleMode) : (S.pic !== 'off' ? label(PICS, S.pic) : '');
      el.osdPic.textContent = (upL ? '✨ ' + upL + ' · ' : '') + label(ASPECTS, S.aspect);
    }
    if (el.osdQuality) {
      if (S.upscaleMode === 'ai_4k') {
        el.osdQuality.textContent = '4K ULTRA HD';
        el.osdQuality.className = 'osd-quality osd-quality-4k';
      } else if (S.upscaleMode === 'crystal_hd') {
        el.osdQuality.textContent = 'CRYSTAL HD';
        el.osdQuality.className = 'osd-quality';
      } else {
        el.osdQuality.className = 'osd-quality';
      }
    }
  }

  /** The series catalog is a large file of its own: it comes after the first screen, then the shelves are rebuilt. */
  function loadVodData() {
    if (window.VOD_DATA && window.VOD_DATA.length) return;
    var urls = [(window.ITV_BASE || '') + 'vod_data.json', REMOTE + 'vod_data.json'], i = 0;
    (function next() {
      if (i >= urls.length) return;
      xhrJson(urls[i++], 20000, function (list) {
        if (!Array.isArray(list) || !list.length) { next(); return; }
        window.VOD_DATA = list;
        homeDirty = true;
        if (TVUI && !atv.search) { atvBuild(); atvLayout(); atvMark(); }
        else if (PHONE && state.tab === 'home') buildHome();
      }, next);
    })();
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
      o.number = parseInt(c.number, 10) || i + 1; // channel data is remote: numbers only, never markup
      o.category = c.category || 'ערוצים';
      o.desc = c.desc || '';
      o.lang = c.lang || 'he';
      o.backups = (c.backups && c.backups.length) ? c.backups : (c.fallbackUrls || []);
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
    if (atv.built) atv.dirty = true;
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
    else if (state.cat !== 'ALL') r = r.filter(function (c) {
      if (c.category === state.cat) return true;
      if (state.cat === 'ספורט (Sports)' && /^(sport|one_)/i.test(c.id)) return true;
      if (state.cat === 'סרטים וסדרות (Movies & Series)' && /^(viva|eretz|keshet_drama|keshet_comedy|hot_|yes_movies)/i.test(c.id)) return true;
      return false;
    });
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
    var heroPi = !ch.isVod && !isRadioCh(ch) && !isApp(ch) ? getEpgProgramInfo(ch) : null;
    if (heroPi && heroPi.cur) {
      var hCur = heroPi.cur;
      var hTime = formatEpgTime(hCur[0]) + '–' + formatEpgTime(hCur[0] + hCur[1]);
      var hSyn = hCur[3] ? ' — ' + hCur[3] : (ch.desc ? ' — ' + ch.desc : '');
      el.heroDesc.textContent = (heroPi.isLive ? '● כעת: ' : 'בקרוב: ') + hCur[2] + ' (' + hTime + ')' + hSyn;
    } else {
      el.heroDesc.textContent = ch.desc || '';
    }
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
    } else if (state.zone === 'atv') { atvMark(); return; }
    else if (state.zone === 'grid') t = cards[state.gridIdx];
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
    if (TVUI) { atvRestore(); return; }
    state.zone = 'grid';
    if (!state.list.length) { markFocus(); return; }
    state.gridIdx = clamp(i, 0, state.list.length - 1);
    ensureRendered(state.gridIdx + GRID_PAGE / 2);
    markFocus();
    updateHero(state.list[state.gridIdx]);
  }
  function focusRail(i) { if (TVUI) { atvFocusPill(atv.tab); return; } state.zone = 'rail'; state.railIdx = clamp(i, 0, state.cats.length - 1); markFocus(); }
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
    scheduleCloudSync();
    homeDirty = true;
    atv.dirty = true;
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
    atv.dirty = true;
    S.lastId = ch.id;
    saveSettings();
    var has = state.cats.some(function (c) { return c.id === 'RECENT'; });
    if (!has) buildCategories();
  }

  // ================================================================== phone: tabs, home (hero pager + shelves), navigation bar
  var TABS = ['home', 'explore', 'library', 'settings'];
  var TAB_TITLES = { explore: 'ערוצים', library: 'הרשימה שלי', settings: 'הגדרות' };
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
        if (!state.list.length) el.grid.innerHTML = emptyState('star', 'עוד אין פה כלום...',
          'שמור ערוצים למועדפים כדי לראות אותם כאן.');
        el.gridTitle.textContent = target === 'FAVORITES' ? '★ הרשימה שלי' : '🕘 צפית לאחרונה';
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
    openDialog({ ico: '', title: 'תבחר את הוויב שלך', html: html, cls: 'profile',
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


  // ================================================================== TV Profiles Engine (Netflix / Disney+ Style)
  var TV_PROFILES_KEY = 'itv_tv_profiles';
  var TV_ACTIVE_KEY = 'itv_tv_active_profile_id';
  var tvProfiles = [];
  var tvActiveProfile = null;
  var tvProfileManaging = false;
  var tvProfileZone = 'cards'; // 'cards' | 'actions'
  var tvProfileIdx = 0;

  function loadTvProfiles() {
    try {
      var raw = localStorage.getItem(TV_PROFILES_KEY);
      if (raw) tvProfiles = JSON.parse(raw);
    } catch (e) { tvProfiles = []; }
    if (!Array.isArray(tvProfiles) || !tvProfiles.length) {
      tvProfiles = [
        {
          id: 'p_main',
          name: 'ראשי',
          avatar: (S && S.avatar) || '1f60e',
          color: 'gold',
          isKids: false,
          favorites: state.favorites ? state.favorites.slice() : [],
          recents: state.recents ? state.recents.slice() : []
        },
        {
          id: 'p_kids',
          name: 'ילדים',
          avatar: '1f981',
          color: 'amber',
          isKids: true,
          favorites: ['partner_kids_il', 'partner_toddlers_luli_il', 'hop_kids_israel', 'junior_plus_kids_il', 'baby_israel_tv', 'kan_hinuchit'],
          recents: []
        }
      ];
      saveTvProfiles();
    }
    var activeId = localStorage.getItem(TV_ACTIVE_KEY);
    tvActiveProfile = tvProfiles.find(function (p) { return p.id === activeId; }) || tvProfiles[0];
    applyTvProfileData(tvActiveProfile, false);
  }

  function saveTvProfiles() {
    if (tvActiveProfile) {
      tvActiveProfile.favorites = state.favorites ? state.favorites.slice() : [];
      tvActiveProfile.recents = state.recents ? state.recents.slice() : [];
    }
    try {
      localStorage.setItem(TV_PROFILES_KEY, JSON.stringify(tvProfiles));
      if (tvActiveProfile) localStorage.setItem(TV_ACTIVE_KEY, tvActiveProfile.id);
    } catch (e) {}
  }

  function getActiveTvProfile() {
    try {
      if (!tvActiveProfile) loadTvProfiles();
    } catch (e) {
      console.warn('loadTvProfiles error:', e);
    }
    return tvActiveProfile;
  }

  function buildChannels() {
    return loadChannels();
  }

  function applyTvProfileData(p, notify) {
    if (!p) return;
    tvActiveProfile = p;
    try { localStorage.setItem(TV_ACTIVE_KEY, p.id); } catch (e) {}
    state.favorites = Array.isArray(p.favorites) ? p.favorites.slice() : [];
    state.recents = Array.isArray(p.recents) ? p.recents.slice() : [];
    lsSet('israel_tv_favorites', state.favorites);
    scheduleCloudSync();
    lsSet('israel_tv_recents', state.recents);
    S.avatar = p.avatar || '1f60e';
    S.kids = !!p.isKids;
    try {
      if (typeof loadChannels === 'function') loadChannels();
    } catch (err) { console.error('loadChannels error in profile:', err); }
    try { updateAvatar(); } catch (err) {}
    try { if (typeof atvRenderPill === 'function') atvRenderPill(); } catch (err) {}
    try { if (typeof buildCategories === 'function') buildCategories(); } catch (err) {}
    try { if (typeof filterList === 'function') filterList(); } catch (err) {}
    try { if (TVUI && typeof atvBuild === 'function') atvBuild(); } catch (err) {}
    if (notify) toast('שלום, ' + p.name + '!');
  }

  function renderTvProfilesScreen() {
    var grid = $('tv-profiles-grid');
    if (!grid) return;
    var html = '';
    tvProfiles.forEach(function (p, idx) {
      var isCurrent = tvActiveProfile && tvActiveProfile.id === p.id;
      var badge = p.isKids ? '<span class="net-prof-badge">🧸 ילדים</span>' : '';
      var editIcon = tvProfileManaging ? '<span class="net-prof-edit-badge">✏️ עריכה</span>' : '';
      html += '<div class="net-prof-card" data-idx="' + idx + '" data-pid="' + esc(p.id) + '">' +
        '<img class="net-prof-avatar" src="' + avatarUrl(p.avatar || '1f60e') + '" alt="">' +
        editIcon +
        '<div class="net-prof-name">' + esc(p.name) + '</div>' +
        badge +
        (isCurrent && !tvProfileManaging ? '<div class="net-prof-active-dot">● פעיל</div>' : '') +
        '</div>';
    });
    if (tvProfiles.length < 8) {
      html += '<div class="net-prof-card net-prof-add" data-idx="' + tvProfiles.length + '" data-act="add">' +
        '<div class="net-prof-add-ico">+</div>' +
        '<div class="net-prof-name">הוסף פרופיל</div>' +
        '</div>';
    }
    grid.innerHTML = html;

    var btnManage = $('btn-tv-prof-manage');
    var btnDone = $('btn-tv-prof-done');
    var btnClose = $('btn-tv-prof-close');
    if (btnManage && btnDone) {
      toggleClass(btnManage, 'hidden', tvProfileManaging);
      toggleClass(btnDone, 'hidden', !tvProfileManaging);
    }
    if (btnManage && !btnManage._bound) {
      btnManage._bound = true;
      btnManage.addEventListener('click', function () {
        tvProfileManaging = true;
        renderTvProfilesScreen();
        tvProfileZone = 'cards';
        tvProfileIdx = 0;
        markTvProfilesFocus();
      });
    }
    if (btnDone && !btnDone._bound) {
      btnDone._bound = true;
      btnDone.addEventListener('click', function () {
        tvProfileManaging = false;
        renderTvProfilesScreen();
        tvProfileZone = 'cards';
        tvProfileIdx = 0;
        markTvProfilesFocus();
      });
    }
    if (btnClose && !btnClose._bound) {
      btnClose._bound = true;
      btnClose.addEventListener('click', function () {
        closeTvProfiles();
      });
    }

    Array.prototype.forEach.call(grid.children, function (card) {
      card.addEventListener('click', function () {
        var act = card.getAttribute('data-act');
        if (act === 'add') {
          openAddProfileDialog();
          return;
        }
        var pid = card.getAttribute('data-pid');
        var p = tvProfiles.find(function (item) { return item.id === pid; });
        if (!p) return;
        if (tvProfileManaging) {
          openEditProfileDialog(p);
        } else {
          applyTvProfileData(p, true);
          saveTvProfiles();
          closeTvProfiles();
          // Land on the home screen; playback starts by itself only with the "play the last channel" setting
          if (TVUI) {
            atvBuild();
            if (atv.rows && atv.rows.length) atvFocus(0, 0);
          } else if (state.visible && state.visible.length) {
            focusGrid(0);
          }
          var lastCh = S.autoplay && p.recents && p.recents.length ? indexInList(p.recents[0]) : -1;
          if (lastCh !== -1) playIndex(lastCh);
        }
      });
    });
  }

  function markTvProfilesFocus() {
    var grid = $('tv-profiles-grid');
    if (!grid) return;
    Array.prototype.forEach.call(grid.querySelectorAll('.net-prof-card'), function (c) {
      c.classList.remove('focused');
    });
    var actBtns = $('tv-profiles-actions').querySelectorAll('button:not(.hidden)');
    Array.prototype.forEach.call(actBtns, function (b) {
      b.classList.remove('focused');
    });

    if (tvProfileZone === 'cards') {
      var card = grid.children[tvProfileIdx];
      if (card) {
        card.classList.add('focused');
        try { card.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' }); } catch (e) {}
      }
    } else {
      var btn = actBtns[tvProfileIdx];
      if (btn) btn.classList.add('focused');
    }
  }

  function openTvProfiles() {
    if (!tvProfiles.length) loadTvProfiles();
    tvProfileManaging = false;
    tvProfileZone = 'cards';
    var curIdx = tvProfiles.findIndex(function (p) { return p.id === (tvActiveProfile && tvActiveProfile.id); });
    tvProfileIdx = curIdx >= 0 ? curIdx : 0;
    state.overlay = 'tv-profiles';
    renderTvProfilesScreen();
    var elScreen = $('tv-profiles');
    if (elScreen) elScreen.classList.remove('hidden');
    markTvProfilesFocus();
  }

  function closeTvProfiles() {
    var elScreen = $('tv-profiles');
    if (elScreen) elScreen.classList.add('hidden');
    state.overlay = null;
    markFocus();
  }

  function tvProfilesKey(code) {
    var totalCards = tvProfiles.length + (tvProfiles.length < 8 ? 1 : 0);
    if (isBack(code)) {
      if (tvProfileManaging) {
        tvProfileManaging = false;
        renderTvProfilesScreen();
        markTvProfilesFocus();
        return;
      }
      closeTvProfiles();
      return;
    }
    if (code === 37) {
      if (tvProfileZone === 'cards') {
        tvProfileIdx = (tvProfileIdx + 1) % totalCards;
        markTvProfilesFocus();
      } else {
        var actBtns = $('tv-profiles-actions').querySelectorAll('button:not(.hidden)');
        tvProfileIdx = Math.min(tvProfileIdx + 1, actBtns.length - 1);
        markTvProfilesFocus();
      }
    } else if (code === 39) {
      if (tvProfileZone === 'cards') {
        tvProfileIdx = (tvProfileIdx - 1 + totalCards) % totalCards;
        markTvProfilesFocus();
      } else {
        tvProfileIdx = Math.max(tvProfileIdx - 1, 0);
        markTvProfilesFocus();
      }
    } else if (code === 40) {
      if (tvProfileZone === 'cards') {
        tvProfileZone = 'actions';
        tvProfileIdx = 0;
        markTvProfilesFocus();
      }
    } else if (code === 38) {
      if (tvProfileZone === 'actions') {
        tvProfileZone = 'cards';
        tvProfileIdx = Math.min(tvProfileIdx, totalCards - 1);
        markTvProfilesFocus();
      }
    }
  }

  function tvProfilesOk() {
    if (tvProfileZone === 'cards') {
      var cards = $('tv-profiles-grid').children;
      var c = cards[tvProfileIdx];
      if (c) c.click();
    } else {
      var actBtns = $('tv-profiles-actions').querySelectorAll('button:not(.hidden)');
      var b = actBtns[tvProfileIdx];
      if (b) b.click();
    }
  }

  function openAddProfileDialog() {
    var chosenAv = AVATARS[tvProfiles.length % AVATARS.length];
    var html = '<div style="margin-bottom:1.5rem;">' +
      '<label style="display:block;margin-bottom:.5rem;font-weight:700;">שם הפרופיל:</label>' +
      '<input id="prof-add-name" type="text" class="dlg-focus" value="פרופיל ' + (tvProfiles.length + 1) + '" style="width:100%;padding:.8rem;border-radius:1rem;background:rgba(255,255,255,.1);border:1px solid rgba(255,255,255,.2);color:#fff;font-size:1.3rem;">' +
      '</div>' +
      '<div style="margin-bottom:1.5rem;">' +
      '<label style="display:block;margin-bottom:.5rem;font-weight:700;">בחירת אווטאר:</label>' +
      '<div class="av-grid">' + AVATARS.map(function (c) {
        return '<button class="btn dlg-focus av-opt' + (c === chosenAv ? ' on' : '') + '" data-av="' + c + '"><img alt="" src="' + avatarUrl(c) + '"></button>';
      }).join('') + '</div>' +
      '</div>' +
      '<div>' +
      '<label style="display:inline-flex;align-items:center;gap:.8rem;cursor:pointer;font-size:1.2rem;font-weight:700;">' +
      '<input id="prof-add-kids" type="checkbox" style="transform:scale(1.4);"> פרופיל ילדים (מציג תכני ילדים בלבד)' +
      '</label>' +
      '</div>';

    openDialog({
      ico: '👤',
      title: 'הוספת פרופיל חדש',
      html: html,
      buttons: [
        { label: 'ביטול' },
        {
          label: 'שמור פרופיל',
          primary: true,
          act: function () {
            var nameInp = $('prof-add-name');
            var kidsInp = $('prof-add-kids');
            var name = (nameInp && nameInp.value.trim()) || ('פרופיל ' + (tvProfiles.length + 1));
            var isKids = kidsInp ? kidsInp.checked : false;
            var newProf = {
              id: 'p_' + Date.now(),
              name: name,
              avatar: chosenAv,
              color: isKids ? 'amber' : 'arctic',
              isKids: isKids,
              favorites: isKids ? ['partner_kids_il', 'partner_toddlers_luli_il', 'hop_kids_israel', 'junior_plus_kids_il', 'baby_israel_tv'] : [],
              recents: []
            };
            tvProfiles.push(newProf);
            applyTvProfileData(newProf, true);
            saveTvProfiles();
            openTvProfiles();
          }
        }
      ]
    });

    Array.prototype.forEach.call(el.dialogBody.querySelectorAll('.av-opt'), function (b) {
      b.onclickAction = function () {
        chosenAv = b.getAttribute('data-av');
        Array.prototype.forEach.call(el.dialogBody.querySelectorAll('.av-opt'), function (x) { toggleClass(x, 'on', x === b); });
      };
      b.addEventListener('click', function (e) { e.stopPropagation(); b.onclickAction(); });
    });
  }

  function openEditProfileDialog(p) {
    var chosenAv = p.avatar || '1f60e';
    var html = '<div style="margin-bottom:1.5rem;">' +
      '<label style="display:block;margin-bottom:.5rem;font-weight:700;">שם הפרופיל:</label>' +
      '<input id="prof-edit-name" type="text" class="dlg-focus" value="' + esc(p.name) + '" style="width:100%;padding:.8rem;border-radius:1rem;background:rgba(255,255,255,.1);border:1px solid rgba(255,255,255,.2);color:#fff;font-size:1.3rem;">' +
      '</div>' +
      '<div style="margin-bottom:1.5rem;">' +
      '<label style="display:block;margin-bottom:.5rem;font-weight:700;">בחירת אווטאר:</label>' +
      '<div class="av-grid">' + AVATARS.map(function (c) {
        return '<button class="btn dlg-focus av-opt' + (c === chosenAv ? ' on' : '') + '" data-av="' + c + '"><img alt="" src="' + avatarUrl(c) + '"></button>';
      }).join('') + '</div>' +
      '</div>' +
      '<div>' +
      '<label style="display:inline-flex;align-items:center;gap:.8rem;cursor:pointer;font-size:1.2rem;font-weight:700;">' +
      '<input id="prof-edit-kids" type="checkbox" ' + (p.isKids ? 'checked' : '') + ' style="transform:scale(1.4);"> פרופיל ילדים (מציג תכני ילדים בלבד)' +
      '</label>' +
      '</div>';

    var btns = [
      { label: 'ביטול' },
      {
        label: 'שמור שינויים',
        primary: true,
        act: function () {
          var nameInp = $('prof-edit-name');
          var kidsInp = $('prof-edit-kids');
          p.name = (nameInp && nameInp.value.trim()) || p.name;
          p.avatar = chosenAv;
          p.isKids = kidsInp ? kidsInp.checked : false;
          if (tvActiveProfile && tvActiveProfile.id === p.id) {
            applyTvProfileData(p, false);
          }
          saveTvProfiles();
          openTvProfiles();
        }
      }
    ];

    if (tvProfiles.length > 1) {
      btns.unshift({
        label: 'מחק פרופיל',
        act: function () {
          confirmBox('האם למחוק את הפרופיל "' + p.name + '"?', function () {
            tvProfiles = tvProfiles.filter(function (x) { return x.id !== p.id; });
            if (tvActiveProfile.id === p.id) {
              applyTvProfileData(tvProfiles[0], true);
            }
            saveTvProfiles();
            openTvProfiles();
          });
        }
      });
    }

    openDialog({
      ico: '✏️',
      title: 'עריכת פרופיל: ' + p.name,
      html: html,
      buttons: btns
    });

    Array.prototype.forEach.call(el.dialogBody.querySelectorAll('.av-opt'), function (b) {
      b.onclickAction = function () {
        chosenAv = b.getAttribute('data-av');
        Array.prototype.forEach.call(el.dialogBody.querySelectorAll('.av-opt'), function (x) { toggleClass(x, 'on', x === b); });
      };
      b.addEventListener('click', function (e) { e.stopPropagation(); b.onclickAction(); });
    });
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
    var parts = ['<span class="live">Live</span>', esc(splitCat(ch.category))];
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
        '<div class="slide-actions"><button class="icon-tonal' + (isFav(ch) ? ' on' : '') + '" data-act="fav" aria-label="מועדף">' + I(isFav(ch) ? 'star' : 'starOutline') + '</button>' +
        '<button class="fab-play" data-act="play">' + I('play') + (isRadioCh(ch) ? 'האזנה' : 'צפייה') + '</button>' +
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
    var macro = state.homeMacro || '', ilOnly = !!state.homeIsraeli;
    var recents = macro ? [] : state.recents.map(function (id) { return state.all[indexInAll(id)]; }).filter(Boolean);
    if (recents.length) shelves.push({ id: 'RECENT', title: 'המשך צפייה', list: recents, wide: true });
    var favs = macro ? [] : state.all.filter(isFav);
    if (favs.length) shelves.push({ id: 'FAVORITES', title: 'המועדפים שלי', list: favs });
    var dubShelf = null, israeli = /ישראל|בעברית|מנויים|מסורת/;
    state.cats.forEach(function (c) {
      if (c.id === 'ALL' || c.id === 'FAVORITES' || c.id === 'RECENT') return;
      if (macro && c.id.split(' (')[0].split(' - ')[0].trim() !== macro) return;
      if (ilOnly && (c.id === 'DUB' || !israeli.test(c.id))) return;
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
    var ilRow = document.createElement('label');
    ilRow.className = 'il-row';
    ilRow.innerHTML = '<span>רק ישראלי</span><input type="checkbox" class="il-toggle"' + (ilOnly ? ' checked' : '') + '><i class="il-switch"></i>';
    frag.appendChild(ilRow);
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

    // the phone app's top row: settings, category chips, "only Israeli"
    $('btn-ph-settings').addEventListener('click', function () { el.navbar.querySelector('.nb-item[data-tab="settings"]').click(); });
    $('home-chips').addEventListener('click', function (e) {
      var chip = e.target.closest('.home-chip');
      if (!chip) return;
      if (chip.getAttribute('data-more')) { setTab('explore'); return; }
      state.homeMacro = chip.getAttribute('data-macro');
      Array.prototype.forEach.call(this.children, function (c) { toggleClass(c, 'on', c === chip); });
      buildHome();
    });
    el.home.addEventListener('change', function (e) {
      if (!e.target.classList.contains('il-toggle')) return;
      state.homeIsraeli = e.target.checked;
      buildHome();
    });

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

  // ================================================================== TV: the Apple TV+ (tvOS) browse screen
  // Big screens get a full-bleed stage instead of the guide: a floating glass pill on top, the focused channel's
  // details on the right, and at the bottom a vertical feed of shelves, each one horizontal row of channel cards.
  // Every move is a translate3d / opacity change on its own layer with tvOS's curve (cubic-bezier(.16, 1, .3, 1),
  // 420 ms): nothing measures the layout, nothing animates width / height / top / left, no scrollIntoView.
  // Resting on a card for 550 ms fades a live preview of the channel in behind everything (the same <video> the
  // player uses), and OK just turns it into the full-screen player – no reload, no cut. The preview is the only
  // silent playback in the app: the moment the channel is watched it plays with full sound, and from then on
  // nothing in the app ever mutes it.
  var TVUI = false;
  var atv = { rows: [], row: 0, cols: [], zone: 'rows', tab: 0, tabs: [], rendered: [], tracks: [], items: [],
    rowEls: [], built: false, dirty: false, search: false, query: '', flip: false, bdFlip: false,
    restTimer: null, metaTimer: null, stopTimer: null, searchTimer: null, focusedEl: null };
  var ATV_FIRST = 14, ATV_MORE = 18;
  var ATV_ROWS = [
    ['all_channels', '📺 כל הערוצים (All Channels)'],
    ['broadcast', 'ערוצי ברודקאסט וחדשות'],
    ['sports', 'ספורט בשידור חי'],
    ['movies', 'סרטים וקולנוע'],
    ['vod', '🍿 סדרות לצפייה ישירה (VOD)'],
    ['kids', 'ילדים ונוער'],
    ['fun', 'בידור, טבע ומוזיקה'],
    ['world', 'ערוצים בינלאומיים'],
    ['radio', 'רדיו ישראלי'],
    ['faith', 'דת ומסורת']
  ];
  var ATV_TABS = [
    ['פרופיל', 'profiles'],
    ['כל הערוצים', 'all_channels'],
    ['צפייה כעת', 'top'],
    ['ערוצי ישראל', 'broadcast'],
    ['ספורט', 'sports'],
    ['סרטים', 'movies'],
    ['סדרות VOD', 'vod'],
    ['ילדים', 'kids'],
    ['רדיו', 'radio'],
    ['מועדפים', 'mine'], ['לוח שידורים', 'guide'],
    ['חיפוש', 'search'],
    ['פיצול מסך', 'multiview'],
    ['הקלטות Drive', 'gdrive'],
    ['הגדרות', 'settings']
  ];

  function atvGeo() { return activeDesign(); }
  function atvRem(rem) { return rem * (parseFloat(document.documentElement.style.fontSize) || 16); }

  /** Which shelf a channel belongs on. */
  function atvBucket(c) {
    var en = c.categoryEn || '', cat = c.category || '';
    if (/ילדים/.test(cat) && !isRadioCh(c)) return 'kids';
    if (isRadioCh(c)) return 'radio';
    if (/ספורט/.test(cat) || en === 'sports') return 'sports';
    if (/ילדים/.test(cat) || en === 'kids') return 'kids';
    if (/סרטים|סדרות|קומדיה/.test(cat) || en === 'movies' || en === 'world_movies' || en === 'comedy' || en === 'series' || en === 'classic') return 'movies';
    if (/מסורת|דת/.test(cat) || en === 'religion' || en === 'tradition' || en === 'community') return 'faith';
    if (/אוכל|טיולים|מדע|מוזיקה|דוקו|לייף/.test(cat) || /^(life|lifestyle|entertainment|cooking|culture|education|docu|documentary|music|travel|food|science)$/.test(en)) return 'fun';
    if (/בינלאומי|חדשות מהעולם/.test(cat) || en === 'world') return 'world';
    if (/ערוצי ישראל|כלכלה/.test(cat) || en === 'israel' || en === 'business') return 'broadcast';
    return 'world';
  }

  function atvRowsData() {
    if (atv.search) {
      var q = atv.query.trim().toLowerCase();
      var res = !q ? [] : state.all.filter(function (c) {
        if (S.kids && !isKidsCh(c)) return false;
        return c.name.toLowerCase().indexOf(q) !== -1 || String(c.number) === q ||
          (c.countryHe && c.countryHe.indexOf(q) !== -1) || (c.title && c.title.toLowerCase().indexOf(q) !== -1);
      });
      return [{ id: 'search', title: q ? (S.kids ? 'תוצאות לחיפוש ילדים "' : 'תוצאות לחיפוש "') + atv.query.trim() + '" · ' + res.length : (S.kids ? 'הקלידו שם ערוץ ילדים' : 'הקלידו שם ערוץ'), list: res }];
    }

    if (S.kids) {
      var kidsAll = state.all.filter(isKidsCh);
      var kidsHe = kidsAll.filter(function (c) { return c.country === 'IL' || c.lang === 'he'; });
      var kidsWorld = kidsAll.filter(function (c) { return c.country !== 'IL' && c.lang !== 'he'; });
      var kidsMine = [];
      var seenK = {};
      (state.recents || []).concat(state.favorites || []).forEach(function (id) {
        var idx = indexInAll(id);
        if (idx !== -1 && isKidsCh(state.all[idx]) && !seenK[id]) {
          seenK[id] = 1;
          kidsMine.push(state.all[idx]);
        }
      });
      var kRows = [];
      if (kidsMine.length) kRows.push({ id: 'mine', title: '🧸 מועדפים לאחרונה (ילדים)', list: kidsMine });
      if (kidsHe.length) kRows.push({ id: 'kids_he', title: '🇮🇱 ערוצי ילדים ונוער בעברית', list: kidsHe });
      if (kidsWorld.length) kRows.push({ id: 'kids_world', title: '🌍 ערוצי ילדים וסדרות מצוירות מהעולם', list: kidsWorld });
      return kRows;
    }

    var buckets = {};
    ATV_ROWS.forEach(function (r) { buckets[r[0]] = []; });
    buckets.all_channels = (S.kids ? state.all.filter(isKidsCh) : state.all).slice();
    if (window.VOD_DATA && window.VOD_DATA.length) {
      buckets.vod = window.VOD_DATA.map(function (s) {
        return {
          id: s.id,
          name: s.title,
          title: s.title + ' - ' + (s.genres || []).join(', '),
          badge: 'VOD',
          category: 'סדרות VOD',
          categoryEn: 'vod',
          logo: s.poster,
          desc: s.desc,
          isVod: true,
          vodData: s,
          url: (s.episodes && s.episodes[0] && s.episodes[0].url) || ''
        };
      });
    }
    var kidsRadio = [];
    state.all.forEach(function (c) {
      var b = atvBucket(c);
      if (b === 'broadcast') {
        if (/^(music_il|keshet_music|foody|keshet_food|good_life|hot_bidur|hot_reality|hot_vacation|yes_docu|radios_video)/i.test(c.id)) buckets.fun.push(c);
      }
      // Israeli sports radio (Radio Sport 5) also opens the sports shelf, next to Sport 5's live stream
      if (b === 'radio' && !c.imported && /ספורט/.test(c.name)) buckets.sports.push(c);
      if (b === 'radio' && /ילדים/.test(c.category || '')) { buckets.kids.push(c); kidsRadio.push(c); return; }
      buckets[b].push(c);
    });
    kidsRadio.forEach(function (c) { buckets.radio.push(c); });
    var rows = [], seen = {}, mine = [];
    state.recents.concat(state.favorites).forEach(function (id) {
      var i = indexInAll(id);
      if (i !== -1 && !seen[id]) { seen[id] = 1; mine.push(state.all[i]); }
    });
    if (mine.length) rows.push({ id: 'mine', title: 'צפייה לאחרונה ומועדפים', list: mine });
    ATV_ROWS.forEach(function (r) {
      var list = buckets[r[0]];
      if (IS_WEB) list = list.filter(function (x) { return !appOnly(x); }).concat(list.filter(appOnly));
      if (list.length) rows.push({ id: r[0], title: r[1], list: list });
    });
    return rows;
  }

  function atvRowIndex(id) { for (var i = 0; i < atv.rows.length; i++) if (atv.rows[i].id === id) return i; return -1; }

  function atvBuild() {
    var keepId = atv.rows[atv.row] && atv.rows[atv.row].id;
    var keepCh = atv.rows[atv.row] && atv.rows[atv.row].list[atv.cols[atv.row] || 0];
    atv.rows = atvRowsData();
    atv.built = true;
    atv.dirty = false;
    atv.cols = atv.rows.map(function () { return 0; });
    atv.rendered = []; atv.tracks = []; atv.items = []; atv.rowEls = []; atv.focusedEl = null;
    var box = $('atv-rows');
    box.innerHTML = '';
    var frag = document.createDocumentFragment();
    atv.rows.forEach(function (row, r) {
      var sec = document.createElement('section');
      sec.className = 'atv-row';
      sec.innerHTML = '<h3 class="atv-row-title">' + esc(row.title) + (row.list.length > 30 ? '<small>' + row.list.length + '</small>' : '') +
        '</h3><div class="atv-track"></div>';
      atv.rowEls.push(sec);
      atv.tracks.push(sec.querySelector('.atv-track'));
      atv.items.push([]);
      atv.rendered.push(0);
      frag.appendChild(sec);
    });
    box.appendChild(frag);
    atv.rows.forEach(function (row, r) { atvRender(r, ATV_FIRST); });
    // stay where we were (a rebuild after a favourite / a new recent)
    var r0 = keepId ? atvRowIndex(keepId) : -1;
    atv.row = r0 === -1 ? 0 : r0;
    if (r0 !== -1 && keepCh) {
      var list = atv.rows[r0].list;
      for (var k = 0; k < list.length; k++) if (list[k].id === keepCh.id) atv.cols[r0] = k;
    }
    atvRenderPill();
    if (!atv.rows.length) { $('atv-rows').innerHTML = '<div class="atv-empty">' + (atv.search ? '' : 'אין ערוצים להצגה') + '</div>'; }
  }

  function atvRender(r, upTo) {
    var row = atv.rows[r], track = atv.tracks[r];
    var want = Math.min(row.list.length, upTo);
    if (want <= atv.rendered[r]) return;
    var frag = document.createDocumentFragment();
    for (var i = atv.rendered[r]; i < want; i++) {
      var ch = row.list[i];
      var it = document.createElement('div');
      it.className = 'atv-item' + (appOnly(ch) ? ' is-locked' : '') + (ch.isVod ? ' is-vod' : '');
      it.setAttribute('data-r', r);
      it.setAttribute('data-c', i);
      var q = isApp(ch) ? 'אפליקציה' : isRadioCh(ch) ? 'רדיו' : (ch.badge || '');
      it.innerHTML = '<div class="atv-card"><div class="atv-logo"><img alt=""><div class="mono hidden"></div></div>' +
        (q ? '<span class="atv-q">' + esc(q) + '</span>' : '') + '<span class="atv-num">' + (ch.number || '') + '</span>' +
        '<i class="atv-sheen"></i></div><div class="atv-name">' + esc(ch.name) + '</div>';
      lazyLogo(it.querySelector('img'), it.querySelector('.mono'), ch);
      frag.appendChild(it);
      atv.items[r].push(it);
    }
    track.appendChild(frag);
    atv.rendered[r] = want;
  }

  function atvRenderPill() {
    var pill = $('atv-pill');
    if (!pill) return;
    try {
      var tabs = ATV_TABS.filter(function (t) {
        return t[1] === 'profiles' || t[1] === 'all_channels' || t[1] === 'top' || t[1] === 'vod' || t[1] === 'search' || t[1] === 'multiview' || t[1] === 'guide' || t[1] === 'gdrive' || t[1] === 'settings' || atvRowIndex(t[1]) !== -1 || (atv.search && t[1] !== 'mine');
      });
      if (!tabs || !tabs.length) {
        tabs = [['מי צופה?', 'profiles'], ['כל הערוצים', 'all_channels'], ['צפייה כעת', 'top'], ['סדרות VOD', 'vod'], ['מדריך שידורים', 'guide'], ['חיפוש', 'search'], ['הגדרות', 'settings']];
      }
      if (S.kids) tabs = tabs.filter(function (t) { return t[1] !== 'settings'; }).concat([['יציאה ממצב ילדים', 'kids-exit']]);
      atv.tabs = tabs;
      pill.innerHTML = tabs.map(function (t, i) {
        var id = t[1];
        if (id === 'profiles') {
          var p = null;
          try { p = getActiveTvProfile(); } catch (pe) {}
          var av = p ? avatarUrl(p.avatar || '1f60e') : avatarUrl('1f60e');
          var nm = p ? p.name : 'פרופיל';
          return '<button class="atv-tab atv-tab-prof" data-i="' + i + '" title="מי צופה? - החלף פרופיל"><img class="atv-prof-ico" src="' + av + '" alt=""><span>' + esc(nm) + '</span></button>';
        }
        if (id === 'search' || id === 'multiview' || id === 'gdrive' || id === 'settings') {
          if (id === 'gdrive') return '<button class="atv-tab ico-only" data-i="' + i + '" aria-label="הקלטות ענן" title="Google Drive והקלטות ענן">☁️</button>';
          var ico = id === 'search' ? I('search') : id === 'multiview' ? I('multiview') : I('settings');
          return '<button class="atv-tab ico-only" data-i="' + i + '" aria-label="' + esc(t[0]) + '" title="' + esc(t[0]) + '">' + ico + '</button>';
        }
        return '<button class="atv-tab" data-i="' + i + '">' + esc(t[0]) + '</button>';
      }).join('');
      if (tabs.length && pill.children.length > 0) {
        pill.classList.remove('hidden');
        pill.setAttribute('data-empty', 'false');
        pill.style.display = 'flex';
        pill.style.visibility = 'visible';
      } else {
        pill.classList.add('hidden');
        pill.setAttribute('data-empty', 'true');
        pill.style.setProperty('display', 'none', 'important');
        pill.style.setProperty('visibility', 'hidden', 'important');
      }
      atvPillState();
    } catch (err) {
      console.error('atvRenderPill error:', err);
      try {
        pill.classList.add('hidden');
        pill.setAttribute('data-empty', 'true');
        pill.style.setProperty('display', 'none', 'important');
        pill.style.setProperty('visibility', 'hidden', 'important');
      } catch (e) {}
    }
  }

  /** The active tab is the white pill; while the remote is on the bar, the focused tab grows a little. */
  function atvPillState() {
    var row = atv.rows[atv.row], active = -1;
    for (var i = 0; i < atv.tabs.length; i++) {
      var t = atv.tabs[i][1];
      if ((atv.search && t === 'search') || (!atv.search && row && t === row.id)) { active = i; break; }
    }
    if (active === -1 && !atv.search && atv.row === 0) active = 0;
    var btns = $('atv-pill').children;
    for (var k = 0; k < btns.length; k++) {
      var onBar = atv.zone === 'pill' && state.zone === 'atv' && !state.overlay;
      // on the bar the focused tab itself wears the white pill; elsewhere the active one does
      toggleClass(btns[k], 'on', !onBar && k === active);
      toggleClass(btns[k], 'focused', onBar && k === atv.tab);
    }
    if (atv.zone !== 'pill') atv.tab = active === -1 ? 0 : active;
  }

  /** Moves the camera: the shelves slide up / down, the row's track glides sideways, the rest is opacity. */
  function atvLayout() {
    var r = atv.row;
    $('atv-rows').style.transform = 'translate3d(0,' + (-r * atvRem(atvGeo().row)) + 'px,0)';
    for (var i = 0; i < atv.rowEls.length; i++) {
      var cls = 'atv-row' + (i < r ? ' above' : i === r ? ' active' : ' below');
      if (Math.abs(i - r) > 2) cls += ' far-off';
      atv.rowEls[i].className = cls;
    }
    var c = atv.cols[r] || 0;
    if (atv.tracks[r]) atv.tracks[r].style.transform = 'translate3d(' + (Math.max(0, c - 1) * atvRem(atvGeo().pitch)) + 'px,0,0)';
  }

  function atvMark() {
    if (atv.focusedEl) atv.focusedEl.classList.remove('focused');
    atv.focusedEl = null;
    if (atv.zone === 'rows' && atv.items[atv.row]) {
      var it = atv.items[atv.row][atv.cols[atv.row] || 0];
      if (it && state.zone === 'atv' && !state.overlay) { it.classList.add('focused'); atv.focusedEl = it; }
    }
    atvPillState();
  }

  function atvCurrent() {
    var row = atv.rows[atv.row];
    return row ? row.list[atv.cols[atv.row] || 0] : null;
  }

  /** Focus a card: camera, focus ring, the list the player zaps through, then (debounced) details and preview. */
  function atvFocus(r, c, fromPointer) {
    if (!atv.rows.length) return;
    r = clamp(r, 0, atv.rows.length - 1);
    var n = atv.rows[r].list.length;
    c = clamp(c, 0, Math.max(0, n - 1));
    atv.row = r;
    atv.cols[r] = c;
    atv.zone = 'rows';
    state.zone = 'atv';
    if (c > atv.rendered[r] - 8) atvRender(r, c + ATV_MORE);
    state.list = atv.rows[r].list;
    state.gridIdx = c;
    if (!fromPointer) atvLayout();
    atvMark();
    atvSettle(atv.rows[r].list[c]);
  }

  function atvFocusPill(i) {
    atv.zone = 'pill';
    state.zone = 'atv';
    atv.tab = clamp(i, 0, atv.tabs.length - 1);
    var t = atv.tabs[atv.tab][1];
    // a row tab jumps the shelves straight to its row
    if ((t === 'top' || t === 'all_channels') && atv.rows.length) { atv.row = 0; atvLayout(); state.list = atv.rows[0].list; }
    else if (atvRowIndex(t) !== -1) { atv.row = atvRowIndex(t); atvLayout(); state.list = atv.rows[atv.row].list; }
    atvMark();
    atvPillState();
    var btns = $('atv-pill').children;
    for (var k = 0; k < btns.length; k++) toggleClass(btns[k], 'focused', k === atv.tab);
    atvSettle(atvCurrent());
  }

  function atvSettle(ch) {
    if (atv.metaTimer) clearTimeout(atv.metaTimer);
    atv.metaTimer = setTimeout(function () { atvMeta(ch); }, fastMode() ? 160 : 70);
    if (atv.restTimer) clearTimeout(atv.restTimer);
    if (state.playing && state.playing !== ch && state.overlay !== 'player') atvStopPreview();
    if (!ch) return;
    atv.restTimer = setTimeout(function () { atvPreview(ch); }, PLATFORM === 'webos' ? 1200 : 550);
  }

  /** The focused channel's details, sliding up into the second of two layers while the first fades. */
  function atvMeta(ch) {
    var a = $('atv-meta-a'), b = $('atv-meta-b');
    var target = atv.flip ? b : a, other = atv.flip ? a : b;
    atv.flip = !atv.flip;
    if (!ch) { other.className = 'atv-meta'; target.className = 'atv-meta'; return; }
    var q = isApp(ch) ? 'אפליקציה' : isRadioCh(ch) ? 'רדיו' : (ch.badge || 'HD');
    var tags = (ch.isVod ? '' : '<span class="atv-live"><i></i>LIVE</span>') + '<span class="atv-pill-q">' + esc(q) + '</span>' +
      '<span class="atv-tag">' + esc(splitCat(ch.category)) + '</span>' +
      (ch.countryHe ? '<span class="atv-tag">' + esc(ch.countryHe) + '</span>' : '') +
      (isFav(ch) ? '<span class="atv-tag">★ מועדף</span>' : '');
    var epgHtml = '';
    var descText = ch.desc || '';
    var pi = !ch.isVod && !isRadioCh(ch) && !isApp(ch) ? getEpgProgramInfo(ch) : null;
    if (pi && pi.cur) {
      var cur = pi.cur, sMin = cur[0], dur = Math.max(1, cur[1]), eMin = sMin + dur;
      var pct = pi.isLive ? Math.min(100, Math.max(0, Math.round(((pi.nowMin - sMin) / dur) * 100))) : 0;
      var timeRange = formatEpgTime(sMin) + ' – ' + formatEpgTime(eMin);
      var nextTxt = pi.next ? ' · הבאה (' + formatEpgTime(pi.next[0]) + '): ' + esc(pi.next[2]) : '';
      epgHtml = '<div class="atv-epg-box">' +
        '<div class="atv-epg-head"><span class="atv-epg-now">' + (pi.isLive ? '● משודר כעת' : 'בקרוב') + '</span>' +
        '<b class="atv-epg-title">' + esc(cur[2]) + '</b>' +
        '<span class="atv-epg-time">' + timeRange + nextTxt + '</span></div>' +
        (pi.isLive ? '<div class="atv-epg-bar"><i style="width:' + pct + '%"></i></div>' : '') +
      '</div>';
      if (cur[3]) descText = cur[3];
    }
    target.innerHTML = '<div class="atv-mlogo"><img alt=""><div class="mono hidden"></div></div>' +
      '<h1 class="atv-title">' + esc(ch.name) + '</h1><div class="atv-tags">' + tags + '</div>' +
      epgHtml +
      (descText ? '<p class="atv-desc">' + esc(descText) + '</p>' : '') +
      (S.showSource ? '<p class="atv-src">' + esc(sourceLine(ch)) + '</p>' : '');
    var logoImg = target.querySelector('img'), logoMono = target.querySelector('.mono');
    setLogo(logoImg, logoMono, ch);
    // the logo stands in for the name (Nuvio hero); a channel without one, or whose logo fails, shows the name instead
    var nextLogo = logoImg.onerror;
    logoImg.onerror = function () {
      nextLogo();
      if (!logoMono.classList.contains('hidden') && target.contains(logoImg)) target.classList.remove('has-logo');
    };
    target.className = 'atv-meta in' + (ch.logo ? ' has-logo' : '');
    other.className = 'atv-meta';
    // the stage: a soft wash in the channel's colours, cross-faded
    var bd = atv.bdFlip ? $('atv-bd-b') : $('atv-bd-a'), bo = atv.bdFlip ? $('atv-bd-a') : $('atv-bd-b');
    atv.bdFlip = !atv.bdFlip;
    var h = hueOf(ch.id);
    bd.style.background = 'radial-gradient(ellipse at 78% 30%, hsla(' + h + ',70%,42%,.62), hsla(' + h + ',70%,42%,0) 58%),' +
      'radial-gradient(ellipse at 18% 75%, hsla(' + ((h + 50) % 360) + ',70%,34%,.5), hsla(' + ((h + 50) % 360) + ',70%,34%,0) 60%),#07070b';
    bd.className = 'atv-bd on';
    bo.className = 'atv-bd';
  }

  // ---- live preview behind the shelves
  function atvPreview(ch) {
    if (!TVUI || state.overlay || state.zone !== 'atv' || ch !== atvCurrent()) return;
    if (state.playing === ch) { document.body.classList.add('atv-live'); return; }
    if (isRadioCh(ch) || isApp(ch) || appOnly(ch) || fastMode()) return;
    if (atv.stopTimer) { clearTimeout(atv.stopTimer); atv.stopTimer = null; }
    if (state.playing) stopPlayback();
    state.preview = ch;
    state.playing = ch;
    el.player.classList.remove('hidden');
    loadStream(ch);
  }

  /** The preview fades back into the stage, then stops (it keeps playing if a new one starts meanwhile). */
  function atvStopPreview() {
    document.body.classList.remove('atv-live');
    if (atv.stopTimer) clearTimeout(atv.stopTimer);
    var was = state.playing;
    atv.stopTimer = setTimeout(function () {
      atv.stopTimer = null;
      if (state.overlay === 'player' || state.playing !== was) return;
      stopPlayback();
      el.player.classList.add('hidden');
    }, 700);
  }

  /** OK on a card: the preview (already playing) becomes the player in place – sound on, shelves slide away. */
  function atvWatch() {
    var ch = atvCurrent();
    if (!ch) return;
    if (ch.isVod && ch.vodData) {
      openVodModal(ch.vodData);
      return;
    }
    if (Dub) Dub.unlockAudio();
    var c = atv.cols[atv.row] || 0;
    state.list = atv.rows[atv.row].list;
    if (atv.restTimer) clearTimeout(atv.restTimer);
    if (atv.stopTimer) { clearTimeout(atv.stopTimer); atv.stopTimer = null; }
    if (state.playing === ch && !isRadioCh(ch) && !isApp(ch)) {
      var m = media();
      state.preview = null;
    state.catchup = state.catchupNext = null;
      m.muted = false;
      m.volume = 1;
      if (m.paused) { var p = m.play(); if (p && p.catch) p.catch(function () {}); }
      state.playIdx = c;
      addRecent(ch);
      crumb('play');
      openPlayer();
      fillOsd(ch);
      showOsd();
      updatePlayIcon();
      if (Dub && state.hls) Dub.attach({ video: el.video, hls: state.hls, channel: ch, url: state.streamUrl });
      return;
    }
    playIndex(c);
  }

  function openVodModal(series) {
    if (!series) return;
    var eps = series.episodes || [];

    // Detect seasons
    var seasonsFound = [];
    var seasonsSet = {};
    eps.forEach(function (ep) {
      var s = ep.season;
      if (s && !seasonsSet[s]) {
        seasonsSet[s] = true;
        seasonsFound.push(s);
      }
    });
    seasonsFound.sort(function (a, b) { return a - b; });

    var seasonTabsHtml = '';
    if (seasonsFound.length > 1) {
      seasonTabsHtml = '<div class="vod-season-tabs">' +
        '<button class="btn dlg-focus season-tab-btn active" data-season="all">כל העונות (' + eps.length + ')</button>';
      seasonsFound.forEach(function (sNum) {
        var count = eps.filter(function (e) { return e.season == sNum; }).length;
        seasonTabsHtml += '<button class="btn dlg-focus season-tab-btn" data-season="' + sNum + '">עונה ' + sNum + ' (' + count + ')</button>';
      });
      seasonTabsHtml += '</div>';
    }

    var epHtml = eps.map(function (ep, idx) {
      var epThumb = ep.thumb || series.backdrop || series.poster || 'assets/vod/checkout.png';
      var sAttr = ep.season ? ' data-ep-season="' + ep.season + '"' : '';
      var epNumStr = ep.season ? ('עונה ' + ep.season + ' · פרק ' + (ep.episode || (idx + 1))) : ('פרק ' + (idx + 1));

      var saved = 0;
      try { saved = parseFloat(localStorage.getItem('itv_vod_pos_' + ep.id) || '0'); } catch (e) {}
      var progressHtml = saved > 30 ? '<div class="vod-ep-progress"><div class="vod-ep-bar" style="width:' + Math.min(95, Math.round((saved / 2700) * 100)) + '%"></div></div>' : '';

      return '<div class="vod-ep-card dlg-focus" data-epi="' + idx + '"' + sAttr + ' tabindex="0">' +
        '<div class="vod-ep-thumb-wrap">' +
          '<img class="vod-ep-thumb" src="' + esc(epThumb) + '" onerror="this.src=\'assets/vod/checkout.png\'" alt="">' +
          '<div class="vod-ep-play-overlay">▶</div>' +
          progressHtml +
        '</div>' +
        '<div class="vod-ep-info">' +
          '<div class="vod-ep-num">' + epNumStr + '</div>' +
          '<div class="vod-ep-title">' + esc(ep.title) + '</div>' +
          '<div class="vod-ep-meta">' +
            (ep.duration ? '<span class="vod-ep-dur">⏱️ ' + esc(ep.duration) + '</span>' : '') +
            (saved > 30 ? '<span class="vod-ep-resumed">נעצר ב-' + Math.floor(saved / 60) + ' דק\'</span>' : '') +
          '</div>' +
        '</div>' +
        '<div class="vod-ep-actions">' +
          '<button class="btn vod-rec-btn" data-rec-epi="' + idx + '" title="הקלט פרק ל-Drive">💾 הקלטה</button>' +
          '<span class="vod-play-pill">צפייה ▶</span>' +
        '</div>' +
      '</div>';
    }).join('');

    var genresHtml = (series.genres || []).map(function (g) {
      return '<span class="vod-chip">' + esc(g) + '</span>';
    }).join('');

    var heroHtml = '<div class="vod-hero">' +
      '<div class="vod-poster-wrap">' +
        '<img class="vod-poster" src="' + esc(series.poster) + '" onerror="this.src=\'assets/vod/checkout.png\'" alt="">' +
      '</div>' +
      '<div class="vod-hero-details">' +
        '<h1 class="vod-hero-title">' + esc(series.title) + '</h1>' +
        '<div class="vod-hero-badges">' +
          '<span class="vod-chip-rating">⭐ ' + esc(series.rating || '9.0') + '</span>' +
          (series.year ? '<span class="vod-chip">' + esc(series.year) + '</span>' : '') +
          '<span class="vod-chip vod-chip-q">FHD 1080p</span>' +
          '<span class="vod-chip">' + eps.length + ' פרקים</span>' +
          genresHtml +
        '</div>' +
        '<p class="vod-hero-desc">' + esc(series.desc || '') + '</p>' +
        '<div class="vod-hero-actions">' +
          '<button class="btn dlg-focus primary vod-hero-play-btn" data-epi="0">▶ צפה עכשיו (פרק 1)</button>' +
          '<button class="btn dlg-focus vod-rec-all-btn">🔴 הקלט סדרה ל-Google Drive</button>' +
        '</div>' +
      '</div>' +
    '</div>';

    var html = heroHtml + seasonTabsHtml +
      '<div class="vod-episodes-section">' +
        '<div class="vod-episodes-heading">פרקי הסדרה (' + eps.length + ')</div>' +
        '<div class="vod-episodes">' + epHtml + '</div>' +
      '</div>';

    openDialog({
      ico: '🍿',
      title: 'VOD ישראל TV+ · ' + series.title,
      html: html,
      wide: true,
      cls: 'vod-dialog',
      buttons: [{ label: 'סגירה', primary: false }]
    });

    setTimeout(function () {
      var d = $('dialog');
      if (!d) return;

      var epCards = d.querySelectorAll('.vod-ep-card');
      for (var ci = 0; ci < epCards.length; ci++) {
        epCards[ci].addEventListener('click', function (e) {
          if (e.target.closest('.vod-rec-btn')) return;
          var epIdx = parseInt(this.getAttribute('data-epi'), 10);
          var ep = eps[epIdx];
          if (ep) {
            closeDialog();
            playVodEpisode(series, ep);
          }
        });
      }

      var heroPlayBtn = d.querySelector('.vod-hero-play-btn');
      if (heroPlayBtn) {
        heroPlayBtn.addEventListener('click', function () {
          if (eps.length) {
            closeDialog();
            playVodEpisode(series, eps[0]);
          }
        });
      }

      var tabs = d.querySelectorAll('.season-tab-btn');
      for (var ti = 0; ti < tabs.length; ti++) {
        tabs[ti].addEventListener('click', function (e) {
          e.stopPropagation();
          var selS = this.getAttribute('data-season');
          for (var tj = 0; tj < tabs.length; tj++) tabs[tj].classList.remove('active');
          this.classList.add('active');
          for (var k = 0; k < epCards.length; k++) {
            var cSeason = epCards[k].getAttribute('data-ep-season');
            if (selS === 'all' || !selS || cSeason === selS) {
              epCards[k].style.display = '';
            } else {
              epCards[k].style.display = 'none';
            }
          }
        });
      }

      var recBtns = d.querySelectorAll('.vod-rec-btn');
      for (var rk = 0; rk < recBtns.length; rk++) {
        recBtns[rk].addEventListener('click', function (e) {
          e.stopPropagation();
          var epIdx = parseInt(this.getAttribute('data-rec-epi'), 10);
          var ep = eps[epIdx];
          if (ep && window.GDrive) {
            window.GDrive.recordVodEpisode(series, ep, function (pct, txt) {
              toast('🔴 ' + txt + ' (' + series.title + ')');
            });
          }
        });
      }

      var recAllBtn = d.querySelector('.vod-rec-all-btn');
      if (recAllBtn) {
        recAllBtn.addEventListener('click', function (e) {
          e.stopPropagation();
          if (!window.GDrive) return;
          if (eps.length) {
            toast('🔴 מתחיל הקלטת סדרה ל-Google Drive (' + eps.length + ' פרקים)');
            window.GDrive.recordVodEpisode(series, eps[0], function (pct, txt) {
              toast('🔴 פרק 1: ' + txt);
            });
          }
        });
      }
    }, 50);
  }

  function playVodEpisode(series, ep) {
    if (!series || !ep) return;
    toast('🍿 מנגן: ' + series.title + ' - ' + ep.title);
    var vodCh = {
      id: ep.id,
      name: series.title + ': ' + ep.title,
      url: ep.url,
      logo: series.poster,
      badge: 'VOD',
      category: 'סדרות לצפייה ישירה (VOD)',
      isVod: true,
      series: series,
      currentEpisode: ep
    };
    state.playing = vodCh;
    openPlayer();
    fillOsd(vodCh);
    showOsd();
    loadStream(vodCh);
    try {
      var savedPos = parseFloat(localStorage.getItem('itv_vod_pos_' + ep.id) || '0');
      if (savedPos > 15) {
        setTimeout(function () {
          if (el.video && el.video.currentTime < 10) {
            el.video.currentTime = savedPos;
            toast('ממשיך מצפייה קודמת');
          }
        }, 1200);
      }
    } catch (ve) {}
  }


  var sleepTimerId = null;
  var sleepTimerEndTime = 0;

  function setSleepTimer(minutes) {
    if (sleepTimerId) {
      clearTimeout(sleepTimerId);
      sleepTimerId = null;
      sleepTimerEndTime = 0;
    }
    if (!minutes || minutes <= 0) {
      toast('💤 טיימר שינה בוטל');
      renderSettings();
      return;
    }
    sleepTimerEndTime = Date.now() + minutes * 60 * 1000;
    toast('💤 טיימר שינה הופעל: יכבה בעוד ' + minutes + ' דקות');
    renderSettings();
    sleepTimerId = setTimeout(function () {
      sleepTimerId = null;
      sleepTimerEndTime = 0;
      if (el.video) {
        el.video.pause();
        el.video.muted = true;
      }
      closePlayer();
      toast('💤 כיבוי אוטומטי - שינה ערבה!');
      renderSettings();
    }, minutes * 60 * 1000);
  }

  function getSleepTimerRemaining() {
    if (!sleepTimerEndTime) return 0;
    var rem = Math.round((sleepTimerEndTime - Date.now()) / 60000);
    return rem > 0 ? rem : 0;
  }

  function cyclePlaybackSpeed() {
    var v = el.video;
    if (!v) return;
    var speeds = [1, 1.25, 1.5, 2, 0.75];
    var cur = v.playbackRate || 1;
    var idx = speeds.indexOf(cur);
    var next = speeds[(idx + 1) % speeds.length];
    v.playbackRate = next;
    toast('⚡ מהירות ניגון: ' + next + 'x');
  }

  function playNextVodEpisode() {
    var ch = state.playing;
    if (!ch || !ch.isVod || !ch.series || !ch.series.episodes) return;
    var eps = ch.series.episodes;
    var curIdx = -1;
    for (var i = 0; i < eps.length; i++) {
      if (eps[i].id === ch.id || eps[i].url === ch.url) { curIdx = i; break; }
    }
    if (curIdx !== -1 && curIdx + 1 < eps.length) {
      playVodEpisode(ch.series, eps[curIdx + 1]);
    } else {
      toast('זהו הפרק האחרון ברשימה זו');
    }
  }

  function playPrevVodEpisode() {
    var ch = state.playing;
    if (!ch || !ch.isVod || !ch.series || !ch.series.episodes) return;
    var eps = ch.series.episodes;
    var curIdx = -1;
    for (var i = 0; i < eps.length; i++) {
      if (eps[i].id === ch.id || eps[i].url === ch.url) { curIdx = i; break; }
    }
    if (curIdx > 0) {
      playVodEpisode(ch.series, eps[curIdx - 1]);
    } else {
      toast('זהו הפרק הראשון');
    }
  }

  function atvOk() {
    if (atv.zone === 'pill') {
      var t = atv.tabs[atv.tab] && atv.tabs[atv.tab][1];
      if (t === 'profiles') openTvProfiles();
      else if (t === 'search') atvOpenSearch();
      else if (t === 'multiview') openMultiView(atvCurrent() || state.playing);
      else if (t === 'guide') { if (Guide) Guide.open(); }
      else if (t === 'gdrive') openDriveRecordingsDialog();
      else if (t === 'settings') openSettings();
      else if (t === 'kids-exit') askPin(function () { setSetting('kids', false); toast('יצאתם ממצב ילדים'); });
      else atvFocus(atv.row, atv.cols[atv.row] || 0);
      return;
    }
    if (atv.zone === 'kb') {
      atvKbPress(atv.kbIdx || 0);
      return;
    }
    atvWatch();
  }

  function atvKey(code) {
    if (isBack(code)) { atvBack(); return; }
    if (atv.zone === 'pill') {
      if (atvGeo().vert && !atv.search) {
        if (code === 40) atvFocusPill(atv.tab + 1);
        else if (code === 38) atvFocusPill(atv.tab - 1);
        else if (code === 37) atvFocus(atv.row, atv.cols[atv.row] || 0);
        return;
      }
      if (code === 37) atvFocusPill(atv.tab + 1);
      else if (code === 39) atvFocusPill(atv.tab - 1);
      else if (code === 40) { if (atv.search) atvFocusSearch(); else atvFocus(atv.row, atv.cols[atv.row] || 0); }
      return;
    }
    if (atv.zone === 'kb') {
      var kbBtns = $('atv-kb') ? $('atv-kb').querySelectorAll('.kb-key').length : 0;
      var ki = atv.kbIdx || 0;
      if (code === 37) atvFocusKb(ki + 1);
      else if (code === 39) atvFocusKb(ki - 1);
      else if (code === 38) { if (ki >= 11) atvFocusKb(ki - 11); else atvFocusSearch(); }
      else if (code === 40) {
        if (ki + 11 < kbBtns) atvFocusKb(ki + 11);
        else if (atv.rows[0] && atv.rows[0].list.length) { atv.zone = 'rows'; atvKbMark(); atvFocus(0, 0); }
      }
      return;
    }
    var c = atv.cols[atv.row] || 0;
    if (code === 37) atvFocus(atv.row, c + 1);
    else if (code === 39) { if (c === 0 && atvGeo().vert && !atv.search) atvFocusPill(atv.tab); else atvFocus(atv.row, c - 1); }
    else if (code === 38) {
      if (atv.search) atvFocusSearch();
      else if (atv.row > 0) atvFocus(atv.row - 1, atv.cols[atv.row - 1] || 0);
      else atvFocusPill(atv.tab);
    } else if (code === 40) {
      if (atv.row < atv.rows.length - 1) atvFocus(atv.row + 1, atv.cols[atv.row + 1] || 0);
    }
  }

  function atvBack() {
    if (atv.search) { atvCloseSearch(); return; }
    if (atv.zone === 'kb') { atv.zone = 'rows'; atvKbMark(); return; }
    // If a channel is playing in background behind the shelves, BACK returns directly to full-screen video!
    if (state.playing && !state.preview) {
      openPlayer();
      showOsd();
      return;
    }
    if (atv.zone === 'pill') {
      atvFocus(atv.row, atv.cols[atv.row] || 0);
      return;
    }
    // If inside a row (column > 0), step back to column 0 of that row first
    if ((atv.cols[atv.row] || 0) > 0) {
      atvFocus(atv.row, 0);
      return;
    }
    // If on row > 0, step back up one row at a time
    if (atv.row > 0) {
      atvFocus(atv.row - 1, 0);
      return;
    }
    // Already at row 0 col 0: go up to category tab pill
    if (atv.zone === 'rows') {
      atvFocusPill(atv.tab || 0);
      return;
    }
    // At the very top: double-tap to exit application
    var now = Date.now();
    if (now - (state.lastBack || 0) < 2500) exitApp();
    else { state.lastBack = now; toast('לחצו שוב BACK ליציאה'); }
  }

  // ---- search: the field takes the details' place, the results are one shelf + On-Screen Keyboard for LG webOS / Smart TV
  var KB_HE = ['א','ב','ג','ד','ה','ו','ז','ח','ט','י','כ','ל','מ','נ','ס','ע','פ','צ','ק','ר','ש','ת','1','2','3','4','5','6','7','8','9','0','SPACE','BACK','CLEAR','EN','DONE'];
  var KB_EN = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','1','2','3','4','5','6','7','8','9','0','SPACE','BACK','CLEAR','HE','DONE'];
  function renderAtvKb() {
    var kb = $('atv-kb');
    if (!kb) return;
    var keys = atv.kbEn ? KB_EN : KB_HE;
    kb.innerHTML = keys.map(function (k, i) {
      var lbl = k === 'SPACE' ? 'רווח' : k === 'BACK' ? '⌫' : k === 'CLEAR' ? 'נקה' : k === 'EN' ? 'EN' : k === 'HE' ? 'עב' : k === 'DONE' ? '✓ תוצאות' : k;
      var wide = (k === 'SPACE' || k === 'CLEAR' || k === 'DONE') ? ' wide' : '';
      return '<button type="button" class="kb-key' + wide + '" data-kbi="' + i + '" data-kbk="' + k + '">' + lbl + '</button>';
    }).join('');
    kb.classList.remove('hidden');
  }
  function atvKbMark() {
    var kb = $('atv-kb');
    if (!kb) return;
    var btns = kb.querySelectorAll('.kb-key');
    for (var i = 0; i < btns.length; i++) toggleClass(btns[i], 'focused', atv.zone === 'kb' && i === (atv.kbIdx || 0));
  }
  function atvKbPress(idx) {
    var kb = $('atv-kb');
    if (!kb) return;
    var btns = kb.querySelectorAll('.kb-key');
    var b = btns[idx];
    if (!b) return;
    var k = b.getAttribute('data-kbk');
    var q = $('atv-q');
    if (k === 'SPACE') q.value += ' ';
    else if (k === 'BACK') q.value = q.value.slice(0, -1);
    else if (k === 'CLEAR') q.value = '';
    else if (k === 'EN') { atv.kbEn = true; renderAtvKb(); atvKbMark(); return; }
    else if (k === 'HE') { atv.kbEn = false; renderAtvKb(); atvKbMark(); return; }
    else if (k === 'DONE') {
      if (atv.rows[0] && atv.rows[0].list.length) atvFocus(0, 0);
      return;
    } else q.value += k;
    atv.query = q.value;
    if (atv.searchTimer) clearTimeout(atv.searchTimer);
    atv.searchTimer = setTimeout(function () { atvBuild(); atvLayout(); atvMark(); atvKbMark(); }, 120);
  }
  function atvOpenSearch() {
    atv.search = true;
    atv.query = '';
    atv.kbIdx = 0;
    $('atv-q').value = '';
    document.body.classList.add('atv-searching');
    renderAtvKb();
    atvBuild();
    atvLayout();
    atvFocusSearch();
  }
  function atvFocusSearch() {
    atv.zone = 'search';
    state.zone = 'atv';
    atvMark();
    atvKbMark();
    $('atv-search').classList.add('focused');
    try {
      var q = $('atv-q');
      q.removeAttribute('readonly');
      q.focus();
      q.click();
    } catch (e) {}
  }
  function atvFocusKb(i) {
    var kb = $('atv-kb');
    if (!kb) return;
    var btns = kb.querySelectorAll('.kb-key');
    if (!btns.length) return;
    atv.zone = 'kb';
    state.zone = 'atv';
    atv.kbIdx = clamp(i, 0, btns.length - 1);
    try { $('atv-q').blur(); } catch (e) {}
    $('atv-search').classList.remove('focused');
    atvMark();
    atvKbMark();
  }
  function atvCloseSearch() {
    atv.search = false;
    try { $('atv-q').blur(); } catch (e) {}
    $('atv-search').classList.remove('focused');
    if ($('atv-kb')) $('atv-kb').classList.add('hidden');
    document.body.classList.remove('atv-searching');
    atvBuild();
    atvFocus(0, 0);
  }
  function atvSearchKey(code, e) {
    var q = $('atv-q');
    if (code === 40) {
      if (e.preventDefault) e.preventDefault();
      q.blur();
      $('atv-search').classList.remove('focused');
      if ($('atv-kb') && !$('atv-kb').classList.contains('hidden')) atvFocusKb(atv.kbIdx || 0);
      else if (atv.rows[0] && atv.rows[0].list.length) atvFocus(0, 0);
      else atvFocusPill(atv.tab);
    } else if (code === 13) {
      // On LG webOS, Enter on the input triggers the native virtual keyboard; if results exist and input has text, jump to results
      if (q.value && atv.rows[0] && atv.rows[0].list.length) {
        if (e.preventDefault) e.preventDefault();
        q.blur();
        $('atv-search').classList.remove('focused');
        atvFocus(0, 0);
      }
    } else if (code === 38) { if (e.preventDefault) e.preventDefault(); q.blur(); $('atv-search').classList.remove('focused'); atvFocusPill(atv.tab); }
    else if (isBack(code) && (code !== 8 || !q.value)) { if (e.preventDefault) e.preventDefault(); atvCloseSearch(); }
  }

  /** Back to the browse screen (after the player, settings, a dialog). */
  function atvRestore() {
    if (!atv.built || atv.dirty) atvBuild();
    state.zone = 'atv';
    if (atv.zone === 'search') atv.zone = 'rows';
    if (atv.rows[atv.row]) { state.list = atv.rows[atv.row].list; state.gridIdx = atv.cols[atv.row] || 0; }
    atvLayout();
    atvMark();
    atvMeta(atvCurrent());
  }

  function atvAction(action) {
    switch (action) {
      case 'favorites': case 'recent':
        if (atvRowIndex('mine') === -1) { toast('אין עדיין מועדפים – הוסיפו ⭐ מתפריט האפשרויות'); return true; }
        atvFocus(atvRowIndex('mine'), 0); return true;
      case 'search': atvOpenSearch(); return true;
      case 'all': atvFocus(0, 0); return true;
      case 'drawer': atvFocusPill(atv.tab); return true;
    }
    return false;
  }

  function setupAtv() {
    var pill = $('atv-pill'), rows = $('atv-rows');
    pill.addEventListener('click', function (e) {
      var b = e.target.closest('.atv-tab');
      if (!b) return;
      atvFocusPill(+b.getAttribute('data-i'));
      atvOk();
    });
    rows.addEventListener('click', function (e) {
      var it = e.target.closest('.atv-item');
      if (!it) return;
      atvFocus(+it.getAttribute('data-r'), +it.getAttribute('data-c'), true);
      atvWatch();
    });
    // Magic Remote / mouse: the pointer focuses what it is over (the camera stays), the wheel moves it
    rows.addEventListener('mouseover', function (e) {
      var it = e.target.closest('.atv-item');
      if (!it || state.overlay) return;
      var r = +it.getAttribute('data-r'), c = +it.getAttribute('data-c');
      if (r !== atv.row || c !== atv.cols[r] || atv.zone !== 'rows') {
        if (PLATFORM === 'webos') {
          // On LG webOS, avoid restarting background HLS preview on every mouse sweep to keep 60fps UI responsiveness
          atv.row = r; atv.cols[r] = c; atv.zone = 'rows'; state.zone = 'atv';
          state.list = atv.rows[r].list; state.gridIdx = c;
          atvMark();
          if (atv.metaTimer) clearTimeout(atv.metaTimer);
          atv.metaTimer = setTimeout(function () { atvMeta(atv.rows[r].list[c]); }, 90);
        } else {
          atvFocus(r, c, r === atv.row);
        }
      }
    });
    if ($('atv-kb')) {
      $('atv-kb').addEventListener('click', function (e) {
        var b = e.target.closest('.kb-key');
        if (!b) return;
        e.stopPropagation();
        var idx = +b.getAttribute('data-kbi');
        atv.kbIdx = idx;
        atvKbPress(idx);
      });
    }
    if ($('atv-search')) {
      $('atv-search').addEventListener('click', function (e) {
        if (e.target.closest('.kb-key')) return;
        atvFocusSearch();
      });
    }
    var wheelAt = 0;
    $('atv').addEventListener('wheel', function (e) {
      if (state.overlay) return;
      var now = Date.now();
      if (now - wheelAt < 160) return;
      wheelAt = now;
      var dx = e.deltaX, dy = e.deltaY;
      if (Math.abs(dx) > Math.abs(dy) || e.shiftKey) atvKey((dx || dy) > 0 ? 37 : 39);
      else atvKey(dy > 0 ? 40 : 38);
    }, { passive: true });
    $('atv-q').addEventListener('input', function () {
      atv.query = $('atv-q').value;
      if (atv.searchTimer) clearTimeout(atv.searchTimer);
      atv.searchTimer = setTimeout(function () { atvBuild(); atvLayout(); atvMark(); }, 220);
    });
    // the preview shows once the picture really moves
    el.video.addEventListener('playing', function () {
      if (TVUI && state.playing && state.overlay !== 'player') document.body.classList.add('atv-live');
    });
    el.video.addEventListener('timeupdate', function () {
      if (state.playing && state.playing.isVod && el.video.currentTime > 5) {
        try {
          localStorage.setItem('itv_vod_pos_' + state.playing.id, el.video.currentTime);
        } catch (e) {}
      }
    });
  }

  // ================================================================== playback
  function playIndex(i) {
    if (i < 0 || i >= state.list.length) return;
    var ch = state.list[i];
    if (isApp(ch)) { openServiceApp(ch); addRecent(ch); return; }
    if (appOnly(ch)) { showGetApp(ch); return; }
    if (ch.isVod || ch.categoryEn === 'vod') {
      var s = ch.vodData || (window.VOD_DATA && window.VOD_DATA.find(function (v) { return v.id === ch.id || v.title === ch.name.replace(' FHD (VOD)', '').trim(); }));
      if (s) { openVodModal(s); return; }
    }
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
    var behind = TVUI && keepPlaying !== false && !!state.playing;   // TV: the channel goes on behind the shelves
    var finish = function () {
      if (!behind) el.player.classList.add('hidden');
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
    if (TVUI) {
      if (state.playing) document.body.classList.add('atv-live');
      atvRestore();
      return;
    }
    var idx = state.playing ? indexInList(state.playing.id) : -1;
    focusGrid(idx !== -1 ? idx : state.gridIdx);
    if (PHONE && state.tab === 'home' && homeDirty) { var y = el.home.scrollTop; buildHome(); el.home.scrollTop = y; }
    if (state.playing && keepPlaying !== false) toast(PHONE ? 'השידור ממשיך ברקע' : 'השידור ממשיך ברקע · OK על הערוץ לחזרה');
  }

  function stopPlayback() {
    if (state.stallTimer) { clearTimeout(state.stallTimer); state.stallTimer = null; }
    if (state.connectTimer) { clearTimeout(state.connectTimer); state.connectTimer = null; }
    if (state.noVideoTimer) { clearTimeout(state.noVideoTimer); state.noVideoTimer = null; }
    if (state.streamUrl && /ottrast\.com|212\.80\.215\.72/i.test(state.streamUrl)) {
      state.lastOttrastStop = Date.now();
    }
    if (Dub) Dub.detach();
    if (state.hls) { try { state.hls.stopLoad(); state.hls.detachMedia(); state.hls.destroy(); } catch (e) {} state.hls = null; }
    el.video.pause(); el.video.removeAttribute('src'); try { el.video.load(); } catch (e) {}
    el.radioAudio.pause(); el.radioAudio.removeAttribute('src');
    state.playing = null;
    state.preview = null;
    document.body.classList.remove('atv-live');
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

  function updateServerButton() {
    if (!el.obServer) return;
    var n = state.candidates ? state.candidates.length : 0;
    toggleClass(el.obServer, 'hidden', n <= 1);
    if (n > 1) {
      var idx = (state.candIdx || 0) + 1;
      el.obServer.setAttribute('aria-label', 'שרת ' + idx + '/' + n);
      el.obServer.innerHTML = I('server') + '<span>שרת ' + idx + '/' + n + '</span>';
    }
  }

  function switchBackupServer() {
    var ch = state.playing;
    if (!ch) return;
    var cands = state.candidates && state.candidates.length ? state.candidates : candidateUrlsOf(ch);
    state.candidates = cands;
    if (cands.length <= 1) { toast('אין שרתי גיבוי נוספים לערוץ זה'); return; }
    state.candIdx = ((state.candIdx || 0) + 1) % cands.length;
    updateServerButton();
    toast('🔀 עובר לשרת ' + (state.candIdx + 1) + ' מתוך ' + cands.length);
    loadCandidate(ch, cands[state.candIdx]);
  }

  
  function pickLevelByHeight(levels, targetH) {
    if (!levels || !levels.length) return 0;
    var closest = 0, minDiff = 99999;
    for (var i = 0; i < levels.length; i++) {
      var h = levels[i].height || 0;
      var diff = Math.abs(h - targetH);
      if (diff < minDiff) { minDiff = diff; closest = i; }
    }
    return closest;
  }

  function applyQualityLevel(hls, levels) {
    if (!hls || !levels || !levels.length) return;
    if (S.lowBandwidth) {
      var low = pickLevelByHeight(levels, 480);
      if (low === -1) low = 0;
      hls.currentLevel = low;
      hls.loadLevel = low;
      hls.autoLevelCapping = low;
      applyVideoPostProcessing();
      return;
    }
    if (S.quality === 'upscale4k' || S.quality === 'max') {
      var best = 0, bestSc = 0;
      for (var i = 0; i < levels.length; i++) {
        var sc = (levels[i].height || 0) * 1000000 + (levels[i].bitrate || 0);
        if (sc >= bestSc) { bestSc = sc; best = i; }
      }
      hls.currentLevel = best;
      hls.loadLevel = best;
      hls.autoLevelCapping = -1;
    } else if (S.quality === '1080p') {
      hls.currentLevel = pickLevelByHeight(levels, 1080);
    } else if (S.quality === '720p') {
      hls.currentLevel = pickLevelByHeight(levels, 720);
    } else if (S.quality === '480p' || S.quality === 'saver') {
      hls.currentLevel = pickLevelByHeight(levels, 480);
    } else if (S.quality === '360p') {
      hls.currentLevel = pickLevelByHeight(levels, 360);
    } else {
      hls.currentLevel = -1; // auto adaptive
    }
    applyVideoPostProcessing();
  }

  function applyVideoPostProcessing() {
    applyPicture();
  }

  function loadStream(ch) {
    var cu = state.catchupNext && state.catchupNext.ch === ch ? state.catchupNext : null;
    state.catchupNext = null;
    state.catchup = cu;
    if (Guide) Guide.fill();
    if (cu) {
      state.candidates = [cu.url];
      state.candIdx = 0;
      updateServerButton();
      loadResolved(ch, cu.url);
      return;
    }
    state.candidates = candidateUrlsOf(ch);
    state.candIdx = 0;
    state.backupIdx = 0;
    updateServerButton();
    loadCandidate(ch, state.candidates[0] || streamUrl(ch));
  }

    // Catch-up: the broadcasters\' own streams that keep hours of video behind the live edge (measured 31.10.2026).
  // Past programmes play from those streams, from the first segment of the programme.
  var CATCHUP = {
    reshet13: { hours: 24, url: 'https://d198ztbnlup2iq.cloudfront.net/out/v1/2d9050c90fb94df8b78d1d98306a1a65/index.m3u8' },
    reshet_13: { hours: 24, url: 'https://d198ztbnlup2iq.cloudfront.net/out/v1/2d9050c90fb94df8b78d1d98306a1a65/index.m3u8' },
    reshet13_subs_il: { hours: 24, url: 'https://d198ztbnlup2iq.cloudfront.net/out/v1/2d9050c90fb94df8b78d1d98306a1a65/index.m3u8' },
    reshet13_access: { hours: 24, url: 'https://d198ztbnlup2iq.cloudfront.net/out/v1/2d9050c90fb94df8b78d1d98306a1a65/index.m3u8' },
    kan11: { hours: 12, url: 'https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8?dvr=43200000' },
    kan_11: { hours: 12, url: 'https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8?dvr=43200000' },
    kan11_subs_il: { hours: 12, url: 'https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8?dvr=43200000' },
    kan11_subs: { hours: 12, url: 'https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8?dvr=43200000' },
    now14: { hours: 12, url: 'https://r.il.cdn-redge.media/livehls/oil/ch14/live/ch14/live.livx/playlist.m3u8?dvr=43200000' },
    ch14: { hours: 12, url: 'https://r.il.cdn-redge.media/livehls/oil/ch14/live/ch14/live.livx/playlist.m3u8?dvr=43200000' },
    ch14_now_fast: { hours: 12, url: 'https://r.il.cdn-redge.media/livehls/oil/ch14/live/ch14/live.livx/playlist.m3u8?dvr=43200000' },
    now14_subs_il: { hours: 12, url: 'https://r.il.cdn-redge.media/livehls/oil/ch14/live/ch14/live.livx/playlist.m3u8?dvr=43200000' },
    kan_hinuchit: { hours: 12, url: 'https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan_edu/live.livx/playlist.m3u8?dvr=43200000' },
    kan_hinuchit_mc: { hours: 12, url: 'https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan_edu/live.livx/playlist.m3u8?dvr=43200000' },
    makan33: { hours: 12, url: 'https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/makan/live.livx/playlist.m3u8?dvr=43200000' },
    makan_33: { hours: 12, url: 'https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/makan/live.livx/playlist.m3u8?dvr=43200000' },
    i24news_he: { hours: 3, url: 'https://i24newshebrew-cdn.encoders.immergo.tv/master.m3u8' },
    i24news_hebrew_hd: { hours: 3, url: 'https://i24newshebrew-cdn.encoders.immergo.tv/master.m3u8' },
    i24news_en: { hours: 3, url: 'https://i24newsenglish-cdn.encoders.immergo.tv/master.m3u8' },
    i24news_english_hd: { hours: 3, url: 'https://i24newsenglish-cdn.encoders.immergo.tv/master.m3u8' },
    i24_english_world_hd: { hours: 3, url: 'https://i24newsenglish-cdn.encoders.immergo.tv/master.m3u8' },
    i24news_ar: { hours: 0.5, url: 'https://i24newsarabic-cdn.encoders.immergo.tv/master.m3u8' },
    i24news_arabic_hd: { hours: 0.5, url: 'https://i24newsarabic-cdn.encoders.immergo.tv/master.m3u8' },
    i24news_fr: { hours: 3, url: 'https://i24newsfrench-cdn.encoders.immergo.tv/master.m3u8' },
    i24news_fr_il: { hours: 3, url: 'https://i24newsfrench-cdn.encoders.immergo.tv/master.m3u8' }
  };

  // channels that repeat another channel's programme guide
  var CATCHUP_EPG = { ch14_now_fast: 'now14', i24news_english_hd: 'i24news_en', i24news_fr_il: 'i24news_fr', i24news_hebrew_hd: 'i24news_he', i24_english_world_hd: 'i24news_en', kan11_subs: 'kan11', kan11_subs_il: 'kan11', now14_subs_il: 'now14', reshet13_access: 'reshet13', reshet13_subs_il: 'reshet13' };
  function openCatchupModal(ch) {
    if (!ch) ch = state.playing;
    if (!ch) return;
    var c = CATCHUP[ch.id] || CATCHUP[(ch.id || '').toLowerCase()];
    var nowMin = Date.now() / 60000;

    function renderModal(arr) {
      if (!arr || !arr.length) {
        openDialog({
          ico: '⏮️',
          title: 'קאץ\' אפ – ' + ch.name,
          html: '<p style="padding:1rem;text-align:center;color:#94a3b8">לא נמצאו תוכניות בלוח השידורים עבור ערוץ זה כעת.<br>' + (c ? 'ניתן לקפוץ אחורה בשידור עד ' + c.hours + ' שעות.' : '') + '</p>',
          buttons: c ? [
            { label: 'חזרה שעה אחורה', primary: true, act: function () { playCatchup(ch, nowMin - 60, nowMin); closeDialog(); } },
            { label: 'חזרה שעתיים אחורה', act: function () { playCatchup(ch, nowMin - 120, nowMin); closeDialog(); } },
            { label: 'סגירה', act: closeDialog }
          ] : [{ label: 'סגירה', primary: true, act: closeDialog }]
        });
        return;
      }
      var minLimit = nowMin - (c ? c.hours * 60 : 24 * 60);
      var pastProgs = [];
      for (var i = 0; i < arr.length; i++) {
        var p = arr[i];
        var s = p[0], dur = p[1];
        if (s + dur >= minLimit && s <= nowMin) {
          pastProgs.push(p);
        }
      }
      pastProgs.reverse();
      if (!pastProgs.length) {
        toast('אין תוכניות מהשעות האחרונות');
        return;
      }
      var html = '<div style="max-height:55vh;overflow-y:auto;display:flex;flex-direction:column;gap:8px;padding:4px;" class="catchup-list">';
      for (var j = 0; j < pastProgs.length; j++) {
        var prog = pastProgs[j];
        var startD = new Date(prog[0] * 60000);
        var endD = new Date((prog[0] + prog[1]) * 60000);
        var sh = String(startD.getHours()).padStart(2, '0') + ':' + String(startD.getMinutes()).padStart(2, '0');
        var eh = String(endD.getHours()).padStart(2, '0') + ':' + String(endD.getMinutes()).padStart(2, '0');
        var isCurrent = (nowMin >= prog[0] && nowMin <= prog[0] + prog[1]);
        var badge = isCurrent ? '<span style="background:#ef4444;color:#fff;border-radius:4px;padding:2px 6px;font-size:0.75rem;font-weight:bold">משודר עכשיו</span>' : '<span style="background:#334155;color:#94a3b8;border-radius:4px;padding:2px 6px;font-size:0.75rem">שודר היום</span>';
        var pImg = prog[4] ? '<img src="' + esc(prog[4]) + '" style="width:70px;height:42px;object-fit:cover;border-radius:6px;flex-shrink:0" loading="lazy" />' : '';
        html += '<button class="dlg-focus catchup-item" data-idx="' + j + '" style="display:flex;align-items:center;gap:12px;background:rgba(255,255,255,0.06);border:1px solid rgba(255,255,255,0.12);border-radius:8px;padding:10px;text-align:right;color:#fff;cursor:pointer;width:100%">' +
                pImg +
                '<div style="flex:1;min-width:0;text-align:right">' +
                  '<div style="display:flex;align-items:center;justify-content:space-between;gap:8px"><strong style="font-size:0.95rem;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">' + esc(prog[2]) + '</strong>' + badge + '</div>' +
                  '<div style="font-size:0.8rem;color:#94a3b8;margin-top:2px">' + sh + ' - ' + eh + '</div>' +
                '</div>' +
                '<span style="background:rgba(59,130,246,0.3);color:#60a5fa;border:1px solid #3b82f6;border-radius:6px;padding:4px 10px;font-size:0.85rem">צפה</span>' +
              '</button>';
      }
      html += '</div>';

      openDialog({
        ico: '⏮️',
        title: 'קאץ\' אפ (שידור חוזר) – ' + ch.name,
        wide: true,
        html: html,
        buttons: [
          { label: 'סגירה', act: closeDialog }
        ]
      });

      var listNodes = el.dialogBody.querySelectorAll('.catchup-item');
      Array.prototype.forEach.call(listNodes, function (btn) {
        btn.addEventListener('click', function () {
          var idx = parseInt(btn.getAttribute('data-idx'), 10);
          var selectedProg = pastProgs[idx];
          if (selectedProg) {
            closeDialog();
            playCatchup(ch, selectedProg[0], selectedProg[0] + selectedProg[1]);
          }
        });
      });
    }

    var epgChannels = (window.ITV_EPG && window.ITV_EPG.channels) || null;
    if (epgChannels) {
      var k = CATCHUP_EPG[ch.id] || ch.id;
      var arr = epgChannels[k] || epgChannels[k.toLowerCase()] || epgChannels[k.replace('_fast', '')] || epgChannels[k.replace('ch14', 'now14')] || epgChannels[k.replace('kan_hinuchit_mc', 'kan_hinuchit')];
      renderModal(arr);
    } else {
      fetch('epg.json')
        .then(function (r) { return r.json(); })
        .then(function (d) {
          window.ITV_EPG = d;
          var k = CATCHUP_EPG[ch.id] || ch.id;
          var arr = d.channels ? (d.channels[k] || d.channels[k.toLowerCase()] || d.channels[k.replace('_fast', '')] || d.channels[k.replace('ch14', 'now14')] || d.channels[k.replace('kan_hinuchit_mc', 'kan_hinuchit')]) : null;
          renderModal(arr);
        })
        .catch(function () {
          renderModal(null);
        });
    }
  }
  window.openCatchupModal = openCatchupModal;
  function playCatchup(ch, startMin, endMin) {
    var c = CATCHUP[ch.id];
    if (!c) return;
    if (indexInList(ch.id) === -1) { state.query = ''; el.searchInput.value = ''; selectCategory('ALL', true); }
    state.catchupNext = { ch: ch, url: c.url, startMs: startMin * 60000, endMs: endMin * 60000, anchorMs: null };
    state.gridIdx = indexInList(ch.id);
    playIndex(state.gridIdx);
  }
  function catchupSeek(hls, details) {
    var cu = state.catchup, frags = details.fragments || [], hit = null;
    for (var i = 0; i < frags.length; i++) {
      if (frags[i].programDateTime && frags[i].programDateTime <= cu.startMs) hit = frags[i]; else if (hit) break;
    }
    if (!hit && frags[0] && frags[0].programDateTime && frags[0].programDateTime < cu.endMs - 60000) hit = frags[0];
    if (!hit) return false;
    cu.anchorMs = hit.programDateTime - hit.start * 1000;
    var pos = Math.max(hit.start, (cu.startMs - cu.anchorMs) / 1000);
    hls.startLoad(pos);
    el.video.currentTime = pos;
    return true;
  }
  function catchupSeekBy(sec) {
    var v = el.video, s = v.seekable;
    if (!state.catchup || !s || !s.length) return;
    v.currentTime = Math.max(s.start(0), Math.min(s.end(s.length - 1) - 15, v.currentTime + sec));
  }
  function catchupPosMin() {
    var cu = state.catchup;
    return cu && cu.anchorMs ? (cu.anchorMs + el.video.currentTime * 1000) / 60000 : 0;
  }

function loadCandidate(ch, rawUrl) {
    var cleanUrl = normalizeStreamUrl(rawUrl);
    if ((cleanUrl === streamUrl(ch) && ch.auth === 'mako') || /mako-streaming\.akamaized\.net/i.test(cleanUrl)) {
      hideError();
      spinner(true);
      makoUrl(cleanUrl, function (url) { if (state.playing === ch) loadResolved(ch, url); },
        function () { if (state.playing === ch) onFatal(ch, 'לא התקבלה הרשאת צפייה מ-mako'); });
      return;
    }
    loadResolved(ch, cleanUrl);
  }

  function loadResolved(ch, url) {
    state.streamUrl = url;
    hideError();
    state.retries = 0;
    if (state.stallTimer) { clearTimeout(state.stallTimer); state.stallTimer = null; }
    if (state.connectTimer) { clearTimeout(state.connectTimer); state.connectTimer = null; }
    if (Dub) Dub.detach();
    if (state.hls) { try { state.hls.stopLoad(); state.hls.detachMedia(); state.hls.destroy(); } catch (e) {} state.hls = null; }
    spinner(true);
    var isOttrast = /ottrast\.com|212\.80\.215\.72|45\.156\.223/i.test(url);
    var sinceOttrast = Date.now() - (state.lastOttrastStop || 0);
    if (isOttrast && sinceOttrast < 380) {
      state.lastOttrastStop = 0;
      state.connectTimer = setTimeout(function () {
        state.connectTimer = null;
        if (state.playing === ch) loadResolved(ch, url);
      }, 280);
      return;
    }
    if (isOttrast) state.lastOttrastStop = Date.now();
    if (state.candidates && (state.candIdx || 0) + 1 < state.candidates.length) {
      state.stallTimer = setTimeout(function () {
        if (state.playing === ch && state.streamUrl === url) onFatal(ch, 'מעבר לשרת גיבוי');
      }, 3500);
    }

    if (isAudioUrl(url, ch)) {
      el.video.pause(); el.video.removeAttribute('src'); try { el.video.load(); } catch (e) {}
      state.isRadio = true;
      el.radioView.classList.remove('hidden');
      el.radioName.textContent = ch.name;
      setLogo(el.radioLogo, el.radioMono, ch);
      el.osdQuality.textContent = '📻 רדיו';
      if (/\.m3u8(\?|$)/i.test(url) && window.Hls && Hls.isSupported()) {
        var rhls = new Hls({ enableWorker: true, manifestLoadingMaxRetry: 2, levelLoadingMaxRetry: 2, fragLoadingMaxRetry: 3 });
        state.hls = rhls;
        rhls.loadSource(url);
        rhls.attachMedia(el.radioAudio);
        rhls.on(Hls.Events.MANIFEST_PARSED, function () { startPlay(el.radioAudio, ch); });
        rhls.on(Hls.Events.ERROR, function (e, data) {
          if (!data.fatal) return;
          if (data.type === Hls.ErrorTypes.MEDIA_ERROR && state.retries < 2) { state.retries++; rhls.recoverMediaError(); return; }
          onFatal(ch, 'שגיאה בניגון השמע');
        });
        return;
      }
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
      var isSport = /sport|ספורט|one_1|one_2|charlton|eurosport|5plus|5live|5gold|5stars|5max|54k/i.test((ch.category || '') + ' ' + (ch.name || '') + ' ' + (ch.id || ''));
      var isOttrast = /ottrast\.com|212\.80\.215\.72|45\.156\.223/i.test(url);
      var low = (S.latency === 'low' || S.latency === 'realtime' || TVUI || (isSport && !isOttrast)) && !delayed;
      var isStable = (S.latency === 'stable' && (!isSport || isOttrast));
      var is4K = S.quality === 'upscale4k';
      var isVod = !!(ch.isVod || ch.categoryEn === 'vod');
      // catch-up plays far behind the live edge on purpose: nothing may pull it back there
      var inCatchup = !!(state.catchup && state.catchup.ch === ch);
      var hls = new Hls({
        enableWorker: true,
        lowLatencyMode: !isVod && !isOttrast && !inCatchup,
        startPosition: (isSport && !isOttrast) ? -1 : undefined,
        startFragPrefetch: true,
        backBufferLength: fastMode() ? 8 : (isSport ? (isOttrast ? 15 : 4) : (isVod ? 30 : 15)),
        // counted in segments, so the distance from the live edge fits every stream: a fixed 2 s on 6 s segments
        // leaves nothing to play and freezes and jumps every few seconds
        liveSyncDurationCount: (isSport && !isOttrast) ? 2 : 3,
        liveMaxLatencyDurationCount: inCatchup ? Infinity : ((isSport && !isOttrast) ? 5 : 8),
        liveDurationInfinity: !isVod,
        maxLiveSyncPlaybackRate: inCatchup ? 1 : 1.05,
        maxBufferLength: isSport ? (isOttrast ? 25 : 15) : (isVod ? 60 : (is4K ? 24 : 20)),
        maxMaxBufferLength: isSport ? (isOttrast ? 45 : 25) : (isVod ? 120 : (is4K ? 45 : 30)),
        maxBufferSize: (isSport && !isOttrast) ? 8 * 1000 * 1000 : (isVod ? 60 * 1000 * 1000 : 30 * 1000 * 1000),
        manifestLoadingMaxRetry: 6,
        levelLoadingMaxRetry: 6,
        fragLoadingMaxRetry: 6,
        // a whole segment must fit: some streams have 10 s segments on slow servers, and cutting them off restarts them
        manifestLoadingTimeOut: 4500,
        fragLoadingTimeOut: 12000,
        levelLoadingTimeOut: 4500,
        abrEwmaDefaultEstimate: 3500000,
        fragLoadingRetryDelay: 300,
        capLevelToPlayerSize: S.quality === 'auto'
      });
      state.hls = hls;
      hls.loadSource(url);
      hls.attachMedia(v);
      var adjusted = false;
      hls.on(Hls.Events.LEVEL_LOADED, function (e, data) {
        if (data && data.details && (!data.details.fragments || !data.details.fragments.length)) {
          onFatal(ch, 'השידור אינו פעיל בשרת זה');
          return;
        }
        if (state.catchup && state.catchup.ch === ch) {
          if (!adjusted) {
            adjusted = true;
            if (!catchupSeek(hls, data.details)) onFatal(ch, 'התכנית כבר לא זמינה');
          }
          return;
        }
        if (!state.catchup && data && data.details && data.details.live) {
          var edge = data.details.edge || (data.details.fragments && data.details.fragments.length ? data.details.fragments[data.details.fragments.length - 1].start : null);
          var lsp = (isSport && !isOttrast && edge != null) ? Math.max(edge - 2, 0) : (hls.liveSyncPosition || edge);
          if (lsp != null && v.readyState >= 1) {
            // Force jump to zero-latency live edge on initial play or when lag exceeds 4s for sports (excluding 10s-segment ottrast)
            var far = (data.details.targetduration || 6) * (isSport ? 6 : 10);
            if (!isOttrast && (!v.currentTime || lsp - v.currentTime > far)) {
              try { v.currentTime = lsp; } catch (err) {}
            }
          }
        }
        if (!adjusted && Dub) { adjusted = true; Dub.adjustMainHls(hls, data.details, ch); }
      });
      hls.on(Hls.Events.MANIFEST_PARSED, function (e, data) {
        if (state.audioOnly) hls.autoLevelCapping = 0;
        var levels = data.levels || [];
        if (levels.length > 0) {
          applyQualityLevel(hls, levels);
        }
        v.addEventListener('loadedmetadata', function onMeta() {
          v.removeEventListener('loadedmetadata', onMeta);
          if (ch.isVod) {
            try {
              var saved = parseFloat(localStorage.getItem('itv_vod_pos_' + ch.id) || '0');
              if (saved > 10 && saved < (v.duration || 999999) - 30) {
                v.currentTime = saved;
                toast('⏩ ממשיך מהנקודה שעצרת (' + Math.floor(saved / 60) + ' דקות)');
              }
            } catch (e) {}
          } else if (isSport && hls.liveSyncPosition != null) {
            try { v.currentTime = hls.liveSyncPosition; } catch (e) {}
          }
        });
        // Anti-stutter auto-recovery watchdog
        var stallCount = 0;
        v.onwaiting = function () {
          stallCount++;
          if (stallCount >= 2 && hls.currentLevel > 0 && (S.quality === 'auto' || S.quality === 'upscale4k')) {
            hls.currentLevel = Math.max(0, hls.currentLevel - 1);
          }
        };
        v.onplaying = function () {
          setTimeout(function () { stallCount = 0; }, 10000);
          // Video Dimension Watchdog: if audio plays but video decoder fails (videoWidth == 0), auto switch to backup stream
          if (!state.isRadio && !state.audioOnly && !ch.isRadio && !ch.isVod) {
            if (state.noVideoTimer) clearTimeout(state.noVideoTimer);
            state.noVideoTimer = setTimeout(function () {
              if (state.playing === ch && !v.paused && v.videoWidth === 0 && v.videoHeight === 0) {
                var cands = state.candidates && state.candidates.length ? state.candidates : candidateUrlsOf(ch);
                if ((state.candIdx || 0) + 1 < cands.length) {
                  toast('מעבר אוטומטי לשידור יציב עם תמונה');
                  state.candIdx = (state.candIdx || 0) + 1;
                  loadCandidate(ch, cands[state.candIdx]);
                }
              }
            }, 1500);
          }
        };
        v.onended = function () {
          if (ch.isVod && ch.vodSeries && ch.vodEpisodeIndex != null) {
            var series = ch.vodSeries;
            var nextIdx = ch.vodEpisodeIndex + 1;
            if (series.episodes && nextIdx < series.episodes.length) {
              var nextEp = series.episodes[nextIdx];
              toast('🍿 הפרק הסתיים · עובר לפרק הבא: ' + (nextEp.title || ('פרק ' + (nextIdx + 1))));
              setTimeout(function () {
                playVodEpisode(series, nextEp);
              }, 1200);
            }
          }
        };
        if (delayed) spinner(true, 'מכין דיבוב מסונכרן… התמונה מושהית ב-' + Dub.settings.delay + ' שניות');
        startPlay(v, ch);
        if (Dub && state.playing === ch && state.preview !== ch) Dub.attach({ video: v, hls: hls, channel: ch, url: url });
      });
      hls.on(Hls.Events.AUDIO_TRACKS_UPDATED, function (e, data) {
        if (hls.audioTracks && hls.audioTracks.length > 0 && hls.audioTrack === -1) {
          hls.audioTrack = 0;
        }
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

  // The app never mutes a channel you watch: every channel starts with full sound, and only the TV's own mute key
  // silences it. The one silent picture is the TV browse screen's background preview (state.preview), and
  // atvWatch() turns its sound on the moment it becomes the player.
  function startPlay(m, ch) {
    var done = function () {
      if (state.stallTimer) { clearTimeout(state.stallTimer); state.stallTimer = null; }
      spinner(false);
      updatePlayIcon();
    };
    m.muted = state.preview === ch;
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
    if (state.stallTimer) { clearTimeout(state.stallTimer); state.stallTimer = null; }
    if (state.noVideoTimer) { clearTimeout(state.noVideoTimer); state.noVideoTimer = null; }
    if (state.playing !== ch) return;
    if (state.catchup) { toast('הצפייה החוזרת לא זמינה · חוזרים לשידור החי'); loadStream(ch); return; }
    var cands = state.candidates && state.candidates.length ? state.candidates : candidateUrlsOf(ch);
    state.candidates = cands;
    while ((state.candIdx || 0) + 1 < cands.length) {
      state.candIdx = (state.candIdx || 0) + 1;
      var bUrl = cands[state.candIdx];
      if (!bUrl || bUrl === state.streamUrl) continue;
      updateServerButton();
      spinner(true, 'עובר לשרת גיבוי (' + (state.candIdx + 1) + '/' + cands.length + ')...');
      loadCandidate(ch, bUrl);
      return;
    }
    if (state.preview === ch && state.overlay !== 'player') { stopPlayback(); el.player.classList.add('hidden'); return; }
    spinner(false);
    if (Dub) Dub.detach();
    if (state.hls) { try { state.hls.destroy(); } catch (e) {} state.hls = null; }
    var insecure = location.protocol === 'https:' && /^http:/i.test(streamUrl(ch));
    var chLogo = $('perror-ch-logo');
    if (chLogo) {
      chLogo.src = ch.logo || 'icon.png';
      chLogo.onerror = function () { chLogo.src = 'icon.png'; };
    }
    el.perrorTitle.textContent = insecure ? 'הערוץ פועל באפליקציה' : 'סליחה, תקלה זמנית בשידור';
    el.perrorDesc.textContent = insecure
      ? 'הערוץ משדר ב-HTTP, ודפדפנים חוסמים שידור כזה מאתר מאובטח. באפליקציות טלוויזיה, אנדרואיד ומחשב השידור פועל תקין.'
      : 'אנו מתנצלים על אי הנוחות. השידור בערוץ "' + ch.name + '" אינו זמין כעת. צוות הערוץ פועל להחזרת השידור בהקדם.';
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

  function openCastDialog(ch) {
    var target = ch || state.playing;
    var m = media();
    var canRemote = !!(m && m.remote && typeof m.remote.prompt === 'function');
    var canAirPlay = !!(m && typeof m.webkitShowPlaybackTargetPicker === 'function');
    var btns = [];
    if (canRemote) {
      btns.push({
        label: '📡 שידור ישיר (Chromecast / Google TV)',
        primary: true,
        act: function () {
          m.remote.prompt().then(function () {
            toast('📡 מחובר לטלוויזיה');
          }).catch(function () {
            toast('לא נבחר מכשיר Cast או שלא נמצאו טלוויזיות תואמות ברשת');
          });
        }
      });
    }
    if (canAirPlay) {
      btns.push({
        label: '🍏 AirPlay (לטלוויזיות LG / Samsung / Apple TV)',
        primary: !canRemote,
        act: function () {
          try { m.webkitShowPlaybackTargetPicker(); } catch (e) { toast('AirPlay אינו זמין כרגע'); }
        }
      });
    }
    btns.push({ label: '📲 אפליקציות לטלוויזיה (LG / Android TV)', act: function () { showGetApp(); } });
    btns.push({ label: 'סגירה', primary: !canRemote && !canAirPlay });

    var html = '<p>צפייה ב' + (target ? '<b>"' + esc(target.name) + '"</b>' : 'שידור חי') + ' על מסך הטלוויזיה:</p>' +
      '<div class="kv"><b>1. Google Cast / Chromecast</b><span>מכשירי Android TV, Google TV ו-Chromecast באותה רשת Wi-Fi (שידור ישיר באפליקציית האנדרואיד או בדפדפן כרום).</span></div>' +
      '<div class="kv"><b>2. טלוויזיות LG webOS</b><span>תמיכה ב-DLNA וב-LG Screen Share (שיקוף מסך) מאפליקציית האנדרואיד, ב-AirPlay 2 מאייפון/מק, או התקנת אפליקציית ישראל TV+ ישירות על הטלוויזיה.</span></div>' +
      '<div class="kv"><b>3. שיקוף מסך (Miracast / Smart View)</b><span>שיתוף מסך מלא מווילון ההתראות בטלפון ("שדר מסך" / "Screen Share" / "Smart View") – פועל עם כל הערוצים והכתוביות.</span></div>';

    openDialog({
      ico: '📺',
      title: 'שידור לטלוויזיה (Cast & Share)',
      wide: true,
      html: html,
      buttons: btns
    });
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
    if (Guide) Guide.fill();
    el.osdNum.textContent = ch.number;
    el.osdName.textContent = ch.name;
    el.osdCat.textContent = splitCat(ch.category) + (S.showSource ? ' · ' + sourceLine(ch).replace(/^מקור: /, '') : '');
    setLogo(el.osdLogo, el.osdMono, ch);
    applyPicture();
    refreshDubUi();
  }
    // TV / computer OSD and programme guide (tvguide.js); phones keep the player below
  var Guide = null;
  function setupGuide() {
    if (PHONE || Guide) return;
    if (!window.ITVGuide) {
      // live update on an installed build whose boot.js predates tvguide.js
      var gs = document.createElement('script');
      gs.src = (window.ITV_BASE || '') + 'tvguide.js' + (window.ITV_LIVE ? '?v=' + window.ITV_LIVE.code : '');
      gs.onload = function () { if (window.ITVGuide && !Guide) setupGuide(); };
      document.body.appendChild(gs);
      return;
    }
    Guide = window.ITVGuide({
      state: state, S: S, osdEl: el.osd, isBack: isBack, isFav: isFav, toggleFav: toggleFav, toast: toast, showOsd: showOsd, playerMenu: playerMenu, markFocus: markFocus,
      playable: function (ch) { return !isApp(ch) && !appOnly(ch); },
      catchupHours: function (ch) { return ch && CATCHUP[ch.id] ? CATCHUP[ch.id].hours : 0; },
      playCatchup: playCatchup,
      inCatchup: function () { return !!state.catchup; },
      catchupPos: catchupPosMin,
      catchupSeek: catchupSeekBy,
      goLive: function () { if (state.playing) loadStream(state.playing); },
      togglePlay: togglePlay,
      logoUrl: function (ch) { return /^https?:/i.test(ch.logo || '') ? ch.logo : (window.ITV_BASE || '') + (ch.logo || ''); },
      hideOsd: function () {
        if (state.osdTimer) clearTimeout(state.osdTimer);
        Guide.onHide();
        el.osd.classList.add('off');
        el.player.classList.add('cursor-hidden');
      },
      play: function (ch) {
        if (!ch) return;
        if (indexInList(ch.id) === -1) { state.query = ''; el.searchInput.value = ''; selectCategory('ALL', true); }
        state.gridIdx = indexInList(ch.id);
        playIndex(state.gridIdx);
      }
    });
  }

function showOsd() {
    el.osd.classList.remove('off');
    el.player.classList.remove('cursor-hidden');
    if (state.osdTimer) clearTimeout(state.osdTimer);
    state.osdTimer = setTimeout(function () {
      if (state.overlay !== 'player' || state.zone === 'perror') return;
      if (state.zone === 'osd') {
        state.zone = 'video';
        markFocus();
      }
      if (Guide) Guide.onHide();
      el.osd.classList.add('off');
      el.player.classList.add('cursor-hidden');
    }, 5000);
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
      act: function () {
        var i = indexInList(ch.id);
        if (i === -1) { selectCategory('ALL', true); i = indexInList(ch.id); }
        if (i !== -1) playIndex(i);
      } }];
    if (foreign(ch) && !appOnly(ch)) items.push({ ico: '🎙️', label: 'צפייה עם דיבוב לעברית', dot: colourOf('dub'), act: function () { watchDubbed(ch); } });
    if (state.candidates && state.candidates.length > 1) {
      items.push({ ico: '🔀', label: 'שרת שידור', stay: true, val: function () { return 'שרת ' + ((state.candIdx || 0) + 1) + '/' + state.candidates.length; },
        act: function () { switchBackupServer(); } });
    }
    items.push({ ico: '⊞', label: 'פיצול מסך', act: function () { openMultiView(ch); } });
    if (!IS_TV) items.push({ ico: '📺', label: 'שידור לטלוויזיה', act: function () { openCastDialog(ch); } });
    items.push({ ico: isFav(ch) ? '★' : '☆', label: isFav(ch) ? 'הסר מהרשימה שלי' : 'שמור ברשימה שלי', dot: colourOf('fav'), act: function () { toggleFav(ch); } });
    items.push({ ico: 'ℹ️', label: 'מידע על התוכנית והערוץ', dot: colourOf('info'), act: function () { showInfo(ch); } });
    if (!S.kids) items.push({ ico: '🙈', label: 'הסתרת הערוץ', act: function () { hideChannel(ch); } });
    if (appOnly(ch)) items.push({ ico: '📲', label: 'הורדת האפליקציה', act: function () { showGetApp(ch); } });
    items.push({ ico: '⚙️', label: 'הגדרות', act: openSettings });
    openMenu(ch.name, '#' + ch.number + ' · ' + splitCat(ch.category), ch, items);
  }


  function startLiveRecordingDialog(ch) {
    if (!ch) return;
    if (!window.GDrive) return;
    var u = window.GDrive.getUser();
    if (!u || !u.access_token) {
      if (window.GDrive) window.GDrive.signIn(function () { startLiveRecordingDialog(ch); });
      return;
    }

    var html = '<div style="padding:1rem;">' +
      '<p style="font-size:1.1rem;margin-bottom:1rem;">בחרו משך זמן להקלטת השידור החי של <b>' + esc(ch.name) + '</b> ישירות ל-Google Drive שלכם:</p>' +
      '<div style="display:grid;grid-template-columns:1fr 1fr;gap:0.8rem;">' +
      '<button class="btn rec-dur-btn" data-sec="900" style="padding:1rem;font-size:1.1rem;font-weight:700;">⏱️ 15 דקות</button>' +
      '<button class="btn primary rec-dur-btn" data-sec="1800" style="padding:1rem;font-size:1.1rem;font-weight:700;">⏱️ 30 דקות</button>' +
      '<button class="btn rec-dur-btn" data-sec="3600" style="padding:1rem;font-size:1.1rem;font-weight:700;">⏱️ שעה אחת</button>' +
      '<button class="btn rec-dur-btn" data-sec="7200" style="padding:1rem;font-size:1.1rem;font-weight:700;">⏱️ שעתיים</button>' +
      '</div></div>';

    openDialog({
      ico: '🔴',
      title: 'הקלטת שידור חי ל-Google Drive',
      html: html,
      buttons: [{ label: 'ביטול' }]
    });

    setTimeout(function () {
      var d = $('dialog');
      if (!d) return;
      var tabs = d.querySelectorAll('.season-tab-btn');
      var epCards = d.querySelectorAll('.vod-ep-card');
      for (var ti = 0; ti < tabs.length; ti++) {
        tabs[ti].addEventListener('click', function (e) {
          e.stopPropagation();
          var selS = this.getAttribute('data-season');
          for (var tj = 0; tj < tabs.length; tj++) {
            tabs[tj].style.background = 'rgba(255,255,255,0.1)';
            tabs[tj].style.border = '1px solid rgba(255,255,255,0.15)';
          }
          this.style.background = '#3b82f6';
          this.style.border = 'none';
          for (var ci = 0; ci < epCards.length; ci++) {
            var card = epCards[ci];
            var cSeason = card.getAttribute('data-ep-season');
            if (selS === 'all' || !selS || cSeason === selS) {
              card.style.display = 'flex';
            } else {
              card.style.display = 'none';
            }
          }
        });
      }
      var btns = d.querySelectorAll('.rec-dur-btn');
      for (var b = 0; b < btns.length; b++) {
        btns[b].addEventListener('click', function () {
          var sec = parseInt(this.getAttribute('data-sec'), 10) || 1800;
          closeDialog();
          window.GDrive.recordLiveStream(ch, sec);
        });
      }
    }, 50);
  }


  function gatherCloudData() {
    var vodMap = {};
    try {
      for (var i = 0; i < localStorage.length; i++) {
        var k = localStorage.key(i);
        if (k && k.indexOf('itv_vod_pos_') === 0) {
          vodMap[k] = localStorage.getItem(k);
        }
      }
    } catch (e) {}

    return {
      profiles: tvProfiles || [],
      favorites: state.favorites || [],
      recents: state.recents || [],
      settings: S || {},
      vodProgress: vodMap,
      updatedAt: parseInt(localStorage.getItem('itv_local_changed') || '0', 10)
    };
  }

  function applyCloudData(d) {
    if (!d) return;
    try {
      localStorage.setItem('itv_local_changed', String(d.updatedAt || 0));
      if (Array.isArray(d.favorites)) {
        state.favorites = d.favorites;
        lsSet('israel_tv_favorites', state.favorites);
      }
      if (Array.isArray(d.recents)) {
        state.recents = d.recents;
        lsSet('israel_tv_recents', state.recents);
      }
      if (Array.isArray(d.profiles) && d.profiles.length) {
        tvProfiles = d.profiles;
        saveTvProfiles();
      }
      if (d.settings && typeof d.settings === 'object') {
        for (var k in d.settings) {
          if (k !== 'cols' && k !== 'uiScale') S[k] = d.settings[k];
        }
        saveSettings();
      }
      if (d.vodProgress && typeof d.vodProgress === 'object') {
        for (var vk in d.vodProgress) {
          try { localStorage.setItem(vk, d.vodProgress[vk]); } catch (ve) {}
        }
      }
      buildCategories();
      filterList();
      if (TVUI && typeof atvBuild === 'function') atvBuild();
    } catch (err) {
      console.error('applyCloudData error:', err);
    }
  }

  function triggerCloudSync(notify) {
    if (!window.GDrive || !window.GDrive.isSignedIn()) {
      if (notify) toast('⚠️ חשבון Google אינו מחובר. התחברו בהגדרות כדי לסנכרן.');
      return;
    }
    if (notify) toast('🔄 מסנכרן מול Google Drive...');
    var local = gatherCloudData();
    window.GDrive.syncData(local, function (appliedCloud) {
      if (appliedCloud) {
        applyCloudData(appliedCloud);
        if (notify) toast('✅ הגדרות ומועדפים עודכנו מהענן!');
      } else {
        if (notify) toast('✅ הנתונים וההגדרות נשמרו בענן!');
      }
    }, function (err) {
      if (notify) toast('⚠️ שגיאה בסנכרון: ' + (err.message || ''));
    });
  }

  var syncDebounceTimer = null;
  function scheduleCloudSync() {
    try { localStorage.setItem('itv_local_changed', String(Date.now())); } catch (e) {}
    if (!window.GDrive || !window.GDrive.isSignedIn()) return;
    clearTimeout(syncDebounceTimer);
    syncDebounceTimer = setTimeout(function () {
      triggerCloudSync(false);
    }, 4000);
  }

  function openDriveRecordingsDialog() {
    if (!window.GDrive) return;
    var u = window.GDrive.getUser();
    if (!u || !u.access_token) {
      if (window.GDrive) window.GDrive.signIn(function () { openDriveRecordingsDialog(); });
      return;
    }

    toast('🔍 טוען הקלטות מ-Google Drive...');
    window.GDrive.listRecordings(function (err, files) {
      if (err) {
        toast('⚠️ שגיאה בטעינת הקלטות: ' + (err.message || ''));
        return;
      }
      if (!files || !files.length) {
        openDialog({
          ico: '📁',
          title: 'ההקלטות שלי ב-Google Drive',
          html: '<div style="text-align:center;padding:2rem 1rem;">' +
            '<div style="font-size:3rem;margin-bottom:1rem;">🍿</div>' +
            '<p style="font-size:1.2rem;font-weight:600;margin:0 0 0.5rem;">אין עדיין הקלטות בתיקייה</p>' +
            '<p style="font-size:0.95rem;color:rgba(255,255,255,0.7);margin:0;">פתחו כל סדרה ב-VOD או שידור חי ולחצו על <b>"💾 הקלט ל-Drive"</b> כדי לשמור אותה ישירות לכאן!</p>' +
            '</div>',
          buttons: [{ label: 'סגירה' }]
        });
        return;
      }

      var itemsHtml = files.map(function (f, idx) {
        var sizeMb = f.size ? (parseInt(f.size, 10) / (1024 * 1024)).toFixed(1) + ' MB' : '';
        var date = f.createdTime ? new Date(f.createdTime).toLocaleDateString('he-IL') : '';
        return '<div class="rec-card" style="display:flex;align-items:center;justify-content:space-between;padding:0.9rem 1.2rem;background:rgba(255,255,255,0.06);border:1px solid rgba(255,255,255,0.12);border-radius:12px;margin-bottom:0.6rem;">' +
          '<div style="flex:1;min-width:0;margin-left:1rem;">' +
          '<div style="font-size:1.1rem;font-weight:700;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">' + esc(f.name) + '</div>' +
          '<div style="font-size:0.85rem;color:rgba(255,255,255,0.6);margin-top:0.2rem;">' + date + (sizeMb ? ' • ' + sizeMb : '') + '</div>' +
          '</div>' +
          '<div style="display:flex;gap:0.6rem;flex:none;">' +
          (f.webViewLink ? '<a href="' + esc(f.webViewLink) + '" target="_blank" class="btn" style="padding:0.4rem 0.8rem;font-size:0.9rem;">🔗 פתח ב-Drive</a>' : '') +
          '</div></div>';
      }).join('');

      openDialog({
        ico: '📁',
        title: 'ההקלטות שלי ב-Google Drive (' + files.length + ')',
        html: '<div style="max-height:55vh;overflow-y:auto;padding:0.5rem;">' + itemsHtml + '</div>',
        buttons: [{ label: 'סגירה' }]
      });
    });
  }

  function playerMenu() {
    var ch = state.playing;
    if (!ch) return;
    var items = [];
    if (ch.isVod) {
      items.push({ ico: '⏩', label: 'דילוג 90 שניות קדימה', act: function () {
        var v = el.video, end = isFinite(v.duration) ? v.duration - 5 : Infinity;
        v.currentTime = Math.min(v.currentTime + 90, end);
        toast('דילגת 90 שניות קדימה');
      } });
      if (ch.series) {
        items.push({ ico: '📋', label: 'פרקים בסדרה (' + ch.series.title + ')', act: function () { openVodModal(ch.series); } });
        items.push({ ico: '💾', label: 'הקלט פרק זה ל-Google Drive', act: function () {
          var ep = ch.currentEpisode || ch.vodEp;
          if (window.GDrive && ch.series && ep) {
            window.GDrive.recordVodEpisode(ch.series, ep);
          } else {
            toast('🔴 שומר פרק ל-Google Drive...');
          }
        } });
      }
      items.push({ ico: '⏭️', label: 'פרק הבא', act: playNextVodEpisode });
      items.push({ ico: '⏮️', label: 'פרק קודם', act: playPrevVodEpisode });
      items.push({ ico: '⚡', label: 'מהירות ניגון', stay: true, val: function () { return (el.video.playbackRate || 1) + 'x'; }, act: cyclePlaybackSpeed });
    } else {
      items.push({ ico: '🔴', label: 'הקלט שידור ל-Google Drive', act: function () { startLiveRecordingDialog(ch); } });
    }
    if (state.catchup) {
      items.push({ ico: '🔴', label: 'חזרה לשידור חי', act: function () { if (state.playing) loadStream(state.playing); } });
    }
    if (CATCHUP[ch.id] || CATCHUP[(ch.id || '').toLowerCase()]) {
      var nowMin = Date.now() / 60000;
      var epgKey = (typeof CATCHUP_EPG !== 'undefined' && CATCHUP_EPG[ch.id]) || ch.id;
      var epgList = (window.ITV_EPG && window.ITV_EPG.channels && window.ITV_EPG.channels[epgKey]) || [];
      var curProg = null;
      for (var pi = 0; pi < epgList.length; pi++) {
        if (epgList[pi][0] <= nowMin && nowMin < epgList[pi][0] + epgList[pi][1]) {
          curProg = epgList[pi];
          break;
        }
      }
      if (curProg && (nowMin - curProg[0] > 1)) {
        items.push({
          ico: '⏮️',
          label: 'התחל תכנית מחדש: ' + curProg[2],
          act: function () { playCatchup(ch, curProg[0], curProg[0] + curProg[1]); }
        });
      }
      items.push({ ico: '⏮️', label: "קאץ' אפ ושידורים קודמים", act: function () { openCatchupModal(ch); } });
    }
    items.push({
      ico: '📶',
      label: 'מצב רשת איטית (5Mbps ומטה)',
      stay: true,
      val: function () { return S.lowBandwidth ? 'פועל (480p)' : 'כבוי (איכות מלאה)'; },
      act: function () {
        setSetting('lowBandwidth', !S.lowBandwidth);
        if (state.hls && state.hls.levels) applyQualityLevel(state.hls, state.hls.levels);
        toast(S.lowBandwidth ? '📶 מצב רשת איטית מופעל · מותאם לקליטה חלשה עד 5Mbps' : '📶 מצב איכות רגילה מופעל');
      }
    });
    if (!state.isRadio) {
      items.push({
        ico: '🎧',
        label: state.audioOnly ? 'חזרה לתמונה' : 'מצב האזנה (בלי תמונה, איכות נמוכה)',
        act: function () { setAudioOnly(!state.audioOnly); }
      });
    }
    if (Guide) items.push({ ico: '▦', label: 'לוח שידורים', act: Guide.open });
    if (state.candidates && state.candidates.length > 1) {
      items.push({ ico: '🔀', label: 'שרת שידור', stay: true, val: function () { return 'שרת ' + ((state.candIdx || 0) + 1) + '/' + state.candidates.length; },
        act: function () { switchBackupServer(); } });
    }
    items.push({ ico: '⊞', label: 'פיצול מסך', act: function () { openMultiView(ch); } });
    if (!state.isRadio) {
      items.push({
        ico: '✨',
        label: 'מצב אפסקייל והשבחת תמונה',
        stay: true,
        val: function () { return label(UPSCALE_MODES, S.upscaleMode); },
        act: function (st) {
          var nextM = cycle(UPSCALE_MODES, S.upscaleMode, st);
          setSetting('upscaleMode', nextM);
          toast('✨ אפסקייל: ' + label(UPSCALE_MODES, nextM));
        }
      });
      items.push({ ico: '🎨', label: 'מצב צבע ותמונה', stay: true, dot: colourOf('picture'), val: function () { return label(PICS, S.pic); },
        act: function (st) { setSetting('pic', cycle(PICS, S.pic, st)); } });
      items.push({ ico: '🖼️', label: 'גודל תמונה', stay: true, dot: colourOf('aspect'), val: function () { return label(ASPECTS, S.aspect); },
        act: function (st) { setSetting('aspect', cycle(ASPECTS, S.aspect, st)); } });
      items.push({ ico: '📶', label: 'איכות', stay: true, val: function () { return label(QUALITIES, S.quality); },
        act: function (st) { setSetting('quality', cycle(QUALITIES, S.quality, st)); if (state.playing) loadStream(state.playing); } });
    }
    if (foreign(ch)) items.push({ ico: '🎙️', label: 'דיבוב לעברית', stay: true, dot: colourOf('dub'), val: function () { return Dub.settings.enabled ? 'פועל' : 'כבוי'; },
      act: function () { toggleDub(); } });
    if (!IS_TV) items.push({ ico: '📺', label: 'שידור לטלוויזיה', act: function () { openCastDialog(ch); } });
    items.push({ ico: isFav(ch) ? '★' : '☆', label: isFav(ch) ? 'הסר מהרשימה שלי' : 'שמור ברשימה שלי', dot: colourOf('fav'), act: function () { toggleFav(ch); } });
    items.push({ ico: '☰', label: 'רשימת ערוצים', dot: colourOf('drawer'), act: openDrawer });
    items.push({ ico: 'ℹ️', label: 'מידע על התוכנית והערוץ', dot: colourOf('info'), act: function () { showInfo(ch); } });
    items.push({ ico: '⟳', label: 'רענן שידור', act: function () { loadStream(ch); } });
    items.push({ ico: '⚙️', label: 'הגדרות', act: openSettings });
    openMenu(ch.name, '#' + ch.number + ' · ' + el.osdQuality.textContent, ch, items);
  }

  // ================================================================== dialogs
  /** opts: { ico, title, html, buttons: [{ label, primary, act, keep }], onBack } */
  // gdrive.js is a separate file and shows its dialogs through these
  window.openDialog = openDialog;
  window.closeDialog = closeDialog;
  window.toast = toast;
  window.loadQrLib = loadQrLib;

  /** What changed in each version, newest first: version.json carries the recent CHANGELOG sections. */
  function openChangelog() {
    var show = function (list, offline) {
      var html = list.map(function (v) {
        return '<section class="cl-ver"><h3>' + esc(v.version) + (v.version === (LIVE ? LIVE.version : BUILD.version) ? '<small>הגרסה שלך</small>' : '') + '</h3><ul>' +
          (v.notes || []).map(function (t) { return '<li>' + esc(t) + '</li>'; }).join('') + '</ul></section>';
      }).join('');
      openDialog({ title: 'לוג שינויים', cls: 'changelog', wide: true, scroll: true,
        html: (offline ? '<p>אין חיבור לשרת, לכן מוצגת רק הגרסה הנוכחית.</p>' : '') + html });
      el.dialogBox.scrollTop = 0;
    };
    var fallback = function () { show([{ version: BUILD.version, notes: BUILD.notes || [] }], true); };
    xhrJson(REMOTE + 'version.json?t=' + Date.now(), 10000, function (data) {
      if (data && data.history && data.history.length) show(data.history);
      else if (data && data.notes) show([{ version: data.version, notes: data.notes }]);
      else fallback();
    }, fallback);
  }
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

  window.onItvEpgReady = function () {
    if (!state.overlay) {
      if (TVUI) atvMeta(atvCurrent());
      else drawHero(current());
    }
  };
  function ensureEpgData(cb) {
    if (window.ITV_EPG && window.ITV_EPG.channels) {
      if (cb) cb(window.ITV_EPG.channels);
      return;
    }
    var urls = /^https?:/.test(location.protocol) ? ['epg.json', 'https://dor2500.github.io/israel-tv-plus/epg.json'] : ['https://dor2500.github.io/israel-tv-plus/epg.json'];
    var i = 0;
    (function next() {
      if (i >= urls.length) {
        if (cb) cb(null);
        return;
      }
      xhrJson(urls[i++], 12000, function (data) {
        if (data && data.channels) {
          window.ITV_EPG = data;
          window.ITV_EPG_CHANNELS = data.channels;
          if (typeof window.onItvEpgReady === 'function') window.onItvEpgReady();
          if (cb) cb(data.channels);
        } else {
          next();
        }
      }, next);
    })();
  }

  function formatEpgTime(m) {
    var d = new Date(m * 60000);
    return ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
  }

  function getEpgProgramInfo(ch) {
    if (!ch) return null;
    var epgChannels = (window.ITV_EPG && window.ITV_EPG.channels) || window.ITV_EPG_CHANNELS || null;
    if (!epgChannels) return null;
    var k = (typeof CATCHUP_EPG !== 'undefined' && CATCHUP_EPG[ch.id]) || ch.id;
    var list = epgChannels[k] || epgChannels[k.toLowerCase()] || epgChannels[k.replace('_fast', '')] || epgChannels[k.replace('_hd', '')] || [];
    if (!list.length) {
      for (var ek in epgChannels) {
        if (ek.toLowerCase() === (ch.id || '').toLowerCase() || (ch.iptv && ek.toLowerCase() === ch.iptv.toLowerCase())) {
          list = epgChannels[ek];
          break;
        }
      }
    }
    if (!list || !list.length) return null;
    var nowMin = Math.floor(Date.now() / 60000);
    for (var i = 0; i < list.length; i++) {
      var p = list[i];
      if (p[0] <= nowMin && nowMin < p[0] + p[1]) {
        return {
          cur: p,
          next: (i + 1 < list.length) ? list[i + 1] : null,
          nowMin: nowMin,
          isLive: true
        };
      }
    }
    for (var j = 0; j < list.length; j++) {
      if (list[j][0] >= nowMin) {
        return {
          cur: list[j],
          next: (j + 1 < list.length) ? list[j + 1] : null,
          nowMin: nowMin,
          isLive: false
        };
      }
    }
    return null;
  }

  function showInfo(ch) {
    if (!ch) return;
    if (!window.ITV_EPG || !window.ITV_EPG.channels) {
      ensureEpgData(function () {
        renderShowInfoModal(ch);
      });
    } else {
      renderShowInfoModal(ch);
    }
  }

  function renderShowInfoModal(ch) {
    var pi = getEpgProgramInfo(ch);
    var cardHtml = '';
    var buttons = [];
    var s = sourceOf(ch);

    if (pi && pi.cur) {
      var cur = pi.cur;
      var sMin = cur[0], dur = cur[1], eMin = sMin + dur;
      var title = cur[2] || '', desc = cur[3] || '', poster = cur[4] || '';
      var sStr = formatEpgTime(sMin), eStr = formatEpgTime(eMin);
      var elapsed = Math.max(0, pi.nowMin - sMin);
      var pct = Math.min(100, Math.max(0, Math.round((elapsed / Math.max(dur, 1)) * 100)));
      var rem = Math.max(0, eMin - pi.nowMin);

      var posterImg = poster ? '<img class="info-prog-poster" src="' + esc(poster) + '" alt="" onerror="this.style.display=\'none\'">' : '';
      var badgeHtml = pi.isLive ? '<span class="info-prog-badge">● שידור חי כעת</span>' : '<span class="info-prog-badge">משודר בקרוב</span>';
      var timeHtml = '<div class="info-prog-time">' + sStr + ' – ' + eStr + ' (נותרו ' + rem + ' דקות)</div>';
      var barHtml = '<div class="info-prog-progress"><div class="info-prog-progress-fill" style="width:' + pct + '%"></div></div>';
      var descHtml = desc ? '<div class="info-prog-desc"><b>תקציר והסבר על התוכנית:</b><br>' + esc(desc) + '</div>' : '';
      var nextHtml = '';
      if (pi.next) {
        var nStr = formatEpgTime(pi.next[0]);
        nextHtml = '<div class="info-prog-next">התוכנית הבאה: <b>' + esc(pi.next[2]) + '</b> בשעה ' + nStr + ' (' + pi.next[1] + ' דק\')</div>';
      }

      cardHtml = '<div class="info-prog-card">' +
        '<div class="info-prog-header">' +
          posterImg +
          '<div class="info-prog-meta">' +
            badgeHtml +
            '<div class="info-prog-title">' + esc(title) + '</div>' +
            timeHtml +
          '</div>' +
        '</div>' +
        barHtml +
        descHtml +
        nextHtml +
      '</div>';

      if (CATCHUP[ch.id] || CATCHUP[(ch.id || '').toLowerCase()]) {
        buttons.push({
          label: '⏮️ צפה מראשית התוכנית',
          act: function () {
            closeDialog();
            playCatchup(ch, sMin, eMin);
          }
        });
      }
    } else if (ch.desc || ch.isVod) {
      cardHtml = '<div class="info-prog-card">' +
        '<div class="info-prog-header">' +
          (ch.logo ? '<img class="info-prog-poster" src="' + esc(ch.logo) + '" alt="" onerror="this.style.display=\'none\'">' : '') +
          '<div class="info-prog-meta">' +
            '<span class="info-prog-badge vod">VOD וסרטים</span>' +
            '<div class="info-prog-title">' + esc(ch.name) + '</div>' +
            '<div class="info-prog-time">' + esc(splitCat(ch.category)) + (ch.badge ? ' · ' + esc(ch.badge) : '') + '</div>' +
          '</div>' +
        '</div>' +
        (ch.desc ? '<div class="info-prog-desc"><b>תקציר והסבר:</b><br>' + esc(ch.desc) + '</div>' : '') +
      '</div>';
    }

    if (CATCHUP[ch.id] || CATCHUP[(ch.id || '').toLowerCase()]) {
      buttons.push({
        label: '📅 לוח שידורים מלא וקאצ\' אפ',
        act: function () {
          closeDialog();
          if (typeof openCatchupModal === 'function') openCatchupModal(ch);
          else if (Guide) Guide.open();
        }
      });
    } else if (Guide) {
      buttons.push({
        label: '📅 לוח שידורים',
        act: function () {
          closeDialog();
          Guide.open();
        }
      });
    }
    buttons.push({ label: 'סגירה', primary: true });

    var rows = [
      ['מספר ערוץ', esc(ch.number)],
      ['קטגוריה', esc(splitCat(ch.category))],
      ['שפה', esc(ch.lang === 'he' ? 'עברית' : Dub ? Dub.langName(ch.lang) : ch.lang)],
      ['איכות שידור', esc(ch.badge || 'FHD')]
    ];
    if (isApp(ch)) {
      rows.push(['סוג', 'שירות מנויים רשמי – נפתח באפליקציה שלו']);
      rows.push(['אתר', '<code>' + esc(ch.site || ch.url) + '</code>']);
    } else {
      rows.push(['מקור השידור', esc(s.by)]);
      if (s.kind) rows.push(['סוג המקור', esc(KIND_LABEL[s.kind] || s.kind)]);
      rows.push(['שרת', '<code>' + esc(s.host) + '</code>']);
      rows.push(['כתובת HLS', '<code>' + esc(ch.url) + '</code>']);
      if (ch.webUrl) rows.push(['באתר (https)', '<code>' + esc(ch.webUrl) + '</code>']);
      if (s.web) rows.push(['בדפדפן', esc(s.web)]);
      if (foreign(ch)) rows.push(['דיבוב', '🎙️ אפשר לדבב לעברית']);
    }

    var techHtml = '<details class="info-tech-details">' +
      '<summary>פרטי ערוץ ושרת שידור טכני</summary>' +
      '<div class="epg-tech-rows" style="margin-top:0.8rem">' +
        rows.map(function (r) {
          return '<div class="kv"><b>' + r[0] + '</b><span>' + r[1] + '</span></div>';
        }).join('') +
        '<p style="margin-top:0.8rem;font-size:1.2rem">כל המקורות: ' + esc(REPO.replace('https://', '')) + '/blob/master/SOURCES.md</p>' +
      '</div>' +
    '</details>';

    openDialog({
      ico: 'ℹ️',
      title: ch.name + ' (ערוץ ' + ch.number + ')',
      wide: true,
      html: cardHtml + techHtml,
      buttons: buttons
    });
  }


  function showPlexModal() {
    var m3u = 'https://dor2500.github.io/israel-tv-plus/plex.m3u';
    var xmltv = 'https://dor2500.github.io/israel-tv-plus/epg.xml';
    var html = '<div style="direction:rtl;text-align:right;font-size:15px;line-height:1.6;color:var(--t-sec);">' +
      '<p><b style="color:var(--t-pri);font-size:16px;">צפייה בכל 269 הערוצים דרך אפליקציית Plex בטלוויזיית LG, סמסונג או סטרימר:</b></p>' +
      '<p>1. פתח את הגדרות שרת ה-Plex שלך במחשב / NAS ← עבור ל-<b>Live TV & DVR</b>.</p>' +
      '<p>2. לחץ על <b>Set up a Plex Tuner</b> ובחר <b>M3U Tuner</b>.</p>' +
      '<div style="background:rgba(255,255,255,0.07);padding:10px 14px;border-radius:10px;margin:8px 0;word-break:break-all;user-select:all;font-family:monospace;font-size:13px;color:var(--t-pri);">' +
      esc(m3u) + '</div>' +
      '<p>3. בלשונית מדריך השידורים (XMLTV Guide), הדבק את הכתובת:</p>' +
      '<div style="background:rgba(255,255,255,0.07);padding:10px 14px;border-radius:10px;margin:8px 0;word-break:break-all;user-select:all;font-family:monospace;font-size:13px;color:var(--t-pri);">' +
      esc(xmltv) + '</div>' +
      '<p>4. פתח את אפליקציית Plex בטלוויזיית ה-LG שלך (מחנות LG Content Store) – כל הערוצים, לוח השידורים והפוסטרים יופיעו אוטומטית!</p>' +
      '</div>';
    openDialog({
      ico: '📡',
      title: 'חיבור ל-Plex ול-DLNA',
      html: html,
      buttons: [
        { label: 'הבנתי, תודה', primary: true }
      ]
    });
  }

  function showGetApp(ch) {
    var items = [['🤖', 'אנדרואיד', 'APK לטלפון וטאבלט', 'IsraelTVPlus-android.apk'], ['📺', 'Android TV / Google TV', 'Xiaomi, Sony, TCL, Fire TV', 'IsraelTVPlus-androidtv.apk'],
      ['🪟', 'Windows', 'התקנה', 'IsraelTVPlus-windows-setup.exe'], ['🖥️', 'LG webOS', 'IPK · Developer Mode', 'com.israeltv.plus_all.ipk'],
      ['🔷', 'Samsung Tizen', 'פרויקט לחתימה', 'IsraelTVPlus-tizen-project.zip'], ['🍏', 'Mac', 'Apple Silicon', 'IsraelTVPlus-macos-arm64.zip'],
      ['🐧', 'Linux', 'x64 · ZIP', 'IsraelTVPlus-linux-x64.zip'], ['🎬', 'Stremio', 'הרחבה (Addon)', 'stremio'], ['📋', 'נגני IPTV', 'פלייליסט M3U', 'm3u']];
    // this device's own download first: a computer must not open on the Android APK
    var mine = /Android/i.test(UA) ? 'IsraelTVPlus-android.apk' : /Windows/i.test(UA) ? 'IsraelTVPlus-windows-setup.exe'
      : /Macintosh|Mac OS/i.test(UA) ? 'IsraelTVPlus-macos-arm64.zip' : /Linux/i.test(UA) ? 'IsraelTVPlus-linux-x64.zip' : '';
    items.sort(function (x, y) { return (y[3] === mine) - (x[3] === mine); });
    var html = '<p>' + (ch ? '"' + esc(ch.name) + '" משדר ב-HTTP, ודפדפנים חוסמים שידור כזה מאתר מאובטח. באפליקציות הוא פועל – וגם כל שאר הערוצים.'
      : 'באפליקציות פועלים כל הערוצים (גם מה שהדפדפן חוסם), עם שלט רחוק, מועדפים, דיבוב ועדכונים אוטומטיים.') + '</p>' +
      '<div class="getapp">' + items.map(function (it) {
        var href = it[3] === 'stremio' ? REMOTE + 'stremio/' : it[3] === 'm3u' ? REMOTE + 'playlist.m3u' : RELEASES + '/download/' + it[3];
        return '<a class="btn dlg-focus' + (it[3] === mine ? ' primary' : '') + '" target="_blank" rel="noopener" href="' + href + '">' + it[0] + ' ' + it[1] + '<small>' + (it[3] === mine ? 'למכשיר הזה · ' : '') + it[2] + '</small></a>';
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
    if (i === -1) { selectCategory('ALL', true); i = indexInList(ch.id); }
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

  function colorDotBtn(c, act) {
    return S[act] && S[act] !== 'none'
      ? '<button type="button" class="hint hint-btn" data-color-act="' + esc(S[act]) + '"><span class="cdot ' + c + '"></span>' + esc(SHORT[S[act]]) + '</button>'
      : '';
  }
  function hintHtml(inPlayer) {
    var p = remoteProfile();
    var h = function (key, text, cls) { return '<span class="hint"><span class="key' + (cls ? ' ' + cls : '') + '">' + key + '</span>' + text + '</span>'; };
    var colours = colorDotBtn('red', 'keyRed') + colorDotBtn('green', 'keyGreen') + colorDotBtn('yellow', 'keyYellow') + colorDotBtn('blue', 'keyBlue');
    var longOk = h('OK ארוך', SHORT[S.longOk] || 'אפשרויות');
    if (p === 'touch') return '';
    if (p === 'keyboard') {
      return (inPlayer
        ? h('▲▼', 'ערוץ') + h('◄', 'רשימת ערוצים') + h('Enter', 'אפשרויות') + h('F', 'מסך מלא') + h('D', 'דיבוב') + h('Esc', 'למדריך')
        : h('Enter', 'צפייה') + h('קליק ימני / O', 'אפשרויות') + h('/', 'חיפוש') + h('S', 'הגדרות') + h('F', 'מסך מלא') + h('0-9', 'מספר ערוץ')) + colours;
    }
    var nav = inPlayer ? h(S.upNext ? '▲ הבא ▼ קודם' : '▲▼', 'החלפת ערוץ') + h('◄', 'רשימת ערוצים') + h('OK', 'אפשרויות')
      : h('OK', 'צפייה') + longOk;
    var base = nav + h('BACK', inPlayer ? 'למדריך' : 'חזרה') + (inPlayer ? '' : h('0-9', 'מספר ערוץ'));
    if (p === 'colors' || p === 'magic') return base + colours;
    return base + h('☰', 'הגדרות') + colours;
  }
  function renderHints() {
    if (el.hints) el.hints.innerHTML = hintHtml(false);
    if (el.atvColors) {
      el.atvColors.innerHTML = colorDotBtn('red', 'keyRed') + colorDotBtn('green', 'keyGreen') + colorDotBtn('yellow', 'keyYellow') + colorDotBtn('blue', 'keyBlue');
    }
    renderOsdHints();
  }
  function renderOsdHints() { if (el.osdHints) el.osdHints.innerHTML = hintHtml(true); }

  function runAction(action) {
    var inPlayer = state.overlay === 'player';
    if (TVUI && !inPlayer && !state.overlay && atvAction(action)) return;
    var ch = inPlayer ? state.playing : (TVUI ? atvCurrent() : state.list[state.gridIdx]);
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
  var QUALITIES = [
    ['auto', 'אוטומטי (Adaptive)'],
    ['max', 'הכי גבוהה שיש'],
    ['upscale4k', 'השבחה ל-4K / Ultra Sharp (AI & חדות מוגברת)'],
    ['1080p', 'Full HD (1080p) מקסימלי'],
    ['720p', 'HD (720p) - מומלץ ליציבות'],
    ['480p', 'SD (480p) - יציב במיוחד'],
    ['360p', '360p - לחיבור חלש (ללא תקיעות)'],
    ['saver', 'חסכונית (SD)']
  ];
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

  var M3_ICONS = {
    profiles: '<svg class="m3-ico" viewBox="0 0 24 24"><path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z"/></svg>',
    google_cloud: '<svg class="m3-ico" viewBox="0 0 24 24"><path fill="currentColor" d="M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM19 18H6c-2.21 0-4-1.79-4-4 0-2.05 1.53-3.76 3.56-3.97l1.07-.11.5-.95C8.08 7.14 9.94 6 12 6c2.62 0 4.88 1.86 5.39 4.43l.3 1.5 1.53.11c1.56.1 2.78 1.41 2.78 2.96 0 1.65-1.35 3-3 3z"/></svg>',
    display: '<svg class="m3-ico" viewBox="0 0 24 24"><path fill="currentColor" d="M21 3H3c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h5v2h8v-2h5c1.1 0 1.99-.9 1.99-2L23 5c0-1.1-.9-2-2-2zm0 14H3V5h18v12z"/></svg>',
    channels: '<svg class="m3-ico" viewBox="0 0 24 24"><path fill="currentColor" d="M21 6h-7.58l3.29-3.29L16 2l-4 4h-.03l-4-4-.71.71L10.56 6H3c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h18c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm0 14H3V8h18v12z"/></svg>',
    player: '<svg class="m3-ico" viewBox="0 0 24 24"><path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 14.5v-9l6 4.5-6 4.5z"/></svg>',
    remote: '<svg class="m3-ico" viewBox="0 0 24 24"><path fill="currentColor" d="M15 9H9c-.55 0-1 .45-1 1v4c0 .55.45 1 1 1h6c.55 0 1-.45 1-1v-4c0-.55-.45-1-1-1zm-1 4h-4v-2h4v2zM6 5v14c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2H8c-1.1 0-2 .9-2 2zm10 14H8V5h8v14z"/></svg>',
    dub: '<svg class="m3-ico" viewBox="0 0 24 24"><path fill="currentColor" d="M12.87 15.07l-2.54-2.51.03-.03c1.74-1.94 2.98-4.17 3.71-6.53H17V4h-7V2H8v2H1v1.99h11.17C11.5 7.92 10.44 9.75 9 11.35 8.07 10.32 7.3 9.19 6.69 8h-2c.73 1.63 1.73 3.17 2.98 4.56l-5.09 5.02L4 19l5-5 3.11 3.11.76-2.04zM18.5 10h-2L12 22h2l1.12-3h4.75L21 22h2l-4.5-12zm-2.62 7l1.62-4.33L19.12 17h-3.24z"/></svg>',
    kids: '<svg class="m3-ico" viewBox="0 0 24 24"><path fill="currentColor" d="M18.92 6.01C18.72 5.42 18.16 5 17.5 5h-11c-.66 0-1.21.42-1.42 1.01L3 12v8c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-1h12v1c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-8l-2.08-5.99zM6.5 16c-.83 0-1.5-.67-1.5-1.5S5.67 13 6.5 13s1.5.67 1.5 1.5S7.33 16 6.5 16zm11 0c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zM5 11l1.5-4.5h11L19 11H5z"/></svg>',
    saver: '<svg class="m3-ico" viewBox="0 0 24 24"><path fill="currentColor" d="M12.3 2a10 10 0 0 0-1.9 20 10 10 0 0 0 9.6-7.3 1 1 0 0 0-1.1-1.2 8 8 0 0 1-7.8-10.4 1 1 0 0 0-1.2-1.1z"/></svg>',
    updates: '<svg class="m3-ico" viewBox="0 0 24 24"><path fill="currentColor" d="M17 1.01L7 1c-1.1 0-2 .9-2 2v18c0 1.1.9 2 2 2h10c1.1 0 2-.9 2-2V3c0-1.1-.9-1.99-2-1.99zM17 19H7V5h10v14zm-1-6h-2.5V9h-3v4H8l4 4 4-4z"/></svg>',
    about: '<svg class="m3-ico" viewBox="0 0 24 24"><path fill="currentColor" d="M11 17h2v-6h-2v6zm1-15C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zM11 9h2V7h-2v2z"/></svg>'
  };

  function settingsSections() {
    var cats = [];
    (window.CHANNELS_DATA || []).forEach(function (c) { if (cats.indexOf(c.category) === -1) cats.push(c.category); });
    var catRows = cats.map(function (c) {
      return { type: 'toggle', label: splitCat(c), on: function () { return (S.hiddenCats || []).indexOf(c) === -1; },
        act: function () {
          var h = (S.hiddenCats || []).slice(), i = h.indexOf(c);
          if (i === -1) h.push(c); else h.splice(i, 1);
          setSetting('hiddenCats', h);
        } };
    });
    var d = state.device;
    var secs = [
      { id: 'profiles', ico: M3_ICONS.profiles, name: 'פרופיל משתמש', desc: 'ניהול פרופילים אישיים, היסטוריית צפייה ומועדפים', rows: [
        action('החלפת פרופיל פעיל', function () { closeSettings(); openTvProfiles(); }, 'בחירת פרופיל משתמש אישי'),
        toggle('profileOnStart', 'הצגת מסך בחירת פרופיל בהפעלה', 'מציג את מסך מי צופה בכל פתיחה של האפליקציה'),
        action('ניהול ועריכת פרופילים', function () { closeSettings(); openTvProfiles(); tvProfileManaging = true; renderTvProfilesScreen(); markTvProfilesFocus(); }, 'שינוי שמות, החלפת תמונות פרופיל והוספת משתמשים')
      ] },
      { id: 'google_cloud', ico: M3_ICONS.google_cloud, name: 'חשבון Google וסנכרון ענן', desc: 'גיבוי הגדרות, מועדפים והקלטות ישירות ל-Google Drive', rows: (function () {
        var signedIn = function () { var u = window.GDrive && window.GDrive.getUser(); return !!(u && u.access_token); };
        var rows = [{ type: 'card', state: function () { return 'gcard'; }, html: function () {
          var u = (window.GDrive && window.GDrive.getUser()) || {};
          if (signedIn()) {
            var pic = u.picture ? '<img class="gc-logo" src="' + esc(u.picture) + '" alt="">' : '<div class="gc-logo">G</div>';
            return '<div class="gc">' + pic + '<div class="gc-text"><div class="gc-name">' + esc(u.name || 'משתמש מחובר') + '</div>' +
              '<div class="gc-sub">' + esc(u.email || '') + '</div>' +
              '<div class="gc-sub">סונכרן: ' + esc(window.GDrive.getLastSyncFormatted()) + '</div></div>' +
              '<span class="gc-on">מחובר</span></div>';
          }
          return '<div class="gc"><div class="gc-logo"><svg width="28" height="28" viewBox="0 0 24 24"><path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/><path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/><path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"/><path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"/></svg></div>' +
            '<div class="gc-text"><div class="gc-name">התחברות עם חשבון Google</div>' +
            '<div class="gc-sub">סנכרון המועדפים, ההיסטוריה, הפרופילים וההגדרות, והקלטה ישירה לענן</div></div>' +
            '<span class="gc-btn">התחברות</span></div>';
        }, act: function () {
          if (!signedIn() && window.GDrive) window.GDrive.signIn(function () { renderSettings(); });
        } }];
        if (!signedIn()) return rows;
        return rows.concat([
          action('סנכרון עכשיו מול הענן', function () { triggerCloudSync(true); }, 'סנכרון מיידי של ערוצים מועדפים, היסטוריה והגדרות מול חשבון ה-Drive'),
          action('ההקלטות שלי ב-Google Drive', function () { openDriveRecordingsDialog(); }, 'צפייה, ניגון וניהול של כל הסדרות והשידורים שהוקלטו בענן'),
          action('התנתקות מחשבון Google', function () {
            confirmBox('להתנתק מחשבון Google?', function () { window.GDrive.signOut(function () { renderSettings(); }); });
          }, '', true)
        ]);
      })() },
      { id: 'display', ico: M3_ICONS.display, name: 'תצוגה וממשק', desc: 'ערכות נושא, סגנון עיצוב טלוויזיה, פלטת צבעים ועיצוב מסך', rows: [
        sep('מראה וסגנון עיצוב'),
        { type: 'choice', tvOnly: true, label: 'עיצוב', help: (DESIGNS[S.design] || DESIGNS.classic).help, get: function () { return label(DESIGN_LIST, S.design); },
          step: function (st) { setSetting('design', cycle(DESIGN_LIST, S.design, st)); } },
        designRow(choice('theme', 'ערכת נושא', THEMES)),
        designRow({ type: 'choice', label: 'פלטת צבעים (Material You)', help: readSystemColors() ? 'צבעי המערכת מבוססים על ערכת הטפט (Dynamic Colors)' : 'ערכות הצבע הנבחרות של הממשק',
          get: function () { return label(PALETTES, S.accent) + (S.accent === 'auto' ? ' (' + label(PALETTES, accentName()) + ')' : ''); },
          step: function (st) {
            var list = PALETTES.filter(function (p) { return p[0] !== 'system' || readSystemColors(); });
            setSetting('accent', cycle(list, S.accent, st));
          } }),
        { type: 'choice', tvOnly: true, label: 'קנה מידה של התצוגה', help: 'התאמת גודל טקסטים ורכיבים לצפייה ממרחק סלון', get: function () { return label(SCALES, S.uiScale); },
          step: function (st) { setSetting('uiScale', cycle(SCALES, S.uiScale, st)); } },
        { type: 'choice', label: 'מספר ערוצים בשורה', get: function () { return S.cols ? String(S.cols) : 'אוטומטי (' + state.cols + ')'; },
          step: function (st) { setSetting('cols', cycle(COLS, S.cols, st)); } },
        sep('רכיבי מסך'),
        tvRow(toggle('hero', 'כרטיס מידע מורחב לערוץ הנבחר')),
        toggle('numbers', 'מספרי ערוצים על גבי הכרטיסים'),
        toggle('showSource', 'מקור ושרת השידור', 'פרטי שרת השידור ואיכות המקור בפרטי הערוץ ובנגן', function () { updateHero(state.list[state.gridIdx]); }),
        tvRow(toggle('hints', 'סרגל מקשי שלט בתחתית המסך')),
        { type: 'choice', label: 'מצב ביצועים ואנימציה', help: 'מצב מהיר מבטל אנימציות כבדות ומשפר תגובתיות בטלוויזיות חלשות',
          get: function () { return label(PERF, S.perf); }, step: function (st) { setSetting('perf', cycle(PERF, S.perf, st)); } }
      ].concat(NATIVE && NATIVE.launchSound ? [
        { type: 'toggle', label: 'צליל פתיחה בהפעלה', help: 'השמעת צליל קצר בעת עליית האפליקציה',
          on: function () { return native('launchSound') !== false; }, act: function () { native('setLaunchSound', native('launchSound') === false); } }
      ] : []) },
      { id: 'channels', ico: M3_ICONS.channels, name: 'ערוצים ומדריך שידורים', desc: 'הגדרת קטגוריות, ערוצים מוסתרים ואפשרויות פתיחה', rows: [
        sep('התנהגות'),
        choice('startCat', 'קטגוריית פתיחה', STARTS),
        toggle('autoplay', 'הפעלת הערוץ האחרון שנצפה בעלייה'),
        sep('ניהול ערוצים ומועדפים'),
        info('ערוצים מוסתרים', function () { return (S.hidden || []).length ? (S.hidden || []).length + ' ערוצים' : 'אין ערוצים מוסתרים'; }),
        action('שחזור כל הערוצים המוסתרים', function () { setSetting('hidden', []); toast('כל הערוצים שוחזרו'); }),
        action('איפוס היסטוריית צפייה (ערוצים אחרונים)', function () { state.recents = []; lsSet('israel_tv_recents', []); buildCategories(); toast('היסטוריית הצפייה אופסה'); }),
        action('מחיקת כל הערוצים המועדפים', function () { confirmBox('למחוק את כל הערוצים המועדפים?', function () { state.favorites = []; lsSet('israel_tv_favorites', []); buildCategories(); filterList(); toast('המועדפים נמחקו'); }); }, '', true)
      ].concat(IS_WEB ? [toggle('hideAppOnly', 'הסתרת ערוצים הפועלים באפליקציה בלבד', 'הסתרת שידורי HTTP הנחסמים בדפדפנים')] : [])
        .concat([sep('קטגוריות מוצגות')]).concat(catRows) },
      { id: 'player', ico: M3_ICONS.player, name: 'נגן וידאו ושידור', desc: 'איכות תמונה, אפסקייל והשבחת וידאו, השהיה ושחזור שידור', rows: [
        sep('השבחת תמונה ואפסקייל'),
        choice('upscaleMode', 'מצב אפסקייל והשבחת תמונה', UPSCALE_MODES, 'מנגנון השבחת רזולוציה וחדות תמונה דינמית מואץ חומרה ל-4K ו-HD'),
        choice('pic', 'מצב צבע ותמונה', PICS), choice('aspect', 'התאמת תמונה למסך', ASPECTS),
        toggle('sharpen', 'שיפור חדות תמונה (SD Enhancer)', 'מנגנון חידוד תמונה מתקדם לשידורים ישנים'),
        choice('quality', 'איכות שידור מועדפת', QUALITIES),
        choice('latency', 'מצב השהיה וחיבור', LAT, 'השהיה נמוכה מקרבת לשידור החי בזמן אמת'),
        toggle('retry', 'חיבור מחדש אוטומטי בעת ניתוק שידור'),
        sep('שליטה ומעבר ערוצים'),
        choice('zap', 'סדר מעבר ערוצים בחיצים', ZAPS),
        { type: 'toggle', label: 'כיוון מעבר ערוצים בשלט', help: S.upNext ? 'חץ למעלה = מעבר לערוץ הבא' : 'חץ למעלה = מעבר לערוץ הקודם', on: function () { return S.upNext; },
          act: function () { setSetting('upNext', !S.upNext); } },
        sep('תצוגת פרטי שידור (OSD)'),
        choice('osdSec', 'משך הצגת פרטי שידור', OSDS),
        toggle('osdClock', 'הצגת שעון בנגן')
      ] },
      { id: 'remote', tvOnly: true, ico: M3_ICONS.remote, name: 'הגדרות שלט רחוק', desc: 'התאמת מקשי צבע וקיצורי דרך בשלט: ' + remoteName(), rows: [
        sep('סוג שלט'),
        { type: 'choice', label: 'סוג שלט מזוהה', help: 'התאמת מקשים והנחיות על המסך בהתאם לדגם השלט',
          get: function () { return label(REMOTES, S.remote); }, step: function (st) { setSetting('remote', cycle(REMOTES, S.remote, st)); } },
        sep('מקשי צבע'),
        { type: 'choice', label: 'מקש אדום (Red)', get: function () { return label(ACTIONS, S.keyRed); }, step: function (st) { setSetting('keyRed', cycle(ACTIONS, S.keyRed, st)); } },
        { type: 'choice', label: 'מקש ירוק (Green)', get: function () { return label(ACTIONS, S.keyGreen); }, step: function (st) { setSetting('keyGreen', cycle(ACTIONS, S.keyGreen, st)); } },
        { type: 'choice', label: 'מקש צהוב (Yellow)', get: function () { return label(ACTIONS, S.keyYellow); }, step: function (st) { setSetting('keyYellow', cycle(ACTIONS, S.keyYellow, st)); } },
        { type: 'choice', label: 'מקש כחול (Blue)', get: function () { return label(ACTIONS, S.keyBlue); }, step: function (st) { setSetting('keyBlue', cycle(ACTIONS, S.keyBlue, st)); } },
        sep('פעולות נוספות'),
        choice('longOk', 'פעולת לחיצה ארוכה על מקש OK', LONGOK, 'קיצור דרך נוח לכל סוגי השלטים'),
        choice('numWait', 'זמן המתנה בהזנת מספר ערוץ', NUMW)
      ] },
      { id: 'dub', ico: M3_ICONS.dub, name: 'דיבוב ותרגום חכם', desc: 'תרגום קולי אוטומטי של ערוצים זרים לעברית', rows: Dub ? [
        sep('מצב שירות'),
        dubToggle('enabled', 'דיבוב קולי פעיל'),
        dubChoice('engine', 'מנוע תרגום', (Dub.NO_LOCAL ? [] : [['local', 'ללא מפתח API – זיהוי דיבור מקומי במכשיר']]).concat([
          ['gemini', 'Google Gemini (מפתח חינמי, דיוק מקסימלי)'], ['openai', 'OpenAI (דיבוב מתקדם)']]),
          Dub.NO_LOCAL ? 'בטלוויזיה הדיבוב פועל דרך Google Gemini לצורך דיוק מרבי וביצועים מהירים'
            : 'ללא מפתח הדיבוב פועל מקומית; עם מפתח Gemini התרגום מדויק ומזהה דוברים'),
        info('מצב שירות', function () { return Dub.missingConfig() || (Dub.settings.enabled ? 'פעיל ומוכן' : 'מושבת'); }),
        action('חיבור מפתח מהטלפון (סריקת QR)', function () { pairKey(); }, 'הזנת מפתח API בקלות באמצעות סריקת קוד QR מהטלפון הנייד'),
        sep('מפתחות API'),
        dubText('geminiKey', 'מפתח Gemini API', 'מפתח חינמי: aistudio.google.com/apikey (נשמר מקומית בלבד)', 'AIza...'),
        dubText('openaiKey', 'מפתח OpenAI API', 'נשמר מקומית במכשיר בלבד', 'sk-...'),
        sep('קול וסנכרון'),
        dubChoice('tts', 'קול הדיבוב', [['auto', 'אוטומטי'], ['browser', 'קול המערכת'], ['google', 'Google TTS'], ['openai', 'OpenAI Audio'], ['none', 'כתוביות בלבד']]),
        { type: 'choice', label: 'מהירות דיבור', get: function () { return Number(Dub.settings.rate).toFixed(2); },
          step: function (st) { Dub.save({ rate: clamp(Math.round((Dub.settings.rate + 0.05 * st) * 100) / 100, 0.8, 1.6) }); } },
        { type: 'choice', label: 'עוצמת שמע מקור בזמן דיבוב', get: function () { return Math.round(Dub.settings.duck * 100) + '%'; },
          step: function (st) { Dub.save({ duck: clamp(Math.round((Dub.settings.duck + 0.05 * st) * 100) / 100, 0.1, 1) }); } },
      ].concat(Dub.IS_TV ? [info('סנכרון שפתיים', 'סנכרון תמונה וקול אוטומטי מלא בטלוויזיה')] : [
        dubToggle('sync', 'סנכרון שפתיים', 'השהיית התמונה לסנכרון מושלם עם הדיבוב'),
        dubChoice('delay', 'משך השהיית וידאו', [[6, '6 שניות'], [8, '8 שניות'], [10, '10 שניות'], [12, '12 שניות'], [15, '15 שניות'], [20, '20 שניות']])
      ]).concat([
        sep('כתוביות ובדיקה'),
        dubToggle('subtitles', 'כתוביות בעברית'),
        dubToggle('showOriginal', 'הצגת טקסט מקורי לצד התרגום'),
        action('בדיקת שמע וקול', function () { Dub.unlockAudio(); Dub.testVoice(); })
      ]) : [info('דיבוב', 'אינו זמין במכשיר זה')] },
      { id: 'kids', ico: M3_ICONS.kids, name: 'בקרת הורים ומצב ילדים', desc: 'הגבלת צפייה לערוצי ילדים בלבד ונעילה בקוד PIN', rows: [
        { type: 'toggle', label: 'הפעלת מצב ילדים', help: 'הגבלת הממשק לערוצי ילדים ותחנות שידור ייעודיות בלבד', on: function () { return S.kids; },
          act: function () {
            if (S.kids) {
              askPin(function () {
                setSetting('kids', false);
                loadChannels();
                buildCategories();
                filterList();
                if (TVUI && typeof atvBuild === 'function') atvBuild();
                renderSettings();
              });
            } else {
              setSetting('kids', true);
              loadChannels();
              buildCategories();
              filterList();
              if (TVUI && typeof atvBuild === 'function') atvBuild();
              toast('מצב ילדים הופעל');
            }
          } },
        { type: 'action', label: S.kidsPin ? 'שינוי קוד PIN (' + S.kidsPin.replace(/./g, '•') + ')' : 'הגדרת קוד PIN ליציאה (4 ספרות)', help: 'ללא קוד נעילה, ניתן לצאת ממצב ילדים באופן חופשי',
          act: function () { askPin(function () { newPin(); }); } }
      ] },
      { id: 'saver', tvOnly: true, ico: M3_ICONS.saver, name: 'שומר מסך OLED', desc: 'מניעת צריבת מסך והגנה על פאנל הטלוויזיה', rows: [
        choice('saver', 'זמן המתנה להפעלת שומר מסך', SAVERS, 'מופעל במדריך שידורים או כשהשידור מושהה בלבד')
      ] },
      { id: 'updates', ico: M3_ICONS.updates, name: 'עדכוני תוכנה ומערכת', desc: 'גרסאות תוכנה, עדכונים אונליין ויומן שינויים', rows: [
        { type: 'card', state: updateCardState, html: updateCardHtml, act: updateCardAction },
        info('גרסת אפליקציה', (d.appVersion ? 'גרסת חבילה ' + d.appVersion + ' · ' : '') + BUILD.version + (BUILD.date ? ' (' + BUILD.date + ')' : '')),
        info('גרסה פעילה', function () { return LIVE ? LIVE.version + ' (עודכנה אונליין)' : BUILD.version; }),
        toggle('autoUpdate', 'בדיקת עדכונים אוטומטית בהפעלה'),
        toggle('liveUpdates', 'עדכונים שוטפים אונליין', 'קבלת שיפורים, ערוצים חדשים ותיקונים ללא צורך בהתקנה מחדש'),
        action('לוג שינויים', openChangelog)
      ] },
      { id: 'about', ico: M3_ICONS.about, name: 'אודות והגדרות מתקדמות', desc: 'פרטי החומרה, מקורות שידור, שרתי מדיה ואיפוס', rows: [
        sep('חומרה ומערכת'),
        info('דגם מכשיר', function () { return d.platform + (d.model ? ' · ' + d.model : ''); }),
        info('מערכת הפעלה', function () { return d.os || '—'; }),
        info('רזולוציית מסך', function () { return screen.width + '×' + screen.height + ' (גודל תצוגה ' + window.innerWidth + '×' + window.innerHeight + ')'; }),
        info('דפדפן מערכת', function () { var m = /Chrome\/([\d.]+)/.exec(UA); return m ? 'Chromium ' + m[1] : UA.slice(0, 60); }),
        info('שלט מזוהה', remoteName),
        sep('יומן תקלות'),
        info('סגירות לא צפויות', crashText),
        info('שגיאה אחרונה', function () { return errors.length ? when(errors[0].t) + ' · ' + errors[0].m + ' (' + errors[0].at + ')' : 'אין שגיאות מתועדות'; }),
        action('איפוס יומן תקלות', function () { crashes = []; errors = []; lsSet('itv_crashes', []); lsSet('itv_errors', []); toast('יומן התקלות אופס'); }),
        sep('ערוצים ושרתים'),
        info('ערוצים פעילים', function () { return (window.CHANNELS_DATA || []).length + ' ערוצים (מתוכם ' + state.all.length + ' מוצגים)'; }),
        action('מקורות שידור ורישיונות', function () { openUrl(REPO + '/blob/master/SOURCES.md'); }),
        action('דף הפרויקט ב-GitHub', function () { openUrl(REPO); }),
        action('שרת מדיה ביתי (Plex & DLNA)', function () { showPlexModal(); }, 'הגדרת 269 ערוצים ולוח שידורים ב-Plex Live TV & DVR או שרת DLNA'),
        action('הורדה למכשירים נוספים (Android, TV, Windows, Mac, Linux)', function () { showGetApp(); }),
        sep('איפוס מערכת'),
        action('איפוס כל ההגדרות לברירת מחדל', function () { confirmBox('לאפס את כל הגדרות האפליקציה? (רשימת המועדפים תישמר)', function () { try { localStorage.removeItem('itv_settings'); } catch (e) {} location.reload(); }); }, '', true)
      ] }
    ];
    if (activeDesign() !== DESIGNS.classic) secs.forEach(function (s) { s.rows = s.rows.filter(function (r) { return !r.classicOnly; }); });
    if (PHONE) {
      secs = secs.filter(function (s) { return !s.tvOnly; });
      secs.forEach(function (s) { s.rows = s.rows.filter(function (r) { return !r.tvOnly; }); });
    }
    return secs;
  }

  function tvRow(r) { r.tvOnly = true; return r; }
  // the classic design is the only one that takes its colours from these rows
  function designRow(r) { r.classicOnly = true; return r; }

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
      return '<div class="nav-item" data-i="' + i + '"><span class="ni-ico">' + s.ico + '</span><span class="ni-label">' + esc(s.name) + '</span></div>';
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
    el.settingsH.innerHTML = '<span class="set-head-ico">' + sec.ico + '</span><span>' + esc(sec.name) + '</span>';
    el.settingsP.textContent = sec.desc || '';
    if (state.setShown !== state.setSection) { state.setShown = state.setSection; el.settingsRows.scrollTop = 0; }
    var n = 0, inGroup = false;
    var group = function (html, open) {
      var out = (inGroup && !open ? '</div>' : '') + (!inGroup && open ? '<div class="set-group">' : '') + html;
      inGroup = open;
      return out;
    };
    el.settingsRows.innerHTML = sec.rows.map(function (r) {
      if (r.type === 'sep') return group('<div class="set-sep">' + esc(r.label) + '</div>', false);
      if (r.type === 'card') return group('<div class="set-row upd-row" data-i="' + (n++) + '"><div class="upd-card ' + r.state() + '">' + r.html() + '</div></div>', false);
      var val = '';
      if (r.type === 'choice') val = '<span class="arr">▶</span><span class="v">' + esc(r.get()) + '</span><span class="arr">◀</span>';
      else if (r.type === 'toggle') val = '<span class="switch' + (r.on() ? ' on' : '') + '"></span>';
      else if (r.type === 'info') val = '<span class="v">' + esc(r.get()) + '</span>';
      else if (r.type === 'text') val = '<input type="' + (r.secret ? 'password' : 'text') + '" placeholder="' + esc(r.placeholder || '') + '" value="' + esc(r.get()) + '">';
      return group('<div class="set-row ' + r.type + (r.danger ? ' danger' : '') + '" data-i="' + (n++) + '"><div class="sr-text"><div class="sr-label">' + esc(r.label) + '</div>' +
        (r.help ? '<div class="sr-help">' + esc(r.help) + '</div>' : '') + '</div><div class="sr-val">' + val + '</div></div>', true);
    }).join('') + group('', false);
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
      if (n && n.app > (BUILD.app || 0) && trustedReleaseUrl(n.url)) { u.native = n; showUpdateButton(); }
      // the newest GitHub release too (it can be out before version.json): a new app version opens the update modal
      checkRelease(u, function () {
        state.lastCheck = Date.now();
        if (PLATFORM === 'webos') u.native = null;
        if (u.native) {
          showUpdateButton();
          if (!manual) offerOnce(u.native);
          else if (state.overlay !== 'dialog') showUpdateDialog();
        }
        refreshSettingsIfOpen();
      });
      if (data.code > activeCode && S.liveUpdates && (data.shell || 0) <= (BUILD.shell || 0)) {
        u.liveDownloading = true;
        try { localStorage.removeItem('itv_bad'); } catch (e) {}
        if (manual) toast('⬆️ נמצא עדכון ' + data.version + ' – מוריד עדכון...');
        var q = '?v=' + data.code;
        prefetch(['style.css', 'ui.js', 'channels.js', 'dub.js', 'tvguide.js', 'gdrive.js', 'app.js'].map(function (f) { return REMOTE + f + q; }), function (ok) {
          u.liveDownloading = false;
          if (ok) {
            state.liveReady = { code: data.code, version: data.version, shell: data.shell || 0 };
            try { localStorage.setItem('itv_live', JSON.stringify(state.liveReady)); localStorage.removeItem('itv_bad'); localStorage.removeItem('itv_pending'); } catch (e) {}
            showUpdateButton();
            if (manual) {
              confirmBox('עדכון גרסה ' + data.version + ' מוכן! להפעיל כעת?', function () {
                location.reload();
              });
            } else if (!applyLiveIfIdle()) {
              toast('⬆️ עדכון ' + data.version + ' מוכן – יופעל בפתיחה הבאה');
            }
          } else {
            if (manual) toast('הורדת העדכון לא הצליחה, נסה שוב');
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
      var list = (r && r.assets) || [];
      var vfile = file.replace('IsraelTVPlus-', 'IsraelTVPlus-' + v + '-');
      var asset = list.filter(function (a) { return a.name === vfile; })[0] || list.filter(function (a) { return a.name === file; })[0];
      if (asset && trustedReleaseUrl(asset.browser_download_url) && !r.draft && !r.prerelease && verNum(v) > verNum(installedAppVersion()) && (!u.native || verNum(v) > verNum(u.native.version))) {
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
    if (state.liveReady) {
      confirmBox('להפעיל כעת את עדכון ' + state.liveReady.version + '?', function () {
        location.reload();
      });
      return;
    }
    if (u && u.native && PLATFORM !== 'webos') showUpdateDialog();
    else checkUpdates(true);
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
        if (PLATFORM === 'android') {
          buttons.push({ label: '🌐 הורדה ישירה (APK)', act: function () { openUrl(u.native.url || RELEASES); } });
        }
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
        if (el.dialogActions) {
          el.dialogActions.innerHTML = '<button class="btn primary" id="upd-fb-btn">🌐 הורדה ישירה בדפדפן</button><button class="btn" id="upd-close-btn">סגירה</button>';
          var fb = $('upd-fb-btn'), cb = $('upd-close-btn');
          if (fb) fb.onclick = function () { openUrl(n.url || RELEASES); };
          if (cb) cb.onclick = function () { closeDialog(); };
        }
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
    // only web links leave the app: a tampered channel/update entry must not reach intent:, file:, tel: or javascript: targets
    if (url && !/^https?:\/\//i.test(String(url))) return;
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
  /** Listening mode: no picture and the lowest quality the stream offers (most of the data is the video). */
  function setAudioOnly(on) {
    state.audioOnly = on;
    toggleClass(el.player, 'audio-only-view', on);
    if (state.hls) { try { state.hls.autoLevelCapping = on ? 0 : -1; if (on) state.hls.nextLevel = 0; } catch (e) {} }
    toast(on ? 'מצב האזנה: בלי תמונה, והשידור עבר לאיכות הנמוכה ביותר' : 'התמונה חזרה');
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
      if (!state.overlay && !TVUI) focusGrid(idx);
      playIndex(idx);
    }, (S.numWait || 1.2) * 1000);
  }

  // ================================================================== search
  function focusSearch() {
    if (state.overlay) return;
    if (TVUI) { atvOpenSearch(); return; }
    var items = topItems();
    focusTop(items.indexOf(el.topSearch));
    el.searchInput.focus();
  }

  // ================================================================== input: keys, long OK, mouse, touch
  function isBack(code) { return code === 461 || code === 27 || code === 10009 || code === 8; }
  function typing(t) { return t && (t.tagName === 'INPUT' || t.tagName === 'TEXTAREA'); }

  function setupInput() {
    setupGuide();
        var wheelGlobalAt = 0;
    window.addEventListener('wheel', function (e) {
      var now = Date.now();
      if (now - wheelGlobalAt < 160) return;
      wheelGlobalAt = now;
      var dy = e.deltaY, dx = e.deltaX;
      if (Math.abs(dy) < 5 && Math.abs(dx) < 5) return;
      
      if (state.overlay === 'player') {
        if (Guide && !el.osd.classList.contains('off')) {
          Guide.playerKey(dy > 0 ? 37 : 39, false);
        } else {
          zap(dy > 0 ? (S.upNext ? 1 : -1) : (S.upNext ? -1 : 1));
        }
        return;
      }
      if (state.overlay === 'tvguide' && Guide) {
        Guide.key(dy > 0 ? 40 : 38);
        return;
      }
      if (!state.overlay && state.zone === 'atv') {
        if (Math.abs(dx) > Math.abs(dy) || e.shiftKey) atvKey((dx || dy) > 0 ? 37 : 39);
        else atvKey(dy > 0 ? 40 : 38);
        return;
      }
    }, { passive: true });
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
    if (el.btnGdrive) el.btnGdrive.addEventListener('click', function () { openDriveRecordingsDialog(); });
    if (el.btnMultiview) el.btnMultiview.addEventListener('click', function () { openMultiView(state.playing || (TVUI ? atvCurrent() : state.list[state.gridIdx])); });
    if (el.btnCastTop) el.btnCastTop.addEventListener('click', function () { openCastDialog(state.playing || (TVUI ? atvCurrent() : state.list[state.gridIdx])); });
    el.btnUpdate.addEventListener('click', showUpdateDialog);
    el.btnGetapp.addEventListener('click', function () { showGetApp(); });
    el.btnKidsExit.addEventListener('click', function () { askPin(function () { setSetting('kids', false); toast('יצאתם ממצב ילדים'); }); });
    document.addEventListener('click', function (e) {
      var cb = e.target.closest && e.target.closest('[data-color-act]');
      if (!cb) return;
      e.stopPropagation();
      runAction(cb.getAttribute('data-color-act'));
    });
    if (el.multiview) {
      el.multiview.addEventListener('click', function (e) {
        var lb = e.target.closest('.mv-lbtn');
        if (lb) { setMultiViewLayout(+lb.getAttribute('data-mv')); return; }
        if (e.target.closest('#mv-close')) { closeMultiView(); return; }
        var actBtn = e.target.closest('[data-mvact]');
        if (actBtn) {
          e.stopPropagation();
          var cellEl = actBtn.closest('.mv-cell');
          var ci = cellEl ? +cellEl.getAttribute('data-cell') : 0;
          var act = actBtn.getAttribute('data-mvact');
          if (act === 'audio') { mv.activeAudio = ci; updateMultiViewAudio(); }
          else if (act === 'server') {
            var cell = mv.cells[ci];
            if (cell && cell.candidates && cell.candidates.length > 1) {
              cell.candIdx = (cell.candIdx + 1) % cell.candidates.length;
              toast('🔀 מסך ' + (ci + 1) + ': שרת ' + (cell.candIdx + 1) + '/' + cell.candidates.length);
              playMultiViewCell(ci, true);
            } else toast('אין שרתי גיבוי נוספים לערוץ זה');
          } else if (act === 'pick') openMultiViewPicker(ci);
          else if (act === 'full') {
            var ch = mv.cells[ci] && mv.cells[ci].ch;
            closeMultiView();
            if (ch) {
              var idx = indexInList(ch.id);
              if (idx === -1) { selectCategory('ALL', true); idx = indexInList(ch.id); }
              if (idx !== -1) playIndex(idx);
            }
          }
          return;
        }
        var cNode = e.target.closest('.mv-cell');
        if (cNode) {
          var cIdx = +cNode.getAttribute('data-cell');
          mv.focusIdx = cIdx;
          mv.activeAudio = cIdx;
          updateMultiViewAudio();
        }
      });
    }
    if (el.mvPicker) {
      el.mvPicker.addEventListener('click', function (e) {
        if (e.target === el.mvPicker || e.target.closest('#mv-picker-close')) { closeMultiViewPicker(); return; }
        var catBtn = e.target.closest('[data-mvcat]');
        if (catBtn) {
          mv.pickerCat = catBtn.getAttribute('data-mvcat');
          renderMultiViewPickerCats();
          renderMultiViewPickerGrid();
          return;
        }
        var card = e.target.closest('.mv-pick-card');
        if (card) {
          var pi = +card.getAttribute('data-picki');
          selectMultiViewChannel(mv.pickerList[pi]);
        }
      });
    }
    if (el.mvPickerSearch) {
      el.mvPickerSearch.addEventListener('input', function () {
        mv.pickerQuery = el.mvPickerSearch.value;
        renderMultiViewPickerGrid();
      });
    }
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
    var setClose = $('btn-settings-close');
    if (setClose) setClose.addEventListener('click', function () { closeSettings(); });
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
    else if (a === 'cast') openCastDialog(state.playing);
    else if (a === 'server') switchBackupServer();
    else if (a === 'multiview') openMultiView(state.playing);
    if (PHONE) haptic('tick');
    showOsd();
  }

  // ================================================================== Multi-View (Split-TV) Engine – Works with ALL 440 Channels
  var mv = {
    layout: 2,
    cells: [],        // [{ id, ch, hls, audioEl, videoEl, candIdx, candidates, timer, startTimer }]
    activeAudio: 0,
    focusIdx: 0,      // 0..layout-1 for cells, -1 for layout buttons / close
    pickerCell: 0,
    pickerOpen: false,
    pickerQuery: '',
    pickerCat: 'ALL',
    pickerList: [],
    pickerIdx: 0
  };

  function mvIsOttrast(u) {
    return /ottrast\.com|212\.80\.215\.72/i.test(u || '');
  }

  function mvCdnPriority(u) {
    if (!u) return 99;
    if (/mako-streaming\.akamaized\.net|keshet|cloudfront\.net|kan\.org\.il|kancdn|redge\.media|gostreaming\.tv|ovoko\.live|pluto\.tv|cast-tv\.com|cdn-israel/i.test(u)) return 1;
    if (/akamaized\.net|fastly|llnwd|bitmovin|mux\.com|brightcove/i.test(u)) return 2;
    if (mvIsOttrast(u)) return 8;
    return 4;
  }

  function mvOttrastUsedByOtherCell(excludeIdx) {
    for (var i = 0; i < mv.cells.length; i++) {
      if (i === excludeIdx) continue;
      var c = mv.cells[i];
      if (c && c.activeUrl && mvIsOttrast(c.activeUrl)) return true;
    }
    return false;
  }

  function mvCandidatesForCell(ch, cellIdx) {
    var base = candidateUrlsOf(ch);
    var ottrastBusy = mvOttrastUsedByOtherCell(cellIdx);
    return base.slice().sort(function (a, b) {
      var pa = mvCdnPriority(a) + (ottrastBusy && mvIsOttrast(a) ? 20 : 0);
      var pb = mvCdnPriority(b) + (ottrastBusy && mvIsOttrast(b) ? 20 : 0);
      return pa - pb;
    });
  }

  function mvDestroyCellPlayer(cell) {
    if (!cell) return;
    if (cell.timer) { clearTimeout(cell.timer); cell.timer = null; }
    if (cell.startTimer) { clearTimeout(cell.startTimer); cell.startTimer = null; }
    if (cell.hls) {
      try { cell.hls.stopLoad(); cell.hls.detachMedia(); cell.hls.destroy(); } catch (e) {}
      cell.hls = null;
    }
    if (cell.videoEl) {
      try { cell.videoEl.pause(); cell.videoEl.removeAttribute('src'); cell.videoEl.load(); } catch (e) {}
    }
    if (cell.audioEl) {
      try { cell.audioEl.pause(); cell.audioEl.removeAttribute('src'); } catch (e) {}
    }
    cell.activeUrl = '';
  }

  function openMultiView(initialCh) {
    // Stop main player & TV background preview completely so zero background streams compete with Multi-View!
    if (atv.restTimer) { clearTimeout(atv.restTimer); atv.restTimer = null; }
    if (atv.stopTimer) { clearTimeout(atv.stopTimer); atv.stopTimer = null; }
    stopPlayback();
    el.player.classList.add('hidden');
    document.body.classList.remove('player-open', 'atv-live');

    var defaults = ['keshet12', 'reshet13', 'now14', 'kan11', 'i24news_he', 'sport5', 'channel16', 'channel9'];
    var picked = [];
    var addCh = function (c) {
      if (!c || isApp(c)) return;
      for (var i = 0; i < picked.length; i++) if (picked[i].id === c.id) return;
      picked.push(c);
    };
    if (initialCh) addCh(initialCh);
    defaults.forEach(function (id) {
      var idx = indexInAll(id);
      if (idx !== -1) addCh(state.all[idx]);
    });
    state.all.slice(0, 12).forEach(addCh);

    if (!mv.cells.length) {
      for (var k = 0; k < 4; k++) {
        var ch = picked[k % picked.length] || state.all[k] || state.all[0];
        mv.cells.push({ id: ch.id, ch: ch, hls: null, candIdx: 0, candidates: [] });
      }
    } else if (initialCh && !isApp(initialCh)) {
      mv.cells[0].id = initialCh.id;
      mv.cells[0].ch = initialCh;
      mv.cells[0].candIdx = 0;
    }

    mv.pickerOpen = false;
    if (el.mvPicker) el.mvPicker.classList.add('hidden');
    state.overlay = 'multiview';
    mv.focusIdx = 0;
    el.multiview.classList.remove('hidden');
    renderMultiView();
  }

  function closeMultiView() {
    for (var i = 0; i < mv.cells.length; i++) mvDestroyCellPlayer(mv.cells[i]);
    mv.pickerOpen = false;
    if (el.mvPicker) el.mvPicker.classList.add('hidden');
    if (el.multiview) el.multiview.classList.add('hidden');
    state.overlay = null;
    if (TVUI) atvRestore();
    else focusGrid(state.gridIdx);
  }

  function setMultiViewLayout(n) {
    mv.layout = n === 3 ? 3 : n === 4 ? 4 : 2;
    if (mv.activeAudio >= mv.layout) mv.activeAudio = 0;
    if (mv.focusIdx >= mv.layout) mv.focusIdx = mv.layout - 1;
    renderMultiView();
  }

  function updateMultiViewAudio() {
    for (var i = 0; i < mv.layout; i++) {
      var cell = mv.cells[i];
      if (!cell) continue;
      var active = i === mv.activeAudio;
      if (cell.videoEl) { cell.videoEl.muted = !active; cell.videoEl.volume = 1; }
      if (cell.audioEl) { cell.audioEl.muted = !active; cell.audioEl.volume = 1; }
      var cellNode = el.mvGrid.children[i];
      if (cellNode) {
        toggleClass(cellNode, 'active-audio', active);
        var abtn = cellNode.querySelector('[data-mvact="audio"]');
        if (abtn) {
          abtn.textContent = active ? '🔊 שמע פעיל' : '🔇 הפעל שמע';
          toggleClass(abtn, 'on', active);
        }
      }
    }
  }

  function renderMultiView() {
    var lbtns = el.multiview.querySelectorAll('.mv-lbtn');
    for (var b = 0; b < lbtns.length; b++) {
      toggleClass(lbtns[b], 'on', +lbtns[b].getAttribute('data-mv') === mv.layout);
    }
    for (var d = 0; d < mv.cells.length; d++) mvDestroyCellPlayer(mv.cells[d]);

    el.mvGrid.className = 'mv-grid mv-' + mv.layout;
    el.mvGrid.innerHTML = '';

    for (var i = 0; i < mv.layout; i++) {
      var cell = mv.cells[i];
      var ch = cell.ch || state.all[indexInAll(cell.id)] || state.all[0];
      cell.ch = ch;
      var div = document.createElement('div');
      div.className = 'mv-cell' + (i === mv.activeAudio ? ' active-audio' : '') + (i === mv.focusIdx ? ' focused' : '');
      div.setAttribute('data-cell', i);
      div.innerHTML =
        '<video playsinline autoplay></video>' +
        '<audio></audio>' +
        '<div class="mv-radio-badge hidden">📻 ' + esc(ch.name) + '</div>' +
        '<div class="mv-cell-status hidden">מתחבר...</div>' +
        '<div class="mv-cell-hud">' +
          '<div class="mv-cell-info"><span class="mv-cell-num">' + ch.number + '</span><span class="mv-cell-name">' + esc(ch.name) + '</span></div>' +
          '<div class="mv-cell-actions">' +
            '<button type="button" class="mv-cbtn' + (i === mv.activeAudio ? ' on' : '') + '" data-mvact="audio">' + (i === mv.activeAudio ? '🔊 שמע פעיל' : '🔇 הפעל שמע') + '</button>' +
            '<button type="button" class="mv-cbtn" data-mvact="server">🔀 שרת</button>' +
            '<button type="button" class="mv-cbtn" data-mvact="pick">📺 החלף ערוץ</button>' +
            '<button type="button" class="mv-cbtn" data-mvact="full">⛶ מסך מלא</button>' +
          '</div>' +
        '</div>';
      el.mvGrid.appendChild(div);
      cell.videoEl = div.querySelector('video');
      cell.audioEl = div.querySelector('audio');
      cell.statusEl = div.querySelector('.mv-cell-status');
      cell.radioEl = div.querySelector('.mv-radio-badge');
      cell.candidates = mvCandidatesForCell(ch, i);
      cell.candIdx = 0;
      (function (idx) {
        mv.cells[idx].startTimer = setTimeout(function () {
          if (state.overlay === 'multiview') playMultiViewCell(idx);
        }, idx * 170);
      })(i);
    }
  }

  function mvShowStatus(cell, txt) {
    if (!cell || !cell.statusEl) return;
    if (!txt) { cell.statusEl.classList.add('hidden'); return; }
    cell.statusEl.textContent = txt;
    cell.statusEl.classList.remove('hidden');
  }

  function mvFailoverCell(idx) {
    var cell = mv.cells[idx];
    if (!cell || state.overlay !== 'multiview') return;
    if (!cell.candidates || !cell.candidates.length) cell.candidates = mvCandidatesForCell(cell.ch, idx);
    if (cell.candIdx + 1 < cell.candidates.length) {
      cell.candIdx++;
      mvShowStatus(cell, 'שרת גיבוי (' + (cell.candIdx + 1) + '/' + cell.candidates.length + ')...');
      playMultiViewCell(idx, true);
    } else {
      mvShowStatus(cell, '⚠️ השידור לא זמין כרגע – לחצו "החלף ערוץ"');
    }
  }

  function playMultiViewCell(idx, keepCandIdx) {
    var cell = mv.cells[idx];
    if (!cell || !cell.ch || state.overlay !== 'multiview') return;
    var ch = cell.ch;
    if (!keepCandIdx) {
      cell.candidates = mvCandidatesForCell(ch, idx);
      cell.candIdx = 0;
    }
    mvDestroyCellPlayer(cell);
    var rawUrl = cell.candidates[cell.candIdx] || streamUrl(ch);
    if (!rawUrl) { mvShowStatus(cell, 'אין מקור שידור'); return; }
    mvShowStatus(cell, 'מתחבר...');

    var needsMako = (ch.auth === 'mako' && rawUrl === streamUrl(ch)) || /mako-streaming\.akamaized\.net/i.test(rawUrl);
    if (needsMako) {
      makoUrl(rawUrl, function (resolvedUrl) {
        if (state.overlay === 'multiview' && mv.cells[idx] === cell) mvStartResolvedCell(idx, resolvedUrl);
      }, function () {
        if (state.overlay === 'multiview' && mv.cells[idx] === cell) mvFailoverCell(idx);
      });
      return;
    }
    mvStartResolvedCell(idx, rawUrl);
  }

  function mvStartResolvedCell(idx, url) {
    var cell = mv.cells[idx];
    if (!cell || !cell.ch || state.overlay !== 'multiview') return;
    var ch = cell.ch;
    cell.activeUrl = url;
    var activeAudio = idx === mv.activeAudio;

    cell.timer = setTimeout(function () {
      if (state.overlay === 'multiview' && mv.cells[idx] === cell && cell.activeUrl === url) {
        mvFailoverCell(idx);
      }
    }, 8500);

    var onPlaying = function () {
      if (cell.timer) { clearTimeout(cell.timer); cell.timer = null; }
      mvShowStatus(cell, '');
    };

    if (isAudioUrl(url, ch)) {
      if (cell.radioEl) { cell.radioEl.textContent = '📻 ' + ch.name; cell.radioEl.classList.remove('hidden'); }
      var a = cell.audioEl;
      a.muted = !activeAudio;
      a.volume = 1;
      a.addEventListener('playing', function onp() { a.removeEventListener('playing', onp); onPlaying(); });
      if (/\.m3u8(\?|$)/i.test(url) && window.Hls && Hls.isSupported()) {
        var ahls = new Hls({ enableWorker: true, manifestLoadingMaxRetry: 2, levelLoadingMaxRetry: 2 });
        cell.hls = ahls;
        ahls.loadSource(url);
        ahls.attachMedia(a);
        ahls.on(Hls.Events.MANIFEST_PARSED, function () {
          var p = a.play(); if (p && p.catch) p.catch(function () {});
        });
        ahls.on(Hls.Events.ERROR, function (e, data) { if (data && data.fatal) mvFailoverCell(idx); });
      } else {
        a.src = url;
        a.onerror = function () { mvFailoverCell(idx); };
        var pa = a.play(); if (pa && pa.catch) pa.catch(function () {});
      }
      return;
    }

    if (cell.radioEl) cell.radioEl.classList.add('hidden');
    var v = cell.videoEl;
    v.muted = !activeAudio;
    v.volume = 1;
    v.addEventListener('playing', function onvp() { v.removeEventListener('playing', onvp); onPlaying(); });

    if (window.Hls && Hls.isSupported()) {
      var hls = new Hls({
        enableWorker: true,
        backBufferLength: 12,
        maxBufferLength: 18,
        manifestLoadingMaxRetry: 1,
        levelLoadingMaxRetry: 1,
        fragLoadingMaxRetry: 2,
        capLevelToPlayerSize: true
      });
      cell.hls = hls;
      hls.loadSource(url);
      hls.attachMedia(v);
      hls.on(Hls.Events.LEVEL_LOADED, function (e, data) {
        if (data && data.details && (!data.details.fragments || !data.details.fragments.length)) {
          mvFailoverCell(idx);
        }
      });
      hls.on(Hls.Events.MANIFEST_PARSED, function () {
        v.muted = idx !== mv.activeAudio;
        var p = v.play();
        if (p && p.catch) p.catch(function () {
          v.muted = true;
          var p2 = v.play(); if (p2 && p2.catch) p2.catch(function () {});
        });
      });
      hls.on(Hls.Events.ERROR, function (e, data) {
        if (!data || !data.fatal) return;
        if (data.type === Hls.ErrorTypes.MEDIA_ERROR && !cell.recovered) {
          cell.recovered = true;
          try { hls.recoverMediaError(); return; } catch (ex) {}
        }
        mvFailoverCell(idx);
      });
    } else {
      v.src = url;
      v.onerror = function () { mvFailoverCell(idx); };
      var pv = v.play(); if (pv && pv.catch) pv.catch(function () {});
    }
  }

  var MV_CATS = [
    ['ALL', 'הכל'],
    ['BROADCAST', 'ערוצי ישראל'],
    ['SPORTS', 'ספורט'],
    ['KIDS', 'ילדים ונוער'],
    ['MOVIES', 'סרטים וסדרות'],
    ['WORLD', 'חדשות והעולם'],
    ['RADIO', 'מוזיקה ורדיו'],
    ['FAV', 'מועדפים']
  ];

  function openMultiViewPicker(cellIdx) {
    mv.pickerCell = cellIdx;
    mv.pickerOpen = true;
    mv.pickerQuery = '';
    mv.pickerCat = 'ALL';
    mv.pickerIdx = 0;
    if (el.mvPickerSearch) el.mvPickerSearch.value = '';
    if (el.mvPickerTitle) el.mvPickerTitle.textContent = 'בחירת ערוץ למסך ' + (cellIdx + 1);
    renderMultiViewPickerCats();
    renderMultiViewPickerGrid();
    el.mvPicker.classList.remove('hidden');
  }

  function closeMultiViewPicker() {
    mv.pickerOpen = false;
    if (el.mvPicker) el.mvPicker.classList.add('hidden');
  }

  function renderMultiViewPickerCats() {
    if (!el.mvPickerCats) return;
    el.mvPickerCats.innerHTML = MV_CATS.map(function (c) {
      return '<button type="button" class="mv-cat-chip' + (c[0] === mv.pickerCat ? ' on' : '') + '" data-mvcat="' + c[0] + '">' + esc(c[1]) + '</button>';
    }).join('');
  }

  function renderMultiViewPickerGrid() {
    if (!el.mvPickerGrid) return;
    var q = (mv.pickerQuery || '').trim().toLowerCase();
    var cat = mv.pickerCat;
    var curId = mv.cells[mv.pickerCell] && mv.cells[mv.pickerCell].ch && mv.cells[mv.pickerCell].ch.id;
    mv.pickerList = state.all.filter(function (c) {
      if (isApp(c)) return false;
      if (cat === 'FAV' && !isFav(c)) return false;
      else if (cat === 'BROADCAST' && atvBucket(c) !== 'broadcast') return false;
      else if (cat === 'SPORTS' && atvBucket(c) !== 'sports') return false;
      else if (cat === 'KIDS' && atvBucket(c) !== 'kids') return false;
      else if (cat === 'MOVIES' && atvBucket(c) !== 'movies') return false;
      else if (cat === 'WORLD' && atvBucket(c) !== 'world' && atvBucket(c) !== 'fun') return false;
      else if (cat === 'RADIO' && !isRadioCh(c) && c.categoryEn !== 'music') return false;
      if (q) {
        return c.name.toLowerCase().indexOf(q) !== -1 || String(c.number) === q || (c.title && c.title.toLowerCase().indexOf(q) !== -1);
      }
      return true;
    });
    mv.pickerIdx = clamp(mv.pickerIdx, 0, Math.max(0, mv.pickerList.length - 1));
    el.mvPickerGrid.innerHTML = mv.pickerList.map(function (c, i) {
      var on = c.id === curId ? ' on' : '';
      var foc = i === mv.pickerIdx ? ' focused' : '';
      return '<div class="mv-pick-card' + on + foc + '" data-picki="' + i + '"><span class="mv-pick-num">' + c.number + '</span><span class="mv-pick-name">' + esc(c.name) + '</span></div>';
    }).join('');
  }

  function selectMultiViewChannel(ch) {
    if (!ch) return;
    var idx = mv.pickerCell;
    var cell = mv.cells[idx];
    if (!cell) return;
    cell.id = ch.id;
    cell.ch = ch;
    cell.candIdx = 0;
    cell.candidates = mvCandidatesForCell(ch, idx);
    var cellNode = el.mvGrid.children[idx];
    if (cellNode) {
      var numEl = cellNode.querySelector('.mv-cell-num');
      var nameEl = cellNode.querySelector('.mv-cell-name');
      if (numEl) numEl.textContent = ch.number;
      if (nameEl) nameEl.textContent = ch.name;
    }
    closeMultiViewPicker();
    playMultiViewCell(idx);
  }

  function multiViewKey(code, e) {
    if (mv.pickerOpen) {
      if (isBack(code)) { closeMultiViewPicker(); return; }
      var n = mv.pickerList.length;
      if (!n) return;
      if (code === 37) mv.pickerIdx = clamp(mv.pickerIdx + 1, 0, n - 1);
      else if (code === 39) mv.pickerIdx = clamp(mv.pickerIdx - 1, 0, n - 1);
      else if (code === 40) mv.pickerIdx = clamp(mv.pickerIdx + 4, 0, n - 1);
      else if (code === 38) mv.pickerIdx = clamp(mv.pickerIdx - 4, 0, n - 1);
      else if (code === 13) { selectMultiViewChannel(mv.pickerList[mv.pickerIdx]); return; }
      var cards = el.mvPickerGrid.querySelectorAll('.mv-pick-card');
      for (var i = 0; i < cards.length; i++) toggleClass(cards[i], 'focused', i === mv.pickerIdx);
      if (cards[mv.pickerIdx]) reveal(cards[mv.pickerIdx]);
      return;
    }
    if (isBack(code)) { closeMultiView(); return; }
    if (code === 37) mv.focusIdx = (mv.focusIdx + 1) % mv.layout;
    else if (code === 39) mv.focusIdx = (mv.focusIdx - 1 + mv.layout) % mv.layout;
    else if (code === 38 || code === 40) {
      mv.activeAudio = mv.focusIdx;
      updateMultiViewAudio();
      toast('🔊 שמע פעיל: ' + (mv.cells[mv.focusIdx] && mv.cells[mv.focusIdx].ch ? mv.cells[mv.focusIdx].ch.name : ''));
      return;
    } else if (code === 13) {
      openMultiViewPicker(mv.focusIdx);
      return;
    }
    for (var c = 0; c < el.mvGrid.children.length; c++) {
      toggleClass(el.mvGrid.children[c], 'focused', c === mv.focusIdx);
    }
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
    if (code === 23 || code === 66 || code === 160 || code === 96) code = 13;
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
    if (code === 23 || code === 66 || code === 160 || code === 96) code = 13;
    if (state.saverOn) { resetSaver(); if (e.preventDefault) e.preventDefault(); return; }
    resetSaver();

    // text fields keep their keys (search box, API keys)
    var t = e.target;
    if (typing(t)) {
      if (state.overlay === 'multiview') {
        if (code === 40 || code === 13) { if (e.preventDefault) e.preventDefault(); t.blur(); }
        else if (isBack(code) && code !== 8) { if (e.preventDefault) e.preventDefault(); t.blur(); closeMultiViewPicker(); }
        return;
      }
      if (state.overlay === 'settings') { settingsKey(code, e); return; }
      if (t.id === 'atv-q') { atvSearchKey(code, e); return; }
      if (t === el.searchInput) {
        if (code === 40 || code === 13) { e.preventDefault(); t.blur(); focusGrid(0); }
        else if (isBack(code) && code !== 8) { e.preventDefault(); t.blur(); focusGrid(state.gridIdx); }
        else if (code === 8 && !t.value) { e.preventDefault(); t.blur(); focusGrid(state.gridIdx); }
        return;
      }
      return;
    }

    // OK / Enter: on TV remote / wheel click, execute shortOk immediately without delay or stuck states
    if (code === 13) {
      if (e.preventDefault) e.preventDefault();
      if (TVUI) {
        shortOk();
        return;
      }
      if (!state.okUpSeen) { shortOk(); return; }
      if (state.okDown) {
        state.okDown = false;
        shortOk();
        return;
      }
      state.okDown = true;
      state.okLong = false;
      if (state.okTimer) clearTimeout(state.okTimer);
      state.okTimer = setTimeout(function () {
        state.okTimer = null;
        state.okDown = false;
        state.okLong = true;
        longOk();
      }, 550);
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
      if ((code === 71 || code === 458) && Guide) { Guide.open(); return; } // G / the remote's Guide key
      if (code === 79 || code === 93) { runAction('menu'); return; } // O / context-menu key
      if (code === 191 && !state.overlay) { if (e.preventDefault) e.preventDefault(); focusSearch(); return; } // '/'
    }

    if ([37, 38, 39, 40].indexOf(code) !== -1 && e.preventDefault) e.preventDefault();
    if (isBack(code) && e.preventDefault) e.preventDefault();

    switch (state.overlay) {
      case 'tv-profiles': tvProfilesKey(code); return;
      case 'multiview': multiViewKey(code, e); return;
      case 'settings': settingsKey(code, e); return;
      case 'dialog': dialogKey(code); return;
      case 'menu': menuKey(code); return;
      case 'drawer': drawerKey(code); return;
      case 'player': playerKey(code); return;
      case 'tvguide': if (Guide) Guide.key(code); return;
      default: if (TVUI) atvKey(code); else guideKey(code);
    }
  }

  function zapOrPage(dir) {
    if (state.overlay === 'player') { zap(dir); return; }
    if (state.overlay === 'tvguide') { if (Guide) Guide.page(dir > 0 ? -1 : 1); return; }
    if (!state.overlay && state.zone === 'atv') { atvKey(dir > 0 ? 38 : 40); return; }
    if (!state.overlay && state.zone === 'grid') focusGrid(state.gridIdx + (dir > 0 ? -1 : 1) * state.cols * 3);
  }

  function shortOk() {
    if (TVUI && state.overlay === 'player' && state.tvEpgOpen) {
      tvEpgOk();
      return;
    }
    switch (state.overlay) {
      case 'tv-profiles': tvProfilesOk(); return;
      case 'multiview': multiViewKey(13, { preventDefault: function () {} }); return;
      case 'settings': settingsKey(13, { preventDefault: function () {} }); return;
      case 'dialog': pressDialog(state.dialogIdx); return;
      case 'menu': runMenuItem(state.menuIdx); return;
      case 'drawer': var i = state.drawerIdx; closeDrawer(true); playIndex(i); return;
      case 'tvguide': if (Guide) Guide.key(13); return;
      case 'player':
        if (state.zone === 'perror') { errorButtons()[state.perrIdx || 0].click(); return; }
        if (el.osd.classList.contains('off')) showOsd(); else if (Guide) Guide.ok(); else playerMenu();
        return;
    }
    if (state.zone === 'atv') { atvOk(); return; }
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
    if (state.zone === 'atv') { if (atv.zone === 'rows') runAction(S.longOk); else atvOk(); return; }
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
    var wasOff = el.osd.classList.contains('off');
    var back = isBack(code);
    if (!back || state.zone === 'perror') showOsd();
    if (state.zone === 'perror') {
      var b = errorButtons();
      if (code === 37) state.perrIdx = clamp((state.perrIdx || 0) + 1, 0, b.length - 1);
      else if (code === 39) state.perrIdx = clamp((state.perrIdx || 0) - 1, 0, b.length - 1);
      else if (code === 38 || code === 40) { zap(code === 40 ? 1 : -1); return; }
      else if (back) { closePlayer(false); return; }
      markFocus();
      return;
    }
    if (Guide && Guide.playerKey(code, wasOff)) { if (back) showOsd(); return; }
    if (back) {
      var now = Date.now();
      if (!PHONE && now - (state.playerBackAt || 0) > 2500) {
        state.playerBackAt = now;
        if (!wasOff) {
          if (state.osdTimer) clearTimeout(state.osdTimer);
          if (Guide) Guide.onHide();
          el.osd.classList.add('off');
          el.player.classList.add('cursor-hidden');
        }
        toast('לחצו שוב BACK לחזרה למסך הראשי');
        return;
      }
      state.playerBackAt = 0;
      closePlayer(true);
      return;
    }
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
    if (state.dialog && state.dialog.scroll && (code === 38 || code === 40)) { el.dialogBox.scrollTop += (code === 40 ? 1 : -1) * atvRem(14); return; }
    if (code === 37 || code === 40) state.dialogIdx = clamp(state.dialogIdx + 1, 0, n - 1);
    else if (code === 39 || code === 38) state.dialogIdx = clamp(state.dialogIdx - 1, 0, n - 1);
    else if (isBack(code)) { closeDialog(); return; }
    markFocus();
  }
})();

