package com.weem.epicinventor.utility;

import java.util.*;
import com.weem.epicinventor.*;

public class GamePlayList {
    protected static String MUSIC_PATH = "/Music/";
    protected static String MUSIC_EXTENTION = ".mp3";
    
    protected ArrayList<GameSong> playList;
    protected boolean loopList;
    protected float transitionTime;
    protected int currentSongIndex;
    protected float currentMaxGain;
    protected static boolean hitFadeOutNext;
    protected boolean stopping;
    protected GamePlayList nextPlayList;
    protected GameSong lastSong;
    protected float volume;
    protected static float maxGain = -99999.0f;
    protected static float minGain = -99999.0f;
    
    public GamePlayList() {
        playList = new ArrayList();
        loopList = true;
        transitionTime = 2.5f;
        currentSongIndex = 0;
        currentMaxGain = -5.0f;
        hitFadeOutNext = false;
        stopping = false;
        nextPlayList = null;
        lastSong = null;
        volume = 100.0f;
    }
    
    public void start() {
        if(!(GameController.props.getProperty("os.name").contains("Linux") && GameController.props.getProperty("java.runtime.name").contains("OpenJDK"))) {
            lastSong = null;
            if(playList != null) {
                if(currentSongIndex >= playList.size()) {
                    currentSongIndex = 0;
                }
                GameSong gs = playList.get(currentSongIndex);
                hitFadeOutNext = false;
                stopping = false;
                gs.play();
            }
        }
    }
    
    public void stop() {
        if(playList != null) {
            stopping = true;
            GameSong gs = null;
            if(currentSongIndex < playList.size()) {
                gs = playList.get(currentSongIndex);
                currentSongIndex++;
            }
            if(currentSongIndex > playList.size()) {
                currentSongIndex = 0;
            }
            if(gs != null) {
                if(transitionTime > 0.0f) {
                    gs.fadeOut();
                } else {
                    gs.stop();
                    if(nextPlayList != null) {
                        nextPlayList.start();
                    }
                }
            }
        }
    }
    
    public boolean getStopping() {
        return stopping;
    }
    
    public GameSong getLastSong() {
        return lastSong;
    }
    
    public GameSong getCurrentSong() {
        GameSong gs = null;
        if(playList != null) {
            if(currentSongIndex < playList.size()) {
                gs = playList.get(currentSongIndex);
            }
        }
        return gs;
    }
    
    public float getMinGain() {
        GameSong gs = getCurrentSong();
        if(gs != null) {
            minGain = gs.getMinGain();
        }
        return minGain;
    }
    
    public float getMaxGain() {
        GameSong gs = getCurrentSong();
        if(gs != null) {
            maxGain = gs.getMaxGain();
        }
        return maxGain;
    }
    
    public void playSongNow(String song, float duration, boolean playOnce) {
        if(playList != null) {
            EIError.debugMsg("playSongNow "+song);
            GameSong gs = playList.get(currentSongIndex);
            playList.add(currentSongIndex+1, new GameSong(MUSIC_PATH+song+MUSIC_EXTENTION, currentMaxGain, transitionTime, duration, this, playOnce));
            hitFadeOutNext = false;
            if(transitionTime > 0.0f) {
                gs.fadeOut();
            } else {
                stop();
                start();
                hitFadeOutNext = false;
            }
        }
    }
    
    public void addToPlayList(String song, float duration) {
        playList.add(new GameSong(MUSIC_PATH+song+MUSIC_EXTENTION, currentMaxGain, transitionTime, duration, this, false));
    }
    
    public void addToPlayListFront(String song, float duration, boolean playOnce) {
        playList.add(0, new GameSong(MUSIC_PATH+song+MUSIC_EXTENTION, currentMaxGain, transitionTime, duration, this, playOnce));
    }
    
    public void setNextPlayList(GamePlayList npl) {
        nextPlayList = npl;
    }
    
    public void setTransitionTime(float tt) {
        transitionTime = tt;
    }
    
    public void setVolume(float v) {
        if(v > 100.0f) {
            v = 100.0f;
        }
        if(v < 0.0f) {
            v = 0.0f;
        }
        float max = getMaxGain();
        float min = getMinGain();
        if(!(max < -500.0f || min < -500.0f)) {
            float diff = max - min;
            float gain = diff*(v/100.0f)+min;
            for(int i = 0; i < playList.size(); i++) {
                GameSong gs = playList.get(i);
                if(gs != null) {
                    gs.stopFadeIn();
                    gs.setVolume(gain);
                    gs.setMaxGain(gain);
                }
            }
        }
        volume = v;
    }
    
    public void setAbsoluteVolume(float volume) {
        for(int i = 0; i < playList.size(); i++) {
            GameSong gs = playList.get(i);
            if(gs != null) {
                gs.setVolume(volume);
            }
        }
    }
    
    public void removeByFileName(String song) {
        if(playList != null) {
            GameSong gs;
            for(int i = 0; i < playList.size(); i++) {
                gs = playList.get(i);
                if(gs.getFilePath().equals(song)) {
                    if(currentSongIndex == i) {
                        if(transitionTime > 0.0f) {
                            gs.fadeOut();
                        } else {
                            gs.stop();
                        }
                    }
                    playList.remove(i);
                    if(currentSongIndex > i && i > 0) {
                        currentSongIndex--;
                    }
                }
            }
        }
    }
    
    private void startNextSong() {
        EIError.debugMsg("startNextSong");
        if(playList != null && !stopping) {
            if(currentSongIndex < playList.size()) {
                lastSong = playList.get(currentSongIndex);
            } else {
                lastSong = null;
            }
            GameSong gs = getNextSong(true);
            if(nextPlayList != null) {
//                EIError.debugMsg("start nextPlayList");
//                nextPlayList.start();
            } else if (gs != null) {
                EIError.debugMsg("next song "+gs.getFilePath());
                gs.play();
            }
        }
        if(nextPlayList != null) {
            EIError.debugMsg("start nextPlayList");
            nextPlayList.start();
        }
        nextPlayList = null;
    }
    
    public GameSong getNextSong(boolean updateIndex) {
        GameSong gs = null;
        int index = currentSongIndex;
        if(playList != null) {
            index++;
            if(index < playList.size()) {
                gs = playList.get(index);
            } else if (loopList) {
                index = 0;
                gs = playList.get(index);
            }
            if(updateIndex) {
                currentSongIndex = index;
            }
        }
        return gs;
    }
    
    public void stoppedCurrentSong() {
        EIError.debugMsg("stoppedCurrentSong "+hitFadeOutNext);
        if(!hitFadeOutNext) {
            startNextSong();
        }
        hitFadeOutNext = false;
    }
    
    public void startFadeOut() {
        if(transitionTime > 0.0f && getCurrentSong() != getNextSong(false) && !stopping) {
            EIError.debugMsg("startFadeOut");
            hitFadeOutNext = true;
            startNextSong();
        }
    }
}
