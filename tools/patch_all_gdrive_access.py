import os

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"

# 1. Update ui.js
ui_path = os.path.join(ROOT, "ui.js")
with open(ui_path, "r", encoding="utf-8") as f:
    ui_code = f.read()

target_ui = "'<button class=\"top-btn nav-top tv\" id=\"btn-settings\" data-nav=\"settings\"><span class=\"ico\">' + I('settings') + '</span><span>הגדרות</span></button>' +"
repl_ui = "'<button class=\"top-btn nav-top\" id=\"btn-gdrive\" data-nav=\"gdrive\" title=\"Google Drive והקלטות ענן\"><span class=\"ico\">☁️</span><span>הקלטות ענן</span></button>' +\n      " + target_ui

if target_ui in ui_code and "btn-gdrive" not in ui_code:
    ui_code = ui_code.replace(target_ui, repl_ui)
    with open(ui_path, "w", encoding="utf-8") as f:
        f.write(ui_code)
    print("ui.js updated with btn-gdrive!")

# 2. Update app.js
app_path = os.path.join(ROOT, "app.js")
with open(app_path, "r", encoding="utf-8") as f:
    app_code = f.read()

# Add btn-gdrive to cacheEls
if "'btn-gdrive'" not in app_code:
    app_code = app_code.replace("'btn-settings'", "'btn-settings', 'btn-gdrive'")
    print("app.js: added btn-gdrive to cacheEls")

# Add click handler for btn-gdrive
if "el.btnGdrive" not in app_code:
    app_code = app_code.replace(
        "el.btnSettings.addEventListener('click', function () { openSettings(); });",
        "el.btnSettings.addEventListener('click', function () { openSettings(); });\n    if (el.btnGdrive) el.btnGdrive.addEventListener('click', function () { openDriveRecordingsDialog(); });"
    )
    print("app.js: added el.btnGdrive click listener")

# Add gdrive to ATV_TABS
if "'gdrive'" not in app_code:
    app_code = app_code.replace(
        "['פיצול מסך', 'multiview'],",
        "['פיצול מסך', 'multiview'],\n    ['הקלטות Drive', 'gdrive'],"
    )
    print("app.js: added gdrive to ATV_TABS")

# Handle gdrive in atvOk
if "t === 'gdrive'" not in app_code:
    app_code = app_code.replace(
        "else if (t === 'guide') { if (Guide) Guide.open(); }",
        "else if (t === 'guide') { if (Guide) Guide.open(); }\n      else if (t === 'gdrive') openDriveRecordingsDialog();"
    )
    print("app.js: handled t === 'gdrive' in atvOk")

# Handle gdrive in atvRenderPill filter and button rendering
old_pill_filter = "return t[1] === 'profiles' || t[1] === 'all_channels' || t[1] === 'top' || t[1] === 'vod' || t[1] === 'search' || t[1] === 'multiview' || t[1] === 'guide' || t[1] === 'settings'"
new_pill_filter = "return t[1] === 'profiles' || t[1] === 'all_channels' || t[1] === 'top' || t[1] === 'vod' || t[1] === 'search' || t[1] === 'multiview' || t[1] === 'guide' || t[1] === 'gdrive' || t[1] === 'settings'"
if old_pill_filter in app_code:
    app_code = app_code.replace(old_pill_filter, new_pill_filter)

old_pill_ico = "if (id === 'search' || id === 'multiview' || id === 'settings') {"
new_pill_ico = "if (id === 'search' || id === 'multiview' || id === 'gdrive' || id === 'settings') {\n          if (id === 'gdrive') return '<button class=\"atv-tab ico-only\" data-i=\"' + i + '\" aria-label=\"הקלטות ענן\" title=\"Google Drive והקלטות ענן\">☁️</button>';"
if old_pill_ico in app_code:
    app_code = app_code.replace(old_pill_ico, new_pill_ico)

with open(app_path, "w", encoding="utf-8") as f:
    f.write(app_code)
print("app.js updated successfully!")

# 3. Update SettingsScreen.kt in Android
settings_path = os.path.join(ROOT, "android", "app", "src", "main", "kotlin", "com", "israeltv", "plus", "ui", "screens", "SettingsScreen.kt")
with open(settings_path, "r", encoding="utf-8") as f:
    k_code = f.read()

# Add Cloud and Folder icons if needed
if "Icons.Rounded.Cloud" not in k_code:
    k_code = k_code.replace(
        "import androidx.compose.material.icons.rounded.Code",
        "import androidx.compose.material.icons.rounded.Cloud\nimport androidx.compose.material.icons.rounded.Folder\nimport androidx.compose.material.icons.rounded.Code"
    )

target_k = 'item(key = "plex") {'
repl_k = '''item(key = "gdrive") {
            SectionHeader("חשבון Google והקלטות ענן (Google Drive)")
            SettingsGroup {
                SettingRow(
                    Icons.Rounded.Cloud,
                    "חיבור ל-Google Drive",
                    "שמירת והקלטת סדרות VOD ושידורים חיים ישירות לתיקיית 'ישראל TV+ - הקלטות'",
                    onClick = {
                        val u = Uri.parse("https://accounts.google.com/o/oauth2/v2/auth?client_id=1083921845892-v7k7u83818e9f28d8b2d28j92k9s28d.apps.googleusercontent.com&response_type=token&scope=https://www.googleapis.com/auth/drive.file%20email%20profile%20openid&redirect_uri=https://dor2500.github.io/israel-tv-plus/")
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, u))
                    }
                )
                SettingRow(
                    Icons.Rounded.Folder,
                    "תיקיית ההקלטות בענן",
                    "פתיחת תיקיית 'ישראל TV+ - הקלטות' ב-Google Drive",
                    onClick = {
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/drive/my-drive")))
                    }
                )
            }
            Spacer(Modifier.height(28.dp))
        }
        item(key = "plex") {'''

if target_k in k_code and 'key = "gdrive"' not in k_code:
    k_code = k_code.replace(target_k, repl_k)
    with open(settings_path, "w", encoding="utf-8") as f:
        f.write(k_code)
    print("SettingsScreen.kt updated with Google Drive section!")

