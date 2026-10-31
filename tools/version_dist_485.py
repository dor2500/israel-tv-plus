import os, shutil, hashlib

VERSION = "4.8.5"
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

# Windows setup if exists
for fn in os.listdir(dist_dir):
    if fn.endswith(".exe") and "Setup" in fn:
        shutil.copyfile(os.path.join(dist_dir, fn), f"{dist_dir}/IsraelTVPlus-{VERSION}-windows-setup.exe")
        shutil.copyfile(os.path.join(dist_dir, fn), f"{dist_dir}/IsraelTVPlus-windows-setup.exe")
        print("Copied Windows exe")
        break

# Compute SHA256SUMS
sums = []
for fname in sorted(os.listdir(dist_dir)):
    p = os.path.join(dist_dir, fname)
    if os.path.isfile(p) and fname != "SHA256SUMS" and not fname.endswith(".txt"):
        with open(p, "rb") as f:
            h = hashlib.sha256(f.read()).hexdigest()
        sums.append(f"{h}  {fname}")

with open(f"{dist_dir}/SHA256SUMS", "w", encoding="utf-8") as f:
    f.write("\n".join(sums) + "\n")
print(f"Generated SHA256SUMS with {len(sums)} files")

