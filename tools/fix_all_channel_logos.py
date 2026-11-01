import json, os, urllib.request, shutil
from PIL import Image, ImageDraw, ImageFont

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

LOGOS_DIR = os.path.join(ROOT, "assets", "logos")
VOD_DIR = os.path.join(ROOT, "assets", "vod")

with open("channels.json", "r", encoding="utf-8") as f:
    channels = json.load(f)

# Helper to create stylized logo PNG if missing
def create_text_badge_logo(filepath, title, subtitle, bg_color, fg_color="#FFFFFF", accent="#3B82F6"):
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    # Rounded rect background
    draw.rounded_rectangle([20, 20, 492, 492], radius=64, fill=bg_color, outline=accent, width=8)
    
    # Text rendering (simple fallback fonts)
    try:
        font_large = ImageFont.truetype("arial.ttf", 64)
        font_small = ImageFont.truetype("arial.ttf", 36)
    except:
        font_large = ImageFont.load_default()
        font_small = ImageFont.load_default()
        
    # Draw centered title
    draw.text((256, 210), title, fill=fg_color, font=font_large, anchor="mm")
    if subtitle:
        draw.text((256, 300), subtitle, fill="#E2E8F0", font=font_small, anchor="mm")
    img.save(filepath, "PNG")
    print(f"Created badge logo: {filepath}")

# 1. Download successful remote logos
remote_downloads = {
    "beinsports_xtra_fhd": "https://www.google.com/s2/favicons?domain=beinsports.com&sz=128",
    "pga_tour_golf_fhd": "https://www.google.com/s2/favicons?domain=pgatour.com&sz=128",
    "vevo_pop_live_fhd": "https://www.google.com/s2/favicons?domain=vevo.com&sz=128",
    "cbs_sports_hq_fhd": "https://www.google.com/s2/favicons?domain=cbssports.com&sz=128",
    "cartoon_network_hd": "https://upload.wikimedia.org/wikipedia/commons/8/80/Cartoon_Network_2010_logo.svg",
    "disney_channel_global": "https://upload.wikimedia.org/wikipedia/commons/d/d2/2019_Disney_Channel_logo.svg",
    "crunchyroll_anime": "https://upload.wikimedia.org/wikipedia/commons/0/08/Crunchyroll_Logo.png",
    "motorvision_hd": "https://i.imgur.com/v4kj8pI.png",
    "cars_tv_hd": "https://i.imgur.com/JLSHiTp.png",
    "racer_international_hd": "https://provider-static.plex.tv/epg/cms/production/dd4e5871-4347-49e3-9ea1-a8770ea9c518/RACER_Intl_logo_1500x1000.png",
    "sky_racing_hd": "https://i.imgur.com/Hf0EiaW.png",
    "ch16_immergo_il": "https://ch16israel.vod.immergo.tv/ch16israel/uploaded/bfbd4781-67a7-437e-8f7e-898a5cbc4ab4.png",
    "i24news_hebrew_hd": "https://www.i24news.tv/images/favicon.png",
    "vacation_channel_hd": "https://static.g-cdn.app/streams/vacation-channel-il-hd.png"
}

opener = urllib.request.build_opener()
opener.addheaders = [('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36')]
urllib.request.install_opener(opener)

for cid, url in remote_downloads.items():
    ext = ".svg" if url.endswith(".svg") else ".png"
    target = os.path.join(LOGOS_DIR, f"{cid}{ext}")
    if not os.path.exists(target) or os.path.getsize(target) < 100:
        try:
            with urllib.request.urlopen(url, timeout=7) as resp:
                data = resp.read()
                if len(data) > 100:
                    with open(target, "wb") as f:
                        f.write(data)
                    print(f"Downloaded logo for {cid} ({len(data)} bytes)")
        except Exception as e:
            print(f"Failed to download {cid}: {e}")

