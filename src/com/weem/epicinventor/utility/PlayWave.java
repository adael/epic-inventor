package com.weem.epicinventor.utility;

import java.io.*;
import java.net.URL;
import com.weem.epicinventor.*;

import javax.sound.sampled.*;

public class PlayWave extends Thread implements LineListener {

    private String filename;
    private AudioInputStream inStream;
    private float volume;
    private double distance;
    private int maxHearingDistance = 800;
    private float minDecibels = -30f;
    
    public PlayWave(String wavfile) {
        volume = Settings.volumeFX;
        distance = -1.0f;
        filename = wavfile;
    }
    
    public PlayWave(String wavfile, float v) {
        volume = v;
        distance = -1.0f;
        filename = wavfile;
    }
    
    public PlayWave(String wavfile, double d) {
        volume = Settings.volumeFX;
        distance = d;
        filename = wavfile;
    }
    
    @Override
    public void run() {
        SourceDataLine auline = getSourceDataLine(filename);
        if(auline != null) {
            auline.start();
            int nBytesRead = 0;
            byte[] abData = new byte[auline.getBufferSize()];
            try {
                if (auline.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) auline.getControl(FloatControl.Type.MASTER_GAIN);

                    if (gainControl != null) {
                        float min = gainControl.getMinimum();
                        float max = gainControl.getMaximum();
                        float decibels = 0.0f;

                        if(distance > 0.0f) {
                            float percentage = (float) distance / (float) maxHearingDistance;
                            percentage *= volume;
                            decibels = minDecibels * percentage;
                        } else {
                            volume *= 10f;
                            volume /= 100f;
                            decibels = min + ((max - min) * volume);
                        }
                        if(decibels > max) {
                            decibels = max;
                        } else if(decibels < min) {
                            decibels = min;
                        }
                        gainControl.setValue(decibels); // Reduce volume by 10 decibels.
                    }
                }
            } catch (Exception e) { 
                EIError.debugMsg(e.getMessage());
            }
            try {
                while (nBytesRead != -1) { 
                    nBytesRead = inStream.read(abData, 0, abData.length);
                    if(nBytesRead < auline.getBufferSize()) {
                        if(nBytesRead > 0) {
                            for(int i = nBytesRead; i < abData.length; i++) {
                                abData[i] = 0;
                            }
                        }
                    }
                    if (nBytesRead >= 0) 
                        auline.write(abData, 0, abData.length);
                } 
            } catch (IOException e) { 
                EIError.debugMsg(e.getMessage());
            } finally { 
                auline.drain();
                auline.close();
            }
        }
    }
    
    private SourceDataLine getSourceDataLine(String fileName) {
        try {
            URL url = getClass().getResource(fileName);
            inStream = AudioSystem.getAudioInputStream(url);
            AudioFormat format = inStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            return line;
        } catch (Exception e) {
            EIError.debugMsg(e.getMessage());
        }
        return null;
    }
    
    @Override
    public void update(LineEvent lineEvent) {
        //System.out.println(lineEvent.getFramePosition()+" "+lineEvent.getType());
    }
}