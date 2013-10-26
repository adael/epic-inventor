package com.weem.epicinventor.utility;

import com.weem.epicinventor.actor.*;

public class Damage {

    private Actor source;
    private int amount;
    private int knockBackX;
    private int knockBackY;

    public Damage(Actor s, int a) {
        source = s;
        amount = a;
    }

    public Damage(Actor s, int a, int x, int y) {
        source = s;
        amount = a;
        knockBackX = x;
        knockBackY = y;
    }

    public Actor getSource() {
        return source;
    }

    public int getAmount() {
        return amount;
    }

    public int getKnockBackX() {
        return knockBackX;
    }

    public int getKnockBackY() {
        return knockBackY;
    }
}