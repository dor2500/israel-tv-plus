import re

# 1. Patch ui.js
with open('ui.js', 'r', encoding='utf-8') as f:
    ui_text = f.read().replace('\r\n', '\n')

old_perror = """    '<div class="perror hidden" id="perror"><div class="perror-box">' +
      '<div class="perror-ico">' + I('info') + '</div><h3 id="perror-title"></h3><p id="perror-desc"></p>' +
      '<div class="perror-actions"><button class="btn primary" id="perror-retry">נסה שוב</button>' +
      '<button class="btn" id="perror-next">ערוץ הבא</button><button class="btn" id="perror-back">חזרה</button></div>' +
    '</div></div>' +"""

new_perror = """    '<div class="perror hidden" id="perror"><div class="perror-box">' +
      '<div class="perror-standby-graphic">' +
        '<div class="perror-colorbars"><span></span><span></span><span></span><span></span><span></span><span></span><span></span></div>' +
        '<div class="perror-logo-wrap"><img id="perror-ch-logo" class="perror-ch-logo" src="icon.png" alt=""></div>' +
      '</div>' +
      '<h3 class="perror-title" id="perror-title">סליחה, תקלה זמנית בשידור</h3>' +
      '<p class="perror-desc" id="perror-desc">השידור בערוץ זה אינו זמין כעת. צוות הערוץ פועל להחזרת השידור בהקדם. אנו מתנצלים על אי הנוחות!</p>' +
      '<div class="perror-actions">' +
        '<button class="btn primary" id="perror-retry">נסה להתחבר שוב</button>' +
        '<button class="btn" id="perror-next">עבור לערוץ הבא (OK)</button>' +
        '<button class="btn" id="perror-back">חזרה למדריך הערוצים</button>' +
      '</div>' +
    '</div></div>' +"""

if old_perror in ui_text:
    ui_text = ui_text.replace(old_perror, new_perror, 1)
    with open('ui.js', 'w', encoding='utf-8', newline='\n') as f:
        f.write(ui_text)
    print("Patched ui.js successfully!")
else:
    print("Could not find old_perror in ui.js!")

# 2. Patch style.css to style the broadcast standby apology card
with open('style.css', 'r', encoding='utf-8') as f:
    css_text = f.read().replace('\r\n', '\n')

perror_css_patch = """
/* Broadcast Technical Apology Screen (שקופית תקלה וסליחה) */
.perror { position: absolute; top: 0; left: 0; right: 0; bottom: 0; z-index: 6; display: flex; align-items: center; justify-content: center; background: radial-gradient(circle at center, rgba(20,24,35,0.96) 0%, rgba(5,7,12,0.98) 100%); backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px); }
.perror-box { width: 72rem; max-width: 92%; text-align: center; padding: 3.5rem 3rem; border-radius: 2.8rem; background: linear-gradient(145deg, rgba(30,35,50,0.95), rgba(15,18,28,0.98)); border: 1px solid rgba(255,255,255,0.12); box-shadow: 0 25px 60px rgba(0,0,0,0.7), 0 0 40px rgba(239,68,68,0.15); animation: errorFadeIn .3s ease-out; }
@keyframes errorFadeIn { from { opacity: 0; transform: scale(.95); } to { opacity: 1; transform: scale(1); } }
.perror-standby-graphic { position: relative; width: 14rem; height: 9rem; margin: 0 auto 1.8rem; border-radius: 1.6rem; overflow: hidden; border: 2px solid rgba(255,255,255,0.2); box-shadow: 0 10px 30px rgba(0,0,0,0.5); background: #111; display: flex; flex-direction: column; }
.perror-colorbars { display: flex; height: 35%; width: 100%; }
.perror-colorbars span { flex: 1; }
.perror-colorbars span:nth-child(1) { background: #c0c0c0; }
.perror-colorbars span:nth-child(2) { background: #c0c000; }
.perror-colorbars span:nth-child(3) { background: #00c0c0; }
.perror-colorbars span:nth-child(4) { background: #00c000; }
.perror-colorbars span:nth-child(5) { background: #c000c0; }
.perror-colorbars span:nth-child(6) { background: #c00000; }
.perror-colorbars span:nth-child(7) { background: #0000c0; }
.perror-logo-wrap { flex: 1; display: flex; align-items: center; justify-content: center; background: rgba(0,0,0,0.75); padding: .5rem; }
.perror-ch-logo { max-height: 4.5rem; max-width: 8rem; object-fit: contain; filter: drop-shadow(0 2px 8px rgba(0,0,0,0.8)); }
.perror-box h3 { font-size: 2.8rem; font-weight: 800; color: #fff; margin: .8rem 0; letter-spacing: -0.5px; text-shadow: 0 2px 10px rgba(0,0,0,0.5); }
.perror-box p { font-size: 1.65rem; line-height: 1.6; color: rgba(255,255,255,0.78); margin: 0 auto 2.5rem; max-width: 58rem; }
.perror-actions { display: flex; flex-wrap: wrap; gap: 1.2rem; justify-content: center; }
.perror-actions .btn { min-height: 5rem; padding: 0 2.8rem; font-size: 1.6rem; border-radius: 2.5rem; transition: transform .2s ease, box-shadow .2s ease; }
.perror-actions .btn:focus, .perror-actions .btn.focused { transform: scale(1.06); box-shadow: 0 0 25px rgba(255,255,255,0.4); outline: 3px solid #fff; }
"""

if '.perror-standby-graphic' not in css_text:
    css_text += perror_css_patch
    with open('style.css', 'w', encoding='utf-8', newline='\n') as f:
        f.write(css_text)
    print("Patched style.css successfully!")
