package com.weem.epicinventor.item;

import com.weem.epicinventor.*;

import java.io.*;
import java.awt.image.*;
import java.util.*;

public class ItemType implements Serializable {

    protected static final long serialVersionUID = -5491724686028030754L;

    transient private ItemManager itemManager;
    transient private Registry registry;
    private String name;
    private int createQty;
    private int maxStack;
    private String category;
    private String type;
    private int techLevel;
    private String item1 = "";
    private int item1Qty = 0;
    private String item2 = "";
    private int item2Qty = 0;
    private String item3 = "";
    private int item3Qty = 0;
    private String item4 = "";
    private int item4Qty = 0;
    private String skill1 = "";
    private String skill2 = "";
    private String workBench = "";
    private float xpModifier = 1f;
    private String description = "";

    public ItemType(ItemManager im, Registry rg, String n, int cq, int ms, String c, String t, int tl,
            String i1, int i1q, String i2, int i2q, String i3, int i3q, String i4,
            int i4q, String s1, String s2, String wb, float xm, String d) {
        name = n;
        itemManager = im;
        registry = rg;
        createQty = cq;
        maxStack = ms;
        category = c;
        type = t;
        techLevel = tl;
        item1 = i1;
        item1Qty = i1q;
        item2 = i2;
        item2Qty = i2q;
        item3 = i3;
        item3Qty = i3q;
        item4 = i4;
        item4Qty = i4q;
        skill1 = s1;
        skill2 = s2;
        workBench = wb;
        xpModifier = xm;
        description = d;
    }

    public int getCreateQty() {
        return createQty;
    }

    public void setTransient(Registry rg) {
        registry = rg;
        itemManager = rg.getItemManager();
    }

    public int getMaxStack() {
        return maxStack;
    }

    public String getName() {
        return name;
    }

    public float getXPModifier() {
        return xpModifier;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getImageName() {
        return "Items/" + name;
    }
    
    public ArrayList<String> getRequirementString() {
        ArrayList<String> requirements = new ArrayList<String>();
        
        if(!item1.equals("None")) {
            requirements.add(item1 + ":" + item1Qty);
        }
        if(!item2.equals("None")) {
            requirements.add(item2 + ":" + item2Qty);
        }
        if(!item3.equals("None")) {
            requirements.add(item3 + ":" + item3Qty);
        }
        if(!item4.equals("None")) {
            requirements.add(item4 + ":" + item4Qty);
        }
        if(!workBench.equals("None")) {
            requirements.add(workBench + ":0");
        }
        
        return requirements;
    }

    public BufferedImage getImage() {
        return registry.getImageLoader().getImage("Items/" + name);
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}