//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractItemsetIndexesSelector.java (sed.data.datasets)
// Utworzono 2005-05-12 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets;

import com.nestor.cs.data.def.ItemsetIndexesSelector;
import com.nestor.cs.data.def.ReadOnlyDataset;

public abstract class AbstractItemsetIndexesSelector implements
        ItemsetIndexesSelector {

    public int[] selectItemsetIndexes(ReadOnlyDataset ds) {
        return selectItemsetIndexes(ds, 0, ds.size() - 1);
    }

}