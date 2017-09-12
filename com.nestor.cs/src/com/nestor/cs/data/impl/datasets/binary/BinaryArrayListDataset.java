//
//Praca Dyplomowa Magisterska: System Exploracji Danych
//Autor: Nestor Paw�owski
//
//VectorDataset.java (sed.data.datasets)
//Utworzono 2005-05-08 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets.binary;

import java.util.ArrayList;
import java.util.List;

import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.BinaryDataset;
import com.nestor.cs.data.def.BinaryReadOnlyDataset;
import com.nestor.cs.data.def.BinaryReadOnlyItemset;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.InvalidAttributeValue;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.datasets.AbstractDataset;
import com.nestor.cs.data.impl.datasets.ArrayItemset;
import com.nestor.cs.util.util.ArrayUtils;

public class BinaryArrayListDataset extends AbstractDataset implements
        BinaryDataset {

    private int cursor = -1;

    private List rows = null;

    public BinaryArrayListDataset(AttributeSet as)
            throws IncompatibleAttributeset {
        this(as, new ArrayList());
    }

    public BinaryArrayListDataset(AttributeSet as, List container)
            throws IncompatibleAttributeset {
        super(as);
        Attributes.checkForBinaryAttributes(as, false);

        this.rows = container;
    }

    public void addBinaryItemset(BinaryReadOnlyItemset it)
            throws IncompatibleAttributeset {
        IncompatibleAttributeset.checkLength(this, it);
        this.rows.add(it.getBinaryValues());
    }

    public void addBinaryItemset(boolean[] values) {
        this.rows.add(values);
    }

    public void addItemset(Object[] values) throws InvalidAttributeValue {
        InvalidAttributeValue.checkValidity(this, values);
        boolean bs[] = new boolean[values.length];
        for (int j = 0; j < values.length; j++) {
            bs[j] = ((Boolean) values[j]).booleanValue();
        }
        this.rows.add(bs);
    }

    public void addItemset(ReadOnlyItemset it) throws IncompatibleAttributeset,
            InvalidAttributeValue {
        IncompatibleAttributeset.checkEquality(this, it);
        InvalidAttributeValue.checkValidity(this, it);
        boolean bs[] = new boolean[it.getValues().length];
        for (int j = 0; j < it.getValues().length; j++) {
            bs[j] = ((Boolean) it.getValues()[j]).booleanValue();
        }
        this.rows.add(bs);
    }

    public void appendBinary(BinaryReadOnlyDataset bds)
            throws IncompatibleAttributeset {
        IncompatibleAttributeset.checkLength(this, bds);
        for (int i = 0; i < bds.size(); i++) {
            bds.setCursorAt(i);
            addBinaryItemset(bds.getBinaryValues());
        }
    }

    public void appendBinary(BinaryReadOnlyDataset bds, int[] indexes)
            throws IncompatibleAttributeset {
        IncompatibleAttributeset.checkEquality(this, bds);
        for (int i = 0; i < indexes.length; i++) {
            bds.setCursorAt(indexes[i]);
            addBinaryItemset(bds.getBinaryValues());
        }
    }

    public BinaryReadOnlyItemset getBinaryItemset(int i) {
        return new BinaryArrayItemset(this, (boolean[]) this.rows.get(i));
    }

    public boolean getBinaryValue(int attrIndex) {
        return ((boolean[]) this.rows.get(this.cursor))[attrIndex];
    }

    public boolean[] getBinaryValues() {
        return (boolean[]) this.rows.get(this.cursor);
    }

    public int getCursorPos() {
        return this.cursor;
    }

    public ReadOnlyItemset getItemset(int i) {
        int k = getCursorPos();
        setCursorAt(i);
        ReadOnlyItemset it = new ArrayItemset(this, getValues());
        setCursorAt(k);
        return it;
    }

    public final Object getValue(int attrIndex) {
        return new Boolean(((boolean[]) this.rows.get(this.cursor))[attrIndex]);
    }

    public Object[] getValues() {
        Boolean[] bs = new Boolean[getAttributeCount()];
        for (int i = 0; i < getAttributeCount(); i++) {
            bs[i] = (Boolean) getValue(i);
        }
        return bs;
    }

    public void removeAll() {
        this.rows.clear();
    }

    public void removeItemset(int i) {
        this.rows.remove(i);
    }

    public void removeItemsets(int[] indexes) {
        for (int i = size() - 1; i >= 0; i--) {
            if (ArrayUtils.isContained(i, indexes))
                removeItemset(i);
        }
    }

    public void retainItemsets(int[] indexes) {
        for (int i = size() - 1; i >= 0; i--) {
            if (!ArrayUtils.isContained(i, indexes))
                removeItemset(i);
        }
    }

    public void setBinaryValue(int attrIndex, boolean b)
            throws IndexOutOfBoundsException {
        ((boolean[]) this.rows.get(this.cursor))[attrIndex] = b;
    }

    public void setCursorAt(int i) {
        this.cursor = i;
    }

    public void setValue(int attrIndex, Object value)
            throws InvalidAttributeValue, IndexOutOfBoundsException {
        if ((this.cursor >= size()) || (this.cursor < 0))
            throw new IndexOutOfBoundsException();
        InvalidAttributeValue.checkValidity(getAttribute(attrIndex), value);
        ((boolean[]) this.rows.get(this.cursor))[attrIndex] = ((Boolean) value)
                .booleanValue();
    }

    public int size() {
        return this.rows.size();
    }

}