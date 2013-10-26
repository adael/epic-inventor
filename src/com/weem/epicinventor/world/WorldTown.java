package com.weem.epicinventor.world;

import com.weem.epicinventor.*;
import com.weem.epicinventor.utility.*;
import com.weem.epicinventor.world.block.*;

import java.util.Random;
import java.util.ArrayList;

public class WorldTown {
    private static Random randGen = null;
    private ArrayList townAreas = null;

    public WorldTown(Random rand){
        randGen = rand;
        townAreas = new ArrayList();
    }

    public int[][] addTownAreas(int[][] newBlockArray, int[] wg, BlockManager bm, int numTownAreas, int townSize, int minX, int maxX){
        Game.loadingText = "Building a Cabin";
        EIError.debugMsg("addTownAreas Start", EIError.ErrorLevel.Notice);
        //build start town
        for(int x = 0; x < townSize; x++){
            if (wg[x] < wg[townSize]) {
                for (int z = wg[x]; z <= wg[townSize]; z++) {
                    if (z == wg[townSize]) {
                        newBlockArray[x][z] = bm.getRandomIdByGroup("Town");
                    } else {
                        newBlockArray[x][z] = bm.getRandomIdByGroup("Dirt");
                    }
                }
            } else {
                for (int z = wg[x]; z >= wg[townSize]; z--) {
                    if (z == wg[townSize]) {
                        newBlockArray[x][z] = bm.getRandomIdByGroup("Town");
                    } else {
                        newBlockArray[x][z] = bm.getRandomIdByGroup("None");
                    }
                }
            }
        }
        numTownAreas--;

        int size = (maxX - minX) / (numTownAreas + 2);
        for(int i = 0; i < numTownAreas; i++){
            makeTown(newBlockArray, wg, bm, townSize, (i+1)*size, (i+2)*size);
        }
        EIError.debugMsg("addTownAreas End", EIError.ErrorLevel.Notice);
        return newBlockArray;
    }

    private int[][] makeTown(int[][] newBlockArray, int[] wg, BlockManager bm, int townSize, int minX, int maxX){
        EIError.debugMsg("makeTown Start", EIError.ErrorLevel.Notice);
        //find spots
        int[] townBounds = null;
        ArrayList pointList = new ArrayList();
        for(int startX = minX; startX < maxX-townSize; startX++){
            for(int endX = startX+townSize; endX < startX+2*townSize; endX++){
                if(wg[startX] == wg[endX]){
                    townBounds = new int[2];
                    townBounds[0] = startX;
                    townBounds[1] = endX;
                    pointList.add(townBounds);
                }
            }
        }
        int listSize = pointList.size();
        if(listSize > 0){
            int index = randGen.nextInt(listSize);
            int[] town = (int[])pointList.get(index);
            int levelSize = town[1]-town[0];
            int townXStart = town[0]+(levelSize-townSize)/2;
            int townXEnd = townXStart+townSize;
            townBounds = new int[2];
            townBounds[0] = townXStart;
            townBounds[1] = townXEnd;
            townAreas.add(townBounds);
            //level for town
            for(int x = town[0]; x < town[1]; x++){
                if (wg[x] < wg[town[0]]) {
                    for (int z = wg[x]; z <= wg[town[0]]; z++) {
                        if (z == wg[town[0]]) {
                            if(x >= townXStart && x <= townXEnd){
                                newBlockArray[x][z] = bm.getRandomIdByGroup("Town");
                            }else{
                                newBlockArray[x][z] = bm.getRandomIdByGroup("Dirt");
                            }
                        } else {
                            newBlockArray[x][z] = bm.getRandomIdByGroup("Dirt");
                        }
                    }
                } else {
                    for (int z = wg[x]; z >= wg[town[0]]; z--) {
                        if (z == wg[town[0]]) {
                            if(x >= townXStart && x <= townXEnd){
                                newBlockArray[x][z] = bm.getRandomIdByGroup("Town");
                            }else{
                                newBlockArray[x][z] = bm.getRandomIdByGroup("Dirt");
                            }
                        } else {
                            newBlockArray[x][z] = bm.getRandomIdByGroup("None");
                        }
                    }
                }
            }
        }else{
            EIError.debugMsg("Could not make town", EIError.ErrorLevel.Error);
        }
        EIError.debugMsg("makeTown End", EIError.ErrorLevel.Notice);
        return newBlockArray;
    }

    public ArrayList getTownAreas(){
        return townAreas;
    }
}
