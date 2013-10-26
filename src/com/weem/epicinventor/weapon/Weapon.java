package com.weem.epicinventor.weapon;

import com.weem.epicinventor.*;

import java.util.*;
import java.io.*;

public class Weapon {
    private static HashMap weaponTypes;
    private final static String CONFIG_FILE = "Weapons.dat";

    private Weapon() {
    }

    public static void init() {
        weaponTypes = new HashMap();

        loadItemTypes("Weapons.dat");
    }

    private static void loadItemTypes(String fn) {
        String line;
        String parts[];
        String damages[];

        try {
            InputStream in = Weapon.class.getResourceAsStream(GameController.CONFIG_DIR + fn);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String itemName = "";
            String type = "";
            int[] damage = new int[21];
            int speed = 0;
            int knockBackX = 0;
            int knockBackY = 0;
            int maxHits = 0;
            int range = 0;
            int animationFrames = 0;
            boolean comesBack = false;

            WeaponType wt;

            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }

                parts = line.split(" ");

                if (parts.length != 10) {
                    System.out.println("Error in " + fn);
                }

                itemName = parts[0];
                type = parts[1];
                
                damages = parts[2].split(":");
                damage = new int[21];
                damage[0] = 0;
                for(int i = 0; i < damages.length; i++) {
                    damage[i + 1] = Integer.parseInt(damages[i]);
                }
                speed = Integer.parseInt(parts[3]);
                knockBackX = Integer.parseInt(parts[4]);
                knockBackY = Integer.parseInt(parts[5]);
                maxHits = Integer.parseInt(parts[6]);
                range = Integer.parseInt(parts[7]);
                animationFrames = Integer.parseInt(parts[8]);
                if(Integer.parseInt(parts[9]) == 1) {
                    comesBack = true;
                } else {
                    comesBack = false;
                }

                wt = new WeaponType(itemName, type, damage, speed, knockBackX, knockBackY, maxHits, range, animationFrames, comesBack);
                weaponTypes.put(itemName, wt);
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static WeaponType getWeaponType(String name) {
        if (weaponTypes.containsKey(name)) {
            return (WeaponType) weaponTypes.get(name);
        }

        return null;
    }
}
