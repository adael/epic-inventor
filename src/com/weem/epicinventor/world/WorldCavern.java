package com.weem.epicinventor.world;

import com.weem.epicinventor.*;
import com.weem.epicinventor.world.block.*;

import java.util.Random;
import java.util.ArrayList;
import java.awt.*;
import com.weem.epicinventor.utility.*;

public class WorldCavern {

    World world;
    private static int MAX_CAVES = 3;
    private static int MIN_CAVES = 2;
    private static int numberCaves = 0;
    private static int CAVERN_FACTOR;
    private static int CAVE_RADIUS;
    private static float CAVE_TERM;
    private static int[] gound;
    private BlockManager blockManager;
    private WorldTown worldTown;
    private static Random randGen = new Random();

    public WorldCavern(World w, WorldTown wt, int cavernFactor, int caveRadius, float caveTerm, int[] worldGround) {
        blockManager = new BlockManager();
        worldTown = wt;

        world = w;
        gound = worldGround;
        CAVERN_FACTOR = cavernFactor;
        CAVE_RADIUS = caveRadius;
        CAVE_TERM = caveTerm;
    }

    public int[][] carveCavern(int[][] currentBlockArray, int size, int xMin, int xMax, int zMin, int zMax, int paintRadius, float splitPercentage) {
        int x = randGen.nextInt(xMax - xMin) + xMin;
        int z = randGen.nextInt(zMax - zMin) + zMin;
        numberCaves = 2;
        int cave_x1 = 0;
        int cave_z1 = 0;
        int cave_x2 = 0;
        int cave_z2 = 0;
        int currentPaintRadius = paintRadius;
        int angle = 0;
        int distance = 0;
        int[] worldSize = world.getWorldSize();

        cave_x1 = (int) (x + (currentPaintRadius - (CAVE_RADIUS + 1)) * Math.sin(30 * Math.PI / 180));
        cave_z1 = (int) (z - (currentPaintRadius - (CAVE_RADIUS + 1)) * Math.cos(30 * Math.PI / 180));

        for (int i = 0; i < CAVERN_FACTOR; i++) {
            world.PaintAtPoint(currentBlockArray, "CaveBG", currentPaintRadius, x, z, (x / size) * size, size);
            if (i == CAVERN_FACTOR - 1) {
                cave_x2 = (int) (x - (currentPaintRadius - (CAVE_RADIUS + 1)) * Math.sin(30 * Math.PI / 180));
                cave_z2 = (int) (z - (currentPaintRadius - (CAVE_RADIUS + 1)) * Math.cos(30 * Math.PI / 180));
            }
            x += randGen.nextInt(Math.max(currentPaintRadius / 2, 1)) - currentPaintRadius / 2;
            z += randGen.nextInt(Math.max(currentPaintRadius / 4, 1)) - currentPaintRadius / 8;
            currentPaintRadius = randGen.nextInt(paintRadius / 2) + paintRadius / 2;
        }

        angle = randGen.nextInt(25 - 15) + 15;
        distance = randGen.nextInt(40 - 30) + 30;
        int cave_x1_end = (int) (cave_x1 + distance * Math.cos(angle * Math.PI / 180));
        int cave_z1_end = (int) (cave_z1 + distance * Math.sin(angle * Math.PI / 180));
        currentBlockArray = world.PaintLine(currentBlockArray, "CaveBG", CAVE_RADIUS, cave_x1, cave_z1, cave_x1_end, cave_z1_end, 0, 0, worldSize[0], worldSize[1]);
        angle = randGen.nextInt(25 - 15) + 15;
        distance = randGen.nextInt(30 - 20) + 20;
        int cave_x2_end = (int) (cave_x2 - distance * Math.cos(angle * Math.PI / 180));
        int cave_z2_end = (int) (cave_z2 + distance * Math.sin(angle * Math.PI / 180));
        currentBlockArray = world.PaintLine(currentBlockArray, "CaveBG", CAVE_RADIUS, cave_x2, cave_z2, cave_x2_end, cave_z2_end, 0, 0, worldSize[0], worldSize[1]);

        currentBlockArray = carveCave(currentBlockArray, cave_x1_end, cave_z1_end, 0, 0, worldSize[0], worldSize[1], 0.02f, World.direction.Right);
        currentBlockArray = carveCave(currentBlockArray, cave_x2_end, cave_z2_end, 0, 0, worldSize[0], worldSize[1], 0.02f, World.direction.Left);

        // Add cave.
        Game.loadingText = "Digging Caves";
        EIError.debugMsg("Add cave noise", EIError.ErrorLevel.Notice);
        float[][] caveNoise;
        for (int b = 0; b < world.HORIZ_BLOCKS; b++) {
            caveNoise = world.GeneratePerlinNoise(32);
            caveNoise = world.InterpolateData(caveNoise, 32, size);
            for (int i = 0; i < 60; i++) {
                world.PaintWithRandomWalk(currentBlockArray, caveNoise, size, Rand.getRange(5, size / 64), "CaveBG", false, b * size, (b + 1) * size - 1, world.GROUND_LEVEL / 8, world.GROUND_LEVEL, size / 2);
            }
        }

        for(int i = 0; i < 20; i++) {
            drawCrag(currentBlockArray, "CaveBG", Rand.getRange(5, 10), Rand.getRange(0, worldSize[0]), Rand.getRange(size / 8, worldSize[1]), 0, worldSize[0], 0, 3 * worldSize[1] / 4);
        }

        return currentBlockArray;
    }

