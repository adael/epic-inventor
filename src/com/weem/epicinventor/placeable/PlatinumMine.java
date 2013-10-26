package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

public class PlatinumMine extends Building {
    
    private static final long serialVersionUID = 10000L;
    transient private final static int generationRate = 60 * 6; //in seconds
    private long generationTimerEnd;
    
    public PlatinumMine(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "PlatinumMine";
        
        totalBuildTime = 60;
        totalHitPoints  = 625;
        powerRequired = 10;
        powerGenerated = 0;
        
        hitPoints = totalHitPoints;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 625;
        super.setTransient(rg);
    }
    
    private void resetGenerationTimer() {
        generationTimerEnd = System.currentTimeMillis() + (generationRate * 1000);
    }

    @Override
    public void updateLong() {
        super.updateLong();
        
        if(generationTimerEnd == 0) {
            resetGenerationTimer();
        }
        
        if(generationTimerEnd <= System.currentTimeMillis() && this.isActivated()) {
            placeableManager.playerAddItem("Platinum", 1);
            registry.getIndicatorManager().createImageIndicator(mapX + (width / 2), mapY + height + 32, "Platinum");
            registry.showMessage("Success", type + " has generated Platinum");
            resetGenerationTimer();
        }
    }
}
