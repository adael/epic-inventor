package com.weem.epicinventor.placeable;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.monster.*;
import com.weem.epicinventor.utility.*;

public class Pasture extends Building {

    private static final long serialVersionUID = 10000L;
    transient private final static int breedRate = 0; //in seconds
    transient private long breedTimerEnd;
    protected int maxAnimals = 12;

    public Pasture(PlaceableManager pm, Registry rg, String sm, String am, int x, int y, Placeable.State cs) {
        super(pm, rg, sm, am, x, y, cs);

        type = "Pasture";

        totalBuildTime = 120;
        totalHitPoints = 500;
        powerRequired = 0;
        powerGenerated = 0;

        hitPoints = totalHitPoints;
    }

    private void resetBreedTimer() {
        breedTimerEnd = registry.currentTime + Rand.getRange(5 * 60 * 1000, 10 * 60 * 1000);
    }

    @Override
    public void update() {
        super.update();

        if (breedTimerEnd == 0) {
            resetBreedTimer();
        }

        if (breedTimerEnd <= System.currentTimeMillis() && this.isActivated()) {
            MonsterManager mm = registry.getMonsterManager();
            if (mm != null) {
                int animals = mm.getCountByTypeWithinXRange("Pig", mapX, mapX + width);
                int animalsToBreed = (int) Math.floor(animals / 2);
                int animalsInPasture = mm.getAnimalCount(this.getPerimeter());
                if(animalsInPasture + animalsToBreed > maxAnimals)
                {
                    animalsToBreed = maxAnimals - animalsInPasture;
                }
                if (animalsToBreed > 0) {
                    for (int i = 0; i < animalsToBreed; i++) {
                        int x = Rand.getRange(mapX, mapX + width - 32);
                        Monster m = mm.spawn("Pig", "Roaming", x, mapY);
                        m.setPosition(x, mapY);
                    }
                    registry.getIndicatorManager().createImageIndicator(mapX + (width / 2), mapY + height + 32, "Pig");
                    registry.showMessage("Success", type + " has bred new pigs");
                }
            }
            resetBreedTimer();
        }
    }

    @Override
    public void setTransient(Registry rg) {
        totalHitPoints = 500;
        maxAnimals = 12;
        super.setTransient(rg);
    }
}
