// In-browser speech recognition for the free "local" dubbing engine (Whisper via transformers.js).
// The model is downloaded once from Hugging Face and cached by the browser.
import { pipeline, env } from 'https://cdn.jsdelivr.net/npm/@huggingface/transformers@4.3.1';

env.allowLocalModels = false;

let asr = null;

let lite = false;
// TVs / phones: no memory arena or pattern planning and no graph rewriting (each keeps extra copies of the weights)
const LITE_SESSION = { enableCpuMemArena: false, enableMemPattern: false, graphOptimizationLevel: 'basic', executionMode: 'sequential' };

async function load(model, lowMem, isLite) {
  lite = !!isLite;
  if (lite && env.backends && env.backends.onnx && env.backends.onnx.wasm) {
    env.backends.onnx.wasm.numThreads = 1;   // TVs / phones: one thread, small memory footprint
  }
  const files = {};
  const progress = (p) => {
    if (p.status !== 'progress' || !p.total) return;
    files[p.file] = [p.loaded, p.total];
    let loaded = 0, total = 0;
    for (const k in files) { loaded += files[k][0]; total += files[k][1]; }
    self.postMessage({ type: 'progress', pct: Math.round(loaded / total * 100) });
  };
  // WebGPU with an fp32 encoder needs a lot of memory: only on devices that report more than 4 GB
  const webgpu = !lowMem && !!(self.navigator && self.navigator.gpu);
  try {
    asr = await pipeline('automatic-speech-recognition', model, webgpu
      ? { device: 'webgpu', dtype: { encoder_model: 'fp32', decoder_model_merged: 'q4' }, progress_callback: progress }
      : { dtype: 'q8', progress_callback: progress, session_options: lite ? LITE_SESSION : undefined });
  } catch (e) {
    if (!webgpu) throw e;
    asr = await pipeline('automatic-speech-recognition', model, { dtype: 'q8', progress_callback: progress });
  }
}

self.onmessage = async (e) => {
  const m = e.data;
  if (m.type === 'load') {
    try {
      await load(m.model, m.lowMem, m.lite);
      self.postMessage({ type: 'ready' });
    } catch (err) {
      self.postMessage({ type: 'error', message: String(err && err.message || err) });
    }
  } else if (m.type === 'run') {
    try {
      // clips are at most ~20 s: no chunking needed (saves memory on TVs)
      const opts = lite ? { task: 'transcribe' } : { task: 'transcribe', chunk_length_s: 30 };
      if (m.language) opts.language = m.language;
      const out = await asr(m.audio, opts);
      self.postMessage({ type: 'text', id: m.id, text: out.text || '' });
    } catch (err) {
      self.postMessage({ type: 'error', id: m.id, message: String(err && err.message || err) });
    }
  }
};
