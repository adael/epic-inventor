package com.weem.epicinventor;

import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.actor.monster.*;
import com.weem.epicinventor.placeable.*;
import com.weem.epicinventor.world.block.*;
import com.weem.epicinventor.item.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.awt.event.*;

public class Settings {

    public static int resolution;
    public static int volumeMusic;
    public static int volumeFX;
    public static int player = -1;
    public static int buttonMoveRight;
    public static int buttonMoveLeft;
    public static int buttonJump;
    public static int buttonAction;
    public static int buttonRobot;
    public static int buttonInventory;
    public static int buttonPause;
    private static ArrayList resolutions;
    private static ArrayList<Player> players;
    private static ArrayList<BlockManager> blockManagers;
    private static ArrayList<PlaceableManager> placeableManagers;
    private static ArrayList<MonsterManager> monsterManagers;
    private static int NUMBER_OF_PLAYER_SLOTS = 4;

    public Settings() {
    }

    public static void init(GameController gc, Registry r) {
        int version;

        resolutions = new ArrayList();
        resolutions.add("800x600");
        resolutions.add("1024x768");
        resolutions.add("1152x864");
        resolutions.add("1280x720");
        resolutions.add("1280x768");
        resolutions.add("1280x800");
        resolutions.add("1280x960");
        resolutions.add("1280x1024");
        resolutions.add("1360x768");
        resolutions.add("1366x768");
        resolutions.add("1440x900");
        resolutions.add("1600x900");
        resolutions.add("1600x1024");
        resolutions.add("1680x1050");
        resolutions.add("1920x1080");
        resolutions.add("1920x1200");
        //resolutions.add("Full Screen");

        players = new ArrayList<Player>();
        blockManagers = new ArrayList<BlockManager>();
        placeableManagers = new ArrayList<PlaceableManager>();
        monsterManagers = new ArrayList<MonsterManager>();

        //try to load the settings file
        try {
            FileInputStream settingsFile = new FileInputStream("Settings.dat");
            ObjectInputStream settings = new ObjectInputStream(settingsFile);

            version = ((Integer) settings.readObject()).intValue();
            resolution = ((Integer) settings.readObject()).intValue();
            volumeMusic = ((Integer) settings.readObject()).intValue();
            volumeFX = ((Integer) settings.readObject()).intValue();
            buttonMoveRight = ((Integer) settings.readObject()).intValue();
            buttonMoveLeft = ((Integer) settings.readObject()).intValue();
            buttonJump = ((Integer) settings.readObject()).intValue();
            buttonAction = ((Integer) settings.readObject()).intValue();
            buttonRobot = ((Integer) settings.readObject()).intValue();
            buttonInventory = ((Integer) settings.readObject()).intValue();
            buttonPause = ((Integer) settings.readObject()).intValue();

            settings.close();
        } catch (Exception e) {
            EIError.debugMsg(e.getMessage(), EIError.ErrorLevel.Warning);
        }

        if (volumeMusic < -1 || volumeMusic > 10) {
            volumeMusic = 8;
        }
        if (volumeMusic == 0) {
            volumeMusic = 8;
        }
        if (volumeMusic == -1) {
            volumeMusic = 0;
        }

        if (volumeFX < -1 || volumeFX > 10) {
            volumeFX = 8;
        }
        if (volumeFX == 0) {
            volumeFX = 8;
        }
        if (volumeFX == -1) {
            volumeFX = 0;
        }

        if (buttonMoveRight == 0) {
            buttonMoveRight = KeyEvent.VK_D;
        }
        if (buttonMoveLeft == 0) {
            buttonMoveLeft = KeyEvent.VK_A;
        }
        if (buttonJump == 0) {
            buttonJump = KeyEvent.VK_SPACE;
        }
        if (buttonAction == 0) {
            buttonAction = KeyEvent.VK_E;
        }
        if (buttonRobot == 0) {
            buttonRobot = KeyEvent.VK_R;
        }
        if (buttonInventory == 0) {
            buttonInventory = KeyEvent.VK_I;
        }
        if (buttonPause == 0) {
            buttonPause = KeyEvent.VK_P;
        }

        //try to load the players
        for (int i = 1; i <= NUMBER_OF_PLAYER_SLOTS; i++) {
            try {
                FileInputStream playerFile = new FileInputStream("Player" + i + ".dat");
                ObjectInputStream playerInfo = new ObjectInputStream(playerFile);

                version = ((Integer) playerInfo.readObject()).intValue();
                players.add((Player) playerInfo.readObject());
                blockManagers.add((BlockManager) playerInfo.readObject());
                placeableManagers.add((PlaceableManager) playerInfo.readObject());
                
                MonsterManager mm = null;
                if (version >= 2) {
                    mm = (MonsterManager) playerInfo.readObject();
                }
                if(mm == null) {
                    mm = new MonsterManager(gc, r);
                }
                monsterManagers.add(mm);

                playerInfo.close();
                EIError.debugMsg("Added Player " + i);
            } catch (Exception e) {
                if (players.size() >= i) {
                    players.set(i - 1, null);
                } else {
                    players.add(null);
                }
                if (blockManagers.size() >= i) {
                    blockManagers.set(i - 1, null);
                } else {
                    blockManagers.add(null);
                }
                if (placeableManagers.size() >= i) {
                    placeableManagers.set(i - 1, null);
                } else {
                    placeableManagers.add(null);
                }
                if (monsterManagers.size() >= i) {
                    monsterManagers.set(i - 1, null);
                } else {
                    monsterManagers.add(null);
                }
                EIError.debugMsg("Couldn't load Player " + i + " " + e.getMessage(), EIError.ErrorLevel.Error);
            }
        }
    }

