package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.placeable.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;

public class HUDFarm extends HUD {

    private Farm farm;
    private final static int INVENTORY_ROWS = 1;
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

    public HUDFarm(HUDManager hm, Farm f, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        farm = f;

        int totalRows = (int) Math.ceil((float) f.getInventorySize() / (float) INVENTORY_COLS);

        if (totalRows >= 1 && totalRows <= 3) {
            setImage("HUD/Farm/BG" + totalRows);
        } else {
            setImage("HUD/Farm/BG");
        }

        //inventory slots
        int slotCount = 0;
        int slotX, slotY = 0;
        HUDArea hudArea = null;
        int row = 0;
        for (int col = 0; col < f.getInventorySize(); col++) {
            slotX = INV_SLOT_START_X + (col * INV_SLOT_WIDTH) + (col * INV_SLOT_SPACING_X);
            slotY = INV_SLOT_START_Y + (row * INV_SLOT_HEIGHT) + (row * INV_SLOT_SPACING_Y);

            hudArea = addArea(slotX, slotY, INV_SLOT_WIDTH, INV_SLOT_HEIGHT, "slot");
            hudArea.setFont("SansSerif", Font.BOLD, 12);
            hudArea.setImage("HUD/Farm/Slot");

            slotCount++;
            if (slotCount >= f.getInventorySize()) {
                break;
            }
        }

        //quickloot
        hudArea = addArea(QUICK_LOOT_X, QUICK_LOOT_Y, QUICK_LOOT_WIDTH, QUICK_LOOT_HEIGHT, "quickloot");
        hudArea.setImage("HUD/Common/QuickLoot");

        //close
        hudArea = addArea(BUTTON_CLOSE_X, BUTTON_CLOSE_Y, BUTTON_CLOSE_WIDTH, BUTTON_CLOSE_HEIGHT, "close");
        hudArea.setImage("HUD/Farm/ButtonClose");

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
                    String hudAreaImage = registry.getFarmInventorySlotImage(farm, i);

                    if (hudAreaImage != null) {
                        hudArea.setFGImage(hudAreaImage);
                        if (hudArea.isInside(registry.getMousePosition())) {
                            registry.setStatusText(registry.getFarmInventorySlotDescription(farm, i));
                        }
                    }
                    int hudAreaQty = registry.getFarmInventorySlotQty(farm, i);
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
            if (registry.getPlayerManager().getCurrentPlayer().getCenterPoint().distance(farm.getCenterPoint()) > registry.getMaxContainerDistance()) {
                shouldRender = false;
                farm.startTransformation();
            }
        }

        if (farm == null) {
            isDirty = true;
        } else {
            if (farm.getIsDirty()) {
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
                    farm.startTransformation();
                    shouldRender = false;
                } else if (hudArea.getType().equals("quickloot")) {
                    SoundClip cl = new SoundClip("Misc/Click");
                    farm.quickLoot();
                }
            }
        }

        registry.setInvSlotFrom("Farm", farm, selectedStart);
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
                            if (farm.getInventory().getQtyFromSlot(i) == 0) {
                                String itemName = hudManager.playerGetInventoryItemName(selectedStart);
                                int qty = hudManager.playerGetInventoryQty(selectedStart);
                                int level = hudManager.playerGetInventoryLevel(selectedStart);

                                if (itemName.contains("Seed")) {
                                    if (registry.getInvHUDFrom().equals("Farm")) {
                                        if (farm != null) {
                                            farm.swapInventory(selectedStart, i);
                                        }
                                    } else if (registry.getInvHUDFrom().equals("QuickBar")) {
                                        if (!registry.getIsQuickBarLocked()) {
                                            if (!itemName.isEmpty() && qty > 0 && farm != null) {
                                                hudManager.playerDeleteInventory(selectedStart, 1);
                                                farm.addItem(i, itemName, 1, level);
                                            }
                                        }
                                    } else {
                                        if (!itemName.isEmpty() && qty > 0 && farm != null) {
                                            if (farm.addItem(i, itemName, 1, level)) {
                                                hudManager.playerDeleteInventory(selectedStart, 1);
                                            }
                                        }
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

    @Override
    public void setShouldRender(boolean sr) {
        shouldRender = sr;
        if (!shouldRender) {
            farm.startTransformation();
        }
    }
}