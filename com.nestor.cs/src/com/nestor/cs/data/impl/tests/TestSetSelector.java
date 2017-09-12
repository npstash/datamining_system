//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// TestSetSelector.java (sed.data.clasf.tests)
// Utworzono 2005-08-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.tests;

import com.nestor.cs.data.def.ItemsetSelector;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.def.TestSet;

public class TestSetSelector implements ItemsetSelector {

    private TestSet test;

    private TestSet[] testSets;

    private int testSetType = TestSet.CONJUNCTION;

    public TestSetSelector(TestSet test) {
        super();
        this.test = test;
    }

    public TestSetSelector(TestSet tests[]) {
        super();
        this.testSets = tests;
    }

    public TestSetSelector(TestSet testSets[], int testSetType) {
        super();
        this.testSets = testSets;
        this.testSetType = testSetType;
    }

    public boolean selectItemset(ReadOnlyItemset it) {
        if (this.test != null) {
            return this.test.classifies(it);
        } else if (this.testSets != null) {
            if (this.testSetType == TestSet.CONJUNCTION) {
                for (int i = 0; i < this.testSets.length; i++) {
                    if (this.testSets[i] != null) {
                        TestSet t = this.testSets[i];
                        if (!t.classifies(it))
                            return false;
                    }
                }
                return true;
            } else if (this.testSetType == TestSet.DISJUNCTION) {
                int testsOK = 0;
                for (int i = 0; i < this.testSets.length; i++) {
                    if (this.testSets[i] != null) {
                        TestSet t = this.testSets[i];
                        if (t.classifies(it))
                            testsOK++;
                        if (testsOK > 1)
                            return false;
                    }
                }
                return (testsOK == 1);
            } else if (this.testSetType == TestSet.ALTERNATIVE) {
                for (int i = 0; i < this.testSets.length; i++) {
                    if (this.testSets[i] != null) {
                        TestSet t = this.testSets[i];
                        if (t.classifies(it))
                            return true;
                    }
                }
                return false;
            }
            return false;
        } else
            return false;
    }

}