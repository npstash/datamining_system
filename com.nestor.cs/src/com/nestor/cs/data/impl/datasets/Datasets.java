//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Datasets.java (sed.data.datasets)
// Utworzono 2005-07-30 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets;

import com.nestor.cs.data.def.ItemsetIterator;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.util.util.ArrayUtils;

public abstract class Datasets {

    public static Comparable[] getExtremumValues(ReadOnlyDataset ds,
            int attrIndex) {
        if (!ds.getAttribute(attrIndex).isSortable())
            return null;
        ItemsetIterator it = ds.iterator();
        Comparable low = null, high = null;
        while (it.hasMoreItemsets()) {
            ReadOnlyItemset item = it.nextItemset();
            Comparable other = (Comparable) item.getValue(attrIndex);
            if (other == null)
                continue;
            if (low == null)
                low = other;
            if (high == null)
                high = other;
            low = low.compareTo(other) < 0 ? low : other;
            high = high.compareTo(other) > 0 ? high : other;
        }
        return new Comparable[] { low, high };
    }

    public static int numOfEqualValuesInARow(ReadOnlyDataset ds, int attrIndex,
            int[] indexes, int startFrom, int limitIndex) {
        Object prevValue = null;
        boolean pset = false;
        int equals = 0;
        for (int i = startFrom; i <= limitIndex; i++) {
            ds.setCursorAt(indexes[i]);
            Object value = ds.getValue(attrIndex);
            if (!pset) {
                prevValue = value;
                equals = 1;
                pset = true;
            } else {
                if (Attributes.areLabelsEqual(value, prevValue))
                    equals++;
                else
                    return equals;
            }
        }
        return equals;
    }

    public static int[] remainingIndexes(int dsSize, int[] indexes) {
        int[] res = new int[dsSize - indexes.length];
        int currentIndex = 0;
        for (int i = 0; i < dsSize; i++) {
            if (!ArrayUtils.isContained(i, indexes))
                res[currentIndex++] = i;
        }
        return res;
    }

}