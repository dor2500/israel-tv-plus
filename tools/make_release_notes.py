"""Release notes for the current VERSION: the matching "## <version>" section of CHANGELOG.md.

usage: python -I tools/make_release_notes.py [out.md]   -> release_notes_latest.md by default

Used by .github/workflows/release.yml as the body of the GitHub release, so the release page, the
in-app update dialog (version.json.notes) and the CHANGELOG always say the same thing.
"""
import os
import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
version = (ROOT / "VERSION").read_text(encoding="utf-8").strip()
text = (ROOT / "CHANGELOG.md").read_text(encoding="utf-8")


def section(v):
    m = re.search(r"^## v?" + re.escape(v) + r"\b.*?$(.*?)(?=^## |\Z)", text, re.S | re.M)
    return m.group(1).strip() if m else ""


body = f"## ישראל TV+ {version}\n\n"
notes = section(version)
body += notes if notes else "—"
body += ("\n\n---\n"
         "**אנדרואיד / Android TV / Wear OS** · **LG webOS (IPK)** · **Samsung Tizen** · "
         "**Windows** · **Linux** · **macOS**\n\n"
         "לכל הפלטפורמות אותו קוד ואותם ערוצים. `version.json` ולוח `SHA256SUMS.txt` מצורפים לבדיקה.\n")

out = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else ROOT / "release_notes_latest.md")
out.write_text(body, encoding="utf-8", newline="\n")
print(f"{out}  {len(body)} chars, {len([l for l in notes.splitlines() if l.startswith('- ')])} notes")
