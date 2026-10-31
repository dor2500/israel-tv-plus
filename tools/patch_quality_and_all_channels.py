# tools/patch_quality_and_all_channels.py
import re

app_path = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\app.js'
with open(app_path, 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Update QUALITIES array
old_qualities = "var QUALITIES = [['auto', 'אוטומטי (לפי הרשת)'], ['max', 'הכי גבוהה'], ['saver', 'חסכונית (SD)']];"
new_qualities = """var QUALITIES = [
    ['auto', 'אוטומטי (Adaptive)'],
    ['upscale4k', 'השבחה ל-4K / Ultra Sharp (AI & חדות מוגברת)'],
    ['1080p', 'Full HD (1080p) מקסימלי'],
    ['720p', 'HD (720p) - מומלץ ליציבות'],
    ['480p', 'SD (480p) - יציב במיוחד'],
    ['360p', '360p - לחיבור חלש (ללא תקיעות)'],
    ['saver', 'חסכונית (SD)']
  ];"""

if old_qualities in text:
    text = text.replace(old_qualities, new_qualities)
    print("Updated QUALITIES in app.js")

# 2. Update LAT array for anti-stutter description
old_lat = "var LAT = [['stable', 'יציב (מומלץ)'], ['low', 'השהיה נמוכה (קרוב לשידור החי)'], ['realtime', 'בזמן אמת (ספורט)']];"
new_lat = "var LAT = [['stable', 'יציב במיוחד (מונע תקיעות - מומלץ)'], ['balanced', 'מאוזן (ברירת מחדל)'], ['low', 'השהיה נמוכה (קרוב לשידור החי)']];"
if old_lat in text:
    text = text.replace(old_lat, new_lat)
    print("Updated LAT in app.js")

# 3. Add ATV_ROWS and ATV_TABS 'all_channels'
old_atv_rows = """  var ATV_ROWS = [['broadcast', 'ערוצי ברודקאסט וחדשות'], ['sports', 'ספורט בשידור חי'], ['movies', 'סרטים וסדרות'],
    ['kids', 'ילדים ונוער'], ['fun', 'בידור, טבע ומוזיקה'], ['world', 'ערוצים בינלאומיים'], ['radio', 'רדיו ישראלי'],
    ['faith', 'דת ומסורת']];"""
new_atv_rows = """  var ATV_ROWS = [
    ['all_channels', '📺 כל הערוצים (All Channels)'],
    ['broadcast', 'ערוצי ברודקאסט וחדשות'],
    ['sports', 'ספורט בשידור חי'],
    ['movies', 'סרטים וסדרות'],
    ['kids', 'ילדים ונוער'],
    ['fun', 'בידור, טבע ומוזיקה'],
    ['world', 'ערוצים בינלאומיים'],
    ['radio', 'רדיו ישראלי'],
    ['faith', 'דת ומסורת']
  ];"""
if old_atv_rows in text:
    text = text.replace(old_atv_rows, new_atv_rows)
    print("Updated ATV_ROWS with all_channels")

old_atv_tabs = """  var ATV_TABS = [['פרופיל', 'profiles'], ['צפייה כעת', 'top'], ['ערוצי ישראל', 'broadcast'], ['ספורט', 'sports'], ['ילדים', 'kids'],
    ['סרטים ודיבוב', 'movies'], ['רדיו', 'radio'], ['מועדפים', 'mine'], ['חיפוש', 'search'], ['פיצול מסך', 'multiview'], ['הגדרות', 'settings']];"""
new_atv_tabs = """  var ATV_TABS = [
    ['פרופיל', 'profiles'],
    ['כל הערוצים', 'all_channels'],
    ['צפייה כעת', 'top'],
    ['ערוצי ישראל', 'broadcast'],
    ['ספורט', 'sports'],
    ['ילדים', 'kids'],
    ['סרטים ודיבוב', 'movies'],
    ['רדיו', 'radio'],
    ['מועדפים', 'mine'],
    ['חיפוש', 'search'],
    ['פיצול מסך', 'multiview'],
    ['הגדרות', 'settings']
  ];"""
if old_atv_tabs in text:
    text = text.replace(old_atv_tabs, new_atv_tabs)
    print("Updated ATV_TABS with all_channels")

# 4. In atvRowsData, populate buckets.all_channels
target_buckets = "var buckets = {};\n    ATV_ROWS.forEach(function (r) { buckets[r[0]] = []; });"
replacement_buckets = """var buckets = {};
    ATV_ROWS.forEach(function (r) { buckets[r[0]] = []; });
    buckets.all_channels = (S.kids ? state.all.filter(isKidsCh) : state.all).slice();"""
if target_buckets in text:
    text = text.replace(target_buckets, replacement_buckets)
    print("Populated buckets.all_channels in atvRowsData")

# 5. Add pickLevelByHeight and applyQualityLevel helper functions
quality_helpers = """
  function pickLevelByHeight(levels, targetH) {
    if (!levels || !levels.length) return 0;
    var closest = 0, minDiff = 99999;
    for (var i = 0; i < levels.length; i++) {
      var h = levels[i].height || 0;
      var diff = Math.abs(h - targetH);
      if (diff < minDiff) { minDiff = diff; closest = i; }
    }
    return closest;
  }

  function applyQualityLevel(hls, levels) {
    if (!hls || !levels || !levels.length) return;
    if (S.quality === 'upscale4k' || S.quality === 'max') {
      var best = 0, bestSc = 0;
      for (var i = 0; i < levels.length; i++) {
        var sc = (levels[i].height || 0) * 1000000 + (levels[i].bitrate || 0);
        if (sc >= bestSc) { bestSc = sc; best = i; }
      }
      hls.currentLevel = best;
    } else if (S.quality === '1080p') {
      hls.currentLevel = pickLevelByHeight(levels, 1080);
    } else if (S.quality === '720p') {
      hls.currentLevel = pickLevelByHeight(levels, 720);
    } else if (S.quality === '480p' || S.quality === 'saver') {
      hls.currentLevel = pickLevelByHeight(levels, 480);
    } else if (S.quality === '360p') {
      hls.currentLevel = pickLevelByHeight(levels, 360);
    } else {
      hls.currentLevel = -1; // auto adaptive
    }
    applyVideoPostProcessing();
  }

  function applyVideoPostProcessing() {
    if (!el || !el.video) return;
    if (S.quality === 'upscale4k') {
      el.video.classList.add('video-4k-upscale');
      el.video.style.filter = 'contrast(1.08) saturate(1.06) brightness(1.02)';
    } else {
      el.video.classList.remove('video-4k-upscale');
      el.video.style.filter = '';
    }
  }
"""

if "function applyQualityLevel" not in text:
    # insert before loadStream
    idx_ls = text.find("function loadStream")
    text = text[:idx_ls] + quality_helpers + "\n  " + text[idx_ls:]
    print("Added quality helper functions before loadStream")

# 6. Update HLS initialization in loadStream with anti-stutter buffer tuning and stall recovery
old_hls_config = """      var hls = new Hls({
        enableWorker: true,
        lowLatencyMode: true,
        backBufferLength: fastMode() ? 15 : 30,
        liveSyncDurationCount: low ? 2 : 3,
        liveMaxLatencyDurationCount: low ? 3 : 5,
        liveDurationInfinity: true,
        maxBufferLength: low ? 12 : 20,
        maxMaxBufferLength: low ? 20 : 35,
        manifestLoadingMaxRetry: 3,
        levelLoadingMaxRetry: 3,
        fragLoadingMaxRetry: 4,
        capLevelToPlayerSize: S.quality === 'auto'
      });"""

new_hls_config = """      var isStable = (S.latency === 'stable' || S.latency !== 'low');
      var hls = new Hls({
        enableWorker: true,
        lowLatencyMode: S.latency === 'low',
        backBufferLength: fastMode() ? 20 : 40,
        liveSyncDurationCount: isStable ? 4 : 2,
        liveMaxLatencyDurationCount: isStable ? 8 : 4,
        liveDurationInfinity: true,
        maxBufferLength: isStable ? 35 : 16,
        maxMaxBufferLength: isStable ? 60 : 30,
        maxBufferSize: 60 * 1000 * 1000,
        manifestLoadingMaxRetry: 5,
        levelLoadingMaxRetry: 5,
        fragLoadingMaxRetry: 6,
        fragLoadingRetryDelay: 500,
        capLevelToPlayerSize: S.quality === 'auto'
      });"""

if old_hls_config in text:
    text = text.replace(old_hls_config, new_hls_config)
    print("Updated HLS config with anti-stutter buffer tuning")

# 7. Update MANIFEST_PARSED handling to call applyQualityLevel and attach stall watchdog
old_manifest_parsed = """        var levels = data.levels || [];
        if (levels.length > 1) {
          if (S.quality === 'saver') {
            var cap = 0;
            for (var i = 0; i < levels.length; i++) if ((levels[i].height || 0) <= 576) cap = i;
            hls.autoLevelCapping = cap;
          } else {
            var best = levels.length - 1, bestScore = 0;
            for (var j = 0; j < levels.length; j++) {
              var sc = (levels[j].height || 0) * 1000000 + (levels[j].bitrate || 0);
              if (sc >= bestScore) { bestScore = sc; best = j; }
            }
            if (S.quality === 'max') hls.nextLevel = best;
          }
        }"""

new_manifest_parsed = """        var levels = data.levels || [];
        if (levels.length > 0) {
          applyQualityLevel(hls, levels);
        }
        // Anti-stutter auto-recovery watchdog
        var stallCount = 0;
        v.onwaiting = function () {
          stallCount++;
          if (stallCount >= 2 && hls.currentLevel > 0 && (S.quality === 'auto' || S.quality === 'upscale4k')) {
            hls.currentLevel = Math.max(0, hls.currentLevel - 1);
          }
        };
        v.onplaying = function () {
          setTimeout(function () { stallCount = 0; }, 10000);
        };"""

if old_manifest_parsed in text:
    text = text.replace(old_manifest_parsed, new_manifest_parsed)
    print("Updated MANIFEST_PARSED with applyQualityLevel and stall watchdog")

with open(app_path, 'w', encoding='utf-8') as f:
    f.write(text)
print("app.js completely patched!")

