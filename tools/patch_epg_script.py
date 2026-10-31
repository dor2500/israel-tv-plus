with open('tools/make_epg.py', 'r', encoding='utf-8') as f:
    text = f.read()

target = """    path = os.path.join(ROOT, "epg.json")
    with open(path, "w", encoding="utf-8") as f:
        json.dump({"generated": int(time.time() // 60), "channels": guide}, f, ensure_ascii=False, separators=(",", ":"))
    print("epg.json:", len(guide), "channels,", os.path.getsize(path) // 1024, "KB")"""

replacement = """    path = os.path.join(ROOT, "epg.json")
    with open(path, "w", encoding="utf-8") as f:
        json.dump({"generated": int(time.time() // 60), "channels": guide}, f, ensure_ascii=False, separators=(",", ":"))
    try:
        sys.path.append(os.path.dirname(__file__))
        from enrich_epg_thumbnails import enrich_epg
        enrich_epg()
    except Exception as e:
        print("enrichment error:", e)
    print("epg.json:", len(guide), "channels,", os.path.getsize(path) // 1024, "KB")"""

text = text.replace('\r\n', '\n')
target = target.replace('\r\n', '\n')
replacement = replacement.replace('\r\n', '\n')

if target in text:
    text = text.replace(target, replacement, 1)
    with open('tools/make_epg.py', 'w', encoding='utf-8') as f:
        f.write(text)
    print("Patched tools/make_epg.py successfully!")
else:
    print("Target not found")

