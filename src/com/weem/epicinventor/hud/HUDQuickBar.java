package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.placeable.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;

public class HUDQuickBar extends HUD {

    private final static int HP_BG_X = 19;
    private final static int HP_BG_Y = 70;
    private final static int HP_BG_WIDTH = 126;
    private final static int HP_BG_HEIGHT = 17;
    private final static int BUTTON_WIDTH = 32;
    private final static int BUTTON_HEIGHT = 21;
    private final static int BUTTON_REPORT_BUGS_X = 522;
    private final static int BUTTON_REPORT_BUGS_WIDTH = 106;
    private final static int BUTTON_INVENTORY_X = 686;
    private final static int BUTTON_PAUSE_X = 652;
    private final static int BUTTON_HELP_X = 720;
    private final static int BUTTON_EXIT_X = 754;
    private final static int BUTTON_Y = 19;
    private final static int HP_BAR_X = 21;
    private final static int HP_BAR_Y = 72;
    private final static int HP_BAR_HEIGHT = 13;
    private final static int HP_TEXT_X = 48;
    private final static int HP_TEXT_Y = 32;
    private final static int XP_BG_X = 19;
    private final static int XP_BG_Y = 103;
    private final static int XP_BG_WIDTH = 604;
    private final static int XP_BG_HEIGHT = 17;
    private final static int XP_BAR_X = 21;
    private final static int XP_BAR_Y = 105;
    private final static int XP_BAR_HEIGHT = 13;
    private final static int XP_TEXT_X = 48;
    private final static int XP_TEXT_Y = 50;
    private final static int LOCK_BG_X = 176;
    private final static int LOCK_BG_Y = 59;
    private final static int LOCK_BG_WIDTH = 19;
    private final static int LOCK_BG_HEIGHT = 20;
    private final static int LOCK_X = 181;
    private final static int LOCK_Y = 62;
    private final static int LOCK_WIDTH = 9;
    private final static int LOCK_HEIGHT = 15;
    private final static int STATUS_X = 30;
    private final static int STATUS_Y = 126;
    private final static int STATUS_WIDTH = 732;
    private final static int STATUS_HEIGHT = 23;
    private final static int STATUS_TEXT_X = 8;
    private final static int STATUS_TEXT_Y = 15;
    private final static int VERSION_WIDTH = 120;
    private final static int VERSION_HEIGHT = 23;
    private final static int VERSION_X = 640;
    private final static int VERSION_Y = 125;
    private final static int SLOT_START_X = 200;
    private final static int SLOT_START_Y = 49;
    private final static int SLOT_WIDTH = 40;
    private final static int SLOT_HEIGHT = 40;
    private final static int SLOT_SPACING = 3;
    private final static int SLOTS = 10;
    private final static int SLOT_TEXT_OFFSET_0 = 32;
    private final static int SLOT_TEXT_OFFSET_10 = 25;
    private final static int SLOT_TEXT_OFFSET_100 = 18;
    private final static int SLOT_TEXT_Y = 37;
    private final static int POWER_TEXT_X = 355;
    private final static int POWER_TEXT_Y = 38;
    private final static int LEVEL_TEXT_X = 355;
    private final static int LEVEL_TEXT_Y = 58;
    private final static int ROBOT_POWER_X = 48;
    private final static int ROBOT_POWER_Y = 8;
    private final static int ROBOT_POWER_WIDTH = 26;
    private final static int ROBOT_POWER_HEIGHT = 26;
    private final static int ROBOT_BUTTON_PASSIVE_X = 81;
    private final static int ROBOT_BUTTON_DEFENSIVE_X = 110;
    private final static int ROBOT_BUTTON_AGGRESSIVE_X = 139;
    private final static int ROBOT_BUTTON_FOLLOW_X = 168;
    private final static int ROBOT_BUTTON_Y = 8;
    private final static int ROBOT_BUTTON_WIDTH = 26;
    private final static int ROBOT_BUTTON_HEIGHT = 28;
    private final static int ROBOT_SLOT_START_X = 201;
    private final static int ROBOT_SLOT_START_Y = 6;
    private final static int ROBOT_SLOT_WIDTH = 30;
    private final static int ROBOT_SLOT_HEIGHT = 30;
    private final static int ROBOT_SLOT_SPACING = 2;
    private final static int ROBOT_SLOTS = 4;
    private final static int ROBOT_BATTERY_Y = 9;
    private final static int ROBOT_BATTERY_WIDTH = 27;
    private final static int ROBOT_BATTERY_HEIGHT = 11;
    private final static int ROBOT_BATTERY_BAR_Y = 11;
    private final static int ROBOT_BATTERY_BAR_HEIGHT = 7;
    private final static int ROBOT_BATTERY_BAR_TEXT_Y = 20;
    private final static int ROBOT_BATTERY_X = 84;
    private final static int ROBOT_BATTERY_BAR_X = 86;
    private final static int ROBOT_BATTERY_BAR_MAX_WIDTH = 21;
    private final static int ROBOT_BATTERY_BAR_TEXT_X = 85;
    private final static int ROBOT_BATTERY_ON_X = 338;
    private final static int ROBOT_BATTERY_BAR_ON_X = 340;
    private final static int ROBOT_BATTERY_BAR_ON_TEXT_X = 340;
    private final static int DIVIDER_X1 = 77;
    private final static int DIVIDER_X2 = 331;
    private final static int DIVIDER_Y = 6;
    private final static int DIVIDER_WIDTH = 1;
    private final static int DIVIDER_HEIGHT = 30;
    private final static int RECAST_FRAMES = 20;
    private int powerUsed;
    private int powerTotal;
    private final static float TOWN_UPDATE_CHECK_INTERVAL = 0.50f;
    private long townUpdateCheckTime;
    private boolean shiftKeyPressed = false;

