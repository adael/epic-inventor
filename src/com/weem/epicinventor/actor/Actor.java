package com.weem.epicinventor.actor;

import com.weem.epicinventor.*;
import com.weem.epicinventor.network.*;
import com.weem.epicinventor.utility.*;
import com.weem.epicinventor.weapon.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.*;
import java.awt.geom.Arc2D;

public abstract class Actor implements Serializable {

    private static final long serialVersionUID = -5771308574194949873L;

    public enum VertMoveMode {

        NOT_JUMPING, JUMPING, FALLING, FLYING
    };

    public enum Facing {

        RIGHT, LEFT
    };

    public enum ActionMode {

        NONE, GATHERING, ATTACKING
    };

    public enum AttackType {

        TOUCH, MELEE, RANGE
    }
    protected String id = "";
    transient protected Manager manager;
    transient protected Registry registry;
    transient protected Arc2D.Double attackArc;
    transient protected int attackArcOffsetX, attackArcOffsetY;
    protected int mapX, lastMapY, mapY;
    transient protected int[] yx, ycm;
    protected int width, height;
    protected int xMoveSize;
    protected boolean stateChanged;
    protected String image = "";
    protected int numAnimationFrames;
    protected int currentAnimationFrame;
    protected int animationFrameDuration;
    protected long animationFrameUpdateTime = 0;
    protected final static double DEFAULT_ANIMATION_DURATION = 0.20;
    protected boolean isAnimating;
    protected boolean isActive;
    protected ActionMode actionMode;
    protected VertMoveMode vertMoveMode;
    protected Facing facing;
    protected AttackType currentAttackType;
    protected int topOffset;
    protected int jumpSize;
    protected int ascendOriginalSize;
    protected int ascendSize;
    protected int ascendCount;
    protected int ascendMax;
    protected int fallSize;
    protected int baseOffset;
    protected int baseWidth;
    protected boolean isStill;
    protected boolean isTryingToMove;
    protected boolean isStomping;
    protected int startJumpSize;
    protected int maxFallSize;
    protected int gravity;
    protected int totalFall = 0;
    protected int completeFall = 0;
    protected int totalHitPoints, hitPoints;
    protected int totalArmorPoints, armorPoints;
    protected int baseHitPoints = 100;
    protected int baseArmorPoints = 0;
    transient protected Rectangle spriteRect;
    transient protected Rectangle attackRect;
    protected int spriteRectOffestX, spriteRectOffestY;
    protected boolean showRect;
    protected boolean showGoals;
    protected int knockBackX = 0;
    protected boolean isDead;
    protected boolean isFeared;
    protected boolean isSlowed;
    protected boolean isPoisoned;
    protected Point fearedSource;
    protected long fearedDuration;
    protected long fearedTotalTime;
    protected long slowedDuration;
    protected long slowedTotalTime;
    protected long poisonedDuration;
    protected long poisonedTotalTime;
    protected int attackRange = 30;
    protected long attackRefreshTimerStart = 0;
    protected long attackRefreshTimerEnd = 0;
    protected int maxFollowDistance;
    protected boolean statusAttackBonus;
    protected boolean statusFear;
    protected boolean statusHeal;
    protected boolean statusPoison;
    protected boolean statusStun;
    protected boolean statusRezSickness;
    protected boolean statusSlowed;
    protected final static int STATUS_WIDTH = 17;
    protected final static int STATUS_SPACING = 1;
    protected boolean canFly;
    protected SoundClip loopingSound;
    transient protected HashMap<Actor, Integer> attackers = new HashMap<Actor, Integer>();
    transient protected Actor lastAttacker;
    protected boolean disregardKnockBack = false;
    protected long stunEnded = 0;
    protected String debugInfo = "";
    protected long chatEnd = 0;
    protected boolean isChatting;
    protected boolean projectileOut = false;

    public Actor(Manager m, Registry rg, String im, int x, int y) {
        manager = m;

        registry = rg;

        mapX = x;
        lastMapY = mapY = y;
        yx = new int[2];
        ycm = new int[2];

        isAnimating = false;
        isActive = true;

        ascendOriginalSize = 2;
        ascendSize = 2;
        ascendMax = 16;

        startJumpSize = 20;
        maxFallSize = 40;
        gravity = 2;

        spriteRect = new Rectangle();
        spriteRectOffestX = 0;
        spriteRectOffestY = 0;
        attackRect = null;
        attackArcOffsetX = 0;
        attackArcOffsetY = 0;
        attackArc = null;
        currentAttackType = AttackType.TOUCH;

        setImage(im);

        id = UUID.randomUUID().toString();
    }

    public void setTransient(Registry rg, Manager m) {
        registry = rg;
        manager = m;
        spriteRect = new Rectangle();
        yx = new int[2];
        ycm = new int[2];
    }

    public String getId() {
        return id;
    }
    
    public void projectileReturned() {
        projectileOut = false;
    }

