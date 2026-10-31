import urllib.request, re

req = urllib.request.Request('https://raw.githubusercontent.com/RokuIL/Live-From-Israel/master/playlist.m3u8', headers={'User-Agent': 'Mozilla/5.0'})
raw = urllib.request.urlopen(req).read().decode('utf-8', 'ignore')

channels = []
cur_name = None
for line in raw.splitlines():
    line = line.strip()
    if line.startswith('#EXTINF:'):
        m = re.search(r',(.+)$', line)
        if m:
            cur_name = m.group(1).strip()
    elif line and not line.startswith('#') and cur_name:
        channels.append((cur_name, line))
        cur_name = None

print(f"Total channels in RokuIL playlist.m3u8: {len(channels)}")
for name, url in channels[:30]:
    print(f" - {name}: {url[:80]}")

