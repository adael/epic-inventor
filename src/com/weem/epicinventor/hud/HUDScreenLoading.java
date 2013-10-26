package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;

import java.awt.*;

public class HUDScreenLoading extends HUD {

    public HUDScreenLoading(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/ScreenLoading/BG");

        setBGColor(Color.BLACK);
        setTextColor(Color.WHITE);
        setTextSize(48);
        setText("Loading...");
    }
    
    @Override
    public void update() {
        setText(Game.loadingText);
    }
}