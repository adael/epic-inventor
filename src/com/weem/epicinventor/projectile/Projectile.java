package com.weem.epicinventor.projectile;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.network.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.Rectangle.*;
import java.awt.geom.AffineTransform;
import java.util.*;

public class Projectile {

    protected ProjectileManager projectileManager;
    protected Registry registry;
    protected String id = "";
    protected String name = "";
    protected int mapX, mapY;
    protected int width, height;
    protected int speed;
    protected BufferedImage image;
    protected BufferedImage[] images;
    protected Rectangle spriteRect;
    protected boolean isDirty;
    protected Point start;
    protected Point end;
    protected Point finalPoint;
    protected Point last;
    protected double slope;
    protected Direction direction;
    protected boolean friendly;
    protected boolean placeable;
    protected boolean disregardTerrain;
    protected int damage;
    protected static final long MAX_DISTANCE = 1600;
    protected Actor source;
    protected int count = 0;
    protected boolean returning = false;
    protected int numAnimationFrames;
    protected int currentAnimationFrame;
    protected boolean isSpinning = false;
    transient protected SoundClip soundClip;

    protected enum Direction {

        RIGHT,
        LEFT
    }

    public Projectile(ProjectileManager pm, Registry rg, Actor as, String im, int sp, Point s, Point e, boolean f, boolean p, boolean dt, int d) {
        projectileManager = pm;
        registry = rg;

        source = as;

        id = UUID.randomUUID().toString();

        start = s;
        end = e;
        finalPoint = s;
        last = new Point();
        last.x = mapX = start.x;
        last.y = mapY = start.y;
        setSlope(start, end);
        count = 0;

        speed = sp;
        friendly = f;
        placeable = p;
        disregardTerrain = dt;
        damage = d;

        if (end.x > start.x) {
            direction = Direction.RIGHT;
        } else {
            direction = Direction.LEFT;
        }
        name = im;
        images = new BufferedImage[8];
        setImage("Projectiles/" + im);

        spriteRect = new Rectangle();

        SoundClip cl = new SoundClip(registry, "Projectile/" + im, getCenterPoint());
    }

    public Projectile(String i, ProjectileManager pm, Registry rg, Actor as, String im, int sp, Point s, Point e, boolean f, boolean p, int d) {
        projectileManager = pm;
        registry = rg;

        source = as;

        id = i;

        start = s;
        end = e;
        last = new Point();
        last.x = mapX = start.x;
        last.y = mapY = start.y;
        setSlope(start, end);
        count = 0;

        speed = sp;
        friendly = f;
        placeable = p;
        damage = d;

        if (end.x > start.x) {
            direction = Direction.RIGHT;
        } else {
            direction = Direction.LEFT;
        }
        name = im;
        images = new BufferedImage[8];
        setImage("Projectiles/" + im);

        spriteRect = new Rectangle();

        SoundClip cl = new SoundClip(registry, "Projectile/" + im, getCenterPoint());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setSound(String s, boolean l) {
        stopSound();
        soundClip = new SoundClip(s);
        if (l) {
            soundClip.setLooping(true);
        }
    }

    public void stopSound() {
        if (soundClip != null) {
            soundClip.setLooping(false);
        }
    }

    public Rectangle getRect() {
        return new Rectangle(mapX, mapY, width, height);
    }

    protected double getAngleFromSlope() {
        double angle = 0.0f;
        angle = Math.atan(slope);
        if (direction == Direction.LEFT) {
            angle += Math.PI;
        }
        return angle;
    }

    public void setIsDirty(boolean d) {
        isDirty = d;
    }

    protected void setSlope(Point s, Point e) {
        slope = 0.0f;
        if ((double) last.x - (double) end.x != 0.0f) {
            slope = ((double) last.y - (double) end.y) / ((double) last.x - (double) end.x);
        } else if (last.y > end.y) {
            slope = 9999999.0f;
        } else if (last.y < end.y) {
            slope = -9999999.0f;
        }
    }

    public boolean isDirty() {
        return isDirty;
    }

    private int getMapY(int x) {
        double y = slope * (double) (x - start.x) + (double) start.y;
        return (int) y;
    }

    public int getDamage() {
        return damage;
    }

    public Actor getSource() {
        return source;
    }

    public Point getCenterPoint() {
        Point p = new Point(mapX + (width / 2), mapY + (height / 2));

        return p;
    }

    private void setImage(String name) {
        BufferedImage im = registry.getImageLoader().getImage(name);
        if (im != null) {
            AffineTransform tx = null;
            AffineTransformOp op = null;

            if (direction == Direction.LEFT) {
                //rotate the image based on where you're shooting
                tx = new AffineTransform();
                tx.rotate(Math.atan(slope), im.getWidth() / 2, im.getHeight() / 2);

                op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                image = op.filter(im, null);

                width = im.getWidth();
                height = im.getHeight();

                //flip horizontally
                tx = new AffineTransform();
                tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-width, 0);
                op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                image = op.filter(image, null);
            } else {
                //rotate the image based on where you're shooting
                tx = new AffineTransform();
                tx.rotate(-Math.atan(slope), im.getWidth() / 2, im.getHeight() / 2);

                op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                image = op.filter(im, null);

                width = im.getWidth();
                height = im.getHeight();
            }
        }
    }

