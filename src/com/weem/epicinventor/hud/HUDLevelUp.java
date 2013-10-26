package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;

import java.awt.*;

public class HUDLevelUp extends HUD {

    private final static int ATTACK_BONUS_X = 47;
    private final static int ATTACK_BONUS_Y = 250;
    private final static int ATTACK_BONUS_WIDTH = 200;
    private final static int ATTACK_BONUS_HEIGHT = 25;
    private final static int HP_BONUS_X = 170;
    private final static int HP_BONUS_Y = 250;
    private final static int HP_BONUS_WIDTH = 200;
    private final static int HP_BONUS_HEIGHT = 25;
    private final static int LEVEL_X = 100;
    private final static int LEVEL_Y = 75;
    private final static int LEVEL_WIDTH = 200;
    private final static int LEVEL_HEIGHT = 25;
    private final static int BUTTON_CLOSE_WIDTH = 42;
    private final static int BUTTON_CLOSE_HEIGHT = 42;
    private final static int BUTTON_CLOSE_X = 216;
    private final static int BUTTON_CLOSE_Y = 0;

    public HUDLevelUp(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/LevelUp/BG");

        HUDArea hudArea = null;

        //close
        hudArea = addArea(BUTTON_CLOSE_X, BUTTON_CLOSE_Y, BUTTON_CLOSE_WIDTH, BUTTON_CLOSE_HEIGHT, "close");
        hudArea.setImage("HUD/LevelUp/ButtonClose");

        //level
        if(registry.getPlayerManager().getCurrentPlayer().getLevel() > 9) {
            hudArea = addArea(LEVEL_X, LEVEL_Y, LEVEL_WIDTH, LEVEL_HEIGHT, "level");
        } else {
            hudArea = addArea(LEVEL_X + 15, LEVEL_Y, LEVEL_WIDTH, LEVEL_HEIGHT, "level");
        }
        hudArea.setFont("SansSerif", Font.BOLD, 70);
        hudArea.setText(Integer.toString(registry.getPlayerManager().getCurrentPlayer().getLevel()));

        //attack bonus
        if(registry.getAttackBonus() > 99) {
            hudArea = addArea(ATTACK_BONUS_X, ATTACK_BONUS_Y, ATTACK_BONUS_WIDTH, ATTACK_BONUS_HEIGHT, "attack_bonus");
        } else if(registry.getAttackBonus() > 9) {
            hudArea = addArea(ATTACK_BONUS_X + 3, ATTACK_BONUS_Y, ATTACK_BONUS_WIDTH, ATTACK_BONUS_HEIGHT, "attack_bonus");
        } else {
            hudArea = addArea(ATTACK_BONUS_X + 6, ATTACK_BONUS_Y, ATTACK_BONUS_WIDTH, ATTACK_BONUS_HEIGHT, "attack_bonus");
        }
        hudArea.setFont("SansSerif", Font.BOLD, 20);
        hudArea.setText("+" + registry.getAttackBonus());

        //hp bonus
        if(registry.getHPBonus() > 99) {
            hudArea = addArea(HP_BONUS_X, HP_BONUS_Y, HP_BONUS_WIDTH, HP_BONUS_HEIGHT, "hp_bonus");
        } else if(registry.getHPBonus() > 9) {
            hudArea = addArea(HP_BONUS_X + 3, HP_BONUS_Y, HP_BONUS_WIDTH, HP_BONUS_HEIGHT, "hp_bonus");
        } else {
            hudArea = addArea(HP_BONUS_X + 6, HP_BONUS_Y, HP_BONUS_WIDTH, HP_BONUS_HEIGHT, "hp_bonus");
        }
        hudArea.setFont("SansSerif", Font.BOLD, 20);
        hudArea.setText("+" + registry.getHPBonus());
    }

    @Override
    public void update() {
        if (shouldRender) {
            HUDArea hudArea;
            for (int i = 0; i < hudAreas.size(); i++) {
                hudArea = hudAreas.get(i);
                if (hudArea.getType().equals("attack_bonus")) {
                    hudArea.setText("+" + registry.getAttackBonus());
                } else if (hudArea.getType().equals("hp_bonus")) {
                    hudArea.setText("+" + registry.getHPBonus());
                } else if (hudArea.getType().equals("level")) {
                    hudArea.setText(Integer.toString(registry.getPlayerManager().getCurrentPlayer().getLevel()));
                } 
            }
        }

        super.update();
    }

    @Override
    public void HUDAreaClicked(HUDArea ha) {
        HUDArea hudArea = null;

        for (int i = 0; i < hudAreas.size(); i++) {
            hudArea = hudAreas.get(i);

            if (hudArea.getType().equals("close")) {
                hudManager.unloadHUD(name);
            }
        }
    }
}