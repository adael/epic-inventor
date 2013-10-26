package com.weem.epicinventor.actor;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.monster.*;
import com.weem.epicinventor.armor.*;
import com.weem.epicinventor.hud.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.network.*;
import com.weem.epicinventor.particle.*;
import com.weem.epicinventor.placeable.*;
import com.weem.epicinventor.projectile.*;
import com.weem.epicinventor.resource.*;
import com.weem.epicinventor.utility.*;
import com.weem.epicinventor.weapon.*;
import com.weem.epicinventor.item.*;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.awt.geom.AffineTransform;

public class Player extends Actor implements Serializable {

    private static final long serialVersionUID = -606404122993560860L;
    private String name;
    private Inventory inventory;
    private ArmorType armorHead;
    private ArmorType armorChest;
    private ArmorType armorLegs;
    private ArmorType armorFeet;
    private int armorHeadLevel;
    private int armorChestLevel;
    private int armorLegsLevel;
    private int armorFeetLevel;
    private Robot robot;
    transient private PlayerManager playerManager;
    private boolean isFallAnimating = false;
    private int selectedItem = 0;
    private boolean mouseClickHeld;
    private boolean invulnerable;
    private boolean invulnerableShow;
    private float invulnerableTotalTime;
    private final static float INVULNERABLE_MAX_TIME = 1.0f;
    transient private BufferedImage[] weaponImages = null;
    private boolean insideRobot;
    private int rezSicknessStack;
    private long rezSicknessEnd;
    transient private int fallBeforeAnimationChange = 32;
    transient private float fallDamageMultiplier = 2.5f;
    transient private final static int REZ_SICKNESS_TIME = 10 * 60 * 1000;
    transient private final static float REZ_SICKNESS_MULTIPLIER = 0.20f;
    transient private final static int INVENTORY_SIZE = 50;
    transient private final static int QB_INVENTORY_SIZE = 10;
    transient private final static int MOVE_SPEED = 8;
    transient private final static int MOVE_SPEED_INSIDE_ROBOT = 15;
    transient private final static int MOVE_SPEED_SLOWED = 4;
    transient private final static int JUMP_SIZE = 20;
    transient private final static int JUMP_SIZE_INSIDE_ROBOT = 30;
    transient private final static int MAX_INSIDE_ROBOT_DISTANCE = 24;
    transient private final static int BASE_HP_REGEN_RATE = 5; //percent of max per min
    private ResourceType currentResourceType;
    private long lastResourceSoundPlay;
    transient private String lowHPSound;
    transient private SoundClip lowHPSoundClip;
    transient private SoundClip weaponSoundClip;
    private float currentHPRegen; //partial hps
    private int currentHPRegenRate; //percent of max per min
    private float attackBonus;
    private boolean isTryingToFlap;
    private int xp;
    private int placingSlot = 0;
    transient private ParticleEmitter particleEmitter;
    private int currentRangedAnimationFrames;
    private int totalRangedAnimationFrames;
    private boolean tbAdded = false;
    transient boolean cameraReturning;
    transient public double cameraX, cameraY;
    transient public double cameraMoveSize = 0;

    public Player() {
        super(null, null, "", 0, 0);
    }

    public Player(PlayerManager pm, Registry rg, String im, int x) {
        super(pm, rg, im, x, 0);

        playerManager = pm;

        inventory = new Inventory(rg, INVENTORY_SIZE);
        robot = new Robot(pm, this, rg, "Images/Robot/Standing", x);

        inventory.addToInventory(0, "ClothesChest", 1);
        playerEquipFromInventory(0);

        inventory.addToInventory(0, "ClothesLegs", 1);
        playerEquipFromInventory(0);

        inventory.addToInventory(0, "ClothesFeet", 1);
        playerEquipFromInventory(0);

        inventory.addToInventory(40, "Stick", 1);
        inventory.addToInventory(41, "Bread", 5);

        inventory.addToInventory(0, "Box", 1);
        inventory.addToInventory(0, "WoodBlock", 1);
        inventory.addToInventory(0, "Wood", 5);
        inventory.addToInventory(0, "StoneBlock", 1);
        inventory.addToInventory(0, "Stone", 5);
        inventory.addToInventory(0, "Copper", 2);
        inventory.addToInventory(0, "Silver", 1);

        resetPlayer();
    }

    public void setPositionToSpawn() {
        Point townHallPoint = playerManager.getNearestTownHallXY(new Point(mapX, mapY));
        mapX = townHallPoint.x;
        mapY = townHallPoint.y;
        if (mapX == 0) {
            mapX = 226;
        }
        if (mapY == 0) {
            mapY = playerManager.findFloor(mapX);
        }
        lastMapY = mapY;
    }

    public void setPositionToRandom() {
        do {
            Point randomPoint = new Point(
                    Rand.getRange(0, registry.getBlockManager().getMapWidth() - width),
                    Rand.getRange(0, registry.getBlockManager().getMapHeight() - height));

            mapX = randomPoint.x;
            mapY = randomPoint.y;

            lastMapY = mapY;
        } while (playerManager.doesRectContainBlocks(mapX, mapY, width, height));
    }

    public void resetPlayer() {
        setPositionToSpawn();

        stateChanged = true;
        isStill = true;
        isTryingToMove = false;
        mouseClickHeld = false;
        facing = Facing.RIGHT;
        actionMode = ActionMode.NONE;
        vertMoveMode = VertMoveMode.NOT_JUMPING;

        topOffset = 10;
        baseOffset = 18;
        baseWidth = 24;
        jumpSize = 8;
        fallSize = 0;
        completeFall = 0;

        ascendOriginalSize = 6;
        ascendSize = 6;
        ascendMax = 48;

        xMoveSize = MOVE_SPEED;
        startJumpSize = JUMP_SIZE;

        knockBackX = 0;

        statusStun = false;

        hitPoints = baseHitPoints;
        weaponImages = new BufferedImage[6 * 2];
        weaponImages[0] = null;

        updateArmorPoints();
        updateHitPoints();
        setInsideRobot(false);
        if (robot.isActive()) {
            robot.toggleActivated(mapX, mapY, true);
        }
        
        projectileOut = false;

        registry.setBossFight(false);
    }

