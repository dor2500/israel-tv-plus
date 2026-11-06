import subprocess, json, urllib.request, re

base = "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3"
headers = {'User-Agent': 'Mozilla/5.0'}

# Test candidate IDs around 2330-2340, 12235-12245, 32145-32155
cands = [2334, 2337, 2359, 2364, 2366, 12240, 12283, 32148]

# Let's also check if there are other responsive IDs
for i in [2335, 2336, 2338, 2339, 12244, 32139, 32149]:
    cands.append(i)

print(f"Testing {len(cands)} candidate stream IDs for Hebrew Nickelodeon...")
for cid in cands:
    url = f"{base}/{cid}/index.m3u8"
    try:
        req = urllib.request.Request(url, headers=headers)
        res = urllib.request.urlopen(req, timeout=3)
        body = res.read().decode('utf-8', errors='ignore')
        # Check if live
        ts = [l for l in body.splitlines() if '.ts' in l]
        if ts:
            m = re.search(r'(\d{13})\.ts', ts[-1])
            is_fresh = bool(m and int(m.group(1)) > 1793400000000)
            print(f"ID {cid}: 200 OK | fresh={is_fresh} | last={ts[-1][:60]}")
    except Exception as e:
        pass