    public void setSpinning(String name) {
        numAnimationFrames = 8;
        BufferedImage im = registry.getImageLoader().getImage("Projectiles/" + name);
        if (im != null) {
            for (int i = 0; i < numAnimationFrames; i++) {
                AffineTransform tx = null;
                AffineTransformOp op = null;

                //rotate the image based on where you're shooting
                tx = new AffineTransform();
                tx.rotate(Math.toRadians(i * 45), im.getWidth() / 2, im.getHeight() / 2);

                op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                images[i] = op.filter(im, null);
            }

            width = im.getWidth();
            height = im.getHeight();
        }

        isSpinning = true;
        currentAnimationFrame = 0;
    }

    public void destroy() {
        stopSound();
        isDirty = true;
    }

    public boolean isFromPlaceable() {
        return placeable;
    }

    public void update() {
        Point ePoint = null;

        if (numAnimationFrames > 0) {
            currentAnimationFrame++;
            if (currentAnimationFrame >= numAnimationFrames) {
                currentAnimationFrame = 0;
            }
            image = images[currentAnimationFrame];
        }

        //do movement
        if (count >= 0) {
            count++;
        }
        if (last.distance(end) > 150) {
            if (count % 5 == 0) {
                last.x = mapX;
                last.y = mapY;
                setSlope(start, end);
            }
        } else if (count >= 0) {
            count = -1;
        }

        mapX += (int) (speed * Math.cos(getAngleFromSlope()));
        mapY += (int) (speed * Math.sin(getAngleFromSlope()));

        if (registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.CLIENT) {
            ePoint = new Point(mapX, mapY);
            //make sure it hasn't been going on for a long time
            if (start.distance(ePoint) > MAX_DISTANCE) {
                stopSound();
                isDirty = true;
                return;
            }

            //check for hitting an actor
            if (friendly) {
                //check to see if it hit any monsters
                if (projectileManager.checkMobProjectileHit(this)) {
                    stopSound();
                    isDirty = true;
                }
            } else {
                //hit player?
                if (projectileManager.checkPlayerProjectileHit(this)) {
                    stopSound();
                    isDirty = true;
                } else if (projectileManager.checkPlaceableProjectileHit(this)) {
                    stopSound();
                    isDirty = true;
                }
            }

            //check for hitting a block
            if (!disregardTerrain) {
                if (projectileManager.checkForBlock(getCenterPoint()) != 0) {
                    stopSound();
                    isDirty = true;
                }
            }

            if (isDirty) {
                stopSound();
                if (registry.getGameController().multiplayerMode == registry.getGameController().multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                    if (registry.getNetworkThread().readyForUpdates) {
                        UpdateProjectile up = new UpdateProjectile();
                        up.id = this.getId();
                        up.action = "Destroy";
                        registry.getNetworkThread().sendData(up);
                    }
                }
                if (source != null) {
                    source.projectileReturned();
                }
            }
        }
    }

    public void render(Graphics g) {
        if (image != null && !isDirty) {
            int xPos = projectileManager.mapToPanelX(mapX);
            int yPos = projectileManager.mapToPanelY(mapY);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = projectileManager.getPHeight() - yPos;

            //subtract the block height since points are bottom left and drawing starts from top left
            yPos -= height;

            xPos -= (image.getHeight() / 2);
            yPos += (image.getHeight() / 2);
            g.drawImage(image, xPos, yPos, null);

            /*
             * xPos = projectileManager.mapToPanelX(start.x); yPos =
             * projectileManager.mapToPanelY(start.y); yPos =
             * projectileManager.getPHeight() - yPos;
             *
             * int xPos2 = projectileManager.mapToPanelX(end.x); int yPos2 =
             * projectileManager.mapToPanelY(end.y); yPos2 =
             * projectileManager.getPHeight() - yPos2;
             *
             *
             * g.setColor(Color.red); g.drawLine(xPos, yPos, xPos2, yPos2);
             */


            /*
             * xPos = projectileManager.mapToPanelX(start.x); yPos =
             * projectileManager.mapToPanelY(start.y);
             *
             * //flip the yPos since drawing happens top down versus bottom up
             * yPos = projectileManager.getPHeight() - yPos;
             *
             * //subtract the block height since points are bottom left and
             * drawing starts from top left yPos -= height;
             * g.setColor(Color.yellow); g.drawRect(xPos - 1, yPos - 1, 2, 2);
             *
             *
             * xPos = projectileManager.mapToPanelX(end.x); yPos =
             * projectileManager.mapToPanelY(end.y);
             *
             * //flip the yPos since drawing happens top down versus bottom up
             * yPos = projectileManager.getPHeight() - yPos;
             *
             * //subtract the block height since points are bottom left and
             * drawing starts from top left yPos -= height;
             * g.setColor(Color.red); g.drawRect(xPos - 1, yPos - 1, 2, 2);
             */
        }
    }
}
