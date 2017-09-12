//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// BinaryArrayReadOnlyDataset.java (sed.data.binary)
// Utworzono 2005-07-29 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets.binary;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.BinaryReadOnlyDataset;
import com.nestor.cs.data.def.BinaryReadOnlyItemset;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.InvalidAttributeValue;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.attributes.BinaryAttribute;
import com.nestor.cs.data.impl.datasets.AbstractReadOnlyDataset;
import com.nestor.cs.data.impl.datasets.ArrayItemset;

public class BinaryArrayReadOnlyDataset extends AbstractReadOnlyDataset
        implements BinaryReadOnlyDataset {

    private int cursor = -1;

    private boolean[][] values;

    public BinaryArrayReadOnlyDataset(AttributeSet as, boolean[][] values) {
        super(as);
        this.values = values;
    }

    public BinaryArrayReadOnlyDataset(ReadOnlyDataset clone)
            throws IncompatibleAttributeset, InvalidAttributeValue {
        super(clone);
        Attributes.checkForBinaryAttributes(clone, false);

        this.values = new boolean[clone.size()][clone.getAttributeCount()];
        if (clone instanceof BinaryReadOnlyDataset) {
            BinaryReadOnlyDataset bds = (BinaryReadOnlyDataset) clone;
            for (int i = 0; i < this.values.length; i++) {
                bds.setCursorAt(i);
                for (int j = 0; j < this.values[i].length; j++)
                    this.values[i][j] = bds.getBinaryValue(j);
            }
        } else {
            for (int i = 0; i < this.values.length; i++) {
                clone.setCursorAt(i);
                for (int j = 0; j < this.values[i].length; j++) {
                    Boolean b = (Boolean) clone.getValue(j);
                    if (b == null)
                        this.values[i][j] = false;
                    else
                        this.values[i][j] = b.booleanValue();
                }
            }
        } // else
    }

    public BinaryArrayReadOnlyDataset(ReadOnlyDataset clone, int[] indexes)
            throws IncompatibleAttributeset {
        super(clone);
        for (int i = 0; i < clone.getAttributeCount(); i++) {
            if (!(clone.getAttribute(i) instanceof BinaryAttribute))
                throw new IncompatibleAttributeset(
                        Messages
                                .getString("BinaryArrayReadOnlyDataset.onlyBinaryAllowed")); //$NON-NLS-1$
        }
        this.values = new boolean[indexes.length][clone.getAttributeCount()];
        if (clone instanceof BinaryReadOnlyDataset) {
            BinaryReadOnlyDataset bds = (BinaryReadOnlyDataset) clone;
            for (int i = 0; i < this.values.length; i++) {
                bds.setCursorAt(indexes[i]);
                for (int j = 0; j < this.values[i].length; j++)
                    this.values[i][j] = bds.getBinaryValue(j);
            }
        } else {
            for (int i = 0; i < this.values.length; i++) {
                clone.setCursorAt(indexes[i]);
                for (int j = 0; j < this.values[i].length; j++)
                    this.values[i][j] = ((Boolean) clone.getValue(j)).booleanValue();
            }
        }
    }

    public BinaryReadOnlyItemset getBinaryItemset(int i) {
        return new BinaryArrayItemset(this, this.values[i]);
    }

    public boolean getBinaryValue(int attrIndex) {
        return this.values[this.cursor][attrIndex];
    }

    public boolean[] getBinaryValues() {
        return this.values[this.cursor];
    }

    public int getCursorPos() {
        return 0;
    }

    public ReadOnlyItemset getItemset(int i) {
        int k = getCursorPos();
        setCursorAt(i);
        ReadOnlyItemset it = new ArrayItemset(this, getValues());
        setCursorAt(k);
        return it;
    }

    public Object getValue(int attrIndex) {
        return new Boolean(this.values[this.cursor][attrIndex]);
    }

    public Object[] getValues() {
        Boolean[] bs = new Boolean[getAttributeCount()];
        for (int i = 0; i < getAttributeCount(); i++) {
            bs[i] = (Boolean) getValue(i);
        }
        return bs;
    }

    public void setCursorAt(int i) {
        this.cursor = i;
    }

    public int size() {
        return this.values.length;
    }
}