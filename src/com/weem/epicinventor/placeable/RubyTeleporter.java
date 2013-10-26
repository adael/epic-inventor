package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

public class RubyTeleporter extends Teleporter {
    
    private static final long serialVersionUID = 10000L;
    
    public RubyTeleporter(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "RubyTeleporter";
    }
}