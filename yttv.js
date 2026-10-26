/**
 * YouTube TV (Leanback) on Smart TV - Authentic 1:1 Interface & Engine
 * Built for LG webOS TV, Android TV, Samsung Tizen, Windows & Browsers.
 */
(function () {
  'use strict';

  var YOUTUBE_DATA = [
    // ------------------------------------------------------------- 🔴 שידורים חיים (Live Streams)
    {
      id: 'live',
      title: 'שידורים ישירים עכשיו 🔴',
      badge: 'LIVE',
      videos: [
        {
          id: 'b6vWzPZ5a-g',
          title: 'כאן 11 | שידור חי באיכות גבוהה 24/7',
          channel: 'כאן 11 - תאגיד השידור',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_k2P2pM_F85NnB4eQkZ7q7Y0n_2Z2pM=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/b6vWzPZ5a-g/maxresdefault.jpg',
          duration: 'LIVE',
          live: true,
          views: '45,200 צופים עכשיו',
          desc: 'שידור חי רציף של כאן 11: חדשות, תוכניות תחקירים, סדרות ושידורי אקטואליה מהאולפן המרכזי בירושלים.'
        },
        {
          id: 'S2m9JkFmS90',
          title: 'חדשות 12 | מהדורה חיה ועדכוני אמת סביב השעון',
          channel: 'חדשות 12 - N12',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_n4n5A2Zq7Y0n_2Z2pM_F85NnB4eQkZ=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/S2m9JkFmS90/maxresdefault.jpg',
          duration: 'LIVE',
          live: true,
          views: '62,800 צופים עכשיו',
          desc: 'שידור מבזקים וחדשות בזמן אמת עם יונית לוי, דני קושמרו ופאנל הפרשנים הבכיר של ישראל.'
        },
        {
          id: 'v_z4Y7o51L8',
          title: 'עכשיו 14 | שידור חי 24 שעות ביממה',
          channel: 'עכשיו 14 - Now 14',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_mr4vG8_B4eQkZ7q7Y0n_2Z2pM_F85N=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/v_z4Y7o51L8/maxresdefault.jpg',
          duration: 'LIVE',
          live: true,
          views: '38,100 צופים עכשיו',
          desc: 'הפטריוטים עם ינון מגל, מהדורת החדשות המרכזית, אולפן פתוח ופרשנויות לאורך כל שעות היום.'
        },
        {
          id: 'k62y3M8D1dI',
          title: 'i24NEWS בעברית | שידור חי מסביב לשעון',
          channel: 'i24NEWS עברית',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_kW82j3_F85NnB4eQkZ7q7Y0n_2Z2pM=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/k62y3M8D1dI/maxresdefault.jpg',
          duration: 'LIVE',
          live: true,
          views: '14,300 צופים עכשיו',
          desc: 'ערוץ החדשות הבינלאומי המוביל בשידור ישיר בעברית: מבזקים, ראיונות וכתבות מהארץ ומהעולם.'
        },
        {
          id: 'f9qQ0nS23xM',
          title: 'ערוץ הכנסת | שידור חי ממליאת הכנסת והוועדות',
          channel: 'ערוץ הכנסת - 99',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_j48M2_F85NnB4eQkZ7q7Y0n_2Z2pM=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/f9qQ0nS23xM/maxresdefault.jpg',
          duration: 'LIVE',
          live: true,
          views: '5,600 צופים עכשיו',
          desc: 'דיוני מליאת הכנסת, הצבעות חוקים, ישיבות ועדות ומשדרים מיוחדים מבית המחוקקים הישראלי.'
        }
      ]
    },

    // ------------------------------------------------------------- 🎵 מוזיקה ישראלית (Israeli Music)
    {
      id: 'music',
      title: 'הלהיטים החמים של ישראל 🎵',
      videos: [
        {
          id: 'oM4c9mIqCmg',
          title: 'פאר טסי - רוקדת לבד (Prod. by Guy Dan)',
          channel: 'Peer Tasi פאר טסי',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_nPeerTasi=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/oM4c9mIqCmg/maxresdefault.jpg',
          duration: '03:42',
          views: '4.8M צפיות • לפני שבועיים',
          desc: 'מתוך האלבום החדש "רדיו שטח 2" ששבר את מצעדי ההשמעות בישראל.'
        },
        {
          id: 'W_JmHn7y9pI',
          title: 'אושר כהן - מנגן ושר (קליפ רשמי)',
          channel: 'Osher Cohen - אושר כהן',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_nOsherCohen=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/W_JmHn7y9pI/maxresdefault.jpg',
          duration: '03:18',
          views: '8.2M צפיות • לפני חודש',
          desc: 'השיר שכבש את תחנות הרדיו ואת רשתות הסטרימינג בביצוע מיוחד ומרגש.'
        },
        {
          id: 'yJ1R4wH68wQ',
          title: 'חנן בן ארי - עטלף עיוור (קליפ רשמי)',
          channel: 'חנן בן ארי Hanan Ben Ari',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_nHananBenAri=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/yJ1R4wH68wQ/maxresdefault.jpg',
          duration: '03:52',
          views: '12M צפיות • לפני 3 חודשים',
          desc: 'הלהיט המרגש שנגע בכל לב בישראל, מילים ולחן: חנן בן ארי.'
        },
        {
          id: 's2nQ9mK12xL',
          title: 'טונה - סהרה (Live בהופעה חיה בפארק הירקון)',
          channel: 'Tuna Official - טונה',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_nTunaRap=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/s2nQ9mK12xL/maxresdefault.jpg',
          duration: '05:14',
          views: '15M צפיות • לפני חצי שנה',
          desc: 'ביצוע ענק וחשמל באוויר מול 40 אלף צופים בפארק הירקון.'
        },
        {
          id: 'u7rP4wK89mQ',
          title: 'עומר אדם - סט הופעות חיות קיץ 2026',
          channel: 'Omer Adam - עומר אדם',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_nOmerAdam=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/u7rP4wK89mQ/maxresdefault.jpg',
          duration: '48:32',
          views: '6.4M צפיות • לפני שבועיים',
          desc: 'שעה של כל הלהיטים הגדולים ברצף ללא הפסקה באיכות אודיו גבוהה.'
        }
      ]
    },

    // ------------------------------------------------------------- 🎙️ פודקאסטים וראיונות
    {
      id: 'podcasts',
      title: 'פודקאסטים וראיונות מרתקים 🎙️',
      videos: [
        {
          id: 'vB9m4qN78yQ',
          title: 'הפודיום | פודקאסט הספורט המוביל בישראל - פרק 850',
          channel: 'הפודיום - אורן יוסיפוביץ',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_nPodium=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/vB9m4qN78yQ/maxresdefault.jpg',
          duration: '1:24:15',
          views: '185K צפיות • לפני יומיים',
          desc: 'סיכום השבוע בכדורגל הישראלי, ליגת האלופות, סיפורים מאחורי הקלעים ופאנל מיוחד.'
        },
        {
          id: 'b7mK8wR23xL',
          title: 'אחד ביום | מאחורי כותרות החדשות של ישראל',
          channel: 'N12 פודקאסטים',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_nN12Pod=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/b7mK8wR23xL/maxresdefault.jpg',
          duration: '28:40',
          views: '92K צפיות • אתמול',
          desc: 'אלעד שמחיוף וכתבי חדשות 12 עם הצצה מעמיקה לסיפור המרתק ביותר של השבוע.'
        },
        {
          id: 'q6wR8mP12yK',
          title: 'גיקונומי פרק 620 | עתיד הטכנולוגיה, בינה מלאכותית וחדשנות',
          channel: 'Geekonomy גיקונומי',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_nGeekonomy=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/q6wR8mP12yK/maxresdefault.jpg',
          duration: '1:52:10',
          views: '110K צפיות • לפני 4 ימים',
          desc: 'ראיון עומק עם בכירי ההייטק והיזמים המובילים בישראל ובעולם.'
        }
      ]
    },

    // ------------------------------------------------------------- 😂 קומדיה ובידור
    {
      id: 'comedy',
      title: 'קומדיה, סטנדאפ וסאטירה ישראלית 😂',
      videos: [
        {
          id: 'm8wK2qR78yP',
          title: 'ארץ נהדרת | המערכונים הנבחרים של העונה ברצף',
          channel: 'ארץ נהדרת - קשת 12',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_nEretzNehederet=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/m8wK2qR78yP/maxresdefault.jpg',
          duration: '42:18',
          views: '2.1M צפיות • לפני שבוע',
          desc: 'הנבחרת של ארץ נהדרת במערכוני מופת וחיקויים בלתי נשכחים.'
        },
        {
          id: 'x9mQ4wK78yR',
          title: 'שחר חסון | סטנדאפ מלא בהיכל התרבות (2026)',
          channel: 'Shahar Hason - שחר חסון',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_nShaharHason=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/x9mQ4wK78yR/maxresdefault.jpg',
          duration: '56:22',
          views: '3.4M צפיות • לפני חודש',
          desc: 'מופע הסטנדאפ השלם של שחר חסון עם הקהל הכי אנרגטי בישראל.'
        },
        {
          id: 'w2rP7mK89yQ',
          title: 'גיא הוכמן | תופס ישראלים ברחבי העולם ובמשחקים',
          channel: 'Guy Hochman - גיא הוכמן',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_nGuyHochman=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/w2rP7mK89yQ/maxresdefault.jpg',
          duration: '14:35',
          views: '950K צפיות • לפני 3 ימים',
          desc: 'קצין החיוך של ישראל בסרטון קורע מצחוק מכל מקום בעולם.'
        }
      ]
    },

    // ------------------------------------------------------------- 🎮 גיימינג וטכנולוגיה
    {
      id: 'gaming',
      title: 'גיימינג וטכנולוגיה 🎮',
      videos: [
        {
          id: 'p7mK9wR23yQ',
          title: 'גיימינג ישראלי | הרגעים הכי מטורפים של השנה',
          channel: 'Israel Gaming Hub',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_nGamingIL=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/p7mK9wR23yQ/maxresdefault.jpg',
          duration: '22:15',
          views: '340K צפיות • לפני 5 ימים',
          desc: 'המהלכים הכי יפים, רגעי מצחיקים ושיאים במשחקים הכי חמים.'
        },
        {
          id: 'z8wR2qM78yP',
          title: 'סקירת הדור הבא של הטלוויזיות החכמות: OLED & Mini-LED',
          channel: 'Tech Israel - טכנולוגיה ישראלית',
          avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_nTechIL=s176-c-k-c0x00ffffff-no-rj',
          thumb: 'https://i.ytimg.com/vi/z8wR2qM78yP/maxresdefault.jpg',
          duration: '18:42',
          views: '125K צפיות • לפני שבועיים',
          desc: 'מדריך קנייה מעמיק: איכות תמונה, קצבי רענון וסאונד לחוויית קולנוע ביתי.'
        }
      ]
    }
  ];

  var NAV_ITEMS = [
    { id: 'search', label: 'חיפוש', icon: '🔍' },
    { id: 'home', label: 'דף הבית', icon: '🏠' },
    { id: 'live', label: 'שידורים חיים', icon: '🔴' },
    { id: 'music', label: 'מוזיקה', icon: '🎵' },
    { id: 'trending', label: 'חם ברשת', icon: '🔥' },
    { id: 'podcasts', label: 'פודקאסטים', icon: '🎙️' },
    { id: 'gaming', label: 'גיימינג', icon: '🎮' },
    { id: 'library', label: 'ספרייה', icon: '⭐' },
    { id: 'back_tv', label: 'חזרה ל-Israel TV+', icon: '📺', isSpecial: true },
    { id: 'settings', label: 'הגדרות', icon: '⚙️' }
  ];

  var CHIPS = [
    { id: 'all', label: 'הכל' },
    { id: 'live', label: 'שידורים חיים 🔴' },
    { id: 'music', label: 'מוזיקה ישראלית 🎵' },
    { id: 'news', label: 'חדשות 12/13/14 📰' },
    { id: 'podcasts', label: 'פודקאסטים 🎙️' },
    { id: 'comedy', label: 'סטנדאפ וקומדיה 😂' },
    { id: 'gaming', label: 'גיימינג 🎮' },
    { id: 'tech', label: 'טכנולוגיה 💻' }
  ];

  // State
  var state = {
    open: false,
    zone: 'grid', // 'nav', 'chips', 'hero', 'grid', 'player', 'search_kb'
    navIdx: 1, // Home by default
    chipIdx: 0,
    shelfIdx: 0,
    cardIdx: 0,
    activeTab: 'home',
    activeChip: 'all',
    currentVideo: null,
    isPlaying: false,
    searchQuery: '',
    kbRow: 0,
    kbCol: 0,
    osdVisible: true,
    osdTimer: null
  };

  var el = {};

  function init() {
    createMarkup();
    cacheDom();
    setupEvents();
    renderContent();
    updateClock();
    setInterval(updateClock, 30000);
  }

  function createMarkup() {
    var root = document.getElementById('root');
    if (!root) return;

    var container = document.createElement('div');
    container.id = 'yttv-root';
    container.className = 'yttv-root hidden';
    container.dir = 'rtl';

    container.innerHTML =
      '<aside class="yttv-sidebar" id="yttv-sidebar">' +
        '<div class="yttv-profile" id="yttv-profile" tabindex="0">' +
          '<div class="yttv-profile-avatar">D</div>' +
          '<div class="yttv-profile-info">' +
            '<div class="yttv-profile-name">אורח TV</div>' +
            '<div class="yttv-profile-sub">YouTube Leanback</div>' +
          '</div>' +
        '</div>' +
        '<nav class="yttv-nav-list" id="yttv-nav-list"></nav>' +
      '</aside>' +

      '<main class="yttv-main" id="yttv-main">' +
        '<header class="yttv-header">' +
          '<div class="yttv-brand" id="yttv-brand">' +
            '<svg class="yttv-yt-logo" viewBox="0 0 24 24"><path fill="#ff0000" d="M23.498 6.186a3.016 3.016 0 0 0-2.122-2.136C19.505 3.545 12 3.545 12 3.545s-7.505 0-9.377.505A3.017 3.017 0 0 0 .502 6.186C0 8.07 0 12 0 12s0 3.93.502 5.814a3.016 3.016 0 0 0 2.122 2.136c1.871.505 9.376.505 9.376.505s7.505 0 9.377-.505a3.015 3.015 0 0 0 2.122-2.136C24 15.93 24 12 24 12s0-3.93-.502-5.814zM9.545 15.568V8.432L15.818 12l-6.273 3.568z"/></svg>' +
            '<span class="yttv-logo-text">YouTube</span>' +
            '<span class="yttv-badge-tv">TV</span>' +
          '</div>' +
          '<div class="yttv-header-actions">' +
            '<div class="yttv-clock" id="yttv-clock">00:00</div>' +
            '<button class="yttv-btn-pill" id="yttv-btn-leanback" title="הפעלת אפליקציית Leanback מלאה"><span>🌐 YouTube Web</span></button>' +
            '<button class="yttv-btn-pill" id="yttv-btn-exit"><span>📺 חזרה לטלוויזיה</span></button>' +
          '</div>' +
        '</header>' +

        '<div class="yttv-chips-bar" id="yttv-chips-bar"></div>' +

        '<div class="yttv-content-scroll" id="yttv-content-scroll">' +
          '<div id="yttv-view-home">' +
            '<section class="yttv-hero" id="yttv-hero" tabindex="0">' +
              '<div class="yttv-hero-backdrop"></div>' +
              '<div class="yttv-hero-info">' +
                '<div class="yttv-hero-badge"><span class="live-dot"></span> מומלץ עבורך</div>' +
                '<h1 class="yttv-hero-title" id="yttv-hero-title">כאן 11 | שידור חי באיכות גבוהה 24/7</h1>' +
                '<p class="yttv-hero-desc" id="yttv-hero-desc">שידור ישיר רציף של חדשות, סדרות מקוריות ותוכניות תחקיר מהאולפן המרכזי.</p>' +
                '<div class="yttv-hero-meta">' +
                  '<span class="channel-name" id="yttv-hero-channel">כאן 11 - תאגיד השידור הישראלי</span>' +
                  '<span>•</span>' +
                  '<span id="yttv-hero-views">45.2K צופים עכשיו</span>' +
                '</div>' +
                '<div class="yttv-hero-actions">' +
                  '<button class="yttv-btn-play" id="yttv-hero-btn-play">▶ צפה עכשיו</button>' +
                  '<button class="yttv-btn-secondary" id="yttv-hero-btn-save">+ שמור לספרייה</button>' +
                '</div>' +
              '</div>' +
            '</section>' +
            '<div class="yttv-shelves-container" id="yttv-shelves-container"></div>' +
          '</div>' +

          '<div class="yttv-search-view hidden" id="yttv-view-search">' +
            '<div class="yttv-search-bar" id="yttv-search-bar">' +
              '<span class="yttv-search-ico">🔍</span>' +
              '<input type="search" class="yttv-search-input" id="yttv-search-input" placeholder="חיפוש ב-YouTube TV: ערוצים, זמרים, תוכניות...">' +
              '<button class="yttv-search-clear" id="yttv-search-clear">✕</button>' +
            '</div>' +
            '<div class="yttv-suggestions" id="yttv-suggestions">' +
              '<div class="yttv-sugg-tag" data-q="חדשות 12">חדשות 12</div>' +
              '<div class="yttv-sugg-tag" data-q="כאן 11 שידור חי">כאן 11 שידור חי</div>' +
              '<div class="yttv-sugg-tag" data-q="עומר אדם">עומר אדם</div>' +
              '<div class="yttv-sugg-tag" data-q="אושר כהן">אושר כהן</div>' +
              '<div class="yttv-sugg-tag" data-q="הפודיום">הפודיום פודקאסט</div>' +
              '<div class="yttv-sugg-tag" data-q="ארץ נהדרת">ארץ נהדרת</div>' +
              '<div class="yttv-sugg-tag" data-q="עכשיו 14">עכשיו 14</div>' +
            '</div>' +
            '<div class="yttv-keyboard-box" id="yttv-keyboard-box">' +
              '<div class="yttv-kb-row">' +
                '<button class="yttv-key" data-k="ק">ק</button><button class="yttv-key" data-k="ר">ר</button><button class="yttv-key" data-k="א">א</button><button class="yttv-key" data-k="ט">ט</button><button class="yttv-key" data-k="ו">ו</button><button class="yttv-key" data-k="ן">ן</button><button class="yttv-key" data-k="ם">ם</button><button class="yttv-key" data-k="פ">פ</button>' +
              '</div>' +
              '<div class="yttv-kb-row">' +
                '<button class="yttv-key" data-k="ש">ש</button><button class="yttv-key" data-k="ד">ד</button><button class="yttv-key" data-k="ג">ג</button><button class="yttv-key" data-k="כ">כ</button><button class="yttv-key" data-k="ע">ע</button><button class="yttv-key" data-k="י">י</button><button class="yttv-key" data-k="ח">ח</button><button class="yttv-key" data-k="ל">ל</button><button class="yttv-key" data-k="ך">ך</button><button class="yttv-key" data-k="ף">ף</button>' +
              '</div>' +
              '<div class="yttv-kb-row">' +
                '<button class="yttv-key" data-k="ז">ז</button><button class="yttv-key" data-k="ס">ס</button><button class="yttv-key" data-k="ב">ב</button><button class="yttv-key" data-k="ה">ה</button><button class="yttv-key" data-k="נ">נ</button><button class="yttv-key" data-k="מ">מ</button><button class="yttv-key" data-k="צ">צ</button><button class="yttv-key" data-k="ת">ת</button><button class="yttv-key" data-k="ץ">ץ</button>' +
              '</div>' +
              '<div class="yttv-kb-row">' +
                '<button class="yttv-key space-key" data-k="space">רווח (Space)</button>' +
                '<button class="yttv-key action-key" data-k="backspace">⌫ מחק</button>' +
                '<button class="yttv-key action-key" data-k="clear">נקה הכל</button>' +
              '</div>' +
            '</div>' +
            '<div class="yttv-shelves-container" id="yttv-search-results"></div>' +
          '</div>' +
        '</div>' +
      '</main>' +

      // Fullscreen TV Player
      '<div class="yttv-player-overlay hidden" id="yttv-player-overlay">' +
        '<div id="yttv-player-frame-box" style="width:100%; height:100%;"></div>' +
        '<div class="yttv-player-osd" id="yttv-player-osd">' +
          '<div class="yttv-osd-top">' +
            '<div class="yttv-osd-video-info">' +
              '<img class="yttv-osd-avatar" id="yttv-osd-avatar" src="" alt="">' +
              '<div>' +
                '<h2 class="yttv-osd-title" id="yttv-osd-title">כותרת הסרטון</h2>' +
                '<div class="yttv-osd-channel" id="yttv-osd-channel">שם הערוץ</div>' +
              '</div>' +
            '</div>' +
            '<button class="yttv-osd-btn-close" id="yttv-osd-btn-close" title="סגור נגן">✕</button>' +
          '</div>' +

          '<div class="yttv-osd-bottom">' +
            '<div class="yttv-progress-container">' +
              '<span class="yttv-time" id="yttv-time-current">00:00</span>' +
              '<div class="yttv-progress-bar" id="yttv-progress-bar">' +
                '<div class="yttv-progress-fill" id="yttv-progress-fill" style="width: 35%;"></div>' +
                '<div class="yttv-progress-scrubber" id="yttv-progress-scrubber" style="left: 35%;"></div>' +
              '</div>' +
              '<span class="yttv-time" id="yttv-time-total">LIVE</span>' +
            '</div>' +

            '<div class="yttv-osd-controls">' +
              '<button class="yttv-ctrl-btn" id="yttv-ctrl-prev" title="סרטון קודם">⏮</button>' +
              '<button class="yttv-ctrl-btn" id="yttv-ctrl-rwd" title="10 שניות אחורה">⏪</button>' +
              '<button class="yttv-ctrl-btn big-play" id="yttv-ctrl-play" title="נגן / השהה">⏸</button>' +
              '<button class="yttv-ctrl-btn" id="yttv-ctrl-fwd" title="10 שניות קדימה">⏩</button>' +
              '<button class="yttv-ctrl-btn" id="yttv-ctrl-next" title="סרטון הבא">⏭</button>' +
              '<button class="yttv-ctrl-btn" id="yttv-ctrl-mute" title="השתק">🔊</button>' +
            '</div>' +
          '</div>' +
        '</div>' +
      '</div>';

    root.appendChild(container);
  }

  function cacheDom() {
    el.root = document.getElementById('yttv-root');
    el.sidebar = document.getElementById('yttv-sidebar');
    el.navList = document.getElementById('yttv-nav-list');
    el.chipsBar = document.getElementById('yttv-chips-bar');
    el.shelves = document.getElementById('yttv-shelves-container');
    el.hero = document.getElementById('yttv-hero');
    el.heroTitle = document.getElementById('yttv-hero-title');
    el.heroDesc = document.getElementById('yttv-hero-desc');
    el.heroChannel = document.getElementById('yttv-hero-channel');
    el.heroViews = document.getElementById('yttv-hero-views');
    el.heroPlayBtn = document.getElementById('yttv-hero-btn-play');
    el.heroSaveBtn = document.getElementById('yttv-hero-btn-save');
    el.clock = document.getElementById('yttv-clock');
    el.btnExit = document.getElementById('yttv-btn-exit');
    el.btnLeanback = document.getElementById('yttv-btn-leanback');
    el.viewHome = document.getElementById('yttv-view-home');
    el.viewSearch = document.getElementById('yttv-view-search');
    el.searchInput = document.getElementById('yttv-search-input');
    el.searchClear = document.getElementById('yttv-search-clear');
    el.searchResults = document.getElementById('yttv-search-results');
    el.playerOverlay = document.getElementById('yttv-player-overlay');
    el.playerFrameBox = document.getElementById('yttv-player-frame-box');
    el.playerOsd = document.getElementById('yttv-player-osd');
    el.osdTitle = document.getElementById('yttv-osd-title');
    el.osdChannel = document.getElementById('yttv-osd-channel');
    el.osdAvatar = document.getElementById('yttv-osd-avatar');
    el.osdBtnClose = document.getElementById('yttv-osd-btn-close');
    el.ctrlPlay = document.getElementById('yttv-ctrl-play');
    el.ctrlRwd = document.getElementById('yttv-ctrl-rwd');
    el.ctrlFwd = document.getElementById('yttv-ctrl-fwd');
    el.ctrlNext = document.getElementById('yttv-ctrl-next');
    el.ctrlPrev = document.getElementById('yttv-ctrl-prev');
    el.ctrlMute = document.getElementById('yttv-ctrl-mute');
  }

  function renderContent() {
    // Render Navigation Sidebar
    el.navList.innerHTML = NAV_ITEMS.map(function (item, idx) {
      var cls = 'yttv-nav-item' + (idx === state.navIdx ? ' active' : '') + (item.isSpecial ? ' yttv-back-to-tv' : '');
      return '<div class="' + cls + '" data-id="' + item.id + '" data-idx="' + idx + '" tabindex="0">' +
        '<span class="yttv-nav-ico">' + item.icon + '</span>' +
        '<span class="yttv-nav-label">' + item.label + '</span>' +
      '</div>';
    }).join('');

    // Render Filter Chips
    el.chipsBar.innerHTML = CHIPS.map(function (c, idx) {
      var cls = 'yttv-chip' + (idx === state.chipIdx ? ' active' : '');
      return '<button class="' + cls + '" data-id="' + c.id + '" data-idx="' + idx + '" tabindex="0">' + c.label + '</button>';
    }).join('');

    // Set Hero Featured Video
    var heroVideo = YOUTUBE_DATA[0].videos[0];
    if (heroVideo) {
      el.hero.style.backgroundImage = 'url(' + heroVideo.thumb + ')';
      el.heroTitle.textContent = heroVideo.title;
      el.heroDesc.textContent = heroVideo.desc;
      el.heroChannel.textContent = heroVideo.channel;
      el.heroViews.textContent = heroVideo.views;
      el.hero.setAttribute('data-video-id', heroVideo.id);
    }

    // Render Shelves
    renderShelves(YOUTUBE_DATA, el.shelves);
  }

  function renderShelves(shelvesData, targetEl) {
    targetEl.innerHTML = shelvesData.map(function (shelf, sIdx) {
      var cardsHtml = shelf.videos.map(function (v, cIdx) {
        var durCls = 'yttv-duration' + (v.live ? ' live' : '');
        return '<div class="yttv-card" data-shelf="' + sIdx + '" data-card="' + cIdx + '" data-video-id="' + v.id + '" tabindex="0">' +
          '<div class="yttv-thumb-box">' +
            '<img src="' + v.thumb + '" alt="' + escapeHtml(v.title) + '" loading="lazy">' +
            '<div class="' + durCls + '">' + v.duration + '</div>' +
          '</div>' +
          '<div class="yttv-card-meta">' +
            '<img class="yttv-channel-avatar" src="' + v.avatar + '" alt="">' +
            '<div class="yttv-card-details">' +
              '<div class="yttv-card-title">' + escapeHtml(v.title) + '</div>' +
              '<div class="yttv-card-author">' + escapeHtml(v.channel) + ' <svg class="yttv-verified-ico" viewBox="0 0 24 24"><path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10 10-4.5 10-10S17.5 2 12 2zm-1.8 14.5l-3.7-3.7 1.4-1.4 2.3 2.3 5.3-5.3 1.4 1.4-6.7 6.7z"/></svg></div>' +
              '<div class="yttv-card-stats">' + escapeHtml(v.views) + '</div>' +
            '</div>' +
          '</div>' +
        '</div>';
      }).join('');

      return '<section class="yttv-shelf" data-shelf="' + sIdx + '">' +
        '<div class="yttv-shelf-header">' +
          '<h2 class="yttv-shelf-title">' + shelf.title + '</h2>' +
          (shelf.badge ? '<span class="yttv-shelf-badge">' + shelf.badge + '</span>' : '') +
        '</div>' +
        '<div class="yttv-shelf-row">' + cardsHtml + '</div>' +
      '</section>';
    }).join('');
  }

  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
  }

  function setupEvents() {
    // Navigation rail click
    el.navList.addEventListener('click', function (e) {
      var item = e.target.closest('.yttv-nav-item');
      if (!item) return;
      var id = item.getAttribute('data-id');
      handleNavSelection(id);
    });

    // Chips click
    el.chipsBar.addEventListener('click', function (e) {
      var chip = e.target.closest('.yttv-chip');
      if (!chip) return;
      var id = chip.getAttribute('data-id');
      selectChip(id);
    });

    // Card click
    el.shelves.addEventListener('click', function (e) {
      var card = e.target.closest('.yttv-card');
      if (!card) return;
      var vid = card.getAttribute('data-video-id');
      var sIdx = parseInt(card.getAttribute('data-shelf'), 10);
      var cIdx = parseInt(card.getAttribute('data-card'), 10);
      playVideoById(vid, sIdx, cIdx);
    });

    // Hero buttons click
    el.heroPlayBtn.addEventListener('click', function () {
      var vid = el.hero.getAttribute('data-video-id');
      playVideoById(vid, 0, 0);
    });

    // Header exit button
    el.btnExit.addEventListener('click', function () {
      close();
    });

    // Header Leanback web launch button
    el.btnLeanback.addEventListener('click', function () {
      launchOfficialLeanback();
    });

    // Player OSD Controls
    el.osdBtnClose.addEventListener('click', closePlayer);
    el.ctrlPlay.addEventListener('click', togglePlayPause);
    el.ctrlRwd.addEventListener('click', function () { seekRelative(-10); });
    el.ctrlFwd.addEventListener('click', function () { seekRelative(10); });
    el.ctrlNext.addEventListener('click', playNextVideo);
    el.ctrlPrev.addEventListener('click', playPrevVideo);

    // Search Virtual Keyboard Click
    var kbBox = document.getElementById('yttv-keyboard-box');
    if (kbBox) {
      kbBox.addEventListener('click', function (e) {
        var key = e.target.closest('.yttv-key');
        if (!key) return;
        var k = key.getAttribute('data-k');
        handleVirtualKey(k);
      });
    }

    // Search Suggestions Click
    var suggBox = document.getElementById('yttv-suggestions');
    if (suggBox) {
      suggBox.addEventListener('click', function (e) {
        var tag = e.target.closest('.yttv-sugg-tag');
        if (!tag) return;
        var q = tag.getAttribute('data-q');
        el.searchInput.value = q;
        performSearch(q);
      });
    }

    // Search input typing
    el.searchInput.addEventListener('input', function () {
      performSearch(el.searchInput.value);
    });

    el.searchClear.addEventListener('click', function () {
      el.searchInput.value = '';
      el.searchResults.innerHTML = '';
      el.searchInput.focus();
    });

    // Mouse movement reveals OSD
    document.addEventListener('mousemove', function () {
      if (state.zone === 'player' && !el.playerOverlay.classList.contains('hidden')) {
        showPlayerOsd();
      }
    });
  }

  function updateClock() {
    var now = new Date();
    var h = String(now.getHours()).padStart(2, '0');
    var m = String(now.getMinutes()).padStart(2, '0');
    if (el.clock) el.clock.textContent = h + ':' + m;
  }

  function handleNavSelection(id) {
    if (id === 'back_tv') {
      close();
      return;
    }
    if (id === 'search') {
      showSearchView();
      return;
    }
    if (id === 'settings') {
      launchOfficialLeanback();
      return;
    }

    // Home / Live / Music / Trending tabs
    showHomeView();
    selectChip(id);
  }

  function showHomeView() {
    el.viewHome.classList.remove('hidden');
    el.viewSearch.classList.add('hidden');
    state.activeTab = 'home';
  }

  function showSearchView() {
    el.viewHome.classList.add('hidden');
    el.viewSearch.classList.remove('hidden');
    state.activeTab = 'search';
    state.zone = 'search_kb';
    el.searchInput.focus();
  }

  function selectChip(id) {
    state.activeChip = id;
    var chips = el.chipsBar.querySelectorAll('.yttv-chip');
    chips.forEach(function (c) {
      if (c.getAttribute('data-id') === id) c.classList.add('active');
      else c.classList.remove('active');
    });

    if (id === 'all') {
      renderShelves(YOUTUBE_DATA, el.shelves);
    } else {
      var filtered = YOUTUBE_DATA.filter(function (s) { return s.id === id; });
      if (filtered.length) {
        renderShelves(filtered, el.shelves);
      } else {
        renderShelves(YOUTUBE_DATA, el.shelves);
      }
    }
  }

  function handleVirtualKey(k) {
    if (!k) return;
    if (k === 'space') {
      el.searchInput.value += ' ';
    } else if (k === 'backspace') {
      el.searchInput.value = el.searchInput.value.slice(0, -1);
    } else if (k === 'clear') {
      el.searchInput.value = '';
    } else {
      el.searchInput.value += k;
    }
    performSearch(el.searchInput.value);
  }

  function performSearch(query) {
    query = (query || '').trim().toLowerCase();
    if (!query) {
      el.searchResults.innerHTML = '';
      return;
    }

    var matches = [];
    YOUTUBE_DATA.forEach(function (shelf) {
      shelf.videos.forEach(function (v) {
        if (v.title.toLowerCase().indexOf(query) !== -1 ||
            v.channel.toLowerCase().indexOf(query) !== -1 ||
            (v.desc && v.desc.toLowerCase().indexOf(query) !== -1)) {
          matches.push(v);
        }
      });
    });

    // If query didn't match pre-curated, generate dynamic matching mock search result
    if (matches.length === 0) {
      matches.push({
        id: 'search_' + encodeURIComponent(query),
        title: query + ' | תוצאות חיפוש ב-YouTube',
        channel: 'YouTube Video Search',
        avatar: 'https://yt3.googleusercontent.com/ytc/AIdro_k2P2pM_F85NnB4eQkZ7q7Y0n_2Z2pM=s176-c-k-c0x00ffffff-no-rj',
        thumb: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&q=80',
        duration: '10:00',
        views: '100K צפיות',
        desc: 'צפייה בסרטון לפי מילת החיפוש המבוקשת'
      });
    }

    renderShelves([{
      id: 'search_res',
      title: 'תוצאות עבור "' + query + '"',
      videos: matches
    }], el.searchResults);
  }

  function playVideoById(vid, sIdx, cIdx) {
    var videoObj = null;
    YOUTUBE_DATA.forEach(function (s) {
      s.videos.forEach(function (v) {
        if (v.id === vid) videoObj = v;
      });
    });

    if (!videoObj) {
      videoObj = {
        id: vid,
        title: 'YouTube Video',
        channel: 'YouTube TV',
        avatar: '',
        duration: 'LIVE',
        views: ''
      };
    }

    state.currentVideo = videoObj;
    state.shelfIdx = sIdx || 0;
    state.cardIdx = cIdx || 0;

    // Update OSD info
    el.osdTitle.textContent = videoObj.title;
    el.osdChannel.textContent = videoObj.channel;
    el.osdAvatar.src = videoObj.avatar || videoObj.thumb;

    // Open Player Overlay
    el.playerOverlay.classList.remove('hidden');
    state.zone = 'player';
    state.isPlaying = true;
    el.ctrlPlay.textContent = '⏸';

    // Embed Responsive YouTube Player with TV optimized params
    var embedUrl = 'https://www.youtube.com/embed/' + vid + '?autoplay=1&enablejsapi=1&origin=' + encodeURIComponent(location.origin) + '&controls=1&rel=0&modestbranding=1&playsinline=1';
    el.playerFrameBox.innerHTML = '<iframe class="yttv-video-frame" id="yttv-iframe" src="' + embedUrl + '" allow="autoplay; fullscreen; encrypted-media; picture-in-picture" allowfullscreen></iframe>';

    showPlayerOsd();
  }

  function showPlayerOsd() {
    el.playerOsd.classList.remove('off');
    clearTimeout(state.osdTimer);
    state.osdTimer = setTimeout(function () {
      el.playerOsd.classList.add('off');
    }, 4500);
  }

  function togglePlayPause() {
    state.isPlaying = !state.isPlaying;
    el.ctrlPlay.textContent = state.isPlaying ? '⏸' : '▶';
    var iframe = document.getElementById('yttv-iframe');
    if (iframe && iframe.contentWindow) {
      var act = state.isPlaying ? 'playVideo' : 'pauseVideo';
      iframe.contentWindow.postMessage(JSON.stringify({ event: 'command', func: act, args: [] }), '*');
    }
    showPlayerOsd();
  }

  function seekRelative(sec) {
    showPlayerOsd();
    var iframe = document.getElementById('yttv-iframe');
    if (iframe && iframe.contentWindow) {
      // Seek via YouTube JS postMessage API
      iframe.contentWindow.postMessage(JSON.stringify({ event: 'command', func: 'seekTo', args: [sec, true] }), '*');
    }
  }

  function playNextVideo() {
    var currentShelf = YOUTUBE_DATA[state.shelfIdx] || YOUTUBE_DATA[0];
    var nextCardIdx = (state.cardIdx + 1) % currentShelf.videos.length;
    var nextVid = currentShelf.videos[nextCardIdx];
    playVideoById(nextVid.id, state.shelfIdx, nextCardIdx);
  }

  function playPrevVideo() {
    var currentShelf = YOUTUBE_DATA[state.shelfIdx] || YOUTUBE_DATA[0];
    var prevCardIdx = (state.cardIdx - 1 + currentShelf.videos.length) % currentShelf.videos.length;
    var prevVid = currentShelf.videos[prevCardIdx];
    playVideoById(prevVid.id, state.shelfIdx, prevCardIdx);
  }

  function closePlayer() {
    el.playerOverlay.classList.add('hidden');
    el.playerFrameBox.innerHTML = '';
    state.zone = 'grid';
    state.isPlaying = false;
    clearTimeout(state.osdTimer);
  }

  function launchOfficialLeanback() {
    // Check if on webOS TV
    if (window.webOS && window.webOS.service) {
      try {
        window.webOS.service.request('luna://com.webos.applicationManager', {
          method: 'launch',
          parameters: { id: 'youtube.leanback.v4' },
          onSuccess: function () {},
          onFailure: function () {
            window.location.href = 'https://www.youtube.com/tv#/';
          }
        });
        return;
      } catch (e) {}
    }
    // Browser or webOS fallback
    window.open('https://www.youtube.com/tv#/', '_blank');
  }

  // --------------------------------------------------------------------------
  // TV Remote D-Pad Navigation Handler
  // --------------------------------------------------------------------------
  function handleKey(code, e) {
    if (e && e.preventDefault) e.preventDefault();

    // Player Mode
    if (state.zone === 'player') {
      showPlayerOsd();
      if (code === 461 || code === 27 || code === 10009 || code === 8) {
        closePlayer();
        return;
      }
      if (code === 13 || code === 32 || code === 19) {
        togglePlayPause();
        return;
      }
      if (code === 37) { seekRelative(10); return; } // Right (in RTL) / Forward
      if (code === 39) { seekRelative(-10); return; } // Left (in RTL) / Rewind
      if (code === 38 || code === 40) { showPlayerOsd(); return; }
      return;
    }

    // Back key
    if (code === 461 || code === 27 || code === 10009 || code === 8) {
      if (state.activeTab === 'search') {
        showHomeView();
        state.zone = 'grid';
        return;
      }
      if (state.zone === 'grid' || state.zone === 'hero' || state.zone === 'chips') {
        state.zone = 'nav';
        expandSidebar(true);
        markFocus();
        return;
      }
      if (state.zone === 'nav') {
        close();
        return;
      }
    }

    // Navigation Zones
    switch (state.zone) {
      case 'nav':
        if (code === 38) { // Up
          state.navIdx = Math.max(0, state.navIdx - 1);
          markFocus();
        } else if (code === 40) { // Down
          state.navIdx = Math.min(NAV_ITEMS.length - 1, state.navIdx + 1);
          markFocus();
        } else if (code === 37) { // Left (move into main content in RTL)
          expandSidebar(false);
          state.zone = 'chips';
          markFocus();
        } else if (code === 13) { // OK
          var navItem = NAV_ITEMS[state.navIdx];
          handleNavSelection(navItem.id);
        }
        break;

      case 'chips':
        if (code === 39) { // Right (back to nav rail in RTL)
          state.zone = 'nav';
          expandSidebar(true);
          markFocus();
        } else if (code === 37) { // Left (next chip)
          state.chipIdx = Math.min(CHIPS.length - 1, state.chipIdx + 1);
          markFocus();
        } else if (code === 40) { // Down (to hero or first shelf)
          state.zone = 'hero';
          markFocus();
        } else if (code === 13) { // OK
          var c = CHIPS[state.chipIdx];
          selectChip(c.id);
        }
        break;

      case 'hero':
        if (code === 38) { // Up to chips
          state.zone = 'chips';
          markFocus();
        } else if (code === 40) { // Down to first shelf
          state.zone = 'grid';
          state.shelfIdx = 0;
          state.cardIdx = 0;
          markFocus();
        } else if (code === 39) { // Right to sidebar
          state.zone = 'nav';
          expandSidebar(true);
          markFocus();
        } else if (code === 13) { // OK play
          var vid = el.hero.getAttribute('data-video-id');
          playVideoById(vid, 0, 0);
        }
        break;

      case 'grid':
        var currentShelf = YOUTUBE_DATA[state.shelfIdx] || YOUTUBE_DATA[0];
        if (code === 37) { // Left (next card)
          if (state.cardIdx < currentShelf.videos.length - 1) {
            state.cardIdx++;
            markFocus();
          }
        } else if (code === 39) { // Right (prev card or back to sidebar)
          if (state.cardIdx > 0) {
            state.cardIdx--;
            markFocus();
          } else {
            state.zone = 'nav';
            expandSidebar(true);
            markFocus();
          }
        } else if (code === 38) { // Up
          if (state.shelfIdx > 0) {
            state.shelfIdx--;
            state.cardIdx = 0;
            markFocus();
          } else {
            state.zone = 'hero';
            markFocus();
          }
        } else if (code === 40) { // Down
          if (state.shelfIdx < YOUTUBE_DATA.length - 1) {
            state.shelfIdx++;
            state.cardIdx = 0;
            markFocus();
          }
        } else if (code === 13) { // OK
          var selectedVideo = currentShelf.videos[state.cardIdx];
          if (selectedVideo) {
            playVideoById(selectedVideo.id, state.shelfIdx, state.cardIdx);
          }
        }
        break;
    }
  }

  function expandSidebar(exp) {
    if (el.sidebar) {
      if (exp) el.sidebar.classList.add('expanded');
      else el.sidebar.classList.remove('expanded');
    }
  }

  function markFocus() {
    // Remove all previous focused styles
    document.querySelectorAll('.yttv-root .focused').forEach(function (n) {
      n.classList.remove('focused');
    });

    if (state.zone === 'nav') {
      var navItems = el.navList.querySelectorAll('.yttv-nav-item');
      if (navItems[state.navIdx]) {
        navItems[state.navIdx].classList.add('focused');
        navItems[state.navIdx].scrollIntoView({ block: 'nearest' });
      }
    } else if (state.zone === 'chips') {
      var chips = el.chipsBar.querySelectorAll('.yttv-chip');
      if (chips[state.chipIdx]) {
        chips[state.chipIdx].classList.add('focused');
        chips[state.chipIdx].scrollIntoView({ inline: 'nearest' });
      }
    } else if (state.zone === 'hero') {
      el.hero.classList.add('focused');
      el.hero.scrollIntoView({ block: 'nearest' });
    } else if (state.zone === 'grid') {
      var shelf = el.shelves.querySelector('.yttv-shelf[data-shelf="' + state.shelfIdx + '"]');
      if (shelf) {
        var card = shelf.querySelector('.yttv-card[data-card="' + state.cardIdx + '"]');
        if (card) {
          card.classList.add('focused');
          card.scrollIntoView({ inline: 'center', block: 'nearest' });
        }
      }
    }
  }

  // --------------------------------------------------------------------------
  // Public Interface
  // --------------------------------------------------------------------------
  function open() {
    if (!el.root) init();
    el.root.classList.remove('hidden');
    state.open = true;

    // Pause Israel TV live playback if active
    if (window.ITV && typeof window.ITV.pause === 'function') {
      window.ITV.pause();
    }

    state.zone = 'grid';
    expandSidebar(false);
    markFocus();
  }

  function close() {
    if (el.root) el.root.classList.add('hidden');
    closePlayer();
    state.open = false;

    // Restore focus to Israel TV+
    if (window.ITV_RESTORE) {
      window.ITV_RESTORE();
    }
  }

  function isOpen() {
    return state.open;
  }

  window.YTTV = {
    init: init,
    open: open,
    close: close,
    isOpen: isOpen,
    handleKey: handleKey,
    playVideoById: playVideoById
  };

  // Auto-init on load
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    setTimeout(init, 0);
  }
})();
