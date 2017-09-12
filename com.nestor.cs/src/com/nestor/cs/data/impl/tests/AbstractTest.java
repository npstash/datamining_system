//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractTest.java (sed.data.clasf)
// Utworzono 2005-07-30 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.tests;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Test;

public abstract class AbstractTest implements Test {

    private Object constraint;

    private int testType;

    public AbstractTest(Object constraint, int testType) {
        super();
        this.constraint = constraint;
        this.testType = testType;
    }

    public Object getConstraint() {
        return this.constraint;
    }

    protected int getOppositeTestType() {
        switch (getTestType()) {
        case EQUAL:
            return INEQUAL;
        case INEQUAL:
            return EQUAL;
        case GREATER:
            return LESS_OR_EQUAL;
        case GREATER_OR_EQUAL:
            return LESS;
        case LESS:
            return GREATER_OR_EQUAL;
        case LESS_OR_EQUAL:
            return GREATER;
        }
        return 0;
    }

    public int getTestType() {
        return this.testType;
    }

    protected String getTestTypeString() {
        switch (this.testType) {
        case EQUAL:
            return "="; //$NON-NLS-1$
        case INEQUAL:
            return "!="; //$NON-NLS-1$
        case GREATER:
            return ">"; //$NON-NLS-1$
        case GREATER_OR_EQUAL:
            return ">="; //$NON-NLS-1$
        case LESS:
            return "<"; //$NON-NLS-1$
        case LESS_OR_EQUAL:
            return "<="; //$NON-NLS-1$
        }
        return null;
    }

    public String toString(Attribute attr) {
        String res = attr.getName() + getTestTypeString();
        res += attr.getNameForValue(getConstraint());
        return res;
    }

    public void updateConstraint(Object constraint) {
        this.constraint = constraint;
    }
}