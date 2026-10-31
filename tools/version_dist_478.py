import os, shutil

dist = "dist"
v = "4.7.8"
old_v = "4.7.7"

# Copy newly built APKs from build outputs
android_apk = "android/app/build/outputs/apk/release/app-release.apk"
wear_apk = "wear/app/build/outputs/apk/release/app-release.apk"

if os.path.exists(android_apk):
    shutil.copy2(android_apk, os.path.join(dist, "IsraelTVPlus-android.apk"))
    shutil.copy2(android_apk, os.path.join(dist, "IsraelTVPlus-androidtv.apk"))
    shutil.copy2(android_apk, os.path.join(dist, f"IsraelTVPlus-{v}-android.apk"))
    shutil.copy2(android_apk, os.path.join(dist, f"IsraelTVPlus-{v}-androidtv.apk"))
    print("Copied Android release APK to dist")

if os.path.exists(wear_apk):
    shutil.copy2(wear_apk, os.path.join(dist, "IsraelTVPlus-wearos.apk"))
    shutil.copy2(wear_apk, os.path.join(dist, f"IsraelTVPlus-{v}-wearos.apk"))
    print("Copied Wear OS release APK to dist")

# Remove old 4.7.7 files
for f in os.listdir(dist):
    if old_v in f:
        os.remove(os.path.join(dist, f))
        print("Removed old:", f)

# Canonical to versioned pairs
pairs = [
    ("IsraelTVPlus-linux-x64.zip", f"IsraelTVPlus-{v}-linux-x64.zip"),
    ("IsraelTVPlus-linux-arm64.zip", f"IsraelTVPlus-{v}-linux-arm64.zip"),
    ("IsraelTVPlus-macos-x64.zip", f"IsraelTVPlus-{v}-macos-x64.zip"),
    ("IsraelTVPlus-macos-arm64.zip", f"IsraelTVPlus-{v}-macos-arm64.zip"),
    ("IsraelTVPlus-tizen-project.zip", f"IsraelTVPlus-{v}-tizen-project.zip"),
    ("IsraelTVPlus-windows-portable.zip", f"IsraelTVPlus-{v}-windows-portable.zip"),
    ("IsraelTVPlus-windows-setup.exe", f"IsraelTVPlus-{v}-windows-setup.exe"),
]

for src, dst in pairs:
    src_p = os.path.join(dist, src)
    dst_p = os.path.join(dist, dst)
    if os.path.exists(src_p):
        shutil.copy2(src_p, dst_p)
        print(f"Copied {src} -> {dst}")

# IPK canonical copy
ipk_v = f"com.israeltv.plus_{v}_all.ipk"
ipk_canon = "com.israeltv.plus_all.ipk"
if os.path.exists(os.path.join(dist, ipk_v)):
    shutil.copy2(os.path.join(dist, ipk_v), os.path.join(dist, ipk_canon))
    print(f"Copied {ipk_v} -> {ipk_canon}")

print("Dist versioning for 4.7.8 complete.")
