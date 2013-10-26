package com.weem.epicinventor.ai;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;

import com.weem.epicinventor.utility.EIError;
import com.weem.epicinventor.utility.Rand;
import java.awt.*;

public class GoalWander extends Goal {

    private Point targetPoint;
    private long nextMove;
    private long nextTurnAround;

    public GoalWander(AI a, Registry r, String t, float b) {
        super(a, r, t, b);

        Actor actor = ai.getActor();
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

        Actor actor = ai.getActor();

        if (actor == null) {
            return;
        }

        targetPoint = getNewPoint(actor.getCenterPoint());
        nextMove = registry.currentTime + Rand.getRange(4000, 8000);
    }

    @Override
    protected void process() {
        super.process();

        Actor actor = ai.getActor();

        if (actor == null) {
            return;
        }

        if (actor.isFeared() && actor.getFearedSource() != null) {
            Actor attacker = actor.getLastAttacker();
            if(attacker != null) {
                actor.setFearedSource(attacker.getCenterPoint());
            }
            actor.moveAwayFromPoint(actor.getFearedSource());
        } else {
            if (registry.currentTime >= nextMove) {
                if (Rand.getRange(1, 4) == 1) {
                    targetPoint = getNewPoint(actor.getCenterPoint());
                } else {
                    turnAround(actor);
                }
                nextMove = registry.currentTime + Rand.getRange(2000, 5000);
            } else if (targetPoint != null) {
                if (Math.abs(actor.getMapX() - targetPoint.x) <= 50) {
                    actor.stopMove();
                    if (Rand.getRange(1, 4) == 1) {
                        targetPoint = getNewPoint(actor.getCenterPoint());
                    } else {
                        turnAround(actor);
                    }
                    nextMove = registry.currentTime + Rand.getRange(2000, 5000);
                } else {
                    if (actor != null && targetPoint != null) {
                        actor.moveTowardsPoint(targetPoint);
                    }
                }
            }
        }
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

    private Point getNewPoint(Point a) {
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