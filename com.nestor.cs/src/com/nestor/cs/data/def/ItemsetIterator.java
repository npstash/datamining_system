//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ItemsetIterator.java (sed.data.interfaces)
// Utworzono 2005-04-18 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public interface ItemsetIterator {

    public boolean hasMoreItemsets();

    public ReadOnlyItemset nextItemset();

}