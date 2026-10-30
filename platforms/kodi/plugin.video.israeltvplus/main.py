# -*- coding: utf-8 -*-
"""Israel TV+ for Kodi (19 Matrix and newer): categories, search and playback with automatic backup servers."""
import json
import os
import re
import ssl
import sys
import time
from urllib.error import HTTPError, URLError
from urllib.parse import parse_qsl, quote, unquote, urlencode, urlsplit
from urllib.request import Request, urlopen

import xbmc
import xbmcaddon
import xbmcgui
import xbmcplugin
import xbmcvfs

ADDON = xbmcaddon.Addon()
BASE_URL = sys.argv[0]
HANDLE = int(sys.argv[1])
SITE = "https://dor2500.github.io/israel-tv-plus/"
USER_AGENT = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Mobile Safari/537.36 IsraelTVPlus"
MAKO_ENTITLEMENT = "https://mass.mako.co.il/ClicksStatistics/entitlementsServicesV2.jsp"
CACHE_SECONDS = 6 * 3600
PROBE_TIMEOUT = 4
PROBE_BUDGET = 20
BUNDLED = os.path.join(ADDON.getAddonInfo("path"), "resources", "channels.json")


def fetch(url, timeout=10):
    with urlopen(Request(url, headers={"User-Agent": USER_AGENT}), timeout=timeout) as r:
        return r.read()


def load_channels():
    """The published list, kept for six hours; a stale copy or the list inside the add-on when offline."""
    folder = xbmcvfs.translatePath(ADDON.getAddonInfo("profile"))
    cache = os.path.join(folder, "channels.json")
    fresh = os.path.exists(cache) and time.time() - os.path.getmtime(cache) < CACHE_SECONDS
    if not fresh:
        try:
            data = json.loads(fetch(SITE + "channels.json").decode("utf-8"))
            os.makedirs(folder, exist_ok=True)
            with open(cache, "w", encoding="utf-8") as f:
                json.dump(data, f, ensure_ascii=False)
            return data
        except (OSError, ValueError) as e:
            xbmc.log("[Israel TV+] channel list refresh failed: %s" % e, xbmc.LOGWARNING)
    for path in (cache, BUNDLED):
        try:
            with open(path, encoding="utf-8") as f:
                return json.load(f)
        except (OSError, ValueError):
            continue
    return []


def short_category(name):
    return name.split(" (")[0].split(" - ")[0].strip()


def plugin_url(**query):
    return BASE_URL + "?" + urlencode(query)


def logo_url(ch):
    logo = ch.get("logo", "")
    if not logo:
        return ""
    return logo if logo.startswith("http") else SITE + quote(logo)


def candidates(ch):
    urls = [ch.get("url", "")] + ch.get("fallbackUrls", []) + ch.get("backups", [])
    seen = []
    for u in urls:
        u = u.strip()
        if u and u not in seen:
            seen.append(u)
    return seen


def add_folder(label, **query):
    li = xbmcgui.ListItem(label)
    li.setArt({"icon": ADDON.getAddonInfo("icon"), "fanart": ADDON.getAddonInfo("fanart")})
    xbmcplugin.addDirectoryItem(HANDLE, plugin_url(**query), li, True)


def add_channel(ch):
    name = ch.get("name", "")
    li = xbmcgui.ListItem(name)
    li.setProperty("IsPlayable", "true")
    logo = logo_url(ch)
    li.setArt({"icon": logo, "thumb": logo, "poster": logo, "fanart": ADDON.getAddonInfo("fanart")})
    plot = ch.get("desc", "")
    if ch.get("kind") == "radio":
        li.setInfo("music", {"title": name})
    elif hasattr(li, "getVideoInfoTag"):
        tag = li.getVideoInfoTag()   # Kodi 20+: setInfo is deprecated there
        tag.setTitle(name)
        tag.setPlot(plot)
    else:
        li.setInfo("video", {"title": name, "plot": plot})
    xbmcplugin.addDirectoryItem(HANDLE, plugin_url(action="play", id=ch.get("id", "")), li, False)


