import json

with open("channels.json", encoding="utf-8") as f:
    chs = json.load(f)

hosts = {}
for c in chs:
    url = c["url"]
    from urllib.parse import urlparse
    h = urlparse(url).netloc
    hosts[h] = hosts.get(h, 0) + 1

for h, count in sorted(hosts.items(), key=lambda x: -x[1])[:20]:
    print(f"{h}: {count} channels")
