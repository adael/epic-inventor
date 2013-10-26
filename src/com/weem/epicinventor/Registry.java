package com.weem.epicinventor;

import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.actor.monster.*;
import com.weem.epicinventor.hud.*;
import com.weem.epicinventor.indicator.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.item.*;
import com.weem.epicinventor.network.*;
import com.weem.epicinventor.pixelize.*;
import com.weem.epicinventor.placeable.*;
import com.weem.epicinventor.projectile.*;
import com.weem.epicinventor.resource.*;
import com.weem.epicinventor.utility.*;
import com.weem.epicinventor.weapon.*;
import com.weem.epicinventor.world.block.*;

import java.awt.*;
import java.awt.image.*;

public class Registry {

    private GameController gameController;
    private ImageLoader imageLoader;
    private ItemManager itemManager;
    private HUDManager hudManager;
    private BlockManager blockManager;
    private PlaceableManager placeableManager;
    private PlayerManager playerManager;
    private ProjectileManager projectileManager;
    private ResourceManager resourceManager;
    private MonsterManager monsterManager;
    private IndicatorManager indicatorManager;
    private PixelizeManager pixelizeManager;
    private Inventory inventory;
    private NetworkThread networkThread;
    private int invSlotFrom = -1;
    private ItemContainer invItemContainerFrom;
    private PlayerContainer invPlayerContainerFrom;
    private Farm invFarmFrom;
    private boolean isQuickBarLocked;
    private String invHUDFrom = "";
    private String statusText = "";
    private int splitCount;
    public long currentTime = 0;
    private boolean bossFight;
    private int attackBonus = 0;
    private int hpBonus = 0;
    private boolean showPortrait;
    private int portraitHP = 0;
    private int portraitHPCurrent = 0;
    private int portraitAttack = 0;
    private String portraitImage = "";
    private String weaponType = "";
    private int weaponLevel = 1;
    private String armorType = "";
    private int armorLevel = 1;

