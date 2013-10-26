package com.weem.epicinventor.inventory;

import com.weem.epicinventor.*;
import com.weem.epicinventor.item.*;

import java.io.*;
import java.util.ArrayList;

public class Inventory implements Serializable {

    protected static final long serialVersionUID = 2758038295129156427L;
    transient private Registry registry;
    ArrayList<InventorySlot> inventorySlots;
    private final static String IMAGE_DIR = "Data/Images/";

    public Inventory(Registry rg, int slots) {
        registry = rg;

        inventorySlots = new ArrayList<InventorySlot>();

        InventorySlot is;

        for (int i = 0; i < slots; i++) {
            is = new InventorySlot(this, registry);
            inventorySlots.add(is);
        }
    }

    public void setTransient(Registry rg) {
        registry = rg;
        for (int i = 0; i < inventorySlots.size(); i++) {
            inventorySlots.get(i).setTransient(rg);
        }
    }

    public String getCategoryFromSlot(int slot) {
        if (slot < 0
                || slot > inventorySlots.size() - 1) {
            return "";
        }

        InventorySlot invSlot = inventorySlots.get(slot);

        if (invSlot == null) {
            return "";
        }

        if (invSlot.getQty() < 1) {
            return "";
        }
        
        ItemType itemType = invSlot.getItemType();
        if(itemType != null) {
            return itemType.getCategory();
        } else {
            return "";
        }
    }

    public String getTypeFromSlot(int slot) {
        if (slot < 0
                || slot > inventorySlots.size() - 1) {
            return "";
        }

        InventorySlot invSlot = inventorySlots.get(slot);

        if (invSlot == null) {
            return "";
        }

        if (invSlot.getQty() < 1) {
            return "";
        }
        
        ItemType itemType = invSlot.getItemType();
        if(itemType != null) {
            return itemType.getType();
        } else {
            return "";
        }
    }

    public int getQtyFromSlot(int slot) {
        if (slot < 0
                || slot > inventorySlots.size() - 1) {
            return 0;
        }

        InventorySlot invSlot = inventorySlots.get(slot);

        if (invSlot == null) {
            return 0;
        }

        if (invSlot.getQty() < 1) {
            return 0;
        }

        return invSlot.getQty();
    }

    public int getLevelFromSlot(int slot) {
        if (slot < 0
                || slot > inventorySlots.size() - 1) {
            return 0;
        }

        InventorySlot invSlot = inventorySlots.get(slot);

        if (invSlot == null) {
            return 0;
        }

        if (invSlot.getLevel() < 1) {
            return 1;
        }

        return invSlot.getLevel();
    }

    public void setLevelFromSlot(int slot, int level) {
        if (slot < 0
                || slot > inventorySlots.size() - 1) {
            return;
        }

        InventorySlot invSlot = inventorySlots.get(slot);

        if (invSlot == null) {
            return;
        }

        invSlot.setLevel(level);
    }

    public String getNameFromSlot(int slot) {
        if (slot < 0
                || slot > inventorySlots.size() - 1) {
            return "";
        }

        InventorySlot invSlot = inventorySlots.get(slot);

        if (invSlot == null) {
            return "";
        }

        if (invSlot.getQty() < 1) {
            return "";
        }
        
        ItemType itemType = invSlot.getItemType();
        if(itemType != null) {
            return itemType.getName();
        } else {
            return "";
        }
    }

    public String getImageFromSlot(int slot) {
        if (slot < 0
                || slot > inventorySlots.size() - 1) {
            return "";
        }

        InventorySlot invSlot = inventorySlots.get(slot);

        if (invSlot == null) {
            return "";
        }

        if (invSlot.getQty() < 1) {
            return "";
        }
        
        ItemType itemType = invSlot.getItemType();
        if(itemType != null) {
            return itemType.getImageName();
        } else {
            return "";
        }
    }

    public String getDescriptionFromSlot(int slot) {
        if (slot < 0
                || slot > inventorySlots.size() - 1) {
            return "";
        }

        InventorySlot invSlot = inventorySlots.get(slot);

        if (invSlot == null) {
            return "";
        }

        if (invSlot.getQty() < 1) {
            return "";
        }
        
        ItemType itemType = invSlot.getItemType();
        if(itemType != null) {
            return itemType.getDescription();
        } else {
            return "";
        }
    }

    public int getItemTypeQty(String name) {
        int qty = 0;

        InventorySlot inventorySlot = null;

        for (int i = 0; i < inventorySlots.size(); i++) {
            inventorySlot = inventorySlots.get(i);
            String slotName = inventorySlot.getItemTypeName();

            if (slotName.equals(name)) {
                qty += inventorySlot.getQty();
            }
        }

        return qty;
    }

    public int getUsedSlots() {
        int used = 0;

        InventorySlot inventorySlot = null;

        for (int i = 0; i < inventorySlots.size(); i++) {
            inventorySlot = inventorySlots.get(i);
            if (inventorySlot.getQty() > 0) {
                used++;
            }
        }

        return used;
    }

