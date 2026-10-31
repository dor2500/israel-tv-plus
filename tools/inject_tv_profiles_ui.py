# Add styles to style.css
css_addition = """
/* ================================================================== TV: Netflix / Disney+ Style Profile System */
.net-prof-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  background: radial-gradient(circle at 50% 35%, #141b2d 0%, #080b12 55%, #030407 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  box-sizing: border-box;
}
.net-prof-box {
  width: 100%;
  max-width: 110rem;
  text-align: center;
  animation: net-prof-in .38s cubic-bezier(.16, 1, .3, 1) both;
}
@keyframes net-prof-in {
  from { opacity: 0; transform: scale(.94) translateY(2rem); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
.net-prof-head {
  margin-bottom: 3.5rem;
}
.net-prof-brand {
  display: inline-flex;
  align-items: center;
  gap: .8rem;
  font-size: 1.6rem;
  font-weight: 700;
  color: rgba(255, 255, 255, .75);
  margin-bottom: 1.2rem;
}
.net-prof-brand img {
  width: 2.8rem;
  height: 2.8rem;
  border-radius: .6rem;
}
.net-prof-title {
  font-size: 4rem;
  font-weight: 800;
  color: #fff;
  margin: 0 0 .8rem;
  letter-spacing: -.03em;
  text-shadow: 0 4px 24px rgba(0, 0, 0, .8);
}
.net-prof-sub {
  font-size: 1.5rem;
  color: rgba(255, 255, 255, .65);
  margin: 0;
}
.net-prof-grid {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 2.5rem;
  margin-bottom: 4rem;
}
.net-prof-card {
  position: relative;
  width: 15rem;
  padding: 1.4rem;
  border-radius: 2rem;
  background: rgba(255, 255, 255, .04);
  border: 3px solid transparent;
  cursor: pointer;
  transition: transform .28s cubic-bezier(.16, 1, .3, 1), box-shadow .28s, border-color .28s, background .28s;
  box-sizing: border-box;
  text-align: center;
}
.net-prof-card.focused, .net-prof-card:hover {
  transform: scale(1.15) translateY(-.6rem);
  border-color: #fff;
  background: rgba(255, 255, 255, .1);
  box-shadow: 0 1.2rem 3.5rem rgba(0, 0, 0, .8), 0 0 30px rgba(255, 255, 255, .35);
}
.net-prof-avatar {
  width: 11rem;
  height: 11rem;
  margin: 0 auto 1.2rem;
  display: block;
  object-fit: contain;
  border-radius: 1.8rem;
  filter: drop-shadow(0 .8rem 1.6rem rgba(0, 0, 0, .5));
  transition: transform .28s;
}
.net-prof-card.focused .net-prof-avatar {
  transform: scale(1.05);
}
.net-prof-name {
  font-size: 1.7rem;
  font-weight: 700;
  color: rgba(255, 255, 255, .9);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: .4rem;
}
.net-prof-badge {
  display: inline-block;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: #111;
  font-size: 1.1rem;
  font-weight: 800;
  padding: .2rem .8rem;
  border-radius: 1rem;
  box-shadow: 0 .2rem .8rem rgba(245, 158, 11, .4);
}
.net-prof-active-dot {
  font-size: 1.1rem;
  color: #22d37c;
  font-weight: 700;
  margin-top: .4rem;
}
.net-prof-edit-badge {
  display: block;
  font-size: 1.2rem;
  color: #38bdf8;
  font-weight: 700;
  margin-bottom: .6rem;
}
.net-prof-add {
  border: 3px dashed rgba(255, 255, 255, .25);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 17rem;
}
.net-prof-add.focused, .net-prof-add:hover {
  border-style: solid;
  border-color: #38bdf8;
  box-shadow: 0 1.2rem 3.5rem rgba(0, 0, 0, .8), 0 0 30px rgba(56, 189, 248, .4);
}
.net-prof-add-ico {
  font-size: 5rem;
  line-height: 1;
  color: rgba(255, 255, 255, .6);
  margin-bottom: .8rem;
  transition: transform .28s, color .28s;
}
.net-prof-add.focused .net-prof-add-ico {
  color: #fff;
  transform: scale(1.2);
}
.net-prof-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2rem;
}
.net-prof-act-btn {
  min-width: 14rem;
  height: 4.8rem;
  font-size: 1.45rem;
  font-weight: 700;
  border-radius: 1.2rem;
  border: 2px solid rgba(255, 255, 255, .2);
  background: rgba(255, 255, 255, .06);
  color: #fff;
  cursor: pointer;
  transition: all .25s;
}
.net-prof-act-btn.primary {
  background: #fff;
  color: #0b0e14;
  border-color: #fff;
}
.net-prof-act-btn.focused, .net-prof-act-btn:hover {
  transform: scale(1.08);
  border-color: #fff;
  box-shadow: 0 0 24px rgba(255, 255, 255, .5);
}
.net-prof-act-btn.primary.focused, .net-prof-act-btn.primary:hover {
  background: #f0f0f0;
  box-shadow: 0 0 30px rgba(255, 255, 255, .7);
}

/* TV topbar profile button */
.atv-tab-prof {
  display: inline-flex !important;
  align-items: center !important;
  gap: .7rem !important;
  padding: .3rem 1.2rem !important;
  border-radius: 2rem !important;
  background: rgba(255, 255, 255, .08) !important;
  font-weight: 700 !important;
}
.atv-tab-prof.focused {
  background: #fff !important;
  color: #0b0e14 !important;
  box-shadow: 0 0 20px rgba(255, 255, 255, .6) !important;
}
.atv-prof-ico {
  width: 2.2rem;
  height: 2.2rem;
  border-radius: 50%;
  object-fit: contain;
}
"""

