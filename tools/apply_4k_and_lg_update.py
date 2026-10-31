import os

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"

# 1. Update index.html with SVG filter for 4K upscale
idx_path = os.path.join(ROOT, "index.html")
with open(idx_path, "r", encoding="utf-8") as f:
    idx_content = f.read()

svg_filter = '''  <svg style="position:absolute;width:0;height:0;overflow:hidden;" aria-hidden="true">
    <filter id="filter-4k-upscale" color-interpolation-filters="sRGB">
      <feConvolveMatrix order="3" preserveAlpha="true" kernelMatrix="0 -0.55 0 -0.55 3.2 -0.55 0 -0.55 0"/>
      <feColorMatrix type="matrix" values="
        1.03 0    0    0   -0.01
        0    1.03 0    0   -0.01
        0    0    1.03 0   -0.01
        0    0    0    1    0"/>
    </filter>
  </svg>
'''

if "filter-4k-upscale" not in idx_content:
    body_tag = '<body class="oled">'
    assert body_tag in idx_content, "body tag not found in index.html"
    idx_content = idx_content.replace(body_tag, body_tag + "\n" + svg_filter, 1)
    with open(idx_path, "w", encoding="utf-8") as f:
        f.write(idx_content)
    print("Added filter-4k-upscale to index.html")

# 2. Update style.css with enhanced 4K styling
css_path = os.path.join(ROOT, "style.css")
with open(css_path, "r", encoding="utf-8") as f:
    css_content = f.read()

old_4k_css = '''.video-4k-upscale {
  image-rendering: -webkit-optimize-contrast !important;
  image-rendering: crisp-edges !important;
  transform: translateZ(0);
}'''

new_4k_css = '''.video-4k-upscale {
  image-rendering: -webkit-optimize-contrast !important;
  image-rendering: high-quality !important;
  -webkit-transform: translate3d(0, 0, 0) !important;
  transform: translate3d(0, 0, 0) !important;
  backface-visibility: hidden !important;
  -webkit-backface-visibility: hidden !important;
  filter: url(#filter-4k-upscale) contrast(1.06) saturate(1.05) brightness(1.01) !important;
  will-change: transform, filter !important;
}

.osd-quality-4k {
  background: linear-gradient(135deg, #ffd700, #ff8c00) !important;
  color: #000 !important;
  font-weight: 800 !important;
  border-radius: 4px !important;
  padding: 2px 7px !important;
  letter-spacing: 0.5px !important;
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.45) !important;
}'''

if old_4k_css in css_content:
    css_content = css_content.replace(old_4k_css, new_4k_css, 1)
    with open(css_path, "w", encoding="utf-8") as f:
        f.write(css_content)
    print("Updated style.css with 4K upscale styles")

# 3. Update boot.js for robust LG update loading
boot_path = os.path.join(ROOT, "boot.js")
with open(boot_path, "r", encoding="utf-8") as f:
    boot_content = f.read()

old_boot_logic = '''    if (live && get('itv_pending') === String(live.code)) set('itv_bad', String(live.code)); // last start never finished
    if (!live || !(live.code > BUILD.code) || settings.liveUpdates === false || forcedLocal ||
        (live.shell || 0) > (BUILD.shell || 0) || get('itv_bad') === String(live.code)) {
      live = null;
    }'''

new_boot_logic = '''    if (live && get('itv_pending') === String(live.code)) set('itv_bad', String(live.code)); // last start never finished
    if (live && live.code > (parseInt(get('itv_bad') || '0', 10))) store.removeItem('itv_bad');
    if (!live || !(live.code > BUILD.code) || settings.liveUpdates === false || forcedLocal ||
        (live.shell || 0) > (BUILD.shell || 0) || get('itv_bad') === String(live.code)) {
      live = null;
    }'''

if old_boot_logic in boot_content:
    boot_content = boot_content.replace(old_boot_logic, new_boot_logic, 1)
    with open(boot_path, "w", encoding="utf-8") as f:
        f.write(boot_content)
    print("Updated boot.js with bad build recovery")

