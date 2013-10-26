package com.weem.epicinventor.utility;

import java.awt.*;

public class Bspline
{
    int STEPS = 8;
    protected Polygon ptsIn;

    public Bspline(int steps)
    {
        EIError.debugMsg("Bspline", EIError.ErrorLevel.Notice);
        STEPS = steps;
        ptsIn = new Polygon();
    }

    // square of an int
    static int sqr(int x)
    {
        return x * x;
    }

    /** add a control point, return index of new control point */
    public void addPoint(int x, int y)
    {
        ptsIn.addPoint(x, y);
    }

    /** remove selected control point */
    public void removePoints()
    {
        ptsIn = new Polygon();
    }

    // the basis function for a cubic B spline
    float b(int i, float t)
    {
        switch(i)
        {
            case -2:
                return (((-t + 3) * t - 3) * t + 1) / 6;
            case -1:
                return (((3 * t - 6) * t) * t + 4) / 6;
            case 0:
                return (((-3 * t + 3) * t + 3) * t + 1) / 6;
            case 1:
                return (t * t * t) / 6;
        }
        return 0; //we only get here if an invalid i is specified
    }

    //evaluate a point on the B spline
    Point p(int i, float t)
    {
        float px = 0;
        float py = 0;
        for (int j = -2; j <= 1; j++)
        {
            px += b(j, t) * ptsIn.xpoints[i + j];
            py += b(j, t) * ptsIn.ypoints[i + j];
        }
        return new Point((int) Math.round(px), (int) Math.round(py));
    }

    public int[] getPoints(int size)
    {
        EIError.debugMsg("Start", EIError.ErrorLevel.Notice);
        int[] sp = new int[size];
        Point q = p(2, 0);
        int y = 0;
        for(int i = 0; i < STEPS+1; i++)
        {
            sp[i] = q.y;
        }
        for(int i = 2; i < ptsIn.npoints - 1; i++)
        {
            for (int j = 1; j <= STEPS; j++)
            {
                q = p(i, j / (float) STEPS);
                y = (i-1)*STEPS+j;
                if(y >= size)
                {
                    y = size-1;
                }
                sp[y] = q.y;
            }
        }
        int lastY = y;
        for(; y < size; y++)
        {
            sp[y] = sp[lastY];
        }
        EIError.debugMsg("End", EIError.ErrorLevel.Notice);
        return sp;
    }
}
