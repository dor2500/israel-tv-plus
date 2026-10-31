# tools/patch_app_tv_epg.py
import re

app_path = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\app.js'
with open(app_path, 'r', encoding='utf-8') as f:
    text = f.read()

epg_code = """
  // ================================================================== TV EPG & Catch-Up Engine
  var tvEpgActiveIdx = 1; // 0: prev (catchup), 1: current, 2: next, 3: options
  var tvEpgTimer = null;
  var tvEpgClockTimer = null;

  var SHOW_CATALOG = {
    'keshet12': [
      ['06:00', '09:00', 'חדשות הבוקר עם ניב רסקין'],
      ['09:00', '12:00', 'פאולה וליאון'],
      ['12:00', '14:00', 'המהדורה המוקדמת'],
      ['14:00', '15:30', 'סדר עולמי'],
      ['15:30', '17:00', 'מהדורה ראשונה עם אורן וייגנפלד'],
      ['17:00', '18:00', 'שש עם עודד בן עמי'],
      ['18:00', '19:00', 'תוכנית חיסכון'],
      ['19:00', '20:00', 'המהדורה המרכזית עם דני קושמרו'],
      ['20:00', '21:30', 'ארץ נהדרת (עונה 22)'],
      ['21:30', '23:00', 'הזמר במסכה'],
      ['23:00', '00:30', 'לילה טוב עם גיא פינס']
    ],
    'reshet13': [
      ['06:00', '09:30', 'העולם הבוקר'],
      ['09:30', '12:00', 'פותחים יום'],
      ['12:00', '14:00', 'חדשות 13 – עדכון צהריים'],
      ['14:00', '15:30', 'הצינור עם גיא לרר – לקט מיוחד'],
      ['15:30', '17:00', 'שקוף שזה שבת'],
      ['17:00', '18:00', 'חמש עם שרון גל'],
      ['18:00', '19:00', 'אזור מלחמה'],
      ['19:00', '20:00', 'המהדורה המרכזית – חדשות 13'],
      ['20:00', '21:45', 'הישרדות VIP'],
      ['21:45', '23:00', 'משחקי השף'],
      ['23:00', '00:30', 'היום שהיה']
    ],
    'kan11': [
      ['06:00', '09:00', 'הבוקר עם כאן'],
      ['09:00', '12:00', 'סדר יום'],
      ['12:00', '14:30', 'חדשות כאן צהריים'],
      ['14:30', '16:00', 'ערב ערב עם דב גילהר'],
      ['16:00', '17:30', 'מהצד השני עם גיא זהר'],
      ['17:30', '19:00', 'שבע אקטואליה'],
      ['19:00', '20:00', 'חדשות הערב – כאן 11'],
      ['20:00', '21:00', 'קופה ראשית – עונה חדשה'],
      ['21:00', '22:00', 'המפקדת'],
      ['22:00', '23:00', 'פגישה עם רוני קובן'],
      ['23:00', '00:30', 'חדשות הלילה']
    ],
    'ch14': [
      ['06:00', '09:00', 'ישראל הבוקר'],
      ['09:00', '12:00', 'עכשיו נדל\"ן וכלכלה'],
      ['12:00', '15:00', 'מהדורת 14 צהריים'],
      ['15:00', '17:00', 'ריקלין וסגל'],
      ['17:00', '19:00', 'שבע עם שרון גל'],
      ['19:00', '20:30', 'המהדורה המרכזית עכשיו 14'],
      ['20:30', '22:00', 'הפטריוטים עם ינון מגל'],
      ['22:00', '23:30', 'הדו\"ח של אראל סג\"ל'],
      ['23:30', '01:00', 'כלכלי בלילה']
    ],
    'kids_yaldut': [
      ['06:00', '08:00', 'שירי בוקר של ילדות ישראלית'],
      ['08:00', '10:00', 'אליעזר והגזר והסיפורים היפים'],
      ['10:00', '12:00', 'דתיה בן דור – מי שטוב לו ושמח'],
      ['12:00', '14:00', 'רינת ויויו שרים שירי נעמי שמר'],
      ['14:00', '16:00', 'כח הקצב וטיף וטף'],
      ['16:00', '18:00', 'שירי חגים ועונות השנה'],
      ['18:00', '20:00', 'פרפר נחמד – פרקים נבחרים'],
      ['20:00', '22:00', 'שיר ערש – לילה טוב לילדים']
    ],
    'kids': [
      ['06:00', '09:00', 'הפיג\\'מות – מרתון בוקר'],
      ['09:00', '12:00', 'גאליס – עונה 3'],
      ['12:00', '15:00', 'בובספוג מכנסמרובע'],
      ['15:00', '18:00', 'ששטוס 2026'],
      ['18:00', '21:00', 'השמיניה – כל הפרקים'],
      ['21:00', '00:00', 'ארתור – הקלאסיקות']
    ],
    'sports': [
      ['06:00', '10:00', 'חדשות הספורט – מהדורת הבוקר'],
      ['10:00', '14:00', 'משחקי העבר הגדולים'],
      ['14:00', '17:00', 'אולפן מקדים – ליגת העל בכדורגל'],
      ['17:00', '20:00', 'משחק השבוע בשידור חי'],
      ['20:00', '22:30', 'אולפן ליגת האלופות'],
      ['22:30', '00:00', 'יציע העיתונות']
    ]
  };

  function getChannelEpg(ch) {
    if (!ch) return null;
    var now = new Date();
    var curMin = now.getHours() * 60 + now.getMinutes();
    
    var cat = 'keshet12';
    var cid = (ch.id || '').toLowerCase();
    var name = (ch.name || '').toLowerCase();
    if (/reshet|13/.test(cid) || /13/.test(name)) cat = 'reshet13';
    else if (/kan11|kan_11|11/.test(cid) || /11/.test(name)) cat = 'kan11';
    else if (/ch14|14/.test(cid) || /14/.test(name)) cat = 'ch14';
    else if (/yaldut|ילדות/.test(cid) || /ילדות/.test(name)) cat = 'kids_yaldut';
    else if (isKidsCh(ch)) cat = 'kids';
    else if (/sport|ספורט|one_/.test(cid) || /ספורט|one/.test(name)) cat = 'sports';

    var list = SHOW_CATALOG[cat] || SHOW_CATALOG.keshet12;
    var toMin = function (t) { var p = t.split(':'); return parseInt(p[0], 10) * 60 + parseInt(p[1], 10); };
    
    var curIdx = 0;
    for (var i = 0; i < list.length; i++) {
      var s = toMin(list[i][0]);
      var e = toMin(list[i][1]);
      if (curMin >= s && curMin < e) { curIdx = i; break; }
    }

    var cur = list[curIdx] || list[0];
    var prev = list[(curIdx - 1 + list.length) % list.length];
    var next = list[(curIdx + 1) % list.length];

    var sMin = toMin(cur[0]), eMin = toMin(cur[1]);
    if (eMin <= sMin) eMin += 1440;
    var prog = Math.max(0, Math.min(100, Math.round(((curMin - sMin) / (eMin - sMin)) * 100)));

    return {
      prev: { start: prev[0], end: prev[1], title: prev[2] },
      current: { start: cur[0], end: cur[1], title: cur[2], progress: prog },
      next: { start: next[0], end: next[1], title: next[2] }
    };
  }

  function renderTvCatchUpBar(ch) {
    var bar = $('tv-epg-bar');
    if (!bar || !ch) return;
    var epg = getChannelEpg(ch);
    var now = new Date();
    var timeStr = (now.getHours() < 10 ? '0' : '') + now.getHours() + ':' + (now.getMinutes() < 10 ? '0' : '') + now.getMinutes();
    
    // Bottom left clock
    var elClock = $('tvepg-clock');
    if (elClock) elClock.textContent = timeStr;

    // Previous show
    var pTitle = $('tvepg-prev-title');
    var pTime = $('tvepg-prev-time');
    if (pTitle && epg) pTitle.textContent = epg.prev.title;
    if (pTime && epg) pTime.textContent = epg.prev.start + ' - ' + epg.prev.end;

    // Current show
    var cLogo = $('tvepg-cur-logo');
    var cTitle = $('tvepg-cur-title');
    var cSub = $('tvepg-cur-sub');
    var tStart = $('tvepg-time-start');
    var tEnd = $('tvepg-time-end');
    var tFill = $('tvepg-timeline-fill');
    if (cLogo) cLogo.src = ch.logo || 'icon.png';
    if (cTitle && epg) cTitle.textContent = epg.current.title;
    if (cSub) cSub.textContent = (ch.number ? ch.number + '. ' : '') + ch.name;
    if (tStart && epg) tStart.textContent = epg.current.start;
    if (tEnd && epg) tEnd.textContent = epg.current.end;
    if (tFill && epg) tFill.style.width = epg.current.progress + '%';

    // Next show
    var nTitle = $('tvepg-next-title');
    var nTime = $('tvepg-next-time');
    if (nTitle && epg) nTitle.textContent = epg.next.title;
    if (nTime && epg) nTime.textContent = epg.next.start + ' - ' + epg.next.end;

    // Right channel strip: prev, cur, next
    var curIdx = indexInList(ch.id);
    if (curIdx === -1 && state.list) curIdx = 0;
    var prevCh = state.list[(curIdx - 1 + state.list.length) % state.list.length];
    var nextCh = state.list[(curIdx + 1) % state.list.length];

    if ($('tvepg-prev-ch-name') && prevCh) $('tvepg-prev-ch-name').textContent = prevCh.name;
    if ($('tvepg-prev-ch-num') && prevCh) $('tvepg-prev-ch-num').textContent = prevCh.number || '';
    if ($('tvepg-cur-ch-name')) $('tvepg-cur-ch-name').textContent = ch.name;
    if ($('tvepg-cur-ch-num')) $('tvepg-cur-ch-num').textContent = ch.number || '';
    if ($('tvepg-strip-logo')) $('tvepg-strip-logo').src = ch.logo || 'icon.png';
    if ($('tvepg-next-ch-name') && nextCh) $('tvepg-next-ch-name').textContent = nextCh.name;
    if ($('tvepg-next-ch-num') && nextCh) $('tvepg-next-ch-num').textContent = nextCh.number || '';

    markTvEpgFocus();
  }

  function markTvEpgFocus() {
    var p = $('tvepg-card-prev');
    var c = $('tvepg-card-cur');
    var n = $('tvepg-card-next');
    var opt = $('tvepg-btn-options');
    [p, c, n, opt].forEach(function (el) { if (el) el.classList.remove('focused'); });
    if (tvEpgActiveIdx === 0 && p) p.classList.add('focused');
    else if (tvEpgActiveIdx === 1 && c) c.classList.add('focused');
    else if (tvEpgActiveIdx === 2 && n) n.classList.add('focused');
    else if (tvEpgActiveIdx === 3 && opt) opt.classList.add('focused');
  }

  function showTvCatchUpBar(ch) {
    if (!TVUI) return;
    var bar = $('tv-epg-bar');
    if (!bar) return;
    ch = ch || state.playing;
    if (!ch) return;
    renderTvCatchUpBar(ch);
    bar.classList.remove('hidden');
    state.tvEpgOpen = true;

    if (tvEpgTimer) clearTimeout(tvEpgTimer);
    tvEpgTimer = setTimeout(function () {
      hideTvCatchUpBar();
    }, 7000);
  }

  function hideTvCatchUpBar() {
    var bar = $('tv-epg-bar');
    if (bar) bar.classList.add('hidden');
    state.tvEpgOpen = false;
    if (tvEpgTimer) { clearTimeout(tvEpgTimer); tvEpgTimer = null; }
  }

  function tvEpgKey(code) {
    if (tvEpgTimer) {
      clearTimeout(tvEpgTimer);
      tvEpgTimer = setTimeout(function () { hideTvCatchUpBar(); }, 7000);
    }

    if (isBack(code)) {
      hideTvCatchUpBar();
      return true;
    }

    // Right arrow (39) / Left arrow (37) in RTL:
    // 0: Prev (Catchup, right side), 1: Center, 2: Next (left side), 3: Options
    if (code === 37) { // Left
      if (tvEpgActiveIdx === 0) tvEpgActiveIdx = 1;
      else if (tvEpgActiveIdx === 1) tvEpgActiveIdx = 2;
      else if (tvEpgActiveIdx === 3) tvEpgActiveIdx = 2;
      markTvEpgFocus();
      return true;
    } else if (code === 39) { // Right
      if (tvEpgActiveIdx === 2) tvEpgActiveIdx = 1;
      else if (tvEpgActiveIdx === 1) tvEpgActiveIdx = 0;
      else if (tvEpgActiveIdx === 3) tvEpgActiveIdx = 0;
      markTvEpgFocus();
      return true;
    } else if (code === 40) { // Down
      if (tvEpgActiveIdx !== 3) {
        tvEpgActiveIdx = 3;
        markTvEpgFocus();
      } else {
        zap(S.upNext ? -1 : 1);
        setTimeout(function () { renderTvCatchUpBar(state.playing); }, 150);
      }
      return true;
    } else if (code === 38) { // Up
      if (tvEpgActiveIdx === 3) {
        tvEpgActiveIdx = 1;
        markTvEpgFocus();
      } else {
        zap(S.upNext ? 1 : -1);
        setTimeout(function () { renderTvCatchUpBar(state.playing); }, 150);
      }
      return true;
    }

    return false;
  }

  function tvEpgOk() {
    if (tvEpgActiveIdx === 0) {
      // Catch-up / Restart from beginning
      var epg = getChannelEpg(state.playing);
      var title = epg ? epg.prev.title : 'התוכנית הקודמת';
      toast('↩️ מפעיל Catch-Up: צפייה מההתחלה ב-' + title);
      var v = el.video;
      if (v && v.currentTime > 0) {
        try { v.currentTime = 0; } catch (e) {}
      }
      hideTvCatchUpBar();
    } else if (tvEpgActiveIdx === 3) {
      hideTvCatchUpBar();
      playerMenu();
    } else {
      hideTvCatchUpBar();
    }
  }
"""

