package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;

import java.awt.*;
import java.util.ArrayList;

public class HUDScreenNewCharacter extends HUD {

    private final static int TITLE_WIDTH = 420;
    private final static int TITLE_HEIGHT = 37;
    private final static int TITLE_X = 188;
    private final static int TITLE_Y = 110;
    private final static int LABEL_CHARACTER_NAME_WIDTH = 186;
    private final static int LABEL_CHARACTER_NAME_HEIGHT = 18;
    private final static int LABEL_CHARACTER_NAME_X = 314;
    private final static int LABEL_CHARACTER_NAME_Y = 214;
    private final static int LABEL_ROBOT_NAME_WIDTH = 146;
    private final static int LABEL_ROBOT_NAME_HEIGHT = 18;
    private final static int LABEL_ROBOT_NAME_X = 334;
    private final static int LABEL_ROBOT_NAME_Y = 314;
    private final static int CHARACTER_NAME_WIDTH = 372;
    private final static int CHARACTER_NAME_HEIGHT = 52;
    private final static int CHARACTER_NAME_X = 211;
    private final static int CHARACTER_NAME_Y = 241;
    private final static int CHARACTER_NAME_TEXT_X = 25;
    private final static int CHARACTER_NAME_TEXT_Y = 35;
    private final static int ROBOT_NAME_WIDTH = 372;
    private final static int ROBOT_NAME_HEIGHT = 52;
    private final static int ROBOT_NAME_X = 211;
    private final static int ROBOT_NAME_Y = 340;
    private final static int ROBOT_NAME_TEXT_X = 25;
    private final static int ROBOT_NAME_TEXT_Y = 35;
    private final static int BUTTON_BACK_WIDTH = 146;
    private final static int BUTTON_BACK_HEIGHT = 40;
    private final static int BUTTON_BACK_X = 217;
    private final static int BUTTON_BACK_Y = 469;
    private final static int BUTTON_CREATE_WIDTH = 146;
    private final static int BUTTON_CREATE_HEIGHT = 40;
    private final static int BUTTON_CREATE_X = 431;
    private final static int BUTTON_CREATE_Y = 469;
    private final static int BUTTON_RED_X_WIDTH = 42;
    private final static int BUTTON_RED_X_HEIGHT = 42;
    private final static int BUTTON_RED_X_X = 733;
    private final static int BUTTON_RED_X_Y = 25;
    private final static int MAX_CHARS = 15;
    private final static int BUTTON_DONATE_WIDTH = 119;
    private final static int BUTTON_DONATE_HEIGHT = 102;
    private final static int BUTTON_DONATE_X = 33;
    private final static int BUTTON_DONATE_Y = 155;
    private final static int BUTTON_HELP_WIDTH = 119;
    private final static int BUTTON_HELP_HEIGHT = 102;
    private final static int BUTTON_HELP_X = 646;
    private final static int BUTTON_HELP_Y = 155;
    private boolean cursorShow;
    private float cursorTotalTime;
    private float CURSOR_MAX_TIME = 0.25f;
    private String characterName = "";
    private String robotName = "";
    private String textFocus = "Character";

    public HUDScreenNewCharacter(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/ScreenNewCharacter/BG");

        HUDArea hudArea = null;

        //title
        hudArea = addArea(TITLE_X, TITLE_Y, TITLE_WIDTH, TITLE_HEIGHT, "title");
        hudArea.setImage("HUD/ScreenNewCharacter/TitleNewCharacter");

        //character name
        hudArea = addArea(LABEL_CHARACTER_NAME_X, LABEL_CHARACTER_NAME_Y, LABEL_CHARACTER_NAME_WIDTH, LABEL_CHARACTER_NAME_HEIGHT, "label_character_name");
        hudArea.setImage("HUD/ScreenNewCharacter/LabelCharacterName");
        hudArea = addArea(CHARACTER_NAME_X, CHARACTER_NAME_Y, CHARACTER_NAME_WIDTH, CHARACTER_NAME_HEIGHT, "character_name");
        hudArea.setImage("HUD/ScreenNewCharacter/BGText");
        hudArea.setFont("SansSerif", Font.BOLD, 28);
        hudArea.setTextXY(CHARACTER_NAME_TEXT_X, CHARACTER_NAME_TEXT_Y);

        //robot name
        hudArea = addArea(LABEL_ROBOT_NAME_X, LABEL_ROBOT_NAME_Y, LABEL_ROBOT_NAME_WIDTH, LABEL_ROBOT_NAME_HEIGHT, "label_robot_name");
        hudArea.setImage("HUD/ScreenNewCharacter/LabelRobotName");
        hudArea = addArea(ROBOT_NAME_X, ROBOT_NAME_Y, ROBOT_NAME_WIDTH, ROBOT_NAME_HEIGHT, "robot_name");
        hudArea.setImage("HUD/ScreenNewCharacter/BGText");
        hudArea.setFont("SansSerif", Font.BOLD, 28);
        hudArea.setTextXY(ROBOT_NAME_TEXT_X, ROBOT_NAME_TEXT_Y);

        //back
        hudArea = addArea(BUTTON_BACK_X, BUTTON_BACK_Y, BUTTON_BACK_WIDTH, BUTTON_BACK_HEIGHT, "back");
        hudArea.setImage("HUD/ScreenNewCharacter/ButtonBack");

        //create
        hudArea = addArea(BUTTON_CREATE_X, BUTTON_CREATE_Y, BUTTON_CREATE_WIDTH, BUTTON_CREATE_HEIGHT, "create");
        hudArea.setImage("HUD/ScreenNewCharacter/ButtonCreate");
        hudArea.setIsActive(false);

        //donate
        hudArea = addArea(BUTTON_DONATE_X, BUTTON_DONATE_Y, BUTTON_DONATE_WIDTH, BUTTON_DONATE_HEIGHT, "donate");
        hudArea.setImage("HUD/Common/Donate");

        //help
        //hudArea = addArea(BUTTON_HELP_X, BUTTON_HELP_Y, BUTTON_HELP_WIDTH, BUTTON_HELP_HEIGHT, "help");
        //hudArea.setImage("HUD/Common/Help");
    }

