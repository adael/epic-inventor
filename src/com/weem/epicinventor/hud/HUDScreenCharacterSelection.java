package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;

import java.awt.*;
import java.util.ArrayList;

public class HUDScreenCharacterSelection extends HUD {

    private final static int TITLE_WIDTH = 420;
    private final static int TITLE_HEIGHT = 37;
    private final static int TITLE_X = 188;
    private final static int TITLE_Y = 110;
    private final static int BUTTON_RED_X_WIDTH = 42;
    private final static int BUTTON_RED_X_HEIGHT = 42;
    private final static int BUTTON_RED_X_X = 733;
    private final static int BUTTON_RED_X_Y = 25;
    private final static int CHARACTER_NAME_X = 211;
    private final static int CHARACTER_NAME_STARY_Y = 168;
    private final static int CHARACTER_NAME_SPACING = 60;
    private final static int CHARACTER_NAME_WIDTH = 372;
    private final static int CHARACTER_NAME_HEIGHT = 52;
    private final static int CHARACTER_NAME_TEXT_X = 25;
    private final static int CHARACTER_NAME_TEXT_Y = 35;
    private final static int CHARACTER_DELETE_X = 542;
    private final static int CHARACTER_DELETE_STARY_Y = 179;
    private final static int CHARACTER_DELETE_SPACING = 60;
    private final static int CHARACTER_DELETE_WIDTH = 30;
    private final static int CHARACTER_DELETE_HEIGHT = 30;
    private final static int BACK_WIDTH = 146;
    private final static int BACK_HEIGHT = 40;
    private final static int BACK_X = 217;
    private final static int BACK_Y = 469;
    private final static int NEW_WIDTH = 181;
    private final static int NEW_HEIGHT = 40;
    private final static int NEW_X = 395;
    private final static int NEW_Y = 468;
    private final static int BUTTON_DONATE_WIDTH = 119;
    private final static int BUTTON_DONATE_HEIGHT = 102;
    private final static int BUTTON_DONATE_X = 33;
    private final static int BUTTON_DONATE_Y = 155;
    private final static int BUTTON_HELP_WIDTH = 119;
    private final static int BUTTON_HELP_HEIGHT = 102;
    private final static int BUTTON_HELP_X = 646;
    private final static int BUTTON_HELP_Y = 155;

    public HUDScreenCharacterSelection(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/ScreenCharacterSelection/BG");

        HUDArea hudArea = null;

        //title
        hudArea = addArea(TITLE_X, TITLE_Y, TITLE_WIDTH, TITLE_HEIGHT, "title");
        hudArea.setImage("HUD/ScreenCharacterSelection/TitleCharacterSelection");

        //back
        hudArea = addArea(BACK_X, BACK_Y, BACK_WIDTH, BACK_HEIGHT, "back");
        hudArea.setImage("HUD/ScreenCharacterSelection/ButtonBack");

        //new
        hudArea = addArea(NEW_X, NEW_Y, NEW_WIDTH, NEW_HEIGHT, "new");
        hudArea.setImage("HUD/ScreenCharacterSelection/ButtonNew");

        //red x
        //hudArea = addArea(BUTTON_RED_X_X, BUTTON_RED_X_Y, BUTTON_RED_X_WIDTH, BUTTON_RED_X_HEIGHT, "red_x");
        //hudArea.setImage("HUD/ScreenCharacterSelection/Exit");

        //characters
        for (int i = 0; i < 4; i++) {
            hudArea = addArea(CHARACTER_NAME_X, (CHARACTER_NAME_SPACING * i) + CHARACTER_NAME_STARY_Y, CHARACTER_NAME_WIDTH, CHARACTER_NAME_HEIGHT, "character_name" + (i + 1));
            hudArea.setImage("HUD/ScreenCharacterSelection/BGText");
            hudArea.setFont("SansSerif", Font.BOLD, 28);
            hudArea.setTextXY(CHARACTER_NAME_TEXT_X, CHARACTER_NAME_TEXT_Y);
        }

        //characters delete
        for (int i = 0; i < 4; i++) {
            hudArea = addArea(CHARACTER_DELETE_X, (CHARACTER_DELETE_SPACING * i) + CHARACTER_DELETE_STARY_Y, CHARACTER_DELETE_WIDTH, CHARACTER_DELETE_HEIGHT, "character_delete" + (i + 1));
            hudArea.setImage("HUD/ScreenCharacterSelection/ButtonDelete");
        }
        
        //donate
        hudArea = addArea(BUTTON_DONATE_X, BUTTON_DONATE_Y, BUTTON_DONATE_WIDTH, BUTTON_DONATE_HEIGHT, "donate");
        hudArea.setImage("HUD/Common/Donate");
        
        //help
        //hudArea = addArea(BUTTON_HELP_X, BUTTON_HELP_Y, BUTTON_HELP_WIDTH, BUTTON_HELP_HEIGHT, "help");
        //hudArea.setImage("HUD/Common/Help");
    }

