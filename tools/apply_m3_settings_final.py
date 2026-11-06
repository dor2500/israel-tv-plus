import os, re

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

# 1. Update ui.js
with open("ui.js", "r", encoding="utf-8") as f:
    ui_code = f.read().replace("\r\n", "\n")

target_ui_main = '<div class="settings-main"><div class="settings-head"><h2 id="settings-h"></h2><p id="settings-p"></p></div>' + \
    '<div class="settings-rows" id="settings-rows"></div></div>'

repl_ui_main = '<div class="settings-main"><div class="settings-head"><h2 id="settings-h"></h2><p id="settings-p"></p></div>' + \
    '<div class="settings-rows" id="settings-rows"></div>' + \
    '<div class="m3-tv-hints" id="settings-tv-hints">' + \
    '<span class="m3-hint-pill"><span class="m3-key">▲▼</span> ניווט</span>' + \
    '<span class="m3-hint-pill"><span class="m3-key">◀▶</span> בחירה / שינוי</span>' + \
    '<span class="m3-hint-pill"><span class="m3-key">OK</span> אישור</span>' + \
    '<span class="m3-hint-pill"><span class="m3-key">Back</span> יציאה</span>' + \
    '</div></div>'

if target_ui_main in ui_code:
    ui_code = ui_code.replace(target_ui_main, repl_ui_main)
    print("ui.js updated via direct match!")
else:
    ui_code = re.sub(
        r'<div class="settings-rows" id="settings-rows"></div></div>',
        '<div class="settings-rows" id="settings-rows"></div><div class="m3-tv-hints" id="settings-tv-hints"><span class="m3-hint-pill"><span class="m3-key">▲▼</span> ניווט</span><span class="m3-hint-pill"><span class="m3-key">◀▶</span> בחירה / שינוי</span><span class="m3-hint-pill"><span class="m3-key">OK</span> אישור</span><span class="m3-hint-pill"><span class="m3-key">Back</span> יציאה</span></div></div>',
        ui_code
    )
    print("ui.js updated via regex!")

with open("ui.js", "w", encoding="utf-8") as f:
    f.write(ui_code)

# 2. Update app.js
with open("app.js", "r", encoding="utf-8") as f:
    app_code = f.read().replace("\r\n", "\n")

m3_icons_block = """  var M3_ICONS = {
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

"""

new_settings_sections = """  function settingsSections() {
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
      { id: 'display', ico: M3_ICONS.display, name: 'תצוגה וממשק', desc: 'ערכות נושא, פלטת צבעים, קנה מידה ועיצוב מסך', rows: [
        sep('מראה'),
        choice('theme', 'ערכת נושא', THEMES),
        { type: 'choice', label: 'פלטת צבעים (Material You)', help: readSystemColors() ? 'צבעי המערכת מבוססים על ערכת הטפט (Dynamic Colors)' : 'ערכות הצבע הנבחרות של הממשק',
          get: function () { return label(PALETTES, S.accent) + (S.accent === 'auto' ? ' (' + label(PALETTES, accentName()) + ')' : ''); },
          step: function (st) {
            var list = PALETTES.filter(function (p) { return p[0] !== 'system' || readSystemColors(); });
            setSetting('accent', cycle(list, S.accent, st));
          } },
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
      { id: 'player', ico: M3_ICONS.player, name: 'נגן וידאו ושידור', desc: 'איכות תמונה, השהיה, שחזור שידור ומעבר ערוצים', rows: [
        sep('תמונה ואיכות'),
        choice('pic', 'יחס תמונה', PICS), choice('aspect', 'התאמת תמונה למסך', ASPECTS),
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
        action('יומן גרסאות וחידושים (GitHub)', function () { openUrl(RELEASES); })
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
    ];"""

# Replace settingsSections in app.js
sec_start = app_code.find("  function settingsSections() {")
sec_end = app_code.find("    if (PHONE) {", sec_start)
if sec_start != -1 and sec_end != -1:
    app_code = app_code[:sec_start] + m3_icons_block + new_settings_sections + "\n" + app_code[sec_end:]
    print("settingsSections replaced with M3 icons and native Hebrew!")
