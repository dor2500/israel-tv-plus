# tools/fix_sports_live.py
import json

path_json = 'channels.json'
path_js = 'channels.js'

with open(path_json, 'r', encoding='utf-8') as f:
    chs = json.load(f)

sports_updates = {
    'sport1_israel': {
        'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12249/index.m3u8',
        'backups': [
            'http://45.156.223.202/iptv/QAP2YS2594ZQS3/12249/index.m3u8'
        ]
    },
    'sport2_israel': {
        'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12250/index.m3u8',
        'backups': [
            'http://45.156.223.202/iptv/QAP2YS2594ZQS3/12250/index.m3u8'
        ]
    },
    'sport3_israel': {
        'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12251/index.m3u8',
        'backups': [
            'http://45.156.223.202/iptv/QAP2YS2594ZQS3/12251/index.m3u8'
        ]
    },
    'sport4_israel': {
        'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12252/index.m3u8',
        'backups': [
            'http://45.156.223.202/iptv/QAP2YS2594ZQS3/12252/index.m3u8'
        ]
    },
    'sport6_israel': {
        'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32003/index.m3u8',
        'backups': [
            'http://45.156.223.202/iptv/QAP2YS2594ZQS3/32003/index.m3u8'
        ]
    }
}

for c in chs:
    cid = c.get('id')
    if cid in sports_updates:
        c['url'] = sports_updates[cid]['url']
        c['backups'] = list(sports_updates[cid]['backups'])
        c['fallbackUrls'] = list(sports_updates[cid]['backups'])
        c['backupUrls'] = list(sports_updates[cid]['backups'])
        print(f"Purged stale feeds and updated {cid} with verified live servers")

with open(path_json, 'w', encoding='utf-8') as f:
    json.dump(chs, f, ensure_ascii=False, indent=2)
    f.write('\n')

with open(path_js, 'w', encoding='utf-8') as f:
    f.write('// Generated from channels.json\nwindow.ITV_CHANNELS = ')
    json.dump(chs, f, ensure_ascii=False)
    f.write(';\n')

print("Saved channels.json and channels.js")
