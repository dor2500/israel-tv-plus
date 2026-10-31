import json

with open("channels.json", encoding="utf-8") as f:
    chs = json.load(f)

for c in chs:
    cid = c.get("id")
    if cid in [
        "simpsons_247", "animal_planet", "investigation_discovery", "extreme_sports_hd",
        "kinohit", "viju_sport", "setanta_sports_2_hd", "kinouzhas", "cineman_vhs_retro",
        "viju_planet", "rain_tv_il"
    ]:
        print(f"ID: {cid} | Name: {c['name']} | Lang: {c.get('lang')} | Desc: {c.get('desc')}")

