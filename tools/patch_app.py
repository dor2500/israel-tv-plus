# -*- coding: utf-8 -*-
import sys, re

app_path = 'app.js'
with open(app_path, 'r', encoding='utf-8') as f:
    app = f.read()

# 1. Update DEFAULTS: add tvStyle and upscaleMode
old_defaults = "theme: 'oled', accent: 'auto', uiScale: 1,"
new_defaults = "theme: 'oled', accent: 'auto', tvStyle: 'appletv', uiScale: 1,"
if old_defaults in app:
    app = app.replace(old_defaults, new_defaults, 1)

old_pic_default = "pic: 'off', aspect: 'fit', sharpen: false,"
new_pic_default = "pic: 'off', aspect: 'fit', upscaleMode: 'off', sharpen: false,"
if old_pic_default in app:
    app = app.replace(old_pic_default, new_pic_default, 1)

# 2. Update loadSettings
old_load = "if (!s.upNextV2) { out.upNext = true; out.upNextV2 = true; }"
new_load = """if (!s.upNextV2) { out.upNext = true; out.upNextV2 = true; }
    if (s.upscaleMode === undefined) {
      if (s.quality === 'upscale4k') out.upscaleMode = 'ai_4k';
      else if (s.sharpen) out.upscaleMode = 'crystal_hd';
    }
    if (!out.tvStyle) out.tvStyle = 'appletv';"""
if old_load in app:
    app = app.replace(old_load, new_load, 1)

# 3. Startup ensureEpgData preload
old_boot_load = "setTimeout(loadVodData, 800);"
new_boot_load = "setTimeout(loadVodData, 800);\n    setTimeout(function () { ensureEpgData(); }, 1200);"
if old_boot_load in app and "ensureEpgData();" not in app:
    app = app.replace(old_boot_load, new_boot_load, 1)

# 4. Update applyAll
old_apply_all = "['theme', 'hero', 'numbers', 'hints', 'perf', 'osdClock', 'pic', 'aspect', 'sharpen', 'kids'].forEach(applySetting);"
new_apply_all = "['theme', 'tvStyle', 'hero', 'numbers', 'hints', 'perf', 'osdClock', 'pic', 'aspect', 'sharpen', 'upscaleMode', 'kids'].forEach(applySetting);"
if old_apply_all in app:
    app = app.replace(old_apply_all, new_apply_all, 1)

# 5. Update applySetting(k)
old_setting_theme = """    if (k === 'theme' || k === 'accent') {
      b.classList.remove('oled', 'light', 'dark');
      b.classList.add(S.theme);
      applyColors();
    } else if (k === 'uiScale' || k === 'cols') { applyScale(); renderGridFresh(); }"""

new_setting_theme = """    if (k === 'theme' || k === 'accent') {
      b.classList.remove('oled', 'light', 'dark');
      b.classList.add(S.theme);
      applyColors();
    } else if (k === 'tvStyle') {
      b.classList.remove('tvstyle-appletv', 'tvstyle-googletv', 'tvstyle-oneui', 'tvstyle-webos', 'tvstyle-cinema');
      b.classList.add('tvstyle-' + (S.tvStyle || 'appletv'));
    } else if (k === 'uiScale' || k === 'cols') { applyScale(); renderGridFresh(); }"""

if old_setting_theme in app:
    app = app.replace(old_setting_theme, new_setting_theme, 1)

old_setting_pic = "else if (k === 'pic' || k === 'aspect' || k === 'sharpen') applyPicture();"
new_setting_pic = "else if (k === 'pic' || k === 'aspect' || k === 'sharpen' || k === 'upscaleMode') applyPicture();"
if old_setting_pic in app:
    app = app.replace(old_setting_pic, new_setting_pic, 1)

