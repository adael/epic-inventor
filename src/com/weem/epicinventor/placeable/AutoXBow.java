package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.monster.*;

import java.awt.*;

public class AutoXBow extends Building {
    
    private static final long serialVersionUID = 10000L;
    private float recastTotalTime;
    private float RECAST_TIME = 1.0f;
    private boolean canFire = true;
    private final static int MAX_RANGE = 300;
    
    public AutoXBow(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "AutoXBow";
        
        totalBuildTime = 90;
        totalHitPoints  = 560;
        powerRequired = 10;
        powerGenerated = 0;
        
        hitPoints = totalHitPoints;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 560;
        super.setTransient(rg);
    }

    @Override
    public void update() {
        super.update();
        
        if(isActivated()) {
            if(canFire)
            {
                //target the closest mob
                Monster m = registry.getMonsterManager().getClosestWithinMax(getCenterPoint(), MAX_RANGE);
                if(m != null) {
                    if(m.getMapX() > mapX) {
                        facingRight = true;
                    } else {
                        facingRight = false;
                    }
                    
                    registry.getProjectileManager().createProjectile(registry.getPlayerManager().getCurrentPlayer(),
                            "Arrow",
                            20,
                            new Point(
                            getMapX(),
                            getMapY()),
                            new Point(
                            m.getCenterPoint().x,
                            m.getCenterPoint().y),
                            true,
                            true,
                            false,
                            70);
                    canFire = false;
                    recastTotalTime = 0;
                } else {
                    facingRight = true;
                }
            } else {
                long p = registry.getImageLoader().getPeriod();
                recastTotalTime = (recastTotalTime
                        + registry.getImageLoader().getPeriod())
                        % (long) (1000 * RECAST_TIME * 2);

                if ((recastTotalTime / (RECAST_TIME * 1000)) > 1) {
                    canFire = true;
                    recastTotalTime = 0;
                }
            }
        }
    }
}