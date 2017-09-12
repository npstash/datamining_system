//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ReadOnlyItemset.java (sed.data.itemsets)
// Utworzono 2005-05-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public interface ReadOnlyItemset extends AttributeSet {

    public Object getValue(int attrIndex);

    public Object[] getValues();
}