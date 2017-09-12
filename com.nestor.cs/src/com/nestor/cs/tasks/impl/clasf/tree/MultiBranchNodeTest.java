//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SingleAttributeTest.java (sed.data.clasf.tree)
// Utworzono 2005-08-03 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree;

import com.nestor.cs.data.def.Test;

public class MultiBranchNodeTest extends AbstractTreeNodeTest {

    private Test[] branchTests;

    public MultiBranchNodeTest(int attrIndex, Test[] branchTests) {
        super(attrIndex);
        this.branchTests = branchTests;
    }

    public int getBranchCount() {
        return this.branchTests.length;
    }

    public Test[] getBranchTests() {
        return this.branchTests;
    }

    public Test getTestForBranch(int branch) {
        if ((branch < 0) || (branch >= this.branchTests.length))
            return null;
        return this.branchTests[branch];
    }

    @Override
    public int selectBranch(Object value) {
        for (int i = 0; i < this.branchTests.length; i++) {
            if (this.branchTests[i].classifies(value))
                return i;
        }
        return SELECT_NODE_CLASS;
    }
}