package com.weem.epicinventor.pixelize;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.utility.Rand;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class PixelizeManager extends Manager {

    private ArrayList<Pixelize> pixelizes;

    public PixelizeManager(GameController gc, Registry rg) {
        super(gc, rg);

        pixelizes = new ArrayList<Pixelize>();
    }

    public void pixelize(BufferedImage image, int mapX, int mapY) {
        pixelize(image, mapX, mapY, 5);
    }

    public void pixelize(BufferedImage image, int mapX, int mapY, int pixelSize) {
        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();
            for (int y = 0; y < height; y += pixelSize) {
                for (int x = 0; x < width; x += pixelSize) {
                    int pixelizeX = x;
                    int pixelizeY = y;
                    int pixelizeWidth = pixelSize;
                    int pixelizeHeight = pixelSize;
                    float velocityX = Rand.getFloat() + 0.25f;
                    float velocityY = ((float) Rand.getRange(2, 5)) + Rand.getFloat() + 0.05f;
                    if (Rand.getRange(0, 1) == 1) {
                        velocityX *= -1;
                    }
                    if (Rand.getRange(0, 1) == 1) {
                        velocityY *= -1;
                    }

                    if (width - x < pixelSize) {
                        pixelizeWidth = width - x;
                        pixelizeX = x - pixelSize + pixelizeWidth;
                    }
                    if (height - y < pixelSize) {
                        pixelizeHeight = height - y;
                        pixelizeY = y - pixelSize + pixelizeHeight;
                    }

                    BufferedImage tmpImage = image.getSubimage(x, y, pixelizeWidth, pixelizeHeight);
                    Pixelize pixelize = new Pixelize(this, registry, tmpImage, mapX + pixelizeX, mapY + height - pixelizeY - pixelSize, velocityX, velocityY);
                    pixelizes.add(pixelize);
                }
            }
        }
    }

    @Override
    public void update() {
        super.update();

        Pixelize pixelize = null;

        for (int i = 0; i < pixelizes.size(); i++) {
            pixelize = pixelizes.get(i);
            pixelize.update();
            if (pixelize.isDirty) {
                pixelizes.remove(i);
            }
        }
    }

    public void render(Graphics g) {
        Pixelize pixelize = null;

        for (int i = 0; i < pixelizes.size(); i++) {
            pixelize = pixelizes.get(i);
            pixelize.render(g);
        }
    }
}
