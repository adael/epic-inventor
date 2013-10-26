package com.weem.epicinventor.actor.monster;

import com.weem.epicinventor.*;
import com.weem.epicinventor.ai.*;
import com.weem.epicinventor.utility.*;

public class LionFly extends Monster {

    private static final long serialVersionUID = 10000L;

    public LionFly(MonsterManager mm, Registry rg, String im, String st, int x, int y, int minDist, int maxDist) {
        super(mm, rg, im, st, x, y, minDist, maxDist, true);

        name = "LionFly";
        displayName = "Flion";

        monsterManager = mm;
        
        difficultyFactor = 0.60f;

        adjustHPForLevel();

        topOffset = 27;
        baseOffset = 12;
        baseWidth = 23;
        startJumpSize = 20;
        jumpSize = 8;
        fallSize = 0;

        xMoveSize = 4;

        adjustTouchDamageForLevel();
        
        canFly = true;

        dropChances.addDropChance("Fur", 75.0f, 1, 2);
        dropChances.addDropChance("Hair", 25.0f, 1, 2);
        dropChances.addDropChance("Skin", 75.0f, 1, 1);

        ai = new AI(registry, this);
        ai.clearGoals();
        ai.addGoal(AI.GoalType.ATTACK_PLAYER, "", (Rand.getRange(0, 1) + Rand.getFloat()));
        ai.activate();
    }
}