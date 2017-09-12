//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DatasetView.java (sed.data.datasets)
// Utworzono 2005-07-31 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets;

import com.nestor.cs.data.def.AttributeSubset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;

public class DatasetView extends AbstractReadOnlyDataset implements
        ReadOnlyDataset {

    private int[] attrIndexes;

    private int cursor = -1;

    private ReadOnlyDataset ds;

    private int[] indexes;

    private String name = null;

    public DatasetView(ReadOnlyDataset ds, AttributeSubset as) {
        this(ds, as, RangeItemsets.createIntArray(0, ds.size() - 1));
    }

    public DatasetView(ReadOnlyDataset ds, AttributeSubset as, int[] indexes) {
        super(new DefaultAttributeSet(as));
        this.ds = ds;
        this.indexes = indexes;
        this.attrIndexes = new int[getAttributeCount()];
        int j = 0;
        for (int i = 0; i < as.getRelatedAttributeSet().getAttributeCount(); i++) {
            if (as.containsAttribute(i))
                this.attrIndexes[j++] = i;
        }
    }

    public DatasetView(ReadOnlyDataset ds, int[] indexes) {
        this(ds, ds, indexes);
    }

    public int getCursorPos() {
        return this.cursor;
    }

    @Override
    public String getDescription() {
        return this.ds.getDescription();
    }

    public ReadOnlyItemset getItemset(int i) {
        int k = this.ds.getCursorPos();
        this.ds.setCursorAt(i);
        ReadOnlyItemset it = new ArrayItemset(this, getValues());
        this.ds.setCursorAt(k);
        return it;
    }

    @Override
    public String getName() {
        if (this.name == null)
            return this.ds.getName();
        else
            return this.name;
    }

    public int getOriginalAttributeIndex(int attrIndex) {
        return this.attrIndexes[attrIndex];
    }

    public int getOriginalRowIndex(int row) {
        return this.indexes[row];
    }

    public Object getValue(int attrIndex) {
        return this.ds.getValue(this.attrIndexes[attrIndex]);
    }

    public Object[] getValues() {
        Object[] values = new Object[getAttributeCount()];
        for (int j = 0; j < values.length; j++) {
            values[j] = this.ds.getValue(this.attrIndexes[j]);
        }
        return values;
    }

    public void setCursorAt(int i) {
        this.cursor = i;
        this.ds.setCursorAt(this.indexes[i]);
    }

    @Override
    public void setDescription(String d) {
        this.ds.setDescription(d);
    }

    @Override
    public void setName(String n) {
        this.name = n;
    }

    public int size() {
        return this.indexes.length;
    }
}