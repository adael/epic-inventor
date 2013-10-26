package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

public class SmallFarm extends Farm {
    
    private static final long serialVersionUID = 10000L;
    
    public SmallFarm(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs, 1);

        type = "SmallFarm";
        
        totalBuildTime = 30;
        totalHitPoints  = 500;
        powerRequired = 0;
        powerGenerated = 0;
        
        hitPoints = totalHitPoints;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 500;
        super.setTransient(rg);
    }
}
