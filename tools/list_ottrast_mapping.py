import json

with open(r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\channels.json", "r", encoding="utf-8") as f:
    chs = json.load(f)

mapping = {}
for c in chs:
    u = c.get("url", "")
    if "ottrast.com" in u:
        cid = u.split("/")[-2]
        mapping[cid] = c.get("name")

for k in sorted(mapping.keys(), key=lambda x: int(x) if x.isdigit() else 0):
    print(f"{k}: {mapping[k]}")

