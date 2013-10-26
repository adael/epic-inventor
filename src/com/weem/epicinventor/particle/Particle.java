package com.weem.epicinventor.particle;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.Rectangle.*;
import java.awt.geom.Point2D.*;
import java.awt.geom.AffineTransform;

public class Particle {

    private ParticleEmitter particleEmitter;
    private Registry registry;
    private float mapX;
    private float mapY;
    public boolean isDirty;
    public float velocityX;
    public float velocityY;
    public float angle;
    public float angularVelocity;
    public float size;
    public int ttl;
    private BufferedImage image;
    private int width;
    private int height;
    private boolean friendly;
    private boolean placeable;
    private boolean disregardTerrain;
    private boolean isNew;
    private int damage;
    private Actor source;

    public Particle(ParticleEmitter pm, Registry rg, Actor as, String im, int x, int y, float vx, float vy, float a, float av, float s, int t, boolean f, boolean p, boolean dt, int d, boolean vbr) {
        particleEmitter = pm;
        registry = rg;
        source = as;
        mapX = x;
        mapY = y;
        velocityX = vx;
        velocityY = vy;
        angle = a;
        angularVelocity = av;
        size = s;
        ttl = t;
        friendly = f;
        placeable = p;
        disregardTerrain = dt;
        damage = d;
        isNew = true;
        
        image = registry.getImageLoader().getImage(im);
        
        if(vbr) {
            double angle = getAngleFromVelocity();
            AffineTransform at = new AffineTransform();
            at.rotate(angle, image.getWidth() / 2, image.getHeight() / 2);
            AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            image = atop.filter(image, null);
        }
        
        width = (int) ((float) image.getWidth() * size);
        height = (int) ((float) image.getHeight() * size);
        
        /*BufferedImage tmpImage = registry.getImageLoader().getImage(im);
        image = registry.getImageLoader().changeColor(tmpImage, (short)color.getRed(), (short)color.getGreen(), (short)color.getBlue());
        width = (int) ((float) image.getWidth() * size);
        height = (int) ((float) image.getHeight() * size);*/

       /* BufferedImage tmpImage = registry.getImageLoader().getImage(im);
        if (tmpImage != null) {
            image = colorImage(tmpImage, color.getRed(), color.getGreen(), color.getBlue());
            width = (int) ((float) image.getWidth() * size);
            height = (int) ((float) image.getHeight() * size);
        }*/
    }
    
    protected double getAngleFromVelocity() {
        double angle = 0.0f;
        float slope = 0.0f;
        if(velocityX == 0.0f) {
            if(velocityX > 0.0f) {
                angle = Math.PI / 2;
            } else {
                angle = -Math.PI / 2;
            }
        } else {
            slope = -velocityY / velocityX;
            angle = Math.atan(slope);
            if (velocityX < 0) {
                angle += Math.PI;
            }
        }
        return angle;
    }

    public BufferedImage colorImage(BufferedImage loadImg, int red, int green, int blue) {
        BufferedImage img = new BufferedImage(loadImg.getWidth(), loadImg.getHeight(), BufferedImage.TRANSLUCENT);
        Graphics2D graphics = img.createGraphics();
        Color newColor = new Color(red, green, blue, 0);
        graphics.setXORMode(newColor);
        graphics.drawImage(loadImg, null, 0, 0);
        graphics.dispose();
        return img;
    }

    public int getDamage() {
        return damage;
    }

    public Actor getSource() {
        return source;
    }

    public boolean isFromPlaceable() {
        return placeable;
    }

    public Rectangle getRect() {
        return new Rectangle((int)mapX, (int)mapY, width, height);
    }

    public void update() {
        ttl--;
        if(!isNew) {
            mapX += velocityX;
            mapY += velocityY;
            angle += angularVelocity;
        } else {
            isNew = false;
        }

        if (registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.CLIENT) {
            Point ePoint = new Point((int)mapX, (int)mapY);

            if (friendly) {
                //check to see if it hit any monsters
                if (particleEmitter.checkMobParticleHit(this)) {
                    isDirty = true;
                }
            } else {
                //hit player?
                /*if (particleEmitter.checkPlayerParticleHit(this)) {
                    isDirty = true;
                } else if (particleEmitter.checkPlaceableParticleHit(this)) {
                    isDirty = true;
                }*/
            }

            //check for hitting a block
            if (!disregardTerrain) {
                if (particleEmitter.checkForBlock(getCenterPoint()) != 0) {
                    isDirty = true;
                }
            }
        }
    }

    public Point getCenterPoint() {
        Point p = new Point((int)(mapX + (width / 2.0f)), (int)(mapY + (height / 2.0f)));

        return p;
    }

    public void render(Graphics g) {
        if (image != null) {
            int xPos = particleEmitter.mapToPanelX((int)mapX);
            int yPos = particleEmitter.mapToPanelY((int)mapY);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = particleEmitter.getPHeight() - yPos;

            //subtract the block height since points are bottom left and drawing starts from top left
            yPos -= height;

            g.drawImage(image, xPos, yPos, null);
        }
    }
}
