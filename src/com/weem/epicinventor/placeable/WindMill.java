package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

public class WindMill extends Building {
    
    private static final long serialVersionUID = 10000L;
    
    public WindMill(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "WindMill";
        
        totalBuildTime = 120;
        totalHitPoints  = 500;
        powerRequired = 0;
        powerGenerated = 10;
        
        hitPoints = totalHitPoints;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 500;
        super.setTransient(rg);
    }
}
