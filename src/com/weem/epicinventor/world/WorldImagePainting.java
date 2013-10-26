package com.weem.epicinventor.world;

import com.weem.epicinventor.utility.*;
import com.weem.epicinventor.world.block.*;
import com.weem.epicinventor.*;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import java.util.jar.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.color.ColorSpace;
import java.util.regex.Pattern;

public class WorldImagePainting {

    private World world;
    private WorldTown worldTown;
    private BlockManager blockManager;
    private static int[] ground;
    private static int level;
    private static int wipZMin;
    private static int wipHeight;
    private static HashMap groups;
    private static HashMap blockTypes;
    private static HashMap groupOverrides;
    private static HashMap blockTypeOverrides;
    private static ArrayList caveSpots;
    private static ArrayList currentLevel;
    private static ArrayList lastLevel;
    private static ArrayList noExitImages;
    private static String config = "WorldImages.dat";
    private static String worldImagePath = "/Images/World/";

    public WorldImagePainting(World w, WorldTown wt, BlockManager bm, int[] wg) {
        world = w;
        worldTown = wt;
        blockManager = bm;
        ground = wg;
        level = 0;
        groups = new HashMap();
        blockTypes = new HashMap();
        groupOverrides = new HashMap();
        blockTypeOverrides = new HashMap();
        caveSpots = new ArrayList();
        currentLevel = new ArrayList();
        lastLevel = new ArrayList();
        noExitImages = new ArrayList();

        loadConfig(config);
    }

