//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ComparableTest.java (sed.data.clasf)
// Utworzono 2005-07-30 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.tests;

import com.nestor.cs.data.def.Test;

public class ComparableTest extends AbstractTest {

    public ComparableTest(Comparable constraint, int testType) {
        super(constraint, testType);
    }

    public boolean classifies(Object value) {
        if (getConstraint() == null) {
            boolean b = (value == null);
            if (getTestType() == Test.EQUAL)
                return b;
            else
                return !b;
        } else {
            if (!(value instanceof Comparable))
                return false;
            switch (getTestType()) {
            case Test.EQUAL:
                return getConstraint().equals(value);
            case Test.INEQUAL:
                return !getConstraint().equals(value);
            case Test.GREATER:
                return ((Comparable) getConstraint())
                        .compareTo(value) < 0;
            case Test.GREATER_OR_EQUAL:
                return ((Comparable) getConstraint())
                        .compareTo(value) <= 0;
            case Test.LESS:
                return ((Comparable) getConstraint())
                        .compareTo(value) > 0;
            case Test.LESS_OR_EQUAL:
                return ((Comparable) getConstraint())
                        .compareTo(value) >= 0;
            }
            return false;
        }
    }

    public Test createOppositeTest() {
        return new ComparableTest(getComparable(), getOppositeTestType());
    }

    public Comparable getComparable() {
        return (Comparable) getConstraint();
    }

}