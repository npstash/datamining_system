//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ValueSetTest.java (sed.data.clasf)
// Utworzono 2005-08-03 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.tests;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Test;

public class ValueSetTest extends AbstractTest {

    private Object[] valueSet;

    public ValueSetTest(Object[] valueSet, int testType) {
        super(valueSet, testType);
        if ((testType != Test.EQUAL) && (testType != Test.INEQUAL))
            throw new IllegalArgumentException();
        this.valueSet = valueSet;
    }

    public boolean classifies(Object value) {
        // mo�liwe �e value == null
        // jesli kt�ry� z element�w valueSet jest null,
        // wystarczy por�wnanie referencji
        for (int i = 0; i < this.valueSet.length; i++) {
            if (this.valueSet[i] == value)
                return getTestType() == EQUAL;
        }
        // �aden z element�w valueSet nie jest null
        // wi�c je�li value == null, to test nie jest spe�niony
        if (value == null)
            return getTestType() == INEQUAL;

        for (int i = 0; i < this.valueSet.length; i++) {
            if (value.equals(this.valueSet[i]))
                return getTestType() == EQUAL;
        }
        return getTestType() == INEQUAL;
    }

    public Test createOppositeTest() {
        return new ValueSetTest(this.valueSet, getOppositeTestType());
    }

    @Override
    public String toString(Attribute attr) {
        String res = attr.getName() + getTestTypeString() + "{"; //$NON-NLS-1$
        for (int i = 0; i < this.valueSet.length; i++) {
            res += attr.getNameForValue(this.valueSet[i]);
            if (i < this.valueSet.length - 1)
                if (getTestType() == EQUAL)
                    res += "|"; //$NON-NLS-1$
                else
                    res += "&"; //$NON-NLS-1$
        }
        res += "}"; //$NON-NLS-1$
        return res;
    }
}