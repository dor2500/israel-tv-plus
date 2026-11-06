import json, urllib.request, os

with open('channels.json', 'r', encoding='utf-8') as f:
    chs = json.load(f)

remote = [c for c in chs if c.get('logo', '').startswith('http')]
print(f"Checking {len(remote)} remote logos...")

opener = urllib.request.build_opener()
opener.addheaders = [('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36')]
urllib.request.install_opener(opener)

ok = []
fail = []
for c in remote:
    url = c['logo']
    cid = c['id']
    try:
        with urllib.request.urlopen(url, timeout=5) as r:
            data = r.read()
            ok.append((cid, len(data), r.headers.get("content-type")))
    except Exception as e:
        fail.append((cid, url, str(e)))

print(f"OK: {len(ok)}, FAIL: {len(fail)}")
print("--- FAILED DOWNLOADS ---")
for f in fail:
    print(f[0], "::", f[2])

