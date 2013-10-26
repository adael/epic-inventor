package com.weem.epicinventor.pixelize;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.Rectangle.*;
import java.awt.geom.Point2D.*;

public class Pixelize {

    private PixelizeManager pixelizeManager;
    private Registry registry;
    private float mapX;
    private float mapY;
    public boolean isDirty;
    public float velocityX;
    public float velocityY;
    private BufferedImage image;
    private int width;
    private int height;

    public Pixelize(PixelizeManager pm, Registry rg, BufferedImage im, int x, int y, float vx, float vy) {
        pixelizeManager = pm;
        registry = rg;
        mapX = x;
        mapY = y;
        velocityX = vx;
        velocityY = vy;

        image = im;
        width = image.getWidth();
        height = image.getHeight();
    }

    public Rectangle getRect() {
        return new Rectangle((int) mapX, (int) mapY, width, height);
    }

    public void update() {
        mapX += velocityX;
        mapY += velocityY;
        
        velocityY -= 0.25f;

        if (pixelizeManager.checkForBlock(getCenterPoint()) != 0) {
            isDirty = true;
        }
    }

    public Point getCenterPoint() {
        Point p = new Point((int) (mapX + (width / 2.0f)), (int) (mapY + (height / 2.0f)));

        return p;
    }

    public void render(Graphics g) {
        if (image != null) {
            int xPos = pixelizeManager.mapToPanelX((int) mapX);
            int yPos = pixelizeManager.mapToPanelY((int) mapY);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = pixelizeManager.getPHeight() - yPos;

            //subtract the block height since points are bottom left and drawing starts from top left
            yPos -= height;

            g.drawImage(image, xPos, yPos, null);
        }
    }
}
