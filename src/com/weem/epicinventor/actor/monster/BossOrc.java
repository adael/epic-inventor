package com.weem.epicinventor.actor.monster;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.Actor;
import com.weem.epicinventor.ai.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;

public class BossOrc extends Monster {

    private static final long serialVersionUID = 10000L;
    public transient SoundClip soundClip;
    private long lastMove;

    public BossOrc(MonsterManager mm, Registry rg, String im, String st, int x, int y, int minDist, int maxDist) {
        super(mm, rg, im, st, x, y, minDist, maxDist, true);

        name = "BossOrc";
        displayName = "Melvin";

        monsterManager = mm;

        baseHitPoints = 6000;
        totalHitPoints = 6000;
        hitPoints = baseHitPoints;

        topOffset = 2;
        baseOffset = 36;
        baseWidth = 48;
        startJumpSize = 20;
        jumpSize = 8;
        fallSize = 0;

        xMoveSize = 3;

        touchDamage = 415;

        disregardKnockBack = true;

        level = -1;

        dropChances.addDropChance("MelvinHead", 100.0f, 1, 1);
        dropChances.addDropChance("Silver", 75.0f, 2, 3);
        dropChances.addDropChance("Gold", 50.0f, 2, 3);
        dropChances.addDropChance("Platinum", 50.0f, 1, 2);
        dropChances.addDropChance("Leather", 20.0f, 1, 2);
        dropChances.addDropChance("Tusk", 20.0f, 1, 1);
        dropChances.addDropChance("Sapphire", 10.0f, 1, 2);
        dropChances.addDropChance("Ruby", 10.0f, 1, 2);
        dropChances.addDropChance("Emerald", 10.0f, 1, 2);
        dropChances.addDropChance("Pulley", 5.0f, 1, 1);
        dropChances.addDropChance("Rope", 5.0f, 1, 1);
        dropChances.addDropChance("Diamond", 3.0f, 1, 2);
        dropChances.addDropChance("ScrapHammer", 5.0f, 1, 1);

        ai = new AI(registry, this);
        ai.clearGoals();
        ai.addGoal(AI.GoalType.BOSS_ORC, "", 1f);
        ai.activate();
    }

    @Override
    public void attack() {
        if (attackRefreshTimerEnd < System.currentTimeMillis()) {
            if (actionMode != ActionMode.ATTACKING) {
                actionMode = ActionMode.ATTACKING;
                stateChanged = true;
            }
            attackRefreshTimerStart = System.currentTimeMillis();
            attackRefreshTimerEnd = System.currentTimeMillis() + meleeSpeed;
            monsterManager.monsterAttackPlaceable(this, this.getSpriteRect(), touchDamage);
        }
    }

    public void jumpTowardsPoint(Point p) {
        if (p != null && knockBackX == 0) {
            int actorX = getMapX();
            int actorCenter = actorX + (getWidth() / 2);
            int targetX = p.x;
            int targetY = p.y;
            int distance = Math.abs(targetX - actorCenter);

            if (distance <= maxFollowDistance) {
                if (targetX > actorCenter) {
                    if (facing != Facing.RIGHT) {
                        moveRight();
                        stopMove();
                    }
                } else {
                    if (facing != Facing.LEFT) {
                        moveLeft();
                        stopMove();
                    }
                }
                checkCollide(0);
            } else {
                if (getIsTryingToMove()) {
                    if (targetX > actorCenter) {
                        if (getFacing() != Facing.RIGHT) {
                            moveRight();
                        }
                    } else {
                        if (getFacing() != Facing.LEFT) {
                            moveLeft();
                        }
                    }
                } else {
                    if (targetX > actorCenter) {
                        moveRight();
                    } else {
                        moveLeft();
                    }
                }

                //try and move
                int oldMapX = actorX;
                if (getIsTryingToMove()) {
                    if (getFacing() == Facing.RIGHT) {
                        actorX += checkCollideRight();
                    } else {
                        actorX -= checkCollideLeft();
                    }
                }

                if (vertMoveMode == VertMoveMode.NOT_JUMPING) {
                    jump();
                }

                //we didn't move anywhere - we've hit a wall
                if (actorX == oldMapX && actorX > 0) {
                } else {
                    lastMove = registry.currentTime;
                }

                setMapX(actorX);
            }
        } else {
            checkCollide(0);
        }
    }

    @Override
    protected void finishJumping() {
        super.finishJumping();

        if (hitPoints < baseHitPoints && isStomping) {
            monsterManager.shakeCamera(500, 10);
            monsterManager.stunPlayersOnGround(1500);
            soundClip = new SoundClip(registry, "Monster/BossOrcStomp", getCenterPoint());
        }
    }

