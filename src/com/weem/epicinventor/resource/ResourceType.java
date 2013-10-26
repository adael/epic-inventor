package com.weem.epicinventor.resource;

import com.weem.epicinventor.*;
import com.weem.epicinventor.world.block.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class ResourceType implements Serializable {

    protected static final long serialVersionUID = 10000L;
    private String name;
    private final static int RESOURCE_HEIGHT = 32;
    transient private Registry registry;
    transient private ResourceManager resourceManager;
    private String type;
    private int qtyMin;
    private int qtyMax;
    private int gatherTime;
    private int[] levels;

    public ResourceType(ResourceManager rm, Registry rg, String n, String t, int qMin, int qMax, int gt, int[] l) {
        registry = rg;
        resourceManager = rm;
        name = n;
        type = t;
        qtyMin = qMin;
        qtyMax = qMax;
        gatherTime = gt;
        levels = l;
    }

    public void setTransient(Registry rg) {
        registry = rg;
        resourceManager = rg.getResourceManager();
    }

    public int getGatherTime() {
        return gatherTime;
    }
    
    public int[] getLevels() {
        return levels;
    }

    public int getQtyMin() {
        return qtyMin;
    }

    public int getQtyMax() {
        return qtyMax;
    }

    public int getQty() {
        return Rand.getRange(qtyMin, qtyMax);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Point getNewXY(int mapWidth, int level) {
        Point p = new Point(0, 0);
        
        p.x = Rand.getRange(1, mapWidth);
        
        Registry r = registry;
        BlockManager bm = r.getBlockManager();

        p.y = Rand.getRange(bm.getLevelBottom(level),
                bm.getLevelTop(level));

        p.y = resourceManager.findNextFloor(p.x-1, p.y, RESOURCE_HEIGHT);
        
        return p;
    }

    public BufferedImage getImage() {
        return registry.getImageLoader().getImage("Resources/" + name);
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}