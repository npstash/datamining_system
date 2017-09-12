//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// RemoveMissingValues.java (sed.data.conversion)
// Utworzono 2005-08-07 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.ItemsetIndexesSelector;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.def.TestSet;
import com.nestor.cs.data.impl.datasets.DatasetView;
import com.nestor.cs.data.impl.datasets.SelectedItemsets;
import com.nestor.cs.data.impl.tests.TestSetSelector;
import com.nestor.cs.util.proc.ProcessingException;

public class RemoveMissingValues extends AbstractDatasetConverter {

    private static int[] getNonNullIndexes(ReadOnlyDataset ds) {
        // pozbywam si� rekord�w o warto�ciach nieznanych
        TestSet nonNullTS = new TestSet(ds);
        for (int a = 0; a < ds.getAttributeCount(); a++) {
            Attribute attr = ds.getAttribute(a);
            nonNullTS.setTest(a, attr.createTest(null, Test.INEQUAL));
        }
        TestSetSelector nonNullTSS = new TestSetSelector(nonNullTS);
        ItemsetIndexesSelector nonNullIIS = new SelectedItemsets(nonNullTSS);
        return nonNullIIS.selectItemsetIndexes(ds);
    }

    public static DatasetView getNonNullView(ReadOnlyDataset ds) {
        return new DatasetView(ds, getNonNullIndexes(ds));
    }

    private int cursor = -1;

    private int[] indexes;

    public boolean containsAttribute(int attrIndex) {
        return getArgConvertedDataset().containsAttribute(attrIndex);
    }

    public boolean equalsToAttributeSet(AttributeSet as) {
        return getArgConvertedDataset().equalsToAttributeSet(as);
    }

    public Attribute getAttribute(int attributeIndex) {
        return getArgConvertedDataset().getAttribute(attributeIndex);
    }

    public int getAttributeCount() {
        return getArgConvertedDataset().getAttributeCount();
    }

    public int getClassLabelIndex() {
        return getArgConvertedDataset().getClassLabelIndex();
    }

    @Override
    public int getCursorPos() {
        return this.cursor;
    }

    public AttributeSet getRelatedAttributeSet() {
        return getArgConvertedDataset().getRelatedAttributeSet();
    }

    public Object getValue(int attrIndex) {
        return getArgConvertedDataset().getValue(attrIndex);
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        ReadOnlyDataset ds = getArgConvertedDataset();
        this.indexes = getNonNullIndexes(ds);
    }

    public void setClassLabelIndex(int attrIndex) {
        getArgConvertedDataset().setClassLabelIndex(attrIndex);
    }

    @Override
    public void setCursorAt(int i) {
        this.cursor = i;
        getArgConvertedDataset().setCursorAt(this.indexes[i]);
    }

    @Override
    public int size() {
        return this.indexes.length;
    }

    public boolean stepProcess() throws ProcessingException {
        return false;
    }
}