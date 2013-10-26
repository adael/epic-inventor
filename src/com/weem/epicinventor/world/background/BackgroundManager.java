package com.weem.epicinventor.world.background;

import com.weem.epicinventor.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;
import java.util.ArrayList;

public class BackgroundManager extends Manager {

    ArrayList<Background> backgrounds;
    ArrayList<Parallax> parallaxBackgrounds;
    private static final int CLOUD_BACKGROUND_COUNT = 100;
    private static final int CLOUD_FORGROUND_COUNT = 50;
    private static final int TREES_COUNT = 300;
    private static final int MOUNTAIN_COUNT = 6;

    public BackgroundManager(GameController gc, Registry rg) {
        super(gc, rg);

        backgrounds = new ArrayList<Background>();
        parallaxBackgrounds = new ArrayList<Parallax>();

        int x, y;
        float ms;
        Background b;
        Parallax parallax;

        int camMapX = registry.getGameController().getMapOffsetX();
        int camMapY = registry.getGameController().getMapOffsetY();
        
        float moveFactor = 0.97f;
        for (int i = 0; i < CLOUD_BACKGROUND_COUNT; i++) {
            x = Rand.getRange(-(getPWidth()/2), getPWidth()*3/2);
            y = Rand.getRange(registry.getBlockManager().getMapSurfaceMin(), registry.getBlockManager().getMapSurfaceMax() + getPHeight() * 3);
            ms = (float) Rand.getFloat() + 0.5f;
            parallax = new Parallax(this, registry, "Background/Cloud" + Rand.getRange(1, 5), "Cloud", x, y, camMapX, camMapY, moveFactor, ms);
            parallaxBackgrounds.add(parallax);
        }
        moveFactor = 0.95f;
        for (int i = 0; i < MOUNTAIN_COUNT; i++) {
            x = Rand.getRange(-getPWidth(), (int)(gameController.getMapWidth()*(1-moveFactor)+gameController.getPWidth()));
            y = Rand.getRange(registry.getBlockManager().getMapSurfaceMin() - 400, registry.getBlockManager().getMapSurfaceMin() + 400);
            parallax = new Parallax(this, registry, "Background/Mountain" + Rand.getRange(1, 2), "Mountain", x, y, camMapX, camMapY, moveFactor, 0.0f);
            parallaxBackgrounds.add(parallax);
        }
        moveFactor = 0.9f;
        for (int i = 0; i < CLOUD_FORGROUND_COUNT; i++) {
            x = Rand.getRange(-(getPWidth()/2), getPWidth()*3/2);
            y = Rand.getRange(registry.getBlockManager().getMapSurfaceMin(), registry.getBlockManager().getMapSurfaceMax() + getPHeight() * 3);
            ms = (float) Rand.getFloat() + 0.5f;
            parallax = new Parallax(this, registry, "Background/Cloud" + Rand.getRange(1, 5), "Cloud", x, y, camMapX, camMapY, moveFactor, ms);
            parallaxBackgrounds.add(parallax);
        }
        moveFactor = 0.75f;
        for (int i = 0; i < TREES_COUNT; i++) {
            x = Rand.getRange(-50, (int)(gameController.getMapWidth()*(1-moveFactor)+gameController.getPWidth()));
            y = Rand.getRange(registry.getBlockManager().getMapSurfaceMin() - 500, registry.getBlockManager().getMapSurfaceMin() - 50);
            parallax = new Parallax(this, registry, "Background/Tree2", "Tree", x, y, camMapX, camMapY, moveFactor, 0.0f);
            parallaxBackgrounds.add(parallax);
        }
        moveFactor = 0.65f;
        for (int i = 0; i < TREES_COUNT; i++) {
            x = Rand.getRange(-50, (int)(gameController.getMapWidth()*(1-moveFactor)+gameController.getPWidth()));
            y = Rand.getRange(registry.getBlockManager().getMapSurfaceMin() - 500, registry.getBlockManager().getMapSurfaceMin() - 50);
            parallax = new Parallax(this, registry, "Background/Tree1", "Tree", x, y, camMapX, camMapY, moveFactor, 0.0f);
            parallaxBackgrounds.add(parallax);
        }
    }

    @Override
    public void update() {
        Background background = null;

        for (int i = 0; i < backgrounds.size(); i++) {
            background = backgrounds.get(i);
            background.update();
        }

        super.update();
    }

    public void updateParallax(int x, int y) {
        Parallax parallax = null;
        int camMapX = registry.getGameController().getMapOffsetX();
        int camMapY = registry.getGameController().getMapOffsetY();
        for (int i = 0; i < parallaxBackgrounds.size(); i++) {
            parallax = parallaxBackgrounds.get(i);
            parallax.updateMapX(camMapX);
            parallax.updateMapY(camMapY);
            parallax.update();
        }
    }

    public void render(Graphics g) {
        g.setColor(new Color(118, 217, 245));
        g.fillRect(0, 0, gameController.getMapWidth(), gameController.getMapHeight());

        Background background = null;

        for (int i = 0; i < backgrounds.size(); i++) {
            background = backgrounds.get(i);
            background.render(g);
        }

        Parallax parallax = null;

        for (int i = 0; i < parallaxBackgrounds.size(); i++) {
            parallax = parallaxBackgrounds.get(i);
            parallax.render(g);
        }
    }
}