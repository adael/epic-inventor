package com.weem.epicinventor.utility;

import com.weem.epicinventor.*;

import java.awt.*;
import java.awt.image.*;

public class ProgressBar {
    
    private static int PROGRESS_CONTAINER_WIDTH = 66;
    private static int PROGRESS_CONTAINER_HEIGHT = 31;
    private static int PROGRESS_BAR_WIDTH = 50;
    private static int PROGRESS_BAR_HEIGHT = 8;
    private static int PROGRESS_BAR_OFFSET_X = 8;
    private static int PROGRESS_BAR_OFFSET_Y = 19;
    private static int PROGRESS_AREA_WIDTH = 48;
    private static int PROGRESS_AREA_HEIGHT = 6;
    private static int PROGRESS_AREA_OFFSET_X = 9;
    private static int PROGRESS_AREA_OFFSET_Y = 20;
    private static int PROGRESS_TEXT_OFFSET_X = 9;
    private static int PROGRESS_TEXT_OFFSET_Y = 15;

    private ProgressBar() {
    }
    
    public static int getWidth() {
        return PROGRESS_CONTAINER_WIDTH;
    }
    
    public static int getHeight() {
        return PROGRESS_CONTAINER_HEIGHT;
    }

    public static void displayProgress(Graphics g, GameController gc, ImageLoader il, int x, int y, int progress, String displayText) {
        int xPos, yPos;
        int progressWidth;
        BufferedImage bi;
        
        x = x - (PROGRESS_CONTAINER_WIDTH / 2);
        y = y + PROGRESS_CONTAINER_HEIGHT;
        
        //progress bar container
        bi = il.getImage("Misc/ProgressContainer");
        if(bi != null) {
            xPos = gc.mapToPanelX(x);
            yPos = gc.mapToPanelY(y);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = gc.getPHeight() - yPos;

            //subtract the block height since points are bottom left and drawing starts from top left
            yPos -= gc.getBlockHeight();

            g.drawImage(bi, xPos, yPos, null);
        }
        
        //progress bar background
        bi = il.getImage("Misc/ProgressBG");
        if(bi != null) {
            xPos = gc.mapToPanelX(x + PROGRESS_BAR_OFFSET_X);
            yPos = gc.mapToPanelY(y - PROGRESS_BAR_OFFSET_Y);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = gc.getPHeight() - yPos;

            //subtract the block height since points are bottom left and drawing starts from top left
            yPos -= gc.getBlockHeight();
            
            //yPos += PROGRESS_BAR_OFFSET_Y;

            g.drawImage(bi, xPos, yPos, null);
        }
        
        //progress bar
        bi = il.getImage("Misc/ProgressBar");
        if(bi != null) {
            if(progress < 0) {
                progress = 0;
            }
            if(progress > 100) {
                progress = 100;
            }
            progressWidth = (PROGRESS_AREA_WIDTH * progress) / 100;
            
            xPos = gc.mapToPanelX(x + PROGRESS_AREA_OFFSET_X);
            yPos = gc.mapToPanelY(y - PROGRESS_AREA_OFFSET_Y);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = gc.getPHeight() - yPos;

            //subtract the block height since points are bottom left and drawing starts from top left
            yPos -= gc.getBlockHeight();

            g.drawImage(bi, xPos, yPos, progressWidth, PROGRESS_AREA_HEIGHT, null);
        }
        
        //text
        if(!displayText.isEmpty()) {
            xPos = gc.mapToPanelX(x + PROGRESS_TEXT_OFFSET_X);
            yPos = gc.mapToPanelY(y - PROGRESS_TEXT_OFFSET_Y);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = gc.getPHeight() - yPos;

            //subtract the block height since points are bottom left and drawing starts from top left
            yPos -= gc.getBlockHeight();
            
            g.setColor(Color.white);
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g.drawString(displayText, xPos, yPos);
        }
    }
}