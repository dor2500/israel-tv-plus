"""Stremio addon generator with full backup streams, high-def metadata, and Nuvio glassmorphic install page.

usage: python -I tools/make_stremio.py
"""
import json
import os
import re
import shutil

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SITE = "https://dor2500.github.io/israel-tv-plus/"
OUT = os.path.join(ROOT, "stremio")
KIND = {
    "official": "✅ שידור רשמי ישיר",
    "fast": "✅ ערוץ FAST רשמי",
    "restream": "⚠️ ממסר צד שלישי",
    "public": "🌐 שידור פתוח (iptv-org)"
}
ISRAELI_CATS = {
    "ערוצי ישראל - שידור חי",
    "ילדים ונוער (בעברית)",
    "רדיו ישראלי (Israeli Radio)",
    "מנויים ואפליקציות (TV Apps)",
    "מסורת וקהילה"
}
CAT_ORDER = [
    "ערוצי ישראל - שידור חי",
    "ילדים ונוער (בעברית)",
    "רדיו ישראלי (Israeli Radio)",
    "מסורת וקהילה",
    "ספורט (Sports)",
    "חדשות מהעולם (News)",
    "כלכלה ועסקים (Business)",
    "סרטים וסדרות (Movies & Series)",
    "קומדיה (Comedy)",
    "ערוצי אוכל (Food Channels)",
    "ילדים מהעולם (Kids World)",
    "טבע ודוקו (Documentary & Nature)",
    "טיולים והרפתקאות (Travel)",
    "מדע ולמידה (Science)",
    "מוזיקה (Music TV)",
    "לייף סטייל ובידור",
    "דת ומסורת",
    "דת ואמונה (Religion)",
    "ערוצים בינלאומיים (World)",
    "מנויים ואפליקציות (TV Apps)"
]


def flag(cc):
    cc = {"UK": "GB"}.get(cc or "", cc or "")
    return "".join(chr(0x1F1E6 + ord(ch) - 65) for ch in cc) if re.fullmatch(r"[A-Z]{2}", cc) else ""


def country_of(c):
    if c.get("countryHe"):
        return c["countryHe"]
    if c["category"] in ISRAELI_CATS or c.get("lang") == "he":
        return "ישראל"
    return "עולם"


def short_cat(cat):
    return re.sub(r"\s*\([^)]*\)", "", cat).strip()


def dump(rel, data):
    p = os.path.join(OUT, rel)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)


def image(c):
    logo = c.get("logo") or ""
    if logo.startswith("http"):
        return logo
    if logo.startswith("/"):
        logo = logo[1:]
    return SITE + logo if logo else SITE + "icon.png"


