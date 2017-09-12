//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractValueConverter.java (com.nestor.cs.tasks.impl.preprocess)
// Utworzono 2005-09-08 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.util.proc.ProcessingException;

public abstract class AbstractValueConverter extends AbstractDatasetConverter {

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

    public AttributeSet getRelatedAttributeSet() {
        return getArgConvertedDataset().getRelatedAttributeSet();
    }

    public void setClassLabelIndex(int attrIndex) {
        getArgConvertedDataset().setClassLabelIndex(attrIndex);
    }

    public boolean stepProcess() throws ProcessingException {
        return false;
    }
}