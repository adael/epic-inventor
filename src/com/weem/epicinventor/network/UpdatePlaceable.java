package com.weem.epicinventor.network;

import com.weem.epicinventor.actor.monster.*;
import com.weem.epicinventor.inventory.*;
import com.weem.epicinventor.placeable.*;

import java.io.*;

public class UpdatePlaceable implements Serializable {
    
    protected static final long serialVersionUID = 10000L;
    public String id = "";
    public int hitPoints;
    public int totalHitPoints;
    public int mapX;
    public int mapY;
    public Monster source;
    public int dataInt;
    public Object dataObject;
    public Placeable.State state;
    public Inventory inventory;
    public String action = "None";
    
    public UpdatePlaceable(String pid) {
        id = pid;
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}
