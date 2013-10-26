package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.placeable.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;

public class HUDPlayerContainer extends HUD {

    private PlayerContainer playerContainer;
    private final static int INVENTORY_ROWS = 3;
    private final static int INVENTORY_COLS = 5;
    private final static int INV_SLOT_START_X = 10;
    private final static int INV_SLOT_START_Y = 46;
    private final static int INV_SLOT_WIDTH = 40;
    private final static int INV_SLOT_HEIGHT = 40;
    private final static int INV_SLOT_SPACING_X = 3;
    private final static int INV_SLOT_SPACING_Y = 3;
    private final static int INV_SLOT_TEXT_OFFSET_0 = 32;
    private final static int INV_SLOT_TEXT_OFFSET_10 = 25;
    private final static int INV_SLOT_TEXT_OFFSET_100 = 18;
    private final static int INV_SLOT_TEXT_Y = 37;
    private final static int QUICK_LOOT_WIDTH = 31;
    private final static int QUICK_LOOT_HEIGHT = 32;
    private final static int QUICK_LOOT_X = 154;
    private final static int QUICK_LOOT_Y = 9;
    private final static int BUTTON_CLOSE_WIDTH = 42;
    private final static int BUTTON_CLOSE_HEIGHT = 42;
    private final static int BUTTON_CLOSE_X = 185;
    private final static int BUTTON_CLOSE_Y = 0;

