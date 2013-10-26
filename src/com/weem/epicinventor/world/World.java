package com.weem.epicinventor.world;

import com.weem.epicinventor.*;
import com.weem.epicinventor.utility.*;
import com.weem.epicinventor.world.block.*;

import java.util.Random;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author Brandon
 */
public class World implements Serializable {

    private static Random randGen = new Random();
    private static int SIZE = 2048;
    public static int HORIZ_BLOCKS = 2;
    private static int NUMBER_TOWN_AREAS = HORIZ_BLOCKS;
    private static int TOWN_SIZE = 42;
    public static int GROUND_LEVEL = 4 * SIZE / 5;
    private static int MNT_SMOOTHING = 15;
    private static int MNT_FACTOR = 25;
    private static int ORE_FACTOR = 20;
    private static boolean INCLUDE_LAVA = false;
    private static int CAVERN_FACTOR = 25;
    private static int CAVE_RADIUS = 3;
    private static float CAVE_TERM = 0.25f;
    public WorldCavern cavern;
    private BlockManager blockManager;
    private int[] worldGround;
    private int worldGroundMax;
    private int worldGroundMin;
    private WorldImagePainting imagePainting;
    public int[][] blockArray;

    enum direction {

        None,
        Left,
        Right,
        Up,
        Down
    }

    public World(String filename) {
        if (filename.equals("")) {
            blockManager = new BlockManager();

            blockArray = GenerateWorld(SIZE, ORE_FACTOR, INCLUDE_LAVA);
        } else {
            World w = Read(filename);
            blockArray = w.blockArray;
        }
        worldGround = null;
    }

    public World(
            String filename, int size, int horiz_blocks, int ground_level, int mnt_smoothing, int mnt_factor, int ore_factor,
            int cavern_factor, float cave_term) {
        
        blockManager = new BlockManager();
        
        SIZE = size;
        HORIZ_BLOCKS = horiz_blocks;
        GROUND_LEVEL = ground_level;
        MNT_SMOOTHING = mnt_smoothing;
        MNT_FACTOR = mnt_factor;
        ORE_FACTOR = ore_factor;

        CAVERN_FACTOR = cavern_factor;
        CAVE_TERM = cave_term;
        if (filename.equals("")) {
            blockArray = GenerateWorld(SIZE, ORE_FACTOR, INCLUDE_LAVA);
        } else {
            World w = Read(filename);
            blockArray = w.blockArray;
        }
        worldGround = null;
    }

