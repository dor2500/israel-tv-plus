# -*- coding: utf-8 -*-
import sys, os

css_path = 'style.css'
with open(css_path, 'r', encoding='utf-8') as f:
    css = f.read()

# Replace existing video.sharpen with hardware accelerated upscale CSS
old_sharpen = 'video.sharpen { filter: url(#retro-sharpen-filter) !important; }'
new_upscale_css = """/* ------------------------------------------------------------------ Hardware-Accelerated Video Upscale & Enhancements */
video.upscale-ai_4k, .video-4k-upscale {
  image-rendering: -webkit-optimize-contrast !important;
  image-rendering: crisp-edges !important;
  -webkit-transform: translate3d(0, 0, 0) !important;
  transform: translate3d(0, 0, 0) !important;
  backface-visibility: hidden !important;
  -webkit-backface-visibility: hidden !important;
  filter: contrast(1.09) saturate(1.07) brightness(1.01) !important;
  will-change: transform, filter !important;
}
video.upscale-crystal_hd, video.sharpen {
  image-rendering: -webkit-optimize-contrast !important;
  image-rendering: crisp-edges !important;
  -webkit-transform: translate3d(0, 0, 0) !important;
  transform: translate3d(0, 0, 0) !important;
  filter: contrast(1.14) saturate(1.10) brightness(1.03) !important;
  will-change: transform, filter !important;
}
video.upscale-cinema_hdr {
  -webkit-transform: translate3d(0, 0, 0) !important;
  transform: translate3d(0, 0, 0) !important;
  filter: contrast(1.18) saturate(1.16) brightness(0.97) !important;
}
video.upscale-vivid_oled {
  -webkit-transform: translate3d(0, 0, 0) !important;
  transform: translate3d(0, 0, 0) !important;
  filter: contrast(1.12) saturate(1.30) brightness(1.02) !important;
}
video.upscale-sports_pro {
  image-rendering: -webkit-optimize-contrast !important;
  image-rendering: crisp-edges !important;
  -webkit-transform: translate3d(0, 0, 0) !important;
  transform: translate3d(0, 0, 0) !important;
  filter: contrast(1.12) saturate(1.18) brightness(1.04) !important;
}
video.upscale-smooth_denoise {
  -webkit-transform: translate3d(0, 0, 0) !important;
  transform: translate3d(0, 0, 0) !important;
  filter: contrast(1.04) saturate(1.03) brightness(1.01) !important;
}
video.upscale-retro_crt {
  -webkit-transform: translate3d(0, 0, 0) !important;
  transform: translate3d(0, 0, 0) !important;
  filter: sepia(0.20) contrast(1.10) saturate(1.12) !important;
}"""

if old_sharpen in css:
    css = css.replace(old_sharpen, new_upscale_css)
elif 'video.upscale-ai_4k' not in css:
    css += '\n' + new_upscale_css