# 2. Copy VOD posters into logos directory with clean names
vod_copies = {
    "vod_checkout": "checkout.png",
    "vod_manayek": "manayek.jpg",
    "vod_tehran": "tehran.jpg",
    "vod_hayehudim_baim": "hayehudim_baim.jpg",
    "vod_hamefakedet": "hamefakedet.jpg",
    "vod_bo_nechol": "bo_nechol.jpg",
    "vod_cramel": "cramel.jpg",
    "vod_zehu_ze": "zehu_ze.jpg",
    "vod_rehearsals": "chazarot.jpg",
    "vod_valley_of_tears": "valley_of_tears.jpg",
}

for cid, fname in vod_copies.items():
    src = os.path.join(VOD_DIR, fname)
    ext = os.path.splitext(fname)[1]
    dst = os.path.join(LOGOS_DIR, f"{cid}{ext}")
    if os.path.exists(src):
        shutil.copy2(src, dst)
        print(f"Copied VOD logo for {cid} -> {dst}")

# 3. Create stylized badges for channels missing files
create_text_badge_logo(os.path.join(LOGOS_DIR, "formula1.png"), "F1", "SKY SPORTS F1", "#E10600", accent="#FFFFFF")
create_text_badge_logo(os.path.join(LOGOS_DIR, "boomerang.png"), "BOOMERANG", "CARTOONS", "#00AEEF", accent="#FFFFFF")
create_text_badge_logo(os.path.join(LOGOS_DIR, "pbs_kids.png"), "PBS KIDS", "HD", "#77A600", accent="#FFFFFF")
create_text_badge_logo(os.path.join(LOGOS_DIR, "powernation.png"), "POWER", "NATION TV", "#F97316", accent="#FFFFFF")
create_text_badge_logo(os.path.join(LOGOS_DIR, "mtv_live.png"), "MTV LIVE", "MUSIC & SHOWS", "#10B981", accent="#FFFFFF")
create_text_badge_logo(os.path.join(LOGOS_DIR, "mtv_hits.png"), "MTV HITS", "CLIPS & POP", "#8B5CF6", accent="#FFFFFF")
create_text_badge_logo(os.path.join(LOGOS_DIR, "rtl2.png"), "RTL ZWEI", "GERMANY HD", "#EF4444", accent="#FFFFFF")

