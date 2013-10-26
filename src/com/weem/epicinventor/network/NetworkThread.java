package com.weem.epicinventor.network;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.actor.monster.*;
import com.weem.epicinventor.hud.*;
import com.weem.epicinventor.placeable.*;
import com.weem.epicinventor.resource.*;
import com.weem.epicinventor.utility.*;
import com.weem.epicinventor.world.block.*;

import java.io.*;
import java.net.*;

public class NetworkThread extends Thread {

    private Registry registry;
    private GameController gameController;
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private String ip;
    private int port;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    public boolean keepRunning = true;
    public boolean readyForUpdates = false;

    public NetworkThread(Registry r, GameController gc, String i, int p) {
        registry = r;
        gameController = gc;
        ip = i;
        port = p;
    }

    @Override
    public void run() // read server messages and act on them.
    {
        if (ip.isEmpty()) {
            doServer();
        } else {
            doClient();
        }

        return;
    }

    public void close() {
        EIError.debugMsg("Closing");
        if (serverSocket != null) {
            try {
                sendData("goodbye");
                serverSocket.close();
            } catch (IOException e) {
                //oh well, ain't no thang
            }
        }
        keepRunning = false;
        serverSocket = null;
    }

    private void listen() {
        EIError.debugMsg("Trying to listen...");
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                //oh well, ain't no thang
            }
        }
        serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            gameController.showMessage("Error", "Couldn't listen on port " + port);
            return;
        }
        EIError.debugMsg("Listening");

        gameController.showMessage("Success", "Waiting for incoming connection...");
        gameController.multiplayerMode = gameController.multiplayerMode.SERVER;

        EIError.debugMsg("Waiting for Connection...");
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            gameController.showMessage("Error", "Couldn't accept connection...");
            return;
        }
        EIError.debugMsg("Connected");

        gameController.setNetworkMode(true);
        gameController.showMessage("Success", "Network Connection Established");
    }

    private void doServer() {
        Object data;
        String playerId = "";

        listen();

        if (clientSocket != null) {
            if (clientSocket.isConnected()) {
                try {
                    EIError.debugMsg("Creating output stream");

                    output = new ObjectOutputStream(clientSocket.getOutputStream());

                    EIError.debugMsg("Creating input stream");
                    input = new ObjectInputStream(clientSocket.getInputStream());

                    EIError.debugMsg("IO Created");

                    while (keepRunning && clientSocket.isConnected()) {
                        data = input.readObject();
                        EIError.debugMsg("Received data: " + data.toString());

                        if (data.getClass().equals(String.class)) {
                            data = (String) data;
                            if (data.toString().equals("goodbye")) {
                                clientSocket.close();
                                if (serverSocket != null) {
                                    serverSocket.close();
                                }

                                keepRunning = false;
                            } else if (data.toString().equals("send block manager")) {
                                EIError.debugMsg("Sending Block Manager Data...");
                                BlockManager bm = registry.getBlockManager();
                                sendData(bm);
                                bm = null;
                                EIError.debugMsg("Block Manager Data Sent");
                            } else if (data.toString().equals("send placable manager")) {
                                EIError.debugMsg("Sending Placeable Manager Data...");
                                PlaceableManager pm = registry.getPlaceableManager();
                                sendData(pm);
                                pm = null;
                                EIError.debugMsg("Placeable Manager Data Sent");
                            } else if (data.toString().equals("send resource manager")) {
                                EIError.debugMsg("Sending Resource Manager Data...");
                                ResourceManager rm = registry.getResourceManager();
                                sendData(rm);
                                rm = null;
                                EIError.debugMsg("Resource Manager Data Sent");
                            } else if (data.toString().equals("send monster manager")) {
                                EIError.debugMsg("Sending Monster Manager Data...");
                                MonsterManager mm = registry.getMonsterManager();
                                sendData(mm);
                                mm = null;
                                EIError.debugMsg("Monster Manager Data Sent");
                            } else if (data.toString().equals("send player")) {
                                EIError.debugMsg("Sending Player Data...");
                                sendData(registry.getPlayerManager().getCurrentPlayer());
                                EIError.debugMsg("Player Data Sent");
                            } else if (data.toString().substring(0, 6).equals("place ")) {
                                String parts[] = data.toString().split(" ");
                                if (parts.length == 4) {
                                    EIError.debugMsg("Adding placeable...");
                                    registry.getPlaceableManager().loadPlaceable(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Placeable.State.Placed);
                                }
                            } else if (data.toString().substring(0, 18).equals("send monster data:")) {
                                String id = data.toString().substring(19);
                                Monster monster = registry.getMonsterManager().getMonsterById(id);
                                if (monster != null) {
                                    EIError.debugMsg("Sending Monster Data (" + monster.getId() + ")...");
                                    sendData(monster);
                                    EIError.debugMsg("Monster Data Sent");
                                }
                            } else if (data.toString().substring(0, 20).equals("send placeable data:")) {
                                String id = data.toString().substring(21);
                                Placeable placeable = registry.getPlaceableManager().getPlaceableById(id);
                                if (placeable != null) {
                                    EIError.debugMsg("Sending Placeable Data (" + placeable.getId() + ")...");
                                    sendData(placeable);
                                    EIError.debugMsg("Placeable Data Sent");
                                }
                            } else {
                                if (!data.toString().isEmpty()) {
                                    gameController.showMessage("Success", "Message: " + data);
                                }
                            }
                        } else if (data.getClass().equals(Player.class)) {
                            EIError.debugMsg("Adding Player...");
                            Player p = (Player) data;
                            p.setTransient(registry);
                            registry.getPlayerManager().registerPlayer(p);
                            playerId = p.getId();
                            EIError.debugMsg("Player Added");
                            readyForUpdates = true;
                        } else if (data.getClass().equals(UpdatePlayer.class)) {
                            UpdatePlayer up = (UpdatePlayer) data;
                            EIError.debugMsg("Updating Player (" + up.id + ")...");
                            registry.getPlayerManager().processPlayerUpdate(up);
                            EIError.debugMsg("Player Updated");
                        } else if (data.getClass().equals(UpdatePlaceable.class)) {
                            UpdatePlaceable up = (UpdatePlaceable) data;
                            EIError.debugMsg("Updating Placeable (" + up.id + ")...");
                            registry.getPlaceableManager().processPlaceableUpdate(up);
                            EIError.debugMsg("Placeable Updated");
                        }
                        data = null;
                    }
                } catch (IOException e) {
                    EIError.debugMsg("Network IO Error: " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    EIError.debugMsg("Network Class Error: " + e.getMessage());
                }
            }
        }

        readyForUpdates = false;

        gameController.setNetworkMode(false);
        gameController.multiplayerMode = gameController.multiplayerMode.NONE;

        PlayerManager pm = registry.getPlayerManager();
        if (pm != null) {
            pm.removePlayer(playerId);
        }

        //restart the server
        if (keepRunning) {
            this.doServer();
        }
    }

    private void doClient() {
        Object data;

        gameController.setLoading(true);
        registry.getHUDManager().loadHUD(HUDManager.HUDType.ScreenLoading);

        try {
            clientSocket = new Socket(ip, port);
        } catch (IOException e) {
            gameController.setLoading(false);
            registry.getHUDManager().unloadHUD("ScreenLoading");
            gameController.showMessage("Error", "Couldn't connect to " + ip + ":" + port);
            return;
        }

        gameController.setNetworkMode(true);
        gameController.multiplayerMode = gameController.multiplayerMode.CLIENT;

        try {
            Game.loadingText = "Establishing Connection";
            EIError.debugMsg("Creating output stream");

            output = new ObjectOutputStream(clientSocket.getOutputStream());

            EIError.debugMsg("Creating input stream");
            input = new ObjectInputStream(clientSocket.getInputStream());

            EIError.debugMsg("IO Created");

            output.writeObject("send block manager");
            Game.loadingText = "Getting World Data...";

            while (keepRunning && clientSocket.isConnected()) {
                data = input.readObject();
                EIError.debugMsg("Received data: " + data.toString());

                if (data.getClass().equals(String.class)) {
                    data = (String) data;
                    if (data.toString().equals("goodbye")) {
                        clientSocket.close();
                        if (serverSocket != null) {
                            serverSocket.close();
                        }

                        keepRunning = false;
                    } else {
                        if (!data.toString().isEmpty()) {
                            gameController.showMessage("Success", "Message: " + data);
                        }
                    }
                } else if (data.getClass().equals(BlockManager.class)) {
                    EIError.debugMsg("Setting Block Manager...");
                    BlockManager bm = (BlockManager) data;
                    bm.setTransient(registry);
                    gameController.setBlockManager(bm);
                    bm = null;
                    EIError.debugMsg("Block Manager set");
                    sendData("send placable manager");
                    Game.loadingText = "Getting Building Data...";
                } else if (data.getClass().equals(PlaceableManager.class)) {
                    EIError.debugMsg("Setting Placeable Manager...");
                    PlaceableManager pm = (PlaceableManager) data;
                    pm.setTransient(registry);
                    gameController.setPlaceableManager(pm);
                    pm = null;
                    registry.getPlayerManager().getCurrentPlayer().init();
                    EIError.debugMsg("Placeable Manager set");
                    sendData("send resource manager");
                    Game.loadingText = "Making Shinies...";
                } else if (data.getClass().equals(ResourceManager.class)) {
                    EIError.debugMsg("Setting Resource Manager...");
                    ResourceManager rm = (ResourceManager) data;
                    rm.setTransient(registry);
                    gameController.setResourceManager(rm);
                    rm = null;
                    EIError.debugMsg("Resource Manager set");
                    sendData("send monster manager");
                    Game.loadingText = "Spawning Evil Bad Guys";
                } else if (data.getClass().equals(MonsterManager.class)) {
                    EIError.debugMsg("Setting Monster Manager...");
                    MonsterManager mm = (MonsterManager) data;
                    mm.setTransient(registry);
                    gameController.setMonsterManager(mm);
                    mm = null;
                    EIError.debugMsg("Monster Manager set");
                    sendData("send player");
                    Game.loadingText = "Initializing Inventors";
                } else if (data.getClass().equals(Player.class)) {
                    EIError.debugMsg("Adding Player...");
                    Player p = (Player) data;
                    p.setTransient(registry);
                    registry.getPlayerManager().registerPlayer(p);
                    EIError.debugMsg("Player Added");

                    EIError.debugMsg("Sending Player Data...");
                    sendData(registry.getPlayerManager().getCurrentPlayer());
                    EIError.debugMsg("Player Data Sent");
                    
                    Game.loadingText = "Here we go!";

                    gameController.setLoading(false);
                    registry.getHUDManager().unloadHUD("ScreenLoading");
                    registry.getBlockManager().updateResolution();
                    gameController.setIsInGame(true);
                    readyForUpdates = true;
                } else if (data.getClass().equals(Resource.class)) {
                    Resource r = (Resource) data;
                    EIError.debugMsg("Adding Resource (" + r.getId() + ")...");
                    r.setTransient(registry, registry.getResourceManager());
                    registry.getResourceManager().registerResource(r);
                    EIError.debugMsg("Resource Added");
                } else if (data.getClass().equals(AggressiveSnake.class)) {
                    AggressiveSnake m = (AggressiveSnake) data;
                    m.setTransient(registry, registry.getMonsterManager());
                    registry.getMonsterManager().registerMonster(m);
                } else if (data.getClass().equals(BossOrc.class)) {
                    BossOrc m = (BossOrc) data;
                    m.setTransient(registry, registry.getMonsterManager());
                    registry.getMonsterManager().registerMonster(m);
                } else if (data.getClass().equals(LionFly.class)) {
                    LionFly m = (LionFly) data;
                    m.setTransient(registry, registry.getMonsterManager());
                    registry.getMonsterManager().registerMonster(m);
                } else if (data.getClass().equals(Orc.class)) {
                    Orc m = (Orc) data;
                    m.setTransient(registry, registry.getMonsterManager());
                    registry.getMonsterManager().registerMonster(m);
                } else if (data.getClass().equals(Pig.class)) {
                    Pig m = (Pig) data;
                    m.setTransient(registry, registry.getMonsterManager());
                    registry.getMonsterManager().registerMonster(m);
                } else if (data.getClass().equals(Porcupine.class)) {
                    Porcupine m = (Porcupine) data;
                    m.setTransient(registry, registry.getMonsterManager());
                    registry.getMonsterManager().registerMonster(m);
                } else if (data.getClass().equals(RedOrc.class)) {
                    RedOrc m = (RedOrc) data;
                    m.setTransient(registry, registry.getMonsterManager());
                    registry.getMonsterManager().registerMonster(m);
                } else if (data.getClass().equals(RockMonster.class)) {
                    RockMonster m = (RockMonster) data;
                    m.setTransient(registry, registry.getMonsterManager());
                    registry.getMonsterManager().registerMonster(m);
                } else if (data.getClass().equals(Snail.class)) {
                    Snail m = (Snail) data;
                    m.setTransient(registry, registry.getMonsterManager());
                    registry.getMonsterManager().registerMonster(m);
                } else if (data.getClass().equals(Snake.class)) {
                    Snake m = (Snake) data;
                    m.setTransient(registry, registry.getMonsterManager());
                    registry.getMonsterManager().registerMonster(m);
                } else if (data.getClass().equals(SpiderWorm.class)) {
                    SpiderWorm m = (SpiderWorm) data;
                    m.setTransient(registry, registry.getMonsterManager());
                    registry.getMonsterManager().registerMonster(m);
                } else if (data.getClass().equals(ZombieWalrus.class)) {
                    ZombieWalrus m = (ZombieWalrus) data;
                    m.setTransient(registry, registry.getMonsterManager());
                    registry.getMonsterManager().registerMonster(m);
                } else if (data.getClass().equals(AutoXBow.class)) {
                    AutoXBow p = (AutoXBow) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(BookShelf.class)) {
                    BookShelf p = (BookShelf) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(BladeTrap.class)) {
                    BladeTrap p = (BladeTrap) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(Box.class)) {
                    Box p = (Box) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(Chest.class)) {
                    Chest p = (Chest) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(CopperMine.class)) {
                    CopperMine p = (CopperMine) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(Crate.class)) {
                    Crate p = (Crate) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(EmeraldTeleporter.class)) {
                    EmeraldTeleporter p = (EmeraldTeleporter) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(Forge.class)) {
                    Forge p = (Forge) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(GoldMine.class)) {
                    GoldMine p = (GoldMine) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(IronMine.class)) {
                    IronMine p = (IronMine) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(ItemContainer.class)) {
                    ItemContainer p = (ItemContainer) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(LargeFarm.class)) {
                    LargeFarm p = (LargeFarm) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(LargeSafe.class)) {
                    LargeSafe p = (LargeSafe) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(LionFlyStatue.class)) {
                    LionFlyStatue p = (LionFlyStatue) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(MedicKit.class)) {
                    MedicKit p = (MedicKit) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(MediumSafe.class)) {
                    MediumSafe p = (MediumSafe) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(Pasture.class)) {
                    Pasture p = (Pasture) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(PlatinumMine.class)) {
                    PlatinumMine p = (PlatinumMine) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(PottedFlower.class)) {
                    PottedFlower p = (PottedFlower) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(RubyTeleporter.class)) {
                    RubyTeleporter p = (RubyTeleporter) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(SapphireTeleporter.class)) {
                    SapphireTeleporter p = (SapphireTeleporter) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(SawMill.class)) {
                    SawMill p = (SawMill) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(ScareCrow.class)) {
                    ScareCrow p = (ScareCrow) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(SilverMine.class)) {
                    SilverMine p = (SilverMine) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(SmallFarm.class)) {
                    SmallFarm p = (SmallFarm) data;
                    p.setTransient(registry);
                } else if (data.getClass().equals(SmallSafe.class)) {
                    SmallSafe p = (SmallSafe) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(SteamEngine.class)) {
                    SteamEngine p = (SteamEngine) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(StoneMine.class)) {
                    StoneMine p = (StoneMine) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(ThornTrap.class)) {
                    ThornTrap p = (ThornTrap) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(TownBlock.class)) {
                    TownBlock p = (TownBlock) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(Building.class)) {
                    Building p = (Building) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(WeaponRack.class)) {
                    WeaponRack p = (WeaponRack) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(WindMill.class)) {
                    WindMill p = (WindMill) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(WorkBench.class)) {
                    WorkBench p = (WorkBench) data;
                    p.setTransient(registry);
                    registry.getPlaceableManager().registerPlaceable(p);
                } else if (data.getClass().equals(UpdatePlayer.class)) {
                    UpdatePlayer up = (UpdatePlayer) data;
                    EIError.debugMsg("Updating Player (" + up.id + ")...");
                    registry.getPlayerManager().processPlayerUpdate(up);
                    EIError.debugMsg("Player Updated");
                } else if (data.getClass().equals(UpdateMonster.class)) {
                    UpdateMonster um = (UpdateMonster) data;
                    EIError.debugMsg("Updating Monster (" + um.id + ")...");
                    registry.getMonsterManager().processMonsterUpdate(um);
                    EIError.debugMsg("Monster Updated");
                } else if (data.getClass().equals(UpdateRobot.class)) {
                    UpdateRobot ur = (UpdateRobot) data;
                    EIError.debugMsg("Updating Robot (" + ur.id + ")...");
                    registry.getPlayerManager().processRobotUpdate(ur);
                    EIError.debugMsg("Robot Updated");
                } else if (data.getClass().equals(UpdatePlaceable.class)) {
                    UpdatePlaceable up = (UpdatePlaceable) data;
                    EIError.debugMsg("Updating Placeable (" + up.id + ")...");
                    registry.getPlaceableManager().processPlaceableUpdate(up);
                    EIError.debugMsg("Placeable Updated");
                } else if (data.getClass().equals(UpdateResource.class)) {
                    UpdateResource ur = (UpdateResource) data;
                    EIError.debugMsg("Updating Resource (" + ur.id + ")...");
                    registry.getResourceManager().processResourceUpdate(ur);
                    EIError.debugMsg("Resource Updated");
                } else if (data.getClass().equals(UpdateProjectile.class)) {
                    UpdateProjectile up = (UpdateProjectile) data;
                    EIError.debugMsg("Updating Projectile...");
                    registry.getProjectileManager().processProjectileUpdate(up);
                    EIError.debugMsg("Projectile Updated");
                }
                data = null;
            }
        } catch (IOException e) {
            EIError.debugMsg("Network IO Error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            EIError.debugMsg("Network Class Error: " + e.getMessage());
        }

        readyForUpdates = false;

        gameController.setNetworkMode(false);
        gameController.multiplayerMode = gameController.multiplayerMode.NONE;

        gameController.showMessage("Error", "Server went offline");
        gameController.saveAndQuit();

        return;
    }

    public boolean sendData(Object data) {
        if (output == null || !clientSocket.isConnected()) {
            return false;
        }

        synchronized (this) {
            try {
                output.flush();
                output.writeObject(data);
                output.flush();
                output.reset();

                if (data.getClass().equals(String.class)) {
                    if (data.equals("goodbye")) {
                        clientSocket.close();
                        if (serverSocket != null) {
                            serverSocket.close();
                        }

                        keepRunning = false;
                    }
                }
            } catch (IOException e) {
                EIError.debugMsg("Network IO Error - Sending");
            }

            return true;
        }
    }
}
