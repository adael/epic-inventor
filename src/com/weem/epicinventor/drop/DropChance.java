package com.weem.epicinventor.drop;

import com.weem.epicinventor.utility.*;

import java.io.*;

public class DropChance implements Serializable {

    protected static final long serialVersionUID = 10000L;
    private String itemName;
    private float percentageChance;
    private int minQty;
    private int maxQty;

    public DropChance(String in, float pc, int min, int max) {
        itemName = in;
        percentageChance = pc;
        minQty = min;
        maxQty = max;
    }

    public void setTransient() {
    }

    public Drop generateDrop() {
        Drop drop = null;

        int randomNumber = Rand.getRange(1, 10000);

        if (randomNumber <= (percentageChance * 100f)) {
            drop = new Drop(itemName, Rand.getRange(minQty, maxQty));
        }

        return drop;
    }
    
    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}