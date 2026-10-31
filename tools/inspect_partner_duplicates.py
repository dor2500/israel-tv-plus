import json

with open('channels.json', 'r', encoding='utf-8') as f:
    chs = json.load(f)

partner_chs = [c for c in chs if 'partner' in c['id'] or 'פרטנר' in c['name'] or 'partner' in c.get('categoryEn', '')]
non_partner = [c for c in chs if c not in partner_chs]

print(f"Total Partner Channels: {len(partner_chs)}")
print("=" * 80)

for p in partner_chs:
    print(f"\nPartner Ch: {p['id']} | {p['name']}")
    print(f"  URL: {p['url']}")
    print(f"  Desc: {p.get('desc', '')}")
    
    # Check if there is any other channel with similar keywords in name or desc
    keywords = [w for w in p['name'].replace('ערוץ', '').replace('פרטנר', '').replace('TV', '').replace('–', '').replace('-', '').replace('HD', '').replace('FHD', '').split() if len(w) > 2]
    matches = []
    for o in non_partner:
        # Check if URL host/path matches or if keywords overlap strongly
        url_match = (p['url'] == o['url'])
        kw_match = any(k in o['name'] for k in keywords)
        if url_match or kw_match:
            matches.append((o['id'], o['name'], o['url'], url_match))
            
    if matches:
        print("  Potential duplicates in other categories:")
        for mid, mname, murl, is_url in matches:
            tag = "[SAME URL!]" if is_url else "[NAME MATCH]"
            print(f"    {tag} {mid:25} | {mname:35} | {murl}")
