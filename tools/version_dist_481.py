import os, shutil, hashlib

VERSION = "4.8.1"
dist_dir = "dist"
os.makedirs(dist_dir, exist_ok=True)

# Android phone & TV
apk_src = "android/app/build/outputs/apk/release/app-release.apk"
if os.path.exists(apk_src):
    shutil.copyfile(apk_src, f"{dist_dir}/IsraelTVPlus-{VERSION}-android.apk")
    shutil.copyfile(apk_src, f"{dist_dir}/IsraelTVPlus-android.apk")
    shutil.copyfile(apk_src, f"{dist_dir}/IsraelTVPlus-{VERSION}-androidtv.apk")
    shutil.copyfile(apk_src, f"{dist_dir}/IsraelTVPlus-androidtv.apk")
    print("Copied Android APKs")

# Wear OS
wear_src = "wear/app/build/outputs/apk/release/app-release.apk"
if os.path.exists(wear_src):
    shutil.copyfile(wear_src, f"{dist_dir}/IsraelTVPlus-{VERSION}-wearos.apk")
    shutil.copyfile(wear_src, f"{dist_dir}/IsraelTVPlus-wearos.apk")
    print("Copied Wear OS APKs")

# WebOS IPK
ipk_v = f"{dist_dir}/com.israeltv.plus_{VERSION}_all.ipk"
ipk_latest = f"{dist_dir}/com.israeltv.plus_all.ipk"
if os.path.exists(ipk_v):
    shutil.copyfile(ipk_v, ipk_latest)
    print("Copied WebOS IPK")

# Tizen zip
tizen_v = f"{dist_dir}/IsraelTVPlus-{VERSION}-tizen-project.zip"
tizen_latest = f"{dist_dir}/IsraelTVPlus-tizen-project.zip"
if os.path.exists(tizen_latest):
    shutil.copyfile(tizen_latest, tizen_v)
    print("Copied Tizen zip")

# Kodi zip
kodi_v = f"{dist_dir}/plugin.video.israeltvplus-{VERSION}.zip"
kodi_latest = f"{dist_dir}/plugin.video.israeltvplus.zip"
if os.path.exists(kodi_v):
    shutil.copyfile(kodi_v, kodi_latest)
    print("Copied Kodi zip")

# Desktop packages
platforms = [
    "windows-portable", "windows-setup", "macos-arm64", "macos-x64", "linux-x64", "linux-arm64"
]
for p in platforms:
    ext = ".exe" if "setup" in p else ".zip"
    latest_f = f"{dist_dir}/IsraelTVPlus-{p}{ext}"
    versioned_f = f"{dist_dir}/IsraelTVPlus-{VERSION}-{p}{ext}"
    if os.path.exists(latest_f):
        shutil.copyfile(latest_f, versioned_f)
    elif os.path.exists(versioned_f):
        shutil.copyfile(versioned_f, latest_f)
print("Updated desktop packages")

# Remove any old version files from dist
for f in os.listdir(dist_dir):
    if any(old in f for old in ["4.7.6", "4.7.7", "4.7.8", "4.7.9", "4.8.0"]):
        os.remove(os.path.join(dist_dir, f))
        print("Removed old dist asset:", f)

# Compute SHA256SUMS.txt
sums = []
for f in sorted(os.listdir(dist_dir)):
    if f == "SHA256SUMS.txt":
        continue
    p = os.path.join(dist_dir, f)
    if os.path.isfile(p):
        h = hashlib.sha256(open(p, "rb").read()).hexdigest()
        sums.append(f"{h}  {f}")

with open(f"{dist_dir}/SHA256SUMS.txt", "w", encoding="utf-8") as f:
    f.write("\n".join(sums) + "\n")

print(f"Generated {dist_dir}/SHA256SUMS.txt with {len(sums)} files.")
