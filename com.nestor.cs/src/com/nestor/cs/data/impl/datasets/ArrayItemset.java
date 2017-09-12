//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ArrayItemset.java (sed.data.itemsets)
// Utworzono 2005-05-24 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets;

import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.InvalidAttributeValue;
import com.nestor.cs.data.def.Itemset;
import com.nestor.cs.data.def.ReadOnlyItemset;

public class ArrayItemset extends AbstractReadOnlyItemset implements Itemset {

    private Object[] values;

    public ArrayItemset(AttributeSet as, Object[] vs) {
        super(as);
        this.values = vs;
    }

    public ArrayItemset(ReadOnlyItemset clone) {
        this(clone, new Object[clone.getAttributeCount()]);

        for (int i = 0; i < clone.getAttributeCount(); i++)
            this.values[i] = clone.getAttribute(i).cloneValue(clone.getValue(i));
    }

    public Object getValue(int attrIndex) {
        return this.values[attrIndex];
    }

    public Object[] getValues() {
        return this.values;
    }

    public void setValue(int attrIndex, Object value)
            throws InvalidAttributeValue {
        InvalidAttributeValue.checkValidity(getAttribute(attrIndex), value);
        this.values[attrIndex] = value;
    }

}