# Append Rich Program Info Card styles and TV Styles
additional_css = """
/* ------------------------------------------------------------------ Rich EPG Program Info Card */
.info-prog-card {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
  background: var(--surface2, rgba(255, 255, 255, 0.06));
  border: 1px solid var(--line, rgba(255, 255, 255, 0.12));
  border-radius: 1.8rem;
  padding: 1.6rem;
  margin-bottom: 1.4rem;
  text-align: right;
  direction: rtl;
}
.info-prog-header {
  display: flex;
  align-items: center;
  gap: 1.4rem;
}
.info-prog-poster {
  width: 10rem;
  height: 6.6rem;
  border-radius: 1.2rem;
  object-fit: cover;
  flex-shrink: 0;
  background: rgba(0, 0, 0, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.1);
}
.info-prog-meta {
  flex: 1;
  min-width: 0;
}
.info-prog-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.15rem;
  font-weight: 700;
  color: #ff5252;
  background: rgba(255, 82, 82, 0.14);
  padding: 0.2rem 0.8rem;
  border-radius: 2rem;
  margin-bottom: 0.4rem;
}
.info-prog-badge.vod {
  color: #40c4ff;
  background: rgba(64, 196, 255, 0.14);
}
.info-prog-title {
  font-size: 1.85rem;
  font-weight: 800;
  color: var(--text, #fff);
  line-height: 1.25;
  margin-bottom: 0.4rem;
}
.info-prog-time {
  font-size: 1.3rem;
  color: var(--text2, rgba(255, 255, 255, 0.75));
  font-weight: 600;
}
.info-prog-progress {
  width: 100%;
  height: 0.55rem;
  background: rgba(255, 255, 255, 0.14);
  border-radius: 9999px;
  overflow: hidden;
  margin: 0.5rem 0;
}
.info-prog-progress-fill {
  height: 100%;
  background: var(--accent, #6366f1);
  border-radius: 9999px;
  transition: width 0.3s ease;
}
.info-prog-desc {
  font-size: 1.45rem;
  line-height: 1.6;
  color: var(--text, #f0f0f0);
  background: rgba(0, 0, 0, 0.25);
  padding: 1.2rem 1.4rem;
  border-radius: 1.2rem;
  white-space: pre-wrap;
  word-break: break-word;
}
.info-prog-next {
  font-size: 1.25rem;
  color: var(--text2, rgba(255, 255, 255, 0.75));
  background: rgba(255, 255, 255, 0.05);
  padding: 0.8rem 1.2rem;
  border-radius: 1rem;
}
.info-tech-details {
  margin-top: 1.2rem;
  border-top: 1px solid var(--line, rgba(255, 255, 255, 0.1));
  padding-top: 1rem;
}
.info-tech-details summary {
  cursor: pointer;
  padding: 0.8rem 0;
  color: var(--text2, rgba(255, 255, 255, 0.7));
  font-size: 1.35rem;
  user-select: none;
  font-weight: 700;
}
.info-tech-details summary:hover {
  color: var(--text, #fff);
}

/* ------------------------------------------------------------------ TV Interface Design Styles */
/* 1. Apple TV Style (Default Liquid Glass) */
body.tvstyle-appletv .m3-settings-container,
body.tvstyle-appletv .dialog-box,
body.tvstyle-appletv .m3-nav-item.focused,
body.tvstyle-appletv .m3-card.focused {
  backdrop-filter: blur(28px) saturate(180%);
  -webkit-backdrop-filter: blur(28px) saturate(180%);
}
body.tvstyle-appletv .m3-card.focused {
  border-radius: 2.2rem;
  box-shadow: 0 16px 36px rgba(0,0,0,0.45), 0 0 0 2px rgba(255,255,255,0.4);
}

/* 2. Google TV / Android TV (Material 3 Expressive) */
body.tvstyle-googletv {
  background-color: #0f1013 !important;
}
body.tvstyle-googletv .m3-settings-container {
  background: #18191e !important;
  border-radius: 2.8rem !important;
  border: 1px solid rgba(255,255,255,0.08) !important;
  box-shadow: 0 20px 48px rgba(0,0,0,0.6) !important;
}
body.tvstyle-googletv .m3-nav-item {
  border-radius: 9999px !important;
  transition: all 0.2s cubic-bezier(0.2, 0, 0, 1) !important;
}
body.tvstyle-googletv .m3-nav-item.focused {
  background: var(--accent, #a8c7fa) !important;
  color: #040e25 !important;
  font-weight: 800 !important;
  transform: scale(1.02) !important;
  box-shadow: 0 4px 16px rgba(168,199,250,0.3) !important;
}
body.tvstyle-googletv .m3-nav-item.focused .m3-nav-name,
body.tvstyle-googletv .m3-nav-item.focused .m3-nav-desc,
body.tvstyle-googletv .m3-nav-item.focused .m3-nav-ico svg {
  color: #040e25 !important;
  fill: #040e25 !important;
}
body.tvstyle-googletv .m3-card.focused {
  border-radius: 2.4rem !important;
  background: rgba(255,255,255,0.12) !important;
  outline: 3px solid var(--accent, #a8c7fa) !important;
  outline-offset: 3px !important;
  box-shadow: 0 12px 32px rgba(0,0,0,0.5) !important;
}
body.tvstyle-googletv .channel-card.focused,
body.tvstyle-googletv .vod-card.focused {
  border-radius: 2.4rem !important;
  outline: 3.5px solid var(--accent, #a8c7fa) !important;
  outline-offset: 4px !important;
  transform: scale(1.06) !important;
}

/* 3. Samsung One UI (Minimalist Flat, Deep AMOLED & Sharp Squircles) */
body.tvstyle-oneui {
  background-color: #000000 !important;
}
body.tvstyle-oneui .m3-settings-container {
  background: #08080a !important;
  border-radius: 1.6rem !important;
  border: 1.5px solid #222228 !important;
  box-shadow: none !important;
}
body.tvstyle-oneui .m3-nav-item {
  border-radius: 1.2rem !important;
  border: 1px solid transparent !important;
}
body.tvstyle-oneui .m3-nav-item.focused {
  background: #15151c !important;
  border: 2px solid var(--accent, #3876f6) !important;
  box-shadow: none !important;
  transform: none !important;
}
body.tvstyle-oneui .m3-card {
  border-radius: 1.4rem !important;
  border: 1px solid rgba(255,255,255,0.06) !important;
}
body.tvstyle-oneui .m3-card.focused {
  background: #181822 !important;
  border: 2.5px solid var(--accent, #3876f6) !important;
  box-shadow: 0 0 18px rgba(56,118,246,0.35) !important;
  transform: none !important;
}
body.tvstyle-oneui .channel-card,
body.tvstyle-oneui .vod-card {
  border-radius: 1.4rem !important;
}
body.tvstyle-oneui .channel-card.focused,
body.tvstyle-oneui .vod-card.focused {
  border: 3px solid var(--accent, #3876f6) !important;
  border-radius: 1.4rem !important;
  transform: scale(1.04) !important;
  box-shadow: 0 8px 24px rgba(0,0,0,0.8) !important;
}
body.tvstyle-oneui .dialog-box {
  border-radius: 1.8rem !important;
  border: 1.5px solid #2a2a34 !important;
  background: #0d0d12 !important;
}

/* 4. LG webOS (Vibrant Gradients, Floating Ribbons & Color Depth) */
body.tvstyle-webos {
  background: radial-gradient(circle at 80% 20%, #151828, #07080f 80%) !important;
}
body.tvstyle-webos .m3-settings-container {
  background: linear-gradient(145deg, rgba(28,32,52,0.92), rgba(13,15,26,0.95)) !important;
  border-radius: 2.2rem !important;
  border: 1.5px solid rgba(130,150,255,0.18) !important;
  box-shadow: 0 24px 60px rgba(0,0,0,0.7) !important;
}
body.tvstyle-webos .m3-nav-item.focused {
  background: linear-gradient(90deg, var(--accent, #ec4899), #8b5cf6) !important;
  color: #ffffff !important;
  border-radius: 1.6rem !important;
  box-shadow: 0 8px 25px rgba(236,72,153,0.35) !important;
  transform: scale(1.03) !important;
}
body.tvstyle-webos .m3-card.focused {
  border-radius: 1.8rem !important;
  background: rgba(255,255,255,0.1) !important;
  border: 2.5px solid #ec4899 !important;
  box-shadow: 0 10px 30px rgba(236,72,153,0.3) !important;
}
body.tvstyle-webos .channel-card.focused,
body.tvstyle-webos .vod-card.focused {
  border: 3px solid #ec4899 !important;
  border-radius: 1.8rem !important;
  transform: scale(1.07) translateY(-4px) !important;
  box-shadow: 0 18px 36px rgba(236,72,153,0.25) !important;
}

/* 5. Cinematic Theater (Dramatic Dark Vignette & High Impact Focus) */
body.tvstyle-cinema {
  background: radial-gradient(circle at 50% 50%, #1a080a 0%, #050102 100%) !important;
}
body.tvstyle-cinema .m3-settings-container {
  background: rgba(14, 4, 6, 0.94) !important;
  border-radius: 1.8rem !important;
  border: 1px solid rgba(229, 9, 20, 0.25) !important;
  box-shadow: 0 30px 80px rgba(0,0,0,0.9), 0 0 30px rgba(229, 9, 20, 0.15) !important;
}
body.tvstyle-cinema .m3-nav-item.focused {
  background: #e50914 !important;
  color: #ffffff !important;
  border-radius: 1.2rem !important;
  box-shadow: 0 8px 28px rgba(229, 9, 20, 0.45) !important;
  transform: scale(1.04) !important;
}
body.tvstyle-cinema .m3-card.focused {
  border-radius: 1.4rem !important;
  border: 2.5px solid #e50914 !important;
  box-shadow: 0 12px 35px rgba(229, 9, 20, 0.4) !important;
  transform: scale(1.03) !important;
}
body.tvstyle-cinema .channel-card.focused,
body.tvstyle-cinema .vod-card.focused {
  border: 3.5px solid #e50914 !important;
  border-radius: 1.4rem !important;
  transform: scale(1.09) !important;
  box-shadow: 0 24px 50px rgba(0,0,0,0.9), 0 0 25px rgba(229, 9, 20, 0.5) !important;
  z-index: 10 !important;
}
"""

if 'info-prog-card' not in css:
    css += '\n' + additional_css

with open(css_path, 'w', encoding='utf-8') as f:
    f.write(css)

print('style.css patched successfully!')