# 6. Add TV_STYLES & UPSCALE_MODES and update applyPicture
old_pics_def = "var PICS = [['off', 'רגיל (ללא שינוי)'], ['cinema', 'קולנוע'], ['vivid', 'צבעים חיים'], ['sports', 'ספורט'], ['warm', 'חם']];"
new_pics_def = """var TV_STYLES = [
    ['appletv', 'Apple TV (Liquid Glass & Frosted Blur – זכוכית שקופה ועיקולים רכים)'],
    ['googletv', 'Google TV / Material 3 (משטחים נקיים, קווי מתאר והדגשות זוהרות)'],
    ['oneui', 'Samsung One UI (מינימליזם שטוח, שחור OLED מלא וריבועים מעוגלים)'],
    ['webos', 'LG webOS (כרטיסיות צבעוניות, עומק שכבות וצלליות אלגנטיות)'],
    ['cinema', 'Cinematic Theater (קולנוע אפל, זום פוקוס דרמטי ורקע סינמטי)']
  ];
  var UPSCALE_MODES = [
    ['off', 'כבוי (תמונה מקורית ללא עיבוד)'],
    ['ai_4k', 'השבחת AI 4K Ultra Sharp (חדות דינמית ושיפור רזולוציה)'],
    ['crystal_hd', 'מנוע Crystal Clear HD (חידוד מוגבר לשידורי SD/HD)'],
    ['cinema_hdr', 'קולנוע HDR Dynamic (טווח דינמי רחב ושחורים עמוקים)'],
    ['vivid_oled', 'צבעים חיים OLED Pop (גוונים עשירים ותמונה בוהקת)'],
    ['sports_pro', 'ספורט Pro Ultra (תנועה חלקה ומשטחים חיים)'],
    ['smooth_denoise', 'החלקה והפחתת רעשים (De-Noise עדין לשידורים מגורענים)'],
    ['retro_crt', 'אפקט שידור קלאסי / רטרו (חמימות נוסטלגית)']
  ];
  var PICS = [['off', 'רגיל (ללא שינוי)'], ['cinema', 'קולנוע'], ['vivid', 'צבעים חיים'], ['sports', 'ספורט'], ['warm', 'חם']];"""

if old_pics_def in app:
    app = app.replace(old_pics_def, new_pics_def, 1)

old_apply_pic = """  function applyPicture() {
    var v = el.video;
    if (!v) return;
    v.className = '';
    if (S.pic !== 'off') v.classList.add('pic-' + S.pic);
    if (S.aspect !== 'fit') v.classList.add('aspect-' + S.aspect);
    if (S.sharpen) v.classList.add('sharpen');
    if (el.osdPic) el.osdPic.textContent = (S.pic !== 'off' ? '✨ ' + label(PICS, S.pic) + ' · ' : '') + label(ASPECTS, S.aspect);
  }"""

new_apply_pic = """  function applyPicture() {
    var v = el.video;
    if (!v) return;
    v.className = '';
    if (S.aspect && S.aspect !== 'fit') v.classList.add('aspect-' + S.aspect);
    if (S.upscaleMode && S.upscaleMode !== 'off') {
      v.classList.add('upscale-' + S.upscaleMode);
    } else if (S.pic && S.pic !== 'off') {
      v.classList.add('pic-' + S.pic);
    }
    if (S.sharpen && (!S.upscaleMode || S.upscaleMode === 'off')) v.classList.add('upscale-crystal_hd');
    if (el.osdPic) {
      var upL = (S.upscaleMode && S.upscaleMode !== 'off') ? label(UPSCALE_MODES, S.upscaleMode) : (S.pic !== 'off' ? label(PICS, S.pic) : '');
      el.osdPic.textContent = (upL ? '✨ ' + upL + ' · ' : '') + label(ASPECTS, S.aspect);
    }
    if (el.osdQuality) {
      if (S.upscaleMode === 'ai_4k') {
        el.osdQuality.textContent = '4K ULTRA HD';
        el.osdQuality.className = 'osd-quality osd-quality-4k';
      } else if (S.upscaleMode === 'crystal_hd') {
        el.osdQuality.textContent = 'CRYSTAL HD';
        el.osdQuality.className = 'osd-quality';
      } else {
        el.osdQuality.className = 'osd-quality';
      }
    }
  }"""

if old_apply_pic in app:
    app = app.replace(old_apply_pic, new_apply_pic, 1)

