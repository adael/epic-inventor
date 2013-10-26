package com.weem.epicinventor.resource;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

public class Resource implements Serializable {
    
    protected static final long serialVersionUID = 10000L;
    protected String id = "";
    private int mapX, mapY;
    private int xp;
    private int width = 32;
    private int height = 32;
    transient protected Registry registry;
    transient private ResourceManager resourceManager;
    private ResourceType resourceType;
    private boolean isDirty;
    private boolean isCollecting;
    private long collectionTime;
    protected int numAnimationFrames;
    protected int currentAnimationFrame;
    protected int animationFrameDuration;
    protected long animationFrameUpdateTime = 0;
    protected final static double DEFAULT_ANIMATION_DURATION = 0.20;
    private boolean isAnimating;
    private String imageName = "";
    transient private Player collectingPlayer;

    public Resource(Registry r, ResourceManager rm, ResourceType rt, int x, int y, int x2) {
        registry = r;
        resourceManager = rm;
        resourceType = rt;
        mapX = x;
        mapY = y;
        xp = x2;
        
        id = UUID.randomUUID().toString();

        if (resourceManager.doesRectContainBlocks(mapX, mapY, width, height)) {
            isDirty = true;
        }

        loopImage("Resources/" + resourceType.getName());
    }

    public void setTransient(Registry rg, ResourceManager rm) {
        registry = rg;
        resourceManager = rm;
        
        if(id == null) {
            id = UUID.randomUUID().toString();
        }
    }
    
    public String getId() {
        return id;
    }

    private void loopImage(String name) {
        imageName = name;

        if (registry.getImageLoader().numImages(name) > 1) {
            BufferedImage im = registry.getImageLoader().getImage(name);

            numAnimationFrames = registry.getImageLoader().numImages(name);

            width = im.getWidth();
            height = im.getHeight();


            currentAnimationFrame = 0;
            animationFrameDuration = (int) (1000 * DEFAULT_ANIMATION_DURATION);
            isAnimating = true;
        } else {
            setImage(name);
        }
    }

    private void setImage(String name) {
        BufferedImage im = registry.getImageLoader().getImage(name);

        if (im != null) {
            imageName = name;
            width = im.getWidth();
            height = im.getHeight();
            isAnimating = false;
        }
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }
    
    public void setMapY(int y) {
        mapY = y;
    }

    public int getXP() {
        return xp;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Point getCenterPoint() {
        return new Point(mapX + (width / 2), mapY + (height / 2));
    }

    public Rectangle getPerimeter() {
        return new Rectangle(mapX, mapY, width, height);
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void destroy() {
        BufferedImage im = null;
        if (isAnimating) {
            im = registry.getImageLoader().getImage(imageName, currentAnimationFrame);
        } else {
            im = registry.getImageLoader().getImage(imageName);
        }
        registry.getPixelizeManager().pixelize(im, mapX, mapY);
        isDirty = true;
    }

    public void setCollecting(Player p, boolean collecting) {
        collectionTime = 0;
        isCollecting = collecting;
        collectingPlayer = p;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public BufferedImage getImage() {
        return resourceType.getImage();
    }

    public void update() {
        if (isAnimating) {
            if(animationFrameUpdateTime <= registry.currentTime) {
                currentAnimationFrame++;
                if(currentAnimationFrame >= numAnimationFrames) {
                    currentAnimationFrame = 0;
                }
                animationFrameUpdateTime = registry.currentTime + animationFrameDuration;
            }
        }

        if (mapX == 0 || mapY == 0) {
            isDirty = true;
        }

        if (isCollecting) {
            collectionTime += registry.getImageLoader().getPeriod();
            if ((collectionTime / 1000) >= resourceType.getGatherTime()) {
                collectionTime = 0;
                isCollecting = false;
                if(collectingPlayer != null) {
                    resourceManager.resourceDoneCollecting(collectingPlayer, this);
                } else {
                    resourceManager.resourceDoneCollecting(this);
                }
            }
        }
    }

    public void render(Graphics g) {
        BufferedImage im;

        int xPos = resourceManager.mapToPanelX(mapX);
        int yPos = resourceManager.mapToPanelY(mapY);

        //flip the yPos since drawing happens top down versus bottom up
        yPos = resourceManager.getPHeight() - yPos;

        //subtract the block height since points are bottom left and drawing starts from top left
        yPos -= resourceManager.getBlockHeight();

        if (isAnimating) {
            im = registry.getImageLoader().getImage(imageName, currentAnimationFrame);
        } else {
            im = registry.getImageLoader().getImage(imageName);
        }

        g.drawImage(im, xPos, yPos, null);

        if (isCollecting) {
            float timeSpent;
            float timeLeft;
            float percentage;
            int hours = 0;
            int minutes = 0;
            int seconds = 0;

            int x = mapX + (width / 2);
            int y = mapY + height;

            timeSpent = (float) (collectionTime / 1000f);
            timeLeft = (float) resourceType.getGatherTime() - timeSpent;
            if (timeLeft < 0) {
                timeLeft = 0;
            }

            if (timeLeft >= 3600) {
                hours = (int) timeLeft / 3600;
                timeLeft -= (hours * 3600);
            }
            if (timeLeft >= 60) {
                minutes = (int) timeLeft / 60;
                timeLeft -= (minutes * 60);
            }
            seconds = (int) timeLeft + 1;

            percentage = ((float) timeSpent / (float) resourceType.getGatherTime()) * 100;

            resourceManager.displayProgress(g,
                    x,
                    y,
                    (int) percentage,
                    hours + ":"
                    + String.format("%02d", minutes) + ":"
                    + String.format("%02d", seconds));
        }
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}