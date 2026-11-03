package com.example.mludo.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundManager {
    private var isMuted = false

    fun setMuted(muted: Boolean) {
        isMuted = muted
    }

    fun isMuted(): Boolean = isMuted

    fun playRollSound() {
        if (isMuted) return
        CoroutineScope(Dispatchers.IO).launch {
            // Short rattle sequence
            repeat(4) { idx ->
                playTone(400 + idx * 80, 0.04)
                Thread.sleep(30)
            }
        }
    }

    fun playStepSound() {
        if (isMuted) return
        CoroutineScope(Dispatchers.IO).launch {
            playTone(600, 0.05)
        }
    }

    fun playCaptureSound() {
        if (isMuted) return
        CoroutineScope(Dispatchers.IO).launch {
            playTone(800, 0.08)
            Thread.sleep(60)
            playTone(300, 0.12)
        }
    }

    fun playWinSound() {
        if (isMuted) return
        CoroutineScope(Dispatchers.IO).launch {
            val notes = listOf(523, 659, 784, 1046) // C E G C
            for (note in notes) {
                playTone(note, 0.12)
                Thread.sleep(100)
            }
        }
    }

    fun playButtonClick() {
        if (isMuted) return
        CoroutineScope(Dispatchers.IO).launch {
            playTone(500, 0.03)
        }
    }

    private fun playTone(freqHz: Int, durationSeconds: Double) {
        try {
            val sampleRate = 22050
            val numSamples = (durationSeconds * sampleRate).toInt()
            val sample = DoubleArray(numSamples)
            val buffer = ByteArray(numSamples)

            for (i in 0 until numSamples) {
                sample[i] = sin(2.0 * Math.PI * i.toDouble() / (sampleRate.toDouble() / freqHz.toDouble()))
                // Scale to 8-bit PCM
                val val8 = (sample[i] * 127.0).toInt()
                buffer[i] = val8.toByte()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep((durationSeconds * 1000).toLong() + 20)
            audioTrack.release()
        } catch (_: Exception) {
            // Ignore audio track exceptions gracefully
        }
    }
}
