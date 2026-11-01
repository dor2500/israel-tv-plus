#!/usr/bin/env python3
"""
Israel TV+ Raspberry Pi CEC Remote Controller Daemon
Captures TV remote control presses over HDMI-CEC via libcec/cec-client
and injects corresponding keyboard strokes to control the Israel TV+ UI smoothly.

Key Mappings:
  - Arrow Up / Down / Left / Right -> Up / Down / Left / Right
  - Select / OK / Enter           -> Enter
  - Back / Exit / Return          -> Escape / Backspace
  - Play / Pause                  -> Space
  - Red / Green / Yellow / Blue   -> Function / Shortcut keys
  - Number keys 0-9               -> 0-9 channel zap
"""

import sys
import subprocess
import time

try:
    from evdev import UInput, ecodes as e
    has_uinput = True
except ImportError:
    has_uinput = False

CEC_KEY_MAP = {
    'up': 103,       # KEY_UP
    'down': 108,     # KEY_DOWN
    'left': 105,     # KEY_LEFT
    'right': 106,    # KEY_RIGHT
    'select': 28,    # KEY_ENTER
    'enter': 28,
    'exit': 1,       # KEY_ESC
    'back': 14,      # KEY_BACKSPACE
    'return': 14,
    'play': 57,      # KEY_SPACE
    'pause': 57,
    'stop': 1,
    'red': 59,       # KEY_F1
    'green': 60,     # KEY_F2
    'yellow': 61,    # KEY_F3
    'blue': 62,      # KEY_F4
    '0': 11, '1': 2, '2': 3, '3': 4, '4': 5,
    '5': 6, '6': 7, '7': 8, '8': 9, '9': 10
}

def inject_key_xdotool(key_name):
    xdotool_map = {
        'up': 'Up', 'down': 'Down', 'left': 'Left', 'right': 'Right',
        'select': 'Return', 'enter': 'Return', 'exit': 'Escape', 'back': 'BackSpace',
        'return': 'BackSpace', 'play': 'space', 'pause': 'space',
        'red': 'F1', 'green': 'F2', 'yellow': 'F3', 'blue': 'F4'
    }
    k = xdotool_map.get(key_name, key_name)
    subprocess.run(['xdotool', 'key', k], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

def main():
    print("[Israel TV+ CEC] Starting HDMI-CEC Remote Daemon...")
    
    # Check if cec-client is installed
    check = subprocess.run(['which', 'cec-client'], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    if check.returncode != 0:
        print("[Israel TV+ CEC] Error: cec-client not found. Please install cec-utils.")
        sys.exit(1)

    cmd = ['cec-client', '-d', '8', '-s']
    proc = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, bufsize=1)

    print("[Israel TV+ CEC] Connected to HDMI CEC bus. Listening for TV remote commands...")
    for line in proc.stdout:
        line = line.strip().lower()
        if 'key pressed:' in line or 'user control pressed' in line:
            for k in CEC_KEY_MAP:
                if f"'{k}'" in line or f": {k}" in line:
                    inject_key_xdotool(k)
                    break

if __name__ == '__main__':
    main()

