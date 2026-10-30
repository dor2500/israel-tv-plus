"""The channel numbers viewers know: Kan 11 is 11, Keshet 12 is 12. Every tool that renumbers goes through here.

usage: python -I tools/channel_numbers.py   -> renumbers channels.json + channels.js in place
Everything without a real number keeps its order from 100 up, so the real ones stay first and never collide.
"""
import json
import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
FIRST_FREE = 100
REAL_NUMBERS = {
    "channel9": 9,
    "calcala10": 10,
    "kan11": 11,
    "keshet12": 12,
    "reshet13": 13,
    "now14": 14,
    "i24news_he": 15,
    "channel16": 16,
    "kan_hinuchit": 23,
    "channel24": 24,
    "channel_98_il": 98,
    "knesset": 99,
}


def assign(channels):
    """Numbers [channels] in the order given and returns them sorted by number."""
    next_free = FIRST_FREE
    for c in channels:
        if c["id"] in REAL_NUMBERS:
            c["number"] = REAL_NUMBERS[c["id"]]
        else:
            c["number"] = next_free
            next_free += 1
    return sorted(channels, key=lambda c: c["number"])


def main():
    path = os.path.join(ROOT, "channels.json")
    with open(path, encoding="utf-8") as f:
        channels = assign(sorted(json.load(f), key=lambda c: c.get("number", 0)))
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(channels, f, ensure_ascii=False, indent=2)
    with open(os.path.join(ROOT, "channels.js"), "w", encoding="utf-8", newline="\n") as f:
        f.write("window.CHANNELS_DATA = window.ITV_CHANNELS = " + json.dumps(channels, ensure_ascii=False, indent=1) + ";\n")
    print("%d channels, real numbers: %s" % (len(channels), sorted(REAL_NUMBERS.values())))


if __name__ == "__main__":
    main()
