package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.placeable.*;

import java.awt.*;

public class HUDScreenCredits extends HUD {

    private final static int TITLE_WIDTH = 420;
    private final static int TITLE_HEIGHT = 37;
    private final static int TITLE_X = 188;
    private final static int TITLE_Y = 110;
    private final static int BUTTON_BACK_WIDTH = 146;
    private final static int BUTTON_BACK_HEIGHT = 40;
    private final static int BUTTON_BACK_X = 333;
    private final static int BUTTON_BACK_Y = 468;
    private final static int BUTTON_RED_X_WIDTH = 42;
    private final static int BUTTON_RED_X_HEIGHT = 42;
    private final static int BUTTON_RED_X_X = 733;
    private final static int BUTTON_RED_X_Y = 25;
    private final static int BUTTON_DONATE_WIDTH = 119;
    private final static int BUTTON_DONATE_HEIGHT = 102;
    private final static int BUTTON_DONATE_X = 33;
    private final static int BUTTON_DONATE_Y = 155;
    private final static int BUTTON_HELP_WIDTH = 119;
    private final static int BUTTON_HELP_HEIGHT = 102;
    private final static int BUTTON_HELP_X = 646;
    private final static int BUTTON_HELP_Y = 155;

    public HUDScreenCredits(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/ScreenCredits/BG");

        HUDArea hudArea = null;

        //title
        hudArea = addArea(TITLE_X, TITLE_Y, TITLE_WIDTH, TITLE_HEIGHT, "title");
        hudArea.setImage("HUD/ScreenCredits/TitleCredits");

        //exit
        hudArea = addArea(BUTTON_BACK_X, BUTTON_BACK_Y, BUTTON_BACK_WIDTH, BUTTON_BACK_HEIGHT, "back");
        hudArea.setImage("HUD/ScreenCredits/ButtonBack");
        
        //donate
        hudArea = addArea(BUTTON_DONATE_X, BUTTON_DONATE_Y, BUTTON_DONATE_WIDTH, BUTTON_DONATE_HEIGHT, "donate");
        hudArea.setImage("HUD/Common/Donate");
        
        //help
        //hudArea = addArea(BUTTON_HELP_X, BUTTON_HELP_Y, BUTTON_HELP_WIDTH, BUTTON_HELP_HEIGHT, "help");
        //hudArea.setImage("HUD/Common/Help");
    }

    @Override
    public void HUDAreaClicked(HUDArea ha) {
        HUDArea hudArea = null;

        for (int i = 0; i < hudAreas.size(); i++) {
            hudArea = hudAreas.get(i);
            if (hudArea == ha) {
                if (hudArea.getType().equals("back")) {
                    hudManager.unloadHUD(name);
                    hudManager.loadHUD(HUDManager.HUDType.ScreenMain);
                } else if (hudArea.getType().equals("red_x")) {
                    hudManager.gameExit();
                } else if (hudArea.getType().equals("donate")) {
                    String url = "http://epicinventor.com/donate.php";

                    try {
                        Desktop.getDesktop().browse(java.net.URI.create(url));
                    } catch (Exception e) {
                    }
                } else if (hudArea.getType().equals("help")) {
                    String url = "http://epicinventor.com/help";

                    try {
                        Desktop.getDesktop().browse(java.net.URI.create(url));
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
}