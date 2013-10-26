package com.weem.epicinventor.world.block;

import com.weem.epicinventor.*;
import com.weem.epicinventor.utility.*;
import com.weem.epicinventor.world.*;

import java.awt.*;
import java.util.*;
import java.io.*;

public class BlockManager implements Serializable {

    protected static final long serialVersionUID = 10000L;

    private int pCols, pRows, mapCols, mapRows;
    private int mapWidth, mapHeight;
    private HashMap blockTypes;
    private HashMap blockTypeIdMap;
    private short[][] blocks;
    transient private int[] xPos;
    private int mapSurfaceMin;
    private int mapSurfaceMax;
    private int mapLevelStart;
    private int mapLevelHeight;
    transient private Registry registry;
    transient private GameController gameController;
    transient private final static int BLOCK_WIDTH = 16;
    transient private final static int BLOCK_HEIGHT = 16;
    transient private final static int TILES_PER_LEVEL = 3;
    transient private final static String CONFIG_FILE = "Blocks.dat";

    public BlockManager(GameController gc, Registry rg) {
        gameController = gc;

        registry = rg;

        pCols = (gameController.getPWidth() / BLOCK_WIDTH) + 1;
        pRows = (gameController.getPHeight() / BLOCK_HEIGHT) + 1;

        blockTypes = new HashMap();
        blockTypeIdMap = new HashMap();

        loadBlockTypes("Blocks.dat");

        loadBlocks();
        xPos = new int[2];

        int xMove = (int) (BLOCK_WIDTH * gameController.getMoveFactor());
        if (xMove == 0) {
            xMove = 1;
        }

        int yMove = (int) (BLOCK_HEIGHT * gameController.getMoveFactor());
        if (yMove == 0) {
            xMove = 1;
        }

        gameController.setXMoveSize(xMove);
        gameController.setYMoveSize(yMove);
    }

    public BlockManager() {
        blockTypes = new HashMap();
        blockTypeIdMap = new HashMap();

        loadBlockTypes("Blocks.dat");
    }

    public void updateResolution() {
        pCols = (gameController.getPWidth() / BLOCK_WIDTH) + 1;
        pRows = (gameController.getPHeight() / BLOCK_HEIGHT) + 1;
    }

    public void setTransient(Registry rg) {
        registry = rg;
        gameController = rg.getGameController();
        xPos = new int[2];

        Iterator it = blockTypes.entrySet().iterator();
        while (it.hasNext()) {
            ArrayList blockTypeList = (ArrayList)(((Map.Entry)it.next()).getValue());
            Iterator itr = blockTypeList.iterator();
            while(itr.hasNext()) {
                BlockType blockType = (BlockType)itr.next();
                blockType.setTransient(rg);
            }
        }
    }