    public static void save() {
        resolutions.add("800x600");
        resolutions.add("1024x768");
        resolutions.add("1152x864");
        resolutions.add("1280x720");
        resolutions.add("1280x768");
        resolutions.add("1280x800");
        resolutions.add("1280x960");
        resolutions.add("1280x1024");
        resolutions.add("1360x768");
        resolutions.add("1366x768");
        resolutions.add("1440x900");
        resolutions.add("1600x900");
        resolutions.add("1600x1024");
        resolutions.add("1680x1050");
        resolutions.add("1920x1080");
        resolutions.add("1920x1200");
        //resolutions.add("Full Screen");

        //try to save the settings file
        try {
            FileOutputStream settingsFile = new FileOutputStream("SettingsTemp.dat");
            ObjectOutputStream settings = new ObjectOutputStream(settingsFile);

            settings.writeObject(new Integer(1)); //settings file version
            settings.writeObject(new Integer(resolution));
            if (volumeMusic == 0) {
                settings.writeObject(new Integer(-1));
            } else {
                settings.writeObject(new Integer(volumeMusic));
            }
            if (volumeFX == 0) {
                settings.writeObject(new Integer(-1));
            } else {
                settings.writeObject(new Integer(volumeFX));
            }
            settings.writeObject(new Integer(buttonMoveRight));
            settings.writeObject(new Integer(buttonMoveLeft));
            settings.writeObject(new Integer(buttonJump));
            settings.writeObject(new Integer(buttonAction));
            settings.writeObject(new Integer(buttonRobot));
            settings.writeObject(new Integer(buttonInventory));
            settings.writeObject(new Integer(buttonPause));

            settings.close();

            moveFile("SettingsTemp.dat", "Settings.dat");
            EIError.debugMsg("Saved Settings", EIError.ErrorLevel.Notice);
        } catch (Exception e) {
            EIError.debugMsg("Couldn't save settings " + e.getMessage(), EIError.ErrorLevel.Error);
        }

        //try to save the players
        for (int i = 1; i <= NUMBER_OF_PLAYER_SLOTS; i++) {
            try {
                //if(players.get(i - 1) != null) {
                FileOutputStream playerFile = new FileOutputStream("PlayerTemp.dat");
                ObjectOutputStream playerInfo = new ObjectOutputStream(playerFile);

                playerInfo.writeObject(new Integer(2)); //settings file version
                playerInfo.writeObject(players.get(i - 1));
                playerInfo.writeObject(blockManagers.get(i - 1));
                playerInfo.writeObject(placeableManagers.get(i - 1));
                playerInfo.writeObject(monsterManagers.get(i - 1));

                playerInfo.close();

                moveFile("PlayerTemp.dat", "Player" + i + ".dat");
                EIError.debugMsg("Saved Player " + i, EIError.ErrorLevel.Notice);
                //}
            } catch (Exception e) {
                EIError.debugMsg("Couldn't save Player " + i + " " + e.getMessage(), EIError.ErrorLevel.Error);
            }
        }
    }

