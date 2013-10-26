package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

public class PottedFlower extends Building {
    
    private static final long serialVersionUID = 10000L;
    
    public PottedFlower(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "PottedFlower";
        
        totalBuildTime = 5;
        totalHitPoints  = 100;
        powerRequired = 0;
        powerGenerated = 0;
        
        hitPoints = totalHitPoints;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 100;
        super.setTransient(rg);
    }
}
