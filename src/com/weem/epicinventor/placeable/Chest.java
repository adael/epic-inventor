package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.utility.Rand;

public class Chest extends PlayerContainer {

    private static final long serialVersionUID = 10000L;
    private boolean hasBeenOpened = false;

    public Chest(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs, 8);

        type = "Chest";

        totalBuildTime = 1;
        totalHitPoints = 625;
        powerRequired = 0;
        powerGenerated = 0;

        hitPoints = totalHitPoints;

        //figure out drops for chest
        String[] drops = new String[42];
        drops[0] = "2 ThornTrap 1 1";
        drops[1] = "2 ScareCrow 1 1";
        drops[2] = "2 SmallFarm 1 1";
        drops[3] = "2 LargeFarm 1 1";
        drops[4] = "2 RobotCopperBlade 1 1";
        drops[5] = "2 RobotSilverBlade 1 1";
        drops[6] = "15 Wood 1 10";
        drops[7] = "10 WoodBlock 1 2";
        drops[8] = "15 Stone 1 10";
        drops[9] = "10 StoneBlock 1 2";
        drops[10] = "10 Copper 1 5";
        drops[11] = "5 CopperSpear 1 1";
        drops[12] = "5 CopperBlade 1 1";
        drops[13] = "10 Iron 1 4";
        drops[14] = "5 IronSpear 1 1";
        drops[15] = "5 IronBlade 1 1";
        drops[16] = "5 Silver 1 3";
        drops[17] = "5 SilverSpear 1 1";
        drops[18] = "5 SilverBlade 1 1";
        drops[19] = "5 Gold 1 2";
        drops[20] = "5 GoldSpear 1 1";
        drops[21] = "5 GoldBlade 1 1";
        drops[22] = "2 Platinum 1 2";
        drops[23] = "5 GunPowder 1 2";
        drops[24] = "10 Bow 1 1";
        drops[25] = "10 Crossbow 1 1";
        drops[26] = "10 Cloth 1 5";
        drops[27] = "10 Leather 1 5";
        drops[28] = "10 Dye 1 1";
        drops[29] = "2 Sapphire 1 3";
        drops[30] = "2 Ruby 1 2";
        drops[31] = "1 Emerald 1 1";
        drops[32] = "1 Diamond 1 1";
        drops[33] = "10 Tusk 1 5";
        drops[34] = "10 Web 1 5";
        drops[35] = "10 Pebble 1 5";
        drops[36] = "10 Flower 1 5";
        drops[37] = "10 Fur 1 5";
        drops[38] = "10 Thorn 1 5";
        drops[39] = "10 Web 1 5";
        drops[40] = "10 WheatSeed 1 5";
        drops[41] = "10 PumpkinSeed 1 5";

        int i = 0;
        int added = 0;
        do {
            i++;
            String drop = drops[Rand.getRange(0, drops.length - 1)];
            String[] parts = drop.split(" ");
            int percentage = Integer.parseInt(parts[0]);
            if (Rand.getRange(1, 100) <= percentage) {
                added++;
                inventory.addToInventory(0, parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
            }
        } while (i < 1000 && added < 8);
    }

    @Override
    public void toggled() {
        if (!hasBeenOpened) {
            //first time openeing - set item levels to player level +3
            Player p = registry.getPlayerManager().getCurrentPlayer();
            
            for (int i = 0; i < inventorySize; i++) {
                int qty = inventory.getQtyFromSlot(i);
                if (qty > 0) {
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
                    inventory.setLevelFromSlot(i, level);
                }
            }
            
            hasBeenOpened = true;
        }
    }

    @Override
    public void setTransient(Registry rg) {
        totalHitPoints = 500;
        super.setTransient(rg);
    }

    @Override
    public void updateLong() {
        super.updateLong();

        if (inventory.getUsedSlots() == 0) {
            isDirty = true;
        }
    }
}
