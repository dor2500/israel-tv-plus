"""The Kodi add-on as an installable zip ("Install from zip file"), stamped with the app's VERSION.

usage: python -I tools/build_kodi.py [out_dir]   -> dist/plugin.video.israeltvplus-<version>.zip
"""
import os
import sys
import zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ADDON = "plugin.video.israeltvplus"
SRC = os.path.join(ROOT, "platforms", "kodi", ADDON)


def main():
    out_dir = sys.argv[1] if len(sys.argv) > 1 else os.path.join(ROOT, "dist")
    os.makedirs(out_dir, exist_ok=True)
    with open(os.path.join(ROOT, "VERSION"), encoding="utf-8") as f:
        version = f.read().strip()
    with open(os.path.join(SRC, "addon.xml"), encoding="utf-8") as f:
        addon_xml = f.read().replace("@VERSION@", version)
    out = os.path.join(out_dir, "%s-%s.zip" % (ADDON, version))
    # Kodi needs forward slashes and exactly one top folder named after the add-on id
    with zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as z:
        z.writestr(ADDON + "/addon.xml", addon_xml)
        z.write(os.path.join(SRC, "main.py"), ADDON + "/main.py")
        z.write(os.path.join(ROOT, "channels.json"), ADDON + "/resources/channels.json")
        z.write(os.path.join(ROOT, "assets", "app", "icon-256.png"), ADDON + "/resources/icon.png")
        z.write(os.path.join(ROOT, "assets", "app", "banner-1280x720.png"), ADDON + "/resources/fanart.png")
    print("%s  %d KB" % (out, os.path.getsize(out) // 1024))


if __name__ == "__main__":
    main()
