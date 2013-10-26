package com.weem.epicinventor.actor.monster;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.ai.*;
import com.weem.epicinventor.utility.*;

public class ZombieWalrus extends Monster {

    private static final long serialVersionUID = 10000L;

    public ZombieWalrus(MonsterManager mm, Registry rg, String im, String st, int x, int y, int minDist, int maxDist) {
        super(mm, rg, im, st, x, y, minDist, maxDist, false);

        name = "ZombieWalrus";
        displayName = "Zombie Walrus";

        monsterManager = mm;
        
        difficultyFactor = 0.80f;
        
        adjustHPForLevel();

        topOffset = 12;
        baseOffset = 29;
        baseWidth = 22;
        startJumpSize = 20;
        jumpSize = 8;
        fallSize = 0;

        xMoveSize = 2;

        adjustTouchDamageForLevel();
        spriteRectOffestX = 10;

        dropChances.addDropChance("Tusk", 75.0f, 1, 2);
        dropChances.addDropChance("Skin", 75.0f, 2, 3);
 
        ai = new AI(registry, this);
        ai.clearGoals();
        ai.addGoal(AI.GoalType.ATTACK_PLAYER, "", (Rand.getRange(0, 1) + Rand.getFloat()));
        if (groundLevel == 0) {
            ai.addGoal(AI.GoalType.ATTACK_PLACEABLE, "", (Rand.getRange(0, 1) + Rand.getFloat()));
        }
        ai.activate();
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
                if(actionMode == ActionMode.ATTACKING) {
                    loopImage("Monsters/" + name + "/Attacking", 0.50);
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