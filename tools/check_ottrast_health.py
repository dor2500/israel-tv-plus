import urllib.request
import time
import json
import concurrent.futures

channels = json.load(open('channels.json', encoding='utf-8'))
ottrast_channels = [c for c in channels if 'ottrast.com' in c.get('url', '')]

def get_seq(c):
    url = c.get('url', '')
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'})
        res = urllib.request.urlopen(req, timeout=5).read().decode('utf-8', errors='ignore')
        for line in res.splitlines():
            if line.startswith('#EXT-X-MEDIA-SEQUENCE:'):
                return c['id'], int(line.split(':')[1])
        return c['id'], 'NO_SEQ'
    except Exception as e:
        return c['id'], f'ERR: {e}'

print(f"Testing {len(ottrast_channels)} ottrast channels for sequence advancement...")

with concurrent.futures.ThreadPoolExecutor(max_workers=20) as executor:
    results1 = dict(executor.map(get_seq, ottrast_channels))

print("Pass 1 completed. Sleeping 12 seconds...")
time.sleep(12)

with concurrent.futures.ThreadPoolExecutor(max_workers=20) as executor:
    results2 = dict(executor.map(get_seq, ottrast_channels))

frozen = []
working = []
error = []

for c in ottrast_channels:
    cid = c['id']
    s1 = results1.get(cid)
    s2 = results2.get(cid)
    name = c['name']
    
    if isinstance(s1, int) and isinstance(s2, int):
        if s2 > s1:
            working.append((cid, name, s1, s2))
        else:
            frozen.append((cid, name, s1, s2))
    else:
        error.append((cid, name, str(s1), str(s2)))

print(f"\n--- RESULTS ---")
print(f"WORKING LIVE (advancing sequence): {len(working)}")
print(f"FROZEN / LOOPING (same sequence): {len(frozen)}")
print(f"ERRORS: {len(error)}")

print("\n--- FROZEN CHANNELS ---")
for cid, name, s1, s2 in frozen:
    print(f"[FROZEN] {cid:30} | {name:40} | seq={s1}")

print("\n--- SAMPLE WORKING CHANNELS ---")
for cid, name, s1, s2 in working[:10]:
    print(f"[LIVE]   {cid:30} | {name:40} | {s1} -> {s2}")

