import os

p = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\src\main\kotlin\com\israeltv\plus\ui\screens\SettingsScreen.kt"
with open(p, "r", encoding="utf-8") as f:
    c = f.read()

c = c.replace("\r\n", "\n")

if "import androidx.compose.material.icons.rounded.Refresh" not in c:
    c = c.replace(
        "import androidx.compose.material.icons.rounded.Folder",
        "import androidx.compose.material.icons.rounded.Folder\nimport androidx.compose.material.icons.rounded.Refresh"
    )

old_gdrive = """        item(key = "gdrive") {
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
        }"""

new_gdrive = """        item(key = "gdrive") {
            SectionHeader("חשבון Google וסנכרון ענן (Google Drive)")
            SettingsGroup {
                SettingRow(
                    Icons.Rounded.Cloud,
                    "התחבר עם חשבון Google",
                    "סנכרון מועדפים, היסטוריה, פרופילים והגדרות בין כל המכשירים, והקלטה ישירה לענן",
                    onClick = {
                        val u = Uri.parse("https://accounts.google.com/o/oauth2/v2/auth?client_id=1083921845892-v7k7u83818e9f28d8b2d28j92k9s28d.apps.googleusercontent.com&response_type=token&scope=https://www.googleapis.com/auth/drive.file%20email%20profile%20openid&redirect_uri=https://dor2500.github.io/israel-tv-plus/")
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, u))
                    }
                )
                SettingRow(
                    Icons.Rounded.Refresh,
                    "סנכרן נתונים והגדרות עכשיו",
                    "סנכרון מיידי של ערוצים מועדפים, פרופילים והגדרות מול Google Drive",
                    onClick = {
                        val u = Uri.parse("https://dor2500.github.io/israel-tv-plus/#sync")
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, u))
                    }
                )
                SettingRow(
                    Icons.Rounded.Folder,
                    "תיקיית ההקלטות והגיבוי בענן",
                    "פתיחת תיקיית 'ישראל TV+ - הקלטות' וקובץ הגיבוי ב-Google Drive",
                    onClick = {
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/drive/my-drive")))
                    }
                )
            }
            Spacer(Modifier.height(28.dp))
        }"""

if old_gdrive in c:
    c = c.replace(old_gdrive, new_gdrive)
    with open(p, "w", encoding="utf-8") as f:
        f.write(c)
    print("SettingsScreen.kt updated successfully!")
else:
    print("old_gdrive block not found")
