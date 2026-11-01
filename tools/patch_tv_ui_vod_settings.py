import os, re

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"

# 1. Update app.js (openVodModal and atvRenderItem)
app_path = os.path.join(ROOT, "app.js")
with open(app_path, "r", encoding="utf-8") as f:
    app_code = f.read().replace("\r\n", "\n")

# A. Add ' is-vod' class to atv-item
target_atv_item = "it.className = 'atv-item' + (appOnly(ch) ? ' is-locked' : '');"
repl_atv_item = "it.className = 'atv-item' + (appOnly(ch) ? ' is-locked' : '') + (ch.isVod ? ' is-vod' : '');"

if target_atv_item in app_code:
    app_code = app_code.replace(target_atv_item, repl_atv_item)
    print("app.js atv-item is-vod patched!")
else:
    print("target_atv_item not found or already patched")

# B. Rewrite openVodModal to be a premier TV & Desktop cinema modal with full D-Pad navigation
old_open_vod_pattern = re.compile(r"function openVodModal\(series\) \{[\s\S]*?\n  \}", re.MULTILINE)

new_open_vod = """  function openVodModal(series) {
    if (!series) return;
    var eps = series.episodes || [];

    // Detect seasons
    var seasonsFound = [];
    var seasonsSet = {};
    eps.forEach(function (ep) {
      var s = ep.season;
      if (s && !seasonsSet[s]) {
        seasonsSet[s] = true;
        seasonsFound.push(s);
      }
    });
    seasonsFound.sort(function (a, b) { return a - b; });

    var seasonTabsHtml = '';
    if (seasonsFound.length > 1) {
      seasonTabsHtml = '<div class="vod-season-tabs">' +
        '<button class="btn dlg-focus season-tab-btn active" data-season="all">כל העונות (' + eps.length + ')</button>';
      seasonsFound.forEach(function (sNum) {
        var count = eps.filter(function (e) { return e.season == sNum; }).length;
        seasonTabsHtml += '<button class="btn dlg-focus season-tab-btn" data-season="' + sNum + '">עונה ' + sNum + ' (' + count + ')</button>';
      });
      seasonTabsHtml += '</div>';
    }

    var epHtml = eps.map(function (ep, idx) {
      var epThumb = ep.thumb || series.backdrop || series.poster || 'assets/vod/checkout.png';
      var sAttr = ep.season ? ' data-ep-season="' + ep.season + '"' : '';
      var epNumStr = ep.season ? ('עונה ' + ep.season + ' · פרק ' + (ep.episode || (idx + 1))) : ('פרק ' + (idx + 1));

      var saved = 0;
      try { saved = parseFloat(localStorage.getItem('itv_vod_pos_' + ep.id) || '0'); } catch (e) {}
      var progressHtml = saved > 30 ? '<div class="vod-ep-progress"><div class="vod-ep-bar" style="width:' + Math.min(95, Math.round((saved / 2700) * 100)) + '%"></div></div>' : '';

      return '<div class="vod-ep-card dlg-focus" data-epi="' + idx + '"' + sAttr + ' tabindex="0">' +
        '<div class="vod-ep-thumb-wrap">' +
          '<img class="vod-ep-thumb" src="' + esc(epThumb) + '" onerror="this.src=\\'assets/vod/checkout.png\\'" alt="">' +
          '<div class="vod-ep-play-overlay">▶</div>' +
          progressHtml +
        '</div>' +
        '<div class="vod-ep-info">' +
          '<div class="vod-ep-num">' + epNumStr + '</div>' +
          '<div class="vod-ep-title">' + esc(ep.title) + '</div>' +
          '<div class="vod-ep-meta">' +
            (ep.duration ? '<span class="vod-ep-dur">⏱️ ' + esc(ep.duration) + '</span>' : '') +
            (saved > 30 ? '<span class="vod-ep-resumed">נעצר ב-' + Math.floor(saved / 60) + ' דק\\'</span>' : '') +
          '</div>' +
        '</div>' +
        '<div class="vod-ep-actions">' +
          '<button class="btn vod-rec-btn" data-rec-epi="' + idx + '" title="הקלט פרק ל-Drive">💾 הקלטה</button>' +
          '<span class="vod-play-pill">צפייה ▶</span>' +
        '</div>' +
      '</div>';
    }).join('');

    var genresHtml = (series.genres || []).map(function (g) {
      return '<span class="vod-chip">' + esc(g) + '</span>';
    }).join('');

    var heroHtml = '<div class="vod-hero">' +
      '<div class="vod-poster-wrap">' +
        '<img class="vod-poster" src="' + esc(series.poster) + '" onerror="this.src=\\'assets/vod/checkout.png\\'" alt="">' +
      '</div>' +
      '<div class="vod-hero-details">' +
        '<h1 class="vod-hero-title">' + esc(series.title) + '</h1>' +
        '<div class="vod-hero-badges">' +
          '<span class="vod-chip-rating">⭐ ' + esc(series.rating || '9.0') + '</span>' +
          (series.year ? '<span class="vod-chip">' + esc(series.year) + '</span>' : '') +
          '<span class="vod-chip vod-chip-q">FHD 1080p</span>' +
          '<span class="vod-chip">' + eps.length + ' פרקים</span>' +
          genresHtml +
        '</div>' +
        '<p class="vod-hero-desc">' + esc(series.desc || '') + '</p>' +
        '<div class="vod-hero-actions">' +
          '<button class="btn dlg-focus primary vod-hero-play-btn" data-epi="0">▶ צפה עכשיו (פרק 1)</button>' +
          '<button class="btn dlg-focus vod-rec-all-btn">🔴 הקלט סדרה ל-Google Drive</button>' +
        '</div>' +
      '</div>' +
    '</div>';

    var html = heroHtml + seasonTabsHtml +
      '<div class="vod-episodes-section">' +
        '<div class="vod-episodes-heading">פרקי הסדרה (' + eps.length + ')</div>' +
        '<div class="vod-episodes">' + epHtml + '</div>' +
      '</div>';

    openDialog({
      ico: '🍿',
      title: 'VOD ישראל TV+ · ' + series.title,
      html: html,
      wide: true,
      cls: 'vod-dialog',
      buttons: [{ label: 'סגירה', primary: false }]
    });

    setTimeout(function () {
      var d = $('dialog');
      if (!d) return;

      var epCards = d.querySelectorAll('.vod-ep-card');
      for (var ci = 0; ci < epCards.length; ci++) {
        epCards[ci].addEventListener('click', function (e) {
          if (e.target.closest('.vod-rec-btn')) return;
          var epIdx = parseInt(this.getAttribute('data-epi'), 10);
          var ep = eps[epIdx];
          if (ep) {
            closeDialog();
            playVodEpisode(series, ep);
          }
        });
      }

      var heroPlayBtn = d.querySelector('.vod-hero-play-btn');
      if (heroPlayBtn) {
        heroPlayBtn.addEventListener('click', function () {
          if (eps.length) {
            closeDialog();
            playVodEpisode(series, eps[0]);
          }
        });
      }

      var tabs = d.querySelectorAll('.season-tab-btn');
      for (var ti = 0; ti < tabs.length; ti++) {
        tabs[ti].addEventListener('click', function (e) {
          e.stopPropagation();
          var selS = this.getAttribute('data-season');
          for (var tj = 0; tj < tabs.length; tj++) tabs[tj].classList.remove('active');
          this.classList.add('active');
          for (var k = 0; k < epCards.length; k++) {
            var cSeason = epCards[k].getAttribute('data-ep-season');
            if (selS === 'all' || !selS || cSeason === selS) {
              epCards[k].style.display = '';
            } else {
              epCards[k].style.display = 'none';
            }
          }
        });
      }

      var recBtns = d.querySelectorAll('.vod-rec-btn');
      for (var rk = 0; rk < recBtns.length; rk++) {
        recBtns[rk].addEventListener('click', function (e) {
          e.stopPropagation();
          var epIdx = parseInt(this.getAttribute('data-rec-epi'), 10);
          var ep = eps[epIdx];
          if (ep && window.GDrive) {
            window.GDrive.recordVodEpisode(series, ep, function (pct, txt) {
              toast('🔴 ' + txt + ' (' + series.title + ')');
            });
          }
        });
      }

      var recAllBtn = d.querySelector('.vod-rec-all-btn');
      if (recAllBtn) {
        recAllBtn.addEventListener('click', function (e) {
          e.stopPropagation();
          if (!window.GDrive) return;
          if (eps.length) {
            toast('🔴 מתחיל הקלטת סדרה ל-Google Drive (' + eps.length + ' פרקים)');
            window.GDrive.recordVodEpisode(series, eps[0], function (pct, txt) {
              toast('🔴 פרק 1: ' + txt);
            });
          }
        });
      }
    }, 50);
  }"""

