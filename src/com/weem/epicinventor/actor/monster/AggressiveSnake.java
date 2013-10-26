package com.weem.epicinventor.actor.monster;

import com.weem.epicinventor.*;
import com.weem.epicinventor.ai.*;
import com.weem.epicinventor.utility.*;

public class AggressiveSnake extends Monster {

    private static final long serialVersionUID = 10000L;
    private final static int MAX_RANGE = 300;

    public AggressiveSnake(MonsterManager mm, Registry rg, String im, String st, int x, int y, int minDist, int maxDist) {
        super(mm, rg, im, st, x, y, minDist, maxDist, false);

        name = "AggressiveSnake";
        displayName = "Snake";

        monsterManager = mm;
        
        difficultyFactor = 0.60f;

        adjustHPForLevel();

        topOffset = 0;
        baseOffset = 3;
        baseWidth = 37;
        startJumpSize = 20;
        jumpSize = 8;
        fallSize = 0;

        xMoveSize = 2;

        adjustTouchDamageForLevel();

        dropChances.addDropChance("Fang", 75.0f, 1, 2);

        ai = new AI(registry, this);
        ai.clearGoals();
        ai.addGoal(AI.GoalType.ATTACK_PLAYER_LUNGE, "", 1f);
        ai.activate();
    }

    @Override
    public int getMaxLungeRange() {
        return MAX_RANGE;
    }
}