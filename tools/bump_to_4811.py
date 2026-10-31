import os, json, re, subprocess

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

VERSION = "4.8.11"
APP_CODE = 591  # 100 + 4*100 + 8*10 + 11

# 1. Update VERSION file
with open("VERSION", "w", encoding="utf-8") as f:
    f.write(VERSION + "\n")
print(f"Updated VERSION to {VERSION}")

# 2. Run make_version.py
if os.path.exists("tools/make_version.py"):
    subprocess.run(["python", "tools/make_version.py"], check=True)
print("make_version.py completed")

# 3. Ensure version.json has direct versioned URLs and correct app code
with open("version.json", "r", encoding="utf-8") as f:
    vdata = json.load(f)

vdata["version"] = VERSION
vdata["app"] = APP_CODE
if "native" in vdata:
    for plat, info in vdata["native"].items():
        info["version"] = VERSION
        if plat == "android":
            info["app"] = APP_CODE
            info["url"] = f"https://github.com/dor2500/israel-tv-plus/releases/download/v{VERSION}/IsraelTVPlus-{VERSION}-android.apk"
        elif plat == "webos":
            info["app"] = APP_CODE
            info["url"] = f"https://github.com/dor2500/israel-tv-plus/releases/download/v{VERSION}/com.israeltv.plus_{VERSION}_all.ipk"
        elif plat == "tizen":
            info["app"] = APP_CODE
            info["url"] = f"https://github.com/dor2500/israel-tv-plus/releases/download/v{VERSION}/IsraelTVPlus-{VERSION}-tizen-project.zip"
        elif plat == "wear":
            info["app"] = 1000000 + 491
            info["url"] = f"https://github.com/dor2500/israel-tv-plus/releases/download/v{VERSION}/IsraelTVPlus-{VERSION}-wearos.apk"
        elif plat == "windows":
            info["app"] = APP_CODE
            info["url"] = f"https://github.com/dor2500/israel-tv-plus/releases/download/v{VERSION}/IsraelTVPlus-windows-setup.exe"
        elif plat == "mac":
            info["app"] = APP_CODE
            info["url"] = f"https://github.com/dor2500/israel-tv-plus/releases/download/v{VERSION}/IsraelTVPlus-macos-arm64.zip"
        elif plat == "linux":
            info["app"] = APP_CODE
            info["url"] = f"https://github.com/dor2500/israel-tv-plus/releases/download/v{VERSION}/IsraelTVPlus-linux-x64.zip"

with open("version.json", "w", encoding="utf-8") as f:
    json.dump(vdata, f, ensure_ascii=False, indent=2)
    f.write("\n")
print(f"Updated version.json for {VERSION}")
