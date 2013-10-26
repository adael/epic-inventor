package com.weem.epicinventor;

import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.actor.monster.*;
import com.weem.epicinventor.armor.*;
import com.weem.epicinventor.drop.*;
import com.weem.epicinventor.hud.*;
import com.weem.epicinventor.indicator.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.item.*;
import com.weem.epicinventor.network.*;
import com.weem.epicinventor.particle.*;
import com.weem.epicinventor.pixelize.*;
import com.weem.epicinventor.placeable.*;
import com.weem.epicinventor.projectile.*;
import com.weem.epicinventor.resource.*;
import com.weem.epicinventor.utility.*;
import com.weem.epicinventor.weapon.*;
import com.weem.epicinventor.world.background.*;
import com.weem.epicinventor.world.block.*;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import javax.sound.sampled.*;
import java.awt.geom.Arc2D;

public class GameController {

    public enum MoveMode {

        NONE, LEFT, RIGHT, UP, DOWN
    };

    public enum MultiplayerMode {

        NONE, CLIENT, SERVER
    };
    private int pWidth, pHeight;
    private MoveMode xMoveMode;
    private MoveMode yMoveMode;
    private int xMoveSize; //size of map move in pixels
    private int yMoveSize; //size of map move in pixels 
    public final static String CONFIG_DIR = "/com/weem/epicinventor/_config/";
    public final static String IMAGES_DIR = "/Images/";
    public final static String MUSIC_DIR = "/Music/";
    public static Properties props;
    private final static double MOVE_FACTOR = 0.75; //smaller is slower
    private final static int MAX_RESOURCE_DISTANCE = 24; //from center
    private final static int MAX_PLACEABLE_DISTANCE = 24; //from center
    private final static int MAX_CONTAINER_DISTANCE = 200; //from center
    private Registry registry;
    private BlockManager blockManager;
    private ItemManager itemManager;
    private PlayerManager playerManager;
    private PlaceableManager placeableManager;
    private ProjectileManager projectileManager;
    private BackgroundManager backgroundManager;
    private MonsterManager monsterManager;
    private ResourceManager resourceManager;
    private IndicatorManager indicatorManager;
    private PixelizeManager pixelizeManager;
    private HUDManager hudManager;
    private int mapOffsetX; //x position of the map
    private int mapOffsetY; //y position of the map
    private Point currentMousePosition;
    private GamePanel gamePanel;
    private BufferedImage cursorImage;
    private String cursorText;
    private float messageTotalTime;
    private float MESSAGE_MAX_TIME = 5.00f;
    private boolean showMessage;
    private String message = "";
    private String messageType = "";
    private boolean networkMode;
    private String gameError;
    private String newPlayerName = "";
    private String newRobotName = "";
    private boolean isLoading;
    private boolean isOnline;
    private volatile NetworkThread networkThread;
    private long period;
    private ImageLoaderThread imageLoadThread;
    private GamePlayList currentPlayList, inventorPlayList, robotPlayList, bossPlayList, belowGroundPlayList;
    public MultiplayerMode multiplayerMode = MultiplayerMode.NONE;
    public boolean startServer = false;
    public boolean serverJoin = false;
    public String serverPort = "7777";
    public String serverIP = "";
    private int cameraShakeAmount = 0;
    private long cameraShakeFinished = 0;
    String currentVersion = "";
    Point townCameraPosition;
    private long nextAutoSave = 0;
    private boolean isInGame = false;
    private int levelUpGraphicDirection = 0;
    private int levelUpGraphicPosition = -340;
    //private ParticleEmitter pe;

    public GameController(int w, int h, long p, GamePanel gp) {
        pWidth = w;
        pHeight = h;

        period = p;

        mapOffsetX = 0;
        mapOffsetY = 30000;
        gamePanel = gp;
        cursorImage = null;
        cursorText = "";

        props = System.getProperties();
        Enumeration e = props.propertyNames();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            EIError.debugMsg(key + ": " + props.getProperty(key), EIError.ErrorLevel.Error);
        }

        registry = new Registry();
        registry.setGameController(this);
        registry.setImageLoader(new ImageLoader(period / 1000000L));
        imageLoadThread = new ImageLoaderThread(registry.getImageLoader(), "StartingImages.dat");
        currentMousePosition = new Point(0, 0);

        hudManager = new HUDManager(this, registry);
        registry.setHUDManager(hudManager);

        Settings.init(this, registry);
        Armor.init();
        Weapon.init();
        Rand.init();

        robotPlayList = new GamePlayList();
        robotPlayList.addToPlayList("RobotSongLoop", 123.0f);

        bossPlayList = new GamePlayList();
        bossPlayList.setTransitionTime(0.0f);
        bossPlayList.addToPlayList("BossLoop", 147.0f);

        belowGroundPlayList = new GamePlayList();
        belowGroundPlayList.addToPlayList("UndergroundLoop", 339.0f);

        inventorPlayList = new GamePlayList();
        inventorPlayList.addToPlayList("InventorSongLoop", 308.0f);
        currentPlayList = inventorPlayList;
        updateMusicVolume();
        inventorPlayList.start();

        isLoading = true;

        try {
            while (!imageLoadThread.getImagesLoaded()) {
                Thread.sleep(10L);
            }
        } catch (Exception c) {
        }
        imageLoadThread = new ImageLoaderThread(registry.getImageLoader(), "Images.dat");
        itemManager = new ItemManager(this, registry);
        registry.setItemManager(itemManager);
        playerManager = new PlayerManager(this, registry);
        registry.setPlayerManager(playerManager);

        String v = RemoteFile.getFileContents("http://epicinventor.com/v/" + Game.VERSION + "/");
        if (v != null) {
            if (!v.equals("")) {
                currentVersion = v;
                isOnline = true;
            }
        }

        //resetGame();
        hudManager.loadHUD(HUDManager.HUDType.ScreenMain);

