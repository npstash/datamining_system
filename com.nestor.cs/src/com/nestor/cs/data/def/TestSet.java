//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// TestSet.java (sed.data.clasf)
// Utworzono 2005-08-02 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

import java.io.Serializable;

public class TestSet implements Serializable {

    public static final int ALTERNATIVE = 3;

    public static final int CONJUNCTION = 1;

    public static final int DISJUNCTION = 2;

    protected AttributeSet attributeSet;

    // 0 - 1
    protected double support = 0;

    private Test[] tests = null;

    private int testSetType = CONJUNCTION;

    public TestSet() {
        super();
    }

    public TestSet(AttributeSet attributeSet) {
        this.attributeSet = attributeSet;
        this.tests = new Test[attributeSet.getAttributeCount()];
    }

    public TestSet(AttributeSet attributeSet, int testSetType) {
        this.attributeSet = attributeSet;
        this.tests = new Test[attributeSet.getAttributeCount()];
        this.testSetType = testSetType;
    }

    public boolean classifies(ReadOnlyItemset it) {
        if (this.testSetType == CONJUNCTION) {
            for (int i = 0; i < this.tests.length; i++) {
                if (this.tests[i] != null) {
                    Test t = this.tests[i];
                    Object value = it.getValue(i);
                    if (!t.classifies(value))
                        return false;
                }
            }
            return true;
        } else if (this.testSetType == DISJUNCTION) {
            int testsOK = 0;
            for (int i = 0; i < this.tests.length; i++) {
                if (this.tests[i] != null) {
                    Test t = this.tests[i];
                    Object value = it.getValue(i);
                    if (t.classifies(value))
                        testsOK++;
                    if (testsOK > 1)
                        return false;
                }
            }
            return (testsOK == 1);
        } else if (this.testSetType == ALTERNATIVE) {
            for (int i = 0; i < this.tests.length; i++) {
                if (this.tests[i] != null) {
                    Test t = this.tests[i];
                    Object value = it.getValue(i);
                    if (t.classifies(value))
                        return true;
                }
            }
            return false;
        }
        return false;
    }

    public double getSupport() {
        return this.support;
    }

    public Test getTest(int attributeIndex) {
        return this.tests[attributeIndex];
    }

    public int getTestSetLength() {
        int res = 0;
        for (int i = 0; i < this.tests.length; i++) {
            if (this.tests[i] != null)
                res++;
        }
        return res;
    }

    public int getTestSetType() {
        return this.testSetType;
    }

    public void setAttributeSet(AttributeSet attributeSet)
            throws IncompatibleAttributeset {
        if (this.attributeSet != null) {
            IncompatibleAttributeset.checkEquality(this.attributeSet,
                    attributeSet);
        }
        this.attributeSet = attributeSet;
    }

    public void setSupport(double support) {
        this.support = support;
    }

    public void setSupport(ReadOnlyDataset ds) {
        if (ds.getClassLabelIndex() != this.attributeSet.getClassLabelIndex())
            return;
        int clAttrIndex = this.attributeSet.getClassLabelIndex();
        Attribute clAttr = this.attributeSet.getAttribute(clAttrIndex);
        int totalCount = 0;
        for (int i = 0; i < ds.size(); i++) {
            ds.setCursorAt(i);
            if (classifies(ds)) {
                totalCount++;
            }
        }
        this.support = (double) totalCount / (double) ds.size();
    }

    public void setTest(int forAttribute, Test t) {
        this.tests[forAttribute] = t;
    }

    @Override
    public String toString() {
        String res = ""; //$NON-NLS-1$
        boolean firstTest = true;
        for (int i = 0; i < this.attributeSet.getAttributeCount(); i++) {
            if (this.tests[i] != null) {
                if (!firstTest)
                    res += " + "; //$NON-NLS-1$
                res += this.tests[i].toString(this.attributeSet.getAttribute(i));
                firstTest = false;
            }
        }
        return res;
    }

}