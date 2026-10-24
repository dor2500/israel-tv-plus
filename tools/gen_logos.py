import os

logos_to_create = {
    'i24news_he.svg': ('#0f172a', '#05070d', '#38bdf8', 'i24NEWS', 'עברית'),
    'i24news_fr.svg': ('#0f172a', '#05070d', '#38bdf8', 'i24NEWS', 'Français'),
    'i24news_ar.svg': ('#0f172a', '#05070d', '#38bdf8', 'i24NEWS', 'العربية'),
    'good_life_israel.svg': ('#1e1b4b', '#0f172a', '#ec4899', 'החיים הטובים', 'Good Life TV'),
    'hidabroot.svg': ('#064e3b', '#022c22', '#10b981', 'הידברות', 'Hidabroot HD'),
    'hala_tv.svg': ('#7c2d12', '#431407', '#f97316', 'Hala TV', 'הלא TV'),
    'kabbalah.svg': ('#1e3a8a', '#0f172a', '#60a5fa', 'קבלה לעם', 'Kabbalah TV'),
    'shelanu_tv.svg': ('#4c1d95', '#2e1065', '#a855f7', 'שלנו TV', 'Shelanu TV'),
    'channel16.svg': ('#1e293b', '#0f172a', '#38bdf8', 'ערוץ 16', 'ישראל'),
    'viju_sport.svg': ('#7f1d1d', '#450a0a', '#ef4444', 'viju+ Sport', 'ספורט פלוס HD'),
    'extreme_sports_hd.svg': ('#831843', '#500724', '#f43f5e', 'EXTREME', 'Extreme Sports HD'),
    'yam_tichoni_hd.svg': ('#0c4a6e', '#082f49', '#38bdf8', 'הים התיכון', 'Mediterranean TV'),
    'premialnoe.svg': ('#4c0519', '#2e020c', '#fb7185', 'סינמה פרימיום', 'Premiere HD'),
    'ostrosyuzhetnoye.svg': ('#3b0764', '#1e013a', '#c084fc', 'מתח ואקשן', 'Action Cinema HD'),
    'kinouzhas.svg': ('#450a0a', '#1c0404', '#f87171', 'סרטי אימה', 'Horror Cinema HD'),
    'kinohit.svg': ('#1e1b4b', '#0f172a', '#818cf8', 'קולנוע להיטים', 'KinoHit HD'),
    'kinosemja.svg': ('#064e3b', '#022c22', '#34d399', 'קולנוע משפחתי', 'Family Cinema HD'),
    'megahit.svg': ('#701a75', '#4a044e', '#e879f9', 'מגה להיטים', 'MegaHit Cinema HD'),
    'premiere.svg': ('#831843', '#500724', '#fb7185', 'סינמה בכורה', 'Premiere Cinema HD'),
    'comedy_ru.svg': ('#78350f', '#451a03', '#f59e0b', 'סינמה קומדיה', 'Comedy Cinema HD'),
    'action_tv1000.svg': ('#1e293b', '#0f172a', '#38bdf8', 'TV1000 אקשן', 'Action TV1000 HD'),
    'tv1000_classic.svg': ('#1e293b', '#0f172a', '#e2e8f0', 'TV1000 קלאסי', 'Classic Cinema'),
    'serial_tv.svg': ('#1e1b4b', '#0f172a', '#a78bfa', 'ערוץ הסדרות', 'Serial HD TV'),
    'rewind_80s.svg': ('#4a044e', '#2e0220', '#f472b6', 'REWIND 80s', 'קולנוע שנות ה-80'),
    'cinema_70s.svg': ('#451a03', '#270e02', '#fbbf24', 'CINEMA 70s', 'קולנוע שנות ה-70'),
    'replay_00s.svg': ('#0f172a', '#020617', '#38bdf8', 'REPLAY 00s', 'קולנוע שנות ה-2000'),
    'simpsons_247.svg': ('#713f12', '#422006', '#facc15', 'משפחת סימפסון', 'The Simpsons 24/7'),
    'cineman_comedy_hd.svg': ('#78350f', '#451a03', '#fbbf24', 'CineMan', 'Comedy HD'),
    'cineman_vhs_retro.svg': ('#312e81', '#1e1b4b', '#818cf8', 'VHS RETRO', 'Cinema Retro HD'),
    'comedy_channel.svg': ('#78350f', '#451a03', '#fbbf24', 'ערוץ הקומדיה', 'Comedy Channel HD'),
    'gulli_kids_hd.svg': ('#831843', '#500724', '#f472b6', 'Gulli Girl', 'גולי קידס HD'),
    'ducktv_plus_hd.svg': ('#713f12', '#422006', '#facc15', 'ducktv PLUS', 'דאק טי וי פלוס'),
    'ducktv_hd.svg': ('#713f12', '#422006', '#facc15', 'ducktv HD', 'דאק טי וי לילדים'),
    'cartoons_short_hd.svg': ('#064e3b', '#022c22', '#34d399', 'Cartoons Short', 'אנימציה קצרצרה HD'),
    'lego_channel_hd.svg': ('#7f1d1d', '#450a0a', '#ef4444', 'LEGO Channel', 'ערוץ לגו לילדים'),
    'tiji_kids_hd.svg': ('#1e1b4b', '#0f172a', '#818cf8', 'TiJi Kids', 'ערוץ טיז\'י לילדים'),
    'jimjam_kids_hd.svg': ('#78350f', '#451a03', '#f59e0b', 'JimJam', 'ג\'ים ג\'אם לילדים'),
    'fresh_kids_hd.svg': ('#047857', '#064e3b', '#10b981', 'Fresh Kids', 'אנימציה וילדים HD'),
    'cartoons_90s.svg': ('#581c87', '#3b0764', '#c084fc', 'Cartoons 90s', 'קלאסיקות מצוירות'),
    'cartoons_big_hd.svg': ('#1e3a8a', '#0f172a', '#60a5fa', 'Cartoons Big', 'סרטי אנימציה HD'),
    'kinokazka.svg': ('#065f46', '#022c22', '#6ee7b7', 'קולנוע אגדות', 'KinoKazka HD'),
    'pixel_tv.svg': ('#4338ca', '#1e1b4b', '#a5b4fc', 'פיקסל TV', 'Pixel TV Kids'),
    'niki_kids.svg': ('#be185d', '#831843', '#f472b6', 'ניקי קידס', 'Niki Kids HD'),
    'kapitan_fantastika.svg': ('#1e293b', '#0f172a', '#38bdf8', 'קפטן פנטסטיקה', 'Kapitan Fantastika'),
    'okids.svg': ('#047857', '#064e3b', '#34d399', 'ערוץ O!', 'ילדים וטבע'),
    'babyshark_tv_hd.svg': ('#0284c7', '#0369a1', '#facc15', 'Baby Shark', 'בייבי שארק TV'),
    'music_4ever.svg': ('#701a75', '#4a044e', '#f472b6', '4ever Music', 'Music Hits HD'),
    'music_4fun.svg': ('#be123c', '#881337', '#fda4af', '4FUN TV', 'Pop Hits HD'),
    'music_vivalatino.svg': ('#c2410c', '#7c2d12', '#fdba74', '¡Viva Latino!', 'Latin Dance HD'),
    'music_1hd.svg': ('#0f172a', '#020617', '#38bdf8', '1HD Music', 'Television'),
    'music_retro7080.svg': ('#854d0e', '#713f12', '#fde047', '70-80 TV', 'Retro Hits HD'),
    'viju_planet.svg': ('#065f46', '#022c22', '#6ee7b7', 'viju+ Planet', 'עולם וטבע HD'),
    'asharq_discovery.svg': ('#78350f', '#451a03', '#f59e0b', 'Asharq Discovery', 'אשארק דיסקברי HD'),
    'history_365.svg': ('#854d0e', '#713f12', '#fde047', '365 היסטוריה', 'History TV'),
    'vacation_channel.svg': ('#0369a1', '#075985', '#38bdf8', 'ערוץ החופשות', 'Vacation TV HD'),
    'viju_explore.svg': ('#1e3a8a', '#0f172a', '#60a5fa', 'viju Explore', 'ערוץ החקר והמדע')
}

out_dir = os.path.join('assets', 'logos')
os.makedirs(out_dir, exist_ok=True)
for fname, (col1, col2, accent, title, sub) in logos_to_create.items():
    svg_content = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <defs>
    <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="{col1}"/>
      <stop offset="100%" stop-color="{col2}"/>
    </linearGradient>
  </defs>
  <rect x="1" y="1" width="298" height="118" rx="22" fill="url(#g)" stroke="{accent}" stroke-opacity="0.75" stroke-width="2.5"/>
  <rect x="110" y="104" width="80" height="5" rx="2.5" fill="{accent}"/>
  <text x="150" y="60" font-family="Heebo, Segoe UI, Arial, sans-serif" font-size="34" font-weight="800" fill="#ffffff" text-anchor="middle">{title}</text>
  <text x="150" y="90" font-family="Heebo, Segoe UI, Arial, sans-serif" font-size="18" font-weight="700" fill="{accent}" text-anchor="middle">{sub}</text>
</svg>
'''
    path = os.path.join(out_dir, fname)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(svg_content)

print(f'Successfully created {len(logos_to_create)} authentic SVG logos!')
