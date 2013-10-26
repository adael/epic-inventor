package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

import java.awt.*;

public class MedicKit extends Building {
    
    private static final long serialVersionUID = 10000L;
    private final static int REGEN_AMOUNT = 5; //percentage increase per min - stacks
    private final static int MAX_REGEN_DISTANCE = 512;
    
    public MedicKit(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "MedicKit";
        
        totalBuildTime = 60;
        totalHitPoints  = 625;
        powerRequired = 0;
        powerGenerated = 0;
        
        hitPoints = totalHitPoints;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 625;
        super.setTransient(rg);
    }
    
    @Override
    public int getHPRegenerationBonus(Point p) {
        int bonus = 0;
        
        if(getCenterPoint().distance(p) <= MAX_REGEN_DISTANCE && isActivated()) {
            bonus = REGEN_AMOUNT;
        }
        
        return bonus;
    }
}