# 7. Update applyVideoPostProcessing
old_post_proc = """  function applyVideoPostProcessing() {
    if (!el || !el.video) return;
    if (S.quality === 'upscale4k') {
      el.video.classList.add('video-4k-upscale');
      el.video.style.filter = 'url(#filter-4k-upscale) contrast(1.06) saturate(1.05) brightness(1.01)';
      if (el.osdQuality) {
        el.osdQuality.textContent = '4K ULTRA HD';
        el.osdQuality.className = 'osd-quality osd-quality-4k';
      }
    } else {
      el.video.classList.remove('video-4k-upscale');
      el.video.style.filter = '';
      if (el.osdQuality) {
        el.osdQuality.className = 'osd-quality';
      }
    }
  }"""

new_post_proc = """  function applyVideoPostProcessing() {
    applyPicture();
  }"""

if old_post_proc in app:
    app = app.replace(old_post_proc, new_post_proc, 1)

# 8. Add ensureEpgData, formatEpgTime, getEpgProgramInfo and replace showInfo
old_show_info = """  function showInfo(ch) {
    var s = sourceOf(ch);
    var rows = [['מספר', esc(ch.number)], ['קטגוריה', esc(splitCat(ch.category))], ['שפה', esc(ch.lang === 'he' ? 'עברית' : Dub ? Dub.langName(ch.lang) : ch.lang)],
      ['איכות', esc(ch.badge || '')]];
    if (isApp(ch)) {
      rows.push(['סוג', 'שירות מנויים רשמי – נפתח באפליקציה שלו']);
      rows.push(['אתר', '<code>' + esc(ch.site || ch.url) + '</code>']);
    } else {
      rows.push(['מקור השידור', esc(s.by)]);
      if (s.kind) rows.push(['סוג המקור', esc(KIND_LABEL[s.kind] || s.kind)]);
      rows.push(['שרת', '<code>' + esc(s.host) + '</code>']);
      rows.push(['כתובת', '<code>' + esc(ch.url) + '</code>']);
      if (ch.webUrl) rows.push(['באתר (https)', '<code>' + esc(ch.webUrl) + '</code>']);
      if (s.web) rows.push(['בדפדפן', esc(s.web)]);
      if (s.found) rows.push(['נמצא ב', esc(s.found)]);
      if (foreign(ch)) rows.push(['דיבוב', '🎙️ אפשר לדבב לעברית']);
    }
    openDialog({
      ico: 'ℹ️', title: ch.name, wide: true,
      html: (ch.desc ? '<p>' + esc(ch.desc) + '</p>' : '') + rows.map(function (r) {
        return '<div class="kv"><b>' + r[0] + '</b><span>' + r[1] + '</span></div>';
      }).join('') + '<p style="margin-top:1.2rem">כל המקורות: ' + esc(REPO.replace('https://', '')) + '/blob/master/SOURCES.md</p>',
      buttons: [{ label: 'סגירה', primary: true }, { label: '📋 כל המקורות', act: function () { openUrl(REPO + '/blob/master/SOURCES.md'); } }]
    });
  }"""

