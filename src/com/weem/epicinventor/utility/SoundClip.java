package com.weem.epicinventor.utility;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;

import java.awt.*;
import java.io.*;
import javax.sound.sampled.*;

public class SoundClip implements LineListener {

    private Clip clip = null;
    private int maxHearingDistance = 800;
    private boolean isLooping;
    private boolean wasHeard = false;

    public SoundClip(String filename) {
        if (Settings.volumeFX > 0) {
            filename = "/Sounds/" + filename + ".wav";
            try {
//                if(GameController.props.getProperty("os.name").contains("Linux") && GameController.props.getProperty("java.runtime.name").contains("OpenJDK")) {
//                } else if(GameController.props.getProperty("os.name").contains("Linux") && GameController.props.getProperty("java.vm.vendor").contains("Oracle")) {
//                    new PlayWave(filename).start();
//                } else {
                    AudioInputStream audioStream = createAudioStream(filename);

                    AudioFormat format = audioStream.getFormat();

                    DataLine.Info info = new DataLine.Info(Clip.class, format);

                    clip = (Clip) AudioSystem.getLine(info);
                    clip.addLineListener(this);
                    clip.open(audioStream);

                    if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                        if (gainControl != null) {
                            float min = gainControl.getMinimum();
                            float max = gainControl.getMaximum();

                            float volume = Settings.volumeFX;
                            volume *= 10f;
                            volume /= 100f;

                            float decibels = min + ((max - min) * volume);

                            gainControl.setValue(decibels); // Reduce volume by 10 decibels.
                        }
                    }

                    play();

                    wasHeard = true;
//                }
            } catch (Exception e) {
                EIError.debugMsg("Problem with " + filename + ": " + e.getMessage(), EIError.ErrorLevel.Error);
            }
        }
    }

    public SoundClip(String filename, float volume) {
        if (Settings.volumeFX > 0) {
            filename = "/Sounds/" + filename + ".wav";
            try {
//                if(GameController.props.getProperty("os.name").contains("Linux") && GameController.props.getProperty("java.runtime.name").contains("OpenJDK")) {
//                } else if(GameController.props.getProperty("os.name").contains("Linux") && GameController.props.getProperty("java.vm.vendor").contains("Oracle")) {
//                    new PlayWave(filename, volume).start();
//                } else {
                    AudioInputStream audioStream = createAudioStream(filename);

                    AudioFormat format = audioStream.getFormat();

                    DataLine.Info info = new DataLine.Info(Clip.class, format);

                    clip = (Clip) AudioSystem.getLine(info);
                    clip.addLineListener(this);
                    clip.open(audioStream);

                    if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                        if (gainControl != null) {
                            float min = gainControl.getMinimum();
                            float max = gainControl.getMaximum();

                            volume *= 10f;
                            volume /= 100f;

                            float decibels = min + ((max - min) * volume);

                            gainControl.setValue(decibels); // Reduce volume by 10 decibels.
                        }
                    }
                    play();
                    wasHeard = true;
//                }
            } catch (Exception e) {
                EIError.debugMsg("Problem with " + filename + ": " + e.getMessage(), EIError.ErrorLevel.Error);
            }
        }
    }

    public SoundClip(Registry registry, String filename, Point p) {
        if (Settings.volumeFX > 0) {
            filename = "/Sounds/" + filename + ".wav";
            try {
//                if(GameController.props.getProperty("os.name").contains("Linux") && GameController.props.getProperty("java.runtime.name").contains("OpenJDK")) {
//                } else if(GameController.props.getProperty("os.name").contains("Linux") && GameController.props.getProperty("java.vm.vendor").contains("Oracle")) {
//                    PlayerManager pm = registry.getPlayerManager();
//                    if (pm != null) {
//                        Player player = pm.getCurrentPlayer();
//                        if (player != null) {
//                            double distance = player.getCenterPoint().distance(p);
//                            if (distance <= maxHearingDistance) {
//                                new PlayWave(filename, distance).start();
//                            }
//                        }
//                    }
//                } else {
                    PlayerManager pm = registry.getPlayerManager();
                    if (pm != null) {
                        Player player = pm.getCurrentPlayer();
                        if (player != null) {
                            double distance = player.getCenterPoint().distance(p);

                            if (distance <= maxHearingDistance) {
                                AudioInputStream audioStream = createAudioStream(filename);

                                AudioFormat format = audioStream.getFormat();

                                DataLine.Info info = new DataLine.Info(Clip.class, format);

                                clip = (Clip) AudioSystem.getLine(info);

                                clip.addLineListener(this);
                                clip.open(audioStream);

                                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                                    if (gainControl != null) {
                                        float min = gainControl.getMinimum();
                                        float max = gainControl.getMaximum();

                                        float volume = Settings.volumeFX;
                                        volume *= 10f;
                                        volume /= 100f;
                                        
                                        float percentage = 1.0f;

                                        if(distance > 0) {
                                            if(distance > maxHearingDistance) {
                                                percentage = 0f;
                                            } else {
                                                percentage = (((float) maxHearingDistance - (float) distance) / (float) maxHearingDistance);  
                                            }
                                            
                                        }

                                        percentage *= volume;

                                        float decibels = min / 2 + ((max - min) / 2 * percentage);

                                        gainControl.setValue(decibels); // Reduce volume by 10 decibels.
                                    }
                                }
                                play();

                                wasHeard = true;
                            }
                        }
                    }
//                }
            } catch (Exception e) {
                EIError.debugMsg("Problem with " + filename + ": " + e.getMessage(), EIError.ErrorLevel.Error);
            }
        }
    }
    
    public boolean getWasHeard() {
        return wasHeard;
    }

    public void setLooping(boolean l) {
        isLooping = l;
    }

    /*private String getFinalSound(String s) {
    int min = 0;
    int max = 0;
    String finalName = s;
    
    for (int i = 1; i <= 9; i++) {
    if (sounds.containsKey(s + i)) {
    if (min == 0) {
    min = 1;
    }
    max = i;
    }
    }
    
    if (min >= 1) {
    finalName = s + Rand.getRange(min, max);
    }
    
    System.out.println(finalName);
    
    return finalName;
    }*/
    private AudioInputStream createAudioStream(String fileName) {
        AudioInputStream audioStream = null;

        try {
            audioStream = AudioSystem.getAudioInputStream(getClass().getResource(fileName));
        } catch (UnsupportedAudioFileException e) {
            EIError.debugMsg("Unsupported Sound File: " + fileName, EIError.ErrorLevel.Error);
        } catch (IOException e) {
            EIError.debugMsg("IO Error with Sound File: " + fileName, EIError.ErrorLevel.Error);
        }

        return audioStream;
    }

    @Override
    public void update(LineEvent lineEvent) {
        if (lineEvent.getType() == LineEvent.Type.STOP) {
            Clip c = (Clip) lineEvent.getLine();
            //c.stop();
            if (isLooping) {
                c.setFramePosition(0);  // NEW
                c.start();
            } else {
                c.close();
            }
        }
    }

    public void close() {
        if (clip != null) {
            clip.close();
        }
    }

    public void play() {
        if (clip != null) {
            clip.start();
        }
    }

    public void stop() {
        if (clip != null) {
            //clip.stop();
            clip.setFramePosition(0);
            clip.close();
        }
    }
}
