package com.weem.epicinventor.utility;

import com.weem.epicinventor.*;

import java.awt.*;
import java.awt.image.*;

public class HPBar {
    
    private static int CONTAINER_WIDTH = 22;
    private static int CONTAINER_HEIGHT = 4;
    private static int BAR_HEIGHT = 2;
    private static int BAR_MAX_WIDTH = 20;
    private static int BAR_OFFSET_X = 1;
    private static int BAR_OFFSET_Y = 1;

    private HPBar() {
    }
    
    public static int getWidth() {
        return CONTAINER_WIDTH;
    }
    
    public static int getHeight() {
        return CONTAINER_HEIGHT;
    }

    public static void displayHP(Graphics g, GameController gc, ImageLoader il, int x, int y, int progress) {
        int xPos, yPos;
        int progressWidth;
        BufferedImage bi;
        
        x = x - (CONTAINER_WIDTH / 2);
        y = y + CONTAINER_HEIGHT;
        
        //container
        bi = il.getImage("Misc/BarBG");
        if(bi != null) {
            xPos = gc.mapToPanelX(x);
            yPos = gc.mapToPanelY(y);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = gc.getPHeight() - yPos;

            //subtract the block height since points are bottom left and drawing starts from top left
            yPos -= gc.getBlockHeight();

            g.drawImage(bi, xPos, yPos, null);
        }
        
        //ar
        bi = il.getImage("Misc/Bar");
        if(bi != null) {
            if(progress < 0) {
                progress = 0;
            }
            if(progress > 100) {
                progress = 100;
            }
            progressWidth = (BAR_MAX_WIDTH * progress) / 100;
            
            xPos = gc.mapToPanelX(x + BAR_OFFSET_X);
            yPos = gc.mapToPanelY(y - BAR_OFFSET_Y);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = gc.getPHeight() - yPos;

            //subtract the block height since points are bottom left and drawing starts from top left
            yPos -= gc.getBlockHeight();

            g.drawImage(bi, xPos, yPos, progressWidth, BAR_HEIGHT, null);
        }
    }
}