# Replace openVodModal
start_idx = app_code.find("  function openVodModal(series) {")
end_idx = app_code.find("  function playVodEpisode(series, ep) {")
if start_idx != -1 and end_idx != -1:
    app_code = app_code[:start_idx] + new_open_vod + "\n\n" + app_code[end_idx:]
    print("openVodModal rewritten successfully!")
else:
    print("openVodModal boundaries not found")

with open(app_path, "w", encoding="utf-8") as f:
    f.write(app_code)
print("app.js updated successfully!")

# 2. Update style.css
css_path = os.path.join(ROOT, "style.css")
with open(css_path, "r", encoding="utf-8") as f:
    css_code = f.read().replace("\r\n", "\n")

# Replace existing VOD series modal and cards block
vod_css_target = """/* ---- VOD series modal and cards */
.vod-dialog .dialog-box { max-width: 52rem; width: 90vw; background: rgba(18, 20, 29, 0.95); backdrop-filter: blur(28px); border: 1px solid rgba(255, 255, 255, 0.15); border-radius: 1.5rem; }
.vod-ep-card { display: flex; align-items: center; justify-content: space-between; padding: 0.9rem 1.2rem; background: rgba(255, 255, 255, 0.06); border: 1px solid rgba(255, 255, 255, 0.1); border-radius: 0.8rem; cursor: pointer; transition: all 0.25s ease; }
.vod-ep-card:hover, .vod-ep-card:focus, .vod-ep-card.focused { background: rgba(255, 255, 255, 0.18); border-color: rgba(255, 255, 255, 0.4); transform: scale3d(1.015, 1.015, 1); }
.vod-ep-info b { display: block; font-size: 1.15rem; color: #fff; margin-bottom: 0.2rem; }
.vod-ep-info span { font-size: 0.9rem; color: rgba(255, 255, 255, 0.65); }
.vod-play-btn { margin-right: 0.8rem; padding: 0.45rem 1rem; font-size: 0.95rem; border-radius: 0.5rem; white-space: nowrap; }"""

