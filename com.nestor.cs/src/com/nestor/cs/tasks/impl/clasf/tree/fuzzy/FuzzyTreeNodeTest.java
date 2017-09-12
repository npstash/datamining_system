//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// FuzzyTreeTest.java (sed.data.clasf.tree.fuzzy)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree.fuzzy;

import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.impl.tests.FuzzyTest;
import com.nestor.cs.data.impl.tests.SplitPointTest;
import com.nestor.cs.tasks.def.TreeNodeTest;

public class FuzzyTreeNodeTest implements TreeNodeTest {

    private int attrIndex;

    private FuzzyTest[] branchTests;

    public FuzzyTreeNodeTest(TreeNodeTest test) {
        super();
        this.attrIndex = test.getTestedAttributeIndex();
        this.branchTests = new FuzzyTest[test.getBranchCount()];
        for (int i = 0; i < this.branchTests.length; i++) {
            Test t = test.getTestForBranch(i);
            if (t == null)
                continue;

            FuzzyTest ft;
            if (t instanceof SplitPointTest) {
                ft = new FuzzySplitPointTest((SplitPointTest) t);
            } else {
                ft = new FuzzyTest.TestWrapper(t);
            }
            this.branchTests[i] = ft;
        }
    }

    public FuzzyTreeNodeTest(int attrIndex, FuzzyTest test) {
        super();
        this.attrIndex = attrIndex;
        this.branchTests = new FuzzyTest[2];
        this.branchTests[BINARY_BRANCH_YES] = test;
        this.branchTests[BINARY_BRANCH_NO] = (FuzzyTest) test.createOppositeTest();
    }

    public FuzzyTreeNodeTest(int attrIndex, FuzzyTest[] branchTests) {
        super();
        this.attrIndex = attrIndex;
        this.branchTests = branchTests;
    }

    public int getBranchCount() {
        return this.branchTests.length;
    }

    public double[] getBranchProbabilities(ReadOnlyItemset it) {
        double[] probs = new double[getBranchCount()];
        Object value = it.getValue(getTestedAttributeIndex());
        for (int i = 0; i < getBranchCount(); i++) {
            FuzzyTest ft = (FuzzyTest) getTestForBranch(i);
            if (ft == null)
                continue;
            probs[i] = ft.getClassifyProb(value);
        }
        return probs;
    }

    public int getTestedAttributeIndex() {
        return this.attrIndex;
    }

    public Test getTestForBranch(int branch) {
        return this.branchTests[branch];
    }

    public int selectBranch(ReadOnlyItemset it) {
        Object value = it.getValue(this.attrIndex);
        for (int i = 0; i < this.branchTests.length; i++) {
            if (this.branchTests[i].classifies(value))
                return i;
        }
        return SELECT_NODE_CLASS;
    }

}