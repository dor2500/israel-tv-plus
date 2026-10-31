import os, shutil, hashlib

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
dist = os.path.join(ROOT, "dist")
v = "4.8.13"

# Copy newly built APK or rename existing
for base in ["IsraelTVPlus-android.apk", "IsraelTVPlus-androidtv.apk", "IsraelTVPlus-wearos.apk", "IsraelTVPlus-windows-setup.exe", "IsraelTVPlus-macos-arm64.zip", "IsraelTVPlus-linux-x64.zip", "IsraelTVPlus-tizen-project.zip", "plugin.video.israeltvplus.zip"]:
    unv = os.path.join(dist, base)
    ver = os.path.join(dist, base.replace("IsraelTVPlus-", f"IsraelTVPlus-{v}-").replace("plugin.video.israeltvplus", f"plugin.video.israeltvplus-{v}"))
    if os.path.exists(unv) and not os.path.exists(ver):
        shutil.copy2(unv, ver)
        print(f"Created versioned {ver}")

for f in ["plex.m3u", "epg.xml", "playlist.m3u"]:
    src = os.path.join(ROOT, f)
    if os.path.exists(src):
        shutil.copy2(src, os.path.join(dist, f))
        print(f"Copied {f} to dist")

lines = []
for fname in sorted(os.listdir(dist)):
    p = os.path.join(dist, fname)
    if os.path.isfile(p) and fname not in ["SHA256SUMS", "SHA256SUMS.txt"]:
        with open(p, "rb") as fh:
            h = hashlib.sha256(fh.read()).hexdigest()
        lines.append(f"{h}  {fname}")

with open(os.path.join(dist, "SHA256SUMS"), "w", encoding="utf-8") as f:
    f.write("\n".join(lines) + "\n")

print("Generated SHA256SUMS for 4.8.13!")
