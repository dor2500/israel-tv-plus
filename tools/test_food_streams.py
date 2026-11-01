import subprocess, json

streams = {
    "haim_hatovim": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2355/index.m3u8",
    "food_channel_il": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12209/index.m3u8",
    "food_channel_plus": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2377/index.m3u8",
    "foody_il": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32154/index.m3u8"
}

for name, u in streams.items():
    cmd = ["ffprobe", "-v", "error", "-select_streams", "v:0", "-show_entries", "stream=width,height,codec_name", "-of", "json", u]
    res = subprocess.run(cmd, capture_output=True, text=True, timeout=10)
    print(f"=== {name} ===")
    print(res.stdout.strip())

