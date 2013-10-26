/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weem.epicinventor.utility;

import javazoom.jl.player.advanced.*;
import javazoom.jl.player.*;
import java.io.*;

/**
 *
 * @author Brandon
 */
public class GameSong extends PlaybackListener implements Runnable {

    private String filePath;
    private AudioDevice audioDevice;
    private AdvancedPlayer player;
    private Thread playerThread;
    private float maxGain;
    private float currentGain;
    private float duration;
    private GameMusicFade fadeIn;
    private GameMusicFade fadeOut;
    private GamePlayList gamePlayList;
    private float fadeTime;
    private boolean currentlyPlaying;
    private boolean playOnce;

    public GameSong(String file, float mg, float ft, float dur, GamePlayList gpl, boolean po) {
        try {
            filePath = file;
            maxGain = mg;
            audioDevice = FactoryRegistry.systemRegistry().createAudioDevice();
            fadeIn = null;
            fadeOut = null;
            fadeTime = ft;
            currentlyPlaying = false;
            playOnce = po;
            gamePlayList = gpl;
            duration = dur;
        } catch (Exception e) {
            EIError.debugMsg(e.getMessage());
        }
    }

    protected void playerInitialize() {
        try {
            audioDevice = FactoryRegistry.systemRegistry().createAudioDevice();
            InputStream stream = getClass().getResourceAsStream(filePath);
            player = new AdvancedPlayer(stream, audioDevice);
            player.setPlayBackListener(this);
        } catch (Exception e) {
            EIError.debugMsg(e.getMessage());
        }
    }

    public void stop() {
        if(player != null) {
            if(fadeTime == 0.0f || this != gamePlayList.getCurrentSong() || gamePlayList.getStopping()) {
                stopFadeIn();
                stopFadeOut();

//                setVolume(getMinGain());
//                if(playerThread != null) {
//                    playerThread.interrupt();
//                    playerThread = null;
//                }
                currentlyPlaying = false;
                stopPlayer();
            }
        }
    }
    
    protected void stopPlayer() {
        if(player != null) {
            AdvancedPlayer p = player;
            player = null;
            p.stop();
        }
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public boolean isPlaying() {
        return currentlyPlaying;
    }
    
    public void stopFadeIn() {
        if(fadeIn != null) {
            fadeIn.fadeStop();
            fadeIn.interrupt();
            fadeIn = null;
        }
    }
    
    protected void stopFadeOut() {
        if(fadeOut != null) {
            fadeOut.fadeStop();
            fadeOut.interrupt();
            fadeOut = null;
        }
    }
    
    public void fadeOut() {
        stopFadeIn();
        if(fadeTime > 0.0f) {
            if(fadeOut != null) {
                if(!fadeOut.isFading()) {
                    stopFadeOut();
                    fadeOut = new GameMusicFade(this, currentGain, getMinGain(), fadeTime, 0.0f);
                }
            } else if(gamePlayList.getStopping()) {
                fadeOut = new GameMusicFade(this, currentGain, getMinGain(), fadeTime, 0.0f);
            }
        } else {
            stop();
        }
    }

    public void play() {
        boolean playListStopping = false;
        if(gamePlayList != null) {
            playListStopping = gamePlayList.getStopping();
        }
        if(!playListStopping) {
            if(playerThread != null && player != null) {
                player.stop();
            }
            player = null;
            playerInitialize();
            //EIError.debugMsg("GameSongThread "+filePath);
            playerThread = new Thread(this, "GameSongThread "+filePath);
            playerThread.start();
        }
    }

    public float getMaxGain() {
        float gainMax = -9999.0f;
        if (audioDevice instanceof JavaSoundAudioDevice) {
            JavaSoundAudioDevice jsAudio = (JavaSoundAudioDevice) audioDevice;
            gainMax = jsAudio.getMaxGain();
        }
        return gainMax;
    }
    
    public void setMaxGain(float gain) {
        maxGain = gain;
    }

    public float getMinGain() {
        float gainMin = -9999.0f;
        if (audioDevice instanceof JavaSoundAudioDevice) {
            JavaSoundAudioDevice jsAudio = (JavaSoundAudioDevice) audioDevice;
            gainMin = jsAudio.getMinGain();
        }
        return gainMin;
    }

    public void setVolume(float gain) {
        if (audioDevice instanceof JavaSoundAudioDevice) {
            JavaSoundAudioDevice jsAudio = (JavaSoundAudioDevice) audioDevice;
            if (gain > maxGain) {
                gain = maxGain;
            }
            if (gain > getMaxGain()) {
                gain = getMaxGain();
            }
            if(fadeOut != null) {
                fadeOut.setFade(gain, (gain+getMinGain())/2, fadeTime, duration-fadeTime);
            }
            currentGain = gain;
            jsAudio.setLineGain(gain);
            //EIError.debugMsg("Setting Gain: "+gain);
        }
    }
    
    public void startFadeOut() {
        if(gamePlayList != null) {
            if(this == gamePlayList.getNextSong(false) && !gamePlayList.getStopping()) {
                stopFadeOut();
            }
            gamePlayList.startFadeOut();
        }
    }
    
    public void endFadeOut() {
        stop();
    }

    @Override
    public void playbackFinished(PlaybackEvent playbackEvent) {
        if(gamePlayList != null) {
            if(playOnce) {
                gamePlayList.removeByFileName(filePath);
            }
        }
        stop();
        if(gamePlayList != null) {
            gamePlayList.stoppedCurrentSong();
        }
    }

    // PlaybackListener members
    @Override
    public void playbackStarted(PlaybackEvent playbackEvent) {
        try {
            if (fadeTime > 0.0f && fadeIn == null) {
                stopFadeIn();
                stopFadeOut();
                setVolume(getMinGain());
                if(gamePlayList != null) {
                    if(gamePlayList.getLastSong() != this) {
                        fadeIn = new GameMusicFade(this, (maxGain+getMinGain())/2, maxGain, fadeTime, 0.0f);
                    }
                }
                fadeOut = new GameMusicFade(this, maxGain, (maxGain+getMinGain())/2, fadeTime, duration-fadeTime);
            } else {
                setVolume(maxGain);
            }
        } catch (Exception e) {
            EIError.debugMsg(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            currentlyPlaying = true;
            EIError.debugMsg("GameSong: "+filePath);
            player.play();
        } catch (Exception e) {
            EIError.debugMsg(e.getMessage());
        }
        currentlyPlaying = false;
    }
}
