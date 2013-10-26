package com.weem.epicinventor;

import com.weem.epicinventor.actor.monster.*;
import com.weem.epicinventor.particle.*;
import com.weem.epicinventor.projectile.*;
import com.weem.epicinventor.resource.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;

public abstract class Manager {

    transient protected GameController gameController;
    transient protected Registry registry;
    private final static float LONG_UPDATE_INTERVAL = 0.50f;
    private long longUpdateCheckTime;

    public Manager() {
        gameController = null;
        registry = null;
    }

    public Manager(GameController gc, Registry rg) {
        gameController = gc;
        registry = rg;
    }

    public void setTransient(Registry rg) {
        registry = rg;
        gameController = rg.getGameController();
    }

    public int findFloor(int xWorld) {
        return gameController.findFloor(xWorld);
    }

    public int findNextFloor(int xWorld, int yWorld, int height) {
        return gameController.findNextFloor(xWorld, yWorld, height);
    }
    
    public boolean isKeyDown(int k) {
        return gameController.isKeyDown(k);
    }
    
    public void gameExit() {
        gameController.gameExit();
    }
    
    public void resetGame(boolean showHelp) {
        gameController.resetGame(showHelp);
    }
    
    public void resetGame() {
        gameController.resetGame();
    }
    
    public boolean getIsPaused() {
        return gameController.getIsPaused();
    }
    
    public boolean getIsMasterPaused() {
        return gameController.getIsMasterPaused();
    }

    public void resumeMasterGame() {
        gameController.resumeMasterGame();
    }

    public void pauseMasterGame() {
        gameController.pauseMasterGame();
    }
    
    public void togglePaused() {
        gameController.togglePaused();
    }
    
    public int getPWidth() {
        return gameController.getPWidth();
    }

    public int getPHeight() {
        return gameController.getPHeight();
    }

    public int getMapWidth() {
        return gameController.getMapWidth();
    }

    public int getMapHeight() {
        return gameController.getMapHeight();
    }

    public int getMapOffsetX() {
        return gameController.getMapOffsetX();
    }

    public int getMapOffsetY() {
        return gameController.getMapOffsetY();
    }

    public int getBlockHeight() {
        return gameController.getBlockHeight();
    }

    public int getBlockWidth() {
        return gameController.getBlockWidth();
    }
    
    public Point getNearestTownHallXY(Point p) {
        return gameController.getNearestTownHallXY(p);
    }
    
    public int getActivatedCount(String type) {
        return gameController.getActivatedCount(type);
    }

    public boolean currentlyPlacing() {
        return gameController.currentlyPlacing();
    }
    
    public boolean checkMobProjectileHit(Projectile p) {
        return gameController.checkMobProjectileHit(p);
    }
    
    public boolean checkMobParticleHit(Particle p) {
        return gameController.checkMobParticleHit(p);
    }
    
    public boolean checkPlayerProjectileHit(Projectile p) {
        return gameController.checkPlayerProjectileHit(p);
    }

    public boolean checkPlaceableProjectileHit(Projectile p) {
        return gameController.checkPlaceableProjectileHit(p);
    }
    
    public ResourceType getResourceTypeByResourceId(String id) {
        return gameController.getResourceTypeByResourceId(id);
    }
    
    public void showLevelUpGraphic() {
        gameController.showLevelUpGraphic();
    }
    
    public Resource getResourceById(String id) {
        return gameController.getResourceById(id);
    }

    public int mapToPanelX(int x) {
        return x - gameController.getMapOffsetX();
    }

    public int mapToPanelY(int y) {
        return y - gameController.getMapOffsetY();
    }

    public int panelToMapX(int x) {
        return x + gameController.getMapOffsetX();
    }

    public int panelToMapY(int y) {
        y = gameController.getMapOffsetY() + gameController.getPHeight() - y;
        
        return y;
    }
    
    public String getCurrentVersion() {
        return gameController.getCurrentVersion();
    }
    
    public boolean getIsOnline() {
        return gameController.getIsOnline();
    }
    
