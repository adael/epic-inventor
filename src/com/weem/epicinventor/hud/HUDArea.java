package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;

import java.awt.*;
import java.awt.image.*;

public class HUDArea {

    private HUD hud;
    private Registry registry;
    private int positionX, positionY, width, height; //relative to HUD inside
    private String type = "";
    private String text = "";
    private BufferedImage bgImage;
    private BufferedImage fgImage;
    private int fgWidth, fgHeight;
    protected boolean border;
    protected Color borderColor;
    private Font textFont;
    private Color textColor;
    private int textX, textY = 0;
    private boolean isActive = true;

    public HUDArea(HUD hd, Registry rg, int x, int y, int w, int h) {
        hud = hd;
        registry = rg;
        positionX = x;
        positionY = y;
        width = w;
        height = h;
        fgHeight = -1;
        fgWidth = -1;
    }

    public HUDArea(HUD hd, Registry rg, int x, int y, int w, int h, String t) {
        hud = hd;
        registry = rg;
        positionX = x;
        positionY = y;
        width = w;
        height = h;
        type = t;
        fgHeight = -1;
        fgWidth = -1;
    }

    public String getType() {
        return type;
    }

    public BufferedImage getFGImage() {
        return fgImage;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setImage(String imageName) {
        bgImage = registry.getImageLoader().getImage(imageName);
    }

    public void setImage(String imageName, int frame) {
        bgImage = registry.getImageLoader().getImage(imageName, frame);
    }

    public void setFGImage(String imageName) {
        if (imageName.equals("")) {
            fgImage = null;
            fgHeight = -1;
            fgWidth = -1;
        } else {
            fgImage = registry.getImageLoader().getImage(imageName);
            if (fgImage != null) {
                fgHeight = fgImage.getHeight();
                fgWidth = fgImage.getWidth();
            }
        }
    }

    public void setFGImage(BufferedImage image) {
        fgImage = image;
        if (image == null) {
            fgHeight = -1;
            fgWidth = -1;
        } else {
            fgHeight = fgImage.getHeight();
            fgWidth = fgImage.getWidth();
        }
    }

    public void setFont(String fontName, int fontType, int fontSize) {
        textFont = new Font(fontName, fontType, fontSize);
    }

    public void setIsActive(boolean a) {
        isActive = a;
    }

    public void setWidth(int w) {
        width = w;
    }

    public void setTextColor(Color c) {
        textColor = c;
    }

    public void setText(String s) {
        text = s;
    }

    public void setTextXY(int x, int y) {
        textX = x;
        textY = y;
    }

    public void setXY(int x, int y) {
        positionX = x;
        positionY = y;
    }

    public void setBorder(Color c) {
        border = true;
        borderColor = c;
    }

    public void removeBorder() {
        border = false;
        borderColor = null;
    }

    public boolean handleClick(Point clickPoint) {
        if (isInside(clickPoint)) {
            return true;
        }

        return false;
    }

    public boolean handleRightClick(Point clickPoint) {
        if (isInside(clickPoint)) {
            return true;
        }

        return false;
    }

    public boolean handleReleased(Point clickPoint) {
        if (isInside(clickPoint)) {
            return true;
        }

        return false;
    }

    public String getText(){
        return text;
    }

    public void update() {
    }

    public void render(Graphics g) {
        if (isActive) {
            if (bgImage != null) {
                g.drawImage(bgImage,
                        hud.getLocationX() + positionX,
                        hud.getLocationY() + positionY,
                        width, height,
                        null);
            }

            if (fgImage != null) {
                g.drawImage(fgImage,
                        hud.getLocationX() + positionX + (width - fgWidth) / 2,
                        hud.getLocationY() + positionY + (height - fgHeight) / 2,
                        fgWidth, fgHeight,
                        null);
            }

            if (border) {
                g.setColor(borderColor);
                g.drawRect(hud.getLocationX() + positionX,
                        hud.getLocationY() + positionY,
                        width,
                        height);
            }

            if (textFont != null && !text.isEmpty()) {
                if(textColor != null) {
                    g.setColor(textColor);
                } else {
                    g.setColor(Color.white);
                }
                g.setFont(textFont);
                if (textY > 0) {
                    g.drawString(text,
                            hud.getLocationX() + positionX + textX,
                            hud.getLocationY() + positionY + textY);
                } else {
                    g.drawString(text,
                            hud.getLocationX() + positionX + textX,
                            hud.getLocationY() + positionY + textFont.getSize());
                }
            }
        }
    }

    public boolean isInside(Point p) {
        if (isActive && p != null) {
            if (p.x >= hud.getLocationX() + positionX
                    && p.x <= (hud.getLocationX() + positionX + width)
                    && p.y >= hud.getLocationY() + positionY
                    && p.y <= (hud.getLocationY() + positionY + height)) {
                return true;
            }
        }

        return false;
    }
}