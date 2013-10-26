package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

public class SteamEngine extends Building {
    
    private static final long serialVersionUID = 10000L;
    
    public SteamEngine(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "SteamEngine";
        
        totalBuildTime = 120;
        totalHitPoints  = 625;
        powerRequired = 0;
        powerGenerated = 50;
        
        hitPoints = totalHitPoints;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 625;
        super.setTransient(rg);
    }
}
