import json
import os

with open('channels.json', 'r', encoding='utf-8') as f:
    chs = json.load(f)

logos_dir = os.path.join('assets', 'logos')
available_logos = set(os.listdir(logos_dir))

# Explicit mapping for core channels
channel_logo_map = {
    'now14': 'assets/logos/now14.svg',
    'i24news_he': 'assets/logos/i24news_he.svg',
    'i24news_fr': 'assets/logos/i24news_fr.svg',
    'i24news_ar': 'assets/logos/i24news_ar.svg',
    'good_life_israel': 'assets/logos/good_life_israel.svg',
    'hidabroot': 'assets/logos/hidabroot.svg',
    'hala_tv': 'assets/logos/hala_tv.svg',
    'kabbalah': 'assets/logos/kabbalah.svg',
    'shelanu_tv': 'assets/logos/shelanu_tv.svg',
    'channel16': 'assets/logos/channel16.svg',
    'viju_sport': 'assets/logos/viju_sport.svg',
    'extreme_sports_hd': 'assets/logos/extreme_sports_hd.svg',
    'yam_tichoni_hd': 'assets/logos/yam_tichoni_hd.svg',
    'premialnoe': 'assets/logos/premialnoe.svg',
    'ostrosyuzhetnoye': 'assets/logos/ostrosyuzhetnoye.svg',
    'kinouzhas': 'assets/logos/kinouzhas.svg',
    'kinohit': 'assets/logos/kinohit.svg',
    'kinosemja': 'assets/logos/kinosemja.svg',
    'megahit': 'assets/logos/megahit.svg',
    'premiere': 'assets/logos/premiere.svg',
    'comedy_ru': 'assets/logos/comedy_ru.svg',
    'action_tv1000': 'assets/logos/action_tv1000.svg',
    'tv1000_classic': 'assets/logos/tv1000_classic.svg',
    'serial_tv': 'assets/logos/serial_tv.svg',
    'rewind_80s': 'assets/logos/rewind_80s.svg',
    'cinema_70s': 'assets/logos/cinema_70s.svg',
    'replay_00s': 'assets/logos/replay_00s.svg',
    'simpsons_247': 'assets/logos/simpsons_247.svg',
    'cineman_comedy_hd': 'assets/logos/cineman_comedy_hd.svg',
    'cineman_vhs_retro': 'assets/logos/cineman_vhs_retro.svg',
    'comedy_channel': 'assets/logos/comedy_channel.svg',
    'gulli_kids_hd': 'assets/logos/gulli_kids_hd.svg',
    'ducktv_plus_hd': 'assets/logos/ducktv_plus_hd.svg',
    'ducktv_hd': 'assets/logos/ducktv_hd.svg',
    'cartoons_short_hd': 'assets/logos/cartoons_short_hd.svg',
    'lego_channel_hd': 'assets/logos/lego_channel_hd.svg',
    'tiji_kids_hd': 'assets/logos/tiji_kids_hd.svg',
    'jimjam_kids_hd': 'assets/logos/jimjam_kids_hd.svg',
    'fresh_kids_hd': 'assets/logos/fresh_kids_hd.svg',
    'cartoons_90s': 'assets/logos/cartoons_90s.svg',
    'cartoons_big_hd': 'assets/logos/cartoons_big_hd.svg',
    'kinokazka': 'assets/logos/kinokazka.svg',
    'pixel_tv': 'assets/logos/pixel_tv.svg',
    'niki_kids': 'assets/logos/niki_kids.svg',
    'kapitan_fantastika': 'assets/logos/kapitan_fantastika.svg',
    'okids': 'assets/logos/okids.svg',
    'babyshark_tv_hd': 'assets/logos/babyshark_tv_hd.svg',
    'music_4ever': 'assets/logos/music_4ever.svg',
    'music_4fun': 'assets/logos/music_4fun.svg',
    'music_vivalatino': 'assets/logos/music_vivalatino.svg',
    'music_1hd': 'assets/logos/music_1hd.svg',
    'music_retro7080': 'assets/logos/music_retro7080.svg',
    'viju_planet': 'assets/logos/viju_planet.svg',
    'asharq_discovery': 'assets/logos/asharq_discovery.svg',
    'history_365': 'assets/logos/history_365.svg',
    'vacation_channel': 'assets/logos/vacation_channel.svg',
    'viju_explore': 'assets/logos/viju_explore.svg'
}

count_updated = 0
for c in chs:
    cid = c.get('id')
    if cid in channel_logo_map:
        c['logo'] = channel_logo_map[cid]
        count_updated += 1
    elif not c.get('logo'):
        # Check if an SVG with this id exists
        expected_svg = f"{cid}.svg"
        if expected_svg in available_logos:
            c['logo'] = f"assets/logos/{expected_svg}"
            count_updated += 1
        else:
            # Generate a nice themed SVG logo for this channel
            name = c.get('name', cid)
            short_name = name.split(' – ')[0].split(' HD')[0].strip()
            cat = c.get('category', '')
            svg_content = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <defs>
    <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#1e293b"/>
      <stop offset="100%" stop-color="#0f172a"/>
    </linearGradient>
  </defs>
  <rect x="1" y="1" width="298" height="118" rx="22" fill="url(#g)" stroke="#38bdf8" stroke-opacity="0.6" stroke-width="2.5"/>
  <rect x="110" y="104" width="80" height="5" rx="2.5" fill="#38bdf8"/>
  <text x="150" y="60" font-family="Heebo, Segoe UI, Arial, sans-serif" font-size="28" font-weight="800" fill="#ffffff" text-anchor="middle">{short_name[:18]}</text>
  <text x="150" y="90" font-family="Heebo, Segoe UI, Arial, sans-serif" font-size="16" font-weight="700" fill="#38bdf8" text-anchor="middle">{cat[:20]}</text>
</svg>
'''
            out_file = f"{cid}.svg"
            with open(os.path.join(logos_dir, out_file), 'w', encoding='utf-8') as lf:
                lf.write(svg_content)
            available_logos.add(out_file)
            c['logo'] = f"assets/logos/{out_file}"
            count_updated += 1

with open('channels.json', 'w', encoding='utf-8') as f:
    json.dump(chs, f, ensure_ascii=False, indent=2)

print(f"Updated logos for {count_updated} channels. Total channels: {len(chs)}")
still_missing = sum(1 for c in chs if not c.get('logo'))
print(f"Channels still missing logo: {still_missing}")
