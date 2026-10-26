#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Minutely Israeli Channel Harvester & Local Pool Updater
======================================================
- Probes and collects active, high-interest Israeli TV & Radio streams across public web sources.
- STRICT SKIP 1: Skips all Idan Plus (עידן פלוס) broadcast channels:
    Kan 11, Keshet 12, Reshet 13, Now 14, Channel 9, Kan 23 Educational, Makan 33, Knesset 99,
    and standard national broadcast radio (Galgalatz, GLZ, Kan Bet, Kan Gimel, etc.).
- STRICT SKIP 2: Skips anything ALREADY EXISTING in the project (channels.json / channels.js):
    "מה שקיים בפרוייקט כבר תמחק ושלא יוסיף" -> Pool contains ONLY NEW, unintegrated discoveries!
- FOCUSES ON: Truly interesting, NEW Israeli streams across web sources (FreeTV, IPTV-org, Radio-Browser, Web streams).
- STRICT RULE: Saves strictly locally into tools/israeli_channels_local_pool.json.
  DO NOT commit or push to GitHub!
"""

import os
import sys
import json
import time
import datetime
import urllib.request
import urllib.parse
import ssl
import re
import concurrent.futures

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LOCAL_POOL_PATH = os.path.join(ROOT, "tools", "israeli_channels_local_pool.json")
DISCOVERED_PATH = os.path.join(ROOT, "tools", "discovered_streams.json")
LOG_PATH = os.path.join(ROOT, "tools", "harvester_log.txt")
CHANNELS_PATH = os.path.join(ROOT, "channels.json")

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Safari/537.36",
    "Accept": "*/*"
}

# Explicit blacklist patterns for Idan Plus (עידן+) channels to skip entirely:
IDAN_PLUS_PATTERNS = [
    # TV Broadcasters
    "kan 11", "kan11", "כאן 11", "channel 11", "ch 11", "ch11", "kan_11", "כאן11", "כאן חדשות",
    "keshet 12", "keshet12", "קשת 12", "channel 12", "ch 12", "ch12", "keshet_12", "ערוץ 12",
    "reshet 13", "reshet13", "רשת 13", "channel 13", "ch 13", "ch13", "reshet_13", "ערוץ 13",
    "now 14", "now14", "channel 14", "ch 14", "ch14", "עכשיו 14", "ערוץ 14", "ch14", "now_14",
    "channel 9", "ch 9", "ch9", "ערוץ 9", "ch_9",
    "kan educational", "חינוכית", "kan 23", "kan23", "כאן חינוכית", "kan_edu",
    "makan", "מכאן", "channel 33", "ch 33", "ch33", "ערוץ 33",
    "knesset", "כנסת", "channel 99", "ch 99", "ch99", "ערוץ 99", "ערוץ הכנסת",
    # Terrestrial national radio
    "galgalatz", "גלגלצ", "glglz",
    "glz", "גלי צה\"ל", "גלי צהל", "גלי צה",
    "kan_bet", "כאן ב", "רשת ב",
    "kan_gimel", "כאן גימל", "רשת ג",
    "kan_88", "כאן 88",
    "kan_tarbut", "כאן תרבות",
    "kan_moreshet", "כאן מורשת",
    "kan_reka", "כאן רקע",
    "kan_kol_hamusica", "קול המוזיקה"
]

def log(msg):
    ts = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    line = f"[{ts}] {msg}"
    print(line, flush=True)
    try:
        with open(LOG_PATH, "a", encoding="utf-8") as f:
            f.write(line + "\n")
    except Exception:
        pass

def load_project_channels():
    """Load existing channels in channels.json to enforce strict non-duplication."""
    existing_ids = set()
    existing_urls = set()
    existing_names = set()
    
    if os.path.isfile(CHANNELS_PATH):
        try:
            with open(CHANNELS_PATH, "r", encoding="utf-8") as f:
                chans = json.load(f)
            if isinstance(chans, dict):
                chans = chans.get("channels", [])
            for c in chans:
                cid = str(c.get("id", "")).strip().lower()
                url = str(c.get("url", "")).strip().lower()
                name = str(c.get("name", "")).strip().lower()
                title = str(c.get("title", "")).strip().lower()
                if cid:
                    existing_ids.add(cid)
                if url:
                    # Strip trailing query/slash for fuzzy matching
                    clean_u = re.sub(r'\?.*$', '', url).rstrip('/')
                    existing_urls.add(url)
                    existing_urls.add(clean_u)
                if name:
                    existing_names.add(name)
                if title:
                    existing_names.add(title)
        except Exception as e:
            log(f"Notice: failed loading project channels ({e})")
            
    return existing_ids, existing_urls, existing_names

PROJECT_IDS, PROJECT_URLS, PROJECT_NAMES = load_project_channels()

PROJECT_BRAND_PATTERNS = [
    # Networks & Broadcasters
    "kan 11", "kan11", "כאן 11", "keshet 12", "keshet12", "קשת 12", "ערוץ 12", "reshet 13", "reshet13", "רשת 13", "ערוץ 13",
    "channel 14", "now 14", "עכשיו 14", "ערוץ 14", "channel 9", "ערוץ 9", "kan 23", "חינוכית", "makan", "מכאן",
    "knesset", "כנסת",
    # Cable & Satellite
    "hot ", "hot_", "הוט", "yes ", "yes_", "יס ", "ויוה", "viva",
    # Sports in project
    "sport 1", "sport 2", "sport 3", "sport 4", "sport 5", "ספורט 1", "ספורט 2", "ספורט 3", "ספורט 4", "ספורט 5", "ערוץ one", "one hd", "one 2", "edge sport",
    # Entertainment in project
    "סברי מרנן", "savri", "ארץ נהדרת", "eretz", "erets", "ערוץ 24", "channel 24", "כלכלה 10", "calcala", "ynet",
    # Radio stations already in project
    "galgalatz", "גלגלצ", "glz", "גלצ", "גלי צהל", "גלי צה\"ל",
    "kan bet", "כאן ב", "רשת ב", "kan gimel", "כאן גימל", "רשת ג", "kan 88", "כאן 88", "kan tarbut", "כאן תרבות",
    "kan moreshet", "כאן מורשת", "kan reka", "כאן רקע", "קול המוזיקה",
    "100fm", "רדיוס", "radios", "102fm", "תל אביב", "tel aviv", "103fm", "ללא הפסקה", "99fm", "eco99", "אקו 99", "eco 99",
    "90fm", "אמצע הדרך", "101.5", "דרום", "darom", "התחנה", "107.5", "חיפה", "radio haifa", "104.5", "צפון", "radio tzafon",
    "101fm", "ירושלים", "radio jerusalem", "לב המדינה", "lev hamedina", "91fm", "גלי ישראל", "galei israel",
    "קול חי", "kol chai", "kol hai", "קול ברמה", "kol barama", "קול פליי", "kol play", "קול רגע", "kol rega",
    "הקצה", "hakatze", "kz radio", "רדיו 2000", "נושמים מזרחית", "noshmim", "מזרחית fm", "mizrahit fm",
    "פרבואה", "pervoe radio", "89.1", "ברסלב", "breslev", "קבלה לעם", "kabbalah", "ג'וינט", "joint radio",
    "א-שמס", "ashams", "a-shams", "נאס", "radio nas", "נהריה", "nahariya", "אילת", "eilat", "קול הים האדום",
    "ivri 6", "עברי 6", "ystream", "up2dance", "star 33", "רק מוזיקה", "rak musica", "radio qualita", "קוואליטה",
    "radioplus", "radio plus", "aviv media", "cs קלאסי", "radio cs", "radio france israel"
]

def is_already_in_project(cid="", url="", name=""):
    """Check if channel is already integrated in channels.json (exact or brand match)."""
    c_low = str(cid).strip().lower()
    u_low = str(url).strip().lower()
    clean_u = re.sub(r'\?.*$', '', u_low).rstrip('/')
    n_low = str(name).strip().lower()

    if c_low and c_low in PROJECT_IDS:
        return True
    if u_low and (u_low in PROJECT_URLS or clean_u in PROJECT_URLS):
        return True
    if n_low and n_low in PROJECT_NAMES:
        return True
    
    # Check brand patterns: if any brand exists in channels.json, skip it
    combined = f"{c_low} {n_low} {u_low}"
    for pat in PROJECT_BRAND_PATTERNS:
        if pat in combined:
            return True
            
    return False

def is_idan_plus(name, cid="", url=""):
    combined = f"{name} {cid} {url}".lower()
    for pat in IDAN_PLUS_PATTERNS:
        if pat in combined:
            return True
    return False

def get_mako_ticket(url):
    """Retrieve viewing token from Mako/FreeTV mass entitlements for Akamai CDN streams."""
    try:
        p = urllib.parse.urlsplit(url)
        lp = p.path + ("?" + p.query if p.query else "")
        ent_url = "https://mass.mako.co.il/ClicksStatistics/entitlementsServicesV2.jsp?et=ngt&lp=" + urllib.parse.quote(lp) + "&rv=AKAMAI"
        req = urllib.request.Request(ent_url, headers=HEADERS)
        with urllib.request.urlopen(req, timeout=4, context=ctx) as r:
            data = json.loads(r.read().decode("utf-8"))
            ticket = urllib.parse.unquote_plus(data["tickets"][0]["ticket"])
            return url + ("&" if "?" in url else "?") + ticket
    except Exception:
        return url

def is_stream_live(url, auth="", timeout=3.5):
    if not url or not url.startswith("http"):
        return False
    if not any(k in url for k in [".m3u8", ".ts", ".mp3", ".aac", "/live", "stream", "livecdn", "streamguys", "akamaized", "80"]):
        if url.endswith((".html", ".htm", ".il/", ".co.il/", ".com/")):
            return False
    
    probe_url = url
    if auth == "mako" or "mako-streaming.akamaized.net" in url:
        probe_url = get_mako_ticket(url)

    try:
        req = urllib.request.Request(probe_url, headers=HEADERS)
        with urllib.request.urlopen(req, timeout=timeout, context=ctx) as r:
            code = r.getcode()
            if code not in (200, 206):
                return False
            chunk = r.read(1024)
            if b"#EXTM3U" in chunk or b"#EXT-X" in chunk:
                return True
            content_type = r.headers.get("Content-Type", "").lower()
            if any(t in content_type for t in ["mpegurl", "audio/", "video/", "octet-stream"]):
                return True
            return len(chunk) > 188
    except Exception:
        return False

def load_json(path, default=None):
    if default is None:
        default = {}
    if os.path.isfile(path):
        try:
            with open(path, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            return default
    return default

def save_json(path, data):
    with open(path, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

def fetch_iptv_org_israel():
    """Fetch iptv-org Israeli candidates, strictly skipping Idan+ and already-in-project channels."""
    candidates = []
    urls = [
        "https://iptv-org.github.io/iptv/countries/il.m3u",
        "https://raw.githubusercontent.com/iptv-org/iptv/master/streams/il.m3u"
    ]
    for url in urls:
        try:
            req = urllib.request.Request(url, headers=HEADERS)
            with urllib.request.urlopen(req, timeout=5, context=ctx) as r:
                content = r.read().decode("utf-8", errors="replace")
            lines = content.splitlines()
            curr_info = {}
            for line in lines:
                line = line.strip()
                if line.startswith("#EXTINF:"):
                    curr_info = {}
                    name_match = re.search(r',(.+)$', line)
                    if name_match:
                        raw_n = name_match.group(1).strip()
                        raw_n = re.sub(r'^.*group-title="[^"]*",', '', raw_n).strip()
                        raw_n = re.sub(r'^.*",', '', raw_n).strip()
                        curr_info["name"] = raw_n
                    logo_match = re.search(r'tvg-logo="([^"]+)"', line)
                    if logo_match:
                        curr_info["logo"] = logo_match.group(1).strip()
                    group_match = re.search(r'group-title="([^"]+)"', line)
                    if group_match:
                        curr_info["group"] = group_match.group(1).strip()
                elif line and not line.startswith("#"):
                    if "name" in curr_info:
                        name = curr_info["name"]
                        cid = re.sub(r'[^a-zA-Z0-9_]', '_', name.lower())[:32]
                        # Strictly skip Idan+ and channels already existing in project
                        if not is_idan_plus(name, cid=cid, url=line) and not is_already_in_project(cid=cid, url=line, name=name):
                            lower_name = name.lower()
                            is_foreign = any(w in lower_name for w in ["al jazeera", "palestine", "quran", "al quds", "al aqsa", "lebanon", "arabic", "french", "russian"])
                            if not is_foreign:
                                candidates.append({
                                    "id": cid,
                                    "name": name,
                                    "category": curr_info.get("group", "סרטים וסדרות"),
                                    "url": line,
                                    "logo": curr_info.get("logo", ""),
                                    "badge": "HD"
                                })
                        curr_info = {}
        except Exception as e:
            log(f"Notice: iptv-org fetch {url} ({e})")
    return candidates

def fetch_radio_browser_israel():
    """Fetch new Israeli internet stations from radio-browser.info (excluding project & Idan+)."""
    candidates = []
    api_url = "https://de1.api.radio-browser.info/json/stations/bycountrycodeexact/IL"
    try:
        req = urllib.request.Request(api_url, headers={"User-Agent": "IsraelTvPlus/4.3"})
        with urllib.request.urlopen(req, timeout=5, context=ctx) as r:
            stations = json.loads(r.read().decode("utf-8"))
        for s in stations:
            name = s.get("name", "").strip()
            url = s.get("url_resolved") or s.get("url", "").strip()
            cid = re.sub(r'[^a-zA-Z0-9_]', '_', name.lower())[:32]
            if not url or is_idan_plus(name, cid=cid, url=url) or is_already_in_project(cid=cid, url=url, name=name):
                continue
            # Drop non-Israeli stations that are mistakenly tagged under country IL
            low_name = name.lower()
            low_url = url.lower()
            if any(w in low_name or w in low_url for w in ["quran", "palestine", "alquds", "islam", "salafi", "assalam", "abdulbasit", "mp3islam", "qurango"]):
                continue
            candidates.append({
                "id": f"rb_{cid}",
                "name": name,
                "category": "רדיו ומוזיקה ישראלית",
                "url": url,
                "logo": s.get("favicon", ""),
                "badge": "LIVE"
            })
    except Exception as e:
        log(f"Notice: radio-browser fetch ({e})")
    return candidates

def get_specialty_external_seeds():
    """Curated list of verified external streams NOT in channels.json."""
    candidates = [
        # --- FreeTV New FAST channel ---
        {"id": "freetv_food", "name": "FreeTV ערוץ האוכל והבישול", "category": "FreeTV ערוצים",
         "url": "https://mako-streaming.akamaized.net/evrideo/hls/live/20001278/free_food/index.m3u8", "auth": "mako", "badge": "HD",
         "logo": "https://freetv.tv/wp-content/themes/freetv/assets/images/logo.svg"},
        # --- International & Specialty Israeli Broadcasters ---
        {"id": "i24_english_world_hd", "name": "i24NEWS English World HD", "category": "אקטואליה ועסקים",
         "url": "https://hlspackager.akamaized.net/live/DB/i24_ENGLISH/HLS/i24_ENGLISH.m3u8", "badge": "FHD",
         "logo": "https://www.i24news.tv/images/favicon.png"},
        {"id": "hala_tv_live", "name": "Hala TV – ערוץ הלא שידור חי HD", "category": "חדש ברשת",
         "url": "https://stream.panet.com/edge/halaTV/playlist.m3u8", "badge": "HD",
         "logo": "https://www.panet.co.il/images/logo.png"},
        {"id": "shelanu_tv_live", "name": "Shelanu.TV ערוץ שלנו שידור ישיר", "category": "חדש ברשת",
         "url": "https://1247634592.rsc.cdn77.org/1247634592/playlist.m3u8", "badge": "HD"},
        # --- Independent Israeli Studios & Campus Radio ---
        {"id": "audioversity_live", "name": "AudioVersity רדיו אוניברסיטת רייכמן", "category": "רדיו ומוזיקה ישראלית",
         "url": "https://1062onair.runi.ac.il/idc123.mp3", "badge": "חי"},
        {"id": "toker_fm_live", "name": "Toker FM רדיו מנחם טוקר שידור חי", "category": "רדיו ומוזיקה ישראלית",
         "url": "https://live.toker.fm:6060/", "badge": "חי"},
        {"id": "radio_oranim_live", "name": "רדיו מכללת אורנים שידור ישיר", "category": "רדיו ומוזיקה ישראלית",
         "url": "https://radio.oranim.ac.il/live", "badge": "חי"},
        {"id": "radio_angelika_live", "name": "Radio Angelika תל אביב שידור ישיר", "category": "רדיו ומוזיקה ישראלית",
         "url": "https://radioangelika.com/stream", "badge": "חי"},
        {"id": "dusha_kavkaza_live", "name": "Душа Кавказа – רדיו קהילת יוצאי קווקז בישראל", "category": "רדיו ומוזיקה ישראלית",
         "url": "https://www.kavkazportal.com/live", "badge": "חי"},
    ]
    return candidates

def run_minute_update():
    log("▶ [Minutely Harvester] Starting probe cycle for NEW Israeli channels (Skipping Idan+ and existing project channels)...")
    
    # Reload project channels to stay fully in sync with channels.json
    global PROJECT_IDS, PROJECT_URLS, PROJECT_NAMES
    PROJECT_IDS, PROJECT_URLS, PROJECT_NAMES = load_project_channels()
    
    pool = load_json(LOCAL_POOL_PATH, default={"last_updated": "", "total_verified": 0, "channels": {}})
    discovered = load_json(DISCOVERED_PATH, default={})
    channels_map = pool.get("channels", {})

    # PURGE: Delete any channel currently in pool that exists in channels.json or is Idan+
    purged_existing = 0
    purged_idan = 0
    clean_channels_map = {}
    for cid, cdata in channels_map.items():
        name = cdata.get("name", "")
        url = cdata.get("url", "")
        if is_idan_plus(name, cid=cid, url=url):
            purged_idan += 1
            log(f"  ✖ Purged Idan+ channel from local pool: {name} ({cid})")
        elif is_already_in_project(cid=cid, url=url, name=name):
            purged_existing += 1
            log(f"  ✖ Purged already-in-project channel: {name} ({cid})")
        else:
            clean_channels_map[cid] = cdata
    channels_map = clean_channels_map

    # Gather all candidate sources (ONLY external / new sources)
    raw_candidates = []
    raw_candidates.extend(get_specialty_external_seeds())
    raw_candidates.extend(fetch_iptv_org_israel())
    raw_candidates.extend(fetch_radio_browser_israel())

    # De-duplicate candidates and strictly verify not in project
    seen_urls = set()
    unique_candidates = []
    for item in raw_candidates:
        url = item.get("url", "").strip()
        name = item.get("name", "").strip()
        cid = item.get("id", "").strip()
        u_key = url.lower()
        if not url or u_key in seen_urls:
            continue
        if is_idan_plus(name, cid=cid, url=url):
            continue
        if is_already_in_project(cid=cid, url=url, name=name):
            continue
        seen_urls.add(u_key)
        unique_candidates.append(item)

    log(f"  Probing {len(unique_candidates)} unique NEW candidates across web (not in channels.json)...")

    # Multi-threaded validation
    now_iso = datetime.datetime.now().isoformat()
    new_verified_this_run = 0

    def check_candidate(cand):
        url = cand.get("url", "")
        auth = cand.get("auth", "")
        live = is_stream_live(url, auth=auth, timeout=3.5)
        return cand, live

    # URL to existing key map for clean deduplication
    url_to_key = {c.get("url", "").strip().lower(): k for k, c in channels_map.items() if c.get("url")}

    with concurrent.futures.ThreadPoolExecutor(max_workers=16) as executor:
        results = executor.map(check_candidate, unique_candidates)
        for cand, live in results:
            cid = cand.get("id") or re.sub(r'[^a-zA-Z0-9_]', '_', cand.get("name", ""))
            url = cand.get("url", "")
            auth = cand.get("auth", "")
            u_low = url.strip().lower()

            discovered[cid] = {
                "name": cand.get("name"),
                "url": url,
                "auth": auth,
                "category": cand.get("category", "חדש ברשת"),
                "badge": cand.get("badge", "HD"),
                "verified": live,
                "last_tested": now_iso
            }

            if live:
                target_key = url_to_key.get(u_low, cid)
                if target_key not in channels_map:
                    new_verified_this_run += 1
                    log(f"  ★ NEW VERIFIED CHANNEL: {cand.get('name')} [{cand.get('category')}]")
                    url_to_key[u_low] = target_key
                channels_map[target_key] = {
                    "id": target_key,
                    "name": cand.get("name"),
                    "category": cand.get("category", "חדש ברשת"),
                    "url": url,
                    "auth": auth,
                    "logo": cand.get("logo", ""),
                    "badge": cand.get("badge", "HD"),
                    "verified": True,
                    "last_verified": now_iso
                }

    pool["last_updated"] = now_iso
    pool["total_verified"] = len(channels_map)
    pool["channels"] = channels_map

    save_json(LOCAL_POOL_PATH, pool)
    save_json(DISCOVERED_PATH, discovered)

    log(f"✔ [Minutely Harvest] Completed. Verified pool size: {len(channels_map)} NEW channels (+{new_verified_this_run} new, {purged_existing} project channels deleted, {purged_idan} Idan+ deleted). Saved strictly locally (NOT pushed to git).")

if __name__ == "__main__":
    run_minute_update()
