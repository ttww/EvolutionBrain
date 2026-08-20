/*
 *  This file is part of the EvolutionBrain project.
 *
 *  Copyright (c) 2011 by Thomas Welsch (ttww@gmx.de). All rights reserved.
 *
 *  EvolutionBrain is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  EvolutionBrain is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with EvolutionBrain.  If not, see <http://www.gnu.org/licenses/>.
 */

package tw.master.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author Thomas Welsch
 */
public class SoundUtils {

    //----------------------------------------------------------------------------------------------

    /**
     * Test method to check sound API.
     */
    public static void TschilpTschilp() {
        try {
            int sampleRate = 22050;
            AudioFormat audioformat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 16, 2, 4,
                    sampleRate, false);
            DataLine.Info datalineinfo = new DataLine.Info(SourceDataLine.class, audioformat);

            if (!AudioSystem.isLineSupported(datalineinfo)) {
                System.err.println("Line matching " + datalineinfo + " is not supported.");
            } else {
                SourceDataLine sourcedataline = (SourceDataLine) AudioSystem.getLine(datalineinfo);
                sourcedataline.open(audioformat);
                sourcedataline.start();

                byte[] samples = new byte[1000];
                for (int s = 1; s < 10; s++) {
                    for (int freq = 1000; freq < 2000; freq += s) {
                        float size = (float) sampleRate / (float) freq;
                        float amplitude = 32000;
                        int adr = 0;
                        for (int i = 0; i < size; i++, adr += 4) {
                            double sin = Math.sin((double) i / (double) size * 2.0 * Math.PI);
                            int sample = (int) (sin * amplitude);
                            samples[adr + 0] = (byte) sample;
                            samples[adr + 1] = (byte) (sample >>> 8);
                            samples[adr + 2] = (byte) sample;
                            samples[adr + 3] = (byte) (sample >>> 8);
                        }
                        sourcedataline.write(samples, 0, adr);
                    }
                }

                sourcedataline.drain();
                sourcedataline.stop();
                sourcedataline.close();
            }
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * A sound is taken from a soundfile , it is loaded into memory(a clip) and then played.
     * This method blocks until the sound is played. For non blocking operations use
     * <code>playSoundAsync()</code>
     *
     * @param soundName	Complete filename with extension with sound."<p>
     * 			Default sounds:<br>
     *				Basso,Blow,Bottle,Frog,Funk,Glass,Hero,
     *				Morse,Ping,Pop,Purr,Sosumi,Submarine,Tack,Tink
     */
    public static void playSound(String soundName) {
        try {
            InputStream in = SoundUtils.class.getResourceAsStream("/sounds/" + soundName);
            AudioInputStream sound = AudioSystem.getAudioInputStream(in);

            // load the sound into memory (a Clip)
            DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
            Clip clip = (Clip) AudioSystem.getLine(info);

            clip.open(sound);
            /*
            	clip.addLineListener(new LineListener() {
              public void update(LineEvent event) {
                if (event.getType() == LineEvent.Type.STOP) {
                  event.getLine().close();
                }
              }
            });
             */
            // play the sound clip
            clip.start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // Test-Case under Linux produces this exception, it's is not relevant....
            // No line matching interface Clip supporting format PCM_SIGNED 22050.0 Hz, 16 bit, stereo, 4 bytes/frame, big-endian is supported.)
            if (e.getMessage().startsWith("No line matching interface Clip supporting")) {
                System.err.println("No sound ?! Ignore playing " + soundName + " because: " + e.getMessage());
            } else
                throw e;
        }

    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Play a sound in the background.
     *
     * @param soundName	File with sound.
     * The Files are loaded without adding ".aiff"<p>
     * Default sounds:<br>
     *				Basso,Blow,Bottle,Frog,Funk,Glass,Hero,
     *				Morse,Ping,Pop,Purr,Sosumi,Submarine,Tack,Tink
     */
    public static void playSoundAsync(final String soundName) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                SoundUtils.playSound(soundName);
            }
        }, "Sound " + soundName).start();
    }

    // ---------------------------------------------------------------------------------------------

    public static void playTink() {
        playSoundAsync("Tink.aiff");
    }

} // class

