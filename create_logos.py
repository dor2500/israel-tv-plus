import os

logos = {}

# Keshet 12
logos['keshet12.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <defs>
    <linearGradient id="k12ribbon" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" stop-color="#ff007f"/>
      <stop offset="25%" stop-color="#ff9900"/>
      <stop offset="50%" stop-color="#33cc33"/>
      <stop offset="75%" stop-color="#00ccff"/>
      <stop offset="100%" stop-color="#9933ff"/>
    </linearGradient>
  </defs>
  <rect width="300" height="120" rx="24" fill="#0d1117" stroke="#1f293d" stroke-width="2"/>
  <path d="M 30 25 C 60 25, 60 95, 90 95 C 120 95, 120 25, 150 25" fill="none" stroke="url(#k12ribbon)" stroke-width="14" stroke-linecap="round"/>
  <text x="215" y="82" font-family="Rubik, Assistant, Arial, sans-serif" font-size="68" font-weight="900" fill="#ffffff" text-anchor="middle">12</text>
  <text x="215" y="106" font-family="Rubik, Assistant, Arial, sans-serif" font-size="16" font-weight="700" fill="#00d2ff" text-anchor="middle">קשת</text>
</svg>'''

# Reshet 13 SVG
logos['reshet13.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <defs>
    <linearGradient id="r13g" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#240046"/>
      <stop offset="100%" stop-color="#5a189a"/>
    </linearGradient>
  </defs>
  <rect width="300" height="120" rx="24" fill="url(#r13g)" stroke="#9d4edd" stroke-width="2"/>
  <circle cx="65" cy="60" r="32" fill="#e0aaff"/>
  <path d="M 60 40 L 70 60 L 60 80" stroke="#240046" stroke-width="6" fill="none" stroke-linecap="round"/>
  <text x="185" y="84" font-family="Rubik, Assistant, Arial, sans-serif" font-size="72" font-weight="900" fill="#ffffff" text-anchor="middle">13</text>
  <text x="185" y="108" font-family="Rubik, sans-serif" font-size="16" font-weight="800" fill="#e0aaff" text-anchor="middle">רשת</text>
</svg>'''

# Now 14
logos['now14.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <defs>
    <linearGradient id="n14g" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#001845"/>
      <stop offset="100%" stop-color="#023e8a"/>
    </linearGradient>
  </defs>
  <rect width="300" height="120" rx="24" fill="url(#n14g)" stroke="#0077b6" stroke-width="2"/>
  <path d="M 40 60 L 65 35 L 90 60 L 65 85 Z" fill="#e63946"/>
  <text x="190" y="82" font-family="Rubik, Assistant, Arial, sans-serif" font-size="70" font-weight="900" fill="#ffffff" text-anchor="middle">14</text>
  <text x="190" y="106" font-family="Rubik, Assistant, Arial, sans-serif" font-size="16" font-weight="700" fill="#e63946" text-anchor="middle">עכשיו 14</text>
</svg>'''

# Kan 11
logos['kan11.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <rect width="300" height="120" rx="24" fill="#000000" stroke="#333333" stroke-width="2"/>
  <rect x="35" y="30" width="100" height="60" rx="16" fill="#ffffff"/>
  <text x="85" y="74" font-family="Rubik, sans-serif" font-size="40" font-weight="900" fill="#000000" text-anchor="middle">כאן</text>
  <text x="210" y="84" font-family="Rubik, sans-serif" font-size="74" font-weight="900" fill="#ffffff" text-anchor="middle">11</text>
</svg>'''

# Kan Educational 23
logos['kan_educational.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <rect width="300" height="120" rx="24" fill="#1a0f00" stroke="#ff6b35" stroke-width="2"/>
  <circle cx="65" cy="60" r="36" fill="#ff6b35"/>
  <text x="65" y="74" font-family="Rubik, sans-serif" font-size="40" font-weight="900" fill="#ffffff" text-anchor="middle">כאן</text>
  <text x="195" y="82" font-family="Rubik, sans-serif" font-size="64" font-weight="900" fill="#ffffff" text-anchor="middle">23</text>
  <text x="195" y="106" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#ff9e00" text-anchor="middle">חינוכית</text>
</svg>'''

# Zoom TV
logos['zoom.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <defs>
    <linearGradient id="zoomg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#ff0055"/>
      <stop offset="100%" stop-color="#7928ca"/>
    </linearGradient>
  </defs>
  <rect width="300" height="120" rx="24" fill="url(#zoomg)"/>
  <text x="150" y="78" font-family="Arial Black, Impact, sans-serif" font-size="56" font-style="italic" font-weight="900" fill="#ffffff" text-anchor="middle" letter-spacing="2">ZOOM</text>
  <text x="150" y="104" font-family="Rubik, sans-serif" font-size="16" font-weight="800" fill="#ffff00" text-anchor="middle">ערוץ זום לילדים</text>
</svg>'''

# Nick Jr
logos['nick_jr.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <rect width="300" height="120" rx="24" fill="#0a192f" stroke="#00a8ff" stroke-width="2"/>
  <rect x="30" y="30" width="120" height="60" rx="30" fill="#ff5e00"/>
  <text x="90" y="72" font-family="Arial Rounded MT Bold, Rubik, sans-serif" font-size="32" font-weight="bold" fill="#ffffff" text-anchor="middle">nick</text>
  <rect x="160" y="30" width="100" height="60" rx="30" fill="#00a8ff"/>
  <text x="210" y="72" font-family="Arial Rounded MT Bold, Rubik, sans-serif" font-size="32" font-weight="bold" fill="#ffffff" text-anchor="middle">jr.</text>
</svg>'''

# Sport 5
logos['sport5.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <defs>
    <linearGradient id="s5g" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#b30000"/>
      <stop offset="100%" stop-color="#e60000"/>
    </linearGradient>
  </defs>
  <rect width="300" height="120" rx="24" fill="url(#s5g)" stroke="#ff4d4d" stroke-width="2"/>
  <text x="100" y="76" font-family="Rubik, sans-serif" font-size="34" font-weight="900" fill="#ffffff" text-anchor="middle">ספורט</text>
  <text x="205" y="86" font-family="Arial Black, Impact, sans-serif" font-size="74" font-weight="900" fill="#ffff00" text-anchor="middle">5</text>
</svg>'''

# Sport 5 Plus
logos['sport5_plus.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <defs>
    <linearGradient id="s5pg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#003366"/>
      <stop offset="100%" stop-color="#0066cc"/>
    </linearGradient>
  </defs>
  <rect width="300" height="120" rx="24" fill="url(#s5pg)" stroke="#0099ff" stroke-width="2"/>
  <text x="85" y="76" font-family="Rubik, sans-serif" font-size="32" font-weight="900" fill="#ffffff" text-anchor="middle">ספורט</text>
  <text x="180" y="86" font-family="Arial Black, Impact, sans-serif" font-size="70" font-weight="900" fill="#ffffff" text-anchor="middle">5</text>
  <text x="245" y="86" font-family="Arial Black, sans-serif" font-size="55" font-weight="900" fill="#ffff00" text-anchor="middle">+</text>
</svg>'''

# BabyFirst
logos['babyfirst.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <defs>
    <linearGradient id="babyg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#ff9a9e"/>
      <stop offset="100%" stop-color="#fecfef"/>
    </linearGradient>
  </defs>
  <rect width="300" height="120" rx="24" fill="#111122" stroke="#ff9a9e" stroke-width="2"/>
  <circle cx="60" cy="60" r="35" fill="url(#babyg)"/>
  <text x="60" y="72" font-size="36" text-anchor="middle">👶</text>
  <text x="180" y="68" font-family="Arial Rounded MT Bold, Rubik, sans-serif" font-size="34" font-weight="bold" fill="#ffffff" text-anchor="middle">BabyFirst</text>
  <text x="180" y="96" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#ff9a9e" text-anchor="middle">בייבי פירסט</text>
</svg>'''

# Knesset 99
logos['knesset.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <rect width="300" height="120" rx="24" fill="#0d1b2a" stroke="#415a77" stroke-width="2"/>
  <text x="85" y="72" font-family="Rubik, sans-serif" font-size="34" font-weight="900" fill="#e0e1dd" text-anchor="middle">הכנסת</text>
  <text x="210" y="82" font-family="Arial Black, sans-serif" font-size="64" font-weight="900" fill="#00b4d8" text-anchor="middle">99</text>
</svg>'''

# Makan 33
logos['makan33.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <rect width="300" height="120" rx="24" fill="#1c1917" stroke="#e11d48" stroke-width="2"/>
  <text x="95" y="72" font-family="Rubik, sans-serif" font-size="38" font-weight="900" fill="#ffffff" text-anchor="middle">מכאן</text>
  <text x="210" y="82" font-family="Arial Black, sans-serif" font-size="64" font-weight="900" fill="#e11d48" text-anchor="middle">33</text>
</svg>'''

# PlusPlus
logos['plusplus.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <rect width="300" height="120" rx="24" fill="#180033" stroke="#a855f7" stroke-width="2"/>
  <text x="150" y="75" font-family="Arial Black, sans-serif" font-size="44" font-weight="900" fill="#a855f7" text-anchor="middle">PLUS PLUS</text>
  <text x="150" y="102" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#38bdf8" text-anchor="middle">ערוץ ילדים ונוער</text>
</svg>'''

# Junior / Wiz
logos['junior.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <rect width="300" height="120" rx="24" fill="#1e1b4b" stroke="#818cf8" stroke-width="2"/>
  <text x="150" y="72" font-family="Arial Black, sans-serif" font-size="44" font-weight="900" fill="#818cf8" text-anchor="middle">JUNIOR</text>
  <text x="150" y="100" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#fbbf24" text-anchor="middle">ערוץ ג'וניור</text>
</svg>'''

# Disney Channel
logos['disney.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <defs>
    <linearGradient id="disneyg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#0066eb"/>
      <stop offset="100%" stop-color="#002b80"/>
    </linearGradient>
  </defs>
  <rect width="300" height="120" rx="24" fill="url(#disneyg)" stroke="#38bdf8" stroke-width="2"/>
  <circle cx="95" cy="50" r="16" fill="#ffffff"/>
  <circle cx="155" cy="50" r="16" fill="#ffffff"/>
  <circle cx="125" cy="72" r="28" fill="#ffffff"/>
  <text x="210" y="76" font-family="Arial Black, Impact, sans-serif" font-size="34" font-weight="900" fill="#ffffff">CHANNEL</text>
</svg>'''

# Hop Yaldut
logos['hop_yaldut.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <rect width="300" height="120" rx="24" fill="#ffef9f" stroke="#ff9f1c" stroke-width="3"/>
  <text x="150" y="68" font-family="Rubik, sans-serif" font-size="48" font-weight="900" fill="#ff0055" text-anchor="middle">!הופ ילדות</text>
  <text x="150" y="100" font-family="Rubik, sans-serif" font-size="18" font-weight="800" fill="#2ec4b6" text-anchor="middle">שירי ילדות וסיפורים</text>
</svg>'''

# Channel 9
logos['ch9.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <rect width="300" height="120" rx="24" fill="#003566" stroke="#0077b6" stroke-width="2"/>
  <text x="100" y="75" font-family="Rubik, sans-serif" font-size="38" font-weight="900" fill="#ffffff" text-anchor="middle">ערוץ</text>
  <text x="200" y="85" font-family="Arial Black, sans-serif" font-size="74" font-weight="900" fill="#ffd60a" text-anchor="middle">9</text>
</svg>'''

# Channel 24
logos['ch24.svg'] = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <rect width="300" height="120" rx="24" fill="#3a0ca3" stroke="#f72585" stroke-width="2"/>
  <text x="90" y="75" font-family="Rubik, sans-serif" font-size="34" font-weight="900" fill="#ffffff" text-anchor="middle">מוזיקה</text>
  <text x="210" y="85" font-family="Arial Black, sans-serif" font-size="68" font-weight="900" fill="#f72585" text-anchor="middle">24</text>
</svg>'''

os.makedirs('assets/logos', exist_ok=True)
for filename, content in logos.items():
    filepath = os.path.join('assets/logos', filename)
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f'Wrote {filepath}')

print('Done creating vector logos!')
