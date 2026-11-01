import json

with open(r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\channels.json", "r", encoding="utf-8") as f:
    chs = json.load(f)

for num in [25, 26, 27, 28, 29, 30, 31, 37, 38, 39, 40, 41, 42, 43]:
    for c in chs:
        if c.get("number") == num:
            print(f"#{num}: {c.get('name')} ({c.get('id')})")
