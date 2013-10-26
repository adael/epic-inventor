package com.weem.epicinventor.utility;

import java.util.Random;

public class Rand {

    private static Random random;

    private Rand() {
    }

    public static void init() {
        random = new Random();
    }

    public static int getRange(int min, int max) {
        if (random == null) {
            init();
        }

        if (max < min) {
            return 0;
        }

        int range = max - min + 1;
        
        if(range == 1) {
            return min;
        } else if (range <= 0) {
            return 0;
        }

        return random.nextInt(range) + min;
    }

    public static float getFloat() {
        if (random == null) {
            init();
        }

        return random.nextFloat();
    }
}