package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

import java.awt.*;

public class WeaponRack extends Building {
    
    private static final long serialVersionUID = 10000L;
    private final static float BONUS_AMOUNT = 0.10f; //percentage increase per hit - stacks
    private final static int MAX_BONUS_DISTANCE = 512;
    
    public WeaponRack(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "WeaponRack";
        
        totalBuildTime = 60;
        totalHitPoints  = 500;
        powerRequired = 0;
        powerGenerated = 0;
        
        hitPoints = totalHitPoints;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 500;
        super.setTransient(rg);
    }
    
    @Override
    public float getAttackBonus(Point p) {
        float bonus = 0;
        
        if(getCenterPoint().distance(p) <= MAX_BONUS_DISTANCE && isActivated()) {
            bonus = BONUS_AMOUNT;
        }
        
        return bonus;
    }
}
