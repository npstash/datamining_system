//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SelectNewClass.java (sed.data.conversion)
// Utworzono 2005-08-10 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.AttributeSubset;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class SelectNewClass extends AbstractDatasetConverter {

    public final Argument argAttributeSubset = new Argument();

    private DefaultAttributeSet convertedAttributeset = null;

    public SelectNewClass() {
        super();
        this.argAttributeSubset.setValueType(AttributeSubset.class);
        this.argAttributeSubset
                .setName(Messages.getString("SelectNewClass.argAttr")); //$NON-NLS-1$
        this.argAttributeSubset.setDescription(Messages
                .getString("SelectNewClass.argDescAttr")); //$NON-NLS-1$
        getConfigurationArguments().addArgument(this.argAttributeSubset);
    }

    public boolean containsAttribute(int attrIndex) {
        return this.convertedAttributeset.containsAttribute(attrIndex);
    }

    public boolean equalsToAttributeSet(AttributeSet as) {
        return this.convertedAttributeset.equalsToAttributeSet(as);
    }

    public AttributeSubset getArgAttributeSubset() {
        return (AttributeSubset) this.argAttributeSubset.getValue();
    }

    public Attribute getAttribute(int attributeIndex) {
        return this.convertedAttributeset.getAttribute(attributeIndex);
    }

    public int getAttributeCount() {
        return this.convertedAttributeset.getAttributeCount();
    }

    public int getClassLabelIndex() {
        return this.convertedAttributeset.getClassLabelIndex();
    }

    public AttributeSet getRelatedAttributeSet() {
        return this.convertedAttributeset.getRelatedAttributeSet();
    }

    public Object getValue(int attrIndex) {
        return getArgConvertedDataset().getValue(attrIndex);
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        this.convertedAttributeset = new DefaultAttributeSet(
                getArgConvertedDataset());
        this.convertedAttributeset.setClassLabelIndex(-1);

        AttributeSubset sub = getArgAttributeSubset();
        for (int i = 0; i < getArgConvertedDataset().getAttributeCount(); i++) {
            if (sub.containsAttribute(i)) {
                this.convertedAttributeset.setClassLabelIndex(i);
                break;
            }
        }
    }

    public void setClassLabelIndex(int attrIndex) {
        this.convertedAttributeset.setClassLabelIndex(attrIndex);
    }

    public boolean stepProcess() throws ProcessingException {
        stepComplete();
        return false;
    }
}