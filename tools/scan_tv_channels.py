import json
import urllib.request
import ssl
import concurrent.futures

with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

non_radio = [c for c in channels if c.get('kind') != 'radio']
print(f"Total Video/TV channels to test: {len(non_radio)}")

def test_stream(ch):
    url = ch.get('url', '')
    req = urllib.request.Request(
        url,
        headers={
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36',
            'Referer': 'https://freetv.tv/'
        }
    )
    try:
        with urllib.request.urlopen(req, timeout=6, context=ctx) as r:
            code = r.getcode()
            content = r.read(2048).decode('utf-8', errors='ignore')
            ct = r.headers.get('content-type', '')
            if code == 200 and ('#EXTM3U' in content or 'mpegurl' in ct or 'video' in ct):
                return True, ch, 'OK'
            elif code == 200:
                return False, ch, f'HTTP 200 but not m3u8: {content[:100].strip()}'
            else:
                return False, ch, f'HTTP {code}'
    except Exception as e:
        return False, ch, str(e)

failed = []
working = []
with concurrent.futures.ThreadPoolExecutor(max_workers=30) as executor:
    results = list(executor.map(test_stream, non_radio))
    for ok, ch, err in results:
        if ok:
            working.append((ch, err))
        else:
            failed.append((ch, err))

print(f"\nResults: Working: {len(working)}, Failed: {len(failed)}")
print("--- FAILED VIDEO CHANNELS ---")
for ch, err in failed:
    print(f"[{ch.get('number')}] {ch.get('name')} (id: {ch.get('id')}): {err}")
    print(f"    URL: {ch.get('url')}")