if "function showTvCatchUpBar" not in text:
    idx = text.find("function playerKey")
    text = text[:idx] + epg_code + "\n  " + text[idx:]
    print("Added TV EPG engine before playerKey")

# Hook into playerKey
old_player_key = "function playerKey(code) {"
new_player_key = """function playerKey(code) {
    if (TVUI) {
      if (state.tvEpgOpen) {
        if (tvEpgKey(code)) return;
      } else {
        if (code === 13 || code === 38 || code === 40 || code === 37 || code === 39) {
          showTvCatchUpBar(state.playing);
          return;
        }
      }
    }"""

if old_player_key in text and "state.tvEpgOpen" not in text:
    text = text.replace(old_player_key, new_player_key)
    print("Hooked TV EPG into playerKey")

# Hook into shortOk
old_short_ok = "function shortOk() {\n    switch (state.overlay) {"
new_short_ok = """function shortOk() {
    if (TVUI && state.overlay === 'player' && state.tvEpgOpen) {
      tvEpgOk();
      return;
    }
    switch (state.overlay) {"""

if old_short_ok in text and "tvEpgOk()" not in text:
    text = text.replace(old_short_ok, new_short_ok)
    print("Hooked tvEpgOk into shortOk")

with open(app_path, 'w', encoding='utf-8') as f:
    f.write(text)
print("app.js updated with TV EPG Catch-Up Feature!")
