import subprocess, os

url = "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32275/index.m3u8"
out_wav = r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_probes\nick_32275.wav"

cmd = [
    "ffmpeg", "-y", "-t", "6", "-i", url,
    "-vn", "-acodec", "pcm_s16le", "-ar", "16000", "-ac", "1", out_wav
]
subprocess.run(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
print("Saved audio:", os.path.exists(out_wav), os.path.getsize(out_wav) if os.path.exists(out_wav) else 0)

