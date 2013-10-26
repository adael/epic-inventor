package com.weem.epicinventor.ai;

import com.weem.epicinventor.*;
import com.weem.epicinventor.placeable.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.actor.monster.*;

public class GoalAttackPlaceable extends Goal {

    private final static int MAX_DISTANCE = 10000;
    private Placeable currentTarget;
    private Placeable newTarget;

    public GoalAttackPlaceable(AI a, Registry r, String t, float b) {
        super(a, r, t, b);
    }

    @Override
    public float calculateDesire() {
        float desire = 0f;

        Actor actor = ai.getActor();
        if (actor == null) {
            return desire;
        }


        newTarget = registry.getPlaceableManager().getClosestActivated(actor.getCenterPoint());
        if (newTarget == null) {
            return desire;
        }

        //figure out the distance between player and mob
        int actorX = ai.getActor().getMapX();
        int placeableX = newTarget.getMapX();
        int distance = Math.abs(placeableX - actorX);

        //see if the mob needs to be attack a placeable
        //closer the player is, the more the mob wants to attack
        if (distance >= MAX_DISTANCE) {
            desire = 0.01f;
        } else {
            desire = 1.0f - ((float) distance / (float) MAX_DISTANCE);
        }

        desire *= bias;
        desire = validateDesire(desire);
        
        ai.getActor().setDebugInfo(ai.getActor().getDebugInfo() + "Placeable (" + ((int) desire * 100) + ") - " + newTarget.toString() + "|");

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

        currentTarget = newTarget;

        if (actor == null || currentTarget == null) {
            terminate();
            return;
        }


        if (actor.isFeared() && actor.getFearedSource() != null) {
            actor.moveAwayFromPoint(actor.getFearedSource());
        } else {
            if (actor.isWithinAttackRange(currentTarget.getCenterPoint())) {
                actor.attack();
            } else {
                if (actor.isAttacking()) {
                    actor.stopAttack();
                }
                actor.moveTowardsPoint(currentTarget.getCenterPoint());
            }
        }
    }

    @Override
    public void terminate() {
        super.terminate();

        Actor actor = ai.getActor();

        if (actor == null) {
            return;
        }

        actor.setActionMode(Actor.ActionMode.NONE);
    }
}