package com.weem.epicinventor.indicator;

import com.weem.epicinventor.*;

import java.awt.*;
import java.awt.image.*;

public class Indicator {

    protected Registry registry;
    private IndicatorManager indicatorManager;
    private String imageName;
    private String text;
    private Font textFont;
    private Color textColor;
    private int mapX, mapY;
    private int startX;
    private int startY, endY;
    private boolean isDirty;
    private int steps = 0;
    private int maxSteps = 200;
    private int maxHeight = 300;
    private float width, height;

    public Indicator(Registry r, IndicatorManager im, int x, int y, String in, String t, Color c, boolean showLong) {
        registry = r;
        indicatorManager = im;
        mapX = x;
        mapY = y;
        imageName = in;
        text = t;
        textColor = c;

        startX = mapX;
        startY = mapY;

        if (!imageName.isEmpty()) {
            width = registry.getImageLoader().getImage(imageName).getWidth();
            height = registry.getImageLoader().getImage(imageName).getHeight();
        }

        if (!text.isEmpty()) {
            if (showLong) {
                maxHeight = 100;
                maxSteps = 75;
            } else {
                maxHeight = 20;
                maxSteps = 15;
            }
            textFont = new Font("SansSerif", Font.BOLD, 18);
        }

        endY = mapY + maxHeight;
        steps = maxSteps;
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void destroy() {
        isDirty = true;
    }

    public void update() {
        steps--;

        float newX, newY;
        float percentage = ((float) steps / (float) maxSteps);

        width = width * percentage;
        height = height * percentage;
        newX = startX - ((int) width / 2);
        newY = startY + (endY - startY) - ((endY - startY) * percentage);

        mapX = (int) newX;
        mapY = (int) newY;

        if (steps <= 0 || mapY >= endY) {
            isDirty = true;
        }
    }

    public void render(Graphics g) {
        if (!isDirty) {
            int xPos = indicatorManager.mapToPanelX(mapX);
            int yPos = indicatorManager.mapToPanelY(mapY);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = indicatorManager.getPHeight() - yPos;

            //subtract the block height since points are bottom left and drawing starts from top left
            yPos -= indicatorManager.getBlockHeight();

            BufferedImage bi = registry.getImageLoader().getImage(imageName);

            if (bi != null) {
                g.drawImage(bi, xPos, yPos, (int) width, (int) height, null);
            }

            if (textFont != null && !text.isEmpty()) {
                g.setFont(textFont);

                registry.ghettoOutline(g, Color.BLACK, text, xPos, yPos + textFont.getSize());

                g.setColor(textColor);
                g.drawString(text, xPos, yPos + textFont.getSize());
            }
        }
    }
}