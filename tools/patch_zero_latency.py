# tools/patch_zero_latency.py
with open("app.js", "r", encoding="utf-8") as f:
    content = f.read()

target = """    if (window.Hls && Hls.isSupported()) {
      var delayed = Dub && Dub.wantsSync(ch);
      var low = (S.latency === 'low' || S.latency === 'realtime' || TVUI) && !delayed;
      var isStable = (S.latency === 'stable' || S.latency !== 'low');
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
      });
      state.hls = hls;
      hls.loadSource(url);
      hls.attachMedia(v);
      var adjusted = false;
      hls.on(Hls.Events.LEVEL_LOADED, function (e, data) {
        if (data && data.details && (!data.details.fragments || !data.details.fragments.length)) {
          onFatal(ch, 'השידור אינו פעיל בשרת זה');
          return;
        }
        if (state.catchup && state.catchup.ch === ch) {
          if (!adjusted) {
            adjusted = true;
            if (!catchupSeek(hls, data.details)) onFatal(ch, 'התכנית כבר לא זמינה');
          }
          return;
        }
        if (!adjusted && Dub) { adjusted = true; Dub.adjustMainHls(hls, data.details, ch); }
      });"""

replacement = """    if (window.Hls && Hls.isSupported()) {
      var delayed = Dub && Dub.wantsSync(ch);
      var isSport = /sport|ספורט|one_1|one_2|charlton|eurosport/i.test((ch.category || '') + ' ' + (ch.name || '') + ' ' + (ch.id || ''));
      var low = (S.latency === 'low' || S.latency === 'realtime' || TVUI || isSport) && !delayed;
      var isStable = (S.latency === 'stable' && !isSport);
      var hls = new Hls({
        enableWorker: true,
        lowLatencyMode: true,
        backBufferLength: fastMode() ? 10 : 20,
        liveSyncDurationCount: isSport ? 1 : (isStable ? 2 : 1),
        liveMaxLatencyDurationCount: isSport ? 2 : (isStable ? 4 : 2),
        liveDurationInfinity: true,
        maxLiveSyncPlaybackRate: 1.15,
        maxBufferLength: isSport ? 8 : 16,
        maxMaxBufferLength: isSport ? 16 : 30,
        maxBufferSize: 30 * 1000 * 1000,
        manifestLoadingMaxRetry: 5,
        levelLoadingMaxRetry: 5,
        fragLoadingMaxRetry: 6,
        fragLoadingRetryDelay: 500,
        capLevelToPlayerSize: S.quality === 'auto'
      });
      state.hls = hls;
      hls.loadSource(url);
      hls.attachMedia(v);
      var adjusted = false;
      hls.on(Hls.Events.LEVEL_LOADED, function (e, data) {
        if (data && data.details && (!data.details.fragments || !data.details.fragments.length)) {
          onFatal(ch, 'השידור אינו פעיל בשרת זה');
          return;
        }
        if (state.catchup && state.catchup.ch === ch) {
          if (!adjusted) {
            adjusted = true;
            if (!catchupSeek(hls, data.details)) onFatal(ch, 'התכנית כבר לא זמינה');
          }
          return;
        }
        if (!state.catchup && data && data.details && data.details.live) {
          var lsp = hls.liveSyncPosition;
          if (lsp != null && v.currentTime && (lsp - v.currentTime > (isSport ? 14 : 25))) {
            v.currentTime = lsp;
          }
        }
        if (!adjusted && Dub) { adjusted = true; Dub.adjustMainHls(hls, data.details, ch); }
      });"""

# Normalize CRLF / LF for matching
content_norm = content.replace("\r\n", "\n")
target_norm = target.replace("\r\n", "\n")
replacement_norm = replacement.replace("\r\n", "\n")

if target_norm in content_norm:
    content_norm = content_norm.replace(target_norm, replacement_norm, 1)
    with open("app.js", "w", encoding="utf-8", newline="\n") as f:
        f.write(content_norm)
    print("Patched app.js successfully!")
else:
    print("Target not found in app.js")

