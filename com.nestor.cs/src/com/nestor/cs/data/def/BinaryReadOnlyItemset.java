//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// BinaryReadOnlyItemset.java (sed.data.binary)
// Utworzono 2005-07-29 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public interface BinaryReadOnlyItemset extends ReadOnlyItemset {

    public boolean getBinaryValue(int attrIndex);

    public boolean[] getBinaryValues();

}