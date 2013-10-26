package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;

public class HUDAreYouSure extends HUD {
    
    private final static int YES_WIDTH = 196;
    private final static int YES_HEIGHT = 52;
    private final static int YES_X = 172;
    private final static int YES_Y = 218;
    private final static int NO_WIDTH = 196;
    private final static int NO_HEIGHT = 52;
    private final static int NO_X = 388;
    private final static int NO_Y = 218;

    public HUDAreYouSure(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/AreYouSure/BG");

        HUDArea hudArea = null;

        //yes
        hudArea = addArea(YES_X, YES_Y, YES_WIDTH, YES_HEIGHT, "yes");
        hudArea.setImage("HUD/AreYouSure/Yes");

        //no
        hudArea = addArea(NO_X, NO_Y, NO_WIDTH, NO_HEIGHT, "no");
        hudArea.setImage("HUD/AreYouSure/ButtonNew");
    }

    @Override
    public void update() {
        if (shouldRender) {
            HUDArea hudArea;

            //update slots
            for (int i = 0; i < hudAreas.size(); i++) {
                hudArea = hudAreas.get(i);
                /*if (hudArea.getType().equals("resolution")) {
                hudArea.setText(resolutions.get(currentResolution).toString());
                }*/
            }
        }

        super.update();
    }

    @Override
    public void HUDAreaClicked(HUDArea ha) {
        HUDArea hudArea = null;

        for (int i = 0; i < hudAreas.size(); i++) {
            hudArea = hudAreas.get(i);
            if (hudArea == ha) {
            }
        }
    }
}