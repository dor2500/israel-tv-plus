// ISRAEL TV PLUS - OLED ULTRA & WEBOS ACCELERATED PLAYER

(function () {
  'use strict';

  // OLED PICTURE ENHANCER PROFILES
  const OLED_MODES = [
    { id: 'vivid', label: 'OLED: צבעים עשירים', filterClass: 'oled-filter-vivid', description: 'ניגודיות וצבעים חיים' },
    { id: 'cinema', label: 'OLED: שחור קולנועי', filterClass: 'oled-filter-cinema', description: 'שחור מוחלט 0-Nit' },
    { id: 'sports', label: 'OLED: ספורט חד', filterClass: 'oled-filter-sports', description: 'חדות גבוהה לתנועה' },
    { id: 'natural', label: 'OLED: טבעי', filterClass: 'oled-filter-natural', description: 'שידור טבעי ללא שינוי' }
  ];

  // RETRO REMASTER & ASPECT PROFILES FOR OLD SERIES
  const RETRO_MODES = [
    { id: 'off', label: 'חידוד רטרו: כבוי', aspectClass: 'aspect-fit', sharpen: false, desc: 'שידור רגיל' },
    { id: 'sharpen', label: 'חידוד רטרו: פעיל', aspectClass: 'aspect-fit', sharpen: true, desc: 'חידוד פנים וטקסטורות SD' },
    { id: 'zoom', label: 'רטרו + מילוי מסך 16:9', aspectClass: 'aspect-zoom', sharpen: true, desc: 'חידוד + מילוי מסך ללא פסים' },
    { id: 'pillarbox', label: 'רטרו + 4:3 קלאסי', aspectClass: 'aspect-43', sharpen: true, desc: 'יחס 4:3 מדויק לסדרות ישנות' },
    { id: 'stretch', label: 'רטרו + מתיחה מלאה', aspectClass: 'aspect-stretch', sharpen: true, desc: 'מתיחה לכל רוחב המסך' }
  ];

  // APPLICATION STATE
  const state = {
    allChannels: window.CHANNELS_DATA || [],
    filteredChannels: [],
    categories: [],
    activeCategory: 'ALL',
    searchQuery: '',
    
    // Focus & Navigation
    currentSection: 'grid', // 'sidebar' | 'grid' | 'search' | 'player' | 'drawer' | 'exit_modal'
    focusedCategoryIdx: 0,
    focusedGridIdx: 0,
    focusedDrawerIdx: 0,
    focusedExitBtn: 0, // 0 = cancel, 1 = confirm
    
    // OLED & Retro State
    oledModeIdx: 0,
    retroModeIdx: 0,
    screensaverActive: false,
    screensaverTimeout: null,
    
    // Player State
    currentChannelIdx: -1,
    hls: null,
    isPlaying: false,
    osdTimeout: null,
    drawerOpen: false,
    exitModalOpen: false
  };

  // DOM ELEMENTS
  const el = {
    clock: document.getElementById('live-clock'),
    osdClock: document.getElementById('osd-clock'),
    screensaverClock: document.getElementById('screensaver-clock'),
    search: document.getElementById('channel-search'),
    categoryList: document.getElementById('category-list'),
    channelsGrid: document.getElementById('channels-grid'),
    categoryTitle: document.getElementById('current-category-title'),
    categoryMeta: document.getElementById('category-meta-text'),
    channelsCountBadge: document.getElementById('channels-count-badge'),
    
    // Hero Spotlight
    heroSpotlight: document.getElementById('hero-spotlight'),
    heroNumber: document.getElementById('hero-number'),
    heroCategory: document.getElementById('hero-category'),
    heroBadgeTag: document.getElementById('hero-badge-tag'),
    heroTitle: document.getElementById('hero-title'),
    heroDesc: document.getElementById('hero-desc'),
    heroLogo: document.getElementById('hero-logo'),
    
    // Badges
    btnOled: document.getElementById('btn-oled-mode'),
    oledLabel: document.getElementById('oled-mode-label'),
    osdOledStatus: document.getElementById('osd-oled-status'),
    btnRetro: document.getElementById('btn-retro-mode'),
    retroLabel: document.getElementById('retro-mode-label'),
    osdRetroStatus: document.getElementById('osd-retro-status'),
    osdAspectStatus: document.getElementById('osd-aspect-status'),
    osdAspectTag: document.getElementById('osd-aspect-tag'),
    screensaver: document.getElementById('oled-screensaver'),
    
    // Player
    playerContainer: document.getElementById('player-container'),
    video: document.getElementById('tv-video'),
    spinner: document.getElementById('player-spinner'),
    spinnerText: document.getElementById('spinner-text'),
    osd: document.getElementById('player-osd'),
    osdNumber: document.getElementById('osd-number'),
    osdLogo: document.getElementById('osd-logo'),
    osdName: document.getElementById('osd-name'),
    osdCategory: document.getElementById('osd-category'),
    osdQuality: document.getElementById('osd-quality'),
    quickDrawer: document.getElementById('quick-drawer'),
    drawerList: document.getElementById('drawer-channel-list'),
    toast: document.getElementById('player-toast'),
    toastMessage: document.getElementById('toast-message'),

    // Exit Modal
    exitModal: document.getElementById('exit-modal'),
    btnCancelExit: document.getElementById('btn-cancel-exit'),
    btnConfirmExit: document.getElementById('btn-confirm-exit')
  };

  // INITIALIZATION
  function init() {
    setupClock();
    setupOledMode();
    setupRetroMode();
    extractCategories();
    renderCategories();
    applyFilter();
    setupKeyListeners();
    setupMouseListeners();
    resetScreensaverTimer();
    
    // Default focus
    setFocus('grid', 0);
  }

  // CLOCK
  function setupClock() {
    function update() {
      const now = new Date();
      const timeStr = now.toLocaleTimeString('he-IL', { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false });
      const shortStr = now.toLocaleTimeString('he-IL', { hour: '2-digit', minute: '2-digit', hour12: false });
      if (el.clock) el.clock.textContent = timeStr;
      if (el.osdClock) el.osdClock.textContent = shortStr;
      if (el.screensaverClock) el.screensaverClock.textContent = shortStr;
    }
    update();
    setInterval(update, 1000);
  }

  // OLED ENHANCER LOGIC
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
    showToast(`✨ משפר תמונה OLED: ${current.label} (${current.description})`);
  }

  function applyOledMode() {
    const current = OLED_MODES[state.oledModeIdx];
    if (el.oledLabel) el.oledLabel.textContent = current.label;
    if (el.osdOledStatus) el.osdOledStatus.textContent = current.label.replace('OLED: ', '');

    if (el.video) {
      OLED_MODES.forEach(m => el.video.classList.remove(m.filterClass));
      el.video.classList.add(current.filterClass);
    }
  }

  // RETRO REMASTER LOGIC (SD / OLD SERIES UPSCALE)
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
      // Remove all aspect classes
      RETRO_MODES.forEach(m => el.video.classList.remove(m.aspectClass));
      el.video.classList.add(current.aspectClass);

      // Apply SVG sharpen filter
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

  // OLED BURN-IN PROTECTION & IDLE SCREENSAVER
  function resetScreensaverTimer() {
    if (state.screensaverActive) {
      wakeScreensaver();
    }
    if (state.screensaverTimeout) clearTimeout(state.screensaverTimeout);

    state.screensaverTimeout = setTimeout(() => {
      if (state.currentSection !== 'player' || el.video.paused) {
        startScreensaver();
      }
    }, 90000);
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

  // CATEGORIES
  function extractCategories() {
    const counts = {};
    state.allChannels.forEach(c => {
      const cat = c.category || 'כללי';
      counts[cat] = (counts[cat] || 0) + 1;
    });

    state.categories = [
      { id: 'ALL', name: 'כל הערוצים', icon: '🌟', count: state.allChannels.length }
    ];

    const priority = [
      { id: 'ערוצי ישראל - שידור חי', icon: '📺' },
      { id: 'ילדים ונוער (Kids)', icon: '👶' },
      { id: 'סרטים וסדרות (Movies & Series)', icon: '🎬' },
      { id: 'ספורט (Sports)', icon: '⚽' },
      { id: 'טבע ודוקו (Documentary & Nature)', icon: '🌿' },
      { id: 'חדשות עולמיות (World News)', icon: '🌍' },
      { id: 'קולנוע עולמי (World Movies)', icon: '🍿' },
      { id: 'מוזיקה בינלאומית (Music)', icon: '🎵' },
      { id: 'רדיו ישראלי', icon: '📻' },
      { id: 'מסורת וקהילה', icon: '🕊️' }
    ];

    priority.forEach(p => {
      if (counts[p.id]) {
        state.categories.push({ id: p.id, name: p.id, icon: p.icon, count: counts[p.id] });
        delete counts[p.id];
      }
    });

    Object.keys(counts).forEach(k => {
      state.categories.push({ id: k, name: k, icon: '📺', count: counts[k] });
    });
  }

  function renderCategories() {
    el.categoryList.innerHTML = '';
    state.categories.forEach((cat, idx) => {
      const item = document.createElement('div');
      item.className = 'category-item' + (cat.id === state.activeCategory ? ' active' : '');
      item.dataset.index = idx;
      item.dataset.id = cat.id;
      item.innerHTML = `
        <span>${cat.icon || ''} ${cat.name}</span>
        <span class="category-count">${cat.count}</span>
      `;
      item.addEventListener('click', () => {
        selectCategory(cat.id, idx);
      });
      el.categoryList.appendChild(item);
    });
  }

  function selectCategory(catId, idx) {
    state.activeCategory = catId;
    state.focusedCategoryIdx = idx;
    renderCategories();
    applyFilter();
    setFocus('grid', 0);
  }

  // FILTERING
  function applyFilter() {
    let list = state.allChannels;

    if (state.activeCategory !== 'ALL') {
      list = list.filter(c => c.category === state.activeCategory);
    }

    if (state.searchQuery.trim()) {
      const q = state.searchQuery.trim().toLowerCase();
      list = list.filter(c => {
        const name = (c.name || '').toLowerCase();
        const title = (c.title || '').toLowerCase();
        const num = String(c.number || '');
        return name.includes(q) || title.includes(q) || num === q;
      });
    }

    state.filteredChannels = list;

    const activeCatObj = state.categories.find(c => c.id === state.activeCategory);
    el.categoryTitle.textContent = activeCatObj ? activeCatObj.name : 'ערוצים';
    el.categoryMeta.textContent = `מציג ${state.filteredChannels.length} מתוך ${state.allChannels.length} ערוצים`;
    el.channelsCountBadge.textContent = `${state.allChannels.length} ערוצים`;

    renderGrid();
  }

  // GRID RENDERING - ULTRA FAST WITH HARDWARE ACCELERATION
  function renderGrid() {
    el.channelsGrid.innerHTML = '';

    if (state.filteredChannels.length === 0) {
      el.channelsGrid.innerHTML = `
        <div style="grid-column: 1 / -1; text-align: center; padding: 60px; color: var(--text-muted); font-size: 20px;">
          לא נמצאו ערוצים מתאימים לחיפוש
        </div>
      `;
      return;
    }

    const fragment = document.createDocumentFragment();

    state.filteredChannels.forEach((ch, idx) => {
      const card = document.createElement('div');
      card.className = 'channel-card';
      card.dataset.index = idx;

      const logoHtml = ch.logo
        ? `<img class="channel-logo-img" src="${ch.logo}" alt="${ch.name}" loading="lazy" onerror="this.onerror=null; this.parentElement.innerHTML='<div class=\\'channel-logo-fallback\\'>${ch.number || '#'}</div>';">`
        : `<div class="channel-logo-fallback">${ch.number || '#'}</div>`;

      card.innerHTML = `
        <div class="card-top">
          <span class="channel-num-badge">#${ch.number || (idx + 1)}</span>
          <span class="channel-cat-pill">${ch.category || ''}</span>
        </div>
        <div class="channel-logo-wrap">
          ${logoHtml}
        </div>
        <div class="channel-card-footer">
          <div class="channel-card-title">${ch.name || ch.title}</div>
        </div>
      `;

      card.addEventListener('click', () => {
        playChannel(idx);
      });

      card.addEventListener('mouseenter', () => {
        updateHeroSpotlight(ch);
      });

      fragment.appendChild(card);
    });

    el.channelsGrid.appendChild(fragment);
    updateFocusVisuals();

    // Initialize Hero with first item
    if (state.filteredChannels.length > 0) {
      updateHeroSpotlight(state.filteredChannels[0]);
    }
  }

  // HERO SPOTLIGHT
  function updateHeroSpotlight(ch) {
    if (!ch) return;
    if (el.heroNumber) el.heroNumber.textContent = `#${ch.number || ''}`;
    if (el.heroCategory) el.heroCategory.textContent = ch.category || '';
    if (el.heroBadgeTag) el.heroBadgeTag.textContent = ch.badge ? `${ch.badge} 1080p` : 'HD Live';
    if (el.heroTitle) el.heroTitle.textContent = ch.name || ch.title;
    if (el.heroDesc) el.heroDesc.textContent = ch.desc || 'שידור חי באיכות גבוהה';
    if (el.heroLogo) {
      if (ch.logo) {
        el.heroLogo.src = ch.logo;
        el.heroLogo.style.display = 'block';
      } else {
        el.heroLogo.style.display = 'none';
      }
    }
  }

  // FOCUS MANAGEMENT
  function setFocus(section, idx) {
    state.currentSection = section;

    document.querySelectorAll('.focused').forEach(e => e.classList.remove('focused'));

    if (section === 'sidebar') {
      const items = el.categoryList.children;
      if (items.length > 0) {
        state.focusedCategoryIdx = Math.max(0, Math.min(idx, items.length - 1));
        const target = items[state.focusedCategoryIdx];
        target.classList.add('focused');
        target.scrollIntoView({ block: 'nearest' });
      }
    } else if (section === 'grid') {
      const cards = el.channelsGrid.querySelectorAll('.channel-card');
      if (cards.length > 0) {
        state.focusedGridIdx = Math.max(0, Math.min(idx, cards.length - 1));
        const target = cards[state.focusedGridIdx];
        target.classList.add('focused');
        target.scrollIntoView({ block: 'nearest' });
        updateHeroSpotlight(state.filteredChannels[state.focusedGridIdx]);
      }
    } else if (section === 'search') {
      el.search.parentElement.classList.add('focused');
      el.search.focus();
    } else if (section === 'drawer') {
      const items = el.drawerList.children;
      if (items.length > 0) {
        state.focusedDrawerIdx = Math.max(0, Math.min(idx, items.length - 1));
        const target = items[state.focusedDrawerIdx];
        target.classList.add('focused');
        target.scrollIntoView({ block: 'nearest' });
      }
    } else if (section === 'exit_modal') {
      state.focusedExitBtn = idx;
      if (idx === 0) el.btnCancelExit.classList.add('focused');
      else el.btnConfirmExit.classList.add('focused');
    }
  }

  function updateFocusVisuals() {
    setFocus(state.currentSection, state.currentSection === 'grid' ? state.focusedGridIdx : state.focusedCategoryIdx);
  }

  // KEYBOARD & REMOTE NAVIGATION
  function setupKeyListeners() {
    window.addEventListener('keydown', handleKey);
    
    el.search.addEventListener('keydown', (e) => {
      resetScreensaverTimer();
      if (e.keyCode === 13) {
        state.searchQuery = el.search.value;
        applyFilter();
        setFocus('grid', 0);
      } else if (e.keyCode === 40 || e.keyCode === 38) {
        setFocus('grid', 0);
      } else if (e.keyCode === 27 || e.keyCode === 461) {
        e.preventDefault();
        el.search.value = '';
        state.searchQuery = '';
        applyFilter();
        setFocus('grid', 0);
      }
    });

    el.search.addEventListener('input', () => {
      resetScreensaverTimer();
      state.searchQuery = el.search.value;
      applyFilter();
    });

    // Exit Modal Buttons
    if (el.btnCancelExit) {
      el.btnCancelExit.addEventListener('click', closeExitModal);
    }
    if (el.btnConfirmExit) {
      el.btnConfirmExit.addEventListener('click', performActualExit);
    }
  }

  function handleKey(e) {
    resetScreensaverTimer();
    const code = e.keyCode || e.which;

    if (state.screensaverActive) {
      wakeScreensaver();
      e.preventDefault();
      return;
    }

    // WebOS specific Back keys: 461 (webOS Back), 27 (Esc)
    if (code === 461 || code === 27) {
      e.preventDefault();
      handleBackKey();
      return;
    }

    // Color keys: 403 (Red), 404 (Green), 405 (Yellow), 406 (Blue)
    if (code === 405) { // Yellow -> Cycle OLED Enhancer Mode!
      e.preventDefault();
      cycleOledMode();
      return;
    }
    if (code === 404) { // Green -> Cycle Retro Remaster & Aspect Ratio!
      e.preventDefault();
      cycleRetroMode();
      return;
    }
    if (code === 403) { // Red -> Show All Channels
      e.preventDefault();
      selectCategory('ALL', 0);
      showToast('מציג את כל הערוצים');
      return;
    }
    if (code === 406) { // Blue -> Focus search
      e.preventDefault();
      setFocus('search', 0);
      return;
    }

    // Media keys in player
    if (state.currentSection === 'player') {
      if (code === 415) { // Play
        e.preventDefault();
        el.video.play();
        showToast('מנגן');
        return;
      }
      if (code === 19) { // Pause
        e.preventDefault();
        el.video.pause();
        showToast('מושהה');
        return;
      }
      if (code === 33) {
        e.preventDefault();
        zapChannel(1);
        return;
      }
      if (code === 34) {
        e.preventDefault();
        zapChannel(-1);
        return;
      }
    }

    // Section specific navigation
    if (state.currentSection === 'grid') {
      handleGridKeys(code, e);
    } else if (state.currentSection === 'sidebar') {
      handleSidebarKeys(code, e);
    } else if (state.currentSection === 'player') {
      handlePlayerKeys(code, e);
    } else if (state.currentSection === 'drawer') {
      handleDrawerKeys(code, e);
    } else if (state.currentSection === 'exit_modal') {
      handleExitModalKeys(code, e);
    }
  }

  function handleGridKeys(code, e) {
    const COLS = 5;
    const total = state.filteredChannels.length;
    if (total === 0) return;

    let next = state.focusedGridIdx;

    if (code === 37) { // Left (In RTL: forward or stay)
      e.preventDefault();
      if ((next + 1) % COLS !== 0 && next + 1 < total) {
        setFocus('grid', next + 1);
      }
    } else if (code === 39) { // Right (In RTL: back or enter sidebar)
      e.preventDefault();
      if (next % COLS === 0) {
        setFocus('sidebar', state.focusedCategoryIdx);
      } else {
        setFocus('grid', next - 1);
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

    if (code === 38) {
      e.preventDefault();
      if (next > 0) setFocus('sidebar', next - 1);
    } else if (code === 40) {
      e.preventDefault();
      if (next < total - 1) setFocus('sidebar', next + 1);
    } else if (code === 37 || code === 13) {
      e.preventDefault();
      const cat = state.categories[next];
      selectCategory(cat.id, next);
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
    if (code === 37 || code === 39) { // Left / Right toggle
      e.preventDefault();
      setFocus('exit_modal', state.focusedExitBtn === 0 ? 1 : 0);
    } else if (code === 13) { // OK
      e.preventDefault();
      if (state.focusedExitBtn === 0) {
        closeExitModal();
      } else {
        performActualExit();
      }
    } else if (code === 461 || code === 27) {
      e.preventDefault();
      closeExitModal();
    }
  }

  // SAFE BACK BUTTON BEHAVIOR: NEVER ACCIDENTALLY EXITS THE APP!
  function handleBackKey() {
    // 1. If Exit Modal is open -> close it
    if (state.exitModalOpen) {
      closeExitModal();
      return;
    }

    // 2. If Quick Drawer inside player is open -> close it
    if (state.drawerOpen) {
      closeQuickDrawer();
      return;
    }

    // 3. If in Player -> return to Grid
    if (state.currentSection === 'player') {
      stopPlayback();
      return;
    }

    // 4. If Search box is focused or has query -> clear it and return to grid
    if (state.currentSection === 'search' || state.searchQuery.trim().length > 0) {
      el.search.value = '';
      state.searchQuery = '';
      applyFilter();
      setFocus('grid', 0);
      return;
    }

    // 5. If focused in sidebar -> return to grid
    if (state.currentSection === 'sidebar') {
      setFocus('grid', 0);
      return;
    }

    // 6. If on a filtered category (e.g. "ילדים" or "סרטים") -> return to "כל הערוצים"
    if (state.activeCategory !== 'ALL') {
      selectCategory('ALL', 0);
      showToast('חזרת לכל הערוצים');
      return;
    }

    // 7. If on main grid at "ALL" -> show Exit Confirmation Modal instead of quitting!
    openExitModal();
  }

  function openExitModal() {
    state.exitModalOpen = true;
    el.exitModal.classList.remove('hidden');
    setFocus('exit_modal', 0); // Focus "המשך צפייה" by default
  }

  function closeExitModal() {
    state.exitModalOpen = false;
    el.exitModal.classList.add('hidden');
    setFocus('grid', state.focusedGridIdx);
  }

  function performActualExit() {
    if (window.webOS && window.webOS.platformBack) {
      window.webOS.platformBack();
    } else if (window.close) {
      window.close();
    }
  }

  // PLAYER CORE
  function playChannel(idx) {
    const ch = state.filteredChannels[idx];
    if (!ch) return;

    state.currentChannelIdx = idx;
    state.currentSection = 'player';
    el.playerContainer.classList.remove('hidden');

    el.osdNumber.textContent = `#${ch.number || (idx + 1)}`;
    el.osdName.textContent = ch.name || ch.title;
    el.osdCategory.textContent = ch.category || 'ערוץ חי';
    el.osdQuality.textContent = ch.badge ? `${ch.badge} Live Stream` : 'HLS 1080p';
    if (ch.logo) {
      el.osdLogo.style.display = 'block';
      el.osdLogo.src = ch.logo;
    } else {
      el.osdLogo.style.display = 'none';
    }

    showSpinner('מתחבר לשידור...');
    showOSD();

    startStream(ch.url);
  }

  function startStream(url) {
    if (state.hls) {
      state.hls.destroy();
      state.hls = null;
    }

    el.video.pause();
    el.video.removeAttribute('src');
    el.video.load();

    if (window.Hls && window.Hls.isSupported()) {
      const hls = new window.Hls({
        enableWorker: true,
        lowLatencyMode: true,
        capLevelToPlayerSize: true,
        maxBufferLength: 8,
        maxMaxBufferLength: 20,
        maxBufferSize: 25 * 1024 * 1024,
        backBufferLength: 30
      });

      state.hls = hls;
      hls.loadSource(url);
      hls.attachMedia(el.video);

      hls.on(window.Hls.Events.MANIFEST_PARSED, () => {
        hideSpinner();
        el.video.play().catch(e => console.log('Autoplay:', e));
        el.osdQuality.textContent = 'שידור חי יציב 1080p';
      });

      hls.on(window.Hls.Events.ERROR, (event, data) => {
        if (data.fatal) {
          switch (data.type) {
            case window.Hls.ErrorTypes.NETWORK_ERROR:
              showToast('מנסה להתחבר מחדש...');
              hls.startLoad();
              break;
            case window.Hls.ErrorTypes.MEDIA_ERROR:
              showToast('משחזר מקודד וידאו...');
              hls.recoverMediaError();
              break;
            default:
              showToast('שידור אינו זמין כעת');
              hls.destroy();
              break;
          }
        }
      });
    } else if (el.video.canPlayType('application/vnd.apple.mpegurl')) {
      el.video.src = url;
      el.video.addEventListener('loadedmetadata', () => {
        hideSpinner();
        el.video.play();
        el.osdQuality.textContent = 'Native HLS Stream';
      }, { once: true });
    } else {
      showToast('פורמט שידור אינו נתמך במכשיר זה');
      hideSpinner();
    }
  }

  function stopPlayback() {
    if (state.hls) {
      state.hls.destroy();
      state.hls = null;
    }
    el.video.pause();
    el.video.removeAttribute('src');
    el.video.load();

    el.playerContainer.classList.add('hidden');
    state.currentSection = 'grid';
    setFocus('grid', state.currentChannelIdx >= 0 ? state.currentChannelIdx : 0);
  }

  function zapChannel(direction) {
    const total = state.filteredChannels.length;
    if (total <= 1) return;

    let nextIdx = state.currentChannelIdx + direction;
    if (nextIdx >= total) nextIdx = 0;
    if (nextIdx < 0) nextIdx = total - 1;

    showToast(`עובר לערוץ: ${state.filteredChannels[nextIdx].name}`);
    playChannel(nextIdx);
  }

  // OSD HELPERS
  function showOSD() {
    el.osd.classList.remove('fade-out');
    if (state.osdTimeout) clearTimeout(state.osdTimeout);
    state.osdTimeout = setTimeout(() => {
      if (!state.drawerOpen) {
        el.osd.classList.add('fade-out');
      }
    }, 5500);
  }

  function showSpinner(text) {
    el.spinnerText.textContent = text || 'טוען שידור...';
    el.spinner.classList.remove('hidden');
  }

  function hideSpinner() {
    el.spinner.classList.add('hidden');
  }

  function showToast(msg) {
    el.toastMessage.textContent = msg;
    el.toast.classList.remove('hidden');
    setTimeout(() => {
      el.toast.classList.add('hidden');
    }, 2800);
  }

  // QUICK DRAWER
  function openQuickDrawer() {
    state.drawerOpen = true;
    el.quickDrawer.classList.remove('hidden');
    el.drawerList.innerHTML = '';

    const fragment = document.createDocumentFragment();

    state.filteredChannels.forEach((ch, idx) => {
      const item = document.createElement('div');
      item.className = 'drawer-channel-item' + (idx === state.currentChannelIdx ? ' active' : '');
      item.dataset.index = idx;
      item.innerHTML = `
        <span class="drawer-item-num">#${ch.number || (idx + 1)}</span>
        ${ch.logo ? `<img class="drawer-item-logo" src="${ch.logo}" alt="logo">` : ''}
        <span class="drawer-item-title">${ch.name || ch.title}</span>
      `;
      item.addEventListener('click', () => {
        closeQuickDrawer();
        playChannel(idx);
      });
      fragment.appendChild(item);
    });

    el.drawerList.appendChild(fragment);
    setFocus('drawer', state.currentChannelIdx >= 0 ? state.currentChannelIdx : 0);
  }

  function closeQuickDrawer() {
    state.drawerOpen = false;
    el.quickDrawer.classList.add('hidden');
    state.currentSection = 'player';
    showOSD();
  }

  // MOUSE & MAGIC REMOTE POINTER
  function setupMouseListeners() {
    document.addEventListener('mousemove', resetScreensaverTimer);
    document.addEventListener('click', resetScreensaverTimer);
  }

  // RUN APP
  window.addEventListener('DOMContentLoaded', init);

})();
