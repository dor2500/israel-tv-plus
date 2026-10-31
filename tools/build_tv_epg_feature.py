# tools/build_tv_epg_feature.py
import re

# 1. Update ui.js
ui_path = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\ui.js'
with open(ui_path, 'r', encoding='utf-8') as f:
    ui_text = f.read()

tv_epg_html = """
    <!-- TV EPG & Catch-Up Bar (TV only, matching FreeTV/NextTV design) -->
    <div class="tv-epg-bar hidden" id="tv-epg-bar">
      <div class="tvepg-backdrop"></div>
      
      <!-- Bottom-Left: Clock & Logo -->
      <div class="tvepg-bottom-left">
        <div class="tvepg-brand"><img src="icon.png" alt=""></div>
        <div class="tvepg-clock" id="tvepg-clock">14:09</div>
      </div>

      <!-- Center: 3-Card Carousel + Timeline Progress + Options button -->
      <div class="tvepg-center">
        <div class="tvepg-carousel" id="tvepg-carousel">
          <button class="tvepg-nav-arrow right" id="tvepg-arrow-right" aria-label="הקודם">&#10095;</button>
          
          <!-- Previous Show (Catch-Up) -->
          <div class="tvepg-card tvepg-prev" id="tvepg-card-prev" tabindex="0">
            <div class="tvepg-card-badge">לתכנית הקודמת</div>
            <div class="tvepg-card-title" id="tvepg-prev-title">הזמר במסכה</div>
            <div class="tvepg-card-time" id="tvepg-prev-time">12:30 - 13:55</div>
            <div class="tvepg-catchup-pill">↩️ צפייה מההתחלה (Catch-Up)</div>
          </div>

          <!-- Currently Playing (Main highlighted card) -->
          <div class="tvepg-card tvepg-current focused" id="tvepg-card-cur" tabindex="0">
            <div class="tvepg-card-logo"><img id="tvepg-cur-logo" src="icon.png" alt=""></div>
            <div class="tvepg-card-badge">משודר עכשיו</div>
            <div class="tvepg-card-title" id="tvepg-cur-title">ארץ נהדרת פרק 39</div>
            <div class="tvepg-card-sub" id="tvepg-cur-sub">קשת 12 FHD</div>
          </div>

          <!-- Next Show -->
          <div class="tvepg-card tvepg-next" id="tvepg-card-next" tabindex="0">
            <div class="tvepg-card-badge">הבאה</div>
            <div class="tvepg-card-title" id="tvepg-next-title">סדר עולמי</div>
            <div class="tvepg-card-time" id="tvepg-next-time">15:00 - 17:00</div>
          </div>

          <button class="tvepg-nav-arrow left" id="tvepg-arrow-left" aria-label="הבא">&#10094;</button>
        </div>

        <!-- Timeline Progress Bar -->
        <div class="tvepg-timeline-wrap">
          <div class="tvepg-timeline-times">
            <span class="tvepg-time-start" id="tvepg-time-start">13:55</span>
            <span class="tvepg-time-day">היום</span>
            <span class="tvepg-time-end" id="tvepg-time-end">15:00</span>
          </div>
          <div class="tvepg-timeline-bar">
            <div class="tvepg-timeline-fill" id="tvepg-timeline-fill" style="width: 45%;"></div>
          </div>
        </div>

        <!-- Options Button -->
        <div class="tvepg-actions">
          <button class="tvepg-btn-options" id="tvepg-btn-options">אפשרויות</button>
        </div>
      </div>

      <!-- Right Side: Vertical Channel Strip -->
      <div class="tvepg-right-strip" id="tvepg-strip">
        <div class="tvepg-strip-item tvepg-strip-prev" id="tvepg-strip-prev">
          <span class="tvepg-strip-name" id="tvepg-prev-ch-name">רשת 13 FHD</span>
          <span class="tvepg-strip-num" id="tvepg-prev-ch-num">13</span>
        </div>
        <div class="tvepg-strip-item tvepg-strip-cur" id="tvepg-strip-cur">
          <img class="tvepg-strip-logo" id="tvepg-strip-logo" src="icon.png" alt="">
          <span class="tvepg-strip-name" id="tvepg-cur-ch-name">קשת 12 FHD</span>
          <span class="tvepg-strip-num" id="tvepg-cur-ch-num">12</span>
        </div>
        <div class="tvepg-strip-item tvepg-strip-next" id="tvepg-strip-next">
          <span class="tvepg-strip-name" id="tvepg-next-ch-name">כאן 11 FHD</span>
          <span class="tvepg-strip-num" id="tvepg-next-ch-num">11</span>
        </div>
      </div>
    </div>
"""

