package com.weem.epicinventor.ai;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.actor.monster.*;

import com.weem.epicinventor.utility.Rand;
import java.awt.*;

public class GoalResourceMonster extends Goal {

    private Point targetPoint;
    private long nextMove;
    private ResourceMonsterState resourceMonsterState;
    private int originalXMoveSize = 0;
    private int chargeSpeedBonus = 9;
    private long thinkTime = 3000;
    private long hideTime = 5000;
    private long moveTime = 5000;

    public enum ResourceMonsterState {

        PEACEFUL, CHARGING, HIDING, THINKING
    };

    public GoalResourceMonster(AI a, Registry r, String t, float b) {
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

        Monster actor = (Monster) ai.getActor();
        resourceMonsterState = ResourceMonsterState.PEACEFUL;

        if (actor == null) {
            return;
        }

        originalXMoveSize = actor.getXMoveSize();
        nextMove = registry.currentTime + moveTime;
    }

    @Override
    protected void process() {
        super.process();

        Monster actor = (Monster) ai.getActor();
        Player player = registry.getClosestPlayer(actor.getCenterPoint(), actor.getMaxAggroRange());

        if (actor == null || player == null) {
            return;
        }

        switch (resourceMonsterState) {
            case PEACEFUL:
                if (actor.getHitPoints() < actor.getTotalHitPoints() || actor.getCenterPoint().distance(player.getCenterPoint()) <= 100) {
                    targetPoint = null;
                    nextMove = registry.currentTime + moveTime;
                    actor.attack();
                    actor.setXMoveSize(originalXMoveSize + chargeSpeedBonus);
                    resourceMonsterState = ResourceMonsterState.CHARGING;
                } else {
                    actor.updatePosition();
                }
                break;
            case CHARGING:
                if (doCharge(actor, player)) {
                    actor.stopMove();
                    actor.stopAttack();
                    actor.setXMoveSize(originalXMoveSize);
                    nextMove = registry.currentTime + thinkTime;
                    resourceMonsterState = ResourceMonsterState.THINKING;
                } else {
                    actor.updatePosition();
                }
                break;
            case HIDING:
                if (registry.currentTime >= nextMove) {
                    actor.updatePosition();
                    targetPoint = null;
                    nextMove = registry.currentTime + moveTime;
                    actor.attack();
                    actor.setXMoveSize(originalXMoveSize + chargeSpeedBonus);
                    resourceMonsterState = ResourceMonsterState.CHARGING;
                } else {
                    if (doHide(actor, player)) {
                        targetPoint = null;
                        nextMove = registry.currentTime + moveTime;
                        actor.attack();
                        actor.setXMoveSize(originalXMoveSize + chargeSpeedBonus);
                        resourceMonsterState = ResourceMonsterState.CHARGING;
                    } else {
                        actor.updatePosition();
                    }
                }
                break;
            case THINKING:
                actor.updatePosition();
                if (registry.currentTime >= nextMove) {
                    if (Rand.getRange(1, 5) <= 2) {
                        targetPoint = null;
                        nextMove = registry.currentTime + moveTime;
                        actor.stopMove();
                        resourceMonsterState = ResourceMonsterState.HIDING;
                    } else {
                        targetPoint = null;
                        nextMove = registry.currentTime + moveTime;
                        actor.attack();
                        actor.setXMoveSize(originalXMoveSize + chargeSpeedBonus);
                        resourceMonsterState = ResourceMonsterState.CHARGING;
                    }
                } else {
                    actor.updatePosition();
                }
                break;
        }
    }

    private boolean doCharge(Actor actor, Player player) {
        //returns true if done charging
        if (registry.currentTime >= nextMove) {
            return true;
        } else if (targetPoint != null) {
            if (actor.getCenterPoint().distance(targetPoint) <= 100) {
                return true;
            } else {
                if (actor != null && targetPoint != null) {
                    actor.moveTowardsPoint(targetPoint);
                }
            }
        } else {
            if (player != null) {
                targetPoint = player.getCenterPoint();
                if (actor.getCenterPoint().x > player.getCenterPoint().x) {
                    targetPoint.x -= 300;
                } else {
                    targetPoint.x += 300;
                }
            }
        }

        if (actor.getMapX() <= 0) {
            return true;
        }

        return false;
    }

    private boolean doHide(Actor actor, Player player) {
        //returns true if done charging
        if (registry.currentTime >= nextMove) {
            if (actor.getIsHiding()) {
                actor.updatePosition();
                return true;
            }
        } else {
            actor.hide();
            actor.updatePosition();
        }

        return false;
    }

    @Override
    public void terminate() {
        super.terminate();
        ai.getActor().stopMove();
    }
}