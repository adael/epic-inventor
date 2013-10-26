package com.weem.epicinventor.actor;

import com.weem.epicinventor.*;
import com.weem.epicinventor.ai.*;
import com.weem.epicinventor.armor.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.network.*;
import com.weem.epicinventor.utility.*;
import com.weem.epicinventor.weapon.*;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.util.*;

public class Robot extends Actor implements Serializable {

    private static final long serialVersionUID = -1914405547599673126L;
    transient protected PlayerManager playerManager;
    transient protected Player player;
    transient protected AI ai;
    private String name;
    private Inventory inventory;
    private int availableSlots;
    private boolean isActivated;
    private long batteryTimeRemaining = 300000;
    private long batteryTimeTotal = 300000;
    private int batteryRechargeMultiplier = 5;
    private final static int BASE_BATTERY_TIME = 300000;
    private final static int BASE_BATTERY_RECHARGE_MULTIPLIER = 5;
    private String mode;
    private boolean isFollowing;
    private boolean invulnerable;
    private boolean invulnerableShow;
    private float invulnerableTotalTime;
    transient private float fallDamageMultiplier = 3.5f;
    private float INVULNERABLE_MAX_TIME = 1.0f;
    private final static int INVENTORY_SIZE = 4;
    transient private BufferedImage imageShieldLeft;
    transient private BufferedImage imageShieldRight;
    private long attachmentAnimationTotalTime;
    private double attachmentAnimationFrameDuration; //duration of a single frame
    protected long attachmentAnimationFrameUpdateTime = 0;
    private int currentAttachmentAnimationFrame;
    private int numAttachmentAnimationFrames;
    private long meleeAnimationTotalTime;
    private double meleeAnimationFrameDuration; //duration of a single frame
    private int currentMeleeAnimationFrame;
    private int numMeleeAnimationFrames;
    private WeaponType meleeWeaponType;
    private int weaponLevel;
    transient private BufferedImage[] meleeImages;
    private boolean isSwinging;

    public Robot(PlayerManager pm, Player p, Registry rg, String im, int x) {
        super(pm, rg, im, x, 0);

        playerManager = pm;
        player = p;

        isStill = true;
        facing = Facing.RIGHT;
        setVertMoveMode(VertMoveMode.NOT_JUMPING);

        hitPoints = baseHitPoints;

        ai = new AI(registry, this);

        topOffset = 14;
        baseOffset = 23;
        baseWidth = 15;
        startJumpSize = 20;
        jumpSize = 8;
        fallSize = 0;
        completeFall = 0;

        xMoveSize = 4;

        mode = "Defensive";
        isFollowing = true;

        inventory = new Inventory(rg, INVENTORY_SIZE);
        availableSlots = 4;
        currentAttackType = AttackType.MELEE;

        //create shield images
        imageShieldRight = registry.getImageLoader().getImage("Robot/Shield");
        imageShieldLeft = registry.getImageLoader().getImage("Robot/Shield");
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-imageShieldRight.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        imageShieldLeft = op.filter(imageShieldRight, null);

        ai.clearGoals();
        ai.setPlayer(player.getId());
        ai.addGoal(AI.GoalType.STARE, p.getId(), 1);
        ai.addGoal(AI.GoalType.FOLLOW, p.getId(), 1);
        ai.addGoal(AI.GoalType.ATTACK_MOBS, null, 1);
        ai.activate();
    }

    public void init() {
        mapY = playerManager.findFloor(mapX);
    }