    public HUDPlayerContainer(HUDManager hm, PlayerContainer pc, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        playerContainer = pc;

        int totalRows = (int) Math.ceil((float) playerContainer.getInventorySize() / (float) INVENTORY_COLS);

        if (totalRows >= 1 && totalRows <= 3) {
            setImage("HUD/PlayerContainer/BG" + totalRows);
        } else {
            setImage("HUD/PlayerContainer/BG");
        }

        //inventory slots
        int slotCount = 0;
        int slotX, slotY = 0;
        HUDArea hudArea = null;
        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                slotX = INV_SLOT_START_X + (col * INV_SLOT_WIDTH) + (col * INV_SLOT_SPACING_X);
                slotY = INV_SLOT_START_Y + (row * INV_SLOT_HEIGHT) + (row * INV_SLOT_SPACING_Y);

                hudArea = addArea(slotX, slotY, INV_SLOT_WIDTH, INV_SLOT_HEIGHT, "slot");
                hudArea.setFont("SansSerif", Font.BOLD, 12);
                hudArea.setImage("HUD/PlayerContainer/Slot");

                slotCount++;
                if (slotCount >= playerContainer.getInventorySize()) {
                    break;
                }
            }
        }

        //quickloot
        hudArea = addArea(QUICK_LOOT_X, QUICK_LOOT_Y, QUICK_LOOT_WIDTH, QUICK_LOOT_HEIGHT, "quickloot");
        hudArea.setImage("HUD/Common/QuickLoot");

        //close
        hudArea = addArea(BUTTON_CLOSE_X, BUTTON_CLOSE_Y, BUTTON_CLOSE_WIDTH, BUTTON_CLOSE_HEIGHT, "close");
        hudArea.setImage("HUD/PlayerContainer/ButtonClose");

        shouldRender = false;
        isContainer = true;
    }

    @Override
    public void update() {
        if (shouldRender) {
            HUDArea hudArea;

            //update slots
            for (int i = 0; i < hudAreas.size(); i++) {
                hudArea = hudAreas.get(i);
                if (hudArea.getType().equals("slot")) {
                    String hudAreaImage = registry.getPlayerContainerInventorySlotImage(playerContainer, i);

                    if (hudAreaImage != null) {
                        hudArea.setFGImage(hudAreaImage);
                        if (hudArea.isInside(registry.getMousePosition())) {
                            registry.setStatusText(registry.getPlayerContainerInventorySlotDescription(playerContainer, i));
                        }
                    }
                    int hudAreaQty = registry.getPlayerContainerInventorySlotQty(playerContainer, i);
                    if (hudAreaQty > 1) {
                        hudArea.setText(String.valueOf(hudAreaQty));
                        if (hudAreaQty < 10) {
                            hudArea.setTextXY(INV_SLOT_TEXT_OFFSET_0, INV_SLOT_TEXT_Y);
                        } else if (hudAreaQty < 100) {
                            hudArea.setTextXY(INV_SLOT_TEXT_OFFSET_10, INV_SLOT_TEXT_Y);
                        } else {
                            hudArea.setTextXY(INV_SLOT_TEXT_OFFSET_100, INV_SLOT_TEXT_Y);
                        }
                    } else {
                        hudArea.setText("");
                        hudArea.setTextXY(INV_SLOT_TEXT_OFFSET_0, INV_SLOT_TEXT_Y);
                    }
                }
            }

            //check to see if player is too far away
            if (registry.getPlayerManager().getCurrentPlayer().getCenterPoint().distance(playerContainer.getCenterPoint()) > registry.getMaxContainerDistance()) {
                shouldRender = false;
            }
        }

        if (playerContainer == null) {
            isDirty = true;
        } else {
            if (playerContainer.getIsDirty()) {
                isDirty = true;
            }
        }

        super.update();
    }

    @Override
    public void HUDAreaClicked(HUDArea ha) {
        HUDArea hudArea = null;

        int selectedStart = registry.getInvSlotFrom();

        for (int i = 0; i < hudAreas.size(); i++) {
            hudArea = hudAreas.get(i);
            if (hudArea == ha) {
                selectedStart = i;
                hudManager.setCursorImageAndText(hudArea.getFGImage(), hudArea.getText());

                if (hudArea.getType().equals("close")) {
                    shouldRender = false;
                } else if (hudArea.getType().equals("quickloot")) {
                    SoundClip cl = new SoundClip("Misc/Click");
                    playerContainer.quickLoot();
                }
            }
        }

        registry.setInvSlotFrom("PlayerContainer", playerContainer, selectedStart);
    }

    @Override
    public void HUDAreaReleased(HUDArea ha) {
        if (shouldRender) {
            int selectedStart = registry.getInvSlotFrom();

            if (selectedStart > -1) {
                HUDArea hudAreaTo = null;

                for (int i = 0; i < hudAreas.size(); i++) {
                    hudAreaTo = hudAreas.get(i);

                    if (hudAreaTo == ha) {
                        if (hudAreaTo.getType().equals("slot") && selectedStart >= 0) {
                            String itemName = hudManager.playerGetInventoryItemName(selectedStart);
                            int qty = hudManager.playerGetInventoryQty(selectedStart);
                            int level = hudManager.playerGetInventoryLevel(selectedStart);

                            if (registry.getInvHUDFrom().equals("PlayerContainer")) {
                                if (playerContainer != null) {
                                    playerContainer.swapInventory(selectedStart, i);
                                }
                            } else if (registry.getInvHUDFrom().equals("QuickBarRobot")) {
                                Inventory robotInventory = registry.getRobotInventory();
                                itemName = robotInventory.getNameFromSlot(selectedStart);
                                qty = robotInventory.getQtyFromSlot(selectedStart);
                                if (!itemName.isEmpty() && qty > 0 && robotInventory != null) {
                                    if (playerContainer.addItem(i, itemName, qty, level)) {
                                        robotInventory.deleteInventory(selectedStart, 0);
                                    }
                                }
                            } else if (registry.getInvHUDFrom().equals("QuickBar")) {
                                if (!registry.getIsQuickBarLocked()) {
                                    if (!itemName.isEmpty() && qty > 0 && playerContainer != null) {
                                        hudManager.playerDeleteInventory(selectedStart, 0);
                                        playerContainer.addItem(i, itemName, qty, level);
                                    }
                                }
                            } else if (registry.getInvHUDFrom().equals("MasterHead")) {
                                if (playerContainer != null) {
                                    if (playerContainer.addItem(i, registry.getPlaverHeadSlotName(registry.getPlayerManager().getCurrentPlayer()), 1, registry.getPlaverHeadSlotLevel(registry.getPlayerManager().getCurrentPlayer()))) {
                                        hudManager.playerEquipHead("", 1);
                                    }
                                }
                            } else if (registry.getInvHUDFrom().equals("MasterChest")) {
                                if (playerContainer != null) {
                                    if (playerContainer.addItem(i, registry.getPlaverChestSlotName(registry.getPlayerManager().getCurrentPlayer()), 1, registry.getPlaverChestSlotLevel(registry.getPlayerManager().getCurrentPlayer()))) {
                                        hudManager.playerEquipChest("", 1);
                                    }
                                }
                            } else if (registry.getInvHUDFrom().equals("MasterLegs")) {
                                if (playerContainer != null) {
                                    if (playerContainer.addItem(i, registry.getPlaverLegsSlotName(registry.getPlayerManager().getCurrentPlayer()), 1, registry.getPlaverLegsSlotLevel(registry.getPlayerManager().getCurrentPlayer()))) {
                                        hudManager.playerEquipLegs("", 1);
                                    }
                                }
                            } else if (registry.getInvHUDFrom().equals("MasterFeet")) {
                                if (playerContainer != null) {
                                    if (playerContainer.addItem(i, registry.getPlaverFeetSlotName(registry.getPlayerManager().getCurrentPlayer()), 1, registry.getPlaverFeetSlotLevel(registry.getPlayerManager().getCurrentPlayer()))) {
                                        hudManager.playerEquipFeet("", 1);
                                    }
                                }
                            } else {
                                if (!itemName.isEmpty() && qty > 0 && playerContainer != null) {
                                    if (playerContainer.addItem(i, itemName, qty, level)) {
                                        hudManager.playerDeleteInventory(selectedStart, 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            registry.setInvSlotFrom("", selectedStart);
        }
    }
}