    public Registry() {
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public BlockManager getBlockManager() {
        return blockManager;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public PlaceableManager getPlaceableManager() {
        return placeableManager;
    }

    public ProjectileManager getProjectileManager() {
        return projectileManager;
    }

    public MonsterManager getMonsterManager() {
        return monsterManager;
    }

    public HUDManager getHUDManager() {
        return hudManager;
    }

    public IndicatorManager getIndicatorManager() {
        return indicatorManager;
    }

    public PixelizeManager getPixelizeManager() {
        return pixelizeManager;
    }

    public NetworkThread getNetworkThread() {
        return networkThread;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public int getHPBonus() {
        return hpBonus;
    }

    public boolean getBossFight() {
        return bossFight;
    }

    public Player getClosestPlayer(Point p, int maxDistance) {
        Player player = playerManager.getClosestPlayer(p, maxDistance);
        return player;
    }

    public int getClosestPlayerX(Point p, int maxDistance) {
        Player player = playerManager.getClosestPlayer(p, maxDistance);
        if (player != null) {
            return player.getMapX();
        }

        return -1;
    }

    public int getClosestPlayerY(Point p, int maxDistance) {
        Player player = playerManager.getClosestPlayer(p, maxDistance);
        if (player != null) {
            return player.getMapY();
        }

        return 0;
    }

    public String getContainerInventorySlotImage(ItemContainer ic, int slot) {
        if (ic != null) {
            return ic.getInventory().getImageFromSlot(slot);
        }
        return "";
    }

    public String getContainerInventorySlotDescription(ItemContainer ic, int slot) {
        if (ic != null) {
            return ic.getInventory().getDescriptionFromSlot(slot);
        }
        return "";
    }

    public int getXPNeededForLevel(int l) {
        return playerManager.getXPNeededForLevel(l);
    }

    public int getContainerInventorySlotQty(ItemContainer ic, int slot) {
        if (ic != null) {
            return ic.getInventory().getQtyFromSlot(slot);
        }
        return 0;
    }

    public String getPlayerContainerInventorySlotImage(PlayerContainer pc, int slot) {
        if (pc != null) {
            return pc.getInventory().getImageFromSlot(slot);
        }
        return "";
    }

    public String getPlayerContainerInventorySlotDescription(PlayerContainer pc, int slot) {
        if (pc != null) {
            return pc.getInventory().getDescriptionFromSlot(slot);
        }
        return "";
    }

    public int getPlayerContainerInventorySlotQty(PlayerContainer pc, int slot) {
        if (pc != null) {
            return pc.getInventory().getQtyFromSlot(slot);
        }
        return 0;
    }

    public String getFarmInventorySlotImage(Farm farm, int slot) {
        if (farm != null) {
            return farm.getInventory().getImageFromSlot(slot);
        }
        return "";
    }

    public String getFarmInventorySlotDescription(Farm farm, int slot) {
        if (farm != null) {
            return farm.getInventory().getDescriptionFromSlot(slot);
        }
        return "";
    }

    public int getFarmInventorySlotQty(Farm farm, int slot) {
        if (farm != null) {
            return farm.getInventory().getQtyFromSlot(slot);
        }
        return 0;
    }

    public Inventory getRobotInventory() {
        return gameController.getRobotInventory();
    }

    public int getRobotInventorySize() {
        return gameController.getRobotInventorySize();
    }

    public int getInvSlotFrom() {
        return invSlotFrom;
    }

    public ItemContainer getInvItemContainerFrom() {
        return invItemContainerFrom;
    }

    public PlayerContainer getInvPlayerContainerFrom() {
        return invPlayerContainerFrom;
    }

    public Farm getInvFarmFrom() {
        return invFarmFrom;
    }

    public String getInvHUDFrom() {
        return invHUDFrom;
    }

    public int getSplitCount() {
        return splitCount;
    }

    public String getItemNameBySlot(int slot) {
        String itemName = "";
        itemName = inventory.getNameFromSlot(slot);
        return itemName;
    }

    public ItemType getItemType(String it) {
        return itemManager.getItemType(it);
    }

    public Point getMousePosition() {
        Point mousePosition = new Point(gameController.getCurrentMousePosition());
        return mousePosition;
    }

    public int getMaxContainerDistance() {
        return gameController.getMaxContainerDistance();
    }

    public Point getMouseMapPosition() {
        Point mousePosition = new Point(gameController.getCurrentMousePosition().x + gameController.getMapOffsetX(), gameController.getPHeight() - gameController.getCurrentMousePosition().y + gameController.getMapOffsetY());
        return mousePosition;
    }

    public String getPlaverHeadSlotImage(Player p) {
        if (p.getArmorTypeHead() != null) {
            return "Items/" + p.getArmorTypeHead().getName();
        }

        return null;
    }

    public String getPlaverChestSlotImage(Player p) {
        if (p.getArmorTypeChest() != null) {
            return "Items/" + p.getArmorTypeChest().getName();
        }

        return null;
    }

    public String getPlaverLegsSlotImage(Player p) {
        if (p.getArmorTypeLegs() != null) {
            return "Items/" + p.getArmorTypeLegs().getName();
        }

        return null;
    }

    public String getPlaverFeetSlotImage(Player p) {
        if (p.getArmorTypeFeet() != null) {
            return "Items/" + p.getArmorTypeFeet().getName();
        }

        return null;
    }

    public String getPlaverHeadSlotName(Player p) {
        if (p.getArmorTypeHead() != null) {
            return p.getArmorTypeHead().getName();
        }

        return null;
    }

    public String getPlaverChestSlotName(Player p) {
        if (p.getArmorTypeChest() != null) {
            return p.getArmorTypeChest().getName();
        }

        return null;
    }

    public String getPlaverLegsSlotName(Player p) {
        if (p.getArmorTypeLegs() != null) {
            return p.getArmorTypeLegs().getName();
        }

        return null;
    }

    public String getPlaverFeetSlotName(Player p) {
        if (p.getArmorTypeFeet() != null) {
            return p.getArmorTypeFeet().getName();
        }

        return null;
    }

    public int getPlaverHeadSlotLevel(Player p) {
        if (p.getArmorTypeHead() != null) {
            return p.getArmorTypeHeadLevel();
        }

        return 1;
    }

    public int getPlaverChestSlotLevel(Player p) {
        if (p.getArmorTypeChest() != null) {
            return p.getArmorTypeChestLevel();
        }

        return 1;
    }

    public int getPlaverLegsSlotLevel(Player p) {
        if (p.getArmorTypeLegs() != null) {
            return p.getArmorTypeLegsLevel();
        }

        return 1;
    }

    public int getPlaverFeetSlotLevel(Player p) {
        if (p.getArmorTypeFeet() != null) {
            return p.getArmorTypeFeetLevel();
        }

        return 1;
    }

    public int getPlaverArmorPoints(Player p) {
        return p.getArmorPoints();
    }

    public int getPlaverTotalHitPoints(Player p) {
        return p.getTotalHitPoints();
    }

    public int getPlaverHitPoints(Player p) {
        return p.getHitPoints();
    }

    public int getPlayerInventorySize(Player p) {
        return p.getInventorySize();
    }

    public String getPlaverInventorySlotImage(int slot) {
        return inventory.getImageFromSlot(slot);
    }

    public String getPlaverInventorySlotType(int slot) {
        return inventory.getTypeFromSlot(slot);
    }

    public String getPlaverInventorySlotName(int slot) {
        return inventory.getNameFromSlot(slot);
    }

    public int getPlaverInventorySlotLevel(int slot) {
        return inventory.getLevelFromSlot(slot);
    }

    public String getPlaverInventorySlotDescription(int slot) {
        boolean showLevel = false;
        String type = inventory.getTypeFromSlot(slot);
        if (type != null) {
            if (type.equals("Weapon") || type.equals("Armor")) {
                showLevel = true;
            }
        }

        if (showLevel) {
            return "(Level " + inventory.getLevelFromSlot(slot) + ")  " + inventory.getDescriptionFromSlot(slot);
        } else {
            return inventory.getDescriptionFromSlot(slot);
        }
    }

    public int getPlaverInventorySlotQty(int slot) {
        return inventory.getQtyFromSlot(slot);
    }

    public int getScreenWidth() {
        return gameController.getPWidth();
    }

    public int getPlayerSelectedItem(Player p) {
        return p.getSelectedItem();
    }

    public int getPlayerSelectedItemSlotIndex(Player p) {
        if (p != null) {
            return p.getSelectedItem() + getPlayerInventorySize(p) - 10;
        }
        return 0;
    }

    public int getPlayerPlacingSlot(Player p) {
        if (p != null) {
            return p.getPlacingSlot();
        }
        return 0;
    }

    public String getPlayerSelectedItemType(Player p) {
        if (p != null) {
            Inventory i = p.getInventory();
            return i.getTypeFromSlot(getPlayerSelectedItemSlotIndex(p));
        }

        return "";
    }

    public String getPlayerSelectedItemName(Player p) {
        if (p != null) {
            Inventory i = p.getInventory();
            return i.getNameFromSlot(getPlayerSelectedItemSlotIndex(p));
        }

        return "";
    }

    public int getPlayerSelectedItemLevel(Player p) {
        if (p != null) {
            Inventory i = p.getInventory();
            return i.getLevelFromSlot(getPlayerSelectedItemSlotIndex(p));
        }

        return 1;
    }

    public String getPlayerSelectedItemWeaponType(Player p) {
        String itemName = getPlayerSelectedItemName(p);
        WeaponType newWeaponType = Weapon.getWeaponType(itemName);
        return newWeaponType.getType();
    }

    public String getStatusText() {
        return statusText;
    }

    public boolean getIsQuickBarLocked() {
        return isQuickBarLocked;
    }

    public boolean getRobotActivated(Player p) {
        return p.isRobotActivated();
    }

    public int getRobotBatteryPercentage(Player p) {
        return p.getRobotBatteryPercentage();
    }

    public String getRobotMode(Player p) {
        if (p != null) {
            return p.getRobotMode();
        }

        return "";
    }

    public boolean getRobotFollowing(Player p) {
        if (p != null) {
            return p.getRobotFollowing();
        }

        return false;
    }

    public GameController getGameController() {
        return gameController;
    }

    public void ghettoOutline(Graphics g, Color c, String text, int x, int y) {
        g.setColor(c);
        g.drawString(text, x + 1, y);
        g.drawString(text, x + 1, y + 1);
        g.drawString(text, x - 1, y);
        g.drawString(text, x - 1, y - 1);
    }
    
    public boolean getShowPortrait() {
        return showPortrait;
    }
    
    public int getPortraitHP() {
        return portraitHP;
    }
    
    public int getPortraitHPCurrent() {
        return portraitHPCurrent;
    }
    
    public int getPortraitAttack() {
        return portraitAttack;
    }
    
    public String getPortraitImage() {
        return portraitImage;
    }
    
    public String getWeaponType() {
        return weaponType;
    }
    
    public int getWeaponLevel() {
        return weaponLevel;
    }
    
    public String getArmorType() {
        return armorType;
    }
    
    public int getArmorLevel() {
        return armorLevel;
    }

    public void setGameController(GameController gc) {
        gameController = gc;
    }

    public void setImageLoader(ImageLoader il) {
        imageLoader = il;
    }

    public void setItemManager(ItemManager im) {
        itemManager = im;
    }

    public void setMonsterManager(MonsterManager mm) {
        monsterManager = mm;
    }

    public void setPlaceableManager(PlaceableManager pm) {
        placeableManager = pm;
    }

    public void setPlayerManager(PlayerManager pm) {
        playerManager = pm;
    }

    public void setBlockManager(BlockManager bm) {
        blockManager = bm;
    }

    public void setResourceManager(ResourceManager rm) {
        resourceManager = rm;
    }

    public void setProjectileManager(ProjectileManager pm) {
        projectileManager = pm;
    }

    public void setHUDManager(HUDManager hm) {
        hudManager = hm;
    }

    public void setIndicatorManager(IndicatorManager im) {
        indicatorManager = im;
    }

    public void setPixelizeManager(PixelizeManager pm) {
        pixelizeManager = pm;
    }

    public void setNetworkThread(NetworkThread nt) {
        networkThread = nt;
    }

    public void setInventory(Inventory inv) {
        inventory = inv;
    }

    public void setAttackBonus(int b) {
        attackBonus = b;
    }

    public void setHPBonus(int b) {
        hpBonus = b;
    }

    public void setBossFight(boolean b) {
        bossFight = b;
    }

    public void setInvSlotFrom(String hud, int i) {
        invHUDFrom = hud;
        invItemContainerFrom = null;
        invSlotFrom = i;
    }

    public void setInvSlotFrom(String hud, ItemContainer ic, int i) {
        invHUDFrom = hud;
        invItemContainerFrom = ic;
        invSlotFrom = i;
    }

    public void setInvSlotFrom(String hud, PlayerContainer pc, int i) {
        invHUDFrom = hud;
        invPlayerContainerFrom = pc;
        invSlotFrom = i;
    }

    public void setInvSlotFrom(String hud, Farm farm, int i) {
        invHUDFrom = hud;
        invFarmFrom = farm;
        invSlotFrom = i;
    }

    public void setIsQuickBarLocked(Boolean locked) {
        isQuickBarLocked = locked;
    }

    public void setSplitCount(int c) {
        splitCount = c;
    }
    
    public void setShowPortrait(boolean p) {
        showPortrait = p;
    }
    
    public void setPortraitHP(int p) {
        portraitHP = p;
    }
    
    public void setPortraitHPCurrent(int p) {
        portraitHPCurrent = p;
    }
    
    public void setPortraitAttack(int p) {
        portraitAttack = p;
    }
    
    public void setPortraitImage(String p) {
        portraitImage = p;
    }
    
    public void setWeaponType(String p) {
        weaponType = p;
    }
    
    public void setWeaponLevel(int l) {
        weaponLevel = l;
    }
    
    public void setArmorType(String p) {
        armorType = p;
    }
    
    public void setArmorLevel(int l) {
        armorLevel = l;
    }

    public void setStatusText(String s) {
        statusText = s;
    }

    public void showMessage(String type, String message) {
        gameController.showMessage(type, message);
    }
}