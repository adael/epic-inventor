package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

public class Box extends PlayerContainer {
    
    private static final long serialVersionUID = 10000L;
    
    public Box(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs, 2);

        type = "Box";
        
        totalBuildTime = 30;
        totalHitPoints  = 560;
        powerRequired = 0;
        powerGenerated = 0;
        
        hitPoints = totalHitPoints;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 560;
        super.setTransient(rg);
    }
}
