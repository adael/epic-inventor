package com.weem.epicinventor.world.background;

import com.weem.epicinventor.*;

import java.awt.*;
import java.awt.image.*;

public class Background {

    protected Registry registry;
    protected BackgroundManager backgroundManager;
    protected float mapX, mapY;
    protected int width, height;
    protected float moveSpeed;
    protected BufferedImage image;

    public Background(BackgroundManager bm, Registry rg, String im, int x, int y, float ms) {
        backgroundManager = bm;
        registry = rg;

        mapX = x;
        mapY = y;
        moveSpeed = ms;

        image = registry.getImageLoader().getImage(im);

        width = image.getWidth();
        height = image.getHeight();
    }

    public void update() {
        mapX += moveSpeed;
        if (mapX > backgroundManager.getMapWidth()) {
            mapX = 0 - width;
        }
    }

    public void render(Graphics g) {
        if (image != null) {
            int xPos = backgroundManager.mapToPanelX((int) mapX);
            int yPos = backgroundManager.mapToPanelY((int) mapY);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = backgroundManager.getPHeight() - yPos;

            //subtract the height since points are bottom left and drawing starts from top left
            yPos -= height;

            g.drawImage(image, xPos, yPos, null);
        }
    }
}
