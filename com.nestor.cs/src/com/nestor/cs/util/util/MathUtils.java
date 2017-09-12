//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// MathUtils.java (javan.util)
// Utworzono 2005-05-23 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util;

public abstract class MathUtils {

    public static double plog(double x, double base) {
        if (x == 0)
            return 0;
        else
            return Math.log(x) / Math.log(base);
    }

}