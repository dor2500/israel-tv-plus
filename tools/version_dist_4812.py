import os, shutil, hashlib

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
dist = os.path.join(ROOT, "dist")
v = "4.8.12"

# Copy newly built APK from android release output
release_apk = os.path.join(ROOT, "android", "app", "build", "outputs", "apk", "release", "app-release.apk")
if os.path.exists(release_apk):
    shutil.copy2(release_apk, os.path.join(dist, f"IsraelTVPlus-{v}-android.apk"))
    shutil.copy2(release_apk, os.path.join(dist, "IsraelTVPlus-android.apk"))
    shutil.copy2(release_apk, os.path.join(dist, f"IsraelTVPlus-{v}-androidtv.apk"))
    shutil.copy2(release_apk, os.path.join(dist, "IsraelTVPlus-androidtv.apk"))
    print("Copied Android release APK to dist")

# Copy plex.m3u and epg.xml to dist
for f in ["plex.m3u", "epg.xml"]:
    src = os.path.join(ROOT, f)
    if os.path.exists(src):
        shutil.copy2(src, os.path.join(dist, f))
        print(f"Copied {f} to dist")

# Ensure unversioned and versioned pairs
pairs = [
    (f"IsraelTVPlus-{v}-android.apk", "IsraelTVPlus-android.apk"),
    (f"IsraelTVPlus-{v}-androidtv.apk", "IsraelTVPlus-androidtv.apk"),
    (f"com.israeltv.plus_{v}_all.ipk", "com.israeltv.plus_all.ipk"),
    (f"IsraelTVPlus-{v}-tizen-project.zip", "IsraelTVPlus-tizen-project.zip"),
    (f"plugin.video.israeltvplus-{v}.zip", "plugin.video.israeltvplus.zip"),
    (f"IsraelTVPlus-{v}-wearos.apk", "IsraelTVPlus-wearos.apk"),
]

for src, dst in pairs:
    s = os.path.join(dist, src)
    d = os.path.join(dist, dst)
    if os.path.exists(s) and not os.path.exists(d):
        shutil.copy2(s, d)
        print(f"Copied {src} -> {dst}")
    elif os.path.exists(d) and not os.path.exists(s):
        shutil.copy2(d, s)
        print(f"Copied {dst} -> {src}")

# Generate SHA256SUMS for 4.8.12
lines = []
for fname in sorted(os.listdir(dist)):
    if v in fname or fname in [
        "IsraelTVPlus-android.apk", "IsraelTVPlus-androidtv.apk",
        "com.israeltv.plus_all.ipk", "IsraelTVPlus-tizen-project.zip",
        "plugin.video.israeltvplus.zip", "IsraelTVPlus-wearos.apk",
        "IsraelTVPlus-windows-setup.exe", "IsraelTVPlus-windows-portable.zip",
        "IsraelTVPlus-macos-arm64.zip", "IsraelTVPlus-linux-x64.zip",
        "plex.m3u", "epg.xml"
    ]:
        p = os.path.join(dist, fname)
        if os.path.isfile(p) and fname not in ["SHA256SUMS", "SHA256SUMS.txt"]:
            with open(p, "rb") as fh:
                h = hashlib.sha256(fh.read()).hexdigest()
            lines.append(f"{h}  {fname}")

with open(os.path.join(dist, "SHA256SUMS"), "w", encoding="utf-8") as f:
    f.write("\n".join(lines) + "\n")
with open(os.path.join(dist, "SHA256SUMS.txt"), "w", encoding="utf-8") as f:
    f.write("\n".join(lines) + "\n")

print(f"Generated SHA256SUMS with {len(lines)} files for v{v}")