new_show_info = """  function ensureEpgData(cb) {
    if (window.ITV_EPG && window.ITV_EPG.channels) {
      if (cb) cb(window.ITV_EPG.channels);
      return;
    }
    var urls = /^https?:/.test(location.protocol) ? ['epg.json', 'https://dor2500.github.io/israel-tv-plus/epg.json'] : ['https://dor2500.github.io/israel-tv-plus/epg.json'];
    var i = 0;
    (function next() {
      if (i >= urls.length) {
        if (cb) cb(null);
        return;
      }
      xhrJson(urls[i++], 12000, function (data) {
        if (data && data.channels) {
          window.ITV_EPG = data;
          window.ITV_EPG_CHANNELS = data.channels;
          if (cb) cb(data.channels);
        } else {
          next();
        }
      }, next);
    })();
  }

  function formatEpgTime(m) {
    var d = new Date(m * 60000);
    return ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
  }

  function getEpgProgramInfo(ch) {
    if (!ch) return null;
    var epgChannels = (window.ITV_EPG && window.ITV_EPG.channels) || window.ITV_EPG_CHANNELS || null;
    if (!epgChannels) return null;
    var k = (typeof CATCHUP_EPG !== 'undefined' && CATCHUP_EPG[ch.id]) || ch.id;
    var list = epgChannels[k] || epgChannels[k.toLowerCase()] || epgChannels[k.replace('_fast', '')] || epgChannels[k.replace('_hd', '')] || [];
    if (!list.length) {
      for (var ek in epgChannels) {
        if (ek.toLowerCase() === (ch.id || '').toLowerCase() || (ch.iptv && ek.toLowerCase() === ch.iptv.toLowerCase())) {
          list = epgChannels[ek];
          break;
        }
      }
    }
    if (!list || !list.length) return null;
    var nowMin = Math.floor(Date.now() / 60000);
    for (var i = 0; i < list.length; i++) {
      var p = list[i];
      if (p[0] <= nowMin && nowMin < p[0] + p[1]) {
        return {
          cur: p,
          next: (i + 1 < list.length) ? list[i + 1] : null,
          nowMin: nowMin,
          isLive: true
        };
      }
    }
    for (var j = 0; j < list.length; j++) {
      if (list[j][0] >= nowMin) {
        return {
          cur: list[j],
          next: (j + 1 < list.length) ? list[j + 1] : null,
          nowMin: nowMin,
          isLive: false
        };
      }
    }
    return null;
  }

  function showInfo(ch) {
    if (!ch) return;
    if (!window.ITV_EPG || !window.ITV_EPG.channels) {
      ensureEpgData(function () {
        renderShowInfoModal(ch);
      });
    } else {
      renderShowInfoModal(ch);
    }
  }

  function renderShowInfoModal(ch) {
    var pi = getEpgProgramInfo(ch);
    var cardHtml = '';
    var buttons = [];
    var s = sourceOf(ch);

    if (pi && pi.cur) {
      var cur = pi.cur;
      var sMin = cur[0], dur = cur[1], eMin = sMin + dur;
      var title = cur[2] || '', desc = cur[3] || '', poster = cur[4] || '';
      var sStr = formatEpgTime(sMin), eStr = formatEpgTime(eMin);
      var elapsed = Math.max(0, pi.nowMin - sMin);
      var pct = Math.min(100, Math.max(0, Math.round((elapsed / Math.max(dur, 1)) * 100)));
      var rem = Math.max(0, eMin - pi.nowMin);

      var posterImg = poster ? '<img class="info-prog-poster" src="' + esc(poster) + '" alt="" onerror="this.style.display=\\'none\\'">' : '';
      var badgeHtml = pi.isLive ? '<span class="info-prog-badge">● שידור חי כעת</span>' : '<span class="info-prog-badge">משודר בקרוב</span>';
      var timeHtml = '<div class="info-prog-time">' + sStr + ' – ' + eStr + ' (נותרו ' + rem + ' דקות)</div>';
      var barHtml = '<div class="info-prog-progress"><div class="info-prog-progress-fill" style="width:' + pct + '%"></div></div>';
      var descHtml = desc ? '<div class="info-prog-desc"><b>תקציר והסבר על התוכנית:</b><br>' + esc(desc) + '</div>' : '';
      var nextHtml = '';
      if (pi.next) {
        var nStr = formatEpgTime(pi.next[0]);
        nextHtml = '<div class="info-prog-next">התוכנית הבאה: <b>' + esc(pi.next[2]) + '</b> בשעה ' + nStr + ' (' + pi.next[1] + ' דק\\')</div>';
      }

      cardHtml = '<div class="info-prog-card">' +
        '<div class="info-prog-header">' +
          posterImg +
          '<div class="info-prog-meta">' +
            badgeHtml +
            '<div class="info-prog-title">' + esc(title) + '</div>' +
            timeHtml +
          '</div>' +
        '</div>' +
        barHtml +
        descHtml +
        nextHtml +
      '</div>';

      if (CATCHUP[ch.id] || CATCHUP[(ch.id || '').toLowerCase()]) {
        buttons.push({
          label: '⏮️ צפה מראשית התוכנית',
          act: function () {
            closeDialog();
            playCatchup(ch, sMin, eMin);
          }
        });
      }
    } else if (ch.desc || ch.isVod) {
      cardHtml = '<div class="info-prog-card">' +
        '<div class="info-prog-header">' +
          (ch.logo ? '<img class="info-prog-poster" src="' + esc(ch.logo) + '" alt="" onerror="this.style.display=\\'none\\'">' : '') +
          '<div class="info-prog-meta">' +
            '<span class="info-prog-badge vod">VOD וסרטים</span>' +
            '<div class="info-prog-title">' + esc(ch.name) + '</div>' +
            '<div class="info-prog-time">' + esc(splitCat(ch.category)) + (ch.badge ? ' · ' + esc(ch.badge) : '') + '</div>' +
          '</div>' +
        '</div>' +
        (ch.desc ? '<div class="info-prog-desc"><b>תקציר והסבר:</b><br>' + esc(ch.desc) + '</div>' : '') +
      '</div>';
    }

    if (CATCHUP[ch.id] || CATCHUP[(ch.id || '').toLowerCase()]) {
      buttons.push({
        label: '📅 לוח שידורים מלא וקאצ\\' אפ',
        act: function () {
          closeDialog();
          if (typeof openCatchupModal === 'function') openCatchupModal(ch);
          else if (Guide) Guide.open();
        }
      });
    } else if (Guide) {
      buttons.push({
        label: '📅 לוח שידורים',
        act: function () {
          closeDialog();
          Guide.open();
        }
      });
    }
    buttons.push({ label: 'סגירה', primary: true });

    var rows = [
      ['מספר ערוץ', esc(ch.number)],
      ['קטגוריה', esc(splitCat(ch.category))],
      ['שפה', esc(ch.lang === 'he' ? 'עברית' : Dub ? Dub.langName(ch.lang) : ch.lang)],
      ['איכות שידור', esc(ch.badge || 'FHD')]
    ];
    if (isApp(ch)) {
      rows.push(['סוג', 'שירות מנויים רשמי – נפתח באפליקציה שלו']);
      rows.push(['אתר', '<code>' + esc(ch.site || ch.url) + '</code>']);
    } else {
      rows.push(['מקור השידור', esc(s.by)]);
      if (s.kind) rows.push(['סוג המקור', esc(KIND_LABEL[s.kind] || s.kind)]);
      rows.push(['שרת', '<code>' + esc(s.host) + '</code>']);
      rows.push(['כתובת HLS', '<code>' + esc(ch.url) + '</code>']);
      if (ch.webUrl) rows.push(['באתר (https)', '<code>' + esc(ch.webUrl) + '</code>']);
      if (s.web) rows.push(['בדפדפן', esc(s.web)]);
      if (foreign(ch)) rows.push(['דיבוב', '🎙️ אפשר לדבב לעברית']);
    }

    var techHtml = '<details class="info-tech-details">' +
      '<summary>פרטי ערוץ ושרת שידור טכני</summary>' +
      '<div class="epg-tech-rows" style="margin-top:0.8rem">' +
        rows.map(function (r) {
          return '<div class="kv"><b>' + r[0] + '</b><span>' + r[1] + '</span></div>';
        }).join('') +
        '<p style="margin-top:0.8rem;font-size:1.2rem">כל המקורות: ' + esc(REPO.replace('https://', '')) + '/blob/master/SOURCES.md</p>' +
      '</div>' +
    '</details>';

    openDialog({
      ico: 'ℹ️',
      title: ch.name + ' (ערוץ ' + ch.number + ')',
      wide: true,
      html: cardHtml + techHtml,
      buttons: buttons
    });
  }"""

