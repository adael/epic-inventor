package com.weem.epicinventor.ai;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;

import java.io.*;
import java.util.ArrayList;

public abstract class Goal implements Serializable {

    protected static final long serialVersionUID = 10000L;
    transient protected AI ai;
    transient protected Registry registry;
    protected String target = "";
    protected float bias;
    protected ArrayList<Goal> subGoals;
    protected boolean active = false;

    public Goal(AI a, Registry r, String t, float b) {
        ai = a;
        registry = r;
        target = t;
        bias = b;
        subGoals = new ArrayList<Goal>();
    }

    public void setTransient(Registry rg, AI a) {
        registry = rg;
        ai = a;
        for(int i = 0; i < subGoals.size(); i++) {
            subGoals.get(i).setTransient(rg, a);
        }
    }

    public float calculateDesire() {
        return 0f;
    }
    
    public String getGoalType() {
        return this.toString();
    }

    protected void activate() {
        active = true;
    }

    protected void process() {
        activateIfNotActive();
        
        //process next subgoal on the stack
        /*
        if (subGoals.size() > 0) {
            //terminate sub goals
            for (int i = 0; i < subGoals.size(); i++) {
                Goal subGoal = subGoals.get(i);
                if(subGoal != null) {
                    subGoal.terminate();
                }
            }
        }*/
    }

    public void terminate() {
        if (subGoals.size() > 0) {
            //terminate sub goals
            for (int i = 0; i < subGoals.size(); i++) {
                Goal subGoal = subGoals.get(i);
                if(subGoal != null) {
                    subGoal.terminate();
                }
            }
            
            //destroy sub goals
            for (int i = 0; i < subGoals.size(); i++) {
                subGoals.remove(i);
            }
        }
    }

    protected void activateIfNotActive() {
        if (!active) {
            activate();
        }
    }
    
    protected float validateDesire(float d) {
        if(d < 0) {
            d = 0;
        }
        
        if(d > 1f) {
            d = 1f;
        }
        
        return d;
    }
    
    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}