package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.monster.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.network.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.*;

public abstract class Placeable implements Serializable {

    private static final long serialVersionUID = 10000L;
    protected String id = "";
    transient protected Registry registry;
    protected PlaceableManager placeableManager;
    protected int mapX, mapY;
    protected int width, height;
    protected String standardImage;
    protected String animationImage;
    protected State currentState;
    protected int numAnimationFrames;
    protected int currentAnimationFrame;
    protected int animationFrameDuration;
    protected long animationFrameUpdateTime = 0;
    protected final static double DEFAULT_ANIMATION_DURATION = 0.20;
    protected boolean isAnimating;
    protected boolean isActive;
    private boolean isBuilding;
    private long buildingTime;
    protected int totalBuildTime;
    transient protected BufferedImage buildingImage;
    transient protected BufferedImage canPlaceImage;
    transient protected BufferedImage cantPlaceImage;
    protected boolean isDirty;
    private boolean isDestroying;
    protected int totalHitPoints = 400;
    protected int hitPoints;
    private boolean isPowered = true;
    private boolean noPowerShow;
    private float noPowerTotalTime;
    private final static float NO_POWER_MAX_TIME = 1.0f;
    private final static int NO_POWER_WIDTH = 32;
    private final static int NO_POWER_HEIGHT = 32;
    protected int powerRequired;
    protected int powerGenerated;
    protected int fearGenerated;
    protected int fearDistance;
    protected long fearDuration;
    protected int touchDamage;
    private long destroyingTime;
    private final static int DESTROY_TIME = 10;
    protected String type = "";
    protected boolean facingRight = true;
    protected Rectangle spriteRect;
    private long lastDamage = 0;

    public enum State {

        New,
        Placed,
        NotPlaced
    }

    public Placeable(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        placeableManager = pm;
        registry = rg;

        mapX = x;
        mapY = y;

        hitPoints = totalHitPoints;

        isAnimating = false;
        isActive = true;

        currentState = cs;

        animationImage = am;
        setImage(sm);

        BufferedImage im;

        buildingImage = registry.getImageLoader().getImage(standardImage);
        buildingImage = ImageLoader.changeToGrayscale(buildingImage);

        canPlaceImage = registry.getImageLoader().getImage(standardImage);
        canPlaceImage = ImageLoader.changeTransperancy(canPlaceImage, 0.7f);
        canPlaceImage = ImageLoader.changeColor(canPlaceImage, (short) 0, (short) 100, (short) 0);

        cantPlaceImage = registry.getImageLoader().getImage(standardImage);
        cantPlaceImage = ImageLoader.changeTransperancy(cantPlaceImage, 0.7f);
        cantPlaceImage = ImageLoader.changeColor(cantPlaceImage, (short) 100, (short) 0, (short) 0);

        id = UUID.randomUUID().toString();
    }