    public int[][] carveCave(int[][] currentBlockArray, int startX, int startZ, int minX, int minZ, int maxX, int maxZ, float splitPercentage, World.direction lastDirection) {
        boolean split = false;
        int angleMin = 0;
        int angleMax = 25;
        int distanceMin = 5;
        int distanceMax = 15;
        int angle = 0;
        int distance = 0;
        World.direction direction = lastDirection;
        World.direction splitDirection = lastDirection;
        int endX = 0;
        int endZ = 0;
        float chance = 0.0f;
        int[] groundLoc;
        int[] newEnd;

        do {
            angle = randGen.nextInt(angleMax - angleMin) + angleMin;
            distance = randGen.nextInt(distanceMax - distanceMin) + distanceMin;

            direction = getDirection(lastDirection);
            if (direction == World.direction.Right) {
                endX = (int) (startX + distance * Math.cos(angle * Math.PI / 180));
            } else {
                endX = (int) (startX - distance * Math.cos(angle * Math.PI / 180));
            }
            //near x bounds change direction
            if (endX + CAVE_RADIUS > maxX) {
                endX = (int) (startX - distance * Math.cos(angle * Math.PI / 180));
                direction = World.direction.Left;
                lastDirection = World.direction.Left;
            }
            if (endX - CAVE_RADIUS < minX) {
                endX = (int) (startX + distance * Math.cos(angle * Math.PI / 180));
                direction = World.direction.Right;
                lastDirection = World.direction.Right;
            }
            endZ = (int) (startZ + distance * Math.sin(angle * Math.PI / 180));

            groundLoc = willBreakGround(startX, startZ, endX, endZ);
            //near ground
            if (groundLoc[3] == 1) {
                newEnd = avoidTown(worldTown, startX, startZ, endX, endZ, maxX);
                if (newEnd[0] > 0 && newEnd[1] > 0) {
                    endX = newEnd[0];
                    endZ = newEnd[1];
                    if (direction == World.direction.Left) {
                        direction = World.direction.Right;
                        lastDirection = World.direction.Right;
                    } else {
                        direction = World.direction.Left;
                        lastDirection = World.direction.Left;
                    }
                    groundLoc = willBreakGround(startX, startZ, endX, endZ);
                    if (groundLoc[0] > 0 && groundLoc[1] > 0) {
                        endX = groundLoc[0];
                        endZ = groundLoc[1];
                    }
                }
                //stop at ground
            } else if (groundLoc[0] > 0 && groundLoc[1] > 0) {
                endX = groundLoc[0];
                endZ = groundLoc[1];
            }

            currentBlockArray = world.PaintLine(currentBlockArray, "CaveBG", CAVE_RADIUS + Rand.getRange(0, 2), startX, startZ, endX, endZ, minX, minZ, maxX, maxZ);

            chance = randGen.nextFloat();
            if (chance <= (splitPercentage * CAVE_TERM) && numberCaves > MIN_CAVES) {
                break;
            }
            if (chance <= splitPercentage && numberCaves < MAX_CAVES) {
                if (direction == World.direction.Left) {
                    splitDirection = World.direction.Right;
                } else {
                    splitDirection = World.direction.Left;
                }
                currentBlockArray = carveCave(currentBlockArray, endX, endZ, minX, minZ, maxX, maxZ, splitPercentage, direction);
                currentBlockArray = carveCave(currentBlockArray, endX, endZ, minX, minZ, maxX, maxZ, splitPercentage, splitDirection);
                numberCaves++;
                split = true;
            }
            startX = endX;
            startZ = endZ;
        } while (split == false && groundLoc[0] == 0 && groundLoc[1] == 0 && groundLoc[2] == 0);

        return currentBlockArray;
    }

    private void drawCrag(int[][] currentBlockArray, String group, int paintRadius, int x, int z, int minX, int maxX, int minZ, int maxZ) {
        if(x > minX && x < maxX) {
            if(gound[x] <= z + paintRadius + 10) {
                z -= paintRadius * 2;
            }
            if(gound[x] > z + paintRadius + 10) {
                world.PaintAtPoint(currentBlockArray, group, paintRadius, x, z, minX, maxX, minZ, maxZ);
                int spines = Rand.getRange(4, 10);
                int direction = 0;
                for(int i = 0; i < spines; i++) {
                    direction = Rand.getRange(360 * i / spines, 360 * i / spines + (360 / spines));
                    drawSpine(currentBlockArray, group, paintRadius / 2, direction, x, z, minX, maxX, minZ, maxZ);
                }
            }
        }
    }
    
