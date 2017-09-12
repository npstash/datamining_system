//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// BinaryItemset.java (sed.data.binary)
// Utworzono 2005-07-29 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public interface BinaryItemset extends Itemset, BinaryReadOnlyItemset {

    public void setBinaryValue(int attrIndex, boolean value)
            throws IndexOutOfBoundsException;

}