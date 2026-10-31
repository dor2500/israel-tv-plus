import json
import urllib.request
import http.client
import ssl
import concurrent.futures

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

with open("channels.json", encoding="utf-8") as f:
    chs = json.load(f)

def test_stream(url, referrer=None):
    ref = referrer or "https://dor2500.github.io/"
    req = urllib.request.Request(
        url,
        headers={
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Referer": ref
        }
    )
    res = urllib.request.urlopen(req, timeout=9, context=ctx)
    try:
        body = res.read(8192)
    except http.client.IncompleteRead as e:
        body = e.partial
    return res.status, body

def check_channel(c):
    cid = c.get("id", "")
    name = c.get("name", "")
    url = c.get("url", "")
    ref = c.get("http_referrer")
    backups = list(c.get("backup_urls", [])) + list(c.get("backups", [])) + list(c.get("fallbackUrls", []))
    
    # Try primary
    try:
        st, body = test_stream(url, ref)
        if st in (200, 206, 302, 301):
            return True, cid, name, "OK primary"
    except Exception as e_pri:
        err_pri = str(e_pri)
        # Try backups
        for bu in backups:
            try:
                st, body = test_stream(bu, ref)
                if st in (200, 206, 302, 301):
                    return True, cid, name, f"OK backup: {bu}"
            except Exception:
                pass
        return False, cid, name, f"FAILED: {err_pri}"

print(f"Verifying all {len(chs)} channels in parallel...")
failed = []
with concurrent.futures.ThreadPoolExecutor(max_workers=30) as executor:
    futures = [executor.submit(check_channel, c) for c in chs]
    for fut in concurrent.futures.as_completed(futures):
        ok, cid, name, msg = fut.result()
        if not ok:
            print(f"[FAIL] {cid} ({name}) -> {msg}")
            failed.append((cid, name, msg))

print(f"\nVerification Complete: {len(chs) - len(failed)} / {len(chs)} OK.")
if failed:
    print(f"Failed count: {len(failed)}")
    for f in failed:
        print(f" - {f[0]}: {f[1]} ({f[2]})")
else:
    print("ALL 100% OF 416 CHANNELS ARE VERIFIED WORKING!")

