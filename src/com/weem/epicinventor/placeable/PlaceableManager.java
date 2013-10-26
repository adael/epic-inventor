package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.actor.monster.*;
import com.weem.epicinventor.drop.*;
import com.weem.epicinventor.hud.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.network.*;
import com.weem.epicinventor.projectile.*;
import com.weem.epicinventor.utility.*;
import com.weem.epicinventor.world.block.*;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.ArrayList;

enum PlaceableType {

    AutoXBow,
    BladeTrap,
    BookShelf,
    Box,
    Cabin,
    Chest,
    CopperMine,
    Crate,
    EmeraldTeleporter,
    Forge,
    GoldMine,
    LargeFarm,
    LargeSafe,
    LionFlyStatue,
    IronMine,
    ItemContainer,
    MedicKit,
    MediumSafe,
    Pasture,
    PlatinumMine,
    PottedFlower,
    RubyTeleporter,
    SapphireTeleporter,
    SawMill,
    ScareCrow,
    SilverMine,
    SmallFarm,
    SmallSafe,
    SteamEngine,
    StoneMine,
    ThornTrap,
    TownBlock,
    TownHall,
    WindMill,
    WeaponRack,
    WorkBench
}

public class PlaceableManager extends Manager implements Serializable {

    protected static final long serialVersionUID = 10000L;
    private HashMap<String, Placeable> placeables;
    Placeable currentlyPlacing;
    private final static int MAX_POWER_DISTANCE = 550; //in pixels

    public PlaceableManager() {
        super();
    }

    public PlaceableManager(GameController gc, Registry rg) {
        super(gc, rg);

        placeables = new HashMap<String, Placeable>();

        generateChests();
    }

    private void generateChests() {
        int chestWidth = 34;
        int chestHeight = 23;
        int[] chestLevels = {2, 2, 3, 3, 4, 5};

        for (int level = 0; level < chestLevels.length; level++) {
            for (int count = 0; count < chestLevels[level]; count++) {
                boolean chestPlaced = false;
                do {
                    Point p = this.getNewXY(gameController.getMapWidth(), chestHeight, level);
                    int y = findNextFloor(p.x + chestWidth, p.y, chestHeight);
                    if (p.y == y) {
                        //make sure the chest isn't spawned in view
                        Placeable chest = loadChest(p.x, p.y - 16);
                        chestPlaced = true;
                    }
                } while (!chestPlaced);
            }
        }
    }

    public Point getNewXY(int mapWidth, int height, int level) {
        Point p = new Point(0, 0);

        p.x = Rand.getRange(1, mapWidth);

        Registry r = registry;
        BlockManager bm = r.getBlockManager();

        p.y = Rand.getRange(bm.getLevelBottom(level),
                bm.getLevelTop(level));

        p.y = this.findNextFloor(p.x - 1, p.y, height);

        return p;
    }

