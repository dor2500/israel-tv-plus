import os

app_js = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\app.js'
with open(app_js, 'r', encoding='utf-8') as f:
    c = f.read()

# 1. nativeKey
old_native_key = '''  function nativeKey() {
    if (PLATFORM === 'android') return 'android';
    if (PLATFORM === 'windows') return 'windows';
    if (PLATFORM === 'tizen') return 'tizen';'''

new_native_key = '''  function nativeKey() {
    if (PLATFORM === 'android') return 'android';
    if (PLATFORM === 'windows') return 'windows';
    if (PLATFORM === 'webos') return 'webos';
    if (PLATFORM === 'tizen') return 'tizen';'''

assert old_native_key in c, 'old_native_key not found'
c = c.replace(old_native_key, new_native_key, 1)

# 2. settingsSections about row
old_about_row = '''        action('📲 האפליקציה לשאר המכשירים', function () { showGetApp(); }),'''
new_about_row = '''        action('📡 חיבור ל-Plex ול-DLNA (טלוויזיות LG)', function () { showPlexModal(); }, 'הגדרת 269 הערוצים ולוח שידורים ב-Plex Live TV & DVR'),
        action('📲 האפליקציה לשאר המכשירים', function () { showGetApp(); }),'''

assert old_about_row in c, 'old_about_row not found'
c = c.replace(old_about_row, new_about_row, 1)

# 3. showPlexModal implementation
plex_modal_code = '''
  function showPlexModal() {
    var m3u = 'https://dor2500.github.io/israel-tv-plus/plex.m3u';
    var xmltv = 'https://dor2500.github.io/israel-tv-plus/epg.xml';
    var html = '<div style="direction:rtl;text-align:right;font-size:15px;line-height:1.6;color:var(--t-sec);">' +
      '<p><b style="color:var(--t-pri);font-size:16px;">צפייה בכל 269 הערוצים דרך אפליקציית Plex בטלוויזיית LG, סמסונג או סטרימר:</b></p>' +
      '<p>1. פתח את הגדרות שרת ה-Plex שלך במחשב / NAS ← עבור ל-<b>Live TV & DVR</b>.</p>' +
      '<p>2. לחץ על <b>Set up a Plex Tuner</b> ובחר <b>M3U Tuner</b>.</p>' +
      '<div style="background:rgba(255,255,255,0.07);padding:10px 14px;border-radius:10px;margin:8px 0;word-break:break-all;user-select:all;font-family:monospace;font-size:13px;color:var(--t-pri);">' +
      esc(m3u) + '</div>' +
      '<p>3. בלשונית מדריך השידורים (XMLTV Guide), הדבק את הכתובת:</p>' +
      '<div style="background:rgba(255,255,255,0.07);padding:10px 14px;border-radius:10px;margin:8px 0;word-break:break-all;user-select:all;font-family:monospace;font-size:13px;color:var(--t-pri);">' +
      esc(xmltv) + '</div>' +
      '<p>4. פתח את אפליקציית Plex בטלוויזיית ה-LG שלך (מחנות LG Content Store) – כל הערוצים, לוח השידורים והפוסטרים יופיעו אוטומטית!</p>' +
      '</div>';
    openDialog({
      ico: '📡',
      title: 'חיבור ל-Plex ול-DLNA',
      html: html,
      buttons: [
        { label: 'הבנתי, תודה', primary: true }
      ]
    });
  }
'''

# Insert showPlexModal right before showGetApp
get_app_idx = c.find('  function showGetApp(ch)')
assert get_app_idx != -1, 'showGetApp(ch) not found'
c = c[:get_app_idx] + plex_modal_code + '\n' + c[get_app_idx:]

with open(app_js, 'w', encoding='utf-8') as f:
    f.write(c)

print('Successfully patched app.js')