    public void hide() {
    }

    public boolean getIsHiding() {
        return false;
    }
    
    public boolean getIsStomping() {
        return isStomping;
    }
    
    public void setIsStomping(boolean s) {
        isStomping = s;
    }

    public void moveLeft() {
        isStill = false;
        isTryingToMove = true;
        stateChanged = true;
        facing = Facing.LEFT;
    }

    public void moveRight() {
        isStill = false;
        isTryingToMove = true;
        stateChanged = true;
        facing = Facing.RIGHT;
    }

    public void moveTowardsPoint(Point p) {
        if (p != null && knockBackX == 0) {
            actionMode = ActionMode.NONE;

            int actorX = getMapX();
            int actorCenterX = actorX + (getWidth() / 2);
            int actorCenterY = getMapY() + (getHeight() / 2);
            int targetX = p.x;
            int targetY = p.y;
            int distance = (int) Math.sqrt(Math.pow(Math.abs(targetX - actorCenterX), 2.0f) + Math.pow(Math.abs(targetY - actorCenterY), 2.0f));

            if (distance <= maxFollowDistance) {
                if (targetX > actorCenterX) {
                    if (facing != Facing.RIGHT) {
                        moveRight();
                        stopMove();
                    }
                } else {
                    if (facing != Facing.LEFT) {
                        moveLeft();
                        stopMove();
                    }
                }
                checkCollide(0);
            } else {
                if (getIsTryingToMove()) {
                    if (targetX > actorCenterX) {
                        if (getFacing() != Actor.Facing.RIGHT) {
                            moveRight();
                        }
                    } else {
                        if (getFacing() != Actor.Facing.LEFT) {
                            moveLeft();
                        }
                    }
                } else {
                    if (targetX > actorCenterX) {
                        moveRight();
                    } else {
                        moveLeft();
                    }
                }

                //try and move
                int oldMapX = actorX;
                if (Math.abs(actorCenterX - targetX) > maxFollowDistance) {
                    if (getIsTryingToMove()) {
                        if (getFacing() == Actor.Facing.RIGHT) {
                            actorX += checkCollideRight();
                        } else {
                            actorX -= checkCollideLeft();
                        }
                    }
                    if (getCanFly()) {
                        if (mapY < (targetY + 30) || mapY < (manager.findNextFloor(mapX, mapY, height) + Rand.getRange(5, 15))) {
                            flap();
                        }
                    } else {
                        //we didn't move anywhere - try jumping?
                        if (actorX == oldMapX) {
                            jump();
                            if (getFacing() == Actor.Facing.RIGHT) {
                                checkCollideRight();
                            } else {
                                checkCollideLeft();
                            }
                        }
                    }
                } else if (getCanFly()) {
                    if (mapY < (targetY + 30) || mapY < (manager.findNextFloor(mapX, mapY, height) + Rand.getRange(5, 15))) {
                        flap();
                    }
                    if (getFacing() == Actor.Facing.RIGHT) {
                        actorX += checkCollideRight();
                    } else {
                        actorX -= checkCollideLeft();
                    }
                }

                setMapX(actorX);
            }
        }
    }

    public void updatePosition() {
        if (!statusStun) {
            if (knockBackX > 0) {
                mapX += checkCollide(knockBackX);
            } else if (knockBackX < 0) {
                mapX -= checkCollide(knockBackX);
            } else {
                if (isTryingToMove) {
                    if (facing == Facing.RIGHT) {
                        mapX += checkCollideRight();
                    } else {
                        mapX -= checkCollideLeft();
                    }
                    checkCollide(0);
                } else if (vertMoveMode != VertMoveMode.NOT_JUMPING) {
                    checkCollide(0);
                }
            }
        }
    }

    public void moveAwayFromPoint(Point p) {
        if (p != null) {
            actionMode = ActionMode.NONE;

            int actorX = getMapX();
            int actorCenter = actorX + (getWidth() / 2);
            int targetX = p.x;
            int targetY = p.y;
            int distance = Math.abs(targetX - actorCenter);

            if (distance <= maxFollowDistance) {
                if (targetX > actorCenter) {
                    if (facing != Facing.RIGHT) {
                        moveLeft();
                        stopMove();
                    }
                } else {
                    if (facing != Facing.LEFT) {
                        moveRight();
                        stopMove();
                    }
                }
            } else {
                if (getIsTryingToMove()) {
                    if (targetX > actorCenter) {
                        if (getFacing() != Actor.Facing.LEFT) {
                            moveLeft();
                        }
                    } else {
                        if (getFacing() != Actor.Facing.RIGHT) {
                            moveRight();
                        }
                    }
                } else {
                    if (targetX > actorCenter) {
                        moveLeft();
                    } else {
                        moveRight();
                    }
                }

                //try and move
                int oldMapX = actorX;
                if (getIsTryingToMove()) {
                    if (getFacing() == Actor.Facing.RIGHT) {
                        actorX += checkCollideRight();
                    } else {
                        actorX -= checkCollideLeft();
                    }
                }

                if (getCanFly()) {
                    if (mapY < (targetY + 30) || mapY < (manager.findFloor(mapX) + Rand.getRange(25, 60))) {
                        flap();
                    }
                } else {
                    //we didn't move anywhere - try jumping?
                    if (actorX == oldMapX) {
                        jump();
                    }
                }

                setMapX(actorX);
            }
        }
    }

