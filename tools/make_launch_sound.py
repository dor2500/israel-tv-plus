"""The launch sound of the Android app (~2 s), made here so it carries no third-party licence: a deep resonant swell
under the intro's glow (A1/A2 with soft harmonics and a slow beat), then a soft two-note glass chime when the emblem
fills in. Writes platforms/android/res/raw/launch.wav (44.1 kHz, mono, 16-bit). Standard library only.

    python -I tools/make_launch_sound.py
"""
import math
import os
import struct
import wave

RATE = 44100
LENGTH = 2.1
OUT = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'platforms', 'android', 'res', 'raw', 'launch.wav')

# the swell: (frequency Hz, gain); a 0.6 Hz detuned twin on each gives the slow "breathing" beat
DRONE = [(55.0, 0.55), (110.0, 0.42), (164.8, 0.16), (220.0, 0.10), (329.6, 0.05)]
# the chime, as the emblem fills (start s, frequency Hz, gain)
NOTES = [(0.70, 1318.5, 0.16), (0.80, 1975.5, 0.13), (0.80, 2637.0, 0.04)]
# bell partials (ratio, relative gain, decay rate 1/s)
PARTIALS = [(1.0, 1.0, 4.5), (2.0, 0.28, 8.0), (2.76, 0.12, 12.0), (5.4, 0.04, 22.0)]


def drone_env(t):
    """0.45 s swell in, a long resonant tail."""
    rise = min(1.0, t / 0.45)
    rise = rise * rise * (3 - 2 * rise)
    return rise * math.exp(-max(0.0, t - 0.45) * 1.9)


def sample(t):
    v = 0.0
    env = drone_env(t)
    for f, g in DRONE:
        v += g * env * (math.sin(2 * math.pi * f * t) + 0.6 * math.sin(2 * math.pi * (f + 0.6) * t))
    for start, f, g in NOTES:
        u = t - start
        if u < 0:
            continue
        attack = min(1.0, u / 0.004)          # 4 ms attack: no click
        for ratio, pg, decay in PARTIALS:
            v += g * pg * attack * math.exp(-decay * u) * math.sin(2 * math.pi * f * ratio * u)
    return v


def main():
    n = int(RATE * LENGTH)
    dry = [sample(i / RATE) for i in range(n)]
    # a little room: three quiet early reflections
    wet = list(dry)
    for delay, g in ((0.031, 0.22), (0.047, 0.16), (0.073, 0.1), (0.11, 0.08)):
        d = int(delay * RATE)
        for i in range(d, n):
            wet[i] += dry[i - d] * g
    peak = max(abs(x) for x in wet) or 1.0
    fade = int(0.3 * RATE)
    frames = bytearray()
    for i, x in enumerate(wet):
        if i > n - fade:
            x *= (n - i) / fade
        frames += struct.pack('<h', int(x / peak * 0.8 * 32767))
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with wave.open(OUT, 'wb') as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(RATE)
        w.writeframes(bytes(frames))
    print(OUT, len(frames) // 1024, 'KB')


if __name__ == '__main__':
    main()
