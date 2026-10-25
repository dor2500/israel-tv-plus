// Israel TV+ screen markup. boot.js puts it into #root before app.js starts, so a live update can change the
// layout together with the code (the installed apps only keep index.html + boot.js).
// One markup for every device: TVs / computers use the guide (rail + grid); phones (body.phone) get the
// Nuvio-style home (hero pager + shelves), the floating navigation bar and bottom sheets. Elements marked
// "ph" exist for phones only, "tv" for the big screen only.

// Material Symbols (Rounded, 24px) – inline SVG, no icon font needed (works offline and on old TV browsers)
window.ITV_ICONS = {
  home: 'M10 19v-5h4v5c0 .55.45 1 1 1h3c.55 0 1-.45 1-1v-7h1.7c.46 0 .68-.57.33-.87L12.67 3.6c-.38-.34-.96-.34-1.34 0l-8.36 7.53c-.34.3-.13.87.33.87H5v7c0 .55.45 1 1 1h3c.55 0 1-.45 1-1z',
  tv: 'M21 6h-7.59l2.94-2.94c.2-.2.2-.51 0-.71s-.51-.2-.71 0L12 5.99 8.36 2.35c-.2-.2-.51-.2-.71 0s-.2.51 0 .71L10.59 6H3c-1.1 0-2 .89-2 2v12c0 1.1.9 2 2 2h18c1.1 0 2-.9 2-2V8c0-1.11-.9-2-2-2zm-1 14H4c-.55 0-1-.45-1-1V9c0-.55.45-1 1-1h16c.55 0 1 .45 1 1v10c0 .55-.45 1-1 1zM9.5 16.8l5.6-3.36c.3-.18.3-.61 0-.79L9.5 9.2c-.31-.19-.7.04-.7.39v6.82c0 .35.39.58.7.39z',
  star: 'M12 17.27l4.15 2.51c.76.46 1.69-.22 1.49-1.08l-1.1-4.72 3.67-3.18c.67-.58.31-1.68-.57-1.75l-4.83-.41-1.89-4.46c-.34-.81-1.5-.81-1.84 0L9.19 8.63l-4.83.41c-.88.07-1.24 1.17-.57 1.75l3.67 3.18-1.1 4.72c-.2.86.73 1.54 1.49 1.08l4.15-2.5z',
  starOutline: 'M19.65 9.04l-4.84-.42-1.89-4.45c-.34-.81-1.5-.81-1.84 0L9.19 8.63l-4.83.41c-.88.07-1.24 1.17-.57 1.75l3.67 3.18-1.1 4.72c-.2.86.73 1.54 1.49 1.08l4.15-2.5 4.15 2.51c.76.46 1.69-.22 1.49-1.08l-1.1-4.73 3.67-3.18c.67-.58.32-1.68-.56-1.75zM12 15.4l-3.76 2.27 1-4.28-3.32-2.88 4.38-.38L12 6.1l1.71 4.04 4.38.38-3.32 2.88 1 4.28L12 15.4z',
  settings: 'M19.5 12c0-.23-.01-.45-.03-.68l1.86-1.41c.4-.3.51-.86.26-1.3l-1.87-3.23c-.25-.44-.79-.62-1.25-.42l-2.15.91c-.37-.26-.76-.49-1.17-.68l-.29-2.31c-.06-.5-.49-.88-.99-.88h-3.73c-.51 0-.94.38-1 .88l-.29 2.31c-.41.19-.8.42-1.17.68l-2.15-.91c-.46-.2-1-.02-1.25.42L2.41 8.62c-.25.44-.14.99.26 1.3l1.86 1.41c-.02.22-.03.44-.03.67s.01.45.03.68l-1.86 1.41c-.4.3-.51.86-.26 1.3l1.87 3.23c.25.44.79.62 1.25.42l2.15-.91c.37.26.76.49 1.17.68l.29 2.31c.06.5.49.88.99.88h3.73c.5 0 .93-.38.99-.88l.29-2.31c.41-.19.8-.42 1.17-.68l2.15.91c.46.2 1 .02 1.25-.42l1.87-3.23c.25-.44.14-.99-.26-1.3l-1.86-1.41c.03-.23.04-.45.04-.68zm-7.46 3.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5 3.5z',
  search: 'M15.5 14h-.79l-.28-.27c1.2-1.4 1.82-3.31 1.48-5.34-.47-2.78-2.79-5-5.59-5.34-4.23-.52-7.79 3.04-7.27 7.27.34 2.8 2.56 5.12 5.34 5.59 2.03.34 3.94-.28 5.34-1.48l.27.28v.79l4.25 4.25c.41.41 1.08.41 1.49 0 .41-.41.41-1.08 0-1.49L15.5 14zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z',
  play: 'M8 6.82v10.36c0 .79.87 1.27 1.54.84l8.14-5.18c.62-.39.62-1.29 0-1.69L9.54 5.98C8.87 5.55 8 6.03 8 6.82z',
  pause: 'M8 19c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2s-2 .9-2 2v10c0 1.1.9 2 2 2zm6-12v10c0 1.1.9 2 2 2s2-.9 2-2V7c0-1.1-.9-2-2-2s-2 .9-2 2z',
  next: 'M7.58 16.89l5.77-4.07c.56-.4.56-1.24 0-1.63L7.58 7.11C6.91 6.65 6 7.12 6 7.93v8.14c0 .81.91 1.28 1.58.82zM16 7v10c0 .55.45 1 1 1s1-.45 1-1V7c0-.55-.45-1-1-1s-1 .45-1 1z',
  prev: 'M7 6c.55 0 1 .45 1 1v10c0 .55-.45 1-1 1s-1-.45-1-1V7c0-.55.45-1 1-1zm3.66 6.82l5.77 4.07c.66.47 1.58-.01 1.58-.82V7.93c0-.81-.91-1.28-1.58-.82l-5.77 4.07c-.57.4-.57 1.24 0 1.64z',
  list: 'M4 13c.55 0 1-.45 1-1s-.45-1-1-1-1 .45-1 1 .45 1 1 1zm0 4c.55 0 1-.45 1-1s-.45-1-1-1-1 .45-1 1 .45 1 1 1zm0-8c.55 0 1-.45 1-1s-.45-1-1-1-1 .45-1 1 .45 1 1 1zm4 4h12c.55 0 1-.45 1-1s-.45-1-1-1H8c-.55 0-1 .45-1 1s.45 1 1 1zm0 4h12c.55 0 1-.45 1-1s-.45-1-1-1H8c-.55 0-1 .45-1 1s.45 1 1 1zM7 8c0 .55.45 1 1 1h12c.55 0 1-.45 1-1s-.45-1-1-1H8c-.55 0-1 .45-1 1z',
  more: 'M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z',
  fullscreen: 'M6 14c-.55 0-1 .45-1 1v3c0 .55.45 1 1 1h3c.55 0 1-.45 1-1s-.45-1-1-1H7v-2c0-.55-.45-1-1-1zm0-4c.55 0 1-.45 1-1V7h2c.55 0 1-.45 1-1s-.45-1-1-1H6c-.55 0-1 .45-1 1v3c0 .55.45 1 1 1zm11 7h-2c-.55 0-1 .45-1 1s.45 1 1 1h3c.55 0 1-.45 1-1v-3c0-.55-.45-1-1-1s-1 .45-1 1v2zM14 6c0 .55.45 1 1 1h2v2c0 .55.45 1 1 1s1-.45 1-1V6c0-.55-.45-1-1-1h-3c-.55 0-1 .45-1 1z',
  back: 'M5 13h11.17l-4.88 4.88c-.39.39-.39 1.03 0 1.42.39.39 1.02.39 1.41 0l6.59-6.59c.39-.39.39-1.02 0-1.41l-6.58-6.6c-.39-.39-1.02-.39-1.41 0-.39.39-.39 1.02 0 1.41L16.17 11H5c-.55 0-1 .45-1 1s.45 1 1 1z',
  mic: 'M12 14c1.66 0 3-1.34 3-3V5c0-1.66-1.34-3-3-3S9 3.34 9 5v6c0 1.66 1.34 3 3 3zm5.91-3c-.49 0-.9.36-.98.85C16.52 14.2 14.47 16 12 16s-4.52-1.8-4.93-4.15c-.08-.49-.49-.85-.98-.85-.61 0-1.09.54-1 1.14.49 3 2.89 5.35 5.91 5.78V20c0 .55.45 1 1 1s1-.45 1-1v-2.08c3.02-.43 5.42-2.78 5.91-5.78.1-.6-.39-1.14-1-1.14z',
  close: 'M18.3 5.71c-.39-.39-1.02-.39-1.41 0L12 10.59 7.11 5.7c-.39-.39-1.02-.39-1.41 0-.39.39-.39 1.02 0 1.41L10.59 12 5.7 16.89c-.39.39-.39 1.02 0 1.41.39.39 1.02.39 1.41 0L12 13.41l4.89 4.89c.39.39 1.02.39 1.41 0 .39-.39.39-1.02 0-1.41L13.41 12l4.89-4.89c.38-.38.38-1.02 0-1.4z',
  info: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 15c-.55 0-1-.45-1-1v-4c0-.55.45-1 1-1s1 .45 1 1v4c0 .55-.45 1-1 1zm1-8h-2V7h2v2z',
  download: 'M16.59 9H15V4c0-.55-.45-1-1-1h-4c-.55 0-1 .45-1 1v5H7.41c-.89 0-1.34 1.08-.71 1.71l4.59 4.59c.39.39 1.02.39 1.41 0l4.59-4.59c.63-.63.19-1.71-.7-1.71zM5 19c0 .55.45 1 1 1h12c.55 0 1-.45 1-1s-.45-1-1-1H6c-.55 0-1 .45-1 1z',
  phone: 'M16 1H8C6.34 1 5 2.34 5 4v16c0 1.66 1.34 3 3 3h8c1.66 0 3-1.34 3-3V4c0-1.66-1.34-3-3-3zm-2 20h-4c-.28 0-.5-.22-.5-.5s.22-.5.5-.5h4c.28 0 .5.22.5.5s-.22.5-.5.5zm3.5-3H6.5V4h11v14z',
  kids: 'M12 2c1.1 0 2 .9 2 2s-.9 2-2 2-2-.9-2-2 .9-2 2-2zm9 7h-5v12c0 .55-.45 1-1 1s-1-.45-1-1v-5h-2v5c0 .55-.45 1-1 1s-1-.45-1-1V9H3c-.55 0-1-.45-1-1s.45-1 1-1h18c.55 0 1 .45 1 1s-.45 1-1 1z',
  pip: 'M18 7h-6c-.55 0-1 .45-1 1v4c0 .55.45 1 1 1h6c.55 0 1-.45 1-1V8c0-.55-.45-1-1-1zm3-4H3c-1.1 0-2 .9-2 2v14c0 1.1.9 1.98 2 1.98h18c1.1 0 2-.88 2-1.98V5c0-1.1-.9-2-2-2zm-1 16.01H4c-.55 0-1-.45-1-1V5.98c0-.55.45-1 1-1h16c.55 0 1 .45 1 1v12.03c0 .55-.45 1-1 1z',
  chevron: 'M14.71 6.71c-.39-.39-1.02-.39-1.41 0L8.71 11.3c-.39.39-.39 1.02 0 1.41l4.59 4.59c.39.39 1.02.39 1.41 0 .39-.39.39-1.02 0-1.41L10.83 12l3.88-3.88c.39-.39.38-1.03 0-1.41z',
  radio: 'M3.24 6.15C2.51 6.43 2 7.17 2 8v12c0 1.1.9 2 2 2h16c1.11 0 2-.9 2-2V8c0-1.1-.9-2-2-2H8.3l7.43-3c.46-.19.68-.71.49-1.17-.19-.46-.71-.68-1.17-.49L3.24 6.15zM7 20c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3zm13-8h-2v-1c0-.55-.45-1-1-1s-1 .45-1 1v1H4V9c0-.55.45-1 1-1h14c.55 0 1 .45 1 1v3z'
};
window.ITV_ICON = function (name, cls) {
  return '<svg class="ic' + (cls ? ' ' + cls : '') + '" viewBox="0 0 24 24" aria-hidden="true"><path d="' +
    (window.ITV_ICONS[name] || '') + '"/></svg>';
};

