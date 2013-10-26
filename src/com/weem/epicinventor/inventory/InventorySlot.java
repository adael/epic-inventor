package com.weem.epicinventor.inventory;

import java.io.*;
import com.weem.epicinventor.*;
import com.weem.epicinventor.item.*;

public class InventorySlot implements Serializable {

    protected static final long serialVersionUID = 7653293255956181100L;
    
    private Inventory inventory;
    transient private Registry registry;
    private ItemType itemType;
    private Item item;
    private int qty;

    public InventorySlot(Inventory i, Registry rg) {
        inventory = i;
        registry = rg;
        qty = 0;
    }

    public void setTransient(Registry rg) {
        registry = rg;
        if(itemType != null) {
            itemType = rg.getItemManager().getItemType(itemType.getName());
            if(itemType.getName().equals("ScrapHammer")) {
                int x = 9;
            }
            if(item == null) {
                int level = 1;
                if(itemType.getType().equals("Weapon") || itemType.getType().equals("Armor")) {
                    level = rg.getPlayerManager().getCurrentPlayer().getLevel();
                }
                item = new Item(itemType, level);
            }
            item.setTransient();
        } else {
            itemType = null;
        }
        if(item != null) {
            item.setTransient();
        }
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getItemTypeName() {
        String n = "";
        
        if(itemType != null) {
            n = itemType.getName();
        }
        
        return n;
    }

    public String getImageName() {
        String name = "";

        if (itemType != null) {
            name = itemType.getImageName();
        }

        return name;
    }

    public String getDescription() {
        String description = "";

        if (itemType != null) {
            description = itemType.getDescription();
        }

        return description;
    }

    public int getLevel() {
        int level = 0;

        if (item != null) {
            level = item.getLevel();
        }

        return level;
    }

    public void setLevel(int level) {
        if (item != null) {
            item.setLevel(level);
        }
    }

    public int getQty() {
        return qty;
    }
    
    public int addItem(ItemType newItemType, int newQty) {
        return addItem(newItemType, newQty, 1);
    }

    /*
     * returns the qty of items that didn't fit in the slot - pfftt hahaha  :p
     * or -1 in case of an error
     */
    public int addItem(ItemType newItemType, int newQty, int level) {
        int leftToDistribute = newQty;

        if (newItemType == null || newQty < 1) {
            return -1;
        }

        if (itemType == null) {
            //slot is clear
            itemType = newItemType;
            item = new Item(itemType, level);
            if (newQty <= newItemType.getMaxStack()) {
                qty = newQty;
                leftToDistribute = 0;
            } else {
                qty = newItemType.getMaxStack();
                leftToDistribute = newQty - newItemType.getMaxStack();
            }
        } else {
            //an item is already in this slot
            if (newItemType == itemType) {
                if ((qty + newQty) <= newItemType.getMaxStack()) {
                    qty += newQty;
                    leftToDistribute = 0;
                } else {
                    leftToDistribute = newQty - (newItemType.getMaxStack() - qty);
                    qty = newItemType.getMaxStack();
                }
            }
        }

        return leftToDistribute;
    }

    public void setQty(int newQty) {
        qty = newQty;

        if (qty < 1) {
            itemType = null;
            qty = 0;
        }
    }

    public void empty() {
        itemType = null;
        item = null;
        qty = 0;
    }

    public int removeQty(int removeQty) {
        qty -= removeQty;

        if (qty < 1) {
            itemType = null;
            item = null;
            qty = 0;
        }

        return qty;
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}