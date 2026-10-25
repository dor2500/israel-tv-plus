import os
import json
import re

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CACHE = os.path.join(ROOT, 'build', 'm3u_cache')

with open(os.path.join(ROOT, 'channels.json'), 'r', encoding='utf-8') as f:
    existing = json.load(f)

existing_urls = {c['url'].split('://', 1)[-1].rstrip('/') for c in existing}
print('Existing channels:', len(existing))

HOSTILE_RE = re.compile(r'jazeera|mayadeen|al-?manar|masirah|al[ -]?aqsa|al[ -]?quds|hezbollah|\bhamas\b|press ?tv|al[ -]?alam(?! al)|hispan ?tv|al[ -]?kawthar|sahar tv|\bifilm\b', re.I)

def is_bad(name, tvg_id, url, country, lang):
    if country in ('PS', 'IR') or lang in ('fa', 'per', 'fas', 'ar'):
        return True
    if any(x in (name + ' ' + tvg_id).lower() for x in ['xxx', 'adult', 'porn', 'erotic', '18+']):
        return True
    if bool(HOSTILE_RE.search(name + ' ' + tvg_id)):
        return True
    return False

all_urls = set(existing_urls)
new_count = 0

# 1. API streams
s_path = os.path.join(CACHE, 'streams.json')
c_path = os.path.join(CACHE, 'channels_api.json')
streams = json.load(open(s_path, 'r', encoding='utf-8'))
ch_api = json.load(open(c_path, 'r', encoding='utf-8'))
ch_map = {c['id']: c for c in ch_api}

for s in streams:
    url = s.get('url', '')
    if not ('.m3u8' in url or '.mpd' in url or 'live' in url or 'stream' in url or 'hls' in url):
        continue
    c_url = url.split('://', 1)[-1].rstrip('/')
    if c_url in all_urls:
        continue
    cid = s.get('channel')
    ch = ch_map.get(cid, {})
    name = s.get('title') or ch.get('name') or ''
    country = ch.get('country') or ''
    langs = ch.get('languages') or []
    lang = langs[0] if langs else ''
    if is_bad(name, cid or '', url, country, lang):
        continue
    all_urls.add(c_url)
    new_count += 1

print('After streams.json:', len(existing) + new_count)

# 2. M3U files
m3u_files = [
    os.path.join(CACHE, 'index.country.m3u'),
    os.path.join(CACHE, 'index.category.m3u'),
    os.path.join(CACHE, 'index.language.m3u'),
    os.path.join(CACHE, 'freetv_master.m3u'),
]
country_dir = os.path.join(CACHE, 'countries')
if os.path.exists(country_dir):
    for ctry in os.listdir(country_dir):
        m3u_files.append(os.path.join(country_dir, ctry))

# Local scratch files
SCRATCH = r"C:\Users\Dorp\.gemini\antigravity-ide\brain\3839f36e-c9ef-4bae-a3c3-f4dec3918250\scratch"
if os.path.exists(SCRATCH):
    for f in os.listdir(SCRATCH):
        if f.endswith('.m3u'):
            m3u_files.append(os.path.join(SCRATCH, f))

for mf in m3u_files:
    if not os.path.exists(mf):
        continue
    with open(mf, 'r', encoding='utf-8', errors='ignore') as f:
        cur_inf = None
        for line in f:
            line = line.strip()
            if line.startswith('#EXTINF:'):
                cur_inf = line
            elif line.startswith('http') and cur_inf:
                url = line
                inf = cur_inf
                cur_inf = None
                c_url = url.split('://', 1)[-1].rstrip('/')
                if c_url in all_urls:
                    continue
                name = inf.split(',')[-1].strip() if ',' in inf else ''
                id_m = re.search(r'tvg-id="([^"]+)"', inf)
                tvg_id = id_m.group(1).strip() if id_m else ''
                country_m = re.search(r'tvg-country="([^"]+)"', inf)
                country = country_m.group(1).strip().upper() if country_m else ''
                lang_m = re.search(r'tvg-language="([^"]+)"', inf)
                lang = lang_m.group(1).strip().lower() if lang_m else ''
                if is_bad(name, tvg_id, url, country, lang):
                    continue
                all_urls.add(c_url)
                new_count += 1

print('Total streams available without duplicates/hostile/Iran/Arabic:', len(existing) + new_count)
