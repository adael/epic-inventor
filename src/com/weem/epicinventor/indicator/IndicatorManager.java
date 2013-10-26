package com.weem.epicinventor.indicator;

import com.weem.epicinventor.*;
import com.weem.epicinventor.drop.*;

import java.awt.*;
import java.util.ArrayList;

public class IndicatorManager extends Manager {

    private ArrayList<Indicator> indicators;
    private final static int DROP_SPACING = 10;
    private final static int DROP_WIDTH = 32;

    public IndicatorManager(GameController gc, Registry rg) {
        super(gc, rg);

        indicators = new ArrayList<Indicator>();
    }

    public void createIndicator(int x, int y, String text) {
        indicators.add(new Indicator(registry, this, x, y, "", text, Color.RED, false));
    }

    public void createXPIndicator(int x, int y, String text) {
        indicators.add(new Indicator(registry, this, x, y, "", text, Color.MAGENTA, true));
    }

    public void createNegativeXPIndicator(int x, int y, String text) {
        indicators.add(new Indicator(registry, this, x, y, "", text, Color.RED, true));
    }

    public void createIndicator(int x, int y, String text, Color c) {
        indicators.add(new Indicator(registry, this, x, y, "", text, c, false));
    }

    public void createImageIndicator(int x, int y, String image) {
        indicators.add(new Indicator(registry, this, x, y, "Items/" + image, "", Color.RED, false));
    }

    public void createIndicator(int x, int y, ArrayList<Drop> drops) {
        int totalWidth = (drops.size() * DROP_WIDTH) + ((drops.size() - 1) * DROP_SPACING);

        if (drops.size() > 0) {
            x -= (totalWidth / 2);
            for (int i = 0; i < drops.size(); i++) {
                Drop d = drops.get(i);
                int newXPos = x + (i * DROP_WIDTH) + (i * DROP_SPACING);
                indicators.add(new Indicator(registry, this, newXPos, y, "Items/" + d.getItemName(), "", Color.RED, false));
            }
        }
    }

    @Override
    public void update() {
        super.update();

        Indicator indicator = null;

        for (int i = 0; i < indicators.size(); i++) {
            indicator = indicators.get(i);

            if (indicator != null) {
                indicator.update();

                if (indicator.isDirty()) {
                    indicators.remove(i);
                }
            }
        }
    }

    public void render(Graphics g) {
        Indicator indicator = null;

        for (int i = (indicators.size() - 1); i >= 0; i--) {
            indicator = indicators.get(i);
            indicator.render(g);
        }
    }
}