import os, shutil, hashlib

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
dist = os.path.join(ROOT, "dist")
v = "4.8.43"

# Ensure versioned and unversioned copies exist for release
if os.path.exists(os.path.join(dist, "IsraelTVPlus-android.apk")):
    shutil.copy2(os.path.join(dist, "IsraelTVPlus-android.apk"), os.path.join(dist, f"IsraelTVPlus-{v}-android.apk"))
if os.path.exists(os.path.join(dist, "IsraelTVPlus-androidtv.apk")):
    shutil.copy2(os.path.join(dist, "IsraelTVPlus-androidtv.apk"), os.path.join(dist, f"IsraelTVPlus-{v}-androidtv.apk"))
if os.path.exists(os.path.join(dist, f"com.israeltv.plus_{v}_all.ipk")):
    shutil.copy2(os.path.join(dist, f"com.israeltv.plus_{v}_all.ipk"), os.path.join(dist, "com.israeltv.plus_all.ipk"))
if os.path.exists(os.path.join(dist, f"plugin.video.israeltvplus-{v}.zip")):
    shutil.copy2(os.path.join(dist, f"plugin.video.israeltvplus-{v}.zip"), os.path.join(dist, "plugin.video.israeltvplus.zip"))
if os.path.exists(os.path.join(dist, "IsraelTVPlus-tizen-project.zip")):
    shutil.copy2(os.path.join(dist, "IsraelTVPlus-tizen-project.zip"), os.path.join(dist, f"IsraelTVPlus-{v}-tizen-project.zip"))

# Also copy web/catalog files into dist
for f in ["plex.m3u", "epg.xml", "epg.json", "playlist.m3u", "playlist-tv.m3u", "playlist-israel.m3u", "radio.m3u", "channels.json", "version.json", "version.js", "vod_data.json"]:
    src = os.path.join(ROOT, f)
    if os.path.exists(src):
        shutil.copy2(src, os.path.join(dist, f))

lines = []
for fname in sorted(os.listdir(dist)):
    p = os.path.join(dist, fname)
    if os.path.isfile(p) and fname not in ["SHA256SUMS", "SHA256SUMS.txt"]:
        with open(p, "rb") as fh:
            h = hashlib.sha256(fh.read()).hexdigest()
        lines.append(f"{h}  {fname}")

out_content = "\n".join(lines) + "\n"
with open(os.path.join(dist, "SHA256SUMS"), "w", encoding="utf-8") as f:
    f.write(out_content)
with open(os.path.join(dist, "SHA256SUMS.txt"), "w", encoding="utf-8") as f:
    f.write(out_content)

print(f"Generated SHA256SUMS and SHA256SUMS.txt for {v} with {len(lines)} files!")

