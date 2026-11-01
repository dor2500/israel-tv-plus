import json, os, re

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

# 1. Update vod_data.json and www/vod_data.json
print("Updating vod_data.json posters...")
with open("vod_data.json", "r", encoding="utf-8") as f:
    vod = json.load(f)

for item in vod:
    vid = item.get("id", "")
    if vid == "vod_ramzor":
        item["poster"] = "assets/vod/ramzor.jpg"
        item["backdrop"] = "assets/vod/ramzor.jpg"
        for ep in item.get("episodes", []):
            if "savri_maranan" in ep.get("thumb", ""):
                ep["thumb"] = "assets/vod/ramzor.jpg"
    elif vid == "vod_tzomet_miller":
        item["poster"] = "assets/vod/tzomet_miller.jpg"
        item["backdrop"] = "assets/vod/tzomet_miller.jpg"
        for ep in item.get("episodes", []):
            if "savri_maranan" in ep.get("thumb", ""):
                ep["thumb"] = "assets/vod/tzomet_miller.jpg"
    elif vid == "vod_mivtza_savta":
        item["poster"] = "assets/vod/mivtza_savta.jpg"
        item["backdrop"] = "assets/vod/mivtza_savta.jpg"
        for ep in item.get("episodes", []):
            if "savri_maranan" in ep.get("thumb", ""):
                ep["thumb"] = "assets/vod/mivtza_savta.jpg"

for p in ["vod_data.json", "www/vod_data.json"]:
    if os.path.exists(os.path.dirname(p)) or p == "vod_data.json":
        with open(p, "w", encoding="utf-8") as f:
            json.dump(vod, f, ensure_ascii=False, indent=2)
        print(f"Saved {p}")

# 2. Update channels.json and channels.js for channel 36 (sport_5_4k and sport5_4k_uhd)
print("Updating channels.json and channels.js...")
with open("channels.json", "r", encoding="utf-8") as f:
    channels = json.load(f)

fhd_sport5 = "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/sport_5/live.livx/playlist.m3u8"
hevc_sport5 = "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/sport_5_4k/live.livx/playlist.m3u8"

for c in channels:
    if c.get("id") in ["sport_5_4k", "sport5_4k_uhd"]:
        c["url"] = fhd_sport5
        backups = [b for b in c.get("backups", []) if b != fhd_sport5]
        if hevc_sport5 not in backups:
            backups.insert(0, hevc_sport5)
        c["backups"] = backups

with open("channels.json", "w", encoding="utf-8") as f:
    json.dump(channels, f, ensure_ascii=False, indent=2)

with open("channels.js", "w", encoding="utf-8") as f:
    f.write("window.CHANNELS_DATA = " + json.dumps(channels, ensure_ascii=False, indent=2) + ";\n")
print("Saved channels.json and channels.js with channel 36 fixed!")

# 3. Enhance app.js
print("Enhancing app.js...")
with open("app.js", "r", encoding="utf-8") as f:
    app = f.read().replace("\r\n", "\n")

# A. candidateUrlsOf: prioritize 4K only if S.quality === 'upscale4k', otherwise FHD first
old_cands_4k = """    // If the browser does not support 4K HEVC in MSE, prioritize FHD H.264 streams so video plays
    if (!isHevcSupported() && /_4k|sport_5_4k|54k/i.test((ch.id || '') + ' ' + (ch.url || '') + ' ' + (ch.name || ''))) {
      var non4k = [], is4k = [];
      for (var k = 0; k < list.length; k++) {
        if (/_4k|54k/i.test(list[k])) is4k.push(list[k]);
        else non4k.push(list[k]);
      }
      return non4k.concat(is4k);
    }"""

new_cands_4k = """    // Prioritize reliable FHD H.264 streams over 4K HEVC unless user explicitly selected 4K upscale mode
    if ((S.quality !== 'upscale4k' || !isHevcSupported()) && /_4k|sport_5_4k|54k/i.test((ch.id || '') + ' ' + (ch.url || '') + ' ' + (ch.name || ''))) {
      var non4k = [], is4k = [];
      for (var k = 0; k < list.length; k++) {
        if (/_4k|54k/i.test(list[k])) is4k.push(list[k]);
        else non4k.push(list[k]);
      }
      return non4k.concat(is4k);
    }"""

if old_cands_4k in app:
    app = app.replace(old_cands_4k, new_cands_4k)
    print("app.js candidateUrlsOf 4K logic upgraded!")
else:
    print("Could not directly match old_cands_4k in app.js")

# B. Video Dimension Watchdog: run on both playing and timeupdate for immediate 1.5s detection
old_watchdog = """          // Video Dimension Watchdog: if audio plays but video decoder fails (videoWidth == 0), auto switch to backup stream
          if (!state.isRadio && !state.audioOnly && !ch.isRadio && !ch.isVod) {
            if (state.noVideoTimer) clearTimeout(state.noVideoTimer);
            state.noVideoTimer = setTimeout(function () {
              if (state.playing === ch && !v.paused && v.videoWidth === 0 && v.videoHeight === 0) {
                var cands = state.candidates && state.candidates.length ? state.candidates : candidateUrlsOf(ch);
                if ((state.candIdx || 0) + 1 < cands.length) {
                  toast('מעבר אוטומטי לשידור FHD (וידאו)');
                  state.candIdx = (state.candIdx || 0) + 1;
                  loadCandidate(ch, cands[state.candIdx]);
                }
              }
            }, 2500);
          }"""