# 4. Update app.js for 4K upscale engine & LG update actions
app_path = os.path.join(ROOT, "app.js")
with open(app_path, "r", encoding="utf-8") as f:
    app_content = f.read()

# applyVideoPostProcessing
old_post = '''  function applyVideoPostProcessing() {
    if (!el || !el.video) return;
    if (S.quality === 'upscale4k') {
      el.video.classList.add('video-4k-upscale');
      el.video.style.filter = 'contrast(1.08) saturate(1.06) brightness(1.02)';
    } else {
      el.video.classList.remove('video-4k-upscale');
      el.video.style.filter = '';
    }
  }'''

new_post = '''  function applyVideoPostProcessing() {
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
  }'''

if old_post in app_content:
    app_content = app_content.replace(old_post, new_post, 1)
    print("Updated applyVideoPostProcessing in app.js")

# applyQualityLevel
old_qual_level = '''    if (S.quality === 'upscale4k' || S.quality === 'max') {
      var best = 0, bestSc = 0;
      for (var i = 0; i < levels.length; i++) {
        var sc = (levels[i].height || 0) * 1000000 + (levels[i].bitrate || 0);
        if (sc >= bestSc) { bestSc = sc; best = i; }
      }
      hls.currentLevel = best;'''

new_qual_level = '''    if (S.quality === 'upscale4k' || S.quality === 'max') {
      var best = 0, bestSc = 0;
      for (var i = 0; i < levels.length; i++) {
        var sc = (levels[i].height || 0) * 1000000 + (levels[i].bitrate || 0);
        if (sc >= bestSc) { bestSc = sc; best = i; }
      }
      hls.currentLevel = best;
      hls.loadLevel = best;
      hls.autoLevelCapping = -1;'''

if old_qual_level in app_content:
    app_content = app_content.replace(old_qual_level, new_qual_level, 1)
    print("Updated applyQualityLevel in app.js")

# remove upscale4k from onstalled downscaling
old_stall = "if (hls && hls.currentLevel > 0 && (S.quality === 'auto' || S.quality === 'upscale4k')) {"
new_stall = "if (hls && hls.currentLevel > 0 && S.quality === 'auto') {"
if old_stall in app_content:
    app_content = app_content.replace(old_stall, new_stall, 1)
    print("Prevented quality downgrade on stall when upscale4k is selected")

# Hls buffer configuration for 4K
old_hls_buf = '''      var hls = new Hls({
        enableWorker: true,
        lowLatencyMode: true,
        backBufferLength: fastMode() ? 10 : 20,
        liveSyncDurationCount: isSport ? 1 : (isStable ? 2 : 1),
        liveMaxLatencyDurationCount: isSport ? 2 : (isStable ? 4 : 2),
        liveDurationInfinity: true,
        maxLiveSyncPlaybackRate: 1.15,
        maxBufferLength: isSport ? 8 : 16,
        maxMaxBufferLength: isSport ? 16 : 30,'''

new_hls_buf = '''      var is4K = S.quality === 'upscale4k';
      var hls = new Hls({
        enableWorker: true,
        lowLatencyMode: !is4K,
        backBufferLength: fastMode() ? 10 : (is4K ? 30 : 20),
        liveSyncDurationCount: isSport && !is4K ? 1 : (isStable ? 2 : 1),
        liveMaxLatencyDurationCount: isSport && !is4K ? 2 : (isStable ? 4 : 2),
        liveDurationInfinity: true,
        maxLiveSyncPlaybackRate: 1.15,
        maxBufferLength: is4K ? 45 : (isSport ? 8 : 16),
        maxMaxBufferLength: is4K ? 90 : (isSport ? 16 : 30),
        maxBufferSize: is4K ? 60 * 1000 * 1000 : (isSport ? 16 * 1000 * 1000 : 32 * 1000 * 1000),'''

