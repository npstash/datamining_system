//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// RangeItemsets.java (sed.data.datasets)
// Utworzono 2005-05-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets;

import com.nestor.cs.data.def.ReadOnlyDataset;

public class RangeItemsets extends AbstractItemsetIndexesSelector {

    public static int[] createIntArray(int fromIncl, int toIncl) {
        int[] indexes = new int[toIncl - fromIncl + 1];
        for (int i = fromIncl; i <= toIncl; i++) {
            indexes[i - fromIncl] = i;
        }
        return indexes;
    }

    public RangeItemsets() {
        super();
    }

    public int[] selectItemsetIndexes(ReadOnlyDataset ds, int startIndex,
            int endIndex) {
        if (ds.size() == 0) {
            return new int[] {};
        }
        int from = startIndex, to = endIndex;
        if (startIndex < 0)
            from = 0;
        if (endIndex >= ds.size()) {
            to = ds.size() - 1;
        }
        if (endIndex < 0)
            to = 0;
        if (startIndex >= ds.size()) {
            from = ds.size() - 1;
        }
        if (to < from) {
            int t = to;
            to = from;
            from = t;
        }
        return createIntArray(from, to);
    }

}