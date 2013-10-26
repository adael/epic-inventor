package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

import java.awt.*;
import java.awt.image.*;

public class BladeTrap extends Building {
    
    private static final long serialVersionUID = 10000L;
    
    public BladeTrap(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "BladeTrap";
        
        totalBuildTime = 60;
        totalHitPoints  = 625;
        powerRequired = 20;
        powerGenerated = 0;
        
        hitPoints = totalHitPoints;
        
        touchDamage = 90;
    }
    
    @Override
    public void setTransient(Registry rg) {
        totalHitPoints  = 625;
        touchDamage = 90;
        super.setTransient(rg);
    }
    
    @Override
    protected void loopImage(String name) {
        if (registry.getImageLoader().numImages(name) > 1) {
            standardImage = name;

            BufferedImage im = registry.getImageLoader().getImage(name);

            numAnimationFrames = registry.getImageLoader().numImages(name);

            width = im.getWidth();
            height = im.getHeight();
            spriteRect = new Rectangle(mapX, mapY, width, height);

            currentAnimationFrame = 0;
            animationFrameDuration = (int) (1000 * 0.01);
            isAnimating = true;
        } else {
            setImage(name);
        }
    }
}
