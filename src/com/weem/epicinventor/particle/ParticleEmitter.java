package com.weem.epicinventor.particle;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;

import com.weem.epicinventor.utility.Rand;
import java.awt.*;
import java.util.*;
import java.util.ArrayList;
import java.awt.geom.Arc2D;

public class ParticleEmitter extends Manager {

    int mapX;
    int mapY;
    private ArrayList<Particle> particles;
    private ArrayList<String> images;
    private int particlesPerGeneration = 2;
    private boolean active;
    private Point targetPoint;
    private Actor source;
    private boolean friendly;
    private boolean placeable;
    private boolean disregardTerrain;
    private int damage;
    private float speed;
    private float maxSpread;
    private int maxDistance;
    private boolean velocityBasedRotation;

    public ParticleEmitter(GameController gc, Registry rg, Actor as, int x, int y, ArrayList<String> im, boolean f, boolean p, boolean dt, int d, float sp, float ms, int md) {
        super(gc, rg);

        source = as;
        mapX = x;
        mapY = y;
        particles = new ArrayList<Particle>();
        images = im;
        friendly = f;
        placeable = p;
        disregardTerrain = dt;
        damage = d;
        speed = sp;
        maxSpread = ms;
        maxDistance = md;
        velocityBasedRotation = false;
    }

    public ParticleEmitter(GameController gc, Registry rg, Actor as, int x, int y, ArrayList<String> im, boolean f, boolean p, boolean dt, int d, float sp, float ms, int md, boolean vbr) {
        super(gc, rg);

        source = as;
        mapX = x;
        mapY = y;
        particles = new ArrayList<Particle>();
        images = im;
        friendly = f;
        placeable = p;
        disregardTerrain = dt;
        damage = d;
        speed = sp;
        maxSpread = ms;
        maxDistance = md;
        velocityBasedRotation = vbr;
    }

    public void setPosition(int x, int y) {
        mapX = x;
        mapY = y;
    }

    public void setActive(boolean a) {
        active = a;
    }

    public void setParticlesPerGeneration(int p) {
        particlesPerGeneration = p;
    }

    public void setTargetPoint(Point p) {
        targetPoint = p;
    }

    @Override
    public void update() {
        super.update();

        Particle particle = null;

        if (active) {
            for (int i = 0; i < particlesPerGeneration; i++) {
                particles.add(generateNewParticle());
            }
        }

        for (int i = 0; i < particles.size(); i++) {
            particle = particles.get(i);
            particle.update();
            if (particle.ttl <= 0 || particle.isDirty) {
                particles.remove(i);
            }
        }
    }
    
    protected double getAngleFromSlope() {
        double angle = 0.0f;
        float slope = 0.0f;
        if((targetPoint.x - mapX) == 0.0f) {
            if((targetPoint.y - mapY) > 0.0f) {
                angle = Math.PI / 2;
            } else {
                angle = -Math.PI / 2;
            }
        } else {
            slope = ((float) (targetPoint.y - mapY)) / ((float) (targetPoint.x - mapX));
            angle = Math.atan(slope);
            if ((targetPoint.x - mapX) < 0) {
                angle += Math.PI;
            }
        }
        return angle;
    }
    
    private Particle generateNewParticle() {
        float velocityX = 0;
        float velocityY = 0;
        
        targetPoint = registry.getMouseMapPosition();

        if (targetPoint != null) {
            double angle = getAngleFromSlope();
            double spread = (Rand.getFloat() - 0.5f) * Math.PI * maxSpread / 180.0f;
            angle += spread;
            velocityX = (float)(speed * Math.cos(angle));
            velocityY = (float)(speed * Math.sin(angle));
        } else {
            velocityX = ((float) Rand.getRange(0, 7)) + Rand.getFloat() + 0.25f;
            velocityY = Rand.getFloat() + 0.05f;
            if (Rand.getRange(0, 1) == 1) {
                velocityY *= -1;
            }
        }

        float angle = 0;
        float angularVelocity = ((float) Rand.getRange(1, 5));
        
        double v = Math.sqrt(Math.pow(velocityY, 2) + Math.pow(velocityX, 2));
        int ttl = (int) (maxDistance / v);
        
        return new Particle(
                this,
                registry,
                source,
                images.get(Rand.getRange(0, images.size() - 1)),
                mapX,
                mapY,
                velocityX,
                velocityY,
                0f,
                angularVelocity,
                Rand.getFloat(),
                ttl,
                friendly,
                placeable,
                disregardTerrain,
                damage,
                velocityBasedRotation);
    }

    public void render(Graphics g) {
        Particle particle = null;

        for (int i = 0; i < particles.size(); i++) {
            particle = particles.get(i);
            particle.render(g);
        }
    }

    public void destroy() {
        Particle particle = null;

        for (int i = 0; i < particles.size(); i++) {
            particles.remove(i);
        }
    }
}
