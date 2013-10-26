package com.weem.epicinventor.actor.monster;

import com.weem.epicinventor.*;
import com.weem.epicinventor.ai.*;
import com.weem.epicinventor.utility.*;

public class Orc extends Monster {

    private static final long serialVersionUID = 10000L;

    public Orc(MonsterManager mm, Registry rg, String im, String st, int x, int y, int minDist, int maxDist) {
        super(mm, rg, im, st, x, y, minDist, maxDist, false);

        name = "Orc";
        displayName = "Orc";

        monsterManager = mm;
        
        difficultyFactor = 0.80f;

        adjustHPForLevel();

        topOffset = 16;
        baseOffset = 18;
        baseWidth = 24;
        startJumpSize = 20;
        jumpSize = 8;
        fallSize = 0;

        xMoveSize = 3;

        adjustTouchDamageForLevel();

        dropChances.addDropChance("Emerald", 15.0f, 1, 2);
        dropChances.addDropChance("Iron", 20.0f, 1, 3);
        dropChances.addDropChance("Tusk", 15.0f, 1, 2);
        dropChances.addDropChance("Skin", 15.0f, 1, 1);

        ai = new AI(registry, this);
        ai.clearGoals();
        ai.addGoal(AI.GoalType.ATTACK_PLAYER, "", (Rand.getRange(0, 1) + Rand.getFloat()));
        ai.activate();
    }
}