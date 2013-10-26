package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.item.*;
import com.weem.epicinventor.placeable.*;

import java.awt.*;
import java.util.*;

public class HUDMaster extends HUD {

    private final static int INVENTORY_ROWS = 8;
    private final static int INVENTORY_COLS = 5;
    private final static int INV_SLOT_START_X = 11;
    private final static int INV_SLOT_START_Y = 37;
    private final static int INV_SLOT_WIDTH = 40;
    private final static int INV_SLOT_HEIGHT = 40;
    private final static int INV_SLOT_SPACING_X = 3;
    private final static int INV_SLOT_SPACING_Y = 3;
    private final static int INV_SLOT_TEXT_OFFSET_0 = 32;
    private final static int INV_SLOT_TEXT_OFFSET_10 = 25;
    private final static int INV_SLOT_TEXT_OFFSET_100 = 18;
    private final static int INV_SLOT_TEXT_Y = 37;
    private final static int INV_TRASH_START_X = 11;
    private final static int INV_TRASH_START_Y = 388;
    private final static int INV_TRASH_WIDTH = 212;
    private final static int INV_TRASH_HEIGHT = 25;
    private final static int PAPERDOLL_X = 328;
    private final static int PAPERDOLL_Y = 65;
    private final static int PAPERDOLL_HEAD_X = 270;
    private final static int PAPERDOLL_HEAD_Y = 54;
    private final static int PAPERDOLL_CHEST_X = 401;
    private final static int PAPERDOLL_CHEST_Y = 54;
    private final static int PAPERDOLL_LEGS_X = 270;
    private final static int PAPERDOLL_LEGS_Y = 117;
    private final static int PAPERDOLL_FEET_X = 401;
    private final static int PAPERDOLL_FEET_Y = 117;
    private final static int VALUE_WIDTH = 81;
    private final static int VALUE_HEIGHT = 28;
    private final static int HP_X = 272;
    private final static int HP_Y = 194;
    private final static int HP_TEXT_OFFSET_X = 5;
    private final static int HP_TEXT_OFFSET_Y = 20;
    private final static int AP_X = 363;
    private final static int AP_Y = 194;
    private final static int AP_TEXT_OFFSET_X = 5;
    private final static int AP_TEXT_OFFSET_Y = 20;
    private final static int ARROW_LEFT_X = 502;
    private final static int ARROW_LEFT_Y = 57;
    private final static int ARROW_RIGHT_X = 723;
    private final static int ARROW_RIGHT_Y = 57;
    private final static int ARROW_WIDTH = 7;
    private final static int ARROW_HEIGHT = 15;
    private final static int CRAFT_LEFT1_X = 519;
    private final static int CRAFT_LEFT1_Y = 50;
    private final static int CRAFT_LEFT1_SIZE = 30;
    private final static int CRAFT_LEFT2_X = 553;
    private final static int CRAFT_LEFT2_Y = 52;
    private final static int CRAFT_LEFT2_SIZE = 36;
    private final static int CRAFT_MAIN_X = 596;
    private final static int CRAFT_MAIN_Y = 56;
    private final static int CRAFT_RIGHT1_X = 643;
    private final static int CRAFT_RIGHT1_Y = 52;
    private final static int CRAFT_RIGHT1_SIZE = 36;
    private final static int CRAFT_RIGHT2_X = 683;
    private final static int CRAFT_RIGHT2_Y = 50;
    private final static int CRAFT_RIGHT2_SIZE = 30;
    private final static int REQUIRE1_X = 521;
    private final static int REQUIRE1_Y = 127;
    private final static int REQUIRE2_X = 571;
    private final static int REQUIRE2_Y = 127;
    private final static int REQUIRE3_X = 621;
    private final static int REQUIRE3_Y = 127;
    private final static int REQUIRE4_X = 671;
    private final static int REQUIRE4_Y = 127;
    private final static int CREATE_X = 562;
    private final static int CREATE_Y = 180;
    private final static int CREATE_WIDTH = 108;
    private final static int CREATE_HEIGHT = 41;
    private final static int CAT_PLACEABLES_X = 489;
    private final static int CAT_PLACEABLES_Y = 260;
    private final static int CAT_PLACEABLES_WIDTH = 87;
    private final static int CAT_ITEMS_X = 580;
    private final static int CAT_ITEMS_Y = 260;
    private final static int CAT_ITEMS_WIDTH = 67;
    private final static int CAT_TECH_X = 650;
    private final static int CAT_TECH_Y = 260;
    private final static int CAT_TECH_WIDTH = 92;
    private final static int CAT_HEIGHT = 31;
    private final static int TYPE_WEAPONS_X = 489;
    private final static int TYPE_WEAPONS_Y = 318;
    private final static int TYPE_WEAPONS_WIDTH = 80;
    private final static int TYPE_ARMOR_X = 576;
    private final static int TYPE_ARMOR_Y = 318;
    private final static int TYPE_ARMOR_WIDTH = 80;
    private final static int TYPE_OTHER_X = 662;
    private final static int TYPE_OTHER_Y = 318;
    private final static int TYPE_OTHER_WIDTH = 80;
    private final static int TYPE_HEIGHT = 31;
    private final static int HAVE_MATERIALS_X = 540;
    private final static int HAVE_MATERIALS_Y = 379;
    private final static int HAVE_MATERIALS_WIDTH = 152;
    private final static int HAVE_MATERIALS_HEIGHT = 31;
    private boolean isButtonCreateEnabled = false;
    private boolean isButtonPlaceablesEnabled = false;
    private boolean isButtonItemsEnabled = true;
    private boolean isButtonAttachmentsEnabled = false;
    private boolean isButtonPDevelopmentEnabled = true;
    private boolean isButtonPCombatEnabled = true;
    private boolean isButtonPOtherEnabled = true;
    private boolean isButtonIWeaponsEnabled = true;
    private boolean isButtonIArmorEnabled = true;
    private boolean isButtonIOtherEnabled = true;
    private boolean isButtonTDevelopmentEnabled = true;
    private boolean isButtonHaveMaterialsEnabled = false;
    private ArrayList<String> matchingItems;
    private ArrayList<String> requirements;
    private int currentItem = 0;
    private boolean shiftKeyPressed = false;
    private String[] craftDescriptions;
    private String[] requirementsDescriptions;