else:
    print(f"Error finding settingsSections boundaries: {sec_start}, {sec_end}")

# Replace icon and header rendering in reallyOpenSettings and renderSettings
app_code = app_code.replace(
    "return '<div class=\"nav-item\" data-i=\"' + i + '\"><span class=\"ni-ico\">' + s.ico + '</span>' + esc(s.name) + '</div>';",
    "return '<div class=\"nav-item\" data-i=\"' + i + '\"><span class=\"ni-ico\">' + s.ico + '</span><span class=\"ni-label\">' + esc(s.name) + '</span></div>';"
)

app_code = app_code.replace(
    "el.settingsH.textContent = sec.name;",
    "el.settingsH.innerHTML = '<span class=\"set-head-ico\">' + sec.ico + '</span><span>' + esc(sec.name) + '</span>';"
)

with open("app.js", "w", encoding="utf-8") as f:
    f.write(app_code)
print("app.js saved successfully!")

# 3. Update style.css
with open("style.css", "r", encoding="utf-8") as f:
    css_code = f.read().replace("\r\n", "\n")

m3_tv_css = """/* ---- Material 3 Expressive TV & Desktop Settings System ---- */
body:not(.phone) #settings.overlay {
  background: rgba(0, 0, 0, 0.78);
  backdrop-filter: blur(36px);
  -webkit-backdrop-filter: blur(36px);
  z-index: 60;
}
body:not(.phone) .settings-box {
  width: 90vw;
  max-width: 124rem;
  height: 86vh;
  margin: auto;
  border-radius: 28px;
  background: rgba(14, 18, 27, 0.98);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 28px 72px rgba(0, 0, 0, 0.85);
  overflow: hidden;
  display: flex;
  direction: rtl;
}
body:not(.phone) .settings-side {
  width: 35rem;
  padding: 2.8rem 1.8rem;
  background: rgba(255, 255, 255, 0.015);
  border-left: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  scrollbar-width: none;
  flex-shrink: 0;
}
body:not(.phone) .settings-side::-webkit-scrollbar { display: none; }
body:not(.phone) .settings-title {
  display: flex;
  align-items: center;
  gap: 1.2rem;
  font-size: 2.4rem;
  font-weight: 800;
  color: #f8fafc;
  letter-spacing: -0.02em;
  padding: 0 1rem 2rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 1.6rem;
}
body:not(.phone) .settings-title .ic {
  font-size: 2.6rem;
  width: 2.6rem;
  height: 2.6rem;
  fill: #60a5fa;
  color: #60a5fa;
  margin: 0;
}
body:not(.phone) .nav-item {
  display: flex;
  align-items: center;
  height: 5.2rem;
  padding: 0 1.8rem;
  margin-bottom: 0.55rem;
  border-radius: 9999px;
  font-size: 1.55rem;
  font-weight: 600;
  color: #94a3b8;
  border: 1.5px solid transparent;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.2, 0, 0, 1);
}
body:not(.phone) .nav-item .ni-ico {
  width: 2.4rem;
  height: 2.4rem;
  margin-left: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: inherit;
}
body:not(.phone) .nav-item .ni-ico svg, body:not(.phone) .m3-ico {
  width: 2.2rem;
  height: 2.2rem;
  fill: currentColor;
}
body:not(.phone) .nav-item.active {
  background: rgba(59, 130, 246, 0.16);
  color: #93c5fd;
  font-weight: 700;
  border-color: rgba(59, 130, 246, 0.28);
}
body:not(.phone) .nav-item.focused {
  background: #2563eb !important;
  color: #ffffff !important;
  transform: scale(1.03) !important;
  outline: 3px solid #60a5fa !important;
  outline-offset: 2px !important;
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.5) !important;
}
body:not(.phone) .settings-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  padding: 2.8rem 3.4rem 2rem;
}
body:not(.phone) .settings-head {
  width: 100%;
}
body:not(.phone) .settings-head h2 {
  display: flex;
  align-items: center;
  gap: 1.2rem;
  font-size: 2.6rem;
  font-weight: 800;
  color: #f8fafc;
  letter-spacing: -0.02em;
  margin: 0;
}
body:not(.phone) .set-head-ico {
  display: flex;
  align-items: center;
  justify-content: center;
}
body:not(.phone) .set-head-ico svg {
  width: 3.2rem;
  height: 3.2rem;
  fill: #60a5fa;
}
body:not(.phone) .settings-head p {
  font-size: 1.35rem;
  color: #94a3b8;
  margin: 0.5rem 0 2rem;
  line-height: 1.4;
}
body:not(.phone) .settings-rows {
  flex: 1;
  overflow-y: auto;
  padding: 0.4rem;
  scrollbar-width: thin;
}
body:not(.phone) .set-group {
  margin-bottom: 1.2rem;
  background: transparent;
}
body:not(.phone) .set-row {
  display: flex;
  align-items: center;
  min-height: 6.2rem;
  padding: 1.1rem 2.2rem;
  margin-bottom: 0.8rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.04);
  border: 1.5px solid rgba(255, 255, 255, 0.07);
  cursor: pointer;
  box-shadow: none;
  transition: all 0.2s cubic-bezier(0.2, 0, 0, 1);
}
body:not(.phone) .set-group .set-row {
  min-height: 6.2rem;
  padding: 1.1rem 2.2rem;
  margin-bottom: 0.8rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.04);
  border: 1.5px solid rgba(255, 255, 255, 0.07);
  box-shadow: none;
}
body:not(.phone) .set-group .set-row:first-child,
body:not(.phone) .set-group .set-row:last-child {
  border-radius: 20px;
}
body:not(.phone) .set-row.focused,
body:not(.phone) .set-group .set-row.focused {
  background: rgba(255, 255, 255, 0.12) !important;
  border-color: #60a5fa !important;
  outline: 3px solid #60a5fa !important;
  outline-offset: 2px !important;
  transform: scale(1.02) !important;
  box-shadow: 0 12px 36px rgba(0, 0, 0, 0.6) !important;
}
body:not(.phone) .set-row .sr-label {
  font-size: 1.65rem;
  font-weight: 700;
  color: #f8fafc;
}
body:not(.phone) .set-row .sr-help {
  font-size: 1.25rem;
  color: #94a3b8;
  margin-top: 0.25rem;
  line-height: 1.4;
}
body:not(.phone) .set-row .sr-val .v {
  background: rgba(0, 0, 0, 0.4);
  padding: 0.45rem 1.4rem;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #93c5fd;
  font-size: 1.45rem;
  font-weight: 700;
  min-width: 14rem;
  text-align: center;
}
body:not(.phone) .set-row.focused .arr {
  color: #60a5fa;
  transform: scale(1.2);
}
body:not(.phone) .set-sep {
  font-size: 1.35rem;
  font-weight: 800;
  color: #60a5fa;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  padding: 2rem 0.6rem 0.8rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 1rem;
}
body:not(.phone) .upd-card.gcard {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.04);
  border: 1.5px solid rgba(255, 255, 255, 0.07);
}

/* Material 3 Expressive TV Navigation Hints Bar */
.m3-tv-hints {
  margin-top: auto;
  padding-top: 1.4rem;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  gap: 1.2rem;
  align-items: center;
  justify-content: flex-start;
  flex-shrink: 0;
}
body.phone .m3-tv-hints { display: none; }
.m3-hint-pill {
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.35rem 0.9rem;
  border-radius: 9999px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #cbd5e1;
  font-size: 1.15rem;
  font-weight: 600;
}
.m3-key {
  background: rgba(255, 255, 255, 0.14);
  color: #ffffff;
  padding: 0.15rem 0.6rem;
  border-radius: 6px;
  font-weight: 800;
  font-size: 1.1rem;
}
"""

css_replace_target = "/* on computers and TVs: a full page with grouped rows, in the theme's own colors */"
tgt_idx = css_code.find(css_replace_target)
if tgt_idx != -1:
    css_code = css_code[:tgt_idx] + m3_tv_css
    print("style.css settings block replaced with M3 Expressive TV system!")
else:
    css_code += "\n" + m3_tv_css
    print("style.css appended with M3 Expressive!")

with open("style.css", "w", encoding="utf-8") as f:
    f.write(css_code.strip() + "\n")
print("style.css saved successfully!")

