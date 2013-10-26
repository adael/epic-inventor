package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;

import java.awt.*;
import java.util.*;

public class HUDConsole extends HUD {

    private final static int MAIN_X = 10;
    private final static int MAIN_Y = 0;
    private final static int MAIN_WIDTH = 700;
    private final static int MAIN_HEIGHT = 50;
    private final static int MAX_HEIGHT = 25;
    private final static int ANIMATE_SPEED = 2;
    private boolean opening;
    private boolean closing;
    private boolean cursorShow;
    private float cursorTotalTime;
    private float CURSOR_MAX_TIME = 0.25f;
    private HUDArea mainArea;
    private String consoleText = "";

    public HUDConsole(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        name = "Console";

        shouldRender = false;

        positionY = 0 - MAX_HEIGHT;

        setImage("HUD/Console/BG");

        mainArea = addArea(MAIN_X, MAIN_Y, MAIN_WIDTH, MAIN_HEIGHT, "main");
        mainArea.setFont("SansSerif", Font.PLAIN, 18);
        mainArea.setText("> ");
    }

    @Override
    public void consoleKey(int k, Character c) {
        //pressed a letter, number or space
        if ((k >= 65 && k <= 90) //a-z
                || (k >= 48 && k <= 57) //0-9
                || (k >= 96 && k <= 105) //0-9
                || k == 32 //space
                || k == 46 //period
                || k == 110 //period
                ) {
            consoleText += c;
        }

        //pressed the backspace
        if (k == 8) {
            if (!consoleText.isEmpty()) {
                consoleText = consoleText.substring(0, consoleText.length() - 1);
            }
        }

        //pressed the enter key
        if (k == 10) {
            keyEnterPressed();
        }
    }

    @Override
    public void keyEnterPressed() {
        if (!Game.RELEASE) {
            if (shouldRender) {
                if (consoleText.isEmpty()) {
                    opening = false;
                    closing = true;
                } else {
                    hudManager.processConsoleCommand(consoleText);
                    opening = false;
                    closing = false;
                    shouldRender = false;
                    consoleText = "";
                    positionY = 0 - MAX_HEIGHT;
                }
            } else {
                shouldRender = true;
                closing = false;
                opening = true;
            }
        }
    }

    @Override
    public void update() {
        super.update();

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

            if (cursorShow) {
                mainArea.setText("> " + consoleText + "_");
            } else {
                mainArea.setText("> " + consoleText);
            }

            if (opening) {
                positionY += ANIMATE_SPEED;
                if (positionY >= 0) {
                    positionY = 0;
                    opening = false;
                }
            }

            if (closing) {
                positionY -= ANIMATE_SPEED;
                if (positionY <= (0 - MAX_HEIGHT)) {
                    positionY = 0 - MAX_HEIGHT;
                    closing = false;
                    shouldRender = false;
                    consoleText = "";
                }
            }
        }
    }
}