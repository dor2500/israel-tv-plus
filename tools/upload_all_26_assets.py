import os, shutil, hashlib, subprocess

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)
dist = os.path.join(ROOT, "dist")
v = "4.8.49"

# 1. Copy freshly built Android APK (36.6 MB)
apk_src = os.path.join(ROOT, "android", "app", "build", "outputs", "apk", "release", "app-release.apk")
if os.path.exists(apk_src):
    shutil.copy2(apk_src, os.path.join(dist, "IsraelTVPlus-android.apk"))
    shutil.copy2(apk_src, os.path.join(dist, "IsraelTVPlus-androidtv.apk"))
    shutil.copy2(apk_src, os.path.join(dist, f"IsraelTVPlus-{v}-android.apk"))
    shutil.copy2(apk_src, os.path.join(dist, f"IsraelTVPlus-{v}-androidtv.apk"))
    print(f"Copied fresh APK ({os.path.getsize(apk_src):,} bytes) to dist!")

# 2. Recompute SHA256SUMS
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
print("Updated SHA256SUMS and SHA256SUMS.txt!")

# 3. All 26+ Assets for release
all_assets = [
    # Android
    f"dist/IsraelTVPlus-android.apk",
    f"dist/IsraelTVPlus-androidtv.apk",
    f"dist/IsraelTVPlus-{v}-android.apk",
    f"dist/IsraelTVPlus-{v}-androidtv.apk",
    # LG webOS
    f"dist/com.israeltv.plus_all.ipk",
    f"dist/com.israeltv.plus_{v}_all.ipk",
    # Samsung Tizen
    f"dist/IsraelTVPlus-tizen-project.zip",
    f"dist/IsraelTVPlus-{v}-tizen-project.zip",
    # Desktop Windows, Mac, Linux
    f"dist/IsraelTVPlus-windows-setup.exe",
    f"dist/IsraelTVPlus-windows-portable.zip",
    f"dist/IsraelTVPlus-macos-arm64.zip",
    f"dist/IsraelTVPlus-macos-x64.zip",
    f"dist/IsraelTVPlus-linux-x64.zip",
    f"dist/IsraelTVPlus-linux-arm64.zip",
    # WearOS
    f"dist/IsraelTVPlus-wearos.apk",
    # Kodi Addon
    f"dist/plugin.video.israeltvplus.zip",
    f"dist/plugin.video.israeltvplus-{v}.zip",
    # Metadata & Playlists
    f"dist/channels.json",
    f"dist/vod_data.json",
    f"dist/epg.xml",
    f"dist/epg.json",
    f"dist/playlist.m3u",
    f"dist/playlist-tv.m3u",
    f"dist/playlist-israel.m3u",
    f"dist/plex.m3u",
    f"dist/radio.m3u",
    f"dist/version.json",
    f"dist/version.js",
    f"dist/SHA256SUMS.txt"
]

existing = [a for a in all_assets if os.path.exists(a)]
print(f"Total existing assets to upload: {len(existing)}")

cmd = ["gh", "release", "upload", f"v{v}"] + existing + ["--clobber"]
print("Uploading assets to GitHub release...")
subprocess.run(cmd, check=True)
print(f"All {len(existing)} assets successfully uploaded to v{v}!")

