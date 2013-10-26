package com.weem.epicinventor.armor;

import java.io.*;

public class ArmorType implements Serializable {

    protected static final long serialVersionUID = 10000L;

    private String set;
    private String type;
    private int[] armor;

    public ArmorType(String s, String t, int[] ab) {
        set = s;
        type = t;
        armor = ab;
    }

    public int[] getArmorBonus() {
        return armor;
    }

    public String getName() {
        return set + type;
    }

    public String getType() {
        return type;
    }

    public String getImageName() {
        return "Armor/" + set + "/" + type;
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}