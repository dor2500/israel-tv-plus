import json

channels = json.load(open('channels.json', encoding='utf-8'))
epg = json.load(open('epg.json', encoding='utf-8'))

epg_channels = epg.get('channels', {})
print(f"Total EPG channels: {len(epg_channels)}")

matched = []
unmatched = []
for c in channels:
    cid = c['id']
    name = c['name']
    # Match by ID or variations
    matched_epg = None
    candidates = [
        cid,
        cid.replace('_hd', ''),
        cid.replace('_fhd', ''),
        cid.replace('_il', ''),
        cid.replace('_fast', ''),
        cid.replace('channel_', 'ch'),
        cid.replace('ch14', 'now14'),
        cid.replace('now_14', 'now14'),
        cid.replace('keshet_12', 'keshet12'),
        cid.replace('reshet_13', 'reshet13'),
        cid.replace('kan_11', 'kan11'),
        cid.replace('channel9', 'channel9_israel_hd'),
    ]
    for cand in candidates:
        if cand in epg_channels:
            matched_epg = cand
            break
    if matched_epg:
        matched.append((cid, name, matched_epg, len(epg_channels[matched_epg])))
    else:
        unmatched.append((cid, name))

print(f"Matched {len(matched)} channels to EPG!")
for m in matched[:15]:
    print(f"  {m[0]} ('{m[1]}') -> {m[2]} ({m[3]} programs)")

