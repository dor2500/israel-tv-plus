import subprocess, os

test_ids = [2330, 2331, 2332, 2333, 2334, 2335, 2336, 2340, 2341, 2344, 2366, 32147, 32149]

for cid in test_ids:
    url = f"http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/{cid}/index.m3u8"
    cmd = ["ffprobe", "-v", "error", "-show_entries", "stream=index,codec_type,tags:format_tags", "-of", "json", "-t", "2", url]
    p = subprocess.run(cmd, capture_output=True, text=True)
    print(f"=== ID {cid} ===")
    print(p.stdout.strip())