    public static Point getResolution(int i) {
        if (i == 16) {
            //full screen
            return new Point(0, 0);
        } else {
            String resString = (String) resolutions.get(i);
            String[] resParts = resString.split("x");
            Point res = null;
            if (resParts.length == 2) {
                res = new Point(Integer.parseInt(resParts[0]), Integer.parseInt(resParts[1]));
            } else {
                res = new Point(800, 600);
            }
            return res;
        }
    }

    public static ArrayList getPlayers() {
        return players;
    }

    public static void setPlayer(int i, Player p) {
        if (players.size() > i) {
            players.set(i, p);
        }
    }

    public static void setBlockManager(int i, BlockManager bm) {
        blockManagers.set(i, bm);
    }

    public static void setPlaceableManager(int i, PlaceableManager pm) {
        placeableManagers.set(i, pm);
    }

    public static void setMonsterManager(int i, MonsterManager mm) {
        monsterManagers.set(i, mm);
    }

    public static void loadPlayer(Registry registry) {
        Player p = (Player) getPlayers().get(player);
        p.setTransient(registry);
        GameController gc = registry.getGameController();
        registry.getPlayerManager().clearPlayers();
        registry.getPlayerManager().registerPlayer(p);
        BlockManager bm = (BlockManager) blockManagers.get(player);
        bm.setTransient(registry);
        gc.setBlockManager(bm);
        PlaceableManager pm = (PlaceableManager) placeableManagers.get(player);
        gc.setPlaceableManager(pm);
        MonsterManager mm = (MonsterManager) monsterManagers.get(player);
        mm.setTransient(registry);
        gc.setMonsterManager(mm);

        p.resetPlayer();

        //unloadUnused();
    }

    public static void unloadUnused() {
        if (player > -1) {
            for (int i = 0; i < NUMBER_OF_PLAYER_SLOTS; i++) {
                if (i != player) {
                    players.set(i, null);
                    blockManagers.set(i, null);
                    placeableManagers.set(i, null);
                    monsterManagers.set(i, null);
                }
            }
        }
    }

    public static ArrayList getPlayerList() {
        ArrayList names = new ArrayList();

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (p != null) {
                names.add(p.getName());
            } else {
                names.add("");
            }
        }
        return names;
    }

    public static void deletePlayer(int i) {
        moveFile("Player" + (i + 1) + ".dat", "Player" + (i + 1) + "Deleted.dat");
    }

    private static void moveFile(String srFile, String dtFile) {
        copyfile(srFile, dtFile);
        deletefile(srFile);
    }

    private static void copyfile(String srFile, String dtFile) {
        try {
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);

            //For Overwrite the file.
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            EIError.debugMsg("File copied " + srFile + " " + dtFile, EIError.ErrorLevel.Notice);
        } catch (Exception e) {
            EIError.debugMsg("Couldn't copy file" + srFile + " " + dtFile + " " + e.getMessage(), EIError.ErrorLevel.Error);
        }
    }

    private static void deletefile(String file) {
        try {
            File f1 = new File(file);
            boolean success = f1.delete();
            if (success) {
                EIError.debugMsg("File deleted " + file, EIError.ErrorLevel.Notice);
            } else {
                EIError.debugMsg("Couldn't delete file " + file, EIError.ErrorLevel.Error);
            }
        } catch (Exception e) {
            EIError.debugMsg("Couldn't delete file " + file + " " + e.getMessage(), EIError.ErrorLevel.Error);
        }
    }
}