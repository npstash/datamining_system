//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// GravRectangle.java (javan.util.grav)
// Utworzono 2005-07-26 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util.grav;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class GravRectangle extends Rectangle2D.Double implements GravShape {

    private boolean immobileX = false;

    private boolean immobileY = false;

    public GravRectangle() {
        super();
    }

    public GravRectangle(double arg0, double arg1, double arg2, double arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public GravRectangle(Rectangle rect) {
        super(rect.x, rect.y, rect.width, rect.height);
    }

    public double getIntersectionWeight(GravShape gs) {
        if (gs instanceof Rectangle2D) {
            Rectangle2D r2d = (Rectangle2D) gs;
            Rectangle2D inters = r2d.createIntersection(this);
            if (!inters.isEmpty())
                return inters.getWidth() * getHeight();
        }
        return 0;
    }

    public boolean isImmobileX() {
        return this.immobileX;
    }

    public boolean isImmobileY() {
        return this.immobileY;
    }

    public void move(double dx, double dy) {
        Rectangle r = getBounds();
        if (isImmobileX())
            dx = 0;
        if (isImmobileY())
            dy = 0;
        setRect(getX() + dx, getY() + dy, getWidth(), getHeight());
    }

    public void moveWithFit(Rectangle fit, double dx, double dy) {
        double newx = this.x + dx;
        double newy = this.y + dy;

        if ((newx < fit.x) || (newx + this.width > fit.x + fit.width)) {
            if ((newx < fit.x) && (newx + this.width > fit.x + fit.width))
                dx = (Math.abs(fit.x - this.x) - Math.abs(fit.x + fit.width - this.x
                        - this.width)) / 2;
            else if (newx < fit.x)
                dx = fit.x - this.x;
            else if (newx + this.width > fit.x + fit.width)
                dx = fit.x + fit.width - this.x - this.width;
        }

        if ((newy < fit.y) || (newy + this.height > fit.y + fit.height)) {
            if ((newy < fit.y) && (newy + this.height > fit.y + fit.height))
                dy = (Math.abs(fit.y - this.y) - Math.abs(fit.y + fit.height - this.y
                        - this.height)) / 2;
            else if (newy < fit.y)
                dy = fit.y - this.y;
            else if (newy + this.height > fit.y + fit.height)
                dy = fit.y + fit.height - this.y - this.height;
        }

        if (isImmobileX())
            dx = 0;
        if (isImmobileY())
            dy = 0;
        move(dx, dy);
    }

    public void setImmobile(boolean immobile) {
        this.immobileX = immobile;
        this.immobileY = immobile;
    }

    public void setImmobile(boolean immobileX, boolean immobileY) {
        this.immobileX = immobileX;
        this.immobileY = immobileY;
    }
}