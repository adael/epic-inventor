package com.weem.epicinventor.weapon;

import java.io.*;

public class WeaponType implements Serializable {

    protected static final long serialVersionUID = 10000L;

    private String itemName = "";
    private String type = "";
    private int[] damage;
    private int speed = 0;
    private int knockBackX = 0;
    private int knockBackY = 0;
    private int maxHits = 0;
    private int range = 0;
    private int animationFrames = 1;
    private boolean comesBack = false;

    public WeaponType(String in, String t, int[] d, int s, int kbx, int kby, int mh, int r, int a, boolean c) {
        itemName = in;
        type = t;
        damage = d;
        speed = s;
        knockBackX = kbx;
        knockBackY = kby;
        maxHits = mh;
        range = r;
        animationFrames = a;
        comesBack = c;
    }

    public String getItemName() {
        return itemName;
    }

    public String getType() {
        return type;
    }

    public int[] getDamage() {
        return damage;
    }

    public int getSpeed() {
        return speed;
    }

    public int getKnockBackX() {
        return knockBackX;
    }

    public int getKnockBackY() {
        return knockBackY;
    }

    public int getMaxHits() {
        return maxHits;
    }

    public int getRange() {
        return range;
    }

    public int getAnimationFrames() {
        return animationFrames;
    }

    public String getImageName() {
        return "Weapon/" + itemName;
    }

    public boolean getComesBack() {
        return comesBack;
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}
