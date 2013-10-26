package com.weem.epicinventor.drop;

import java.io.*;

public class Drop implements Serializable {

    protected static final long serialVersionUID = 10000L;
    private String itemName;
    private int qty;
    private int level;

    public Drop(String in, int q) {
        itemName = in;
        qty = q;
        level = 1;
    }

    public Drop(String in, int q, int l) {
        itemName = in;
        qty = q;
        level = l;
    }

    public void setTransient() {
    }

    public String getItemName() {
        return itemName;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int q) {
        qty = q;
    }

    public int getLevel() {
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