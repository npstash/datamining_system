//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// TestSelector.java (sed.data.itemsets)
// Utworzono 2005-07-31 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.tests;

import com.nestor.cs.data.def.ItemsetSelector;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.def.Test;

public class TestSelector implements ItemsetSelector {

    private int attributeIndex;

    private Test test;

    public TestSelector(int attributeIndex, Test test) {
        super();
        this.attributeIndex = attributeIndex;
        this.test = test;
    }

    public boolean selectItemset(ReadOnlyItemset it) {
        return this.test.classifies(it.getValue(this.attributeIndex));
    }

}