    public HUDQuickBar(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/QuickBar/BG");

        HUDArea hudArea = null;

        //slots
        int slotX = 0;
        for (int i = 0; i < SLOTS; i++) {
            slotX = SLOT_START_X + (i * SLOT_WIDTH) + (i * SLOT_SPACING);

            hudArea = addArea(slotX, SLOT_START_Y, SLOT_WIDTH, SLOT_HEIGHT, "slot");
            hudArea.setFont("SansSerif", Font.BOLD, 12);
            hudArea.setImage("HUD/QuickBar/Slot");
        }

        //buttons
        hudArea = addArea(BUTTON_INVENTORY_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, "button_inventory");
        hudArea.setImage("HUD/QuickBar/ButtonInventory");
        hudArea = addArea(BUTTON_REPORT_BUGS_X, BUTTON_Y, BUTTON_REPORT_BUGS_WIDTH, BUTTON_HEIGHT, "button_report_bugs");
        hudArea.setImage("HUD/QuickBar/ButtonReportBugs");
        hudArea = addArea(BUTTON_PAUSE_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, "button_pause");
        hudArea.setImage("HUD/QuickBar/ButtonPause");
        hudArea = addArea(BUTTON_HELP_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, "button_help");
        hudArea.setImage("HUD/QuickBar/ButtonHelp");
        hudArea = addArea(BUTTON_EXIT_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, "button_exit");
        hudArea.setImage("HUD/QuickBar/ButtonExit");

        //hp
        hudArea = addArea(HP_BG_X, HP_BG_Y, HP_BG_WIDTH, HP_BG_HEIGHT, "hp_bg");
        hudArea.setImage("HUD/QuickBar/HPBG");
        hudArea = addArea(HP_BAR_X, HP_BAR_Y, 1, HP_BAR_HEIGHT, "hp_bar");
        hudArea.setImage("HUD/QuickBar/HPBar");
        hudArea = addArea(HP_TEXT_X, HP_TEXT_Y, 1, 1, "hp");
        hudArea.setFont("SansSerif", Font.PLAIN, 11);

        //xp
        hudArea = addArea(XP_BG_X, XP_BG_Y, XP_BG_WIDTH, XP_BG_HEIGHT, "xp_bg");
        hudArea.setImage("HUD/QuickBar/XPBG");
        hudArea = addArea(XP_BAR_X, XP_BAR_Y, 1, XP_BAR_HEIGHT, "xp_bar");
        hudArea.setImage("HUD/QuickBar/XPBar");
        hudArea = addArea(XP_BG_X, XP_BG_Y, XP_BG_WIDTH, XP_BG_HEIGHT, "xp_overlay");
        hudArea.setImage("HUD/QuickBar/XPOverlay");
        hudArea = addArea(XP_TEXT_X, XP_TEXT_Y, 1, 1, "xp");
        hudArea.setFont("SansSerif", Font.PLAIN, 11);

        //lock
        hudArea = addArea(LOCK_BG_X, LOCK_BG_Y, LOCK_BG_WIDTH, LOCK_BG_HEIGHT, "lock_bg");
        hudArea.setImage("HUD/QuickBar/LockBG");
        hudArea = addArea(LOCK_X, LOCK_Y, LOCK_WIDTH, LOCK_HEIGHT, "lock");
        hudArea.setImage("HUD/QuickBar/Lock");

        //status
        hudArea = addArea(STATUS_X, STATUS_Y, STATUS_WIDTH, STATUS_HEIGHT, "status");
        hudArea.setFont("SansSerif", Font.PLAIN, 11);
        hudArea.setTextXY(STATUS_TEXT_X, STATUS_TEXT_Y);
        hudArea.setImage("HUD/QuickBar/StatusBG");

        //version
        hudArea = addArea(VERSION_X, VERSION_Y, VERSION_WIDTH, VERSION_HEIGHT, "version");
        hudArea.setFont("SansSerif", Font.BOLD, 12);
        hudArea.setTextXY(STATUS_TEXT_X, STATUS_TEXT_Y);
        hudArea.setText("Beta Version: " + Game.VERSION);

        //power
        hudArea = addArea(POWER_TEXT_X, POWER_TEXT_Y, 1, 1, "power");
        hudArea.setFont("SansSerif", Font.BOLD, 13);
        hudArea.setTextColor(new Color(112, 223, 255));

        //level
        hudArea = addArea(LEVEL_TEXT_X, LEVEL_TEXT_Y, 1, 1, "level");
        hudArea.setFont("SansSerif", Font.BOLD, 13);
        hudArea.setTextColor(new Color(243, 238, 102));

        //robot stuff
        hudArea = addArea(ROBOT_POWER_X, ROBOT_POWER_Y, ROBOT_POWER_WIDTH, ROBOT_POWER_HEIGHT, "robot_power");
        hudArea.setImage("HUD/QuickBar/RobotPower");
        hudArea = addArea(DIVIDER_X1, DIVIDER_Y, DIVIDER_WIDTH, DIVIDER_HEIGHT, "divider1");
        hudArea.setImage("HUD/QuickBar/Divider");
        hudArea = addArea(DIVIDER_X2, DIVIDER_Y, DIVIDER_WIDTH, DIVIDER_HEIGHT, "divider2");
        hudArea.setImage("HUD/QuickBar/Divider");
        hudArea.setIsActive(false);

        //robot buttons
        hudArea = addArea(ROBOT_BUTTON_PASSIVE_X, ROBOT_BUTTON_Y, ROBOT_BUTTON_WIDTH, ROBOT_BUTTON_HEIGHT, "robot_button_passive");
        hudArea.setImage("HUD/QuickBar/RobotPassive");
        hudArea.setIsActive(false);
        hudArea = addArea(ROBOT_BUTTON_DEFENSIVE_X, ROBOT_BUTTON_Y, ROBOT_BUTTON_WIDTH, ROBOT_BUTTON_HEIGHT, "robot_button_defensive");
        hudArea.setImage("HUD/QuickBar/RobotDefensive");
        hudArea.setIsActive(false);
        hudArea = addArea(ROBOT_BUTTON_AGGRESSIVE_X, ROBOT_BUTTON_Y, ROBOT_BUTTON_WIDTH, ROBOT_BUTTON_HEIGHT, "robot_button_aggressive");
        hudArea.setImage("HUD/QuickBar/RobotAggressive");
        hudArea.setIsActive(false);
        hudArea = addArea(ROBOT_BUTTON_FOLLOW_X, ROBOT_BUTTON_Y, ROBOT_BUTTON_WIDTH, ROBOT_BUTTON_HEIGHT, "robot_button_follow");
        hudArea.setImage("HUD/QuickBar/RobotFollow");
        hudArea.setIsActive(false);

        //robot slots
        slotX = 0;
        for (int i = 0; i < ROBOT_SLOTS; i++) {
            slotX = ROBOT_SLOT_START_X + (i * ROBOT_SLOT_WIDTH) + (i * ROBOT_SLOT_SPACING);

            hudArea = addArea(slotX, ROBOT_SLOT_START_Y, ROBOT_SLOT_WIDTH, ROBOT_SLOT_HEIGHT, "robot_slot" + (i + 1));
            hudArea.setImage("HUD/QuickBar/RobotSlot");
            hudArea.setIsActive(false);
        }

        //robot battery
        hudArea = addArea(ROBOT_BATTERY_X, ROBOT_BATTERY_Y, ROBOT_BATTERY_WIDTH, ROBOT_BATTERY_HEIGHT, "robot_battery_bg");
        hudArea.setImage("HUD/QuickBar/RobotBattery");
        hudArea = addArea(ROBOT_BATTERY_BAR_X, ROBOT_BATTERY_BAR_Y, 1, ROBOT_BATTERY_BAR_HEIGHT, "robot_battery_bar");
        hudArea.setImage("HUD/QuickBar/RobotBatteryMeter");
        hudArea = addArea(ROBOT_BATTERY_BAR_TEXT_X, ROBOT_BATTERY_BAR_TEXT_Y, 1, 1, "robot_battery_level");
        hudArea.setFont("SansSerif", Font.PLAIN, 11);
        hudArea.setText("100%");
    }

