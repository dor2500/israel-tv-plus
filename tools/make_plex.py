"""
Generates plex.m3u and epg.xml specifically formatted for Plex Live TV & DVR.
Compatible with Plex Media Server, xTeVe, Threadfin, and Plex app on LG webOS.
"""
import json
import os
import re
import datetime

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SITE = "https://dor2500.github.io/israel-tv-plus/"


def epoch_to_plex_time(epoch_minutes):
    """Epoch minutes -> 20261031200000 +0000 (XMLTV format)"""
    dt = datetime.datetime.fromtimestamp(epoch_minutes * 60, datetime.timezone.utc)
    return dt.strftime("%Y%m%d%H%M%S +0000")


def xml_escape(s):
    if not s:
        return ""
    return (s.replace("&", "&amp;")
             .replace("<", "&lt;")
             .replace(">", "&gt;")
             .replace('"', "&quot;")
             .replace("'", "&apos;"))


def generate_plex():
    with open(os.path.join(ROOT, "channels.json"), "r", encoding="utf-8") as f:
        channels = json.load(f)

    with open(os.path.join(ROOT, "epg.json"), "r", encoding="utf-8") as f:
        epg_data = json.load(f)

    epg_channels = epg_data.get("channels", {})

    # Filter out radio for Plex TV Tuner (Plex Live TV is for video channels)
    tv_channels = [c for c in channels if "רדיו" not in c.get("category", "") and c.get("url")]

    # 1. Generate plex.m3u
    m3u_lines = ["#EXTM3U url-tvg=\"https://dor2500.github.io/israel-tv-plus/epg.xml\""]
    for ch in tv_channels:
        cid = ch.get("id", "")
        cname = ch.get("name", "")
        logo = ch.get("logo", "")
        if logo and not logo.startswith("http"):
            logo = SITE + logo.lstrip("/")
        cat = ch.get("category", "ערוצי ישראל")
        num = ch.get("number", "")
        url = ch.get("url", "")

        extinf = f'#EXTINF:-1 tvg-id="{cid}" tvg-name="{cname}" tvg-logo="{logo}" tvg-chno="{num}" group-title="{cat}",{cname}'
        m3u_lines.append(extinf)
        m3u_lines.append(url)

    plex_m3u_content = "\n".join(m3u_lines) + "\n"

    with open(os.path.join(ROOT, "plex.m3u"), "w", encoding="utf-8") as f:
        f.write(plex_m3u_content)

    dist_dir = os.path.join(ROOT, "dist")
    if os.path.isdir(dist_dir):
        with open(os.path.join(dist_dir, "plex.m3u"), "w", encoding="utf-8") as f:
            f.write(plex_m3u_content)

    print(f"Generated plex.m3u with {len(tv_channels)} channels")

    # 2. Generate epg.xml (XMLTV for Plex)
    xml_lines = [
        '<?xml version="1.0" encoding="UTF-8"?>',
        '<!DOCTYPE tv SYSTEM "xmltv.dtd">',
        f'<tv generator-info-name="Israel TV+ Plex Guide" source-info-url="{SITE}">',
    ]

    for ch in tv_channels:
        cid = ch.get("id", "")
        cname = xml_escape(ch.get("name", ""))
        logo = ch.get("logo", "")
        if logo and not logo.startswith("http"):
            logo = SITE + logo.lstrip("/")
        logo = xml_escape(logo)

        xml_lines.append(f'  <channel id="{cid}">')
        xml_lines.append(f'    <display-name lang="he">{cname}</display-name>')
        if logo:
            xml_lines.append(f'    <icon src="{logo}" />')
        xml_lines.append('  </channel>')

    total_progs = 0
    for ch in tv_channels:
        cid = ch.get("id", "")
        progs = epg_channels.get(cid, [])
        for p in progs:
            # p = [start, duration, title, desc, icon]
            start_ep = p[0]
            stop_ep = p[0] + p[1]
            title = xml_escape(p[2])
            desc = xml_escape(p[3] if len(p) > 3 else "")
            icon = xml_escape(p[4] if len(p) > 4 else "")

            start_str = epoch_to_plex_time(start_ep)
            stop_str = epoch_to_plex_time(stop_ep)

            xml_lines.append(f'  <programme start="{start_str}" stop="{stop_str}" channel="{cid}">')
            xml_lines.append(f'    <title lang="he">{title}</title>')
            if desc:
                xml_lines.append(f'    <desc lang="he">{desc}</desc>')
            if icon:
                xml_lines.append(f'    <icon src="{icon}" />')
            xml_lines.append('  </programme>')
            total_progs += 1

    xml_lines.append('</tv>')
    xml_content = "\n".join(xml_lines) + "\n"

    with open(os.path.join(ROOT, "epg.xml"), "w", encoding="utf-8") as f:
        f.write(xml_content)

    if os.path.isdir(dist_dir):
        with open(os.path.join(dist_dir, "epg.xml"), "w", encoding="utf-8") as f:
            f.write(xml_content)

    print(f"Generated epg.xml with {total_progs} programmes for Plex ({os.path.getsize(os.path.join(ROOT, 'epg.xml')) // 1024} KB)")


if __name__ == "__main__":
    generate_plex()
