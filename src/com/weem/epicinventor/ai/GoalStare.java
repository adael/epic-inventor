package com.weem.epicinventor.ai;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.utility.EIError;

public class GoalStare extends Goal {

    private final static int CLOSE_DISTANCE = 25;

    public GoalStare(AI a, Registry r, String t, float b) {
        super(a, r, t, b);
    }

    @Override
    public float calculateDesire() {
        float desire = 0.10f;

        return desire;
    }

    @Override
    protected void activate() {
        super.activate();
        
        ai.getActor().stopMove();
    }

    @Override
    protected void process() {
        super.process();
        
        Actor actor = ai.getActor();
        
        Player player = registry.getPlayerManager().getPlayerById(target);
        
        if(actor == null || player == null) {
            return;
        }
        
        int actorX = actor.getMapX();
        int targetX = player.getMapX();

        if (targetX > actorX) {
            actor.setFacing(Actor.Facing.RIGHT);
        } else {
            actor.setFacing(Actor.Facing.LEFT);
        }
        if(actor.getVertMoveMode() == Actor.VertMoveMode.FALLING) {
            actor.checkCollide(0);
        }
    }

    @Override
    public void terminate() {
        super.terminate();
    }
}