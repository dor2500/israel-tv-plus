import json

with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

print(f"Total channels loaded: {len(channels)}")

# 1. Fix channel 36 and 106 (Sport 5 4K)
fhd_sport5_freetv = 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/sport_5/live.livx/playlist.m3u8'
fhd_sport5_charlton = 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32004/index.m3u8'
fhd_sport5_ip = 'http://45.156.223.202/iptv/QAP2YS2594ZQS3/32004/index.m3u8'
fhd_sport5_akamai = 'https://rgelive.akamaized.net/hls/live/2043095/live3/playlist.m3u8'

for ch in channels:
    cid = ch.get('id', '')
    num = ch.get('number')
    
    # Channel 36 & 106
    if cid in ['sport_5_4k', 'sport5_4k_uhd'] or num in [36, 106]:
        ch['backups'] = [
            fhd_sport5_freetv,
            fhd_sport5_charlton,
            fhd_sport5_ip,
            fhd_sport5_akamai
        ]
        ch['desc'] = 'שידורי ספורט ישירים בטכנולוגיית 4K Ultra HD ו-FHD H.264 מלא: ליגת האלופות, ליגת העל בכדורגל, מכבי תל אביב ביורוליג והמשחקים המרכזיים בחדות שיא עם גיבוי FHD אוטומטי.'
        print(f"Patched Sport 5 4K backups for [{num}] {ch.get('name')}")

    # Upgrade stream.mcquack.net to HTTPS
    url = ch.get('url', '')
    if 'http://stream.mcquack.net/' in url:
        ch['url'] = url.replace('http://stream.mcquack.net/', 'https://stream.mcquack.net/')
        print(f"Upgraded to HTTPS: [{num}] {ch.get('name')}")
    
    backups = ch.get('backups', [])
    new_backups = []
    for b in backups:
        if 'http://stream.mcquack.net/' in b:
            new_backups.append(b.replace('http://stream.mcquack.net/', 'https://stream.mcquack.net/'))
        else:
            new_backups.append(b)
    ch['backups'] = new_backups

    # Channel 68 (Nick Jr Party)
    if cid == 'nick_jr_party_il' or num == 68:
        ch['url'] = 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2337/index.m3u8'
        ch['backups'] = [
            'http://45.156.223.202/iptv/QAP2YS2594ZQS3/2337/index.m3u8',
            'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12240/index.m3u8'
        ]
        print(f"Patched Nick Jr stream for [{num}] {ch.get('name')}")

    # Channel 193 (CNN)
    if cid == 'cnn_intl_hd' or num == 193:
        ch['url'] = 'https://jmp2.uk/plu-5421f71da6af422839419cb3.m3u8'
        ch['backups'] = ['https://turnerlive.warnermediacdn.com/hls/live/586495/cnngo/cnn_slate/VIDEO_0_3564000.m3u8']
        print(f"Patched CNN for [{num}] {ch.get('name')}")

    # Charlton Sport 1, 2, 3, 4, 6: ensure direct IP backups exist
    if cid == 'sport1_israel':
        ch['backups'] = ['http://45.156.223.202/iptv/QAP2YS2594ZQS3/12249/index.m3u8', 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2353/index.m3u8', 'http://45.156.223.202/iptv/QAP2YS2594ZQS3/2353/index.m3u8', 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32001/index.m3u8']
    elif cid == 'sport2_israel':
        ch['backups'] = ['http://45.156.223.202/iptv/QAP2YS2594ZQS3/12250/index.m3u8', 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2352/index.m3u8', 'http://45.156.223.202/iptv/QAP2YS2594ZQS3/2352/index.m3u8', 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32001/index.m3u8']
    elif cid == 'sport3_israel':
        ch['backups'] = ['http://45.156.223.202/iptv/QAP2YS2594ZQS3/12251/index.m3u8', 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2388/index.m3u8', 'http://45.156.223.202/iptv/QAP2YS2594ZQS3/2388/index.m3u8', 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32002/index.m3u8']
    elif cid == 'sport4_israel':
        ch['backups'] = ['http://45.156.223.202/iptv/QAP2YS2594ZQS3/12252/index.m3u8', 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2389/index.m3u8', 'http://45.156.223.202/iptv/QAP2YS2594ZQS3/2389/index.m3u8', 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32002/index.m3u8']
    elif cid == 'sport6_israel':
        ch['backups'] = ['http://45.156.223.202/iptv/QAP2YS2594ZQS3/32003/index.m3u8', 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32004/index.m3u8']

# Save channels.json
with open('channels.json', 'w', encoding='utf-8') as f:
    json.dump(channels, f, ensure_ascii=False, indent=2)

# Rebuild channels.js
with open('channels.js', 'w', encoding='utf-8') as f:
    f.write('// Generated from channels.json\nwindow.ITV_CHANNELS = ' + json.dumps(channels, ensure_ascii=False) + ';\n')

print("Successfully updated channels.json and channels.js!")

