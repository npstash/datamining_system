//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SplitPoint.java (sed.data.conversion.discretizers)
// Utworzono 2005-08-01 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public class SplitPoint implements Comparable {

    public static boolean isExampleInBounds(Comparable obj,
            Comparable lowBound, Comparable highBound) {
        if (lowBound == null) {
            if (highBound == null)
                return true;
            else
                return (obj.compareTo(highBound) <= 0);
        } else {
            if (highBound == null)
                return (obj.compareTo(lowBound) >= 0);
            else
                return ((obj.compareTo(lowBound) >= 0) && (obj
                        .compareTo(highBound) <= 0));
        }
    }

    private Comparable lastLeft, firstRight, split;

    public SplitPoint(Comparable lastLeftExclude, Comparable firstRightInclude) {
        super();
        this.lastLeft = lastLeftExclude;
        this.firstRight = firstRightInclude;
        try {
            double l = ((Double) lastLeftExclude).doubleValue();
            double r = ((Double) firstRightInclude).doubleValue();
            this.split = new Double((l + r) / 2);
        } catch (ClassCastException e) {
            this.split = firstRightInclude;
        }
    }

    public int compareTo(Object arg0) {
        return getSplitPoint().compareTo(((SplitPoint) arg0).getSplitPoint());
    }

    public Comparable getLeftSplitBound() {
        return this.lastLeft;
    }

    public Comparable getRightSplitBound() {
        return this.firstRight;
    }

    public Comparable getSplitPoint() {
        return this.split;
    }

    @Override
    public String toString() {
        return getSplitPoint().toString();
    }
}