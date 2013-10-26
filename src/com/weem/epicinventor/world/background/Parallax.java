package com.weem.epicinventor.world.background;

import com.weem.epicinventor.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;
import java.awt.image.*;

public class Parallax {

    protected Registry registry;
    protected BackgroundManager backgroundManager;
    protected float mapX, mapY, initialMapX, initialMapY, initialCamX, initialCamY, currentCamX, currentCamY;
    protected int width, height, initialYPos;
    protected float moveSpeed;
    protected float velocity, velocityOffset;
    protected BufferedImage image;
    protected String type, imageName;

    public Parallax(BackgroundManager bm, Registry rg, String im, String t, int x, int y, int px, int py, float ms, float v) {
        backgroundManager = bm;
        registry = rg;

        type = t;
        mapX = initialMapX = x;
        mapY = initialMapY = y;
        currentCamX = currentCamY = 0;
        initialYPos = -99999999;
        initialCamX = px;
        initialCamY = py;
        moveSpeed = ms;
        velocity = v;
        
        imageName = im;
        image = registry.getImageLoader().getImage(im);
        
        if(image != null) {
            width = image.getWidth();
            height = image.getHeight();
        }
    }

    public void updateMapX(int x) {
        currentCamX = x;
        mapX = (currentCamX - initialCamX)*moveSpeed + initialMapX + velocityOffset;
    }

    public void updateMapY(int y) {
        currentCamY = y;
        mapY = (currentCamY - initialCamY)*moveSpeed/3 + initialMapY;
    }

    public void update() {
        velocityOffset += velocity;
        int maxOffset = (int)((1 - moveSpeed) * (float)backgroundManager.getMapWidth()) + width * 2;
        if (velocityOffset > maxOffset) {
            velocityOffset = 0 - (maxOffset + width * 2);
        }
    }

    public void render(Graphics g) {
        if (image != null) {
            int xPos = backgroundManager.mapToPanelX((int) mapX);
            int yPos = backgroundManager.mapToPanelY((int) mapY);
            if(initialYPos == -99999999) {
                initialYPos = yPos;
            }
            if(registry.getGameController().getKey(84)) {
//                int pWidth = registry.getGameController().getPWidth();
//                int pHeight = registry.getGameController().getPHeight();
//                if(currentCamX > pWidth || currentCamY - initialCamY > pHeight || currentCamY - initialCamY < -pHeight) {
//                    xPos = (int)((pWidth - initialCamX) * moveSpeed + initialMapX + velocityOffset);
//                    yPos = initialYPos;
//                }
                mapX = initialMapX + velocityOffset;
                mapY = initialMapY;
            }

            //flip the yPos since drawing happens top down versus bottom up
            yPos = backgroundManager.getPHeight() - yPos;

            //subtract the height since points are bottom left and drawing starts from top left
            yPos -= height;

            g.drawImage(image, xPos, yPos, null);
        }
    }
}
