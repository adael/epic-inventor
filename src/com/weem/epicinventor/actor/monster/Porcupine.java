package com.weem.epicinventor.actor.monster;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.ai.*;
import com.weem.epicinventor.utility.*;

public class Porcupine extends Monster {

    private static final long serialVersionUID = 10000L;
    private int originalXMoveSize = 2;
    private int maxXMoveSize = 5;
    private long nextSlowDown = 0;

    public Porcupine(MonsterManager mm, Registry rg, String im, String st, int x, int y, int minDist, int maxDist) {
        super(mm, rg, im, st, x, y, minDist, maxDist, false);

        name = "Porcupine";
        displayName = "Porcupine";

        monsterManager = mm;
        
        difficultyFactor = 0.50f;

        adjustHPForLevel();

        topOffset = 1;
        baseOffset = 13;
        baseWidth = 28;
        startJumpSize = 20;
        jumpSize = 8;
        fallSize = 0;

        xMoveSize = 2;

        adjustTouchDamageForLevel();

        dropChances.addDropChance("Needle", 75.0f, 1, 5);

        ai = new AI(registry, this);
        ai.clearGoals();
        ai.addGoal(AI.GoalType.ATTACK_PLAYER, "", (Rand.getRange(0, 1) + Rand.getFloat()));
        if (groundLevel == 0) {
            ai.addGoal(AI.GoalType.ATTACK_PLACEABLE, "", (Rand.getRange(0, 1) + Rand.getFloat()));
        }
        ai.activate();
    }

    @Override
    public int applyDamage(int damage, Actor a) {
        int ret = super.applyDamage(damage, a);

        if (ret > 0) {
            xMoveSize++;
            if (xMoveSize > maxXMoveSize) {
                xMoveSize = maxXMoveSize;
            }
            stateChanged = true;
            updateImage();
            nextSlowDown = registry.currentTime + 3000;
        }

        return ret;
    }

    @Override
    public int applyDamage(int damage, Actor a, boolean fromPlaceable) {
        int ret = super.applyDamage(damage, a, fromPlaceable);

        if (ret > 0) {
            xMoveSize++;
            if (xMoveSize > maxXMoveSize) {
                xMoveSize = maxXMoveSize;
            }
            stateChanged = true;
            updateImage();
            nextSlowDown = registry.currentTime + 3000;
        }

        return ret;
    }

    @Override
    public int applyDamage(int damage, Actor a, boolean fromPlaceable, boolean sound) {
        int ret = super.applyDamage(damage, a, fromPlaceable, sound);

        if (ret > 0) {
            xMoveSize++;
            if (xMoveSize > maxXMoveSize) {
                xMoveSize = maxXMoveSize;
            }
            stateChanged = true;
            updateImage();
            nextSlowDown = registry.currentTime + 3000;
        }

        return ret;
    }

    @Override
    protected void updateImage() {
        if (stateChanged) {
            String state = "";
            if (xMoveSize > originalXMoveSize) {
                state = "Pissed";
            }
            if (vertMoveMode == VertMoveMode.JUMPING) {
                setImage("Monsters/" + name + "/Jumping" + state);
            } else if (vertMoveMode == VertMoveMode.FLYING) {
                loopImage("Monsters/" + name + "/Flapping" + state);
            } else if (vertMoveMode == VertMoveMode.FALLING) {
                loopImage("Monsters/" + name + "/Falling" + state);
            } else {
                if (this.isAttacking()) {
                    loopImage("Monsters/" + name + "/Attacking" + state);
                } else if (isStill) {
                    setImage("Monsters/" + name + "/Standing" + state);
                } else {
                    loopImage("Monsters/" + name + "/Walking" + state);
                }
            }

            stateChanged = false;
        }
    }

    @Override
    public void updateLong() {
        super.updateLong();

        if (nextSlowDown > 0) {
            //mob is speeding up
            if (xMoveSize < maxXMoveSize) {
                xMoveSize++;
                stateChanged = true;
                updateImage();
            }
        } else {
            //mob is going back to normal speed
            if (xMoveSize > originalXMoveSize) {
                xMoveSize--;
                stateChanged = true;
                updateImage();
            }
        }
        if (xMoveSize > maxXMoveSize) {
            xMoveSize = maxXMoveSize;
        }
        if (nextSlowDown < System.currentTimeMillis()) {
            nextSlowDown = 0;
        }
    }
}