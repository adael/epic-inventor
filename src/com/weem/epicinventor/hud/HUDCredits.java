package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.utility.*;

public class HUDCredits extends HUD {

    private final static int MAIN_MENU_X = 595;
    private final static int MAIN_MENU_Y = 23;
    private final static int MAIN_MENU_WIDTH = 116;
    private final static int MAIN_MENU_HEIGHT = 25;
    private final static int EXIT_X = 721;
    private final static int EXIT_Y = 23;
    private final static int EXIT_WIDTH = 56;
    private final static int EXIT_HEIGHT = 25;

    public HUDCredits(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/Credits/BG");

        HUDArea hudArea = null;

        //main_menu
        hudArea = addArea(MAIN_MENU_X, MAIN_MENU_Y, MAIN_MENU_WIDTH, MAIN_MENU_HEIGHT, "main_menu");
        hudArea.setImage("HUD/Credits/ButtonMainMenu");

        //exit
        hudArea = addArea(EXIT_X, EXIT_Y, EXIT_WIDTH, EXIT_HEIGHT, "exit");
        hudArea.setImage("HUD/Credits/ButtonExit");
    }

    @Override
    public void HUDAreaClicked(HUDArea ha) {
        HUDArea hudArea = null;

        for (int i = 0; i < hudAreas.size(); i++) {
            hudArea = hudAreas.get(i);
            if (hudArea == ha) {
                if (hudArea.getType().equals("main_menu")) {
                    SoundClip cl = new SoundClip("Misc/Click");
                    hudManager.unloadHUD(name);
                    hudManager.togglePauseHUD();
                } else if (hudArea.getType().equals("exit")) {
                    SoundClip cl = new SoundClip("Misc/Click");
                    hudManager.unloadHUD(name);
                    hudManager.resumeMasterGame();
                }
            }
        }
    }
}