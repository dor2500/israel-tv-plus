import json, urllib.request, os

with open('channels.json', 'r', encoding='utf-8') as f:
    chs = json.load(f)

remote_channels = [c for c in chs if c.get('logo', '').startswith('http')]
print(f"Total remote channels: {len(remote_channels)}")
for c in remote_channels:
    print(c['id'], "::", c['name'], "::", c['logo'])