    public void setTransient(Registry rg) {
        yx = new int[2];
        ycm = new int[2];

        if (armorHead != null) {
            if (armorHead.getArmorBonus() == null) {
                armorHead = Armor.getArmorType(armorHead.getName());
            }
        }
        if (armorHeadLevel < 1) {
            armorHeadLevel = this.getLevel();
        }

        if (armorChest != null) {
            if (armorChest.getArmorBonus() == null) {
                armorChest = Armor.getArmorType(armorChest.getName());
            }
        }
        if (armorChestLevel < 1) {
            armorChestLevel = this.getLevel();
        }

        if (armorLegs != null) {
            if (armorLegs.getArmorBonus() == null) {
                armorLegs = Armor.getArmorType(armorLegs.getName());
            }
        }
        if (armorLegsLevel < 1) {
            armorLegsLevel = this.getLevel();
        }

        if (armorFeet != null) {
            if (armorFeet.getArmorBonus() == null) {
                armorFeet = Armor.getArmorType(armorFeet.getName());
            }
        }
        if (armorFeetLevel < 1) {
            armorFeetLevel = this.getLevel();
        }

        playerManager = rg.getPlayerManager();
        registry = rg;
        manager = rg.getPlayerManager();
        fallDamageMultiplier = 2.5f;

        weaponImages = new BufferedImage[6 * 2];
        weaponImages[0] = null;
        attackArcOffsetX = 0;
        attackArcOffsetY = 10;

        if (!playerManager.getCurrentPlayerSet()) {
            //set the current player if it hasn't been set.  This is needed for setting the inventory levels properly
            playerManager.setCurrentPlayer(this);
        }

        robot.setTransient(rg, this);
        inventory.setTransient(rg);

        lowHPSound = null;
        lowHPSoundClip = null;

        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public void init() {
        Point townHallPoint = playerManager.getNearestTownHallXY(new Point(mapX, mapY));
        mapX = townHallPoint.x;
        mapY = townHallPoint.y;
        if (mapX == 0) {
            mapX = 226;
        }
        if (mapY == 0) {
            mapY = playerManager.findFloor(mapX);
        }
        lastMapY = mapY;

        robot.init();
    }
    
    public boolean getCameraReturning() {
        return cameraReturning;
    }
    
    public void setCameraReturning(boolean r) {
        cameraReturning = r;
        if(cameraReturning) {
            cameraX = mapX;
            cameraY = mapY;
            cameraMoveSize = 0;
        }
    }

    public int getBaseOffset() {
        return baseOffset;
    }

    public int getXP() {
        return xp;
    }

    public int getLevel() {
        int[] xpTable = new int[21];
        xpTable[0] = 0;
        xpTable[1] = 80;
        xpTable[2] = 245;
        xpTable[3] = 720;
        xpTable[4] = 1805;
        xpTable[5] = 3920;
        xpTable[6] = 7605;
        xpTable[7] = 13520;
        xpTable[8] = 22445;
        xpTable[9] = 35280;
        xpTable[10] = 53045;
        xpTable[11] = 76880;
        xpTable[12] = 108045;
        xpTable[13] = 147920;
        xpTable[14] = 198005;
        xpTable[15] = 259920;
        xpTable[16] = 335405;
        xpTable[17] = 426320;
        xpTable[18] = 534645;
        xpTable[19] = 662480;
        xpTable[20] = 812045;

        int level = 1;

        for (int i = 0; i < xpTable.length; i++) {
            //last level
            if (i == xpTable.length - 1) {
                return xpTable.length - 1;
            }

            if (xpTable[i] <= xp && xp < xpTable[i + 1]) {
                return (i + 1);
            }
        }

        return level;
    }

    public int getAttackBonus() {
        int level = this.getLevel();
        int[] attackTable = new int[21];
        attackTable[0] = 0;
        attackTable[1] = 8;
        attackTable[2] = 11;
        attackTable[3] = 16;
        attackTable[4] = 21;
        attackTable[5] = 27;
        attackTable[6] = 33;
        attackTable[7] = 39;
        attackTable[8] = 46;
        attackTable[9] = 53;
        attackTable[10] = 60;
        attackTable[11] = 67;
        attackTable[12] = 74;
        attackTable[13] = 81;
        attackTable[14] = 88;
        attackTable[15] = 96;
        attackTable[16] = 103;
        attackTable[17] = 110;
        attackTable[18] = 117;
        attackTable[19] = 125;
        attackTable[20] = 132;

        if (level > 0 && level <= attackTable.length) {
            return attackTable[level];
        } else {
            return attackTable[1];
        }
    }

    public void addXP(int x) {
        if (x > 0) {
            int oldLevel = getLevel();
            int oldAttackBonus = getAttackBonus();
            int oldHP = totalHitPoints;
            xp += x;
            if (xp > 812045) {
                xp = 812045;
            }
            updateHitPoints();
            registry.getIndicatorManager().createXPIndicator(mapX + (width / 2), mapY + 50, "+" + Integer.toString(x) + "xp");

            //check to see if we've leveled
            if (getLevel() > oldLevel && playerManager.getCurrentPlayer() == this) {
                //hp back to full
                hitPoints = totalHitPoints;

                //show level hud
                SoundClip cl = new SoundClip("Player/LevelUp");
                registry.setAttackBonus(getAttackBonus() - oldAttackBonus);
                registry.setHPBonus(totalHitPoints - oldHP);
                registry.getHUDManager().loadHUD(HUDManager.HUDType.LevelUp);
                playerManager.showLevelUpGraphic();
            }
        }
    }

    public void removeXP(int x, int mx, int my) {
        if (x > 0) {
            int oldXP = xp;
            int oldLevel = getLevel();
            xp -= x;
            if (xp < 0) {
                xp = 0;
            }

            //make sure we didn't lose a level
            if (this.getLevel() < oldLevel) {
                xp = playerManager.getXPNeededForLevel(oldLevel);
                if (oldXP - xp > 0) {
                    registry.getIndicatorManager().createNegativeXPIndicator(mapX + (width / 2), mapY + 50, "-" + Integer.toString(oldXP - xp) + "xp");
                    registry.getResourceManager().spawnXPCrystal(mx, my + 25, oldXP - xp);
                }
            } else if (oldXP != xp) {
                if (x > 0) {
                    registry.getIndicatorManager().createNegativeXPIndicator(mapX + (width / 2), mapY + 50, "-" + Integer.toString(x) + "xp");
                    registry.getResourceManager().spawnXPCrystal(mx, my + 25, x);
                }
            }
        }
    }

    public boolean isPlayerPerformingAction() {
        if (actionMode == ActionMode.NONE) {
            return false;
        } else {
            return true;
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void setFallSize(int fs) {
        fallSize = fs;
        robot.setFallSize(fs);
    }

    public int getInventorySize() {
        return INVENTORY_SIZE;
    }

    public int getStartJumpSize() {
        return startJumpSize;
    }

    public int playerAddItem(int startSlot, String name, int qty) {
        return inventory.addToInventory(startSlot, name, qty);
    }

    public int playerAddItem(int startSlot, String name, int qty, int level) {
        return inventory.addToInventory(startSlot, name, qty, level);
    }

    public String playerGetInventoryItemName(int slot) {
        return inventory.getNameFromSlot(slot);
    }

    public String playerGetInventoryItemCategory(int slot) {
        return inventory.getCategoryFromSlot(slot);
    }

    public int playerGetInventoryQty(int slot) {
        return inventory.getQtyFromSlot(slot);
    }

    public int playerGetInventoryLevel(int slot) {
        return inventory.getLevelFromSlot(slot);
    }

    public void playerDeleteInventory(int slot, int qty) {
        playerDeleteInventory(slot, qty, false);
    }

    public void playerDeleteInventory(int slot, int qty, boolean giveXP) {
        String itemName = inventory.getNameFromSlot(slot);
        int itemQty = inventory.getQtyFromSlot(slot);
        if (giveXP) {
            playerGiveItemXP(itemName, itemQty);
        }
        inventory.deleteInventory(slot, qty);
    }

    public void playerGiveItemXP(String itemName, int itemQty) {
        if (itemName != null) {
            //give the player xp - xp table is built on the xp for a kill and then an item multiplier is applied
            SoundClip cl = new SoundClip("Player/Good");
            this.addXP(getXPForItem(this.getLevel(), itemName) * itemQty);
        }
    }

    public void setPlayerSlotQuantity(int slot, int qty) {
        inventory.setSlotQuantity(slot, qty);
    }

    @Override
    public void setShowRect(boolean r) {
        showRect = r;
        robot.setShowRect(r);
    }

    public boolean setInsideRobot(boolean i) {
        xMoveSize = MOVE_SPEED;
        startJumpSize = JUMP_SIZE;
        if (i && robot.getIsActivated()) {
            if (!insideRobot) {
                if (getCenterPoint().distance(robot.getCenterPoint()) <= MAX_INSIDE_ROBOT_DISTANCE) {
                    SoundClip cl = new SoundClip(registry, "Robot/RideStart", getCenterPoint());
                    xMoveSize = MOVE_SPEED_INSIDE_ROBOT;
                    startJumpSize = JUMP_SIZE_INSIDE_ROBOT;
                    insideRobot = true;
                    robot.stopAttacks();

                    if (particleEmitter != null) {
                        particleEmitter.setActive(false);
                        if (weaponSoundClip != null) {
                            weaponSoundClip.stop();
                        }
                    }
                    return true;
                }
            }
        } else {
            if (insideRobot) {
                SoundClip cl = new SoundClip(registry, "Robot/RideStop", getCenterPoint());
                insideRobot = false;
                return true;
            }
        }

        jumpSize = startJumpSize;

        return false;
    }

    public void scrollQuickBar(int steps) {
        selectedItem += steps;

        if (selectedItem < 0) {
            selectedItem = 0;
        }

        if (selectedItem >= QB_INVENTORY_SIZE) {
            selectedItem = QB_INVENTORY_SIZE - 1;
        }

        setPlayerSelectedItem(selectedItem);
    }

    public void setInventory(Inventory i) {
        inventory = i;
    }

    public void setPlayerSelectedItem(int i) {
        if (i >= 0 && i <= 9) {
            selectedItem = i;
            weaponImages[0] = null;
        }
    }

    public void setWeaponImages() {
        synchronized (this) {
            int slotIndex = selectedItem + INVENTORY_SIZE - QB_INVENTORY_SIZE;
            if (inventory.getTypeFromSlot(slotIndex).equals("Weapon")) {
                String imageName = inventory.getImageFromSlot(slotIndex);
                imageName = imageName.replace("Items/", "Weapons/");
                BufferedImage weaponImage = registry.getImageLoader().getImage(imageName);
                if (weaponImage != null) {
                    int angle = 0;
                    for (int j = 0; j < 6; j++) {
                        weaponImages[j] = weaponImage;
                        angle = (int) ((float) j / 4.0f * 90.0f);
                        AffineTransform at = new AffineTransform();
                        at.rotate(Math.toRadians(angle), weaponImages[j].getWidth() / 2, weaponImages[j].getHeight() / 2);
                        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                        weaponImages[j] = atop.filter(weaponImages[j], null);
                    }
                    for (int j = 0; j < 6; j++) {
                        weaponImages[j + 6] = weaponImage;
                        angle = (int) ((float) j / 4.0f * 90.0f);
                        AffineTransform at = new AffineTransform();
                        at.rotate(Math.toRadians(angle), weaponImages[j + 6].getWidth() / 2, weaponImages[j + 6].getHeight() / 2);
                        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                        weaponImages[j + 6] = atop.filter(weaponImages[j + 6], null);
                        AffineTransform tx = null;
                        tx = AffineTransform.getScaleInstance(-1, 1);
                        tx.translate(-1 * weaponImages[j + 6].getWidth(), 0);
                        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                        weaponImages[j + 6] = op.filter(weaponImages[j + 6], null);
                    }
                    attackRange = weaponImage.getHeight() + 10;
                }
            }
        }
    }

    public void setName(String n) {
        name = n;
        if (!tbAdded) {
            if (name.toLowerCase().contains("total") && name.toLowerCase().contains("biscuit")) {
                inventory.addToInventory(39, "TBHead", 1);
                playerEquipFromInventory(39);
                tbAdded = true;
            }
        }
    }

    public void setRobotName(String n) {
        robot.setName(n);
    }

    public void setArmorTypeHead(String armorName, int l) {
        armorHead = Armor.getArmorType(armorName);
        armorHeadLevel = l;
    }

    public void setArmorTypeChest(String armorName, int l) {
        armorChest = Armor.getArmorType(armorName);
        armorChestLevel = l;
    }

    public void setArmorTypeLegs(String armorName, int l) {
        armorLegs = Armor.getArmorType(armorName);
        armorLegsLevel = l;
    }

    public void setArmorTypeFeet(String armorName, int l) {
        armorFeet = Armor.getArmorType(armorName);
        armorFeetLevel = l;
    }

    public void playerSwapInventory(int from, int to) {
        int slotIndex = selectedItem + INVENTORY_SIZE - QB_INVENTORY_SIZE;
        if (slotIndex == from || slotIndex == to) {
            weaponImages[0] = null;
        }
        inventory.swapInventoryLocations(from, to);
    }

    public void playerUnEquipToInventory(String equipmentType, int to) {
        if (equipmentType.equals("head")) {
            if (playerAddItem(to, armorHead.getName(), 1, armorHeadLevel) == 0) {
                armorHead = null;
                armorHeadLevel = 0;
            }
            updateArmorPoints();
        } else if (equipmentType.equals("chest")) {
            if (playerAddItem(to, armorChest.getName(), 1, armorChestLevel) == 0) {
                armorChest = null;
                armorChestLevel = 0;
            }
            updateArmorPoints();
        } else if (equipmentType.equals("legs")) {
            if (playerAddItem(to, armorLegs.getName(), 1, armorLegsLevel) == 0) {
                armorLegs = null;
                armorLegsLevel = 0;
            }
            updateArmorPoints();
        } else if (equipmentType.equals("feet")) {
            if (playerAddItem(to, armorFeet.getName(), 1, armorFeetLevel) == 0) {
                armorFeet = null;
                armorFeetLevel = 0;
            }
            updateArmorPoints();
        }
    }

    public void playerUnEquipToDelete(String equipmentType) {
        if (equipmentType.equals("head")) {
            armorHead = null;
            armorHeadLevel = 0;
            updateArmorPoints();
        } else if (equipmentType.equals("cheat")) {
            armorChest = null;
            armorChestLevel = 0;
            updateArmorPoints();
        } else if (equipmentType.equals("legs")) {
            armorLegs = null;
            armorLegsLevel = 0;
            updateArmorPoints();
        } else if (equipmentType.equals("feet")) {
            armorFeet = null;
            armorFeetLevel = 0;
            updateArmorPoints();
        }
    }

    public void playerCraftItem(String itemType) {
        boolean canCreate = false;
        ArrayList<String> requirements = playerManager.getItemTypeRequirements(itemType);

        //check to see if we have the needed materials
        if (requirements != null) {
            canCreate = true;

            for (int i = 0; i < requirements.size(); i++) {
                String[] parts = requirements.get(i).toString().split(":");
                if (parts.length == 2) {
                    HUDArea hudArea;
                    int qtyOnHand = inventory.getItemTypeQty(parts[0]);
                    int qtyNeeded = Integer.parseInt(parts[1]);

                    boolean isPlaceable = false;
                    ItemType it = registry.getItemType(parts[0]);
                    if (it != null) {
                        if (it.getCategory().equals("Placeable")) {
                            isPlaceable = true;
                        }
                    }

                    if (isPlaceable) {
                        if (registry.getPlaceableManager().getActivatedCount(parts[0]) <= 0) {
                            canCreate = false;
                        }
                    } else {
                        if (qtyOnHand < qtyNeeded) {
                            canCreate = false;
                        }
                    }
                }
            }
        }

        if (canCreate) {
            int qty = registry.getItemType(itemType).getCreateQty();
            int level = this.getLevel();
            int levelCheck = Rand.getRange(1, 100);

            if (levelCheck > 90) {
                level += 3;
            } else if (levelCheck > 75) {
                level += 2;
            } else if (levelCheck > 50) {
                level += 1;
            }
            if (level < 1) {
                level = 1;
            }
            if (level > 20) {
                level = 20;
            }

            //we have the needed materials, try to add to inventory
            if (inventory.addToInventory(0, itemType, qty, level) < qty) {
                SoundClip cl = new SoundClip("Player/Good");

                //if successful, remove the needed materials
                for (int i = 0; i < requirements.size(); i++) {
                    String[] parts = requirements.get(i).toString().split(":");
                    if (parts.length == 2) {
                        ItemType it = registry.getItemType(parts[0]);
                        if (!it.getCategory().equals("Placeable")) {
                            inventory.deleteItems(parts[0], Integer.parseInt(parts[1]));
                        }
                    }
                }

                //give the player xp - xp table is built on the xp for a kill and then an item multiplier is applied
                this.addXP(getXPForItem(this.getLevel(), itemType));
            }
        }
    }

    private int getXPForItem(int playerLevel, String itemName) {
        int[] xpTable = new int[31];
        xpTable[0] = 0;
        xpTable[1] = 23;
        xpTable[2] = 32;
        xpTable[3] = 41;
        xpTable[4] = 52;
        xpTable[5] = 65;
        xpTable[6] = 79;
        xpTable[7] = 96;
        xpTable[8] = 114;
        xpTable[9] = 135;
        xpTable[10] = 157;
        xpTable[11] = 181;
        xpTable[12] = 206;
        xpTable[13] = 234;
        xpTable[14] = 263;
        xpTable[15] = 295;
        xpTable[16] = 328;
        xpTable[17] = 362;
        xpTable[18] = 399;
        xpTable[19] = 438;
        xpTable[20] = 478;
        xpTable[21] = 520;
        xpTable[22] = 564;
        xpTable[23] = 610;
        xpTable[24] = 657;
        xpTable[25] = 707;
        xpTable[26] = 758;
        xpTable[27] = 811;
        xpTable[28] = 866;
        xpTable[29] = 923;
        xpTable[30] = 981;

        //get the player xp - xp table is built on the xp for a kill and then an item multiplier is applied
        ItemType it = registry.getItemType(itemName);
        if (it != null && playerLevel >= 0 && playerLevel < xpTable.length) {
            float xp = registry.getItemType(itemName).getXPModifier();
            xp *= (float) xpTable[playerLevel];
            return (int) xp;
        } else {
            return 0;
        }
    }

    public void playerEquipFromInventory(int slot) {
        String type = inventory.getTypeFromSlot(slot);

        if (type.equals("Armor")) {
            String name = inventory.getNameFromSlot(slot);
            ArmorType newArmorType = Armor.getArmorType(name);

            if (newArmorType != null) {
                if (newArmorType.getType().equals("Head")) {
                    if (armorHead == null) {
                        //armor slot was previously empty - equip and remove from inv
                        armorHead = newArmorType;
                        armorHeadLevel = inventory.getLevelFromSlot(slot);
                        inventory.deleteInventory(slot, 0);
                    } else {
                        //switching out armor for another
                        ArmorType oldArmorType = armorHead;
                        int oldArmorLevel = armorHeadLevel;
                        armorHead = newArmorType;
                        armorHeadLevel = inventory.getLevelFromSlot(slot);
                        inventory.deleteInventory(slot, 0);
                        inventory.addToInventory(slot, oldArmorType.getName(), 1, oldArmorLevel);
                    }
                } else if (newArmorType.getType().equals("Chest")) {
                    if (armorChest == null) {
                        //armor slot was previously empty - equip and remove from inv
                        armorChest = newArmorType;
                        armorChestLevel = inventory.getLevelFromSlot(slot);
                        inventory.deleteInventory(slot, 0);
                    } else {
                        //switching out armor for another
                        ArmorType oldArmorType = armorChest;
                        int oldArmorLevel = armorChestLevel;
                        armorChest = newArmorType;
                        armorChestLevel = inventory.getLevelFromSlot(slot);
                        inventory.deleteInventory(slot, 0);
                        inventory.addToInventory(slot, oldArmorType.getName(), 1, oldArmorLevel);
                    }
                } else if (newArmorType.getType().equals("Legs")) {
                    if (armorLegs == null) {
                        //armor slot was previously empty - equip and remove from inv
                        armorLegs = newArmorType;
                        armorLegsLevel = inventory.getLevelFromSlot(slot);
                        inventory.deleteInventory(slot, 0);
                    } else {
                        //switching out armor for another
                        ArmorType oldArmorType = armorLegs;
                        int oldArmorLevel = armorLegsLevel;
                        armorLegs = newArmorType;
                        armorLegsLevel = inventory.getLevelFromSlot(slot);
                        inventory.deleteInventory(slot, 0);
                        inventory.addToInventory(slot, oldArmorType.getName(), 1, oldArmorLevel);
                    }
                } else if (newArmorType.getType().equals("Feet")) {
                    if (armorFeet == null) {
                        //armor slot was previously empty - equip and remove from inv
                        armorFeet = newArmorType;
                        armorFeetLevel = inventory.getLevelFromSlot(slot);
                        inventory.deleteInventory(slot, 0);
                    } else {
                        //switching out armor for another
                        ArmorType oldArmorType = armorFeet;
                        int oldArmorLevel = armorFeetLevel;
                        armorFeet = newArmorType;
                        armorFeetLevel = inventory.getLevelFromSlot(slot);
                        inventory.deleteInventory(slot, 0);
                        inventory.addToInventory(slot, oldArmorType.getName(), 1, oldArmorLevel);
                    }
                }

                updateArmorPoints();
            }
        }
    }

    private void updateArmorPoints() {
        int newArmorPoints = baseArmorPoints;
        int[] bonuses;

        if (armorHead != null) {
            bonuses = armorHead.getArmorBonus();
            newArmorPoints += bonuses[armorHeadLevel];
        }
        if (armorChest != null) {
            bonuses = armorChest.getArmorBonus();
            newArmorPoints += bonuses[armorChestLevel];
        }
        if (armorLegs != null) {
            bonuses = armorLegs.getArmorBonus();
            newArmorPoints += bonuses[armorLegsLevel];
        }
        if (armorFeet != null) {
            bonuses = armorFeet.getArmorBonus();
            newArmorPoints += bonuses[armorFeetLevel];
        }

        armorPoints = newArmorPoints;
    }

    private void updateHitPoints() {
        int level = this.getLevel();
        int[] hpTable = new int[31];
        hpTable[0] = 0;
        hpTable[1] = 50;
        hpTable[2] = 100;
        hpTable[3] = 150;
        hpTable[4] = 200;
        hpTable[5] = 250;
        hpTable[6] = 300;
        hpTable[7] = 350;
        hpTable[8] = 400;
        hpTable[9] = 450;
        hpTable[10] = 500;
        hpTable[11] = 550;
        hpTable[12] = 600;
        hpTable[13] = 650;
        hpTable[14] = 700;
        hpTable[15] = 750;
        hpTable[16] = 800;
        hpTable[17] = 850;
        hpTable[18] = 900;
        hpTable[19] = 950;
        hpTable[20] = 1000;

        if (level > 0 && level <= hpTable.length) {
            totalHitPoints = hpTable[level];
        } else {
            totalHitPoints = hpTable[1];
        }

        baseHitPoints = totalHitPoints;
        if (hitPoints > totalHitPoints) {
            hitPoints = totalHitPoints;
        }
    }

    public int getMeleeRecastPercentage() {
        int percentage = 0;
        float total = attackRefreshTimerEnd - attackRefreshTimerStart;

        if (total > 0) {
            float current = System.currentTimeMillis() - attackRefreshTimerStart;
            float diff = (current / total) * 100f;

            percentage = (int) diff;

            if (percentage < 0) {
                percentage = 0;
            }

            if (percentage >= 100) {
                percentage = 0;
            }
        }

        return percentage;
    }

    public String getName() {
        return name;
    }

    public ArmorType getArmorTypeHead() {
        return armorHead;
    }

    public ArmorType getArmorTypeChest() {
        return armorChest;
    }

    public ArmorType getArmorTypeLegs() {
        return armorLegs;
    }

    public ArmorType getArmorTypeFeet() {
        return armorFeet;
    }

    public int getArmorTypeHeadLevel() {
        return armorHeadLevel;
    }

    public int getArmorTypeChestLevel() {
        return armorChestLevel;
    }

    public int getArmorTypeLegsLevel() {
        return armorLegsLevel;
    }

    public int getArmorTypeFeetLevel() {
        return armorFeetLevel;
    }

    public boolean getInsideRobot() {
        return insideRobot;
    }

    public int getHitPointPercentage() {
        float percentage = (float) hitPoints / (float) totalHitPoints;

        percentage = percentage * 100;

        return (int) percentage;
    }

    public void addHitPoints(int hp) {
        hitPoints += hp;
        if (hitPoints > totalHitPoints) {
            hitPoints = totalHitPoints;
        }
    }

    public int getRobotBatteryPercentage() {
        return robot.getBatteryPercentage();
    }

    public Robot getRobot() {
        return robot;
    }

    public String getRobotMode() {
        return robot.getMode();
    }

    public boolean getRobotFollowing() {
        return robot.getIsFollowing();
    }

    public Inventory getRobotInventory() {
        return robot.getInventory();
    }

    public int getRobotInventorySize() {
        return robot.getInventorySize();
    }

    public boolean isRobotActivated() {
        return robot.getIsActivated();
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public boolean isPlayerMoving() {
        if (isStill && vertMoveMode == VertMoveMode.NOT_JUMPING) {
            return false;
        } else {
            return true;
        }
    }

    public void startGather(String rt) {
        stateChanged = true;
        actionMode = ActionMode.GATHERING;
        currentResourceType = playerManager.getResourceTypeByResourceId(rt);
    }

    @Override
    public void jump() {
        if (!statusStun) {
            if (insideRobot && robot.getInventory().contains("Propeller") && vertMoveMode != VertMoveMode.JUMPING && vertMoveMode != VertMoveMode.NOT_JUMPING) {
                flap();
            } else if (vertMoveMode == VertMoveMode.NOT_JUMPING) {
                setVertMoveMode(VertMoveMode.JUMPING);
                jumpSize = startJumpSize;
                if (armorFeet == null) {
                    SoundClip cl = new SoundClip("Player/Jump" + Rand.getRange(1, 3));
                } else {
                    if (armorFeet.getName().equals("SpringFeet") && !insideRobot) {
                        SoundClip cl = new SoundClip("Player/JumpSpring" + Rand.getRange(1, 3));
                    } else {
                        SoundClip cl = new SoundClip("Player/Jump" + Rand.getRange(1, 3));
                    }
                }
            }
        }
    }

    public void stopGather() {
        stateChanged = true;
        actionMode = ActionMode.NONE;
        currentResourceType = null;
    }

    public void attack(Point clickPoint) {
        if (actionMode != ActionMode.ATTACKING && attackRefreshTimerEnd < System.currentTimeMillis()) {
            if (registry.getPlayerSelectedItemType(this).equals("Weapon")) {
                String itemName = registry.getPlayerSelectedItemName(this);
                WeaponType newWeaponType = Weapon.getWeaponType(itemName);
                if (newWeaponType != null) {
                    if (newWeaponType.getType().equals("Melee")) {
                        playerManager.stopActions(this);
                        meleeAttack(newWeaponType, registry.getPlayerSelectedItemLevel(this));
                    } else if (newWeaponType.getType().equals("Ranged")) {
                        playerManager.stopActions(this);
                        rangedAttack(newWeaponType, registry.getPlayerSelectedItemLevel(this), clickPoint);
                    }
                }
            }
        }
        mouseClickHeld = true;
    }

    @Override
    public void attack() {
        attack(null);
    }

    public int getSelectedItemIndex() {
        return selectedItem + INVENTORY_SIZE - QB_INVENTORY_SIZE;
    }

    public int getPlacingSlot() {
        return placingSlot;
    }

    public void handleClick(Point clickPoint) {
        if(cameraReturning) {
            this.setCameraReturning(false);
        }
        if (!insideRobot && !statusStun) {
            if (registry.getPlayerSelectedItemType(this).equals("Weapon")) {
                attack(clickPoint);
            } else if (inventory.getCategoryFromSlot(getSelectedItemIndex()).equals("Placeable")) {
                if (!playerManager.currentlyPlacing() && playerManager.getCurrentPlayer() == this) {
                    if (playerManager.playerStandingOnTownBlocks() || inventory.getNameFromSlot(getSelectedItemIndex()).equals("TownBlock")) {
                        placingSlot = getSelectedItemIndex();
                        playerManager.loadPlaceable(inventory.getNameFromSlot(placingSlot), mapX + baseOffset, mapY);
                    } else {
                        registry.showMessage("Error", "You must be standing on a base to place an object");
                    }
                }
            } else if (inventory.getTypeFromSlot(getSelectedItemIndex()).equals("Animal")) {
                if (registry.getPlayerSelectedItemName(this).equals("Pig")) {
                    SoundClip cl = new SoundClip(registry, "Monster/DiePig", getCenterPoint());
                    Monster m = registry.getMonsterManager().spawn("Pig", "Roaming", mapX, mapY);
                    m.setPosition(mapX, mapY);
                    playerManager.playerDeleteInventory(registry.getPlayerSelectedItemSlotIndex(this), 1);
                }
            } else if (inventory.getTypeFromSlot(getSelectedItemIndex()).equals("Usable")) {
                if (registry.getPlayerSelectedItemName(this).equals("Compass")) {
                    SoundClip cl = new SoundClip(registry, "Misc/Teleport", getCenterPoint());
                    setPositionToSpawn();
                } else if (registry.getPlayerSelectedItemName(this).equals("BrokenMirror")) {
                    SoundClip cl = new SoundClip(registry, "Misc/Teleport", getCenterPoint());
                    setPositionToRandom();
                }
            } else if (inventory.getTypeFromSlot(getSelectedItemIndex()).equals("Consumable")) {
                if (hitPoints >= baseHitPoints) {
                    registry.showMessage("Error", "You already have full hitpoints");
                } else {
                    boolean consumed = false;
                    if (registry.getPlayerSelectedItemName(this).equals("Bacon")) {
                        addHitPoints((int) (((float) totalHitPoints / 100f) * 20f));
                        consumed = true;
                    } else if (registry.getPlayerSelectedItemName(this).equals("Bread")) {
                        addHitPoints((int) (((float) totalHitPoints / 100f) * 30f));
                        consumed = true;
                    } else if (registry.getPlayerSelectedItemName(this).equals("Pumpkin")) {
                        addHitPoints((int) (((float) totalHitPoints / 100f) * 60f));
                        consumed = true;
                    } else if (registry.getPlayerSelectedItemName(this).equals("TulipSandwich")) {
                        addHitPoints((int) (((float) totalHitPoints / 100f) * 75f));
                        consumed = true;
                        registry.getMonsterManager().spawnBossOrc(this);
                    }
                    if (consumed) {
                        playerManager.playerDeleteInventory(registry.getPlayerSelectedItemSlotIndex(this), 1);
                        SoundClip cl = new SoundClip(registry, "Player/Eat" + Rand.getRange(1, 2), getCenterPoint());
                    }
                }
            }

            mouseClickHeld = true;
        }
    }

    public void handleRightClick() {
        playerManager.cancelPlaceable();
    }

    public void handleReleased(Point clickPoint) {
        mouseClickHeld = false;
        stateChanged = true;
        updateImage();
        if (particleEmitter != null) {
            particleEmitter.setActive(false);
            if (weaponSoundClip != null) {
                weaponSoundClip.stop();
            }
        }
    }

    @Override
    public void meleeAttack(WeaponType newWeaponType, int level) {
        if (newWeaponType != null) {
            Point mapPoint = new Point(registry.getMouseMapPosition());
            actionMode = ActionMode.ATTACKING;
            currentAttackType = AttackType.MELEE;
            stateChanged = true;
            attackRefreshTimerStart = System.currentTimeMillis();
            attackRefreshTimerEnd = System.currentTimeMillis() + newWeaponType.getSpeed();

            attackArc = getAttackArc();

            int kbX = newWeaponType.getKnockBackX();
            if (facing == Facing.LEFT) {
                kbX = -1 * kbX;
            }

            int[] damages = newWeaponType.getDamage();

            int damage = (int) ((float) damages[level] + this.getAttackBonus());
            damage -= (int) ((float) damage * (REZ_SICKNESS_MULTIPLIER * (float) rezSicknessStack));
            if (damage <= 0) {
                damage = 1;
            }

            ArrayList<String> monstersHit = playerManager.attackDamageAndKnockBack(this, attackArc, mapPoint, damage, kbX, newWeaponType.getKnockBackY(), newWeaponType.getMaxHits(), newWeaponType.getItemName());
            if (monstersHit.contains("Pig") && newWeaponType.getItemName().equals("Net")) {
                this.playerAddItem(0, "Pig", 1);
            }
        }
    }

    public void rangedAttack(WeaponType newWeaponType, int level, Point clickPoint) {
        if (newWeaponType != null && clickPoint != null) {
            actionMode = ActionMode.ATTACKING;
            currentAttackType = AttackType.RANGE;
            stateChanged = true;
            attackRefreshTimerStart = System.currentTimeMillis();
            attackRefreshTimerEnd = System.currentTimeMillis() + newWeaponType.getSpeed();

            int[] damages = newWeaponType.getDamage();

            int damage = (int) ((float) damages[level] + this.getAttackBonus());
            damage -= (int) ((float) damage * (REZ_SICKNESS_MULTIPLIER * (float) rezSicknessStack));
            if (damage <= 0) {
                damage = 1;
            }

            totalRangedAnimationFrames = newWeaponType.getAnimationFrames();

            if (newWeaponType.getItemName().equals("FlameCannon")) {
                if (!mouseClickHeld && particleEmitter != null) {
                    particleEmitter.destroy();
                    particleEmitter = null;
                }
                setFlameCannon(damages[level]);
            } else {
                String projectileType = "Arrow";
                int projectileSpeed = 20;
                boolean projectileShoot = true;

                if (newWeaponType.getItemName().equals("Boomerang")) {
                    projectileType = "Boomerang";
                    projectileSpeed = 12;
                    if (projectileOut) {
                        projectileShoot = false;
                    } else {
                        projectileOut = true;
                    }
                    if (projectileShoot) {
                        Projectile p = registry.getProjectileManager().createReturningProjectile(this,
                                projectileType,
                                projectileSpeed,
                                new Point(
                                this.getMapX() + baseOffset + spriteRect.width / 2,
                                this.getMapY() + spriteRect.height / 2),
                                new Point(
                                playerManager.panelToMapX(clickPoint.x),
                                playerManager.panelToMapY(clickPoint.y)),
                                true,
                                false,
                                false,
                                damage);
                        p.setSpinning("Boomerang");
                        p.setSound("Projectile/Boomerang", true);
                    }
                } else {
                    if (newWeaponType.getItemName().equals("SlingShot")) {
                        projectileType = "Pebble";
                    } else if (newWeaponType.getItemName().equals("HandCannon")) {
                        projectileType = "Bullet";
                    } else if (newWeaponType.getItemName().equals("AutoHandCannon")) {
                        projectileType = "Bullet";
                        damage = (int) ((float) damages[level] + (this.getAttackBonus() / 4));
                        damage -= (int) ((float) damage * (REZ_SICKNESS_MULTIPLIER * (float) rezSicknessStack));
                        if (damage <= 0) {
                            damage = 1;
                        }
                    }

                    registry.getProjectileManager().createProjectile(this,
                            projectileType,
                            projectileSpeed,
                            new Point(
                            this.getMapX() + baseOffset + spriteRect.width / 2,
                            this.getMapY() + spriteRect.height / 2),
                            new Point(
                            playerManager.panelToMapX(clickPoint.x),
                            playerManager.panelToMapY(clickPoint.y)),
                            true,
                            false,
                            false,
                            damage);
                }
            }
        }
    }

    public void stopJump() {
        if (vertMoveMode == VertMoveMode.JUMPING) {
            setVertMoveMode(VertMoveMode.FALLING);
            setFallSize(0);
        }
    }

    public void stopAscend() {
        if (vertMoveMode == VertMoveMode.FLYING) {
            setVertMoveMode(VertMoveMode.FALLING);
        }
    }

    public void robotSetMode(String m) {
        robot.setMode(m);
    }

    public void robotToggleActivated() {
        robot.toggleActivated(mapX, mapY, false);
    }

    public void robotToggleFollow() {
        robot.toggleFollow();
    }

    public int getAdjustedDamage(int damage) {
        damage = (int) ((float) damage * (1f + (REZ_SICKNESS_MULTIPLIER * (float) rezSicknessStack)));
        damage -= Math.floor(getArmorPoints() / 5);
        return damage;
    }

    public int getTotalHitPoints() {
        return totalHitPoints;
    }

    @Override
    public int applyDamage(int damage, Actor a) {
        if (insideRobot) {
            return 0;
        } else {
            if (damage <= 0) {
                return 0;
            }

            damage = getAdjustedDamage(damage);

            registerAttacker(a, damage);

            if (damage <= 0) {
                damage = 1;
            }

            if (damage > 0) {
                SoundClip cl = new SoundClip("Player/Hurt" + Rand.getRange(1, 5));
                registry.getIndicatorManager().createIndicator(mapX + (width / 2), mapY + 50, "-" + Integer.toString(damage));
                hitPoints -= damage;
                invulnerable = true;
                playerManager.stopActions(this);
            }
        }

        if (registry.getGameController().multiplayerMode == registry.getGameController().multiplayerMode.SERVER && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(this.getId());
                up.mapX = this.getMapX();
                up.mapY = this.getMapY();
                up.vertMoveMode = this.getVertMoveMode();
                up.action = "ApplyDamage";
                up.dataInt = damage;
                up.actor = a;
                registry.getNetworkThread().sendData(up);
            }
        }

        return damage;
    }

    @Override
    protected void setStatuses() {
        super.setStatuses();

        statusHeal = false;
        if (currentHPRegenRate > BASE_HP_REGEN_RATE) {
            statusHeal = true;
        }
    }

    public boolean getIsTryingToFly() {
        return isTryingToMove;
    }

    /*
     * public void setIsTryingToFlap(boolean f) { up.vertMoveMode =
     * this.getVertMoveMode(); if (f != isTryingToFlap) { if
     * (registry.getGameController().multiplayerMode ==
     * registry.getGameController().multiplayerMode.CLIENT &&
     * registry.getNetworkThread() != null) { if
     * (registry.getNetworkThread().readyForUpdates) { UpdatePlayer up = new
     * UpdatePlayer(this.getId()); up.mapX = this.getMapX(); up.mapY =
     * this.getMapY(); up.vertMoveMode = this.getVertMoveMode(); up.action =
     * "SetIsTryingToFlap"; registry.getNetworkThread().sendData(up); } } }
     * isTryingToFlap = f; }
     */
    @Override
    public void flap() {
        if (vertMoveMode != VertMoveMode.FLYING) {
            setVertMoveMode(VertMoveMode.FLYING);
        }
        if (playerManager.getCurrentPlayer() == this && registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(this.getId());
                up.mapX = this.getMapX();
                up.mapY = this.getMapY();
                up.vertMoveMode = this.getVertMoveMode();
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    @Override
    public void update() {
        if (weaponImages[0] == null) {
            setWeaponImages();
        }

        super.update();

        updateArmorPoints();

        if (vertMoveMode == vertMoveMode.NOT_JUMPING && !insideRobot) {
            startJumpSize = JUMP_SIZE;
            if (armorFeet != null) {
                if (armorFeet.getName().equals("SpringFeet")) {
                    startJumpSize = JUMP_SIZE_INSIDE_ROBOT;
                }
            }
            jumpSize = startJumpSize;
        }

        boolean attackingRanged = false;

        if (insideRobot) {
            xMoveSize = MOVE_SPEED_INSIDE_ROBOT;
        } else if (isSlowed) {
            xMoveSize = MOVE_SPEED_SLOWED;
        } else {
            xMoveSize = MOVE_SPEED;
        }

        //allows you to fly with propeller while holding space
        if (insideRobot && robot.getInventory().contains("Propeller") && !statusStun) {
            if (vertMoveMode != VertMoveMode.JUMPING && vertMoveMode != VertMoveMode.NOT_JUMPING) {
                if (playerManager.isKeyDown(Settings.buttonJump)) {
                    //EIError.debugMsg("Update: Flap");
                    flap();
                }
            }
        }

        //update animation
        if (isActive && isAnimating && actionMode == ActionMode.ATTACKING) {
            currentAnimationFrame++;
            if (currentAnimationFrame >= 6) {
                currentAnimationFrame = 0;
                actionMode = ActionMode.NONE;
                stateChanged = true;
                updateImage();
            }
        } else if (isActive && isAnimating) {
            if (animationFrameUpdateTime <= registry.currentTime) {
                currentAnimationFrame++;
                if (currentAnimationFrame >= numAnimationFrames) {
                    currentAnimationFrame = 0;
                }
                animationFrameUpdateTime = registry.currentTime + animationFrameDuration;
            }
        }

        //update position
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
                } else if (vertMoveMode != VertMoveMode.NOT_JUMPING) {
                    checkCollide(0);
                }
            }
        }

        //jumping/falling checks
        if (vertMoveMode == VertMoveMode.JUMPING) {
            updateJumping();
        } else if (vertMoveMode == VertMoveMode.FLYING) {
            updateAscending();
        } else if (vertMoveMode == VertMoveMode.FALLING) {
            updateFalling();
            if (fallSize > startJumpSize + 2 * gravity) {
                stateChanged = true;
            }
        }

        //check to see if player is touching an enemy
        if (!invulnerable && registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.CLIENT) {
            Damage damage = playerManager.getMonsterTouchDamage(spriteRect);
            if (damage != null) {
                int touchDamage = damage.getAmount();
                if (touchDamage > 0) {
                    applyDamage(touchDamage, damage.getSource());
                    if (damage.getKnockBackX() > 0 || damage.getKnockBackY() > 0) {
                        applyKnockBack(damage.getKnockBackX(), damage.getKnockBackY());
                    }
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

        updateImage();

        if (hitPoints <= 0) {
            setCameraReturning(true);
            
            BufferedImage im = null;
 
            if (isAnimating) {
                im = registry.getImageLoader().getImage(image, currentAnimationFrame);
            } else {
                im = registry.getImageLoader().getImage(image);
            }
            registry.getPixelizeManager().pixelize(im, mapX, mapY);
            
            int oldX = mapX;
            int oldY = mapY;
            resetPlayer();

            //take away 5% of xp
            float totalXPForCurrentLevel = 0;
            if (this.getLevel() > 1) {
                totalXPForCurrentLevel = playerManager.getXPNeededForLevel(this.getLevel() + 1) - playerManager.getXPNeededForLevel(this.getLevel());
            } else {
                totalXPForCurrentLevel = playerManager.getXPNeededForLevel(this.getLevel() + 1);
            }
            this.removeXP((int) (totalXPForCurrentLevel * 0.05f), oldX, oldY);

            playerManager.playerDied();
        }

        if (this.getHitPointPercentage() >= 10 && this.getHitPointPercentage() <= 15) {
            if (lowHPSound != null) {
                if (!lowHPSound.equals("HeartBeatSlow")) {
                    if (lowHPSoundClip != null) {
                        lowHPSoundClip.stop();
                    }
                    lowHPSound = "HeartBeatSlow";
                    lowHPSoundClip = new SoundClip("Player/" + lowHPSound);
                    lowHPSoundClip.setLooping(true);
                }
            } else {
                lowHPSound = "HeartBeatSlow";
                lowHPSoundClip = new SoundClip("Player/" + lowHPSound);
                lowHPSoundClip.setLooping(true);
            }
        } else if (this.getHitPointPercentage() >= 0 && this.getHitPointPercentage() <= 9) {
            if (lowHPSound != null) {
                if (!lowHPSound.equals("HeartBeatFast")) {
                    if (lowHPSoundClip != null) {
                        lowHPSoundClip.stop();
                    }
                    lowHPSound = "HeartBeatFast";
                    lowHPSoundClip = new SoundClip("Player/" + lowHPSound);
                    lowHPSoundClip.setLooping(true);
                }
            } else {
                lowHPSound = "HeartBeatFast";
                lowHPSoundClip = new SoundClip("Player/" + lowHPSound);
                lowHPSoundClip.setLooping(true);
            }
        } else {
            if (lowHPSound != null) {
                if (!lowHPSound.isEmpty()) {
                    if (lowHPSoundClip != null) {
                        lowHPSoundClip.stop();
                    }
                    lowHPSound = null;
                }
            }
        }

        if (robot != null) {
            robot.update();
        }

        if (particleEmitter != null) {
            if (facing == Actor.Facing.LEFT) {
                particleEmitter.setPosition(mapX, mapY + 30);
            } else {
                particleEmitter.setPosition(mapX + 55, mapY + 30);
            }
            particleEmitter.update();
        }

        if (mouseClickHeld == true && attackRefreshTimerEnd < registry.currentTime && !insideRobot) {
            attack(registry.getMousePosition());
        }
    }

    public void updateLong() {
        //calc HP regen rate
        currentHPRegenRate = BASE_HP_REGEN_RATE;
        currentHPRegenRate += registry.getPlaceableManager().getHPRegenerationBonus(getCenterPoint());

        //regen some HP
        float hpToRegen = (getTotalHitPoints() / 100f) * (float) currentHPRegenRate;
        hpToRegen = hpToRegen / 60f;

        currentHPRegen += hpToRegen;
        if (currentHPRegen >= 1) {
            addHitPoints((int) currentHPRegen);
            currentHPRegen = 0;
        }

        //calc attack bonus
        statusAttackBonus = false;
        attackBonus = 1f + registry.getPlaceableManager().getAttackBonus(getCenterPoint());
        if (attackBonus > 1) {
            statusAttackBonus = true;
        }

        //see if we have rez sickness
        statusRezSickness = false;
        if (rezSicknessStack > 0) {
            if (rezSicknessEnd <= System.currentTimeMillis()) {
                rezSicknessStack = 0;
            }
            if (rezSicknessStack > 0) {
                statusRezSickness = true;
            }
        }
    }

    public void renderPlayer(Graphics g, int x, int y, boolean imageOverride) {
        BufferedImage im;
        String itemName = "";
        int statusCount = 0;
        int statusX = 0;
        int statusNewXPos = 0;
        WeaponType newWeaponType = null;

        int xPos, yPos;

        int animationFrame = currentAnimationFrame;

        //draw springs
        if (armorFeet != null && vertMoveMode != VertMoveMode.NOT_JUMPING) {
            if (armorFeet.getName().equals("SpringFeet")) {
                im = registry.getImageLoader().getImage("Player/Spring");
                if (im != null) {
                    if (vertMoveMode == VertMoveMode.FALLING && fallSize > startJumpSize) {
                        xPos = manager.mapToPanelX(mapX + 23);
                        yPos = manager.mapToPanelY(mapY + 5);

                        //flip the yPos since drawing happens top down versus bottom up
                        yPos = manager.getPHeight() - yPos;

                        g.drawImage(im, xPos, yPos, null);

                        xPos = manager.mapToPanelX(mapX + 30);
                        yPos = manager.mapToPanelY(mapY + 5);

                        //flip the yPos since drawing happens top down versus bottom up
                        yPos = manager.getPHeight() - yPos;

                        g.drawImage(im, xPos, yPos, null);
                    } else if (vertMoveMode == VertMoveMode.JUMPING) {
                        if (facing == Facing.RIGHT) {
                            xPos = manager.mapToPanelX(mapX + 23);
                            yPos = manager.mapToPanelY(mapY + 5);

                            //flip the yPos since drawing happens top down versus bottom up
                            yPos = manager.getPHeight() - yPos;

                            g.drawImage(im, xPos, yPos, null);

                            xPos = manager.mapToPanelX(mapX + 30);
                            yPos = manager.mapToPanelY(mapY + 8);

                            //flip the yPos since drawing happens top down versus bottom up
                            yPos = manager.getPHeight() - yPos;

                            g.drawImage(im, xPos, yPos, null);
                        } else {
                            xPos = manager.mapToPanelX(mapX + 23);
                            yPos = manager.mapToPanelY(mapY + 8);

                            //flip the yPos since drawing happens top down versus bottom up
                            yPos = manager.getPHeight() - yPos;

                            g.drawImage(im, xPos, yPos, null);

                            xPos = manager.mapToPanelX(mapX + 30);
                            yPos = manager.mapToPanelY(mapY + 5);

                            //flip the yPos since drawing happens top down versus bottom up
                            yPos = manager.getPHeight() - yPos;

                            g.drawImage(im, xPos, yPos, null);
                        }
                    } else {
                        if (!isTryingToMove) {
                            xPos = manager.mapToPanelX(mapX + 23);
                            yPos = manager.mapToPanelY(mapY + 5);

                            //flip the yPos since drawing happens top down versus bottom up
                            yPos = manager.getPHeight() - yPos;

                            g.drawImage(im, xPos, yPos, null);

                            xPos = manager.mapToPanelX(mapX + 30);
                            yPos = manager.mapToPanelY(mapY + 5);

                            //flip the yPos since drawing happens top down versus bottom up
                            yPos = manager.getPHeight() - yPos;

                            g.drawImage(im, xPos, yPos, null);
                        } else {
                            if (animationFrame == 0) {
                                xPos = manager.mapToPanelX(mapX + 23);
                                yPos = manager.mapToPanelY(mapY + 5);

                                //flip the yPos since drawing happens top down versus bottom up
                                yPos = manager.getPHeight() - yPos;

                                g.drawImage(im, xPos, yPos, null);

                                xPos = manager.mapToPanelX(mapX + 30);
                                yPos = manager.mapToPanelY(mapY + 5);

                                //flip the yPos since drawing happens top down versus bottom up
                                yPos = manager.getPHeight() - yPos;

                                g.drawImage(im, xPos, yPos, null);
                            } else {
                                xPos = manager.mapToPanelX(mapX + 22);
                                yPos = manager.mapToPanelY(mapY + 5);

                                //flip the yPos since drawing happens top down versus bottom up
                                yPos = manager.getPHeight() - yPos;

                                g.drawImage(im, xPos, yPos, null);

                                xPos = manager.mapToPanelX(mapX + 32);
                                yPos = manager.mapToPanelY(mapY + 6);

                                //flip the yPos since drawing happens top down versus bottom up
                                yPos = manager.getPHeight() - yPos;

                                g.drawImage(im, xPos, yPos, null);
                            }
                        }
                    }
                }
            }
        }

        if (actionMode == ActionMode.ATTACKING || mouseClickHeld) {
            itemName = registry.getPlayerSelectedItemName(this);
            newWeaponType = Weapon.getWeaponType(itemName);
            if (newWeaponType != null) {
                if (newWeaponType.getType().equals("Ranged")) {
                    isAnimating = true;
                    animationFrame = 4;
                }
            }
        }

        if (imageOverride) {
            im = registry.getImageLoader().getImage("Player/Standing");
        } else {
            if (isAnimating) {
                im = registry.getImageLoader().getImage(image, animationFrame);
            } else {
                im = registry.getImageLoader().getImage(image);
            }
            if (invulnerable) {
                if (invulnerableShow) {
                    invulnerableShow = false;
                } else {
                    invulnerableShow = true;
                    im = null;
                }
            }
        }

        if (x > 0 || y > 0) {
            xPos = x;
            yPos = y;
        } else {
            xPos = playerManager.mapToPanelX(mapX);
            yPos = playerManager.mapToPanelY(mapY);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = playerManager.getPHeight() - yPos;

            //subtract the height since points are bottom left and drawing starts from top left
            yPos -= height;
        }

        if (im != null) {
            if (facing == Facing.LEFT && !imageOverride) {
                AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
                tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-width, 0);
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                BufferedImage imLeft = op.filter(im, null);
                if (imLeft != null) {
                    g.drawImage(imLeft, xPos, yPos, null);
                }
            } else {
                g.drawImage(im, xPos, yPos, null);
            }

            renderArmor(g, xPos, yPos, imageOverride, animationFrame);

            //render attack
            if ((actionMode == ActionMode.ATTACKING || mouseClickHeld) && !imageOverride && currentAttackType == AttackType.RANGE) {
                if (newWeaponType != null) {
                    if (newWeaponType.getType().equals("Ranged")) {
                        renderRangedAttack(g);
                    }
                }
            }
            if (actionMode == ActionMode.ATTACKING && !imageOverride) {
                if (newWeaponType != null) {
                    if (newWeaponType.getType().equals("Melee") && currentAttackType != AttackType.RANGE) {
                        renderMeleeAttack(g, xPos, yPos);
                    }
                }
            }
        }

        if (!imageOverride) {
            //render parachute?
            if (vertMoveMode == VertMoveMode.FALLING && fallSize > startJumpSize) {
                if (inventory.containsFromTop("Parachute", 10)) {
                    xPos = manager.mapToPanelX(mapX - 7);
                    yPos = manager.mapToPanelY(mapY + 40);

                    //flip the yPos since drawing happens top down versus bottom up
                    yPos = manager.getPHeight() - yPos;

                    //subtract the height since points are bottom left and drawing starts from top left
                    yPos -= height;

                    im = registry.getImageLoader().getImage("Player/Parachute");
                    if (im != null) {
                        g.drawImage(im, xPos, yPos, null);
                    }
                }
            }

            //render statuses
            if (statusAttackBonus) {
                statusCount++;
            }
            if (statusFear) {
                statusCount++;
            }
            if (statusHeal) {
                statusCount++;
            }
            if (statusPoison) {
                statusCount++;
            }
            if (statusRezSickness) {
                statusCount++;
            }
            if (statusStun) {
                statusCount++;
            }
            if (statusSlowed) {
                statusCount++;
            }

            if (statusCount > 0) {
                int totalWidth = (statusCount * STATUS_WIDTH) + ((statusCount - 1) * STATUS_SPACING);
                statusX = getCenterPoint().x - (totalWidth / 2);
                int i = 0;

                if (statusFear) {
                    statusNewXPos = statusX + (i * STATUS_WIDTH) + (i * STATUS_SPACING);

                    xPos = manager.mapToPanelX(statusNewXPos);
                    yPos = manager.mapToPanelY(mapY + 30);

                    //flip the yPos since drawing happens top down versus bottom up
                    yPos = manager.getPHeight() - yPos;

                    //subtract the height since points are bottom left and drawing starts from top left
                    yPos -= height;

                    im = registry.getImageLoader().getImage("Effects/Fear");
                    if (im != null) {
                        g.drawImage(im, xPos, yPos, null);
                    }
                    i++;
                }

                if (statusAttackBonus) {
                    statusNewXPos = statusX + (i * STATUS_WIDTH) + (i * STATUS_SPACING);

                    xPos = manager.mapToPanelX(statusNewXPos);
                    yPos = manager.mapToPanelY(mapY + 30);

                    //flip the yPos since drawing happens top down versus bottom up
                    yPos = manager.getPHeight() - yPos;

                    //subtract the height since points are bottom left and drawing starts from top left
                    yPos -= height;

                    im = registry.getImageLoader().getImage("Effects/AttackBonus");
                    if (im != null) {
                        g.drawImage(im, xPos, yPos, null);
                    }
                    i++;
                }

                if (statusHeal) {
                    statusNewXPos = statusX + (i * STATUS_WIDTH) + (i * STATUS_SPACING);

                    xPos = manager.mapToPanelX(statusNewXPos);
                    yPos = manager.mapToPanelY(mapY + 30);

                    //flip the yPos since drawing happens top down versus bottom up
                    yPos = manager.getPHeight() - yPos;

                    //subtract the height since points are bottom left and drawing starts from top left
                    yPos -= height;

                    im = registry.getImageLoader().getImage("Effects/Heal");
                    if (im != null) {
                        g.drawImage(im, xPos, yPos, null);
                    }
                    i++;
                }

                if (statusPoison) {
                    statusNewXPos = statusX + (i * STATUS_WIDTH) + (i * STATUS_SPACING);

                    xPos = manager.mapToPanelX(statusNewXPos);
                    yPos = manager.mapToPanelY(mapY + 30);

                    //flip the yPos since drawing happens top down versus bottom up
                    yPos = manager.getPHeight() - yPos;

                    //subtract the height since points are bottom left and drawing starts from top left
                    yPos -= height;

                    im = registry.getImageLoader().getImage("Effects/Poison");
                    if (im != null) {
                        g.drawImage(im, xPos, yPos, null);
                    }
                    i++;
                }

                if (statusRezSickness) {
                    statusNewXPos = statusX + (i * STATUS_WIDTH) + (i * STATUS_SPACING);

                    xPos = manager.mapToPanelX(statusNewXPos);
                    yPos = manager.mapToPanelY(mapY + 30);

                    //flip the yPos since drawing happens top down versus bottom up
                    yPos = manager.getPHeight() - yPos;

                    //subtract the height since points are bottom left and drawing starts from top left
                    yPos -= height;

                    im = registry.getImageLoader().getImage("Effects/RezSickness");
                    if (im != null) {
                        g.drawImage(im, xPos, yPos, null);
                    }
                    i++;
                }

                if (statusStun) {
                    statusNewXPos = statusX + (i * STATUS_WIDTH) + (i * STATUS_SPACING);

                    xPos = manager.mapToPanelX(statusNewXPos);
                    yPos = manager.mapToPanelY(mapY + 30);

                    //flip the yPos since drawing happens top down versus bottom up
                    yPos = manager.getPHeight() - yPos;

                    //subtract the height since points are bottom left and drawing starts from top left
                    yPos -= height;

                    im = registry.getImageLoader().getImage("Effects/Stun");
                    if (im != null) {
                        g.drawImage(im, xPos, yPos, null);
                    }
                    i++;
                }

                if (statusSlowed) {
                    statusNewXPos = statusX + (i * STATUS_WIDTH) + (i * STATUS_SPACING);

                    xPos = manager.mapToPanelX(statusNewXPos);
                    yPos = manager.mapToPanelY(mapY + 30);

                    //flip the yPos since drawing happens top down versus bottom up
                    yPos = manager.getPHeight() - yPos;

                    //subtract the height since points are bottom left and drawing starts from top left
                    yPos -= height;

                    im = registry.getImageLoader().getImage("Effects/Slowed");
                    if (im != null) {
                        g.drawImage(im, xPos, yPos, null);
                    }
                    i++;
                }
            }
        }

        if (actionMode == ActionMode.GATHERING && animationFrame == 0 && currentResourceType != null) {
            if (registry.currentTime - lastResourceSoundPlay > 500) {
                //only play if we haven't played for at least a half second
                lastResourceSoundPlay = registry.currentTime;
                SoundClip cl = new SoundClip(registry, "Player/Gather" + currentResourceType.getType() + Rand.getRange(1, 3), getCenterPoint());
            }
        }
    }

    public void renderMeleeAttack(Graphics g, int xPos, int yPos) {
        int[] offset = null;
        BufferedImage weaponImage = null;
        if (currentAnimationFrame <= 5) {
            offset = getMeleeAttackRotateOffsets(weaponImages[0]);
            offset[0] += xPos;
            offset[1] += yPos;

            weaponImage = getMeleeAttackRotateImage();
            g.drawImage(weaponImage, offset[0], offset[1], null);
        }
    }

    public void renderRangedAttack(Graphics g) {
        BufferedImage weaponImage = null;
        String itemName = registry.getPlayerSelectedItemName(this);
        if (totalRangedAnimationFrames > 1) {
            if (currentAnimationFrame != currentRangedAnimationFrames) {
                currentRangedAnimationFrames++;
                if (currentRangedAnimationFrames >= totalRangedAnimationFrames) {
                    currentRangedAnimationFrames = 0;
                }
            }
            weaponImage = registry.getImageLoader().getImage("Weapons/" + itemName, currentRangedAnimationFrames);
        } else {
            weaponImage = registry.getImageLoader().getImage("Weapons/" + itemName);
        }

        if (weaponImage != null) {
            int xPos = playerManager.mapToPanelX(mapX);
            int yPos = playerManager.mapToPanelY(mapY);

            //flip the yPos since drawing happens top down versus bottom up
            yPos = playerManager.getPHeight() - yPos;

            //subtract the height since points are bottom left and drawing starts from top left
            yPos -= height;

            if (facing == Facing.LEFT) {
                AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
                tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-width, 0);
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                weaponImage = op.filter(weaponImage, null);
            }
            g.drawImage(weaponImage, xPos, yPos, null);
        }
    }

    private BufferedImage getMeleeAttackRotateImage() {
        BufferedImage weaponImage = null;
        if (facing == Facing.RIGHT) {
            weaponImage = weaponImages[currentAnimationFrame];
        } else {
            weaponImage = weaponImages[currentAnimationFrame + 6];
        }
        return weaponImage;
    }

    private int[] getMeleeAttackRotateOffsets(BufferedImage weaponImage) {
        int angle = 90 - (int) ((float) currentAnimationFrame / 4.0f * 90.0f);
        int[] offset = new int[2];
        if (weaponImage != null) {
            offset[0] = -1 * weaponImage.getWidth() / 2 + 20;
            offset[1] = -1 * weaponImage.getHeight() / 2 + 34;
            if (facing == Facing.LEFT) {
                angle = 90 + (int) ((float) currentAnimationFrame / 4.0f * 90.0f);
                offset[0] += 20;
            }
            offset[0] += (int) ((weaponImage.getHeight() / 2 + 20) * Math.cos(Math.toRadians(angle)));
            offset[1] -= (int) ((weaponImage.getHeight() / 2 + 15) * Math.sin(Math.toRadians(angle)));
        } else {
            offset[0] = 0;
            offset[1] = 0;
        }

        return offset;
    }

    public void renderArmor(Graphics g, int xPos, int yPos, boolean imageOverride, int animationFrame) {
        BufferedImage im;

        //draw the feet armor
        if (armorFeet != null) {
            String armorImageName = armorFeet.getImageName();

            if (imageOverride) {
                armorImageName += "/Standing";
                im = registry.getImageLoader().getImage(armorImageName);
            } else {
                armorImageName += image.replace("Player/", "/");
                if (isAnimating) {
                    im = registry.getImageLoader().getImage(armorImageName, animationFrame);
                } else {
                    im = registry.getImageLoader().getImage(armorImageName);
                }
            }

            if (im != null) {
                if (facing == Facing.LEFT && !imageOverride) {
                    AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
                    tx = AffineTransform.getScaleInstance(-1, 1);
                    tx.translate(-width, 0);
                    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imLeft = op.filter(im, null);
                    if (imLeft != null) {
                        g.drawImage(imLeft, xPos, yPos, null);
                    }
                } else {
                    g.drawImage(im, xPos, yPos, null);
                }
            }
        }

        //draw the legs armor
        if (armorLegs != null) {
            String armorImageName = armorLegs.getImageName();

            if (imageOverride) {
                armorImageName += "/Standing";
                im = registry.getImageLoader().getImage(armorImageName);
            } else {
                armorImageName += image.replace("Player/", "/");
                if (isAnimating) {
                    im = registry.getImageLoader().getImage(armorImageName, animationFrame);
                } else {
                    im = registry.getImageLoader().getImage(armorImageName);
                }
            }

            if (im != null) {
                if (facing == Facing.LEFT && !imageOverride) {
                    AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
                    tx = AffineTransform.getScaleInstance(-1, 1);
                    tx.translate(-width, 0);
                    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imLeft = op.filter(im, null);
                    if (imLeft != null) {
                        g.drawImage(imLeft, xPos, yPos, null);
                    }
                } else {
                    g.drawImage(im, xPos, yPos, null);
                }
            }
        }

        //draw the chest armor
        if (armorChest != null) {
            String armorImageName = armorChest.getImageName();

            if (imageOverride) {
                armorImageName += "/Standing";
                im = registry.getImageLoader().getImage(armorImageName);
            } else {
                armorImageName += image.replace("Player/", "/");
                if (isAnimating) {
                    im = registry.getImageLoader().getImage(armorImageName, animationFrame);
                } else {
                    im = registry.getImageLoader().getImage(armorImageName);
                }
            }

            if (im != null) {
                if (facing == Facing.LEFT && !imageOverride) {
                    AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
                    tx = AffineTransform.getScaleInstance(-1, 1);
                    tx.translate(-width, 0);
                    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imLeft = op.filter(im, null);
                    if (imLeft != null) {
                        g.drawImage(imLeft, xPos, yPos, null);
                    }
                } else {
                    g.drawImage(im, xPos, yPos, null);
                }
            }
        }

        //draw the head armor
        if (armorHead != null) {
            String armorImageName = armorHead.getImageName();

            if (imageOverride) {
                armorImageName += "/Standing";
                im = registry.getImageLoader().getImage(armorImageName);
            } else {
                armorImageName += image.replace("Player/", "/");
                if (isAnimating) {
                    im = registry.getImageLoader().getImage(armorImageName, animationFrame);
                } else {
                    im = registry.getImageLoader().getImage(armorImageName);
                }
            }

            if (im != null) {
                if (facing == Facing.LEFT && !imageOverride) {
                    AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
                    tx = AffineTransform.getScaleInstance(-1, 1);
                    tx.translate(-width, 0);
                    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imLeft = op.filter(im, null);
                    if (imLeft != null) {
                        g.drawImage(imLeft, xPos, yPos, null);
                    }
                } else {
                    g.drawImage(im, xPos, yPos, null);
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (!insideRobot) {
            renderPlayer(g, 0, 0, false);
            super.render(g);
        }

        if (robot != null) {
            robot.render(g);
        }

        FontMetrics fm = g.getFontMetrics();
        int messageWidth = fm.stringWidth(name);

        int xPos = playerManager.mapToPanelX((int) mapX + (width / 2) - (messageWidth / 2));
        int yPos = playerManager.mapToPanelY((int) mapY);
        yPos = playerManager.getPHeight() - yPos;

        Font textFont = new Font("SansSerif", Font.BOLD, 14);
        g.setFont(textFont);

        registry.ghettoOutline(g, Color.BLACK, name, xPos, yPos - 60);

        g.setColor(Color.white);
        g.drawString(name,
                xPos,
                yPos - 60);

        /*
         * if (attackArc != null) { int xPos =
         * manager.mapToPanelX((int)attackArc.x); int yPos =
         * manager.mapToPanelY((int)attackArc.y); yPos = manager.getPHeight() -
         * yPos; yPos -= attackArc.height; g.setColor(Color.red);
         * g.drawArc(xPos, yPos, (int)attackArc.width, (int)attackArc.height,
         * (int)attackArc.start, (int)attackArc.extent); }
         */

        if (particleEmitter != null) {
            particleEmitter.render(g);
        }
    }

    @Override
    protected void finishJumping() {
        if (registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.CLIENT) {
            //if the player falls more than 20 pixels, start applying fall damage
            // fall damage goes to a max of 120 pixels where the user takes 100% damage
            if ((completeFall - startJumpSize) >= 20) {
                float percentage = (float) (completeFall - startJumpSize - 20) / 100f;
                if (insideRobot) {
                    if (robot.getInventory().contains("Propeller")) {
                        //no fall damage
                    } else {
                        robot.applyDamage((int) ((float) totalHitPoints * percentage), null);
                    }
                } else {
                    if (inventory.containsFromTop("Parachute", 10)) {
                        //no fall damage
                    } else {
                        applyDamage((int) ((float) totalHitPoints * percentage), null);
                    }
                }
            }
        }
        setVertMoveMode(VertMoveMode.NOT_JUMPING, false);
        robot.setVertMoveMode(VertMoveMode.NOT_JUMPING, false);
        totalFall = 0;
        fallSize = 0;

        knockBackX = 0;
        if (!isTryingToMove) {
            stopMove();
        } else if (!image.equals("Player/Walking")) {
            if (facing == Facing.RIGHT) {
                moveRight();
            } else {
                moveLeft();
            }
        }
        isFallAnimating = false;
    }

    @Override
    protected void updateImage() {
        if (stateChanged) {
            if (actionMode == ActionMode.ATTACKING || (mouseClickHeld && currentAttackType == AttackType.RANGE)) {
                if (currentAnimationFrame == 0 || !image.equals("Player/Swinging")) {
                    loopImage("Player/Swinging", 0.10f);
                }
            } else if (vertMoveMode == VertMoveMode.FALLING && fallSize > startJumpSize) {
                loopImage("Player/Falling");
            } else if (vertMoveMode == VertMoveMode.JUMPING) {
                setImage("Player/Jumping");
            } else {
                if (actionMode == ActionMode.GATHERING) {
                    loopImage("Player/Gathering");
                } else if (!isTryingToMove) {
                    setImage("Player/Standing");
                } else {
                    loopImage("Player/Walking");
                }
            }

            stateChanged = false;

            robot.setFacing(facing);
            robot.setVertMoveMode(vertMoveMode);
            robot.setIsTryingToMove(isTryingToMove);
        }
    }

    private void setFlameCannon(int damage) {
        if (particleEmitter == null) {
            ArrayList<String> images = new ArrayList<String>();
            images.add("Particles/Flame1");
            images.add("Particles/Flame2");
            images.add("Particles/Flame3");
            images.add("Particles/Flame4");
            images.add("Particles/Flame5");
            images.add("Particles/Flame6");
            images.add("Particles/Flame7");
            images.add("Particles/Flame8");
            particleEmitter = new ParticleEmitter(registry.getGameController(), registry, this, mapX + 30, mapY + 50, images, true, false, true, 1, 10.0f, 10.0f, 400, true);
            particleEmitter.setParticlesPerGeneration(damage);

            if (weaponSoundClip != null) {
                weaponSoundClip.stop();
            }
            weaponSoundClip = new SoundClip("Projectile/FlameCannon");
            weaponSoundClip.setLooping(true);
        } else {
            particleEmitter.setActive(true);
        }
    }

    private void destoryParticleEmitter() {
        if (particleEmitter != null) {
            particleEmitter.destroy();
            particleEmitter = null;
            if (weaponSoundClip != null) {
                weaponSoundClip.stop();
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