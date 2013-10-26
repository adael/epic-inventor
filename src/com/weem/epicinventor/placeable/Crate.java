package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

public class Crate extends PlayerContainer {
    
    private static final long serialVersionUID = 10000L;
    
    public Crate(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs, 4);

        type = "Crate";
        
        totalBuildTime = 30;
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
}
