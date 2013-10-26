package com.weem.epicinventor.projectile;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.network.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.Rectangle.*;
import java.awt.geom.AffineTransform;
import java.util.*;

public class ReturningProjectile extends Projectile {

    transient protected long nextHitTime = 0;
    transient protected int returningDistance = 0;

    public ReturningProjectile(ProjectileManager pm, Registry rg, Actor as, String im, int sp, Point s, Point e, boolean f, boolean p, boolean dt, int d) {
        super(pm, rg, as, im, sp, s, e, f, p, dt, d);
    }

    public ReturningProjectile(String i, ProjectileManager pm, Registry rg, Actor as, String im, int sp, Point s, Point e, boolean f, boolean p, int d) {
        super(i, pm, rg, as, im, sp, s, e, f, p, d);
    }

    public void update() {
        Point ePoint = null;

        if (!isDirty) {
            if (returning) {
                if (mapX >= end.x - 20 && mapX <= end.x + 20 && mapY >= end.y - 20 && mapY <= end.y + 20) {
                    if (registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.CLIENT) {
                        source.projectileReturned();
                        stopSound();
                        isDirty = true;
                    }
                }
                end = this.source.getCenterPoint();
            } else if (mapX >= end.x - 10 && mapX <= end.x + 10 && mapY >= end.y - 10 && mapY <= end.y + 10) {
                start = end;
                end = finalPoint;
                last = start;

                setSlope(start, end);
                count = 0;
                if (end.x > start.x) {
                    direction = Direction.RIGHT;
                } else {
                    direction = Direction.LEFT;
                }

                returning = true;
            }

            if (numAnimationFrames > 0) {
                currentAnimationFrame++;
                if (currentAnimationFrame >= numAnimationFrames) {
                    currentAnimationFrame = 0;
                }
                image = images[currentAnimationFrame];
            }

            //do movement

            if (count >= 0) {
                count++;
            }
            //if (last.distance(end) > 150) {
            if (count % 10 == 0) {
                last.x = mapX;
                last.y = mapY;
                setSlope(start, end);
            }

            mapX += (int) (speed * Math.cos(getAngleFromSlope()));
            mapY += (int) (speed * Math.sin(getAngleFromSlope()));

            if (end.x > start.x) {
                direction = Direction.RIGHT;
            } else {
                direction = Direction.LEFT;
            }

            if (registry.getGameController().multiplayerMode != registry.getGameController().multiplayerMode.CLIENT) {
                ePoint = new Point(mapX, mapY);

                //make sure it hasn't been going on for a long time
                if (start.distance(ePoint) > MAX_DISTANCE) {
                    source.projectileReturned();
                    stopSound();
                    isDirty = true;
                }

                //check for hitting an actor
                if (nextHitTime <= registry.currentTime) {
                    if (friendly) {
                        //check to see if it hit any monsters
                        if (projectileManager.checkMobProjectileHit(this)) {
                            //isDirty = true;
                        }
                    } else {
                        //hit player?
                        if (projectileManager.checkPlayerProjectileHit(this)) {
                            //isDirty = true;
                        } else if (projectileManager.checkPlaceableProjectileHit(this)) {
                            //isDirty = true;
                        }
                    }

                    nextHitTime = registry.currentTime + 200;
                }

                if (isDirty) {
                    stopSound();
                    if (registry.getGameController().multiplayerMode == registry.getGameController().multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                        if (registry.getNetworkThread().readyForUpdates) {
                            UpdateProjectile up = new UpdateProjectile();
                            up.id = this.getId();
                            up.action = "Destroy";
                            registry.getNetworkThread().sendData(up);
                        }
                    }
                    if (source != null) {
                        source.projectileReturned();
                    }
                }
            }
        }
    }
}
