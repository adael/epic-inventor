package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;

import java.awt.*;

public class HUDScreenMultiPlayer extends HUD {

    private final static int BUTTON_JOIN_WIDTH = 220;
    private final static int BUTTON_JOIN_HEIGHT = 40;
    private final static int BUTTON_JOIN_X = 296;
    private final static int BUTTON_JOIN_Y = 328;
    private final static int BUTTON_HOST_WIDTH = 220;
    private final static int BUTTON_HOST_HEIGHT = 40;
    private final static int BUTTON_HOST_X = 296;
    private final static int BUTTON_HOST_Y = 378;
    private final static int BUTTON_BACK_WIDTH = 146;
    private final static int BUTTON_BACK_HEIGHT = 40;
    private final static int BUTTON_BACK_X = 333;
    private final static int BUTTON_BACK_Y = 468;
    private final static int BUTTON_DONATE_WIDTH = 119;
    private final static int BUTTON_DONATE_HEIGHT = 102;
    private final static int BUTTON_DONATE_X = 33;
    private final static int BUTTON_DONATE_Y = 155;
    private final static int BUTTON_HELP_WIDTH = 119;
    private final static int BUTTON_HELP_HEIGHT = 102;
    private final static int BUTTON_HELP_X = 646;
    private final static int BUTTON_HELP_Y = 155;

    public HUDScreenMultiPlayer(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/ScreenMultiPlayer/BG");

        HUDArea hudArea = null;

        //join
        hudArea = addArea(BUTTON_JOIN_X, BUTTON_JOIN_Y, BUTTON_JOIN_WIDTH, BUTTON_JOIN_HEIGHT, "join");
        hudArea.setImage("HUD/ScreenMultiPlayer/ButtonJoin");

        //host
        hudArea = addArea(BUTTON_HOST_X, BUTTON_HOST_Y, BUTTON_HOST_WIDTH, BUTTON_HOST_HEIGHT, "host");
        hudArea.setImage("HUD/ScreenMultiPlayer/ButtonHost");

        //back
        hudArea = addArea(BUTTON_BACK_X, BUTTON_BACK_Y, BUTTON_BACK_WIDTH, BUTTON_BACK_HEIGHT, "back");
        hudArea.setImage("HUD/ScreenMultiPlayer/ButtonBack");
        
        //donate
        hudArea = addArea(BUTTON_DONATE_X, BUTTON_DONATE_Y, BUTTON_DONATE_WIDTH, BUTTON_DONATE_HEIGHT, "donate");
        hudArea.setImage("HUD/Common/Donate");
        
        //help
        //hudArea = addArea(BUTTON_HELP_X, BUTTON_HELP_Y, BUTTON_HELP_WIDTH, BUTTON_HELP_HEIGHT, "help");
        //hudArea.setImage("HUD/Common/Help");
        
        hudManager.setStartServer(false);
        hudManager.setServerJoin(false);
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
                } else if (hudArea.getType().equals("join")) {
                    hudManager.unloadHUD(name);
                    hudManager.loadHUD(HUDManager.HUDType.ScreenMultiPlayerJoin);
                } else if (hudArea.getType().equals("host")) {
                    hudManager.unloadHUD(name);
                    hudManager.loadHUD(HUDManager.HUDType.ScreenMultiPlayerHost);
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