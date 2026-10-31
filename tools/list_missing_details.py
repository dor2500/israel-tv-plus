import json

with open('epg.json', 'r', encoding='utf-8') as f:
    epg = json.load(f)['channels']

with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

cid_to_ch = {c['id']: c for c in channels}

for cid, progs in sorted(epg.items()):
    missing = [p[2] for p in progs if not (len(p) > 4 and p[4])]
    if missing:
        cname = cid_to_ch.get(cid, {}).get('name', cid)
        cat = cid_to_ch.get(cid, {}).get('category', '')
        print(f"=== {cid} ({cname}) [{cat}] - {len(missing)} missing ===")
        unique_titles = sorted(list(set(missing)))
        for t in unique_titles[:8]:
            print(f"  - {t}")
