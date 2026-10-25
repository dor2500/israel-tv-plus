import json
import urllib.request
import gzip
import time

chs = json.load(open('channels.json', encoding='utf-8'))
il = [c for c in chs if 'ישראל' in c.get('category','') or 'Israel' in c.get('country','') or 'israel' in c.get('id','') or 'HOT' in c['name'] or 'yes' in c['name']]

print(f"Testing {len(il)} Israeli channels...")
working = []
failed = []

for c in il:
    url = c['url']
    name = c['name']
    if not url.startswith('http'):
        failed.append((name, url, "invalid url"))
        continue
    
    # Skip web pages
    if not any(ext in url for ext in ['.m3u8', '.ts', '.mp3', '.aac', '/stream', '/play', ':80', ':8080']) and not url.endswith(('.m3u8', '.mp4')):
        if 'yes.co.il' in url or 'hot.net.il' in url or 'stingtv' in url or 'partner' in url or 'cellcom' in url:
            failed.append((name, url, "web page not video"))
            continue

    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36',
        'Referer': 'https://google.com'
    }
    try:
        req = urllib.request.Request(url, headers=headers)
        with urllib.request.urlopen(req, timeout=4) as res:
            if res.status != 200:
                failed.append((name, url, f"HTTP {res.status}"))
                continue
            data = res.read(1024)
            if b'<html' in data.lower() or b'<!doctype' in data.lower():
                failed.append((name, url, "returns HTML webpage"))
                continue
            working.append((name, url))
    except Exception as e:
        failed.append((name, url, str(e)))

print(f"DONE! Working: {len(working)}, Failed: {len(failed)}")
print("\n--- SAMPLE WORKING (first 20) ---")
for n, u in working[:20]:
    print(f"OK: {n} -> {u}")

print("\n--- SAMPLE FAILED (first 30) ---")
for n, u, err in failed[:30]:
    print(f"FAIL: {n} -> {u} ({err})")
