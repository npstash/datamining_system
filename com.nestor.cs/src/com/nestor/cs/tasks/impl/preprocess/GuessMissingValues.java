//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ReplaceMissingValues.java (sed.data.conversion)
// Utworzono 2005-08-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess;

import java.util.Random;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.ItemsetIndexesSelector;
import com.nestor.cs.data.def.ItemsetSelector;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.attributes.ContinuousAttribute;
import com.nestor.cs.data.impl.attributes.DiscreteAttribute;
import com.nestor.cs.data.impl.datasets.DatasetView;
import com.nestor.cs.data.impl.datasets.SelectedItemsets;
import com.nestor.cs.data.impl.tests.TestSelector;
import com.nestor.cs.util.proc.ProcessingException;

public class GuessMissingValues extends AbstractLearningDatasetConverter {

    private static Object[] getReplacements(ReadOnlyDataset dsTrain)
            throws ProcessingException {
        Object[] replacements = new Object[dsTrain.getAttributeCount()];

        for (int a = 0; a < dsTrain.getAttributeCount(); a++) {
            Attribute attr = dsTrain.getAttribute(a);

            if (attr instanceof DiscreteAttribute) {
                Distribution d = Distribution.createDistribution(dsTrain, a);
                Object major = d.getFirstMajorLabel();
                if (major == null) {
                    Random rnd = new Random();
                    major = attr.getAllowedValue(rnd.nextInt(attr
                            .getAllowedValuesCount()));
                }
                replacements[a] = major;
            } else if (attr instanceof ContinuousAttribute) {
                replacements[a] = new Double(Attributes
                        .getMeanValue(dsTrain, a));
            } else {
                throw new ProcessingException(Messages
                        .getString("GuessMissingValues.unknownValueType")); //$NON-NLS-1$
            }
        }
        return replacements;
    }

    private Object[] replacements;

    private Object[][] replacementsByClassIndex;

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

    public Object getValue(int attrIndex) {
        Object v = getArgConvertedDataset().getValue(attrIndex);
        if (v != null)
            return v;

        int clAttrIndex = getArgConvertedDataset().getClassLabelIndex();
        if (clAttrIndex >= 0) {
            Attribute clAttr = getArgConvertedDataset().getAttribute(
                    clAttrIndex);
            Object clLabel = getArgConvertedDataset().getValue(clAttrIndex);
            if (clLabel != null) {
                int clLabelIndex = clAttr.getAllowedValueIndex(clLabel);
                if (clLabelIndex >= 0)
                    return this.replacementsByClassIndex[clLabelIndex][attrIndex];
            }
        }
        return this.replacements[attrIndex];
    }

    @Override
    public void learn(ReadOnlyDataset dsTrain) throws ProcessingException {
        super.learn(dsTrain);

        this.replacements = getReplacements(dsTrain);
        
        // najcz�stsze wartosci dla poszczegolnych klas
        if (dsTrain.getClassLabelIndex() >= 0) {
            int clAttrIndex = dsTrain.getClassLabelIndex();
            Attribute clAttr = dsTrain.getAttribute(clAttrIndex);
            if (clAttr.getAllowedValuesCount() != Attribute.INFINITE_VALUES) {
                this.replacementsByClassIndex = new Object[clAttr
                        .getAllowedValuesCount()][];
                for (int clIndex = 0; clIndex < this.replacementsByClassIndex.length; clIndex++) {

                    // tworze widok na baze z dana etykieta klasy
                    Object clLabel = clAttr.getAllowedValue(clIndex);
                    Test testCl = clAttr.createTest(clLabel, Test.EQUAL);
                    ItemsetSelector selectCl = new TestSelector(clAttrIndex,
                            testCl);
                    ItemsetIndexesSelector iis = new SelectedItemsets(selectCl);
                    ReadOnlyDataset nds = new DatasetView(dsTrain, iis
                            .selectItemsetIndexes(dsTrain));

                    this.replacementsByClassIndex[clIndex] = getReplacements(nds);
                }//for
            }//if
        }//if
    }

    public boolean requiresLearning() {
        return true;
    }

    public void setClassLabelIndex(int attrIndex) {
        getArgConvertedDataset().setClassLabelIndex(attrIndex);
    }

}