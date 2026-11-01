import sys

with open("app.js", "r", encoding="utf-8") as f:
    code = f.read()

old_catchup = """  var CATCHUP = {
    reshet13: { hours: 24, url: 'https://d198ztbnlup2iq.cloudfront.net/out/v1/2d9050c90fb94df8b78d1d98306a1a65/index.m3u8' },
    kan11: { hours: 6, url: 'https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8?dvr=21600000' },
    kan_hinuchit: { hours: 6, url: 'https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan_edu/live.livx/playlist.m3u8?dvr=21600000' },
    i24news_he: { hours: 3, url: 'https://i24newshebrew-cdn.encoders.immergo.tv/master.m3u8' },
    i24news_en: { hours: 3, url: 'https://i24newsenglish-cdn.encoders.immergo.tv/2/streamPlaylist.m3u8' },
    i24news_fr: { hours: 3, url: 'https://i24newsfrench-cdn.encoders.immergo.tv/master.m3u8' }
  };"""

new_catchup = """  var CATCHUP = {
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
    i24news_ar: { hours: 3, url: 'https://i24newsarabic-cdn.encoders.immergo.tv/master.m3u8' },
    i24news_arabic_hd: { hours: 3, url: 'https://i24newsarabic-cdn.encoders.immergo.tv/master.m3u8' },
    i24news_fr: { hours: 3, url: 'https://i24newsfrench-cdn.encoders.immergo.tv/master.m3u8' },
    i24news_fr_il: { hours: 3, url: 'https://i24newsfrench-cdn.encoders.immergo.tv/master.m3u8' }
  };

  function openCatchupModal(ch) {
    if (!ch) ch = state.playing;
    if (!ch) return;
    var c = CATCHUP[ch.id] || CATCHUP[(ch.id || '').toLowerCase()];
    var nowMin = Date.now() / 60000;

    function renderModal(arr) {
      if (!arr || !arr.length) {
        openDialog({
          ico: '⏮️',
          title: 'קאץ\\' אפ – ' + ch.name,
          html: '<p style=\"padding:1rem;text-align:center;color:#94a3b8\">לא נמצאו תוכניות בלוח השידורים עבור ערוץ זה כעת.<br>' + (c ? 'ניתן לקפוץ אחורה בשידור עד ' + c.hours + ' שעות.' : '') + '</p>',
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
      var html = '<div style=\"max-height:55vh;overflow-y:auto;display:flex;flex-direction:column;gap:8px;padding:4px;\" class=\"catchup-list\">';
      for (var j = 0; j < pastProgs.length; j++) {
        var prog = pastProgs[j];
        var startD = new Date(prog[0] * 60000);
        var endD = new Date((prog[0] + prog[1]) * 60000);
        var sh = String(startD.getHours()).padStart(2, '0') + ':' + String(startD.getMinutes()).padStart(2, '0');
        var eh = String(endD.getHours()).padStart(2, '0') + ':' + String(endD.getMinutes()).padStart(2, '0');
        var isCurrent = (nowMin >= prog[0] && nowMin <= prog[0] + prog[1]);
        var badge = isCurrent ? '<span style=\"background:#ef4444;color:#fff;border-radius:4px;padding:2px 6px;font-size:0.75rem;font-weight:bold\">משודר עכשיו</span>' : '<span style=\"background:#334155;color:#94a3b8;border-radius:4px;padding:2px 6px;font-size:0.75rem\">שודר היום</span>';
        var pImg = prog[4] ? '<img src=\"' + esc(prog[4]) + '\" style=\"width:70px;height:42px;object-fit:cover;border-radius:6px;flex-shrink:0\" loading=\"lazy\" />' : '';
        html += '<button class=\"dlg-focus catchup-item\" data-idx=\"' + j + '\" style=\"display:flex;align-items:center;gap:12px;background:rgba(255,255,255,0.06);border:1px solid rgba(255,255,255,0.12);border-radius:8px;padding:10px;text-align:right;color:#fff;cursor:pointer;width:100%\">' +
                pImg +
                '<div style=\"flex:1;min-width:0;text-align:right\">' +
                  '<div style=\"display:flex;align-items:center;justify-content:space-between;gap:8px\"><strong style=\"font-size:0.95rem;overflow:hidden;text-overflow:ellipsis;white-space:nowrap\">' + esc(prog[2]) + '</strong>' + badge + '</div>' +
                  '<div style=\"font-size:0.8rem;color:#94a3b8;margin-top:2px\">' + sh + ' - ' + eh + '</div>' +
                '</div>' +
                '<span style=\"background:rgba(59,130,246,0.3);color:#60a5fa;border:1px solid #3b82f6;border-radius:6px;padding:4px 10px;font-size:0.85rem\">צפה</span>' +
              '</button>';
      }
      html += '</div>';

      openDialog({
        ico: '⏮️',
        title: 'קאץ\\' אפ (שידור חוזר) – ' + ch.name,
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
      var k = ch.id;
      var arr = epgChannels[k] || epgChannels[k.toLowerCase()] || epgChannels[k.replace('_fast', '')] || epgChannels[k.replace('ch14', 'now14')] || epgChannels[k.replace('kan_hinuchit_mc', 'kan_hinuchit')];
      renderModal(arr);
    } else {
      fetch('epg.json')
        .then(function (r) { return r.json(); })
        .then(function (d) {
          window.ITV_EPG = d;
          var k = ch.id;
          var arr = d.channels ? (d.channels[k] || d.channels[k.toLowerCase()] || d.channels[k.replace('_fast', '')] || d.channels[k.replace('ch14', 'now14')] || d.channels[k.replace('kan_hinuchit_mc', 'kan_hinuchit')]) : null;
          renderModal(arr);
        })
        .catch(function () {
          renderModal(null);
        });
    }
  }
  window.openCatchupModal = openCatchupModal;"""

assert old_catchup in code, "old_catchup block not found"
code = code.replace(old_catchup, new_catchup, 1)

# Now check playerMenu items
menu_target = """    if (Guide) items.push({ ico: '▦', label: 'לוח שידורים', act: Guide.open });"""
menu_replacement = """    if (state.catchup) {
      items.push({ ico: '🔴', label: 'חזרה לשידור חי', act: function () { if (state.playing) loadStream(state.playing); } });
    }
    if (CATCHUP[ch.id] || CATCHUP[(ch.id || '').toLowerCase()]) {
      items.push({ ico: '⏮️', label: 'קאץ\\' אפ (שידור חוזר 24 שעות)', act: function () { openCatchupModal(ch); } });
    }
    if (Guide) items.push({ ico: '▦', label: 'לוח שידורים', act: Guide.open });"""

assert menu_target in code, "menu_target not found"
code = code.replace(menu_target, menu_replacement, 1)

with open("app.js", "w", encoding="utf-8", newline="\n") as f:
    f.write(code)

print("app.js patched successfully")

# Also copy to www/app.js if www directory exists
import os, shutil
if os.path.exists("www/app.js"):
    shutil.copy("app.js", "www/app.js")
    print("Copied to www/app.js")