# Format into JS string
tv_epg_js = "  // TV EPG and Catch-up Timeline overlay\n"
for line in tv_epg_html.strip().split("\n"):
    tv_epg_js += f"  '{line}' +\n"

if "id=\"tv-epg-bar\"" not in ui_text:
    idx = ui_text.find("'<div class=\"osd\" id=\"osd\">'")
    ui_text = ui_text[:idx] + tv_epg_js + "  " + ui_text[idx:]
    with open(ui_path, 'w', encoding='utf-8') as f:
        f.write(ui_text)
    print("Added tv-epg-bar into ui.js")
else:
    print("tv-epg-bar already in ui.js")

# 2. Update style.css
css_path = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\style.css'
with open(css_path, 'r', encoding='utf-8') as f:
    css_text = f.read()

tv_epg_css = """
/* ------------------------------------------------------------------ TV EPG & Catch-Up Bar */
.tv-epg-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 36rem;
  z-index: 99;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  padding: 2.5rem 4rem 3.2rem;
  box-sizing: border-box;
  direction: rtl;
  pointer-events: auto;
  animation: tvepg-in .32s cubic-bezier(.16, 1, .3, 1) both;
}
@keyframes tvepg-in {
  from { opacity: 0; transform: translateY(3rem); }
  to { opacity: 1; transform: translateY(0); }
}
.tvepg-backdrop {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(3, 5, 10, 0.98) 0%, rgba(3, 5, 10, 0.85) 60%, transparent 100%);
  pointer-events: none;
  z-index: -1;
}
.tvepg-bottom-left {
  display: flex;
  align-items: center;
  gap: 1.4rem;
  min-width: 16rem;
  z-index: 10;
}
.tvepg-brand img {
  width: 4.2rem;
  height: 4.2rem;
  border-radius: 1.2rem;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.6);
}
.tvepg-clock {
  font-size: 2.6rem;
  font-weight: 800;
  color: rgba(255, 255, 255, 0.95);
  letter-spacing: -0.02em;
  font-variant-numeric: tabular-nums;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.8);
}
.tvepg-center {
  flex: 1;
  max-width: 82rem;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 10;
}
.tvepg-carousel {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1.8rem;
  margin-bottom: 1.4rem;
  width: 100%;
}
.tvepg-nav-arrow {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.15);
  color: #fff;
  width: 3.6rem;
  height: 3.6rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.6rem;
  cursor: pointer;
  transition: all .2s;
}
.tvepg-nav-arrow:hover, .tvepg-nav-arrow.focused {
  background: #fff;
  color: #000;
  transform: scale(1.15);
}
.tvepg-card {
  background: rgba(18, 24, 38, 0.82);
  backdrop-filter: blur(20px);
  border: 1.5px solid rgba(255, 255, 255, 0.14);
  border-radius: 1.6rem;
  padding: 1.6rem 2.2rem;
  color: #fff;
  text-align: center;
  cursor: pointer;
  box-sizing: border-box;
  transition: transform .24s cubic-bezier(0.16, 1, 0.3, 1), border-color .22s, box-shadow .22s, background .22s;
  min-width: 21rem;
  max-width: 24rem;
}
.tvepg-card.tvepg-current {
  min-width: 30rem;
  max-width: 32rem;
  background: rgba(14, 20, 32, 0.94);
  border: 2.5px solid #ffffff;
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.8), 0 0 30px rgba(255, 255, 255, 0.25);
  transform: scale(1.05);
}
.tvepg-card.focused, .tvepg-card:hover {
  border-color: #60a5fa !important;
  background: rgba(25, 35, 55, 0.96) !important;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.9), 0 0 35px rgba(96, 165, 250, 0.55) !important;
  transform: scale(1.08) translateY(-0.4rem) !important;
}
.tvepg-card-logo img {
  height: 4.2rem;
  max-width: 8rem;
  object-fit: contain;
  margin-bottom: 0.6rem;
}
.tvepg-card-badge {
  font-size: 1.15rem;
  color: rgba(255, 255, 255, 0.65);
  font-weight: 600;
  margin-bottom: 0.4rem;
}
.tvepg-card-title {
  font-size: 1.75rem;
  font-weight: 800;
  color: #fff;
  margin-bottom: 0.4rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.tvepg-card-sub {
  font-size: 1.25rem;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 600;
}
.tvepg-card-time {
  font-size: 1.25rem;
  color: rgba(255, 255, 255, 0.65);
  direction: ltr;
  display: inline-block;
  font-variant-numeric: tabular-nums;
  font-weight: 600;
}
.tvepg-catchup-pill {
  display: inline-block;
  font-size: 0.95rem;
  font-weight: 700;
  color: #38bdf8;
  background: rgba(56, 189, 248, 0.12);
  border: 1px solid rgba(56, 189, 248, 0.25);
  padding: 0.35rem 0.9rem;
  border-radius: 1rem;
  margin-top: 0.8rem;
}
.tvepg-timeline-wrap {
  width: 30rem;
  margin-bottom: 1.4rem;
}
.tvepg-timeline-times {
  display: flex;
  justify-content: space-between;
  font-size: 1.15rem;
  color: rgba(255, 255, 255, 0.75);
  margin-bottom: 0.5rem;
  font-weight: 600;
  direction: ltr;
}
.tvepg-timeline-day {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 700;
}
.tvepg-timeline-bar {
  width: 100%;
  height: 6px;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 3px;
  overflow: hidden;
  position: relative;
}
.tvepg-timeline-fill {
  height: 100%;
  background: #ef4444;
  border-radius: 3px;
  box-shadow: 0 0 10px rgba(239, 68, 68, 0.9);
  transition: width 0.3s;
}
.tvepg-btn-options {
  background: rgba(255, 255, 255, 0.12);
  border: 1.5px solid rgba(255, 255, 255, 0.25);
  color: #fff;
  font-size: 1.35rem;
  font-weight: 700;
  padding: 0.65rem 3.2rem;
  border-radius: 2rem;
  cursor: pointer;
  transition: all .2s;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.4);
}
.tvepg-btn-options:hover, .tvepg-btn-options.focused {
  background: #ffffff;
  color: #000000;
  border-color: #ffffff;
  transform: scale(1.08);
  box-shadow: 0 0 25px rgba(255, 255, 255, 0.7);
}
.tvepg-right-strip {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
  align-items: flex-end;
  min-width: 18rem;
  z-index: 10;
}
.tvepg-strip-item {
  display: flex;
  align-items: center;
  gap: 1.1rem;
  font-size: 1.45rem;
  color: rgba(255, 255, 255, 0.45);
  font-weight: 700;
  transition: all .2s;
  cursor: pointer;
}
.tvepg-strip-item.tvepg-strip-cur {
  font-size: 1.95rem;
  color: #ffffff;
  font-weight: 900;
  transform: scale(1.08);
  text-shadow: 0 2px 14px rgba(0, 0, 0, 0.9);
}
.tvepg-strip-logo {
  width: 3.4rem;
  height: 3.4rem;
  object-fit: contain;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.5));
}
.tvepg-strip-num {
  background: rgba(255, 255, 255, 0.15);
  padding: 0.25rem 0.7rem;
  border-radius: 0.7rem;
  color: #fff;
  font-size: 1.35rem;
  min-width: 2.4rem;
  text-align: center;
}
.tvepg-strip-cur .tvepg-strip-num {
  background: #ef4444;
  box-shadow: 0 0 12px rgba(239, 68, 68, 0.6);
}
"""

if ".tv-epg-bar" not in css_text:
    css_text += "\n" + tv_epg_css
    with open(css_path, 'w', encoding='utf-8') as f:
        f.write(css_text)
    print("Added tv-epg-bar styling into style.css")
else:
    print("tv-epg-bar styling already in style.css")