    public HUDMaster(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/Master/BG");

        //inventory slots
        int slotX, slotY = 0;
        HUDArea hudArea = null;
        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                slotX = INV_SLOT_START_X + (col * INV_SLOT_WIDTH) + (col * INV_SLOT_SPACING_X);
                slotY = INV_SLOT_START_Y + (row * INV_SLOT_HEIGHT) + (row * INV_SLOT_SPACING_Y);

                hudArea = addArea(slotX, slotY, INV_SLOT_WIDTH, INV_SLOT_HEIGHT, "inventory");
                hudArea.setFont("SansSerif", Font.BOLD, 12);
                hudArea.setImage("HUD/Master/Slot");
            }
        }

        //trash
        hudArea = addArea(INV_TRASH_START_X, INV_TRASH_START_Y, INV_TRASH_WIDTH, INV_TRASH_HEIGHT, "trash");
        hudArea.setImage("HUD/Master/Trash");

        //paperdoll
        hudArea = addArea(PAPERDOLL_HEAD_X, PAPERDOLL_HEAD_Y, INV_SLOT_WIDTH, INV_SLOT_HEIGHT, "head");
        hudArea.setImage("HUD/Master/Slot");
        hudArea = addArea(PAPERDOLL_CHEST_X, PAPERDOLL_CHEST_Y, INV_SLOT_WIDTH, INV_SLOT_HEIGHT, "chest");
        hudArea.setImage("HUD/Master/Slot");
        hudArea = addArea(PAPERDOLL_LEGS_X, PAPERDOLL_LEGS_Y, INV_SLOT_WIDTH, INV_SLOT_HEIGHT, "legs");
        hudArea.setImage("HUD/Master/Slot");
        hudArea = addArea(PAPERDOLL_FEET_X, PAPERDOLL_FEET_Y, INV_SLOT_WIDTH, INV_SLOT_HEIGHT, "feet");
        hudArea.setImage("HUD/Master/Slot");

        //hp
        hudArea = addArea(HP_X, HP_Y, VALUE_WIDTH, VALUE_HEIGHT, "hp");
        hudArea.setFont("SansSerif", Font.BOLD, 16);
        hudArea.setImage("HUD/Master/Value");

        //armor
        hudArea = addArea(AP_X, AP_Y, VALUE_WIDTH, VALUE_HEIGHT, "ap");
        hudArea.setFont("SansSerif", Font.BOLD, 16);
        hudArea.setImage("HUD/Master/Value");

        //arrows
        hudArea = addArea(ARROW_LEFT_X, ARROW_LEFT_Y, ARROW_WIDTH, ARROW_HEIGHT, "arrow_left");
        hudArea.setImage("HUD/Master/ArrowLeft");
        hudArea = addArea(ARROW_RIGHT_X, ARROW_RIGHT_Y, ARROW_WIDTH, ARROW_HEIGHT, "arrow_right");
        hudArea.setImage("HUD/Master/ArrowRight");

        //crafting items
        hudArea = addArea(CRAFT_LEFT1_X, CRAFT_LEFT1_Y, CRAFT_LEFT1_SIZE, CRAFT_LEFT1_SIZE, "craft1");
        hudArea.setImage("HUD/Master/Slot");
        hudArea = addArea(CRAFT_LEFT2_X, CRAFT_LEFT2_Y, CRAFT_LEFT2_SIZE, CRAFT_LEFT2_SIZE, "craft2");
        hudArea.setImage("HUD/Master/Slot");
        hudArea = addArea(CRAFT_MAIN_X, CRAFT_MAIN_Y, INV_SLOT_WIDTH, INV_SLOT_HEIGHT, "craft3");
        hudArea.setImage("HUD/Master/Slot");
        hudArea = addArea(CRAFT_RIGHT1_X, CRAFT_RIGHT1_Y, CRAFT_RIGHT1_SIZE, CRAFT_RIGHT1_SIZE, "craft4");
        hudArea.setImage("HUD/Master/Slot");
        hudArea = addArea(CRAFT_RIGHT2_X, CRAFT_RIGHT2_Y, CRAFT_RIGHT2_SIZE, CRAFT_RIGHT2_SIZE, "craft5");
        hudArea.setImage("HUD/Master/Slot");

        //crafting requirements
        hudArea = addArea(REQUIRE1_X, REQUIRE1_Y, INV_SLOT_WIDTH, INV_SLOT_HEIGHT, "requirement1");
        hudArea.setFont("SansSerif", Font.BOLD, 12);
        hudArea.setImage("HUD/Master/Slot");
        hudArea = addArea(REQUIRE2_X, REQUIRE2_Y, INV_SLOT_WIDTH, INV_SLOT_HEIGHT, "requirement2");
        hudArea.setFont("SansSerif", Font.BOLD, 12);
        hudArea.setImage("HUD/Master/Slot");
        hudArea = addArea(REQUIRE3_X, REQUIRE3_Y, INV_SLOT_WIDTH, INV_SLOT_HEIGHT, "requirement3");
        hudArea.setFont("SansSerif", Font.BOLD, 12);
        hudArea.setImage("HUD/Master/Slot");
        hudArea = addArea(REQUIRE4_X, REQUIRE4_Y, INV_SLOT_WIDTH, INV_SLOT_HEIGHT, "requirement4");
        hudArea.setFont("SansSerif", Font.BOLD, 12);
        hudArea.setImage("HUD/Master/Slot");

        //create
        hudArea = addArea(CREATE_X, CREATE_Y, CREATE_WIDTH, CREATE_HEIGHT, "create");
        hudArea.setImage("HUD/Master/Create");

        //crafting categories
        hudArea = addArea(CAT_PLACEABLES_X, CAT_PLACEABLES_Y, CAT_PLACEABLES_WIDTH, CAT_HEIGHT, "placeables");
        hudArea.setImage("HUD/Master/Placeables");
        hudArea = addArea(CAT_ITEMS_X, CAT_ITEMS_Y, CAT_ITEMS_WIDTH, CAT_HEIGHT, "items");
        hudArea.setImage("HUD/Master/ItemsOn");
        hudArea = addArea(CAT_TECH_X, CAT_TECH_Y, CAT_TECH_WIDTH, CAT_HEIGHT, "attachments");
        hudArea.setImage("HUD/Master/Attachments");

        //crafting type - placeables
        hudArea = addArea(TYPE_WEAPONS_X, TYPE_WEAPONS_Y, TYPE_WEAPONS_WIDTH, TYPE_HEIGHT, "p_development");
        hudArea.setImage("HUD/Master/DevelopmentOn");
        hudArea.setIsActive(false);
        hudArea = addArea(TYPE_ARMOR_X, TYPE_ARMOR_Y, TYPE_ARMOR_WIDTH, TYPE_HEIGHT, "p_combat");
        hudArea.setImage("HUD/Master/CombatOn");
        hudArea.setIsActive(false);
        hudArea = addArea(TYPE_OTHER_X, TYPE_OTHER_Y, TYPE_OTHER_WIDTH, TYPE_HEIGHT, "p_other");
        hudArea.setImage("HUD/Master/OtherOn");
        hudArea.setIsActive(false);

        //crafting type - items
        hudArea = addArea(TYPE_WEAPONS_X, TYPE_WEAPONS_Y, TYPE_WEAPONS_WIDTH, TYPE_HEIGHT, "i_weapons");
        hudArea.setImage("HUD/Master/WeaponsOn");
        hudArea = addArea(TYPE_ARMOR_X, TYPE_ARMOR_Y, TYPE_ARMOR_WIDTH, TYPE_HEIGHT, "i_armor");
        hudArea.setImage("HUD/Master/ArmorOn");
        hudArea = addArea(TYPE_OTHER_X, TYPE_OTHER_Y, TYPE_OTHER_WIDTH, TYPE_HEIGHT, "i_other");
        hudArea.setImage("HUD/Master/OtherOn");

        //crafting type - dev
        hudArea = addArea(TYPE_ARMOR_X, TYPE_ARMOR_Y, TYPE_ARMOR_WIDTH, TYPE_HEIGHT, "t_development");
        hudArea.setImage("HUD/Master/DevelopmentOn");
        hudArea.setIsActive(false);

        //crafting - craftable
        hudArea = addArea(HAVE_MATERIALS_X, HAVE_MATERIALS_Y, HAVE_MATERIALS_WIDTH, HAVE_MATERIALS_HEIGHT, "have_materials");
        hudArea.setImage("HUD/Master/HaveMaterials");
        hudArea.setIsActive(true);

        craftDescriptions = new String[5];
        craftDescriptions[0] = "";
        craftDescriptions[1] = "";
        craftDescriptions[2] = "";
        craftDescriptions[3] = "";
        craftDescriptions[4] = "";

        requirementsDescriptions = new String[5];
        requirementsDescriptions[0] = "";
        requirementsDescriptions[1] = "";
        requirementsDescriptions[2] = "";
        requirementsDescriptions[3] = "";

        update();

        LoadMatchingItems();
        shouldRender = false;
    }

    private void LoadMatchingItems() {
        synchronized (this) {
            //update matching items in crafting window
            String category = "";
            if (isButtonPlaceablesEnabled) {
                category = "Placeable";
            } else if (isButtonItemsEnabled) {
                category = "Item";
            } else if (isButtonAttachmentsEnabled) {
                category = "Attachment";
            }

            String types = "";
            if (isButtonPDevelopmentEnabled && isButtonPlaceablesEnabled) {
                types += "|Development|";
            }
            if (isButtonPCombatEnabled && isButtonPlaceablesEnabled) {
                types += "|Combat|";
            }
            if (isButtonPOtherEnabled && isButtonPlaceablesEnabled) {
                types += "|Other|";
            }
            if (isButtonIWeaponsEnabled && isButtonItemsEnabled) {
                types += "|Weapon|";
            }
            if (isButtonIArmorEnabled && isButtonItemsEnabled) {
                types += "|Armor|";
            }
            if (isButtonIOtherEnabled && isButtonItemsEnabled) {
                types += "|Other|";
                types += "|Usable|";
            }
            if (isButtonTDevelopmentEnabled && isButtonAttachmentsEnabled) {
                types += "|Development|";
            }

            matchingItems = new ArrayList<String>(hudManager.getItemTypeList(category, types));
            if (matchingItems != null) {
                if (isButtonHaveMaterialsEnabled) {
                    ArrayList<String> tmp = new ArrayList<String>();
                    for (int i = 0; i < matchingItems.size(); i++) {
                        if (canCreate(matchingItems.get(i))) {
                            tmp.add(matchingItems.get(i));
                        }
                    }
                    matchingItems = tmp;
                }
                currentItem = 0;

                showMatchingItems();
            }
        }
    }

    private boolean canCreate(String itemName) {
        boolean canCreate = false;

        synchronized (this) {
            requirements = new ArrayList<String>(hudManager.getItemTypeRequirements(itemName));

            if (requirements != null) {
                canCreate = true;

                for (int i = 0; i < requirements.size(); i++) {
                    String[] parts = requirements.get(i).toString().split(":");
                    if (parts.length == 2) {
                        HUDArea hudArea;
                        int qtyOnHand = 0;
                        int qtyNeeded = Integer.parseInt(parts[1]);

                        boolean isPlaceable = false;
                        ItemType it = registry.getItemType(parts[0]);
                        if (it != null) {
                            if (it.getCategory().equals("Placeable")) {
                                isPlaceable = true;
                            }
                        }

                        if (isPlaceable) {
                            if (registry.getPlaceableManager().getActivatedCount(parts[0]) <= 0) {
                                canCreate = false;
                            }
                        } else {
                            for (int x = 0; x < hudAreas.size(); x++) {
                                hudArea = hudAreas.get(x);
                                //if (hudArea.getType().equals("inventory")) {
                                if (registry.getPlaverInventorySlotImage(x) != null) {
                                    if (registry.getPlaverInventorySlotImage(x).equals("Items/" + parts[0])) {
                                        qtyOnHand += registry.getPlaverInventorySlotQty(x);
                                    }
                                }
                                //}
                            }
                            if (qtyOnHand < qtyNeeded) {
                                canCreate = false;
                            }
                        }
                    }
                }
            }
        }

        return canCreate;
    }

    private void checkIfCanCreate() {
        boolean canCreate = false;

        synchronized (this) {
            if (matchingItems != null) {
                if (matchingItems.size() > 0) {
                    String item = matchingItems.get(currentItem);
                    if (canCreate(item)) {
                        canCreate = true;
                    }
                }
            }
            if (canCreate) {
                getHUDAreaByType("create").setImage("HUD/Master/CreateOn");
            } else {
                getHUDAreaByType("create").setImage("HUD/Master/Create");
            }
        }
    }

    private void LoadRequirements() {
        requirements = null;

        synchronized (this) {
            if (matchingItems != null) {
                if (matchingItems.size() > 0) {
                    requirements = hudManager.getItemTypeRequirements(matchingItems.get(currentItem));
                }

                for (int i = 1; i <= 4; i++) {
                    getHUDAreaByType("requirement" + i).setImage("");
                    getHUDAreaByType("requirement" + i).setFGImage("");
                    getHUDAreaByType("requirement" + i).setText("");
                    requirementsDescriptions[i - 1] = "";
                }

                if (requirements != null) {
                    setImage("HUD/Master/BG" + requirements.size());

                    for (int i = 0; i < requirements.size(); i++) {
                        String[] parts = requirements.get(i).toString().split(":");
                        if (parts.length == 2) {
                            //add the requirement slot
                            HUDArea hudArea = getHUDAreaByType("requirement" + (i + 1));

                            hudArea.setImage("HUD/Master/Slot");
                            hudArea.setFGImage("Items/" + parts[0]);

                            ItemType it = registry.getItemType(parts[0]);
                            if (it != null) {
                                requirementsDescriptions[i] = it.getDescription();
                            }

                            int qty = Integer.parseInt(parts[1]);
                            if (qty > 0) {
                                hudArea.setText(String.valueOf(qty));
                                if (qty < 10) {
                                    hudArea.setTextXY(INV_SLOT_TEXT_OFFSET_0, INV_SLOT_TEXT_Y);
                                } else if (qty < 100) {
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
                } else {
                    setImage("HUD/Master/BG");
                }

                checkIfCanCreate();
            }
        }
    }

    private void showMatchingItems() {
        int position = currentItem;
        ArrayList itemsShown = new ArrayList();

        synchronized (this) {
            if (matchingItems != null) {
                if ((matchingItems.size() - 1) >= position) {
                    itemsShown.add(position);
                    getHUDAreaByType("craft3").setImage("Items/" + matchingItems.get(position));
                    ItemType it = registry.getItemType(matchingItems.get(position));
                    if (it != null) {
                        craftDescriptions[2] = it.getDescription();
                    }
                } else {
                    getHUDAreaByType("craft3").setImage("");
                    craftDescriptions[2] = "";
                }

                position++;
                if (position > (matchingItems.size() - 1)) {
                    position = 0;
                }

                if ((matchingItems.size() - 1) >= position && !itemsShown.contains(position)) {
                    itemsShown.add(position);
                    getHUDAreaByType("craft4").setImage("Items/" + matchingItems.get(position));
                    ItemType it = registry.getItemType(matchingItems.get(position));
                    if (it != null) {
                        craftDescriptions[3] = it.getDescription();
                    }
                } else {
                    getHUDAreaByType("craft4").setImage("");
                    craftDescriptions[3] = "";
                }

                position++;
                if (position > (matchingItems.size() - 1)) {
                    position = 0;
                }

                if ((matchingItems.size() - 1) >= position && !itemsShown.contains(position)) {
                    itemsShown.add(position);
                    getHUDAreaByType("craft5").setImage("Items/" + matchingItems.get(position));
                    ItemType it = registry.getItemType(matchingItems.get(position));
                    if (it != null) {
                        craftDescriptions[4] = it.getDescription();
                    }
                } else {
                    getHUDAreaByType("craft5").setImage("");
                    craftDescriptions[4] = "";
                }

                position = (currentItem - 1);
                if (position < 0) {
                    position += matchingItems.size();
                }

                if (position >= 0 && position < matchingItems.size() && !itemsShown.contains(position)) {
                    itemsShown.add(position);
                    getHUDAreaByType("craft2").setImage("Items/" + matchingItems.get(position));
                    ItemType it = registry.getItemType(matchingItems.get(position));
                    if (it != null) {
                        craftDescriptions[1] = it.getDescription();
                    }
                } else {
                    getHUDAreaByType("craft2").setImage("");
                    craftDescriptions[1] = "";
                }

                position = (currentItem - 2);
                if (position < 0) {
                    position += matchingItems.size();
                }

                if (position >= 0 && position < matchingItems.size() && !itemsShown.contains(position)) {
                    itemsShown.add(position);
                    getHUDAreaByType("craft1").setImage("Items/" + matchingItems.get(position));
                    ItemType it = registry.getItemType(matchingItems.get(position));
                    if (it != null) {
                        craftDescriptions[0] = it.getDescription();
                    }
                } else {
                    getHUDAreaByType("craft1").setImage("");
                    craftDescriptions[0] = "";
                }
            }

            LoadRequirements();
        }
    }

    private void UpdateButtons() {
        if (isButtonPlaceablesEnabled) {
            getHUDAreaByType("placeables").setImage("HUD/Master/PlaceablesOn");
            getHUDAreaByType("items").setImage("HUD/Master/Items");
            getHUDAreaByType("attachments").setImage("HUD/Master/Attachments");

            getHUDAreaByType("p_development").setIsActive(true);
            getHUDAreaByType("p_combat").setIsActive(true);
            getHUDAreaByType("p_other").setIsActive(true);
            getHUDAreaByType("i_weapons").setIsActive(false);
            getHUDAreaByType("i_armor").setIsActive(false);
            getHUDAreaByType("i_other").setIsActive(false);
            getHUDAreaByType("t_development").setIsActive(false);

            getHUDAreaByType("create").setIsActive(true);
        } else if (isButtonItemsEnabled) {
            getHUDAreaByType("placeables").setImage("HUD/Master/Placeables");
            getHUDAreaByType("items").setImage("HUD/Master/ItemsOn");
            getHUDAreaByType("attachments").setImage("HUD/Master/Attachments");

            getHUDAreaByType("p_development").setIsActive(false);
            getHUDAreaByType("p_combat").setIsActive(false);
            getHUDAreaByType("p_other").setIsActive(false);
            getHUDAreaByType("i_weapons").setIsActive(true);
            getHUDAreaByType("i_armor").setIsActive(true);
            getHUDAreaByType("i_other").setIsActive(true);
            getHUDAreaByType("t_development").setIsActive(false);

            getHUDAreaByType("create").setIsActive(true);
        } else if (isButtonAttachmentsEnabled) {
            getHUDAreaByType("placeables").setImage("HUD/Master/Placeables");
            getHUDAreaByType("items").setImage("HUD/Master/Items");
            getHUDAreaByType("attachments").setImage("HUD/Master/AttachmentsOn");

            getHUDAreaByType("p_development").setIsActive(false);
            getHUDAreaByType("p_combat").setIsActive(false);
            getHUDAreaByType("p_other").setIsActive(false);
            getHUDAreaByType("i_weapons").setIsActive(false);
            getHUDAreaByType("i_armor").setIsActive(false);
            getHUDAreaByType("i_other").setIsActive(false);
            getHUDAreaByType("t_development").setIsActive(true);

            getHUDAreaByType("create").setIsActive(true);
        }

        if (isButtonPDevelopmentEnabled) {
            getHUDAreaByType("p_development").setImage("HUD/Master/DevelopmentOn");
        } else {
            getHUDAreaByType("p_development").setImage("HUD/Master/Development");
        }
        if (isButtonPCombatEnabled) {
            getHUDAreaByType("p_combat").setImage("HUD/Master/CombatOn");
        } else {
            getHUDAreaByType("p_combat").setImage("HUD/Master/Combat");
        }
        if (isButtonPOtherEnabled) {
            getHUDAreaByType("p_other").setImage("HUD/Master/OtherOn");
        } else {
            getHUDAreaByType("p_other").setImage("HUD/Master/Other");
        }
        if (isButtonIWeaponsEnabled) {
            getHUDAreaByType("i_weapons").setImage("HUD/Master/WeaponsOn");
        } else {
            getHUDAreaByType("i_weapons").setImage("HUD/Master/Weapons");
        }
        if (isButtonIArmorEnabled) {
            getHUDAreaByType("i_armor").setImage("HUD/Master/ArmorOn");
        } else {
            getHUDAreaByType("i_armor").setImage("HUD/Master/Armor");
        }
        if (isButtonIOtherEnabled) {
            getHUDAreaByType("i_other").setImage("HUD/Master/OtherOn");
        } else {
            getHUDAreaByType("i_other").setImage("HUD/Master/Other");
        }
        if (isButtonTDevelopmentEnabled) {
            getHUDAreaByType("t_development").setImage("HUD/Master/DevelopmentOn");
        } else {
            getHUDAreaByType("t_development").setImage("HUD/Master/Development");
        }
        if (isButtonHaveMaterialsEnabled) {
            getHUDAreaByType("have_materials").setImage("HUD/Master/HaveMaterialsOn");
        } else {
            getHUDAreaByType("have_materials").setImage("HUD/Master/HaveMaterials");
        }

        LoadMatchingItems();
    }

    @Override
    public void HUDAreaClicked(HUDArea ha) {
        HUDArea hudArea = null;


        synchronized (this) {

            int selectedStart = registry.getInvSlotFrom();

            for (int i = 0; i < hudAreas.size(); i++) {
                hudArea = hudAreas.get(i);
                if (hudArea == ha) {
                    if (hudArea.getType().equals("requirement1")
                            || hudArea.getType().equals("requirement2")
                            || hudArea.getType().equals("requirement3")
                            || hudArea.getType().equals("requirement4")) {
                        int id = Integer.parseInt(hudArea.getType().substring(11, 12));
                        id--;
                        if (id >= 0 && id < requirements.size()) {
                            String[] parts = requirements.get(id).toString().split(":");
                            ItemType it = registry.getItemType(parts[0]);
                            if (it != null) {
                                boolean doLoad = false;
                                if (it.getCategory().equals("Placeable")) {
                                    isButtonPlaceablesEnabled = true;
                                    isButtonPDevelopmentEnabled = true;
                                    isButtonPCombatEnabled = true;
                                    isButtonPOtherEnabled = true;
                                    doLoad = true;
                                } else if (it.getCategory().equals("Item")) {
                                    isButtonItemsEnabled = true;
                                    isButtonIWeaponsEnabled = true;
                                    isButtonIArmorEnabled = true;
                                    isButtonIOtherEnabled = true;
                                    doLoad = true;
                                } else if (it.getCategory().equals("Attachment")) {
                                    isButtonAttachmentsEnabled = true;
                                    isButtonTDevelopmentEnabled = true;
                                    doLoad = true;
                                }
                                if (doLoad) {
                                    LoadMatchingItems();
                                    for (int x = 0; x < matchingItems.size(); x++) {
                                        String itemName = matchingItems.get(x);
                                        if (itemName != null) {
                                            if (itemName.equals(it.getName())) {
                                                currentItem = x;
                                                break;
                                            }
                                        }
                                    }
                                    showMatchingItems();
                                }
                            }
                        }
                    } else {
                        if (selectedStart != i && registry.getSplitCount() > 0) {
                            if (i >= 0 && selectedStart >= 0 && registry.getPlaverInventorySlotQty(i) == 0) {
                                if (hudAreas.get(i).getType().equals("inventory") && hudAreas.get(selectedStart).getType().equals("inventory")) {
                                    String in = registry.getItemNameBySlot(selectedStart);
                                    hudManager.setPlayerSlotQuantity(selectedStart, registry.getPlaverInventorySlotQty(selectedStart) - registry.getSplitCount());
                                    hudManager.playerAddItem(i, in, registry.getSplitCount());
                                }
                            }
                            registry.setInvSlotFrom("Master", selectedStart);
                        } else {
                            selectedStart = i;
                            hudManager.setCursorImageAndText(hudArea.getFGImage(), hudArea.getText());
                            if (hudArea.getType().equals("head")) {
                                registry.setInvSlotFrom("MasterHead", selectedStart);
                            } else if (hudArea.getType().equals("chest")) {
                                registry.setInvSlotFrom("MasterChest", selectedStart);
                            } else if (hudArea.getType().equals("legs")) {
                                registry.setInvSlotFrom("MasterLegs", selectedStart);
                            } else if (hudArea.getType().equals("feet")) {
                                registry.setInvSlotFrom("MasterFeet", selectedStart);
                            } else {
                                registry.setInvSlotFrom("Master", selectedStart);
                            }
                        }
                    }
                    if (hudArea.getType().equals("placeables")) {
                        isButtonPlaceablesEnabled = true;
                        isButtonItemsEnabled = false;
                        isButtonAttachmentsEnabled = false;
                        UpdateButtons();
                    }
                    if (hudArea.getType().equals("items")) {
                        isButtonPlaceablesEnabled = false;
                        isButtonItemsEnabled = true;
                        isButtonAttachmentsEnabled = false;
                        UpdateButtons();
                    }
                    if (hudArea.getType().equals("attachments")) {
                        isButtonPlaceablesEnabled = false;
                        isButtonItemsEnabled = false;
                        isButtonAttachmentsEnabled = true;
                        UpdateButtons();
                    }
                    if (hudArea.getType().equals("p_development")) {
                        isButtonPDevelopmentEnabled = !isButtonPDevelopmentEnabled;
                        UpdateButtons();
                    }
                    if (hudArea.getType().equals("p_combat")) {
                        isButtonPCombatEnabled = !isButtonPCombatEnabled;
                        UpdateButtons();
                    }
                    if (hudArea.getType().equals("p_other")) {
                        isButtonPOtherEnabled = !isButtonPOtherEnabled;
                        UpdateButtons();
                    }
                    if (hudArea.getType().equals("i_weapons")) {
                        isButtonIWeaponsEnabled = !isButtonIWeaponsEnabled;
                        UpdateButtons();
                    }
                    if (hudArea.getType().equals("i_armor")) {
                        isButtonIArmorEnabled = !isButtonIArmorEnabled;
                        UpdateButtons();
                    }
                    if (hudArea.getType().equals("i_other")) {
                        isButtonIOtherEnabled = !isButtonIOtherEnabled;
                        UpdateButtons();
                    }
                    if (hudArea.getType().equals("t_development")) {
                        isButtonTDevelopmentEnabled = !isButtonTDevelopmentEnabled;
                        UpdateButtons();
                    }
                    if (hudArea.getType().equals("have_materials")) {
                        isButtonHaveMaterialsEnabled = !isButtonHaveMaterialsEnabled;
                        UpdateButtons();
                    }
                    if (hudArea.getType().equals("arrow_left")) {
                        currentItem--;
                        if (currentItem < 0) {
                            currentItem = matchingItems.size() - 1;
                        }
                        showMatchingItems();
                    }
                    if (hudArea.getType().equals("arrow_right")) {
                        currentItem++;
                        if (currentItem > (matchingItems.size() - 1)) {
                            currentItem = 0;
                        }
                        showMatchingItems();
                    }
                    if (hudArea.getType().equals("create")) {
                        if (matchingItems != null && matchingItems.size() > 0) {
                            hudManager.playerCraftItem(matchingItems.get(currentItem));
                            if (isButtonHaveMaterialsEnabled) {
                                ArrayList<String> tmp = new ArrayList<String>();
                                for (int j = 0; j < matchingItems.size(); j++) {
                                    if (canCreate(matchingItems.get(j))) {
                                        tmp.add(matchingItems.get(j));
                                    }
                                }
                                if (matchingItems.size() != tmp.size()) {
                                    UpdateButtons();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void HUDAreaRightClicked(HUDArea ha) {
        HUDArea hudArea = null;

        int selectedStart = registry.getInvSlotFrom();

        synchronized (this) {
            for (int i = 0; i < hudAreas.size(); i++) {
                hudArea = hudAreas.get(i);
                if (hudArea == ha) {
                    int maxQty = registry.getPlaverInventorySlotQty(i);
                    if (selectedStart == i && registry.getSplitCount() > 0) {
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
                        selectedStart = i;
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
        }

        registry.setInvSlotFrom("Master", selectedStart);
    }

    @Override
    public void HUDAreaReleased(HUDArea ha) {
        int selectedStart = registry.getInvSlotFrom();

        synchronized (this) {
            if (selectedStart > -1) {
                HUDArea hudAreaTo = null;
                HUDArea hudAreaFrom = hudAreas.get(selectedStart);

                if (registry.getSplitCount() < 1) {
                    for (int i = 0; i < hudAreas.size(); i++) {
                        hudAreaTo = hudAreas.get(i);
                        if (hudAreaTo == ha) {
                            if (registry.getInvHUDFrom().equals("Container")) {
                                ItemContainer itemContainer = registry.getInvItemContainerFrom();
                                if (itemContainer != null) {
                                    String itemName = itemContainer.getInventory().getNameFromSlot(selectedStart);
                                    int qty = itemContainer.getInventory().getQtyFromSlot(selectedStart);
                                    int level = itemContainer.getInventory().getLevelFromSlot(selectedStart);

                                    if (!itemName.isEmpty() && qty > 0) {
                                        if (hudAreaTo.getType().equals("head")) {
                                            hudManager.playerEquipHead(itemName, level);
                                            itemContainer.deleteInventory(selectedStart, 0);
                                        } else if (hudAreaTo.getType().equals("chest")) {
                                            hudManager.playerEquipChest(itemName, level);
                                            itemContainer.deleteInventory(selectedStart, 0);
                                        } else if (hudAreaTo.getType().equals("legs")) {
                                            hudManager.playerEquipLegs(itemName, level);
                                            itemContainer.deleteInventory(selectedStart, 0);
                                        } else if (hudAreaTo.getType().equals("feet")) {
                                            hudManager.playerEquipFeet(itemName, level);
                                            itemContainer.deleteInventory(selectedStart, 0);
                                        } else if (hudAreaTo.getType().equals("inventory")) {
                                            int oldQty = qty;
                                            if (hudManager.playerAddItem(i, itemName, qty, level) < oldQty) {
                                                itemContainer.deleteInventory(selectedStart, 0);
                                            }
                                        } else if (hudAreaTo.getType().equals("trash")) {
                                            Player p = null;
                                            PlayerManager pm = registry.getPlayerManager();
                                            if (pm != null) {
                                                p = pm.getCurrentPlayer();
                                                if (p != null) {
                                                    p.playerGiveItemXP(itemName, qty);
                                                }
                                            }
                                            itemContainer.deleteInventory(selectedStart, 0);
                                        }
                                    }
                                }
                            } else if (registry.getInvHUDFrom().equals("PlayerContainer")) {
                                PlayerContainer playerContainer = registry.getInvPlayerContainerFrom();
                                if (playerContainer != null) {
                                    String itemName = playerContainer.getInventory().getNameFromSlot(selectedStart);
                                    int qty = playerContainer.getInventory().getQtyFromSlot(selectedStart);
                                    int level = playerContainer.getInventory().getLevelFromSlot(selectedStart);

                                    if (!itemName.isEmpty() && qty > 0) {
                                        if (hudAreaTo.getType().equals("head")) {
                                            hudManager.playerEquipHead(itemName, level);
                                            playerContainer.deleteInventory(selectedStart, 0);
                                        } else if (hudAreaTo.getType().equals("chest")) {
                                            hudManager.playerEquipChest(itemName, level);
                                            playerContainer.deleteInventory(selectedStart, 0);
                                        } else if (hudAreaTo.getType().equals("legs")) {
                                            hudManager.playerEquipLegs(itemName, level);
                                            playerContainer.deleteInventory(selectedStart, 0);
                                        } else if (hudAreaTo.getType().equals("feet")) {
                                            hudManager.playerEquipFeet(itemName, level);
                                            playerContainer.deleteInventory(selectedStart, 0);
                                        } else if (hudAreaTo.getType().equals("inventory")) {
                                            int oldQty = qty;
                                            if (hudManager.playerAddItem(i, itemName, qty, level) < oldQty) {
                                                playerContainer.deleteInventory(selectedStart, 0);
                                            }
                                        } else if (hudAreaTo.getType().equals("trash")) {
                                            Player p = null;
                                            PlayerManager pm = registry.getPlayerManager();
                                            if (pm != null) {
                                                p = pm.getCurrentPlayer();
                                                if (p != null) {
                                                    p.playerGiveItemXP(itemName, qty);
                                                }
                                            }
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
                                        if (hudAreaTo.getType().equals("inventory")) {
                                            int oldQty = qty;
                                            if (hudManager.playerAddItem(i, itemName, qty) < oldQty) {
                                                farm.deleteInventory(selectedStart, 0);
                                            }
                                        } else if (hudAreaTo.getType().equals("trash")) {
                                            Player p = null;
                                            PlayerManager pm = registry.getPlayerManager();
                                            if (pm != null) {
                                                p = pm.getCurrentPlayer();
                                                if (p != null) {
                                                    p.playerGiveItemXP(itemName, qty);
                                                }
                                            }
                                            farm.deleteInventory(selectedStart, 0);
                                        }
                                    }
                                }
                            } else if (registry.getInvHUDFrom().equals("QuickBarRobot")) {
                                Inventory robotInventory = registry.getRobotInventory();
                                if (robotInventory != null) {
                                    String itemName = robotInventory.getNameFromSlot(selectedStart);
                                    int qty = robotInventory.getQtyFromSlot(selectedStart);
                                    int level = robotInventory.getLevelFromSlot(selectedStart);

                                    if (!itemName.isEmpty() && qty > 0) {
                                        if (hudAreaTo.getType().equals("head")) {
                                            hudManager.playerEquipHead(itemName, level);
                                            robotInventory.deleteInventory(selectedStart, 0);
                                        } else if (hudAreaTo.getType().equals("chest")) {
                                            hudManager.playerEquipChest(itemName, level);
                                            robotInventory.deleteInventory(selectedStart, 0);
                                        } else if (hudAreaTo.getType().equals("legs")) {
                                            hudManager.playerEquipLegs(itemName, level);
                                            robotInventory.deleteInventory(selectedStart, 0);
                                        } else if (hudAreaTo.getType().equals("feet")) {
                                            hudManager.playerEquipFeet(itemName, level);
                                            robotInventory.deleteInventory(selectedStart, 0);
                                        } else if (hudAreaTo.getType().equals("inventory")) {
                                            int oldQty = qty;
                                            if (hudManager.playerAddItem(i, itemName, qty) < oldQty) {
                                                robotInventory.deleteInventory(selectedStart, 0);
                                            }
                                        } else if (hudAreaTo.getType().equals("trash")) {
                                            Player p = null;
                                            PlayerManager pm = registry.getPlayerManager();
                                            if (pm != null) {
                                                p = pm.getCurrentPlayer();
                                                if (p != null) {
                                                    p.playerGiveItemXP(itemName, qty);
                                                }
                                            }
                                            robotInventory.deleteInventory(selectedStart, 0);
                                        }
                                    }
                                }
                            } else if (registry.getInvHUDFrom().equals("QuickBar") && !registry.getIsQuickBarLocked()) {
                                if (hudAreaTo.getType().equals("head")
                                        || hudAreaTo.getType().equals("chest")
                                        || hudAreaTo.getType().equals("legs")
                                        || hudAreaTo.getType().equals("feet")) {
                                    hudManager.playerEquipFromInventory(selectedStart);
                                } else if (hudAreaTo.getType().equals("inventory")) {
                                    hudManager.playerSwapInventory(selectedStart, i);
                                } else if (hudAreaTo.getType().equals("trash")) {
                                    hudManager.playerDeleteInventory(selectedStart, 0, true);
                                }
                            } else if (hudAreaFrom.getType().equals("inventory")) {
                                if (hudAreaTo.getType().equals("head")
                                        || hudAreaTo.getType().equals("chest")
                                        || hudAreaTo.getType().equals("legs")
                                        || hudAreaTo.getType().equals("feet")) {
                                    hudManager.playerEquipFromInventory(selectedStart);
                                } else if (hudAreaTo.getType().equals("inventory")) {
                                    hudManager.playerSwapInventory(selectedStart, i);
                                } else if (hudAreaTo.getType().equals("trash")) {
                                    hudManager.playerDeleteInventory(selectedStart, 0, true);
                                }
                            } else if (hudAreaFrom.getType().equals("head")) {
                                if (hudAreaTo.getType().equals("inventory")) {
                                    hudManager.playerUnEquipToInventory("head", i);
                                } else if (hudAreaTo.getType().equals("trash")) {
                                    hudManager.playerUnEquipToDelete("head");
                                }
                            } else if (hudAreaFrom.getType().equals("chest")) {
                                if (hudAreaTo.getType().equals("inventory")) {
                                    hudManager.playerUnEquipToInventory("chest", i);
                                } else if (hudAreaTo.getType().equals("trash")) {
                                    hudManager.playerUnEquipToDelete("chest");
                                }
                            } else if (hudAreaFrom.getType().equals("legs")) {
                                if (hudAreaTo.getType().equals("inventory")) {
                                    hudManager.playerUnEquipToInventory("legs", i);
                                } else if (hudAreaTo.getType().equals("trash")) {
                                    hudManager.playerUnEquipToDelete("legs");
                                }
                            } else if (hudAreaFrom.getType().equals("feet")) {
                                if (hudAreaTo.getType().equals("inventory")) {
                                    hudManager.playerUnEquipToInventory("feet", i);
                                } else if (hudAreaTo.getType().equals("trash")) {
                                    hudManager.playerUnEquipToDelete("feet");
                                }
                            }
                        }
                    }
                }
                registry.setSplitCount(0);
            }
        }

        registry.setInvSlotFrom("", selectedStart);
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
    public void toggleMasterHUD() {
        shouldRender = !shouldRender;
    }

    @Override
    public void update() {
        if (shouldRender) {
            HUDArea hudArea;

            Player p = null;
            PlayerManager pm = registry.getPlayerManager();
            if (pm != null) {
                p = pm.getCurrentPlayer();
            }
            if (p != null) {
                //update slots
                for (int i = 0; i < hudAreas.size(); i++) {
                    hudArea = hudAreas.get(i);
                    if (hudArea.getType().equals("inventory")) {
                        String hudAreaImage = registry.getPlaverInventorySlotImage(i);
                        if (hudAreaImage != null) {
                            hudArea.setFGImage(hudAreaImage);
                            if (hudArea.isInside(registry.getMousePosition())) {
                                registry.setStatusText(registry.getPlaverInventorySlotDescription(i));
                                if (registry.getPlaverInventorySlotType(i).equals("Weapon")) {
                                    registry.setWeaponType(registry.getPlaverInventorySlotName(i));
                                    registry.setWeaponLevel(registry.getPlaverInventorySlotLevel(i));
                                    hudManager.showArmorHUD(false);
                                    hudManager.showWeaponHUD(true);
                                } else if (registry.getPlaverInventorySlotType(i).equals("Armor")) {
                                    registry.setArmorType(registry.getPlaverInventorySlotName(i));
                                    registry.setArmorLevel(registry.getPlaverInventorySlotLevel(i));
                                    hudManager.showArmorHUD(true);
                                    hudManager.showWeaponHUD(false);
                                }
                            }
                        }
                        int hudAreaQty = registry.getPlaverInventorySlotQty(i);
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
                    } else if (hudArea.getType().equals("head")) {
                        String hudAreaImage = registry.getPlaverHeadSlotImage(registry.getPlayerManager().getCurrentPlayer());
                        if (hudAreaImage != null) {
                            hudArea.setFGImage(hudAreaImage);
                        } else {
                            hudArea.setFGImage("");
                        }
                        if (p.getArmorTypeHead() != null) {
                            if (hudArea.isInside(registry.getMousePosition())) {
                                registry.setArmorType(p.getArmorTypeHead().getName());
                                registry.setArmorLevel(p.getArmorTypeHeadLevel());
                                hudManager.showArmorHUD(true);
                                hudManager.showWeaponHUD(false);
                            }
                        }
                    } else if (hudArea.getType().equals("chest")) {
                        String hudAreaImage = registry.getPlaverChestSlotImage(registry.getPlayerManager().getCurrentPlayer());
                        if (hudAreaImage != null) {
                            hudArea.setFGImage(hudAreaImage);
                        } else {
                            hudArea.setFGImage("");
                        }
                        if (p.getArmorTypeChest() != null) {
                            if (hudArea.isInside(registry.getMousePosition())) {
                                registry.setArmorType(p.getArmorTypeChest().getName());
                                registry.setArmorLevel(p.getArmorTypeChestLevel());
                                hudManager.showArmorHUD(true);
                                hudManager.showWeaponHUD(false);
                            }
                        }
                    } else if (hudArea.getType().equals("legs")) {
                        String hudAreaImage = registry.getPlaverLegsSlotImage(registry.getPlayerManager().getCurrentPlayer());
                        if (hudAreaImage != null) {
                            hudArea.setFGImage(hudAreaImage);
                        } else {
                            hudArea.setFGImage("");
                        }
                        if (p.getArmorTypeLegs() != null) {
                            if (hudArea.isInside(registry.getMousePosition())) {
                                registry.setArmorType(p.getArmorTypeLegs().getName());
                                registry.setArmorLevel(p.getArmorTypeLegsLevel());
                                hudManager.showArmorHUD(true);
                                hudManager.showWeaponHUD(false);
                            }
                        }
                    } else if (hudArea.getType().equals("feet")) {
                        String hudAreaImage = registry.getPlaverFeetSlotImage(registry.getPlayerManager().getCurrentPlayer());
                        if (hudAreaImage != null) {
                            hudArea.setFGImage(hudAreaImage);
                        } else {
                            hudArea.setFGImage("");
                        }
                        if (p.getArmorTypeFeet() != null) {
                            if (hudArea.isInside(registry.getMousePosition())) {
                                registry.setArmorType(p.getArmorTypeFeet().getName());
                                registry.setArmorLevel(p.getArmorTypeFeetLevel());
                                hudManager.showArmorHUD(true);
                                hudManager.showWeaponHUD(false);
                            }
                        }
                    } else if (hudArea.getType().equals("ap")) {
                        hudArea.setText(String.valueOf(registry.getPlaverArmorPoints(registry.getPlayerManager().getCurrentPlayer())));
                        hudArea.setTextXY(AP_TEXT_OFFSET_X, AP_TEXT_OFFSET_Y);
                        hudAreaText(hudArea, "Your total Armor Points.  When you get hit, armor weakens it so you take less damage.");
                    } else if (hudArea.getType().equals("hp")) {
                        hudArea.setText(String.valueOf(registry.getPlaverHitPoints(registry.getPlayerManager().getCurrentPlayer())));
                        hudArea.setTextXY(HP_TEXT_OFFSET_X, HP_TEXT_OFFSET_Y);
                        hudAreaText(hudArea, requirementsDescriptions[0]);
                        hudAreaText(hudArea, "Your total Hit Points.  When you run out, you're dead.");
                    } else if (hudArea.getType().equals("trash")) {
                        hudAreaText(hudArea, "Drag an item here to get rid of it.  Be careful, this is permanent!");
                    } else if (hudArea.getType().equals("craft1")) {
                        hudAreaText(hudArea, craftDescriptions[0]);
                    } else if (hudArea.getType().equals("craft2")) {
                        hudAreaText(hudArea, craftDescriptions[1]);
                    } else if (hudArea.getType().equals("craft3")) {
                        hudAreaText(hudArea, craftDescriptions[2]);
                    } else if (hudArea.getType().equals("craft4")) {
                        hudAreaText(hudArea, craftDescriptions[3]);
                    } else if (hudArea.getType().equals("craft5")) {
                        hudAreaText(hudArea, craftDescriptions[4]);
                    } else if (hudArea.getType().equals("requirement1")) {
                        hudAreaText(hudArea, requirementsDescriptions[0]);
                    } else if (hudArea.getType().equals("requirement2")) {
                        hudAreaText(hudArea, requirementsDescriptions[1]);
                    } else if (hudArea.getType().equals("requirement3")) {
                        hudAreaText(hudArea, requirementsDescriptions[2]);
                    } else if (hudArea.getType().equals("requirement4")) {
                        hudAreaText(hudArea, requirementsDescriptions[3]);
                    }
                }

                checkIfCanCreate();
            }
        }

        super.update();
    }

    @Override
    public void render(Graphics g) {
        super.render(g);

        if (shouldRender) {
            hudManager.playerRender(g, positionX + PAPERDOLL_X, positionY + PAPERDOLL_Y, true);
        }
    }
}