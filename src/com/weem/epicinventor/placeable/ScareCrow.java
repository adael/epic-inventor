package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

public class ScareCrow extends Building {
    
    private static final long serialVersionUID = 10000L;
    
    public ScareCrow(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "ScareCrow";
        
        totalBuildTime = 60;
        totalHitPoints  = 500;
        powerRequired = 0;
        powerGenerated = 0;
        
        hitPoints = totalHitPoints;
        
        fearGenerated = 20;
        fearDistance = 300;
        fearDuration = 5;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 500;
        super.setTransient(rg);
    }
}
