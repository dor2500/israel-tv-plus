import json

with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

print(f"Initial channels: {len(channels)}")

yaldut_url = "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/yalduti/live.livx/playlist.m3u8"
yaldut_songs_url = "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/yalduti_songs/live.livx/playlist.m3u8"
design_url = "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/imagin_lifestyle/live.livx/playlist.m3u8"

# Deduplicate:
# - yalduti_tv -> consolidate into hop_yaldut_il ("ערוץ הופ! ילדות ישראלית HD")
# - partner_home_design_il, freetv_lifestyle_il -> consolidate into partner_design_il ("ערוץ בית ועיצוב ישראלי HD")
# - partner_toddlers_luli_il -> consolidate into luli_tv_il
# - partner_vacation_il -> consolidate into reshet13_nofesh_il
# - partner_viva_il -> consolidate into viva_israel
# - big_brother_live_hd -> remove since it's off-season or duplicate of reshet13
# - eurosport_1_il -> remove since 7203 is Eurosport 2 (eurosport_2_il) and 12204 is offline
ids_to_remove = {
    'yalduti_tv',
    'partner_home_design_il',
    'freetv_lifestyle_il',
    'partner_toddlers_luli_il',
    'partner_vacation_il',
    'partner_viva_il',
    'big_brother_live_hd',
    'eurosport_1_il'
}

new_channels = []

for c in channels:
    cid = c['id']
    if cid in ids_to_remove:
        continue
        
    if cid == 'hop_yaldut_il':
        c['name'] = "ערוץ הופ! ילדות ישראלית HD"
        c['title'] = "ערוץ הופ! ילדות ישראלית (ילדותי)"
        c['desc'] = "ערוץ שירי וסדרות ילדות ישראלית מבית הופ! ו-FreeTV – קלאסיקות, שירים ותוכניות אהובות לילדים ולכל המשפחה בשידור חי רציף 24/7."
        c['url'] = yaldut_url
        c['backups'] = [yaldut_songs_url]
        c['badge'] = "HD"
        c['category'] = "ערוצי ילדים ונוער בעברית"
        c['categoryEn'] = "kids"
        
    elif cid == 'partner_design_il':
        c['name'] = "ערוץ בית ועיצוב ישראלי HD"
        c['title'] = "ערוץ בית ועיצוב ישראלי – לייף סטייל ושיפוצים"
        c['desc'] = "ערוץ העיצוב והסטיילינג הישראלי – שיפוצים, אדריכלות, עיצוב הבית ולייף סטייל בשידור חי 24/7 באיכות Full HD."
        c['url'] = design_url
        c['backups'] = []
        c['badge'] = "FHD"
        c['category'] = "ערוצי פרטנר TV (Partner TV)"
        c['categoryEn'] = "partner"
        
    elif cid == 'hot_3_fhd':
        c['url'] = "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8"
        c['backups'] = []
        
    elif cid == 'yes_movies_kids_il':
        c['url'] = "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2365/index.m3u8"
        c['backups'] = []
        
    elif cid == 'nat_geo_wild_il':
        c['url'] = "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12259/index.m3u8"
        c['backups'] = []
        
    elif cid == 'fox_news_hd':
        c['url'] = "https://fox-foxnewsnow-samsungus.amagi.tv/playlist.m3u8"
        c['backups'] = []
        
    new_channels.append(c)

# Renumber 1..N cleanly
for i, c in enumerate(new_channels, 1):
    c['number'] = i

ids = [c['id'] for c in new_channels]
urls = [c['url'] for c in new_channels]
names = [c['name'] for c in new_channels]
nums = [c['number'] for c in new_channels]

print(f"\nFinal Channel Count: {len(new_channels)}")
print(f"Unique IDs:     {len(set(ids))} (duplicates: {len(ids) - len(set(ids))})")
print(f"Unique URLs:    {len(set(urls))} (duplicates: {len(urls) - len(set(urls))})")
print(f"Unique Names:   {len(set(names))} (duplicates: {len(names) - len(set(names))})")
print(f"Unique Numbers: {len(set(nums))} (duplicates: {len(nums) - len(set(nums))})")

with open('channels.json', 'w', encoding='utf-8') as f:
    json.dump(new_channels, f, ensure_ascii=False, indent=2)

with open('channels.js', 'w', encoding='utf-8') as f:
    f.write('window.CHANNELS = ' + json.dumps(new_channels, ensure_ascii=False, indent=2) + ';\n')

print("Saved channels.json and channels.js")
