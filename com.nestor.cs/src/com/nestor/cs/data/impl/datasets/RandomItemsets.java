//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// RandomItemsets.java (sed.data.datasets)
// Utworzono 2005-05-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets;

import java.util.Random;

import com.nestor.cs.data.def.ReadOnlyDataset;

public class RandomItemsets extends AbstractItemsetIndexesSelector {

    private int randomSubdatasetSize;

    private boolean repeats;

    private Long seed;

    public RandomItemsets(int randomSubdatasetSize, boolean repeats, Long seed) {
        super();
        this.randomSubdatasetSize = randomSubdatasetSize;
        this.repeats = repeats;
        this.seed = seed;
    }

    public int[] selectItemsetIndexes(ReadOnlyDataset ds, int startIndex,
            int endIndex) {
        int[] indexes = new int[this.randomSubdatasetSize];
        int currentIndex = 0;
        boolean selected[] = null;
        int probeSize = endIndex - startIndex + 1;
        if ((!this.repeats) && (probeSize < this.randomSubdatasetSize)) {
            this.repeats = true;
        }
        if (!this.repeats) {
            selected = new boolean[probeSize];
            for (int i = 0; i < selected.length; i++) {
                selected[i] = false;
            }
        }
        Random rnd = (this.seed == null) ? new Random() : new Random(this.seed
                .longValue());
        while (currentIndex < this.randomSubdatasetSize) {
            boolean selectionOK = false;
            int selIndex = -1;
            while (!selectionOK) {
                selIndex = rnd.nextInt(probeSize);
                if (!this.repeats) {
                    if (selected[selIndex]) {
                        continue;
                    } else {
                        selectionOK = true;
                        selected[selIndex] = true;
                    }
                } else {
                    selectionOK = true;
                }
            }
            if (selIndex == -1)
                throw new Error();
            indexes[currentIndex++] = selIndex;
        }
        if (startIndex > 0)
            for (int i = 0; i < indexes.length; i++)
                indexes[i] += startIndex;
        return indexes;
    }
}