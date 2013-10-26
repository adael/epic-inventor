package com.weem.epicinventor.utility;

public class GameMusicFade extends Thread {
    private float fadeStartGain;
    private float fadeEndGain;
    private float fadeCurrentGain;
    private float fadeTime;
    private float fadeTimeOffset;
    private GameSong gameSong;
    private boolean isFading;
    private boolean stop;
    private static long RATE = 100L;
    
    public GameMusicFade(GameSong gs, float fs, float fe, float ft, float fto) {
        gameSong = gs;
        stop = false;
        setFade(fs, fe, ft, fto);
        start();
    }
    
    protected void setFade(float fs, float fe, float ft, float fto) {
        fadeCurrentGain = fadeStartGain = fs;
        fadeEndGain = fe;
        fadeTime = ft;
        fadeTimeOffset = fto;
    }
    
    public boolean isFading() {
        return isFading;
    }
    
    public void fadeStop() {
        stop = true;
    }

    public void run() {
        try {
            Thread.sleep((int) (fadeTimeOffset*1000.0f));
            isFading = true;
            if(fadeEndGain < fadeStartGain) {
                gameSong.startFadeOut();
            }
            int maxLoops = (int)(fadeTime*(1000.0f/(float)RATE));
            for(int i = 0; i < maxLoops; i++) {
                if(!stop) {
                    gameSong.setVolume(fadeCurrentGain);
                    fadeCurrentGain += (fadeEndGain - fadeStartGain) / (float)maxLoops;
                    Thread.sleep(RATE);
                }
            }
            if(!stop) {
                gameSong.setVolume(fadeEndGain);
            }
            isFading = false;
            if(fadeEndGain < fadeStartGain) {
                gameSong.endFadeOut();
            }
        } catch (Exception e) {
            EIError.debugMsg(e.getMessage());
        }
    }
}
