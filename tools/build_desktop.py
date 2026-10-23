"""Desktop apps for Linux (x64, ARM64 / Raspberry Pi) and macOS (Apple Silicon, Intel) from the official Electron
release zips + platforms/desktop/main.js + the web app. Runs on Windows: the Electron zips are copied entry by entry,
so Unix permissions and the macOS framework symlinks survive.

usage: python -I tools/build_desktop.py [--test-win]
  needs build/electron/electron-v<VER>-<target>.zip (downloaded from github.com/electron/electron/releases and checked
  against its SHASUMS256.txt). --test-win also makes build/desktop-win (a Windows copy for testing only).
out: dist/IsraelTVPlus-linux-x64.zip, -linux-arm64.zip, -macos-arm64.zip, -macos-x64.zip
"""
import glob
import json
import os
import re
import sys
import zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CACHE = os.path.join(ROOT, "build", "electron")
DIST = os.path.join(ROOT, "dist")
VERSION = open(os.path.join(ROOT, "VERSION"), encoding="utf-8").read().strip()
WEB_FILES = [l.strip() for l in open(os.path.join(ROOT, "tools", "webfiles.txt"), encoding="utf-8")
             if l.strip() and not l.startswith("#")]
LINUX_README = """ישראל TV+ ללינוקס
================
1. חלצו את התיקייה.
2. הפעלה: ./israel-tv-plus   (או הוסיפו לתפריט: ./install-menu.sh)
אם מופיעה שגיאת sandbox:  ./israel-tv-plus --no-sandbox
"""
MAC_README = """ישראל TV+ למק
============
1. גררו את "Israel TV Plus.app" לתיקיית Applications.
2. האפליקציה אינה חתומה ב-Apple, לכן בפעם הראשונה הריצו ב-Terminal:
     xattr -cr "/Applications/Israel TV Plus.app"
   ואז פתחו אותה רגיל (או: קליק ימני > Open).
"""
INSTALL_MENU = """#!/bin/sh
# adds Israel TV+ to the application menu of the current user
DIR="$(cd "$(dirname "$0")" && pwd)"
mkdir -p "$HOME/.local/share/applications"
cat > "$HOME/.local/share/applications/israel-tv-plus.desktop" <<EOF
[Desktop Entry]
Type=Application
Name=Israel TV+
Name[he]=ישראל TV+
Comment=Live Israeli TV and radio
Exec="$DIR/israel-tv-plus" %U
Icon=$DIR/israel-tv-plus.png
Categories=AudioVideo;Video;TV;
Terminal=false
EOF
echo "added to the menu"
"""


def app_files():
    """(path inside resources/app, bytes) for main.js, package.json and the web app."""
    yield "main.js", open(os.path.join(ROOT, "platforms", "desktop", "main.js"), "rb").read()
    yield "package.json", json.dumps({"name": "israel-tv-plus", "productName": "Israel TV Plus", "version": VERSION,
                                      "main": "main.js", "author": "dor2500"}, indent=2).encode()
    for f in WEB_FILES:
        yield "www/" + f, open(os.path.join(ROOT, f), "rb").read()
    for p in glob.glob(os.path.join(ROOT, "assets", "**", "*"), recursive=True):
        rel = os.path.relpath(p, ROOT).replace("\\", "/")
        if os.path.isfile(p) and not re.search(r"&|\.map$|banner-1280|icon-1024", rel):
            yield "www/" + rel, open(p, "rb").read()


def add(zo, name, data, mode=0o644):
    zi = zipfile.ZipInfo(name, (2026, 10, 23, 12, 0, 0))
    zi.external_attr = (0o100000 | mode) << 16
    zi.create_system = 3  # unix, so the mode bits are honoured
    zi.compress_type = zipfile.ZIP_DEFLATED
    zo.writestr(zi, data)


def build(target, ver):
    src = os.path.join(CACHE, f"electron-v{ver}-{target}.zip")
    mac = target.startswith("darwin")
    label = target.replace("darwin", "macos")
    top = f"IsraelTVPlus-{label}/"
    out = os.path.join(DIST, f"IsraelTVPlus-{label}.zip")
    if mac:
        res = top + "Israel TV Plus.app/Contents/Resources/"
    elif target.startswith("win32"):
        res = top + "resources/"
    else:
        res = top + "resources/"
    with zipfile.ZipFile(src) as zi, zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as zo:
        for it in zi.infolist():
            name = it.filename
            if name.endswith("default_app.asar"):
                continue
            if mac:
                name = name.replace("Electron.app/", "Israel TV Plus.app/", 1)
            elif name == "electron":
                name = "israel-tv-plus"
            elif name == "electron.exe":
                name = "IsraelTVPlus.exe"
            ni = zipfile.ZipInfo(top + name, it.date_time)
            ni.external_attr, ni.create_system, ni.compress_type = it.external_attr, it.create_system, it.compress_type
            zo.writestr(ni, zi.read(it))
        for rel, data in app_files():
            add(zo, res + "app/" + rel, data)
        if mac:
            add(zo, top + "README.txt", MAC_README.encode())
        elif not target.startswith("win32"):
            add(zo, top + "README.txt", LINUX_README.encode())
            add(zo, top + "install-menu.sh", INSTALL_MENU.encode(), 0o755)
            add(zo, top + "israel-tv-plus.png", open(os.path.join(ROOT, "assets", "app", "icon-512.png"), "rb").read())
    print(f"{out}  {os.path.getsize(out) / 1e6:.0f} MB")
    return out


def main():
    zips = glob.glob(os.path.join(CACHE, "electron-v*-linux-x64.zip"))
    if not zips:
        sys.exit("download the Electron zips into build/electron first (see the docstring)")
    ver = re.search(r"electron-v([\d.]+)-", os.path.basename(zips[0])).group(1)
    os.makedirs(DIST, exist_ok=True)
    for t in ("linux-x64", "linux-arm64", "darwin-arm64", "darwin-x64"):
        build(t, ver)
    if "--test-win" in sys.argv:
        z = build("win32-x64", ver)
        test = os.path.join(ROOT, "build", "desktop-win")
        with zipfile.ZipFile(z) as zf:
            zf.extractall(test)
        os.remove(z)
        print("test copy:", test)


if __name__ == "__main__":
    main()
