//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ClassifierElement.java (sed.data.clasf)
// Utworzono 2005-07-31 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.Dataset;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.impl.datasets.ArrayItemset;
import com.nestor.cs.tasks.def.Classifier;
import com.nestor.cs.tasks.impl.preprocess.AbstractLearningDatasetConverter;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public abstract class AbstractClassifier extends
        AbstractLearningDatasetConverter implements Classifier {

    public final Argument argCacheResults = new Argument(new Boolean(true));

    protected Object[] cachedValues = null;

    protected boolean[] valueCached = null;

    public AbstractClassifier() {
        super();
        this.argCacheResults.setName(Messages
                .getString("AbstractClassifier.argCache")); //$NON-NLS-1$
        this.argCacheResults.setDescription(Messages
                .getString("AbstractClassifier.argDescCache")); //$NON-NLS-1$
        this.argCacheResults.setRequired(false);
        getConfigurationArguments().addArgument(this.argCacheResults);
    }

    public boolean containsAttribute(int attrIndex) {
        return getArgConvertedDataset().containsAttribute(attrIndex);
    }

    public boolean equalsToAttributeSet(AttributeSet as) {
        return getArgConvertedDataset().equalsToAttributeSet(as);
    }

    public boolean getArgCacheResults() {
        if (this.argCacheResults.getValue() != null)
            return ((Boolean) this.argCacheResults.getValue()).booleanValue();
        return false;
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
        if (attrIndex != getArgConvertedDataset().getClassLabelIndex()) {
            return getArgConvertedDataset().getValue(attrIndex);
        }
        try {
            if (this.cachedValues != null) {
                int cursor = getCursorPos();
                if (!this.valueCached[cursor]) {
                    ReadOnlyItemset it = getArgConvertedDataset();
                    if (it == getArgTrainingDataset())
                        it = new ArrayItemset(it);
                    this.cachedValues[cursor] = classify(getArgTrainingDataset(), it);
                    this.valueCached[cursor] = true;
                }
                return this.cachedValues[cursor];
            } else {
                return classify(getArgTrainingDataset(),
                        getArgConvertedDataset());
            }
        } catch (IncompatibleAttributeset e) {
            return null;
        }
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();
        if (getArgCacheResults()) {
            this.cachedValues = new Object[getArgConvertedDataset().size()];
            this.valueCached = new boolean[getArgConvertedDataset().size()];
        } else {
            this.cachedValues = null;
            this.valueCached = null;
        }
    }

    @Override
    public void learn(ReadOnlyDataset dsTrain) throws ProcessingException {
        super.learn(dsTrain);

        if (dsTrain.getClassLabelIndex() < 0)
            throw new MissingClassLabelAttribute();
    }

    public void learnAndClassify(ReadOnlyDataset dsTrain, Dataset classified)
            throws IncompatibleAttributeset, ProcessingException {
        IncompatibleAttributeset.checkEquality(dsTrain, classified);

        learn(dsTrain);
        int clIndex = dsTrain.getClassLabelIndex();
        for (int i = 0; i < classified.size(); i++) {
            classified.setCursorAt(i);
            try {
                classified.setValue(clIndex, classify(dsTrain, classified));
            } catch (IncompatibleAttributeset e) {
                throw e;
            } catch (Exception e) {
                throw new ProcessingException(e);
            }
        }
    }

    public void setClassLabelIndex(int attrIndex) {
        getArgConvertedDataset().setClassLabelIndex(attrIndex);
    }
}