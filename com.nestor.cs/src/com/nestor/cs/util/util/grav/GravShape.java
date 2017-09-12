//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// GravShape.java (javan.util.grav)
// Utworzono 2005-07-26 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util.grav;

import java.awt.Rectangle;

public interface GravShape {

    public double getCenterX();

    public double getCenterY();

    public void move(double dx, double dy);

    public void moveWithFit(Rectangle fit, double dx, double dy);

    public double getIntersectionWeight(GravShape gs);

}