vod_css_new = """/* ---- VOD series cinema modal and cards (TV & Desktop) ---- */
.vod-dialog .dialog-box {
  max-width: 108rem;
  width: 92vw;
  max-height: 88vh;
  background: rgba(13, 16, 28, 0.96);
  backdrop-filter: blur(32px);
  -webkit-backdrop-filter: blur(32px);
  border: 1.5px solid rgba(255, 255, 255, 0.14);
  border-radius: 2.2rem;
  box-shadow: 0 32px 80px rgba(0, 0, 0, 0.85);
  padding: 2.4rem 2.8rem 2rem;
  text-align: right;
  direction: rtl;
  overflow-y: auto;
}
.vod-hero {
  display: flex;
  gap: 2.2rem;
  align-items: flex-start;
  margin-bottom: 1.8rem;
  padding-bottom: 1.6rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.vod-poster-wrap {
  flex-shrink: 0;
}
.vod-poster {
  width: 165px;
  height: 240px;
  object-fit: cover;
  border-radius: 16px;
  box-shadow: 0 12px 36px rgba(0, 0, 0, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.15);
}
.vod-hero-details {
  flex: 1;
  min-width: 0;
}
.vod-hero-title {
  margin: 0 0 0.8rem;
  font-size: 2.5rem;
  font-weight: 900;
  color: #fff;
  letter-spacing: -0.02em;
}
.vod-hero-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
  align-items: center;
  margin-bottom: 1rem;
}
.vod-chip {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #e2e8f0;
  font-size: 1rem;
  font-weight: 700;
  padding: 0.25rem 0.75rem;
  border-radius: 8px;
}
.vod-chip-rating {
  background: rgba(234, 179, 8, 0.18);
  border: 1px solid rgba(234, 179, 8, 0.45);
  color: #facc15;
  font-size: 1.05rem;
  font-weight: 900;
  padding: 0.25rem 0.8rem;
  border-radius: 8px;
}
.vod-chip-q {
  background: rgba(59, 130, 246, 0.2);
  border-color: rgba(59, 130, 246, 0.5);
  color: #60a5fa;
}
.vod-hero-desc {
  font-size: 1.15rem;
  line-height: 1.6;
  color: #cbd5e1;
  margin: 0 0 1.2rem;
  max-height: 5.6rem;
  overflow-y: auto;
}
.vod-hero-actions {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}
.vod-hero-play-btn {
  background: linear-gradient(135deg, #2563eb, #1d4ed8) !important;
  color: #fff !important;
  font-size: 1.15rem !important;
  font-weight: 800 !important;
  padding: 0.65rem 1.6rem !important;
  border-radius: 12px !important;
  border: none !important;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.4);
}
.vod-hero-play-btn.focused {
  transform: scale(1.05);
  box-shadow: 0 0 0 3px #60a5fa, 0 8px 24px rgba(37, 99, 235, 0.6) !important;
}
.vod-rec-all-btn {
  background: rgba(239, 68, 68, 0.16) !important;
  border: 1px solid rgba(239, 68, 68, 0.45) !important;
  color: #fca5a5 !important;
  font-size: 1.1rem !important;
  font-weight: 700 !important;
  padding: 0.65rem 1.3rem !important;
  border-radius: 12px !important;
  cursor: pointer;
}
.vod-rec-all-btn.focused {
  background: rgba(239, 68, 68, 0.35) !important;
  border-color: #ef4444 !important;
  color: #fff !important;
  transform: scale(1.04);
}

.vod-season-tabs {
  display: flex;
  gap: 0.6rem;
  overflow-x: auto;
  padding-bottom: 0.8rem;
  margin-bottom: 1.2rem;
  scrollbar-width: none;
}
.vod-season-tabs::-webkit-scrollbar { display: none; }
.season-tab-btn {
  padding: 0.55rem 1.25rem !important;
  font-size: 1.1rem !important;
  font-weight: 700 !important;
  border-radius: 12px !important;
  background: rgba(255, 255, 255, 0.08) !important;
  color: #e2e8f0 !important;
  border: 1.5px solid rgba(255, 255, 255, 0.12) !important;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
}
.season-tab-btn.active {
  background: #2563eb !important;
  border-color: #60a5fa !important;
  color: #fff !important;
  box-shadow: 0 4px 14px rgba(37, 99, 235, 0.45);
}
.season-tab-btn.focused {
  transform: scale(1.05);
  border-color: #60a5fa !important;
  box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.5);
}

.vod-episodes-section {
  display: flex;
  flex-direction: column;
}
.vod-episodes-heading {
  font-size: 1.3rem;
  font-weight: 800;
  color: #cbd5e1;
  margin-bottom: 0.8rem;
}
.vod-episodes {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 0.9rem;
  max-height: 44vh;
  overflow-y: auto;
  padding: 0.4rem;
  scrollbar-width: thin;
}
.vod-ep-card {
  display: flex;
  align-items: center;
  gap: 1.1rem;
  padding: 0.8rem 1.1rem;
  background: rgba(255, 255, 255, 0.05);
  border: 1.5px solid rgba(255, 255, 255, 0.09);
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  overflow: hidden;
}
.vod-ep-card:hover, .vod-ep-card:focus, .vod-ep-card.focused {
  background: rgba(59, 130, 246, 0.22) !important;
  border-color: #60a5fa !important;
  transform: scale(1.025) !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.5), 0 0 0 2px rgba(96, 165, 250, 0.4) !important;
}
.vod-ep-thumb-wrap {
  width: 110px;
  height: 64px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  flex-shrink: 0;
  background: #000;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
}
.vod-ep-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.vod-ep-play-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.35);
  color: #fff;
  font-size: 1.2rem;
  opacity: 0.8;
}
.vod-ep-card.focused .vod-ep-play-overlay {
  background: rgba(37, 99, 235, 0.4);
  opacity: 1;
}
.vod-ep-progress {
  position: absolute;
  bottom: 0; left: 0; right: 0;
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
}
.vod-ep-bar {
  height: 100%;
  background: #ef4444;
}
.vod-ep-info {
  flex: 1;
  min-width: 0;
}
.vod-ep-num {
  font-size: 0.92rem;
  font-weight: 700;
  color: #60a5fa;
  margin-bottom: 0.15rem;
}
.vod-ep-title {
  font-size: 1.15rem;
  font-weight: 800;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.vod-ep-meta {
  font-size: 0.88rem;
  color: #94a3b8;
  margin-top: 0.2rem;
  display: flex;
  gap: 0.6rem;
}
.vod-ep-actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-shrink: 0;
}
.vod-play-pill {
  background: #2563eb;
  color: #fff;
  font-size: 0.95rem;
  font-weight: 700;
  padding: 0.4rem 0.9rem;
  border-radius: 8px;
}
.vod-ep-card.focused .vod-play-pill {
  background: #fff;
  color: #1e3a8a;
}

/* ATV Shelf VOD Cards */
.atv-item.is-vod .atv-card {
  overflow: hidden;
  background: #111;
}
.atv-item.is-vod .atv-logo img {
  max-width: 100%;
  max-height: 100%;
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 1.25rem;
}
.atv-item.is-vod .atv-q {
  background: #2563eb;
  color: #fff;
  font-weight: 800;
}"""

