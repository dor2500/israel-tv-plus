import json
import urllib.request

with open(r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\channels.json', 'r', encoding='utf-8') as f:
    chs = json.load(f)

check_ids = ['arutz_hayeladim', 'logi_tv', 'cartoon_network_hd', 'hot_buzz_il', 'humor_channel_il', 'history_il', 'hop_yaldut_il', 'travel_channel_il', 'disney_jr_il', 'viva_israel', 'viva_plus_il', 'viva_premium_il', 'hot_cinema_1', 'hot_cinema_2', 'hot_cinema_family_il', 'hot_israeli_cinema', 'hot_romantic_cinema']

for cid in check_ids:
    c = next((x for x in chs if x['id'] == cid), None)
    if not c:
        print(f'{cid}: NOT FOUND')
        continue
    url = c.get('url', '')
    name = c.get('name', '')
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req, timeout=4) as resp:
            data = resp.read(300).decode('utf-8', errors='ignore')
            first_line = data.split('\n')[0].strip() if data else ''
            print(f'{cid} | {name}: OK ({resp.getcode()}) -> {first_line}')
    except Exception as e:
        print(f'{cid} | {name}: FAILED -> {e}')

