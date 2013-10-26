package com.weem.epicinventor.world.block;

import com.weem.epicinventor.*;

import java.awt.image.*;
import java.io.*;

public class BlockType implements Serializable {

    protected static final long serialVersionUID = 10000L;

    private int type;
    private String name;
    private String group;
    private int playerDamage;
    private boolean foundation;
    private boolean background;
    transient private Registry registry;

    public BlockType(Registry rg, int t, String n, String g, int pd, boolean f, boolean bg) {
        registry = rg;
        type = t;
        name = n;
        group = g;
        playerDamage = pd;
        foundation = f;
        background = bg;
    }

    public void setTransient(Registry rg) {
        registry = rg;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getGroup(){
        return group;
    }
    
    public boolean isBackground() {
        return background;
    }

    public BufferedImage getImage() {
        return registry.getImageLoader().getImage("Blocks/" + name);
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}