    private void loadBlockTypes(String fn) {
        int i = 0;
        String line;
        String parts[];
        ArrayList blockTypeList;
        BlockType blockType;

        try {
            InputStream in = getClass().getResourceAsStream(GameController.CONFIG_DIR + fn);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }

                i++;

                parts = line.split(" ");
                if (parts.length != 5) {
                    System.out.println("Error in " + fn);
                }

                blockType = new BlockType(registry,
                        i,
                        parts[0],
                        parts[1],
                        Integer.parseInt(parts[2]),
                        Boolean.parseBoolean(parts[3]),
                        Boolean.parseBoolean(parts[4]));

                blockTypeIdMap.put(new Integer(i), blockType);

                if (blockTypes.containsKey(parts[1])) {
                    blockTypeList = null;
                    blockTypeList = (ArrayList) blockTypes.get(parts[1]);
                    blockTypeList.add(blockType);
                    blockTypes.put(parts[1], blockTypeList);
                } else {
                    blockTypeList = null;
                    blockTypeList = new ArrayList();
                    blockTypeList.add(blockType);
                    blockTypes.put(parts[1], blockTypeList);
                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public int getRandomIdByGroup(String g) {
        int id = 0;

        if(g.equals("Dirt") && Rand.getRange(0, 1200) < 5) {
            g = "DirtAccent";
        } else if(g.equals("Stone") && Rand.getRange(0, 1200) < 5) {
            g = "StoneAccent";
        }
        if (blockTypes.containsKey(g)) {
            ArrayList blockTypeList;

            blockTypeList = (ArrayList) blockTypes.get(g);

            BlockType bt = (BlockType) blockTypeList.get(Rand.getRange(0, blockTypeList.size() - 1));

            id = bt.getType();
        } else {
            id = 0;
        }

        return id;
    }
    
    public BlockType getRandomBlockTypeByGroup(String g) {
        int id = 0;

        if (blockTypes.containsKey(g)) {
            ArrayList blockTypeList;

            blockTypeList = (ArrayList) blockTypes.get(g);

            BlockType bt = (BlockType) blockTypeList.get(Rand.getRange(0, blockTypeList.size() - 1));

            return bt;
        } else {
            return null;
        }
    }

    public int getBlockTypeIdByName(String name) {
        int id = 0;
        Iterator it = blockTypes.entrySet().iterator();
        while (it.hasNext() && id == 0) {
            ArrayList blockTypeList = (ArrayList)(((Map.Entry)it.next()).getValue());
            Iterator itr = blockTypeList.iterator();
            while(itr.hasNext() && id == 0) {
                BlockType blockType = (BlockType)itr.next();
                if(blockType.getName().equals(name)) {
                    id = blockType.getType();
                }
            }
        }
        return id;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public boolean isIdInGroup(int id, String g) {
        if (id == 0 && g.equals("None")) {
            return true;
        }

        if (blockTypes.containsKey(g)) {
            ArrayList blockTypeList;

            blockTypeList = (ArrayList) blockTypes.get(g);

            for (int i = 0; i < blockTypeList.size(); i++) {
                BlockType bt = (BlockType) blockTypeList.get(i);
                if (bt.getType() == id) {
                    return true;
                }
            }
        }

        return false;
    }

    public static int getBlockWidth() {
        return BLOCK_WIDTH;
    }

    public static int getBlockHeight() {
        return BLOCK_HEIGHT;
    }

    public int getPWidth() {
        return gameController.getPWidth();
    }

    public int getPHeight() {
        return gameController.getPHeight();
    }

    public int getMapOffsetX() {
        return gameController.getMapOffsetX();
    }

    public int getMapOffsetY() {
        return gameController.getMapOffsetY();
    }

    public int findFloor(int xWorld) {
        int xCol = (xWorld / BLOCK_WIDTH);
        if (xCol == -1) {
            int i = 0;
        } else {
            if(xCol < blocks.length && xCol >= 0)
            {
                for (int y = (blocks[xCol].length - 1); y >= 0; y--) {
                    if (!isIdInGroup(blocks[xCol][y], "None"))
                    {
                        BlockType bt = getBlockTypeById(blocks[xCol][y]);
                        if (bt != null && !bt.isBackground()) {
                            return (y * BLOCK_HEIGHT) + BLOCK_HEIGHT;
                        }
                    }
                }
            }
        }

        return 0;
    }

    public short[] blocksUnder(int xStartPix, int xEndPix, int yPix) {
        int xStart = xStartPix / BLOCK_WIDTH;
        int xEnd = xEndPix / BLOCK_WIDTH;
        short[] underBlocks = new short[2 + (xEndPix - xStartPix) / BLOCK_WIDTH];

        for (int i = 0; i < 1 + (xEndPix - xStartPix) / BLOCK_WIDTH; i++) {
            underBlocks[i] = 0;
        }
        underBlocks[0] = blocks[xStart][(yPix - 1) / BLOCK_HEIGHT];
        if (!isIdInGroup(underBlocks[0], "None")) {
            for (int xCol = xStart; xCol <= xEnd; xCol++) {
                if(underBlocks.length < (xCol - xStart) && blocks.length < xCol && blocks[xCol].length < (yPix - 1) / BLOCK_HEIGHT) {
                    underBlocks[xCol - xStart] = blocks[xCol][(yPix - 1) / BLOCK_HEIGHT];
                }
            }
        }
        return underBlocks;
    }

    public int[] getTownStartEnd(int x, int y) {
        boolean isTown = false;
        BlockType bt = null;
        
        xPos[0] = xPos[1] = -1;
        int xCurrent = (x - 1) / BLOCK_WIDTH;
        int yCurrent = (y - 1) / BLOCK_HEIGHT;
        short b = blocks[xCurrent][yCurrent];
        if (isIdInGroup(b, "Town")) {
            boolean keepLooping = true;
            while (keepLooping) {
                xPos[0] = xCurrent * BLOCK_WIDTH;
                xCurrent--;
                if (xCurrent < 0) {
                    break;
                }
                b = blocks[xCurrent][yCurrent];
                if(!isIdInGroup(b, "Town")) {
                    keepLooping = false;
                }
                bt = getBlockTypeById(blocks[xCurrent][yCurrent+1]);
                if (bt != null && !bt.isBackground()) {
                    keepLooping = false;
                }
            }
            xCurrent++;
            b = blocks[xCurrent][yCurrent];
            isTown = false;
            if (isIdInGroup(b, "Town")) {
                isTown = true;
            }
            while (isTown) {
                xPos[1] = (xCurrent + 1) * BLOCK_WIDTH;
                xCurrent++;
                if (xCurrent > blocks.length - 1) {
                    break;
                }
                b = blocks[xCurrent][yCurrent];

                isTown = isIdInGroup(b, "Town");
                bt = getBlockTypeById(blocks[xCurrent][yCurrent+1]);
                if (bt != null && !bt.isBackground()) {
                    isTown = false;
                }
            }
        }
        return xPos;
    }

    public int findNextFloor(int xWorld, int yWorld, int height) {
        int xCol = (xWorld / BLOCK_WIDTH);
        int yCol = (yWorld / BLOCK_HEIGHT);

        int blockSpaceFound = 0;

        int blockSpaceNeeded = (int)Math.ceil((float)height / (float)BLOCK_HEIGHT);
        if (blockSpaceNeeded < 1) {
            blockSpaceNeeded = 1;
        }
        if(xCol > 0 && xCol < blocks.length && yCol < blocks[xCol].length) {
            for (int y = yCol; y >= 0; y--) {
                if (!isIdInGroup(blocks[xCol][y], "None")) {
                    BlockType bt = getBlockTypeById(blocks[xCol][y]);
                    if (bt != null && !bt.isBackground()) {
                        if (blockSpaceFound >= blockSpaceNeeded) {
                            return ((y+1) * BLOCK_HEIGHT) + BLOCK_HEIGHT;
                        }
                        blockSpaceFound = 0;
                    } else {
                        blockSpaceFound++;
                    }
                } else {
                    blockSpaceFound++;
                }
            }
        }
        else
        {
            int i = 0;
        }
        return 0;
    }

    public boolean doesRectContainBlocks(int mapX, int mapY, int width, int height) {
        int xStart = mapX / BLOCK_WIDTH;
        int xEnd = (mapX + width) / BLOCK_WIDTH;
        
        int yStart = mapY / BLOCK_HEIGHT;
        int yEnd = (mapY + height) / BLOCK_HEIGHT;
        
        for(int xCol = xStart; xCol <= xEnd; xCol++) {
            for(int yCol = yStart; yCol <= yEnd; yCol++) {
                if(xCol < blocks.length && xCol >= 0)
                {
                    if(yCol < blocks[xCol].length && yCol > 0) {
                        if (blocks[xCol][yCol] != 0) {
                            BlockType bt = getBlockTypeById(blocks[xCol][yCol]);
                            if (bt != null && !bt.isBackground()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    public void setBlockByGroup(int mapX, int mapY, String g) {
        setBlock(mapX, mapY, (short)getRandomIdByGroup(g));
    }
    
    public void setBlock(int mapX, int mapY, short blockId) {
        int x = mapX / BLOCK_WIDTH;
        int y = mapY / BLOCK_HEIGHT;
        
        blocks[x][y] = blockId;
    }
    
    public String getBlockGroup(int mapX, int mapY) {
        String group = "None";
        short blockId = getBlockFromPoint(new Point(mapX, mapY));
        BlockType bt = getBlockTypeById(blockId);
        if(bt != null) {
            group = bt.getGroup();
        }
        return group;
    }

    public BlockType getBlockTypeById(short id) {
        Integer i = new Integer(id);
        BlockType bt = null;
        if(blockTypeIdMap.containsKey(i)) {
            bt = (BlockType)blockTypeIdMap.get(i);
        }
        return bt;
    }

    public short getBlockFromPoint(Point p) {
        int xIndex = (p.x / BLOCK_WIDTH);
        int yIndex = (p.y / BLOCK_HEIGHT);

        if (xIndex >= 0 && xIndex <= (mapCols - 1) && yIndex >= 0 && yIndex <= (mapRows - 1)) {
            return blocks[xIndex][yIndex];
        }

        return 0;
    }

    public int mapToPanelX(int x) {
        return x - gameController.getMapOffsetX();
    }

    public int mapToPanelY(int y) {
        return y - gameController.getMapOffsetY();
    }
    
    public void update() {
    }

    public void render(Graphics g) {
        //render only the blocks that are in the view port

        int pOffsetX = (gameController.getMapOffsetX() / BLOCK_WIDTH);
        int pOffsetY = (gameController.getMapOffsetY() / BLOCK_HEIGHT);
        BlockType blockType = null;
        int xPos, yPos;

        int yRange = pRows + pOffsetY;
        int xRange;
        yPos = mapToPanelY(pOffsetY * BLOCK_HEIGHT);
        yPos = getPHeight() - yPos;
        yPos -= BLOCK_HEIGHT;
        for (int y = pOffsetY; y <= yRange; y++) {
            xRange = pCols + pOffsetX;
            xPos = mapToPanelX(pOffsetX * BLOCK_WIDTH);
            for (int x = pOffsetX; x <= xRange; x++) {
                if (x <= (mapCols - 1) && y <= (mapRows - 1)) {
                    blockType = getBlockTypeById(blocks[x][y]);
                    if(blockType != null) {
                        g.drawImage(blockType.getImage(), xPos, yPos, null);
                    }
                }
                xPos += BLOCK_WIDTH;
            }
            yPos -= BLOCK_HEIGHT;
        }

        /*System.out.println("Surface Min: " + mapSurfaceMin);
        int yPos = this.mapToPanelY(mapSurfaceMin);
        yPos = this.getPHeight() - yPos;
        yPos -= 1;
        g.setColor(Color.red);
        g.drawLine(0, yPos, 10000, yPos);*/
    }

    private void loadBlocks() {
        World newWorld = new World("");
        BlockType bt = null;

        int[] size = newWorld.getWorldSize();

        mapCols = size[0];
        mapRows = size[1];

        mapWidth = BLOCK_WIDTH * mapCols;
        mapHeight = BLOCK_HEIGHT * mapRows;
        mapSurfaceMin = newWorld.getWorldGroundMin() * BLOCK_HEIGHT;
        mapSurfaceMax = newWorld.getWorldGroundMax() * BLOCK_HEIGHT;
        mapLevelStart = newWorld.getWipZMin() * BLOCK_HEIGHT;
        mapLevelHeight = newWorld.getWipHeight() * BLOCK_HEIGHT * TILES_PER_LEVEL;

        blocks = new short[size[0]][size[1]];

        for (int x = 0; x < size[0]; x++) {
            for (int y = size[1] - 1; y > -1; y--) {
                blocks[x][y] = (short)newWorld.blockArray[x][y];
            }
        }
    }
    
    public int getMapSurfaceMin() {
        return mapSurfaceMin;
    }
    
    public int getMapSurfaceMax() {
        return mapSurfaceMax;
    }
    
    public int getLevelByY(int y) {
        int level = 0;
        
        if(y >= mapSurfaceMin) {
            return 0;
        }
        
        int distanceFromSurface = mapSurfaceMin - y;
        
        level = (int) Math.ceil((float) distanceFromSurface / (float) mapLevelHeight);
        
        return level;
    }
    
    public int getLevelTop(int level) {
        int y = 0;
        
        if(level <= 0) {
            return mapHeight;
        }
        
        return mapSurfaceMin - ((level - 1) * mapLevelHeight) - 1;
    }
    
    public int getLevelBottom(int level) {
        int y = 0;
        
        if(level <= 0) {
            return mapSurfaceMin;
        }
        
        return mapSurfaceMin - (level * mapLevelHeight);
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}