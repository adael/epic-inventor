package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.armor.*;

import java.awt.*;
import java.text.*;

public class HUDArmorInfo extends HUD {

    private final static int NAME_X = 10;
    private final static int NAME_Y = 5;
    private final static int NAME_WIDTH = 210;
    private final static int NAME_HEIGHT = 32;
    private final static int AP_X = 99;
    private final static int AP_Y = 93;
    private final static int AP_WIDTH = 45;
    private final static int AP_HEIGHT = 35;
    private final static int OFFSET = 7;

    public HUDArmorInfo(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/ArmorInfo/BG");

        HUDArea hudArea = null;

        //name
        hudArea = addArea(NAME_X, NAME_Y, NAME_WIDTH, NAME_HEIGHT, "name");
        hudArea.setFont("SansSerif", Font.BOLD, 24);

        //ap
        hudArea = addArea(AP_X, AP_Y, AP_WIDTH, AP_HEIGHT, "ap");
        hudArea.setFont("SansSerif", Font.BOLD, 24);

        shouldRender = false;
    }

    @Override
    public void update() {
        if (shouldRender) {
            Player p = registry.getPlayerManager().getCurrentPlayer();
            if (p != null) {
                HUDArea hudArea;
                ArmorType newArmorType = Armor.getArmorType(registry.getArmorType());
                if (newArmorType != null) {
                    int[] armors = newArmorType.getArmorBonus();
                    int armor = armors[registry.getArmorLevel()];
                    int playerLevel = p.getLevel();
                    int amoreLevel = registry.getArmorLevel();

                    for (int i = 0; i < hudAreas.size(); i++) {
                        hudArea = hudAreas.get(i);
                        if (hudArea.getType().equals("name")) {
                            String itemName = registry.getArmorType();
                            itemName = itemName.replace("ClothesChest", "Standard Shirt");
                            itemName = itemName.replace("ClothesFeet", "Standard Shoes");
                            itemName = itemName.replace("ClothesLegs", "Standard Pants");
                            itemName = itemName.replace("NinjaHead", "Ninja Mask");
                            itemName = itemName.replace("TBHead", "Total Biscuit Hat");
                            itemName = itemName.replace("Melvin", "Melvin's");
                            itemName = itemName.replace("Head", " Helmet");
                            itemName = itemName.replace("Chest", " Chest");
                            itemName = itemName.replace("Feet", "  Boots");
                            itemName = itemName.replace("Legs", " Legs");
                            
                            hudArea.setText(itemName + " " + registry.getArmorLevel());
                            if(amoreLevel == 20) {
                                hudArea.setTextColor(Color.MAGENTA);
                            } else if(amoreLevel <= playerLevel) {
                                hudArea.setTextColor(Color.WHITE);
                            } else if(amoreLevel == playerLevel + 1) {
                                hudArea.setTextColor(Color.GREEN);
                            } else if(amoreLevel == playerLevel + 2) {
                                hudArea.setTextColor(Color.BLUE);
                            } else {
                                hudArea.setTextColor(Color.MAGENTA);
                            }
                        } else if (hudArea.getType().equals("ap")) {
                            hudArea.setText(Integer.toString(armor));
                            if (Integer.toString(armor).length() == 1) {
                                hudArea.setXY(AP_X + (OFFSET * 2), AP_Y);
                            } else if (Integer.toString(armor).length() == 2) {
                                hudArea.setXY(AP_X + OFFSET, AP_Y);
                            } else if (Integer.toString(armor).length() == 3) {
                                hudArea.setXY(AP_X, AP_Y);
                            }
                        }
                    }
                }
            }
        }

        super.update();
    }
}