package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;
import com.weem.epicinventor.drop.*;
import com.weem.epicinventor.hud.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.network.*;

import java.awt.*;
import java.util.*;
import java.util.ArrayList;

public class ItemContainer extends Building {

    private static final long serialVersionUID = 10000L;
    protected Inventory inventory;
    transient protected HUD hud;
    protected int inventorySize = 15;
    protected final static int MAX_IDLE_TIME = 30;
    protected long idleTime;

    public ItemContainer(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs, ArrayList<Drop> drops) {
        super(pm, rg, sm, am, x, y, cs);

        type = "ItemContainer";

        inventory = new Inventory(rg, inventorySize);

        initHUD();

        Drop drop = null;

        for (int i = 0; i < drops.size(); i++) {
            drop = drops.get(i);
            inventory.addToInventory(0, drop.getItemName(), drop.getQty(), drop.getLevel());
        }
    }

    public ItemContainer(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs, int i) {
        super(pm, rg, sm, am, x, y, cs);

        inventorySize = i;

        inventory = new Inventory(rg, inventorySize);

        initHUD();
    }

    protected void initHUD() {
        hud = registry.getHUDManager().loadContainerHUD(this);
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

        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        hud = registry.getHUDManager().loadContainerHUD(this);
    }
    
    @Override
    public void setInventory(Inventory i) {
        inventory = i;
    }

    @Override
    public boolean handleRightClick(Point clickPoint) {
        Point mapPoint = new Point(
                placeableManager.panelToMapX(clickPoint.x),
                placeableManager.panelToMapY(clickPoint.y));

        if (currentState == State.Placed) {
            if (isInside(mapPoint)) {
                if (registry.getPlayerManager().getCurrentPlayer().getCenterPoint().distance(getCenterPoint()) <= registry.getMaxContainerDistance()) {
                    placeableManager.toggleContainerHUD(hud);
                    return true;
                }
            }
        }

        return false;
    }

    public Inventory getInventory() {
        return inventory;
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

    public boolean addItem(int slot, String name, int qty) {
        if (inventory.addToInventory(slot, name, qty) == 0) {
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
            return false;
        }
    }

    public boolean addItem(String name, int qty) {
        if (inventory.addToInventory(0, name, qty) == 0) {
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
            return false;
        }
    }

    public boolean addItem(int slot, String name, int qty, int level) {
        if (inventory.addToInventory(slot, name, qty, level) == 0) {
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
            return false;
        }
    }

    public boolean addItem(String name, int qty, int level) {
        if (inventory.addToInventory(0, name, qty, level) == 0) {
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
            return false;
        }
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

    @Override
    public void update() {
        if (inventory.isEmpty()) {
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

        if (!hud.getShouldRender()) {
            idleTime += registry.getImageLoader().getPeriod();
            if ((idleTime / 1000) >= MAX_IDLE_TIME) {
                isDirty = true;
            }
        } else {
            idleTime = 0;
        }

        super.update();
    }
}
