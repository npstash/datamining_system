//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractDatasetConverter.java (sed.data.conversion)
// Utworzono 2005-06-09 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.tasks.def.LearningContext;
import com.nestor.cs.tasks.def.LearningMethod;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public abstract class AbstractLearningDatasetConverter extends
        AbstractDatasetConverter implements LearningMethod {

    public final Argument argTrainDataset = new Argument();

    private LearningContext learningContext = null;

    public AbstractLearningDatasetConverter() {
        super();
        this.argTrainDataset.setValueType(ReadOnlyDataset.class);
        this.argTrainDataset.setName(Messages
                .getString("AbstractLearningDatasetConverter.argTrainDataset")); //$NON-NLS-1$
        this.argTrainDataset
                .setDescription(Messages
                        .getString("AbstractLearningDatasetConverter.argDescTrainDataset")); //$NON-NLS-1$
        this.argTrainDataset.setRequired(false);
        getConfigurationArguments().addArgument(this.argTrainDataset);
    }

    protected void createLearningContext(ReadOnlyDataset dsTrain) {
        this.learningContext = new LearningContext(dsTrain);
    }

    public ReadOnlyDataset getArgTrainingDataset() {
        ReadOnlyDataset res = (ReadOnlyDataset) this.argTrainDataset.getValue();
        if (res == null)
            return getArgConvertedDataset();
        else
            return res;
    }

    public LearningContext getLearningContext() {
        if (this.learningContext == null)
            this.learningContext = new LearningContext(getArgTrainingDataset());
        return this.learningContext;
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        // jesli zbiory s� r�ne trzeba sprawdzic czy s�
        // zgodne (czy maj� takie same zbiory atrybut�w)
        if (getArgConvertedDataset() != getArgTrainingDataset())
            try {
                IncompatibleAttributeset.checkEquality(
                        getArgConvertedDataset(), getArgTrainingDataset());
            } catch (IncompatibleAttributeset e) {
                throw new ProcessingException(e);
            }
    }

    public void learn(ReadOnlyDataset dsTrain) throws ProcessingException {
        createLearningContext(dsTrain);
    }

    protected void setLearningContext(LearningContext learningContext) {
        this.learningContext = learningContext;
    }

    public boolean stepProcess() throws ProcessingException {
        if (requiresLearning())
            learn(getArgTrainingDataset());
        stepComplete();
        return false;
    }

}