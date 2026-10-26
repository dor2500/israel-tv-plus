/**
 * ISRAEL TV PLUS - Hebrew live dubbing engine (מנוע דיבוב לעברית)
 *
 * Pipeline:  live audio of the channel  ->  ~4-10 s clips cut at pauses
 *            ->  speech-to-text + translation to Hebrew (Gemini / OpenAI / Whisper in the browser)
 *            ->  Hebrew voice-over (device voice / Google / OpenAI) while the original audio is lowered,
 *                plus Hebrew subtitles.
 *
 * Modes:
 *  - instant: taps the audio of the playing <video> (hls.js only) and speaks as soon as a line is translated
 *             (2-5 s behind, like a live interpreter).
 *  - sync:    a hidden "listener" player (lowest quality) runs at the live edge and the picture you watch runs
 *             `delay` seconds behind it, so every Hebrew line is spoken when its speaker talks on screen.
 */
(function () {
  'use strict';

  const LS_KEY = 'israel_tv_dub';
  const IS_WEBOS = /Web0S|webOS/i.test(navigator.userAgent);
  // TVs: one video decoder (no lip-sync listener) and too little memory for the in-device Whisper model –
  // loading it there made the LG app crash back to its start screen.
  const IS_TV = /Web0S|webOS|Tizen|SMART-TV|AndroidTV/i.test(navigator.userAgent);
  // The in-device engine (Whisper in a WebView) takes ~800 MB of the page's memory while it starts – measured on an
  // Android TV WebView even with the tiny model. TVs / small devices can't spare that (the LG app crashed in 3.0.0):
  // there only the cloud engines run. Phones / computers with more memory use the tiny model in the app.
  const NO_LOCAL = IS_TV || (navigator.deviceMemory || 8) <= 3;
  const LITE = /IsraelTVPlus\/\d+ AndroidPhone/.test(navigator.userAgent) || (navigator.deviceMemory || 8) <= 4;
  const LITE_MODEL = 'onnx-community/whisper-tiny';

  const DEFAULTS = {
    enabled: false,          // dubbing switched on (applies to foreign channels)
    engine: 'gemini',        // 'gemini' | 'openai' | 'local'
    geminiKey: '',
    geminiModel: 'gemini-flash-latest',
    openaiKey: '',
    openaiSttModel: 'gpt-4o-mini-transcribe',
    openaiTextModel: 'gpt-4o-mini',
    openaiTtsModel: 'gpt-4o-mini-tts',
    openaiVoice: 'alloy',
    localModel: 'onnx-community/whisper-base',
    tts: 'auto',             // 'auto' | 'browser' | 'google' | 'openai' | 'none'
    voiceName: '',           // preferred device voice ('' = automatic by speaker gender)
    rate: 1.1,
    duck: 0.2,               // level of the original sound while the Hebrew voice speaks (0..1)
    subtitles: true,
    showOriginal: false,
    sync: !IS_TV,            // lip-sync mode (picture delayed); TVs usually have a single video decoder
    delay: 12
  };

  const LANG_NAMES = {
    en: 'אנגלית', ru: 'רוסית', fr: 'צרפתית', ar: 'ערבית', uk: 'אוקראינית', es: 'ספרדית',
    de: 'גרמנית', sk: 'סלובקית', it: 'איטלקית', pt: 'פורטוגזית', tr: 'טורקית', pl: 'פולנית', nl: 'הולנדית',
    el: 'יוונית', ro: 'רומנית', hu: 'הונגרית', cs: 'צ\'כית', fa: 'פרסית', hi: 'הינדי', zh: 'סינית', ja: 'יפנית',
    ko: 'קוריאנית', sv: 'שוודית', bg: 'בולגרית', sr: 'סרבית', hr: 'קרואטית', am: 'אמהרית', auto: 'שפה זרה'
  };
  const WHISPER_LANG = {
    en: 'english', ru: 'russian', fr: 'french', ar: 'arabic', uk: 'ukrainian', es: 'spanish', de: 'german',
    it: 'italian', pt: 'portuguese', tr: 'turkish', pl: 'polish', nl: 'dutch', el: 'greek', ro: 'romanian',
    hu: 'hungarian', cs: 'czech', fa: 'persian', hi: 'hindi', zh: 'chinese', ja: 'japanese', ko: 'korean'
  };
  const NO_DUB = { he: 1, music: 1 };

  // Typical Whisper hallucinations on music / silence.
  const HALLUCINATION = /(субтитр|продолжение следует|редактор субтитров|thank(s| you) for watching|amara\.org|подпишись|sous-titr|اشترك)/i;

  const TARGET_SR = 16000;
  const FRAME = 320;            // 20 ms @ 16 kHz
  const MIN_SEG = 4.0;          // s
  const MAX_SEG = 10.0;         // s
  const MAX_BATCH = 20.0;       // s of audio per request when requests pile up

  let settings = load();
  let ui = { container: null, onStatus: null, toast: null };

  // Runtime
  let ac = null;                // AudioContext
  let mainTap = null;           // { src, gain } for the main <video> (created once, lives forever)
  let session = null;           // current dubbing session
  let sessionSeq = 0;
  let subEl = null, subHe = null, subSrc = null, subTimer = null;
  let geminiNoThinking = false;
  let localWorker = null, localReady = null, localJobs = {}, localJobId = 0;
  const debugLog = [];        // last translated lines (HebrewDub.log), handy for troubleshooting

  // ---------------------------------------------------------------- settings
  function load() {
    try {
      const saved = JSON.parse(localStorage.getItem(LS_KEY) || '{}');
      const s = Object.assign({}, DEFAULTS, saved);
      // no key = the free in-device engine (works without any account); a key switches to Gemini automatically
      if (s.engine === 'gemini' && !s.geminiKey && !NO_LOCAL) s.engine = 'local';
      if (s.engine === 'local' && NO_LOCAL) s.engine = 'gemini';
      if (IS_TV) s.sync = false;
      s.migratedFromLocal = false;
      return s;
    } catch (e) {
      return Object.assign({}, DEFAULTS);
    }
  }

  function save(patch) {
    // typing a Gemini key while on the keyless engine: use the key (better translation)
    if (patch && patch.geminiKey && !patch.engine && settings.engine === 'local') patch.engine = 'gemini';
    Object.assign(settings, patch || {});
    try { localStorage.setItem(LS_KEY, JSON.stringify(settings)); } catch (e) {}
  }

  function isForeign(ch) {
    return !!ch && !NO_DUB[ch.lang || 'he'];
  }

  function langName(code) {
    return LANG_NAMES[code] || LANG_NAMES.auto;
  }

  /** Returns '' when the selected engine can run, otherwise a Hebrew explanation. */
  function missingConfig() {
    if (settings.engine === 'gemini' && !settings.geminiKey) return 'חסר מפתח Gemini API (חינמי) – הזן אותו בהגדרות הדיבוב';
    if (settings.engine === 'openai' && !settings.openaiKey) return 'חסר מפתח OpenAI API – הזן אותו בהגדרות הדיבוב';
    if (settings.engine === 'local' && NO_LOCAL) return 'במכשיר הזה אין מספיק זיכרון לזיהוי דיבור מקומי – הדיבוב עובד עם מפתח Gemini חינמי';
    if (settings.engine === 'local' && !('Worker' in window)) return 'הדפדפן אינו תומך במנוע המקומי';
    return '';
  }

  function hlsAvailable() {
    return !!(window.Hls && window.Hls.isSupported());
  }

  function wantsSync(ch) {
    return !IS_TV && settings.enabled && settings.sync && isForeign(ch) && hlsAvailable() && !missingConfig();
  }

  /** Called from hls.js LEVEL_LOADED of the main player: delays the picture in lip-sync mode. */
  function adjustMainHls(hls, details, ch) {
    if (!wantsSync(ch) || !details || !details.live) return;
    const td = details.targetduration || 6;
    const target = td * 3 + settings.delay;
    hls.config.liveSyncDuration = target;
    hls.config.liveMaxLatencyDuration = target + 30;
    // hls.js already picked its live start position; restart loading `delay` seconds further back.
    const first = details.fragments && details.fragments[0] ? details.fragments[0].start : 0;
    const pos = Math.max(first + 0.5, details.edge - target);
    hls.stopLoad();
    hls.startLoad(pos);
  }

  // ---------------------------------------------------------------- status / subtitles
  function status(state, text) {
    if (session) { session.state = state; session.statusText = text || ''; }
    if (ui.onStatus) ui.onStatus(state, text || '');
  }

  function ensureSubtitleEl() {
    if (subEl || !ui.container) return;
    subEl = document.createElement('div');
    subEl.className = 'dub-subtitles hidden';
    subEl.innerHTML = '<div class="dub-sub-src" dir="auto"></div><div class="dub-sub-he" dir="rtl"></div>';
    subSrc = subEl.firstChild;
    subHe = subEl.lastChild;
    ui.container.appendChild(subEl);
  }

  function showSubtitle(he, src) {
    if (!settings.subtitles) return;
    ensureSubtitleEl();
    if (!subEl) return;
    clearTimeout(subTimer);
    subHe.textContent = he;
    subSrc.textContent = settings.showOriginal ? (src || '') : '';
    subSrc.style.display = settings.showOriginal && src ? '' : 'none';
    subEl.classList.remove('hidden');
  }

  function hideSubtitle(after) {
    clearTimeout(subTimer);
    subTimer = setTimeout(() => { if (subEl) subEl.classList.add('hidden'); }, after || 0);
  }

  // ---------------------------------------------------------------- audio graph
  function ensureCtx() {
    if (!ac) {
      const AC = window.AudioContext || window.webkitAudioContext;
      ac = new AC();
    }
    if (ac.state === 'suspended') ac.resume().catch(() => {});
    return ac;
  }

  /**
   * TVs: the audio is taken from the stream fragments hls.js appends (decoded in software by the Web Audio API),
   * not from the <video> element – LG/Samsung play video through a hardware pipeline the page can't hear, and they
   * have one decoder (no second "listener" player). Fragments are appended seconds before they are shown, so every
   * line is translated in advance and spoken when the picture reaches it (lip-sync for free).
   */
  function attachFragmentCapture(sess) {
    const H = window.Hls;
    const hls = sess.mainHls;
    const init = {};
    let chain = Promise.resolve();
    const onInit = (e, d) => { for (const k in d.tracks) if (d.tracks[k].initSegment) init[k] = d.tracks[k].initSegment; };
    const onAppend = (e, d) => {
      if (!sess.alive || (d.type !== 'audio' && d.type !== 'audiovideo') || !init[d.type] || !d.data) return;
      const head = init[d.type];
      const bytes = new Uint8Array(head.length + d.data.length);
      bytes.set(head);
      bytes.set(d.data, head.length);
      const start = d.frag && typeof d.frag.start === 'number' ? d.frag.start + (d.part ? d.part.start - d.frag.start : 0) : null;
      chain = chain.then(() => ac.decodeAudioData(bytes.buffer)).then((ab) => {
        if (!sess.alive) return;
        sess.fragPush(downmix16k(ab), start);
      }).catch(() => {});
    };
    hls.on(H.Events.FRAG_PARSING_INIT_SEGMENT, onInit);
    hls.on(H.Events.BUFFER_APPENDING, onAppend);
    sess.detachFrag = () => {
      try { hls.off(H.Events.FRAG_PARSING_INIT_SEGMENT, onInit); hls.off(H.Events.BUFFER_APPENDING, onAppend); } catch (e) {}
    };
  }

  function downmix16k(ab) {
    const a = ab.getChannelData(0);
    const b = ab.numberOfChannels > 1 ? ab.getChannelData(1) : a;
    const ratio = ab.sampleRate / TARGET_SR;
    const out = new Float32Array(Math.floor(a.length / ratio));
    for (let n = 0; n < out.length; n++) {
      const i0 = Math.floor(n * ratio), i1 = Math.min(a.length, Math.floor((n + 1) * ratio));
      let s = 0;
      for (let i = i0; i < i1; i++) s += a[i] + b[i];
      out[n] = i1 > i0 ? s / (2 * (i1 - i0)) : 0;
    }
    return out;
  }

  function setOriginalLevel(level) {
    if (!session) return;
    if (session.mode === 'instant' && mainTap) {
      mainTap.gain.gain.setTargetAtTime(level, ac.currentTime, 0.12);
    } else if (session.video) {
      session.video.volume = Math.max(0, Math.min(1, level));
    }
  }

  /** Capture node: downmix + resample to 16 kHz mono, then feed the segmenter. */
  function makeCaptureNode(onSamples) {
    const proc = ac.createScriptProcessor(4096, 2, 1);
    const ratio = ac.sampleRate / TARGET_SR;
    let carry = 0;
    proc.onaudioprocess = (e) => {
      const ib = e.inputBuffer;
      const a = ib.getChannelData(0);
      const b = ib.numberOfChannels > 1 ? ib.getChannelData(1) : a;
      const out = new Float32Array(Math.floor((a.length - carry) / ratio) + 1);
      let n = 0;
      let pos = carry;
      while (pos < a.length) {
        const i0 = Math.floor(pos);
        const i1 = Math.min(a.length, Math.floor(pos + ratio));
        let s = 0;
        for (let i = i0; i < i1; i++) s += a[i] + b[i];
        out[n++] = i1 > i0 ? s / (2 * (i1 - i0)) : 0;
        pos += ratio;
      }
      carry = pos - a.length;
      onSamples(out.subarray(0, n));
      // ScriptProcessor must output something; keep it silent.
      e.outputBuffer.getChannelData(0).fill(0);
    };
    const mute = ac.createGain();
    mute.gain.value = 0;
    proc.connect(mute);
    mute.connect(ac.destination);
    return proc;
  }

  // ---------------------------------------------------------------- segmenter
  function makeSegmenter(sess) {
    let buf = new Float32Array(TARGET_SR * 16);
    let len = 0;
    let rms = [];          // per 20 ms frame
    let pending = 0, pendingSum = 0;
    let startWall = 0;
    let startMedia = null;   // media time (video.currentTime scale) of buf[0], when the audio comes from fragments
    const floorWin = [];

    function noiseFloor() {
      if (floorWin.length < 50) return 0.004;
      const s = floorWin.slice().sort((x, y) => x - y);
      return Math.max(0.002, s[Math.floor(s.length * 0.15)]);
    }

    function cut(at) {
      const seg = buf.slice(0, at);
      const segFrames = rms.slice(0, Math.floor(at / FRAME));
      const loud = segFrames.filter(v => v > noiseFloor() * 2.2).length;
      const startedAt = startWall;
      const mediaAt = startMedia;
      buf.copyWithin(0, at, len);
      len -= at;
      rms = rms.slice(Math.floor(at / FRAME));
      startWall = startedAt + at / TARGET_SR * 1000;
      if (startMedia != null) startMedia += at / TARGET_SR;
      // Skip silence: need at least ~0.6 s of sound clearly above the floor.
      if (loud * FRAME / TARGET_SR < 0.6) return;
      sess.onSegment({ audio: seg, wall: startedAt, dur: seg.length / TARGET_SR, sourceLatency: sess.listenerLatency(),
        media: mediaAt != null ? mediaAt : sess.listenerMediaTime(startedAt) });
    }

    return function push(samples, mediaStart) {
      if (!sess.alive) return;
      if (mediaStart != null) {
        // a jump in the timeline (level switch, gap): start a new phrase there
        if (len && Math.abs(mediaStart - (startMedia + len / TARGET_SR)) > 1.5) { len = 0; rms = []; pending = 0; pendingSum = 0; }
        if (len === 0) startMedia = mediaStart;
      }
      if (len === 0) startWall = Date.now() - samples.length / TARGET_SR * 1000;
      if (len + samples.length > buf.length) {
        const nb = new Float32Array((len + samples.length) * 2);
        nb.set(buf.subarray(0, len));
        buf = nb;
      }
      buf.set(samples, len);
      // frame energies
      for (let i = 0; i < samples.length; i++) {
        pendingSum += samples[i] * samples[i];
        if (++pending === FRAME) {
          const v = Math.sqrt(pendingSum / FRAME);
          rms.push(v);
          floorWin.push(v);
          if (floorWin.length > 500) floorWin.shift();
          pending = 0; pendingSum = 0;
        }
      }
      len += samples.length;

      const secs = len / TARGET_SR;
      if (secs < MIN_SEG) return;
      const floor = noiseFloor();
      // End of a phrase: the last 0.35 s are quiet.
      const tail = rms.slice(-18);
      if (tail.length === 18 && tail.every(v => v < floor * 1.8)) {
        cut(len);
        return;
      }
      if (secs >= MAX_SEG) {
        // Cut at the quietest 100 ms in the last 3 s.
        const from = Math.max(0, rms.length - 150);
        let best = rms.length - 1, bestVal = Infinity;
        for (let i = from; i < rms.length - 5; i++) {
          const v = rms[i] + rms[i + 1] + rms[i + 2] + rms[i + 3] + rms[i + 4];
          if (v < bestVal) { bestVal = v; best = i + 2; }
        }
        cut(Math.min(len, best * FRAME));
      }
    };
  }

  // ---------------------------------------------------------------- helpers
  function wavBase64(samples) {
    const bytes = new Uint8Array(44 + samples.length * 2);
    const dv = new DataView(bytes.buffer);
    const str = (o, s) => { for (let i = 0; i < s.length; i++) bytes[o + i] = s.charCodeAt(i); };
    str(0, 'RIFF'); dv.setUint32(4, 36 + samples.length * 2, true); str(8, 'WAVE');
    str(12, 'fmt '); dv.setUint32(16, 16, true); dv.setUint16(20, 1, true); dv.setUint16(22, 1, true);
    dv.setUint32(24, TARGET_SR, true); dv.setUint32(28, TARGET_SR * 2, true); dv.setUint16(32, 2, true); dv.setUint16(34, 16, true);
    str(36, 'data'); dv.setUint32(40, samples.length * 2, true);
    // normalise quiet broadcasts a little
    let peak = 0;
    for (let i = 0; i < samples.length; i++) peak = Math.max(peak, Math.abs(samples[i]));
    const gain = peak > 0 ? Math.min(4, 0.9 / peak) : 1;
    for (let i = 0; i < samples.length; i++) {
      const v = Math.max(-1, Math.min(1, samples[i] * gain));
      dv.setInt16(44 + i * 2, v < 0 ? v * 0x8000 : v * 0x7fff, true);
    }
    let bin = '';
    for (let i = 0; i < bytes.length; i += 0x8000) bin += String.fromCharCode.apply(null, bytes.subarray(i, i + 0x8000));
    return { b64: btoa(bin), bytes: bytes };
  }

  function concatAudio(list) {
    const total = list.reduce((s, x) => s + x.audio.length, 0);
    const out = new Float32Array(total);
    let o = 0;
    list.forEach(x => { out.set(x.audio, o); o += x.audio.length; });
    return out;
  }

  function cleanHebrew(t) {
    return (t || '').replace(/[֑-ׇ]/g, '').replace(/^["'\s-]+|["'\s]+$/g, '').trim();
  }

  async function httpError(res) {
    let msg = res.status + '';
    try {
      const j = await res.json();
      msg += ' ' + ((j.error && (j.error.message || j.error.status)) || '');
    } catch (e) {}
    const err = new Error(msg.trim());
    err.status = res.status;
    return err;
  }

  // ---------------------------------------------------------------- engines
  const SYSTEM_PROMPT =
    'You are a professional live TV interpreter dubbing a foreign broadcast into Hebrew for Israeli viewers. ' +
    'You get a short audio clip cut from a live broadcast; it can start or end in the middle of a sentence. ' +
    'Transcribe the speech and translate it into natural, fluent, spoken Israeli Hebrew that will be read aloud as a voice-over. ' +
    'Rules: keep the Hebrew about as long as the original or shorter so it fits the timing; no niqqud, no quotes, no notes; ' +
    'keep names and brands; do not repeat what is already in the previous lines (clips may overlap a word). ' +
    'If there is no clear speech (music, singing, applause, noise, silence) return empty strings. ' +
    'If the speech is already in Hebrew return "hebrew" empty. ' +
    'speaker = gender of the main speaker in this clip: "male", "female" or "unknown".';

  function contextLines(sess) {
    return sess.history.slice(-3).map(h => '- ' + h.src + ' => ' + h.he).join('\n');
  }

  async function runGemini(sess, audio) {
    const wav = wavBase64(audio);
    const ctxLines = contextLines(sess);
    const prompt = 'Channel: ' + sess.channel.name + ' (expected language: ' + (sess.channel.lang || 'auto') + ').\n' +
      (ctxLines ? 'Previous lines (source => hebrew):\n' + ctxLines + '\n' : '') +
      'Dub this clip into Hebrew.';
    const gen = {
      responseMimeType: 'application/json',
      responseSchema: {
        type: 'OBJECT',
        properties: {
          lang: { type: 'STRING' },
          source: { type: 'STRING' },
          hebrew: { type: 'STRING' },
          speaker: { type: 'STRING', enum: ['male', 'female', 'unknown'] }
        },
        required: ['lang', 'source', 'hebrew', 'speaker']
      }
    };
    if (!geminiNoThinking) gen.thinkingConfig = { thinkingLevel: 'minimal' };
    const body = {
      systemInstruction: { parts: [{ text: SYSTEM_PROMPT }] },
      contents: [{ role: 'user', parts: [{ inlineData: { mimeType: 'audio/wav', data: wav.b64 } }, { text: prompt }] }],
      generationConfig: gen
    };
    const url = 'https://generativelanguage.googleapis.com/v1beta/models/' + encodeURIComponent(settings.geminiModel) + ':generateContent';
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'x-goog-api-key': settings.geminiKey },
      body: JSON.stringify(body),
      signal: sess.abort.signal
    });
    if (!res.ok) {
      const err = await httpError(res);
      if (res.status === 400 && !geminiNoThinking && /think/i.test(err.message)) {
        geminiNoThinking = true;
        return runGemini(sess, audio);
      }
      throw err;
    }
    const j = await res.json();
    const parts = (((j.candidates || [])[0] || {}).content || {}).parts || [];
    const text = parts.filter(p => !p.thought).map(p => p.text || '').join('');
    let out = {};
    try { out = JSON.parse(text); } catch (e) { out = { hebrew: text, source: '' }; }
    return { he: cleanHebrew(out.hebrew), src: (out.source || '').trim(), gender: out.speaker || 'unknown' };
  }

  async function runOpenAI(sess, audio) {
    const wav = wavBase64(audio);
    const fd = new FormData();
    fd.append('file', new Blob([wav.bytes], { type: 'audio/wav' }), 'clip.wav');
    fd.append('model', settings.openaiSttModel);
    const lang = sess.channel.lang;
    if (WHISPER_LANG[lang]) fd.append('language', lang);
    const prev = sess.history.slice(-2).map(h => h.src).join(' ');
    if (prev) fd.append('prompt', prev.slice(-400));
    const headers = { Authorization: 'Bearer ' + settings.openaiKey };
    let res = await fetch('https://api.openai.com/v1/audio/transcriptions', { method: 'POST', headers, body: fd, signal: sess.abort.signal });
    if (!res.ok) throw await httpError(res);
    const src = ((await res.json()).text || '').trim();
    if (!src || HALLUCINATION.test(src)) return { he: '', src: '' };

    const ctxLines = contextLines(sess);
    res = await fetch('https://api.openai.com/v1/chat/completions', {
      method: 'POST',
      headers: Object.assign({ 'Content-Type': 'application/json' }, headers),
      signal: sess.abort.signal,
      body: JSON.stringify({
        model: settings.openaiTextModel,
        response_format: { type: 'json_object' },
        messages: [
          { role: 'system', content: SYSTEM_PROMPT + ' You get the transcript instead of audio. Answer as JSON: {"hebrew": "...", "speaker": "male|female|unknown"} (guess the speaker from grammar, else unknown).' },
          { role: 'user', content: (ctxLines ? 'Previous lines:\n' + ctxLines + '\n' : '') + 'Transcript: ' + src }
        ]
      })
    });
    if (!res.ok) throw await httpError(res);
    let out = {};
    try { out = JSON.parse((await res.json()).choices[0].message.content); } catch (e) {}
    return { he: cleanHebrew(out.hebrew), src: src, gender: out.speaker || 'unknown' };
  }

  function localEngine() {
    if (localReady) return localReady;
    localReady = new Promise((resolve, reject) => {
      try {
        localWorker = new Worker('dub-local-worker.js', { type: 'module' });
      } catch (e) {
        reject(e);
        return;
      }
      localWorker.onmessage = (e) => {
        const m = e.data;
        if (m.type === 'progress') {
          status('loading', m.pct >= 100 ? 'מאתחל את מנוע זיהוי הדיבור…' : 'מוריד מודל זיהוי דיבור למכשיר (פעם אחת)… ' + m.pct + '%');
        } else if (m.type === 'ready') {
          resolve();
        } else if (m.type === 'error' && m.id == null) {
          reject(new Error(m.message));
        } else if (m.id != null && localJobs[m.id]) {
          const job = localJobs[m.id];
          delete localJobs[m.id];
          if (m.type === 'error') job.reject(new Error(m.message));
          else job.resolve(m.text);
        }
      };
      localWorker.onerror = (e) => reject(new Error(e.message || 'worker error'));
      localWorker.postMessage({ type: 'load', model: LITE ? LITE_MODEL : settings.localModel, lowMem: LITE || (navigator.deviceMemory || 8) <= 4, lite: LITE });
    });
    localReady.catch(() => { localReady = null; });
    return localReady;
  }

  async function translateFree(text, sl) {
    const url = 'https://translate.googleapis.com/translate_a/single?client=gtx&dt=t&tl=iw&sl=' +
      encodeURIComponent(sl && sl !== 'auto' ? sl : 'auto') + '&q=' + encodeURIComponent(text);
    try {
      const res = await fetch(url);
      if (res.ok) {
        const j = await res.json();
        return (j[0] || []).map(x => x[0]).join('');
      }
    } catch (e) {}
    if (!sl || sl === 'auto') throw new Error('תרגום נכשל');
    const res = await fetch('https://api.mymemory.translated.net/get?langpair=' + sl + '|he&q=' + encodeURIComponent(text));
    const j = await res.json();
    return (j.responseData && j.responseData.translatedText) || '';
  }

  async function runLocal(sess, audio) {
    await localEngine();
    const id = ++localJobId;
    const text = await new Promise((resolve, reject) => {
      localJobs[id] = { resolve, reject };
      localWorker.postMessage({ type: 'run', id, audio, language: WHISPER_LANG[sess.channel.lang] || null }, [audio.buffer]);
    });
    const src = (text || '').trim();
    if (!src || src.length < 2 || HALLUCINATION.test(src) || /^[\s.…♪*\[\]()-]+$/.test(src)) return { he: '', src: '' };
    const he = await translateFree(src, sess.channel.lang);
    return { he: cleanHebrew(he), src: src, gender: 'unknown' };
  }

  function runEngine(sess, audio) {
    if (settings.engine === 'openai') return runOpenAI(sess, audio);
    if (settings.engine === 'local') return runLocal(sess, audio);
    return runGemini(sess, audio);
  }

  // ---------------------------------------------------------------- voices
  let voicesCache = [];

  function hebrewVoices() {
    if (!('speechSynthesis' in window)) return [];
    const all = speechSynthesis.getVoices();
    if (all.length) voicesCache = all;
    return voicesCache.filter(v => /^(he|iw)([-_]|$)/i.test(v.lang))
      .sort((a, b) => score(b) - score(a));
    function score(v) { return (/natural|online|neural/i.test(v.name) ? 2 : 0) + (v.localService ? 0 : 1); }
  }

  if ('speechSynthesis' in window) {
    speechSynthesis.getVoices();
    speechSynthesis.addEventListener && speechSynthesis.addEventListener('voiceschanged', hebrewVoices);
  }

  function voiceGender(v) {
    if (/hila|carmit|female|zira|אישה/i.test(v.name)) return 'female';
    if (/asaf|avri|male|guy/i.test(v.name)) return 'male';
    return 'unknown';
  }

  function pickVoice(gender) {
    const list = hebrewVoices();
    if (!list.length) return null;
    if (settings.voiceName) {
      const v = list.find(x => x.name === settings.voiceName);
      if (v) return v;
    }
    if (gender === 'male' || gender === 'female') {
      const v = list.find(x => voiceGender(x) === gender);
      if (v) return v;
    }
    return list[0];
  }

  function ttsMode() {
    let t = settings.tts;
    if (t === 'openai' && !settings.openaiKey) t = 'auto';
    if (t === 'auto') t = hebrewVoices().length ? 'browser' : 'google';
    if (t === 'browser' && !hebrewVoices().length) t = 'google';
    return t;
  }

  function splitForGoogle(text) {
    const parts = [];
    let rest = text;
    while (rest.length > 180) {
      let at = Math.max(rest.lastIndexOf('. ', 180), rest.lastIndexOf(', ', 180), rest.lastIndexOf(' ', 180));
      if (at < 40) at = 180;
      parts.push(rest.slice(0, at + 1).trim());
      rest = rest.slice(at + 1);
    }
    if (rest.trim()) parts.push(rest.trim());
    return parts;
  }

  // Google's TTS answers 404 when a Referer is sent, and <audio> has no referrerPolicy attribute,
  // so the page's referrer policy is switched off only while such a clip loads.
  let noRefCount = 0;
  function suppressReferrer() {
    let m = document.querySelector('meta[name="referrer"]');
    if (!m) {
      m = document.createElement('meta');
      m.name = 'referrer';
      m.dataset.dub = '1';
      document.head.appendChild(m);
    }
    noRefCount++;
    m.content = 'no-referrer';
    let restored = false;
    return () => {
      if (restored) return;
      restored = true;
      if (--noRefCount === 0 && m.dataset.dub) m.remove();
    };
  }

  function playAudioUrl(sess, url, rate) {
    return new Promise((resolve) => {
      const restore = /^https:\/\/translate\.google/.test(url) ? suppressReferrer() : () => {};
      const a = new Audio();
      a.preload = 'auto';
      a.src = url;
      a.defaultPlaybackRate = a.playbackRate = rate;
      a.preservesPitch = true;
      sess.currentAudio = a;
      const loadTimer = setTimeout(restore, 4000);
      a.addEventListener('canplaythrough', () => { clearTimeout(loadTimer); restore(); }, { once: true });
      const done = () => { a.onended = a.onerror = null; clearTimeout(loadTimer); restore(); resolve(); };
      a.onended = done;
      a.onerror = done;
      a.play().catch(done);
    });
  }

  async function speak(sess, item, rate) {
    const mode = ttsMode();
    if (mode === 'none') {
      await new Promise(r => setTimeout(r, Math.max(1500, item.he.length * 70)));
      return;
    }
    if (mode === 'browser') {
      await new Promise((resolve) => {
        const u = new SpeechSynthesisUtterance(item.he);
        const v = pickVoice(item.gender);
        if (v) u.voice = v;
        u.lang = (v && v.lang) || 'he-IL';
        u.rate = rate;
        u.onend = u.onerror = () => resolve();
        sess.utterance = u;
        speechSynthesis.cancel();
        speechSynthesis.speak(u);
        // Safety net: some engines never fire onend.
        setTimeout(resolve, 2500 + item.he.length * 160 / rate);
      });
      return;
    }
    if (mode === 'openai') {
      try {
        const voice = item.gender === 'female' && settings.openaiVoice === 'alloy' ? 'nova' : settings.openaiVoice;
        const res = await fetch('https://api.openai.com/v1/audio/speech', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', Authorization: 'Bearer ' + settings.openaiKey },
          signal: sess.abort.signal,
          body: JSON.stringify({
            model: settings.openaiTtsModel, voice: voice, input: item.he, response_format: 'mp3',
            instructions: 'Speak fluent native Israeli Hebrew, like a lively professional TV dubbing artist.'
          })
        });
        if (res.ok) {
          const url = URL.createObjectURL(await res.blob());
          await playAudioUrl(sess, url, rate);
          URL.revokeObjectURL(url);
          return;
        }
      } catch (e) {
        if (!sess.alive) return;
      }
      // fall through to the free voice
    }
    for (const part of splitForGoogle(item.he)) {
      if (!sess.alive) return;
      await playAudioUrl(sess, 'https://translate.google.com/translate_tts?ie=UTF-8&client=tw-ob&tl=iw&q=' + encodeURIComponent(part), rate);
    }
  }

  // ---------------------------------------------------------------- session
  function makeSession(opts) {
    const sess = {
      id: ++sessionSeq,
      alive: true,
      mode: IS_TV ? 'frag' : settings.sync && hlsAvailable() ? 'sync' : 'instant',
      channel: opts.channel,
      url: opts.url,
      video: opts.video,
      mainHls: opts.hls,
      listenerHls: null,
      listenerEl: null,
      capture: null,
      srcNode: null,
      pending: [],          // segments waiting for the engine
      busy: false,
      speakQueue: [],
      speaking: false,
      history: [],
      abort: new AbortController(),
      errors: 0,
      ticker: null,
      state: 'loading',
      statusText: ''
    };
    sess.listenerLatency = () => {
      if (sess.mode !== 'sync' || !sess.listenerHls) return 0;
      return sess.listenerHls.latency || 0;
    };
    // Listener media time at the given wall-clock moment (null outside lip-sync mode).
    sess.listenerMediaTime = (wall) => {
      if (sess.mode !== 'sync' || !sess.listenerEl) return null;
      return sess.listenerEl.currentTime - (Date.now() - wall) / 1000;
    };
    sess.onSegment = (seg) => {
      if (!sess.alive) return;
      sess.pending.push(seg);
      // Instant mode must stay close to live: drop old audio when the engine can't keep up.
      const maxPending = sess.mode === 'instant' ? MAX_BATCH : MAX_BATCH * 2;
      while (sess.pending.reduce((s, x) => s + x.dur, 0) > maxPending) sess.pending.shift();
      pump(sess);
    };
    return sess;
  }

  async function pump(sess) {
    if (sess.busy || !sess.pending.length || !sess.alive) return;
    // Batch everything that is waiting (keeps the number of requests low on free tiers).
    const batch = [];
    let dur = 0;
    while (sess.pending.length && (dur === 0 || dur + sess.pending[0].dur <= MAX_BATCH)) {
      const s = sess.pending.shift();
      batch.push(s);
      dur += s.dur;
    }
    sess.busy = true;
    if (!sess.speaking) status('translating', 'מתרגם לעברית…');
    try {
      const res = await runEngine(sess, concatAudio(batch));
      if (!sess.alive) return;
      sess.errors = 0;
      debugLog.push({ t: new Date().toISOString().slice(11, 19), dur: +dur.toFixed(1), src: res.src, he: res.he });
      if (debugLog.length > 50) debugLog.shift();
      if (res.he) {
        sess.history.push({ src: res.src || '', he: res.he });
        if (sess.history.length > 6) sess.history.shift();
        sess.speakQueue.push({ he: res.he, src: res.src, gender: res.gender, wall: batch[0].wall, dur: dur, latency: batch[0].sourceLatency, media: batch[0].media });
        tick(sess);
      } else if (!sess.speaking) {
        status('listening', 'מאזין לשידור…');
      }
    } catch (e) {
      if (!sess.alive || e.name === 'AbortError') return;
      sess.errors++;
      const quota = e.status === 429;
      const auth = e.status === 401 || e.status === 403 || (e.status === 400 && /key/i.test(e.message));
      status('error', quota ? 'חריגה ממכסת הבקשות – ממשיך בעוד רגע…' : auth ? 'מפתח API שגוי – בדוק בהגדרות הדיבוב' : 'שגיאת תרגום: ' + e.message);
      if (auth) { stopSession(sess); return; }
      await new Promise(r => setTimeout(r, quota ? 15000 : Math.min(10000, 1500 * sess.errors)));
    } finally {
      sess.busy = false;
    }
    if (sess.alive) pump(sess);
  }

  /** Decides when the next Hebrew line is spoken. */
  function tick(sess) {
    if (!sess.alive || sess.speaking || !sess.speakQueue.length) return;
    const item = sess.speakQueue[0];
    let wait = 0;
    if (sess.mode === 'sync' && sess.mainHls) {
      // The line was heard by the listener at item.wall; the picture shows it (mainLatency - listenerLatency) later.
      const offset = Math.max(0, (sess.mainHls.latency || 0) - (item.latency || 0)) * 1000;
      wait = item.wall + offset - Date.now() - 150;
      // Both players normally share one media timeline, which is more precise than hls.js' latency estimate.
      if (item.media != null && sess.listenerEl && sess.video) {
        const ctDiff = sess.listenerEl.currentTime - sess.video.currentTime;
        const latDiff = (sess.mainHls.latency || 0) - (sess.listenerHls ? sess.listenerHls.latency || 0 : 0);
        if (ctDiff > 0 && Math.abs(ctDiff - latDiff) < 3.5) wait = (item.media - sess.video.currentTime) * 1000 - 150;
      }
      if (sess.video && sess.video.paused) wait = Math.max(wait, 250);
    } else if (sess.mode === 'frag' && item.media != null && sess.video) {
      // spoken when the picture reaches the moment the words were said
      wait = (item.media - sess.video.currentTime) * 1000 - 150;
      if (sess.video.paused) wait = Math.max(wait, 250);
      if (wait < -8000) { sess.speakQueue.shift(); tick(sess); return; } // far behind (seek / stall): skip the line
    }
    if (wait > 30) {
      clearTimeout(sess.ticker);
      sess.ticker = setTimeout(() => tick(sess), Math.min(wait, 1000));
      return;
    }
    sess.speakQueue.shift();
    // Faster speech when the line is long for its slot or lines are queuing up.
    const est = item.he.length / 14;                 // seconds at rate 1
    let rate = settings.rate;
    rate = Math.max(rate, Math.min(1.6, est / Math.max(1.5, item.dur * 0.95)));
    if (sess.speakQueue.length) rate = Math.min(1.7, rate + 0.15 * sess.speakQueue.length);
    if (sess.mode !== 'instant' && wait < -2500) rate = Math.min(1.7, rate + 0.2);

    sess.speaking = true;
    if (sess.mode === 'sync' && sess.mainHls) {
      const offset = Math.max(0, (sess.mainHls.latency || 0) - (item.latency || 0));
      item.late = +(item.media != null && sess.video ? sess.video.currentTime - item.media : (Date.now() - item.wall) / 1000 - offset).toFixed(2);
      item.offset = +offset.toFixed(1);
    }
    debugLog.push({ t: new Date().toISOString().slice(11, 19), spoke: item.he.slice(0, 40), late: item.late, offset: item.offset, rate: +rate.toFixed(2) });
    status('speaking', settings.subtitles ? 'מדבב לעברית 🔊' : 'מדבב: ' + item.he);
    showSubtitle(item.he, item.src);
    setOriginalLevel(settings.duck);
    speak(sess, item, rate).then(() => {
      sess.speaking = false;
      if (!sess.alive) return;
      if (!sess.speakQueue.length) {
        setOriginalLevel(1);
        hideSubtitle(900);
        status(sess.busy ? 'translating' : 'listening', sess.busy ? 'מתרגם לעברית…' : 'מאזין לשידור…');
      }
      tick(sess);
    });
  }

  function startListener(sess) {
    return new Promise((resolve, reject) => {
      const v = document.createElement('video');
      v.className = 'dub-listener';
      v.playsInline = true;
      v.setAttribute('playsinline', '');
      v.preload = 'auto';
      document.body.appendChild(v);
      sess.listenerEl = v;
      const hls = new window.Hls({
        enableWorker: true,
        lowLatencyMode: false,
        startLevel: 0,
        capLevelToPlayerSize: true,
        backBufferLength: 10,
        maxBufferLength: 12,
        manifestLoadingMaxRetry: 2,
        levelLoadingMaxRetry: 2
      });
      sess.listenerHls = hls;
      hls.on(window.Hls.Events.MANIFEST_PARSED, (e, data) => {
        hls.autoLevelCapping = 0;
        hls.currentLevel = 0;
        sess.srcNode = ac.createMediaElementSource(v);
        sess.srcNode.connect(sess.capture);
        v.play().then(resolve).catch(reject);
      });
      hls.on(window.Hls.Events.ERROR, (e, data) => {
        if (!data.fatal) return;
        if (data.type === window.Hls.ErrorTypes.MEDIA_ERROR) hls.recoverMediaError();
        else reject(new Error('listener: ' + data.details));
      });
      hls.loadSource(sess.url);
      hls.attachMedia(v);
    });
  }

  async function startSession(opts) {
    stopSession(session);
    const err = missingConfig();
    if (err) {
      if (ui.toast) ui.toast('🎙️ ' + err);
      status('error', err);
      return;
    }
    if (!hlsAvailable() || !opts.hls) {
      status('error', 'דיבוב זמין רק לשידורי HLS בדפדפן תומך');
      return;
    }
    ensureCtx();
    const sess = makeSession(opts);
    session = sess;
    status('loading', sess.mode === 'sync' ? 'מכין דיבוב מסונכרן (התמונה מושהית ב-' + settings.delay + ' שנ׳)…' : 'מכין דיבוב…');
    if (settings.engine === 'local') localEngine().catch(e => status('error', 'טעינת המודל המקומי נכשלה: ' + e.message));
    try {
      if (sess.mode === 'frag') {
        sess.fragPush = makeSegmenter(sess);
        attachFragmentCapture(sess);
        if (sess.alive) status('listening', 'מאזין לשידור…');
        return;
      }
      sess.capture = makeCaptureNode(makeSegmenter(sess));
      if (sess.mode === 'sync') {
        await startListener(sess);
      } else {
        if (!mainTap) {
          const src = ac.createMediaElementSource(opts.video);
          const gain = ac.createGain();
          src.connect(gain);
          gain.connect(ac.destination);
          mainTap = { src, gain, video: opts.video };
        }
        mainTap.src.connect(sess.capture);
      }
      if (sess.alive) status('listening', 'מאזין לשידור…');
    } catch (e) {
      if (sess.alive) {
        status('error', 'לא ניתן ללכוד את שמע הערוץ (' + e.message + ')');
        stopSession(sess);
      }
    }
  }

  function stopSession(sess) {
    if (!sess) return;
    sess.alive = false;
    try { sess.abort.abort(); } catch (e) {}
    clearTimeout(sess.ticker);
    if (sess.utterance && 'speechSynthesis' in window) speechSynthesis.cancel();
    if (sess.currentAudio) { try { sess.currentAudio.pause(); } catch (e) {} }
    if (sess.mode === 'instant' && mainTap) {
      try { mainTap.src.disconnect(sess.capture); } catch (e) {}
      mainTap.gain.gain.setTargetAtTime(1, ac.currentTime, 0.05);
    } else if (sess.video) {
      sess.video.volume = 1;
    }
    if (sess.detachFrag) sess.detachFrag();
    if (sess.capture) { try { sess.capture.disconnect(); } catch (e) {} sess.capture.onaudioprocess = null; }
    if (sess.listenerHls) { try { sess.listenerHls.destroy(); } catch (e) {} }
    if (sess.srcNode) { try { sess.srcNode.disconnect(); } catch (e) {} }
    if (sess.listenerEl) { sess.listenerEl.remove(); }
    hideSubtitle(0);
    if (session === sess) {
      session = null;
      if (ui.onStatus) ui.onStatus('off', '');
    }
  }

  // ---------------------------------------------------------------- public API
  window.HebrewDub = {
    DEFAULTS,
    LANG_NAMES,
    IS_TV,
    NO_LOCAL,
    get settings() { return settings; },
    log: debugLog,
    save,
    init(opts) {
      ui = Object.assign(ui, opts || {});
      ensureSubtitleEl();
    },
    isForeign,
    langName,
    missingConfig,
    wantsSync,
    adjustMainHls,
    hebrewVoices,
    isActive() { return !!session; },
    state() { return session ? session.state : 'off'; },
    /** Called by the app whenever a channel starts playing (after hls.js parsed the manifest). */
    attach(opts) {
      stopSession(session);
      if (settings.enabled && isForeign(opts.channel)) startSession(opts);
    },
    detach() { stopSession(session); },
    /** Turn dubbing on/off for the current channel. Returns the new enabled flag. */
    setEnabled(on, opts) {
      save({ enabled: !!on });
      if (!on) { stopSession(session); return false; }
      if (opts && isForeign(opts.channel)) startSession(opts);
      return true;
    },
    unlockAudio() {
      // Called from a user gesture so the AudioContext and speech engine may start.
      try { ensureCtx(); } catch (e) {}
    },
    testVoice() {
      ensureCtx();
      const fake = { alive: true, abort: new AbortController() };
      const text = 'שלום! זהו קול הדיבוב לעברית של ישראל TV פלוס.';
      showSubtitle(text, 'Hello! This is the Hebrew dubbing voice of Israel TV Plus.');
      return speak(fake, { he: text, gender: 'unknown' }, settings.rate).then(() => hideSubtitle(800));
    },
    ttsMode,
    async listGeminiModels(key) {
      const res = await fetch('https://generativelanguage.googleapis.com/v1beta/models?pageSize=200', { headers: { 'x-goog-api-key': key } });
      if (!res.ok) throw await httpError(res);
      const j = await res.json();
      return (j.models || [])
        .filter(m => (m.supportedGenerationMethods || []).indexOf('generateContent') !== -1 && /flash/i.test(m.name) && !/tts|image|live|audio|embed/i.test(m.name))
        .map(m => m.name.replace(/^models\//, ''));
    }
  };
})();
