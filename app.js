/**
 * ISRAEL TV PLUS - LG webOS Smart TV & Web Application Engine
 * Optimized for LG OLED, Light Mode, Magic Remote, touch screens and 60fps hardware acceleration.
 * Hebrew live dubbing for foreign channels: see dub.js.
 */

(function () {
  'use strict';

  // APPLICATION STATE
  const state = {
    allChannels: [],
    filteredChannels: [],
    categories: [],
    activeCategory: 'ALL',
    searchQuery: '',
    favorites: [],
    recents: [], // last watched channel ids, newest first
    getAppOpen: false,
    theme: 'oled', // 'oled' | 'light'

    // Focus & Navigation
    currentSection: 'grid', // 'grid' | 'sidebar' | 'search' | 'player' | 'drawer' | 'exit_modal' | 'settings'
    sectionBeforeSettings: 'grid',
    focusedGridIdx: 0,
    focusedCategoryIdx: 0,
    focusedDrawerIdx: 0,
    focusedExitBtn: 0, // 0 = Cancel, 1 = Exit

    // Playback
    playingChannelIndex: -1,
    playingChannel: null,
    isRadio: false,
    hlsInstance: null,
    osdTimeout: null,
    toastTimeout: null,
    screensaverTimeout: null,
    screensaverActive: false,
    drawerOpen: false,
    exitModalOpen: false,
    settingsOpen: false,

    // OLED & Retro Remaster Modes
    oledModeIdx: 0,
    retroModeIdx: 0,

    // Direct Number Typing
    typedDigits: '',
    digitTimeout: null,
    lastBackPressTime: 0
  };

  const Dub = window.HebrewDub;

  // PLATFORM: the native apps (Android / Android TV, Windows, LG webOS, Samsung Tizen) load the app from local
  // files, so http streams and streams without CORS play there. The https web site can't play those.
  const UA = navigator.userAgent;
  const PLATFORM = new URLSearchParams(location.search).get('platform') || (window.AndroidApp ? 'android'
    : (window.chrome && window.chrome.webview) ? 'windows'
    : (window.webOS || window.PalmSystem || /Web0S|webOS/i.test(UA)) ? 'webos'
    : (window.tizen || /Tizen/i.test(UA)) ? 'tizen'
    : location.protocol === 'https:' ? 'web' : 'local'); // ?platform=web previews the web site's view locally
  const IS_WEB = PLATFORM === 'web';
  // Android TV boxes report a ~960px wide screen (density 2): lay the guide out at 1600px like on LG TVs.
  if (/AndroidTV/.test(UA)) {
    const vp = document.querySelector('meta[name="viewport"]');
    if (vp) vp.setAttribute('content', 'width=1600, user-scalable=no');
  }
  const RELEASES = 'https://github.com/dor2500/israel-tv-plus/releases/latest';
  const MAX_RECENTS = 12;

  const isApp = (ch) => ch && ch.kind === 'app';
  const isRadioCh = (ch) => ch && (ch.kind === 'radio' || ch.categoryEn === 'radio');
  /** True when this channel can't play inside the https web site (it works in the apps). */
  /** The stream to play here: the web site uses an https copy (webUrl) where the main source is http-only. */
  const streamUrl = (ch) => (IS_WEB && ch.webUrl) || ch.url;
  const appOnly = (ch) => IS_WEB && ch && !isApp(ch) && (ch.webOk === false || /^http:/i.test(streamUrl(ch)));

  // OLED PRESETS
  const OLED_MODES = [
    { id: 'cinema', label: 'קולנוע שחור מושלם', filterClass: 'oled-filter-cinema', desc: 'True Black' },
    { id: 'vivid', label: 'צבעים עשירים', filterClass: 'oled-filter-vivid', desc: 'צבעוניות חיה מוגברת' },
    { id: 'sports', label: 'ספורט Boost', filterClass: 'oled-filter-sports', desc: 'תנועה מהירה וחדות' },
    { id: 'natural', label: 'טבעי ומאוזן', filterClass: 'oled-filter-natural', desc: 'צבעי מקור נאמנים' }
  ];

  // RETRO REMASTER (SD / VINTAGE 4:3 UPSCALE) PRESETS
  const RETRO_MODES = [
    { id: 'off', label: 'חידוד: כבוי', sharpen: false, aspectClass: 'aspect-fit', desc: 'ללא שינוי' },
    { id: 'pillarbox', label: 'חידוד רטרו: 4:3 מקורי', sharpen: true, aspectClass: 'aspect-pillarbox', desc: 'פילטר חידוד + יחס 4:3 ללא עיוות' },
    { id: 'zoom', label: 'חידוד רטרו: 16:9 מילוי', sharpen: true, aspectClass: 'aspect-zoom', desc: 'פילטר חידוד + חיתוך חכם 16:9' },
    { id: 'stretch', label: 'חידוד רטרו: מתיחה', sharpen: true, aspectClass: 'aspect-stretch', desc: 'פילטר חידוד + מתיחה מלאה' }
  ];

  // CATEGORY ICONS (matched by keyword)
  const CATEGORY_ICONS = [
    [/אפליקציות|מנויים/, '📲'], [/רדיו/, '📻'],
    [/ישראל/, '📡'], [/ילדים/, '🧸'], [/סרטים|סדרות/, '🎬'], [/ספורט/, '⚽'], [/מוזיקה/, '🎵'],
    [/לייף|בידור/, '🏖️'], [/דוקו|טבע/, '🌍'], [/חדשות|News/i, '📰'], [/מסורת|יהדות/, '🕍'], [/דת|אמונה/, '🙏'], [/בינלאומי|World/i, '🌐']
  ];

  // DOM ELEMENTS CACHE
  let el = {};

  // INITIALIZATION
  window.addEventListener('DOMContentLoaded', initApp);

  function initApp() {
    cacheDomElements();
    loadTheme();
    loadFavorites();
    loadRecents();
    document.body.classList.add('platform-' + PLATFORM);
    loadChannels();
    setupNativeBridge();
    setupGetApp();
    setupClock();
    setupOledMode();
    setupRetroMode();
    setupWebPlayerControls();
    setupHeroButtons();
    setupDub();
    setupSettings();
    setupKeyListeners();
    setupMouseListeners();
    resetScreensaverTimer();
    window.addEventListener('resize', () => highlightFocusedItem(true));
    // Mouse / touch scrolling: add the next page of cards when nearing the bottom.
    const moreOnScroll = () => {
      const g = el.channelsGrid;
      const nearGrid = g.scrollHeight - g.scrollTop - g.clientHeight < 800;
      const nearPage = document.documentElement.scrollHeight - window.scrollY - window.innerHeight < 800;
      if (nearGrid || nearPage) ensureGridRendered(gridRendered);
    };
    el.channelsGrid.addEventListener('scroll', moreOnScroll, { passive: true });
    window.addEventListener('scroll', moreOnScroll, { passive: true });

    // Initial Focus on the first channel card
    setFocus('grid', 0);
  }

  function $(id) { return document.getElementById(id); }

  function cacheDomElements() {
    el = {
      app: $('app'),
      search: $('channel-search'),
      channelsGrid: $('channels-grid'),
      categoryList: $('category-list'),
      channelsCountBadge: $('channels-count-badge'),
      currentCategoryTitle: $('current-category-title'),
      categoryMetaText: $('category-meta-text'),
      liveClock: $('live-clock'),

      // Theme Toggle
      btnThemeToggle: $('btn-theme-toggle'),
      btnThemeToggle2: $('btn-theme-toggle-2'),
      themeBtnIcon: $('theme-btn-icon'),
      themeBtnLabel: $('theme-btn-label'),

      // Hero Spotlight
      heroSpotlight: $('hero-spotlight'),
      heroGlow: $('hero-glow'),
      heroNumber: $('hero-number'),
      heroCategory: $('hero-category'),
      heroBadgeTag: $('hero-badge-tag'),
      heroLangTag: $('hero-lang-tag'),
      heroFavTag: $('hero-fav-tag'),
      heroTitle: $('hero-title'),
      heroDesc: $('hero-desc'),
      heroLogo: $('hero-logo'),
      heroMonogram: $('hero-monogram'),
      heroBtnPlay: $('hero-btn-play'),
      heroBtnDub: $('hero-btn-dub'),
      heroBtnFav: $('hero-btn-fav'),
      heroNote: $('hero-note'),

      // "Get the app" panel
      btnGetApp: $('btn-get-app'),
      getAppModal: $('getapp-modal'),
      getAppTitle: $('getapp-title'),
      getAppDesc: $('getapp-desc'),
      btnCloseGetApp: $('btn-close-getapp'),

      // Picture modes
      btnOled: $('btn-oled-mode'),
      btnRetro: $('btn-retro-mode'),
      oledLabel: $('oled-mode-label'),
      retroLabel: $('retro-mode-label'),

      // Dubbing
      btnDubToggle: $('btn-dub-toggle'),
      dubToggleLabel: $('dub-toggle-label'),
      osdDubStatus: $('osd-dub-status'),
      osdDubText: $('osd-dub-text'),
      webBtnDub: $('web-btn-dub'),
      webDubLabel: $('web-dub-label'),

      // Settings
      btnOpenSettings: $('btn-open-settings'),
      settingsModal: $('settings-modal'),
      btnCloseSettings: $('btn-close-settings'),

      // Direct Number HUD
      channelNumberHud: $('channel-number-hud'),
      hudDigits: $('hud-digits'),
      hudChannelName: $('hud-channel-name'),

      // Player Container
      playerContainer: $('player-container'),
      btnFloatingClose: $('btn-floating-close-player'),
      video: $('tv-video'),
      radio: $('radio-audio'),
      spinner: $('player-spinner'),
      spinnerText: $('spinner-text'),
      playerRadioBackdrop: $('player-radio-backdrop'),
      radioBackdropLogo: $('radio-backdrop-logo'),
      radioBackdropName: $('radio-backdrop-name'),
      playerErrorOverlay: $('player-error-overlay'),
      playerErrorTitle: $('player-error-title'),
      playerErrorDesc: $('player-error-desc'),
      btnErrorBack: $('btn-error-back'),
      btnErrorDirectLink: $('btn-error-direct-link'),
      osd: $('player-osd'),
      osdNumber: $('osd-number'),
      osdLogo: $('osd-logo'),
      osdMonogram: $('osd-monogram'),
      osdName: $('osd-name'),
      osdCategory: $('osd-category'),
      osdRetroStatus: $('osd-retro-status'),
      osdAspectTag: $('osd-aspect-tag'),
      osdClock: $('osd-clock'),
      osdOledStatus: $('osd-oled-status'),
      osdAspectStatus: $('osd-aspect-status'),
      osdQuality: $('osd-quality'),

      // Web Player Actions
      webBtnBack: $('web-btn-back'),
      webBtnPrev: $('web-btn-prev'),
      webBtnNext: $('web-btn-next'),
      webBtnPlayPause: $('web-btn-playpause'),
      webPlayIcon: $('web-play-icon'),
      webBtnMute: $('web-btn-mute'),
      webMuteIcon: $('web-mute-icon'),
      webBtnAspect: $('web-btn-aspect'),
      webBtnSettings: $('web-btn-settings'),
      webBtnFullscreen: $('web-btn-fullscreen'),

      // Drawer
      quickDrawer: $('quick-drawer'),
      drawerChannelList: $('drawer-channel-list'),

      // Toast
      toast: $('player-toast'),
      toastMessage: $('toast-message'),

      // Screensaver
      screensaver: $('oled-screensaver'),
      screensaverClock: $('screensaver-clock'),

      // Exit Modal
      exitModal: $('exit-modal'),
      btnCancelExit: $('btn-cancel-exit'),
      btnConfirmExit: $('btn-confirm-exit')
    };
  }

  // HELPERS
  function escapeHtml(s) {
    return String(s == null ? '' : s).replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  }

  function hueOf(id) {
    let h = 7;
    for (let i = 0; i < id.length; i++) h = (h * 31 + id.charCodeAt(i)) % 360;
    return h;
  }

  /** Short brand text for channels without a logo (prefers the Latin brand name). */
  function monogramText(ch) {
    const latin = (ch.name.match(/[A-Za-z0-9][A-Za-z0-9!+&'.:-]*(?:\s+[A-Za-z0-9!+&'.:-]+)*/g) || [])
      .map(s => s.replace(/\b(HD|Live|TV)\b/g, '').trim())
      .filter(s => /[A-Za-z]{2,}/.test(s))
      .sort((a, b) => b.length - a.length);
    if (latin.length) return latin[0].slice(0, 18);
    return ch.name.replace(/\bHD\b|\(.*?\)/g, '').trim().split(/\s+/).slice(0, 2).join(' ');
  }

  function fillMonogram(node, ch) {
    const h = hueOf(ch.id);
    node.textContent = monogramText(ch);
    node.style.background = `linear-gradient(135deg, hsl(${h} 70% 42%), hsl(${(h + 40) % 360} 75% 30%))`;
  }

  /** Shows the logo image, or a monogram tile when there is none / it fails to load. */
  function setLogo(img, mono, ch) {
    if (!img || !mono) return;
    fillMonogram(mono, ch);
    const showMono = () => { img.classList.add('hidden'); mono.classList.remove('hidden'); };
    if (!ch.logo) { img.removeAttribute('src'); showMono(); return; }
    img.onerror = showMono;
    img.onload = () => { img.classList.remove('hidden'); mono.classList.add('hidden'); };
    img.classList.remove('hidden');
    mono.classList.add('hidden');
    img.referrerPolicy = 'no-referrer'; // remote logo hosts (imgur) refuse some referrers
    img.src = ch.logo;
  }

  function splitCategoryName(name) {
    const m = name.match(/^(.*?)\s*\(([^)]*[A-Za-z][^)]*)\)\s*$/);
    const parts = m ? { main: m[1], sub: m[2] } : { main: name, sub: '' };
    parts.main = parts.main.replace(/\s*-\s*שידור חי\s*$/, '').replace(/\s*-\s*$/, '');
    return parts;
  }

  function categoryIcon(name) {
    for (const [re, icon] of CATEGORY_ICONS) if (re.test(name)) return icon;
    return '📺';
  }

  function media() {
    return state.isRadio ? el.radio : el.video;
  }

  // THEME MANAGEMENT (OLED PURE BLACK vs LIGHT MODE)
  function loadTheme() {
    try {
      const saved = localStorage.getItem('israel_tv_theme');
      state.theme = saved === 'light' ? 'light' : 'oled';
    } catch (e) {
      state.theme = 'oled';
    }
    applyTheme();

    if (el.btnThemeToggle) el.btnThemeToggle.addEventListener('click', toggleTheme);
    if (el.btnThemeToggle2) el.btnThemeToggle2.addEventListener('click', toggleTheme);
  }

  function toggleTheme() {
    state.theme = state.theme === 'oled' ? 'light' : 'oled';
    try {
      localStorage.setItem('israel_tv_theme', state.theme);
    } catch (e) {}
    applyTheme();
    showToast(state.theme === 'light' ? '☀️ מצב יום' : '🌙 מצב לילה (OLED)');
  }

  function applyTheme() {
    const light = state.theme === 'light';
    document.body.classList.toggle('light-theme', light);
    document.body.classList.toggle('oled-theme', !light);
    if (el.themeBtnIcon) el.themeBtnIcon.textContent = light ? '🌙' : '☀️';
    if (el.themeBtnLabel) el.themeBtnLabel.textContent = light ? 'מצב לילה' : 'מצב יום';
    const meta = document.querySelector('meta[name="theme-color"]');
    if (meta) meta.setAttribute('content', light ? '#f1f5f9' : '#05070d');
  }

  // FAVORITES
  function loadFavorites() {
    try {
      const saved = localStorage.getItem('israel_tv_favorites');
      state.favorites = saved ? JSON.parse(saved) : [];
    } catch (e) {
      state.favorites = [];
    }
  }

  function saveFavorites() {
    try {
      localStorage.setItem('israel_tv_favorites', JSON.stringify(state.favorites));
    } catch (e) {}
  }

  function toggleFavorite(channelId) {
    const idx = state.favorites.indexOf(channelId);
    let isFav = false;
    if (idx !== -1) {
      state.favorites.splice(idx, 1);
      showToast('הוסר ממועדפים');
    } else {
      state.favorites.push(channelId);
      isFav = true;
      showToast('נוסף למועדפים ⭐');
    }
    saveFavorites();

    buildCategories();
    if (state.activeCategory === 'FAVORITES') {
      applyFilter();
    } else {
      updateCardFavoriteIcon(channelId, isFav);
      updateHeroSpotlight(state.filteredChannels[state.focusedGridIdx]);
    }
  }

  function updateCardFavoriteIcon(channelId, isFav) {
    el.channelsGrid.querySelectorAll('.channel-card').forEach(card => {
      if (card.dataset.id === channelId) card.classList.toggle('is-fav', isFav);
    });
  }

  // RECENTLY WATCHED
  function loadRecents() {
    try {
      const saved = JSON.parse(localStorage.getItem('israel_tv_recents') || '[]');
      state.recents = Array.isArray(saved) ? saved.slice(0, MAX_RECENTS) : [];
    } catch (e) {
      state.recents = [];
    }
  }

  function addRecent(ch) {
    const had = state.recents.length;
    state.recents = [ch.id, ...state.recents.filter(id => id !== ch.id)].slice(0, MAX_RECENTS);
    try { localStorage.setItem('israel_tv_recents', JSON.stringify(state.recents)); } catch (e) {}
    // The category list only needs a rebuild when "recent" appears for the first time.
    if (!had) { buildCategories(); }
    else {
      const cat = state.categories.find(c => c.id === 'RECENT');
      if (cat) cat.count = state.recents.length;
    }
  }

  // NATIVE APPS BRIDGE (Android / Windows) + opening the TV-service apps
  function setupNativeBridge() {
    // The Android / Windows shells send remote keys (Back, colour keys, CH+/-) through here.
    window.__itvKey = (code) => handleKeyDown({
      keyCode: code, which: code, target: document.body, preventDefault() {}, stopPropagation() {}
    });
    // Samsung remotes only send these keys to apps that ask for them.
    if (PLATFORM === 'tizen' && window.tizen && window.tizen.tvinputdevice) {
      ['ColorF0Red', 'ColorF1Green', 'ColorF2Yellow', 'ColorF3Blue', 'ChannelUp', 'ChannelDown', 'MediaPlayPause',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'].forEach(k => {
        try { window.tizen.tvinputdevice.registerKey(k); } catch (e) {}
      });
    }
  }

  function webosLaunch(id, params) {
    try {
      const bridge = new window.PalmServiceBridge();
      bridge.call('luna://com.webos.applicationManager/launch', JSON.stringify({ id, params: params || {} }));
      return true;
    } catch (e) {
      return false;
    }
  }

  /** Opens a TV-service app (Partner / Cellcom / STING / yes / HOT ...): the installed app on Android, else its site. */
  function openServiceApp(ch) {
    if (PLATFORM === 'android' && window.AndroidApp.openApp) {
      window.AndroidApp.openApp(ch.androidTv || '', ch.android || '', ch.site || ch.url);
      showToast(`📲 פותח את ${ch.name}...`);
      return;
    }
    if (PLATFORM === 'windows') {
      window.chrome.webview.postMessage(JSON.stringify({ open: ch.site || ch.url }));
      showToast(`🌐 פותח את ${ch.name} בדפדפן`);
      return;
    }
    if (PLATFORM === 'webos' && webosLaunch('com.webos.app.browser', { target: ch.site || ch.url })) {
      showToast(`🌐 פותח את ${ch.name} בדפדפן הטלוויזיה`);
      return;
    }
    window.open(ch.site || ch.url, '_blank', 'noopener');
  }

  // "GET THE APP" PANEL (web site only: downloads for every platform)
  function setupGetApp() {
    if (!el.getAppModal) return;
    el.getAppModal.querySelectorAll('a[data-asset]').forEach(a => {
      a.href = a.dataset.asset ? `${RELEASES}/download/${a.dataset.asset}` : RELEASES;
    });
    if (el.btnGetApp) {
      el.btnGetApp.classList.toggle('hidden', PLATFORM !== 'web' && PLATFORM !== 'local');
      el.btnGetApp.addEventListener('click', () => openGetApp());
    }
    if (el.btnCloseGetApp) el.btnCloseGetApp.addEventListener('click', closeGetApp);
    el.getAppModal.addEventListener('click', (e) => { if (e.target === el.getAppModal) closeGetApp(); });
  }

  function openGetApp(ch) {
    state.getAppOpen = true;
    el.getAppTitle.textContent = ch ? `"${ch.name}" זמין באפליקציה` : 'ישראל TV+ לכל המכשירים';
    el.getAppDesc.textContent = ch
      ? 'הערוץ משדר בפרוטוקול HTTP, ודפדפנים חוסמים שידור כזה מתוך אתר מאובטח. באפליקציה הוא פועל – וגם כל שאר הערוצים.'
      : 'באפליקציות פועלים כל הערוצים (גם אלה שהדפדפן חוסם), עם שלט רחוק, מועדפים ודיבוב לעברית.';
    el.getAppModal.classList.remove('hidden');
    const first = el.getAppModal.querySelector('.getapp-item');
    if (first) first.focus();
  }

  function closeGetApp() {
    state.getAppOpen = false;
    el.getAppModal.classList.add('hidden');
  }

  // CLOCK
  function setupClock() {
    function update() {
      const now = new Date();
      const shortStr = now.toLocaleTimeString('he-IL', { hour: '2-digit', minute: '2-digit' });
      if (el.liveClock) el.liveClock.textContent = shortStr;
      if (el.osdClock) el.osdClock.textContent = shortStr;
      if (el.screensaverClock) el.screensaverClock.textContent = shortStr;
    }
    update();
    setInterval(update, 1000);
  }

  // OLED MODES
  function setupOledMode() {
    try {
      const saved = localStorage.getItem('israel_tv_oled_mode');
      if (saved) {
        const found = OLED_MODES.findIndex(m => m.id === saved);
        if (found !== -1) state.oledModeIdx = found;
      }
    } catch (e) {}

    applyOledMode();
    if (el.btnOled) el.btnOled.addEventListener('click', cycleOledMode);
  }

  function cycleOledMode() {
    state.oledModeIdx = (state.oledModeIdx + 1) % OLED_MODES.length;
    applyOledMode();
    const current = OLED_MODES[state.oledModeIdx];
    try {
      localStorage.setItem('israel_tv_oled_mode', current.id);
    } catch (e) {}
    showToast(`✨ מצב תמונה: ${current.label} (${current.desc})`);
  }

  function applyOledMode() {
    const current = OLED_MODES[state.oledModeIdx];
    if (el.oledLabel) el.oledLabel.textContent = `תמונה: ${current.label}`;
    if (el.osdOledStatus) el.osdOledStatus.textContent = current.label;

    if (el.video) {
      OLED_MODES.forEach(m => el.video.classList.remove(m.filterClass));
      el.video.classList.add(current.filterClass);
    }
  }

  // RETRO REMASTER (SD UPSCALE & ASPECT RATIO)
  function setupRetroMode() {
    try {
      const saved = localStorage.getItem('israel_tv_retro_mode');
      if (saved) {
        const found = RETRO_MODES.findIndex(m => m.id === saved);
        if (found !== -1) state.retroModeIdx = found;
      }
    } catch (e) {}

    applyRetroMode();
    if (el.btnRetro) el.btnRetro.addEventListener('click', cycleRetroMode);
  }

  function cycleRetroMode() {
    state.retroModeIdx = (state.retroModeIdx + 1) % RETRO_MODES.length;
    applyRetroMode();
    const current = RETRO_MODES[state.retroModeIdx];
    try {
      localStorage.setItem('israel_tv_retro_mode', current.id);
    } catch (e) {}
    showToast(`🎞️ ${current.label} - ${current.desc}`);
  }

  function applyRetroMode() {
    const current = RETRO_MODES[state.retroModeIdx];
    if (el.retroLabel) el.retroLabel.textContent = current.label;
    if (el.osdAspectStatus) el.osdAspectStatus.textContent = current.label.replace('חידוד רטרו: ', '');

    if (el.video) {
      RETRO_MODES.forEach(m => el.video.classList.remove(m.aspectClass));
      el.video.classList.add(current.aspectClass);
      el.video.classList.toggle('retro-sharpen-active', current.sharpen);
      if (el.osdRetroStatus) el.osdRetroStatus.classList.toggle('hidden', !current.sharpen);
      if (el.osdAspectTag) {
        el.osdAspectTag.textContent = current.id === 'pillarbox' ? '4:3' : (current.id === 'zoom' ? '16:9 Zoom' : '16:9');
      }
    }
  }

  // SCREENSAVER
  function resetScreensaverTimer() {
    if (state.screensaverActive) wakeScreensaver();
    if (state.screensaverTimeout) clearTimeout(state.screensaverTimeout);

    state.screensaverTimeout = setTimeout(() => {
      const m = media();
      if (state.currentSection !== 'player' || (m && m.paused)) {
        startScreensaver();
      }
    }, 120000);
  }

  function startScreensaver() {
    state.screensaverActive = true;
    el.screensaver.classList.remove('hidden');
  }

  function wakeScreensaver() {
    state.screensaverActive = false;
    el.screensaver.classList.add('hidden');
    resetScreensaverTimer();
  }

  // CHANNELS LOADING & CATEGORIES
  function loadChannels() {
    // On the https web site, imported channels that a browser can't play (http / no CORS) are hidden;
    // the webOS app (file://) shows them all.
    const inBrowser = location.protocol === 'https:';
    const raw = (window.CHANNELS_DATA || []).filter(c => !(inBrowser && c.web === false));
    state.allChannels = raw.map((c, i) => ({
      ...c,
      number: c.number || (i + 1),
      category: c.category || 'ערוצים',
      desc: c.desc || 'שידור חי באיכות HD',
      lang: c.lang || 'he'
    }));

    buildCategories();
    applyFilter();
  }

  function buildCategories() {
    const catMap = {};
    state.allChannels.forEach(c => {
      catMap[c.category] = (catMap[c.category] || 0) + 1;
    });

    const list = [
      { id: 'ALL', name: 'כל הערוצים', icon: '📺', count: state.allChannels.length }
    ];

    if (state.favorites.length > 0) {
      list.push({ id: 'FAVORITES', name: 'מועדפים', icon: '⭐', count: state.favorites.length });
    }
    if (state.recents.length > 0) {
      list.push({ id: 'RECENT', name: 'צפית לאחרונה', icon: '🕘', count: state.recents.length });
    }

    const foreignCount = state.allChannels.filter(c => Dub && Dub.isForeign(c)).length;
    if (foreignCount) {
      list.push({ id: 'DUB', name: 'דיבוב לעברית', sub: 'ערוצים זרים', icon: '🎙️', count: foreignCount, special: true });
    }

    // Ordered categories
    const priority = [
      'ערוצי ישראל - שידור חי',
      'רדיו ישראלי (Israeli Radio)',
      'מנויים ואפליקציות (TV Apps)',
      'ילדים ונוער (בעברית)',
      'חדשות מהעולם (News)',
      'סרטים וסדרות (Movies & Series)',
      'ספורט (Sports)',
      'ילדים מהעולם (Kids World)',
      'מוזיקה (Music TV)',
      'מוזיקה ורדיו חי (Music & Radio)',
      'טבע ודוקו (Documentary & Nature)',
      'לייף סטייל ובידור',
      'מסורת וקהילה',
      'דת ואמונה (Religion)',
      'ערוצים בינלאומיים (World)'
    ];

    const pushCat = (cat) => {
      const parts = splitCategoryName(cat);
      list.push({ id: cat, name: parts.main, sub: parts.sub, icon: categoryIcon(cat), count: catMap[cat] });
      delete catMap[cat];
    };
    priority.forEach(cat => { if (catMap[cat]) pushCat(cat); });
    Object.keys(catMap).forEach(pushCat);

    state.categories = list;
    renderSidebar();
  }

  function renderSidebar() {
    el.categoryList.innerHTML = '';
    state.categories.forEach((cat, idx) => {
      const item = document.createElement('div');
      item.className = `category-item ${cat.id === state.activeCategory ? 'active' : ''} ${cat.special ? 'special' : ''}`;
      item.dataset.index = idx;
      item.dataset.id = cat.id;

      item.innerHTML = `
        <span class="category-icon">${cat.icon}</span>
        <span class="category-name">
          <span class="category-main">${escapeHtml(cat.name)}</span>
          ${cat.sub ? `<span class="category-sub" dir="auto">${escapeHtml(cat.sub)}</span>` : ''}
        </span>
        <span class="category-count">${cat.count}</span>
      `;

      item.addEventListener('click', () => {
        selectCategory(cat.id, idx);
        setFocus('grid', 0);
      });

      el.categoryList.appendChild(item);
    });
  }

  function selectCategory(catId, idx) {
    state.activeCategory = catId;
    state.focusedCategoryIdx = idx;
    state.focusedGridIdx = 0;
    renderSidebar();
    applyFilter();
    el.channelsGrid.scrollTop = 0;
  }

  function applyFilter() {
    const q = state.searchQuery.trim().toLowerCase();
    let result = state.allChannels;

    if (state.activeCategory === 'FAVORITES') {
      result = result.filter(c => state.favorites.includes(c.id));
    } else if (state.activeCategory === 'RECENT') {
      const byId = new Map(result.map(c => [c.id, c]));
      result = state.recents.map(id => byId.get(id)).filter(Boolean);
    } else if (state.activeCategory === 'DUB') {
      result = result.filter(c => Dub && Dub.isForeign(c));
    } else if (state.activeCategory !== 'ALL') {
      result = result.filter(c => c.category === state.activeCategory);
    }

    if (q) {
      result = result.filter(c =>
        c.name.toLowerCase().includes(q) ||
        (c.title && c.title.toLowerCase().includes(q)) ||
        (c.desc && c.desc.toLowerCase().includes(q)) ||
        (c.number && c.number.toString() === q)
      );
    }

    state.filteredChannels = result;

    const currentCatObj = state.categories.find(c => c.id === state.activeCategory);
    el.currentCategoryTitle.textContent = currentCatObj ? `${currentCatObj.icon} ${currentCatObj.name}` : 'ערוצים';
    el.categoryMetaText.textContent = `${result.length} ערוצים`;
    if (el.channelsCountBadge) el.channelsCountBadge.textContent = `${state.allChannels.length} ערוצים`;

    // Keep the "playing" index pointing at the same channel inside the new list.
    if (state.playingChannel) {
      state.playingChannelIndex = result.findIndex(c => c.id === state.playingChannel.id);
    }

    renderGrid();
    renderDrawerList();

    if (state.filteredChannels.length > 0) {
      state.focusedGridIdx = Math.min(state.focusedGridIdx, state.filteredChannels.length - 1);
      updateHeroSpotlight(state.filteredChannels[state.focusedGridIdx]);
    }
  }

  // RENDER CHANNELS GRID
  // With 1000+ channels the cards are added in pages (as focus / scroll gets near the end), so the TV stays fast.
  const GRID_PAGE = 60;
  let gridRendered = 0;

  function renderGrid() {
    el.channelsGrid.innerHTML = '';
    gridRendered = 0;

    if (state.filteredChannels.length === 0) {
      el.channelsGrid.innerHTML = `<div class="grid-empty">לא נמצאו ערוצים מתאימים לקטגוריה או לחיפוש</div>`;
      return;
    }

    ensureGridRendered(Math.max(GRID_PAGE, state.focusedGridIdx + GRID_PAGE / 2));
    highlightFocusedItem();
  }

  /** Makes sure cards 0..idx exist (plus a little look-ahead). */
  function ensureGridRendered(idx) {
    const total = state.filteredChannels.length;
    const want = Math.min(total, Math.ceil((idx + 1) / GRID_PAGE) * GRID_PAGE);
    if (want <= gridRendered) return;

    const frag = document.createDocumentFragment();
    state.filteredChannels.slice(gridRendered, want).forEach((ch, i) => {
      const idx = gridRendered + i;
      const card = document.createElement('div');
      const isPlaying = state.playingChannel && state.playingChannel.id === ch.id;
      const isFav = state.favorites.includes(ch.id);
      const foreign = Dub && Dub.isForeign(ch);

      const app = isApp(ch), radio = isRadioCh(ch), locked = appOnly(ch);
      card.className = `channel-card${isPlaying ? ' is-playing' : ''}${isFav ? ' is-fav' : ''}` +
        `${app ? ' is-app' : ''}${radio ? ' is-radio' : ''}${locked ? ' is-locked' : ''}`;
      card.dataset.index = idx;
      card.dataset.id = ch.id;
      card.style.setProperty('--card-hue', hueOf(ch.id));

      card.innerHTML = `
        <div class="card-top">
          <span class="channel-num-badge">${ch.number}</span>
          <div class="card-top-right">
            <span class="card-playing-indicator"><span class="pulse-dot"></span>${radio ? 'מנגן' : 'משדר'}</span>
            <span class="card-fav-star">⭐</span>
            ${foreign ? `<span class="dub-chip" title="ניתן לדיבוב לעברית">🎙️ ${ch.lang === 'auto' ? 'זר' : ch.lang.toUpperCase()}</span>` : ''}
            ${app ? '<span class="app-chip" title="פותח את האפליקציה הרשמית">↗ אפליקציה</span>' : ''}
            ${locked ? '<span class="lock-chip" title="פועל באפליקציות (אנדרואיד, Windows, טלוויזיה)">📲 באפליקציה</span>' : ''}
            ${radio ? '<span class="music-chip" title="רדיו">📻</span>' : (ch.lang === 'music' ? '<span class="music-chip" title="מוזיקה">♪</span>' : '')}
          </div>
        </div>
        <div class="channel-logo-wrap">
          <img class="channel-logo-img" alt="" loading="lazy">
          <div class="monogram hidden"></div>
        </div>
        <div class="channel-card-title">${escapeHtml(ch.name)}</div>
        <div class="channel-card-meta">${escapeHtml(cardMeta(ch))}</div>
      `;
      setLogo(card.querySelector('.channel-logo-img'), card.querySelector('.monogram'), ch);

      card.addEventListener('click', () => {
        setFocus('grid', idx);
        if (Dub) Dub.unlockAudio();
        playChannel(idx);
      });

      card.addEventListener('mouseenter', () => {
        if (state.currentSection === 'grid' || state.currentSection === 'sidebar' || state.currentSection === 'search') {
          setFocus('grid', idx, true);
        }
      });

      frag.appendChild(card);
    });
    el.channelsGrid.appendChild(frag);
    gridRendered = want;
  }

  /** Short second line on a card: what it is + language. */
  function cardMeta(ch) {
    if (isApp(ch)) return 'שירות / אפליקציה רשמית';
    if (isRadioCh(ch)) return 'רדיו בשידור חי';
    const cat = splitCategoryName(ch.category).main.replace(/^ערוצי ישראל$/, 'ישראל');
    const lang = Dub && Dub.isForeign(ch) && ch.lang !== 'auto' ? Dub.langName(ch.lang) : '';
    return lang ? `${cat} · ${lang}` : cat;
  }

  function gridColumns() {
    const cards = el.channelsGrid.querySelectorAll('.channel-card');
    if (cards.length < 2) return 1;
    const top = cards[0].offsetTop;
    let n = 0;
    while (n < cards.length && cards[n].offsetTop === top) n++;
    return Math.max(1, n);
  }

  // HERO SPOTLIGHT
  function updateHeroSpotlight(ch) {
    if (!ch) return;

    el.heroNumber.textContent = `#${ch.number}`;
    el.heroCategory.textContent = splitCategoryName(ch.category).main;
    el.heroBadgeTag.textContent = ch.badge || 'HD';
    el.heroTitle.textContent = ch.name;
    el.heroDesc.textContent = ch.desc || 'שידור חי רציף ואיכותי';
    el.heroSpotlight.style.setProperty('--card-hue', hueOf(ch.id));

    const isFav = state.favorites.includes(ch.id);
    if (el.heroFavTag) el.heroFavTag.classList.toggle('hidden', !isFav);
    if (el.heroBtnFav) el.heroBtnFav.innerHTML = isFav ? '<span>★</span> הסר ממועדפים' : '<span>☆</span> מועדפים';

    const foreign = Dub && Dub.isForeign(ch);
    if (el.heroLangTag) {
      el.heroLangTag.classList.toggle('hidden', !foreign);
      if (foreign) el.heroLangTag.textContent = `🎙️ ${Dub.langName(ch.lang)} ← דיבוב לעברית`;
    }
    const app = isApp(ch), locked = appOnly(ch);
    if (el.heroBtnDub) el.heroBtnDub.classList.toggle('hidden', !foreign || app || locked);
    if (el.heroBtnPlay) {
      el.heroBtnPlay.innerHTML = app ? '<span>↗</span> פתיחת האפליקציה'
        : locked ? '<span>📲</span> צפייה באפליקציה'
        : isRadioCh(ch) ? '<span>🎧</span> האזנה' : '<span>▶</span> צפייה';
    }
    if (el.heroNote) {
      el.heroNote.textContent = app ? (PLATFORM === 'android' ? 'נפתח באפליקציה הרשמית (אם אינה מותקנת – בחנות Google Play)' : 'נפתח באתר הרשמי של השירות')
        : locked ? 'הדפדפן חוסם את השידור הזה – הוא פועל באפליקציות לאנדרואיד, Windows וטלוויזיה' : '';
      el.heroNote.classList.toggle('hidden', !el.heroNote.textContent);
    }
    el.heroSpotlight.style.setProperty('--hero-img', ch.logo ? `url("${String(ch.logo).replace(/"/g, '%22')}")` : 'none');

    setLogo(el.heroLogo, el.heroMonogram, ch);
  }

  function setupHeroButtons() {
    const current = () => state.filteredChannels[state.focusedGridIdx];
    if (el.heroBtnPlay) el.heroBtnPlay.addEventListener('click', () => {
      if (Dub) Dub.unlockAudio();
      playChannel(state.focusedGridIdx);
    });
    if (el.heroBtnDub) el.heroBtnDub.addEventListener('click', () => {
      if (!Dub) return;
      Dub.unlockAudio();
      if (Dub.missingConfig()) {
        showToast('🎙️ ' + Dub.missingConfig());
        openSettings();
        return;
      }
      Dub.save({ enabled: true });
      refreshDubUi();
      playChannel(state.focusedGridIdx);
    });
    if (el.heroBtnFav) el.heroBtnFav.addEventListener('click', () => {
      const ch = current();
      if (ch) toggleFavorite(ch.id);
    });
  }

  // FOCUS SYSTEM
  function setFocus(section, index, noScroll) {
    state.currentSection = section;

    if (section === 'grid') {
      state.focusedGridIdx = Math.max(0, Math.min(index, state.filteredChannels.length - 1));
      updateHeroSpotlight(state.filteredChannels[state.focusedGridIdx]);
    } else if (section === 'sidebar') {
      state.focusedCategoryIdx = Math.max(0, Math.min(index, state.categories.length - 1));
    } else if (section === 'drawer') {
      state.focusedDrawerIdx = Math.max(0, Math.min(index, state.filteredChannels.length - 1));
    } else if (section === 'exit_modal') {
      state.focusedExitBtn = index;
    }

    highlightFocusedItem(noScroll);
  }

  function highlightFocusedItem(noScroll) {
    document.querySelectorAll('.focused').forEach(elem => elem.classList.remove('focused'));
    const reveal = (target) => {
      if (!target) return;
      target.classList.add('focused');
      if (!noScroll) target.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'nearest' });
    };

    if (state.currentSection === 'grid') {
      ensureGridRendered(state.focusedGridIdx + GRID_PAGE / 2);
      reveal(el.channelsGrid.querySelectorAll('.channel-card')[state.focusedGridIdx]);
    } else if (state.currentSection === 'sidebar') {
      reveal(el.categoryList.querySelectorAll('.category-item')[state.focusedCategoryIdx]);
    } else if (state.currentSection === 'search') {
      el.search.parentElement.classList.add('focused');
      el.search.focus();
    } else if (state.currentSection === 'drawer') {
      reveal(el.drawerChannelList.querySelectorAll('.drawer-item')[state.focusedDrawerIdx]);
    } else if (state.currentSection === 'exit_modal') {
      (state.focusedExitBtn === 0 ? el.btnCancelExit : el.btnConfirmExit).classList.add('focused');
    }
  }

  // PLAYBACK SYSTEM
  let networkRetryCount = 0;

  function hidePlayerError() {
    if (el.playerErrorOverlay) el.playerErrorOverlay.classList.add('hidden');
  }

  function showPlayerError(channel, reason) {
    hideSpinner();
    if (Dub) Dub.detach();
    if (state.hlsInstance) {
      try { state.hlsInstance.destroy(); } catch (e) {}
      state.hlsInstance = null;
    }
    if (el.playerErrorOverlay) {
      const insecure = location.protocol === 'https:' && /^http:/i.test(streamUrl(channel));
      el.playerErrorTitle.textContent = insecure ? 'השידור זמין באפליקציית הטלוויזיה' : 'השידור אינו זמין כרגע';
      el.playerErrorDesc.textContent = insecure
        ? `הערוץ "${channel.name}" משדר ב-HTTP, ודפדפנים חוסמים שידור כזה מתוך אתר מאובטח (HTTPS). הוא פועל באפליקציית LG webOS ובנגנים חיצוניים כמו VLC.`
        : `לא הצלחנו להתחבר לשידור של "${channel.name}" (${reason || 'שגיאת רשת'}). ייתכן שהשידור כבוי זמנית – נסו שוב מאוחר יותר או ערוץ אחר.`;
      if (el.btnErrorDirectLink) el.btnErrorDirectLink.href = streamUrl(channel);
      el.playerErrorOverlay.classList.remove('hidden');
    }
  }

  function playChannel(index) {
    if (index < 0 || index >= state.filteredChannels.length) return;
    const channel = state.filteredChannels[index];

    // Service apps open outside; http channels on the web site point to the apps.
    if (isApp(channel)) { openServiceApp(channel); addRecent(channel); return; }
    if (appOnly(channel)) { openGetApp(channel); return; }

    state.playingChannelIndex = index;
    state.playingChannel = channel;
    addRecent(channel);

    el.channelsGrid.querySelectorAll('.channel-card').forEach((c, idx) => {
      c.classList.toggle('is-playing', idx === index);
    });
    el.drawerChannelList.querySelectorAll('.drawer-item').forEach((c, idx) => {
      c.classList.toggle('is-playing', idx === index);
    });

    enterFullscreenPlayer();
    updateOSD(channel);
    showOSD();
    showSpinner('מתחבר לשידור חי...');
    refreshDubUi();

    loadVideoStream(streamUrl(channel), channel);
  }

  function isAudioUrl(url, channel) {
    return url.includes('.mp3') || url.includes('.audio') || url.includes('_mp3') || url.includes('_aac') ||
      url.endsWith('.aac') || (channel && channel.categoryEn === 'music' && !url.includes('.m3u8'));
  }

  function startPlayback(m, channel) {
    const p = m.play();
    if (!p) { hideSpinner(); return; }
    p.then(() => {
      hideSpinner();
      updatePlayPauseButton(false);
    }).catch(() => {
      // Autoplay blocked: try muted
      m.muted = true;
      m.play().then(() => {
        hideSpinner();
        updatePlayPauseButton(false);
        showToast('השידור פועל במצב מושתק (לחץ 🔊 לשמע)');
      }).catch(e => showPlayerError(channel, e && e.message));
    });
  }

  function loadVideoStream(url, channel) {
    hidePlayerError();
    networkRetryCount = 0;
    if (Dub) Dub.detach();

    if (state.hlsInstance) {
      try { state.hlsInstance.destroy(); } catch (e) {}
      state.hlsInstance = null;
    }

    // Radio streams play in their own <audio> element, so the video element stays HLS-only.
    if (isAudioUrl(url, channel)) {
      el.video.pause();
      el.video.removeAttribute('src');
      el.video.load();
      state.isRadio = true;
      if (el.radioBackdropLogo) {
        el.radioBackdropLogo.src = channel.logo || '';
        el.radioBackdropLogo.style.display = channel.logo ? 'block' : 'none';
      }
      if (el.radioBackdropName) el.radioBackdropName.textContent = channel.name;
      el.playerRadioBackdrop.classList.remove('hidden');
      el.osdQuality.textContent = 'רדיו • שמע חי';
      el.radio.src = url;
      el.radio.onerror = () => showPlayerError(channel, 'שגיאה בניגון השמע');
      startPlayback(el.radio, channel);
      return;
    }

    state.isRadio = false;
    el.radio.pause();
    el.radio.removeAttribute('src');
    el.playerRadioBackdrop.classList.add('hidden');
    el.osdQuality.textContent = 'HLS Live';

    // Video / HLS Stream
    if (window.Hls && Hls.isSupported()) {
      const delayed = Dub && Dub.wantsSync(channel);
      const hls = new Hls({
        enableWorker: true,
        lowLatencyMode: !delayed,
        backBufferLength: 60,
        manifestLoadingMaxRetry: 2,
        levelLoadingMaxRetry: 2
      });
      hls.loadSource(url);
      hls.attachMedia(el.video);

      let levelAdjusted = false;
      hls.on(Hls.Events.LEVEL_LOADED, (event, data) => {
        if (!levelAdjusted && Dub) {
          levelAdjusted = true;
          Dub.adjustMainHls(hls, data.details, channel);
        }
      });

      hls.on(Hls.Events.MANIFEST_PARSED, (event, data) => {
        if (delayed) showSpinner(`מכין דיבוב מסונכרן… התמונה מושהית ב-${Dub.settings.delay} שניות`);
        else hideSpinner();
        const p = el.video.play();
        const onPlaying = () => {
          hideSpinner();
          updatePlayPauseButton(false);
        };
        if (p !== undefined) {
          p.then(onPlaying).catch(() => {
            el.video.muted = true;
            el.video.play().then(() => {
              onPlaying();
              showToast('השידור פועל במצב מושתק (לחץ 🔊 לשמע)');
            }).catch(() => {});
          });
        }
        el.video.addEventListener('playing', hideSpinner, { once: true });
        const lvl = data.levels && data.levels[data.levels.length - 1];
        if (lvl && lvl.height) el.osdQuality.textContent = `HLS Live ${lvl.height}p`;
        if (Dub && state.playingChannel === channel) {
          Dub.attach({ video: el.video, hls: hls, channel: channel, url: url });
        }
      });

      hls.on(Hls.Events.ERROR, (event, data) => {
        if (data.fatal) {
          switch (data.type) {
            case Hls.ErrorTypes.NETWORK_ERROR:
              if (networkRetryCount < 1) {
                networkRetryCount++;
                showToast('מנסה להתחבר לשידור...');
                hls.startLoad();
              } else {
                showPlayerError(channel, 'שגיאת חיבור ברשת');
              }
              break;
            case Hls.ErrorTypes.MEDIA_ERROR:
              hls.recoverMediaError();
              break;
            default:
              showPlayerError(channel, 'פורמט אינו נתמך בדפדפן');
              break;
          }
        }
      });

      state.hlsInstance = hls;
    } else if (el.video.canPlayType('application/vnd.apple.mpegurl')) {
      // Native Apple HLS (Safari iOS & Mac) – dubbing needs hls.js, so it is not offered here.
      el.video.src = url;
      el.video.addEventListener('loadedmetadata', () => startPlayback(el.video, channel), { once: true });
      el.video.addEventListener('error', () => showPlayerError(channel, 'שגיאה בניגון הווידאו'), { once: true });
    } else {
      el.video.src = url;
      startPlayback(el.video, channel);
    }
  }

  // HEBREW DUBBING
  const DUB_STATE_CLASS = ['off', 'loading', 'listening', 'translating', 'speaking', 'error'];

  function setupDub() {
    if (!Dub) return;
    Dub.init({
      container: el.playerContainer,
      toast: showToast,
      onStatus: (st, text) => {
        if (el.osdDubStatus) {
          el.osdDubStatus.classList.toggle('hidden', st === 'off');
          DUB_STATE_CLASS.forEach(c => el.osdDubStatus.classList.remove('st-' + c));
          el.osdDubStatus.classList.add('st-' + st);
        }
        if (el.osdDubText) el.osdDubText.textContent = text;
        if (st === 'error') {
          showOSD();
          showToast('🎙️ ' + text);
        }
        if (st === 'listening' || st === 'speaking') hideSpinner();
      }
    });
    if (el.btnDubToggle) el.btnDubToggle.addEventListener('click', () => toggleDub());
    if (el.webBtnDub) el.webBtnDub.addEventListener('click', (e) => { e.stopPropagation(); toggleDub(); });
    refreshDubUi();
  }

  function refreshDubUi() {
    if (!Dub) return;
    const on = Dub.settings.enabled;
    if (el.btnDubToggle) el.btnDubToggle.classList.toggle('active', on);
    if (el.dubToggleLabel) el.dubToggleLabel.textContent = on ? 'דיבוב: פעיל' : 'דיבוב: כבוי';
    const ch = state.playingChannel;
    const foreign = !!ch && Dub.isForeign(ch);
    if (el.webBtnDub) {
      el.webBtnDub.classList.toggle('hidden', !foreign);
      el.webBtnDub.classList.toggle('active', on);
      if (el.webDubLabel) el.webDubLabel.textContent = on ? 'דיבוב פעיל' : 'דיבוב לעברית';
    }
  }

  function toggleDub() {
    if (!Dub) return;
    Dub.unlockAudio();
    const turnOn = !Dub.settings.enabled;
    if (turnOn && Dub.missingConfig()) {
      showToast('🎙️ ' + Dub.missingConfig());
      openSettings();
      return;
    }
    Dub.save({ enabled: turnOn });
    refreshDubUi();
    const ch = state.playingChannel;
    const inPlayer = !el.playerContainer.classList.contains('hidden');
    if (!turnOn) {
      Dub.detach();
      showToast('🎙️ הדיבוב כובה');
      return;
    }
    if (ch && !Dub.isForeign(ch)) {
      showToast('🎙️ הדיבוב הופעל – יפעל אוטומטית בערוצים שאינם בעברית');
      return;
    }
    showToast(`🎙️ דיבוב לעברית הופעל${Dub.settings.sync ? ' (מסונכרן)' : ''}`);
    if (ch && inPlayer && state.hlsInstance) {
      if (Dub.settings.sync) {
        // Lip-sync needs the picture to be delayed: restart the stream with the delay.
        loadVideoStream(streamUrl(ch), ch);
        showSpinner('מכין דיבוב מסונכרן…');
      } else {
        Dub.attach({ video: el.video, hls: state.hlsInstance, channel: ch, url: streamUrl(ch) });
      }
    }
  }

  // SETTINGS
  function setupSettings() {
    if (el.btnOpenSettings) el.btnOpenSettings.addEventListener('click', openSettings);
    if (el.webBtnSettings) el.webBtnSettings.addEventListener('click', (e) => { e.stopPropagation(); openSettings(); });
    if (el.btnCloseSettings) el.btnCloseSettings.addEventListener('click', closeSettings);
    el.settingsModal.addEventListener('click', (e) => { if (e.target === el.settingsModal) closeSettings(); });
    if (!Dub) return;

    const s = () => Dub.settings;
    const f = {
      enabled: $('set-dub-enabled'), engine: $('set-engine'), geminiKey: $('set-gemini-key'), geminiModel: $('set-gemini-model'),
      openaiKey: $('set-openai-key'), localModel: $('set-local-model'), tts: $('set-tts'), voice: $('set-voice'),
      rate: $('set-rate'), duck: $('set-duck'), sync: $('set-sync'), delay: $('set-delay'), subs: $('set-subs'), orig: $('set-orig'),
      outRate: $('out-rate'), outDuck: $('out-duck'), rowVoice: $('row-voice'), rowDelay: $('row-delay')
    };
    el.settingsFields = f;

    const fillModels = (models) => {
      const cur = s().geminiModel;
      const list = Array.from(new Set([cur, 'gemini-flash-latest'].concat(models || [])));
      f.geminiModel.innerHTML = list.map(m => `<option value="${escapeHtml(m)}">${escapeHtml(m)}</option>`).join('');
      f.geminiModel.value = cur;
    };
    const fillVoices = () => {
      const voices = Dub.hebrewVoices();
      f.voice.innerHTML = '<option value="">אוטומטי (לפי מין הדובר)</option>' +
        voices.map(v => `<option value="${escapeHtml(v.name)}">${escapeHtml(v.name)}</option>`).join('');
      f.voice.value = s().voiceName;
      if (!voices.length) f.voice.innerHTML = '<option value="">לא נמצא קול עברי במכשיר – ישמש קול Google</option>';
    };
    const loadModels = () => {
      if (!s().geminiKey) return;
      Dub.listGeminiModels(s().geminiKey).then(fillModels).catch(() => {});
    };

    el.syncSettingsForm = () => {
      const st = s();
      f.enabled.checked = st.enabled;
      f.engine.value = st.engine;
      f.geminiKey.value = st.geminiKey;
      f.openaiKey.value = st.openaiKey;
      f.localModel.value = st.localModel;
      f.tts.value = st.tts;
      f.rate.value = st.rate;
      f.duck.value = st.duck;
      f.sync.checked = st.sync;
      f.delay.value = String(st.delay);
      f.subs.checked = st.subtitles;
      f.orig.checked = st.showOriginal;
      fillModels();
      fillVoices();
      updateVisibility();
    };

    function updateVisibility() {
      const st = s();
      el.settingsModal.querySelectorAll('.set-group').forEach(g => g.classList.toggle('hidden', g.dataset.engine !== st.engine));
      f.rowVoice.classList.toggle('hidden', !(st.tts === 'auto' || st.tts === 'browser'));
      f.rowDelay.classList.toggle('hidden', !st.sync);
      f.outRate.textContent = Number(st.rate).toFixed(2).replace(/0$/, '');
      f.outDuck.textContent = Math.round(st.duck * 100) + '%';
    }

    const bind = (node, evt, fn) => node.addEventListener(evt, () => { fn(); updateVisibility(); refreshDubUi(); });
    bind(f.enabled, 'change', () => {
      if (f.enabled.checked && Dub.missingConfig()) showToast('🎙️ ' + Dub.missingConfig());
      Dub.save({ enabled: f.enabled.checked });
      if (!f.enabled.checked) Dub.detach();
    });
    bind(f.engine, 'change', () => Dub.save({ engine: f.engine.value }));
    bind(f.geminiKey, 'change', () => { Dub.save({ geminiKey: f.geminiKey.value.trim() }); loadModels(); });
    bind(f.geminiModel, 'change', () => Dub.save({ geminiModel: f.geminiModel.value }));
    bind(f.openaiKey, 'change', () => Dub.save({ openaiKey: f.openaiKey.value.trim() }));
    bind(f.localModel, 'change', () => Dub.save({ localModel: f.localModel.value }));
    bind(f.tts, 'change', () => Dub.save({ tts: f.tts.value }));
    bind(f.voice, 'change', () => Dub.save({ voiceName: f.voice.value }));
    bind(f.rate, 'input', () => Dub.save({ rate: parseFloat(f.rate.value) }));
    bind(f.duck, 'input', () => Dub.save({ duck: parseFloat(f.duck.value) }));
    bind(f.sync, 'change', () => Dub.save({ sync: f.sync.checked }));
    bind(f.delay, 'change', () => Dub.save({ delay: parseInt(f.delay.value, 10) }));
    bind(f.subs, 'change', () => Dub.save({ subtitles: f.subs.checked }));
    bind(f.orig, 'change', () => Dub.save({ showOriginal: f.orig.checked }));
    $('btn-test-voice').addEventListener('click', () => { Dub.unlockAudio(); Dub.testVoice(); });
    if ('speechSynthesis' in window && speechSynthesis.addEventListener) {
      speechSynthesis.addEventListener('voiceschanged', () => { if (state.settingsOpen) fillVoices(); });
    }
    loadModels();
  }

  function settingsFocusables() {
    return Array.from(el.settingsModal.querySelectorAll('.set-focus'))
      .filter(n => n.offsetParent !== null && !n.closest('.hidden'));
  }

  function openSettings() {
    if (state.settingsOpen) return;
    state.settingsOpen = true;
    state.sectionBeforeSettings = state.currentSection === 'settings' ? 'grid' : state.currentSection;
    if (el.syncSettingsForm) el.syncSettingsForm();
    el.settingsModal.classList.remove('hidden');
    state.currentSection = 'settings';
    document.querySelectorAll('.focused').forEach(n => n.classList.remove('focused'));
    const first = settingsFocusables()[0];
    if (first) first.focus();
  }

  function closeSettings() {
    if (!state.settingsOpen) return;
    state.settingsOpen = false;
    if (document.activeElement && el.settingsModal.contains(document.activeElement)) document.activeElement.blur();
    el.settingsModal.classList.add('hidden');
    const back = state.sectionBeforeSettings;
    if (back === 'player' && !el.playerContainer.classList.contains('hidden')) {
      state.currentSection = 'player';
      showOSD();
    } else {
      setFocus('grid', state.focusedGridIdx, true);
    }
  }

  function handleSettingsKeys(code, e) {
    const list = settingsFocusables();
    const cur = document.activeElement;
    let i = list.indexOf(cur);
    if (code === 38 || code === 40) {
      e.preventDefault();
      if (i === -1) i = 0;
      else i = (i + (code === 40 ? 1 : -1) + list.length) % list.length;
      list[i].focus();
      list[i].scrollIntoView({ block: 'nearest', behavior: 'smooth' });
      return;
    }
    if (!cur || i === -1) return;
    const step = code === 37 ? 1 : code === 39 ? -1 : 0; // RTL: left = next
    if (step && cur.tagName === 'SELECT') {
      e.preventDefault();
      const n = cur.options.length;
      cur.selectedIndex = (cur.selectedIndex + step + n) % n;
      cur.dispatchEvent(new Event('change'));
    } else if (step && cur.type === 'range') {
      e.preventDefault();
      const st = parseFloat(cur.step) || 0.05;
      cur.value = Math.min(parseFloat(cur.max), Math.max(parseFloat(cur.min), parseFloat(cur.value) - step * st));
      cur.dispatchEvent(new Event('input'));
    } else if (code === 13 && cur.type === 'checkbox') {
      e.preventDefault();
      cur.checked = !cur.checked;
      cur.dispatchEvent(new Event('change'));
    }
  }

  // INTERACTIVE WEB PLAYER CONTROLS
  function setupWebPlayerControls() {
    const on = (node, fn) => {
      if (node) node.addEventListener('click', (e) => { e.stopPropagation(); fn(); });
    };
    on(el.btnFloatingClose, exitFullscreenToGrid);
    on(el.btnErrorBack, exitFullscreenToGrid);
    on(el.webBtnBack, handleBackKey);
    on(el.webBtnPrev, () => zapChannel(-1));
    on(el.webBtnNext, () => zapChannel(1));
    on(el.webBtnPlayPause, togglePlayPause);
    on(el.webBtnMute, toggleMute);
    on(el.webBtnAspect, cycleRetroMode);
    on(el.webBtnFullscreen, toggleBrowserFullscreen);

    [el.video, el.radio].forEach(m => {
      m.addEventListener('play', () => { if (m === media()) updatePlayPauseButton(false); });
      m.addEventListener('pause', () => { if (m === media()) updatePlayPauseButton(true); });
      m.addEventListener('volumechange', () => {
        if (m === media() && el.webMuteIcon) el.webMuteIcon.textContent = m.muted ? '🔇' : '🔊';
      });
    });

    el.playerContainer.addEventListener('mousemove', () => {
      showOSD();
      resetScreensaverTimer();
    });

    el.playerContainer.addEventListener('click', (e) => {
      if (e.target.closest('.osd-web-actions') || e.target.closest('.quick-drawer') ||
          e.target.closest('.player-error-box') || e.target.closest('.player-floating-close')) return;
      if (el.osd.classList.contains('osd-hidden')) showOSD();
      else el.osd.classList.add('osd-hidden');
    });

    el.playerContainer.addEventListener('dblclick', (e) => {
      if (e.target.closest('.osd-web-actions') || e.target.closest('.quick-drawer')) return;
      toggleBrowserFullscreen();
    });
  }

  function togglePlayPause() {
    const m = media();
    if (!m) return;
    if (m.paused) {
      m.play().then(() => {
        updatePlayPauseButton(false);
        showToast('מנגן ▶');
      }).catch(() => {});
    } else {
      m.pause();
      updatePlayPauseButton(true);
      showToast('מושהה ⏸');
    }
  }

  function updatePlayPauseButton(isPaused) {
    if (el.webPlayIcon) el.webPlayIcon.textContent = isPaused ? '▶' : '⏸';
  }

  function toggleMute() {
    const m = media();
    if (!m) return;
    m.muted = !m.muted;
    if (el.webMuteIcon) el.webMuteIcon.textContent = m.muted ? '🔇' : '🔊';
    showToast(m.muted ? 'מושתק 🔇' : 'שמע מופעל 🔊');
  }

  function toggleBrowserFullscreen() {
    const doc = document;
    const docEl = document.documentElement;

    if (!doc.fullscreenElement && !doc.webkitFullscreenElement) {
      if (docEl.requestFullscreen) docEl.requestFullscreen().catch(() => {});
      else if (docEl.webkitRequestFullscreen) docEl.webkitRequestFullscreen();
    } else {
      if (doc.exitFullscreen) doc.exitFullscreen();
      else if (doc.webkitExitFullscreen) doc.webkitExitFullscreen();
    }
  }

  function enterFullscreenPlayer() {
    state.currentSection = 'player';
    el.playerContainer.classList.remove('hidden');
    document.body.classList.add('player-open');
    showOSD();
  }

  function exitFullscreenToGrid() {
    hidePlayerError();
    hideSpinner();
    el.playerContainer.classList.add('hidden');
    document.body.classList.remove('player-open');
    closeQuickDrawer();
    state.currentSection = 'grid';
    setFocus('grid', state.focusedGridIdx);
    if (state.playingChannel) showToast('חזרת למדריך – השידור ממשיך ברקע');
  }

  function zapChannel(direction) {
    const list = state.filteredChannels;
    if (list.length === 0) return;
    let next = state.playingChannelIndex;
    // skip service apps and (on the web site) channels that only play in the apps
    for (let i = 0; i < list.length; i++) {
      next = (next + direction + list.length) % list.length;
      if (!isApp(list[next]) && !appOnly(list[next])) break;
    }
    playChannel(next);
  }

  // OSD & TOAST
  function updateOSD(channel) {
    el.osdNumber.textContent = `${channel.number}`;
    el.osdName.textContent = channel.name;
    el.osdCategory.textContent = splitCategoryName(channel.category).main;
    setLogo(el.osdLogo, el.osdMonogram, channel);
  }

  function showOSD() {
    el.osd.classList.remove('osd-hidden');
    el.playerContainer.classList.remove('cursor-hidden');
    if (state.osdTimeout) clearTimeout(state.osdTimeout);
    state.osdTimeout = setTimeout(() => {
      if (state.settingsOpen || state.drawerOpen) return;
      el.osd.classList.add('osd-hidden');
      el.playerContainer.classList.add('cursor-hidden');
    }, 4500);
  }

  function showToast(msg) {
    el.toastMessage.textContent = msg;
    el.toast.classList.remove('hidden');
    if (state.toastTimeout) clearTimeout(state.toastTimeout);
    state.toastTimeout = setTimeout(() => {
      el.toast.classList.add('hidden');
    }, 3200);
  }

  function showSpinner(text) {
    el.spinnerText.textContent = text || 'טוען...';
    el.spinner.classList.remove('hidden');
  }

  function hideSpinner() {
    el.spinner.classList.add('hidden');
  }

  // QUICK DRAWER IN PLAYER
  function renderDrawerList() {
    // One HTML string + one delegated click handler: cheap even for 1000+ channels.
    const playingId = state.playingChannel && state.playingChannel.id;
    el.drawerChannelList.innerHTML = state.filteredChannels.map((ch, idx) => `
      <div class="drawer-item${ch.id === playingId ? ' is-playing' : ''}" data-index="${idx}">
        <span class="drawer-num">${ch.number}</span>
        <span class="drawer-name">${escapeHtml(ch.name)}</span>
        ${Dub && Dub.isForeign(ch) ? '<span class="drawer-dub">🎙️</span>' : ''}
      </div>`).join('');
    if (!el.drawerChannelList.dataset.wired) {
      el.drawerChannelList.dataset.wired = '1';
      el.drawerChannelList.addEventListener('click', (e) => {
        const item = e.target.closest('.drawer-item');
        if (!item) return;
        e.stopPropagation();
        closeQuickDrawer();
        playChannel(parseInt(item.dataset.index, 10));
      });
    }
  }

  function openQuickDrawer() {
    state.drawerOpen = true;
    el.quickDrawer.classList.remove('hidden');
    setFocus('drawer', state.playingChannelIndex !== -1 ? state.playingChannelIndex : 0);
  }

  function closeQuickDrawer() {
    state.drawerOpen = false;
    el.quickDrawer.classList.add('hidden');
    if (state.currentSection === 'drawer') state.currentSection = 'player';
  }

  // DIRECT CHANNEL NUMBER TYPING
  function handleDigitInput(digit) {
    state.typedDigits += digit;

    const num = parseInt(state.typedDigits, 10);
    const target = state.allChannels.find(c => c.number === num);

    el.channelNumberHud.classList.remove('hidden');
    el.hudDigits.textContent = state.typedDigits;
    el.hudChannelName.textContent = target ? target.name : 'מחפש ערוץ...';

    if (state.digitTimeout) clearTimeout(state.digitTimeout);
    state.digitTimeout = setTimeout(() => {
      el.channelNumberHud.classList.add('hidden');
      const finalNum = parseInt(state.typedDigits, 10);
      state.typedDigits = '';

      if (target) {
        let gridIdx = state.filteredChannels.findIndex(c => c.id === target.id);
        if (gridIdx === -1) {
          selectCategory('ALL', 0);
          gridIdx = state.filteredChannels.findIndex(c => c.id === target.id);
        }
        if (gridIdx !== -1) {
          setFocus('grid', gridIdx);
          playChannel(gridIdx);
        }
      } else {
        showToast(`ערוץ מספר ${finalNum} לא קיים`);
      }
    }, 1200);
  }

  // SMART BACK BUTTON: NEVER ACCIDENTALLY EXITS!
  function handleBackKey() {
    if (state.getAppOpen) {
      closeGetApp();
      return;
    }
    if (state.settingsOpen) {
      closeSettings();
      return;
    }

    // 1. If Quick Drawer is open inside player -> close it
    if (state.drawerOpen) {
      closeQuickDrawer();
      return;
    }

    // 2. If Exit Modal is open -> close it
    if (state.exitModalOpen) {
      closeExitModal();
      return;
    }

    // 3. If in Fullscreen Player -> return to Grid (Keep video playing smoothly!)
    if (state.currentSection === 'player') {
      exitFullscreenToGrid();
      return;
    }

    // 4. If Search box is focused or has query -> clear it and return to grid
    if (state.currentSection === 'search' || state.searchQuery.trim().length > 0) {
      el.search.value = '';
      el.search.blur();
      state.searchQuery = '';
      applyFilter();
      setFocus('grid', 0);
      showToast('חזרת לכל הערוצים');
      return;
    }

    // 5. If focused in sidebar -> return to grid
    if (state.currentSection === 'sidebar') {
      setFocus('grid', state.focusedGridIdx || 0);
      return;
    }

    // 6. If on a filtered category (e.g. "ילדים" or "מועדפים") -> return to "כל הערוצים"
    if (state.activeCategory !== 'ALL') {
      selectCategory('ALL', 0);
      setFocus('grid', 0);
      showToast('חזרת לכל הערוצים');
      return;
    }

    // 7. Already at the root of the Guide: require a double press within 2.5 seconds to exit.
    const now = Date.now();
    if (now - state.lastBackPressTime < 2500) {
      performActualExit();
    } else {
      state.lastBackPressTime = now;
      showToast('לחץ שוב BACK ליציאה מהאפליקציה');
    }
  }

  function openExitModal() {
    state.exitModalOpen = true;
    el.exitModal.classList.remove('hidden');
    setFocus('exit_modal', 0);
  }

  function closeExitModal() {
    state.exitModalOpen = false;
    el.exitModal.classList.add('hidden');
    setFocus('grid', state.focusedGridIdx);
  }

  function performActualExit() {
    showToast('להתראות!');
    try {
      if (PLATFORM === 'android' && window.AndroidApp.exit) {
        window.AndroidApp.exit();
      } else if (PLATFORM === 'windows') {
        window.chrome.webview.postMessage(JSON.stringify({ exit: true }));
      } else if (PLATFORM === 'tizen' && window.tizen) {
        window.tizen.application.getCurrentApplication().exit();
      } else if (window.webOS && window.webOS.platformBack) {
        window.webOS.platformBack();
      } else {
        window.close();
      }
    } catch (e) {
      window.close();
    }
  }

  // REMOTE CONTROL KEYBOARD DISPATCHER
  function setupKeyListeners() {
    window.addEventListener('keydown', handleKeyDown);

    el.search.addEventListener('input', (e) => {
      state.searchQuery = e.target.value;
      state.focusedGridIdx = 0;
      applyFilter();
    });
    el.search.addEventListener('focus', () => {
      if (state.currentSection !== 'search') setFocus('search', 0);
    });

    el.btnCancelExit.addEventListener('click', closeExitModal);
    el.btnConfirmExit.addEventListener('click', performActualExit);
  }

  function isTypingTarget(t) {
    return t && ((t.tagName === 'INPUT' && /^(text|password|search|email|url)$/i.test(t.type)) || t.tagName === 'TEXTAREA');
  }

  function handleKeyDown(e) {
    resetScreensaverTimer();

    const code = e.keyCode || e.which;

    // Samsung Tizen remote: Back = 10009, colour keys 403-406 like webOS, CH+/- = 427/428.
    if (code === 10009) { e.preventDefault(); handleBackKey(); return; }

    // "Get the app" panel: Back / Escape closes it, Tab / arrows move between the links.
    if (state.getAppOpen) {
      if (code === 461 || code === 27 || code === 8) { e.preventDefault(); closeGetApp(); }
      return;
    }

    // Settings dialog has its own navigation; text fields keep their keys.
    if (state.settingsOpen) {
      if (code === 461 || code === 27 || (code === 8 && !isTypingTarget(e.target))) {
        e.preventDefault();
        if (isTypingTarget(e.target)) e.target.blur();
        else closeSettings();
        return;
      }
      if (isTypingTarget(e.target) && code !== 38 && code !== 40) return;
      handleSettingsKeys(code, e);
      return;
    }

    // Web & Keyboard Shortcuts (when not typing in the search box)
    if (state.currentSection !== 'search' && !isTypingTarget(e.target)) {
      // Space (32) -> Play / Pause
      if (code === 32 && (state.currentSection === 'player' || (media() && !media().paused))) {
        e.preventDefault();
        togglePlayPause();
        return;
      }

      // 'F' (70) -> Browser Fullscreen
      if (code === 70) {
        e.preventDefault();
        toggleBrowserFullscreen();
        return;
      }

      // 'M' (77) -> Mute / Unmute
      if (code === 77) {
        e.preventDefault();
        toggleMute();
        return;
      }

      // 'D' (68) -> Hebrew dubbing on/off
      if (code === 68) {
        e.preventDefault();
        toggleDub();
        return;
      }

      // 'S' (83) -> Settings
      if (code === 83) {
        e.preventDefault();
        openSettings();
        return;
      }

      // Number keys (0-9) and numpad
      if (code >= 48 && code <= 57) {
        handleDigitInput(String(code - 48));
        return;
      }
      if (code >= 96 && code <= 105) {
        handleDigitInput(String(code - 96));
        return;
      }
    }

    switch (code) {
      // BACK BUTTON (webOS, Escape, Backspace)
      case 461: // webOS Back
      case 27:  // Escape
      case 8:   // Backspace
        if (state.currentSection !== 'search' || el.search.value === '' || code !== 8) {
          e.preventDefault();
          handleBackKey();
        }
        break;

      // RED BUTTON (403) -> Return to All Channels
      case 403:
        e.preventDefault();
        selectCategory('ALL', 0);
        setFocus('grid', 0);
        showToast('כל הערוצים 📺');
        break;

      // GREEN BUTTON (404) -> Retro Remaster & Aspect Ratio Mode
      case 404:
        e.preventDefault();
        cycleRetroMode();
        break;

      // YELLOW BUTTON (405) -> In Grid: Toggle Favorite | In Player: Cycle OLED Mode
      case 405:
        e.preventDefault();
        if (state.currentSection === 'player') {
          cycleOledMode();
        } else {
          const ch = state.filteredChannels[state.focusedGridIdx];
          if (ch) toggleFavorite(ch.id);
        }
        break;

      // BLUE BUTTON (406) -> Hebrew dubbing (in player) / Search (in guide)
      case 406:
        e.preventDefault();
        if (state.currentSection === 'player' || state.currentSection === 'drawer') toggleDub();
        else setFocus('search', 0);
        break;

      // CHANNEL UP / DOWN (33 / 34, 427 / 428)
      case 33:
      case 427:
        e.preventDefault();
        zapChannel(1);
        break;
      case 34:
      case 428:
        e.preventDefault();
        zapChannel(-1);
        break;

      // SECTION-SPECIFIC KEYS
      default:
        dispatchSectionKey(code, e);
        break;
    }
  }

  function dispatchSectionKey(code, e) {
    if (state.currentSection === 'grid') {
      handleGridKeys(code, e);
    } else if (state.currentSection === 'sidebar') {
      handleSidebarKeys(code, e);
    } else if (state.currentSection === 'player') {
      handlePlayerKeys(code, e);
    } else if (state.currentSection === 'drawer') {
      handleDrawerKeys(code, e);
    } else if (state.currentSection === 'search') {
      handleSearchKeys(code, e);
    } else if (state.currentSection === 'exit_modal') {
      handleExitModalKeys(code, e);
    }
  }

  function handleGridKeys(code, e) {
    const total = state.filteredChannels.length;
    if (total === 0) {
      if (code === 39) { e.preventDefault(); setFocus('sidebar', state.focusedCategoryIdx); }
      return;
    }

    const COLS = gridColumns();
    const next = state.focusedGridIdx;

    if (code === 37) { // Left (RTL: next card)
      e.preventDefault();
      if (next % COLS < COLS - 1 && next < total - 1) setFocus('grid', next + 1);
    } else if (code === 39) { // Right (RTL: previous card or sidebar)
      e.preventDefault();
      if (next % COLS > 0) setFocus('grid', next - 1);
      else setFocus('sidebar', state.focusedCategoryIdx);
    } else if (code === 38) { // Up
      e.preventDefault();
      if (next - COLS >= 0) setFocus('grid', next - COLS);
      else setFocus('search', 0);
    } else if (code === 40) { // Down
      e.preventDefault();
      if (next + COLS < total) setFocus('grid', next + COLS);
      else if (Math.floor(next / COLS) < Math.floor((total - 1) / COLS)) setFocus('grid', total - 1);
    } else if (code === 13) { // Enter / OK
      e.preventDefault();
      if (Dub) Dub.unlockAudio();
      playChannel(state.focusedGridIdx);
    }
  }

  function handleSidebarKeys(code, e) {
    const total = state.categories.length;
    const next = state.focusedCategoryIdx;

    if (code === 38) { // Up
      e.preventDefault();
      if (next > 0) setFocus('sidebar', next - 1);
    } else if (code === 40) { // Down
      e.preventDefault();
      if (next < total - 1) setFocus('sidebar', next + 1);
    } else if (code === 37 || code === 13) { // Left (into grid) or OK
      e.preventDefault();
      const cat = state.categories[next];
      selectCategory(cat.id, next);
      setFocus('grid', 0);
    }
  }

  function handleSearchKeys(code, e) {
    if (code === 40 || code === 13) {
      e.preventDefault();
      el.search.blur();
      setFocus('grid', 0);
    }
  }

  function handlePlayerKeys(code, e) {
    showOSD();

    if (code === 38) {
      e.preventDefault();
      zapChannel(-1);
    } else if (code === 40) {
      e.preventDefault();
      zapChannel(1);
    } else if (code === 37) {
      e.preventDefault();
      openQuickDrawer();
    } else if (code === 13) {
      e.preventDefault();
      togglePlayPause();
    }
  }

  function handleDrawerKeys(code, e) {
    const total = state.filteredChannels.length;
    const next = state.focusedDrawerIdx;

    if (code === 38) {
      e.preventDefault();
      if (next > 0) setFocus('drawer', next - 1);
    } else if (code === 40) {
      e.preventDefault();
      if (next < total - 1) setFocus('drawer', next + 1);
    } else if (code === 13) {
      e.preventDefault();
      closeQuickDrawer();
      playChannel(next);
    } else if (code === 39) {
      e.preventDefault();
      closeQuickDrawer();
    }
  }

  function handleExitModalKeys(code, e) {
    if (code === 37 || code === 39) {
      e.preventDefault();
      setFocus('exit_modal', state.focusedExitBtn === 0 ? 1 : 0);
    } else if (code === 13) {
      e.preventDefault();
      if (state.focusedExitBtn === 0) closeExitModal();
      else performActualExit();
    }
  }

  // MAGIC REMOTE MOUSE SUPPORT
  function setupMouseListeners() {
    window.addEventListener('mousemove', resetScreensaverTimer);
    window.addEventListener('click', resetScreensaverTimer);
    window.addEventListener('touchstart', resetScreensaverTimer, { passive: true });
  }

  // Exposed for debugging / automated checks.
  window.__itv = { state, playChannel, toggleDub, openSettings };
})();
