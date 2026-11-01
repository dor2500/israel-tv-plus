import sys

with open("app.js", "r", encoding="utf-8") as f:
    text = f.read()

# 1. Update applyQualityLevel for lowBandwidth
old_apply_quality = """  function applyQualityLevel(hls, levels) {
    if (!hls || !levels || !levels.length) return;
    if (S.quality === 'upscale4k' || S.quality === 'max') {"""

new_apply_quality = """  function applyQualityLevel(hls, levels) {
    if (!hls || !levels || !levels.length) return;
    if (S.lowBandwidth) {
      var low = pickLevelByHeight(levels, 480);
      if (low === -1) low = 0;
      hls.currentLevel = low;
      hls.loadLevel = low;
      hls.autoLevelCapping = low;
      applyVideoPostProcessing();
      return;
    }
    if (S.quality === 'upscale4k' || S.quality === 'max') {"""

assert old_apply_quality in text, "old_apply_quality not found"
text = text.replace(old_apply_quality, new_apply_quality, 1)

# 2. Update playerMenu
old_menu_items = """    if (ch.isVod) {
      if (ch.series) {
        items.push({ ico: '📋', label: 'פרקים בסדרה (' + ch.series.title + ')', act: function () { openVodModal(ch.series); } });"""

new_menu_items = """    if (ch.isVod) {
      items.push({ ico: '⏩', label: 'דלג על פתיח (Skip Intro)', act: function () { el.video.currentTime = 85; toast('⏩ דולג על הפתיח'); } });
      if (ch.series) {
        items.push({ ico: '📋', label: 'פרקים בסדרה (' + ch.series.title + ')', act: function () { openVodModal(ch.series); } });"""

assert old_menu_items in text, "old_menu_items not found"
text = text.replace(old_menu_items, new_menu_items, 1)

old_drawer_guide = """    if (Guide) items.push({ ico: '▦', label: 'לוח שידורים', act: Guide.open });"""
new_drawer_guide = """    items.push({
      ico: '📶',
      label: 'מצב רשת איטית (5Mbps ומטה)',
      stay: true,
      val: function () { return S.lowBandwidth ? 'פועל (480p)' : 'כבוי (איכות מלאה)'; },
      act: function () {
        setSetting('lowBandwidth', !S.lowBandwidth);
        if (state.hls && state.hls.levels) applyQualityLevel(state.hls, state.hls.levels);
        toast(S.lowBandwidth ? '📶 מצב רשת איטית מופעל · מותאם לקליטה חלשה עד 5Mbps' : '📶 מצב איכות רגילה מופעל');
      }
    });
    if (!state.isRadio) {
      items.push({
        ico: '🎧',
        label: 'מצב האזנה בלבד (חיסכון בסוללה)',
        act: function () {
          toast('🎧 מצב האזנה מופעל · הקש על הנגן לחזרה לוידאו');
          var pl = $('player');
          if (pl) pl.classList.toggle('audio-only-view');
        }
      });
    }
    if (Guide) items.push({ ico: '▦', label: 'לוח שידורים', act: Guide.open });"""

assert old_drawer_guide in text, "old_drawer_guide not found"
text = text.replace(old_drawer_guide, new_drawer_guide, 1)

with open("app.js", "w", encoding="utf-8", newline="\n") as f:
    f.write(text)

print("app.js updated successfully")

# 3. Add audio-only CSS rule to style.css
with open("style.css", "r", encoding="utf-8") as f:
    css = f.read()

if "audio-only-view" not in css:
    css += """
/* Audio-Only / Battery Saver mode */
#player.audio-only-view video {
  visibility: hidden !important;
}
#player.audio-only-view::before {
  content: '🎧 מצב האזנה פעיל (חיסכון בסוללה ודאטה) · הקש בכל מקום לחזרה לוידאו';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #10b981;
  background: rgba(0, 0, 0, 0.9);
  border: 1px solid #10b981;
  border-radius: 50px;
  padding: 14px 28px;
  font-size: 1.05rem;
  font-weight: bold;
  z-index: 50;
  pointer-events: none;
}
"""
    with open("style.css", "w", encoding="utf-8", newline="\n") as f:
        f.write(css)
    print("style.css updated with audio-only styles")

# Sync to www/
import shutil
shutil.copy2("app.js", "www/app.js")
shutil.copy2("style.css", "www/style.css")
print("Synced to www/")