    @Override
    public void update() {
        if (shouldRender) {
            HUDArea hudArea;

            ArrayList players = Settings.getPlayerList();
            String playerName = "";
            
            //see if we have players
            int playerCount = 0;
            for (int i = 0; i < players.size(); i++) {
                String p = (String)players.get(i);
                if (p != null && !p.isEmpty()) {
                    playerCount++;
                }
            }

            //update slots
            int j = 0;
            for (int i = 0; i < hudAreas.size(); i++) {
                hudArea = hudAreas.get(i);
                
                if(hudArea.getType().equals("character_name"+(j+1))) {
                    if(j < players.size()) {
                        playerName = (String)players.get(j);
                        if(playerName != null) {
                            hudArea.setText(playerName);
                        }
                    }
                    j++;
                } else if (hudArea.getType().equals("new")) {
                    if(playerCount < 4) {
                        hudArea.setIsActive(true);
                    } else {
                        hudArea.setIsActive(false);
                    }
                }
            }

            if (playerCount == 0) {
                hudManager.unloadHUD(name);
                hudManager.loadHUD(HUDManager.HUDType.ScreenNewCharacter);
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
                if (hudArea.getType().equals("red_x")) {
                    hudManager.gameExit();
                } else if (hudArea.getType().equals("back")) {
                    hudManager.unloadHUD(name);
                    hudManager.loadHUD(HUDManager.HUDType.ScreenMain);
                } else if (hudArea.getType().equals("new")) {
                    hudManager.unloadHUD(name);
                    hudManager.loadHUD(HUDManager.HUDType.ScreenNewCharacter);
                } else if (hudArea.getType().equals("character_name1")) {
                    //clicked
                    loadPlayer(hudArea, 0);
                } else if (hudArea.getType().equals("character_name2")) {
                    //clicked
                    loadPlayer(hudArea, 1);
                } else if (hudArea.getType().equals("character_name3")) {
                    //clicked
                    loadPlayer(hudArea, 2);
                } else if (hudArea.getType().equals("character_name4")) {
                    //clicked
                    loadPlayer(hudArea, 3);
                } else if (hudArea.getType().equals("character_delete1")) {
                    //clicked
                    Settings.deletePlayer(0);
                    Settings.setPlayer(0, null);
                } else if (hudArea.getType().equals("character_delete2")) {
                    //clicked
                    Settings.deletePlayer(1);
                    Settings.setPlayer(1, null);
                } else if (hudArea.getType().equals("character_delete3")) {
                    //clicked
                    Settings.deletePlayer(2);
                    Settings.setPlayer(2, null);
                } else if (hudArea.getType().equals("character_delete4")) {
                    //clicked
                    Settings.deletePlayer(3);
                    Settings.setPlayer(3, null);
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

    private void loadPlayer(HUDArea hudArea, int i) {
        if(hudArea.getText().length() > 0) {
            Settings.player = i;
            hudManager.loadPlayer();
            hudManager.unloadHUD(name);
            hudManager.resetGame();
        }
    }
}