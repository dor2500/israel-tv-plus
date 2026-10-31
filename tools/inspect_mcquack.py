import urllib.request
import json
import subprocess
import os

active_nums = [20, 26, 27, 28, 29, 30, 33, 34, 36, 37, 38, 39, 42, 44, 45, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 59, 60, 61, 62, 63, 66, 69, 71, 73, 74, 75, 76, 78, 80, 81, 82, 83, 84, 85, 86, 92, 93]

print(f"Checking {len(active_nums)} mcquack channels...")
for n in active_nums:
    u = f"http://stream.mcquack.net/{n}/index.m3u8"
    try:
        # run ffprobe to get service_name or channel title
        cmd = ["ffprobe", "-v", "error", "-show_entries", "program_tags:stream_tags:format_tags", "-of", "json", u]
        res = subprocess.run(cmd, capture_output=True, text=True, timeout=5)
        out = res.stdout
        print(f"[{n}] {u}")
        try:
            data = json.loads(out)
            tags = data.get("format", {}).get("tags", {})
            if tags:
                print(f"    Tags: {tags}")
        except Exception:
            pass
    except Exception as e:
        print(f"[{n}] Err: {e}")