if vod_css_target in css_code:
    css_code = css_code.replace(vod_css_target, vod_css_new)
    print("VOD CSS replaced successfully!")
else:
    print("vod_css_target not found")

# Add TV Settings luxury styles
tv_settings_css = """
/* ---- TV & Desktop Settings Premium Layout & Focus Styling ---- */
body:not(.phone) #settings.overlay {
  background: rgba(0, 0, 0, 0.72);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
}
body:not(.phone) .settings-box {
  width: 92vw;
  max-width: 122rem;
  height: 86vh;
  margin: auto;
  border-radius: 2.4rem;
  background: rgba(13, 17, 28, 0.95);
  backdrop-filter: blur(32px);
  -webkit-backdrop-filter: blur(32px);
  border: 1.5px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 32px 80px rgba(0, 0, 0, 0.85);
  overflow: hidden;
}
body:not(.phone) .settings-side {
  width: 36rem;
  background: rgba(255, 255, 255, 0.02);
  border-left: 1.5px solid rgba(255, 255, 255, 0.08);
  padding: 2.8rem 1.8rem;
}
body:not(.phone) .settings-title {
  font-size: 2.6rem;
  font-weight: 900;
  color: #fff;
  padding-bottom: 1.8rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 1.4rem;
}
body:not(.phone) .nav-item {
  height: 5.4rem;
  font-size: 1.6rem;
  font-weight: 700;
  border-radius: 1.4rem;
  margin-bottom: 0.55rem;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
}
body:not(.phone) .nav-item.active {
  background: rgba(255, 255, 255, 0.09);
  color: #fff;
  font-weight: 800;
  border: 1px solid rgba(255, 255, 255, 0.12);
}
body:not(.phone) .nav-item.focused {
  background: linear-gradient(135deg, #2563eb, #1d4ed8) !important;
  color: #fff !important;
  transform: scale(1.03) !important;
  border-color: #60a5fa !important;
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.5), 0 0 0 2px rgba(96, 165, 250, 0.4) !important;
}
body:not(.phone) .settings-main {
  padding: 2.8rem 3.4rem 2rem;
}
body:not(.phone) .settings-head h2 {
  font-size: 2.6rem;
  font-weight: 900;
  color: #fff;
  letter-spacing: -0.02em;
}
body:not(.phone) .settings-head p {
  font-size: 1.35rem;
  color: #94a3b8;
  margin: 0.5rem 0 1.8rem;
  line-height: 1.4;
}
body:not(.phone) .set-row {
  min-height: 6rem;
  padding: 1rem 2rem;
  margin-bottom: 0.75rem;
  border-radius: 1.6rem;
  background: rgba(255, 255, 255, 0.05);
  border: 1.5px solid rgba(255, 255, 255, 0.08);
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
}
body:not(.phone) .set-row.focused {
  background: rgba(255, 255, 255, 0.14) !important;
  border-color: #60a5fa !important;
  transform: scale(1.02) !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.5), 0 0 0 2px rgba(96, 165, 250, 0.4) !important;
}
body:not(.phone) .set-row .sr-label {
  font-size: 1.65rem;
  font-weight: 700;
  color: #fff;
}
body:not(.phone) .set-row .sr-help {
  font-size: 1.25rem;
  color: #94a3b8;
  margin-top: 0.25rem;
}
body:not(.phone) .set-row .sr-val .v {
  background: rgba(0, 0, 0, 0.35);
  padding: 0.4rem 1.4rem;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #60a5fa;
  font-weight: 800;
  min-width: 14rem;
}
body:not(.phone) .set-row.focused .arr {
  color: #60a5fa;
  transform: scale(1.2);
}
body:not(.phone) .set-sep {
  font-size: 1.35rem;
  font-weight: 900;
  color: #60a5fa;
  letter-spacing: 0.05em;
  padding: 1.8rem 0.5rem 0.6rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 0.8rem;
}
"""

if "/* ---- TV & Desktop Settings Premium Layout & Focus Styling ---- */" not in css_code:
    css_code += "\n" + tv_settings_css
    print("TV Settings CSS appended successfully!")

with open(css_path, "w", encoding="utf-8") as f:
    f.write(css_code)
print("style.css updated successfully!")
