import json, re

with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

print("Channels around 70:")
for ch in channels:
    num = ch.get('number')
    if num in range(65, 75):
        print(f"#{num}: {ch.get('id')} | {ch.get('name')} | URL: {ch.get('url')}")

print("\nAll Nickelodeon / Nick channels:")
for ch in channels:
    if 'nick' in ch.get('id', '').lower() or 'ניק' in ch.get('name', ''):
        print(f"#{ch.get('number')}: {ch.get('id')} | {ch.get('name')} | URL: {ch.get('url')} | backups: {ch.get('backups')}")
