//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AttributeSubset.java (sed.data.attributes)
// Utworzono 2005-07-30 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public interface AttributeSubset {

    public boolean containsAttribute(int attrIndex);

    public AttributeSet getRelatedAttributeSet();
}