    public void jump() {
        jump(startJumpSize);
    }

    public void jump(int initialVelocity) {
        if (!statusStun) {
            if (vertMoveMode == VertMoveMode.NOT_JUMPING || vertMoveMode == VertMoveMode.FLYING) {
                setVertMoveMode(VertMoveMode.JUMPING);
                jumpSize = initialVelocity;
            }
        }
    }

    public void flap() {
        if (!statusStun) {
            if (vertMoveMode != VertMoveMode.FLYING) {
                setVertMoveMode(VertMoveMode.FLYING);
            }
        }
    }

    public void fear(Point p, long d) {
        isFeared = true;
        fearedSource = p;
        fearedDuration = d;
        fearedTotalTime = 0;
    }

    public void unfear() {
        isFeared = false;
        fearedSource = null;
        fearedDuration = 0;
        fearedTotalTime = 0;
    }

    public void slow(long d) {
        isSlowed = true;
        slowedDuration = d;
        slowedTotalTime = 0;
    }

    public void unslow() {
        isSlowed = false;
        slowedDuration = 0;
        slowedTotalTime = 0;
    }

    public void poison(long d) {
        isPoisoned = true;
        poisonedDuration = d;
        poisonedTotalTime = 0;
    }

    public void unpoison() {
        isPoisoned = false;
        poisonedDuration = 0;
        poisonedTotalTime = 0;
    }

    public void setFearedSource(Point p) {
        fearedSource = p;
    }

    public void stopMove() {
        isTryingToMove = false;
        stateChanged = true;
        isStill = true;
    }

    public int getArmorPoints() {
        return armorPoints;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public int getBaseHitPoints() {
        return baseHitPoints;
    }

    public int getTotalHitPoints() {
        return totalHitPoints;
    }

    public Point getFearedSource() {
        return fearedSource;
    }

    public boolean getStatusStun() {
        return statusStun;
    }

    public String getDebugInfo() {
        if (debugInfo == null) {
            return "";
        } else {
            return debugInfo;
        }
    }

    public void setStatusStun(boolean s, long time) {
        statusStun = s;
        stunEnded = registry.currentTime + time;
        if (statusStun) {
            //stopMove();
            stateChanged = true;
            updateImage();
            actionMode = Actor.ActionMode.NONE;
        }
    }

    protected void updateImage() {
        //to be overridden
    }

    protected void setImage(String name) {
        BufferedImage im = registry.getImageLoader().getImage(name);
        if (im != null) {
            image = name;

            width = im.getWidth();
            height = im.getHeight();
            isAnimating = false;
        }
    }

    public int attackDamageAndKnockBack(Actor source, Arc2D.Double arc, Point mapPoint, int damage, int kbX, int kbY, String weaponType) {
        int damageTaken = 0;
        if (registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.CLIENT) {
            //System.out.println(spriteRect.intersects(r) + ":" + hitPoints);
            if (arc.intersects(spriteRect) && hitPoints > 0) {
                int range = 3 * Math.abs(kbX) / 4;
                if (range < 1) {
                    range = 1;
                }

                int randX = Rand.getRange(1, range);
                int baseX = Math.abs(kbX) / 4;

                if (kbX < 0) {
                    kbX = baseX + randX;
                    kbX = -1 * kbX;
                } else {
                    kbX = baseX + randX;
                }

                damageTaken = applyDamage(damage, source);

                if (!disregardKnockBack) {
                    applyKnockBack(kbX, kbY);
                }
            }
            //System.out.println(spriteRect.x+","+spriteRect.y+" "+spriteRect.width+","+spriteRect.height+" "+r.x+","+r.y+" "+r.width+","+r.height);
        }
        return damageTaken;
    }

    public void applyKnockBack(int x, int y) {
        knockBackX = x;
        jump(y);
        if (registry.getGameController().multiplayerMode == registry.getGameController().multiplayerMode.SERVER && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdateMonster um = new UpdateMonster(this.getId());
                um.mapX = this.getMapX();
                um.mapY = this.getMapY();
                um.action = "ApplyKnockBack";
                um.dataInt = x;
                um.dataInt2 = y;
                registry.getNetworkThread().sendData(um);
            }
        }
    }

    public int getKnockBackX() {
        return knockBackX;
    }

