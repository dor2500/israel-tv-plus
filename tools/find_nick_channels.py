import os, json

repo_root = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
channels_path = os.path.join(repo_root, "channels.json")

with open(channels_path, "r", encoding="utf-8") as f:
    channels = json.load(f)

for c in channels:
    name = c.get("name", "")
    cid = c.get("id", "")
    num = c.get("number", "")
    url = c.get("url", "")
    if "ניק" in name or "nick" in name.lower() or str(num) == "70" or cid == "nickelodeon_il":
        print(f"#{num} [{cid}] {name} -> {url}")