if old_show_info in app:
    app = app.replace(old_show_info, new_show_info, 1)

# 9. Update playerMenu
old_player_menu_items = """    if (!state.isRadio) {
      items.push({ ico: '✨', label: 'מצב תמונה', stay: true, dot: colourOf('picture'), val: function () { return label(PICS, S.pic); },
        act: function (st) { setSetting('pic', cycle(PICS, S.pic, st)); } });"""

new_player_menu_items = """    if (!state.isRadio) {
      items.push({
        ico: '✨',
        label: 'מצב אפסקייל והשבחת תמונה',
        stay: true,
        val: function () { return label(UPSCALE_MODES, S.upscaleMode); },
        act: function (st) {
          var nextM = cycle(UPSCALE_MODES, S.upscaleMode, st);
          setSetting('upscaleMode', nextM);
          toast('✨ אפסקייל: ' + label(UPSCALE_MODES, nextM));
        }
      });
      items.push({ ico: '🎨', label: 'מצב צבע ותמונה', stay: true, dot: colourOf('picture'), val: function () { return label(PICS, S.pic); },
        act: function (st) { setSetting('pic', cycle(PICS, S.pic, st)); } });"""

if old_player_menu_items in app:
    app = app.replace(old_player_menu_items, new_player_menu_items, 1)

