package com.weem.epicinventor.actor.monster;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.ai.*;
import com.weem.epicinventor.placeable.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;
import java.awt.geom.Arc2D;

public class Pig extends Monster {

    private static final long serialVersionUID = 10000L;

    public Pig(MonsterManager mm, Registry rg, String im, String st, int x, int y, int minDist, int maxDist) {
        super(mm, rg, im, st, x, y, minDist, maxDist, true);

        name = "Pig";
        displayName = "Pig";

        monsterManager = mm;
        
        difficultyFactor = 0.10f;

        adjustHPForLevel();

        topOffset = 13;
        baseOffset = 6;
        baseWidth = 21;
        startJumpSize = 20;
        jumpSize = 8;
        fallSize = 0;

        xMoveSize = 1;

        touchDamage = 0;

        dropChances.addDropChance("Bacon", 50.0f, 1, 1);

        ai = new AI(registry, this);
        ai.clearGoals();
        ai.addGoal(AI.GoalType.WANDER, "", (Rand.getRange(0, 1) + Rand.getFloat()));
        ai.activate();
    }

    @Override
    public void fear(Point p, long d) {
        super.fear(p, d);
        
        xMoveSize = 6;
    }

    @Override
    public void unfear() {
        super.unfear();
        
        xMoveSize = 1;
    }

    @Override
    public int applyDamage(int damage, Actor a) {
        int ret = super.applyDamage(damage, a);

        if (a != null && damage > 0) {
            this.fear(a.getCenterPoint(), 5);
        }

        return ret;
    }

    @Override
    public int applyDamage(int damage, Actor a, boolean fromPlaceable) {
        int ret = super.applyDamage(damage, a, fromPlaceable);

        if (a != null && damage > 0) {
            this.fear(a.getCenterPoint(), 5);
        }

        return ret;
    }
    
    @Override


    public int attackDamageAndKnockBack(Actor source, Arc2D.Double arc, Point mapPoint, int damage, int kbX, int kbY, String weaponType) {
        int damageTaken = 0;
        if (registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.CLIENT) {
            //System.out.println(spriteRect.intersects(r) + ":" + hitPoints);
            if (arc.intersects(spriteRect) && hitPoints > 0) {
                if(weaponType != null) {
                    if(weaponType.equals("Net")) {
                        this.isDead = true;
                    }
                }
                int range = 3 * Math.abs(kbX) / 4;
                if (range < 1) {
                    range = 1;
                }

                int randX = Rand.getRange(1, range);
                int baseX = Math.abs(kbX) / 4;

                if (kbX < 0) {
                    kbX = baseX + randX;
                    kbX = -1 * kbX;
                } else {
                    kbX = baseX + randX;
                }

                damageTaken = applyDamage(damage, source);

                if (!disregardKnockBack) {
                    applyKnockBack(kbX, kbY);
                }
            }
            //System.out.println(spriteRect.x+","+spriteRect.y+" "+spriteRect.width+","+spriteRect.height+" "+r.x+","+r.y+" "+r.width+","+r.height);
        }
        return damageTaken;
    }

    @Override
    public int getXPByPlayer(Actor a) {
        return 0;
    }

    @Override
    protected void determineLevel() {
        level = 1;
    }

    @Override
    public void update() {
        super.update();

        PlaceableManager pm = registry.getPlaceableManager();
        if(pm != null) {
            Placeable p = pm.getPasture(this.getPerimeter());
            if(p != null) {
                if(mapX < p.getMapX()) {
                    mapX = p.getMapX();
                }
                if(mapX > p.getMapX() + p.getWidth() - this.width) {
                    mapX = p.getMapX() + p.getWidth() - this.width;
                }
            }
        }
    }
}