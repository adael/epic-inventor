package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.utility.*;
import com.weem.epicinventor.weapon.*;

import java.awt.*;
import java.text.*;

public class HUDPause extends HUD {

    private final static int BUTTON_X = 35;
    private final static int BUTTON_Y_START = 36;
    private final static int BUTTON_Y_SPACING = 60;
    private final static int BUTTON_WIDTH = 186;
    private final static int BUTTON_HEIGHT = 47;

    public HUDPause(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/Pause/BG");

        HUDArea hudArea = null;

        //resume
        hudArea = addArea(BUTTON_X, BUTTON_Y_START + (BUTTON_Y_SPACING * 0), BUTTON_WIDTH, BUTTON_HEIGHT, "resume");
        hudArea.setImage("HUD/Pause/ButtonResume");

        //tutorial
        hudArea = addArea(BUTTON_X, BUTTON_Y_START + (BUTTON_Y_SPACING * 1), BUTTON_WIDTH, BUTTON_HEIGHT, "tutorial");
        hudArea.setImage("HUD/Pause/ButtonTutorial");

        //crafting
        hudArea = addArea(BUTTON_X, BUTTON_Y_START + (BUTTON_Y_SPACING * 2), BUTTON_WIDTH, BUTTON_HEIGHT, "crafting");
        hudArea.setImage("HUD/Pause/ButtonCrafting");

        //donate
        hudArea = addArea(BUTTON_X, BUTTON_Y_START + (BUTTON_Y_SPACING * 3), BUTTON_WIDTH, BUTTON_HEIGHT, "donate");
        hudArea.setImage("HUD/Pause/ButtonDonate");

        //credits
        hudArea = addArea(BUTTON_X, BUTTON_Y_START + (BUTTON_Y_SPACING * 4), BUTTON_WIDTH, BUTTON_HEIGHT, "credits");
        hudArea.setImage("HUD/Pause/ButtonCredits");

        //save
        hudArea = addArea(BUTTON_X, BUTTON_Y_START + (BUTTON_Y_SPACING * 5), BUTTON_WIDTH, BUTTON_HEIGHT, "save");
        hudArea.setImage("HUD/Pause/ButtonSave");

        shouldRender = false;
    }

    @Override
    public void HUDAreaClicked(HUDArea ha) {
        HUDArea hudArea = null;

        for (int i = 0; i < hudAreas.size(); i++) {
            hudArea = hudAreas.get(i);
            if (hudArea == ha) {
                if (hudArea.getType().equals("resume")) {
                    SoundClip cl = new SoundClip("Misc/Click");
                    shouldRender = false;
                    hudManager.resumeMasterGame();
                } else if (hudArea.getType().equals("tutorial")) {
                    SoundClip cl = new SoundClip("Misc/Click");
                    shouldRender = false;
                    hudManager.loadHUD(HUDManager.HUDType.Tutorial);
                } else if (hudArea.getType().equals("credits")) {
                    SoundClip cl = new SoundClip("Misc/Click");
                    shouldRender = false;
                    hudManager.loadHUD(HUDManager.HUDType.Credits);
                } else if (hudArea.getType().equals("crafting")) {
                    String url = "http://www.epicinventor.com/crafting/";

                    try {
                        Desktop.getDesktop().browse(java.net.URI.create(url));
                    } catch (Exception e) {
                    }
                } else if (hudArea.getType().equals("donate")) {
                    String url = "http://epicinventor.com/donate.php";

                    try {
                        Desktop.getDesktop().browse(java.net.URI.create(url));
                    } catch (Exception e) {
                    }
                } else if (hudArea.getType().equals("save")) {
                    SoundClip cl = new SoundClip("Misc/Click");
                    registry.getMonsterManager().removeAllMonsters();
                    hudManager.saveAndQuit();
                }
            }
        }
    }

    @Override
    public void togglePauseHUD() {
        shouldRender = !shouldRender;
    }
}