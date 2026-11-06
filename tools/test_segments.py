import json
import urllib.request
import ssl
import concurrent.futures
from urllib.parse import urljoin

with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

non_radio = [c for c in channels if c.get('kind') != 'radio']
print(f"Testing segments for {len(non_radio)} video channels...")

def test_full_stream(ch):
    url = ch.get('url', '')
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36',
        'Referer': 'https://freetv.tv/'
    }
    try:
        # Step 1: fetch m3u8
        req = urllib.request.Request(url, headers=headers)
        with urllib.request.urlopen(req, timeout=4, context=ctx) as r:
            body = r.read(16384).decode('utf-8', errors='ignore')
        
        # If it's a master playlist, get the first variant
        lines = [line.strip() for line in body.splitlines() if line.strip()]
        seg_url = None
        for i, line in enumerate(lines):
            if line.startswith('#EXT-X-STREAM-INF'):
                if i + 1 < len(lines):
                    variant_url = urljoin(url, lines[i+1])
                    # fetch variant
                    req2 = urllib.request.Request(variant_url, headers=headers)
                    with urllib.request.urlopen(req2, timeout=4, context=ctx) as r2:
                        var_body = r2.read(16384).decode('utf-8', errors='ignore')
                    var_lines = [l.strip() for l in var_body.splitlines() if l.strip()]
                    for vl in var_lines:
                        if not vl.startswith('#'):
                            seg_url = urljoin(variant_url, vl)
                            break
                    break
            elif not line.startswith('#'):
                seg_url = urljoin(url, line)
                break
        
        if not seg_url:
            return False, ch, 'No segment found in playlist'
        
        # Step 2: fetch segment (first 1024 bytes)
        req_seg = urllib.request.Request(seg_url, headers=headers)
        with urllib.request.urlopen(req_seg, timeout=4, context=ctx) as r_seg:
            data = r_seg.read(1024)
            if len(data) > 0:
                return True, ch, 'OK'
            return False, ch, 'Empty segment'
    except Exception as e:
        return False, ch, str(e)

failed = []
working = []
with concurrent.futures.ThreadPoolExecutor(max_workers=30) as executor:
    results = list(executor.map(test_full_stream, non_radio))
    for ok, ch, err in results:
        if ok:
            working.append((ch, err))
        else:
            failed.append((ch, err))

print(f"\nDone! Segments OK: {len(working)}, Segments FAILED: {len(failed)}")
print("--- FAILED CHANNELS ---")
for ch, err in failed:
    print(f"[{ch.get('number')}] {ch.get('name')} (id: {ch.get('id')}): {err}")
    print(f"    URL: {ch.get('url')[:90]}")
    if ch.get('backups'):
        print(f"    Backups: {ch.get('backups')[:2]}")

