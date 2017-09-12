//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SortedItemsets.java (sed.data.datasets)
// Utworzono 2005-05-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets;

import java.util.ArrayList;

import com.nestor.cs.data.def.ReadOnlyDataset;

public class SortedItemsets extends AbstractItemsetIndexesSelector {

    private int ascending;

    private int sortBy;

    public SortedItemsets(int sortBy, boolean ascending) {
        super();
        this.ascending = ascending ? 1 : -1;
        this.sortBy = sortBy;
    }

    public int[] selectItemsetIndexes(ReadOnlyDataset ds, int startIndex,
            int endIndex) {
        if (this.sortBy >= ds.getAttributeCount()) {
            return null;
        }
        int setSize = endIndex - startIndex + 1;
        ArrayList al = new ArrayList(setSize);
        boolean used[] = new boolean[setSize];
        for (int i = 0; i < used.length; i++)
            used[i] = false;
        int totalUnused = used.length;
        while (totalUnused > 0) {
            Comparable maxValue = null;
            int maxIndex = -1;
            for (int i = 0; i < used.length; i++)
                if (!used[i]) {
                    ds.setCursorAt(i + startIndex);
                    Comparable nextValue = (Comparable) ds.getValue(this.sortBy);
                    if (nextValue == null) {
                        totalUnused--;
                        used[i] = true;
                    } else if (maxValue == null) {
                        maxValue = nextValue;
                        maxIndex = i;
                    } else {
                        maxValue = maxValue.compareTo(nextValue) * this.ascending > 0 ? nextValue
                                : maxValue;
                        maxIndex = maxValue == nextValue ? i : maxIndex;
                    }
                }
            if (maxIndex >= 0) {
                used[maxIndex] = true;
                totalUnused--;
                al.add(new Integer(maxIndex + startIndex));
            }
        }
        int[] indexes = new int[al.size()];
        for (int i = 0; i < indexes.length; i++)
            indexes[i] = ((Integer) al.get(i)).intValue();
        return indexes;
    }

}