window.ITV_UI = (function (I) {
  // VisionOS-style glass skeletons: what the phone home shows until app.js has built the real one
  var rep = function (s, n) { var o = ''; for (var i = 0; i < n; i++) o += s; return o; };
  var skelCard = '<div class="pcard"><div class="pc-media skel"></div><div class="skel skel-t"></div><div class="skel skel-s"></div></div>';
  var skelHome = '<div class="slide skel-slide"><div class="slide-body"><div class="skel skel-logo"></div><div class="skel skel-line"></div>' +
    '<div class="skel skel-pill"></div></div></div>';
  var skelShelves = rep('<section class="shelf skel-shelf"><div class="shelf-head"><div class="skel skel-h"></div></div>' +
    '<div class="shelf-row">' + rep(skelCard, 4) + '</div></section>', 3);
  return '' +
'<svg class="defs" aria-hidden="true"><defs>' +
  '<filter id="retro-sharpen-filter" x="0%" y="0%" width="100%" height="100%">' +
    '<feConvolveMatrix order="3" preserveAlpha="true" kernelMatrix="0 -0.8 0 -0.8 4.2 -0.8 0 -0.8 0"/></filter>' +
'</defs></svg>' +

'<div id="app" class="app">' +
  // ---------------------------------------------------------------- top bar (TV: brand + search + actions; phone: top app bar)
  '<header class="topbar" id="topbar">' +
    '<div class="brand"><img class="brand-icon" src="icon.png" alt=""><div class="brand-text">' +
      '<div class="brand-name">ישראל <b>TV+</b></div><div class="brand-tab ph" id="brand-tab"></div><div class="brand-sub"><span class="live-dot"></span>' +
      '<span id="count-badge">ערוצים</span></div></div></div>' +
    '<div class="top-search nav-top" id="top-search" data-nav="search">' +
      '<span class="ico">' + I('search') + '</span><input id="search-input" type="search" autocomplete="off" spellcheck="false" ' +
      'enterkeyhint="search" placeholder="חיפוש ערוץ – שם, מדינה או מספר"></div>' +
    '<div class="top-actions">' +
      '<button class="top-btn nav-top hidden" id="btn-update" data-nav="update"><span class="ico">' + I('download') + '</span><span>עדכון זמין</span></button>' +
      '<button class="top-btn nav-top hidden" id="btn-getapp" data-nav="getapp"><span class="ico">' + I('phone') + '</span><span>הורדת האפליקציה</span></button>' +
      '<button class="top-btn nav-top hidden" id="btn-kids-exit" data-nav="kids"><span class="ico">' + I('kids') + '</span><span>מצב ילדים</span></button>' +
      '<button class="top-btn ph" id="btn-search" aria-label="חיפוש"><span class="ico">' + I('search') + '</span></button>' +
      '<button class="top-btn nav-top tv" id="btn-settings" data-nav="settings"><span class="ico">' + I('settings') + '</span><span>הגדרות</span></button>' +
      '<div class="clock" id="clock">00:00</div>' +
      // phone: the profile avatar (3D Noto Emoji) at the trailing end of the Apple TV-style header; a dot = update
      '<button class="avatar ph" id="btn-avatar" aria-label="פרופיל ועדכונים"><img id="avatar-img" alt="">' +
        '<i class="avatar-dot hidden" id="avatar-dot"></i></button>' +
    '</div>' +
  '</header>' +

  // ---------------------------------------------------------------- phone home: hero pager + shelves (Nuvio style)
  '<main class="home ph" id="home">' +
    '<section class="pager" id="pager"><div class="pager-track" id="pager-track">' + skelHome + '</div>' +
      '<div class="pager-dots" id="pager-dots"></div></section>' +
    '<div class="shelves" id="shelves">' + skelShelves + '</div>' +
  '</main>' +

  '<div class="body" id="guide-body">' +
    // ---------------------------------------------------------------- categories rail (phone: filter chips)
    '<nav class="rail" id="rail"><div class="rail-list" id="rail-list"></div></nav>' +

    // ---------------------------------------------------------------- guide
    '<main class="guide" id="guide">' +
      '<section class="hero" id="hero">' +
        '<div class="hero-bg" id="hero-bg"></div>' +
        '<div class="hero-logo" id="hero-logo-box"><img id="hero-logo" alt=""><div class="mono hidden" id="hero-mono"></div></div>' +
        '<div class="hero-info">' +
          '<div class="hero-tags"><span class="tag num" id="hero-num">1</span><span class="tag" id="hero-cat"></span>' +
            '<span class="tag q" id="hero-q"></span><span class="tag lang hidden" id="hero-lang"></span>' +
            '<span class="tag fav hidden" id="hero-fav">★ מועדף</span></div>' +
          '<h1 class="hero-title" id="hero-title"></h1>' +
          '<p class="hero-desc" id="hero-desc"></p>' +
          '<p class="hero-src" id="hero-src"></p>' +
        '</div>' +
      '</section>' +
      '<div class="grid-head"><h2 id="grid-title">כל הערוצים (הכל)</h2><span id="grid-count"></span></div>' +
      '<div class="grid" id="grid"></div>' +
    '</main>' +
  '</div>' +

  // ---------------------------------------------------------------- TV: Apple TV+ browse screen (app.js "TV: the Apple TV+")
  '<div class="atv" id="atv">' +
    '<div class="atv-stage"><div class="atv-bd" id="atv-bd-a"></div><div class="atv-bd" id="atv-bd-b"></div><div class="atv-shade"></div></div>' +
    '<nav class="atv-pill" id="atv-pill"></nav><div class="atv-clock" id="atv-clock"></div>' +
    '<section class="atv-meta" id="atv-meta-a"></section><section class="atv-meta" id="atv-meta-b"></section>' +
    '<div class="atv-search" id="atv-search"><span class="ico">' + I('search') + '</span><input id="atv-q" type="search" ' +
      'autocomplete="off" spellcheck="false" enterkeyhint="search" placeholder="חיפוש ערוץ – שם, מדינה, ז׳אנר או מספר"></div>' +
    '<div class="atv-shelves" id="atv-shelves"><div class="atv-rows" id="atv-rows"></div></div>' +
  '</div>' +

  '<footer class="hints tv" id="hints"></footer>' +

  // ---------------------------------------------------------------- phone: floating navigation bar
  '<nav class="navbar ph" id="navbar" role="tablist">' +
    '<div class="nb-track"><div class="nb-pill" id="nb-pill"></div>' +
    '<button class="nb-item" data-tab="home" role="tab">' + I('home') + '<span>בית</span></button>' +
    '<button class="nb-item" data-tab="explore" role="tab">' + I('tv') + '<span>ערוצים</span></button>' +
    '<button class="nb-item" data-tab="library" role="tab">' + I('star') + '<span>שלי</span></button>' +
    '<button class="nb-item" data-tab="settings" role="tab">' + I('settings') + '<span>הגדרות</span></button>' +
    '</div>' +
    // the search action of the glass bar: a round glass button that opens into the search field (app.js moves #top-search here)
    '<button class="nb-search" id="nb-search" aria-label="חיפוש">' + I('search', 'nbs-find') + I('close', 'nbs-close') + '</button>' +
  '</nav>' +

  // ---------------------------------------------------------------- number entry
  '<div class="numhud hidden" id="numhud"><div class="numhud-digits" id="numhud-digits"></div>' +
    '<div class="numhud-name" id="numhud-name"></div></div>' +

  '<audio id="radio-audio" preload="none"></audio>' +

  // ---------------------------------------------------------------- player
  '<div class="player hidden" id="player">' +
    '<div class="video-box"><video id="video" playsinline webkit-playsinline preload="auto"></video></div>' +
    '<div class="radio-view hidden" id="radio-view"><div class="radio-card">' +
      '<div class="radio-logo"><img id="radio-logo" alt=""><div class="mono hidden" id="radio-mono"></div></div>' +
      '<h2 id="radio-name"></h2><div class="radio-live"><span class="live-dot"></span> רדיו בשידור חי</div>' +
      '<div class="eq"><i></i><i></i><i></i><i></i><i></i></div></div></div>' +
    '<div class="spinner hidden" id="spinner"><div class="ring"></div><div id="spinner-text">מתחבר...</div></div>' +
    '<div class="perror hidden" id="perror"><div class="perror-box">' +
      '<div class="perror-ico">' + I('info') + '</div><h3 id="perror-title"></h3><p id="perror-desc"></p>' +
      '<div class="perror-actions"><button class="btn primary" id="perror-retry">נסה שוב</button>' +
      '<button class="btn" id="perror-next">ערוץ הבא</button><button class="btn" id="perror-back">חזרה</button></div>' +
    '</div></div>' +

    '<div class="osd" id="osd">' +
      '<div class="osd-top">' +
        '<button class="obtn osd-back ph" data-act="back" aria-label="חזרה">' + I('back') + '</button>' +
        '<div class="osd-ch"><div class="osd-logo"><img id="osd-logo" alt=""><div class="mono hidden" id="osd-mono"></div></div>' +
          '<div class="osd-titles"><div class="osd-name"><span class="osd-num" id="osd-num">1</span><span id="osd-name"></span></div>' +
          '<div class="osd-tags"><span class="live-pill"><span class="live-dot"></span>LIVE</span>' +
          '<span id="osd-cat"></span><span id="osd-quality"></span><span id="osd-pic"></span></div>' +
          '<div class="osd-epg" id="osd-epg"></div></div></div>' +
        '<div class="osd-clock" id="osd-clock">00:00</div>' +
      '</div>' +
      '<div class="osd-center ph" id="osd-center">' +
        '<button class="obtn round" data-act="prev" aria-label="ערוץ קודם">' + I('prev') + '</button>' +
        '<button class="obtn round big" data-act="playpause" aria-label="השהה / נגן"><b class="ob-play">' + I('pause') + '</b></button>' +
        '<button class="obtn round" data-act="next" aria-label="ערוץ הבא">' + I('next') + '</button>' +
      '</div>' +
      '<div class="osd-dub hidden" id="osd-dub"><span class="dub-dot"></span><span id="osd-dub-text"></span></div>' +
      '<div class="osd-bottom">' +
        '<div class="osd-buttons" id="osd-buttons">' +
          '<button class="obtn tv" data-act="back" title="למדריך">' + I('back') + '<span>מדריך</span></button>' +
          '<button class="obtn tv" data-act="prev" title="ערוץ קודם">' + I('prev') + '</button>' +
          '<button class="obtn tv" data-act="playpause" title="השהה / נגן"><b class="ob-play" id="ob-play">' + I('pause') + '</b></button>' +
          '<button class="obtn tv" data-act="next" title="ערוץ הבא">' + I('next') + '</button>' +
          '<button class="obtn" data-act="list" title="רשימת ערוצים">' + I('list') + '<span>ערוצים</span></button>' +
          '<button class="obtn ph" data-act="dub" title="דיבוב לעברית">' + I('mic') + '<span>דיבוב</span></button>' +
          '<button class="obtn ph hidden" data-act="pip" id="ob-pip" title="תמונה בתוך תמונה">' + I('pip') + '</button>' +
          '<button class="obtn" data-act="menu" title="אפשרויות">' + I('more') + '<span>אפשרויות</span></button>' +
          '<button class="obtn" data-act="fullscreen" title="מסך מלא">' + I('fullscreen') + '</button>' +
        '</div>' +
        '<div class="osd-hints" id="osd-hints"></div>' +
      '</div>' +
    '</div>' +

    '<div class="drawer sheet hidden" id="drawer"><div class="sheet-handle ph"></div>' +
      '<div class="drawer-head"><h3>ערוצים</h3><span id="drawer-cat"></span></div>' +
      '<div class="drawer-list" id="drawer-list"></div></div>' +
  '</div>' +

  // ---------------------------------------------------------------- options menu (long OK / menu / colour key; phone: bottom sheet)
  '<div class="overlay hidden" id="menu"><div class="menu-box sheet"><div class="sheet-handle ph"></div><div class="menu-head">' +
    '<div class="menu-logo"><img id="menu-logo" alt=""><div class="mono hidden" id="menu-mono"></div></div>' +
    '<div><div class="menu-title" id="menu-title"></div><div class="menu-sub" id="menu-sub"></div></div></div>' +
    '<div class="menu-list" id="menu-list"></div></div></div>' +

  // ---------------------------------------------------------------- settings
  '<div class="overlay settings hidden" id="settings"><div class="settings-box">' +
    '<div class="settings-side"><div class="settings-title">' + I('settings') + '<span>הגדרות</span></div><div class="settings-nav" id="settings-nav"></div></div>' +
    '<div class="settings-main"><div class="settings-head"><h2 id="settings-h"></h2><p id="settings-p"></p></div>' +
    '<div class="settings-rows" id="settings-rows"></div></div>' +
  '</div></div>' +

  // ---------------------------------------------------------------- dialog (info / update / exit / PIN / get the app)
  '<div class="overlay hidden" id="dialog"><div class="dialog-box" id="dialog-box">' +
    '<div class="dialog-ico" id="dialog-ico"></div><h2 id="dialog-title"></h2><div class="dialog-body" id="dialog-body"></div>' +
    '<div class="dialog-actions" id="dialog-actions"></div></div></div>' +

  '<div class="toast hidden" id="toast"></div>' +

  '<div class="saver hidden" id="saver"><div class="saver-in" id="saver-in"><div class="saver-clock" id="saver-clock"></div>' +
    '<div class="saver-text">שומר מסך • לחצו על מקש כלשהו</div></div></div>' +
'</div>';
})(window.ITV_ICON);
