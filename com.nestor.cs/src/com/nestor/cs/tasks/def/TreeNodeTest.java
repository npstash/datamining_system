//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// TreeNodeTest.java (sed.data.clasf.tree)
// Utworzono 2005-08-02 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.def;

import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.def.Test;

public interface TreeNodeTest {

    public static final int BINARY_BRANCH_NO = 1;

    public static final int BINARY_BRANCH_YES = 0;

    public static final int SELECT_NODE_CLASS = -1;

    public int getBranchCount();

    public int getTestedAttributeIndex();

    public Test getTestForBranch(int branch);

    public int selectBranch(ReadOnlyItemset it);
}