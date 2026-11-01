import os, shutil, hashlib, subprocess, sys

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)
dist = os.path.join(ROOT, "dist")
os.makedirs(dist, exist_ok=True)
v = "4.8.48"

# 1. Android APK
apk_src = os.path.join(ROOT, "android", "app", "build", "outputs", "apk", "release", "app-release.apk")
if not os.path.exists(apk_src):
    apk_src = os.path.join(dist, "IsraelTVPlus-android.apk")

if os.path.exists(apk_src):
    shutil.copy2(apk_src, os.path.join(dist, "IsraelTVPlus-android.apk"))
    shutil.copy2(apk_src, os.path.join(dist, f"IsraelTVPlus-{v}-android.apk"))
    shutil.copy2(apk_src, os.path.join(dist, "IsraelTVPlus-androidtv.apk"))
    shutil.copy2(apk_src, os.path.join(dist, f"IsraelTVPlus-{v}-androidtv.apk"))
    print(f"Copied APKs ({os.path.getsize(apk_src):,} bytes) to dist!")

# 2. Build webOS IPK
print("Building LG webOS IPK...")
subprocess.run([sys.executable, "-I", "tools/build_ipk.py", dist], check=True)
ipk_versioned = os.path.join(dist, f"com.israeltv.plus_{v}_all.ipk")
if os.path.exists(ipk_versioned):
    shutil.copy2(ipk_versioned, os.path.join(dist, "com.israeltv.plus_all.ipk"))

# 3. Build Tizen project zip
print("Building Tizen project zip...")
subprocess.run([sys.executable, "tools/build_tizen_zip.py"], check=True)
tizen_src = os.path.join(dist, "IsraelTVPlus-tizen-project.zip")
if os.path.exists(tizen_src):
    shutil.copy2(tizen_src, os.path.join(dist, f"IsraelTVPlus-{v}-tizen-project.zip"))

# 4. Build Kodi addon zip
print("Building Kodi addon zip...")
subprocess.run([sys.executable, "tools/build_kodi.py"], check=True)
kodi_src = os.path.join(dist, f"plugin.video.israeltvplus-{v}.zip")
if os.path.exists(kodi_src):
    shutil.copy2(kodi_src, os.path.join(dist, "plugin.video.israeltvplus.zip"))

# 5. Copy web & metadata files
for f in ["plex.m3u", "epg.xml", "epg.json", "playlist.m3u", "playlist-tv.m3u", "playlist-israel.m3u", "radio.m3u", "channels.json", "channels.js", "version.json", "version.js", "vod_data.json"]:
    src = os.path.join(ROOT, f)
    if os.path.exists(src):
        shutil.copy2(src, os.path.join(dist, f))

# 6. Recompute SHA256SUMS
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

print(f"Dist packages built and SHA256SUMS updated for v{v} successfully!")