def show_root(channels):
    add_folder("[B]חיפוש[/B]", action="search")
    add_folder("כל הערוצים", action="list")
    names = []
    for ch in channels:
        name = short_category(ch.get("category", ""))
        if name and name not in names:
            names.append(name)
    for name in names:
        add_folder(name, action="list", category=name)
    xbmcplugin.endOfDirectory(HANDLE)


def show_list(channels, category=None):
    for ch in channels:
        if category is None or short_category(ch.get("category", "")) == category:
            add_channel(ch)
    xbmcplugin.setContent(HANDLE, "videos")
    xbmcplugin.endOfDirectory(HANDLE)


def show_search(channels):
    query = xbmcgui.Dialog().input("חיפוש ערוץ, קטגוריה או מספר").strip().lower()
    if not query:
        xbmcplugin.endOfDirectory(HANDLE, succeeded=False)
        return
    for ch in channels:
        text = " ".join(str(ch.get(k, "")) for k in ("name", "title", "category", "countryHe")).lower()
        if query in text or str(ch.get("number", "")) == query:
            add_channel(ch)
    xbmcplugin.setContent(HANDLE, "videos")
    xbmcplugin.endOfDirectory(HANDLE)


def with_ticket(url, auth):
    """Keshet's own CDN wants a viewing ticket from mako; every other stream plays as is."""
    if "mako-streaming.akamaized.net" not in url.lower() and not (auth == "mako" and "mako" in url.lower()):
        return url
    parts = urlsplit(url)
    path = parts.path + ("?" + parts.query if parts.query else "")
    text = fetch(MAKO_ENTITLEMENT + "?et=ngt&rv=AKAMAI&lp=" + quote(path, safe=""), PROBE_TIMEOUT).decode("utf-8", "replace")
    found = re.search(r'"ticket"\s*:\s*"([^"]+)"', text)
    if not found:
        raise ValueError("no mako ticket")
    return url + ("&" if "?" in url else "?") + unquote(found.group(1))


def answers(url):
    try:
        with urlopen(Request(url, headers={"User-Agent": USER_AGENT}), timeout=PROBE_TIMEOUT) as r:
            r.read(1)
        return True
    except HTTPError as e:
        return e.code < 400
    except URLError as e:
        # some Kodi builds ship no CA list: that says nothing about the stream, the player has its own TLS
        return isinstance(e.reason, ssl.SSLError)
    except (OSError, ValueError):
        return False


def resolve(ch):
    """The first server that answers, within a budget so a dead channel doesn't hang the UI."""
    deadline = time.time() + PROBE_BUDGET
    for url in candidates(ch):
        if time.time() > deadline:
            break
        try:
            playable = with_ticket(url, ch.get("auth", ""))
        except (OSError, ValueError):
            continue
        if answers(playable):
            return playable
    return None


def play(channels, channel_id):
    ch = next((c for c in channels if c.get("id") == channel_id), None)
    url = resolve(ch) if ch else None
    if not url:
        xbmcgui.Dialog().notification("ישראל TV+", "שידור לא זמין", xbmcgui.NOTIFICATION_WARNING)
        xbmcplugin.setResolvedUrl(HANDLE, False, xbmcgui.ListItem())
        return
    li = xbmcgui.ListItem(ch.get("name", ""), path=url + "|User-Agent=" + quote(USER_AGENT))
    if ".m3u8" in url.lower():
        li.setMimeType("application/vnd.apple.mpegurl")
        li.setContentLookup(False)
    xbmcplugin.setResolvedUrl(HANDLE, True, li)


def main():
    params = dict(parse_qsl(sys.argv[2].lstrip("?")))
    channels = load_channels()
    action = params.get("action")
    if action == "list":
        show_list(channels, params.get("category"))
    elif action == "search":
        show_search(channels)
    elif action == "play":
        play(channels, params.get("id"))
    else:
        show_root(channels)


if __name__ == "__main__":
    main()
