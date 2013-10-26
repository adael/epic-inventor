package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

public class LionFlyStatue extends Building {
    
    private static final long serialVersionUID = 10000L;
    
    public LionFlyStatue(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "LionFlyStatue";
        
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