    public void Write(String filename) {
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(filename);
            out = new ObjectOutputStream(fos);
            out.writeObject(this);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public World Read(String filename) {
        World w = null;
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(filename);
            in = new ObjectInputStream(fis);
            w = (World) in.readObject();
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return w;
    }

    public int[][] GenerateWorld(int size, int oreFactor, boolean includeLava) {
        EIError.debugMsg("GenerateWorld Start", EIError.ErrorLevel.Notice);
        Game.loadingText = "Generating World";
        float[][] noise;

        int[][] newBlockArray = GenerateConstant(size * HORIZ_BLOCKS, size, GROUND_LEVEL, "Dirt");

        // Add gaps.
        for (int b = 0; b < HORIZ_BLOCKS; b++) {
            noise = GeneratePerlinNoise(32);
            noise = InterpolateData(noise, 32, size);
            for (int i = 0; i < MNT_FACTOR; i++) {
                if(randGen.nextFloat() > .10) {
                    int xMin = randGen.nextInt(size - 50) + b*size;
                    int xMax = Math.max(randGen.nextInt((b + 1) * size - xMin) + xMin, xMin + 50);
                    PaintWithRandomWalk(newBlockArray, noise, size, Math.max(randGen.nextInt(size / 64), 1), "None", false, xMin, xMax, 0, GROUND_LEVEL, size / 8);
                } else {
                    PaintRectangle(newBlockArray, blockManager.getRandomIdByGroup("None"), randGen.nextInt(HORIZ_BLOCKS*size - 1), randGen.nextInt(GROUND_LEVEL), Rand.getRange(30, size / 4), Rand.getRange(25, size / 16), 0, HORIZ_BLOCKS*size, 0, GROUND_LEVEL);
                }
            }
        }

        //all fall down
        EIError.debugMsg("Fall Down", EIError.ErrorLevel.Notice);
        Game.loadingText = "Digging Terrain";
        int moveTo = -1;
        int tmpBlock = blockManager.getRandomIdByGroup("None");
        for (int x = 0; x < size * HORIZ_BLOCKS; x++) {
            moveTo = -1;
            for (int z = 0; z < GROUND_LEVEL; z++) {
                if (blockManager.isIdInGroup(newBlockArray[x][z], "None")) {
                    if (moveTo == -1) {
                        moveTo = z;
                    }
                } else {
                    if (moveTo > -1) {
                        tmpBlock = newBlockArray[x][moveTo];
                        newBlockArray[x][moveTo] = newBlockArray[x][z];
                        newBlockArray[x][z] = tmpBlock;
                        moveTo++;
                    }
                }
            }
        }
        
        SmoothSerface(newBlockArray, size);

        WorldTown wt = new WorldTown(randGen);
        wt.addTownAreas(newBlockArray, worldGround, blockManager, NUMBER_TOWN_AREAS, TOWN_SIZE, 0, size * HORIZ_BLOCKS);
        
        // Add Stone.
        EIError.debugMsg("Add Stone", EIError.ErrorLevel.Notice);
        Game.loadingText = "Dropping Rocks";
        for (int b = 0; b < HORIZ_BLOCKS; b++) {
            noise = GeneratePerlinNoise(32);
            noise = InterpolateData(noise, 32, size);
            for (int i = 0; i < oreFactor; i++) {
                PaintWithRandomWalk(newBlockArray, noise, size, Math.max(randGen.nextInt(size / 64), 1), "Stone", false, b * size, (b + 1) * size - 1, 20, GROUND_LEVEL, size / 2);
            }
        }

        // Add ore.
        EIError.debugMsg("Add Ore", EIError.ErrorLevel.Notice);
        for (int b = 0; b < HORIZ_BLOCKS; b++) {
            noise = GeneratePerlinNoise(32);
            noise = InterpolateData(noise, 32, size);
            for (int i = 0; i < oreFactor; i++) {
                PaintWithRandomWalk(newBlockArray, noise, size, Math.max(randGen.nextInt(size / 64), 1), "Stone", false, b * size, (b + 1) * size - 1, 20, GROUND_LEVEL, size / 2);
            }
        }

        // Add sand.
        Game.loadingText = "Sprinkling in Some Sand";
        EIError.debugMsg("Add Sand", EIError.ErrorLevel.Notice);
        for (int b = 0; b < HORIZ_BLOCKS; b++) {
            noise = GeneratePerlinNoise(32);
            noise = InterpolateData(noise, 32, size);
            for (int i = 0; i < oreFactor; i++) {
                PaintWithRandomWalk(newBlockArray, noise, size, Math.max(randGen.nextInt(size / 64), 1), "Sand", false, b * size, (b + 1) * size - 1, 20, GROUND_LEVEL, size / 2);
            }
        }

        // Add mud.
        Game.loadingText = "Making Mud";
        EIError.debugMsg("Add Mud", EIError.ErrorLevel.Notice);
        for (int b = 0; b < HORIZ_BLOCKS; b++) {
            noise = GeneratePerlinNoise(32);
            noise = InterpolateData(noise, 32, size);
            for (int i = 0; i < oreFactor; i++) {
                PaintWithRandomWalk(newBlockArray, noise, size, Math.max(randGen.nextInt(size / 64), 1), "Mud", false, b * size, (b + 1) * size - 1, 20, GROUND_LEVEL, size / 2);
            }
        }

        // Carve some caves into the ground.
//        cavern = new WorldCavern(this, wt, CAVERN_FACTOR, CAVE_RADIUS, CAVE_TERM, worldGround);
//        newBlockArray = cavern.carveCavern(newBlockArray, size, (HORIZ_BLOCKS * size - size * 3 / 4), (HORIZ_BLOCKS * size - size / 4), (size / 16), (GROUND_LEVEL / 4), size / 32, 0.1f);

        imagePainting = new WorldImagePainting(this, wt, blockManager, worldGround);
        newBlockArray = imagePainting.paintImages(newBlockArray, 20, SIZE*HORIZ_BLOCKS-20, 20, GROUND_LEVEL);

        noise = null;
        System.gc();

        EIError.debugMsg("GenerateWorld End", EIError.ErrorLevel.Notice);
        return newBlockArray;
    }

    public int getWipZMin() {
        return imagePainting.getWipZMin();
    }

    public int getWipHeight() {
        return imagePainting.getWipHeight();
    }

    public int getWorldGroundMin() {
        return worldGroundMin;
    }
    
    public int getWorldGroundMax() {
        return worldGroundMax;
    }

    public int[][] SmoothSerface(int[][] newBlockArray, int size) {
        //Curve fit ground
        EIError.debugMsg("Curve Fit", EIError.ErrorLevel.Notice);
        int[] ptsIn = new int[size * HORIZ_BLOCKS];
        int lastZ = 0;
        Bspline spline = new Bspline(size / MNT_SMOOTHING);
        for (int z = GROUND_LEVEL; z > 0; z--) {
            if (newBlockArray[0][z] != blockManager.getRandomIdByGroup("None")) {
                ptsIn[0] = z;
                lastZ = z;
                spline.addPoint(0, z);
                break;
            }
        }
        for (int x = 1; x < size * HORIZ_BLOCKS; x++) {
            if (newBlockArray[x][lastZ] != blockManager.getRandomIdByGroup("None")) {
                for (int z = lastZ; z < GROUND_LEVEL + 1; z++) {
                    if (blockManager.isIdInGroup(newBlockArray[x][z], "None")) {
                        ptsIn[x] = z - 1;
                        lastZ = ptsIn[x];
                        if (x % (size / MNT_SMOOTHING) == 0 || x == size * HORIZ_BLOCKS - 1) {
                            spline.addPoint(x, ptsIn[x]);
                        }
                        break;
                    }
                }
            } else {
                for (int z = lastZ; z > 0; z--) {
                    if (newBlockArray[x][z] != blockManager.getRandomIdByGroup("None")) {
                        ptsIn[x] = z;
                        lastZ = z;
                        if (x % (size / MNT_SMOOTHING) == 0 || x == size * HORIZ_BLOCKS - 1) {
                            spline.addPoint(x, z);
                        }
                        break;
                    }
                }
            }
            if (ptsIn[x] == 0) {
                ptsIn[x] = lastZ;
            }
        }
        worldGround = spline.getPoints(size * HORIZ_BLOCKS);
        worldGroundMax = -1;
        worldGroundMin = 9999999;
        for (int x = 0; x < size * HORIZ_BLOCKS; x++) {
            if (worldGround[x] > 3) {
                if (ptsIn[x] < worldGround[x]) {
                    for (int z = ptsIn[x]; z <= worldGround[x]; z++) {
                        if (z == worldGround[x] && blockManager.isIdInGroup(newBlockArray[x][z - 1], "Dirt")) {
                            if(z < worldGroundMin) {
                                worldGroundMin = z;
                            }
                            if(z > worldGroundMax) {
                                worldGroundMax = z;
                            }
                            newBlockArray[x][z] = blockManager.getRandomIdByGroup("Grass");
                        } else {
                            newBlockArray[x][z] = blockManager.getRandomIdByGroup("Dirt");
                        }
                    }
                } else {
                    for (int z = ptsIn[x]; z >= worldGround[x]; z--) {
                        if (z == worldGround[x] && blockManager.isIdInGroup(newBlockArray[x][z - 1], "Dirt")) {
                            if(z < worldGroundMin) {
                                worldGroundMin = z;
                            }
                            if(z > worldGroundMax) {
                                worldGroundMax = z;
                            }
                            newBlockArray[x][z] = blockManager.getRandomIdByGroup("Grass");
                        } else {
                            newBlockArray[x][z] = blockManager.getRandomIdByGroup("None");
                        }
                    }
                }
            } else {
                EIError.debugMsg("Bspline hit bottom", EIError.ErrorLevel.Error);
            }
        }
        return newBlockArray;
    }

    // Generates a set of constant values.
    public int[][] GenerateConstant(int sizeX, int sizeZ, int fillZ, String group) {
        int[][] data = new int[sizeX][sizeZ];
        for (int x = 0; x < sizeX; x++) {
            for (int z = 0; z < sizeZ; z++) {
                if (z < fillZ) {
                    data[x][z] = blockManager.getRandomIdByGroup(group);
                } else {
                    data[x][z] = blockManager.getRandomIdByGroup("None");
                }
            }
        }
        return data;
    }

    public float[][] GenerateGradient(int size) {
        float[][] data = new float[size][size];

        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                data[x][z] = (float) z / size;
            }
        }

