//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SelectedItemsets.java (sed.data.datasets)
// Utworzono 2005-05-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets;

import java.util.ArrayList;

import com.nestor.cs.data.def.ItemsetSelector;
import com.nestor.cs.data.def.ReadOnlyDataset;

public class SelectedItemsets extends AbstractItemsetIndexesSelector {

    private ItemsetSelector is;

    public SelectedItemsets(ItemsetSelector is) {
        super();
        this.is = is;
    }

    public int[] selectItemsetIndexes(ReadOnlyDataset ds, int startIndex,
            int endIndex) {
        ArrayList al = new ArrayList();
        for (int i = startIndex; i <= endIndex; i++) {
            ds.setCursorAt(i);
            if (this.is.selectItemset(ds)) {
                al.add(new Integer(i));
            }
        }
        int[] indexes = new int[al.size()];
        for (int i = 0; i < al.size(); i++) {
            indexes[i] = ((Integer) al.get(i)).intValue();
        }
        return indexes;
    }

}