    private void drawSpine(int[][] currentBlockArray, String group, int paintRadius, int currentDirection, int x, int z, int minX, int maxX, int minZ, int maxZ) {
        if(x > minX && x < maxX && z > minZ && z < maxZ) {
            if(paintRadius != 0 && gound[x] > z + paintRadius + 10) {
                int directionOffset = Rand.getRange(-20, 20);
                int paintRadiusChange = -1 * Rand.getRange(0, 1);
                int newX = x + (int)(paintRadius * Math.cos(Math.toRadians(currentDirection + directionOffset)));
                int newZ = z + (int)(paintRadius * Math.sin(Math.toRadians(currentDirection + directionOffset)));
                world.PaintAtPoint(currentBlockArray, group, paintRadius + paintRadiusChange, newX, newZ, minX, maxX, minZ, maxZ);
                if(paintRadius > 0) {
                    drawSpine(currentBlockArray, group, paintRadius + paintRadiusChange, currentDirection + directionOffset, newX, newZ, minX, maxX, minZ, maxZ);
                }
            }
        }
        return;
    }

    private int[] avoidTown(WorldTown wt, int startX, int startZ, int endX, int endZ, int maxX) {
        int[] newEnd = new int[2];
        newEnd[0] = 0;
        newEnd[1] = 0;
        ArrayList townAreas = wt.getTownAreas();
        int size = townAreas.size();
        World.direction direction = World.direction.Left;
        if (size > 0) {
            int[] townBounds = (int[]) townAreas.get(0);
            int townSize = townBounds[1] - townBounds[0];
            if (startX > endX) {
                if (endX <= 2 * townSize) {
                    direction = World.direction.Right;
                } else {
                    for (int i = 0; i < size; i++) {
                        townBounds = (int[]) townAreas.get(i);
                        if (townBounds[1] >= endX - 30 && townBounds[1] <= endX) {
                            direction = World.direction.Right;
                            break;
                        }
                    }
                }
            } else {
                direction = World.direction.Right;
                if (endX >= maxX - 2 * townSize) {
                    direction = World.direction.Left;
                } else {
                    for (int i = 0; i < size; i++) {
                        townBounds = (int[]) townAreas.get(i);
                        if (townBounds[0] <= endX + 30 && townBounds[0] >= endX) {
                            direction = World.direction.Left;
                            break;
                        }
                    }
                }
            }
            if ((startX > endX && direction == World.direction.Right) || (startX < endX && direction == World.direction.Left)) {
                newEnd[0] = 2 * startX - endX;
                newEnd[1] = startZ;
                //System.out.println(newEnd[0]+","+newEnd[1]+" "+endX+","+endZ);
            }
        }
        return newEnd;
    }

    private World.direction getDirection(World.direction lastDirection) {
        World.direction direction = lastDirection;
        float chance = randGen.nextFloat();
        if (chance < 0.02f) {
            if (lastDirection == World.direction.Left) {
                direction = World.direction.Right;
            } else {
                direction = World.direction.Left;
            }
        }
        return direction;
    }

    public int[] willBreakGround(int startX, int startZ, int endX, int endZ) {
        int[] groundLoc = new int[4];
        groundLoc[0] = 0;
        groundLoc[1] = 0;
        groundLoc[2] = 0;
        groundLoc[3] = 0;
        int dx = endX - startX;
        int dz = endZ - startZ;
        int count = (int) Math.sqrt(dx * dx + dz * dz);
        int i;
        double angle = 0.0;
        for (i = 0; i < count; i++) {
            if (gound[startX + (dx * i / count)] - (40 + CAVE_RADIUS) <= startZ + (dz * i / count)) {
                groundLoc[3] = 1;
            }
            if (gound[startX + (dx * i / count)] + CAVE_RADIUS * 2 / 3 <= startZ + (dz * i / count)) {
                groundLoc[2] = 1;
                break;
            }
        }
        if (groundLoc[2] == 1) {
            if (i < 2) {
                i = 2;
            }
            angle = Math.atan((gound[startX + (dx * i / count)] + CAVE_RADIUS * 2 - startZ) / (dx * i / count)) * 180 / Math.PI;
            if (angle < 46.0) {
                endZ = gound[startX + (dx * i / count)] + CAVE_RADIUS * 2;
                endX = dx * i / count + startX;
                groundLoc[2] = 0;
            } else {
                endZ -= dz / 3 + startZ;
            }
            dx = endX - startX;
            dz = endZ - startZ;
        }
        for (i = 0; i < count; i++) {
            if (gound[startX + (dx * i / count)] + CAVE_RADIUS <= startZ + (dz * i / count)) {
                groundLoc[0] = startX + (dx * i / count);
                groundLoc[1] = startZ + (dz * i / count);
                break;
            }
        }
        return groundLoc;
    }
}
