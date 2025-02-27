package com.example.myapplicationtest1;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;



public class Sound {

    private MediaPlayer mediaPlayer;

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void Sound_Click() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        //mediaPlayer = MediaPlayer.create(context, R.raw.button_click);
        mediaPlayer.start();
    }

    Sound() {

    }


    static public class ToneGenerator {//creates various sounds for the game


        public static void toneGenerator(float startFreq, float endFreq, int nbOfSeconds) {
            final int sampleRate = 44100; // Standard audio sample rate
            int numSamples = nbOfSeconds * sampleRate; // Total samples for the duration
            double[] sample = new double[numSamples]; // Array to hold sample data
            byte[] generatedSound = new byte[2 * numSamples]; // Output buffer for audio
            new Thread(() -> {
                // Generate the tone samples
                for (int i = 0; i < numSamples; ++i) {
                    double currentFreq = startFreq + ((endFreq - startFreq) * i / numSamples); // Linear interpolation
                    sample[i] = Math.sin(2 * Math.PI * i * currentFreq / sampleRate); // Sine wave calculation
                }

                // Convert to 16-bit PCM format
                int index = 0;
                for (final double value : sample) {
                    // Scale to max amplitude for 16-bit PCM
                    final short val = (short) ((value * 32767));
                    // Little-endian format: LSB first
                    generatedSound[index++] = (byte) (val & 0x00ff);
                    generatedSound[index++] = (byte) ((val & 0xff00) >>> 8);
                }

                // Play the tone using AudioTrack
                AudioTrack audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        generatedSound.length,
                        AudioTrack.MODE_STATIC
                );

                audioTrack.write(generatedSound, 0, generatedSound.length);
                audioTrack.play();

                // Wait for the tone to finish
                try {
                    Thread.sleep(nbOfSeconds * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }

                // Release the AudioTrack resources
                audioTrack.stop();
                audioTrack.release();
            }).start();
        }
    }
}
