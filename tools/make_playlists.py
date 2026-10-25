"""M3U playlists for IPTV players (Kodi PVR IPTV Simple, VLC, TiviMate, IPTV Smarters, OTT Navigator, Perfect Player,
Enigma2, iPlayTV / Channels on Apple TV, GSE, Smart IPTV ...), served by GitHub Pages:

  playlist.m3u          everything (TV + radio)
  playlist-israel.m3u   Israeli TV, Hebrew kids and Israeli radio
  playlist-tv.m3u       TV only
  radio.m3u             radio only

Players use the original stream URLs (no browser limits). Most players can't draw SVG, so local SVG logos are
rasterised to playlist-logos/*.png (outside assets/, so the apps don't carry them) with headless Microsoft Edge.

usage: python -I tools/make_playlists.py
"""
import json
import os
import subprocess

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SITE = "https://dor2500.github.io/israel-tv-plus/"
EPG = "https://epgshare01.online/epgshare01/epg_ripper_IL1.xml.gz"
EDGE = [p for p in (r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
                    r"C:\Program Files\Microsoft\Edge\Application\msedge.exe") if os.path.isfile(p)]
ISRAELI = ("ערוצי ישראל - שידור חי", "ילדים ונוער (בעברית)", "רדיו ישראלי (Israeli Radio)", "מסורת וקהילה")


def png_logo(svg_rel):
    """assets/logos/x.svg -> playlist-logos/x.png (600x240), drawn by Edge; '' when it can't be made."""
    svg = os.path.join(ROOT, svg_rel)
    if not os.path.isfile(svg):
        return ""
    rel = "playlist-logos/" + os.path.splitext(os.path.basename(svg_rel))[0] + ".png"
    png = os.path.join(ROOT, rel)
    if os.path.isfile(png) and os.path.getmtime(png) >= os.path.getmtime(svg):
        return rel
    if not EDGE:
        return ""
    os.makedirs(os.path.dirname(png), exist_ok=True)
    subprocess.run([EDGE[0], "--headless", "--disable-gpu", "--hide-scrollbars", "--default-background-color=00000000",
                    "--force-device-scale-factor=2", "--window-size=300,120", "--screenshot=" + png,
                    "file:///" + svg.replace("\\", "/")], capture_output=True, timeout=60)
    return rel if os.path.isfile(png) else ""


def logo_url(c):
    logo = c.get("logo") or ""
    if not logo:
        return ""
    if logo.startswith("http"):
        return logo
    if logo.endswith(".svg"):
        logo = png_logo(logo) or logo
    return SITE + logo


def group(c):
    """Hebrew category name without the English part in brackets ("ילדים ונוער (בעברית)" stays whole)."""
    cat = c["category"].replace(" - שידור חי", "")
    head, _, tail = cat.partition(" (")
    return head if tail and any("a" <= ch.lower() <= "z" for ch in tail) else cat


def entry(c):
    attrs = [f'tvg-id="{c.get("iptv", "")}"', f'tvg-name="{c["name"]}"', f'tvg-chno="{c["number"]}"',
             f'tvg-logo="{logo_url(c)}"', f'group-title="{group(c)}"']
    if c.get("kind") == "radio":
        attrs.append('radio="true"')
    name = c["name"].replace("\u2066", "").replace("\u2069", "").replace(",", " ")
    return f'#EXTINF:-1 {" ".join(attrs)},{name}\n{c["url"]}\n'


def write(name, items):
    with open(os.path.join(ROOT, name), "w", encoding="utf-8", newline="\n") as f:
        f.write(f'#EXTM3U x-tvg-url="{EPG}"\n')
        for c in items:
            f.write(entry(c))
    print(f"{name}: {len(items)}")


def main():
    with open(os.path.join(ROOT, "channels.json"), encoding="utf-8") as f:
        # channels that need a viewing ticket from the broadcaster (Keshet on mako) only play in the apps
        chans = [c for c in json.load(f) if c.get("kind") != "app" and not c.get("auth")]
    write("playlist.m3u", chans)
    write("playlist-israel.m3u", [c for c in chans if c["category"] in ISRAELI])
    write("playlist-tv.m3u", [c for c in chans if c.get("kind") != "radio"])
    write("radio.m3u", [c for c in chans if c.get("kind") == "radio"])


if __name__ == "__main__":
    main()
