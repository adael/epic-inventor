package com.weem.epicinventor.armor;

import com.weem.epicinventor.*;

import java.util.*;
import java.io.*;

public class Armor {

    private static HashMap armorTypes;
    private final static String CONFIG_FILE = "Armor.dat";

    private Armor() {
    }

    public static void init() {
        armorTypes = new HashMap();

        loadItemTypes("Armor.dat");
    }

    private static void loadItemTypes(String fn) {
        String line;
        String parts[];
        String armorBonuses[];

        try {
            InputStream in = Armor.class.getResourceAsStream(GameController.CONFIG_DIR + fn);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String set = "";
            String type = "";
            int[] armorBonus = new int[21];

            ArmorType at;

            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }

                parts = line.split(" ");

                if (parts.length != 3) {
                    System.out.println("Error in " + fn);
                }

                set = parts[0];
                type = parts[1];
                
                armorBonuses = parts[2].split(":");
                armorBonus = new int[21];
                armorBonus[0] = 0;
                for(int i = 0; i < armorBonuses.length; i++) {
                    armorBonus[i + 1] = Integer.parseInt(armorBonuses[i]);
                }

                at = new ArmorType(set, type, armorBonus);
                armorTypes.put(set + type, at);
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static ArmorType getArmorType(String name) {
        if (armorTypes.containsKey(name)) {
            return (ArmorType) armorTypes.get(name);
        }

        return null;
    }
}