//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// FuzzyTest.java (sed.data.clasf.tree.fuzzy)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.tests;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Test;

public interface FuzzyTest extends Test {

    public static class TestWrapper implements FuzzyTest {

        private Test disjointTest;

        public TestWrapper(Test disjointTest) {
            super();
            this.disjointTest = disjointTest;
        }

        public boolean classifies(Object value) {
            return disjointTest.classifies(value);
        }

        public Test createOppositeTest() {
            return new TestWrapper(disjointTest.createOppositeTest());
        }

        public double getClassifyProb(Object value) {
            if (value == null)
                return 0;
            return disjointTest.classifies(value) ? 1 : 0;
        }

        public Object getConstraint() {
            return disjointTest.getConstraint();
        }

        public int getTestType() {
            return disjointTest.getTestType();
        }

        public String toString(Attribute attr) {
            return disjointTest.toString(attr);
        }
    }

    public double getClassifyProb(Object value);

}