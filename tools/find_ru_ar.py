import json, re

with open("channels.json", encoding="utf-8") as f:
    chs = json.load(f)

cyrillic = re.compile(r'[\u0400-\u04FF]')
arabic = re.compile(r'[\u0600-\u06FF]')

ru_channels = []
ar_channels = []

for c in chs:
    lang = c.get("lang", "")
    name = c.get("name", "")
    desc = c.get("desc", "")
    title = c.get("title", "")
    
    is_ru = lang == "ru" or bool(cyrillic.search(name)) or bool(cyrillic.search(title)) or "רוסית" in name or "רוסית" in desc
    is_ar = lang == "ar" or bool(arabic.search(name)) or bool(arabic.search(title)) or "בערבית" in name or "בערבית" in desc or "ערבית" in name
    
    if is_ru:
        ru_channels.append(c)
    elif is_ar:
        ar_channels.append(c)

print(f"Total channels: {len(chs)}")
print(f"Russian channels found: {len(ru_channels)}")
for c in ru_channels:
    print(f" - [RU] {c['name']} ({c['id']})")

print(f"\nArabic channels found: {len(ar_channels)}")
for c in ar_channels:
    print(f" - [AR] {c['name']} ({c['id']})")

