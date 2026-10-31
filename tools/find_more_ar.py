import json, re

with open("channels.json", encoding="utf-8") as f:
    chs = json.load(f)

for c in chs:
    name = c.get("name", "")
    desc = c.get("desc", "")
    title = c.get("title", "")
    cid = c.get("id", "")
    if any(k in name.lower() or k in desc.lower() or k in title.lower() or k in cid.lower() 
           for k in ['shams', 'nas', 'arab', 'jazeera', 'asharq', 'aljazeera', 'ערבי']):
        print(f"Candidate Arabic: {name} ({cid}) - lang: {c.get('lang')}")

