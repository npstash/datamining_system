//
//Praca Dyplomowa Magisterska: System Exploracji Danych
//Autor: Nestor Paw�owski
//
//ArrayItemset.java (sed.data.itemsets)
//Utworzono 2005-05-24 w �rodowisku Eclipse 3.0
//
package com.nestor.cs.data.impl.datasets.binary;

import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.BinaryItemset;
import com.nestor.cs.data.def.BinaryReadOnlyItemset;
import com.nestor.cs.data.def.InvalidAttributeValue;
import com.nestor.cs.data.impl.datasets.AbstractReadOnlyItemset;

public class BinaryArrayItemset extends AbstractReadOnlyItemset implements
        BinaryItemset {

    private boolean[] values;

    public BinaryArrayItemset(AttributeSet as, boolean[] vs) {
        super(as);
        this.values = vs;
    }

    public BinaryArrayItemset(BinaryReadOnlyItemset clone) {
        this(clone, new boolean[clone.getAttributeCount()]);
        for (int i = 0; i < clone.getAttributeCount(); i++) {
            this.values[i] = clone.getBinaryValue(i);
        }
    }

    public boolean getBinaryValue(int attrIndex) {
        return this.values[attrIndex];
    }

    public boolean[] getBinaryValues() {
        return this.values;
    }

    public Object getValue(int attrIndex) {
        return new Boolean(this.values[attrIndex]);
    }

    public Object[] getValues() {
        Boolean[] bs = new Boolean[this.values.length];
        for (int i = 0; i < this.values.length; i++) {
            bs[i] = new Boolean(this.values[i]);
        }
        return bs;
    }

    public void setBinaryValue(int attrIndex, boolean value)
            throws IndexOutOfBoundsException {
        this.values[attrIndex] = value;
    }

    public void setValue(int attrIndex, Object value)
            throws InvalidAttributeValue {
        InvalidAttributeValue.checkValidity(getAttribute(attrIndex), value);
        this.values[attrIndex] = ((Boolean) value).booleanValue();
    }
}