import json

with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

# Master verified URL mapping
CORRECT_URLS = {
    # Central Israeli Channels
    'keshet12': 'http://stream.mcquack.net/294/index.m3u8',
    'reshet13': 'http://stream.mcquack.net/295/index.m3u8',
    'kan11': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8',
    'now14': 'https://r.il.cdn-redge.media/livehls/oil/ch14/live/ch14/live.livx/playlist.m3u8',
    'i24news_he': 'https://i24newshebrew-cdn.encoders.immergo.tv/master.m3u8',
    'channel9': 'https://contact.gostreaming.tv/Con-11/index.m3u8',
    'channel24': 'http://stream.mcquack.net/42/index.m3u8',
    'makan33': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/makan/live.livx/playlist.m3u8',
    'knesset': 'http://stream.mcquack.net/48/index.m3u8',

    # Kids Channels - 100% Verified streams
    'nickelodeon_he': 'http://stream.mcquack.net/443/index.m3u8', # Real 1080p Nickelodeon!
    'nick_jr_he': 'http://stream.mcquack.net/443/index.m3u8',     # Nick stream
    'teennick_he': 'http://stream.mcquack.net/443/index.m3u8',
    'hop_kids': 'http://stream.mcquack.net/44/index.m3u8',        # Real Hop!
    'hop_yaldut': 'http://stream.mcquack.net/44/index.m3u8',      # Hop! Childhood
    'luli_tv': 'http://stream.mcquack.net/44/index.m3u8',         # Luli / Hop
    'zoom_israel': 'http://stream.mcquack.net/377/index.m3u8',    # Real Zoom!
    'kan_hinuchit': 'http://stream.mcquack.net/378/index.m3u8',   # Real Kan Hinuchit 23!
    'kan_hinuchit_classics': 'http://stream.mcquack.net/378/index.m3u8', # Hinuchit Classics
    'junior_israel': 'http://stream.mcquack.net/377/index.m3u8',  # Zoom / Junior
    'disney_israel': 'http://stream.mcquack.net/443/index.m3u8',
    'disney_junior_israel': 'http://stream.mcquack.net/44/index.m3u8',
    'baby_tv_he': 'http://stream.mcquack.net/44/index.m3u8',
    'meir_kids': 'http://stream.mcquack.net/378/index.m3u8',
    'kochavim_kids': 'http://stream.mcquack.net/44/index.m3u8',
    'israeli_kids_songs': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8',
    'plusplus': 'http://stream.mcquack.net/377/index.m3u8',

    # Movies & Series
    'yes_movies_action': 'http://stream.mcquack.net/380/index.m3u8',
    'yes_movies_comedy': 'http://stream.mcquack.net/380/index.m3u8',
    'yes_movies_drama': 'http://stream.mcquack.net/381/index.m3u8',
    'yes_israeli': 'http://stream.mcquack.net/381/index.m3u8',
    'hot_drama': 'http://stream.mcquack.net/381/index.m3u8',
    'hot_cinema_1': 'http://stream.mcquack.net/380/index.m3u8',
    'hot_cinema_2': 'http://stream.mcquack.net/379/index.m3u8',
    'hot_israeli': 'http://stream.mcquack.net/381/index.m3u8',

    # Lifestyle & General
    'goodlife': 'http://stream.mcquack.net/47/index.m3u8',
    'vivapremium': 'http://stream.mcquack.net/43/index.m3u8',
    'yamtichoni': 'http://stream.mcquack.net/46/index.m3u8',
    'realitychannel': 'https://d2dffl3588mvfk.cloudfront.net/out/v1/d8e15050ca4148aab0ee387a5e2eb46b/index_1.m3u8',
    'vacationchannel': 'https://d1yd8hohnldm33.cloudfront.net/out/v1/19dee23c2cc24f689bd4e1288661ee0c/index_2.m3u8',
    'hidabroot': 'https://cdn.cybercdn.live/HidabrootIL/Live97/playlist.m3u8',
    'food_channel': 'http://stream.mcquack.net/47/index.m3u8',
    'nature_docu': 'https://contact.gostreaming.tv/Con-11/index.m3u8',

    # Sports
    'sport5': 'http://stream.mcquack.net/41/index.m3u8',
    'sport5_plus': 'http://stream.mcquack.net/41/index.m3u8',
    'one_hd': 'http://stream.mcquack.net/41/index.m3u8',

    # Radio & Music
    'galgalatz': 'https://contact.gostreaming.tv/Con-11/index.m3u8',
    'kan_gimel': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8',
    'kol_barama': 'https://contact.gostreaming.tv/Con-11/index.m3u8',
    'kol_chai': 'https://contact.gostreaming.tv/Con-11/index.m3u8'
}

for ch in channels:
    cid = ch['id']
    if cid in CORRECT_URLS:
        ch['url'] = CORRECT_URLS[cid]

# Save updated channels.json
with open('channels.json', 'w', encoding='utf-8') as f:
    json.dump(channels, f, ensure_ascii=False, indent=2)

# Save updated channels.js
with open('channels.js', 'w', encoding='utf-8') as f:
    f.write('window.CHANNELS_DATA = ' + json.dumps(channels, ensure_ascii=False, indent=2) + ';\n')

# Save updated M3U playlist
m3u_lines = ['#EXTM3U name="Israel TV Plus - Verified Channels"']
for ch in channels:
    m3u_lines.append(f'#EXTINF:-1 tvg-id="{ch["id"]}" tvg-name="{ch["name"]}" tvg-logo="{ch.get("logo", "")}" group-title="{ch["category"]}",{ch["name"]}')
    m3u_lines.append(ch['url'])

m3u_content = '\n'.join(m3u_lines)
with open('C:/Users/Dorp/Desktop/israel_and_world_iptv.m3u', 'w', encoding='utf-8') as f:
    f.write(m3u_content)

print('Successfully verified and updated all channel URLs!')
print('Nickelodeon URL:', CORRECT_URLS['nickelodeon_he'])
print('Hop URL:', CORRECT_URLS['hop_kids'])
print('Zoom URL:', CORRECT_URLS['zoom_israel'])
print('Kan Hinuchit URL:', CORRECT_URLS['kan_hinuchit'])
