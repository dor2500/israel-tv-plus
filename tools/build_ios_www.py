"""Builds www/, the web app the iOS shell carries (Capacitor webDir): tools/webfiles.txt + assets + platforms/ios.

usage: python -I tools/build_ios_www.py
"""
import os
import shutil

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
WWW = os.path.join(ROOT, "www")
HLS_TAG = '<script src="assets/hls.min.js"></script>'


def main():
    shutil.rmtree(WWW, ignore_errors=True)
    os.makedirs(WWW)
    with open(os.path.join(ROOT, "tools", "webfiles.txt"), encoding="utf-8") as f:
        files = [l.strip() for l in f if l.strip() and not l.startswith("#")]
    for name in files:
        shutil.copy2(os.path.join(ROOT, name), os.path.join(WWW, name))
    shutil.copytree(os.path.join(ROOT, "assets"), os.path.join(WWW, "assets"))
    for name in ("ios.js", "ios.css"):
        shutil.copy2(os.path.join(ROOT, "platforms", "ios", name), os.path.join(WWW, name))

    path = os.path.join(WWW, "index.html")
    with open(path, encoding="utf-8", newline="") as f:
        html = f.read()
    if HLS_TAG not in html:
        raise SystemExit("index.html: hls.min.js tag not found")
    html = html.replace(HLS_TAG, HLS_TAG + '\n  <script src="ios.js"></script>')
    html = html.replace("</head>", '  <link rel="stylesheet" href="ios.css">\n</head>', 1)
    with open(path, "w", encoding="utf-8", newline="") as f:
        f.write(html)
    print("www:", len(files), "files + assets")


if __name__ == "__main__":
    main()
