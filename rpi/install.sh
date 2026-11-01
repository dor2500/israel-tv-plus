#!/bin/bash
set -e

# ==============================================================================
# Israel TV+ Appliance OS Installer
# Turns any Raspberry Pi (Pi 2, 3, 4, 5, Zero 2W) into a dedicated TV Set-Top Box
# ==============================================================================

echo "=========================================================="
echo "🇮🇱 מתקין את מערכת ההפעלה הייעודית של Israel TV+ לרספברי פאי"
echo "=========================================================="

if [ "$EUID" -ne 0 ]; then
  echo "אנא הפעל את הסקריפט עם הרשאות root (sudo bash install.sh)"
  exit 1
fi

TARGET_USER="pi"
if ! id "$TARGET_USER" &>/dev/null; then
    TARGET_USER=$(logname 2>/dev/null || echo "$SUDO_USER")
fi
USER_HOME=$(eval echo "~$TARGET_USER")

echo "[1/6] מעדכן חבילות מערכת..."
apt-get update -y

echo "[2/6] מתקין תלויות חומרה, דפדפן ותמיכה בשלט הטלוויזיה (CEC)..."
apt-get install -y --no-install-recommends \
    xserver-xorg \
    x11-xserver-utils \
    xinit \
    matchbox-window-manager \
    chromium-browser \
    cec-utils \
    python3 \
    python3-pip \
    xdotool \
    git \
    alsa-utils \
    pulseaudio

echo "[3/6] מוריד ומגדיר את קבצי Israel TV+..."
mkdir -p "$USER_HOME/israel-tv-plus"
cp -r . "$USER_HOME/israel-tv-plus/" 2>/dev/null || git clone https://github.com/dor2500/israel-tv-plus.git "$USER_HOME/israel-tv-plus"
chown -R "$TARGET_USER:$TARGET_USER" "$USER_HOME/israel-tv-plus"
chmod +x "$USER_HOME/israel-tv-plus/rpi/start_kiosk.sh"
chmod +x "$USER_HOME/israel-tv-plus/rpi/cec_remote.py"

echo "[4/6] מגדיר שירות מערכת (systemd) להפעלה אוטומטית בהדלקה..."
cp "$USER_HOME/israel-tv-plus/rpi/israeltv.service" /etc/systemd/system/
sed -i "s/User=pi/User=$TARGET_USER/g" /etc/systemd/system/israeltv.service
sed -i "s|/home/pi|$USER_HOME|g" /etc/systemd/system/israeltv.service

systemctl daemon-reload
systemctl enable israeltv.service

echo "[5/6] מאמטב את הגדרות ה-GPU וה-HDMI ב-config.txt..."
CONFIG_FILE="/boot/config.txt"
[ ! -f "$CONFIG_FILE" ] && CONFIG_FILE="/boot/firmware/config.txt"

if [ -f "$CONFIG_FILE" ]; then
    grep -q "gpu_mem=" "$CONFIG_FILE" && sed -i 's/gpu_mem=.*/gpu_mem=256/' "$CONFIG_FILE" || echo "gpu_mem=256" >> "$CONFIG_FILE"
    grep -q "hdmi_force_hotplug=1" "$CONFIG_FILE" || echo "hdmi_force_hotplug=1" >> "$CONFIG_FILE"
    grep -q "hdmi_drive=2" "$CONFIG_FILE" || echo "hdmi_drive=2" >> "$CONFIG_FILE"
fi

echo "[6/6] התקנה הושלמה בהצלחה!"
echo "המכשיר יאתחל כעת ויעלה ישירות ל-Israel TV+ במסך מלא עם שליטה מלאה משלט הטלוויזיה."
echo "הפעל מחדש את הרספברי פאי: sudo reboot"

