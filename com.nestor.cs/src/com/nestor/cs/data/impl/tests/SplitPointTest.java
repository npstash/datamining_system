//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SplitPointTest.java (sed.data.clasf.tree)
// Utworzono 2005-08-02 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.tests;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.SplitPoint;
import com.nestor.cs.data.def.Test;

public class SplitPointTest extends AbstractTest {

    private SplitPoint splitPoint;

    public SplitPointTest(SplitPoint split) {
        this(split, GREATER_OR_EQUAL);
    }

    public SplitPointTest(SplitPoint split, int testType) {
        super(split.getSplitPoint(), testType);
        if ((testType != Test.GREATER_OR_EQUAL) && (testType != Test.LESS))
            throw new IllegalArgumentException();
        this.splitPoint = split;
    }

    public boolean classifies(Object value) {
        if (value == null)
            return false;
        if (getTestType() == GREATER_OR_EQUAL) {
            return this.splitPoint.getSplitPoint().compareTo(value) <= 0;
        } else {
            return this.splitPoint.getSplitPoint().compareTo(value) > 0;
        }
    }

    public Test createOppositeTest() {
        return new SplitPointTest(getSplitPoint(), getOppositeTestType());
    }

    public SplitPoint getSplitPoint() {
        return this.splitPoint;
    }

    @Override
    public String toString(Attribute attr) {
        return attr.getName() + getTestTypeString() + getConstraint();
    }

}