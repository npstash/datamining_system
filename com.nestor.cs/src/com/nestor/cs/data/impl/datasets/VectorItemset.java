//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DefaultItemset.java (sed.data.itemsets)
// Utworzono 2005-05-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets;

import java.util.Vector;

import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.InvalidAttributeValue;
import com.nestor.cs.data.def.Itemset;
import com.nestor.cs.data.def.ReadOnlyItemset;

public class VectorItemset extends AbstractReadOnlyItemset implements Itemset {

    private Vector values;

    public VectorItemset(AttributeSet as) {
        super(as);
        this.values = new Vector(getAttributeCount());
        for (int i = 0; i < getAttributeCount(); i++)
            this.values.add(null);
    }

    public VectorItemset(AttributeSet as, Object[] vs)
            throws InvalidAttributeValue {
        this(as);
        for (int i = 0; i < as.getAttributeCount(); i++)
            this.values.set(i, vs[i]);
    }

    public VectorItemset(ReadOnlyItemset clone) {
        this((AttributeSet) clone);
        for (int i = 0; i < clone.getAttributeCount(); i++)
            this.values.set(i, clone.getAttribute(i).cloneValue(clone.getValue(i)));
    }

    public Object getValue(int attrIndex) {
        return this.values.get(attrIndex);
    }

    public Object[] getValues() {
        return this.values.toArray();
    }

    public void setValue(int attrIndex, Object value)
            throws InvalidAttributeValue {
        InvalidAttributeValue.checkValidity(getAttribute(attrIndex), value);
        this.values.set(attrIndex, value);
    }

}