    public int[] getCurrentPower() {
        return gameController.getCurrentPower();
    }
    
    public void shakeCamera(long time, int amount) {
        gameController.shakeCamera(time, amount);
    }
    
    public void stunPlayersOnGround(long duration) {
        gameController.stunPlayersOnGround(duration);
    }

    public boolean playerStandingOnTownBlocks() {
        return gameController.playerStandingOnTownBlocks();
    }

    public String playerGetInventoryItemCategory(int slot) {
        return gameController.playerGetInventoryItemCategory(slot);
    }

    public String playerGetInventoryItemName(int slot) {
        return gameController.playerGetInventoryItemName(slot);
    }

    public int playerGetInventoryQty(int slot) {
        return gameController.playerGetInventoryQty(slot);
    }

    public int playerGetInventoryLevel(int slot) {
        return gameController.playerGetInventoryLevel(slot);
    }

    public int playerAddItem(String name, int qty) {
        return gameController.playerAddItem(0, name, qty);
    }

    public int playerAddItem(String name, int qty, int level) {
        return gameController.playerAddItem(0, name, qty, level);
    }

    public int checkMapX(int x, int objWidth) {
        return gameController.checkMapX(x, objWidth);
    }

    public int checkMapY(int y, int objHeight) {
        return gameController.checkMapY(y, objHeight);
    }

    public void centerCameraOnPoint(Point p) {
        gameController.centerCameraOnPoint(p);
    }

    public int checkForBlock(Point p) {
        return gameController.checkForBlock(p);
    }
    
    public boolean doesRectContainBlocks(int mapX, int mapY, int width, int height) {
        return gameController.doesRectContainBlocks(mapX, mapY, width, height);
    }

    public void monsterAttackPlaceable(Monster source, Rectangle attackRect, int meleeDamage) {
        gameController.monsterAttackPlaceable(source, attackRect, meleeDamage);
    }

    public void displayProgress(Graphics g, int x, int y, int progress, String displayText) {
        ProgressBar.displayProgress(g, gameController, registry.getImageLoader(), x, y, progress, displayText);
    }

    public void displayHP(Graphics g, int x, int y, int progress) {
        HPBar.displayHP(g, gameController, registry.getImageLoader(), x, y, progress);
    }
    
    public int getXPNeededForLevel(int l) {
        int[] xpTable = new int[22];
        xpTable[0] = 0;
        xpTable[1] = 0;
        xpTable[2] = 80;
        xpTable[3] = 245;
        xpTable[4] = 720;
        xpTable[5] = 1805;
        xpTable[6] = 3920;
        xpTable[7] = 7605;
        xpTable[8] = 13520;
        xpTable[9] = 22445;
        xpTable[10] = 35280;
        xpTable[11] = 53045;
        xpTable[12] = 76880;
        xpTable[13] = 108045;
        xpTable[14] = 147920;
        xpTable[15] = 198005;
        xpTable[16] = 259920;
        xpTable[17] = 335405;
        xpTable[18] = 426320;
        xpTable[19] = 534645;
        xpTable[20] = 662480;
        xpTable[21] = 812045;

        if (l > 1 && l <= xpTable.length) {
            return xpTable[l];
        } else {
            return 0;
        }
    }
    
    public void saveAndQuit() {
        gameController.saveAndQuit();
    }
    
    public void updateMusicVolume() {
        gameController.updateMusicVolume();
    }
    
    public void updateMusicVolume(float v) {
        gameController.updateMusicVolume(v);
    }
    
    public boolean isInPlayerView(Point p) {
        return gameController.isInPlayerView(p);
    }
    
    public boolean isInFrontOfPlaceable(Rectangle r) {
        return gameController.isInFrontOfPlaceable(r);
    }

    public void update() {
        longUpdateCheckTime += registry.getImageLoader().getPeriod();
        if (((float)longUpdateCheckTime / 1000.0f) >= LONG_UPDATE_INTERVAL) {
            longUpdateCheckTime = 0;
            updateLong();
        }
    }

    public void updateLong() {
    }
}