    @Override
    public void setTransient(Registry rg) {
        super.setTransient(rg);

        try {
            for (String key : placeables.keySet()) {
                Placeable placeable = (Placeable) placeables.get(key);

                //convert town halls to cabins
                if (placeable.getType().equals("TownHall")) {
                    if (gameController.multiplayerMode != gameController.multiplayerMode.CLIENT) {
                        placeable.setIsDirty(true);
                        Placeable cabin = new Cabin(this, registry, "Placeables/Cursor/Cabin", "Placeables/Placed/Cabin", placeable.getMapX(), placeable.getMapY(), Placeable.State.Placed);
                        registerPlaceable(cabin);
                    }
                } else {
                    placeable.setTransient(rg);
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
    }

    public Placeable getRandomPlacable() {
        Placeable p = null;
        Object[] ks = placeables.keySet().toArray();
        if (ks.length > 0) {
            String s = ks[Rand.getRange(0, ks.length - 1)].toString();
            p = placeables.get(s);
        }
        return p;
    }

    public boolean isPlaceableWithin(Point p, int dist) {
        boolean ret = false;
        for (String key : placeables.keySet()) {
            Placeable placeable = placeables.get(key);
            if (placeable.getCenterPoint().distance(p) <= dist) {
                ret = true;
            }
        }
        return ret;
    }

    public void registerPlaceable(Placeable p) {
        if (!placeables.containsKey(p.getId())) {
            placeables.put(p.getId(), p);
        }
    }

    public Placeable getPlaceableById(String id) {
        if (placeables.containsKey(id)) {
            Placeable placeable = placeables.get(id);
            return placeable;
        } else {
            return null;
        }
    }

    public Placeable loadPlaceable(String n, int x, int y) {
        return loadPlaceable(n, x, y, Placeable.State.New);
    }

    public Placeable loadPlaceable(String n, int x, int y, Placeable.State state) {
        Placeable placeable = null;
        Player p = registry.getPlayerManager().getCurrentPlayer();
        int mapY = findNextFloor(x, y, 1);

        PlaceableType whichPlaceable = PlaceableType.valueOf(n);

        switch (whichPlaceable) {
            case AutoXBow:
                placeable = new AutoXBow(this, registry, "Placeables/Cursor/AutoXBow", "", x, mapY, state);
                break;
            case BladeTrap:
                placeable = new BladeTrap(this, registry, "Placeables/Cursor/BladeTrap", "Placeables/Placed/BladeTrap", x, mapY, state);
                break;
            case BookShelf:
                placeable = new BookShelf(this, registry, "Placeables/Cursor/BookShelf", "Placeables/Placed/BookShelf", x, mapY, state);
                break;
            case Box:
                placeable = new Box(this, registry, "Placeables/Cursor/Box", "Placeables/Placed/Box", x, mapY, state);
                break;
            case Cabin:
                placeable = new Cabin(this, registry, "Placeables/Cursor/Cabin", "Placeables/Placed/Cabin", x, mapY, state);
                break;
            case Chest:
                placeable = new Chest(this, registry, "Placeables/Cursor/Chest", "Placeables/Placed/Chest", x, mapY, state);
                break;
            case CopperMine:
                placeable = new CopperMine(this, registry, "Placeables/Cursor/CopperMine", "Placeables/Placed/CopperMine", x, mapY, state);
                break;
            case Crate:
                placeable = new Crate(this, registry, "Placeables/Cursor/Crate", "Placeables/Placed/Crate", x, mapY, state);
                break;
            case EmeraldTeleporter:
                placeable = new EmeraldTeleporter(this, registry, "Placeables/Cursor/EmeraldTeleporter", "Placeables/Placed/EmeraldTeleporter", x, mapY, state);
                break;
            case Forge:
                placeable = new Forge(this, registry, "Placeables/Cursor/Forge", "Placeables/Placed/Forge", x, mapY, state);
                break;
            case GoldMine:
                placeable = new GoldMine(this, registry, "Placeables/Cursor/GoldMine", "Placeables/Placed/GoldMine", x, mapY, state);
                break;
            case IronMine:
                placeable = new IronMine(this, registry, "Placeables/Cursor/IronMine", "Placeables/Placed/IronMine", x, mapY, state);
                break;
            case LargeFarm:
                placeable = new LargeFarm(this, registry, "Placeables/Cursor/LargeFarm", "", x, mapY, state);
                break;
            case LargeSafe:
                placeable = new LargeSafe(this, registry, "Placeables/Cursor/LargeSafe", "", x, mapY, state);
                break;
            case LionFlyStatue:
                placeable = new LionFlyStatue(this, registry, "Placeables/Cursor/LionFlyStatue", "Placeables/Placed/LionFlyStatue", x, mapY, state);
                break;
            case MedicKit:
                placeable = new MedicKit(this, registry, "Placeables/Cursor/MedicKit", "", x, mapY, state);
                break;
            case MediumSafe:
                placeable = new MediumSafe(this, registry, "Placeables/Cursor/MediumSafe", "", x, mapY, state);
                break;
            case Pasture:
                placeable = new Pasture(this, registry, "Placeables/Cursor/Pasture", "", x, mapY, state);
                break;
            case PlatinumMine:
                placeable = new PlatinumMine(this, registry, "Placeables/Cursor/PlatinumMine", "Placeables/Placed/PlatinumMine", x, mapY, state);
                break;
            case PottedFlower:
                placeable = new PottedFlower(this, registry, "Placeables/Cursor/PottedFlower", "Placeables/Placed/PottedFlower", x, mapY, state);
                break;
            case RubyTeleporter:
                placeable = new RubyTeleporter(this, registry, "Placeables/Cursor/RubyTeleporter", "Placeables/Placed/RubyTeleporter", x, mapY, state);
                break;
            case SapphireTeleporter:
                placeable = new SapphireTeleporter(this, registry, "Placeables/Cursor/SapphireTeleporter", "Placeables/Placed/SapphireTeleporter", x, mapY, state);
                break;
            case SawMill:
                placeable = new SawMill(this, registry, "Placeables/Cursor/SawMill", "Placeables/Placed/SawMill", x, mapY, state);
                break;
            case ScareCrow:
                placeable = new ScareCrow(this, registry, "Placeables/Cursor/ScareCrow", "", x, mapY, state);
                break;
            case SilverMine:
                placeable = new SilverMine(this, registry, "Placeables/Cursor/SilverMine", "Placeables/Placed/SilverMine", x, mapY, state);
                break;
            case SmallFarm:
                placeable = new SmallFarm(this, registry, "Placeables/Cursor/SmallFarm", "", x, mapY, state);
                break;
            case SmallSafe:
                placeable = new SmallSafe(this, registry, "Placeables/Cursor/SmallSafe", "", x, mapY, state);
                break;
            case SteamEngine:
                placeable = new SteamEngine(this, registry, "Placeables/Cursor/SteamEngine", "Placeables/Placed/SteamEngine", x, mapY, state);
                break;
            case StoneMine:
                placeable = new StoneMine(this, registry, "Placeables/Cursor/StoneMine", "Placeables/Placed/StoneMine", x, mapY, state);
                break;
            case ThornTrap:
                placeable = new ThornTrap(this, registry, "Placeables/Cursor/ThornTrap", "", x, mapY, state);
                break;
            case TownBlock:
                placeable = new TownBlock(this, registry, "Placeables/Cursor/TownBlock", "Placeables/Placed/TownBlock", x, mapY, state);
                break;
            case TownHall:
                placeable = new Building(this, registry, "Placeables/Cursor/TownHall", "Placeables/Placed/TownHall", x, mapY, state);
                break;
            case WeaponRack:
                placeable = new WeaponRack(this, registry, "Placeables/Cursor/WeaponRack", "Placeables/Placed/WeaponRack", x, mapY, state);
                break;
            case WindMill:
                placeable = new WindMill(this, registry, "Placeables/Cursor/WindMill", "Placeables/Placed/WindMill", x, mapY, state);
                break;
            case WorkBench:
                placeable = new WorkBench(this, registry, "Placeables/Cursor/WorkBench", "Placeables/Placed/WorkBench", x, mapY, state);
                break;
        }
        int[] range = gameController.getTownStartEnd(x, y);
        if ((placeable.getWidth() > range[1] - range[0] || range[0] == -1 || range[1] == -1) && whichPlaceable != PlaceableType.TownBlock) {
            registry.showMessage("Error", "Not enough town blocks to place that");
        } else {
            if (state == Placeable.State.Placed) {
                if (isOverOther(placeable)) {
                    placeable = null;
                }
                if (placeable != null) {
                    registerPlaceable(placeable);

                    if (state == Placeable.State.Placed) {
                        if (isOverOther(placeable)) {
                            placeable = null;
                        }

                        placeable.setIsBuilding(true);
                        if (registry.getGameController().multiplayerMode == registry.getGameController().multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                            if (registry.getNetworkThread().readyForUpdates) {
                                registry.getNetworkThread().sendData(placeable);
                            }
                        }
                    }
                }
            } else {
                if (placeable != null) {
                    registerPlaceable(placeable);
                    currentlyPlacing = placeable;
                }
            }
        }
        return placeable;
    }

    public Placeable loadInitialTownHall(int x) {
        int mapY = 0;
        Placeable placeable = null;

        mapY = findFloor(x);
        placeable = new Cabin(this, registry, "Placeables/Placed/Cabin", "Placeables/Placed/Cabin", x, mapY, Placeable.State.Placed);

        registerPlaceable(placeable);
        if (gameController.multiplayerMode == gameController.multiplayerMode.SERVER && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                registry.getNetworkThread().sendData(placeable);
            }
        }

        return placeable;
    }

    public Placeable loadContainer(int x, ArrayList<Drop> drops) {
        int mapY = 0;
        Placeable placeable = null;

        mapY = findFloor(x);
        placeable = new ItemContainer(this, registry, "Placeables/Placed/ItemContainer", "Placeables/Placed/ItemContainer", x, mapY, Placeable.State.Placed, drops);

        registerPlaceable(placeable);
        if (gameController.multiplayerMode == gameController.multiplayerMode.SERVER && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                registry.getNetworkThread().sendData(placeable);
            }
        }

        return placeable;
    }

    public Placeable loadChest(int x, int y) {
        Placeable placeable = new Chest(this, registry, "Placeables/Placed/Chest", "Placeables/Placed/Chest", x, y, Placeable.State.Placed);

        registerPlaceable(placeable);
        if (gameController.multiplayerMode == gameController.multiplayerMode.SERVER && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                registry.getNetworkThread().sendData(placeable);
            }
        }

        return placeable;
    }

