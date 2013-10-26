package com.weem.epicinventor.ai;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.actor.monster.*;

public class GoalAttackPlayerLunge extends Goal {

    private static int MAX_DISTANCE;
    private static int lungeRange = 200;
    private long nextLunge = 0;

    public GoalAttackPlayerLunge(AI a, Registry r, String t, float b) {
        super(a, r, t, b);
        MAX_DISTANCE = MonsterManager.mobSpawnRangeMax;
    }

    public Player getPlayerToAttack(Monster actor) {
        return registry.getClosestPlayer(actor.getCenterPoint(), actor.getMaxAggroRange());
    }

    @Override
    public float calculateDesire() {
        float desire = 0;

        //figure out the distance between player and mob
        int actorX = ai.getActor().getMapX();
        int playerX = registry.getClosestPlayerX(ai.getActor().getCenterPoint(), MAX_DISTANCE);
        int distance = Math.abs(playerX - actorX);

        //see if the mob needs to be attack the player
        //closer the player is, the more the mob wants to attack
        if (distance >= MAX_DISTANCE || playerX < 0) {
            desire = 0;
        } else {
            desire = 1.0f - ((float) distance / (float) MAX_DISTANCE);
        }

        desire *= bias;
        desire = validateDesire(desire);

        ai.getActor().setDebugInfo(ai.getActor().getDebugInfo() + "Player (" + ((int) desire * 100) + ")|");

        return desire;
    }

    @Override
    protected void activate() {
        super.activate();
    }

    @Override
    protected void process() {
        super.process();

        Monster actor = (Monster) ai.getActor();
        Player player = getPlayerToAttack(actor);

        if (actor == null || player == null) {
            terminate();
            return;
        }

        if (actor.isFeared() && actor.getFearedSource() != null) {
            actor.moveAwayFromPoint(actor.getFearedSource());
        } else {
            if (actor.getCenterPoint().distance(player.getCenterPoint()) > actor.getMaxLungeRange()) {
                actor.moveTowardsPoint(player.getCenterPoint());
            } else {
                if (actor.getCenterPoint().x < player.getCenterPoint().x) {
                    if(actor.getFacing() != Actor.Facing.RIGHT) {
                        actor.setFacing(Actor.Facing.RIGHT);
                    }
                    actor.checkCollideRight();
                } else {
                    if(actor.getFacing() != Actor.Facing.LEFT) {
                        actor.setFacing(Actor.Facing.LEFT);
                    }
                    actor.checkCollideLeft();
                }
                if (actor.isMoving()) {
                    actor.stopMove();
                } else {
                    if(nextLunge == 0) {
                        nextLunge = registry.currentTime + 5000;
                    }
                    if (actor.getVertMoveMode() == Actor.VertMoveMode.NOT_JUMPING && registry.currentTime >= nextLunge) {
                        if (actor.getCenterPoint().x < player.getCenterPoint().x) {
                            actor.applyKnockBack(30, 10);
                        } else {
                            actor.applyKnockBack(-30, 10);
                        }
                        nextLunge = registry.currentTime + 5000;
                    }
                }
            }
        }

        if (player != null) {
            if (player.getPerimeter().intersects(actor.getPerimeter())) {
                actor.attack();
            } else {
                if (actor.isAttacking()) {
                    actor.stopAttack();
                }
            }
        }
    }

    @Override
    public void terminate() {
        super.terminate();
        ai.getActor().stopMove();
    }
}