import os, glob
from PIL import Image

frames_dir = r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_probes\frames"

for f in sorted(glob.glob(os.path.join(frames_dir, "*.jpg"))):
    cid = os.path.basename(f).replace(".jpg", "")
    im = Image.open(f)
    w, h = im.size
    # let's sample colors or properties
    # Is it mostly colorful/cartoon?
    colors = im.convert("P", palette=Image.ADAPTIVE, colors=32).getcolors()
    # print if something interesting
