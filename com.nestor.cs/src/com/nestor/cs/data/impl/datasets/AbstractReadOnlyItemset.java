//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// GenericItemset.java (sed.data.itemsets)
// Utworzono 2005-05-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.ReadOnlyItemset;

public abstract class AbstractReadOnlyItemset implements ReadOnlyItemset {

    protected AttributeSet attributeSet;

    public AbstractReadOnlyItemset(AttributeSet as) {
        this.attributeSet = as;
        if (as instanceof AbstractReadOnlyItemset) {
            this.attributeSet = ((AbstractReadOnlyItemset) as).attributeSet;
        }
    }

    public boolean containsAttribute(int attrIndex) {
        return this.attributeSet.containsAttribute(attrIndex);
    }

    public boolean equalsToAttributeSet(AttributeSet as) {
        return this.attributeSet.equalsToAttributeSet(as);
    }

    public Attribute getAttribute(int attributeIndex) {
        return this.attributeSet.getAttribute(attributeIndex);
    }

    public int getAttributeCount() {
        return this.attributeSet.getAttributeCount();
    }

    public int getClassLabelIndex() {
        return this.attributeSet.getClassLabelIndex();
    }

    public AttributeSet getRelatedAttributeSet() {
        return this.attributeSet.getRelatedAttributeSet();
    }

    public void setClassLabelIndex(int attrIndex) {
        this.attributeSet.setClassLabelIndex(attrIndex);
    }
}