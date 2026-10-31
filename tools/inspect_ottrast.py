import json

with open("channels.json", encoding="utf-8") as f:
    chs = json.load(f)

for c in chs:
    if "ottrast.com" in c["url"]:
        print(f"OTTRAST: {c['name']} ({c['id']}) -> {c['url']}")
