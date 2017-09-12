//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// RuleItemset.java (sed.data.clasf.cba)
// Utworzono 2005-08-15 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.cba;

import java.util.ArrayList;

class RuleItemset {

    protected ArrayList ancestors = null;

    protected ArrayList supersets = new ArrayList();

    protected boolean[] condSet;

    protected int condSuppCount;

    protected int ruleSuppCount;

    protected int y;

    protected RuleItemset(boolean[] condSet, int condSuppCount, int y,
            int ruleSuppCount) {
        this.condSet = condSet;
        this.condSuppCount = condSuppCount;
        this.y = y;
        this.ruleSuppCount = ruleSuppCount;
    }

    @Override
    public String toString() {
        String s = ""; //$NON-NLS-1$
        for (int i = 0; i < this.condSet.length; i++) {
            s += (this.condSet[i]) ? "1" : "0"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        s += " condCnt=" + this.condSuppCount + " y=" + this.y + " ruleCnt=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + this.ruleSuppCount;
        return s;
    }
}