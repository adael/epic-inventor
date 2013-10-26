package com.weem.epicinventor.ai;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.network.*;
import com.weem.epicinventor.utility.*;

import java.io.*;
import java.util.ArrayList;

public class AI implements Serializable {

    protected static final long serialVersionUID = 10000L;
    transient private Registry registry;
    transient private Actor actor;
    private ArrayList<Goal> goals;
    private boolean active = false;
    private Goal currentGoal;
    private Goal previousGoal;
    private boolean changed;
    private String player;

    public enum GoalType {
        WANDER, FLEE, FOLLOW, STARE, BOSS_ORC, RESOURCE_MONSTER, ATTACK_MOBS, ATTACK_PLAYER, ATTACK_PLAYER_LUNGE, ATTACK_PLAYER_RANGED, ATTACK_PLAYER_RANGED_AGGRESSIVE, ATTACK_PLACEABLE, ATTACK_TOWN
    };

    public AI(Registry r, Actor a) {
        registry = r;
        actor = a;

        goals = new ArrayList<Goal>();
    }

    public void setTransient(Actor a, Registry rg) {
        registry = rg;
        actor = a;
        for (int i = 0; i < goals.size(); i++) {
            goals.get(i).setTransient(rg, this);
        }
        if (currentGoal != null) {
            currentGoal.setTransient(rg, this);
        }
        if (previousGoal != null) {
            previousGoal.setTransient(rg, this);
        }
    }

    public Goal getPreviousGoal() {
        return previousGoal;
    }

    public Goal getCurrentGoal() {
        return currentGoal;
    }

    public void setPreviousGoal(Goal g) {
        g.setTransient(registry, this);
        previousGoal = g;
    }

    public void setCurrentGoal(Goal g) {
        g.setTransient(registry, this);
        currentGoal = g;
    }
    
    public void setPlayer(String p) {
        player = p;
    }
    
    public String getPlayer() {
        return player;
    }

    public boolean getChanged() {
        return changed;
    }

    public void setChanged(boolean c) {
        changed = c;
    }

    public void activate() {
        active = true;
    }

    public void process() {
        process(false);
    }

    public void process(boolean skipDecide) {
        if (active) {
            if (!skipDecide) {
                decide();
            }

            if (currentGoal != previousGoal) {
                changed = true;
                if (previousGoal != null) {
                    previousGoal.terminate();
                }
                currentGoal.activate();
                changed = true;
            }

            if (currentGoal != null) {
                currentGoal.process();
            }

            previousGoal = currentGoal;
        }
    }

    public void terminate() {
        if (currentGoal != null) {
            currentGoal.terminate();
        }
        if (previousGoal != null) {
            previousGoal.terminate();
        }
    }

    private void decide() {
        float highestDesire = 0;

        for (int i = 0; i < goals.size(); i++) {
            Goal goal = goals.get(i);
            if (goal != null) {
                float desire = goal.calculateDesire();
                if (desire > highestDesire || highestDesire == 0) {
                    highestDesire = desire;
                    currentGoal = goal;
                }
            }
        }
    }

    public void removeGoal(String goalType) {
        if (goals.size() > 0) {
            //terminate sub goals
            for (int i = 0; i < goals.size(); i++) {
                Goal goal = goals.get(i);
                if (goal != null) {
                    System.out.println("Goal: " + goal.getGoalType());
                    if (goal.getGoalType().equals(goalType)) {
                        System.out.println("Goal Terminated");
                        goal.terminate();
                    }
                }
            }

            //destroy sub goals
            for (int i = 0; i < goals.size(); i++) {
                Goal goal = goals.get(i);
                if (goal != null) {
                    if (goal.getGoalType().equals(goalType)) {
                        System.out.println("Goal Terminated");
                        goals.remove(i);
                    }
                }
            }
        }
    }

    public void clearGoals() {
        if (goals.size() > 0) {
            //terminate sub goals
            for (int i = 0; i < goals.size(); i++) {
                Goal goal = goals.get(i);
                if (goal != null) {
                    goal.terminate();
                }
            }

            //destroy sub goals
            for (int i = 0; i < goals.size(); i++) {
                goals.remove(i);
            }
        }
    }

    public boolean addGoal(GoalType gt, String target) {
        float bias = Rand.getFloat() + 0.5f; //0.5 - 1.5

        return addGoal(gt, target, bias);
    }

    public boolean addGoal(GoalType gt, String target, float bias) {
        switch (gt) {
            case BOSS_ORC:
                goals.add(new GoalBossOrc(this, registry, target, bias));
                return true;
            case WANDER:
                goals.add(new GoalWander(this, registry, target, bias));
                return true;
            case FLEE:
                goals.add(new GoalFlee(this, registry, target, bias));
                break;
            case FOLLOW:
                goals.add(new GoalFollow(this, registry, target, bias));
                break;
            case RESOURCE_MONSTER:
                goals.add(new GoalResourceMonster(this, registry, target, bias));
                return true;
            case STARE:
                goals.add(new GoalStare(this, registry, target, bias));
                break;
            case ATTACK_MOBS:
                goals.add(new GoalAttackMobs(this, registry, target, bias));
                break;
            case ATTACK_PLAYER:
                goals.add(new GoalAttackPlayer(this, registry, target, bias));
                break;
            case ATTACK_PLAYER_LUNGE:
                goals.add(new GoalAttackPlayerLunge(this, registry, target, bias));
                return true;
            case ATTACK_PLAYER_RANGED:
                goals.add(new GoalAttackPlayerRanged(this, registry, target, bias));
                break;
            case ATTACK_PLAYER_RANGED_AGGRESSIVE:
                goals.add(new GoalAttackPlayerRangedAggressive(this, registry, target, bias));
                break;
            case ATTACK_PLACEABLE:
                goals.add(new GoalAttackPlaceable(this, registry, target, bias));
                break;
            case ATTACK_TOWN:
                break;
        }

        return false;
    }

    public Actor getActor() {
        return actor;
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}