    public int getHPRegenerationBonus(Point p) {
        int bonus = 0;

        Placeable placeable = null;

        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                if (placeable != null) {
                    bonus += placeable.getHPRegenerationBonus(p);
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        return bonus;
    }

    @Override
    public boolean checkPlaceableProjectileHit(Projectile p) {
        if (gameController.multiplayerMode != gameController.multiplayerMode.CLIENT) {
            Placeable placeable = null;

            try {
                for (String key : placeables.keySet()) {
                    placeable = (Placeable) placeables.get(key);
                    if (placeable != null) {
                        if (placeable.getPerimeter().contains(p.getCenterPoint())) {
                            placeable.applyDamage((Monster) p.getSource(), p.getDamage());
                            return true;
                        }
                    }
                }
            } catch (ConcurrentModificationException concEx) {
                //another thread was trying to modify placeables while iterating
                //we'll continue and the new item can be grabbed on the next update
            }
        }

        return false;
    }

    public float getAttackBonus(Point p) {
        float bonus = 0;

        Placeable placeable = null;

        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                if (placeable != null) {
                    bonus += placeable.getAttackBonus(p);
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        return bonus;
    }

    @Override
    public int getActivatedCount(String type) {
        int count = 0;

        Placeable placeable = null;

        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                if (placeable != null) {
                    if (placeable.isActivated() && placeable.getItemName().equals(type)) {
                        count++;
                    }
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        return count;
    }

    public int getTotalCount(String type, Placeable skipPlaceable) {
        int count = 0;

        HashMap<String, Placeable> tempHashMap = new HashMap<String, Placeable>(placeables);

        Placeable placeable = null;

        try {
            for (String key : tempHashMap.keySet()) {
                placeable = (Placeable) tempHashMap.get(key);
                if (placeable != null) {
                    String testString = placeable.getItemName();
                    if (type.equals(testString) && placeable != skipPlaceable) {
                        count++;
                    }
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        return count;
    }

    public boolean handleClick(Point clickPoint) {
        return placeCurrent();
    }

    public boolean handleRightClick(Point clickPoint) {
        Placeable placeable = null;

        //start from the top and work our way down to "layer" the huds
        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                if (placeable.handleRightClick(clickPoint)) {
                    return true;
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        return false;
    }

    public void toggleContainerHUD(HUD h) {
        gameController.toggleContainerHUD(h);
    }

    public void stopDestroy() {
        Placeable placeable = null;

        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                placeable.setDestroying(false);
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
    }

    public boolean startDestroy(Player player) {
        Placeable placeable = null;
        Placeable closestPlaceable = null;
        
        double closestDistance = 0;
        
        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                placeable.setDestroying(false);
                if (player.getCenterPoint().distance(placeable.getCenterPoint()) < closestDistance || closestDistance == 0) {
                    closestPlaceable = placeable;
                    closestDistance = player.getCenterPoint().distance(placeable.getCenterPoint());
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify resources while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
        
        if (closestPlaceable != null) {
            if(closestDistance <= closestPlaceable.width) {
                if (closestPlaceable.canDestroy()
                        && !closestPlaceable.getType().equals("TownHall")
                        && !closestPlaceable.getType().equals("ItemContainer")) {
                    if (closestPlaceable.setDestroying(true)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void placeableDoneDestroying(Placeable p) {
        gameController.stopGather();

        if (gameController.playerAddItem(p.getItemName(), 1) == 0) {
            p.destroy();
            SoundClip cl = new SoundClip("Player/Good");
        }

        if (gameController.multiplayerMode != gameController.multiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlaceable up = new UpdatePlaceable(p.getId());
                up.hitPoints = p.getHitPoints();
                up.totalHitPoints = p.getTotalHitPoints();
                up.action = "DoneDestroying";
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public boolean teleportPlayer(Placeable p) {
        Placeable placeable = null;

        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                if (placeable.getType().equals(p.getType()) && placeable != p) {
                    Player player = registry.getPlayerManager().getCurrentPlayer();
                    if (player != null) {
                        SoundClip cl = new SoundClip("Misc/Teleport");
                        player.setPosition(placeable.getCenterPoint().x - 25, placeable.getMapY());
                        return true;
                    }
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        registry.showMessage("Error", "No matching teleporter found");

        return false;
    }

    public boolean placeCurrent() {
        if (currentlyPlacing != null && currentlyPlacing.checkCanPlace()) {
            String currentlyPlacingType = currentlyPlacing.getType();
            if (currentlyPlacingType.equals("EmeraldTeleporter")) {
                if (this.getTotalCount("EmeraldTeleporter", currentlyPlacing) >= 2) {
                    registry.showMessage("Error", "You can only place 2 Emerald Teleporters at a time");
                    return false;
                }
            }
            if (currentlyPlacingType.equals("RubyTeleporter")) {
                if (this.getTotalCount("RubyTeleporter", currentlyPlacing) >= 2) {
                    registry.showMessage("Error", "You can only place 2 Ruby Teleporters at a time");
                    return false;
                }
            }
            if (currentlyPlacingType.equals("SapphireTeleporter")) {
                if (this.getTotalCount("SapphireTeleporter", currentlyPlacing) >= 2) {
                    registry.showMessage("Error", "You can only place 2 Sapphire Teleporters at a time");
                    return false;
                }
            }
            currentlyPlacing.setState(Placeable.State.Placed);
            if (registry.getGameController().multiplayerMode == registry.getGameController().multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                if (registry.getNetworkThread().readyForUpdates) {
                    registry.getNetworkThread().sendData(currentlyPlacing);
                }
            } else if (gameController.multiplayerMode == gameController.multiplayerMode.CLIENT && registry.getNetworkThread() != null) {
                if (registry.getNetworkThread().readyForUpdates) {
                    registry.getNetworkThread().sendData("place " + currentlyPlacing.getType() + " " + currentlyPlacing.getMapX() + " " + currentlyPlacing.getMapY());
                }
                currentlyPlacing.isDirty = true;
            }
            currentlyPlacing = null;
            return true;
        }

        return false;
    }

    public void cancelPlaceable() {
        if (currentlyPlacing()) {
            if (placeables.containsKey(currentlyPlacing.getId())) {
                placeables.remove(currentlyPlacing.getId());
            }
            currentlyPlacing = null;
        }
    }

    public void playerDeleteInventory(int slot, int qty) {
        gameController.playerDeleteInventory(slot, qty);
    }

    public boolean currentlyPlacing() {
        if (currentlyPlacing == null) {
            return false;
        }

        return true;
    }

    @Override
    public void monsterAttackPlaceable(Monster source, Rectangle attackRect, int meleeDamage) {
        int dmg = 0;
        Placeable placeable = null;

        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                dmg = placeable.attackDamage(source, attackRect, meleeDamage);
                if (dmg > 0) {
                    break;
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
    }

    public int[] getPower() {
        int[] powerArray = new int[3];
        int usedPower = 0, totalPower = 0, availablePower = 0;

        Placeable placeable = null;

        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                if (placeable.canDestroy()) {
                    usedPower += placeable.getPowerRequired();
                    totalPower += placeable.getPowerGenerated();
                    availablePower += placeable.getPowerGenerated();
                    availablePower -= placeable.getPowerRequired();
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        powerArray[0] = usedPower;
        powerArray[1] = totalPower;
        powerArray[2] = availablePower;

        return powerArray;
    }

    public int[] getTownStartEndUnderPlayer() {
        return gameController.getTownStartEndUnderPlayer();
    }

    public Placeable getClosestActivated(Point p) {
        Placeable placeable = null;
        Placeable closestPlaceable = null;

        double closestDistance = 0;

        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                if (placeable.isActivated() && !placeable.getItemName().equals("ItemContainer")) {
                    double distance = p.distance(placeable.getCenterPoint());
                    if ((distance < closestDistance || closestDistance == 0)
                            && !placeable.getType().equals("ItemContiner")
                            && !placeable.getType().equals("PlayerContiner")
                            && !placeable.getType().equals("Chest")
                            && !placeable.getType().equals("TownHall")
                            && !placeable.getType().equals("Cabin")) {
                        closestPlaceable = placeable;
                        closestDistance = distance;
                    }
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        return closestPlaceable;
    }

    public Placeable getClosest(Point p) {
        Placeable placeable = null;
        Placeable closestPlaceable = null;

        double closestDistance = 0;

        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                if (!placeable.getItemName().equals("ItemContainer")) {
                    double distance = p.distance(placeable.getCenterPoint());
                    if ((distance < closestDistance || closestDistance == 0)) {
                        closestPlaceable = placeable;
                        closestDistance = distance;
                    }
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        return closestPlaceable;
    }

    @Override
    public Point getNearestTownHallXY(Point p) {
        int closestX = 0;
        Point townHall = new Point(0, 0);

        Placeable placeable = null;

        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                if (placeable.getType().equals("TownHall") || placeable.getType().equals("Cabin")) {
                    int distance = (int) placeable.getCenterPoint().distance(p);
                    if (distance < closestX || closestX == 0) {
                        closestX = distance;
                        townHall.x = placeable.getMapX() + (placeable.getWidth() / 2);
                        townHall.y = placeable.getMapY();
                    }
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        return townHall;
    }

    public boolean isOverOther() {
        return isOverOther(currentlyPlacing);
    }

    public boolean isOverOther(Placeable p) {
        boolean ret = false;
        if (p != null) {
            Placeable cp = null;
            try {
                for (String key : placeables.keySet()) {
                    cp = (Placeable) placeables.get(key);
                    if (!cp.getId().equals(p.getId())) {
                        if ((p.getMapX() >= cp.getMapX() && p.getMapX() < cp.getMapX() + cp.getWidth())
                                || (p.getMapX() + p.getWidth() > cp.getMapX() && p.getMapX() + p.getWidth() < cp.getMapX() + cp.getWidth())
                                || (p.getMapX() < cp.getMapX() && p.getMapX() + p.getWidth() > cp.getMapX())) {
                            if (p.getMapY() >= cp.getMapY() && p.getMapY() < cp.getMapY() + cp.getHeight()) {
                                ret = true;
                            }
                        }
                    }
                }
            } catch (ConcurrentModificationException concEx) {
                //another thread was trying to modify placeables while iterating
                //we'll continue and the new item can be grabbed on the next update
            }
        }
        return ret;
    }

    public void checkIfFeared(Monster m) {
        int fearValue;
        Placeable placeable = null;

        if (gameController.multiplayerMode != gameController.multiplayerMode.CLIENT) {
            try {
                for (String key : placeables.keySet()) {
                    placeable = (Placeable) placeables.get(key);
                    fearValue = placeable.getFearGenerated();
                    if (fearValue > 0 && placeable.canDestroy()) {
                        if (placeable.getCenterPoint().distance(m.getCenterPoint()) <= placeable.getFearDistance()) {
                            if (Rand.getRange(1, fearValue) == 1) {
                                m.fear(placeable.getCenterPoint(), placeable.getFearDuration());

                                if (gameController.multiplayerMode == gameController.multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                                    if (registry.getNetworkThread().readyForUpdates) {
                                        UpdateMonster um = new UpdateMonster(m.getId());
                                        um.mapX = m.getMapX();
                                        um.mapY = m.getMapY();
                                        um.action = "Fear";
                                        um.dataPoint = placeable.getCenterPoint();
                                        um.dataLong = placeable.getFearDuration();
                                        registry.getNetworkThread().sendData(um);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ConcurrentModificationException concEx) {
                //another thread was trying to modify placeables while iterating
                //we'll continue and the new item can be grabbed on the next update
            }
        }
    }

    public void checkPlaceableDamageAgainstMob(Monster m) {
        if (gameController.multiplayerMode != gameController.multiplayerMode.CLIENT) {
            Placeable placeable = null;

            try {
                for (String key : placeables.keySet()) {
                    placeable = (Placeable) placeables.get(key);
                    if (placeable.canDestroy()) {
                        if (placeable.getPerimeter().intersects(m.getPerimeter())) {
                            m.applyDamage(placeable.getTouchDamage(), registry.getPlayerManager().getCurrentPlayer(), true);
                        }
                    }
                }
            } catch (ConcurrentModificationException concEx) {
                //another thread was trying to modify placeables while iterating
                //we'll continue and the new item can be grabbed on the next update
            }
        }
    }

    @Override
    public void update() {
        super.update();

        Placeable placeable = null;

        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                if (placeable != null) {
                    placeable.update();

                    if (placeable.isDirty()) {
                        placeables.remove(key);
                    }
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        if (currentlyPlacing != null) {
            Point p = null;
            if (currentlyPlacing.getItemName().equals("TownBlock")) {
                p = getBlockSnap();
            } else {
                p = getTownSnap();
            }
            if (currentlyPlacing.getState() == Placeable.State.New) {
                currentlyPlacing.setState(Placeable.State.NotPlaced);
            }
            currentlyPlacing.setPosition(p.x, p.y);
        }
    }

    protected Point getBlockSnap() {
        if (currentlyPlacing != null) {
            int x = (gameController.getCurrentMousePosition().x
                    + gameController.getMapOffsetX())
                    / BlockManager.getBlockWidth()
                    * BlockManager.getBlockWidth();
            int y = (gameController.getMapOffsetY()
                    - gameController.getCurrentMousePosition().y
                    + gameController.getPHeight())
                    / BlockManager.getBlockHeight()
                    * BlockManager.getBlockHeight();
            if (!doesRectContainBlocks(x + 1, y + 1, currentlyPlacing.getWidth() - 2, currentlyPlacing.getHeight() - 2)) {
                y = findNextFloor(x, y, currentlyPlacing.getHeight()) - BlockManager.getBlockHeight() - currentlyPlacing.getHeight();
            }
            Point p = new Point(x, y);
            return p;
        } else {
            return new Point(0, 0);
        }
    }

    protected Point getTownSnap() {
        if (currentlyPlacing != null) {
            int x = (gameController.getCurrentMousePosition().x
                    + gameController.getMapOffsetX())
                    / BlockManager.getBlockWidth()
                    * BlockManager.getBlockWidth();

            int[] xStartEnd = getTownStartEndUnderPlayer();
            if (xStartEnd[0] != -1) {
                if (x < xStartEnd[0]) {
                    x = xStartEnd[0];
                } else if (x + currentlyPlacing.getWidth() > xStartEnd[1]) {
                    x = xStartEnd[1] - currentlyPlacing.getWidth();
                }
            }
            int y = findNextFloor(x, currentlyPlacing.getMapY(), 1) - BlockManager.getBlockHeight();
            Point p = new Point(x, y);
            return p;
        } else {
            return new Point(0, 0);
        }
    }

    @Override
    public boolean isInFrontOfPlaceable(Rectangle r) {
        Placeable placeable = null;

        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                if (placeable != null) {
                    if (placeable.getPerimeter().intersects(r)) {
                        return true;
                    }
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        return false;
    }

    public Placeable getPasture(Rectangle r) {
        Placeable placeable = null;

        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                if (placeable != null) {
                    if (placeable.getType().equals("Pasture")) {
                        if (placeable.getPerimeter().intersects(r)) {
                            return placeable;
                        }
                    }
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        return null;
    }

    @Override
    public void updateLong() {
        boolean nearArea;
        Point placeablePoint, areaPoint;
        Placeable placeable = null;
        int[] powerValues = this.getPower();

        //loop through the placeable to figure out power for each one
        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                if (placeable != null) {
                    //only affect placeables which are built and require power
                    if (placeable.canDestroy() && placeable.getPowerRequired() > 0) {
                        //check availablePower
                        if (powerValues[2] < 0) {
                            placeable.setIsPowered(false);
                        } else {
                            placeable.setIsPowered(true);
                        }
                    }
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        //run long update for placeables
        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                if (placeable != null) {
                    placeable.updateLong();
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
    }

    public void render(Graphics g) {
        Placeable placeable = null;

        try {
            for (String key : placeables.keySet()) {
                placeable = (Placeable) placeables.get(key);
                placeable.render(g);
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify placeables while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
    }

    public void processPlaceableUpdate(UpdatePlaceable up) {
        EIError.debugMsg("Setting Placeable " + up.id + ", Action: " + up.action);
        if (up != null) {
            if (placeables.containsKey(up.id)) {
                Placeable placeable = placeables.get(up.id);
                if (placeable != null) {
                    placeable.setHitPoints(up.hitPoints, up.totalHitPoints);
                    if (up.totalHitPoints > 0) {
                        placeable.setHitPoints(up.hitPoints, up.totalHitPoints);
                    }
                    if (up.mapY > 0) {
                        placeable.setPosition(up.mapX, up.mapY);
                    }
                    if (up.action.equals("ApplyDamage")) {
                        placeable.applyDamage(up.source, up.dataInt);
                    }
                    if (up.action.equals("DoneDestroying")) {
                        placeableDoneDestroying(placeable);
                    } else if (up.action.equals("InventoryUpdate")) {
                        Inventory i = up.inventory;
                        i.setTransient(registry);
                        placeable.setInventory(i);
                    } else if (up.action.equals("IsDirty")) {
                        placeable.setIsDirty(true);
                    } else if (up.action.equals("SetState")) {
                        placeable.setState(up.state);
                    }
                }
            } else {
                if (gameController.multiplayerMode == gameController.multiplayerMode.CLIENT && registry.getNetworkThread() != null) {
                    if (registry.getNetworkThread().readyForUpdates) {
                        EIError.debugMsg("Placeable not found - need " + up.id);
                        registry.getNetworkThread().sendData("send placeable data: " + up.id);
                    }
                }
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
