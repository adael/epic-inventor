package com.weem.epicinventor.actor.monster;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.Actor;
import com.weem.epicinventor.ai.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;

public class RockMonster extends Monster {

    private static final long serialVersionUID = 10000L;
    public transient SoundClip soundClip;
    private long lastMove;
    private boolean isHiding = true;

    public RockMonster(MonsterManager mm, Registry rg, String im, String st, int x, int y, int minDist, int maxDist) {
        super(mm, rg, im, st, x, y, minDist, maxDist, true);

        name = "RockMonster";
        displayName = "Rock Monster";

        monsterManager = mm;
        
        difficultyFactor = 0.10f;

        adjustHPForLevel();

        topOffset = 0;
        baseOffset = 16;
        baseWidth = 16;
        startJumpSize = 20;
        jumpSize = 8;
        fallSize = 0;

        xMoveSize = 3;

        adjustTouchDamageForLevel();

        disregardKnockBack = true;

        dropChances.addDropChance("Stone", 100.0f, 2, 6);
        dropChances.addDropChance("Web", 50.0f, 1, 2);
        dropChances.addDropChance("Thorn", 25.0f, 1, 1);

        ai = new AI(registry, this);
        ai.clearGoals();
        ai.addGoal(AI.GoalType.RESOURCE_MONSTER, "", 1f);
        ai.activate();
    }

    @Override
    public void hide() {
        stateChanged = true;
        isHiding = true;
    }

    @Override
    public boolean getIsHiding() {
        return isHiding;
    }

    @Override
    public int applyDamage(int damage, Actor a, boolean fromPlaceable) {
        if (damage > 1 && isHiding) {
            damage = 1;
        }

        return super.applyDamage(damage, a, fromPlaceable);
    }

    @Override
    public int applyDamage(int damage, Actor a) {
        if (damage > 1 && isHiding) {
            damage = 1;
        }

        return super.applyDamage(damage, a);
    }

    @Override
    public void attack() {
        if (attackRefreshTimerEnd < System.currentTimeMillis()) {
            isHiding = false;
            if (actionMode != ActionMode.ATTACKING) {
                actionMode = ActionMode.ATTACKING;
                stateChanged = true;
            }
            attackRefreshTimerStart = System.currentTimeMillis();
            attackRefreshTimerEnd = System.currentTimeMillis() + meleeSpeed;
            monsterManager.monsterAttackPlaceable(this, this.getSpriteRect(), touchDamage);
        }
    }

    @Override
    public Damage getMonsterTouchDamage(Rectangle r) {
        if (hitPoints > 0) {
            if (spriteRect != null && r != null) {
                if (spriteRect.intersects(r)) {
                    playerDamage += touchDamage;
                    if (this.isAttacking()) {
                        monsterManager.shakeCamera(100, 3);
                        soundClip = new SoundClip(registry, "Monster/RockMonsterBonk", getCenterPoint());

                        if (facing == Facing.RIGHT) {
                            return new Damage(this, touchDamage, 20, 10);
                        } else {
                            return new Damage(this, touchDamage, -20, 10);
                        }
                    } else {
                        return new Damage(this, touchDamage);
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected void updateImage() {
        if (stateChanged) {
            hideDisplayName = false;
            if (vertMoveMode == VertMoveMode.JUMPING) {
                setImage("Monsters/" + name + "/Jumping");
            } else if (vertMoveMode == VertMoveMode.FLYING) {
                loopImage("Monsters/" + name + "/Flapping");
            } else if (vertMoveMode == VertMoveMode.FALLING) {
                loopImage("Monsters/" + name + "/Falling");
            } else {
                if (this.isAttacking()) {
                    loopImage("Monsters/" + name + "/Attacking", 0.05);
                } else if (isHiding) {
                    hideDisplayName = true;
                    setImage("Monsters/" + name + "/Hiding");
                } else if (isStill) {
                    setImage("Monsters/" + name + "/Standing");
                } else {
                    loopImage("Monsters/" + name + "/Walking", 0.05);
                }
            }

            stateChanged = false;
        }
    }

    public void setLastMove(long m) {
        lastMove = m;
    }
}