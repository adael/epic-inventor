package com.weem.epicinventor.ai;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.actor.monster.*;

import com.weem.epicinventor.utility.EIError;
import com.weem.epicinventor.utility.Rand;
import java.awt.*;

public class GoalBossOrc extends Goal {

    private Point targetPoint;
    private long nextMove;
    private long nextTurnAround;
    private boolean playerHasHit = false;
    private BossOrcState bossOrcState;
    private int originalXMoveSize = 0;
    private int chargeSpeedBonus = 12;
    private int jumpSpeedBonus = 7;
    private long thinkTime = 1000;
    private long moveTime = 5000;
    private int lastHP = 0;

    public enum BossOrcState {

        PEACEFUL, CHARGING, JUMPING, THINKING, STUNNED
    };

    public GoalBossOrc(AI a, Registry r, String t, float b) {
        super(a, r, t, b);
    }

    @Override
    public float calculateDesire() {
        float desire = 0;

        desire *= bias;
        desire = validateDesire(desire);

        return desire;
    }

    @Override
    protected void activate() {
        super.activate();

        BossOrc actor = (BossOrc) ai.getActor();
        bossOrcState = BossOrcState.PEACEFUL;

        if (actor == null) {
            return;
        }

        originalXMoveSize = actor.getXMoveSize();
        targetPoint = getNewWanderPoint(actor.getCenterPoint());
        nextMove = registry.currentTime + moveTime;
    }

    @Override
    protected void process() {
        super.process();

        BossOrc actor = (BossOrc) ai.getActor();

        if (actor == null) {
            return;
        }

        if (actor.getStatusStun() && bossOrcState != BossOrcState.STUNNED) {
            actor.stopMove();
            bossOrcState = BossOrcState.STUNNED;
            actor.playNewClip("Monster/BossOrcStunned" + Rand.getRange(1, 3), 2000);
        }

        switch (bossOrcState) {
            case PEACEFUL:
                doWander(actor);
                if (actor.getHitPoints() < actor.getBaseHitPoints()) {
                    registry.setBossFight(true);
                    lastHP = actor.getHitPoints();
                    targetPoint = null;
                    nextMove = registry.currentTime + moveTime;
                    actor.attack();
                    actor.setXMoveSize(originalXMoveSize + chargeSpeedBonus);
                    actor.setLastMove(registry.currentTime);
                    bossOrcState = BossOrcState.CHARGING;
                    actor.setIsStomping(false);
                    actor.playNewClip("Monster/BossOrcCharging" + Rand.getRange(1, 3), 2000);
                }
                break;
            case CHARGING:
                registry.setBossFight(true);
                if (actor.getStatusStun()) {
                    bossOrcState = BossOrcState.STUNNED;
                    actor.setIsStomping(false);
                } else {
                    Player player = registry.getClosestPlayer(actor.getCenterPoint(), actor.getMaxAggroRange());
                    if (doCharge(actor, player)) {
                        actor.stopMove();
                        actor.stopAttack();
                        actor.setXMoveSize(originalXMoveSize);
                        nextMove = registry.currentTime + thinkTime;
                        bossOrcState = BossOrcState.THINKING;
                        actor.setIsStomping(false);
                    }
                }
                break;
            case JUMPING:
                registry.setBossFight(true);
                if (registry.currentTime >= nextMove) {
                    actor.updatePosition();
                    actor.stopMove();
                    actor.stopAttack();
                    actor.setXMoveSize(originalXMoveSize);
                    nextMove = registry.currentTime + thinkTime;
                    bossOrcState = BossOrcState.THINKING;
                } else {
                    Player player = registry.getClosestPlayer(actor.getCenterPoint(), actor.getMaxAggroRange());
                    if (doJump(actor, player)) {
                        actor.stopMove();
                        actor.stopAttack();
                        actor.setXMoveSize(originalXMoveSize);
                        nextMove = registry.currentTime + thinkTime;
                        bossOrcState = BossOrcState.THINKING;
                        actor.setIsStomping(false);
                    }
                }
                break;
            case THINKING:
                registry.setBossFight(true);
                actor.updatePosition();
                if (lastHP > actor.getHitPoints()) {
                    nextMove = 0;
                }
                if (registry.currentTime >= nextMove) {
                    if (Rand.getRange(1, 5) <= 2) {
                        targetPoint = null;
                        nextMove = registry.currentTime + moveTime;
                        actor.setXMoveSize(originalXMoveSize + jumpSpeedBonus);
                        bossOrcState = BossOrcState.JUMPING;
                        actor.setIsStomping(true);
                        actor.playNewClip("Monster/BossOrcJumping" + Rand.getRange(1, 1), 1000);
                    } else {
                        targetPoint = null;
                        nextMove = registry.currentTime + moveTime;
                        actor.attack();
                        actor.setXMoveSize(originalXMoveSize + chargeSpeedBonus);
                        bossOrcState = BossOrcState.CHARGING;
                        actor.setIsStomping(false);
                        actor.playNewClip("Monster/BossOrcCharging" + Rand.getRange(1, 3), 2000);
                    }
                }
                break;
            case STUNNED:
                registry.setBossFight(true);
                actor.updatePosition();
                if (!actor.getStatusStun()) {
                    actor.setXMoveSize(originalXMoveSize);
                    nextMove = registry.currentTime + thinkTime;
                    bossOrcState = BossOrcState.THINKING;
                    actor.setIsStomping(false);
                }
                break;
        }

        lastHP = actor.getHitPoints();
    }

