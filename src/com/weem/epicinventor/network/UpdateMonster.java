package com.weem.epicinventor.network;

import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.ai.*;

import java.awt.*;
import java.io.*;

public class UpdateMonster implements Serializable {
    
    protected static final long serialVersionUID = 10000L;
    public String id = "";
    public int mapX;
    public int mapY;
    public String action = "None";
    public int dataInt;
    public int dataInt2;
    public Point dataPoint;
    public long dataLong;
    public Actor actor;
    public String dataString = "";
    public Goal currentGoal;
    public Goal previousGoal;
    
    public UpdateMonster(String pid) {
        id = pid;
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}
