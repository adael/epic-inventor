package com.weem.epicinventor.drop;

import java.io.*;
import java.util.ArrayList;

public class DropChanceCollection implements Serializable {

    protected static final long serialVersionUID = 10000L;
    private ArrayList<DropChance> dropChances;

    public DropChanceCollection() {
        dropChances = new ArrayList<DropChance>();
    }

    public void setTransient() {
        for(int i = 0; i < dropChances.size(); i++) {
            dropChances.get(i).setTransient();
        }
    }

    public void addDropChance(String in, float pc, int min, int max) {
        DropChance dropChance = new DropChance(in, pc, min, max);

        dropChances.add(dropChance);
    }

    public void clearDropChances() {
        dropChances.clear();
    }

    public ArrayList<Drop> generateDrops() {
        Drop drop = null;
        DropChance dropChance = null;
        ArrayList<Drop> drops = new ArrayList<Drop>();

        for (int i = 0; i < dropChances.size(); i++) {
            dropChance = dropChances.get(i);
            drop = dropChance.generateDrop();
            if (drop != null) {
                drops.add(drop);
            }
        }

        return drops;
    }
    
    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}