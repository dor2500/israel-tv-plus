import json

ver = '4.8.12'
code = 202610312133
app_num = 592
date = '2026-10-31'

# 1. VERSION
with open(r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\VERSION', 'w', encoding='utf-8') as f:
    f.write(ver)

# 2. appinfo.json
with open(r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\appinfo.json', 'r', encoding='utf-8') as f:
    ai = json.load(f)
ai['version'] = ver
with open(r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\appinfo.json', 'w', encoding='utf-8') as f:
    json.dump(ai, f, indent=2, ensure_ascii=False)

# 3. version.js
with open(r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\version.js', 'w', encoding='utf-8') as f:
    f.write(f'// written by tools/make_version.py\nwindow.ITV_BUILD = {{"version": "{ver}", "code": {code}, "app": {app_num}, "shell": 3, "date": "{date}"}};\n')

# 4. version.json
notes = [
    'תיקון הסרגל העליון (Floating Pill / Top Navbar): בניית הטאבים מיד בעת עליית האפליקציה בטלוויזיה, תמיכה מלאה בכל הטאבים ומניעת הופעת קו/קפסולה ריקה.',
    'חיסול השהיית הספורט (Zero-Latency Live Edge): סנכרון ישיר לקצה השידור החי (Live Edge) ללא השהייה של דקה מול השידור המקורי, צמצום באפר הספורט ל-6 שניות והאצה חכמה (1.4x) לסגירת פערים.',
    'ממשק טלוויזיה מהיר וחלק (60 FPS Smooth Navigation): אופטימיזציה לטלוויזיות LG webOS המונעת עומס וידאו ברקע בעת מעבר מהיר בין ערוצים.',
    'שרת DLNA ייעודי למחשב ולטלוויזיות LG: הזרמה ישירה של ערוצי הטלוויזיה (387 ערוצים) ללא תלות ב-Plex, התאמה מלאה לנגן התמונות והווידאו של LG webOS ותמיכה מלאה ב-SSDP.'
]

with open(r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\version.json', 'r', encoding='utf-8') as f:
    vj = json.load(f)
vj['version'] = ver
vj['code'] = code
vj['app'] = app_num
vj['date'] = date
vj['notes'] = notes
for k in vj.get('native', {}):
    vj['native'][k]['version'] = ver
    vj['native'][k]['app'] = app_num
    if 'url' in vj['native'][k]:
        vj['native'][k]['url'] = vj['native'][k]['url'].replace('4.8.11', ver)

with open(r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\version.json', 'w', encoding='utf-8') as f:
    json.dump(vj, f, indent=2, ensure_ascii=False)

# 5. CHANGELOG.md
with open(r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\CHANGELOG.md', 'r', encoding='utf-8') as f:
    cl = f.read()

new_entry = f'''## [{ver}] - {date}

### Added & Fixed
- **תיקון היעלמות הסרגל העליון (Top Navbar / ATV Pill Fix)**:
  - אתחול מלא של `atvBuild()` ו-`atvLayout()` מיד בעת עליית האפליקציה במסכי טלוויזיה.
  - הוספת הטאב "כל הערוצים" (`all_channels`) לטאבים המותרים ב-pill וקפיצה מיידית לראש הרשימה.
  - הסתרת קפסולה ריקה ב-CSS באמצעות `.atv-pill:empty {{ display: none !important; }}` כדי למנוע הופעת קו בודד ריק על המסך.
- **סנכרון שידורי ספורט בזמן אמת (Zero-Latency Live Edge)**:
  - ביטול פיגור של דקה בשידורי ספורט (ספורט 1-4, ספורט 5, 5Live, 5Gold, ONE וכו').
  - סנכרון מידי לקצה החי של השידור (`hls.liveSyncPosition`).
  - הגבלת באפר הספורט ל-6 שניות בלבד והאצת סנכרון חכמה של 1.4x לסגירת כל עיכוב.
  - שמירה על שידור חי בזמן אמת גם כאשר מופעל מצב השבחת תמונה ל-4K.
- **ממשק טלוויזיה מהיר וחלק (60 FPS Smooth UI on webOS TV)**:
  - דחיית טעינת וידאו מקדים ברקע (`atvPreview`) ל-1.2 שניות ב-webOS TV למניעת עומס פענוח חומרה בעת דפדוף מהיר בין ערוצים.
- **שרת DLNA ייעודי למחשב ולטלוויזיות LG (Israel TV+ PC DLNA)**:
  - שרת DLNA עצמאי (פורט 8200) במחשב עם 387 ערוצי שידור ישירים.
  - שידור SSDP מתוקן עם תמיכה ישירה בטלוויזיות LG ברשת (`192.168.1.104`, `192.168.1.6`, `192.168.1.7`).
  - הנגשת שידורי HLS ישירים ב-DIDL XML ללא 302 Redirects המכשילים את נגן webOS.
  - תמיכה מלאה ב-SUBSCRIBE/UNSUBSCRIBE לניהול אירועים תקין.

'''

with open(r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\CHANGELOG.md', 'w', encoding='utf-8') as f:
    f.write(new_entry + cl)

print('All version files updated to', ver)