with open("style.css", "r", encoding="utf-8") as f:
    css_content = f.read()

if ".net-prof-overlay" not in css_content:
    with open("style.css", "a", encoding="utf-8") as f:
        f.write("\n" + css_addition)
    print("Appended netflix profile styles to style.css")
else:
    print("Styles already present in style.css")

# Add markup to ui.js
ui_markup = """  // ---------------------------------------------------------------- TV Netflix-style profiles screen
  '<div class="overlay net-prof-overlay hidden" id="tv-profiles" role="dialog" aria-modal="true">' +
    '<div class="net-prof-box">' +
      '<div class="net-prof-head">' +
        '<div class="net-prof-brand"><img src="icon.png" alt="">ישראל <b>TV+</b></div>' +
        '<h1 class="net-prof-title">מי צופה עכשיו?</h1>' +
        '<p class="net-prof-sub">בחרו פרופיל לקבלת מועדפים, היסטוריה ותכנים מותאמים אישית</p>' +
      '</div>' +
      '<div class="net-prof-grid" id="tv-profiles-grid"></div>' +
      '<div class="net-prof-actions" id="tv-profiles-actions">' +
        '<button class="btn net-prof-act-btn" id="btn-tv-prof-manage">ניהול פרופילים</button>' +
        '<button class="btn net-prof-act-btn hidden" id="btn-tv-prof-done">סיום ניהול</button>' +
        '<button class="btn net-prof-act-btn primary" id="btn-tv-prof-close">המשך לצפייה</button>' +
      '</div>' +
    '</div>' +
  '</div>' +

"""

with open("ui.js", "r", encoding="utf-8") as f:
    ui_content = f.read()

target = "  // ---------------------------------------------------------------- dialog\n  '<div class=\"overlay hidden\" id=\"dialog\">"
if "id=\"tv-profiles\"" not in ui_content:
    if target in ui_content:
        ui_content = ui_content.replace(target, ui_markup + target)
        with open("ui.js", "w", encoding="utf-8") as f:
            f.write(ui_content)
        print("Inserted tv-profiles markup into ui.js")
    else:
        print("Target not found in ui.js!")
else:
    print("tv-profiles already in ui.js")