        return data;
    }

    // Radial gradient concentrated with high values at the outside.
    public float[][] GenerateRadialGradient(int size) {
        float[][] data = new float[size][size];

        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                float dist = (float) Math.sqrt(Math.pow(x - size / 2, 2));
                data[x][z] = Clamp(dist / size * 0.3f * (float) z / size, 0, 1);
            }
        }
        return data;
    }

    public float Clamp(float value, float min, float max) {
        if (value < min) {
            value = min;
        }
        if (value > max) {
            value = max;
        }
        return value;
    }

    // Generates some perlin noise!
    public float[][] GeneratePerlinNoise(int size) {
        float[][] data = new float[size][size];

        float[][] noise = null;
        for (int f = 4; f <= 32; f *= 2) {
            EIError.debugMsg("GenerateNoise", EIError.ErrorLevel.Notice);
            noise = GenerateNoise(f, 2f / f);
            EIError.debugMsg("InterpolateData", EIError.ErrorLevel.Notice);
            noise = InterpolateData(noise, f, size);
            EIError.debugMsg("AddDataTo", EIError.ErrorLevel.Notice);
            AddDataTo(data, noise, size);
            EIError.debugMsg("AddDataTo End", EIError.ErrorLevel.Notice);
        }

        return data;
    }

    // Generates a cube of noise with sides of length size. Noise falls in a linear
    // distribution ranging from 0 to magnitude.
    public float[][] GenerateNoise(int size, float magnitude) {
        float[][] noiseArray = new float[size + (size / 8)][size + (size / 8)];
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                noiseArray[x][z] = randGen.nextFloat() * magnitude;
            }
        }
        return noiseArray;
    }

    // Resizes dataIn, with size sizeIn, to be of size sizeOut.
    public float[][] InterpolateData(float[][] dataIn, int sizeIn, int sizeOut) {
        float[][] dataOut = new float[sizeOut][sizeOut];

        int r = sizeOut / sizeIn;
        if (r < 1) {
            r = 1;
        }

        for (int x = 0; x < sizeOut; x++) {
            for (int z = 0; z < sizeOut; z++) {
                int xIn0 = x / r, zIn0 = z / r;
                if (xIn0 >= sizeIn - 2) {
                    xIn0 = sizeIn - 2;
                }
                if (zIn0 >= sizeIn - 2) {
                    zIn0 = sizeIn - 2;
                }
                int xIn1 = xIn0 + 1, zIn1 = zIn0 + 1;

                float v00 = dataIn[xIn0][zIn0];
                float v10 = dataIn[xIn1][zIn0];
                float v01 = dataIn[xIn0][zIn1];
                float v11 = dataIn[xIn1][zIn1];

                float xS = ((float) (x % r)) / r;
                float zS = ((float) (z % r)) / r;

                dataOut[x][z] = v00 * (1 - xS) * (1 - zS)
                        + v10 * xS * (1 - zS)
                        + v01 * (1 - xS) * zS
                        + v11 * xS * zS;
            }
        }

        return dataOut;
    }

    // Adds the values in dataSrc to the values in dataDst, storing the result in dataDst.
    public void AddDataTo(float[][] dataDst, float[][] dataSrc, int size, float scalarDst, float scalarSrc) {
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                dataDst[x][z] = Math.max(Math.min(dataDst[x][z] * scalarDst + dataSrc[x][z] * scalarSrc, 1), 0);
            }
        }
    }

    public void AddDataTo(float[][] dataDst, float[][] dataSrc, int size) {
        AddDataTo(dataDst, dataSrc, size, 1, 1);
    }

    // Does a random walk of noiseData, setting cells to 0 in BlockArray in the process.
    public void PaintWithRandomWalk(
            int[][] currentBlockArray, float[][] noiseData, int size,
            int paintRadius, String group, boolean dontStopAtEdge) {
        int x = Rand.getRange(paintRadius, size - paintRadius);
        int z = Rand.getRange(paintRadius, size - paintRadius);
        EIError.debugMsg("Start 1" + x + " " + z + " " + paintRadius, EIError.ErrorLevel.Notice);
        PaintWithRandomWalk(
                currentBlockArray, noiseData, size,
                paintRadius, group, dontStopAtEdge,
                x, z, 0, size / 64);
    }

    public void PaintWithRandomWalk(
            int[][] currentBlockArray, float[][] noiseData, int size,
            int paintRadius, String group, boolean dontStopAtEdge,
            int xMin, int xMax, int zMin, int zMax, int maxLoops) {
        int x = Rand.getRange(xMin + paintRadius, xMax - paintRadius);
        int z = Rand.getRange(zMin + paintRadius, zMax - paintRadius);
        EIError.debugMsg("Start 2" + zMax + " " + zMin + " " + paintRadius, EIError.ErrorLevel.Notice);
        PaintWithRandomWalk(
                currentBlockArray, noiseData, size,
                paintRadius, group, dontStopAtEdge,
                x, z, xMin, maxLoops);
    }

    public void PaintWithRandomWalk(
            int[][] currentBlockArray, float[][] noiseData, int size,
            int paintRadius, String group, boolean dontStopAtEdge,
            int x, int z, int xOffset, int maxLoops) {
        EIError.debugMsg("Start 3" + size + " " + paintRadius + " " + dontStopAtEdge + " " + x + " " + z + " " + xOffset + " ", EIError.ErrorLevel.Notice);
        if (z < size / 50) {
            z = 0;
        }

        int count = 0;
        int loops = 0;
        float newNoise = 0.0f;
        float oldNoise = 0.0f;
        int newX = 0;
        int newZ = 0;
        int currentX = 0;
        int currentZ = 0;
        int CHECK_POINTS = 8;
        paintRadius = Math.max(paintRadius, 2);
        boolean groundIssue = false;
        if(worldGround != null) {
            if(worldGround.length > x + xOffset && worldGround[x + xOffset] < z + paintRadius * 3) {
                groundIssue = true;
            }
        }
        if(!groundIssue)
        while (dontStopAtEdge == false || count < size) {
            xOffset = (xOffset/size)*size;
            if(newNoise > oldNoise) {
                paintRadius += Rand.getRange(0, 2);
            } else {
                paintRadius -= Rand.getRange(0, 2);
            }
            paintRadius = Math.min(Math.max(paintRadius, 2), 10);

            if(x < noiseData.length && z < noiseData[x].length) {
                oldNoise = noiseData[x][z];
            }

            PaintAtPoint(currentBlockArray, group, paintRadius, x + xOffset, z, 0, size * HORIZ_BLOCKS, 0, size);
            
            newNoise = 0.0f;
            newX = 0;
            newZ = 0;
            
            int degrees = 360 * Rand.getRange(0, CHECK_POINTS) / CHECK_POINTS;
            if(degrees == 90 || degrees == 270) {
                degrees = 360 * Rand.getRange(0, CHECK_POINTS) / CHECK_POINTS;
            }
            currentX = x + (int)(paintRadius * Math.cos(Math.toRadians(degrees)));
            currentZ = z + (int)(paintRadius * Math.sin(Math.toRadians(degrees)));
            groundIssue = false;
            if(worldGround != null) {
                if(currentX < 0 || currentX >= size || currentZ < 0 || currentZ >= size) {
                    break;
                }
                if(worldGround[currentX + xOffset] < currentZ + paintRadius * 3) {
                    groundIssue = true;
                }
            }
            if(currentX > 0 && currentX < size && currentZ < size && currentZ > 0 && !groundIssue) {
                newNoise = noiseData[currentX][currentZ];
                newX = currentX;
                newZ = currentZ;
            } else {
                newX = x - (int)(paintRadius * Math.cos(Math.toRadians(degrees)));
                newZ = z - (int)(paintRadius * Math.sin(Math.toRadians(degrees)));
                if(newX < 0 || newX >= size || newZ < 0 || newZ >= size) {
                    break;
                }
                newNoise = noiseData[newX][newZ];
            }
            x = newX;
            z = newZ;
            
            loops += paintRadius;
            if (loops > maxLoops) {
                break;
            }
        }
        EIError.debugMsg("End", EIError.ErrorLevel.Notice);
    }

    public int[] GetComposition(int[][] currentBlockArray, int paintValue, int paintRadius, int x, int z, int xOffset, int size) {
        int[] blockTypes = new int[3];
        blockTypes[0] = 0;
        blockTypes[1] = 0;
        blockTypes[2] = 0;
        for (int dx = -paintRadius; dx <= paintRadius; dx++) {
            for (int dz = -paintRadius; dz <= paintRadius; dz++) {
                if (x - xOffset + dx >= 0 && z + dz >= 0 && x - xOffset + dx < size && z + dz < size) {
                    if (
                            currentBlockArray[x + dx][z + dz] == blockManager.getRandomIdByGroup("None") ||
                            currentBlockArray[x + dx][z + dz] == blockManager.getRandomIdByGroup("Town")
                       ) {
                        blockTypes[0]++;
                    } else if (currentBlockArray[x + dx][z + dz] == paintValue) {
                        blockTypes[1]++;
                    } else {
                        blockTypes[2]++;
                    }
                }
            }
        }
        return blockTypes;
    }

    public boolean PaintAtPoint(int[][] currentBlockArray, String group, int paintRadius, int x, int z, int minX, int maxX, int minZ, int maxZ) {
        boolean painted = false;
        for (int dx = -paintRadius; dx <= paintRadius; dx++) {
            for (int dz = -paintRadius; dz <= paintRadius; dz++) {
                if (x + dx >= minX && z + dz >= minZ && x + dx < maxX && z + dz < maxZ) {
                    if (
                            dx * dx + dz * dz < paintRadius * paintRadius &&
                            currentBlockArray[x + dx][z + dz] != blockManager.getRandomIdByGroup("None") &&
                            currentBlockArray[x + dx][z + dz] != blockManager.getRandomIdByGroup("Town")
                       ) {
                        currentBlockArray[x + dx][z + dz] = blockManager.getRandomIdByGroup(group);
                        painted = true;
                    }
                }
            }
        }
        return painted;
    }

    public boolean PaintAtPoint(int[][] currentBlockArray, String group, int paintRadius, int x, int z, int xOffset, int size) {
        boolean painted = false;
        for (int dx = -paintRadius; dx <= paintRadius; dx++) {
            for (int dz = -paintRadius; dz <= paintRadius; dz++) {
                if (x - xOffset + dx >= 0 && z + dz >= 0 && x - xOffset + dx < size && z + dz < size) {
                    if (
                            dx * dx + dz * dz < paintRadius * paintRadius &&
                            currentBlockArray[x + dx][z + dz] != blockManager.getRandomIdByGroup("None") &&
                            currentBlockArray[x + dx][z + dz] != blockManager.getRandomIdByGroup("Town")
                       ) {
                        currentBlockArray[x + dx][z + dz] = blockManager.getRandomIdByGroup(group);
                        painted = true;
                    }
                }
            }
        }
        return painted;
    }

    public boolean PaintRectangle(int[][] currentBlockArray, int paintValue, int x, int z, int width, int height, int minX, int maxX, int minZ, int maxZ) {
        boolean painted = false;
        for (int dx = x; dx <= x+width; dx++) {
            for (int dz = z; dz <= z+height; dz++) {
                if (dx >= minX && dz >= minZ && dx < maxX && dz < maxZ) {

                    if (
                            currentBlockArray[dx][dz] != blockManager.getRandomIdByGroup("None") &&
                            currentBlockArray[dx][dz] != blockManager.getRandomIdByGroup("Town")
                       ) {
                        currentBlockArray[dx][dz] = paintValue;
                        painted = true;
                    }
                }
            }
        }
        return painted;
    }

    public int[][] PaintLine(int[][] currentBlockArray, String group, int radius, int startX, int startZ, int endX, int endZ, int minX, int minZ, int maxX, int maxZ) {
        int dx = endX - startX;
        int dz = endZ - startZ;
        int count = (int) Math.sqrt(dx * dx + dz * dz);
        for (int i = 0; i < count; i++) {
            PaintAtPoint(currentBlockArray, group, radius, startX + (dx * i / count), startZ + (dz * i / count), 0, SIZE * HORIZ_BLOCKS, 0, SIZE);
        }
        return currentBlockArray;
    }

    public void Print() {
        this.Print(blockArray);
    }

    public void Print(int[][] printBlockArray) {
        String str = null;
        for (int z = SIZE - 1; z > -1; z--) {
            str = "";
            for (int x = 0; x < SIZE * HORIZ_BLOCKS; x++) {
                if (z < 3) {
                    str += "2";
                } else {
                    if (blockManager.isIdInGroup(printBlockArray[x][z], "None")) {
                        str += " ";
                    } else if (blockManager.isIdInGroup(printBlockArray[x][z], "Dirt")) {
                        str += "2";
                    } else if (blockManager.isIdInGroup(printBlockArray[x][z], "Grass")) {
                        str += "0";
                    } else if (blockManager.isIdInGroup(printBlockArray[x][z], "Stone")) {
                        str += "1";
                    } else if (blockManager.isIdInGroup(printBlockArray[x][z], "Town")) {
                        str += "3";
                    }
                }
            }
            //System.out.println(str);
        }
    }

    public int[] getWorldSize() {
        int[] size = new int[2];
        size[0] = SIZE * HORIZ_BLOCKS;
        size[1] = SIZE;
        return size;
    }

    public int[][] getBlockArray(){
        return blockArray;
    }
}
