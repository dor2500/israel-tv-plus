import os, shutil

dist = "dist"
v = "4.7.7"
old_v = "4.7.6"

# Remove any old 4.7.6 specific files
for f in os.listdir(dist):
    if old_v in f:
        os.remove(os.path.join(dist, f))
        print("Removed old:", f)

# Canonical to versioned pairs
pairs = [
    ("IsraelTVPlus-android.apk", f"IsraelTVPlus-{v}-android.apk"),
    ("IsraelTVPlus-androidtv.apk", f"IsraelTVPlus-{v}-androidtv.apk"),
    ("IsraelTVPlus-wearos.apk", f"IsraelTVPlus-{v}-wearos.apk"),
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

print("Dist versioning complete.")
