package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.monster.*;

import java.awt.*;

public class Cabin extends Building {
    
    private static final long serialVersionUID = 10000L;
    
    public Cabin(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "Cabin";
        
        powerRequired = 0;
        powerGenerated = 0;
    }

    @Override
    public int attackDamage(Monster source, Rectangle attackRect, int damage) {
        return 0;
    }
}
