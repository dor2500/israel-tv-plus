"""Checks every channel: playlist reachable, redirects, CORS (needed by the web app), and grabs one
video frame with ffmpeg so the picture can be compared with the channel name.

usage: python -I tools/check_channels.py [out_dir] [channels.json] [--ids id1,id2]
writes <out>/report.json, <out>/frames/<id>.jpg and contact sheets <out>/sheet_XX.jpg
"""
import json
import os
import subprocess
import sys
from concurrent.futures import ThreadPoolExecutor

import requests
from PIL import Image, ImageDraw, ImageFont

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Safari/537.36"
HEADERS = {"User-Agent": UA, "Origin": "https://dor2500.github.io", "Referer": "https://dor2500.github.io/"}


def probe(ch, out):
    url = ch["url"]
    r = {"id": ch["id"], "number": ch.get("number"), "name": ch["name"], "url": url}
    try:
        resp = requests.get(url, headers=HEADERS, timeout=15, stream=True, allow_redirects=True)
        r["status"] = resp.status_code
        r["final_url"] = resp.url
        r["redirected"] = resp.url != url
        r["cors"] = resp.headers.get("Access-Control-Allow-Origin", "")
        r["ctype"] = resp.headers.get("Content-Type", "")
        head = b""
        for chunk in resp.iter_content(4096):
            head += chunk
            if len(head) >= 4096:
                break
        resp.close()
        r["is_m3u8"] = head.lstrip().startswith(b"#EXTM3U")
    except Exception as e:  # noqa: BLE001
        r["status"] = 0
        r["error"] = str(e)[:200]
    audio_only = ch.get("lang") == "music" and ".m3u8" not in url
    r["audio_only"] = audio_only
    if r.get("status") == 200 and not audio_only:
        jpg = os.path.join(out, "frames", ch["id"] + ".jpg")
        cmd = ["ffmpeg", "-hide_banner", "-loglevel", "error", "-y", "-user_agent", UA, "-rw_timeout", "15000000",
               "-i", url, "-ss", "3", "-frames:v", "1", "-vf", "scale=480:-2", jpg]
        try:
            p = subprocess.run(cmd, capture_output=True, timeout=60, text=True)
            r["frame"] = os.path.isfile(jpg) and p.returncode == 0
            if not r["frame"]:
                r["ffmpeg_error"] = (p.stderr or "")[-200:]
        except subprocess.TimeoutExpired:
            r["frame"] = False
            r["ffmpeg_error"] = "timeout"
        try:
            p = subprocess.run(["ffprobe", "-v", "error", "-user_agent", UA, "-rw_timeout", "15000000",
                                "-show_entries", "stream=codec_type,codec_name,width,height:format_tags=service_name,service_provider",
                                "-of", "json", url], capture_output=True, timeout=40, text=True)
            info = json.loads(p.stdout or "{}")
            r["streams"] = [{k: s.get(k) for k in ("codec_type", "codec_name", "width", "height") if s.get(k)} for s in info.get("streams", [])]
            r["service"] = (info.get("format", {}).get("tags") or {})
        except Exception:  # noqa: BLE001
            pass
    return r


def sheets(results, out):
    font = ImageFont.truetype("C:/Windows/Fonts/arialbd.ttf", 18)
    items = [r for r in results if r.get("frame")]
    per = 12
    for s in range(0, len(items), per):
        sheet = Image.new("RGB", (4 * 480, 3 * 300), "black")
        d = ImageDraw.Draw(sheet)
        for i, r in enumerate(items[s:s + per]):
            im = Image.open(os.path.join(out, "frames", r["id"] + ".jpg")).convert("RGB")
            im.thumbnail((480, 270))
            x, y = (i % 4) * 480, (i // 4) * 300
            sheet.paste(im, (x, y))
            d.rectangle([x, y + 270, x + 480, y + 300], fill=(30, 30, 30))
            d.text((x + 6, y + 274), f"#{r['number']} {r['id']}", fill="yellow", font=font)
        sheet.save(os.path.join(out, f"sheet_{s // per:02d}.jpg"), quality=80)


def main():
    out = sys.argv[1] if len(sys.argv) > 1 and not sys.argv[1].startswith("--") else os.path.join(ROOT, "..", "israel-tv-plus-work", "check")
    src = sys.argv[2] if len(sys.argv) > 2 and not sys.argv[2].startswith("--") else os.path.join(ROOT, "channels.json")
    ids = None
    if "--ids" in sys.argv:
        ids = set(sys.argv[sys.argv.index("--ids") + 1].split(","))
    os.makedirs(os.path.join(out, "frames"), exist_ok=True)
    with open(src, encoding="utf-8") as f:
        chans = json.load(f)
    if ids:
        chans = [c for c in chans if c["id"] in ids]
    with ThreadPoolExecutor(10) as ex:
        results = list(ex.map(lambda c: probe(c, out), chans))
    with open(os.path.join(out, "report.json"), "w", encoding="utf-8") as f:
        json.dump(results, f, ensure_ascii=False, indent=1)
    sheets(results, out)
    ok = sum(1 for r in results if r.get("status") == 200)
    print(f"{len(results)} channels, playlist OK {ok}, frames {sum(1 for r in results if r.get('frame'))}")
    for r in results:
        if r.get("status") != 200 or (not r.get("frame") and not r.get("audio_only")):
            print("FAIL", r["number"], r["id"], r.get("status"), r.get("error", r.get("ffmpeg_error", ""))[:120])


if __name__ == "__main__":
    main()
