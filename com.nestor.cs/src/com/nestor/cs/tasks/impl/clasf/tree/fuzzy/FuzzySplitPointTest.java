//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// FuzzySplitPointTest.java (sed.data.clasf.tree.fuzzy)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree.fuzzy;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.SplitPoint;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.impl.tests.FuzzyTest;
import com.nestor.cs.data.impl.tests.SplitPointTest;
import com.nestor.cs.tasks.impl.preprocess.discr.TrapezoidalFuzzyRediscretizer;

public class FuzzySplitPointTest extends SplitPointTest implements FuzzyTest {

    public FuzzySplitPointTest(SplitPointTest st) {
        super(st.getSplitPoint(), st.getTestType());
    }

    public FuzzySplitPointTest(SplitPoint split) {
        super(split);
    }

    public FuzzySplitPointTest(SplitPoint split, int testType) {
        super(split, testType);
    }

    @Override
    public Test createOppositeTest() {
        return new FuzzySplitPointTest(getSplitPoint(), getOppositeTestType());
    }

    public double getClassifyProb(Object value) {
        if (value == null)
            return 0;
        if (getTestType() == GREATER_OR_EQUAL) {
            return TrapezoidalFuzzyRediscretizer.getRightMembershipLevel(
                    getSplitPoint(), (Comparable) value);
        } else if (getTestType() == LESS) {
            return TrapezoidalFuzzyRediscretizer.getLeftMembershipLevel(
                    getSplitPoint(), (Comparable) value);
        }
        return 0;
    }

    @Override
    public String toString(Attribute attr) {
        SplitPoint sp = getSplitPoint();
        String s = "P(" + attr.getName(); //$NON-NLS-1$
        if (getTestType() == GREATER_OR_EQUAL) {
            s += ">" + sp.getLeftSplitBound(); //$NON-NLS-1$
        } else {
            s += "<" + sp.getRightSplitBound(); //$NON-NLS-1$
        }
        s += ")"; //$NON-NLS-1$
        return s;
    }
}