new_watchdog = """          // Video Dimension Watchdog: if audio plays but video decoder fails (videoWidth == 0), auto switch to backup stream
          if (!state.isRadio && !state.audioOnly && !ch.isRadio && !ch.isVod) {
            if (state.noVideoTimer) clearTimeout(state.noVideoTimer);
            state.noVideoTimer = setTimeout(function () {
              if (state.playing === ch && !v.paused && v.videoWidth === 0 && v.videoHeight === 0) {
                var cands = state.candidates && state.candidates.length ? state.candidates : candidateUrlsOf(ch);
                if ((state.candIdx || 0) + 1 < cands.length) {
                  toast('מעבר אוטומטי לשידור יציב עם תמונה');
                  state.candIdx = (state.candIdx || 0) + 1;
                  loadCandidate(ch, cands[state.candIdx]);
                }
              }
            }, 1500);
          }"""

if old_watchdog in app:
    app = app.replace(old_watchdog, new_watchdog)
    print("app.js watchdog timeout lowered to 1.5s!")

# C. Optimize HLS timeouts for lightning fast failover (<5s) and 5Mbps network bandwidth estimate
old_hls_timeouts = """        manifestLoadingTimeOut: 10000,
        fragLoadingTimeOut: 20000,
        levelLoadingTimeOut: 10000,"""

new_hls_timeouts = """        manifestLoadingTimeOut: 4500,
        fragLoadingTimeOut: 12000,
        levelLoadingTimeOut: 4500,
        abrEwmaDefaultEstimate: 3500000,"""

if old_hls_timeouts in app:
    app = app.replace(old_hls_timeouts, new_hls_timeouts)
    print("app.js HLS timeouts optimized for fast startup & 5Mbps connections!")

# D. Add "Start-Over (התחל תכנית מחדש)" to playerMenu for live broadcast channels with catchup
old_player_menu_catchup = """    if (state.catchup) {
      items.push({ ico: '🔴', label: 'חזרה לשידור חי', act: function () { if (state.playing) loadStream(state.playing); } });
    }
    if (CATCHUP[ch.id] || CATCHUP[(ch.id || '').toLowerCase()]) {
      items.push({ ico: '⏮️', label: 'קאץ\' אפ (שידור חוזר)', act: function () { openCatchupModal(ch); } });
    }"""

new_player_menu_catchup = """    if (state.catchup) {
      items.push({ ico: '🔴', label: 'חזרה לשידור חי', act: function () { if (state.playing) loadStream(state.playing); } });
    }
    if (CATCHUP[ch.id] || CATCHUP[(ch.id || '').toLowerCase()]) {
      var curProg = getCurrentEpgProg(ch);
      if (curProg && curProg[0] < Date.now() / 60000) {
        items.push({
          ico: '⏮️',
          label: 'התחל תכנית מחדש: ' + (curProg[2] || 'התכנית הנוכחית'),
          act: function () { playCatchup(ch, curProg[0], curProg[0] + curProg[1]); }
        });
      }
      items.push({ ico: '⏮️', label: 'קאץ\' אפ ושידורים קודמים', act: function () { openCatchupModal(ch); } });
    }"""

if old_player_menu_catchup in app:
    app = app.replace(old_player_menu_catchup, new_player_menu_catchup)
    print("app.js playerMenu enhanced with Start-Over!")

with open("app.js", "w", encoding="utf-8") as f:
    f.write(app)
print("app.js saved successfully!")

# 4. Enhance tvguide.js for Start-Over on currently airing show
print("Enhancing tvguide.js...")
with open("tvguide.js", "r", encoding="utf-8") as f:
    guide = f.read().replace("\r\n", "\n")

old_guide_click = """    if (canCatchup(ch, p)) { closeGuide(); resetOsd(); api.playCatchup(ch, startOf(p), endOf(p)); return; }
    if (startOf(p) <= m && m < endOf(p)) { closeGuide(); resetOsd(); api.play(ch); }"""

new_guide_click = """    if (canCatchup(ch, p)) { closeGuide(); resetOsd(); api.playCatchup(ch, startOf(p), endOf(p)); return; }
    if (startOf(p) <= m && m < endOf(p)) {
      if (api.catchupHours && api.catchupHours(ch) && (m - startOf(p) > 2)) {
        closeGuide(); resetOsd();
        api.playCatchup(ch, startOf(p), endOf(p));
        api.toast('⏮️ מתחיל את התכנית מחדש: ' + p[2]);
        return;
      }
      closeGuide(); resetOsd(); api.play(ch);
    }"""

if old_guide_click in guide:
    guide = guide.replace(old_guide_click, new_guide_click)
    print("tvguide.js enhanced with Start-Over on click!")

with open("tvguide.js", "w", encoding="utf-8") as f:
    f.write(guide)
print("tvguide.js saved successfully!")
