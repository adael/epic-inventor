package com.weem.epicinventor.item;

import com.weem.epicinventor.*;
import com.weem.epicinventor.world.block.*;

import java.awt.*;
import java.util.*;
import java.io.*;

public class ItemManager extends Manager {

    private ArrayList<Item> items;
    private HashMap itemTypes;
    private ArrayList<ItemType> itemTypesList;
    private final static String CONFIG_FILE = "Items.dat";

    public ItemManager(GameController gc, Registry rg) {
        super(gc, rg);

        items = new ArrayList<Item>();

        itemTypes = new HashMap();
        itemTypesList = new ArrayList<ItemType>();

        loadItemTypes("Items.dat");
    }

    private void loadItemTypes(String fn) {
        String line;
        String parts[];

        try {
            InputStream in = getClass().getResourceAsStream(GameController.CONFIG_DIR + fn);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String name = "";
            int createQty = 0;
            int maxStack = 0;
            String category = "";
            String type = "";
            int techLevel = 0;
            String item1 = "";
            int item1Qty = 0;
            String item2 = "";
            int item2Qty = 0;
            String item3 = "";
            int item3Qty = 0;
            String item4 = "";
            int item4Qty = 0;
            String skill1 = "";
            String skill2 = "";
            String workBench = "";
            float xpModifier = 1f;
            String description = "";

            ItemType it;

            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }

                parts = line.split(" ", 19);

                if (parts.length != 19) {
                    System.out.println("Error in " + fn);
                }

                name = parts[0];
                createQty = Integer.parseInt(parts[1]);
                maxStack = Integer.parseInt(parts[2]);
                category = parts[3];
                type = parts[4];
                techLevel = Integer.parseInt(parts[5]);
                item1 = parts[6];
                item1Qty = Integer.parseInt(parts[7]);
                item2 = parts[8];
                item2Qty = Integer.parseInt(parts[9]);
                item3 = parts[10];
                item3Qty = Integer.parseInt(parts[11]);
                item4 = parts[12];
                item4Qty = Integer.parseInt(parts[13]);
                skill1 = parts[14];
                skill2 = parts[15];
                workBench = parts[16];
                xpModifier = Float.parseFloat(parts[17]);
                description = parts[18];

                it = new ItemType(this, registry, name, createQty, maxStack, category, type, techLevel,
                        item1, item1Qty, item2, item2Qty, item3, item3Qty, item4, item4Qty,
                        skill1, skill2, workBench, xpModifier, description);
                
                itemTypes.put(name, it);
                
                itemTypesList.add(it);
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public ItemType getItemType(String name) {
        if (itemTypes.containsKey(name)) {
            return (ItemType) itemTypes.get(name);
        }

        return null;
    }
    
    public ArrayList<String> getItemTypeList(String category, String types) {
        ArrayList<String> matchingItems = new ArrayList<String>();
        
        ItemType itemType = null;

        for (int i = 0; i < itemTypesList.size(); i++) {
            itemType = itemTypesList.get(i);
            if(itemType.getCategory().equals(category) && types.contains("|" + itemType.getType() + "|")) {
                matchingItems.add(itemType.getName());
            }
        }
        
        return matchingItems;
    }
    
    public ArrayList<String> getItemTypeRequirements(String n) {
        ArrayList<String> requirements = new ArrayList<String>();
        
        if(itemTypes.containsKey(n)) {
            ItemType it = (ItemType) itemTypes.get(n);
            if(it != null) {
                requirements = it.getRequirementString();
            }
        }
        
        return requirements;
    }

    public void render(Graphics g) {
    }
}