//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// BinaryExtremalValues.java (sed.data.clasf.jep)
// Utworzono 2005-07-29 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets.binary;

import com.nestor.cs.data.def.BinaryReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.datasets.AbstractItemsetIndexesSelector;

public class MaxBinaryItemsets extends AbstractItemsetIndexesSelector {

    protected boolean searchForMinItemsets = false;

    public MaxBinaryItemsets() {
        super();
    }

    public MaxBinaryItemsets(boolean reverse) {
        super();
        this.searchForMinItemsets = reverse;
    }

    public int[] selectItemsetIndexes(ReadOnlyDataset ds, int startIndex,
            int endIndex) {
        if (!(ds instanceof BinaryReadOnlyDataset))
            return new int[0];
        BinaryReadOnlyDataset bds = (BinaryReadOnlyDataset) ds;

        boolean[] possibleMax = new boolean[endIndex - startIndex + 1];
        for (int i = 0; i < possibleMax.length; i++)
            possibleMax[i] = true;

        for (int i = 0; i < possibleMax.length; i++) {
            if (!possibleMax[i])
                continue;
            for (int j = i + 1; j < possibleMax.length; j++) {
                if (!possibleMax[j])
                    continue;
                bds.setCursorAt(i + startIndex);
                boolean[] vi = bds.getBinaryValues();
                bds.setCursorAt(j + startIndex);
                boolean[] vj = bds.getBinaryValues();
                int rel = Operators.getRelation(vi, vj);

                if (this.searchForMinItemsets) {
                    if ((rel & Operators.EQUAL_OR_SUBSET) != 0) {
                        possibleMax[j] = false;
                        continue;
                    } else if ((rel & Operators.SUPERSET) != 0) {
                        possibleMax[i] = false;
                        break;
                    }
                } else {
                    if ((rel & Operators.EQUAL_OR_SUBSET) != 0) {
                        possibleMax[i] = false;
                        break;
                    } else if ((rel & Operators.SUPERSET) != 0) {
                        possibleMax[j] = false;
                        continue;
                    }
                }
            }
        }

        int maxCount = 0;
        for (int i = 0; i < possibleMax.length; i++)
            if (possibleMax[i])
                maxCount++;

        int[] res = new int[maxCount];
        int currentIndex = 0;
        for (int i = 0; i < possibleMax.length; i++)
            if (possibleMax[i])
                res[currentIndex++] = i + startIndex;

        return res;
    }

}