old_info_item = "items.push({ ico: 'ℹ️', label: 'פרטי הערוץ', dot: colourOf('info'), act: function () { showInfo(ch); } });"
new_info_item = "items.push({ ico: 'ℹ️', label: 'מידע על התוכנית והערוץ', dot: colourOf('info'), act: function () { showInfo(ch); } });"
if old_info_item in app:
    app = app.replace(old_info_item, new_info_item)

# 10. Update settingsSections
old_sec_display = """      { id: 'display', ico: M3_ICONS.display, name: 'תצוגה וממשק', desc: 'ערכות נושא, פלטת צבעים, קנה מידה ועיצוב מסך', rows: [
        sep('מראה'),
        choice('theme', 'ערכת נושא', THEMES),"""

new_sec_display = """      { id: 'display', ico: M3_ICONS.display, name: 'תצוגה וממשק', desc: 'ערכות נושא, סגנון עיצוב טלוויזיה, פלטת צבעים ועיצוב מסך', rows: [
        sep('מראה וסגנון עיצוב'),
        choice('tvStyle', 'סגנון עיצוב ממשק הטלוויזיה', TV_STYLES, 'בחירת שפת העיצוב: Apple TV זכוכיתי, Google TV מודרני, סמסונג One UI שטוח, LG webOS או קולנוע סינמטי'),
        choice('theme', 'ערכת נושא', THEMES),"""

if old_sec_display in app:
    app = app.replace(old_sec_display, new_sec_display, 1)

old_sec_player = """      { id: 'player', ico: M3_ICONS.player, name: 'נגן וידאו ושידור', desc: 'איכות תמונה, השהיה, שחזור שידור ומעבר ערוצים', rows: [
        sep('תמונה ואיכות'),
        choice('pic', 'יחס תמונה', PICS), choice('aspect', 'התאמת תמונה למסך', ASPECTS),
        toggle('sharpen', 'שיפור חדות תמונה (SD Enhancer)', 'מנגנון חידוד תמונה מתקדם לשידורים ישנים'),"""

new_sec_player = """      { id: 'player', ico: M3_ICONS.player, name: 'נגן וידאו ושידור', desc: 'איכות תמונה, אפסקייל והשבחת וידאו, השהיה ושחזור שידור', rows: [
        sep('השבחת תמונה ואפסקייל'),
        choice('upscaleMode', 'מצב אפסקייל והשבחת תמונה', UPSCALE_MODES, 'מנגנון השבחת רזולוציה וחדות תמונה דינמית מואץ חומרה ל-4K ו-HD'),
        choice('pic', 'מצב צבע ותמונה', PICS), choice('aspect', 'התאמת תמונה למסך', ASPECTS),
        toggle('sharpen', 'שיפור חדות תמונה (SD Enhancer)', 'מנגנון חידוד תמונה מתקדם לשידורים ישנים'),"""

if old_sec_player in app:
    app = app.replace(old_sec_player, new_sec_player, 1)

with open(app_path, 'w', encoding='utf-8') as f:
    f.write(app)

print('app.js patched successfully!')
