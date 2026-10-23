import os

logos = {
    # Nickelodeon Israel Hebrew
    'nickelodeon_he.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <defs>
        <linearGradient id="nickg" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#ff5900"/>
          <stop offset="100%" stop-color="#ff7b00"/>
        </linearGradient>
      </defs>
      <rect width="300" height="120" rx="24" fill="url(#nickg)" stroke="#ffffff" stroke-width="2"/>
      <text x="150" y="68" font-family="Arial Black, Impact, sans-serif" font-size="38" font-weight="900" fill="#ffffff" text-anchor="middle" letter-spacing="-0.5">nickelodeon</text>
      <rect x="50" y="80" width="200" height="28" rx="14" fill="#000000"/>
      <text x="150" y="100" font-family="Rubik, sans-serif" font-size="16" font-weight="900" fill="#ffffff" text-anchor="middle">ערוץ ניקלודיאון בעברית</text>
    </svg>''',

    # Nick Jr. Israel Hebrew
    'nick_jr_he.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#001830" stroke="#00a8ff" stroke-width="2"/>
      <rect x="25" y="24" width="125" height="55" rx="28" fill="#ff5e00"/>
      <text x="87" y="63" font-family="Arial Rounded MT Bold, Rubik, sans-serif" font-size="30" font-weight="bold" fill="#ffffff" text-anchor="middle">nick</text>
      <rect x="155" y="24" width="115" height="55" rx="28" fill="#00a8ff"/>
      <text x="212" y="63" font-family="Arial Rounded MT Bold, Rubik, sans-serif" font-size="30" font-weight="bold" fill="#ffffff" text-anchor="middle">jr.</text>
      <rect x="40" y="86" width="220" height="24" rx="12" fill="rgba(255,255,255,0.15)"/>
      <text x="150" y="103" font-family="Rubik, sans-serif" font-size="14" font-weight="800" fill="#00d2ff" text-anchor="middle">ניק ג'וניור בעברית לפעוטות</text>
    </svg>''',

    # TeenNick Israel
    'teennick.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <defs>
        <linearGradient id="tng" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#4a0e4e"/>
          <stop offset="100%" stop-color="#070b19"/>
        </linearGradient>
      </defs>
      <rect width="300" height="120" rx="24" fill="url(#tng)" stroke="#ec4899" stroke-width="2"/>
      <text x="150" y="65" font-family="Arial Black, Impact, sans-serif" font-size="44" font-weight="900" fill="#ec4899" text-anchor="middle">TEEN<tspan fill="#ff7b00">NICK</tspan></text>
      <text x="150" y="100" font-family="Rubik, sans-serif" font-size="16" font-weight="800" fill="#ffffff" text-anchor="middle">טין ניק ישראל לנוער</text>
    </svg>''',

    # Disney Junior Hebrew
    'disney_junior.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <defs>
        <linearGradient id="djg" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#ff0055"/>
          <stop offset="100%" stop-color="#ff5500"/>
        </linearGradient>
      </defs>
      <rect width="300" height="120" rx="24" fill="#001a4d" stroke="#ff0055" stroke-width="2"/>
      <circle cx="80" cy="50" r="14" fill="#ffffff"/>
      <circle cx="130" cy="50" r="14" fill="#ffffff"/>
      <circle cx="105" cy="68" r="24" fill="#ffffff"/>
      <text x="205" y="60" font-family="Arial Black, sans-serif" font-size="28" font-weight="900" fill="#ffffff">Junior</text>
      <text x="150" y="102" font-family="Rubik, sans-serif" font-size="15" font-weight="800" fill="#ffd166" text-anchor="middle">דיסני ג'וניור בעברית</text>
    </svg>''',

    # Hop Luli TV
    'luli_tv.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <defs>
        <linearGradient id="lulig" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#ffeedd"/>
          <stop offset="100%" stop-color="#ffd6a5"/>
        </linearGradient>
      </defs>
      <rect width="300" height="120" rx="24" fill="url(#lulig)" stroke="#ff9f1c" stroke-width="3"/>
      <circle cx="65" cy="60" r="34" fill="#ff9f1c"/>
      <text x="65" y="72" font-size="34" text-anchor="middle">🐣</text>
      <text x="180" y="65" font-family="Rubik, sans-serif" font-size="44" font-weight="900" fill="#d90429" text-anchor="middle">לולי LULI</text>
      <text x="180" y="98" font-family="Rubik, sans-serif" font-size="16" font-weight="800" fill="#ff7b00" text-anchor="middle">ערוץ לולי לפעוטות בעברית</text>
    </svg>''',

    # Baby TV Israel Hebrew
    'baby_tv_he.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <rect width="300" height="120" rx="24" fill="#111827" stroke="#ec4899" stroke-width="2"/>
      <circle cx="60" cy="60" r="34" fill="#ec4899"/>
      <text x="60" y="72" font-size="34" text-anchor="middle">🍼</text>
      <text x="180" y="62" font-family="Arial Rounded MT Bold, Rubik, sans-serif" font-size="34" font-weight="bold" fill="#ffffff" text-anchor="middle">BabyTV</text>
      <text x="180" y="96" font-family="Rubik, sans-serif" font-size="16" font-weight="800" fill="#f472b6" text-anchor="middle">ערוץ בייבי בעברית</text>
    </svg>''',

    # Israeli Kids Songs
    'kids_songs_he.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
      <defs>
        <linearGradient id="songsg" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#4361ee"/>
          <stop offset="100%" stop-color="#7209b7"/>
        </linearGradient>
      </defs>
      <rect width="300" height="120" rx="24" fill="url(#songsg)" stroke="#4cc9f0" stroke-width="2"/>
      <circle cx="60" cy="60" r="32" fill="#4cc9f0"/>
      <text x="60" y="70" font-size="30" text-anchor="middle">🎵</text>
      <text x="180" y="62" font-family="Rubik, sans-serif" font-size="32" font-weight="900" fill="#ffffff" text-anchor="middle">שירי ילדים ישראליים</text>
      <text x="180" y="95" font-family="Rubik, sans-serif" font-size="16" font-weight="700" fill="#4cc9f0" text-anchor="middle">השירים האהובים מכל הזמנים</text>
    </svg>'''
}

os.makedirs('assets/logos', exist_ok=True)
for fname, content in logos.items():
    p = os.path.join('assets/logos', fname)
    with open(p, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f'Wrote {p}')

print('Generated all Hebrew kids channel logos!')
