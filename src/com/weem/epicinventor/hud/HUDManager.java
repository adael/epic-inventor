package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.placeable.*;

import java.awt.*;
import java.util.ArrayList;
import java.awt.image.*;

public class HUDManager extends Manager {

    private ArrayList<HUD> huds;
    private HUDPortrait portraitHUD = null;
    private HUDWeaponInfo weaponHUD = null;
    private HUDArmorInfo armorHUD = null;

    public enum HUDType {

        AreYouSure,
        ArmorInfo,
        Console,
        Credits,
        Farm,
        LevelUp,
        Master,
        Pause,
        Portrait,
        QuickBar,
        ScreenCharacterSelection,
        ScreenCredits,
        ScreenLoading,
        ScreenKeys,
        ScreenMain,
        ScreenMultiPlayer,
        ScreenMultiPlayerHost,
        ScreenMultiPlayerJoin,
        ScreenNewCharacter,
        ScreenSettings,
        Tutorial,
        Version,
        WeaponInfo
    }

    public HUDManager(GameController gc, Registry rg) {
        super(gc, rg);

        huds = new ArrayList<HUD>();

    }

    public void loadHUD(HUDType whichHUD) {
        HUD hud = null;

        switch (whichHUD) {
            case AreYouSure:
                hud = new HUDAreYouSure(this, registry, (getPWidth() - 760) / 2, 0, 760, 560);
                hud.setName("AreYouSure");
                huds.add(hud);
                break;
            case ArmorInfo:
                armorHUD = new HUDArmorInfo(this, registry, (getPWidth() - 264) / 2, 242, 232, 184);
                armorHUD.setName("ArmorInfo");
                huds.add(armorHUD);
                break;
            case Console:
                hud = new HUDConsole(this, registry, (getPWidth() - 750) / 2, 0, 750, 25);
                hud.setName("Console");
                huds.add(hud);
                break;
            case Credits:
                hud = new HUDCredits(this, registry, (getPWidth() - 800) / 2, 0, 800, 600);
                hud.setName("Credits");
                huds.add(hud);
                break;
            case LevelUp:
                hud = new HUDLevelUp(this, registry, (getPWidth() - 263) / 2, (getPHeight() - 313) / 2, 263, 313);
                hud.setName("LevelUp");
                huds.add(hud);
                break;
            case Master:
                hud = new HUDMaster(this, registry, (getPWidth() - 752) / 2, 5, 752, 422);
                hud.setName("Master");
                huds.add(hud);
                break;
            case Pause:
                hud = new HUDPause(this, registry, (getPWidth() - 250) / 2, (getPHeight() - 411) / 2, 250, 411);
                hud.setName("Pause");
                huds.add(hud);
                break;
            case Portrait:
                portraitHUD = new HUDPortrait(this, registry, (getPWidth() - 752) / 2, 5, 187, 88);
                portraitHUD.setName("Portrait");
                huds.add(portraitHUD);
                break;
            case QuickBar:
                hud = new HUDQuickBar(this, registry, (getPWidth() - 792) / 2, getPHeight() - 149, 792, 149);
                hud.setName("QuickBar");
                huds.add(hud);
                break;
            case ScreenCharacterSelection:
                hud = new HUDScreenCharacterSelection(this, registry, (getPWidth() - 800) / 2, 0, 800, 600);
                hud.setName("ScreenCharacterSelection");
                huds.add(hud);
                break;
            case ScreenCredits:
                hud = new HUDScreenCredits(this, registry, (getPWidth() - 800) / 2, 0, 800, 600);
                hud.setName("ScreenCredits");
                huds.add(hud);
                break;
            case ScreenLoading:
                hud = new HUDScreenLoading(this, registry, 0, 0, getPWidth(), getPHeight());
                hud.setName("ScreenLoading");
                huds.add(hud);
                break;
            case ScreenKeys:
                hud = new HUDScreenKeys(this, registry, (getPWidth() - 800) / 2, 0, 800, 600);
                hud.setName("ScreenKeys");
                huds.add(hud);
                break;
            case ScreenMain:
                hud = new HUDScreenMain(this, registry, (getPWidth() - 800) / 2, 0, 800, 600);
                hud.setName("ScreenMain");
                huds.add(hud);
                break;
            case ScreenMultiPlayer:
                hud = new HUDScreenMultiPlayer(this, registry, (getPWidth() - 800) / 2, 0, 800, 600);
                hud.setName("ScreenMultiPlayer");
                huds.add(hud);
                break;
            case ScreenMultiPlayerHost:
                hud = new HUDScreenMultiPlayerHost(this, registry, (getPWidth() - 800) / 2, 0, 800, 600);
                hud.setName("ScreenMultiPlayerHost");
                huds.add(hud);
                break;
            case ScreenMultiPlayerJoin:
                hud = new HUDScreenMultiPlayerJoin(this, registry, (getPWidth() - 800) / 2, 0, 800, 600);
                hud.setName("ScreenMultiPlayerJoin");
                huds.add(hud);
                break;
            case ScreenNewCharacter:
                hud = new HUDScreenNewCharacter(this, registry, (getPWidth() - 800) / 2, 0, 800, 600);
                hud.setName("ScreenNewCharacter");
                huds.add(hud);
                break;
            case ScreenSettings:
                hud = new HUDScreenSettings(this, registry, (getPWidth() - 800) / 2, 0, 800, 600);
                hud.setName("ScreenSettings");
                huds.add(hud);
                break;
            case Tutorial:
                hud = new HUDTutorial(this, registry, (getPWidth() - 800) / 2, (getPHeight() - 600) / 2, 800, 600);
                hud.setName("Tutorial");
                huds.add(hud);
                break;
            case Version:
                hud = new HUDVersion(this, registry, (getPWidth() - 800) / 2, 0, 800, 30);
                hud.setName("Version");
                huds.add(hud);
                break;
            case WeaponInfo:
                weaponHUD = new HUDWeaponInfo(this, registry, (getPWidth() - 264) / 2, 242, 232, 184);
                weaponHUD.setName("WeaponInfo");
                huds.add(weaponHUD);
                break;
        }
    }

