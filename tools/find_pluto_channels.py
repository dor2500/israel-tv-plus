import json

with open(r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\channels.json", "r", encoding="utf-8") as f:
    chs = json.load(f)

pluto_chs = []
for c in chs:
    u = c.get("url", "")
    if "pluto.tv" in u or "plutotv" in u:
        pluto_chs.append((c.get("number"), c.get("id"), c.get("name"), u))

print(f"Total Pluto TV channels: {len(pluto_chs)}")
for num, cid, name, u in pluto_chs:
    print(f"#{num} [{cid}] {name} -> {u}")

