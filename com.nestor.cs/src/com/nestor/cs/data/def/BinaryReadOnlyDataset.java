//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// BinaryReadOnlyDataset.java (sed.data.binary)
// Utworzono 2005-07-29 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public interface BinaryReadOnlyDataset extends ReadOnlyDataset,
        BinaryReadOnlyItemset {

    public BinaryReadOnlyItemset getBinaryItemset(int i);

}