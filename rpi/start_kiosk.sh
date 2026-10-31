#!/bin/bash
# Israel TV+ Kiosk Startup Script
# Configured for Raspberry Pi 2, 3, 4, 5 with hardware acceleration and TV remote control

xset s off
xset -dpms
xset s noblank

# Start CEC remote daemon in background
python3 /home/pi/israel-tv-plus/rpi/cec_remote.py &

# Detect Pi model and set optimal flags
PI_MODEL=$(tr -d '\0' < /proc/device-tree/model 2>/dev/null || echo "Raspberry Pi")
echo "Starting Israel TV+ on $PI_MODEL..."

CHROMIUM_FLAGS=(
    --kiosk
    --noerrdialogs
    --disable-infobars
    --disable-session-crashed-bubble
    --disable-translate
    --check-for-update-interval=31536000
    --autoplay-policy=no-user-gesture-required
    --enable-features=OverlayScrollbar,VaapiVideoDecoder
    --enable-gpu-rasterization
    --enable-zero-copy
    --ignore-gpu-blocklist
    --enable-accelerated-video-decode
    --disable-pinch
    --overscroll-history-navigation=0
    --hide-scrollbars
    --window-size=1920,1080
    --window-position=0,0
)

# Launch Israel TV+ Fullscreen Appliance
chromium-browser "${CHROMIUM_FLAGS[@]}" "https://dor2500.github.io/israel-tv-plus/"