def main():
    with open(os.path.join(ROOT, "channels.json"), encoding="utf-8") as f:
        chans = json.load(f)

    # Sort channels by their canonical number
    chans.sort(key=lambda x: (x.get("number") is None, x.get("number", 9999), x.get("name", "")))

    shutil.rmtree(os.path.join(OUT, "catalog"), ignore_errors=True)
    shutil.rmtree(os.path.join(OUT, "meta"), ignore_errors=True)
    shutil.rmtree(os.path.join(OUT, "stream"), ignore_errors=True)

    cats = [c for c in CAT_ORDER if any(ch["category"] == c for ch in chans)]
    for c in sorted({ch["category"] for ch in chans}):
        if c not in cats:
            cats.append(c)

    countries = {}
    for c in chans:
        cat = c["category"]
        countries.setdefault(cat, set()).add(country_of(c))

    manifest = {
        "id": "org.israeltvplus.addon",
        "version": "4.8.2",
        "name": "ישראל TV+ Live",
        "description": "390+ ערוצי טלוויזיה ישראליים ובינלאומיים בשידור חי, תחנות רדיו, שרתי גיבוי ומספור שלט נוח.",
        "logo": SITE + "assets/app/icon-512.png",
        "background": SITE + "splash.png",
        "types": ["tv"],
        "idPrefixes": ["itv_"],
        "resources": ["catalog", "meta", "stream"],
        "catalogs": [
            {
                "type": "tv",
                "id": "itv_" + re.sub(r"[^a-z0-9]+", "_", (c.get("categoryEn") or "cat").lower()).strip("_"),
                "name": short_cat(cat),
                "extra": [{"name": "genre", "options": sorted(countries[cat])}] if len(countries[cat]) > 1 else []
            }
            for cat in cats
            for c in chans if c["category"] == cat
        ][:len(cats)]
    }
    dump("manifest.json", manifest)

    israeli_sports = {'sport1_israel', 'sport2_israel', 'sport3_israel', 'sport4_israel', 'sport5_israel_hd', 'sport5_plus_hd', 'sport5_live_hd', 'sport5_gold_hd', 'sport5_4k_uhd', 'one_1_hd', 'one_2_hd', 'one_doco_hd'}

    for cat, cat_info in zip(cats, manifest["catalogs"]):
        metas = []
        for c in chans:
            matches_cat = (c["category"] == cat) or (cat == "ספורט (Sports)" and c["id"] in israeli_sports)
            if not matches_cat:
                continue
            src = c.get("src") or {}
            app = c.get("kind") == "app"
            desc = c.get("desc") or ""
            num_str = f"#{c.get('number')}" if c.get('number') else ""
            if app:
                desc += "\n\nשירות מנויים – נפתח באתר / באפליקציה הרשמית (צפייה עם מנוי)."
            else:
                desc += f"\n\n📡 מקור השידור: {src.get('by', '')}\n{KIND.get(src.get('kind'), '')}"
                if c.get("subsUrl"):
                    desc += "\n🛡️ קיים שרת גיבוי אוטומטי (CloudFront / Ottrast)"
            meta = {
                "id": "itv_" + c["id"],
                "type": "tv",
                "name": f"{c.get('number', '')}. {c['name']}" if c.get('number') else c['name'],
                "poster": image(c),
                "posterShape": "landscape",
                "logo": image(c),
                "background": image(c),
                "description": desc.strip(),
                "genres": [short_cat(cat), country_of(c)],
                "releaseInfo": "רדיו" if c.get("kind") == "radio" else ("אפליקציה" if app else "LIVE"),
            }
            metas.append({k: meta[k] for k in ("id", "type", "name", "poster", "posterShape", "description", "genres")})
            meta["country"] = country_of(c)
            dump(f"meta/tv/itv_{c['id']}.json", {"meta": meta})

            streams = []
            if app:
                streams.append({"name": "ישראל TV+", "title": "פתיחה באתר הרשמי של " + c["name"], "externalUrl": c.get("site") or c["url"]})
            elif c.get("auth"):
                title = f"{c['name']}\n📡 {src.get('by', '')}\n{KIND.get(src.get('kind'), '')}"
                official_site = c.get("site") or ("https://www.mako.co.il/mako-vod-live-tv/ch12" if "12" in c["id"] else "https://12plus.co.il")
                streams.append({"name": "ישראל TV+\nרשמי", "title": f"פתיחה באתר הרשמי של {c['name']}", "externalUrl": official_site})
                streams.append({"name": "ישראל TV+\n[FHD 1080p]", "title": title, "url": c["url"], "behaviorHints": {"notWebReady": True, "bingeGroup": "itv-live"}})
            else:
                title = f"{c['name']}\n📡 {src.get('by', '')}\n{KIND.get(src.get('kind'), '')}"
                http = c["url"].startswith("http:")
                streams.append({
                    "name": "ישראל TV+\n[FHD ראשי]",
                    "title": title,
                    "url": c["url"],
                    "behaviorHints": {"notWebReady": True, "bingeGroup": "itv-live"} if http or c.get("webOk") is False else {"bingeGroup": "itv-live"}
                })
                # Backup server stream
                if c.get("subsUrl"):
                    streams.append({
                        "name": "ישראל TV+\n[שרת גיבוי HD]",
                        "title": f"{c['name']} (שרת גיבוי מהיר)",
                        "url": c["subsUrl"],
                        "behaviorHints": {"bingeGroup": "itv-live"}
                    })
                if c.get("webUrl"):
                    streams.append({
                        "name": "ישראל TV+\n[HTTPS חלופי]",
                        "title": c["name"] + "\nמקור חלופי (https)",
                        "url": c["webUrl"],
                        "behaviorHints": {"bingeGroup": "itv-live"}
                    })
            dump(f"stream/tv/itv_{c['id']}.json", {"streams": streams})
        dump(f"catalog/tv/{cat_info['id']}.json", {"metas": metas})
        for country in countries[cat]:
            dump(f"catalog/tv/{cat_info['id']}/genre={country}.json", {"metas": [m for m in metas if country in m["genres"][1:]]})

    url = SITE + "stremio/manifest.json"
    page = f"""<!DOCTYPE html>
<html lang="he" dir="rtl">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>ישראל TV+ עבור Stremio | תוסף השידורים החיים הגדול בישראל</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Rubik:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
  <style>
    :root {{
      --bg: #06080F;
      --card-bg: rgba(18, 24, 38, 0.72);
      --card-border: rgba(255, 255, 255, 0.12);
      --accent-primary: #6366f1;
      --accent-secondary: #a855f7;
      --accent-gradient: linear-gradient(135deg, #6366f1 0%, #a855f7 50%, #ec4899 100%);
      --text-main: #f8fafc;
      --text-muted: #94a3b8;
    }}
    * {{ box-sizing: border-box; margin: 0; padding: 0; }}
    body {{
      font-family: 'Rubik', system-ui, -apple-system, sans-serif;
      background-color: var(--bg);
      background-image: 
        radial-gradient(circle at 50% 15%, rgba(99, 102, 241, 0.18) 0%, transparent 55%),
        radial-gradient(circle at 85% 75%, rgba(236, 72, 153, 0.12) 0%, transparent 45%),
        radial-gradient(circle at 15% 85%, rgba(168, 85, 247, 0.12) 0%, transparent 45%);
      color: var(--text-main);
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 2rem 1.2rem;
      direction: rtl;
    }}
    .glass-card {{
      max-width: 680px;
      width: 100%;
      background: var(--card-bg);
      backdrop-filter: blur(28px) saturate(180%);
      -webkit-backdrop-filter: blur(28px) saturate(180%);
      border: 1px solid var(--card-border);
      border-radius: 2.2rem;
      padding: 3.5rem 2.5rem;
      text-align: center;
      box-shadow: 0 24px 60px rgba(0, 0, 0, 0.6), inset 0 1px 1px rgba(255, 255, 255, 0.15);
      position: relative;
      overflow: hidden;
    }}
    .glass-card::before {{
      content: '';
      position: absolute;
      top: 0;
      left: 15%;
      right: 15%;
      height: 2px;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.6), transparent);
    }}
    .logo-wrap {{
      position: relative;
      width: 110px;
      height: 110px;
      margin: 0 auto 1.8rem;
    }}
    .logo-wrap img {{
      width: 100%;
      height: 100%;
      object-fit: cover;
      border-radius: 2rem;
      box-shadow: 0 12px 30px rgba(99, 102, 241, 0.4);
      border: 2px solid rgba(255, 255, 255, 0.2);
    }}
    .live-badge {{
      position: absolute;
      bottom: -6px;
      left: 50%;
      transform: translateX(-50%);
      background: #ef4444;
      color: #fff;
      font-size: 0.75rem;
      font-weight: 800;
      padding: 0.2rem 0.7rem;
      border-radius: 1rem;
      letter-spacing: 0.05em;
      box-shadow: 0 0 15px rgba(239, 68, 68, 0.7);
      display: flex;
      align-items: center;
      gap: 0.35rem;
    }}
    .live-dot {{
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: #fff;
      animation: pulse 1.4s infinite;
    }}
    @keyframes pulse {{
      0%, 100% {{ opacity: 1; transform: scale(1); }}
      50% {{ opacity: 0.4; transform: scale(0.8); }}
    }}
    h1 {{
      font-size: 2.4rem;
      font-weight: 900;
      margin-bottom: 0.8rem;
      letter-spacing: -0.02em;
      background: linear-gradient(135deg, #ffffff 30%, #cbd5e1 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }}
    .tagline {{
      font-size: 1.15rem;
      color: var(--text-muted);
      line-height: 1.6;
      margin-bottom: 2.2rem;
    }}
    .stats-row {{
      display: flex;
      justify-content: center;
      gap: 1.5rem;
      margin-bottom: 2.4rem;
    }}
    .stat-pill {{
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.08);
      padding: 0.6rem 1.2rem;
      border-radius: 1.2rem;
      font-size: 0.95rem;
      font-weight: 600;
      color: #e2e8f0;
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }}
    .stat-pill span {{
      color: #818cf8;
      font-weight: 800;
    }}
    .btn-group {{
      display: flex;
      flex-direction: column;
      gap: 1rem;
      margin-bottom: 2.2rem;
    }}
    @media (min-width: 480px) {{
      .btn-group {{ flex-direction: row; }}
    }}
    .btn {{
      flex: 1;
      padding: 1.1rem 1.6rem;
      border-radius: 1.2rem;
      font-size: 1.1rem;
      font-weight: 700;
      text-decoration: none;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 0.6rem;
      cursor: pointer;
      transition: all 0.24s cubic-bezier(0.16, 1, 0.3, 1);
      border: none;
      outline: none;
    }}
    .btn-primary {{
      background: var(--accent-gradient);
      color: #fff;
      box-shadow: 0 10px 28px rgba(99, 102, 241, 0.35);
    }}
    .btn-primary:hover {{
      transform: translateY(-2px);
      box-shadow: 0 14px 34px rgba(99, 102, 241, 0.5);
    }}
    .btn-secondary {{
      background: rgba(255, 255, 255, 0.07);
      color: #fff;
      border: 1px solid rgba(255, 255, 255, 0.12);
    }}
    .btn-secondary:hover {{
      background: rgba(255, 255, 255, 0.12);
      transform: translateY(-2px);
    }}
    .copy-box {{
      background: rgba(0, 0, 0, 0.35);
      border: 1px solid rgba(255, 255, 255, 0.08);
      border-radius: 1.2rem;
      padding: 0.8rem 1rem;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 0.8rem;
      margin-bottom: 1.5rem;
    }}
    .copy-box code {{
      direction: ltr;
      font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
      font-size: 0.9rem;
      color: #cbd5e1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }}
    .btn-copy {{
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.15);
      color: #fff;
      padding: 0.5rem 1rem;
      border-radius: 0.8rem;
      font-size: 0.85rem;
      font-weight: 600;
      cursor: pointer;
      white-space: nowrap;
      transition: all 0.2s;
    }}
    .btn-copy:hover {{
      background: #6366f1;
    }}
    .features-list {{
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
      gap: 0.8rem;
      text-align: right;
      padding-top: 1.5rem;
      border-top: 1px solid rgba(255, 255, 255, 0.08);
    }}
    .feature-item {{
      font-size: 0.85rem;
      color: var(--text-muted);
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }}
    .feature-item svg {{
      color: #10b981;
      flex-shrink: 0;
    }}
  </style>
</head>
<body>
  <main class="glass-card">
    <div class="logo-wrap">
      <img src="../assets/app/icon-512.png" alt="ישראל TV+">
      <div class="live-badge"><span class="live-dot"></span> LIVE</div>
    </div>
    <h1>ישראל TV+ ל-Stremio</h1>
    <p class="tagline">{len(chans)} ערוצי שידור חי ורדיו מסודרים בקטגוריות, תמיכה מלאה במספרי שלט, איכות FHD ושרתי גיבוי אוטומטיים.</p>

    <div class="stats-row">
      <div class="stat-pill">📺 <span>{len(chans)}</span> ערוצים</div>
      <div class="stat-pill">🗂️ <span>{len(cats)}</span> קטגוריות</div>
      <div class="stat-pill">🛡️ <span>100%</span> שרתי גיבוי</div>
    </div>

    <div class="btn-group">
      <a class="btn btn-primary" href="stremio://dor2500.github.io/israel-tv-plus/stremio/manifest.json">
        <span>🚀 התקנה באפליקציה</span>
      </a>
      <a class="btn btn-secondary" href="https://web.stremio.com/#/addons?addon={url.replace(':', '%3A').replace('/', '%2F')}" target="_blank">
        <span>🌐 התקנה ב-Web</span>
      </a>
    </div>

    <div class="copy-box">
      <code id="manifest-url">{url}</code>
      <button class="btn-copy" onclick="copyManifest()">העתק קישור</button>
    </div>

    <div class="features-list">
      <div class="feature-item">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
        מספור מותאם לשלט טלוויזיה
      </div>
      <div class="feature-item">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
        איכות FHD 1080p מקורית
      </div>
      <div class="feature-item">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
        שרתי גיבוי ישירים Ottrast/CF
      </div>
      <div class="feature-item">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
        ערוצי ספורט 4K ודוקו בלעדיים
      </div>
    </div>
  </main>

  <script>
    function copyManifest() {{
      const u = document.getElementById('manifest-url').innerText;
      navigator.clipboard.writeText(u).then(() => {{
        const b = document.querySelector('.btn-copy');
        const orig = b.innerText;
        b.innerText = 'הועתק! ✓';
        b.style.background = '#10b981';
        setTimeout(() => {{
          b.innerText = orig;
          b.style.background = '';
        }}, 2200);
      }});
    }}
  </script>
</body>
</html>
"""
    with open(os.path.join(OUT, "index.html"), "w", encoding="utf-8", newline="\n") as f:
        f.write(page)
    print(f"stremio: {len(chans)} channels in {len(cats)} catalogs generated successfully!")


if __name__ == "__main__":
    main()
