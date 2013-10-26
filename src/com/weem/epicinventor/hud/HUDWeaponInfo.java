package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.weapon.*;

import java.awt.*;
import java.text.*;
import java.util.Locale.*;

public class HUDWeaponInfo extends HUD {

    private final static int NAME_X = 10;
    private final static int NAME_Y = 5;
    private final static int NAME_WIDTH = 210;
    private final static int NAME_HEIGHT = 32;
    private final static int BASE_X = 36;
    private final static int BASE_Y = 78;
    private final static int BASE_WIDTH = 35;
    private final static int BASE_HEIGHT = 21;
    private final static int WEAPON_X = 100;
    private final static int WEAPON_Y = 78;
    private final static int WEAPON_WIDTH = 35;
    private final static int WEAPON_HEIGHT = 21;
    private final static int TOTAL_X = 165;
    private final static int TOTAL_Y = 78;
    private final static int TOTAL_WIDTH = 35;
    private final static int TOTAL_HEIGHT = 21;
    private final static int SPEED_X = 45;
    private final static int SPEED_Y = 135;
    private final static int SPEED_WIDTH = 45;
    private final static int SPEED_HEIGHT = 35;
    private final static int DPS_X = 140;
    private final static int DPS_Y = 135;
    private final static int DPS_WIDTH = 45;
    private final static int DPS_HEIGHT = 35;
    private final static int OFFSET_1 = 5;
    private final static int OFFSET_2 = 7;

    public HUDWeaponInfo(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/WeaponInfo/BG");

        HUDArea hudArea = null;

        //name
        hudArea = addArea(NAME_X, NAME_Y, NAME_WIDTH, NAME_HEIGHT, "name");
        hudArea.setFont("SansSerif", Font.BOLD, 24);

        //base
        hudArea = addArea(BASE_X, BASE_Y, BASE_WIDTH, BASE_HEIGHT, "base");
        hudArea.setFont("SansSerif", Font.BOLD, 20);

        //weapon
        hudArea = addArea(WEAPON_X, WEAPON_Y, WEAPON_WIDTH, WEAPON_HEIGHT, "weapon");
        hudArea.setFont("SansSerif", Font.BOLD, 20);

        //total
        hudArea = addArea(TOTAL_X, TOTAL_Y, TOTAL_WIDTH, TOTAL_HEIGHT, "total");
        hudArea.setFont("SansSerif", Font.BOLD, 20);

        //speed
        hudArea = addArea(SPEED_X, SPEED_Y, SPEED_WIDTH, SPEED_HEIGHT, "speed");
        hudArea.setFont("SansSerif", Font.BOLD, 24);

        //dps
        hudArea = addArea(DPS_X, DPS_Y, DPS_WIDTH, DPS_HEIGHT, "dps");
        hudArea.setFont("SansSerif", Font.BOLD, 24);

        shouldRender = false;
    }

