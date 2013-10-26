package com.weem.epicinventor.ai;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;

public class GoalFollow extends Goal {

    private final static int CLOSE_DISTANCE = 25;

    public GoalFollow(AI a, Registry r, String t, float b) {
        super(a, r, t, b);
    }

    @Override
    public float calculateDesire() {
        float desire = 0;
        
        Actor actor = ai.getActor();
        
        Player player = registry.getPlayerManager().getPlayerById(target);
        
        if(actor == null || player == null) {
            return desire;
        }
        
        if(!actor.getIsFollowing()) {
            return desire;
        }

        //figure out the distance between player and target
        int actorX = actor.getMapX();
        int targetX = player.getMapX();
        int distance = Math.abs(targetX - actorX);
        
        //see if the actor needs to move closer
        //closer the target is, the better it feels
        if (distance <= CLOSE_DISTANCE) {
            desire = 0;
        } else {
            desire = 1.0f - ((float) CLOSE_DISTANCE / (float) distance);
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
        Player player = registry.getPlayerManager().getPlayerById(target);
        
        if(actor == null || player == null) {
            return;
        }
        
        Point p = player.getCenterPoint();
        
        actor.moveTowardsPoint(player.getCenterPoint());
    }

    @Override
    public void terminate() {
        super.terminate();
        ai.getActor().stopMove();
    }
}