package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;

public class HUDTutorial extends HUD {

    private final static int NEXT_X = 679;
    private final static int NEXT_Y = 529;
    private final static int NEXT_WIDTH = 92;
    private final static int NEXT_HEIGHT = 41;
    private final static int FINISH_X = 354;
    private final static int FINISH_Y = 529;
    private final static int FINISH_WIDTH = 92;
    private final static int FINISH_HEIGHT = 41;
    private final static int MAIN_MENU_X = 595;
    private final static int MAIN_MENU_Y = 23;
    private final static int MAIN_MENU_WIDTH = 116;
    private final static int MAIN_MENU_HEIGHT = 25;
    private final static int EXIT_X = 721;
    private final static int EXIT_Y = 23;
    private final static int EXIT_WIDTH = 56;
    private final static int EXIT_HEIGHT = 25;
    private final static int IMAGE_COUNT = 6;
    private int currentImage = 1;

    public HUDTutorial(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/Tutorial/1");

        HUDArea hudArea = null;

        //next
        hudArea = addArea(NEXT_X, NEXT_Y, NEXT_WIDTH, NEXT_HEIGHT, "next");
        hudArea.setImage("HUD/Tutorial/ButtonNext");

        //finish
        hudArea = addArea(FINISH_X, FINISH_Y, FINISH_WIDTH, FINISH_HEIGHT, "finish");
        hudArea.setImage("HUD/Tutorial/ButtonFinish");
        hudArea.setIsActive(false);

        //main_menu
        hudArea = addArea(MAIN_MENU_X, MAIN_MENU_Y, MAIN_MENU_WIDTH, MAIN_MENU_HEIGHT, "main_menu");
        hudArea.setImage("HUD/Tutorial/ButtonMainMenu");

        //exit
        hudArea = addArea(EXIT_X, EXIT_Y, EXIT_WIDTH, EXIT_HEIGHT, "exit");
        hudArea.setImage("HUD/Tutorial/ButtonExit");
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
                } else if (hudArea.getType().equals("next")) {
                    SoundClip cl = new SoundClip("Misc/Click");
                    if (currentImage < IMAGE_COUNT) {
                        currentImage++;
                        setImage("HUD/Tutorial/" + currentImage);
                    }
                    if (currentImage >= IMAGE_COUNT) {
                        hudArea.setIsActive(false);
                        this.getHUDAreaByType("finish").setIsActive(true);
                    }
                } else if (hudArea.getType().equals("finish")) {
                    SoundClip cl = new SoundClip("Misc/Click");
                    hudManager.unloadHUD(name);
                    hudManager.resumeMasterGame();
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (shouldRender) {
            g.setColor(new Color(25, 25, 25));
            g.fillRect(0, 0, hudManager.getPWidth(), hudManager.getPHeight());

            super.render(g);
        }
    }
}