import json

with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

print(f"Initial channels count: {len(channels)}")

by_id = {c['id']: c for c in channels}

# 1. partner_reality_il -> merge into reshet13_reality_il
if 'reshet13_reality_il' in by_id and 'partner_reality_il' in by_id:
    p = by_id['partner_reality_il']
    r = by_id['reshet13_reality_il']
    backups = r.get('backups', [])
    if p['url'] not in backups and p['url'] != r['url']:
        backups.append(p['url'])
    r['backups'] = backups
    print("Merged partner_reality_il -> reshet13_reality_il")

# 2. partner_junior_il -> merge into junior_tv
if 'junior_tv' in by_id and 'partner_junior_il' in by_id:
    p = by_id['partner_junior_il']
    j = by_id['junior_tv']
    backups = j.get('backups', [])
    if p['url'] not in backups and p['url'] != j['url']:
        backups.append(p['url'])
    j['backups'] = backups
    print("Merged partner_junior_il -> junior_tv")

# 3. partner_kids_il -> merge into kids_channel
if 'kids_channel' in by_id and 'partner_kids_il' in by_id:
    p = by_id['partner_kids_il']
    k = by_id['kids_channel']
    backups = k.get('backups', [])
    if p['url'] not in backups and p['url'] != k['url']:
        backups.append(p['url'])
    k['backups'] = backups
    print("Merged partner_kids_il -> kids_channel")

# 4. partner_humor_il -> merge into humor_channel_il
if 'humor_channel_il' in by_id and 'partner_humor_il' in by_id:
    p = by_id['partner_humor_il']
    h = by_id['humor_channel_il']
    backups = h.get('backups', [])
    if p['url'] not in backups and p['url'] != h['url']:
        backups.append(p['url'])
    h['backups'] = backups
    print("Merged partner_humor_il -> humor_channel_il")

# 5. partner_turkish_dramas_il -> merge into turkish_dramas_plus
if 'turkish_dramas_plus' in by_id and 'partner_turkish_dramas_il' in by_id:
    p = by_id['partner_turkish_dramas_il']
    t = by_id['turkish_dramas_plus']
    backups = t.get('backups', [])
    if p['url'] not in backups and p['url'] != t['url']:
        backups.append(p['url'])
    t['backups'] = backups
    print("Merged partner_turkish_dramas_il -> turkish_dramas_plus")

# 6. partner_good_life_il -> merge into home_plus_il
if 'home_plus_il' in by_id and 'partner_good_life_il' in by_id:
    p = by_id['partner_good_life_il']
    hp = by_id['home_plus_il']
    backups = hp.get('backups', [])
    if p['url'] not in backups and p['url'] != hp['url']:
        backups.append(p['url'])
    hp['backups'] = backups
    print("Merged partner_good_life_il -> home_plus_il")

# Channels to delete (the duplicate Partner wrappers)
del_ids = {
    'partner_reality_il',
    'partner_junior_il',
    'partner_kids_il',
    'partner_humor_il',
    'partner_turkish_dramas_il',
    'partner_good_life_il'
}

new_chs = []
for c in channels:
    if c['id'] in del_ids:
        continue
    if c['id'] == 'partner_design_il':
        c['id'] = 'home_design_israel_hd'
        c['category'] = 'ערוצי ישראל - שידור חי'
        c['categoryEn'] = 'israel'
    elif c['id'] == 'partner_gameshow_il':
        c['id'] = 'gameshow_channel_il'
        c['name'] = 'ערוץ השעשועונים והמשחקים HD'
        c['title'] = 'ערוץ השעשועונים והחידונים 24/7'
        c['category'] = 'ערוצי ישראל - שידור חי'
        c['categoryEn'] = 'israel'
    new_chs.append(c)

# Renumber 1..N
for i, c in enumerate(new_chs, 1):
    c['number'] = i

ids = [c['id'] for c in new_chs]
urls = [c['url'] for c in new_chs]
names = [c['name'] for c in new_chs]
nums = [c['number'] for c in new_chs]

print(f"\nFinal Channel Count: {len(new_chs)}")
print(f"Unique IDs:     {len(set(ids))} (duplicates: {len(ids) - len(set(ids))})")
print(f"Unique URLs:    {len(set(urls))} (duplicates: {len(urls) - len(set(urls))})")
print(f"Unique Names:   {len(set(names))} (duplicates: {len(names) - len(set(names))})")
print(f"Unique Numbers: {len(set(nums))} (duplicates: {len(nums) - len(set(nums))})")

with open('channels.json', 'w', encoding='utf-8') as f:
    json.dump(new_chs, f, ensure_ascii=False, indent=2)

with open('channels.js', 'w', encoding='utf-8') as f:
    f.write('window.CHANNELS = ' + json.dumps(new_chs, ensure_ascii=False, indent=2) + ';\n')

print("Saved channels.json and channels.js")
