# tools/patch_profiles_immediate.py
import re

ui_path = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\ui.js'
with open(ui_path, 'r', encoding='utf-8') as f:
    ui_text = f.read()

# Remove the 'המשך לצפייה' button from ui.js
ui_text_new = re.sub(
    r"""\s*'\s*<button class="btn net-prof-act-btn primary" id="btn-tv-prof-close">המשך לצפייה</button>'\s*\+""",
    '',
    ui_text
)

if ui_text != ui_text_new:
    with open(ui_path, 'w', encoding='utf-8') as f:
        f.write(ui_text_new)
    print("ui.js updated: removed 'המשך לצפייה' button")
else:
    print("Notice: 'המשך לצפייה' pattern not found in ui.js")

app_path = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\app.js'
with open(app_path, 'r', encoding='utf-8') as f:
    app_text = f.read()

# Replace playChannel(targetCh) and ensure instant profile switch
old_click_logic = """        } else {
          applyTvProfileData(p, true);
          saveTvProfiles();
          closeTvProfiles();
          // Immediately enter playback / TV guide
          if (TVUI) {
            var targetCh = null;
            if (p.recents && p.recents.length) {
              for (var ri = 0; ri < p.recents.length; ri++) {
                var cIdx = indexInAll(p.recents[ri]);
                if (cIdx !== -1 && (!p.isKids || isKidsCh(state.all[cIdx]))) {
                  targetCh = state.all[cIdx];
                  break;
                }
              }
            }
            if (!targetCh && state.visible && state.visible.length) targetCh = state.visible[0];
            if (!targetCh && state.all && state.all.length) {
              targetCh = p.isKids ? state.all.find(isKidsCh) : state.all[0];
            }
            if (targetCh) {
              playChannel(targetCh);
            }
          }
        }"""

new_click_logic = """        } else {
          applyTvProfileData(p, true);
          saveTvProfiles();
          closeTvProfiles();
          // Immediately enter playback / TV guide like Netflix
          if (TVUI) {
            atvBuild();
            if (atv.rows && atv.rows.length) {
              atvFocus(0, 0);
            }
            var targetCh = null;
            if (p.recents && p.recents.length) {
              for (var ri = 0; ri < p.recents.length; ri++) {
                var cIdx = indexInAll(p.recents[ri]);
                if (cIdx !== -1 && (!p.isKids || isKidsCh(state.all[cIdx]))) {
                  targetCh = state.all[cIdx];
                  break;
                }
              }
            }
            if (!targetCh && state.visible && state.visible.length) targetCh = state.visible[0];
            if (!targetCh && state.all && state.all.length) {
              targetCh = p.isKids ? state.all.find(isKidsCh) : state.all[0];
            }
            if (targetCh) {
              var pIdx = indexInList(targetCh.id);
              if (pIdx !== -1) playIndex(pIdx);
            }
          } else {
            if (state.visible && state.visible.length) {
              focusGrid(0);
              playIndex(0);
            }
          }
        }"""

if old_click_logic in app_text:
    app_text = app_text.replace(old_click_logic, new_click_logic)
    with open(app_path, 'w', encoding='utf-8') as f:
        f.write(app_text)
    print("app.js updated: fixed playChannel -> playIndex and instant ATV switch")
else:
    print("Notice: old_click_logic not matched exactly in app.js, checking regex replacement")
    # Try regex
    pattern = r"""applyTvProfileData\(p, true\);[\s\S]*?playChannel\(targetCh\);[\s\S]*?\}\s*\}"""
    m = re.search(pattern, app_text)
    if m:
        app_text = app_text[:m.start()] + new_click_logic.strip() + app_text[m.end():]
        with open(app_path, 'w', encoding='utf-8') as f:
            f.write(app_text)
        print("app.js updated via regex successfully!")
