//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// RemoveAttributes.java (sed.data.conversion)
// Utworzono 2005-08-09 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess;

import java.util.ArrayList;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.AttributeSubset;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class RemoveAttributes extends AbstractDatasetConverter {

    public final Argument argAttributeSubset = new Argument();

    private DefaultAttributeSet convertedAttributeSet = new DefaultAttributeSet();

    private ArrayList linksToOriginal = new ArrayList();

    public RemoveAttributes() {
        super();

        this.argAttributeSubset.setName(Messages
                .getString("RemoveAttributes.argAttrs")); //$NON-NLS-1$
        this.argAttributeSubset.setDescription(Messages
                .getString("RemoveAttributes.argDescAttrs")); //$NON-NLS-1$
        this.argAttributeSubset.setValueType(AttributeSubset.class);
        getConfigurationArguments().addArgument(this.argAttributeSubset);
    }

    public boolean containsAttribute(int attrIndex) {
        return this.convertedAttributeSet.containsAttribute(attrIndex);
    }

    public boolean equalsToAttributeSet(AttributeSet as) {
        return this.convertedAttributeSet.equalsToAttributeSet(as);
    }

    public AttributeSubset getArgAttributeSubset() {
        return (AttributeSubset) this.argAttributeSubset.getValue();
    }

    public Attribute getAttribute(int attributeIndex) {
        return this.convertedAttributeSet.getAttribute(attributeIndex);
    }

    public int getAttributeCount() {
        return this.convertedAttributeSet.getAttributeCount();
    }

    public int getClassLabelIndex() {
        return this.convertedAttributeSet.getClassLabelIndex();
    }

    public AttributeSet getRelatedAttributeSet() {
        return this.convertedAttributeSet;
    }

    public Object getValue(int attrIndex) {
        int origIndex = ((Integer) this.linksToOriginal.get(attrIndex)).intValue();
        return getArgConvertedDataset().getValue(origIndex);
    }

    public void setClassLabelIndex(int attrIndex) {
        this.convertedAttributeSet.setClassLabelIndex(attrIndex);
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        AttributeSubset asub = getArgAttributeSubset();

        try {
            IncompatibleAttributeset.checkEquality(asub
                    .getRelatedAttributeSet(), getArgConvertedDataset());
        } catch (IncompatibleAttributeset e) {
            throw new ProcessingException(Messages
                    .getString("RemoveAttributes.invalidDs")); //$NON-NLS-1$
        }

        this.convertedAttributeSet.removeAll();
        this.linksToOriginal.clear();
        for (int i = 0; i < getArgConvertedDataset().getAttributeCount(); i++) {
            if (!asub.containsAttribute(i)) {
                Attribute attr = getArgConvertedDataset().getAttribute(i);
                this.convertedAttributeSet.addAttribute(attr);
                if (i == getArgConvertedDataset().getClassLabelIndex()) {
                    int clI = this.convertedAttributeSet.getAttributeCount() - 1;
                    this.convertedAttributeSet.setClassLabelIndex(clI);
                }
                this.linksToOriginal.add(new Integer(i));
            }
        }
    }

    public boolean stepProcess() throws ProcessingException {
        stepComplete();
        return false;
    }
}