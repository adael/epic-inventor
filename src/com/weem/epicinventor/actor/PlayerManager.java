package com.weem.epicinventor.actor;

import com.weem.epicinventor.*;
import com.weem.epicinventor.ai.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.actor.monster.*;
import com.weem.epicinventor.network.*;
import com.weem.epicinventor.placeable.*;
import com.weem.epicinventor.projectile.*;
import com.weem.epicinventor.resource.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.*;
import java.util.ArrayList;

public class PlayerManager extends Manager {

    private HashMap<String, Player> players;
    boolean isUpdating = false;
    private Player currentPlayer;
    private boolean currentPlayerSet = false;

    public PlayerManager(GameController gc, Registry rg) {
        super(gc, rg);

        players = new HashMap<String, Player>();

        Player p;

        p = new Player(this, registry, "Player/Standing", 226);
        players.put(p.getId(), p);
        currentPlayer = p;
    }

    public void clearPlayers() {
        players = null;
        currentPlayer = null;
        players = new HashMap<String, Player>();
    }

    public void giveXP(Monster m) {
        Player player = null;

        try {
            for (String key : players.keySet()) {
                player = (Player) players.get(key);
                player.addXP(m.getXPByPlayer(player));
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify players while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
    }

    public void registerPlayer(Player p) {
        if (!players.containsKey(p.getId())) {
            players.put(p.getId(), p);
            if (players.size() == 1) {
                setCurrentPlayer(p);
            }
        }
    }

    public void setCurrentPlayer(Player p) {
        currentPlayer = p;
        currentPlayerSet = true;
    }

    public boolean getCurrentPlayerSet() {
        return currentPlayerSet;
    }

    public void removePlayer(String playerId) {
        if (players.containsKey(playerId)) {
            players.remove(playerId);
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public HashMap<String, Player> getPlayers() {
        return players;
    }

    public void stopActions() {
        gameController.stopActions(currentPlayer);
    }

    public void stopActions(Player p) {
        gameController.stopActions(p);
    }

    public Inventory getRobotInventory() {
        return currentPlayer.getRobotInventory();
    }

    public int getRobotInventorySize() {
        return currentPlayer.getRobotInventorySize();
    }

    public Player getPlayerById(String id) {
        if (players.containsKey(id)) {
            Player player = players.get(id);
            return player;
        } else {
            return null;
        }
    }

    public Player getClosestPlayer(Point p, int maxDistance) {
        Player player = null;
        Player closestPlayer = null;

        double closestDistance = 0;

        try {
            for (String key : players.keySet()) {
                player = (Player) players.get(key);
                if (p.distance(player.getCenterPoint()) < closestDistance || closestDistance == 0) {
                    closestPlayer = player;
                    closestDistance = p.distance(player.getCenterPoint());
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify players while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        if (closestPlayer != null && closestDistance <= maxDistance) {
            return closestPlayer;
        }

        return null;
    }

    public boolean isInPlayerView(Point p) {
        Player player = null;

        if (p != null) {
            try {
                for (String key : players.keySet()) {
                    player = (Player) players.get(key);
                    if (p.x >= gameController.getMapOffsetX()
                            && p.x <= gameController.getMapOffsetX() + gameController.getPWidth()
                            && p.y >= gameController.getMapOffsetY()
                            && p.y <= gameController.getMapOffsetY() + gameController.getPHeight()) {
                        return true;
                    }
                }
            } catch (ConcurrentModificationException concEx) {
                //another thread was trying to modify players while iterating
                //we'll continue and the new item can be grabbed on the next update
            }
        }

        return false;
    }

    public void playerMoveLeft() {
        currentPlayer.moveLeft();
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.action = "MoveLeft";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void playerMoveRight() {
        currentPlayer.moveRight();
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.action = "MoveRight";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void playerStopMove() {
        currentPlayer.stopMove();
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.action = "StopMove";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void playerShowRect(Boolean r) {
        currentPlayer.setShowRect(r);
    }

    public void playerStartGather(String rt) {
        currentPlayer.startGather(rt);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.action = "StartGather";
                up.dataString = rt;
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public boolean playerGoInsideRobot() {
        boolean ret = currentPlayer.setInsideRobot(true);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.action = "GoInsideRobot";
                registry.getNetworkThread().sendData(up);
            }
        }

        return ret;
    }

    public boolean isPlayerInsideRobot() {
        return currentPlayer.getInsideRobot();
    }

    public void playerGetOutOfRobot() {
        currentPlayer.setInsideRobot(false);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.action = "GetOutOfRobot";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public boolean playerToggleInsideRobot() {
        boolean ret;
        if (currentPlayer.getInsideRobot()) {
            ret = currentPlayer.setInsideRobot(false);
            if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
                if (registry.getNetworkThread().readyForUpdates) {
                    UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                    up.name = currentPlayer.getName();
                    up.mapX = currentPlayer.getMapX();
                    up.mapY = currentPlayer.getMapY();
                    up.vertMoveMode = currentPlayer.getVertMoveMode();
                    up.action = "GetOutOfRobot";
                    registry.getNetworkThread().sendData(up);
                }
            }
        } else {
            ret = currentPlayer.setInsideRobot(true);
            if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
                if (registry.getNetworkThread().readyForUpdates) {
                    UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                    up.name = currentPlayer.getName();
                    up.mapX = currentPlayer.getMapX();
                    up.mapY = currentPlayer.getMapY();
                    up.vertMoveMode = currentPlayer.getVertMoveMode();
                    up.action = "GoInsideRobot";
                    registry.getNetworkThread().sendData(up);
                }
            }
        }

        return ret;
    }

    public void playerEquipHead(String armorName, int level) {
        currentPlayer.setArmorTypeHead(armorName, level);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.inventory = currentPlayer.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void playerEquipChest(String armorName, int level) {
        currentPlayer.setArmorTypeChest(armorName, level);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.inventory = currentPlayer.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void playerEquipLegs(String armorName, int level) {
        currentPlayer.setArmorTypeLegs(armorName, level);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.inventory = currentPlayer.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void playerEquipFeet(String armorName, int level) {
        currentPlayer.setArmorTypeFeet(armorName, level);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.inventory = currentPlayer.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void handleClick(Point clickPoint) {
        currentPlayer.handleClick(clickPoint);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.action = "HandleClick";
                up.dataPoint = clickPoint;
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void handleRightClick() {
        currentPlayer.handleRightClick();
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.action = "HandleRightClick";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void handleReleased(Point clickPoint) {
        currentPlayer.handleReleased(clickPoint);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.action = "HandleReleased";
                up.dataPoint = clickPoint;
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public ArrayList<String> attackDamageAndKnockBack(Actor source, Arc2D.Double arc, Point mapPoint, int damage, int knockBackX, int knockBackY, int maxHits, String weaponType) {
        return gameController.attackDamageAndKnockBack(source, arc, mapPoint, damage, knockBackX, knockBackY, maxHits, 0, weaponType);
    }

    public void playerRender(Graphics g, int x, int y, boolean imageOverride) {
        currentPlayer.renderPlayer(g, x, y, imageOverride);
    }

    @Override
    public int playerAddItem(String name, int qty) {
        int ret = playerAddItem(0, name, qty);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.inventory = currentPlayer.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }

        return ret;
    }

    public int playerAddItem(Player p, String name, int qty) {
        int ret = playerAddItem(p, 0, name, qty);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(p.getId());
                up.name = p.getName();
                up.mapX = p.getMapX();
                up.mapY = p.getMapY();
                up.vertMoveMode = p.getVertMoveMode();
                up.inventory = p.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }

        return ret;
    }

    public int playerAddItem(int slot, String name, int qty) {
        int ret = currentPlayer.playerAddItem(slot, name, qty);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.inventory = currentPlayer.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }

        return ret;
    }

    public int playerAddItem(Player p, int slot, String name, int qty) {
        int ret = p.playerAddItem(slot, name, qty);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(p.getId());
                up.name = p.getName();
                up.mapX = p.getMapX();
                up.mapY = p.getMapY();
                up.vertMoveMode = p.getVertMoveMode();
                up.inventory = p.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }

        return ret;
    }

    public int playerAddItem(int slot, String name, int qty, int level) {
        int ret = currentPlayer.playerAddItem(slot, name, qty, level);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.inventory = currentPlayer.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }

        return ret;
    }

    public int playerAddItem(Player p, int slot, String name, int qty, int level) {
        int ret = p.playerAddItem(slot, name, qty, level);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(p.getId());
                up.name = p.getName();
                up.mapX = p.getMapX();
                up.mapY = p.getMapY();
                up.vertMoveMode = p.getVertMoveMode();
                up.inventory = p.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }

        return ret;
    }

    @Override
    public boolean checkPlayerProjectileHit(Projectile p) {
        if (gameController.multiplayerMode != gameController.multiplayerMode.CLIENT) {
            try {
                for (String key : players.keySet()) {
                    Player player = (Player) players.get(key);
                    if (player != null) {
                        if (player.getRobot().getIsActivated() && player.getRobot().getPerimeter().contains(p.getCenterPoint())) {
                            player.getRobot().applyDamage(p.getDamage(), p.getSource());
                            return true;
                        }
                        if (player.getPerimeter().contains(p.getCenterPoint())) {
                            player.applyDamage(p.getDamage(), p.getSource());
                            if (p.getName().equals("Goo")) {
                                player.slow(2);
                            } else if (p.getName().equals("Web")) {
                                player.slow(3);
                            }
                            return true;
                        }
                    }
                }
            } catch (ConcurrentModificationException concEx) {
                //another thread was trying to modify players while iterating
                //we'll continue and the new item can be grabbed on the next update
            }
        }

        return false;
    }

    @Override
    public String playerGetInventoryItemCategory(int slot) {
        return currentPlayer.playerGetInventoryItemCategory(slot);
    }

    @Override
    public String playerGetInventoryItemName(int slot) {
        return currentPlayer.playerGetInventoryItemName(slot);
    }

    @Override
    public int playerGetInventoryQty(int slot) {
        return currentPlayer.playerGetInventoryQty(slot);
    }

    @Override
    public int playerGetInventoryLevel(int slot) {
        return currentPlayer.playerGetInventoryLevel(slot);
    }

    public Inventory getPlayerInventory() {
        return currentPlayer.getInventory();
    }

    public void playerDeleteInventory(int slot, int qty) {
        gameController.playerDeleteInventory(slot, qty, false);
    }

    public void playerDeleteInventory(int slot, int qty, boolean giveXP) {
        currentPlayer.playerDeleteInventory(slot, qty, giveXP);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.inventory = currentPlayer.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void setPlayerSlotQuantity(int slot, int qty) {
        currentPlayer.setPlayerSlotQuantity(slot, qty);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.inventory = currentPlayer.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void setPlayerSelectedItem(int i) {
        currentPlayer.setPlayerSelectedItem(i);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.action = "SetSelectedItem";
                up.dataInt = i;
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void setPlayerNames(String characterName, String robotName) {
        currentPlayer.setName(characterName);
        currentPlayer.setRobotName(robotName);
    }

    public void setUpdating(boolean u) {
        isUpdating = u;
    }

    public void initPlayers() {
        try {
            for (String key : players.keySet()) {
                Player p = (Player) players.get(key);
                if (p != null) {
                    p.init();
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify players while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
    }

    public void playerSwapInventory(int from, int to) {
        currentPlayer.playerSwapInventory(from, to);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.inventory = currentPlayer.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void playerEquipFromInventory(int slot) {
        currentPlayer.playerEquipFromInventory(slot);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.inventory = currentPlayer.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void playerDied() {
        gameController.playerDied();
    }

    public void playerUnEquipToInventory(String equipmentType, int to) {
        currentPlayer.playerUnEquipToInventory(equipmentType, to);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.inventory = currentPlayer.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void playerUnEquipToDelete(String equipmentType) {
        currentPlayer.playerUnEquipToDelete(equipmentType);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.inventory = currentPlayer.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void playerCraftItem(String itemType) {
        currentPlayer.playerCraftItem(itemType);
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.inventory = currentPlayer.getInventory();
                up.action = "InventoryUpdate";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void robotSetMode(String m) {
        currentPlayer.robotSetMode(m);
    }

    public void robotToggleActivated() {
        currentPlayer.robotToggleActivated();
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.action = "RobotToggleActivated";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void robotToggleFollow() {
        currentPlayer.robotToggleFollow();
    }

    public ArrayList<String> getItemTypeRequirements(String n) {
        return gameController.getItemTypeRequirements(n);
    }

    public void playerStopGather() {
        currentPlayer.stopGather();
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.action = "StopGather";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void playerStopJump() {
        currentPlayer.stopJump();
        currentPlayer.stopAscend();
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.action = "StopJump";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void playerJump() {
        currentPlayer.jump();
        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(currentPlayer.getId());
                up.name = currentPlayer.getName();
                up.mapX = currentPlayer.getMapX();
                up.mapY = currentPlayer.getMapY();
                up.vertMoveMode = currentPlayer.getVertMoveMode();
                up.action = "Jump";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public boolean isPlayerPerformingAction() {
        return currentPlayer.isPlayerPerformingAction();
    }

    public boolean isPlayerMoving() {
        return currentPlayer.isPlayerMoving();
    }

    public Placeable loadPlaceable(String n, int x, int y) {
        return gameController.loadPlaceable(n, x, y);
    }

    public void cancelPlaceable() {
        gameController.cancelPlaceable();
    }

    @Override
    public boolean playerStandingOnTownBlocks() {
        boolean ret = true;
        boolean allNull = true;
        short[] standingBlocks = gameController.blocksUnder(currentPlayer.getMapX() + currentPlayer.getBaseOffset(), currentPlayer.getMapX() + currentPlayer.getWidth() - currentPlayer.getBaseOffset(), currentPlayer.getMapY());
        for (int i = 0; i < standingBlocks.length; i++) {
            if (!gameController.isIdInGroup(standingBlocks[i], "None")) {
                allNull = false;
            }
            if (!(gameController.isIdInGroup(standingBlocks[i], "None") || gameController.isIdInGroup(standingBlocks[i], "Town"))) {
                ret = false;
            }
        }
        if (allNull == true) {
            ret = false;
        }
        return ret;
    }

    public int[] getTownStartEndUnderPlayer() {
        return gameController.getTownStartEnd(currentPlayer.getCenterPoint().x, currentPlayer.getMapY());
    }

    public Damage getMonsterTouchDamage(Rectangle r) {
        return gameController.getMonsterTouchDamage(r);
    }

    public Point getCenterPoint() {
        return currentPlayer.getCenterPoint();
    }

    @Override
    public void stunPlayersOnGround(long duration) {
        try {
            for (String key : players.keySet()) {
                Player p = (Player) players.get(key);
                if (p != null) {
                    if (p.getVertMoveMode() == Actor.VertMoveMode.NOT_JUMPING) {
                        p.setStatusStun(true, duration);
                        if (gameController.multiplayerMode == gameController.multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                            if (registry.getNetworkThread().readyForUpdates) {
                                UpdatePlayer up = new UpdatePlayer(p.getId());
                                up.name = p.getName();
                                up.mapX = p.getMapX();
                                up.mapY = p.getMapY();
                                up.vertMoveMode = p.getVertMoveMode();
                                up.dataBoolean = true;
                                up.dataLong = duration;
                                up.action = "SetStun";
                                registry.getNetworkThread().sendData(up);
                            }
                        }
                    }
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify players while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        setCamera();
    }

    public void setCamera() {
        if (currentPlayer != null) {
            if (currentPlayer.getCameraReturning()) {
                boolean xGood = false;

                if(currentPlayer.cameraMoveSize < 350) {
                    if(currentPlayer.cameraMoveSize == 0)
                    {
                        currentPlayer.cameraMoveSize = 0.25;
                    }
                    currentPlayer.cameraMoveSize *= 1.05;
                }

                if (Math.abs(currentPlayer.cameraX - currentPlayer.mapX) > 200) {
                    if (currentPlayer.cameraX > currentPlayer.mapX) {
                        currentPlayer.cameraX -= currentPlayer.cameraMoveSize;
                    } else {
                        currentPlayer.cameraX += currentPlayer.cameraMoveSize;
                    }
                } else {
                    currentPlayer.cameraX = currentPlayer.mapX;
                    xGood = true;
                }

                if (Math.abs(currentPlayer.cameraY - currentPlayer.mapY) > 200) {
                    if (currentPlayer.cameraY > currentPlayer.mapY) {
                        currentPlayer.cameraY -= currentPlayer.cameraMoveSize;
                    } else {
                        currentPlayer.cameraY += currentPlayer.cameraMoveSize;
                    }
                } else {
                    currentPlayer.cameraY = currentPlayer.mapY;
                    if (xGood) {
                        currentPlayer.setCameraReturning(false);
                    }
                }
                
                centerCameraOnPoint(new Point((int) currentPlayer.cameraX, (int) currentPlayer.cameraY));
            } else {
                centerCameraOnPoint(new Point(currentPlayer.mapX, currentPlayer.mapY));
            }
        }
    }

    @Override
    public void update() {
        super.update();

        try {
            for (String key : players.keySet()) {
                Player p = (Player) players.get(key);
                if (p != null) {
                    p.update();
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify players while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        setCamera();
    }

    @Override
    public void updateLong() {
        super.updateLong();

        try {
            for (String key : players.keySet()) {
                Player p = (Player) players.get(key);
                if (p != null) {
                    p.updateLong();
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify players while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
    }

    public void processPlayerUpdate(UpdatePlayer up) {
        if (up != null) {
            if (players.containsKey(up.id)) {
                Player player = players.get(up.id);
                if (player != null) {
                    EIError.debugMsg("Setting " + up.name + " to " + up.mapX + ":" + up.mapY + ", Action: " + up.action);
                    player.setPosition(up.mapX, up.mapY);
                    player.setVertMoveMode(up.vertMoveMode);
                    if (up.action.equals("ApplyDamage")) {
                        player.applyDamage(up.dataInt, up.actor);
                    } else if (up.action.equals("CollectedResource")) {
                        Resource resource = getResourceById(up.dataString);
                        registry.getResourceManager().resourceDoneCollecting(player, resource);
                    } else if (up.action.equals("GetOutOfRobot")) {
                        player.setInsideRobot(false);
                    } else if (up.action.equals("GoInsideRobot")) {
                        player.setInsideRobot(true);
                    } else if (up.action.equals("HandleClick")) {
                        player.handleClick(up.dataPoint);
                    } else if (up.action.equals("HandleRightClick")) {
                        player.handleRightClick();
                    } else if (up.action.equals("HandleReleased")) {
                        player.handleReleased(up.dataPoint);
                    } else if (up.action.equals("InventoryUpdate")) {
                        Inventory i = up.inventory;
                        i.setTransient(registry);

                        Player p = this.getPlayerById(up.id);

                        if (p == currentPlayer) {
                            registry.setInventory(i);
                        }

                        player.setInventory(i);
                    } else if (up.action.equals("Jump")) {
                        player.jump();
                    } else if (up.action.equals("MoveLeft")) {
                        player.moveLeft();
                    } else if (up.action.equals("MoveRight")) {
                        player.moveRight();
                    } else if (up.action.equals("RobotToggleActivated")) {
                        player.robotToggleActivated();
                    } else if (up.action.equals("ScrollQuickBar")) {
                        player.scrollQuickBar(up.dataInt);
                    } else if (up.action.equals("SetSelectedItem")) {
                        player.setPlayerSelectedItem(up.dataInt);
                    } else if (up.action.equals("SetStun")) {
                        player.setStatusStun(up.dataBoolean, up.dataLong);
                    } else if (up.action.equals("StartGather")) {
                        player.startGather(up.dataString);
                    } else if (up.action.equals("StopGather")) {
                        player.stopGather();
                    } else if (up.action.equals("StopJump")) {
                        player.stopJump();
                    } else if (up.action.equals("StopMove")) {
                        player.stopMove();
                    }
                }
            }
        }
    }

    public void processRobotUpdate(UpdateRobot ur) {
        if (ur != null) {
            if (players.containsKey(ur.playerId)) {
                Player player = players.get(ur.playerId);
                if (player != null) {
                    player.getRobot().setPosition(ur.mapX, ur.mapY);
                    if (ur.previousGoal != null) {
                        Goal g = ur.previousGoal;
                        g.setTransient(registry, player.getRobot().ai);
                        player.getRobot().ai.setPreviousGoal(g);
                    }
                    if (ur.currentGoal != null) {
                        Goal g = ur.currentGoal;
                        g.setTransient(registry, player.getRobot().ai);
                        player.getRobot().ai.setCurrentGoal(g);
                    }
                    if (ur.action.equals("ApplyDamage")) {
                        Actor a = ur.actor;
                        if (a != null) {
                            a.setTransient(registry, registry.getPlayerManager());
                        }
                        player.getRobot().applyDamage(ur.dataInt, a);
                    }
                }
            }
        }
    }

    public void render(Graphics g) {
        try {
            for (String key : players.keySet()) {
                Player p = (Player) players.get(key);
                if (p != null) {
                    p.render(g);
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify players while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
    }
}
