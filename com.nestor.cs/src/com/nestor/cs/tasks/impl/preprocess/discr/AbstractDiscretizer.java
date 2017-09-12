//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractDiscretizer.java (sed.data.conversion.discretizers)
// Utworzono 2005-06-09 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess.discr;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.AttributeSubset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.attributes.ContinuousAttribute;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSubset;
import com.nestor.cs.tasks.def.Discretizer;
import com.nestor.cs.tasks.impl.preprocess.AbstractLearningDatasetConverter;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public abstract class AbstractDiscretizer extends
        AbstractLearningDatasetConverter implements Discretizer {

    public final Argument argAttributeSubset = new Argument();

    private DefaultAttributeSet convertedAttributeset;

    private AttributeSubset usedAttributeSubset = null;

    public AbstractDiscretizer() {
        super();
        this.argAttributeSubset.setValueType(AttributeSubset.class);
        this.argAttributeSubset.setName(Messages
                .getString("AbstractDiscretizer.argDiscretizedAttributes")); //$NON-NLS-1$
        this.argAttributeSubset.setDescription(Messages
                .getString("AbstractDiscretizer.argDescDiscretizedAttributes")); //$NON-NLS-1$
        this.argAttributeSubset.setRequired(false);
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
        if (this.usedAttributeSubset.containsAttribute(attrIndex))
            return getDiscreteValue(getArgConvertedDataset()
                    .getValue(attrIndex), attrIndex);
        else
            return getArgConvertedDataset().getValue(attrIndex);
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        this.usedAttributeSubset = getArgAttributeSubset();

        // jesli nie ustawiono parametru okreslajacego ktore
        // atrybuty maja byc zdyskretyzowane, zdyskretyzowane zostana
        // wszystkie o wartosciach rzeczywistych
        if (this.usedAttributeSubset == null) {
            ReadOnlyDataset ds = getArgTrainingDataset();
            this.usedAttributeSubset = new DefaultAttributeSubset(ds);

            for (int i = 0; i < ds.getAttributeCount(); i++) {
                Attribute attr = ds.getAttribute(i);
                if (attr instanceof ContinuousAttribute)
                    ((DefaultAttributeSubset) this.usedAttributeSubset)
                            .setAttributeContained(i, true);
            }
        }
        this.convertedAttributeset = new DefaultAttributeSet();
    }

    @Override
    public void learn(ReadOnlyDataset dsTrain) throws ProcessingException {
        super.learn(dsTrain);
        for (int a = 0; a < dsTrain.getAttributeCount(); a++) {
            boolean converted = false;
            if (this.usedAttributeSubset.containsAttribute(a)) {
                System.out.println(Messages.getString("AbstractDiscretizer.1") //$NON-NLS-1$
                        + dsTrain.getAttribute(a).getName());
                Attribute newAttr = discretizeAttribute(dsTrain, a);
                if (newAttr != null) {
                    if (newAttr.getName() == null)
                        newAttr.setName(dsTrain.getAttribute(a).getName());
                    this.convertedAttributeset.addAttribute(newAttr);
                    converted = true;
                } else {
                    System.out.println(Messages
                            .getString("AbstractDiscretizer.0")); //$NON-NLS-1$
                }
            }
            if (!converted) {
                this.convertedAttributeset.addAttribute(dsTrain.getAttribute(a));
            }
        }
        this.convertedAttributeset.setClassLabelIndex(dsTrain.getClassLabelIndex());
    }

    public void setClassLabelIndex(int attrIndex) {
        this.convertedAttributeset.setClassLabelIndex(attrIndex);
    }

}