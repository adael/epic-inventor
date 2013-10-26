package com.weem.epicinventor.actor.monster;

import com.weem.epicinventor.*;
import com.weem.epicinventor.ai.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;

public class Snail extends Monster {

    private static final long serialVersionUID = 10000L;
    private float recastTotalTime;
    private float RECAST_TIME = 1.5f;
    private boolean canFire = true;
    private final static int MAX_RANGE = 400;

    public Snail(MonsterManager mm, Registry rg, String im, String st, int x, int y, int minDist, int maxDist) {
        super(mm, rg, im, st, x, y, minDist, maxDist, false);

        name = "Snail";
        displayName = "Snail";

        monsterManager = mm;
        
        difficultyFactor = 0.60f;

        adjustHPForLevel();

        topOffset = 17;
        baseOffset = 7;
        baseWidth = 58;
        startJumpSize = 20;
        jumpSize = 8;
        fallSize = 0;

        xMoveSize = 1;

        adjustTouchDamageForLevel();

        dropChances.addDropChance("LargeShell", 75.0f, 1, 1);

        ai = new AI(registry, this);
        ai.clearGoals();
        ai.addGoal(AI.GoalType.ATTACK_PLAYER_RANGED, "", (Rand.getRange(0, 1) + Rand.getFloat()));
        if (groundLevel == 0) {
            ai.addGoal(AI.GoalType.ATTACK_PLACEABLE, "", (Rand.getRange(0, 1) + Rand.getFloat()));
        }
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
                    "Goo",
                    10,
                    new Point(
                    getMapX(),
                    getMapY()),
                    targetPoint,
                    false,
                    false,
                    false,
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

    @Override
    protected void updateImage() {
        if (stateChanged) {
            if (vertMoveMode == VertMoveMode.JUMPING) {
                setImage("Monsters/" + name + "/Jumping");
            } else if (vertMoveMode == VertMoveMode.FLYING) {
                loopImage("Monsters/" + name + "/Flapping");
            } else if (vertMoveMode == VertMoveMode.FALLING) {
                loopImage("Monsters/" + name + "/Falling");
            } else {
                if (actionMode == ActionMode.ATTACKING) {
                    setImage("Monsters/" + name + "/RangeAttacking");
                    //loopImage("Monsters/" + name + "/RangeAttacking", 0.50);
                } else if (isStill) {
                    setImage("Monsters/" + name + "/Standing");
                } else {
                    loopImage("Monsters/" + name + "/Walking");
                }
            }

            stateChanged = false;
        }
    }
}