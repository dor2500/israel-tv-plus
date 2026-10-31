import os, json

path = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\israeli_channels_master.json"
with open(path, "r", encoding="utf-8") as f:
    d = json.load(f)

chs = d.get("all_working_israeli_channels", [])
print(f"Total working: {len(chs)}")
for c in chs:
    name = c.get("name", "")
    url = c.get("url", "")
    if any(k in name.lower() for k in ["nick", "ניק"]):
        print(f"{name} -> {url}")
