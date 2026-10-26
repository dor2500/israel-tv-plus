"""Stamps the version (file VERSION) into every platform and writes the update files.

usage: python -I tools/make_version.py

writes
  version.js     window.ITV_BUILD = {version, code, app, shell, date} – loaded by index.html (the installed build)
  version.json   what the apps compare with when they check for updates (served by GitHub Pages):
                 code   – grows with every publish; a higher code makes the apps load the published screen, code
                          and channel list (live update, no reinstall)
                 app    – version number of the installed packages (3.1.2 -> 312); higher = new installer to download
                 shell  – what the native shells (Android / Windows bridges) offer; a live build that needs a newer
                          shell is not loaded by older ones
                 native – per-platform installer links, notes – from CHANGELOG.md
and updates appinfo.json (LG), platforms/tizen/config.xml, platforms/windows (setup + exe version).
"""
import datetime
import json
import os
import re

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SHELL = 3   # bump when MainActivity.java / IsraelTVPlus.cs gain something the web code depends on
DL = "https://github.com/dor2500/israel-tv-plus/releases/latest/download/"


def read(p):
    with open(os.path.join(ROOT, p), encoding="utf-8") as f:
        return f.read()


def write(p, s):
    with open(os.path.join(ROOT, p), "w", encoding="utf-8", newline="\n") as f:
        f.write(s)


def notes(version):
    """Bullet lines of the "## <version>" section of CHANGELOG.md."""
    try:
        text = read("CHANGELOG.md")
    except FileNotFoundError:
        return []
    m = re.search(r"^## v?" + re.escape(version) + r"\b.*?$(.*?)(?=^## |\Z)", text, re.S | re.M)
    if not m:
        return []
    return [re.sub(r"\*\*|`", "", l.strip()[2:]) for l in m.group(1).splitlines() if l.strip().startswith("- ")]


def main():
    version = read("VERSION").strip()
    major, minor, patch = (int(x) for x in version.split("."))
    if major == 4 and minor == 3:
        app = 500 + patch
    elif major == 4 and minor == 4:
        app = 540 + patch
    else:
        app = max(550, major * 100 + minor * 10 + patch)
    now = datetime.datetime.now(datetime.timezone.utc)
    code = int(now.strftime("%Y%m%d%H%M"))
    date = now.strftime("%Y-%m-%d")
    build = {"version": version, "code": code, "app": app, "shell": SHELL, "date": date}
    # https relay for the web site's http channels (relay/worker.js); the file relay.url holds its address once deployed
    try:
        relay = read("relay.url").strip()
    except FileNotFoundError:
        relay = ""
    if relay:
        build["relay"] = relay
    write("version.js", "// written by tools/make_version.py\nwindow.ITV_BUILD = " + json.dumps(build) + ";\n")

    # the release of this exact version, not releases/latest: right after a release GitHub's CDN can still answer
    # "latest" with the previous file, and the apps would reinstall the old version (seen with 3.4.0)
    dl = DL.replace("/latest/download/", "/download/v%s/" % version)
    native = {
        "android": {"version": version, "app": app, "url": dl + "IsraelTVPlus-android.apk"},
        "windows": {"version": version, "app": app, "url": dl + "IsraelTVPlus-windows-setup.exe"},
        "webos": {"version": version, "app": app, "url": dl + "com.israeltv.plus_all.ipk",
                  "note": "המסך, הקוד והערוצים מתעדכנים אונליין גם בלי התקנה מחדש"},
        "tizen": {"version": version, "app": app, "url": dl + "IsraelTVPlus-tizen-project.zip"},
        "mac": {"version": version, "app": app, "url": dl + "IsraelTVPlus-macos-arm64.zip"},
        "linux": {"version": version, "app": app, "url": dl + "IsraelTVPlus-linux-x64.zip"},
    }
    data = dict(build, notes=notes(version), native=native)
    write("version.json", json.dumps(data, ensure_ascii=False, indent=2) + "\n")

    info = json.loads(read("appinfo.json"))
    info["version"] = version
    write("appinfo.json", json.dumps(info, ensure_ascii=False, indent=2) + "\n")
    write("platforms/tizen/config.xml", re.sub(r'(<widget[^>]*?\sversion=")[\d.]+(")', r"\g<1>" + version + r"\2",
                                               read("platforms/tizen/config.xml")))
    write("platforms/windows/setup.iss", re.sub(r'#define AppVersion "[\d.]+"', f'#define AppVersion "{version}"',
                                                read("platforms/windows/setup.iss")))
    write("platforms/windows/IsraelTVPlus.cs", re.sub(r'AssemblyVersion\("[\d.]+"\)', f'AssemblyVersion("{version}.0")',
                                                      read("platforms/windows/IsraelTVPlus.cs")))
    print(f"version {version} (app {app}, code {code}, shell {SHELL}), {len(data['notes'])} notes")


if __name__ == "__main__":
    main()