# 4. Explicit mapping table for channels needing fixes
CHANNEL_LOGO_FIXES = {
    # 15 broken local paths
    "one_doco": "assets/logos/one_doco_hd.png",
    "freetv_kids_family_il": "assets/logos/freetv_kids_family_il.png",
    "yes_movies_kids_plus_il": "assets/logos/yes_movies_kids_plus_il.png",
    "keshet_music_il": "assets/logos/keshet_music.png",
    "hot_docu_il": "assets/logos/hot_8.png",
    "humor_2_channel_il": "assets/logos/humor_channel_il.png",
    "viva_vintage_il": "assets/logos/viva_israel.png",
    "freetv_action_cinema_il": "assets/logos/freetv_movies_action.png",
    "freetv_drama_cinema_il": "assets/logos/freetv_movies_drama.png",
    "freetv_comedy_cinema_il": "assets/logos/freetv_movies_comedy.png",
    "freetv_israeli_cinema_il": "assets/logos/freetv_israeli_movies.png",
    "mtv_live_hd": "assets/logos/mtv_live.png",
    "mtv_clips_hd": "assets/logos/mtv_hits.png",
    "rtl_zwei_hd": "assets/logos/rtl2.png",
    "natgeo_wild_il": "assets/logos/nat_geo_wild_il.png",

    # Israeli channels with broken remote URLs
    "knesset_tv_hd": "assets/logos/knesset.png",
    "channel_9_israel_hd": "assets/logos/ch9.png",
    "ch14_now_fast": "assets/logos/now14.png",
    "hidabroot_tv_hd": "assets/logos/hidabroot.png",
    "i24news_arabic_hd": "assets/logos/i24news_ar.png",
    "i24news_english_hd": "assets/logos/i24news_en.png",
    "i24news_hebrew_hd": "assets/logos/i24news_he.png",
    "kan_hinuchit_mc": "assets/logos/kan_hinuchit.png",
    "hop_kids_mc": "assets/logos/hop_kids_israel.png",
    "zoom_kids_mc": "assets/logos/zoom_kids_israel.png",
    "viva_premium_mc": "assets/logos/viva_premium_il.png",
    "good_life_mc": "assets/logos/good_life_israel.png",
    "yes_movies_action_mc": "assets/logos/yes_movies_action.png",
    "hot_drama_mc": "assets/logos/hot_drama.png",
    "hot_doco_hd": "assets/logos/hot_8.png",
    "channel_hayeladim_hd": "assets/logos/arutz_hayeladim.png",
    "junior_channel_hd": "assets/logos/junior.png",
    "baby_channel_hd": "assets/logos/baby_tv_he.png",
    "luli_channel_hd": "assets/logos/luli_tv.png",
    "yes_movies_kids_hd": "assets/logos/yes_movies_kids_il.png",
    "hala_tv_hd": "assets/logos/hala_tv_il.png",

    # VOD Channels
    "vod_checkout": "assets/logos/vod_checkout.png",
    "vod_manayek": "assets/logos/vod_manayek.jpg",
    "vod_tehran": "assets/logos/vod_tehran.jpg",
    "vod_hayehudim_baim": "assets/logos/vod_hayehudim_baim.jpg",
    "vod_hamefakedet": "assets/logos/vod_hamefakedet.jpg",
    "vod_bo_nechol": "assets/logos/vod_bo_nechol.jpg",
    "vod_cramel": "assets/logos/vod_cramel.jpg",
    "vod_zehu_ze": "assets/logos/vod_zehu_ze.jpg",
    "vod_rehearsals": "assets/logos/vod_rehearsals.jpg",
    "vod_valley_of_tears": "assets/logos/vod_valley_of_tears.jpg",

    # Remaining remote channels
    "formula1_hd": "assets/logos/formula1.png",
    "boomerang_hd": "assets/logos/boomerang.png",
    "pbs_kids_hd": "assets/logos/pbs_kids.png",
    "powernation_hd": "assets/logos/powernation.png",
    "disney_channel_global": "assets/logos/disney.png",
    "cartoon_network_hd": "assets/logos/cartoon_network_hd.svg",
    "crunchyroll_anime": "assets/logos/crunchyroll_anime.png",
    "motorvision_hd": "assets/logos/motorvision_hd.png",
    "cars_tv_hd": "assets/logos/cars_tv_hd.png",
    "racer_international_hd": "assets/logos/racer_international_hd.png",
    "sky_racing_hd": "assets/logos/sky_racing_hd.png",
    "ch16_immergo_il": "assets/logos/ch16_immergo_il.png",
    "vacation_channel_hd": "assets/logos/vacation_channel_il.png",
    "beinsports_xtra_fhd": "assets/logos/beinsports_xtra_fhd.png",
    "pga_tour_golf_fhd": "assets/logos/pga_tour_golf_fhd.png",
    "vevo_pop_live_fhd": "assets/logos/vevo_pop_live_fhd.png",
    "cbs_sports_hq_fhd": "assets/logos/cbs_sports_hq_fhd.png",
}

# Apply fixes to channels.json
updated_count = 0
for ch in channels:
    cid = ch.get("id")
    if cid in CHANNEL_LOGO_FIXES:
        old = ch.get("logo", "")
        new = CHANNEL_LOGO_FIXES[cid]
        ch["logo"] = new
        updated_count += 1
        print(f"Updated {cid}: {old} -> {new}")

with open("channels.json", "w", encoding="utf-8") as f:
    json.dump(channels, f, indent=2, ensure_ascii=False)
    f.write("\n")

print(f"\nApplied fixes to {updated_count} channels in channels.json!")

# Also sync channels.js and www/channels.json
channels_js_content = f"// Generated from channels.json\nwindow.ITV_CHANNELS = {json.dumps(channels, ensure_ascii=False)};\n"
with open("channels.js", "w", encoding="utf-8") as f:
    f.write(channels_js_content)
print("Updated channels.js")

if os.path.exists("www/channels.json"):
    shutil.copy2("channels.json", "www/channels.json")
    print("Updated www/channels.json")
if os.path.exists("www/channels.js"):
    shutil.copy2("channels.js", "www/channels.js")
    print("Updated www/channels.js")

