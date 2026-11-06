import urllib.request

freetv_candidates = [
    'nickelodeon', 'nick', 'nick_il', 'nickelodeon_il', 'nick_kids', 'nick_plus', 'teennick'
]

print("Probing FreeTV for Nickelodeon:")
for c in freetv_candidates:
    url = f"https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/{c}/live.livx/playlist.m3u8"
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        res = urllib.request.urlopen(req, timeout=3)
        if res.status == 200:
            print(f"FreeTV MATCH: {c} -> 200 OK")
    except Exception:
        pass