    public boolean contains(String name) {
        int qty = 0;

        InventorySlot inventorySlot = null;

        for (int i = 0; i < inventorySlots.size(); i++) {
            inventorySlot = inventorySlots.get(i);
            String slotName = inventorySlot.getItemTypeName();

            if (slotName.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public int getLevelForType(String name) {
        InventorySlot inventorySlot = null;

        for (int i = 0; i < inventorySlots.size(); i++) {
            inventorySlot = inventorySlots.get(i);
            String slotName = inventorySlot.getItemTypeName();

            if (slotName.equals(name)) {
                return inventorySlot.getLevel();
            }
        }

        return 1;
    }

    public boolean containsFromTop(String name, int slotCount) {
        int qty = 0;

        InventorySlot inventorySlot = null;

        for (int i = inventorySlots.size() - slotCount; i < inventorySlots.size(); i++) {
            inventorySlot = inventorySlots.get(i);
            String slotName = inventorySlot.getItemTypeName();

            if (slotName.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public boolean isEmpty() {
        int qty = 0;

        InventorySlot inventorySlot = null;

        for (int i = 0; i < inventorySlots.size(); i++) {
            inventorySlot = inventorySlots.get(i);
            if (inventorySlot.getQty() > 0) {
                return false;
            }
        }

        return true;
    }

    public int addToInventory(int startSlot, String name, int qty) {
        return addToInventory(startSlot, name, qty, 1);
    }

    public int addToInventory(int startSlot, String name, int qty, int level) {
        ItemType itemType = registry.getItemType(name);
        InventorySlot inventorySlot = null;

        if (itemType == null || qty < 1) {
            return -1;
        }

        int leftToDistribute = qty;

        //try and stack with an existing slot
        if (startSlot == 0) {
            for (int i = inventorySlots.size() - 1; i >= 0; i--) {
                inventorySlot = inventorySlots.get(i);
                if (inventorySlot.getItemTypeName().equals(name)) {
                    leftToDistribute = inventorySlot.addItem(itemType, leftToDistribute, level);
                }

                if (leftToDistribute <= 0) {
                    break;
                }
            }
        }

        if (leftToDistribute > 0) {
            for (int i = startSlot; i < inventorySlots.size(); i++) {
                inventorySlot = inventorySlots.get(i);
                leftToDistribute = inventorySlot.addItem(itemType, leftToDistribute, level);

                if (leftToDistribute <= 0) {
                    break;
                }
            }
        }

        return leftToDistribute;
    }

    public void deleteInventory(int slot, int qty) {
        InventorySlot invSlot = inventorySlots.get(slot);

        if (invSlot == null) {
            return;
        }

        int endingQty = invSlot.getQty() - qty;

        if (qty <= 0 || endingQty < 1) {
            setSlotQuantity(slot, 0);
        } else {
            setSlotQuantity(slot, endingQty);
        }

        return;
    }

    public void setSlotQuantity(int slot, int qty) {
        if (slot < 0 || slot > inventorySlots.size() - 1) {
            return;
        }

        InventorySlot invSlot = inventorySlots.get(slot);

        if (invSlot == null) {
            return;
        }

        int currentQty = invSlot.getQty();
        if (currentQty >= qty) {
            invSlot.setQty(qty);
        }
        return;
    }

    public void deleteItems(String name, int qty) {
        if (qty <= 0) {
            return;
        }

        int leftToDelete = qty;

        InventorySlot inventorySlot = null;

        for (int i = (inventorySlots.size() - 1); i >= 0; i--) {
            inventorySlot = inventorySlots.get(i);

            if (inventorySlot.getItemTypeName().equals(name)) {
                if (inventorySlot.getQty() > leftToDelete) {
                    inventorySlot.setQty(inventorySlot.getQty() - leftToDelete);
                    leftToDelete = 0;
                } else {
                    leftToDelete -= inventorySlot.getQty();
                    inventorySlot.setQty(0);
                }
            }

            if (leftToDelete <= 0) {
                break;
            }
        }

        return;
    }

    public void swapInventoryLocations(int slotFrom, int slotTo) {
        if (slotFrom < 0
                || slotFrom > inventorySlots.size() - 1
                || slotTo < 0
                || slotTo > inventorySlots.size() - 1
                || slotFrom == slotTo) {
            return;
        }

        InventorySlot invSlotFrom = inventorySlots.get(slotFrom);
        InventorySlot invSlotTo = inventorySlots.get(slotTo);

        if (invSlotFrom == null || invSlotTo == null) {
            return;
        }

        if (invSlotFrom.getQty() < 1) {
            return;
        }

        if (invSlotFrom.getItemType() == invSlotTo.getItemType()) {
            int qtyLeft = invSlotTo.addItem(invSlotFrom.getItemType(), invSlotFrom.getQty(), invSlotFrom.getLevel());
            invSlotFrom.setQty(qtyLeft);
        } else {
            inventorySlots.set(slotTo, invSlotFrom);
            inventorySlots.set(slotFrom, invSlotTo);
        }

        return;
    }

    public void equipFromSlot(int slotFrom, int slotTo) {
        if (slotFrom < 0
                || slotFrom > inventorySlots.size() - 1
                || slotTo < 0
                || slotTo > inventorySlots.size() - 1
                || slotFrom == slotTo) {
            return;
        }

        InventorySlot invSlotFrom = inventorySlots.get(slotFrom);
        InventorySlot invSlotTo = inventorySlots.get(slotTo);

        if (invSlotFrom == null || invSlotTo == null) {
            return;
        }

        if (invSlotFrom.getQty() < 1) {
            return;
        }

        if (invSlotFrom.getItemType() == invSlotTo.getItemType()) {
            int qtyLeft = invSlotTo.addItem(invSlotFrom.getItemType(), invSlotFrom.getQty(), invSlotFrom.getLevel());
            invSlotFrom.setQty(qtyLeft);
        } else {
            inventorySlots.set(slotTo, invSlotFrom);
            inventorySlots.set(slotFrom, invSlotTo);
        }

        return;
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}
