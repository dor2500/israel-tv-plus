import json
import urllib.request
import time
import concurrent.futures

with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

print(f"Auditing all {len(channels)} channels for freeze / looping pre-recorded chunks...")

def fetch_m3u8_info(c):
    url = c.get('url', '')
    cid = c.get('id', '')
    name = c.get('name', '')
    
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
        'Accept': '*/*'
    }
    try:
        req = urllib.request.Request(url, headers=headers)
        with urllib.request.urlopen(req, timeout=6) as res:
            body = res.read().decode('utf-8', errors='ignore')
            
            # If master playlist, resolve first sub-stream
            if '#EXT-X-STREAM-INF' in body:
                for line in body.splitlines():
                    line = line.strip()
                    if line and not line.startswith('#'):
                        if line.startswith('http'):
                            sub_url = line
                        else:
                            base = url.rsplit('/', 1)[0]
                            sub_url = f"{base}/{line}"
                        try:
                            sub_req = urllib.request.Request(sub_url, headers=headers)
                            with urllib.request.urlopen(sub_req, timeout=6) as sub_res:
                                body = sub_res.read().decode('utf-8', errors='ignore')
                        except Exception:
                            pass
                        break
                        
            seq = None
            is_vod = '#EXT-X-ENDLIST' in body
            last_ts = None
            for line in body.splitlines():
                if line.startswith('#EXT-X-MEDIA-SEQUENCE:'):
                    try:
                        seq = int(line.split(':')[1])
                    except Exception:
                        pass
                elif line.strip() and not line.startswith('#'):
                    last_ts = line.strip()
                    
            return {
                'id': cid,
                'name': name,
                'url': url,
                'seq': seq,
                'last_ts': last_ts,
                'is_vod': is_vod,
                'ok': True
            }
    except Exception as e:
        return {
            'id': cid,
            'name': name,
            'url': url,
            'ok': False,
            'err': str(e)
        }

with concurrent.futures.ThreadPoolExecutor(max_workers=30) as executor:
    pass1 = list(executor.map(fetch_m3u8_info, channels))

pass1_by_id = {item['id']: item for item in pass1}
print("Pass 1 finished. Waiting 12 seconds to observe live advancement...")
time.sleep(12)

with concurrent.futures.ThreadPoolExecutor(max_workers=30) as executor:
    pass2 = list(executor.map(fetch_m3u8_info, channels))

pass2_by_id = {item['id']: item for item in pass2}

frozen_list = []
active_live = []
errors = []

for c in channels:
    cid = c['id']
    name = c['name']
    p1 = pass1_by_id.get(cid, {})
    p2 = pass2_by_id.get(cid, {})
    
    if not p1.get('ok') or not p2.get('ok'):
        errors.append((cid, name, c['url'], p1.get('err') or p2.get('err')))
        continue
        
    s1 = p1.get('seq')
    s2 = p2.get('seq')
    ts1 = p1.get('last_ts')
    ts2 = p2.get('last_ts')
    
    # If media sequence exists and did not change
    if s1 is not None and s2 is not None:
        if s2 > s1:
            active_live.append((cid, name, s1, s2))
        else:
            frozen_list.append((cid, name, c['url'], f"seq frozen at {s1}"))
    elif ts1 is not None and ts2 is not None:
        if ts1 != ts2:
            active_live.append((cid, name, 'ts_changed', ''))
        else:
            # Check if VOD
            if p1.get('is_vod'):
                active_live.append((cid, name, 'vod', ''))
            else:
                frozen_list.append((cid, name, c['url'], "last_ts unchanged"))
    else:
        active_live.append((cid, name, 'unknown_stream', ''))

print(f"\n================ SUMMARY ================")
print(f"Total Channels Checked: {len(channels)}")
print(f"Active Live (Advancing): {len(active_live)}")
print(f"FROZEN / PRE-RECORDED STUCK: {len(frozen_list)}")
print(f"Errors (Dead stream): {len(errors)}")

print(f"\n--- ALL FROZEN CHANNELS ---")
for cid, name, url, reason in frozen_list:
    print(f"FROZEN: {cid:26} | {name:38} | {reason} | {url}")

print(f"\n--- ALL ERROR CHANNELS ---")
for cid, name, url, err in errors:
    print(f"ERROR:  {cid:26} | {name:38} | {err} | {url}")

with open('tools/frozen_audit_results.json', 'w', encoding='utf-8') as f:
    json.dump({
        'frozen': frozen_list,
        'errors': errors,
        'active_count': len(active_live)
    }, f, ensure_ascii=False, indent=2)
print("\nWrote results to tools/frozen_audit_results.json")
