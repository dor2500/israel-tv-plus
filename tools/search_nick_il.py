import urllib.request, json, re

# Search iptv-org and other sources for Nickelodeon Israel
for u in [
    'https://iptv-org.github.io/iptv/countries/il.m3u',
    'https://raw.githubusercontent.com/Free-TV/IPTV/master/playlist.m3u8'
]:
    try:
        req = urllib.request.Request(u, headers={'User-Agent': 'Mozilla/5.0'})
        txt = urllib.request.urlopen(req, timeout=5).read().decode('utf-8', errors='ignore')
        lines = txt.splitlines()
        for idx, line in enumerate(lines):
            if 'nickelodeon' in line.lower() or 'nick' in line.lower():
                print(u, line)
                if idx + 1 < len(lines):
                    print("   ", lines[idx+1])
    except Exception as e:
        print(e)
