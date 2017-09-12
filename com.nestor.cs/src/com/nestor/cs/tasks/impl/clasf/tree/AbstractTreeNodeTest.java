//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractTreeNodeTest.java (sed.data.clasf.tree)
// Utworzono 2005-08-04 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree;

import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.tasks.def.TreeNodeTest;

public abstract class AbstractTreeNodeTest implements TreeNodeTest {

    private int attributeIndex;

    public AbstractTreeNodeTest(int attributeIndex) {
        super();
        this.attributeIndex = attributeIndex;
    }

    public int getTestedAttributeIndex() {
        return this.attributeIndex;
    }

    public abstract int selectBranch(Object value);

    public int selectBranch(ReadOnlyItemset it) {
        return selectBranch(it.getValue(this.attributeIndex));
    }
}