package com.weem.epicinventor.world.block;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class Block implements Serializable {

    protected static final long serialVersionUID = 10000L;

    private int mapX, mapY;
    private BlockType blockType;
    private BlockManager blockManager;

    public Block(BlockManager bm, BlockType bt, int x, int y) {
        blockManager = bm;
        blockType = bt;
        mapX = x;
        mapY = y;
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public BlockType getBlockType(){
        return blockType;
    }
    
    public boolean isBackground() {
        return blockType.isBackground();
    }

    public BufferedImage getImage() {
        return blockType.getImage();
    }

    public void display(Graphics g) {
        int xPos = blockManager.mapToPanelX(mapX);
        int yPos = blockManager.mapToPanelY(mapY);

        //flip the yPos since drawing happens top down versus bottom up
        yPos = blockManager.getPHeight() - yPos;

        //subtract the block height since points are bottom left and drawing starts from top left
        yPos -= blockManager.getBlockHeight();

        g.drawImage(blockType.getImage(), xPos, yPos, null);
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}