    public int applyDamage(int damage, Actor a) {
        if (damage <= 0) {
            return 0;
        }

        damage -= Math.floor(getArmorPoints() / 5);

        registerAttacker(a, damage);

        if (damage <= 0) {
            damage = 1;
        }

        hitPoints -= damage;
        registry.getIndicatorManager().createIndicator(mapX + (width / 2), mapY + 50, "-" + Integer.toString(damage));
        SoundClip cl = new SoundClip(registry, "Weapon/Hit", getCenterPoint());

        return damage;
    }

    protected void registerAttacker(Actor a, int d) {
        if (a != null && d > 0) {
            lastAttacker = a;

            if (attackers == null) {
                attackers = new HashMap<Actor, Integer>();
            }

            if (attackers.containsKey(a)) {
                attackers.put(a, attackers.get(a) + d);
            } else {
                attackers.put(a, d);
            }
        }
    }

    public Actor getLastAttacker() {
        return lastAttacker;
    }

    public Arc2D.Double getAttackArc() {
        return getAttackArc(attackRange);
    }

    public Arc2D.Double getAttackArc(int range) {
        Arc2D.Double arc = null;
        if (spriteRect != null) {
            arc = new Arc2D.Double(
                    (double) spriteRect.x - range + spriteRect.width / 2 + attackArcOffsetX,
                    (double) spriteRect.y - range + spriteRect.height / 2 + attackArcOffsetY,
                    2 * range,
                    2 * range,
                    -90,
                    180,
                    Arc2D.PIE);
            if (facing == Facing.LEFT) {
                arc = new Arc2D.Double(
                        (double) spriteRect.x - range + spriteRect.width / 2 + attackArcOffsetX,
                        (double) spriteRect.y - range + spriteRect.height / 2 + attackArcOffsetY,
                        2 * range,
                        2 * range,
                        90,
                        180,
                        Arc2D.PIE);
            }
        }
        return arc;
    }

    public void doChat(long duration) {
        chatEnd = registry.currentTime + duration;
        isChatting = true;
    }

    public void stopChat() {
        chatEnd = 0;
        isChatting = false;
    }

    public boolean getIsDead() {
        return isDead;
    }

    public Rectangle getSpriteRect() {
        return spriteRect;
    }

    public boolean getStateChanged() {
        return stateChanged;
    }

    public boolean getCanFly() {
        return canFly;
    }

    protected void setStatuses() {
        statusFear = false;
        if (isFeared) {
            statusFear = true;
        }
        statusSlowed = false;
        if (isSlowed) {
            statusSlowed = true;
        }
        statusPoison = false;
        if (isPoisoned) {
            statusPoison = true;
        }
    }

    public void attack() {
        //to be overridden
    }

    public void meleeAttack(WeaponType newWeaponType, int level) {
        //to be overridden
    }

    protected void loopImage(String name, double fDuration) {
        if (registry.getImageLoader().numImages(name) > 1) {
            image = name;

            BufferedImage im = registry.getImageLoader().getImage(name);

            numAnimationFrames = registry.getImageLoader().numImages(name);

            width = im.getWidth();
            height = im.getHeight();

            currentAnimationFrame = 0;
            animationFrameDuration = (int) (1000 * fDuration);
            animationFrameUpdateTime = (long) (registry.currentTime + animationFrameDuration);

            isAnimating = true;
        } else {
            setImage(name);
        }
    }

    protected void loopImage(String name) {
        loopImage(name, DEFAULT_ANIMATION_DURATION);
    }

