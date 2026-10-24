import os
import re
import json
import requests
from concurrent.futures import ThreadPoolExecutor

SCRATCH = r'C:\Users\Dorp\.gemini\antigravity-ide\brain\3839f36e-c9ef-4bae-a3c3-f4dec3918250\scratch'
LOGOS_DIR = os.path.join('assets', 'logos')

with open('channels.json', 'r', encoding='utf-8') as f:
    existing_channels = json.load(f)

existing_ids = {c['id'] for c in existing_channels}
existing_urls = {c['url'] for c in existing_channels}

UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Safari/537.36"
headers = {"User-Agent": UA, "Referer": "https://dor2500.github.io/"}

def test_url(url):
    try:
        r = requests.get(url, headers=headers, timeout=2.0, stream=True)
        if r.status_code == 200:
            chunk = next(r.iter_content(512), b"")
            if b"#EXTM3U" in chunk or b"#EXT-X" in chunk or len(chunk) > 100:
                return True
    except Exception:
        pass
    return False

files_to_check = [
    ('iptvorg_sports.m3u', 'ספורט (Sports)', 'sports'),
    ('iptvorg_music.m3u', 'מוזיקה (Music TV)', 'music'),
    ('iptvorg_kids.m3u', 'ילדים מהעולם (Kids World)', 'kids'),
    ('iptvorg_cat_news.m3u', 'חדשות מהעולם (News)', 'news'),
]

candidates = []

for fname, cat_he, cat_en in files_to_check:
    fpath = os.path.join(SCRATCH, fname)
    if not os.path.exists(fpath):
        continue
    with open(fpath, 'r', encoding='utf-8', errors='ignore') as f:
        lines = f.readlines()
    
    current_meta = None
    for line in lines:
        line = line.strip()
        if line.startswith('#EXTINF:'):
            current_meta = line
        elif line.startswith('http') and current_meta:
            url = line
            if '.m3u8' in url and url not in existing_urls:
                # Extract title after comma
                title = current_meta.split(',')[-1].strip() if ',' in current_meta else "Channel"
                clean_title = re.sub(r'[\(\[\{].*?[\)\]\}]', '', title).strip()
                cid = re.sub(r'[^a-zA-Z0-9_]', '', clean_title.lower().replace(' ', '_'))
                if cid and cid not in existing_ids:
                    candidates.append({
                        'id': cid,
                        'name': f"{clean_title} HD",
                        'title': clean_title,
                        'category': cat_he,
                        'categoryEn': cat_en,
                        'url': url,
                        'badge': 'HD',
                        'desc': f'שידור חי – {clean_title}'
                    })
            current_meta = None

print(f"Total candidates collected: {len(candidates)}")

working_channels = []

def worker(cand):
    if test_url(cand['url']):
        return cand
    return None

print("Probing candidates in parallel (batch 2)...")
with ThreadPoolExecutor(max_workers=30) as executor:
    sample = candidates[250:550]
    for res in executor.map(worker, sample):
        if res and res['id'] not in existing_ids and res['url'] not in existing_urls:
            working_channels.append(res)
            existing_ids.add(res['id'])
            existing_urls.add(res['url'])
            if len(working_channels) >= 50:
                break

print(f"Verified {len(working_channels)} active streams to add!")

for ch in working_channels:
    cid = ch['id']
    logo_file = f"{cid}.svg"
    logo_path = os.path.join(LOGOS_DIR, logo_file)
    if not os.path.exists(logo_path):
        svg_content = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <defs>
    <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#0f172a"/>
      <stop offset="100%" stop-color="#020617"/>
    </linearGradient>
  </defs>
  <rect x="1" y="1" width="298" height="118" rx="22" fill="url(#g)" stroke="#38bdf8" stroke-opacity="0.75" stroke-width="2.5"/>
  <rect x="110" y="104" width="80" height="5" rx="2.5" fill="#38bdf8"/>
  <text x="150" y="60" font-family="Heebo, Segoe UI, Arial, sans-serif" font-size="28" font-weight="800" fill="#ffffff" text-anchor="middle">{ch['title'][:18]}</text>
  <text x="150" y="90" font-family="Heebo, Segoe UI, Arial, sans-serif" font-size="16" font-weight="700" fill="#38bdf8" text-anchor="middle">{ch['categoryEn'].upper()}</text>
</svg>
'''
        with open(logo_path, 'w', encoding='utf-8') as lf:
            lf.write(svg_content)
    ch['logo'] = f"assets/logos/{logo_file}"

existing_channels.extend(working_channels)

with open('channels.json', 'w', encoding='utf-8') as f:
    json.dump(existing_channels, f, ensure_ascii=False, indent=2)

print(f"New total channels in channels.json: {len(existing_channels)}")
