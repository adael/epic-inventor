package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

public class MediumSafe extends PlayerContainer {
    
    private static final long serialVersionUID = 10000L;
    
    public MediumSafe(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs, 10);

        type = "MediumSafe";
        
        totalBuildTime = 60;
        totalHitPoints  = 825;
        powerRequired = 0;
        powerGenerated = 0;
        
        hitPoints = totalHitPoints;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 825;
        super.setTransient(rg);
    }
}
