"""Samsung Tizen project zip: platforms/tizen/config.xml + the web app + the assets folder.

usage: python -I tools/build_tizen_zip.py [out_dir]   -> <out_dir>/IsraelTVPlus-tizen-project.zip (default: dist/)

The zip is what a Tizen Studio user imports; Samsung TVs only install signed packages, so the .wgt is
built and signed in Tizen Studio with the TV's own certificate (see docs/RELEASE.md).
"""
import os
import sys
import zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SKIP = ("&", ".map")

WEB_FILES = [l.strip() for l in open(os.path.join(ROOT, "tools", "webfiles.txt"), encoding="utf-8")
             if l.strip() and not l.startswith("#") and l.strip() not in ("icon.png", "manifest.webmanifest")]


def main():
    out_dir = sys.argv[1] if len(sys.argv) > 1 else os.path.join(ROOT, "dist")
    os.makedirs(out_dir, exist_ok=True)
    path = os.path.join(out_dir, "IsraelTVPlus-tizen-project.zip")
    with zipfile.ZipFile(path, "w", zipfile.ZIP_DEFLATED) as z:
        z.write(os.path.join(ROOT, "platforms", "tizen", "config.xml"), "config.xml")
        for f in WEB_FILES:
            z.write(os.path.join(ROOT, f), f)
        z.write(os.path.join(ROOT, "assets", "app", "icon-512.png"), "icon.png")
        for base, _, names in os.walk(os.path.join(ROOT, "assets")):
            for n in sorted(names):
                if any(s in n for s in SKIP):
                    continue
                full = os.path.join(base, n)
                z.write(full, os.path.relpath(full, ROOT).replace("\\", "/"))
    print(f"{path}  {os.path.getsize(path) // 1024} KB, {len(z.namelist())} files")


if __name__ == "__main__":
    main()