    public void loadConfig(String config) {
        String line;
        String type;

        try {
            InputStream in = getClass().getResourceAsStream(GameController.CONFIG_DIR + config);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }
                StringTokenizer tokens = new StringTokenizer(line);
                if (tokens.countTokens() == 1) {
                } else if (tokens.countTokens() == 3) {
                    type = tokens.nextToken();
                    if (type.equals("group")) {
                        addGroup(tokens);
                    } else if (type.equals("blockType")) {
                        addBlockType(tokens);
                    }
                } else if(tokens.countTokens() == 4) {
                    type = tokens.nextToken();
                    if (type.equals("group")) {
                        addGroupOverride(tokens);
                    } else if (type.equals("blockType")) {
                        addBlockTypeOverride(tokens);
                    }
                } else {
                    EIError.debugMsg("wrong config argument count", EIError.ErrorLevel.Error);
                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void addGroup(StringTokenizer tokens) {
        int tokenCount = tokens.countTokens();
        String[] settings = new String[tokenCount+1];
        String line = "";
        for (int i = 0; i < tokenCount; i++) {
            settings[i] = tokens.nextToken();
            line += " " + settings[i];
        }
        EIError.debugMsg("Image Paint Group:" + line, EIError.ErrorLevel.Notice);
        groups.put(Integer.parseInt(settings[1], 16), settings);
    }

    public void addBlockType(StringTokenizer tokens) {
        int tokenCount = tokens.countTokens();
        String[] settings = new String[tokenCount+1];
        String line = "";
        for (int i = 0; i < tokenCount; i++) {
            settings[i] = tokens.nextToken();
            line += " " + settings[i];
        }
        settings[tokenCount] = Integer.toString(blockManager.getBlockTypeIdByName(settings[0]));
        EIError.debugMsg("Image Paint Block Type:" + line, EIError.ErrorLevel.Notice);
        blockTypes.put(Integer.parseInt(settings[1], 16), settings);
    }

    public void addGroupOverride(StringTokenizer tokens) {
        int tokenCount = tokens.countTokens();
        String[] settings = new String[tokenCount+1];
        String line = "";
        for (int i = 0; i < tokenCount; i++) {
            settings[i] = tokens.nextToken();
            line += " " + settings[i];
        }
        EIError.debugMsg("Image Paint Group:" + line, EIError.ErrorLevel.Notice);
        groupOverrides.put(settings[tokenCount-1]+""+Integer.parseInt(settings[1], 16), settings);
    }

    public void addBlockTypeOverride(StringTokenizer tokens) {
        int tokenCount = tokens.countTokens();
        String[] settings = new String[tokenCount+1];
        String line = "";
        for (int i = 0; i < tokenCount; i++) {
            settings[i] = tokens.nextToken();
            line += " " + settings[i];
        }
        settings[tokenCount] = Integer.toString(blockManager.getBlockTypeIdByName(settings[0]));
        EIError.debugMsg("Image Paint Block Type:" + line, EIError.ErrorLevel.Notice);
        blockTypeOverrides.put(settings[tokenCount-1]+""+Integer.parseInt(settings[1], 16), settings);
    }

    public int getWipZMin() {
        return wipZMin;
    }

    public int getWipHeight() {
        return wipHeight;
    }

    public BufferedImage[] getPaintImages(String theme) {
        BufferedImage[] images = null;
        String line;
        ArrayList imageNames = new ArrayList();

        try {
            InputStream in = getClass().getResourceAsStream(GameController.CONFIG_DIR + config);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }
                StringTokenizer tokens = new StringTokenizer(line);
                if (tokens.countTokens() == 1) {
                    String[] nameParts = line.split("/");
                    if(nameParts[0].equals(theme)) {
                        imageNames.add(nameParts[1]);
                    }
                } else if (tokens.countTokens() == 3) {
                } else if(tokens.countTokens() == 4) {
                } else {
                    EIError.debugMsg("wrong config argument count", EIError.ErrorLevel.Error);
                }
            }
            in.close();
            images = new BufferedImage[imageNames.size()];
            for(int i = 0; i < imageNames.size(); i++) {
                images[i] = loadImage(worldImagePath+theme+"/"+imageNames.get(i));
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
//        try {
//            String[] imageNames = ResourceList.getResourceListing(WorldImagePainting.class, worldImagePath+theme+"/");
//            int count = 0;
//            for(int i = 0; i < imageNames.length; i++) {
//                if(!imageNames[i].equals(".svn")) {
//                    count++;
//                }
//            }
//            images = new BufferedImage[count];
//            count = 0;
//            for(int i = 0; i < imageNames.length; i++) {
//                if(!imageNames[i].equals(".svn")) {
//                    images[count] = loadImage(worldImagePath+theme+"/"+imageNames[i]);
//                    count++;
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Error: " + e.getMessage());
//        }
        return images;
    }

    public BufferedImage loadImage(String fnm) {
        BufferedImage im = null;
        try {
            im = ImageIO.read(ImageLoader.class.getResource(fnm));
        } catch (IOException e) {
            //System.out.println("Load Image error for " + fnm + ":\n" + e);
        }
        return im;
    }
    
    public int getMaxZCaveSpot() {
        int count = caveSpots.size();
        int[] caveSpot;
        int zMax = 0;
        int iMax = -1;
        for(int i = 0; i < count; i++) {
            caveSpot = (int [])caveSpots.get(i);
            if(caveSpot[0] > zMax && caveSpot[2] - caveSpot[1] > 3) {
                zMax = caveSpot[0];
                iMax = i;
            }
        }
        return iMax;
    }
    
    public int[][] spawnCaves(int[][] currentBlockArray, int caves, int minX, int maxX, int minZ, int maxZ) {
        ArrayList maxCaveSpots = new ArrayList();
        for(int i = 0; i < caves*2; i++) {
            int iMax = getMaxZCaveSpot();
            int[] caveSpot = null;
            if(iMax > -1) {
                caveSpot = (int[])caveSpots.remove(iMax);
            }
            if(caveSpot != null) {
                maxCaveSpots.add(caveSpot);
            }
        }
        int removes = maxCaveSpots.size() - caves;
        for(int i = 0; i < removes; i++) {
            maxCaveSpots.remove(Rand.getRange(0, maxCaveSpots.size() - 1));
        }
        for(int i = 0; i < maxCaveSpots.size(); i++) {
            int[] caveSpot = (int[])maxCaveSpots.get(i);
            World.direction direction;
            if(Rand.getFloat() < .5) {
                direction = World.direction.Left;
            } else {
                direction = World.direction.Right;
            }
            WorldCavern cavern = new WorldCavern(world, worldTown, 5, 3, 3, ground);
            currentBlockArray = cavern.carveCave(currentBlockArray, (caveSpot[1]+caveSpot[2])/2, caveSpot[0], minX, minZ, maxX, maxZ, 0.0f, direction);
        }
        return currentBlockArray;
    }

    public int[][] paintImages(int[][] currentBlockArray, int xMin, int xMax, int zMin, int zMax) {
        BufferedImage[] images = getPaintImages("Theme1");
        int h = images[0].getHeight();
        int w = images[0].getWidth();
        int i = 0;
        level = 0;
        wipZMin = zMin;
        wipHeight = h;
        String[] messages = new String[] {"Weem-atizing Elements", "Calculating the 'Epic'", "Removing Mukshapumpa", "Equipping Melvin", "Thanking Testers", "Sneaking up behind you...", "Injecting Bacon" };
        boolean closeToGround = false;
        Game.loadingText = "Making Things Interesting...";
        for(int z = zMin; z <= zMax-h; z += h) {
            if(messages.length > level) {
                Game.loadingText = messages[level];
            }
            for(int x = xMin; x <= xMax-w; x += w) {
                closeToGround = closeToGround(x, z, w, h);
                //System.out.println("closeToGround "+closeToGround);
                if(!closeToGround) {
                    i = getPaintImageIndex(currentBlockArray, images, x, z, xMin, xMax, (z == zMin));
                    if(i > -1) {
                        currentBlockArray = paintImage(currentBlockArray, images[i], x, z);
                    }
                }
            }
            //System.out.println("level");
            lastLevel = (ArrayList)currentLevel.clone();
            currentLevel.clear();
            level++;
            
            //new level
        }
        currentBlockArray = spawnCaves(currentBlockArray, 3, xMin, xMax, zMin, zMax);
        noExitImages.clear();
        return currentBlockArray;
    }

    public int getPaintImageIndex(int[][] currentBlockArray, BufferedImage[] images, int x, int z, int xMin, int xMax, boolean bottomLevel) {
        int[][] pixels = null;
        int i = -1;
        int fit = 0;
        int count = 0;
        do {
            i = Rand.getRange(0, images.length-1);
            pixels = getImagePixels(images[i]);
            fit = paintImageFit(currentBlockArray, pixels, x, z, xMin, xMax, bottomLevel);
            count++;
        } while(fit < 1 && count <= images.length);
        if(fit == 2 && noExitImages.size() < 1 || count > images.length) {
            i = -1;
        }
        //System.out.println("index: "+i+" fit: "+fit);
        return i;
    }

    public int paintImageFit(int[][] currentBlockArray, int[][] pixels, int x, int z, int xMin, int xMax, boolean bottomLevel) {
        int fit = 0;
        int[] exitData = new int[2];
        exitData[0] = getExits(pixels);
        if(ground[x] < z+pixels[0].length*1.5) {
            boolean exitUp = exitUp(lastLevel, x);
            if(currentLevel.size() == 0) {
                if(bottomConnect(currentBlockArray, pixels, x, z)) {
                    if((exitData[0] & Integer.parseInt("1", 16)) > 0) {
                        //System.out.println("top level fit");
                        fit = 1;
                    }
                } else if (!exitUp) {
                    //System.out.println("top level not fit");
                    fit = 2;
                }
            } else {
                int[] leftExits = (int[])currentLevel.get(currentLevel.size()-1);
                //System.out.println("Close to groud "+exitUp+" "+exitData[0]);
                if(exitUp && (exitData[0] & Integer.parseInt("1", 16)) > 0) {
                    //System.out.println("exitUp");
                    fit = 1;
                } else if(leftConnect(currentBlockArray, pixels, x, z) && (exitData[0] & Integer.parseInt("1", 16)) > 0) {
                    //System.out.println("leftConnect");
                    fit = 1;
                } else if(!exitUp && !((leftExits[0] & Integer.parseInt("2", 16)) > 0) && exitData[0] == 0) {
                    //System.out.println("not exitUp");
                    fit = 1;
                }
            }
        } else if(bottomLevel && (exitData[0] & Integer.parseInt("1", 16)) > 0) {
            //System.out.println("bottom and up");
            fit = 1;
        } else if(bottomConnect(currentBlockArray, pixels, x, z) && (exitData[0] & Integer.parseInt("3", 16)) > 0) {
            //System.out.println("bottom connects");
            fit = 1;
        } else {
            if(leftConnect(currentBlockArray, pixels, x, z)) {
                //System.out.println("else leftConnect");
                fit = 1;
            } else if(lastLevel.size() > 0) {
                int[] range = conectionRange(lastLevel, x, pixels.length);
                if(currentLevel.size() == 0 && range[2] == 1) {
                    //System.out.println("nothing placed and more chances");
                    fit = 1;
                } else if(range[2] == 0) {
                    //System.out.println("no more chances");
                    fit = 2;
                }
            }
        }
        exitData[1] = x;
        if(fit == 1) {
            currentLevel.add(exitData);
        } if(fit == 2) {
            exitData[0] = 0;
            if(noExitImages.size() > 0) {
                currentLevel.add(exitData);
            }
        }
        return fit;
    }
    
    public int[] conectionRange(ArrayList level, int xStart, int width) {
        int[] range = new int[3];
        int[] current = null;
        int i = 0;
        range[0] = range[1] = range[2] = -1;
        for(; i < level.size(); i++) {
            current = (int[])level.get(i);
            if(current[1] >= xStart) {
                break;
            }
        }
        if(level.size() > i + 2) {
            current = (int[])level.get(i+1);
            int[] last = (int[])level.get(i);
            if((last[0] & Integer.parseInt("2", 16)) > 0 && (current[0] & Integer.parseInt("8", 16)) > 0) {
                range[0] = range[1] = last[1];
                if((current[0] & Integer.parseInt("1", 16)) > 0) {
                    range[2] = 1;
                }
            }
            for(i = i + 2; i < level.size(); i++) {
                current = (int[])level.get(i);
                last = (int[])level.get(i-1);
                if(current[1]-last[1] > width) {
                    break;
                }
                if((current[0] & Integer.parseInt("1", 16)) > 0) {
                    range[2] = 1;
                }
                if((last[0] & Integer.parseInt("2", 16)) > 0 && (current[0] & Integer.parseInt("8", 16)) > 0) {
                    range[1] = last[1];
                } else {
                    break;
                }
            }
        }
        return range;
    }

    public int getExits(int[][] pixels) {
        int exits = 0;
        int w = pixels.length;
        int h = pixels[0].length;
        for(int i = 3; i < w; i++) {
            if(
                isCaveBG(getBlockTypeIdFromPixel(pixels[i][h-1])) &&
                isCaveBG(getBlockTypeIdFromPixel(pixels[i-1][h-1])) &&
                isCaveBG(getBlockTypeIdFromPixel(pixels[i-2][h-1]))
              ) {
                exits = exits | 1;
            }
            if(
                isCaveBG(getBlockTypeIdFromPixel(pixels[i][0])) &&
                isCaveBG(getBlockTypeIdFromPixel(pixels[i-1][0])) &&
                isCaveBG(getBlockTypeIdFromPixel(pixels[i-2][0]))
              ) {
                exits = exits | 4;
            }
        }
        for(int i = 3; i < h; i++) {
            if(
                isCaveBG(getBlockTypeIdFromPixel(pixels[w-1][i])) &&
                isCaveBG(getBlockTypeIdFromPixel(pixels[w-1][i-1])) &&
                isCaveBG(getBlockTypeIdFromPixel(pixels[w-1][i-2]))
              ) {
                exits = exits | 2;
            }
            if(
                isCaveBG(getBlockTypeIdFromPixel(pixels[0][i])) &&
                isCaveBG(getBlockTypeIdFromPixel(pixels[0][i-1])) &&
                isCaveBG(getBlockTypeIdFromPixel(pixels[0][i-2]))
              ) {
                exits = exits | 8;
            }
        }
        if(exits == 0) {
            noExitImages.add(pixels);
        }
        return exits;
    }

    public boolean leftConnect(int[][] currentBlockArray, int[][] pixels, int x, int z) {
        boolean connects = false;
        boolean imageConnects = false;
        for(int i = 3; i < pixels[0].length && !connects; i++) {
            if(
                isCaveBG(currentBlockArray[x-1][z+i]) &&
                isCaveBG(currentBlockArray[x-1][z+i-1]) &&
                isCaveBG(currentBlockArray[x-1][z+i-2]) &&
                isCaveBG(getBlockTypeIdFromPixel(pixels[0][i])) &&
                isCaveBG(getBlockTypeIdFromPixel(pixels[0][i-1])) &&
                isCaveBG(getBlockTypeIdFromPixel(pixels[0][i-2]))
              ) {
                imageConnects = true;
            }
        }
        if(imageConnects) {
            int[] currentExits = null;
            int[] nextExits = null;
            int lastX = x;
            for(int i = currentLevel.size()-1; i > 0; i--) {
                currentExits = (int [])currentLevel.get(i);
                nextExits = (int [])currentLevel.get(i);
                if((currentExits[0] & Integer.parseInt("4", 16)) > 0) {
                    if(exitUp(lastLevel, currentExits[1])) {
                        connects = true;
                    }
                }
                //if next tile is not a tile distance away
                if(nextExits[1] != (lastX-pixels.length)) {
                    break;
                }
                //if tile does not connects to the left
                if(!((currentExits[0] & Integer.parseInt("8", 16)) > 0 && (nextExits[0] & Integer.parseInt("8", 16)) > 0)) {
                    break;
                }
                lastX = currentExits[1];
            }
        }
        return connects;
    }
    
    public boolean exitUp(ArrayList level, int x) {
        boolean exitUp = false;
        int[] exits = null;
        for(int i = 0; i < level.size(); i++) {
            exits = (int [])level.get(i);
            if(exits[1] == x) {
                if((exits[0] & Integer.parseInt("1", 16)) > 0) {
                    exitUp = true;
                }
            }
        }
        return exitUp;
    }

    public boolean bottomConnect(int[][] currentBlockArray, int[][] pixels, int x, int z) {
        boolean connects = false;
        for(int i = 3; i < pixels.length && !connects; i++) {
            if(
                isCaveBG(currentBlockArray[x+i][z-1]) &&
                isCaveBG(currentBlockArray[x+i-1][z-1]) &&
                isCaveBG(currentBlockArray[x+i-2][z-1]) &&
                isCaveBG(getBlockTypeIdFromPixel(pixels[i][0])) &&
                isCaveBG(getBlockTypeIdFromPixel(pixels[i-1][0])) &&
                isCaveBG(getBlockTypeIdFromPixel(pixels[i-2][0]))
              ) {
                connects = true;
            }
        }
        return connects;
    }

    public boolean isCaveBG(int blockTypeId) {
        boolean isCave = false;
        if(blockTypeId > 0) {
            if(blockManager.getBlockTypeById((short)blockTypeId).isBackground()) {
                isCave = true;
            }
        }
        return isCave;
    }

    public boolean closeToGround(int currentX, int currentZ, int width, int hight) {
        boolean closeToGround = false;
        for(int x = currentX; x < currentX + width; x++) {
            if(currentZ+hight-10 > ground[x]) {
                closeToGround = true;
            }
        }
        return closeToGround;
    }

    public int[][] paintImage(int[][] currentBlockArray, BufferedImage image, int xStart, int zStart) {
        int[][] pixels = getImagePixels(image);
        int[] caveSpot = new int[3];
        caveSpot[0] = -1;
        caveSpot[1] = -1;
        caveSpot[2] = -1;

        for(int z = 0; z < pixels[0].length; z++) {
            for(int x = 0; x < pixels.length; x++) {
                if(zStart+z+20 < ground[xStart+x]) {
                    currentBlockArray[xStart+x][zStart+z] = paintBlock(currentBlockArray[xStart+x][zStart+z], pixels[x][z]);
                    if(z == pixels[0].length - 1 && isCaveBG(currentBlockArray[xStart+x][zStart+z])) {
                        if(caveSpot[0] == -1) {
                            caveSpot[0] = zStart+z;
                            caveSpot[1] = xStart+x;
                            caveSpot[2] = xStart+x;
                        } else {
                            caveSpot[2] = xStart+x;
                        }
                    }
                }
            }
        }
        if(caveSpot[0] > 0) {
            caveSpots.add(caveSpot);
        }
        return currentBlockArray;
    }

    public int getBlockTypeIdFromPixel(int imagePixel) {
        int blockTypeId = -1;
        String[] line = (String[])groups.get(imagePixel);
        if(line != null) {
            int newBlockType = blockManager.getRandomIdByGroup(line[0]);
            if(newBlockType > 0) {
                blockTypeId = newBlockType;
            }
        }
        line = (String[])blockTypes.get(imagePixel);
        if(line != null) {
            int newBlockType = Integer.parseInt(line[line.length - 1]);
            if(newBlockType > 0) {
                blockTypeId = newBlockType;
            }
        }
        int blockTypeOverrideId = getBlockTypeOverrideIdFromPixel(imagePixel);
        if(blockTypeOverrideId > 0) {
            blockTypeId = blockTypeOverrideId;
        }
        return blockTypeId;
    }

    public int getBlockTypeOverrideIdFromPixel(int imagePixel) {
        int blockTypeId = -1;
        String[] line = (String[])groupOverrides.get((level+1)+""+imagePixel);
        if(line != null) {
            int newBlockType = blockManager.getRandomIdByGroup(line[0]);
            if(newBlockType > 0) {
                blockTypeId = newBlockType;
            }
        }
        line = (String[])blockTypeOverrides.get((level+1)+""+imagePixel);
        if(line != null) {
            int newBlockType = Integer.parseInt(line[line.length - 1]);
            if(newBlockType > 0) {
                blockTypeId = newBlockType;
            }
        }
        return blockTypeId;
    }

    public int paintBlock(int currentBlock, int imagePixel) {
        int newBlockType = getBlockTypeIdFromPixel(imagePixel);
        if(newBlockType != -1) {
            currentBlock = newBlockType;
        }
        return currentBlock;
    }
    
    public int[][] getImagePixels(BufferedImage image) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        int[] tmpPixels = new int[w * h];
        image.getRGB(0, 0, w, h, tmpPixels, 0, w);
        int[][] pixels = new int[w][h];
        
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                pixels[x][h-1-y] = tmpPixels[x+y*w] & Integer.parseInt("FFFFFF", 16);
            }
        }
        return pixels;
    }
}
