package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

import java.awt.*;

public class Teleporter extends Building {
    
    private static final long serialVersionUID = 10000L;
    
    public Teleporter(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "Teleporter";
        
        totalBuildTime = 30;
        totalHitPoints  = 625;
        powerRequired = 30;
        powerGenerated = 0;
        
        hitPoints = totalHitPoints;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 625;
        powerRequired = 30;
        super.setTransient(rg);
    }

    @Override
    public boolean handleRightClick(Point clickPoint) {
        if (this.isActivated()) {
            Point mapPoint = new Point(
                    placeableManager.panelToMapX(clickPoint.x),
                    placeableManager.panelToMapY(clickPoint.y));

            if (currentState == State.Placed) {
                if (isInside(mapPoint)) {
                    if (registry.getPlayerManager().getCurrentPlayer().getCenterPoint().distance(getCenterPoint()) <= 40) {
                        placeableManager.teleportPlayer(this);
                        return true;
                    }
                }
            }
        }

        return false;
    }
}