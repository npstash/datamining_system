//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ItemsetIndexesSelector.java (sed.data.datasets)
// Utworzono 2005-05-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public interface ItemsetIndexesSelector {

    public int[] selectItemsetIndexes(ReadOnlyDataset ds);

    public int[] selectItemsetIndexes(ReadOnlyDataset ds, int startIndex,
            int endIndex);
}