    public int checkCollide(int xMove) {
        //EIError.debugMsg("checkCollide "+xMove+" "+(mapX+baseOffset)+" "+mapX+" "+baseWidth+" "+(height-topOffset));
        int xCanMove = 0;
        boolean hit = false;
        int blockWidth = manager.getBlockWidth();
        int blockHeight = manager.getBlockHeight();
        int[] y = ycm;
        y[0] = y[1] = 0;

        int xMoveABS = Math.abs(xMove);
        for (xCanMove = 0; xCanMove < xMoveABS && !hit; xCanMove += blockWidth - 1) {
            y = getYByVertMoveMode(xCanMove, xMoveABS);
            hit = manager.doesRectContainBlocks(mapX + baseOffset + (xMove > 0 ? xCanMove : -xCanMove), mapY + y[0], baseWidth, height - topOffset);
            //EIError.debugMsg("Find first hit X "+xCanMove+" Y "+y[0]+" Hit "+hit);
        }
        hit = manager.doesRectContainBlocks(mapX + baseOffset + (xMove > 0 ? xCanMove : -xCanMove), mapY + y[0], baseWidth, height - topOffset);
        if (!hit && xCanMove > xMoveABS) {
            xCanMove = xMoveABS;
            y = getYByVertMoveMode(xCanMove, xMoveABS);
            hit = manager.doesRectContainBlocks(mapX + baseOffset + (xMove > 0 ? xCanMove : -xCanMove), mapY + y[0], baseWidth, height - topOffset);
            //EIError.debugMsg("Back off from last loop X "+xCanMove+" Y "+y[0]+" Hit "+hit);
        }
        if (hit && xCanMove > 0) {
            xCanMove = getCollideXBackoffToBlock((xMove > 0 ? xCanMove : -xCanMove), blockWidth);
            if (xCanMove > 0) {
                xCanMove--;
            }
            //EIError.debugMsg("Back off from hit X "+xCanMove+" Y "+y[0]+" Hit "+hit);
        }
        if (vertMoveMode == VertMoveMode.NOT_JUMPING) {
            if (xCanMove > xMoveABS) {
                xCanMove = xMoveABS;
                hit = manager.doesRectContainBlocks(mapX + baseOffset + (xMove > 0 ? xCanMove : -xCanMove), mapY + y[0], baseWidth, height - topOffset);
            }
            if (hit) {
                y = stepUp(xMove, xCanMove, blockWidth, blockHeight);
                xCanMove = y[1];
                //EIError.debugMsg("Step up X "+xCanMove+" Y "+y[0]+" Hit "+hit);
            }
        } else {
            y = fixCollideY(y, xCanMove, xMove, blockHeight);
            if (y[0] == 0 && xCanMove == 0 && vertMoveMode == VertMoveMode.FLYING) {
                //try ceiling slide
                xCanMove++;
                hit = manager.doesRectContainBlocks(mapX + baseOffset + (xMove > 0 ? xCanMove : -xCanMove), mapY, baseWidth, height - topOffset);
                if (hit) {
                    xCanMove = 0;
                }
                //EIError.debugMsg("Ceiling slide X "+xCanMove+" Y "+y[0]+" Hit "+hit);
            }
        }
        if (manager.doesRectContainBlocks(mapX + baseOffset + (xMove > 0 ? xCanMove : -xCanMove), mapY + y[0], baseWidth, height - topOffset)) {
            y[0] = 0;
            xCanMove = 0;
        }
        mapY += y[0];
        return xCanMove;
    }

    public int[] stepUp(int xMove, int xCanMove, int blockWidth, int blockHeight) {
        yx[0] = 0;
        yx[1] = xCanMove;
        boolean hit = false;
        hit = manager.doesRectContainBlocks(mapX + baseOffset + (xMove > 0 ? (xCanMove + blockWidth / 8) : -(xCanMove + blockWidth / 8)), mapY + blockHeight, baseWidth, height - topOffset);
        if (!hit) {
            yx[0] = blockHeight;
            yx[1] = xCanMove + blockWidth / 8;
        }
        //registry.getIndicatorManager().createIndicator(mapX + (width / 2), mapY + 50, Integer.toString(yx[1]));
        return yx;
    }

    public int[] fixCollideY(int[] y, int xCanMove, int xMove, int blockHeight) {
        boolean hit = false;
        int xMoveABS = Math.abs(xMove);
        y = getYByVertMoveMode(xCanMove, xMoveABS);
        hit = manager.doesRectContainBlocks(mapX + baseOffset + (xMove > 0 ? xCanMove : -xCanMove), mapY + y[0], baseWidth, height - topOffset);
        if (hit) {
            y = backOffY(y, (xMove > 0 ? xCanMove : -xCanMove), blockHeight);
            y[1] = y[0];
            //EIError.debugMsg("Back off y X "+xCanMove+" Y "+y[0]+" Hit "+hit);
        }
        if (y[0] > y[1]) {
            //falling
            hit = false;
            for (; y[0] > y[1] && !hit; y[0] -= blockHeight) {
                hit = manager.doesRectContainBlocks(mapX + baseOffset + (xMove > 0 ? xCanMove : -xCanMove), mapY + y[0], baseWidth, height - topOffset);
            }
            if (y[0] < y[1]) {
                y[0] = y[1];
            }
            //EIError.debugMsg("Falling backoff y X "+xCanMove+" Y "+y[0]+" Hit "+hit);
            hit = manager.doesRectContainBlocks(mapX + baseOffset + (xMove > 0 ? xCanMove : -xCanMove), mapY + y[0], baseWidth, height - topOffset);
            if (hit) {
                y = backOffY(y, (xMove > 0 ? xCanMove : -xCanMove), blockHeight);
                //EIError.debugMsg("Falling backoff y X "+xCanMove+" Y "+y[0]+" Hit "+hit);
            }
        } else if (y[0] < y[1]) {
            //jumping
            hit = false;
            for (; y[0] < y[1] && !hit; y[0] += blockHeight) {
                hit = manager.doesRectContainBlocks(mapX + baseOffset + (xMove > 0 ? xCanMove : -xCanMove), mapY + y[0], baseWidth, height - topOffset);
            }
            if (y[0] > y[1]) {
                y[0] = y[1];
            }
            //EIError.debugMsg("Jumping backoff y X "+xCanMove+" Y "+y[0]+" Hit "+hit);
            hit = manager.doesRectContainBlocks(mapX + baseOffset + (xMove > 0 ? xCanMove : -xCanMove), mapY + y[0], baseWidth, height - topOffset);
            if (hit && y[0] != 0) {
                y = backOffY(y, (xMove > 0 ? xCanMove : -xCanMove), blockHeight);
                y[0]--;
                //EIError.debugMsg("Jumping backoff y X "+xCanMove+" Y "+y[0]+" Hit "+hit);
            }
        }
        return y;
    }

