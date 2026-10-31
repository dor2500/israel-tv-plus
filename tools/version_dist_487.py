# tools/version_dist_487.py
import os, shutil, hashlib

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
dist = os.path.join(ROOT, "dist")
v = "4.8.7"

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

# Generate SHA256SUMS for 4.8.7
lines = []
for fname in sorted(os.listdir(dist)):
    if v in fname or fname in ["IsraelTVPlus-android.apk", "IsraelTVPlus-androidtv.apk", "com.israeltv.plus_all.ipk", "IsraelTVPlus-tizen-project.zip", "plugin.video.israeltvplus.zip", "IsraelTVPlus-wearos.apk", "IsraelTVPlus-windows-setup.exe", "IsraelTVPlus-windows-portable.zip", "IsraelTVPlus-macos-arm64.zip", "IsraelTVPlus-linux-x64.zip"]:
        p = os.path.join(dist, fname)
        if os.path.isfile(p) and fname not in ["SHA256SUMS", "SHA256SUMS.txt"]:
            with open(p, "rb") as fh:
                h = hashlib.sha256(fh.read()).hexdigest()
            lines.append(f"{h}  {fname}")

with open(os.path.join(dist, "SHA256SUMS"), "w", encoding="utf-8") as f:
    f.write("\n".join(lines) + "\n")
print(f"Generated SHA256SUMS with {len(lines)} entries")
