//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Attributeset.java (sed.data.interfaces)
// Utworzono 2005-04-18 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

import java.io.Serializable;

public interface AttributeSet extends AttributeSubset, Serializable {

    public boolean equalsToAttributeSet(AttributeSet as);

    public Attribute getAttribute(int attributeIndex);

    public int getAttributeCount();

    public int getClassLabelIndex();

    public void setClassLabelIndex(int attrIndex);

}