    @Override
    public void newCharacterKey(int k, Character c) {
        newCharacterKey(k, c, false);
    }

    @Override
    public void newCharacterKey(int k, Character c, boolean tab) {
        if (tab) {
            textTab();
        } else {
            //pressed a letter, number or space
            if ((k >= 65 && k <= 90) //a-z
                    || (k >= 48 && k <= 57) //0-9
                    || (k >= 96 && k <= 105) //0-9
                    || k == 32 //space
                    || k == 46 //period
                    || k == 110 //period
                    ) {
                if (textFocus.equals("Character")) {
                    if (characterName.length() < MAX_CHARS) {
                        characterName += c;
                    }
                } else {
                    if (robotName.length() < MAX_CHARS) {
                        robotName += c;
                    }
                }
            }

            //pressed the backspace
            if (k == 8) {
                if (textFocus.equals("Character")) {
                    if (!characterName.isEmpty()) {
                        characterName = characterName.substring(0, characterName.length() - 1);
                    }
                } else {
                    if (!robotName.isEmpty()) {
                        robotName = robotName.substring(0, robotName.length() - 1);
                    }
                }
            }

            //pressed the enter key
            if (k == 10) {
                textTab();
            }
        }
    }

    private void textTab() {
        if (textFocus.equals("Character")) {
            textFocus = "Robot";
        } else {
            textFocus = "Character";
        }
    }

    @Override
    public void update() {
        if (shouldRender) {
            //make the cursor flash
            long p = registry.getImageLoader().getPeriod();
            cursorTotalTime = (cursorTotalTime
                    + registry.getImageLoader().getPeriod())
                    % (long) (1000 * CURSOR_MAX_TIME * 2);

            if ((cursorTotalTime / (CURSOR_MAX_TIME * 1000)) > 1) {
                cursorTotalTime = 0;
                cursorShow = !cursorShow;
            }

            HUDArea hudArea;

            for (int i = 0; i < hudAreas.size(); i++) {
                hudArea = hudAreas.get(i);
                if (hudArea.getType().equals("character_name")) {
                    if (textFocus.equals("Character") && cursorShow) {
                        hudArea.setText(characterName + "_");
                    } else {
                        hudArea.setText(characterName);
                    }
                } else if (hudArea.getType().equals("robot_name")) {
                    if (textFocus.equals("Robot") && cursorShow) {
                        hudArea.setText(robotName + "_");
                    } else {
                        hudArea.setText(robotName);
                    }
                } else if (hudArea.getType().equals("create")) {
                    if (!characterName.trim().isEmpty() && !robotName.trim().isEmpty()) {
                        hudArea.setIsActive(true);
                    } else {
                        hudArea.setIsActive(false);
                    }
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
            if (hudArea == ha) {
                if (hudArea.getType().equals("character_name")) {
                    textFocus = "Character";
                } else if (hudArea.getType().equals("robot_name")) {
                    textFocus = "Robot";
                } else if (hudArea.getType().equals("create")) {
                    ArrayList players = Settings.getPlayerList();
                    for (int j = 0; j < players.size(); j++) {
                        if (Settings.getPlayers().get(j) == null) {
                            Settings.player = j;
                            break;
                        }
                    }
                    hudManager.setNames(characterName, robotName);
                    hudManager.unloadHUD(name);
                    hudManager.resetGame(true);
                } else if (hudArea.getType().equals("back")) {
                    //see if we have players
                    boolean playerFound = false;
                    ArrayList players = Settings.getPlayerList();
                    for (int x = 0; x < players.size(); x++) {
                        if (!players.get(x).toString().isEmpty()) {
                            playerFound = true;
                        }
                    }

                    if (!playerFound) {
                        hudManager.unloadHUD(name);
                        hudManager.loadHUD(HUDManager.HUDType.ScreenMain);
                    } else {
                        hudManager.unloadHUD(name);
                        hudManager.loadHUD(HUDManager.HUDType.ScreenCharacterSelection);
                    }
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