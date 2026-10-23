/**
 * ISRAEL TV PLUS - LG webOS Smart TV Application Engine
 * Optimized for LG OLED, Light Mode, Magic Remote, and 60fps Hardware Acceleration
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
    theme: 'oled', // 'oled' | 'light'

    // Focus & Navigation
    currentSection: 'grid', // 'grid' | 'sidebar' | 'search' | 'player' | 'drawer' | 'exit_modal'
    focusedGridIdx: 0,
    focusedCategoryIdx: 0,
    focusedDrawerIdx: 0,
    focusedExitBtn: 0, // 0 = Cancel, 1 = Exit

    // Playback
    playingChannelIndex: -1,
    hlsInstance: null,
    osdTimeout: null,
    toastTimeout: null,
    screensaverTimeout: null,
    screensaverActive: false,
    drawerOpen: false,
    exitModalOpen: false,

    // OLED & Retro Remaster Modes
    oledModeIdx: 0,
    retroModeIdx: 0,

    // Direct Number Typing
    typedDigits: '',
    digitTimeout: null,
    lastBackPressTime: 0
  };

  // OLED PRESETS
  const OLED_MODES = [
    { id: 'cinema', label: 'קולנוע שחור מושלם', filterClass: 'oled-filter-cinema', desc: 'True Black 0.0 nits' },
    { id: 'vivid', label: 'צבעים עשירים Vivid', filterClass: 'oled-filter-vivid', desc: 'צבעוניות חיה מוגברת' },
    { id: 'sports', label: 'ספורט Boost', filterClass: 'oled-filter-sports', desc: 'תנועה מהירה וחדות' },
    { id: 'natural', label: 'טבעי ומאוזן', filterClass: 'oled-filter-natural', desc: 'צבעי מקור נאמנים' }
  ];

  // RETRO REMASTER (SD / VINTAGE 4:3 UPSCALE) PRESETS
  const RETRO_MODES = [
    { id: 'off', label: 'כבוי (רגיל)', sharpen: false, aspectClass: 'aspect-fit', desc: 'ללא שינוי' },
    { id: 'pillarbox', label: 'חידוד רטרו: 4:3 מקורי', sharpen: true, aspectClass: 'aspect-pillarbox', desc: 'פילטר חידוד + יחס 4:3 ללא עיוות' },
    { id: 'zoom', label: 'חידוד רטרו: 16:9 מילוי', sharpen: true, aspectClass: 'aspect-zoom', desc: 'פילטר חידוד + חיתוך חכם 16:9' },
    { id: 'stretch', label: 'חידוד רטרו: מתיחה', sharpen: true, aspectClass: 'aspect-stretch', desc: 'פילטר חידוד + מתיחה מלאה' }
  ];

  // DOM ELEMENTS CACHE
  let el = {};

  // INITIALIZATION
  window.addEventListener('DOMContentLoaded', initApp);

  function initApp() {
    cacheDomElements();
    loadTheme();
    loadFavorites();
    loadChannels();
    setupClock();
    setupOledMode();
    setupRetroMode();
    setupWebPlayerControls();
    setupKeyListeners();
    setupMouseListeners();
    resetScreensaverTimer();

    // Initial Focus on the first channel card
    setFocus('grid', 0);
  }

  function cacheDomElements() {
    el = {
      app: document.getElementById('app'),
      search: document.getElementById('channel-search'),
      channelsGrid: document.getElementById('channels-grid'),
      categoryList: document.getElementById('category-list'),
      channelsCountBadge: document.getElementById('channels-count-badge'),
      currentCategoryTitle: document.getElementById('current-category-title'),
      categoryMetaText: document.getElementById('category-meta-text'),
      liveClock: document.getElementById('live-clock'),

      // Theme Toggle
      btnThemeToggle: document.getElementById('btn-theme-toggle'),
      themeBtnIcon: document.getElementById('theme-btn-icon'),
      themeBtnLabel: document.getElementById('theme-btn-label'),

      // Hero Spotlight
      heroSpotlight: document.getElementById('hero-spotlight'),
      heroNumber: document.getElementById('hero-number'),
      heroCategory: document.getElementById('hero-category'),
      heroBadgeTag: document.getElementById('hero-badge-tag'),
      heroFavTag: document.getElementById('hero-fav-tag'),
      heroTitle: document.getElementById('hero-title'),
      heroDesc: document.getElementById('hero-desc'),
      heroLogo: document.getElementById('hero-logo'),

      // Quick Badges
      btnOled: document.getElementById('btn-oled-mode'),
      btnRetro: document.getElementById('btn-retro-mode'),
      oledLabel: document.getElementById('oled-mode-label'),
      retroLabel: document.getElementById('retro-mode-label'),

      // Direct Number HUD
      channelNumberHud: document.getElementById('channel-number-hud'),
      hudDigits: document.getElementById('hud-digits'),
      hudChannelName: document.getElementById('hud-channel-name'),

      // Player Container
      playerContainer: document.getElementById('player-container'),
      video: document.getElementById('tv-video'),
      spinner: document.getElementById('player-spinner'),
      spinnerText: document.getElementById('spinner-text'),
      osd: document.getElementById('player-osd'),
      osdNumber: document.getElementById('osd-number'),
      osdLogo: document.getElementById('osd-logo'),
      osdName: document.getElementById('osd-name'),
      osdCategory: document.getElementById('osd-category'),
      osdRetroStatus: document.getElementById('osd-retro-status'),
      osdAspectTag: document.getElementById('osd-aspect-tag'),
      osdClock: document.getElementById('osd-clock'),
      osdOledStatus: document.getElementById('osd-oled-status'),
      osdAspectStatus: document.getElementById('osd-aspect-status'),
      osdQuality: document.getElementById('osd-quality'),

      // Web Player Actions
      webBtnBack: document.getElementById('web-btn-back'),
      webBtnPlayPause: document.getElementById('web-btn-playpause'),
      webPlayIcon: document.getElementById('web-play-icon'),
      webBtnMute: document.getElementById('web-btn-mute'),
      webMuteIcon: document.getElementById('web-mute-icon'),
      webBtnAspect: document.getElementById('web-btn-aspect'),
      webBtnFullscreen: document.getElementById('web-btn-fullscreen'),

      // Drawer
      quickDrawer: document.getElementById('quick-drawer'),
      drawerChannelList: document.getElementById('drawer-channel-list'),

      // Toast
      toast: document.getElementById('player-toast'),
      toastMessage: document.getElementById('toast-message'),

      // Screensaver
      screensaver: document.getElementById('oled-screensaver'),
      screensaverClock: document.getElementById('screensaver-clock'),

      // Exit Modal
      exitModal: document.getElementById('exit-modal'),
      btnCancelExit: document.getElementById('btn-cancel-exit'),
      btnConfirmExit: document.getElementById('btn-confirm-exit')
    };
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

    if (el.btnThemeToggle) {
      el.btnThemeToggle.addEventListener('click', toggleTheme);
    }
  }

  function toggleTheme() {
    state.theme = state.theme === 'oled' ? 'light' : 'oled';
    try {
      localStorage.setItem('israel_tv_theme', state.theme);
    } catch (e) {}
    applyTheme();
    showToast(state.theme === 'light' ? '☀️ הופעל מצב יום (Light Mode)' : '🌙 הופעל מצב לילה (OLED True Black)');
  }

  function applyTheme() {
    if (state.theme === 'light') {
      document.body.className = 'light-theme';
      if (el.themeBtnIcon) el.themeBtnIcon.textContent = '🌙';
      if (el.themeBtnLabel) el.themeBtnLabel.textContent = 'מצב לילה (OLED)';
    } else {
      document.body.className = 'oled-theme';
      if (el.themeBtnIcon) el.themeBtnIcon.textContent = '☀️';
      if (el.themeBtnLabel) el.themeBtnLabel.textContent = 'מצב יום (Light)';
    }
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
    const cards = el.channelsGrid.querySelectorAll('.channel-card');
    cards.forEach(card => {
      if (card.dataset.id === channelId) {
        const star = card.querySelector('.card-fav-star');
        if (star) star.style.display = isFav ? 'inline-block' : 'none';
      }
    });
  }

  // CLOCK
  function setupClock() {
    function update() {
      const now = new Date();
      const timeStr = now.toLocaleTimeString('he-IL', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
      const shortStr = now.toLocaleTimeString('he-IL', { hour: '2-digit', minute: '2-digit' });
      if (el.liveClock) el.liveClock.textContent = timeStr;
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

    if (el.btnOled) {
      el.btnOled.addEventListener('click', cycleOledMode);
    }
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
    if (el.oledLabel) el.oledLabel.textContent = `OLED: ${current.label}`;
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

    if (el.btnRetro) {
      el.btnRetro.addEventListener('click', cycleRetroMode);
    }
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

      if (current.sharpen) {
        el.video.classList.add('retro-sharpen-active');
        if (el.osdRetroStatus) el.osdRetroStatus.classList.remove('hidden');
      } else {
        el.video.classList.remove('retro-sharpen-active');
        if (el.osdRetroStatus) el.osdRetroStatus.classList.add('hidden');
      }

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
      if (state.currentSection !== 'player' || (el.video && el.video.paused)) {
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
    const raw = window.CHANNELS_DATA || [];
    state.allChannels = raw.map((c, i) => ({
      ...c,
      number: c.number || (i + 1),
      category: c.category || 'ערוצים',
      desc: c.desc || 'שידור חי באיכות HD'
    }));

    buildCategories();
    applyFilter();
  }

  function buildCategories() {
    const catMap = {};
    state.allChannels.forEach(c => {
      const cat = c.category;
      catMap[cat] = (catMap[cat] || 0) + 1;
    });

    const list = [
      { id: 'ALL', name: 'כל הערוצים 📺', count: state.allChannels.length }
    ];

    if (state.favorites.length > 0) {
      list.push({ id: 'FAVORITES', name: 'מועדפים ⭐', count: state.favorites.length });
    }

    // Ordered categories
    const priority = [
      'ערוצי ישראל - שידור חי',
      'ילדים ונוער (בעברית)',
      'סרטים וסדרות (Movies & Series)',
      'ערוצי ספורט (Sports)',
      'מוזיקה ורדיו חי (Music & Radio)',
      'לייף סטייל ובידור',
      'דוקו וטבע',
      'ערוצים בינלאומיים (World)'
    ];

    priority.forEach(cat => {
      if (catMap[cat]) {
        list.push({ id: cat, name: cat, count: catMap[cat] });
        delete catMap[cat];
      }
    });

    // Any remaining
    Object.keys(catMap).forEach(cat => {
      list.push({ id: cat, name: cat, count: catMap[cat] });
    });

    state.categories = list;
    renderSidebar();
  }

  function renderSidebar() {
    el.categoryList.innerHTML = '';
    state.categories.forEach((cat, idx) => {
      const item = document.createElement('div');
      item.className = `category-item ${cat.id === state.activeCategory ? 'active' : ''}`;
      item.dataset.index = idx;
      item.dataset.id = cat.id;

      item.innerHTML = `
        <span>${cat.name}</span>
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
    renderSidebar();
    applyFilter();
  }

  function applyFilter() {
    const q = state.searchQuery.trim().toLowerCase();
    let result = state.allChannels;

    if (state.activeCategory === 'FAVORITES') {
      result = result.filter(c => state.favorites.includes(c.id));
    } else if (state.activeCategory !== 'ALL') {
      result = result.filter(c => c.category === state.activeCategory);
    }

    if (q) {
      result = result.filter(c =>
        c.name.toLowerCase().includes(q) ||
        (c.title && c.title.toLowerCase().includes(q)) ||
        (c.number && c.number.toString() === q)
      );
    }

    state.filteredChannels = result;

    const currentCatObj = state.categories.find(c => c.id === state.activeCategory);
    const catName = currentCatObj ? currentCatObj.name : 'ערוצים';
    el.currentCategoryTitle.textContent = catName;
    el.categoryMetaText.textContent = `מציג ${result.length} ערוצים`;
    if (el.channelsCountBadge) el.channelsCountBadge.textContent = `${state.allChannels.length} ערוצים`;

    renderGrid();
    renderDrawerList();

    if (state.filteredChannels.length > 0) {
      state.focusedGridIdx = Math.min(state.focusedGridIdx, state.filteredChannels.length - 1);
      updateHeroSpotlight(state.filteredChannels[state.focusedGridIdx]);
    }
  }

  // RENDER CHANNELS GRID
  function renderGrid() {
    el.channelsGrid.innerHTML = '';

    if (state.filteredChannels.length === 0) {
      el.channelsGrid.innerHTML = `
        <div style="grid-column: 1 / -1; text-align: center; padding: 60px 0; color: var(--text-muted); font-size: 18px;">
          לא נמצאו ערוצים מתאימים לקטגוריה או לחיפוש הנוכחי
        </div>
      `;
      return;
    }

    state.filteredChannels.forEach((ch, idx) => {
      const card = document.createElement('div');
      const isPlaying = state.playingChannelIndex !== -1 && state.filteredChannels[state.playingChannelIndex]?.id === ch.id;
      const isFav = state.favorites.includes(ch.id);

      card.className = `channel-card ${isPlaying ? 'is-playing' : ''}`;
      card.dataset.index = idx;
      card.dataset.id = ch.id;

      card.innerHTML = `
        <div class="card-top">
          <span class="channel-num-badge">#${ch.number}</span>
          <div class="card-top-right">
            <span class="card-playing-indicator"><span class="pulse-dot"></span>משדר</span>
            <span class="card-fav-star" style="display: ${isFav ? 'inline-block' : 'none'};">⭐</span>
          </div>
        </div>
        <div class="channel-logo-wrap">
          <img class="channel-logo-img" src="${ch.logo}" alt="${ch.name}" loading="lazy"
               onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
          <div class="channel-logo-fallback" style="display: none;">
            <span class="fallback-num">#${ch.number}</span>
            <span class="fallback-name">${ch.name}</span>
          </div>
        </div>
        <div class="channel-card-footer">
          <div class="channel-card-title">${ch.name}</div>
        </div>
      `;

      card.addEventListener('click', () => {
        setFocus('grid', idx);
        playChannel(idx);
      });

      card.addEventListener('mouseenter', () => {
        setFocus('grid', idx);
      });

      el.channelsGrid.appendChild(card);
    });

    highlightFocusedItem();
  }

  // HERO SPOTLIGHT
  function updateHeroSpotlight(ch) {
    if (!ch) return;

    el.heroNumber.textContent = `#${ch.number}`;
    el.heroCategory.textContent = ch.category;
    el.heroBadgeTag.textContent = ch.badge || 'FHD 1080p';
    el.heroTitle.textContent = ch.name;
    el.heroDesc.textContent = ch.desc || 'שידור חי רציף ואיכותי';

    const isFav = state.favorites.includes(ch.id);
    if (el.heroFavTag) {
      if (isFav) el.heroFavTag.classList.remove('hidden');
      else el.heroFavTag.classList.add('hidden');
    }

    if (el.heroLogo) {
      el.heroLogo.style.display = 'block';
      el.heroLogo.src = ch.logo;
    }
  }

  // FOCUS SYSTEM
  function setFocus(section, index) {
    state.currentSection = section;

    if (section === 'grid') {
      state.focusedGridIdx = Math.max(0, Math.min(index, state.filteredChannels.length - 1));
      const ch = state.filteredChannels[state.focusedGridIdx];
      updateHeroSpotlight(ch);
    } else if (section === 'sidebar') {
      state.focusedCategoryIdx = Math.max(0, Math.min(index, state.categories.length - 1));
    } else if (section === 'drawer') {
      state.focusedDrawerIdx = Math.max(0, Math.min(index, state.filteredChannels.length - 1));
    } else if (section === 'exit_modal') {
      state.focusedExitBtn = index;
    }

    highlightFocusedItem();
  }

  function highlightFocusedItem() {
    document.querySelectorAll('.focused').forEach(elem => elem.classList.remove('focused'));

    if (state.currentSection === 'grid') {
      const cards = el.channelsGrid.querySelectorAll('.channel-card');
      const target = cards[state.focusedGridIdx];
      if (target) {
        target.classList.add('focused');
        target.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
      }
    } else if (state.currentSection === 'sidebar') {
      const items = el.categoryList.querySelectorAll('.category-item');
      const target = items[state.focusedCategoryIdx];
      if (target) {
        target.classList.add('focused');
        target.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
      }
    } else if (state.currentSection === 'search') {
      el.search.classList.add('focused');
      el.search.focus();
    } else if (state.currentSection === 'drawer') {
      const items = el.drawerChannelList.querySelectorAll('.drawer-item');
      const target = items[state.focusedDrawerIdx];
      if (target) {
        target.classList.add('focused');
        target.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
      }
    } else if (state.currentSection === 'exit_modal') {
      if (state.focusedExitBtn === 0) el.btnCancelExit.classList.add('focused');
      else el.btnConfirmExit.classList.add('focused');
    }
  }

  // PLAYBACK SYSTEM
  function playChannel(index) {
    if (index < 0 || index >= state.filteredChannels.length) return;

    state.playingChannelIndex = index;
    const channel = state.filteredChannels[index];

    const cards = el.channelsGrid.querySelectorAll('.channel-card');
    cards.forEach((c, idx) => {
      c.classList.toggle('is-playing', idx === index);
    });

    enterFullscreenPlayer();
    updateOSD(channel);
    showOSD();
    showSpinner('מתחבר לשידור חי...');

    loadVideoStream(channel.url);
  }

  // HLS LOADER WITH DYNAMIC CORS / HTTPS PROXY FOR WEB BROWSERS
  function createHlsLoader() {
    if (!window.Hls || !Hls.DefaultConfig || !Hls.DefaultConfig.loader) {
      return null;
    }
    const BaseLoader = Hls.DefaultConfig.loader;
    return class WebCorsLoader extends BaseLoader {
      constructor(config) {
        super(config);
        const originalLoad = this.load.bind(this);
        this.load = function (context, config, callbacks) {
          if (window.location.protocol === 'https:' && context.url && context.url.startsWith('http://')) {
            context.url = 'https://corsproxy.io/?url=' + encodeURIComponent(context.url);
          }
          originalLoad(context, config, callbacks);
        };
      }
    };
  }

  const WebCorsLoaderClass = createHlsLoader();

  function loadVideoStream(url) {
    if (state.hlsInstance) {
      state.hlsInstance.destroy();
      state.hlsInstance = null;
    }

    if (window.Hls && Hls.isSupported()) {
      const hlsConfig = {
        enableWorker: true,
        lowLatencyMode: true,
        backBufferLength: 60
      };

      if (window.location.protocol === 'https:' && WebCorsLoaderClass) {
        hlsConfig.pLoader = WebCorsLoaderClass;
        hlsConfig.fLoader = WebCorsLoaderClass;
        hlsConfig.loader = WebCorsLoaderClass;
      }

      const hls = new Hls(hlsConfig);

      hls.loadSource(url);
      hls.attachMedia(el.video);

      hls.on(Hls.Events.MANIFEST_PARSED, () => {
        hideSpinner();
        el.video.play().catch(() => {});
        updatePlayPauseButton(false);
      });

      hls.on(Hls.Events.ERROR, (event, data) => {
        if (data.fatal) {
          switch (data.type) {
            case Hls.ErrorTypes.NETWORK_ERROR:
              showToast('שגיאת רשת בשידור, מנסה להתחבר מחדש...');
              hls.startLoad();
              break;
            case Hls.ErrorTypes.MEDIA_ERROR:
              showToast('מתקן שגיאת וידאו...');
              hls.recoverMediaError();
              break;
            default:
              showToast('לא ניתן לטעון שידור זה כעת');
              hideSpinner();
              break;
          }
        }
      });

      state.hlsInstance = hls;
    } else if (el.video.canPlayType('application/vnd.apple.mpegurl')) {
      let finalUrl = url;
      if (window.location.protocol === 'https:' && url.startsWith('http://')) {
        finalUrl = 'https://corsproxy.io/?url=' + encodeURIComponent(url);
      }
      el.video.src = finalUrl;
      el.video.addEventListener('loadedmetadata', () => {
        hideSpinner();
        el.video.play().catch(() => {});
        updatePlayPauseButton(false);
      }, { once: true });
    } else {
      el.video.src = url;
      el.video.play().catch(() => {});
      updatePlayPauseButton(false);
    }
  }

  // INTERACTIVE WEB PLAYER CONTROLS
  function setupWebPlayerControls() {
    if (el.webBtnBack) {
      el.webBtnBack.addEventListener('click', (e) => {
        e.stopPropagation();
        handleBackKey();
      });
    }

    if (el.webBtnPlayPause) {
      el.webBtnPlayPause.addEventListener('click', (e) => {
        e.stopPropagation();
        togglePlayPause();
      });
    }

    if (el.webBtnMute) {
      el.webBtnMute.addEventListener('click', (e) => {
        e.stopPropagation();
        toggleMute();
      });
    }

    if (el.webBtnAspect) {
      el.webBtnAspect.addEventListener('click', (e) => {
        e.stopPropagation();
        cycleRetroMode();
      });
    }

    if (el.webBtnFullscreen) {
      el.webBtnFullscreen.addEventListener('click', (e) => {
        e.stopPropagation();
        toggleBrowserFullscreen();
      });
    }

    if (el.video) {
      el.video.addEventListener('play', () => updatePlayPauseButton(false));
      el.video.addEventListener('pause', () => updatePlayPauseButton(true));
      el.video.addEventListener('volumechange', () => {
        if (el.webMuteIcon) {
          el.webMuteIcon.textContent = (el.video.muted || el.video.volume === 0) ? '🔇' : '🔊';
        }
      });
    }

    if (el.playerContainer) {
      el.playerContainer.addEventListener('mousemove', () => {
        showOSD();
        resetScreensaverTimer();
      });

      el.playerContainer.addEventListener('click', (e) => {
        if (e.target.closest('.osd-web-actions') || e.target.closest('.quick-drawer') || e.target.closest('.remote-hints')) return;
        if (el.osd.classList.contains('hidden')) {
          showOSD();
        } else {
          el.osd.classList.add('hidden');
        }
      });

      el.playerContainer.addEventListener('dblclick', (e) => {
        if (e.target.closest('.osd-web-actions') || e.target.closest('.quick-drawer')) return;
        toggleBrowserFullscreen();
      });
    }
  }

  function togglePlayPause() {
    if (!el.video) return;
    if (el.video.paused) {
      el.video.play().then(() => {
        updatePlayPauseButton(false);
        showToast('מנגן ▶️');
      }).catch(() => {});
    } else {
      el.video.pause();
      updatePlayPauseButton(true);
      showToast('מושהה ⏸️');
    }
  }

  function updatePlayPauseButton(isPaused) {
    if (el.webPlayIcon) {
      el.webPlayIcon.textContent = isPaused ? '▶️' : '⏸️';
    }
  }

  function toggleMute() {
    if (!el.video) return;
    el.video.muted = !el.video.muted;
    if (el.webMuteIcon) {
      el.webMuteIcon.textContent = el.video.muted ? '🔇' : '🔊';
    }
    showToast(el.video.muted ? 'מושתק 🔇' : 'שמע מופעל 🔊');
  }

  function toggleBrowserFullscreen() {
    const doc = document;
    const docEl = document.documentElement;

    if (!doc.fullscreenElement && !doc.webkitFullscreenElement) {
      if (docEl.requestFullscreen) {
        docEl.requestFullscreen();
      } else if (docEl.webkitRequestFullscreen) {
        docEl.webkitRequestFullscreen();
      }
      showToast('מסך מלא הופעל ⛶');
    } else {
      if (doc.exitFullscreen) {
        doc.exitFullscreen();
      } else if (doc.webkitExitFullscreen) {
        doc.webkitExitFullscreen();
      }
      showToast('יצאת ממסך מלא');
    }
  }

  function enterFullscreenPlayer() {
    state.currentSection = 'player';
    el.playerContainer.classList.remove('hidden');
    showOSD();
  }

  function exitFullscreenToGrid() {
    // Exit fullscreen, keep video playing smoothly in background!
    el.playerContainer.classList.add('hidden');
    closeQuickDrawer();
    state.currentSection = 'grid';
    setFocus('grid', state.focusedGridIdx);
    showToast('חזרת למדריך הערוצים (השידור ממשיך ברקע)');
  }

  function stopPlayback() {
    if (state.hlsInstance) {
      state.hlsInstance.destroy();
      state.hlsInstance = null;
    }
    el.video.pause();
    el.video.src = '';
    state.playingChannelIndex = -1;
    exitFullscreenToGrid();
  }

  function zapChannel(direction) {
    if (state.filteredChannels.length === 0) return;
    let next = state.playingChannelIndex + direction;
    if (next < 0) next = state.filteredChannels.length - 1;
    if (next >= state.filteredChannels.length) next = 0;

    playChannel(next);
  }

  // OSD & TOAST
  function updateOSD(channel) {
    el.osdNumber.textContent = `#${channel.number}`;
    el.osdName.textContent = channel.name;
    el.osdCategory.textContent = channel.category;

    if (el.osdLogo) {
      el.osdLogo.style.display = 'block';
      el.osdLogo.src = channel.logo;
    }
  }

  function showOSD() {
    el.osd.classList.remove('hidden');
    if (state.osdTimeout) clearTimeout(state.osdTimeout);
    state.osdTimeout = setTimeout(() => {
      el.osd.classList.add('hidden');
    }, 4500);
  }

  function showToast(msg) {
    el.toastMessage.textContent = msg;
    el.toast.classList.remove('hidden');
    if (state.toastTimeout) clearTimeout(state.toastTimeout);
    state.toastTimeout = setTimeout(() => {
      el.toast.classList.add('hidden');
    }, 3000);
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
    el.drawerChannelList.innerHTML = '';
    state.filteredChannels.forEach((ch, idx) => {
      const item = document.createElement('div');
      item.className = 'drawer-item';
      item.dataset.index = idx;
      item.innerHTML = `
        <span class="drawer-num">#${ch.number}</span>
        <span class="drawer-name">${ch.name}</span>
      `;
      item.addEventListener('click', () => {
        closeQuickDrawer();
        playChannel(idx);
      });
      el.drawerChannelList.appendChild(item);
    });
  }

  function openQuickDrawer() {
    state.drawerOpen = true;
    el.quickDrawer.classList.remove('hidden');
    setFocus('drawer', state.playingChannelIndex !== -1 ? state.playingChannelIndex : 0);
  }

  function closeQuickDrawer() {
    state.drawerOpen = false;
    el.quickDrawer.classList.add('hidden');
    if (state.currentSection === 'drawer') {
      state.currentSection = 'player';
    }
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
        const gridIdx = state.filteredChannels.findIndex(c => c.id === target.id);
        if (gridIdx !== -1) {
          setFocus('grid', gridIdx);
          playChannel(gridIdx);
        } else {
          selectCategory('ALL', 0);
          const newIdx = state.filteredChannels.findIndex(c => c.id === target.id);
          if (newIdx !== -1) {
            setFocus('grid', newIdx);
            playChannel(newIdx);
          }
        }
      } else {
        showToast(`ערוץ מספר ${finalNum} לא קיים`);
      }
    }, 1200);
  }

  // SMART BACK BUTTON: NEVER ACCIDENTALLY EXITS!
  function handleBackKey() {
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
      showToast('חזרת לכל הערוצים');
      return;
    }

    // 7. If video is currently playing in background, return to Fullscreen!
    if (state.playingChannelIndex !== -1 && el.video && !el.video.paused) {
      enterFullscreenPlayer();
      return;
    }

    // 8. If already at the root of the Guide:
    // Require double-click within 2.5 seconds to exit gracefully.
    const now = Date.now();
    if (now - state.lastBackPressTime < 2500) {
      performActualExit();
    } else {
      state.lastBackPressTime = now;
      showToast('לחץ שוב לחזרה / יציאה מהאפליקציה');
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
      if (window.webOS && window.webOS.platformBack) {
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
      applyFilter();
    });

    el.btnCancelExit.addEventListener('click', closeExitModal);
    el.btnConfirmExit.addEventListener('click', performActualExit);
  }

  function handleKeyDown(e) {
    resetScreensaverTimer();

    const code = e.keyCode || e.which;

    // Web & Keyboard Shortcuts (when not focused on search input)
    if (state.currentSection !== 'search') {
      // Space (32) -> Play / Pause
      if (code === 32) {
        if (state.currentSection === 'player' || (el.video && !el.video.paused)) {
          e.preventDefault();
          togglePlayPause();
          return;
        }
      }

      // 'F' or 'f' (70) -> Browser Fullscreen
      if (code === 70) {
        e.preventDefault();
        toggleBrowserFullscreen();
        return;
      }

      // 'M' or 'm' (77) -> Mute / Unmute
      if (code === 77) {
        e.preventDefault();
        toggleMute();
        return;
      }

      // Number keys (0-9)
      if (code >= 48 && code <= 57) {
        handleDigitInput(String(code - 48));
        return;
      }
      // Numpad (0-9)
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
        if (state.currentSection !== 'search' || el.search.value === '') {
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

      // YELLOW BUTTON (405) -> In Grid: Toggle Favorite ⭐ | In Player: Cycle OLED Mode
      case 405:
        e.preventDefault();
        if (state.currentSection === 'player') {
          cycleOledMode();
        } else {
          const ch = state.filteredChannels[state.focusedGridIdx];
          if (ch) toggleFavorite(ch.id);
        }
        break;

      // BLUE BUTTON (406) -> Search
      case 406:
        e.preventDefault();
        setFocus('search', 0);
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
    if (total === 0) return;

    const COLS = 5;
    let next = state.focusedGridIdx;

    if (code === 37) { // Left (RTL moves to next card on the left)
      e.preventDefault();
      if (next % COLS < COLS - 1 && next < total - 1) {
        setFocus('grid', next + 1);
      }
    } else if (code === 39) { // Right (RTL moves to previous card or opens sidebar)
      e.preventDefault();
      if (next % COLS > 0) {
        setFocus('grid', next - 1);
      } else {
        setFocus('sidebar', state.focusedCategoryIdx);
      }
    } else if (code === 38) { // Up
      e.preventDefault();
      if (next - COLS >= 0) {
        setFocus('grid', next - COLS);
      } else {
        setFocus('search', 0);
      }
    } else if (code === 40) { // Down
      e.preventDefault();
      if (next + COLS < total) {
        setFocus('grid', next + COLS);
      }
    } else if (code === 13) { // Enter / OK
      e.preventDefault();
      playChannel(state.focusedGridIdx);
    }
  }

  function handleSidebarKeys(code, e) {
    const total = state.categories.length;
    let next = state.focusedCategoryIdx;

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
    if (code === 40) {
      e.preventDefault();
      setFocus('grid', 0);
    } else if (code === 13) {
      e.preventDefault();
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
      if (el.video.paused) {
        el.video.play();
        showToast('מנגן');
      } else {
        el.video.pause();
        showToast('מושהה');
      }
    }
  }

  function handleDrawerKeys(code, e) {
    const total = state.filteredChannels.length;
    let next = state.focusedDrawerIdx;

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
    } else if (code === 39 || code === 461 || code === 27) {
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
    } else if (code === 461 || code === 27) {
      e.preventDefault();
      closeExitModal();
    }
  }

  // MAGIC REMOTE MOUSE SUPPORT
  function setupMouseListeners() {
    window.addEventListener('mousemove', resetScreensaverTimer);
    window.addEventListener('click', resetScreensaverTimer);
  }

})();
