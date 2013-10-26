package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.hud.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.network.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;
import java.util.*;
import java.io.*;

public abstract class PlayerContainer extends Building implements Serializable {

    private static final long serialVersionUID = 10000L;
    protected Inventory inventory;
    transient protected HUD hud;
    protected int inventorySize;
    protected final static int MAX_IDLE_TIME = 30;
    protected long idleTime;

    public PlayerContainer(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs, int is) {
        super(pm, rg, sm, am, x, y, cs);

        type = "PlayerContainer";

        inventorySize = is;

        inventory = new Inventory(rg, inventorySize);

        initHUD();
    }

    @Override
    public void setTransient(Registry rg) {
        registry = rg;
        placeableManager = rg.getPlaceableManager();

        buildingImage = registry.getImageLoader().getImage(standardImage);
        buildingImage = registry.getImageLoader().changeToGrayscale(buildingImage);

        canPlaceImage = registry.getImageLoader().getImage(standardImage);
        canPlaceImage = registry.getImageLoader().changeTransperancy(canPlaceImage, 0.7f);
        canPlaceImage = registry.getImageLoader().changeColor(canPlaceImage, (short) 0, (short) 100, (short) 0);

        cantPlaceImage = registry.getImageLoader().getImage(standardImage);
        cantPlaceImage = registry.getImageLoader().changeTransperancy(cantPlaceImage, 0.7f);
        cantPlaceImage = registry.getImageLoader().changeColor(cantPlaceImage, (short) 100, (short) 0, (short) 0);

        inventory.setTransient(rg);

        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        hud = registry.getHUDManager().loadPlayerContainerHUD(this);
    }

    protected void initHUD() {
        hud = registry.getHUDManager().loadPlayerContainerHUD(this);
    }

    @Override
    public boolean setDestroying(boolean destroying) {
        if (destroying && inventory.getUsedSlots() > 0) {
            registry.showMessage("Error", "You must emtpy out this container before destroying it");
            return false;
        }
        return super.setDestroying(destroying);
    }

    @Override
    public void setInventory(Inventory i) {
        inventory = i;
    }

    public int getInventorySize() {
        return inventorySize;
    }

    public void quickLoot() {
        for (int i = 0; i < inventorySize; i++) {
            int qty = inventory.getQtyFromSlot(i);
            if (qty > 0) {
                int oldQty = qty;
                String name = inventory.getNameFromSlot(i);
                int level = inventory.getLevelFromSlot(i);

                if(placeableManager.playerAddItem(name, qty, level) < oldQty) {
                    deleteInventory(i, oldQty - qty);
                }
            }
        }
        hud.setShouldRender(false);
    }

    @Override
    public boolean handleRightClick(Point clickPoint) {
        if (this.isActivated()) {
            Point mapPoint = new Point(
                    placeableManager.panelToMapX(clickPoint.x),
                    placeableManager.panelToMapY(clickPoint.y));

            if (currentState == State.Placed) {
                if (isInside(mapPoint)) {
                    if (registry.getPlayerManager().getCurrentPlayer().getCenterPoint().distance(getCenterPoint()) <= registry.getMaxContainerDistance()) {
                        this.toggled();
                        placeableManager.toggleContainerHUD(hud);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean addItem(int slot, String name, int qty) {
        int qtyLeft = inventory.addToInventory(slot, name, qty);
        if (qtyLeft == 0) {
            if (registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.NONE && registry.getNetworkThread() != null) {
                if (registry.getNetworkThread().readyForUpdates) {
                    UpdatePlaceable up = new UpdatePlaceable(this.getId());
                    up.hitPoints = this.getHitPoints();
                    up.totalHitPoints = this.getTotalHitPoints();
                    up.inventory = inventory;
                    up.action = "InventoryUpdate";
                    registry.getNetworkThread().sendData(up);
                }
            }
            return true;
        } else {
            if(qty - qtyLeft > 0) {
               deleteInventory(slot, qty - qtyLeft); 
            }
            return false;
        }
    }

    public boolean addItem(String name, int qty) {
        return addItem(0, name, qty);
    }

    public boolean addItem(int slot, String name, int qty, int level) {
        int qtyLeft = inventory.addToInventory(slot, name, qty, level);
        if (qtyLeft == 0) {
            if (registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.NONE && registry.getNetworkThread() != null) {
                if (registry.getNetworkThread().readyForUpdates) {
                    UpdatePlaceable up = new UpdatePlaceable(this.getId());
                    up.hitPoints = this.getHitPoints();
                    up.totalHitPoints = this.getTotalHitPoints();
                    up.inventory = inventory;
                    up.action = "InventoryUpdate";
                    registry.getNetworkThread().sendData(up);
                }
            }
            return true;
        } else {
            if(qty - qtyLeft > 0) {
               deleteInventory(slot, qty - qtyLeft); 
            }
            return false;
        }
    }

    public boolean addItem(String name, int qty, int level) {
        return addItem(0, name, qty, level);
    }

    public void deleteInventory(int slot, int qty) {
        inventory.deleteInventory(slot, qty);
        if (registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlaceable up = new UpdatePlaceable(this.getId());
                up.hitPoints = this.getHitPoints();
                up.totalHitPoints = this.getTotalHitPoints();
                up.inventory = inventory;
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void swapInventory(int from, int to) {
        inventory.swapInventoryLocations(from, to);
        if (registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlaceable up = new UpdatePlaceable(this.getId());
                up.hitPoints = this.getHitPoints();
                up.totalHitPoints = this.getTotalHitPoints();
                up.inventory = inventory;
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }
    }
}