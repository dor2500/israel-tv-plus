import json

with open(r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\channels.json", "r", encoding="utf-8") as f:
    chs = json.load(f)

for c in chs:
    u = c.get("url", "")
    if "mcquack.net" in u:
        print(f"#{c.get('number')}: {c.get('name')} -> {u}")

