package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;
import com.weem.epicinventor.hud.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.network.*;

import java.awt.*;
import java.util.*;

public abstract class Farm extends Building {

    private static final long serialVersionUID = 10000L;
    protected Inventory inventory;
    transient protected HUD hud;
    protected int inventorySize;
    protected final static int MAX_IDLE_TIME = 30;
    protected long idleTime;
    protected boolean isTransforming;
    protected long transformTime = 0;
    protected long totalTransformTime = 0;

    public Farm(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs, int is) {
        super(pm, rg, sm, am, x, y, cs);

        type = "Farm";

        inventorySize = is;

        inventory = new Inventory(rg, inventorySize);

        hud = registry.getHUDManager().loadFarmHUD(this, inventorySize);
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

        hud = registry.getHUDManager().loadFarmHUD(this, inventorySize);
    }

    protected void initHUD() {
        hud = registry.getHUDManager().loadFarmHUD(this, inventorySize);
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
        if (this.isActivated() && !isTransforming) {
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
        }

        return false;
    }

    public void startTransformation() {
        boolean canTransform = false;
        
        //only transform if we've got a seed
        for (int i = 0; i <= 1; i++) {
            if (inventory.getNameFromSlot(i).equals("WheatSeed") || inventory.getNameFromSlot(i).equals("PumpkinSeed")) {
                canTransform = true;
            }
        }
        
        if (!isTransforming && canTransform) {
            int usedSlots = inventory.getUsedSlots();
            switch (usedSlots) {
                case 1:
                    isTransforming = true;
                    totalTransformTime = (long) (1 * 60);
                    break;
                case 2:
                    isTransforming = true;
                    totalTransformTime = (long) (1.5f * 60f);
                    break;
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
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
        super.update();

        if (isTransforming) {
            transformTime += registry.getImageLoader().getPeriod();
            if ((transformTime / 1000) >= totalTransformTime) {
                transformTime = 0;
                isTransforming = false;

                for (int i = 0; i <= 1; i++) {
                    if (inventory.getNameFromSlot(i).equals("WheatSeed")) {
                        inventory.deleteInventory(i, 1);
                        inventory.addToInventory(i, "Bread", 1);
                    } else if (inventory.getNameFromSlot(i).equals("PumpkinSeed")) {
                        inventory.deleteInventory(i, 1);
                        inventory.addToInventory(i, "Pumpkin", 1);
                    }
                }
                registry.showMessage("Success", "Your farm has finished producing!");
            }
        }
    }

    @Override
    public void render(Graphics g) {
        super.render(g);

        if (isTransforming) {
            float timeSpent;
            float timeLeft;
            float percentage;
            int hours = 0;
            int minutes = 0;
            int seconds = 0;

            int x = mapX + (width / 2);
            int y = mapY + height;

            timeSpent = (float) (transformTime / 1000f);
            timeLeft = (float) totalTransformTime - timeSpent;
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

            percentage = ((float) timeSpent / (float) totalTransformTime) * 100;

            placeableManager.displayProgress(g,
                    x,
                    y,
                    (int) percentage,
                    hours + ":"
                    + String.format("%02d", minutes) + ":"
                    + String.format("%02d", seconds));
        }
    }
}