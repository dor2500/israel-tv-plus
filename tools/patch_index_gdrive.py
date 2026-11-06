import os

p = "index.html"
with open(p, "r", encoding="utf-8") as f:
    c = f.read()

target = '<script src="assets/hls.min.js"></script>'
replacement = '<script src="assets/hls.min.js"></script>\n  <script src="https://accounts.google.com/gsi/client" async defer></script>'

if target in c:
    c = c.replace(target, replacement, 1)
    with open(p, "w", encoding="utf-8") as f:
        f.write(c)
    print("index.html updated successfully with Google GIS library!")
else:
    print("target not found")

