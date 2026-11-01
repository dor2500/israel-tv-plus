import re
import os

repo_dir = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"

# 1. Update style.css
style_path = os.path.join(repo_dir, "style.css")
with open(style_path, "r", encoding="utf-8") as f:
    css = f.read()

# Fix atv-pill
pill_old = """/* ---- the floating pill */
.atv-pill:empty { display: none !important; }
.atv-pill.hidden { display: none !important; }
.atv-pill { position: absolute; top: 2.75rem; left: 50%; z-index: 3; display: flex; align-items: center; height: 3.625rem;
  padding: 0 .3rem; border-radius: 999px; white-space: nowrap; background: linear-gradient(180deg, rgba(12, 14, 22, .36) 0%, rgba(12, 14, 22, .14) 100%);
  border: 1px solid rgba(255, 255, 255, .14); box-shadow: 0 1rem 3rem rgba(0, 0, 0, .35);
  -webkit-transform: translate3d(-50%, 0, 0); transform: translate3d(-50%, 0, 0); }"""

pill_new = """/* ---- the floating pill */
.atv-pill:empty,
.atv-pill.hidden,
.atv-pill[data-empty="true"],
body.player-open .atv-pill {
  display: none !important;
  visibility: hidden !important;
  opacity: 0 !important;
  pointer-events: none !important;
}
.atv-pill {
  position: absolute; top: 2.75rem; left: 50%; z-index: 3; display: flex; align-items: center; height: 3.625rem;
  min-width: 14rem;
  padding: 0 .4rem; border-radius: 999px; white-space: nowrap; background: linear-gradient(180deg, rgba(12, 14, 22, .36) 0%, rgba(12, 14, 22, .14) 100%);
  border: 1px solid rgba(255, 255, 255, .14); box-shadow: 0 1rem 3rem rgba(0, 0, 0, .35);
  -webkit-transform: translate3d(-50%, 0, 0); transform: translate3d(-50%, 0, 0);
}"""

if pill_old in css:
    css = css.replace(pill_old, pill_new)
    print("style.css: atv-pill updated successfully")
else:
    print("style.css: atv-pill old target not exact match, using regex replacement")
    css = re.sub(
        r"/\* ---- the floating pill \*/\s*\.atv-pill:empty[^\{]*\{[^\}]*\}\s*\.atv-pill\.hidden[^\{]*\{[^\}]*\}\s*\.atv-pill\s*\{[^}]*\}",
        pill_new,
        css
    )

# Append Google Drive and VOD recording styles if not present
gdrive_styles = """
/* Google Drive Cloud Recording & VOD UI */
.vod-rec-btn {
  background: rgba(239, 68, 68, 0.16);
  border: 1px solid rgba(239, 68, 68, 0.4);
  color: #fff;
  padding: 0.45rem 0.85rem;
  border-radius: 8px;
  font-size: 0.92rem;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  cursor: pointer;
  transition: background 0.2s, transform 0.2s, border-color 0.2s;
}
.vod-rec-btn:hover, .vod-rec-btn.focused {
  background: rgba(239, 68, 68, 0.38) !important;
  border-color: #ef4444 !important;
  transform: scale(1.05) !important;
}
.gdrive-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.08) 0%, rgba(255, 255, 255, 0.03) 100%);
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 16px;
  padding: 1.25rem;
  margin-bottom: 1rem;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.35);
}
.gdrive-user-row {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.gdrive-avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: 2px solid #3b82f6;
  object-fit: cover;
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.4);
}
.gdrive-badge-connected {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  background: rgba(34, 197, 94, 0.18);
  color: #4ade80;
  border: 1px solid rgba(34, 197, 94, 0.4);
  border-radius: 999px;
  padding: 0.25rem 0.7rem;
  font-size: 0.85rem;
  font-weight: 700;
}
"""

if ".vod-rec-btn" not in css:
    css += "\n" + gdrive_styles
    print("style.css: added gdrive_styles")

with open(style_path, "w", encoding="utf-8") as f:
    f.write(css)

print("style.css saved successfully!")

