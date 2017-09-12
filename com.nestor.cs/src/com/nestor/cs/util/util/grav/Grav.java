//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Grav.java (javan.util.grav)
// Utworzono 2005-07-26 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util.grav;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Random;

public class Grav extends Observable {

    public static double getCurrentIntersectionWeights(GravShape[] shapes) {
        double currentWeight = 0;
        for (int i = 0; i < shapes.length - 1; i++) {
            for (int j = i + 1; j < shapes.length; j++) {
                currentWeight += shapes[i].getIntersectionWeight(shapes[j]);
            }
        }
        return currentWeight;
    }

    private int delay = 0;

    private Rectangle fit;

    private Collection gravShapes;

    private int maxIterations = 0;

    private boolean moveAll = false;

    private boolean notify = false;

    private Random rnd = new Random();

    private int step;

    private boolean tillZero = false;

    public Grav(Collection gravShapes, Rectangle fit, int step) {
        super();
        this.gravShapes = gravShapes;
        this.fit = fit;
        this.step = step;
    }

    public Point2D.Double getShapePushDelta(GravShape shape) {
        Point2D.Double delta = new Point2D.Double(0, 0);
        int shapeIndex = this.gravShapes.size(), currentIndex = 0;
        for (Iterator iter = this.gravShapes.iterator(); iter.hasNext();) {
            GravShape gs = (GravShape) iter.next();
            if (gs == shape) {
                shapeIndex = currentIndex;
                continue;
            }
            double intersW = gs.getIntersectionWeight(shape);
            if ((intersW > 0) || this.moveAll) {
                double dx = -(gs.getCenterX() - shape.getCenterX());
                double dy = -(gs.getCenterY() - shape.getCenterY());
                double dist = Math.sqrt((dx * dx) + (dy * dy));
                if (dist != 0) {
                    delta.x += this.step / dist * dx;
                    delta.y += this.step / dist * dy;
                } else {
                    double sdx;
                    double sdy;
                    if (shapeIndex > currentIndex) {
                        sdx = 1;
                        sdy = 1;
                    } else {
                        sdx = -1;
                        sdy = -1;
                    }
                    delta.x += this.step * sdx;
                    delta.y += this.step * sdy;
                }
            }
            currentIndex++;
        }
        return delta;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setFit(Rectangle fit) {
        this.fit = fit;
    }

    public void setGravShapes(Collection gravShapes) {
        this.gravShapes = gravShapes;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public void setMoveAll(boolean moveAll) {
        this.moveAll = moveAll;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void setTillZero(boolean tillZero) {
        this.tillZero = tillZero;
    }

    public void startPushAlgorithm() {
        GravShape[] shapes = new GravShape[this.gravShapes.size()];
        this.gravShapes.toArray(shapes);

        double undoX[] = null;
        double undoY[] = null;
        if (!this.tillZero) {
            undoX = new double[shapes.length];
            undoY = new double[shapes.length];
        }
        Point2D.Double delta[] = new Point2D.Double[shapes.length];

        boolean loop = true;
        double lastWeight = Double.MAX_VALUE;
        int iteration = this.maxIterations;
        do {
            for (int i = 0; i < shapes.length; i++) {
                delta[i] = getShapePushDelta(shapes[i]);
            }
            for (int i = 0; i < shapes.length; i++) {
                if (!this.tillZero) {
                    undoX[i] = shapes[i].getCenterX();
                    undoY[i] = shapes[i].getCenterY();
                }
                if (this.fit != null) {
                    shapes[i].moveWithFit(this.fit, delta[i].x, delta[i].y);
                } else {
                    shapes[i].move(delta[i].x, delta[i].y);
                }
                if (!this.tillZero) {
                    undoX[i] = shapes[i].getCenterX() - undoX[i];
                    undoY[i] = shapes[i].getCenterY() - undoY[i];
                }
            }

            double weight = getCurrentIntersectionWeights(shapes);
            if (weight == 0)
                loop = false;

            if (!this.tillZero) {
                if (weight > lastWeight) {
                    for (int i = 0; i < shapes.length; i++) {
                        shapes[i].move(-undoX[i], -undoY[i]);
                    }
                    loop = false;
                }
                lastWeight = weight;
            }

            if (this.notify) {
                setChanged();
                notifyObservers();
            }

            if (this.delay > 0) {
                try {
                    Thread.sleep(this.delay);
                } catch (InterruptedException e) {
                }
            }

            iteration--;
            if (iteration == 0)
                loop = false;

        } while (loop);
    }

}