    public void setTransient(Registry rg) {
        registry = rg;
        placeableManager = rg.getPlaceableManager();

        buildingImage = registry.getImageLoader().getImage(standardImage);
        buildingImage = ImageLoader.changeToGrayscale(buildingImage);

        canPlaceImage = registry.getImageLoader().getImage(standardImage);
        canPlaceImage = ImageLoader.changeTransperancy(canPlaceImage, 0.7f);
        canPlaceImage = ImageLoader.changeColor(canPlaceImage, (short) 0, (short) 100, (short) 0);

        cantPlaceImage = registry.getImageLoader().getImage(standardImage);
        cantPlaceImage = ImageLoader.changeTransperancy(cantPlaceImage, 0.7f);
        cantPlaceImage = ImageLoader.changeColor(cantPlaceImage, (short) 100, (short) 0, (short) 0);

        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public void animateStart() {
        if (!animationImage.equals("")) {
            loopImage(animationImage);
        }
    }

    public void animateStop() {
        setImage(standardImage);
    }

    public String getId() {
        return id;
    }

    public Point getCenterPoint() {
        return new Point(mapX + (width / 2), mapY + (height / 2));
    }

    public void toggled() {
    }

    public boolean setDestroying(boolean destroying) {
        destroyingTime = 0;
        isDestroying = destroying;
        return true;
    }

    public int applyDamage(Monster source, int damage) {
        if (damage > 0) {
            if (damage > 80) {
                damage = 80;
            }
            if (!getItemName().equals("ItemContainer")) {
                registry.showMessage("Error", "One of your towns is under attack!");
                hitPoints -= damage;
                lastDamage = registry.currentTime;
                registry.getIndicatorManager().createIndicator(mapX + (width / 2), mapY + 50, "-" + Integer.toString(damage));
                if (source != null) {
                    SoundClip cl = new SoundClip(registry, "Monster/Chew" + source.getName() + Rand.getRange(1, 2), getCenterPoint());
                } else {
                    SoundClip cl = new SoundClip(registry, "Monster/Chew", getCenterPoint());
                }
                if (registry.getGameController().multiplayerMode == registry.getGameController().multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                    if (registry.getNetworkThread().readyForUpdates) {
                        UpdatePlaceable up = new UpdatePlaceable(this.getId());
                        up.hitPoints = this.getHitPoints();
                        up.totalHitPoints = this.getTotalHitPoints();
                        up.source = source;
                        up.dataInt = damage;
                        up.action = "ApplyDamage";
                        registry.getNetworkThread().sendData(up);
                    }
                }
            }
        }
        return damage;
    }

    public int attackDamage(Monster source, Rectangle attackRect, int damage) {
        int damageTaken = 0;
        if (registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.CLIENT) {
            if (canDestroy() && spriteRect.intersects(attackRect) && hitPoints > 0) {
                damageTaken = applyDamage(source, damage);
            }
        }
        return damageTaken;
    }

    public boolean canDestroy() {
        if (currentState == State.Placed && !isBuilding) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void destroy() {
        isDirty = true;
        if (registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlaceable up = new UpdatePlaceable(this.getId());
                up.hitPoints = this.getHitPoints();
                up.totalHitPoints = this.getTotalHitPoints();
                up.action = "IsDirty";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public String getItemName() {
        String parts[];
        String itemName = "";

        if (standardImage != null) {
            parts = standardImage.split("/");
            itemName = parts[parts.length - 1];
        }

        return itemName;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public int getTotalHitPoints() {
        return totalHitPoints;
    }

    protected void setImage(String name) {
        BufferedImage im = registry.getImageLoader().getImage(name);

        if (im != null) {
            standardImage = name;

            width = im.getWidth();
            height = im.getHeight();
            spriteRect = new Rectangle(mapX, mapY, width, height);
            isAnimating = false;
        }
    }

    public void setHitPoints(int h, int t) {
        hitPoints = h;
        totalHitPoints = t;
    }

    public void setInventory(Inventory i) {
    }

    protected void setState(Placeable.State state) {
        if (currentState == State.NotPlaced && state == State.Placed) {
            isBuilding = true;
            placeableManager.playerDeleteInventory(registry.getPlayerPlacingSlot(registry.getPlayerManager().getCurrentPlayer()), 1);
        }
        currentState = state;
    }

    public Placeable.State getState() {
        return currentState;
    }

    protected void setIsPowered(boolean p) {
        isPowered = p;
        if (isPowered) {
            noPowerShow = false;
            noPowerTotalTime = 0;
        }
    }

    public void setIsBuilding(boolean b) {
        isBuilding = b;
    }

    protected void loopImage(String name) {
        if (registry.getImageLoader().numImages(name) > 1) {
            standardImage = name;

            BufferedImage im = registry.getImageLoader().getImage(name);

            numAnimationFrames = registry.getImageLoader().numImages(name);

            width = im.getWidth();
            height = im.getHeight();
            spriteRect = new Rectangle(mapX, mapY, width, height);

            currentAnimationFrame = 0;
            animationFrameDuration = (int) (1000 * DEFAULT_ANIMATION_DURATION);
            isAnimating = true;
        } else {
            setImage(name);
        }
    }

    public void stopLooping() {
        isAnimating = false;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPowerGenerated() {
        return powerGenerated;
    }

    public int getPowerRequired() {
        return powerRequired;
    }

    public int getFearGenerated() {
        return fearGenerated;
    }

    public int getFearDistance() {
        return fearDistance;
    }

    public long getFearDuration() {
        return fearDuration;
    }

    public boolean getIsDirty() {
        return isDirty;
    }

    public void setIsDirty(boolean d) {
        isDirty = d;
    }

    public int getTouchDamage() {
        return touchDamage;
    }

    public String getType() {
        return type;
    }

    public boolean handleRightClick(Point clickPoint) {
        return false;
    }

    protected boolean isInside(Point p) {
        if (p.x >= mapX
                && p.x <= (mapX + width)
                && p.y >= mapY
                && p.y <= (mapY + height)) {
            return true;
        }

        return false;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isActivated() {
        if (canDestroy() && isPowered) {
            return true;
        } else {
            return false;
        }
    }

    public void setActive(boolean a) {
        isActive = a;
    }

    public void setPosition(int x, int y) {
        mapX = x;
        mapY = y;
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public Rectangle getPerimeter() {
        return new Rectangle(mapX, mapY, width, height);
    }

    public boolean checkCanPlace() {
        boolean canPlace = true;
        if (placeableManager.isOverOther()) {
            canPlace = false;
        }
        if (currentState == Placeable.State.New) {
            canPlace = false;
        }
        return canPlace;
    }

    public void update() {
        if (isActive && isAnimating) {
            if (animationFrameUpdateTime <= registry.currentTime) {
                currentAnimationFrame++;
                if (currentAnimationFrame >= numAnimationFrames) {
                    currentAnimationFrame = 0;
                }
                animationFrameUpdateTime = registry.currentTime + animationFrameDuration;
            }
        }

        if (isBuilding) {
            buildingTime += registry.getImageLoader().getPeriod();
            if ((buildingTime / 1000) >= totalBuildTime) {
                buildingTime = 0;
                isBuilding = false;
                animateStart();
                if (!type.isEmpty()) {
                    registry.showMessage("Success", type + " has finished building!");
                }
            }
        }

        if (isDestroying) {
            destroyingTime += registry.getImageLoader().getPeriod();
            if ((destroyingTime / 1000) >= DESTROY_TIME) {
                destroyingTime = 0;
                isDestroying = false;
                placeableManager.placeableDoneDestroying(this);
            }
        }

        //check status of no power
        if (!isPowered) {
            long p = registry.getImageLoader().getPeriod();
            noPowerTotalTime = (noPowerTotalTime
                    + registry.getImageLoader().getPeriod())
                    % (long) (1000 * NO_POWER_MAX_TIME * 2);

            if ((noPowerTotalTime / (NO_POWER_MAX_TIME * 1000)) > 1) {
                noPowerShow = !noPowerShow;
                noPowerTotalTime = 0;
            }
        }

        if (hitPoints <= 0) {
            if (!type.isEmpty()) {
                registry.showMessage("Error", type + " has been destroyed!");
            }
            SoundClip cl = new SoundClip(registry, "Placeable/Destroy", getCenterPoint());

            BufferedImage im = null;
            if (isAnimating) {
                im = registry.getImageLoader().getImage(standardImage, currentAnimationFrame);
            } else {
                im = registry.getImageLoader().getImage(standardImage);
            }
            registry.getPixelizeManager().pixelize(im, mapX, mapY);

            isDirty = true;
        }
    }

    public void updateLong() {
        if (registry.currentTime - lastDamage > 5000 || lastDamage == 0) {
            if (hitPoints < totalHitPoints) {
                hitPoints++;
            } else {
                hitPoints = totalHitPoints;
            }
        }
    }

    public int getHPRegenerationBonus(Point p) {
        //to be overridden
        return 0;
    }

    public float getAttackBonus(Point p) {
        //to be overridden
        return 0;
    }

    public void render(Graphics g) {
        if (currentState != Placeable.State.New) {
            BufferedImage im;
            BufferedImage imLeft;
            AffineTransform tx;
            AffineTransformOp op;

            int xPos = placeableManager.mapToPanelX(mapX);
            int yPos = placeableManager.mapToPanelY(mapY);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = placeableManager.getPHeight() - yPos;

            //subtract the height since points are bottom left and drawing starts from top left
            yPos -= height;

            if (isBuilding) {
                if (buildingImage != null) {
                    g.drawImage(buildingImage, xPos, yPos, null);
                }

                float timeSpent;
                float timeLeft;
                float percentage;
                int hours = 0;
                int minutes = 0;
                int seconds = 0;

                int x = mapX + (width / 2);
                int y = mapY + height;

                timeSpent = (float) (buildingTime / 1000f);
                timeLeft = (float) totalBuildTime - timeSpent;
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

                percentage = ((float) timeSpent / (float) totalBuildTime) * 100;

                placeableManager.displayProgress(g,
                        x,
                        y,
                        (int) percentage,
                        hours + ":"
                        + String.format("%02d", minutes) + ":"
                        + String.format("%02d", seconds));
            } else if (!isPowered) {
                if (buildingImage != null) {
                    g.drawImage(buildingImage, xPos, yPos, null);
                    if (noPowerShow) {
                        Point centerPoint = getCenterPoint();
                        centerPoint.x -= NO_POWER_WIDTH / 2;
                        centerPoint.y -= NO_POWER_HEIGHT / 2;

                        xPos = placeableManager.mapToPanelX(centerPoint.x);
                        yPos = placeableManager.mapToPanelY(centerPoint.y);

                        //flip the yPos since drawing happens top down versus bottom up
                        yPos = placeableManager.getPHeight() - yPos;

                        //subtract the height since points are bottom left and drawing starts from top left
                        yPos -= NO_POWER_HEIGHT;

                        g.drawImage(registry.getImageLoader().getImage("Misc/NoPower"), xPos, yPos, null);
                    }
                    /*
                     * BufferedImage im;
                     *
                     * int xPos = placeableManager.mapToPanelX(mapX); int yPos =
                     * placeableManager.mapToPanelY(mapY);
                     *
                     * //flip the yPos since drawing happens top down versus
                     * bottom up yPos = placeableManager.getPHeight() - yPos;
                     *
                     * //subtract the height since points are bottom left and
                     * drawing starts from top left yPos -= height;
                     */
                }
            } else {
                if (isAnimating) {
                    im = registry.getImageLoader().getImage(standardImage, currentAnimationFrame);
                } else {
                    im = registry.getImageLoader().getImage(standardImage);
                    if (currentState == State.NotPlaced) {
                        if (placeableManager.isOverOther() || placeableManager.doesRectContainBlocks(mapX + 1, mapY + 1, width - 2, height - 2)) {
                            im = cantPlaceImage;
                        } else {
                            im = canPlaceImage;
                        }
                    }
                }

                if (im != null) {
                    if (facingRight) {
                        g.drawImage(im, xPos, yPos, null);
                    } else {
                        tx = AffineTransform.getScaleInstance(1, -1);
                        tx = AffineTransform.getScaleInstance(-1, 1);
                        tx.translate(-width, 0);
                        op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                        imLeft = op.filter(im, null);
                        if (imLeft != null) {
                            g.drawImage(imLeft, xPos, yPos, null);
                        }
                    }
                }
            }

            if (isDestroying) {
                float timeSpent;
                float timeLeft;
                float percentage;
                int hours = 0;
                int minutes = 0;
                int seconds = 0;

                int x = mapX + (width / 2);
                int y = mapY + height;

                timeSpent = (float) (destroyingTime / 1000f);
                timeLeft = (float) DESTROY_TIME - timeSpent;
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

                percentage = ((float) timeSpent / (float) DESTROY_TIME) * 100;

                placeableManager.displayProgress(g,
                        x,
                        y,
                        (int) percentage,
                        hours + ":"
                        + String.format("%02d", minutes) + ":"
                        + String.format("%02d", seconds));
            }

            if (hitPoints < totalHitPoints) {
                float percentage;

                int x = mapX + (width / 2);
                int y = mapY + height;

                percentage = ((float) hitPoints / (float) totalHitPoints) * 100;

                placeableManager.displayHP(g,
                        x,
                        y,
                        (int) percentage);
            }

            /*
             * if (spriteRect != null) { xPos =
             * placeableManager.mapToPanelX(spriteRect.x); yPos =
             * placeableManager.mapToPanelY(spriteRect.y);
             *
             * //flip the yPos since drawing happens top down versus bottom up
             * yPos = placeableManager.getPHeight() - yPos;
             *
             * //subtract the height since points are bottom left and drawing
             * starts from top left yPos -= height;
             *
             *
             * g.setColor(Color.blue); g.drawRect(xPos, yPos, spriteRect.width,
             * spriteRect.height);
            }
             */
        }
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}
