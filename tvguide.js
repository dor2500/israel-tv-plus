// TV / computer player OSD and the programme guide grid. Phones keep their own player (app.js).
// Schedules come from epg.json (tools/make_epg.py). app.js creates this once with the few things it shares.
window.ITVGuide = function (api) {
  'use strict';
  var REMOTE_EPG = 'https://dor2500.github.io/israel-tv-plus/epg.json';
  var DAYS = ['ראשון', 'שני', 'שלישי', 'רביעי', 'חמישי', 'שישי', 'שבת'];
  var MONTHS = ['בינואר', 'בפברואר', 'במרץ', 'באפריל', 'במאי', 'ביוני', 'ביולי', 'באוגוסט', 'בספטמבר', 'באוקטובר', 'בנובמבר', 'בדצמבר'];
  var SCALE = 0.8;   // rem per minute in the grid
  var ROWS = 5;
  var EDGE_MIN = 25; // a programme starting this close to the far edge scrolls the grid
  var MAX_OFFSET = 8;

  var epg = {}, epgDone = false;
  var osd = { zone: 'big', offset: 0, browse: null };
  var g = { rows: [], row: 0, i: 0, zone: 'grid', anchor: 0, win: 0, top: 0 };
  var o = {}, gui = {}, lastX = -1, lastY = -1;

  /* ---------- schedule ---------- */
  function loadEpg() {
    var urls = /^https?:/.test(location.protocol) ? ['epg.json', REMOTE_EPG] : [REMOTE_EPG];
    function use(data) {
      epg = data.channels || {};
      epgDone = true;
      renderOsd();
      if (api.state.overlay === 'tvguide') openGuide();
    }
    (function next(n) {
      if (n >= urls.length) {
        // the snapshot the webOS package carries (tools/build_ipk.py): a script, since XHR can't read local files
        if (window.ITV_EPG) { use(window.ITV_EPG); return; }
        var s = document.createElement('script');
        s.src = 'epg-snapshot.js';
        s.onload = function () { if (window.ITV_EPG) use(window.ITV_EPG); };
        s.onerror = function () { epgDone = true; if (api.state.overlay === 'tvguide') renderGuide(); };
        document.body.appendChild(s);
        return;
      }
      var x = new XMLHttpRequest();
      x.open('GET', urls[n] + '?t=' + Math.floor(Date.now() / 3600000));
      x.onload = function () {
        try {
          if (x.status !== 200) throw 0;
          use(JSON.parse(x.responseText));
        } catch (e) { next(n + 1); }
      };
      x.onerror = function () { next(n + 1); };
      x.send();
    })(0);
  }
  function now() { return Date.now() / 60000; }
  function progs(ch) { return (ch && epg[ch.id]) || []; }
  function startOf(p) { return p[0]; }
  function endOf(p) { return p[0] + p[1]; }
  // Past programmes that can still be played: fully inside the channel's catch-up window, a few minutes' margin
  function canCatchup(ch, p) {
    var h = api.catchupHours(ch), m = now();
    return !!h && endOf(p) <= m && startOf(p) >= m - h * 60 + 5;
  }
  // The earliest programme remote focus may reach on this channel
  function firstIndex(ch) {
    var air = airingIndex(ch), list = progs(ch), i = air;
    if (air === -1) return -1;
    while (i > 0 && canCatchup(ch, list[i - 1])) i--;
    return i;
  }
  function airingIndex(ch) {
    var list = progs(ch), m = now();
    for (var i = 0; i < list.length; i++) if (m < endOf(list[i])) return i;
    return -1;
  }

  /* ---------- small helpers ---------- */
  function esc(s) { return String(s == null ? '' : s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/"/g, '&quot;'); }
  function pad(n) { return (n < 10 ? '0' : '') + n; }
  function clock(m) { var d = new Date(m * 60000); return pad(d.getHours()) + ':' + pad(d.getMinutes()); }
  function clockSec(m) { var d = new Date(m * 60000); return '<bdi class="tv-t">' + pad(d.getHours()) + ':' + pad(d.getMinutes()) + ':' + pad(d.getSeconds()) + '</bdi>'; }
  function t(m) { return '<bdi class="tv-t">' + clock(m) + '</bdi>'; }
  function range(p) { return '<bdi class="tv-t">' + clock(startOf(p)) + ' – ' + clock(endOf(p)) + '</bdi>'; }
  function dayLabel(m) {
    var d = new Date(m * 60000), today = new Date();
    return d.toDateString() === today.toDateString() ? 'היום' : 'יום ' + DAYS[d.getDay()];
  }
  function logo(ch) {
    return '<img class="tv-logo" alt="" src="' + esc(api.logoUrl(ch)) + '" onerror="this.style.visibility=\'hidden\'">';
  }
  // A programme's own picture when the feed has one, otherwise the channel logo on a dark card.
  function poster(ch, p) {
    var base = '<div class="tv-poster-logo">' + logo(ch) + '</div>';
    return p && p[4] ? base + '<img class="tv-poster" referrerpolicy="no-referrer" alt="" src="' + esc(p[4]) + '" onerror="this.style.display=\'none\'">' : base;
  }
  function zapList() {
    var s = api.state, list = api.S.zap === 'all' ? s.all : s.list;
    return list.filter(function (c) { return api.playable(c); });
  }
  function neighbour(ch, dir) {
    var list = zapList(), cur = 0;
    for (var i = 0; i < list.length; i++) if (list[i].id === ch.id) cur = i;
    return list.length ? list[(cur + dir + list.length) % list.length] : ch;
  }

  /* ---------- player OSD ---------- */
  function buildOsd() {
    var box = document.createElement('div');
    box.id = 'tvosd';
    box.innerHTML =
      '<div id="tvo-strip"></div>' +
      '<div id="tvo-peek" data-tv="peek"><i class="tv-chev right"></i><div class="tv-peek-txt"></div></div>' +
      '<div id="tvo-main"><div id="tvo-big" class="tv-card" data-tv="big"></div>' +
        '<div id="tvo-bar"><i></i><u></u></div><div class="tv-timing" id="tvo-timing"></div>' +
        '<div id="tvo-opts-wrap"><span id="tvo-live" data-tv="live">לשידור החי</span><span id="tvo-opts" data-tv="opts">אפשרויות</span></div></div>' +
      '<div id="tvo-next"><div id="tvo-small" class="tv-card" data-tv="small"></div><div class="tv-timing" id="tvo-next-time"></div></div>' +
      '<div id="tvo-go" data-tv="small"><i class="tv-chev left"></i></div>';
    api.osdEl.appendChild(box);
    var brand = document.createElement('div');
    brand.className = 'tv-brand';
    brand.innerHTML = '<div class="tv-clock"></div>';
    api.osdEl.appendChild(brand);
    ['strip', 'peek', 'big', 'bar', 'timing', 'opts', 'live', 'next', 'small', 'go'].forEach(function (k) { o[k] = document.getElementById('tvo-' + (k === 'timing' ? 'timing' : k)); });
    o.nextTime = document.getElementById('tvo-next-time');
    box.addEventListener('click', function (e) {
      e.stopPropagation();
      var n = e.target.closest('[data-tv]');
      if (!n) return;
      var v = n.getAttribute('data-tv');
      if (v === 'big') { osd.zone = 'big'; ok(); }
      else if (v === 'opts') { osd.zone = 'opts'; ok(); }
      else if (v === 'live') api.goLive();
      else if (v === 'small') moveOffset(1);
      else if (v === 'peek') moveOffset(-1);
      else if (v.indexOf('ch:') === 0) api.play(chById(v.slice(3)));
      api.showOsd();
    });
  }
  function chById(id) {
    var all = api.state.all;
    for (var i = 0; i < all.length; i++) if (all[i].id === id) return all[i];
    return null;
  }
  function browsed() { return osd.browse || api.state.playing; }
  function stripItem(ch, cls) {
    return '<div class="tv-strip-item ' + cls + '" data-tv="ch:' + esc(ch.id) + '"><span class="tv-num">' + ch.number + '</span>' +
      '<div class="tv-who"><div class="tv-strip-logo">' + logo(ch) + '</div><div class="tv-nm">' + esc(ch.name) + '</div></div></div>';
  }
  function renderOsd(flip) {
    var ch = browsed();
    if (!ch || !o.big) return;
    box().classList.toggle('catchup', api.inCatchup());
    if (api.inCatchup()) { renderCatchup(ch, flip); return; }
    var list = progs(ch), air = airingIndex(ch), has = air !== -1;
    var idx = air + osd.offset;
    var p = has ? list[idx] : null, next = has ? list[idx + 1] : null, prev = has ? list[idx - 1] : null;
    var upDir = api.S.upNext ? 1 : -1;

    var sf = osd.zone === 'strip';
    o.strip.className = sf ? 'on' : '';
    o.strip.innerHTML = stripItem(neighbour(ch, upDir), '') + '<div class="tv-arrow">▲</div>' +
      stripItem(ch, 'mid' + (sf ? ' focus' : '')) + '<div class="tv-arrow">▼</div>' + stripItem(neighbour(ch, -upDir), '');

    o.peek.className = prev && (osd.offset > -1 || canCatchup(ch, prev)) ? '' : 'none';
    if (prev) o.peek.querySelector('.tv-peek-txt').innerHTML = '<div>לתכנית הקודמת</div><small class="tv-clip">' + esc(prev[2]) + '</small><small>' + range(prev) + '</small>';

    var airingNow = p && startOf(p) <= now();
    var label = !p ? 'משודר עכשיו' : osd.offset < 0 ? (canCatchup(ch, p) ? 'OK לצפייה חוזרת' : 'שודר קודם') : osd.offset === 0 ? (airingNow ? 'משודר עכשיו' : 'בקרוב') : osd.offset === 1 ? 'הבאה' : 'בהמשך';
    o.big.innerHTML = poster(ch, p) + '<div class="tv-cap"><em>' + label + '</em><b class="tv-clip">' + esc(p ? p[2] : ch.name) + '</b>' +
      '<span class="tv-clip">' + esc(p ? ch.name : 'אין מידע על התכנית') + '</span></div>';
    o.big.classList.toggle('focus', osd.zone === 'big');
    o.big.classList.remove('flip');
    if (flip) { void o.big.offsetWidth; o.big.classList.add('flip'); }

    var live = p && osd.offset === 0 && airingNow;
    o.bar.className = live ? '' : 'none';
    o.timing.innerHTML = !p ? '' : live
      ? '<span>' + t(startOf(p)) + '</span><span class="tv-day">' + dayLabel(startOf(p)) + '</span><span>' + t(endOf(p)) + '</span>'
      : '<span class="tv-mid">' + range(p) + '</span>';
    progress();

    o.opts.classList.toggle('focus', osd.zone === 'opts');
    o.live.classList.remove('focus');
    o.next.style.visibility = next ? '' : 'hidden';
    o.go.className = next && osd.offset < MAX_OFFSET ? '' : 'none';
    if (next) {
      o.small.innerHTML = poster(ch, next) + '<div class="tv-cap"><em>' + (osd.offset === -1 ? 'משודר עכשיו' : osd.offset === 0 ? 'הבאה' : 'בהמשך') + '</em><b class="tv-clip">' + esc(next[2]) + '</b></div>';
      o.nextTime.innerHTML = '<span class="tv-mid">' + range(next) + '</span>';
    }
  }
  function box() { return api.osdEl.querySelector('#tvosd'); }
  function catchupProg(ch) {
    var list = progs(ch), pos = api.catchupPos();
    for (var i = 0; i < list.length; i++) if (pos < endOf(list[i])) return list[i];
    return null;
  }
  // Watching the past: the card shows the programme at the playhead and the bar is the position inside it
  function renderCatchup(ch, flip) {
    var p = catchupProg(ch), upDir = api.S.upNext ? 1 : -1;
    osd.cuProg = p;
    o.strip.className = '';
    o.strip.innerHTML = stripItem(neighbour(ch, upDir), '') + '<div class="tv-arrow">▲</div>' + stripItem(ch, 'mid') + '<div class="tv-arrow">▼</div>' + stripItem(neighbour(ch, -upDir), '');
    o.peek.className = 'none';
    o.go.className = 'none';
    o.next.style.visibility = 'hidden';
    o.big.innerHTML = poster(ch, p) + '<div class="tv-cap"><em>צפייה חוזרת · ◄ ► 30 שניות</em><b class="tv-clip">' + esc(p ? p[2] : ch.name) + '</b><span class="tv-clip">' + esc(ch.name) + '</span></div>';
    o.big.classList.toggle('focus', osd.zone === 'big');
    o.big.classList.remove('flip');
    if (flip) { void o.big.offsetWidth; o.big.classList.add('flip'); }
    o.bar.className = p ? '' : 'none';
    o.opts.classList.toggle('focus', osd.zone === 'opts');
    o.live.classList.toggle('focus', osd.zone === 'live');
    progress();
  }
  function progress() {
    if (api.inCatchup()) {
      var cp = o.big && catchupProg(api.state.playing), pos = api.catchupPos();
      if (cp !== osd.cuProg) { renderOsd(); return; }
      if (!cp) return;
      var cpct = Math.max(0, Math.min(100, (pos - startOf(cp)) / cp[1] * 100));
      o.bar.firstChild.style.width = cpct + '%';
      o.bar.lastChild.style.right = cpct + '%';
      o.timing.innerHTML = '<span>' + t(startOf(cp)) + '</span><span class="tv-day">' + clockSec(pos) + '</span><span>' + t(endOf(cp)) + '</span>';
      return;
    }
    var ch = browsed(), i = airingIndex(ch);
    if (i === -1 || !o.bar) return;
    var p = progs(ch)[i], pct = Math.max(0, Math.min(100, (now() - startOf(p)) / p[1] * 100));
    o.bar.firstChild.style.width = pct + '%';
    o.bar.lastChild.style.right = pct + '%';
  }
  function moveOffset(d) {
    var ch = browsed(), air = airingIndex(ch), list = progs(ch), n = osd.offset + d;
    var floor = Math.min(-1, firstIndex(ch) - air);
    if (air === -1 || n < floor || n > MAX_OFFSET || air + n < 0 || air + n >= list.length) return false;
    osd.offset = n; osd.zone = 'big';
    renderOsd(true);
    return true;
  }
  function resetOsd() { osd.zone = 'big'; osd.offset = 0; osd.browse = null; }
  function ok() {
    var ch = browsed();
    if (osd.zone === 'opts') { api.playerMenu(); return; }
    if (osd.zone === 'live') { api.goLive(); return; }
    if (api.inCatchup()) { api.togglePlay(); return; }
    if (osd.zone === 'strip') { var c = osd.browse; resetOsd(); if (c && c.id !== api.state.playing.id) api.play(c); else renderOsd(); return; }
    var p = progs(ch)[airingIndex(ch) + osd.offset];
    if (osd.offset < 0 && p && canCatchup(ch, p)) { var cs = startOf(p), ce = endOf(p); resetOsd(); api.playCatchup(ch, cs, ce); return; }
    if (osd.offset > 0 && p) api.toast(p[2] + ' · ' + dayLabel(startOf(p)) + ' ב-' + clock(startOf(p)));
    else if (osd.offset === 0) api.hideOsd();
  }
  // Returns true when the key was used here; app.js handles the rest (zapping with the OSD closed).
  function playerKey(code, osdWasOff) {
    if (api.inCatchup()) {
      if (api.isBack(code)) { api.goLive(); return true; }
      if (osdWasOff) return code === 37 || code === 39;
      if (osd.zone === 'big') {
        if (code === 37 || code === 39) { api.catchupSeek(code === 37 ? 30 : -30); progress(); }
        else if (code === 40) { osd.zone = 'live'; renderOsd(); }
        else return false;                   // Up zaps, which goes back to live
        return true;
      }
      if (code === 38) osd.zone = 'big';
      else if (code === 37) osd.zone = 'opts';
      else if (code === 39) osd.zone = 'live';
      renderOsd();
      return true;
    }
    if (osdWasOff) return code === 37 || code === 39;
    if (api.isBack(code)) {
      if (osd.browse || osd.offset || osd.zone !== 'big') { resetOsd(); renderOsd(true); return true; }
      return false;
    }
    if (osd.zone === 'big') {
      if (code === 37) moveOffset(1);        // later: time runs right to left
      else if (code === 39) { if (!moveOffset(-1)) { osd.zone = 'strip'; renderOsd(); } }
      else if (code === 40) { osd.zone = 'opts'; renderOsd(); }
      else return false;                     // Up keeps zapping
      return true;
    }
    if (osd.zone === 'opts') {
      if (code === 38) { osd.zone = 'big'; renderOsd(); return true; }
      if (code === 40) return false;          // Down from options zaps channel down!
      return true;
    }
    var up = api.S.upNext ? 1 : -1;
    if (code === 38 || code === 40) { osd.browse = neighbour(browsed(), code === 38 ? up : -up); osd.offset = 0; renderOsd(true); }
    else if (code === 37) { osd.zone = 'big'; renderOsd(); }
    return true;
  }

  /* ---------- guide ---------- */
  function buildGuide() {
    var box = document.createElement('section');
    box.id = 'tvguide';
    box.className = 'hidden';
    box.innerHTML =
      '<div id="tvg-head"><div id="tvg-title"><b>לוח שידורים</b><span id="tvg-day"></span></div><div id="tvg-ruler"></div></div>' +
      '<div id="tvg-body"><div id="tvg-rail"></div><div id="tvg-grid"></div></div>' +
      '<div id="tvg-empty" class="hidden">טוען את לוח השידורים…</div>' +
      '<div id="tvg-detail"><div id="tvg-thumb"></div><div id="tvg-txt"><div id="tvg-name" class="tv-clip"></div><div id="tvg-time"></div><div id="tvg-desc"></div><div id="tvg-cta"></div></div></div>' +
      '<div class="tv-brand"><div class="tv-clock"></div></div>';
    document.getElementById('root').appendChild(box);
    gui.box = box;
    ['ruler', 'rail', 'grid', 'empty', 'detail', 'thumb', 'name', 'time', 'desc', 'cta', 'day'].forEach(function (k) { gui[k] = document.getElementById('tvg-' + k); });
    box.addEventListener('click', function (e) {
      var h = e.target.closest('[data-heart]'), c = e.target.closest('[data-p]');
      if (h) { api.toggleFav(chById(h.getAttribute('data-heart'))); renderGuide(); }
      else if (c) { pick(c); activate(); }
    });
    box.addEventListener('mousemove', function (e) {
      // a redraw under a still pointer fires mousemove too; only a real move may take the focus
      if (e.clientX === lastX && e.clientY === lastY) return;
      lastX = e.clientX; lastY = e.clientY;
      var c = e.target.closest('[data-p]');
      if (c && !c.classList.contains('focus') && !c.classList.contains('past')) { pick(c); renderGuide(); }
    });
    box.addEventListener('wheel', function (e) {
      e.preventDefault();
      guideKey(e.deltaY > 0 ? (e.shiftKey ? 37 : 40) : (e.shiftKey ? 39 : 38));
    }, { passive: false });
  }
  function pick(cell) {
    var a = cell.getAttribute('data-p').split(':'), r = +a[0], i = +a[1], list = progs(g.rows[r]);
    if (endOf(list[i]) <= now() && !canCatchup(g.rows[r], list[i])) return;
    g.row = r; g.i = i; g.zone = 'grid'; g.anchor = Math.max(startOf(list[i]), now());
  }
  function floor30(m) { return Math.floor(m / 30) * 30; }
  function indexAt(ch, m) {
    var list = progs(ch), first = Math.max(0, firstIndex(ch));
    for (var i = first; i < list.length; i++) if (m < endOf(list[i])) return i;
    return list.length - 1;
  }
  function openGuide() {
    var s = api.state, m = now();
    g.rows = s.all.filter(function (c) { return airingIndex(c) !== -1 && api.playable(c); });
    g.rows.sort(function (a, b) { return a.number - b.number; });
    g.row = 0;
    if (s.playing) for (var r = 0; r < g.rows.length; r++) if (g.rows[r].id === s.playing.id) g.row = r;
    g.top = Math.max(0, Math.min(g.row, g.rows.length - ROWS));
    g.zone = 'grid'; g.anchor = m; g.win = floor30(m);
    if (s.overlay !== 'tvguide') { s.stack.push(s.overlay); s.overlay = 'tvguide'; }
    gui.box.classList.remove('hidden');
    document.body.classList.add('tvguide-open');
    if (g.rows.length) g.i = indexAt(g.rows[g.row], m);
    renderGuide();
  }
  function closeGuide() {
    var s = api.state;
    gui.box.classList.add('hidden');
    document.body.classList.remove('tvguide-open');
    s.overlay = s.stack.pop() || null;
    if (s.overlay === 'player') { resetOsd(); renderOsd(); api.showOsd(); }
    else api.markFocus();
  }
  function renderGuide() {
    var empty = !g.rows.length;
    gui.empty.classList.toggle('hidden', !empty);
    gui.empty.textContent = epgDone ? 'לוח השידורים לא זמין כרגע' : 'טוען את לוח השידורים…';
    gui.detail.style.visibility = empty ? 'hidden' : '';
    var fs = parseFloat(getComputedStyle(document.documentElement).fontSize);
    var width = gui.grid.offsetWidth / fs, winLen = width / SCALE, m = now();
    gui.day.textContent = 'יום ' + DAYS[new Date(g.win * 60000).getDay()];
    if (empty) { gui.rail.innerHTML = gui.grid.innerHTML = gui.ruler.innerHTML = ''; return; }

    var ch = g.rows[g.row], list = progs(ch), p = list[g.i];
    if (g.row < g.top) g.top = g.row;
    if (g.row >= g.top + ROWS) g.top = g.row - ROWS + 1;
    if (startOf(p) < g.win) g.win = floor30(startOf(p));
    else if (endOf(p) <= m && startOf(p) < g.win + 1) g.win = floor30(startOf(p));
    else if (startOf(p) >= g.win + winLen - EDGE_MIN) g.win = floor30(startOf(p)) - 30;
    var a = g.win, b = g.win + winLen, x = function (v) { return (v - a) * SCALE; };

    // Everything is cut to the visible window, so titles stay readable and ruler labels never get clipped.
    var ruler = '';
    for (var v = floor30(a); v <= b; v += 15) {
      if (v < a) continue;
      ruler += '<i class="tv-tick' + (v % 30 === 0 ? ' major' : '') + '" style="right:' + x(v) + 'rem"></i>';
      if (v % 30 === 0 && v > a + 3 && v < b - 3) ruler += '<span class="tv-tlabel" style="right:' + x(v) + 'rem">' + clock(v) + '</span>';
    }
    var nowIn = m >= a && m < b;
    if (nowIn && m > a + 3 && m < b - 3) ruler += '<div class="tv-nowflag" style="right:' + x(m) + 'rem">' + clock(m) + '</div>';
    gui.ruler.innerHTML = ruler;

    var rail = '', rows = '';
    for (var r = g.top; r < Math.min(g.rows.length, g.top + ROWS); r++) {
      var c = g.rows[r], fav = api.isFav(c), rowTop = (r - g.top) * 7.6;
      rail += '<div class="tv-rail-item"><span class="tv-heart' + (fav ? ' on' : '') + (g.zone === 'rail' && r === g.row ? ' focus' : '') + '" data-heart="' + esc(c.id) + '">' +
        '<svg viewBox="0 0 24 24"><path d="M12 21s-8.500-5.200-8.500-11.200A4.800 4.800 0 0 1 12 6.600a4.800 4.800 0 0 1 8.500 3.200C20.500 15.800 12 21 12 21z"/></svg></span>' +
        '<span class="tv-num">' + c.number + '</span><div class="tv-rail-logo">' + logo(c) + '</div><div class="tv-nm">' + esc(c.name) + '</div></div>';
      var pl = progs(c);
      for (var i = 0; i < pl.length; i++) {
        var q = pl[i], s = startOf(q), e = endOf(q);
        if (e <= a || s >= b) continue;
        var vs = Math.max(s, a), w = (Math.min(e, b) - vs) * SCALE;
        var cls = (e <= m ? (canCatchup(c, q) ? ' cu' : ' past') : '') + (s <= m && m < e ? ' air' : '') + (g.zone === 'grid' && r === g.row && i === g.i ? ' focus' : '');
        rows += '<div class="tv-p' + cls + '" data-p="' + r + ':' + i + '" style="top:' + rowTop + 'rem;right:' + x(vs) + 'rem;width:' + w + 'rem"><span class="tv-ttl">' + (w < 3.5 ? '' : esc(q[2])) + '</span></div>';
      }
    }
    gui.rail.innerHTML = rail;
    gui.grid.innerHTML = rows + (nowIn ? '<div class="tv-nowline" style="right:' + x(m) + 'rem"></div>' : '');

    var airing = startOf(p) <= m && m < endOf(p), d = new Date(startOf(p) * 60000);
    gui.thumb.innerHTML = poster(ch, p);
    gui.name.textContent = p[2];
    gui.time.innerHTML = esc(ch.name) + ' · ' + d.getDate() + ' ' + MONTHS[d.getMonth()] + ' · ' + range(p);
    gui.desc.textContent = p[3];
    gui.cta.textContent = g.zone === 'rail' ? (api.isFav(ch) ? 'OK להסרה מהמועדפים' : 'OK להוספה למועדפים') : airing ? 'OK לצפייה עכשיו' : canCatchup(ch, p) ? 'OK לצפייה חוזרת' : '';
  }
  function activate() {
    var ch = g.rows[g.row], p = progs(ch)[g.i];
    if (!p) return;
    var m = now();
    if (canCatchup(ch, p)) { closeGuide(); resetOsd(); api.playCatchup(ch, startOf(p), endOf(p)); return; }
    if (startOf(p) <= m && m < endOf(p)) {
      if (api.catchupHours && api.catchupHours(ch) && (m - startOf(p) > 2)) {
        closeGuide(); resetOsd();
        api.playCatchup(ch, startOf(p), endOf(p));
        api.toast('⏮️ מתחיל את התכנית מחדש: ' + p[2]);
        return;
      }
      closeGuide(); resetOsd(); api.play(ch);
    }
    else api.toast(p[2] + ' · ' + dayLabel(startOf(p)) + ' ב-' + clock(startOf(p)));
  }
  function guideKey(code) {
    if (api.isBack(code)) { closeGuide(); return; }
    if (!g.rows.length) return;
    var ch = g.rows[g.row], list = progs(ch);
    if (code === 38 || code === 40) {
      var r = g.row + (code === 38 ? -1 : 1);
      if (r < 0 || r >= g.rows.length) return;
      g.row = r;
      g.i = indexAt(g.rows[r], g.anchor);  // the anchor stops focus drifting on repeated Up/Down through short shows
    } else if (g.zone === 'rail') {
      if (code === 37) { g.zone = 'grid'; g.anchor = Math.max(now(), startOf(list[g.i])); }
      else if (code === 13) api.toggleFav(ch);
    } else if (code === 37) {
      if (g.i < list.length - 1) { g.i++; g.anchor = startOf(list[g.i]); }
    } else if (code === 39) {
      if (g.i > firstIndex(ch)) { g.i--; g.anchor = endOf(list[g.i]) <= now() ? startOf(list[g.i]) : Math.max(startOf(list[g.i]), now()); }
      else g.zone = 'rail';
    } else if (code === 13) { activate(); return; }
    else return;
    renderGuide();
  }
  function page(dir) {
    var r = Math.max(0, Math.min(g.rows.length - 1, g.row + dir * ROWS));
    if (!g.rows.length || r === g.row) return;
    g.row = r; g.i = indexAt(g.rows[r], g.anchor); renderGuide();
  }

  /* ---------- clock ---------- */
  function tick() {
    var txt = clock(now()), els = document.querySelectorAll('.tv-clock');
    for (var i = 0; i < els.length; i++) if (els[i].textContent !== txt) els[i].textContent = txt;
    if (api.state.overlay === 'player') {
      progress();
      var ch = browsed(), air = ch ? airingIndex(ch) : -1;
      if (air !== tick.air) { tick.air = air; renderOsd(); } // the show just changed
    } else if (api.state.overlay === 'tvguide' && txt !== tick.min) { renderGuide(); }
    tick.min = txt;
  }

  buildOsd();
  buildGuide();
  loadEpg();
  setInterval(loadEpg, 6 * 3600000);
  tick();
  setInterval(tick, 5000);
  setInterval(function () { if (api.inCatchup() && api.state.overlay === 'player') progress(); }, 1000);

  return {
    fill: function () { resetOsd(); renderOsd(true); },
    onHide: function () { resetOsd(); renderOsd(); },
    ok: ok,
    playerKey: playerKey,
    open: openGuide,
    key: guideKey,
    page: page
  };
};