    private void doWander(Actor actor) {
        if (targetPoint != null) {
            if (actor.getCenterPoint().distance(targetPoint) <= 100) {
                actor.stopMove();
                if (Rand.getRange(1, 3) == 1) {
                    targetPoint = getNewWanderPoint(actor.getCenterPoint());
                } else {
                    turnAround(actor);
                }
                nextMove = registry.currentTime + moveTime;
                actor.updatePosition();
            } else {
                if (actor != null && targetPoint != null) {
                    actor.moveTowardsPoint(targetPoint);
                } else {
                    actor.updatePosition();
                }
            }
        } else if (registry.currentTime >= nextMove) {
            if (Rand.getRange(1, 3) == 1) {
                targetPoint = getNewWanderPoint(actor.getCenterPoint());
            } else {
                turnAround(actor);
            }
            nextMove = registry.currentTime + moveTime;
            actor.updatePosition();
        } else {
            actor.updatePosition();
        }
    }

    private boolean doCharge(Actor actor, Player player) {
        boolean updatedPosition = false;
        
        //returns true if done charging
        if (registry.currentTime >= nextMove) {
            return true;
        } else if (targetPoint != null) {
            if (actor.getCenterPoint().distance(targetPoint) <= 100) {
                return true;
            } else {
                if (actor != null && targetPoint != null) {
                    actor.moveTowardsPoint(targetPoint);
                    updatedPosition = true;
                }
            }
        } else {
            if (player != null) {
                targetPoint = player.getCenterPoint();
                if (actor.getCenterPoint().x > player.getCenterPoint().x) {
                    targetPoint.x -= 500;
                } else {
                    targetPoint.x += 500;
                }
            }
        }
        
        if(!updatedPosition) {
            actor.updatePosition();
        }
        
        if(actor.getMapX() <= 0) {
            return true;
        }

        return false;
    }

    private boolean doJump(BossOrc actor, Player player) {
        //returns true if done charging
        if (registry.currentTime >= nextMove) {
            if (actor.getVertMoveMode() == Actor.VertMoveMode.NOT_JUMPING) { 
                actor.updatePosition();
                return true;
            }
        } else if (targetPoint != null) {
            if (actor.getCenterPoint().distance(targetPoint) <= 100) {
                actor.updatePosition();
                return true;
            } else {
                if (actor != null && targetPoint != null) {
                    actor.jumpTowardsPoint(targetPoint);
                } else {
                    actor.updatePosition();
                }
            }
        } else {
            if (player != null) {
                targetPoint = player.getCenterPoint();
                if (actor.getCenterPoint().x > player.getCenterPoint().x) {
                    targetPoint.x -= 500;
                } else {
                    targetPoint.x += 500;
                }
            }
            actor.updatePosition();
        }

        return false;
    }

    private void turnAround(Actor actor) {
        if (actor != null) {
            if (actor.getFacing() == Actor.Facing.RIGHT) {
                actor.setFacing(Actor.Facing.LEFT);
            } else {
                actor.setFacing(Actor.Facing.RIGHT);
            }
            actor.stopMove();
        }
        targetPoint = null;
    }

    private Point getNewWanderPoint(Point a) {
        int newX = Rand.getRange(50, 200);
        if (Rand.getRange(0, 1) == 1) {
            newX *= -1;
        }
        newX += a.x;

        targetPoint = new Point(newX, a.y);

        return new Point(newX, a.y);
    }

    @Override
    public void terminate() {
        super.terminate();
        ai.getActor().stopMove();
    }
}