    @Override
    public void moveTowardsPoint(Point p) {
        boolean updatedPosition = false;

        if (p != null && knockBackX == 0) {
            int actorX = getMapX();
            int actorCenter = actorX + (getWidth() / 2);
            int targetX = p.x;
            int targetY = p.y;
            int distance = Math.abs(targetX - actorCenter);

            if (distance <= maxFollowDistance) {
                if (targetX > actorCenter) {
                    if (facing != Facing.RIGHT) {
                        moveRight();
                        stopMove();
                    }
                } else {
                    if (facing != Facing.LEFT) {
                        moveLeft();
                        stopMove();
                    }
                }
            } else {
                if (getIsTryingToMove()) {
                    if (targetX > actorCenter) {
                        if (getFacing() != Facing.RIGHT) {
                            moveRight();
                        }
                    } else {
                        if (getFacing() != Facing.LEFT) {
                            moveLeft();
                        }
                    }
                } else {
                    if (targetX > actorCenter) {
                        moveRight();
                    } else {
                        moveLeft();
                    }
                }

                //try and move
                int oldMapX = actorX;
                if (getIsTryingToMove()) {
                    if (!updatedPosition) {
                        if (getFacing() == Facing.RIGHT) {
                            actorX += checkCollideRight();
                        } else {
                            actorX -= checkCollideLeft();
                        }
                    }
                    updatedPosition = true;
                }

                //are we entering a cave?
                if (manager.doesRectContainBlocks(actorX + baseOffset, mapY + 33, width - baseOffset, height * 3)) {
                    actorX = oldMapX;
                }

                //we didn't move anywhere - we've hit a wall
                if (actorX == oldMapX && actorX > 0) {
                } else {
                    lastMove = registry.currentTime;
                }

                setMapX(actorX);
            }
        }

        if (!updatedPosition) {
            checkCollide(0);
        }
    }

    @Override
    public Damage getMonsterTouchDamage(Rectangle r) {
        if (hitPoints > 0) {
            if (spriteRect != null && r != null) {
                if (spriteRect.intersects(r)) {
                    playerDamage += touchDamage;
                    if (this.isAttacking()) {
                        setStatusStun(true, 5000);
                        monsterManager.shakeCamera(100, 3);
                        soundClip = new SoundClip(registry, "Monster/BossOrcBonk", getCenterPoint());

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
            if (vertMoveMode == VertMoveMode.JUMPING) {
                setImage("Monsters/" + name + "/Jumping");
            } else if (vertMoveMode == VertMoveMode.FLYING) {
                loopImage("Monsters/" + name + "/Flapping");
            } else if (vertMoveMode == VertMoveMode.FALLING) {
                loopImage("Monsters/" + name + "/Falling");
            } else if (statusStun) {
                loopImage("Monsters/" + name + "/Stunned");
            } else {
                if (this.isAttacking()) {
                    loopImage("Monsters/" + name + "/Attacking", 0.05);
                } else if (isStill) {
                    setImage("Monsters/" + name + "/Standing");
                } else {
                    loopImage("Monsters/" + name + "/Walking");
                }
            }

            stateChanged = false;
        }
    }

    @Override
    public void updateLong() {
        if (hitPoints == baseHitPoints) {
            if (nextSoundPlay <= registry.currentTime) {
                soundClip = new SoundClip(registry, "Monster/Ambient" + name + Rand.getRange(1, 4), getCenterPoint());
                nextSoundPlay = registry.currentTime + Rand.getRange(6000, 10000);
                doChat(2000);
            }
        }
    }

    public void playNewClip(String name, long duration) {
        if (soundClip != null) {
            soundClip.stop();
        }
        soundClip = new SoundClip(registry, name, getCenterPoint());
        doChat(duration);
    }

    public void setLastMove(long m) {
        lastMove = m;
    }

    @Override
    public void update() {
        super.update();

        if (isDead) {
            monsterManager.setNextBossOrcSpawn(0);
        }

        if (this.isAttacking()) {
            topOffset = 44;
            baseWidth = 52;
            if (this.facing == Facing.RIGHT) {
                baseOffset = 12;
            } else {
                baseOffset = 64;
            }
        } else {
            topOffset = 2;
            baseWidth = 48;
            baseOffset = 36;
        }

        if (isDead) {
            registry.setBossFight(false);
        }

        //if boss hasn't moved in 15 seconds, he's probably stuck, remove him and respawn
        if (this.isAttacking()) {
            if (registry.currentTime - lastMove > (15 * 1000)) {
                isDirty = true;
                registry.setBossFight(false);
            }
        }
    }
}