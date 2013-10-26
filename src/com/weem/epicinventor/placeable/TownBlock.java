package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;

public class TownBlock extends Placeable {
    
    private static final long serialVersionUID = 10000L;

    public TownBlock(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "TownBlock";
        
        totalBuildTime = 1;
    }
    
    @Override
    public boolean checkCanPlace() {
        boolean canPlace = super.checkCanPlace();
        String group = registry.getBlockManager().getBlockGroup(mapX, mapY);
        if(group.equals("Town")) {
            canPlace = false;
        }
        return canPlace;
    }
    
    @Override
    protected void setState(Placeable.State state) {
        super.setState(state);
        if(state == Placeable.State.Placed) {
            setIsDirty(true);
            registry.getBlockManager().setBlockByGroup(mapX, mapY, "Town");
        }
    }
}
