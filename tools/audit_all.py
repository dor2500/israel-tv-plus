import json
import urllib.request
import ssl
import concurrent.futures

with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

def check_channel(ch):
    url = ch.get('url', '')
    if not url:
        return False, ch, 'No URL'
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36',
        'Referer': 'https://freetv.tv/'
    }
    req = urllib.request.Request(url, headers=headers)
    try:
        with urllib.request.urlopen(req, timeout=5, context=ctx) as r:
            code = r.getcode()
            if code == 200:
                return True, ch, 'OK'
            return False, ch, f'HTTP {code}'
    except Exception as e:
        return False, ch, str(e)

failed = []
with concurrent.futures.ThreadPoolExecutor(max_workers=35) as executor:
    results = list(executor.map(check_channel, channels))
    for ok, ch, err in results:
        if not ok:
            failed.append((ch, err))

print(f"Total: {len(channels)}, Failed: {len(failed)}")
for ch, err in failed:
    print(f"[{ch.get('number')}] {ch.get('name')} ({ch.get('id')}): {err}")

