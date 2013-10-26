package com.weem.epicinventor.ai;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;

public class GoalFlee extends Goal {

    private final static int SAFE_DISTANCE = 200;

    public GoalFlee(AI a, Registry r, String t, float b) {
        super(a, r, t, b);
    }

    @Override
    public float calculateDesire() {
        float desire = 0;

        //figure out the distance between player and mob
        int actorX = ai.getActor().getMapX();
        int playerX = registry.getClosestPlayerX(ai.getActor().getCenterPoint(), registry.getScreenWidth());
        int distance = Math.abs(playerX - actorX);
        
        //see if the mob needs to be scared
        //closer the player is, the more scared the mob is
        if (distance >= SAFE_DISTANCE) {
            desire = 0;
        } else {
            desire = 1.0f - ((float) distance / (float) SAFE_DISTANCE);
        }

        desire *= bias;
        desire = validateDesire(desire);

        return desire;
    }

    @Override
    protected void activate() {
        super.activate();
    }

    @Override
    protected void process() {
        super.process();
        
        Actor actor = ai.getActor();
        
        int actorX = actor.getMapX();
        int playerX = registry.getClosestPlayerX(actor.getCenterPoint(), registry.getScreenWidth());
        int distance = Math.abs(playerX - actorX);

        if (actor.getIsTryingToMove()) {
            if (playerX > actorX) {
                if (actor.getFacing() != Actor.Facing.LEFT) {
                    actor.moveLeft();
                }
            } else {
                if (actor.getFacing() != Actor.Facing.RIGHT) {
                    actor.moveRight();
                }
            }
        } else {
            if (playerX > actorX) {
                actor.moveLeft();
            } else {
                actor.moveRight();
            }
        }

        //try and move
        int oldMapX = actorX;
        if (actor.getIsTryingToMove()) {
            if (actor.getFacing() == Actor.Facing.RIGHT) {
                actorX += actor.checkCollideRight();
            } else {
                actorX -= actor.checkCollideLeft();
            }
        }

        //we didn't move anywhere - try jumping?
        if (actorX == oldMapX) {
            actor.jump();
        }
        
        actor.setMapX(actorX);
    }

    @Override
    public void terminate() {
        super.terminate();
        ai.getActor().stopMove();
    }
}