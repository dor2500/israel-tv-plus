import re

with open('index.html', encoding='utf-8') as f:
    text = f.read()

ids = re.findall(r'id="([^"]+)"', text)
for i in ids:
    if any(k in i.lower() for k in ['player', 'osd', 'control', 'btn', 'bar', 'menu', 'epg']):
        print(i)