    public int[] backOffY(int[] y, int x, int blockHeight) {
        boolean hit = true;
        if (y[0] < 0) {
            y[0] = getCollideYBackoffToBlock(y[0], blockHeight);
            for (; y[0] < 0 && hit;) {
                hit = manager.doesRectContainBlocks(mapX + baseOffset + x, mapY + y[0], baseWidth, height - topOffset);
                if (hit) {
                    y[0] = getCollideYBackoffToBlock(y[0] + 1, blockHeight);
                }
            }
        } else if (y[0] > 0) {
            y[0] = getCollideYBackoffToBlock(y[0], blockHeight) - 1;
            for (; y[0] > 0 && hit;) {
                hit = manager.doesRectContainBlocks(mapX + baseOffset + x, mapY + y[0], baseWidth, height - topOffset);
                if (hit) {
                    y[0] = getCollideYBackoffToBlock(y[0] - 1, blockHeight) - 1;
                }
            }
        }
        return y;
    }

    protected BufferedImage flipHorizontal(BufferedImage bi) {
        AffineTransform tx;
        AffineTransformOp op;

        tx = AffineTransform.getScaleInstance(1, -1);
        tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-bi.getWidth(), 0);
        op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        bi = op.filter(bi, null);

        return bi;
    }

    private int getCollideXBackoffToBlock(int xPosChange, int blockWidth) {
        int xPosNew = mapX + baseOffset + xPosChange;
        if (xPosChange > 0) {
            xPosNew += baseWidth;
        }
        xPosNew = (xPosNew / blockWidth) * blockWidth;
        if (xPosChange < 0) {
            xPosNew += blockWidth;
        }
        xPosNew -= mapX + baseOffset;
        if (xPosChange > 0) {
            xPosNew -= baseWidth;
        }
        //EIError.debugMsg("getCollideXBackoffToBlock "+xPosNew);
        return Math.abs(xPosNew);
    }

    private int getCollideYBackoffToBlock(int yPosChange, int blockHeight) {
        int yPosNew = mapY + yPosChange;
        if (yPosChange > 0) {
            yPosNew += height - topOffset;
        }
        yPosNew = (yPosNew / blockHeight) * blockHeight;
        if (yPosChange < 0) {
            yPosNew += blockHeight;
        }
        yPosNew -= mapY;
        if (yPosChange > 0) {
            yPosNew -= height - topOffset;
        }
        return yPosNew;
    }

    private int[] getYByVertMoveMode(int i, int xMove) {
        int[] y = new int[2];
        y[0] = y[1] = 0;
        if (vertMoveMode == VertMoveMode.FALLING) {
            y[1] = -fallSize;
            if (i != 0) {
                y[0] = -1 * (int) ((float) (fallSize * i) / (float) xMove);
            }
            if (y[0] < y[1]) {
                y[0] = y[1];
            }
        } else if (vertMoveMode == VertMoveMode.JUMPING) {
            y[1] = jumpSize;
            if (i != 0) {
                y[0] = (int) ((float) (jumpSize * i) / (float) xMove);
            }
            if (y[0] > y[1]) {
                y[0] = y[1];
            }
        } else if (vertMoveMode == VertMoveMode.FLYING) {
            y[1] = ascendSize;
            if (i != 0) {
                y[0] = (int) ((float) (ascendSize * i) / (float) xMove);
            }
            if (y[0] > y[1]) {
                y[0] = y[1];
            }
        }
        return y;
    }

    public int checkCollideRight() {
        return checkCollide(xMoveSize);
    }

    public int checkCollideLeft() {
        return checkCollide(-1 * xMoveSize);
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

    public Point getCenterPoint() {
        if (spriteRect == null) {
            int offsetX = -1 * spriteRectOffestX;
            if (facing == Facing.RIGHT) {
                offsetX = spriteRectOffestX;
            }
            spriteRect = new Rectangle(mapX + baseOffset + offsetX, mapY - topOffset + spriteRectOffestY, baseWidth, height - topOffset);
        }

        Point p = new Point(spriteRect.x + spriteRect.width / 2, spriteRect.y + spriteRect.height / 2);

        return p;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isFeared() {
        return isFeared;
    }

    public boolean isSlowed() {
        return isSlowed;
    }

    public boolean isPoisoned() {
        return isPoisoned;
    }

    public boolean isWithinAttackRange(Point p) {
        if (getCenterPoint().distance(p) <= attackRange) {
            return true;
        } else {
            return false;
        }
    }

    public AttackType getCurrentAttackType() {
        return currentAttackType;
    }

    public int getAttankRange() {
        return attackRange;
    }

    public Rectangle getAttackRect(int range) {
        Rectangle rect = new Rectangle(mapX + baseOffset + baseWidth / 2, mapY - topOffset - range, baseWidth / 2 + range, height + range * 2);
        if (facing == Facing.LEFT) {
            rect = new Rectangle(mapX + baseOffset - range, mapY - topOffset - range, baseWidth / 2 + range, height + range * 2);
        }
        return rect;
    }

    public void setActive(boolean a) {
        isActive = a;
    }

    public void setMapX(int x) {
        mapX = x;
    }

    public void setPosition(int x, int y) {
        mapX = x;
        mapY = y;
    }

    public void setFacing(Facing f) {
        stateChanged = true;
        facing = f;
    }

    public void setActionMode(ActionMode a) {
        actionMode = a;
    }

    public void setFallSize(int fs) {
        fallSize = fs;
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public Facing getFacing() {
        return facing;
    }

    public ActionMode getActionMode() {
        return actionMode;
    }

    public boolean getIsTryingToMove() {
        return isTryingToMove;
    }

    public boolean getIsFollowing() {
        //meant to be overridden - specially for the robot
        return false;
    }

    public Boolean getShowRect() {
        return showRect;
    }

    public Rectangle getPerimeter() {
        return new Rectangle(mapX, mapY, width, height);
    }

    protected void checkIfFalling() {
        if (vertMoveMode != VertMoveMode.JUMPING && vertMoveMode != VertMoveMode.FLYING) {
            Point p = null;
            boolean falling = true;
            if (manager.doesRectContainBlocks(mapX + baseOffset, mapY - 1, baseWidth, height - topOffset)) {
                falling = false;
                knockBackX = 0;
            }
            if (falling == true) {
                totalFall = 0;
                setVertMoveMode(VertMoveMode.FALLING, false);
            } else if (falling != true && vertMoveMode != VertMoveMode.NOT_JUMPING) {
                setVertMoveMode(VertMoveMode.NOT_JUMPING, true);
            }
        }
    }

    public void updateJumping() {
        //check if we can keep jumping or not
        if (jumpSize < 0) {
            //player reached the top of the jump
            jumpSize = startJumpSize;
            setFallSize(0);
            setVertMoveMode(VertMoveMode.FALLING);
        } else {
            if (mapY - lastMapY < jumpSize && (mapY - lastMapY) > 0) {
                setVertMoveMode(VertMoveMode.FALLING, false);
            }
            jumpSize -= gravity;
        }
    }

    public void updateAscending() {
        //check if we can keep ascending or not
        if (ascendSize < 0) {
            //player reached the top of the ascend
            ascendSize = ascendOriginalSize;
            ascendCount = 0;
            fallSize = 0;
            setVertMoveMode(VertMoveMode.FALLING, false);
        } else {
            if (ascendCount >= ascendMax) {
                ascendSize -= gravity;
            }
            ascendCount++;
        }
    }

    public void updateFalling() {
        if(this.getTotalHitPoints() == 6000) {
            //System.out.println(lastMapY + ":" + mapY + ":" + fallSize);
        }
        if (lastMapY - mapY < fallSize && fallSize > 0) {
            finishJumping();
        } else {
            totalFall += fallSize;
        }
        fallSize += gravity;
        completeFall += gravity;
        if (fallSize > maxFallSize) {
            fallSize = maxFallSize;
        }
    }

    protected void finishJumping() {
        //EIError.debugMsg("finishJumping");
        setVertMoveMode(VertMoveMode.NOT_JUMPING);
        totalFall = 0;
        fallSize = 0;
        knockBackX = 0;

        if (!isTryingToMove) {
            stopMove();
        } else {
            if (facing == Facing.RIGHT) {
                moveRight();
            } else {
                moveLeft();
            }
        }
    }

    public void setShowRect(boolean r) {
        showRect = r;
    }

    public void setShowGoals(boolean g) {
        showGoals = g;
    }

    public void setStateChanged(boolean s) {
        if (s) {
            stateChanged = s;
        }
    }

    public void setDebugInfo(String d) {
        debugInfo = d;
    }

    public void setVertMoveMode(VertMoveMode v) {
        //System.out.println("VertMoveMode "+v);
        setVertMoveMode(v, true);
    }

    public Actor.VertMoveMode getVertMoveMode() {
        return vertMoveMode;
    }

    public void setVertMoveMode(VertMoveMode v, boolean sc) {
        if (sc) {
            stateChanged = sc;
        }
        vertMoveMode = v;
        if(vertMoveMode == VertMoveMode.NOT_JUMPING) {
            completeFall = 0;
        }
    }

    public void setIsTryingToMove(boolean m) {
        isTryingToMove = m;
    }

    public void update() {
        lastMapY = mapY;
        spriteRect = null;
        int offsetX = -1 * spriteRectOffestX;
        if (facing == Facing.RIGHT) {
            offsetX = spriteRectOffestX;
        }
        spriteRect = new Rectangle(mapX + baseOffset + offsetX, mapY - topOffset + spriteRectOffestY, baseWidth, height - topOffset);

        if (isChatting && registry.currentTime >= chatEnd) {
            isChatting = false;
        }

        if (statusStun && registry.currentTime >= stunEnded) {
            this.setStatusStun(false, 0);
            if (registry.getGameController().multiplayerMode == registry.getGameController().multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                if (registry.getNetworkThread().readyForUpdates) {
                    UpdatePlayer up = new UpdatePlayer(this.getId());
                    up.mapX = this.getMapX();
                    up.mapY = this.getMapY();
                    up.vertMoveMode = this.getVertMoveMode();
                    up.dataBoolean = false;
                    up.dataLong = 0;
                    up.action = "SetStun";
                    registry.getNetworkThread().sendData(up);
                }
            }
        }
        if (isActive && isFeared) {
            long p = registry.getImageLoader().getPeriod();
            fearedTotalTime += (registry.getImageLoader().getPeriod())
                    % (long) (1000 * fearedDuration);

            // calculate current displayable image position
            if (fearedTotalTime >= (fearedDuration * 1000)) {
                unfear();
            }
        }
        if (isActive && isSlowed) {
            long p = registry.getImageLoader().getPeriod();
            slowedTotalTime += (registry.getImageLoader().getPeriod())
                    % (long) (1000 * slowedDuration);

            // calculate current displayable image position
            if (slowedTotalTime >= (slowedDuration * 1000)) {
                unslow();
            }
        }
        if (isActive && isPoisoned) {
            long p = registry.getImageLoader().getPeriod();
            poisonedTotalTime += (registry.getImageLoader().getPeriod())
                    % (long) (1000 * poisonedDuration);

            // calculate current displayable image position
            if (poisonedTotalTime >= (poisonedDuration * 1000)) {
                unpoison();
            }
        }

        setStatuses();
    }

    public void render(Graphics g) {
        int xPos = 0, yPos = 0;
        BufferedImage im;
        BufferedImage imLeft;
        AffineTransform tx;
        AffineTransformOp op;

        if (isChatting) {
            im = registry.getImageLoader().getImage("Misc/ChatBubble");

            if (facing == Facing.LEFT) {
                xPos = manager.mapToPanelX(mapX - width + 25);
                yPos = manager.mapToPanelY(mapY + 10);
            } else {
                xPos = manager.mapToPanelX(mapX + width - 25);
                yPos = manager.mapToPanelY(mapY + 10);
            }

            //flip the yPos since drawing happens top down versus bottom up
            yPos = manager.getPHeight() - yPos;

            //subtract the height since points are bottom left and drawing starts from top left
            yPos -= height;

            if (im != null) {
                if (facing == Facing.LEFT) {
                    tx = AffineTransform.getScaleInstance(1, -1);
                    tx = AffineTransform.getScaleInstance(-1, 1);
                    tx.translate(-width, 0);
                    op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                    imLeft = op.filter(im, null);
                    if (imLeft != null) {
                        g.drawImage(imLeft, xPos, yPos, null);
                    }
                } else {
                    g.drawImage(im, xPos, yPos, null);
                }
            }
        }

        if (showRect && spriteRect != null) {
            xPos = manager.mapToPanelX(spriteRect.x);
            yPos = manager.mapToPanelY(spriteRect.y);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = manager.getPHeight() - yPos;

            //subtract the height since points are bottom left and drawing starts from top left
            yPos -= height;

            g.setColor(Color.red);
            g.drawRect(xPos, yPos, spriteRect.width, spriteRect.height);
        }

        if (showGoals && debugInfo != null) {
            xPos = manager.mapToPanelX(mapX);
            yPos = manager.mapToPanelY(mapY + height + 20);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = manager.getPHeight() - yPos;

            //subtract the height since points are bottom left and drawing starts from top left
            yPos -= height;

            String[] parts = debugInfo.split("\\|");
            for (int i = 0; i < parts.length; i++) {
                g.setColor(Color.RED);
                g.setFont(new Font("SansSerif", Font.BOLD, 18));
                g.drawString(parts[i], xPos, yPos + (i * 15));
            }
        }

        if (showRect && attackRect != null) {
            xPos = manager.mapToPanelX(attackRect.x);
            yPos = manager.mapToPanelY(attackRect.y);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = manager.getPHeight() - yPos;

            //subtract the height since points are bottom left and drawing starts from top left
            yPos -= attackRect.height + topOffset;


            g.setColor(Color.red);
            g.drawRect(xPos, yPos, attackRect.width, attackRect.height);
        }
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}
