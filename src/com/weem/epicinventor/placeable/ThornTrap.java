package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

public class ThornTrap extends Building {
    
    private static final long serialVersionUID = 10000L;
    
    public ThornTrap(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "ThornTrap";
        
        totalBuildTime = 30;
        totalHitPoints  = 500;
        powerRequired = 0;
        powerGenerated = 0;
        
        hitPoints = totalHitPoints;
        
        touchDamage = 60;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 500;
        touchDamage = 60;
        super.setTransient(rg);
    }
}
