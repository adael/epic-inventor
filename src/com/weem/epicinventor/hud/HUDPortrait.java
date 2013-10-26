package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;

import java.awt.*;

public class HUDPortrait extends HUD {

    private final static int PORTRAIT_X = 5;
    private final static int PORTRAIT_Y = 5;
    private final static int PORTRAIT_WIDTH = 74;
    private final static int PORTRAIT_HEIGHT = 74;
    private final static int HP_X = 110;
    private final static int HP_Y = 12;
    private final static int HP_WIDTH = 60;
    private final static int HP_HEIGHT = 15;
    private final static int ATTACK_X = 110;
    private final static int ATTACK_Y = 35;
    private final static int ATTACK_WIDTH = 60;
    private final static int ATTACK_HEIGHT = 15;

    public HUDPortrait(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/Portrait/BG");

        HUDArea hudArea = null;

        //portrait
        hudArea = addArea(PORTRAIT_X, PORTRAIT_Y, PORTRAIT_WIDTH, PORTRAIT_HEIGHT, "portrait");

        //hp
        hudArea = addArea(HP_X, HP_Y, HP_WIDTH, HP_HEIGHT, "hp");
        hudArea.setFont("SansSerif", Font.BOLD, 12);

        //attack
        hudArea = addArea(ATTACK_X, ATTACK_Y, ATTACK_WIDTH, ATTACK_HEIGHT, "attack");
        hudArea.setFont("SansSerif", Font.BOLD, 12);

        shouldRender = false;
    }

    @Override
    public void update() {
        if (shouldRender) {
            HUDArea hudArea;
            for (int i = 0; i < hudAreas.size(); i++) {
                hudArea = hudAreas.get(i);
                if (hudArea.getType().equals("portrait")) {
                    if (!registry.getPortraitImage().isEmpty()) {
                        hudArea.setImage("HUD/Portrait/" + registry.getPortraitImage());
                    } else {
                        hudArea.setImage("");
                    }
                } else if (hudArea.getType().equals("hp")) {
                    hudArea.setText(Integer.toString(registry.getPortraitHPCurrent()) + " / " + Integer.toString(registry.getPortraitHP()));
                } else if (hudArea.getType().equals("attack")) {
                    hudArea.setText(Integer.toString(registry.getPortraitAttack()));
                }
            }
        }

        super.update();
    }
}