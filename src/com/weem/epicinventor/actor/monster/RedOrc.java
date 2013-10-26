package com.weem.epicinventor.actor.monster;

import com.weem.epicinventor.*;
import com.weem.epicinventor.ai.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;

public class RedOrc extends Monster {

    private static final long serialVersionUID = 10000L;
    private float recastTotalTime;
    private float RECAST_TIME = 4.0f;
    private boolean canFire = true;
    private final static int MAX_RANGE = 400;

    public RedOrc(MonsterManager mm, Registry rg, String im, String st, int x, int y, int minDist, int maxDist) {
        super(mm, rg, im, st, x, y, minDist, maxDist, false);

        name = "RedOrc";
        displayName = "Red Orc";

        monsterManager = mm;
        
        difficultyFactor = 1.00f;

        adjustHPForLevel();

        topOffset = 16;
        baseOffset = 18;
        baseWidth = 24;
        startJumpSize = 20;
        jumpSize = 8;
        fallSize = 0;

        xMoveSize = 3;

        adjustTouchDamageForLevel();

        dropChances.addDropChance("FireGland", 4.0f, 1, 2);
        dropChances.addDropChance("Ruby", 15.0f, 1, 2);
        dropChances.addDropChance("Silver", 20.0f, 1, 3);
        dropChances.addDropChance("Tusk", 15.0f, 1, 2);
        dropChances.addDropChance("Skin", 15.0f, 1, 1);
 
        ai = new AI(registry, this);
        ai.clearGoals();
        ai.addGoal(AI.GoalType.ATTACK_PLAYER_RANGED_AGGRESSIVE, "", (Rand.getRange(0, 1) + Rand.getFloat()));
        ai.activate();
    }

    @Override
    public int getMaxShootRange() {
        return MAX_RANGE;
    }

    @Override
    public void shoot(Point targetPoint) {
        if (actionMode != ActionMode.ATTACKING) {
            actionMode = ActionMode.ATTACKING;
            stateChanged = true;
        }
        if (canFire) {
            registry.getProjectileManager().createProjectile(this,
                    "FireBall",
                    10,
                    new Point(
                    getMapX(),
                    getMapY()),
                    targetPoint,
                    false,
                    false,
                    true,
                    (int) ((float) touchDamage * 0.75f));
            canFire = false;
            recastTotalTime = 0;
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