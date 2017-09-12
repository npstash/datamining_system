//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DatasetSplit.java (sed.data.datasets)
// Utworzono 2005-08-07 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.datasets.DatasetView;
import com.nestor.cs.data.impl.datasets.ReadOnlyDatasetWrapper;
import com.nestor.cs.tasks.def.MultiResultTask;
import com.nestor.cs.tasks.impl.ConfigurableTaskHelper;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class DatasetSplit extends ConfigurableTaskHelper implements
        MultiResultTask {

    public final Argument argDataset = new Argument();

    public final Argument argRandomize = new Argument(new Boolean(false));

    public final Argument argRandomSeed = new Argument();

    public final Argument argSplitRatio = new Argument(new Double(10));

    private ReadOnlyDatasetWrapper dsTest;

    private ReadOnlyDatasetWrapper dsTrain;

    private TaskResult[] faces;

    public DatasetSplit() {
        super();

        this.argDataset.setName(Messages.getString("DatasetSplit.argSplitDs")); //$NON-NLS-1$
        this.argDataset.setDescription(Messages
                .getString("DatasetSplit.argDescSplitDs")); //$NON-NLS-1$
        this.argDataset.setValueType(ReadOnlyDataset.class);
        getConfigurationArguments().addArgument(this.argDataset);

        this.argSplitRatio.setName(Messages.getString("DatasetSplit.argPercent")); //$NON-NLS-1$
        this.argSplitRatio.setDescription(Messages
                .getString("DatasetSplit.argDescPercent")); //$NON-NLS-1$
        this.argSplitRatio.setLowBound(new Double(0));
        this.argSplitRatio.setHighBound(new Double(99));
        getConfigurationArguments().addArgument(this.argSplitRatio);

        this.argRandomize.setName(Messages.getString("DatasetSplit.argRandomize")); //$NON-NLS-1$
        this.argRandomize.setDescription(Messages
                .getString("DatasetSplit.argDescRandomize")); //$NON-NLS-1$
        getConfigurationArguments().addArgument(this.argRandomize);

        this.argRandomSeed.setName(Messages.getString("DatasetSplit.argRandomSeed")); //$NON-NLS-1$
        this.argRandomSeed.setValueType(Integer.class);
        this.argRandomSeed.setRequired(false);
        this.argRandomSeed.setName(Messages.getString("DatasetSplit.argRandomSeed")); //$NON-NLS-1$
        this.argRandomSeed.setDescription(Messages
                .getString("DatasetSplit.argDescRandomSeed")); //$NON-NLS-1$
        getConfigurationArguments().addArgument(this.argRandomSeed);

        this.faces = new TaskResult[2];
        //        dsTrain = new ArrayListDataset(asCommon);
        this.dsTrain = new ReadOnlyDatasetWrapper(null);
        this.faces[0] = new TaskResult(
                Messages.getString("DatasetSplit.learningDs"), this, this.dsTrain); //$NON-NLS-1$

        //        dsTest = new ArrayListDataset(asCommon);
        this.dsTest = new ReadOnlyDatasetWrapper(null);
        this.faces[1] = new TaskResult(
                Messages.getString("DatasetSplit.testDs"), this, this.dsTest); //$NON-NLS-1$
    }

    public TaskResult[] getTaskResults() {
        return this.faces;
    }

    public Random getRandomizer() {
        if (this.argRandomSeed.getValue() == null)
            return new Random();
        else
            return new Random(((Integer) this.argRandomSeed.getValue()).intValue());
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        ReadOnlyDataset ds = (ReadOnlyDataset) this.argDataset.getValue();
        double splitR = ((Double) this.argSplitRatio.getValue()).doubleValue() / 100d;
        int nTest = (int) (ds.size() * splitR);
        int nTotal = ds.size();

        ArrayList allIndexes = new ArrayList(nTotal);
        for (int i = 0; i < nTotal; i++) {
            allIndexes.add(new Integer(i));
        }
        if (((Boolean) this.argRandomize.getValue()).booleanValue())
            Collections.shuffle(allIndexes, getRandomizer());

        int[] trainIndexes = new int[nTotal - nTest];
        int[] testIndexes = new int[nTest];

        try {
            int trainI = 0, testI = 0;
            for (int i = 0; i < nTotal; i++) {
                int index = ((Integer) allIndexes.get(i)).intValue();
                if (i < nTest)
                    testIndexes[testI++] = index;
                else
                    trainIndexes[trainI++] = index;
            }

            DatasetView trainView = new DatasetView(ds, trainIndexes);
            DatasetView testView = new DatasetView(ds, testIndexes);

            this.dsTrain.setWrappedDataset(trainView);
            this.dsTest.setWrappedDataset(testView);

            this.dsTrain.setName(ds.getName() + "-train"); //$NON-NLS-1$
            this.dsTest.setName(ds.getName() + "-test"); //$NON-NLS-1$
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}