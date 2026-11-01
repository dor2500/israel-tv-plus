import json
import urllib.request
import ssl
import concurrent.futures

with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

def test_ch(ch):
    url = ch.get('url', '')
    req = urllib.request.Request(
        url,
        headers={
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36',
            'Referer': 'https://freetv.tv/'
        }
    )
    try:
        with urllib.request.urlopen(req, timeout=4, context=ctx) as r:
            content = r.read(1024).decode('utf-8', errors='ignore')
            ct = r.headers.get('content-type', '')
            if '#EXTM3U' in content or 'mpegurl' in ct or 'video' in ct or 'application' in ct:
                return True, ch, ''
            return False, ch, f'Bad content: {content[:30]}'
    except Exception as e:
        return False, ch, str(e)

failed = []
working = 0
with concurrent.futures.ThreadPoolExecutor(max_workers=35) as executor:
    results = list(executor.map(test_ch, channels))
    for ok, ch, err in results:
        if ok:
            working += 1
        else:
            failed.append((ch, err))

print(f'Done! Working: {working}, Failed: {len(failed)}')
for ch, err in failed:
    print(f"[{ch.get('number')}] {ch.get('name')} ({ch.get('id')}): {err}")

