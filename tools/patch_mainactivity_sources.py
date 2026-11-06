import os

p = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\platforms\android\src\com\israeltv\plus\MainActivity.java"
with open(p, "r", encoding="utf-8") as f:
    content = f.read()

target = """            if (Build.VERSION.SDK_INT >= 26 && !getPackageManager().canRequestPackageInstalls()) {
                report("error", "יש לאשר התקנת עדכונים מהאפליקציה (נפתח מסך ההגדרות) ואז לנסות שוב");
                runOnUiThread(() -> start(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()))));
                return;
            }"""

replacement = """            if (Build.VERSION.SDK_INT >= 26 && !getPackageManager().canRequestPackageInstalls()) {
                report("error", "יש לאשר התקנת אפליקציות ממקור לא מוכר (נפתח מסך ההגדרות) או להוריד ישירות בדפדפן");
                runOnUiThread(() -> {
                    try {
                        start(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName())));
                    } catch (Exception e1) {
                        try {
                            start(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES));
                        } catch (Exception e2) {
                            try {
                                start(new Intent(Settings.ACTION_SECURITY_SETTINGS));
                            } catch (Exception ignored) {}
                        }
                    }
                });
                return;
            }"""

if target in content:
    content = content.replace(target, replacement)
    with open(p, "w", encoding="utf-8") as f:
        f.write(content)
    print("Successfully patched MainActivity.java!")
else:
    print("Target not found, trying normalized newlines...")
    content_norm = content.replace("\r\n", "\n")
    target_norm = target.replace("\r\n", "\n")
    replacement_norm = replacement.replace("\r\n", "\n")
    if target_norm in content_norm:
        content_norm = content_norm.replace(target_norm, replacement_norm)
        with open(p, "w", encoding="utf-8") as f:
            f.write(content_norm.replace("\n", "\r\n"))
        print("Successfully patched MainActivity.java with CRLF!")
    else:
        print("Failed to find target in MainActivity.java")

