package com.israeltv.plus.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Offline, pure synthesized cinematic sub-bass startup sound (Netflix / YouTube TV style):
 * - Sub-bass sine drop: 110Hz sweeping down to 55Hz over 180ms.
 * - Warm harmonic fifth chord swell: 110Hz + 165Hz + 220Hz with soft exponential decay over 1.4s.
 * Programmatically generated via Android AudioTrack PCM 16-bit.
 */
object CinematicBassSound {
    private const val SAMPLE_RATE = 44100
    private const val DROP_SEC = 0.180
    private const val TOTAL_SEC = 1.6

    fun play() {
        Thread {
            try {
                val totalSamples = (SAMPLE_RATE * TOTAL_SEC).toInt()
                val buffer = ShortArray(totalSamples)

                val dropSamples = (SAMPLE_RATE * DROP_SEC).toInt()
                val f0 = 110.0
                val f1 = 55.0
                val dropSlope = (f1 - f0) / DROP_SEC

                val chordF1 = 110.0
                val chordF2 = 165.0
                val chordF3 = 220.0

                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    var sampleVal = 0.0

                    // 1. Sub-bass sine drop (110Hz -> 55Hz over 180ms)
                    if (t <= DROP_SEC) {
                        val phaseDrop = 2.0 * PI * (f0 * t + 0.5 * dropSlope * t * t)
                        val attack = (t / 0.012).coerceIn(0.0, 1.0)
                        val dropAmp = 0.88 * attack
                        sampleVal += dropAmp * sin(phaseDrop)
                    }

                    // 2. Warm fifth chord swell (110Hz + 165Hz + 220Hz with exponential decay over 1.4s)
                    val swellAttack = (t / 0.14).coerceIn(0.0, 1.0)
                    val decayTime = (t - 0.14).coerceAtLeast(0.0)
                    val decay = exp(-decayTime / 0.45)
                    val chordAmp = 0.68 * swellAttack * decay

                    val phase1 = 2.0 * PI * chordF1 * t
                    val phase2 = 2.0 * PI * chordF2 * t
                    val phase3 = 2.0 * PI * chordF3 * t

                    val chord = 0.44 * sin(phase1) + 0.36 * sin(phase2) + 0.20 * sin(phase3)
                    sampleVal += chordAmp * chord

                    // Soft saturation limit
                    val clamped = sampleVal.coerceIn(-1.0, 1.0)
                    buffer[i] = (clamped * 32767.0).toInt().toShort()
                }

                val minBufferSize = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                )
                val trackBufferSize = maxOf(minBufferSize, buffer.size * 2)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(trackBufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()

                Thread.sleep((TOTAL_SEC * 1000 + 200).toLong())
                track.stop()
                track.release()
            } catch (_: Throwable) {
                // Audio synthesis is non-fatal
            }
        }.start()
    }
}