    public void showPortrait(boolean s) {
        if (portraitHUD != null) {
            portraitHUD.setShouldRender(s);
        }
    }

    public void loadPlayer() {
        gameController.loadPlayer();
    }

    public void setNames(String characterName, String robotName) {
        gameController.setPlayerNames(characterName, robotName);
    }

    public void unloadHUD(String n) {
        HUD hud = null;

        //start from the top and work our way down to "layer" the huds
        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.getName().equals(n)) {
                hud.shouldRender = false;
                hud = null;
                huds.remove(i);
            }
        }
    }

    public HUD loadContainerHUD(ItemContainer ic) {
        HUD hud = null;

        hud = new HUDContainer(this, ic, registry, (getPWidth() - 264) / 2, 242, 232, 184);
        huds.add(hud);

        return hud;
    }

    public HUD loadPlayerContainerHUD(PlayerContainer pc) {
        HUD hud = null;

        hud = new HUDPlayerContainer(this, pc, registry, (getPWidth() - 264) / 2, 242, 232, 184);
        huds.add(hud);

        return hud;
    }

    public HUD loadFarmHUD(Farm f, int invSize) {
        HUD hud = null;

        hud = new HUDFarm(this, f, registry, (getPWidth() - 264) / 2, 242, 232, 184);
        huds.add(hud);

        return hud;
    }

    public boolean handleClick(Point clickPoint) {
        HUD hud = null;

        boolean doUpdate = !gameController.getIsMasterPaused();

        //start from the top and work our way down to "layer" the huds
        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (doUpdate || hud.getName().equals("Pause") || hud.getName().equals("Tutorial") || hud.getName().equals("Credits")) {
                if (hud.handleClick(clickPoint)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean handleRightClick(Point clickPoint) {
        HUD hud = null;

        boolean doUpdate = !gameController.getIsMasterPaused();

        //start from the top and work our way down to "layer" the huds
        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (doUpdate || hud.getName().equals("Pause") || hud.getName().equals("Tutorial") || hud.getName().equals("Credits")) {
                if (hud.handleRightClick(clickPoint)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean handleReleased(Point clickPoint) {
        HUD hud = null;

        //start from the top and work our way down to "layer" the huds
        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.handleReleased(clickPoint)) {
                checkRestoreCuror();
                return true;
            }
        }

        checkRestoreCuror();

        return false;
    }

    private void checkRestoreCuror() {
        int selectedStart = registry.getInvSlotFrom();

        if (selectedStart > -1) {
            restoreCursor();
        }
        selectedStart = -1;

        registry.setInvSlotFrom("", selectedStart);
    }

    public void setCursorImageAndText(BufferedImage img, String ct) {
        gameController.setCursorImageAndText(img, ct);
    }

    public void playerRender(Graphics g, int x, int y, boolean imageOverride) {
        gameController.playerRender(g, x, y, imageOverride);
    }

    public void playerDeleteInventory(int slot, int qty) {
        gameController.playerDeleteInventory(slot, qty, false);
    }

    public void playerDeleteInventory(int slot, int qty, boolean giveXP) {
        gameController.playerDeleteInventory(slot, qty, giveXP);
    }

    public void setPlayerSlotQuantity(int slot, int qty) {
        gameController.setPlayerSlotQuantity(slot, qty);
    }

    public void setPlayerSelectedItem(int i) {
        gameController.setPlayerSelectedItem(i);
    }

    public int playerAddItem(int slot, String name, int qty, int level) {
        return gameController.playerAddItem(slot, name, qty, level);
    }

    public int playerAddItem(int slot, String name, int qty) {
        return gameController.playerAddItem(slot, name, qty);
    }

    public int playerAddItem(String name, int qty) {
        return gameController.playerAddItem(name, qty);
    }

    public void playerSwapInventory(int from, int to) {
        gameController.playerSwapInventory(from, to);
    }

    public void playerEquipFromInventory(int slot) {
        gameController.playerEquipFromInventory(slot);
    }

    public void playerUnEquipToInventory(String equipmentType, int to) {
        gameController.playerUnEquipToInventory(equipmentType, to);
    }

    public void playerUnEquipToDelete(String equipmentType) {
        gameController.playerUnEquipToDelete(equipmentType);
    }

    public void playerCraftItem(String itemType) {
        gameController.playerCraftItem(itemType);
    }

    public void playerEquipHead(String armorName, int l) {
        gameController.playerEquipHead(armorName, l);
    }

    public void playerEquipChest(String armorName, int l) {
        gameController.playerEquipChest(armorName, l);
    }

    public void playerEquipLegs(String armorName, int l) {
        gameController.playerEquipLegs(armorName, l);
    }

    public void playerEquipFeet(String armorName, int l) {
        gameController.playerEquipFeet(armorName, l);
    }

    public void robotSetMode(String m) {
        gameController.robotSetMode(m);
    }

    public void robotToggleActivated() {
        gameController.robotToggleActivated();
    }

    public void robotToggleFollow() {
        gameController.robotToggleFollow();
    }

    public ArrayList<String> getItemTypeList(String category, String types) {
        return gameController.getItemTypeList(category, types);
    }

    public ArrayList<String> getItemTypeRequirements(String n) {
        return gameController.getItemTypeRequirements(n);
    }

    public void processConsoleCommand(String c) {
        gameController.processConsoleCommand(c);
    }

    public void restoreCursor() {
        gameController.restoreCursor();
    }

    public boolean isConsoleOpen() {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.getName().equals("Console")) {
                return hud.getShouldRender();
            }
        }

        return false;
    }

    public boolean isKeysOpen() {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.getName().equals("ScreenKeys")) {
                return hud.getShouldRender();
            }
        }

        return false;
    }

    public boolean isNewCharacterOpen() {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.getName().equals("ScreenNewCharacter")) {
                return hud.getShouldRender();
            }
        }

        return false;
    }

    public boolean isMultiPlayerJoinOpen() {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.getName().equals("ScreenMultiPlayerJoin")) {
                return hud.getShouldRender();
            }
        }

        return false;
    }

    public boolean isMultiPlayerHostOpen() {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.getName().equals("ScreenMultiPlayerHost")) {
                return hud.getShouldRender();
            }
        }

        return false;
    }

    public void consoleKey(int k, Character c) {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.getName().equals("Console")) {
                hud.consoleKey(k, c);
            }
        }
    }

    public void settingsKey(int k, Character c) {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.getName().equals("ScreenKeys")) {
                hud.settingsKey(k, c);
            }
        }
    }

    public void newCharacterKey(int k, Character c) {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.getName().equals("ScreenNewCharacter")) {
                hud.newCharacterKey(k, c);
            }
        }
    }

    public void multiPlayerJoinKey(int k, Character c) {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.getName().equals("ScreenMultiPlayerJoin")) {
                hud.newCharacterKey(k, c);
            }
        }
    }

    public void multiPlayerHostKey(int k, Character c) {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.getName().equals("ScreenMultiPlayerHost")) {
                hud.newCharacterKey(k, c);
            }
        }
    }

    public void newCharacterKey(int k, Character c, boolean tab) {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.getName().equals("ScreenNewCharacter")) {
                hud.newCharacterKey(k, c, true);
            }
        }
    }

    public void multiPlayerJoinKey(int k, Character c, boolean tab) {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.getName().equals("ScreenMultiPlayerJoin")) {
                hud.newCharacterKey(k, c, true);
            }
        }
    }

    public void multiPlayerHostKey(int k, Character c, boolean tab) {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud.getName().equals("ScreenMultiPlayerHost")) {
                hud.newCharacterKey(k, c, true);
            }
        }
    }

    public void setServerIP(String ip) {
        gameController.serverIP = ip;
    }

    public void setServerPort(String p) {
        gameController.serverPort = p;
    }

    public void setStartServer(boolean s) {
        gameController.startServer = s;
    }

    public void setServerJoin(boolean s) {
        gameController.serverJoin = s;
    }

    public void shiftPressed() {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            hud.shiftPressed();
        }
    }

    public void shiftRelease() {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            hud.shiftRelease();
        }
    }

    public void showWeaponHUD(boolean s) {
        if (weaponHUD != null) {
            weaponHUD.shouldRender = s;
        }
    }

    public void showArmorHUD(boolean s) {
        if (armorHUD != null) {
            armorHUD.shouldRender = s;
        }
    }

    public void toggleMasterHUD() {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            hud.toggleMasterHUD();
        }

        checkRestoreCuror();
    }

    public void togglePauseHUD() {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            hud.togglePauseHUD();
        }

        checkRestoreCuror();
    }

    public void toggleContainerHUD(HUD h) {
        HUD hud = null;

        boolean isOpen = false;

        //see if the hud is currently open
        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            if (hud == h) {
                isOpen = hud.getShouldRender();
                break;
            }
        }

        if (isOpen) {
            hud.setShouldRender(false);
        } else {
            //make sure all the other huds are closed and show the container
            for (int i = (huds.size() - 1); i >= 0; i--) {
                hud = huds.get(i);
                if (hud.getIsContainer()) {
                    if (hud == h) {
                        hud.setShouldRender(true);
                    } else {
                        hud.setShouldRender(false);
                    }
                }
            }
        }

        checkRestoreCuror();
    }

    public void keyEnterPressed() {
        HUD hud = null;

        for (int i = (huds.size() - 1); i >= 0; i--) {
            hud = huds.get(i);
            hud.keyEnterPressed();
        }
    }

    @Override
    public void update() {
        super.update();

        boolean doUpdate = !gameController.getIsMasterPaused();

        HUD hud = null;

        for (int i = 0; i < huds.size(); i++) {
            hud = huds.get(i);
            if (doUpdate || hud.getName().equals("Pause") || hud.getName().equals("Tutorial") || hud.getName().equals("Credits")) {
                hud.update();
            }

            if (hud.isDirty()) {
                hud = null;
                huds.remove(i);
            }
        }
    }

    public void render(Graphics g) {
        HUD hud = null;

        for (int i = 0; i < huds.size(); i++) {
            hud = huds.get(i);
            hud.render(g);
        }
    }
}