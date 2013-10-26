package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

public class LargeSafe extends PlayerContainer {
    
    private static final long serialVersionUID = 10000L;
    
    public LargeSafe(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs, 15);

        type = "LargeSafe";
        
        totalBuildTime = 120;
        totalHitPoints  = 1225;
        powerRequired = 0;
        powerGenerated = 0;
        
        hitPoints = totalHitPoints;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 1225;
        super.setTransient(rg);
    }
}