    @Override
    public void update() {
        if (shouldRender) {
            Player p = registry.getPlayerManager().getCurrentPlayer();
            if (p != null) {
                HUDArea hudArea;
                WeaponType newWeaponType = Weapon.getWeaponType(registry.getWeaponType());
                if (newWeaponType != null) {
                    int[] damages = newWeaponType.getDamage();
                    int damage = damages[registry.getWeaponLevel()];
                    double speed = (float) newWeaponType.getSpeed() / 1000d;
                    String speedString = Double.toString(speed);
                    if(speedString.contains(",")) {
                        speed = Double.parseDouble(speedString.replace(",", "."));
                    }
                    int attackBonus = p.getAttackBonus();
                    if(registry.getWeaponType().equals("AutoHandCannon")) {
                        attackBonus = attackBonus / 4;
                    }
                    int total = attackBonus + damage;
                    int playerLevel = p.getLevel();
                    int weaponLevel = registry.getWeaponLevel();

                    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                    dfs.setDecimalSeparator('.');
                    dfs.setGroupingSeparator(',');
                    DecimalFormat twoDForm = new DecimalFormat("0.00", dfs);

                    for (int i = 0; i < hudAreas.size(); i++) {
                        hudArea = hudAreas.get(i);
                        if (hudArea.getType().equals("name")) {
                            String itemName = registry.getWeaponType();
                            itemName = itemName.replace("Blade", " Blade");
                            itemName = itemName.replace("Hammer", " Hammer");
                            itemName = itemName.replace("Spear", " Spear");
                            itemName = itemName.replace("Sword", " Sword");
                            itemName = itemName.replace("CrossBow", " Cross Bow");
                            itemName = itemName.replace("AutoHand", "Auto");
                            itemName = itemName.replace("Cannon", " Cannon");
                            itemName = itemName.replace("SlingShot", " Sling Shot");

                            hudArea.setText(itemName + " " + registry.getWeaponLevel());
                            if (weaponLevel == 20) {
                                hudArea.setTextColor(Color.MAGENTA);
                            } else if (weaponLevel <= playerLevel) {
                                hudArea.setTextColor(Color.WHITE);
                            } else if (weaponLevel == playerLevel + 1) {
                                hudArea.setTextColor(Color.GREEN);
                            } else if (weaponLevel == playerLevel + 2) {
                                hudArea.setTextColor(Color.BLUE);
                            } else {
                                hudArea.setTextColor(Color.MAGENTA);
                            }
                        } else if (hudArea.getType().equals("base")) {
                            hudArea.setText(Integer.toString(attackBonus));
                            if (Integer.toString(attackBonus).length() == 1) {
                                hudArea.setXY(BASE_X + (OFFSET_1 * 2), BASE_Y);
                            } else if (Integer.toString(attackBonus).length() == 2) {
                                hudArea.setXY(BASE_X + OFFSET_1, BASE_Y);
                            } else if (Integer.toString(attackBonus).length() == 3) {
                                hudArea.setXY(BASE_X, BASE_Y);
                            }
                        } else if (hudArea.getType().equals("weapon")) {
                            hudArea.setText(Integer.toString(damage));
                            if (Integer.toString(damage).length() == 1) {
                                hudArea.setXY(WEAPON_X + (OFFSET_1 * 2), WEAPON_Y);
                            } else if (Integer.toString(damage).length() == 2) {
                                hudArea.setXY(WEAPON_X + OFFSET_1, WEAPON_Y);
                            } else if (Integer.toString(damage).length() == 3) {
                                hudArea.setXY(WEAPON_X, WEAPON_Y);
                            }
                        } else if (hudArea.getType().equals("total")) {
                            hudArea.setText(Integer.toString(total));
                            if (Integer.toString(total).length() == 1) {
                                hudArea.setXY(TOTAL_X + (OFFSET_1 * 2), TOTAL_Y);
                            } else if (Integer.toString(total).length() == 2) {
                                hudArea.setXY(TOTAL_X + OFFSET_1, TOTAL_Y);
                            } else if (Integer.toString(total).length() == 3) {
                                hudArea.setXY(TOTAL_X, TOTAL_Y);
                            }
                        } else if (hudArea.getType().equals("speed")) {
                            try {
                                hudArea.setText(Float.toString(Float.valueOf(twoDForm.format(speed))));
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        } else if (hudArea.getType().equals("dps")) {
                            int dps = (int) ((float) total / speed);
                            hudArea.setText(Integer.toString(dps));
                            if (Integer.toString(dps).length() == 1) {
                                hudArea.setXY(DPS_X + (OFFSET_2 * 3), DPS_Y);
                            } else if (Integer.toString(dps).length() == 2) {
                                hudArea.setXY(DPS_X + (OFFSET_2 * 2), DPS_Y);
                            } else if (Integer.toString(dps).length() == 3) {
                                hudArea.setXY(DPS_X + (OFFSET_2 * 1), DPS_Y);
                            } else if (Integer.toString(dps).length() == 4) {
                                hudArea.setXY(DPS_X, DPS_Y);
                            }
                        }
                    }
                }
            }
        }

        super.update();
    }
}