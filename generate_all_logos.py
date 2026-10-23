import os

additional_logos = {
    'yes_action.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#001233" stroke="#00b4d8" stroke-width="2"/>
      <text x="100" y="76" font-family="Arial, sans-serif" font-size="52" font-weight="900" fill="#00b4d8" text-anchor="middle">yes</text>
      <text x="210" y="70" font-family="Rubik, sans-serif" font-size="28" font-weight="900" fill="#ffffff" text-anchor="middle">ACTION</text>
      <text x="210" y="98" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#90e0ef" text-anchor="middle">אקשן ומתח</text>
    </svg>''',

    'yes_comedy.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#1b120c" stroke="#f77f00" stroke-width="2"/>
      <text x="100" y="76" font-family="Arial, sans-serif" font-size="52" font-weight="900" fill="#00b4d8" text-anchor="middle">yes</text>
      <text x="210" y="70" font-family="Rubik, sans-serif" font-size="26" font-weight="900" fill="#fcbf49" text-anchor="middle">COMEDY</text>
      <text x="210" y="98" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#f77f00" text-anchor="middle">קומדיה וצחוק</text>
    </svg>''',

    'yes_drama.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#2d0036" stroke="#c77dff" stroke-width="2"/>
      <text x="100" y="76" font-family="Arial, sans-serif" font-size="52" font-weight="900" fill="#00b4d8" text-anchor="middle">yes</text>
      <text x="210" y="70" font-family="Rubik, sans-serif" font-size="28" font-weight="900" fill="#e0aaff" text-anchor="middle">DRAMA</text>
      <text x="210" y="98" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#c77dff" text-anchor="middle">דרמה ורגש</text>
    </svg>''',

    'yes_israeli.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#001a33" stroke="#3a86ff" stroke-width="2"/>
      <text x="95" y="76" font-family="Arial, sans-serif" font-size="50" font-weight="900" fill="#00b4d8" text-anchor="middle">yes</text>
      <text x="205" y="74" font-family="Rubik, sans-serif" font-size="32" font-weight="900" fill="#ffffff" text-anchor="middle">ישראלי</text>
      <text x="205" y="100" font-family="Rubik, sans-serif" font-size="15" font-weight="700" fill="#3a86ff" text-anchor="middle">קולנוע ישראלי</text>
    </svg>''',

    'hot_drama.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#800000" stroke="#e60000" stroke-width="2"/>
      <rect x="25" y="30" width="110" height="60" rx="12" fill="#e60000"/>
      <text x="80" y="74" font-family="Arial Black, Impact, sans-serif" font-size="44" font-weight="900" fill="#ffffff" text-anchor="middle">HOT</text>
      <text x="210" y="72" font-family="Rubik, sans-serif" font-size="30" font-weight="900" fill="#ffffff" text-anchor="middle">דרמה</text>
    </svg>''',

    'hot_cinema.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#200000" stroke="#e60000" stroke-width="2"/>
      <rect x="25" y="30" width="110" height="60" rx="12" fill="#e60000"/>
      <text x="80" y="74" font-family="Arial Black, Impact, sans-serif" font-size="44" font-weight="900" fill="#ffffff" text-anchor="middle">HOT</text>
      <text x="210" y="70" font-family="Rubik, sans-serif" font-size="24" font-weight="900" fill="#ffd166" text-anchor="middle">CINEMA</text>
      <text x="210" y="96" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#ffffff" text-anchor="middle">סרטים מובחרים</text>
    </svg>''',

    'hot_israeli.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#200000" stroke="#e60000" stroke-width="2"/>
      <rect x="25" y="30" width="110" height="60" rx="12" fill="#e60000"/>
      <text x="80" y="74" font-family="Arial Black, Impact, sans-serif" font-size="44" font-weight="900" fill="#ffffff" text-anchor="middle">HOT</text>
      <text x="210" y="72" font-family="Rubik, sans-serif" font-size="30" font-weight="900" fill="#ffffff" text-anchor="middle">ישראלי</text>
    </svg>''',

    'one_hd.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#001845" stroke="#0077b6" stroke-width="2"/>
      <text x="120" y="82" font-family="Arial Black, Impact, sans-serif" font-size="64" font-style="italic" font-weight="900" fill="#00b4d8" text-anchor="middle">ONE</text>
      <rect x="200" y="42" width="70" height="40" rx="8" fill="#e63946"/>
      <text x="235" y="70" font-family="Arial, sans-serif" font-size="24" font-weight="900" fill="#ffffff" text-anchor="middle">HD</text>
    </svg>''',

    'galgalatz.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#780000" stroke="#c1121f" stroke-width="2"/>
      <circle cx="65" cy="60" r="34" fill="#c1121f" stroke="#ffffff" stroke-width="3"/>
      <text x="65" y="70" font-size="32" text-anchor="middle">📻</text>
      <text x="185" y="72" font-family="Rubik, sans-serif" font-size="38" font-weight="900" fill="#ffffff" text-anchor="middle">גלגלצ TV</text>
      <text x="185" y="98" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#fdf0d5" text-anchor="middle">מוזיקה ישראלית ולועזית</text>
    </svg>''',

    'kan_gimel.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#003049" stroke="#669bbc" stroke-width="2"/>
      <circle cx="65" cy="60" r="34" fill="#669bbc"/>
      <text x="65" y="72" font-family="Rubik, sans-serif" font-size="36" font-weight="900" fill="#003049" text-anchor="middle">כאן</text>
      <text x="185" y="72" font-family="Rubik, sans-serif" font-size="38" font-weight="900" fill="#ffffff" text-anchor="middle">כאן גימל</text>
      <text x="185" y="98" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#669bbc" text-anchor="middle">רק מוזיקה ישראלית</text>
    </svg>''',

    'kol_barama.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#1b263b" stroke="#415a77" stroke-width="2"/>
      <text x="150" y="68" font-family="Rubik, sans-serif" font-size="32" font-weight="900" fill="#e0e1dd" text-anchor="middle">קול ברמה TV</text>
      <text x="150" y="98" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#778da9" text-anchor="middle">שידור חי מול האולפן</text>
    </svg>''',

    'kol_chai.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#2d0c03" stroke="#d00000" stroke-width="2"/>
      <text x="150" y="68" font-family="Rubik, sans-serif" font-size="34" font-weight="900" fill="#ffffff" text-anchor="middle">קול חי TV</text>
      <text x="150" y="98" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#ffba08" text-anchor="middle">שידור חי מהאולפנים</text>
    </svg>''',

    'cnn.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#0c0c0c" stroke="#cc0000" stroke-width="2"/>
      <text x="150" y="84" font-family="Arial Black, Impact, sans-serif" font-size="64" font-weight="900" fill="#cc0000" text-anchor="middle" letter-spacing="4">CNN</text>
      <text x="150" y="106" font-family="Arial, sans-serif" font-size="14" font-weight="700" fill="#ffffff" text-anchor="middle">INTERNATIONAL</text>
    </svg>''',

    'bbc.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#bb1919" stroke="#ffffff" stroke-width="1"/>
      <rect x="40" y="35" width="45" height="45" fill="#ffffff"/>
      <text x="62" y="68" font-family="Arial Black, sans-serif" font-size="28" font-weight="bold" fill="#000000" text-anchor="middle">B</text>
      <rect x="95" y="35" width="45" height="45" fill="#ffffff"/>
      <text x="117" y="68" font-family="Arial Black, sans-serif" font-size="28" font-weight="bold" fill="#000000" text-anchor="middle">B</text>
      <rect x="150" y="35" width="45" height="45" fill="#ffffff"/>
      <text x="172" y="68" font-family="Arial Black, sans-serif" font-size="28" font-weight="bold" fill="#000000" text-anchor="middle">C</text>
      <text x="240" y="66" font-family="Arial Black, sans-serif" font-size="22" font-weight="900" fill="#ffffff">NEWS</text>
    </svg>''',

    'nasa.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#0b3d91" stroke="#fc3d21" stroke-width="2"/>
      <circle cx="70" cy="60" r="35" fill="#0b3d91" stroke="#ffffff" stroke-width="2"/>
      <text x="70" y="66" font-family="Arial Black, sans-serif" font-size="16" font-weight="900" fill="#ffffff" text-anchor="middle">NASA</text>
      <text x="190" y="72" font-family="Rubik, sans-serif" font-size="32" font-weight="900" fill="#ffffff" text-anchor="middle">NASA TV HD</text>
      <text x="190" y="98" font-family="Rubik, sans-serif" font-size="15" font-weight="700" fill="#fc3d21" text-anchor="middle">שידור ישיר מחלל</text>
    </svg>''',

    'nature_docu.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#0f382c" stroke="#52b788" stroke-width="2"/>
      <circle cx="65" cy="60" r="32" fill="#52b788"/>
      <text x="65" y="70" font-size="32" text-anchor="middle">🌿</text>
      <text x="185" y="70" font-family="Rubik, sans-serif" font-size="32" font-weight="900" fill="#ffffff" text-anchor="middle">טבע ופרא HD</text>
      <text x="185" y="96" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#74c69d" text-anchor="middle">דוקו חיות ונופים</text>
    </svg>''',

    'food_channel.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#441111" stroke="#f28482" stroke-width="2"/>
      <circle cx="65" cy="60" r="32" fill="#f28482"/>
      <text x="65" y="70" font-size="32" text-anchor="middle">🍳</text>
      <text x="185" y="70" font-family="Rubik, sans-serif" font-size="32" font-weight="900" fill="#ffffff" text-anchor="middle">ערוץ האוכל HD</text>
      <text x="185" y="96" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#f7cac9" text-anchor="middle">תוכניות בישול ושפים</text>
    </svg>'''
}

os.makedirs('assets/logos', exist_ok=True)
for filename, content in additional_logos.items():
    p = os.path.join('assets/logos', filename)
    with open(p, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f'Wrote {p}')

print('All additional logos written successfully!')