    public void setTransient(Registry rg, Player p) {
        yx = new int[2];
        ycm = new int[2];

        playerManager = rg.getPlayerManager();
        registry = rg;
        manager = rg.getPlayerManager();
        player = p;
        fallDamageMultiplier = 3.5f;

        inventory.setTransient(rg);

        imageShieldRight = registry.getImageLoader().getImage("Robot/Shield");
        imageShieldLeft = registry.getImageLoader().getImage("Robot/Shield");
        attackArcOffsetX = 0;
        attackArcOffsetY = 0;
        
        this.updateArmorPoints();

        ai = new AI(registry, this);
        ai.clearGoals();
        ai.setPlayer(player.getId());
        ai.addGoal(AI.GoalType.STARE, player.getId(), 1);
        ai.addGoal(AI.GoalType.FOLLOW, player.getId(), 1);
        ai.addGoal(AI.GoalType.ATTACK_MOBS, null, 1);
        ai.activate();

        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public boolean getIsActivated() {
        return isActivated;
    }

    @Override
    public boolean getIsFollowing() {
        return isFollowing;
    }

    public Player getPlayer() {
        return player;
    }

    public String getMode() {
        return mode;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getInventorySize() {
        return availableSlots;
    }

    public int getBatteryPercentage() {
        int batteryPercentage = batteryPercentage = (int) (((float) batteryTimeRemaining / (float) batteryTimeTotal) * 100);

        if (batteryPercentage > 100) {
            batteryPercentage = 100;
        }

        if (batteryPercentage <= 1) {
            batteryPercentage = 1;
        }

        return batteryPercentage;
    }

    public boolean isMoving() {
        if (isStill && vertMoveMode == VertMoveMode.NOT_JUMPING) {
            return false;
        } else {
            return true;
        }
    }

    public void setIsActivated(boolean a) {
        isActivated = a;
    }

    public void setMode(String m) {
        mode = m;
    }

    public void setName(String n) {
        name = n;
    }

    public void setIsFollowing(boolean f) {
        isFollowing = f;
    }

    public void setFallSize(int fs) {
        fallSize = fs;
    }

    public void toggleActivated() {
        toggleActivated(mapX, mapY, false);
    }

    public void toggleFollow() {
        isFollowing = !isFollowing;
    }

    public void toggleActivated(int x, int y, boolean isSpawn) {
        isActivated = !isActivated;
        if (isActivated) {
            if (this.getBatteryPercentage() < 20) {
                isActivated = false;
                if (!isSpawn && player == playerManager.getCurrentPlayer()) {
                    registry.showMessage("Error", "Your robot must have at least a 20% charge to be activated");
                }
            } else {
                mapX = x;
                mapY = y;
                actionMode = ActionMode.NONE;
                stopMove();
                jump(10);
                if (!isSpawn) {
                    SoundClip cl = new SoundClip(registry, "Robot/PowerUp", getCenterPoint());
                }
            }
        } else {
            player.setInsideRobot(false);
            if (!isSpawn) {
                SoundClip cl = new SoundClip(registry, "Robot/PowerDown", getCenterPoint());
            }
        }
    }

    public void stopJump() {
        if (vertMoveMode == VertMoveMode.JUMPING) {
            setVertMoveMode(VertMoveMode.FALLING);
        }
    }

    @Override
    public void attack() {
        if (actionMode != ActionMode.ATTACKING && attackRefreshTimerEnd < System.currentTimeMillis()) {
            meleeAttack(meleeWeaponType, weaponLevel);
        }
    }

    @Override
    public void meleeAttack(WeaponType newWeaponType, int level) {
        int kbX = 20;
        int kbY = 5;
        int damage = (player.getAttackBonus() * 2);
        int maxHits = 2;
        int weaponSpeed = 600;

        if (newWeaponType != null) {
            int[] damages = newWeaponType.getDamage();

            kbX = newWeaponType.getKnockBackX();
            kbY = newWeaponType.getKnockBackY();
            damage += damages[level];
            maxHits = newWeaponType.getMaxHits();
            weaponSpeed = newWeaponType.getSpeed();
        }

        isSwinging = true;
        actionMode = ActionMode.ATTACKING;
        stateChanged = true;
        attackRefreshTimerStart = System.currentTimeMillis();
        attackRefreshTimerEnd = System.currentTimeMillis() + weaponSpeed;

        attackArc = getAttackArc();
        if (facing == Facing.LEFT) {
            kbX = -1 * kbX;
        }
        
        String itemName = "";
        if(newWeaponType != null) {
            itemName = newWeaponType.getItemName();
        }
        playerManager.attackDamageAndKnockBack(this, attackArc, null, damage, kbX, kbY, maxHits, itemName);
    }

    public void updateArmorPoints() {
        int[] bonuses;
        int l = 0;
        
        armorPoints = 0;
        
        if (inventory.contains("RobotScrapArmor")) {
            ArmorType at = Armor.getArmorType("RobotScrapArmorHead");
            if(at != null) {
                bonuses = at.getArmorBonus();
                l = inventory.getLevelForType("RobotScrapArmor");
                armorPoints = bonuses[l];
                return;
            }
        }
        if (inventory.contains("RobotGoldArmor")) {
            ArmorType at = Armor.getArmorType("RobotGoldArmorHead");
            if(at != null) {
                bonuses = at.getArmorBonus();
                l = inventory.getLevelForType("RobotGoldArmor");
                armorPoints = bonuses[l];
                return;
            }
        }
        if (inventory.contains("RobotSilverArmor")) {
            ArmorType at = Armor.getArmorType("RobotSilverArmorHead");
            if(at != null) {
                bonuses = at.getArmorBonus();
                l = inventory.getLevelForType("RobotSilverArmor");
                armorPoints = bonuses[l];
                return;
            }
        }
        if (inventory.contains("RobotIronArmor")) {
            ArmorType at = Armor.getArmorType("RobotIronArmorHead");
            if(at != null) {
                bonuses = at.getArmorBonus();
                l = inventory.getLevelForType("RobotIronArmor");
                armorPoints = bonuses[l];
                return;
            }
        }
        if (inventory.contains("RobotCopperArmor")) {
            ArmorType at = Armor.getArmorType("RobotCopperArmorHead");
            if(at != null) {
                bonuses = at.getArmorBonus();
                l = inventory.getLevelForType("RobotCopperArmor");
                armorPoints = bonuses[l];
                return;
            }
        }
        if (inventory.contains("RobotWoodArmor")) {
            ArmorType at = Armor.getArmorType("RobotWoodArmorHead");
            if(at != null) {
                bonuses = at.getArmorBonus();
                l = inventory.getLevelForType("RobotWoodArmor");
                armorPoints = bonuses[l];
                return;
            }
        }
    }

    @Override
    public int applyDamage(int damage, Actor a) {
        if (damage <= 0) {
            return 0;
        }

        damage -= getArmorPoints();

        registerAttacker(a, damage);

        if (damage <= 0) {
            damage = 1;
        }

        if (damage > 0) {
            float playerPercent = (float) player.getAdjustedDamage(damage) / (float) player.getTotalHitPoints();
            batteryTimeRemaining -= batteryTimeTotal * playerPercent;
            invulnerable = true;
            SoundClip cl = new SoundClip("Robot/Hurt" + Rand.getRange(1, 3));

            if (registry.getGameController().multiplayerMode == registry.getGameController().multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                if (registry.getNetworkThread().readyForUpdates) {
                    UpdateRobot ur = new UpdateRobot(player.getId(), this.getId());
                    ur.mapX = this.getMapX();
                    ur.mapY = this.getMapY();
                    ur.action = "ApplyDamage";
                    ur.dataInt = damage;
                    ur.actor = a;
                    registry.getNetworkThread().sendData(ur);
                }
            }
        }

        return damage;
    }

    private void createMeleeFrames() {
        AffineTransform tx = null;
        AffineTransformOp op = null;

        meleeImages = null;
        meleeImages = new BufferedImage[16];
        meleeImages[0] = null;

        if (meleeWeaponType != null) {
            BufferedImage im = registry.getImageLoader().getImage("Attachments/" + meleeWeaponType.getItemName());

            meleeAnimationFrameDuration = 0.05;
            numMeleeAnimationFrames = 8;
            currentMeleeAnimationFrame = 0;

            //right
            for (int i = 0; i < 8; i++) {
                int rotation = i * 45;

                tx = new AffineTransform();
                tx.rotate(Math.toRadians(rotation), im.getWidth() / 2, im.getHeight() / 2);

                op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                meleeImages[i] = op.filter(im, null);
            }

            //left
            for (int i = 8; i < 16; i++) {
                int rotation = i * 45;
                rotation += 180;

                tx = new AffineTransform();
                tx.rotate(Math.toRadians(-rotation), im.getWidth() / 2, im.getHeight() / 2);

                op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                meleeImages[i] = op.filter(im, null);
            }
        }
    }

    @Override
    public void update() {
        if (isActivated) {
            super.update();

            //calculate current battery capacity
            batteryTimeTotal = BASE_BATTERY_TIME;
            if (inventory.contains("BlueBattery")) {
                batteryTimeTotal += 2.5 * 60 * 1000;
            }
            //set the attachment animations up
            if (inventory.contains("Propeller")) {
                attachmentAnimationFrameDuration = 10;
                numAttachmentAnimationFrames = 9;
            }

            if (isActive && isAnimating) {
                if (animationFrameUpdateTime <= registry.currentTime) {
                    currentAnimationFrame++;
                    if (currentAnimationFrame > numAnimationFrames) {
                        currentAnimationFrame = 0;
                    }
                    animationFrameUpdateTime = registry.currentTime + animationFrameDuration;
                }

                //melee animation update
                if (meleeWeaponType != null && meleeAnimationFrameDuration > 0) {
                    meleeAnimationTotalTime = (meleeAnimationTotalTime
                            + registry.getImageLoader().getPeriod())
                            % (long) (1000 * meleeAnimationFrameDuration * numMeleeAnimationFrames);

                    // calculate current displayable image position
                    currentMeleeAnimationFrame = (int) (meleeAnimationTotalTime / (meleeAnimationFrameDuration * 1000));
                    if (currentMeleeAnimationFrame >= numMeleeAnimationFrames) {
                        currentMeleeAnimationFrame = 0;
                    }
                }
            }

            if (player.getInsideRobot()) {
                if (hitPoints > 0) {
                    mapX = player.getMapX();
                    mapY = player.getMapY();
                } else {
                    SoundClip cl = new SoundClip(registry, "Robot/Die", getCenterPoint());
                    isDead = true;
                    ai.terminate();
                    player.setInsideRobot(false);
                }

//                if (vertMoveMode == VertMoveMode.JUMPING) {
//                    updateJumping();
//                } else if (vertMoveMode == VertMoveMode.FALLING) {
//                    updateFalling();
//                }

                //check to see if robot is touching an enemy
                if (!invulnerable && registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.CLIENT) {
                    Damage damage = playerManager.getMonsterTouchDamage(spriteRect);
                    if (damage != null) {
                        int touchDamage = damage.getAmount();
                        if (touchDamage > 0) {
                            applyDamage(touchDamage, damage.getSource());
                        }
                    }
                }

                //check status of invulnerability
                if (invulnerable) {
                    long p = registry.getImageLoader().getPeriod();
                    invulnerableTotalTime = (invulnerableTotalTime
                            + registry.getImageLoader().getPeriod())
                            % (long) (1000 * INVULNERABLE_MAX_TIME * 2);

                    if ((invulnerableTotalTime / (INVULNERABLE_MAX_TIME * 1000)) > 1) {
                        invulnerable = false;
                        invulnerableTotalTime = 0;
                    }
                }
            } else {
                //update weapon stuff
                WeaponType newWeaponType = null;
                boolean meleeWeaponFound = false;
                if (inventory.contains("RobotGoldBlade")) {
                    meleeWeaponFound = true;
                    newWeaponType = Weapon.getWeaponType("RobotGoldBlade");
                    weaponLevel = inventory.getLevelForType("RobotGoldBlade");
                } else if (inventory.contains("RobotSilverBlade")) {
                    meleeWeaponFound = true;
                    newWeaponType = Weapon.getWeaponType("RobotSilverBlade");
                    weaponLevel = inventory.getLevelForType("RobotSilverBlade");
                } else if (inventory.contains("RobotIronBlade")) {
                    meleeWeaponFound = true;
                    newWeaponType = Weapon.getWeaponType("RobotIronBlade");
                    weaponLevel = inventory.getLevelForType("RobotIronBlade");
                } else if (inventory.contains("RobotCopperBlade")) {
                    meleeWeaponFound = true;
                    newWeaponType = Weapon.getWeaponType("RobotCopperBlade");
                    weaponLevel = inventory.getLevelForType("RobotCopperBlade");
                } else if (inventory.contains("RobotStoneBlade")) {
                    meleeWeaponFound = true;
                    newWeaponType = Weapon.getWeaponType("RobotStoneBlade");
                    weaponLevel = inventory.getLevelForType("RobotStoneBlade");
                }
                if (meleeWeaponFound) {
                    if (newWeaponType != meleeWeaponType) {
                        meleeWeaponType = newWeaponType;
                        createMeleeFrames();
                    }
                    attackRange = newWeaponType.getRange();
                } else {
                    if (meleeWeaponType != null) {
                        meleeWeaponType = null;
                        createMeleeFrames();
                    }
                }
                if (attackRefreshTimerEnd <= System.currentTimeMillis() || meleeWeaponType == null) {
                    isSwinging = false;
                }

                if (hitPoints > 0) {
                    if (knockBackX > 0) {
                        mapX += checkCollide(knockBackX);
                    } else if (knockBackX < 0) {
                        mapX -= checkCollide(-1 * knockBackX);
                    } else {
                        if (registry.getGameController().multiplayerMode == registry.getGameController().multiplayerMode.CLIENT) {
                            ai.process(true);
                        } else {
                            ai.process();
                        }
                        if (ai.getChanged()) {
                            ai.setChanged(false);
                            if (registry.getGameController().multiplayerMode == registry.getGameController().multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                                if (registry.getNetworkThread().readyForUpdates) {
                                    UpdateRobot ur = new UpdateRobot(player.getId(), this.getId());
                                    ur.mapX = this.getMapX();
                                    ur.mapY = this.getMapY();
                                    ur.previousGoal = ai.getPreviousGoal();
                                    ur.currentGoal = ai.getCurrentGoal();
                                    registry.getNetworkThread().sendData(ur);
                                }
                            }
                        }
                    }
                } else {
                    SoundClip cl = new SoundClip(registry, "Robot/Die", getCenterPoint());
                    isDead = true;
                    ai.terminate();
                    player.setInsideRobot(false);
                }

                if (vertMoveMode == VertMoveMode.JUMPING) {
                    updateJumping();
                } else if (vertMoveMode == VertMoveMode.FALLING) {
                    updateFalling();
                }

                //check to see if robot is touching an enemy
                if (!invulnerable && registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.CLIENT) {
                    Damage damage = playerManager.getMonsterTouchDamage(spriteRect);
                    if (damage != null) {
                        int touchDamage = damage.getAmount();
                        if (touchDamage > 0) {
                            applyDamage(touchDamage, damage.getSource());
                        }
                    }
                }

                //check status of invulnerability
                if (invulnerable) {
                    long p = registry.getImageLoader().getPeriod();
                    invulnerableTotalTime = (invulnerableTotalTime
                            + registry.getImageLoader().getPeriod())
                            % (long) (1000 * INVULNERABLE_MAX_TIME * 2);

                    if ((invulnerableTotalTime / (INVULNERABLE_MAX_TIME * 1000)) > 1) {
                        invulnerable = false;
                        invulnerableTotalTime = 0;
                    }
                }

                mapX = playerManager.checkMapX(mapX, width);
                mapY = playerManager.checkMapY(mapY, height);

                checkIfFalling();
            }

            updateImage();

            batteryTimeRemaining -= registry.getImageLoader().getPeriod();
            if (batteryTimeRemaining <= 0) {
                batteryTimeRemaining = 0;
                toggleActivated();
            }
        } else {
            //calculate current battery recharge rate
            batteryRechargeMultiplier = BASE_BATTERY_RECHARGE_MULTIPLIER;
            if (inventory.contains("GreenBattery")) {
                batteryRechargeMultiplier *= 1.5;
            }

            batteryTimeRemaining += registry.getImageLoader().getPeriod() * batteryRechargeMultiplier;
            if (batteryTimeRemaining >= batteryTimeTotal) {
                batteryTimeRemaining = batteryTimeTotal;
            }
        }
        //attachments animation update
        if (attachmentAnimationFrameDuration > 0) {
            if (attachmentAnimationFrameUpdateTime <= registry.currentTime) {
                currentAttachmentAnimationFrame++;
                if (currentAttachmentAnimationFrame >= numAttachmentAnimationFrames) {
                    currentAttachmentAnimationFrame = 0;
                }
                attachmentAnimationFrameUpdateTime = registry.currentTime + (int) attachmentAnimationFrameDuration;
            }
        }
    }

    private void renderAccessory(Graphics g, String imageName, int xOffset, int yOffset) {
        renderAccessory(g, imageName, -1, xOffset, yOffset);
    }

    private void renderAccessory(Graphics g, String imageName, int frame, int xOffset, int yOffset) {
        BufferedImage im;
        BufferedImage imLeft;

        int xPos = playerManager.mapToPanelX(mapX + xOffset);
        int yPos = playerManager.mapToPanelY(mapY + yOffset);

        //flip the yPos since drawing happens top down versus bottom up
        yPos = playerManager.getPHeight() - yPos;

        //subtract the height since points are bottom left and drawing starts from top left
        yPos -= height;

        if (frame >= 0) {
            im = registry.getImageLoader().getImage("Attachments/" + imageName, frame);
        } else {
            im = registry.getImageLoader().getImage("Attachments/" + imageName);
        }

        if (im != null) {
            if (facing == Facing.LEFT) {
                g.drawImage(flipHorizontal(im), xPos, yPos, null);
            } else {
                g.drawImage(im, xPos, yPos, null);
            }
        }
    }

    private void renderAccessories(Graphics g, int xOffset, int yOffset) {
        boolean renderHeadItems = true;
        if (player.getInsideRobot()) {
            if ((vertMoveMode == VertMoveMode.FLYING || vertMoveMode == VertMoveMode.FALLING) && inventory.contains("Propeller")) {
                //propeller
                renderHeadItems = false;
            } else {
                //riding
                renderHeadItems = false;
            }
        }

        if (inventory.contains("RobotCopperArmor")) {
            renderAccessory(g, "RobotCopperArmor", xOffset, yOffset);
        }
        if (inventory.contains("RobotGoldArmor")) {
            renderAccessory(g, "RobotGoldArmor", xOffset, yOffset);
        }
        if (inventory.contains("RobotIronArmor")) {
            renderAccessory(g, "RobotIronArmor", xOffset, yOffset);
        }
        if (inventory.contains("RobotScrapArmor")) {
            renderAccessory(g, "RobotScrapArmor", xOffset, yOffset);
        }
        if (inventory.contains("RobotSilverArmor")) {
            renderAccessory(g, "RobotSilverArmor", xOffset, yOffset);
        }
        if (inventory.contains("RobotWoodArmor")) {
            renderAccessory(g, "RobotWoodArmor", xOffset, yOffset);
        }
        if (inventory.contains("ShutterShades")) {
            renderAccessory(g, "ShutterShades", xOffset, yOffset);
        }
        if (inventory.contains("Disguise")) {
            renderAccessory(g, "Disguise", xOffset, yOffset);
        }
        if (renderHeadItems) {
            if (inventory.contains("FireFighterHat")) {
                renderAccessory(g, "FireFighterHat", xOffset, yOffset);
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (isActivated) {
            int offsetX = 0;
            int offsetY = 0;
            BufferedImage im;
            BufferedImage imLeft;
            AffineTransform tx;
            AffineTransformOp op;

            if (isAnimating) {
                im = registry.getImageLoader().getImage(image, currentAnimationFrame);
            } else {
                im = registry.getImageLoader().getImage(image);
            }

            int xPos = playerManager.mapToPanelX(mapX);
            int yPos = playerManager.mapToPanelY(mapY);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = playerManager.getPHeight() - yPos;

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

            //render accessories
            offsetX = 0;
            offsetY = 0;
            if (vertMoveMode == VertMoveMode.JUMPING) {
                offsetY += 3;
            } else if (image.equals("Robot/Walking") && currentAnimationFrame % 2 != 0) {
                offsetY += 1;
            }
            renderAccessories(g, offsetX, offsetY);

            //are we swinging a melee weapon?
            if (isSwinging) {
                if (meleeWeaponType != null) {
                    if (meleeImages[0] == null) {
                        createMeleeFrames();
                    }

                    int frame = currentMeleeAnimationFrame;
                    if (facing == Facing.LEFT) {
                        frame += 8;
                        if (meleeImages[frame] != null) {
                            g.drawImage(meleeImages[frame], xPos + 5, yPos + 5, null);
                        }
                    } else {
                        if (meleeImages[frame] != null) {
                            g.drawImage(meleeImages[frame], xPos - 5, yPos + 5, null);
                        }
                    }
                }
            }

            //is the player riding inside the robot?
            if (player.getInsideRobot()) {
                if ((vertMoveMode == VertMoveMode.FLYING || vertMoveMode == VertMoveMode.FALLING) && inventory.contains("Propeller")) {
                    renderAccessory(g, "Propeller", currentAttachmentAnimationFrame, offsetX, offsetY);
                } else {
                    renderAccessory(g, "Riding", offsetX, offsetY);
                }
            }

            //draw energy image
            if (invulnerable) {
                if (Rand.getRange(1, 4) == 1) {
                    invulnerableShow = false;
                } else {
                    invulnerableShow = true;
                }
                if (invulnerableShow) {
                    if (facing == Facing.LEFT) {
                        if (imageShieldLeft != null) {
                            g.drawImage(imageShieldLeft, xPos, yPos, null);
                        }
                    } else {
                        if (imageShieldRight != null) {
                            g.drawImage(imageShieldRight, xPos, yPos, null);
                        }
                    }
                }
            }

            super.render(g);

            if (!player.getInsideRobot()) {
                FontMetrics fm = g.getFontMetrics();
                int messageWidth = fm.stringWidth(name);

                xPos = playerManager.mapToPanelX((int) mapX + (width / 2) - (messageWidth / 2));
                yPos = playerManager.mapToPanelY((int) mapY);
                yPos = playerManager.getPHeight() - yPos;

                Font textFont = new Font("SansSerif", Font.BOLD, 14);
                g.setFont(textFont);

                registry.ghettoOutline(g, Color.BLACK, name, xPos, yPos - 50);

                g.setColor(Color.white);
                g.drawString(name,
                        xPos,
                        yPos - 50);
            }
        }


//        if (attackArc != null) {
//            int xPos = manager.mapToPanelX((int)attackArc.x);
//            int yPos = manager.mapToPanelY((int)attackArc.y);
//            yPos = manager.getPHeight() - yPos;
//            yPos -= attackArc.height;
//            g.setColor(Color.red);
//            g.drawArc(xPos, yPos, (int)attackArc.width, (int)attackArc.height, (int)attackArc.start, (int)attackArc.extent);
//        }
    }

    public void stopAttacks() {
        actionMode = ActionMode.NONE;
        isSwinging = false;
        stateChanged = true;
        updateImage();
    }

    @Override
    protected void updateImage() {
        if (stateChanged) {
            if (actionMode == ActionMode.ATTACKING) {
                loopImage("Robot/Bashing");
            } else if (vertMoveMode == VertMoveMode.JUMPING) {
                setImage("Robot/Jumping");
            } else if (vertMoveMode == VertMoveMode.FLYING) {
                loopImage("Monsters/" + name + "/Standing");
            } else {
                if (!isTryingToMove) {
                    loopImage("Robot/Standing");
                } else {
                    loopImage("Robot/Walking");
                }
            }
            stateChanged = false;
        }
    }

    @Override
    protected void finishJumping() {
        if (!inventory.contains("Propeller")) {
            int currentStartJumpSize = startJumpSize;
            if (player.getInsideRobot()) {
                currentStartJumpSize = player.getStartJumpSize();
            }
            if (registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.CLIENT) {
                if ((fallSize - currentStartJumpSize - gravity * 3) > 0) {
                    applyDamage((int) ((fallSize - currentStartJumpSize - gravity * 3) * fallDamageMultiplier), null);
                }
            }
        }
        setVertMoveMode(VertMoveMode.NOT_JUMPING, false);
        totalFall = 0;
        fallSize = 0;
        if (!isTryingToMove) {
            stopMove();
        } else if (!image.equals("Robot/Walking")) {
            if (facing == Facing.RIGHT) {
                moveRight();
            } else {
                moveLeft();
            }
        }
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}