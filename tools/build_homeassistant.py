#!/usr/bin/env python3
"""Build Home Assistant distribution archive for Israel TV+."""
import os
import zipfile

def build():
    repo = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    dist = os.path.join(repo, "dist")
    os.makedirs(dist, exist_ok=True)
    out_zip = os.path.join(dist, "IsraelTVPlus-homeassistant.zip")

    cc_dir = os.path.join(repo, "custom_components", "israel_tv_plus")

    with zipfile.ZipFile(out_zip, "w", zipfile.ZIP_DEFLATED) as z:
        for root, dirs, files in os.walk(cc_dir):
            for file in files:
                full_path = os.path.join(root, file)
                rel_path = os.path.relpath(full_path, repo)
                z.write(full_path, rel_path)

        card_path = os.path.join(repo, "platforms", "homeassistant", "israel-tv-card.js")
        if os.path.isfile(card_path):
            z.write(card_path, "israel-tv-card.js")

        readme_path = os.path.join(repo, "platforms", "homeassistant", "README.md")
        if os.path.isfile(readme_path):
            z.write(readme_path, "README.md")

        hacs_path = os.path.join(repo, "hacs.json")
        if os.path.isfile(hacs_path):
            z.write(hacs_path, "hacs.json")

    size_kb = os.path.getsize(out_zip) / 1024
    print(f"Built {out_zip} ({size_kb:.1f} KB)")

if __name__ == "__main__":
    build()

