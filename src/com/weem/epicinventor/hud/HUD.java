package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;

import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;

public abstract class HUD {

    protected HUDManager hudManager;
    protected Registry registry;
    protected int positionX, positionY, width, height;
    protected BufferedImage bgImage;
    protected boolean border;
    protected Color bgColor;
    protected Color borderColor;
    protected Color textColor;
    protected int textSize;
    protected String text;
    protected ArrayList<HUDArea> hudAreas;
    protected boolean shouldRender;
    protected String name = "";
    protected boolean isContainer = false;
    protected boolean isDirty;

    public HUD(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        hudManager = hm;
        registry = rg;
        positionX = x;
        positionY = y;
        width = w;
        height = h;

        shouldRender = true;

        hudAreas = new ArrayList<HUDArea>();
    }

    public int getLocationX() {
        return positionX;
    }

    public int getLocationY() {
        return positionY;
    }

    public boolean getShouldRender() {
        return shouldRender;
    }

    public String getName() {
        return name;
    }

    public boolean getIsContainer() {
        return isContainer;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setImage(String imageName) {
        bgImage = registry.getImageLoader().getImage(imageName);
    }

    public void setBorder(Color c) {
        border = true;
        borderColor = c;
    }

    public void setBGColor(Color c) {
        bgColor = c;
    }

    public void setTextColor(Color c) {
        textColor = c;
    }

    public void setTextSize(int s) {
        textSize = s;
    }

    public void setText(String t) {
        text = t;
    }

    public void setShouldRender(boolean sr) {
        shouldRender = sr;
    }

    public void setName(String n) {
        name = n;
    }

    public void removeBorder() {
        border = false;
        borderColor = null;
    }

    public HUDArea addArea(int x, int y, int w, int h) {
        HUDArea hudArea = new HUDArea(this, registry, x, y, w, h);
        hudAreas.add(hudArea);

        return hudArea;
    }

    public HUDArea addArea(int x, int y, int w, int h, String t) {
        HUDArea hudArea = new HUDArea(this, registry, x, y, w, h, t);
        hudAreas.add(hudArea);

        return hudArea;
    }

    public boolean handleClick(Point clickPoint) {
        if (shouldRender) {
            HUDArea hudArea = null;

            if (isInside(clickPoint)) {
                //start from the top and work our way down to "layer" the huds
                for (int i = (hudAreas.size() - 1); i >= 0; i--) {
                    hudArea = hudAreas.get(i);
                    if (hudArea.handleClick(clickPoint)) {
                        HUDAreaClicked(hudArea);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void HUDAreaClicked(HUDArea hudArea) {
        //To be overriden by specific huds to do the click event
    }

    public boolean handleRightClick(Point clickPoint) {
        if (shouldRender) {
            HUDArea hudArea = null;

            if (isInside(clickPoint)) {
                //start from the top and work our way down to "layer" the huds
                for (int i = (hudAreas.size() - 1); i >= 0; i--) {
                    hudArea = hudAreas.get(i);
                    if (hudArea.handleRightClick(clickPoint)) {
                        HUDAreaRightClicked(hudArea);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void HUDAreaRightClicked(HUDArea hudArea) {
        //To be overriden by specific huds to do the click event
    }

    public boolean handleReleased(Point clickPoint) {
        HUDArea hudArea = null;
        boolean ret = false;
        
        if(!shouldRender) {
            return false;
        }

        if (isInside(clickPoint)) {
            //start from the top and work our way down to "layer" the huds
            for (int i = (hudAreas.size() - 1); i >= 0; i--) {
                hudArea = hudAreas.get(i);
                if (hudArea.handleReleased(clickPoint)) {
                    HUDAreaReleased(hudArea);
                    ret = true;
                    break;
                }
            }
        }

        return ret;
    }

    public void HUDAreaReleased(HUDArea hudArea) {
        //To be overriden by specific huds to do the released event
    }

    public void shiftPressed() {
    }

    public void shiftRelease() {
    }

    public void toggleMasterHUD() {
    }

    public void togglePauseHUD() {
    }

    public void keyEnterPressed() {
    }

    public void consoleKey(int k, Character c) {
    }

    public void settingsKey(int k, Character c) {
    }

    public void newCharacterKey(int k, Character c) {
    }

    public void newCharacterKey(int k, Character c, boolean tab) {
    }

    public void update() {
        if (shouldRender) {
            HUDArea hudArea = null;

            for (int i = 0; i < hudAreas.size(); i++) {
                hudArea = hudAreas.get(i);
                hudArea.update();
            }
        }
    }

    public HUDArea getHUDAreaByType(String name) {
        HUDArea hudArea;

        for (int i = 0; i < hudAreas.size(); i++) {
            hudArea = hudAreas.get(i);
            if (hudArea.getType().equals(name)) {
                return hudArea;
            }
        }

        return null;
    }

    protected void hudAreaText(HUDArea ha, String t) {
        if (ha.isInside(registry.getMousePosition()) && !t.isEmpty()) {
            registry.setStatusText(t);
        }
    }

    public void render(Graphics g) {
        if (shouldRender) {
            HUDArea hudArea = null;

            if (bgColor != null) {
                g.setColor(bgColor);
                g.fillRect(positionX, positionY, width, height);
            }

            if (bgImage != null) {
                g.drawImage(bgImage, positionX, positionY, width, height, null);
            }

            if (border) {
                g.setColor(borderColor);
                g.drawRect(positionX, positionY, width, height);
            }

            if (textColor != null && text != null && textSize > 0) {
                g.setColor(textColor);
                g.setFont(new Font("SansSerif", Font.BOLD, textSize));

                //center the text
                FontMetrics fm = g.getFontMetrics();
                int messageWidth = fm.stringWidth(text);
                int messageAscent = fm.getMaxAscent();
                int messageDescent = fm.getMaxDescent();
                int messageX = (width / 2) - (messageWidth / 2);
                int messageY = (height / 2) - (messageDescent / 2) + (messageAscent / 2);

                g.drawString(text, messageX, messageY - 100);
            }

            for (int i = 0; i < hudAreas.size(); i++) {
                hudArea = hudAreas.get(i);
                hudArea.render(g);
            }
        }
    }

    private boolean isInside(Point p) {
        if (p.x >= positionX
                && p.x <= (positionX + width)
                && p.y >= positionY
                && p.y <= (positionY + height)) {
            return true;
        }

        return false;
    }
}