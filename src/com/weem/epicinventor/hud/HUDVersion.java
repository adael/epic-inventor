package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;

import java.awt.*;
import java.awt.Color;

public class HUDVersion extends HUD {

    private final static int TEXT_X = 10;
    private final static int TEXT_Y = 6;
    private final static int TEXT_WIDTH = 300;
    private final static int TEXT_HEIGHT = 20;
    private final static int SUBMIT_X = 645;
    private final static int SUBMIT_Y = 0;
    private final static int SUBMIT_WIDTH = 155;
    private final static int SUBMIT_HEIGHT = 30;

    public HUDVersion(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/Version/BG");

        HUDArea hudArea = null;

        hudArea = addArea(TEXT_X, TEXT_Y, TEXT_WIDTH, TEXT_HEIGHT, "version");
        hudArea.setFont("SansSerif", Font.BOLD, 14);
        hudArea.setTextColor(Color.WHITE);
        hudArea.setText("ALPHA Pre-Release v" + Game.VERSION);
        
        hudArea = addArea(SUBMIT_X, SUBMIT_Y, SUBMIT_WIDTH, SUBMIT_HEIGHT, "submit");
        hudArea.setImage("HUD/Version/Submit");
    }

    @Override
    public void HUDAreaClicked(HUDArea ha) {
        HUDArea hudArea = null;

        for (int i = 0; i < hudAreas.size(); i++) {
            hudArea = hudAreas.get(i);
            if (hudArea == ha) {
                if (hudArea.getType().equals("submit")) {
                    String url = "http://epicinventor.com/forum/index.php?board=8.0";

                    try {
                        Desktop.getDesktop().browse(java.net.URI.create(url));
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
}