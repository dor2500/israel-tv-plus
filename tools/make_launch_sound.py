"""The launch sound of the Android app: a soft two-note glass chime (~0.9 s), made here so it carries no third-party
licence. Writes platforms/android/res/raw/launch.wav (44.1 kHz, mono, 16-bit). Standard library only.

    python -I tools/make_launch_sound.py
"""
import math
import os
import struct
import wave

RATE = 44100
LENGTH = 0.95
OUT = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'platforms', 'android', 'res', 'raw', 'launch.wav')

# (start s, frequency Hz, gain): a rising fifth + octave, like a glass being tapped twice
NOTES = [(0.00, 1318.5, 0.55), (0.11, 1975.5, 0.45), (0.11, 2637.0, 0.12)]
# bell partials (ratio, relative gain, decay rate 1/s)
PARTIALS = [(1.0, 1.0, 6.5), (2.0, 0.28, 11.0), (2.76, 0.12, 16.0), (5.4, 0.04, 28.0)]


def sample(t):
    v = 0.0
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
    for delay, g in ((0.031, 0.22), (0.047, 0.16), (0.073, 0.1)):
        d = int(delay * RATE)
        for i in range(d, n):
            wet[i] += dry[i - d] * g
    peak = max(abs(x) for x in wet) or 1.0
    fade = int(0.08 * RATE)
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
