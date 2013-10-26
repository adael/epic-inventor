package com.weem.epicinventor.item;

import java.io.*;

public class Item implements Serializable {

    protected static final long serialVersionUID = 10000L;
    private ItemType itemType;
    
    private int level;
    
    public Item(ItemType it, int l) {
        itemType = it;
        level = l;
        
        if(level < 1) {
            level = 1;
        }
        if(level > 20) {
            level = 20;
        }
    }

    public void setTransient() {
        if(level > 20) {
            level = 20;
        }
    }
    
    public ItemType getItemType() {
        return itemType;
    }
    
    public int getLevel() {
        if(level < 1) {
            level = 1;
        }
        
        return level;
    }
    
    public void setLevel(int l) {
        level = l;
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}
