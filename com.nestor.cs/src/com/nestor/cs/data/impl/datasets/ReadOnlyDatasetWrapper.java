//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DatasetWrapper.java (sed.data.datasets)
// Utworzono 2005-06-09 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets;

import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;

public class ReadOnlyDatasetWrapper extends AbstractReadOnlyDataset {

    private ReadOnlyDataset ds = null;

    public ReadOnlyDatasetWrapper(ReadOnlyDataset ds) {
        super(ds);
        this.ds = ds;
    }

    public int getCursorPos() {
        return this.ds.getCursorPos();
    }

    public ReadOnlyItemset getItemset(int i) {
        return this.ds.getItemset(i);
    }

    public Object getValue(int attrIndex) {
        return this.ds.getValue(attrIndex);
    }

    public Object[] getValues() {
        return this.ds.getValues();
    }

    public void setCursorAt(int i) {
        this.ds.setCursorAt(i);
    }

    public void setWrappedDataset(ReadOnlyDataset ds) {
        this.ds = ds;
        if (ds instanceof AbstractReadOnlyItemset) {
            super.attributeSet = ((AbstractReadOnlyItemset) ds).attributeSet;
        } else {
            super.attributeSet = ds;
        }
    }

    public int size() {
        return this.ds.size();
    }
}