package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.placeable.*;

import java.awt.*;
import java.io.*;

public class HUDScreenMain extends HUD {

    private final static int BUTTON_WIDTH = 232;
    private final static int BUTTON_HEIGHT = 52;
    private final static int BUTTON_LARGE_WIDTH = 172;
    private final static int BUTTON_LARGE_HEIGHT = 101;
    private final static int BUTTON_SINGLE_PLAYER_X = 219;
    private final static int BUTTON_SINGLE_PLAYER_Y = 143;
    private final static int BUTTON_MULTI_PLAYER_X = 407;
    private final static int BUTTON_MULTI_PLAYER_Y = 143;
    private final static int BUTTON_SETTINGS_X = 290;
    private final static int BUTTON_SETTINGS_Y = 338;
    private final static int BUTTON_CREDITS_X = 290;
    private final static int BUTTON_CREDITS_Y = 350;
    private final static int BUTTON_EXIT_X = 290;
    private final static int BUTTON_EXIT_Y = 412;
    private final static int BUTTON_DONATE_WIDTH = 119;
    private final static int BUTTON_DONATE_HEIGHT = 102;
    private final static int BUTTON_DONATE_X = 33;
    private final static int BUTTON_DONATE_Y = 155;
    private final static int BUTTON_HELP_WIDTH = 119;
    private final static int BUTTON_HELP_HEIGHT = 102;
    private final static int BUTTON_HELP_X = 646;
    private final static int BUTTON_HELP_Y = 155;
    private final static int BUTTON_DOWNLOAD_WIDTH = 127;
    private final static int BUTTON_DOWNLOAD_HEIGHT = 128;
    private final static int BUTTON_DOWNLOAD_X = 580;
    private final static int BUTTON_DOWNLOAD_Y = 9;

    public HUDScreenMain(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/ScreenMain/BG");

        HUDArea hudArea = null;

        //single player
        hudArea = addArea(BUTTON_SINGLE_PLAYER_X, BUTTON_SINGLE_PLAYER_Y, BUTTON_LARGE_WIDTH, BUTTON_LARGE_HEIGHT, "single_player");
        hudArea.setImage("HUD/ScreenMain/ButtonSinglePlayer");

        //multi player
        hudArea = addArea(BUTTON_MULTI_PLAYER_X, BUTTON_MULTI_PLAYER_Y, BUTTON_LARGE_WIDTH, BUTTON_LARGE_HEIGHT, "multi_player");
        if (hudManager.getIsOnline() && Game.VERSION.equals(hudManager.getCurrentVersion())) {
            hudArea.setImage("HUD/ScreenMain/ButtonMultiPlayer");
        } else {
            hudArea.setImage("HUD/ScreenMain/ButtonMultiPlayerOff");
        }

        //settings
        hudArea = addArea(BUTTON_SETTINGS_X, BUTTON_SETTINGS_Y, BUTTON_WIDTH, BUTTON_HEIGHT, "settings");
        hudArea.setImage("HUD/ScreenMain/ButtonSettings");

        //credits
        //hudArea = addArea(BUTTON_CREDITS_X, BUTTON_CREDITS_Y, BUTTON_WIDTH, BUTTON_HEIGHT, "credits");
        //hudArea.setImage("HUD/ScreenMain/ButtonCredits");

        //exit
        hudArea = addArea(BUTTON_EXIT_X, BUTTON_EXIT_Y, BUTTON_WIDTH, BUTTON_HEIGHT, "exit");
        hudArea.setImage("HUD/ScreenMain/ButtonExit");

        //donate
        hudArea = addArea(BUTTON_DONATE_X, BUTTON_DONATE_Y, BUTTON_DONATE_WIDTH, BUTTON_DONATE_HEIGHT, "donate");
        hudArea.setImage("HUD/Common/Donate");

        //help
        //hudArea = addArea(BUTTON_HELP_X, BUTTON_HELP_Y, BUTTON_HELP_WIDTH, BUTTON_HELP_HEIGHT, "help");
        //hudArea.setImage("HUD/Common/Help");

        if (hudManager.getIsOnline() && !Game.VERSION.equals(hudManager.getCurrentVersion())) {
            //download
            hudArea = addArea(BUTTON_DOWNLOAD_X, BUTTON_DOWNLOAD_Y, BUTTON_DOWNLOAD_WIDTH, BUTTON_DOWNLOAD_HEIGHT, "download");
            hudArea.setImage("HUD/ScreenMain/NewVersion");
        }

        hudManager.setStartServer(false);
        hudManager.setServerJoin(false);
    }

    @Override
    public void HUDAreaClicked(HUDArea ha) {
        HUDArea hudArea = null;

        for (int i = 0; i < hudAreas.size(); i++) {
            hudArea = hudAreas.get(i);
            if (hudArea == ha) {
                if (hudArea.getType().equals("single_player")) {
                    hudManager.unloadHUD(name);
                    hudManager.loadHUD(HUDManager.HUDType.ScreenCharacterSelection);
                } else if (hudArea.getType().equals("multi_player")) {
                    if (hudManager.getIsOnline() && Game.VERSION.equals(hudManager.getCurrentVersion())) {
                        hudManager.unloadHUD(name);
                        hudManager.loadHUD(HUDManager.HUDType.ScreenMultiPlayer);
                    } else {
                        registry.showMessage("Error", "Must be online and have latest version to play.  Try updating and restarting.");
                    }
                } else if (hudArea.getType().equals("settings")) {
                    hudManager.unloadHUD(name);
                    hudManager.loadHUD(HUDManager.HUDType.ScreenSettings);
                } else if (hudArea.getType().equals("exit")) {
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
                } else if (hudArea.getType().equals("download")) {
                    Process p = null;
                    try {
                        p = Runtime.getRuntime().exec("EpicInventorUpdater");
                    } catch (IOException ex) {
                    }
                    
                    if (p == null) {
                        registry.showMessage("Error", "Could not launch auto-updater, run manually from folder");
                    } else {
                        hudManager.gameExit();
                    }
                }
            }
        }
    }
}