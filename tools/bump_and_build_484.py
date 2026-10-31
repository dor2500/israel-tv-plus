# tools/bump_and_build_484.py
import os, json, re, subprocess

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

VERSION = "4.8.4"

# 1. Update VERSION file
with open("VERSION", "w", encoding="utf-8") as f:
    f.write(VERSION + "\n")
print(f"Updated VERSION to {VERSION}")

# 2. Update appinfo.json
with open("appinfo.json", "r+", encoding="utf-8") as f:
    d = json.load(f)
    d["version"] = VERSION
    f.seek(0)
    json.dump(d, f, indent=2)
    f.truncate()
print(f"Updated appinfo.json to {VERSION}")

# 3. Update version.js
with open("version.js", "w", encoding="utf-8") as f:
    f.write(f'window.ITV_VERSION = "{VERSION}";\n')
print(f"Updated version.js to {VERSION}")

# 4. Update tizen config.xml
tizen_cfg = "platforms/tizen/config.xml"
if os.path.exists(tizen_cfg):
    with open(tizen_cfg, "r", encoding="utf-8") as f:
        c = f.read()
    c = re.sub(r'version="[\d\.]+"', f'version="{VERSION}"', c)
    with open(tizen_cfg, "w", encoding="utf-8") as f:
        f.write(c)
    print(f"Updated tizen config.xml to {VERSION}")

# 5. Update windows files
for p in ["platforms/windows/IsraelTVPlus.cs", "platforms/windows/setup.iss"]:
    if os.path.exists(p):
        with open(p, "r", encoding="utf-8") as f:
            c = f.read()
        c = re.sub(r'4\.\d+\.\d+', VERSION, c)
        with open(p, "w", encoding="utf-8") as f:
            f.write(c)
        print(f"Updated {p} to {VERSION}")

# 6. Re-run make_version.py & make_stremio.py
if os.path.exists("tools/make_version.py"):
    subprocess.run(["python", "tools/make_version.py"], check=True)
if os.path.exists("tools/make_stremio.py"):
    subprocess.run(["python", "tools/make_stremio.py"], check=True)

# 7. Build IPK, Kodi, Tizen, Desktop
for script in ["tools/build_ipk.py", "tools/build_kodi.py", "tools/build_tizen_zip.py", "tools/build_desktop.py", "tools/build_ios_www.py"]:
    if os.path.exists(script):
        try:
            subprocess.run(["python", script], check=True)
            print(f"Executed {script} successfully")
        except Exception as e:
            print(f"Failed {script}: {e}")