        EIError.debugMsg("max.heap: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "mb", EIError.ErrorLevel.Error);
        EIError.debugMsg("current.heap: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "mb", EIError.ErrorLevel.Error);
        EIError.debugMsg("free.memory: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + "mb", EIError.ErrorLevel.Error);

        EIError.debugMsg("Game started", EIError.ErrorLevel.Error);
    }

    public void setLoading(boolean l) {
        isLoading = l;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void updateMusicVolume() {
        updateMusicVolume(Settings.volumeMusic);
    }

    public boolean getIsPaused() {
        return gamePanel.getIsPaused();
    }

    public boolean getIsMasterPaused() {
        return gamePanel.getIsMasterPaused();
    }

    public void togglePaused() {
        gamePanel.togglePaused();
    }

    public void updateMusicVolume(float volume) {
        volume *= 10f;

        if (currentPlayList != null) {
            currentPlayList.setVolume(volume);
        }
        if (inventorPlayList != null) {
            inventorPlayList.setVolume(volume);
        }
        if (robotPlayList != null) {
            robotPlayList.setVolume(volume);
        }
        if (bossPlayList != null) {
            bossPlayList.setVolume(volume);
        }
        if (belowGroundPlayList != null) {
            belowGroundPlayList.setVolume(volume);
        }
    }

    public void resizePanel(int w, int h) {
        if (w == 0 && h == 0) {
            //full screen
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            pWidth = (int) dim.getWidth();
            pHeight = (int) dim.getHeight();
        } else {
            pWidth = w;
            pHeight = h;
        }

        gamePanel.resizePanel(w, h);
    }

    public void resetGame() {
        resetGame(false);
    }

    public void resetGame(boolean showHelp) {
        Point resolution = Settings.getResolution(Settings.resolution);
        if (resolution != null) {
            resizePanel(resolution.x, resolution.y);
        }

        hudManager.loadHUD(HUDManager.HUDType.ScreenLoading);

        mapOffsetX = 0;
        mapOffsetY = 30000;
        cursorImage = null;
        cursorText = "";

        Game.loadingText = "Creating World";

        if (blockManager == null) {
            blockManager = new BlockManager(this, registry);
        }
        blockManager.updateResolution();
        registry.setBlockManager(blockManager);

        Game.loadingText = "Initializing Inventor";
//        playerManager.setUpdating(true);
        playerManager.initPlayers();

        Game.loadingText = "Spinning Up Power Grid";
        boolean newPM = false;
        if (placeableManager == null) {
            placeableManager = new PlaceableManager(this, registry);
            newPM = true;
        }
        registry.setPlaceableManager(placeableManager);

        projectileManager = new ProjectileManager(this, registry);
        registry.setProjectileManager(projectileManager);

        Game.loadingText = "Making Pretty Clouds";
        backgroundManager = new BackgroundManager(this, registry);

        Game.loadingText = "Spawning Evil Bad Guys";
        if (monsterManager == null) {
            monsterManager = new MonsterManager(this, registry);
        }
        registry.setMonsterManager(monsterManager);

        /*
         * Game.loadingText = "Spawning Evil Bad Guys"; monsterManager = new
         * MonsterManager(this, registry);
         * registry.setMonsterManager(monsterManager);
         */

        Game.loadingText = "Making Shinies";
        resourceManager = new ResourceManager(this, registry);
        registry.setResourceManager(resourceManager);

        indicatorManager = new IndicatorManager(this, registry);
        registry.setIndicatorManager(indicatorManager);

        pixelizeManager = new PixelizeManager(this, registry);
        registry.setPixelizeManager(pixelizeManager);

        registry.setInventory(playerManager.getPlayerInventory());

        if (newPM) {
            placeableManager.loadInitialTownHall(0);
        }

        Game.loadingText = "Loading Images";
        try {
            while (!imageLoadThread.getImagesLoaded()) {
                Thread.sleep(100L);
            }
            imageLoadThread = null;
        } catch (Exception c) {
        }
        placeableManager.setTransient(registry);
        monsterManager.setTransient(registry);

        hudManager.unloadHUD("ScreenLoading");

        isInGame = true;

        hudManager.loadHUD(HUDManager.HUDType.Master);
        hudManager.loadHUD(HUDManager.HUDType.QuickBar);
        hudManager.loadHUD(HUDManager.HUDType.Portrait);
        hudManager.loadHUD(HUDManager.HUDType.WeaponInfo);
        hudManager.loadHUD(HUDManager.HUDType.ArmorInfo);
        //hudManager.loadHUD(HUDManager.HUDType.Version);
        hudManager.loadHUD(HUDManager.HUDType.Pause);
        hudManager.loadHUD(HUDManager.HUDType.Console);

        if (startServer) {
            networkThread = new NetworkThread(registry, this, "", Integer.parseInt(serverPort));
            registry.setNetworkThread(networkThread);
            networkThread.start();
        } else if (serverJoin) {
            networkThread = new NetworkThread(registry, this, serverIP, Integer.parseInt(serverPort));
            registry.setNetworkThread(networkThread);
            networkThread.start();
        }

        updateMusicVolume();

        isLoading = false;

        if (showHelp) {
            gamePanel.pauseMasterGame();
            hudManager.loadHUD(HUDManager.HUDType.Tutorial);
        }

        EIError.debugMsg("World loaded", EIError.ErrorLevel.Error);

        EIError.debugMsg("max.heap: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "mb", EIError.ErrorLevel.Error);
        EIError.debugMsg("current.heap: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "mb", EIError.ErrorLevel.Error);
        EIError.debugMsg("free.memory: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + "mb", EIError.ErrorLevel.Error);

        /*
         * ArrayList<String> images = new ArrayList<String>();
         * images.add("Misc/Particle1"); images.add("Misc/Particle2");
         * images.add("Misc/Particle3"); pe = new ParticleEmitter(this,
         * registry, playerManager.getCurrentPlayer().getMapX() + 100,
         * playerManager.getCurrentPlayer().getMapY() + 100, images);
         */
    }

    public void addStuff() {
        playerAddItem("ScrapHead", 1);
        playerManager.getCurrentPlayer().playerEquipFromInventory(0);
        playerAddItem("ScrapChest", 1);
        playerManager.getCurrentPlayer().playerEquipFromInventory(0);
        playerAddItem("ScrapLegs", 1);
        playerManager.getCurrentPlayer().playerEquipFromInventory(0);
        playerAddItem("ScrapFeet", 1);
        playerManager.getCurrentPlayer().playerEquipFromInventory(0);

        playerAddItem("WoodHead", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 35);
        playerAddItem("WoodChest", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 36);
        playerAddItem("WoodLegs", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 37);
        playerAddItem("WoodFeet", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 38);

        playerAddItem("ScrapHammer", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 39);
        playerAddItem("WoodHammer", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 40);
        playerAddItem("Bow", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 41);
        playerAddItem("CrossBow", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 42);
        playerAddItem("ThornTrap", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 43);
        playerAddItem("WorkBench", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 44);
        playerAddItem("SteamEngine", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 45);
        playerAddItem("ScareCrow", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 46);
        playerAddItem("RobotGoldBlade", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 47);
        playerAddItem("Propeller", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 48);
        playerAddItem("AutoXBow", 1);
        playerManager.getCurrentPlayer().playerSwapInventory(0, 49);

        playerAddItem("Wood", 30);
        playerAddItem("Stone", 15);
        playerAddItem("Copper", 2);
        playerAddItem("BlueBattery", 1);
        playerAddItem("GreenBattery", 1);
        playerAddItem("FireFighterHat", 1);
        playerAddItem("Disguise", 1);
        playerAddItem("ShutterShades", 1);
    }

    public boolean getIsInGame() {
        return isInGame;
    }

    public void setIsInGame(boolean g) {
        isInGame = g;
    }

    public long getNextAutoSave() {
        return nextAutoSave;
    }

    public void setNextAutoSave(long a) {
        nextAutoSave = a;
    }

    public void setBlockManager(BlockManager bm) {
        blockManager = bm;
        registry.setBlockManager(bm);
    }

    public void setPlaceableManager(PlaceableManager pm) {
        placeableManager = pm;
        registry.setPlaceableManager(pm);
    }

    public void setResourceManager(ResourceManager rm) {
        resourceManager = rm;
        registry.setResourceManager(rm);
    }

    public void setMonsterManager(MonsterManager mm) {
        monsterManager = mm;
        registry.setMonsterManager(mm);
    }

    public void loadPlayer() {
        Settings.loadPlayer(registry);
    }

    public void setPlayerNames(String characterName, String robotName) {
        playerManager.setPlayerNames(characterName, robotName);
    }

    public void showMessage(String mType, String m) {
        showMessage = true;
        messageTotalTime = 0;
        messageType = mType;
        message = m;
    }

    public int getPWidth() {
        return pWidth;
    }

    public int getPHeight() {
        return pHeight;
    }

    public int getMapWidth() {
        return blockManager.getMapWidth();
    }

    public int getMaxContainerDistance() {
        return MAX_CONTAINER_DISTANCE;
    }

    public int getMapHeight() {
        return blockManager.getMapHeight();
    }

    public int getMapOffsetX() {
        return mapOffsetX;
    }

    public int getMapOffsetY() {
        return mapOffsetY;
    }

    public double getMoveFactor() {
        return MOVE_FACTOR;
    }

    public int getBlockWidth() {
        return blockManager.getBlockWidth();
    }

    public int getBlockHeight() {
        return blockManager.getBlockHeight();
    }

    public MoveMode getXMoveMode() {
        return xMoveMode;
    }

    public MoveMode getYMoveMode() {
        return yMoveMode;
    }

    public int getXMoveSize() {
        return xMoveSize;
    }

    public int getYMoveSize() {
        return yMoveSize;
    }

    public void checkIfFeared(Monster m) {
        placeableManager.checkIfFeared(m);
    }

    public boolean checkMobProjectileHit(Projectile p) {
        return monsterManager.checkMobProjectileHit(p);
    }

    public boolean checkMobParticleHit(Particle p) {
        return monsterManager.checkMobParticleHit(p);
    }

    public boolean checkPlayerProjectileHit(Projectile p) {
        return playerManager.checkPlayerProjectileHit(p);
    }

    public boolean checkPlaceableProjectileHit(Projectile p) {
        return placeableManager.checkPlaceableProjectileHit(p);
    }

    public void checkPlaceableDamageAgainstMob(Monster m) {
        placeableManager.checkPlaceableDamageAgainstMob(m);
    }

    public int checkForBlock(Point p) {
        short b = blockManager.getBlockFromPoint(p);
        if (!blockManager.isIdInGroup(b, "None")) {
            BlockType bt = blockManager.getBlockTypeById(b);
            if (bt != null && !bt.isBackground()) {
                return (p.y / blockManager.getBlockHeight()) * blockManager.getBlockHeight() + blockManager.getBlockHeight();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public Inventory getRobotInventory() {
        if (playerManager != null) {
            return playerManager.getRobotInventory();
        } else {
            return null;
        }
    }

    public int getRobotInventorySize() {
        if (playerManager != null) {
            return playerManager.getRobotInventorySize();
        } else {
            return 0;
        }
    }

    public void gameExit() {
        System.exit(0);
    }

    public int findFloor(int xWorld) {
        int y = 0;
        if (blockManager != null) {
            y = blockManager.findFloor(xWorld);
        }
        return y;
    }

    public int findNextFloor(int xWorld, int yWorld, int height) {
        return blockManager.findNextFloor(xWorld, yWorld, height);
    }

    public short[] blocksUnder(int xStartPix, int xEndPix, int yPix) {
        return blockManager.blocksUnder(xStartPix, xEndPix, yPix);
    }

    public boolean isIdInGroup(int id, String g) {
        return blockManager.isIdInGroup(id, g);
    }

    public int[] getTownStartEnd(int x, int y) {
        return blockManager.getTownStartEnd(x, y);
    }

    public int mapToPanelX(int x) {
        return x - mapOffsetX;
    }

    public int mapToPanelY(int y) {
        return y - mapOffsetY;
    }

    public int panelToMapX(int x) {
        return x + mapOffsetX;
    }

    public int panelToMapY(int y) {
        y = mapOffsetY + pHeight - y;

        return y;
    }

    public void setXMoveSize(int size) {
        xMoveSize = size;
    }

    public void setYMoveSize(int size) {
        yMoveSize = size;
    }

    public void setMapOffsetX(int x) {
        mapOffsetX = x;
    }

    public void setMapOffsetY(int y) {
        mapOffsetY = y;
    }

    public void giveXP(Monster m) {
        playerManager.giveXP(m);
    }

    public void dropLoot(Monster m, int x, int y, ArrayList<Drop> drops) {
        Drop drop = null;
        ArrayList<Drop> overflowDrops = new ArrayList<Drop>();

        Player p = null;
        Actor a = m.getLastAttacker();
        if (a instanceof com.weem.epicinventor.actor.Robot) {
            p = ((com.weem.epicinventor.actor.Robot) a).getPlayer();
        } else {
            if (a != null) {
                p = playerManager.getPlayerById(a.getId());
            }
        }

        for (int i = 0; i < drops.size(); i++) {
            drop = drops.get(i);
            int remaining = drop.getQty();

            //figure out what level should drop
            int level = 1;
            int levelCheck = Rand.getRange(1, 100);

            if (p != null) {
                level = p.getLevel();
                if (levelCheck > 90) {
                    level += 3;
                } else if (levelCheck > 75) {
                    level += 2;
                } else if (levelCheck > 50) {
                    level += 1;
                }
            }
            if (level < 1) {
                level = 1;
            }
            if (level > 20) {
                level = 20;
            }

            if (p != null && m.getCenterPoint().distance(p.getCenterPoint()) < monsterManager.getPWidth()) {
                //make sure we're near by or you don't get no..  duh nuh nuh, nuhnuhnuhnuh..  satis..faction!
                remaining = playerAddItem(p, drop.getItemName(), drop.getQty(), level);
            }
            if (remaining > 0) {
                drop.setQty(remaining);
                drop.setLevel(level);
                overflowDrops.add(drop);
            }
        }

        if (overflowDrops.size() > 0) {
            placeableManager.loadContainer(x, overflowDrops);
        }

        indicatorManager.createIndicator(x, y, drops);
    }

    public void shakeCamera(long time, int amount) {
        cameraShakeFinished = registry.currentTime + time;
        cameraShakeAmount = amount;
    }

    public boolean getKey(int k) {
        return gamePanel.getKey(k);
    }

    public void centerCameraOnPoint(Point p) {
        int newX = p.x;
        int newY = p.y;

        if (townCameraPosition == null) {
            townCameraPosition = new Point(p.x, p.y);
        }

        if (getKey(84) && townCameraPosition != null) {
            newX = townCameraPosition.x;
            newY = townCameraPosition.y;
        } else {
            if (cameraShakeFinished > 0) {
                int shakeOffsetX = Rand.getRange(1, 4);
                if (Rand.getRange(0, 1) == 0) {
                    shakeOffsetX *= -1;
                }

                int shakeOffsetY = Rand.getRange(1, 4);
                if (Rand.getRange(0, 1) == 0) {
                    shakeOffsetY *= -1;
                }

                newX += shakeOffsetX;
                newY += shakeOffsetY;

                if (registry.currentTime >= cameraShakeFinished) {
                    cameraShakeFinished = 0;
                }
            }
        }

        newX = newX - (pWidth / 2);
        newY = newY - (pHeight / 2);

        setMapOffsetX(checkMapX(newX, pWidth));
        setMapOffsetY(checkMapY(newY, pHeight));
    }

    public void stunPlayersOnGround(long duration) {
        playerManager.stunPlayersOnGround(duration);
    }

    public boolean isInPlayerView(Point p) {
        return playerManager.isInPlayerView(p);
    }

    public boolean isInFrontOfPlaceable(Rectangle r) {
        return placeableManager.isInFrontOfPlaceable(r);
    }

    public int checkMapX(int x, int objWidth) {
        //make sure we can't go too far to the left
        if (x <= 0) {
            x = 0;
        }

        //make sure we can't go too far to the right
        if (blockManager != null) {
            if (x > (blockManager.getMapWidth() - objWidth)) {
                x = blockManager.getMapWidth() - objWidth;
            }
        }

        return x;
    }

    public int checkMapY(int y, int objHeight) {
        //make sure we can't go too far to the bottom
        if (y <= 0) {
            y = 0;
        }

        //make sure we can't go too far to the top
        if (y > (blockManager.getMapSurfaceMax() + 2000 - objHeight)) {
            y = blockManager.getMapSurfaceMax() + 2000 - objHeight;
        }

        return y;
    }

    public void moveLeft() {
        playerManager.playerMoveLeft();
        xMoveMode = MoveMode.LEFT;
    }

    public void moveRight() {
        playerManager.playerMoveRight();
        xMoveMode = MoveMode.RIGHT;
    }

    public void moveUp() {
        yMoveMode = MoveMode.UP;
    }

    public void moveDown() {
        yMoveMode = MoveMode.DOWN;
    }

    public void stopXMove() {
        playerManager.playerStopMove();
    }

    public void stopYMove() {
        yMoveMode = MoveMode.NONE;
    }

    public void stopGather() {
        playerManager.playerStopGather();
    }

    public void startGather() {
        if (!playerManager.isPlayerMoving() && !playerManager.isPlayerPerformingAction()) {
            if (!playerManager.isPlayerInsideRobot()) {
                String rid = resourceManager.startGather(playerManager.getCurrentPlayer(), playerManager.getCenterPoint(), MAX_RESOURCE_DISTANCE);
                if (!rid.isEmpty()) {
                    playerManager.playerStartGather(rid);
                } else if (placeableManager.startDestroy(playerManager.getCurrentPlayer())) {
                    playerManager.playerStartGather(null);
                } else if (playerManager.playerToggleInsideRobot()) {
                    //we're good!
                }
            } else if (playerManager.playerToggleInsideRobot()) {
                //we're good!
            }
        }
    }

    public void stopJump() {
        playerManager.playerStopJump();
    }

    public void quit() {
        if (networkThread != null) {
            networkThread.close();
        }
        networkThread = null;
        registry.setNetworkThread(null);

        backgroundManager = null;
        blockManager = null;
        registry.setBlockManager(null);
        resourceManager = null;
        registry.setResourceManager(null);
        placeableManager = null;
        registry.setPlaceableManager(null);
        monsterManager = null;
        registry.setMonsterManager(null);
        playerManager = null;
        registry.setPlayerManager(null);
        projectileManager = null;
        registry.setProjectileManager(null);
    }

    public void saveAndQuit() {
        if (networkThread != null) {
            networkThread.close();
        }

        gamePanel.resumeMasterGame();
        isInGame = false;
        networkThread = null;
        registry.setNetworkThread(null);
        hudManager.unloadHUD("Master");
        hudManager.unloadHUD("ArmorInfo");
        hudManager.unloadHUD("WeaponInfo");
        hudManager.unloadHUD("Portrait");
        hudManager.unloadHUD("QuickBar");
        //hudManager.unloadHUD("Version");
        hudManager.unloadHUD("Pause");
        hudManager.unloadHUD("Console");
        isLoading = true;
        resizePanel(800, 600);
        hudManager.loadHUD(HUDManager.HUDType.ScreenLoading);

        Settings.setPlayer(Settings.player, playerManager.getCurrentPlayer());
        if (multiplayerMode != MultiplayerMode.CLIENT) {
            //only save this stuff if acting as a server or playing single player
            Settings.setBlockManager(Settings.player, blockManager);
            Settings.setPlaceableManager(Settings.player, placeableManager);
            Settings.setMonsterManager(Settings.player, monsterManager);
        }
        Settings.save();
        multiplayerMode = MultiplayerMode.NONE;

        backgroundManager = null;
        blockManager = null;
        registry.setBlockManager(null);
        resourceManager = null;
        registry.setResourceManager(null);
        placeableManager = null;
        registry.setPlaceableManager(null);
        monsterManager = null;
        registry.setMonsterManager(null);
        playerManager = null;
        registry.setPlayerManager(null);
        projectileManager = null;
        registry.setProjectileManager(null);

        playerManager = new PlayerManager(this, registry);
        registry.setPlayerManager(playerManager);

        hudManager.unloadHUD("Loading");
        hudManager.loadHUD(HUDManager.HUDType.ScreenMain);
    }

    public void jump() {
        playerManager.playerJump();
    }

    public void toggleMasterHUD() {
        hudManager.toggleMasterHUD();
    }

    public void togglePauseHUD() {
        hudManager.togglePauseHUD();
        gamePanel.toggleMasterPaused();
    }

    public void resumeMasterGame() {
        gamePanel.resumeMasterGame();
    }

    public void pauseMasterGame() {
        gamePanel.resumeGame();
        gamePanel.pauseMasterGame();
    }

    public void toggleContainerHUD(HUD h) {
        hudManager.toggleContainerHUD(h);
    }

    public void keyEnterPressed() {
        hudManager.keyEnterPressed();
    }

    public boolean playerStandingOnTownBlocks() {
        return playerManager.playerStandingOnTownBlocks();
    }

    public void playerRender(Graphics g, int x, int y, boolean imageOverride) {
        if (playerManager != null) {
            playerManager.playerRender(g, x, y, imageOverride);
        }
    }

    public int playerAddItem(String name, int qty) {
        return playerAddItem(0, name, qty);
    }

    public int playerAddItem(Player p, String name, int qty) {
        return playerAddItem(p, 0, name, qty);
    }

    public int playerAddItem(Player p, String name, int qty, int level) {
        return playerAddItem(p, 0, name, qty, level);
    }

    public int playerAddItem(int slot, String name, int qty) {
        return playerManager.playerAddItem(slot, name, qty);
    }

    public int playerAddItem(Player p, int slot, String name, int qty) {
        return playerManager.playerAddItem(p, slot, name, qty);
    }

    public int playerAddItem(int slot, String name, int qty, int level) {
        return playerManager.playerAddItem(slot, name, qty, level);
    }

    public int playerAddItem(Player p, int slot, String name, int qty, int level) {
        return playerManager.playerAddItem(p, slot, name, qty, level);
    }

    public String playerGetInventoryItemCategory(int slot) {
        return playerManager.playerGetInventoryItemCategory(slot);
    }

    public String playerGetInventoryItemName(int slot) {
        return playerManager.playerGetInventoryItemName(slot);
    }

    public int playerGetInventoryQty(int slot) {
        return playerManager.playerGetInventoryQty(slot);
    }

    public int playerGetInventoryLevel(int slot) {
        return playerManager.playerGetInventoryLevel(slot);
    }

    public void playerDeleteInventory(int slot, int qty) {
        playerManager.playerDeleteInventory(slot, qty, false);
    }

    public void playerDeleteInventory(int slot, int qty, boolean giveXP) {
        playerManager.playerDeleteInventory(slot, qty, giveXP);
    }

    public void setPlayerSlotQuantity(int slot, int qty) {
        playerManager.setPlayerSlotQuantity(slot, qty);
    }

    public void setPlayerSelectedItem(int i) {
        playerManager.setPlayerSelectedItem(i);
    }

    public void playerSwapInventory(int from, int to) {
        playerManager.playerSwapInventory(from, to);
    }

    public void playerEquipFromInventory(int slot) {
        playerManager.playerEquipFromInventory(slot);
    }

    public void playerUnEquipToInventory(String equipmentType, int to) {
        playerManager.playerUnEquipToInventory(equipmentType, to);
    }

    public void playerEquipHead(String armorName, int l) {
        playerManager.playerEquipHead(armorName, l);
    }

    public void playerEquipChest(String armorName, int l) {
        playerManager.playerEquipChest(armorName, l);
    }

    public void playerEquipLegs(String armorName, int l) {
        playerManager.playerEquipLegs(armorName, l);
    }

    public void playerEquipFeet(String armorName, int l) {
        playerManager.playerEquipFeet(armorName, l);
    }

    public void robotSetMode(String m) {
        monsterManager.resetAggro();
        playerManager.robotSetMode(m);
    }

    public void robotToggleActivated() {
        playerManager.robotToggleActivated();
    }

    public void robotToggleFollow() {
        playerManager.robotToggleFollow();
    }

    public Placeable loadPlaceable(String n, int x, int y) {
        return placeableManager.loadPlaceable(n, x, y);
    }

    public void cancelPlaceable() {
        placeableManager.cancelPlaceable();
    }

    public Damage getMonsterTouchDamage(Rectangle r) {
        return monsterManager.getMonsterTouchDamage(r);
    }

    public void playerUnEquipToDelete(String equipmentType) {
        playerManager.playerUnEquipToDelete(equipmentType);
    }

    public void playerCraftItem(String itemType) {
        playerManager.playerCraftItem(itemType);
    }

    public void playerDied() {
        Thread thread = new Thread() {

            @Override
            public void run() {
                projectileManager.removeAll();
                monsterManager.removeAllMonsters();
            }
        };
 
        thread.start();

        SoundClip cl = new SoundClip("Misc/GameOver");
    }

    public void updateParallax(int x, int y) {
        if (backgroundManager != null) {
            backgroundManager.updateParallax(x, y);
        }
    }

    public ResourceType getResourceTypeByResourceId(String id) {
        return resourceManager.getResourceTypeByResourceId(id);
    }

    public Resource getResourceById(String id) {
        return resourceManager.getResourceById(id);
    }

    public ArrayList<String> getItemTypeList(String category, String types) {
        return itemManager.getItemTypeList(category, types);
    }

    public ArrayList<String> getItemTypeRequirements(String n) {
        return itemManager.getItemTypeRequirements(n);
    }

    public void monsterAttackPlaceable(Monster source, Rectangle attackRect, int meleeDamage) {
        placeableManager.monsterAttackPlaceable(source, attackRect, meleeDamage);
    }

    public void stopActions() {
        stopActions(playerManager.getCurrentPlayer());
    }

    public void stopActions(Player p) {
        if (p == playerManager.getCurrentPlayer()) {
            if (resourceManager != null) {
                resourceManager.stopGather();
            }
            if (playerManager != null) {
                playerManager.playerStopGather();
            }
            if (placeableManager != null) {
                placeableManager.stopDestroy();
                placeableManager.cancelPlaceable();
            }
        }
    }

    public int[] getTownStartEndUnderPlayer() {
        return playerManager.getTownStartEndUnderPlayer();
    }

    public Point getNearestTownHallXY(Point p) {
        if (placeableManager != null) {
            return placeableManager.getNearestTownHallXY(p);
        } else {
            return new Point(0, 0);
        }
    }

    public int getActivatedCount(String type) {
        return placeableManager.getActivatedCount(type);
    }

    public int[] getCurrentPower() {
        return placeableManager.getPower();
    }

    public void handleClick(Point clickPoint) {
        boolean handled = false;

        if (!hudManager.handleClick(clickPoint)) {
            if (!gamePanel.getIsPaused() && !gamePanel.getIsMasterPaused()) {
                if (monsterManager != null) {
                    handled = monsterManager.handleClick(clickPoint);
                    if (placeableManager != null && !handled) {
                        if (!placeableManager.handleClick(clickPoint)) {
                            playerManager.handleClick(clickPoint);
                        }
                    } else {
                        playerManager.handleClick(clickPoint);
                    }
                }
            }
        }
    }

    public void handleRightClick(Point clickPoint) {
        if (!hudManager.handleRightClick(clickPoint)) {
            if (placeableManager != null) {
                if (!placeableManager.handleRightClick(clickPoint)) {
                    if (playerManager != null) {
                        playerManager.handleRightClick();
                    }
                }
            }
        }
    }

    public void handleReleased(Point clickPoint) {
        if (!hudManager.handleReleased(clickPoint)) {
        }
        if (playerManager != null) {
            playerManager.handleReleased(clickPoint);
        }
    }

    public void handleMouseScroll(int steps) {
        playerManager.getCurrentPlayer().scrollQuickBar(steps);
        if (multiplayerMode != MultiplayerMode.NONE && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(playerManager.getCurrentPlayer().getId());
                up.name = playerManager.getCurrentPlayer().getName();
                up.mapX = playerManager.getCurrentPlayer().getMapX();
                up.mapY = playerManager.getCurrentPlayer().getMapY();
                up.vertMoveMode = playerManager.getCurrentPlayer().getVertMoveMode();
                up.action = "ScrollQuickBar";
                up.dataInt = steps;
                registry.getNetworkThread().sendData(up);
            }
        }
    }

    public void setCursorImageAndText(BufferedImage img, String ct) {
        cursorImage = img;
        cursorText = ct;
    }

    public boolean doesRectContainBlocks(int mapX, int mapY, int width, int height) {
        if (blockManager != null) {
            return blockManager.doesRectContainBlocks(mapX, mapY, width, height);
        } else {
            return false;
        }
    }

    public ArrayList<String> attackDamageAndKnockBack(Actor source, Arc2D.Double arc, Point mapPoint, int damage, int knockBackX, int knockBackY, int maxHits, int playerIndex, String weaponType) {
        if (source != null) {
            projectileManager.weaponSwing(arc, source.getCenterPoint());
        }
        return monsterManager.attackDamageAndKnockBack(source, arc, mapPoint, damage, knockBackX, knockBackY, maxHits, weaponType);
    }

    public void renderCursorImage(Graphics g) {
        if (cursorImage != null) {
            int textX = 30 + (1 - cursorText.length()) * 8;
            BufferedImage bimg = new BufferedImage(40, 40, BufferedImage.TRANSLUCENT);
            bimg.createGraphics().drawImage(cursorImage, 0, 0, null);
            Graphics gCursor = bimg.getGraphics();
            Font textFont = new Font("SansSerif", Font.BOLD, 12);
            gCursor.setFont(textFont);
            gCursor.drawString(cursorText, textX, 35);
            g.drawImage(bimg, currentMousePosition.x, currentMousePosition.y, null);
        }
    }

    public void restoreCursor() {
        cursorImage = null;
    }

    public void updateMusic() {
        if (playerManager != null) {
            if (registry.getBossFight()) {
                if (currentPlayList != bossPlayList) {
                    currentPlayList.stop();
                    currentPlayList = bossPlayList;
                    currentPlayList.start();
                    currentPlayList.playSongNow("BossIntro", 9.0f, true);
                }
            } else {
                Point p = playerManager.getCenterPoint();
                if (p.y > 0) {
                    int level = blockManager.getLevelByY(p.y);
                    if (level > 1 && currentPlayList != belowGroundPlayList) {
                        currentPlayList.stop();
                        currentPlayList = belowGroundPlayList;
                        currentPlayList.start();
                    } else if (level == 0) {
                        boolean inRobot = playerManager.isPlayerInsideRobot();
                        if (inRobot && currentPlayList != robotPlayList) {
                            currentPlayList.stop();
                            currentPlayList = robotPlayList;
                            currentPlayList.start();
                        } else if (!inRobot && currentPlayList != inventorPlayList) {
                            currentPlayList.stop();
                            currentPlayList = inventorPlayList;
                            currentPlayList.start();
                        }
                    }
                }
            }
        }
    }

    public void showLevelUpGraphic() {
        levelUpGraphicDirection = 2;
        levelUpGraphicPosition = -340;
    }

    public void update() {
        if (gameError == null) {
            registry.currentTime = System.currentTimeMillis();
            registry.setStatusText("");

            int oldX = mapOffsetX;
            int oldY = mapOffsetY;

            if (!gamePanel.getIsPaused() && !gamePanel.getIsMasterPaused()) {
                if (backgroundManager != null && !isLoading) {
                    backgroundManager.update();
                }
                if (blockManager != null && !isLoading) {
                    blockManager.update();
                    updateMusic();
                }
                if (resourceManager != null && !isLoading) {
                    resourceManager.update();
                }
                if (placeableManager != null && !isLoading) {
                    placeableManager.update();
                }
                if (monsterManager != null && !isLoading) {
                    hudManager.showPortrait(false);
                    monsterManager.update();
                }
                if (pixelizeManager != null && !isLoading) {
                    pixelizeManager.update();
                }
                if (playerManager != null && !isLoading) {
                    playerManager.update();
                }
                if (projectileManager != null && !isLoading) {
                    projectileManager.update();
                }
                /*
                 * if (pe != null && !isLoading) { pe.update(); }
                 */
                if (indicatorManager != null && !isLoading) {
                    indicatorManager.update();
                }
            }
            if (hudManager != null) {
                hudManager.showWeaponHUD(false);
                hudManager.showArmorHUD(false);
                hudManager.update();
            }

            this.updateParallax(mapOffsetX - oldX, mapOffsetY - oldY);

            if (levelUpGraphicDirection == 1) {
                //left
                levelUpGraphicPosition -= 8;
                if (levelUpGraphicPosition < -340) {
                    levelUpGraphicPosition = 340;
                    levelUpGraphicDirection = 0;
                }
            } else if (levelUpGraphicDirection == 2) {
                //right
                levelUpGraphicPosition += 8;
                if (levelUpGraphicPosition > 500) {
                    levelUpGraphicPosition = 0;
                    levelUpGraphicDirection = 1;
                }
            }

            if (registry.currentTime >= nextAutoSave && isInGame) {
                if (nextAutoSave > 0) {
                    new Thread() {

                        public void run() {
                            try {
                                showMessage("Success", "Game Auto-saved...");
                                Settings.setPlayer(Settings.player, playerManager.getCurrentPlayer());
                                if (multiplayerMode != MultiplayerMode.CLIENT) {
                                    Settings.setBlockManager(Settings.player, blockManager);
                                    Settings.setPlaceableManager(Settings.player, placeableManager);
                                }
                                Settings.save();
                            } catch (Exception e) {
                                throw new RuntimeException(e.getMessage());
                            }
                        }
                    }.start();
                }
                nextAutoSave = registry.currentTime + Rand.getRange(5 * 60 * 1000, 10 * 60 * 1000);
            }

            //show a message
            if (showMessage) {
                long p = registry.getImageLoader().getPeriod();
                messageTotalTime = (messageTotalTime
                        + registry.getImageLoader().getPeriod())
                        % (long) (1000 * MESSAGE_MAX_TIME * 2);

                if ((messageTotalTime / (MESSAGE_MAX_TIME * 1000)) > 1) {
                    messageTotalTime = 0;
                    showMessage = false;
                }
            }
        }
    }

    public void render(Graphics g) {
        if (gameError != null) {
            g.fillRect(0, 0, pWidth, pHeight);
            g.setFont(new Font("SansSerif", Font.BOLD, 18));

            String[] parts = gameError.split("\n");
            for (int i = 0; i < parts.length; i++) {
                //center the text
                FontMetrics fm = g.getFontMetrics();
                int messageWidth = fm.stringWidth(parts[i]);
                int messageAscent = fm.getMaxAscent();
                int messageDescent = fm.getMaxDescent();
                int messageX = (pWidth / 2) - (messageWidth / 2);
                int messageY = (pHeight / 2) - (messageDescent / 2) + (messageAscent / 2) + (i * 25);

                registry.ghettoOutline(g, Color.BLACK, parts[i], messageX, messageY - 100);

                g.setColor(Color.white);

                g.drawString(parts[i], messageX, messageY - 100);
            }
        } else {
            if (backgroundManager != null && !isLoading) {
                backgroundManager.render(g);
            }
            if (blockManager != null && !isLoading) {
                blockManager.render(g);
            }
            if (placeableManager != null && !isLoading) {
                placeableManager.render(g);
            }
            if (resourceManager != null && !isLoading) {
                resourceManager.render(g);
            }
            if (monsterManager != null && !isLoading) {
                monsterManager.render(g);
            }
            if (pixelizeManager != null && !isLoading) {
                pixelizeManager.render(g);
            }
            if (playerManager != null && !isLoading) {
                playerManager.render(g);
            }
            if (projectileManager != null && !isLoading) {
                projectileManager.render(g);
            }
            /*
             * if (pe != null && !isLoading) { pe.render(g); }
             */
            if (indicatorManager != null && !isLoading) {
                indicatorManager.render(g);
            }
            if (hudManager != null) {
                hudManager.render(g);
            }

            //low life indicator
            if (playerManager != null && !isLoading) {
                if (playerManager.getCurrentPlayer().getHitPointPercentage() < 15) {
                    BufferedImage im = registry.getImageLoader().getImage("Misc/Danger");
                    if (im != null) {
                        g.drawImage(im, 0, 0, null);
                    }
                }
            }

            if (levelUpGraphicDirection != 0) {
                BufferedImage im = registry.getImageLoader().getImage("Misc/LargeInventor");
                if (levelUpGraphicPosition > 0) {
                    g.drawImage(im, 0, pHeight - 481, null);
                } else {
                    g.drawImage(im, levelUpGraphicPosition, pHeight - 481, null);
                }
            }

            if (gamePanel.getIsPaused()) {
                g.setFont(new Font("SansSerif", Font.BOLD, 20));

                String pausedString = "- Paused -";

                //center the text
                FontMetrics fm = g.getFontMetrics();
                int messageWidth = fm.stringWidth(pausedString);
                int messageAscent = fm.getMaxAscent();
                int messageDescent = fm.getMaxDescent();
                int messageX = (pWidth / 2) - (messageWidth / 2);
                int messageY = (pHeight / 2) - (messageDescent / 2) + (messageAscent / 2);

                registry.ghettoOutline(g, Color.BLACK, pausedString, messageX, messageY - 100);

                g.setColor(Color.white);
                g.drawString(pausedString, messageX, messageY - 100);
            } else if (showMessage && !message.isEmpty()) {
                g.setFont(new Font("SansSerif", Font.BOLD, 20));

                //center the text
                FontMetrics fm = g.getFontMetrics();
                int messageWidth = fm.stringWidth(message);
                int messageAscent = fm.getMaxAscent();
                int messageDescent = fm.getMaxDescent();
                int messageX = (pWidth / 2) - (messageWidth / 2);
                int messageY = (pHeight / 2) - (messageDescent / 2) + (messageAscent / 2);

                registry.ghettoOutline(g, Color.BLACK, message, messageX, messageY - 50);

                if (messageType.equals("Success")) {
                    g.setColor(Color.white);
                } else if (messageType.equals("Error")) {
                    g.setColor(Color.red);
                }

                g.drawString(message, messageX, messageY - 50);
            }

            renderCursorImage(g);
        }
    }

    public Point getCurrentMousePosition() {
        return currentMousePosition;
    }

    public void setCurrentMousePosition(Point p) {
        currentMousePosition = p;
    }

    public boolean currentlyPlacing() {
        return placeableManager.currentlyPlacing();
    }

    public void shiftPressed() {
        hudManager.shiftPressed();
    }

    public void shiftRelease() {
        hudManager.shiftRelease();
    }

    public void numPressed(int i) {
        if (i == 0) {
            i = 10;
        }
        playerManager.setPlayerSelectedItem(i - 1);
    }

    public void processConsoleCommand(String c) {
        int qty = 0;

        String[] parts = c.split(" ");

        if (networkMode) {
            if (networkThread == null) {
                setNetworkMode(false);
            }

            if (parts[0].toLowerCase().equals("spawn")) {
                /*
                 * Spawns monsters - Syntax: spawn <monster type> spawn <monster
                 * type> <qty>
                 */
                if (parts.length < 2) {
                    showMessage("Error", "Wrong number of arguments for (" + parts[0] + ")");
                }

                if (parts.length == 3) {
                    qty = Integer.parseInt(parts[2]);
                } else {
                    qty = 1;
                }
                int x = playerManager.getCurrentPlayer().getMapX();
                int y = playerManager.getCurrentPlayer().getMapY();
                for (int i = 0; i < qty; i++) {
                    monsterManager.spawn(parts[1], "Roaming", x, y);
                }
            } else {
                if (!networkThread.sendData(c)) {
                    setNetworkMode(false);
                    showMessage("Error", "Couldn't send message...");
                }
            }


            return;
        } else if (parts[0].toLowerCase().equals("spawn")) {
            /*
             * Spawns monsters - Syntax: spawn <monster type> spawn <monster
             * type> <qty>
             */
            if (parts.length < 2) {
                showMessage("Error", "Wrong number of arguments for (" + parts[0] + ")");
            }

            if (parts.length == 3) {
                qty = Integer.parseInt(parts[2]);
            } else {
                qty = 1;
            }
            int x = playerManager.getCurrentPlayer().getMapX();
            int y = playerManager.getCurrentPlayer().getMapY();
            for (int i = 0; i < qty; i++) {
                monsterManager.spawn(parts[1], "Roaming", x, y);
            }
            return;
        } else if (parts[0].toLowerCase().equals("invadd")) {
            /*
             * Adds item to player inventory - Syntax: invadd <item type> invadd
             * <item type> <qty>
             */
            if (parts.length < 2) {
                showMessage("Error", "Wrong number of arguments for (" + parts[0] + ")");
            }
            if (parts[1].equals("stuff")) {
                addStuff();
            } else {
                if (parts.length == 3) {
                    qty = Integer.parseInt(parts[2]);
                } else {
                    qty = 1;
                }
                if (playerAddItem(parts[1], qty) == 0) {
                    showMessage("Error", "Couldn't add Item (" + parts[1] + ")");
                }
            }
            return;
        } else if (parts[0].toLowerCase().equals("equip")) {
            /*
             * Adds item to player inventory - Syntax: invadd <item type> invadd
             * <item type> <qty>
             */
            if (parts.length != 2) {
                showMessage("Error", "Wrong number of arguments for (" + parts[0] + ")");
            }

            qty = 1;

            playerDeleteInventory(40, 0);
            playerAddItem(40, parts[1], 1);
            return;
        } else if (parts[0].toLowerCase().equals("addxp")) {
            if (parts.length != 2) {
                showMessage("Error", "Wrong number of arguments for (" + parts[0] + ")");
            }

            playerManager.getCurrentPlayer().addXP(Integer.parseInt(parts[1]));
            return;
        } else if (parts[0].toLowerCase().equals("outlines")) {
            /*
             * Toggles actor outlines - Syntax: outlines
             */
            if (playerManager.getCurrentPlayer().getShowRect()) {
                playerManager.playerShowRect(false);
                monsterManager.showRects(false);
            } else {
                playerManager.playerShowRect(true);
                monsterManager.showRects(true);
            }
            return;
        } else if (parts[0].toLowerCase().equals("settcp")) {
            townCameraPosition.x = playerManager.getCurrentPlayer().getMapX();
            townCameraPosition.y = playerManager.getCurrentPlayer().getMapY();
            showMessage("Error", "set");
            return;
        } else if (parts[0].toLowerCase().equals("goals")) {
            if (monsterManager.getShowGoals()) {
                monsterManager.showGoals(false);
            } else {
                monsterManager.showGoals(true);
            }
            showMessage("Error", "done");
            return;
        } else if (parts[0].toLowerCase().equals("stun")) {
            this.stunPlayersOnGround(5000);
            return;
        } else if (parts[0].toLowerCase().equals("kb") && parts.length == 2) {
            String[] xy = parts[1].split("x");
            if (xy.length == 2) {
                playerManager.getCurrentPlayer().applyKnockBack(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
            }
            return;
        } else if (parts[0].toLowerCase().equals("mvol") && parts.length == 2) {
            inventorPlayList.setVolume(Integer.parseInt(parts[1]));
            robotPlayList.setVolume(Integer.parseInt(parts[1]));
            bossPlayList.setVolume(Integer.parseInt(parts[1]));
            belowGroundPlayList.setVolume(Integer.parseInt(parts[1]));

            return;
        }

        showMessage("Error", "Command (" + parts[0] + ") Not Understood");
    }

    public boolean isConsoleOpen() {
        return hudManager.isConsoleOpen();
    }

    public boolean isKeysOpen() {
        return hudManager.isKeysOpen();
    }

    public boolean isNewCharacterOpen() {
        return hudManager.isNewCharacterOpen();
    }

    public boolean isMultiPlayerJoinOpen() {
        return hudManager.isMultiPlayerJoinOpen();
    }

    public boolean isMultiPlayerHostOpen() {
        return hudManager.isMultiPlayerHostOpen();
    }

    public boolean isKeyDown(int k) {
        return gamePanel.getKey(k);
    }

    public void setNetworkMode(boolean m) {
        networkMode = m;
        /*
         * server needs to keep running at least... revisit this later if
         * (!networkMode) { if (networkThread != null) {
         * networkThread.keepRunning = false; networkThread = null;
         * registry.setNetworkThread(null); } }
         */
    }

    public void consoleKey(int k, Character c) {
        hudManager.consoleKey(k, c);
    }

    public void settingsKey(int k, Character c) {
        hudManager.settingsKey(k, c);
    }

    public void newCharacterKey(int k, Character c) {
        hudManager.newCharacterKey(k, c);
    }

    public void multiPlayerJoinKey(int k, Character c) {
        hudManager.multiPlayerJoinKey(k, c);
    }

    public void multiPlayerHostKey(int k, Character c) {
        hudManager.multiPlayerHostKey(k, c);
    }

    public void newCharacterKey(int k, Character c, boolean tab) {
        hudManager.newCharacterKey(k, c, true);
    }

    public void multiPlayerJoinKey(int k, Character c, boolean tab) {
        hudManager.multiPlayerJoinKey(k, c, true);
    }

    public void multiPlayerHostKey(int k, Character c, boolean tab) {
        hudManager.multiPlayerHostKey(k, c, true);
    }
}