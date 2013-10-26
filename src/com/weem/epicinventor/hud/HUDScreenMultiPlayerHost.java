package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;

import java.awt.*;

public class HUDScreenMultiPlayerHost extends HUD {

    private final static int PORT_WIDTH = 372;
    private final static int PORT_HEIGHT = 52;
    private final static int PORT_X = 210;
    private final static int PORT_Y = 190;
    private final static int PORT_TEXT_X = 25;
    private final static int PORT_TEXT_Y = 35;
    private final static int PORT_MAX_CHARS = 5;
    private final static int BUTTON_BACK_WIDTH = 146;
    private final static int BUTTON_BACK_HEIGHT = 40;
    private final static int BUTTON_BACK_X = 216;
    private final static int BUTTON_BACK_Y = 468;
    private final static int BUTTON_CREATE_WIDTH = 146;
    private final static int BUTTON_CREATE_HEIGHT = 40;
    private final static int BUTTON_CREATE_X = 430;
    private final static int BUTTON_CREATE_Y = 468;
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
    private String port = "7777";
    private String textFocus = "Port";

    public HUDScreenMultiPlayerHost(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/ScreenMultiPlayerHost/BG");

        HUDArea hudArea = null;

        //port
        hudArea = addArea(PORT_X, PORT_Y, PORT_WIDTH, PORT_HEIGHT, "port");
        hudArea.setImage("HUD/ScreenMultiPlayerHost/BGText");
        hudArea.setFont("SansSerif", Font.BOLD, 28);
        hudArea.setTextXY(PORT_TEXT_X, PORT_TEXT_Y);

        //back
        hudArea = addArea(BUTTON_BACK_X, BUTTON_BACK_Y, BUTTON_BACK_WIDTH, BUTTON_BACK_HEIGHT, "back");
        hudArea.setImage("HUD/ScreenMultiPlayerHost/ButtonBack");

        //create
        hudArea = addArea(BUTTON_CREATE_X, BUTTON_CREATE_Y, BUTTON_CREATE_WIDTH, BUTTON_CREATE_HEIGHT, "create");
        hudArea.setImage("HUD/ScreenMultiPlayerHost/ButtonCreate");
        
        //donate
        hudArea = addArea(BUTTON_DONATE_X, BUTTON_DONATE_Y, BUTTON_DONATE_WIDTH, BUTTON_DONATE_HEIGHT, "donate");
        hudArea.setImage("HUD/Common/Donate");
        
        //help
        //hudArea = addArea(BUTTON_HELP_X, BUTTON_HELP_Y, BUTTON_HELP_WIDTH, BUTTON_HELP_HEIGHT, "help");
        //hudArea.setImage("HUD/Common/Help");
    }

    @Override
    public void newCharacterKey(int k, Character c) {
        //pressed a number
        if ((k >= 48 && k <= 57) //0-9
                || (k >= 96 && k <= 105) //0-9
                ) {
            if (textFocus.equals("Port")) {
                if (port.length() < PORT_MAX_CHARS) {
                    port += c;
                }
            }
        }

        //pressed the backspace
        if (k == 8) {
            if (textFocus.equals("Port")) {
                if (!port.isEmpty()) {
                    port = port.substring(0, port.length() - 1);
                }
            }
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
                if (hudArea.getType().equals("port")) {
                    if (textFocus.equals("Port") && cursorShow) {
                        hudArea.setText(port + "_");
                    } else {
                        hudArea.setText(port);
                    }
                } else if (hudArea.getType().equals("create")) {
                    if (!port.trim().isEmpty()) {
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
                if (hudArea.getType().equals("back")) {
                    hudManager.unloadHUD(name);
                    hudManager.loadHUD(HUDManager.HUDType.ScreenMultiPlayer);
                } else if (hudArea.getType().equals("create")) {
                    hudManager.setServerPort(port);
                    hudManager.setStartServer(true);
                    hudManager.setServerJoin(false);
                    hudManager.unloadHUD(name);
                    hudManager.loadHUD(HUDManager.HUDType.ScreenCharacterSelection);
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