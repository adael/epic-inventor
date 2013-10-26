package com.weem.epicinventor.projectile;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.network.*;
import com.weem.epicinventor.utility.*;

import com.weem.epicinventor.utility.EIError;
import java.awt.*;
import java.util.*;
import java.util.ArrayList;
import java.awt.geom.Arc2D;

public class ProjectileManager extends Manager {

    private HashMap<String, Projectile> projectiles;

    public ProjectileManager(GameController gc, Registry rg) {
        super(gc, rg);

        projectiles = new HashMap<String, Projectile>();
    }

    public void registerProjectile(Projectile p) {
        if (!projectiles.containsKey(p.getId())) {
            projectiles.put(p.getId(), p);
        }
    }

    public void createProjectile(Actor source, String im, int sp, Point start, Point end, boolean f, boolean p, boolean dt, int d) {
        if (gameController.multiplayerMode != gameController.multiplayerMode.CLIENT) {
            Projectile projectile = new Projectile(this, registry, source, im, sp, start, end, f, p, dt, d);

            if (gameController.multiplayerMode == gameController.multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                if (registry.getNetworkThread().readyForUpdates) {
                    UpdateProjectile up = new UpdateProjectile();
                    up.id = projectile.getId();
                    if (source != null) {
                        up.playerId = source.getId();
                    }
                    up.image = im;
                    up.speed = sp;
                    up.start = start;
                    up.end = end;
                    up.friendly = f;
                    up.placeable = p;
                    up.disregardTerrain = p;
                    up.damage = d;
                    up.action = "Create";
                    registry.getNetworkThread().sendData(up);
                }
            }

            registerProjectile(projectile);
            
            return;
        } else {
            return;
        }
    }

    public ReturningProjectile createReturningProjectile(Actor source, String im, int sp, Point start, Point end, boolean f, boolean p, boolean dt, int d) {
        if (gameController.multiplayerMode != gameController.multiplayerMode.CLIENT) {
            ReturningProjectile projectile = new ReturningProjectile(this, registry, source, im, sp, start, end, f, p, dt, d);

            if (gameController.multiplayerMode == gameController.multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                if (registry.getNetworkThread().readyForUpdates) {
                    UpdateProjectile up = new UpdateProjectile();
                    up.id = projectile.getId();
                    if (source != null) {
                        up.playerId = source.getId();
                    }
                    up.image = im;
                    up.speed = sp;
                    up.start = start;
                    up.end = end;
                    up.friendly = f;
                    up.placeable = p;
                    up.disregardTerrain = p;
                    up.damage = d;
                    up.action = "Create";
                    registry.getNetworkThread().sendData(up);
                }
            }

            registerProjectile(projectile);
            
            return projectile;
        } else {
            return null;
        }
    }

    public void weaponSwing(Arc2D.Double arc, Point p) {
        boolean projectileHit = false;

        Projectile projectile = null;

        try {
            for (String key : projectiles.keySet()) {
                projectile = (Projectile) projectiles.get(key);
                if (projectile != null) {
                    if (arc.intersects(projectile.getRect())) {
                        projectile.setIsDirty(true);
                        projectileHit = true;
                    }
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify projectiles while iterating
            //we'll continue and the new item can be grabbed on the next update
        }

        if (projectileHit) {
            SoundClip cl = new SoundClip(registry, "Player/HitProjectile", p);
        }
    }
    public void removeAll() {
        Projectile projectile = null;
        ArrayList deadProjectiles = new ArrayList();

        try {
            for (String key : projectiles.keySet()) {
                projectile = (Projectile) projectiles.get(key);
                deadProjectiles.add(key);
            }

            if (deadProjectiles.size() > 0) {
                for (int i = 0; i < deadProjectiles.size(); i++) {
                    projectiles.remove((String) deadProjectiles.get(i));
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify projectiles while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
    }

    @Override
    public void update() {
        super.update();

        Projectile projectile = null;
        ArrayList deadProjectiles = new ArrayList();

        try {
            for (String key : projectiles.keySet()) {
                projectile = (Projectile) projectiles.get(key);
                projectile.update();
                if (projectile.isDirty()) {
                    deadProjectiles.add(key);
                }
            }

            if (deadProjectiles.size() > 0) {
                for (int i = 0; i < deadProjectiles.size(); i++) {
                    projectiles.remove((String) deadProjectiles.get(i));
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify projectiles while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
    }

    public void render(Graphics g) {
        Projectile projectile = null;

        try {
            for (String key : projectiles.keySet()) {
                projectile = (Projectile) projectiles.get(key);
                projectile.render(g);
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify monsters while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
    }

    public void processProjectileUpdate(UpdateProjectile up) {
        if (up.action.equals("Create")) {
            Player p = registry.getPlayerManager().getPlayerById(up.playerId);
            Projectile projectile = new Projectile(up.id, this, registry, p, up.image, up.speed, up.start, up.end, up.friendly, up.placeable, up.damage);
            registerProjectile(projectile);
        } else if (up != null) {
            if (projectiles.containsKey(up.id)) {
                Projectile projectile = projectiles.get(up.id);
                if (projectile != null) {
                    EIError.debugMsg(up.id + " (" + up.action + ")");
                    if (up.action.equals("Destroy")) {
                        projectile.destroy();
                    }
                }
            }
        }
    }
}