    @Override
    public void update() {
        if (shouldRender) {
            int invSlot;
            int playerInventorySlot;
            HUDArea hudArea;

            Player p = null;
            PlayerManager pm = registry.getPlayerManager();
            if (pm != null) {
                p = pm.getCurrentPlayer();
            }
            if (p != null) {
                townUpdateCheckTime += registry.getImageLoader().getPeriod();
                if ((townUpdateCheckTime / 1000) >= TOWN_UPDATE_CHECK_INTERVAL) {
                    townUpdateCheckTime = 0;
                    int[] powerVals = hudManager.getCurrentPower();
                    powerUsed = powerVals[0];
                    powerTotal = powerVals[1];
                }

                if (registry.getRobotActivated(p)) {
                    setImage("HUD/QuickBar/BGRobotOn");
                } else {
                    setImage("HUD/QuickBar/BG");
                }

                Inventory robotInventory = registry.getRobotInventory();

                //update slots
                for (int i = 0; i < hudAreas.size(); i++) {
                    hudArea = hudAreas.get(i);
                    if (hudArea.getType().equals("slot")) {
                        playerInventorySlot = i + registry.getPlayerInventorySize(registry.getPlayerManager().getCurrentPlayer()) - SLOTS;

                        String hudAreaImage = registry.getPlaverInventorySlotImage(playerInventorySlot);

                        if (hudAreaImage != null) {
                            hudArea.setFGImage(hudAreaImage);
                            if (hudArea.isInside(registry.getMousePosition())) {
                                registry.setStatusText(registry.getPlaverInventorySlotDescription(playerInventorySlot));
                                if (registry.getPlaverInventorySlotType(playerInventorySlot).equals("Weapon")) {
                                    registry.setWeaponType(registry.getPlaverInventorySlotName(playerInventorySlot));
                                    registry.setWeaponLevel(registry.getPlaverInventorySlotLevel(playerInventorySlot));
                                    hudManager.showArmorHUD(false);
                                    hudManager.showWeaponHUD(true);
                                } else if (registry.getPlaverInventorySlotType(playerInventorySlot).equals("Armor")) {
                                    registry.setArmorType(registry.getPlaverInventorySlotName(playerInventorySlot));
                                    registry.setArmorLevel(registry.getPlaverInventorySlotLevel(playerInventorySlot));
                                    hudManager.showArmorHUD(true);
                                    hudManager.showWeaponHUD(false);
                                }
                            }
                        }
                        int hudAreaQty = registry.getPlaverInventorySlotQty(playerInventorySlot);
                        if (hudAreaQty > 1) {
                            hudArea.setText(String.valueOf(hudAreaQty));
                            if (hudAreaQty < 10) {
                                hudArea.setTextXY(SLOT_TEXT_OFFSET_0, SLOT_TEXT_Y);
                            } else if (hudAreaQty < 100) {
                                hudArea.setTextXY(SLOT_TEXT_OFFSET_10, SLOT_TEXT_Y);
                            } else {
                                hudArea.setTextXY(SLOT_TEXT_OFFSET_100, SLOT_TEXT_Y);
                            }
                        } else {
                            hudArea.setText("");
                            hudArea.setTextXY(SLOT_TEXT_OFFSET_0, SLOT_TEXT_Y);
                        }
                        if (registry.getPlayerSelectedItem(registry.getPlayerManager().getCurrentPlayer()) == i) {
                            hudArea.setImage("HUD/QuickBar/SlotOn");
                        } else {
                            hudArea.setImage("HUD/QuickBar/Slot");
                        }

                        //add recast animation for melee weaspons
                        String hudAreaType = registry.getPlaverInventorySlotType(playerInventorySlot);
                        if (hudAreaType != null) {
                            if (hudAreaType.equals("Weapon")) {
                                int recastPercentage = registry.getPlayerManager().getCurrentPlayer().getMeleeRecastPercentage();
                                if (recastPercentage > 0) {
                                    int frame = (recastPercentage * RECAST_FRAMES) / 100;
                                    if (frame > RECAST_FRAMES) {
                                        frame = RECAST_FRAMES;
                                    }
                                    if (registry.getPlayerSelectedItem(registry.getPlayerManager().getCurrentPlayer()) == i) {
                                        hudArea.setImage("HUD/QuickBar/SlotRecastOn", frame);
                                    } else {
                                        hudArea.setImage("HUD/QuickBar/SlotRecast", frame);
                                    }
                                }
                            }
                        }
                    } else if (hudArea.getType().equals("hp_bg")) {
                        hudAreaText(hudArea, "Your current Hit Points.  When you run out, you're dead.");
                    } else if (hudArea.getType().equals("hp_bar")) {
                        hudArea.setWidth(((HP_BG_WIDTH - 4) * registry.getPlaverHitPoints(registry.getPlayerManager().getCurrentPlayer())) / registry.getPlaverTotalHitPoints(registry.getPlayerManager().getCurrentPlayer()));
                        hudAreaText(hudArea, "Your current Hit Points.  When you run out, you're dead.");
                    } else if (hudArea.getType().equals("hp")) {
                        hudArea.setText(registry.getPlaverHitPoints(registry.getPlayerManager().getCurrentPlayer()) + " / " + registry.getPlaverTotalHitPoints(registry.getPlayerManager().getCurrentPlayer()));
                        hudArea.setTextXY(HP_TEXT_X, HP_TEXT_Y);
                        hudAreaText(hudArea, "Your current Hit Points.  When you run out, you're dead.");
                    } else if (hudArea.getType().equals("xp_bg")) {
                        hudAreaText(hudArea, "Your current XP.  Kill things, level up, get stronger.");
                    } else if (hudArea.getType().equals("xp_overlay")) {
                        hudAreaText(hudArea, "Your current XP.  Kill things, level up, get stronger.");
                    } else if (hudArea.getType().equals("xp_bar")) {
                        int current = registry.getPlayerManager().getCurrentPlayer().getXP() - registry.getXPNeededForLevel(registry.getPlayerManager().getCurrentPlayer().getLevel());
                        int needed = registry.getXPNeededForLevel(registry.getPlayerManager().getCurrentPlayer().getLevel() + 1) - registry.getXPNeededForLevel(registry.getPlayerManager().getCurrentPlayer().getLevel());

                        if (needed == 0) {
                            needed = 1;
                        }

                        hudArea.setWidth(((XP_BG_WIDTH - 4) * current / needed));
                        hudAreaText(hudArea, "Your current XP.  Kill things, level up, get stronger.");
                    } else if (hudArea.getType().equals("xp")) {
                        hudArea.setText(registry.getPlayerManager().getCurrentPlayer().getXP() + " / " + registry.getXPNeededForLevel(registry.getPlayerManager().getCurrentPlayer().getLevel() + 1));
                        hudArea.setTextXY(XP_TEXT_X, XP_TEXT_Y);
                        hudAreaText(hudArea, "Your current XP.  Kill things, level up, get stronger.");
                    } else if (hudArea.getType().equals("status")) {
                        hudArea.setText(registry.getStatusText());
                    } else if (hudArea.getType().equals("power")) {
                        hudArea.setText(powerUsed + " / " + powerTotal);
                        hudArea.setTextXY(POWER_TEXT_X, POWER_TEXT_Y);
                    } else if (hudArea.getType().equals("level")) {
                        hudArea.setText(Integer.toString(registry.getPlayerManager().getCurrentPlayer().getLevel()));
                        hudArea.setTextXY(LEVEL_TEXT_X, LEVEL_TEXT_Y);
                    } else if (hudArea.getType().equals("power_label")) {
                        hudAreaText(hudArea, "Your current Power Used / Available.  Some placeables require power to operate.");
                    } else if (hudArea.getType().equals("lock")) {
                        if (registry.getIsQuickBarLocked()) {
                            hudArea.setImage("HUD/QuickBar/LockOn");
                        } else {
                            hudArea.setImage("HUD/QuickBar/Lock");
                        }
                        hudAreaText(hudArea, "When activated, you can't accidentally (or otherwise) drag items off your Quick Bar");
                    } else if (hudArea.getType().equals("robot_power")) {
                        if (registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer())) {
                            hudArea.setImage("HUD/QuickBar/RobotPowerOn");
                        } else {
                            hudArea.setImage("HUD/QuickBar/RobotPower");
                        }
                        hudAreaText(hudArea, "Toggles your robot on and off");
                    } else if (hudArea.getType().equals("robot_battery_level")) {
                        if (registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer())) {
                            hudArea.setXY(ROBOT_BATTERY_BAR_ON_TEXT_X, ROBOT_BATTERY_BAR_TEXT_Y);
                        } else {
                            hudArea.setXY(ROBOT_BATTERY_BAR_TEXT_X, ROBOT_BATTERY_BAR_TEXT_Y);
                        }
                        hudArea.setText(Integer.toString(registry.getRobotBatteryPercentage(registry.getPlayerManager().getCurrentPlayer())) + "%");
                        hudAreaText(hudArea, "Your robot's current battery level.  When he runs out, he goes away.");
                    } else if (hudArea.getType().equals("robot_battery_bar")) {
                        if (registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer())) {
                            hudArea.setXY(ROBOT_BATTERY_BAR_ON_X, ROBOT_BATTERY_BAR_Y);
                        } else {
                            hudArea.setXY(ROBOT_BATTERY_BAR_X, ROBOT_BATTERY_BAR_Y);
                        }
                        hudArea.setWidth((ROBOT_BATTERY_BAR_MAX_WIDTH * registry.getRobotBatteryPercentage(registry.getPlayerManager().getCurrentPlayer())) / 100);
                        hudAreaText(hudArea, "Your robot's current battery level.  When he runs out, he goes away.");
                    } else if (hudArea.getType().equals("divider2")) {
                        hudArea.setIsActive(registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer()));
                    } else if (hudArea.getType().equals("robot_button_passive")) {
                        if (registry.getRobotMode(registry.getPlayerManager().getCurrentPlayer()).equals("Passive")) {
                            hudArea.setImage("HUD/QuickBar/RobotPassiveOn");
                        } else {
                            hudArea.setImage("HUD/QuickBar/RobotPassive");
                        }
                        hudArea.setIsActive(registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer()));
                        hudAreaText(hudArea, "Passive Mode: Your robot will just sit there and take it");
                    } else if (hudArea.getType().equals("robot_button_defensive")) {
                        if (registry.getRobotMode(registry.getPlayerManager().getCurrentPlayer()).equals("Defensive")) {
                            hudArea.setImage("HUD/QuickBar/RobotDefensiveOn");
                        } else {
                            hudArea.setImage("HUD/QuickBar/RobotDefensive");
                        }
                        hudArea.setIsActive(registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer()));
                        hudAreaText(hudArea, "Defensive Mode: Your robot will only attack if you're being attacked");
                    } else if (hudArea.getType().equals("robot_button_aggressive")) {
                        if (registry.getRobotMode(registry.getPlayerManager().getCurrentPlayer()).equals("Aggressive")) {
                            hudArea.setImage("HUD/QuickBar/RobotAggressiveOn");
                        } else {
                            hudArea.setImage("HUD/QuickBar/RobotAggressive");
                        }
                        hudArea.setIsActive(registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer()));
                        hudAreaText(hudArea, "Aggressive Mode: Your robot will attack anything in site");
                    } else if (hudArea.getType().equals("robot_button_follow")) {
                        if (registry.getRobotFollowing(registry.getPlayerManager().getCurrentPlayer())) {
                            hudArea.setImage("HUD/QuickBar/RobotFollowOn");
                        } else {
                            hudArea.setImage("HUD/QuickBar/RobotFollow");
                        }
                        hudArea.setIsActive(registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer()));
                        hudAreaText(hudArea, "Auto Follow: When activated, your robot will follow you around");
                    } else if (hudArea.getType().equals("robot_slot1")) {
                        invSlot = 0;

                        String hudAreaImage = robotInventory.getImageFromSlot(invSlot);
                        if (hudAreaImage != null) {
                            hudArea.setFGImage(hudAreaImage);
                            if (hudArea.isInside(registry.getMousePosition())) {
                                registry.setStatusText(robotInventory.getDescriptionFromSlot(invSlot));
                            }
                        }

                        if (registry.getRobotInventorySize() >= (invSlot + 1)) {
                            hudArea.setImage("HUD/QuickBar/RobotSlotOn");
                        } else {
                            hudArea.setImage("HUD/QuickBar/RobotSlot");
                        }
                        hudArea.setIsActive(registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer()));
                    } else if (hudArea.getType().equals("robot_slot2")) {
                        invSlot = 1;

                        String hudAreaImage = robotInventory.getImageFromSlot(invSlot);
                        if (hudAreaImage != null) {
                            hudArea.setFGImage(hudAreaImage);
                            if (hudArea.isInside(registry.getMousePosition())) {
                                registry.setStatusText(robotInventory.getDescriptionFromSlot(invSlot));
                            }
                        }

                        if (registry.getRobotInventorySize() >= (invSlot + 1)) {
                            hudArea.setImage("HUD/QuickBar/RobotSlotOn");
                        } else {
                            hudArea.setImage("HUD/QuickBar/RobotSlot");
                        }
                        hudArea.setIsActive(registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer()));
                    } else if (hudArea.getType().equals("robot_slot3")) {
                        invSlot = 2;

                        String hudAreaImage = robotInventory.getImageFromSlot(invSlot);
                        if (hudAreaImage != null) {
                            hudArea.setFGImage(hudAreaImage);
                            if (hudArea.isInside(registry.getMousePosition())) {
                                registry.setStatusText(robotInventory.getDescriptionFromSlot(invSlot));
                            }
                        }

                        if (registry.getRobotInventorySize() >= (invSlot + 1)) {
                            hudArea.setImage("HUD/QuickBar/RobotSlotOn");
                        } else {
                            hudArea.setImage("HUD/QuickBar/RobotSlot");
                        }
                        hudArea.setIsActive(registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer()));
                    } else if (hudArea.getType().equals("robot_slot4")) {
                        invSlot = 3;

                        String hudAreaImage = robotInventory.getImageFromSlot(invSlot);
                        if (hudAreaImage != null) {
                            hudArea.setFGImage(hudAreaImage);
                            if (hudArea.isInside(registry.getMousePosition())) {
                                registry.setStatusText(robotInventory.getDescriptionFromSlot(invSlot));
                            }
                        }

                        if (registry.getRobotInventorySize() >= (invSlot + 1)) {
                            hudArea.setImage("HUD/QuickBar/RobotSlotOn");
                        } else {
                            hudArea.setImage("HUD/QuickBar/RobotSlot");
                        }
                        hudArea.setIsActive(registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer()));
                    } else if (hudArea.getType().equals("robot_battery_bg")) {
                        if (registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer())) {
                            hudArea.setXY(ROBOT_BATTERY_ON_X, ROBOT_BATTERY_Y);
                        } else {
                            hudArea.setXY(ROBOT_BATTERY_X, ROBOT_BATTERY_Y);
                        }
                        hudAreaText(hudArea, "Your robot's current battery level.  When he runs out, he goes away.");
                    } else if (hudArea.getType().equals("robot_battery_bar")) {
                        if (registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer())) {
                            hudArea.setXY(ROBOT_BATTERY_BAR_ON_X, ROBOT_BATTERY_BAR_Y);
                        } else {
                            hudArea.setXY(ROBOT_BATTERY_BAR_X, ROBOT_BATTERY_BAR_Y);
                        }
                        hudAreaText(hudArea, "Your robot's current battery level.  When he runs out, he goes away.");
                    } else if (hudArea.getType().equals("robot_battery_level")) {
                        if (registry.getRobotActivated(registry.getPlayerManager().getCurrentPlayer())) {
                            hudArea.setXY(ROBOT_BATTERY_BAR_ON_TEXT_X, ROBOT_BATTERY_BAR_TEXT_Y);
                        } else {
                            hudArea.setXY(ROBOT_BATTERY_BAR_TEXT_X, ROBOT_BATTERY_BAR_TEXT_Y);
                        }
                        hudArea.setText("100%");
                        hudAreaText(hudArea, "Your robot's current battery level.  When he runs out, he goes away.");
                    } else if (hudArea.getType().equals("button_inventory")) {
                        hudAreaText(hudArea, "Opens your inventory and crafting window");
                    } else if (hudArea.getType().equals("button_report_bugs")) {
                        hudAreaText(hudArea, "Let us know about any problems so we can fix them!");
                    } else if (hudArea.getType().equals("button_pause")) {
                        hudAreaText(hudArea, "Pauses and UnPaused the game");
                        if (hudManager.getIsPaused()) {
                            hudArea.setImage("HUD/QuickBar/ButtonPlay");
                        } else {
                            hudArea.setImage("HUD/QuickBar/ButtonPause");
                        }
                    } else if (hudArea.getType().equals("button_help")) {
                        hudAreaText(hudArea, "For Noobs' eyes only");
                    } else if (hudArea.getType().equals("button_exit")) {
                        hudAreaText(hudArea, "Pauses the game and brings up some options (including save)");
                    }
                }
            }
        }

        super.update();
    }

    @Override
    public void HUDAreaClicked(HUDArea ha) {
        HUDArea hudArea = null;
        int playerInventorySlot = 0;

        int selectedStart = registry.getInvSlotFrom();

        String hudFrom = "QuickBar";

        for (int i = 0; i < hudAreas.size(); i++) {
            hudArea = hudAreas.get(i);

            playerInventorySlot = i + registry.getPlayerInventorySize(registry.getPlayerManager().getCurrentPlayer()) - SLOTS;

            if (hudArea == ha) {
                if (selectedStart != playerInventorySlot && registry.getSplitCount() > 0) {
                    if (i >= 0 && selectedStart >= 0 && registry.getPlaverInventorySlotQty(playerInventorySlot) == 0) {
                        if (hudAreas.get(i).getType().equals("slot") && hudAreas.get(selectedStart - registry.getPlayerInventorySize(registry.getPlayerManager().getCurrentPlayer()) + SLOTS).getType().equals("slot")) {
                            String in = registry.getItemNameBySlot(selectedStart);
                            hudManager.setPlayerSlotQuantity(selectedStart, registry.getPlaverInventorySlotQty(selectedStart) - registry.getSplitCount());
                            hudManager.playerAddItem(playerInventorySlot, in, registry.getSplitCount());
                        }
                    }
                    registry.setInvSlotFrom("QuickBar", selectedStart);
                } else {
                    if (hudArea.getType().equals("slot")) {
                        selectedStart = playerInventorySlot;
                        hudManager.setCursorImageAndText(hudArea.getFGImage(), hudArea.getText());
                    } else if (hudArea.getType().equals("robot_slot1") && registry.getRobotInventorySize() >= 1) {
                        selectedStart = 0;
                        hudFrom = "QuickBarRobot";
                        hudManager.setCursorImageAndText(hudArea.getFGImage(), hudArea.getText());
                    } else if (hudArea.getType().equals("robot_slot2") && registry.getRobotInventorySize() >= 2) {
                        selectedStart = 1;
                        hudFrom = "QuickBarRobot";
                        hudManager.setCursorImageAndText(hudArea.getFGImage(), hudArea.getText());
                    } else if (hudArea.getType().equals("robot_slot3") && registry.getRobotInventorySize() >= 3) {
                        selectedStart = 2;
                        hudFrom = "QuickBarRobot";
                        hudManager.setCursorImageAndText(hudArea.getFGImage(), hudArea.getText());
                    } else if (hudArea.getType().equals("robot_slot4") && registry.getRobotInventorySize() >= 4) {
                        selectedStart = 3;
                        hudFrom = "QuickBarRobot";
                        hudManager.setCursorImageAndText(hudArea.getFGImage(), hudArea.getText());
                    } else if (hudArea.getType().equals("lock")) {
                        SoundClip cl = new SoundClip("Misc/Click");
                        registry.setIsQuickBarLocked(!registry.getIsQuickBarLocked());
                    } else if (hudArea.getType().equals("button_inventory")) {
                        SoundClip cl = new SoundClip("Misc/Click");
                        hudManager.toggleMasterHUD();
                    } else if (hudArea.getType().equals("button_report_bugs")) {
                        SoundClip cl = new SoundClip("Misc/Click");
                        String url = "http://epicinventor.com/forum/index.php?board=9.0";

                        try {
                            Desktop.getDesktop().browse(java.net.URI.create(url));
                        } catch (Exception e) {
                        }
                    } else if (hudArea.getType().equals("button_help")) {
                        SoundClip cl = new SoundClip("Misc/Click");
                        hudManager.pauseMasterGame();
                        hudManager.loadHUD(HUDManager.HUDType.Tutorial);
                    } else if (hudArea.getType().equals("button_pause")) {
                        SoundClip cl = new SoundClip("Misc/Click");
                        hudManager.togglePaused();
                    } else if (hudArea.getType().equals("button_exit")) {
                        SoundClip cl = new SoundClip("Misc/Click");
                        hudManager.togglePauseHUD();
                        hudManager.pauseMasterGame();
                    } else if (hudArea.getType().equals("robot_power")) {
                        SoundClip cl = new SoundClip("Misc/Click");
                        hudManager.robotToggleActivated();
                    } else if (hudArea.getType().equals("robot_button_passive")) {
                        SoundClip cl = new SoundClip("Misc/Click");
                        hudManager.robotSetMode("Passive");
                    } else if (hudArea.getType().equals("robot_button_defensive")) {
                        SoundClip cl = new SoundClip("Misc/Click");
                        hudManager.robotSetMode("Defensive");
                    } else if (hudArea.getType().equals("robot_button_aggressive")) {
                        SoundClip cl = new SoundClip("Misc/Click");
                        hudManager.robotSetMode("Aggressive");
                    } else if (hudArea.getType().equals("robot_button_follow")) {
                        SoundClip cl = new SoundClip("Misc/Click");
                        hudManager.robotToggleFollow();
                    }
                }
            }
        }

        registry.setInvSlotFrom(hudFrom, selectedStart);
    }

    @Override
    public void HUDAreaRightClicked(HUDArea ha) {
        HUDArea hudArea = null;
        int playerInventorySlot = 0;

        int selectedStart = registry.getInvSlotFrom();

        for (int i = 0; i < hudAreas.size(); i++) {

            playerInventorySlot = i + registry.getPlayerInventorySize(registry.getPlayerManager().getCurrentPlayer()) - SLOTS;

            hudArea = hudAreas.get(i);
            if (hudArea == ha) {
                int maxQty = registry.getPlaverInventorySlotQty(playerInventorySlot);
                if (selectedStart == playerInventorySlot && registry.getSplitCount() > 0) {
                    if (maxQty > registry.getSplitCount()) {
                        if (shiftKeyPressed) {
                            registry.setSplitCount(registry.getSplitCount() + 10);
                        } else {
                            registry.setSplitCount(registry.getSplitCount() + 1);
                        }
                        if (registry.getSplitCount() > maxQty) {
                            registry.setSplitCount(maxQty);
                        }
                        hudManager.setCursorImageAndText(hudArea.getFGImage(), Integer.toString(registry.getSplitCount()));
                    }
                } else {
                    selectedStart = playerInventorySlot;
                    if (shiftKeyPressed) {
                        registry.setSplitCount(10);
                    } else {
                        registry.setSplitCount(1);
                    }
                    if (registry.getSplitCount() > maxQty) {
                        registry.setSplitCount(maxQty);
                    }
                    hudManager.setCursorImageAndText(hudArea.getFGImage(), Integer.toString(registry.getSplitCount()));
                }
            }
        }

        registry.setInvSlotFrom("QuickBar", selectedStart);
    }

    @Override
    public void shiftPressed() {
        shiftKeyPressed = true;
    }

    @Override
    public void shiftRelease() {
        shiftKeyPressed = false;
    }

    @Override
    public void HUDAreaReleased(HUDArea ha) {
        int playerInventorySlot = 0;
        int selectedStart = registry.getInvSlotFrom();
        Inventory robotInventory = registry.getRobotInventory();

        if (selectedStart > -1) {
            HUDArea hudAreaTo = null;

            if (registry.getSplitCount() < 1) {
                for (int i = 0; i < hudAreas.size(); i++) {
                    hudAreaTo = hudAreas.get(i);

                    playerInventorySlot = i + registry.getPlayerInventorySize(registry.getPlayerManager().getCurrentPlayer()) - SLOTS;

                    if (hudAreaTo == ha) {
                        if (hudAreaTo.getType().equals("slot") && selectedStart >= 0) {
                            if (registry.getInvHUDFrom().equals("Container")) {
                                ItemContainer itemContainer = registry.getInvItemContainerFrom();
                                if (itemContainer != null) {
                                    String itemName = itemContainer.getInventory().getNameFromSlot(selectedStart);
                                    int qty = itemContainer.getInventory().getQtyFromSlot(selectedStart);

                                    if (!itemName.isEmpty() && qty > 0) {
                                        int oldQty = qty;
                                        if (hudManager.playerAddItem(playerInventorySlot, itemName, qty) < oldQty) {
                                            itemContainer.deleteInventory(selectedStart, 0);
                                        }
                                    }
                                }
                            } else if (registry.getInvHUDFrom().equals("PlayerContainer")) {
                                PlayerContainer playerContainer = registry.getInvPlayerContainerFrom();
                                if (playerContainer != null) {
                                    String itemName = playerContainer.getInventory().getNameFromSlot(selectedStart);
                                    int qty = playerContainer.getInventory().getQtyFromSlot(selectedStart);

                                    if (!itemName.isEmpty() && qty > 0) {
                                        int oldQty = qty;
                                        if (hudManager.playerAddItem(playerInventorySlot, itemName, qty) < oldQty) {
                                            playerContainer.deleteInventory(selectedStart, 0);
                                        }
                                    }
                                }
                            } else if (registry.getInvHUDFrom().equals("Farm")) {
                                Farm farm = registry.getInvFarmFrom();
                                if (farm != null) {
                                    String itemName = farm.getInventory().getNameFromSlot(selectedStart);
                                    int qty = farm.getInventory().getQtyFromSlot(selectedStart);

                                    if (!itemName.isEmpty() && qty > 0) {
                                        int oldQty = qty;
                                        if (hudManager.playerAddItem(playerInventorySlot, itemName, qty) < oldQty) {
                                            farm.deleteInventory(selectedStart, 0);
                                        }
                                    }
                                }
                            } else if (registry.getInvHUDFrom().equals("MasterHead")) {
                                hudManager.playerUnEquipToInventory("head", playerInventorySlot);
                            } else if (registry.getInvHUDFrom().equals("MasterChest")) {
                                hudManager.playerUnEquipToInventory("chest", playerInventorySlot);
                            } else if (registry.getInvHUDFrom().equals("MasterLegs")) {
                                hudManager.playerUnEquipToInventory("legs", playerInventorySlot);
                            } else if (registry.getInvHUDFrom().equals("MasterFeet")) {
                                hudManager.playerUnEquipToInventory("feet", playerInventorySlot);
                            } else if (registry.getInvHUDFrom().equals("QuickBarRobot")) {
                                if (robotInventory != null) {
                                    String itemName = robotInventory.getNameFromSlot(selectedStart);
                                    int qty = robotInventory.getQtyFromSlot(selectedStart);

                                    if (!itemName.isEmpty() && qty > 0) {
                                        int oldQty = qty;
                                        if (hudManager.playerAddItem(playerInventorySlot, itemName, qty) < oldQty) {
                                            robotInventory.deleteInventory(selectedStart, 0);
                                        }
                                    }
                                }
                            } else {
                                if (selectedStart == playerInventorySlot) {
                                    hudManager.setPlayerSelectedItem(i);
                                } else {
                                    hudManager.playerSwapInventory(selectedStart, playerInventorySlot);
                                }
                            }
                        } else if (hudAreaTo.getType().length() == 11 && selectedStart >= 0) {
                            if (hudAreaTo.getType().substring(0, 10).equals("robot_slot")) {
                                int slotNumber = Integer.parseInt(hudAreaTo.getType().substring(10, 11)) - 1;
                                String itemName = hudManager.playerGetInventoryItemName(selectedStart);
                                String itemType = hudManager.playerGetInventoryItemCategory(selectedStart);
                                int qty = hudManager.playerGetInventoryQty(selectedStart);
                                int level = hudManager.playerGetInventoryLevel(selectedStart);

                                if (itemType.equals("Attachment") && registry.getRobotInventorySize() >= (slotNumber + 1)) {
                                    if (registry.getInvHUDFrom().equals("Container")) {
                                        ItemContainer itemContainer = registry.getInvItemContainerFrom();
                                        if (itemContainer != null) {
                                            if (robotInventory.getQtyFromSlot(slotNumber) > 0) {
                                                String oldItemName = robotInventory.getNameFromSlot(slotNumber);
                                                int oldQty = robotInventory.getQtyFromSlot(slotNumber);
                                                int oldLevel = robotInventory.getLevelFromSlot(slotNumber);
                                                if (!oldItemName.isEmpty() && oldQty > 0) {
                                                    robotInventory.deleteInventory(slotNumber, 0);
                                                    robotInventory.addToInventory(slotNumber, itemName, qty, level);

                                                    itemContainer.deleteInventory(selectedStart, 0);
                                                    itemContainer.addItem(selectedStart, oldItemName, oldQty, oldLevel);
                                                }
                                            } else {
                                                itemContainer.deleteInventory(selectedStart, 0);
                                                robotInventory.addToInventory(slotNumber, itemName, qty);
                                            }

                                        }
                                    } else if (registry.getInvHUDFrom().equals("PlayerContainer")) {
                                        PlayerContainer playerContainer = registry.getInvPlayerContainerFrom();
                                        if (playerContainer != null) {
                                            if (robotInventory.getQtyFromSlot(slotNumber) > 0) {
                                                String oldItemName = robotInventory.getNameFromSlot(slotNumber);
                                                int oldQty = robotInventory.getQtyFromSlot(slotNumber);
                                                int oldLevel = robotInventory.getLevelFromSlot(slotNumber);
                                                if (!oldItemName.isEmpty() && oldQty > 0) {
                                                    robotInventory.deleteInventory(slotNumber, 0);
                                                    robotInventory.addToInventory(slotNumber, itemName, qty, level);

                                                    playerContainer.deleteInventory(selectedStart, 0);
                                                    playerContainer.addItem(selectedStart, oldItemName, oldQty, oldLevel);
                                                }
                                            } else {
                                                playerContainer.deleteInventory(selectedStart, 0);
                                                robotInventory.addToInventory(slotNumber, itemName, qty);
                                            }
                                        }
                                    } else if (registry.getInvHUDFrom().equals("QuickBar")) {
                                        if (!registry.getIsQuickBarLocked()) {
                                            if (!itemName.isEmpty() && qty > 0 && robotInventory != null) {
                                                if (robotInventory.getQtyFromSlot(slotNumber) > 0) {
                                                    String oldItemName = robotInventory.getNameFromSlot(slotNumber);
                                                    int oldQty = robotInventory.getQtyFromSlot(slotNumber);
                                                    if (!oldItemName.isEmpty() && oldQty > 0) {
                                                        robotInventory.deleteInventory(slotNumber, 0);
                                                        robotInventory.addToInventory(slotNumber, itemName, qty);

                                                        hudManager.playerDeleteInventory(selectedStart, 0);
                                                        hudManager.playerAddItem(selectedStart, oldItemName, oldQty);
                                                    }
                                                } else {
                                                    hudManager.playerDeleteInventory(selectedStart, 0);
                                                    robotInventory.addToInventory(slotNumber, itemName, qty);
                                                }
                                            }
                                        }
                                    } else if (registry.getInvHUDFrom().equals("QuickBarRobot")) {
                                        if (selectedStart != playerInventorySlot) {
                                            robotInventory.swapInventoryLocations(selectedStart, slotNumber);
                                        }
                                    } else if (registry.getInvHUDFrom().equals("MasterHead")) {
                                        if (robotInventory != null) {
                                            if (robotInventory.addToInventory(slotNumber, registry.getPlaverHeadSlotName(registry.getPlayerManager().getCurrentPlayer()), qty) == 0) {
                                                hudManager.playerEquipHead("", 1);
                                            }
                                        }
                                    } else if (registry.getInvHUDFrom().equals("MasterChest")) {
                                        if (robotInventory != null) {
                                            if (robotInventory.addToInventory(slotNumber, registry.getPlaverChestSlotName(registry.getPlayerManager().getCurrentPlayer()), qty) == 0) {
                                                hudManager.playerEquipChest("", 1);
                                            }
                                        }
                                    } else if (registry.getInvHUDFrom().equals("MasterLegs")) {
                                        if (robotInventory != null) {
                                            if (robotInventory.addToInventory(slotNumber, registry.getPlaverLegsSlotName(registry.getPlayerManager().getCurrentPlayer()), qty) == 0) {
                                                hudManager.playerEquipLegs("", 1);
                                            }
                                        }
                                    } else if (registry.getInvHUDFrom().equals("MasterFeet")) {
                                        if (robotInventory != null) {
                                            if (robotInventory.addToInventory(slotNumber, registry.getPlaverFeetSlotName(registry.getPlayerManager().getCurrentPlayer()), qty) == 0) {
                                                hudManager.playerEquipFeet("", 1);
                                            }
                                        }
                                    } else {
                                        if (!itemName.isEmpty() && qty > 0 && robotInventory != null) {
                                            if (robotInventory.getQtyFromSlot(slotNumber) > 0) {
                                                String oldItemName = robotInventory.getNameFromSlot(slotNumber);
                                                int oldQty = robotInventory.getQtyFromSlot(slotNumber);
                                                if (!oldItemName.isEmpty() && oldQty > 0) {
                                                    robotInventory.deleteInventory(slotNumber, 0);
                                                    robotInventory.addToInventory(slotNumber, itemName, qty);

                                                    hudManager.playerDeleteInventory(selectedStart, 0);
                                                    hudManager.playerAddItem(selectedStart, oldItemName, oldQty);
                                                }
                                            } else {
                                                hudManager.playerDeleteInventory(selectedStart, 0);
                                                robotInventory.addToInventory(slotNumber, itemName, qty);
                                            }
                                        }
                                    }
                                }
                                PlayerManager pm = registry.getPlayerManager();
                                if(pm != null) {
                                    Player p = pm.getCurrentPlayer();
                                    if(p != null) {
                                        p.getRobot().updateArmorPoints();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            registry.setSplitCount(0);
        }

        registry.setInvSlotFrom("", selectedStart);
    }
}