if old_hls_buf in app_content:
    app_content = app_content.replace(old_hls_buf, new_hls_buf, 1)
    print("Updated Hls buffer configuration for 4K upscale")

# Update checkUpdates handling on webOS
old_check = '''      if (data.code > activeCode && S.liveUpdates && (data.shell || 0) <= (BUILD.shell || 0)) {
        u.liveDownloading = true;
        var q = '?v=' + data.code;
        prefetch(['style.css', 'ui.js', 'channels.js', 'dub.js', 'tvguide.js', 'app.js'].map(function (f) { return REMOTE + f + q; }), function (ok) {
          u.liveDownloading = false;
          if (ok) {
            state.liveReady = { code: data.code, version: data.version, shell: data.shell || 0 };
            try { localStorage.setItem('itv_live', JSON.stringify(state.liveReady)); localStorage.removeItem('itv_bad'); } catch (e) {}
            if (!applyLiveIfIdle()) {
              showUpdateButton();
              toast('⬆️ עדכון ' + data.version + ' מוכן – יופעל בפתיחה הבאה (או בהגדרות ← עדכונים)');
            }
          }
          refreshSettingsIfOpen();
        });
      } else if (manual && !u.native) toast('✅ הגרסה עדכנית (' + (LIVE ? LIVE.version : BUILD.version) + ')');'''

new_check = '''      if (data.code > activeCode && S.liveUpdates && (data.shell || 0) <= (BUILD.shell || 0)) {
        u.liveDownloading = true;
        try { localStorage.removeItem('itv_bad'); } catch (e) {}
        if (manual) toast('⬆️ נמצא עדכון ' + data.version + ' – מוריד עדכון...');
        var q = '?v=' + data.code;
        prefetch(['style.css', 'ui.js', 'channels.js', 'dub.js', 'tvguide.js', 'app.js'].map(function (f) { return REMOTE + f + q; }), function (ok) {
          u.liveDownloading = false;
          if (ok) {
            state.liveReady = { code: data.code, version: data.version, shell: data.shell || 0 };
            try { localStorage.setItem('itv_live', JSON.stringify(state.liveReady)); localStorage.removeItem('itv_bad'); localStorage.removeItem('itv_pending'); } catch (e) {}
            showUpdateButton();
            if (manual) {
              confirmBox('עדכון גרסה ' + data.version + ' מוכן! להפעיל כעת?', function () {
                location.reload();
              });
            } else if (!applyLiveIfIdle()) {
              toast('⬆️ עדכון ' + data.version + ' מוכן – יופעל בפתיחה הבאה');
            }
          } else {
            if (manual) toast('הורדת העדכון לא הצליחה, נסה שוב');
          }
          refreshSettingsIfOpen();
        });
      } else if (manual && !u.native) toast('✅ הגרסה עדכנית (' + (LIVE ? LIVE.version : BUILD.version) + ')');'''

if old_check in app_content:
    app_content = app_content.replace(old_check, new_check, 1)
    print("Updated checkUpdates in app.js")

# Update updateCardAction to allow one-click apply
old_uc_act = '''  function updateCardAction() {
    var u = state.update;
    if (state.checking) return;
    if (u && (u.native || state.liveReady)) showUpdateDialog(); else checkUpdates(true);
  }'''

new_uc_act = '''  function updateCardAction() {
    var u = state.update;
    if (state.checking) return;
    if (state.liveReady) {
      confirmBox('להפעיל כעת את עדכון ' + state.liveReady.version + '?', function () {
        location.reload();
      });
      return;
    }
    if (u && u.native && PLATFORM !== 'webos') showUpdateDialog();
    else checkUpdates(true);
  }'''

if old_uc_act in app_content:
    app_content = app_content.replace(old_uc_act, new_uc_act, 1)
    print("Updated updateCardAction in app.js")

with open(app_path, "w", encoding="utf-8") as f:
    f.write(app_content)

print("All 4K and LG update optimizations applied!")
