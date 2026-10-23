// Israel TV+ screen markup. boot.js puts it into #root before app.js starts, so a live update can change the
// layout together with the code (the installed apps only keep index.html + boot.js).
window.ITV_UI = '' +
'<svg class="defs" aria-hidden="true"><defs>' +
  '<filter id="retro-sharpen-filter" x="0%" y="0%" width="100%" height="100%">' +
    '<feConvolveMatrix order="3" preserveAlpha="true" kernelMatrix="0 -0.8 0 -0.8 4.2 -0.8 0 -0.8 0"/></filter>' +
'</defs></svg>' +

'<div id="app" class="app">' +
  // ---------------------------------------------------------------- top bar
  '<header class="topbar">' +
    '<div class="brand"><img class="brand-icon" src="icon.png" alt=""><div class="brand-text">' +
      '<div class="brand-name">ישראל <b>TV+</b></div><div class="brand-sub"><span class="live-dot"></span>' +
      '<span id="count-badge">ערוצים</span></div></div></div>' +
    '<div class="top-search nav-top" id="top-search" data-nav="search">' +
      '<span class="ico">🔍</span><input id="search-input" type="text" autocomplete="off" spellcheck="false" ' +
      'placeholder="חיפוש ערוץ – שם או מספר"></div>' +
    '<div class="top-actions">' +
      '<button class="top-btn nav-top hidden" id="btn-update" data-nav="update"><span class="ico">⬆️</span><span>עדכון זמין</span></button>' +
      '<button class="top-btn nav-top hidden" id="btn-getapp" data-nav="getapp"><span class="ico">📲</span><span>הורדת האפליקציה</span></button>' +
      '<button class="top-btn nav-top hidden" id="btn-kids-exit" data-nav="kids"><span class="ico">🧸</span><span>מצב ילדים</span></button>' +
      '<button class="top-btn nav-top" id="btn-settings" data-nav="settings"><span class="ico">⚙️</span><span>הגדרות</span></button>' +
      '<div class="clock" id="clock">00:00</div>' +
    '</div>' +
  '</header>' +

  '<div class="body">' +
    // ---------------------------------------------------------------- categories rail
    '<nav class="rail" id="rail"><div class="rail-list" id="rail-list"></div></nav>' +

    // ---------------------------------------------------------------- guide
    '<main class="guide" id="guide">' +
      '<section class="hero" id="hero">' +
        '<div class="hero-logo" id="hero-logo-box"><img id="hero-logo" alt=""><div class="mono hidden" id="hero-mono"></div></div>' +
        '<div class="hero-info">' +
          '<div class="hero-tags"><span class="tag num" id="hero-num">1</span><span class="tag" id="hero-cat"></span>' +
            '<span class="tag q" id="hero-q"></span><span class="tag lang hidden" id="hero-lang"></span>' +
            '<span class="tag fav hidden" id="hero-fav">⭐ מועדף</span></div>' +
          '<h1 class="hero-title" id="hero-title"></h1>' +
          '<p class="hero-desc" id="hero-desc"></p>' +
          '<p class="hero-src" id="hero-src"></p>' +
        '</div>' +
      '</section>' +
      '<div class="grid-head"><h2 id="grid-title">כל הערוצים</h2><span id="grid-count"></span></div>' +
      '<div class="grid" id="grid"></div>' +
    '</main>' +
  '</div>' +

  '<footer class="hints" id="hints"></footer>' +

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
      '<div class="perror-ico">⚠️</div><h3 id="perror-title"></h3><p id="perror-desc"></p>' +
      '<div class="perror-actions"><button class="btn primary" id="perror-retry">⟳ נסה שוב</button>' +
      '<button class="btn" id="perror-next">ערוץ הבא ▼</button><button class="btn" id="perror-back">↩ למדריך</button></div>' +
    '</div></div>' +

    '<div class="osd" id="osd">' +
      '<div class="osd-top">' +
        '<div class="osd-ch"><div class="osd-logo"><img id="osd-logo" alt=""><div class="mono hidden" id="osd-mono"></div></div>' +
          '<div class="osd-titles"><div class="osd-name"><span class="osd-num" id="osd-num">1</span><span id="osd-name"></span></div>' +
          '<div class="osd-tags"><span class="live-pill"><span class="live-dot"></span>LIVE</span>' +
          '<span id="osd-cat"></span><span id="osd-quality"></span><span id="osd-pic"></span></div></div></div>' +
        '<div class="osd-clock" id="osd-clock">00:00</div>' +
      '</div>' +
      '<div class="osd-dub hidden" id="osd-dub"><span class="dub-dot"></span><span id="osd-dub-text"></span></div>' +
      '<div class="osd-bottom">' +
        '<div class="osd-buttons" id="osd-buttons">' +
          '<button class="obtn" data-act="back" title="למדריך">↩<span>מדריך</span></button>' +
          '<button class="obtn" data-act="prev" title="ערוץ קודם">▲</button>' +
          '<button class="obtn" data-act="playpause" title="השהה / נגן"><b id="ob-play">⏸</b></button>' +
          '<button class="obtn" data-act="next" title="ערוץ הבא">▼</button>' +
          '<button class="obtn" data-act="mute" title="השתקה"><b id="ob-mute">🔊</b></button>' +
          '<button class="obtn" data-act="list" title="רשימת ערוצים">☰<span>ערוצים</span></button>' +
          '<button class="obtn" data-act="menu" title="אפשרויות">⋯<span>אפשרויות</span></button>' +
          '<button class="obtn" data-act="fullscreen" title="מסך מלא">⛶</button>' +
        '</div>' +
        '<div class="osd-hints" id="osd-hints"></div>' +
      '</div>' +
    '</div>' +

    '<div class="drawer hidden" id="drawer"><div class="drawer-head"><h3>ערוצים</h3><span id="drawer-cat"></span></div>' +
      '<div class="drawer-list" id="drawer-list"></div></div>' +
  '</div>' +

  // ---------------------------------------------------------------- options menu (long OK / menu / colour key)
  '<div class="overlay hidden" id="menu"><div class="menu-box"><div class="menu-head">' +
    '<div class="menu-logo"><img id="menu-logo" alt=""><div class="mono hidden" id="menu-mono"></div></div>' +
    '<div><div class="menu-title" id="menu-title"></div><div class="menu-sub" id="menu-sub"></div></div></div>' +
    '<div class="menu-list" id="menu-list"></div></div></div>' +

  // ---------------------------------------------------------------- settings
  '<div class="overlay settings hidden" id="settings"><div class="settings-box">' +
    '<div class="settings-side"><div class="settings-title">⚙️ הגדרות</div><div class="settings-nav" id="settings-nav"></div></div>' +
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
