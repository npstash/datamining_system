//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Itemset.java (sed.data)
// Utworzono 2005-04-18 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public interface Itemset extends ReadOnlyItemset {

    public void setValue(int attrIndex, Object value)
            throws InvalidAttributeValue, IndexOutOfBoundsException;
}