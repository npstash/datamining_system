//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// MultiValueTest.java (sed.data.clasf)
// Utworzono 2005-08-03 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.tests;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Test;

public class MultiTest extends AbstractTest {

    protected Test[] tests;

    public MultiTest(Test[] tests, int testType) {
        super(tests, testType);
        if ((testType != Test.EQUAL) && (testType != Test.INEQUAL))
            throw new IllegalArgumentException();
        this.tests = tests;
    }

    public boolean classifies(Object value) {
        for (int i = 0; i < this.tests.length; i++) {
            if (this.tests[i].classifies(value))
                return getTestType() == EQUAL;
        }
        return !(getTestType() == EQUAL);
    }

    public Test getSubTest(int c) {
        return this.tests[c];
    }

    @Override
    public String toString(Attribute attr) {
        String res = ""; //$NON-NLS-1$
        for (int i = 0; i < this.tests.length; i++) {
            String ts = this.tests[i].toString(attr);
            if (getTestType() == INEQUAL)
                res += "!"; //$NON-NLS-1$
            res += "(" + ts + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            if (i < this.tests.length - 1) {
                if (getTestType() == EQUAL)
                    res += " OR "; //$NON-NLS-1$
                else if (getTestType() == INEQUAL)
                    res += " AND "; //$NON-NLS-1$
            }
        }
        return res;
    }

    public Test createOppositeTest() {
        return new MultiTest(this.tests, getOppositeTestType());
    }
}