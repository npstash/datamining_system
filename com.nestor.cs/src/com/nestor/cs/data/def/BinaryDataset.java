//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// BinaryDataset.java (sed.data.binary)
// Utworzono 2005-07-29 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public interface BinaryDataset extends Dataset, BinaryReadOnlyDataset,
        BinaryItemset {

    public void addBinaryItemset(BinaryReadOnlyItemset it)
            throws IncompatibleAttributeset;

    public void addBinaryItemset(boolean[] values);

    public void appendBinary(BinaryReadOnlyDataset ds)
            throws IncompatibleAttributeset;

    public void appendBinary(BinaryReadOnlyDataset ds, int[] indexes)
            throws IncompatibleAttributeset;

}