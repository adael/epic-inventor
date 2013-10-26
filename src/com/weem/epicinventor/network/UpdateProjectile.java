package com.weem.epicinventor.network;

import java.awt.*;
import java.io.*;

public class UpdateProjectile implements Serializable {

    protected static final long serialVersionUID = 10000L;
    public String id = "";
    public String playerId = "";
    public String image;
    public int speed;
    public Point start;
    public Point end;
    public boolean friendly;
    public boolean placeable;
    public boolean disregardTerrain;
    public int damage;
    public String action;

    public UpdateProjectile() {
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}