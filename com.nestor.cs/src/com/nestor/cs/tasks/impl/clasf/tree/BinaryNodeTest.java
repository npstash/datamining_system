//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SingleAttributeTest.java (sed.data.clasf.tree)
// Utworzono 2005-08-02 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree;

import com.nestor.cs.data.def.Test;

public class BinaryNodeTest extends AbstractTreeNodeTest {

    private Test t;

    public BinaryNodeTest(int attrIndex, Test t) {
        super(attrIndex);
        this.t = t;
    }

    public int getBranchCount() {
        return 2;
    }

    public Test getTestForBranch(int branch) {
        if ((branch < 0) || (branch > 1))
            return null;
        if (branch == BINARY_BRANCH_YES)
            return this.t;
        else
            return this.t.createOppositeTest();
    }

    @Override
    public int selectBranch(Object value) {
        if (this.t.classifies(value))
            return BINARY_BRANCH_YES;
        else
            return BINARY_BRANCH_NO;
    }
}