package com.nestor.cs.tasks.impl.preprocess.discr;

import com.nestor.cs.util.proc.ProcessingException;

public class ProportionalEqualFrequencyDiscretizer extends
        EqualFrequencyDiscretizer {

    public ProportionalEqualFrequencyDiscretizer() {
        super();
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        try {
            int prBins = (int) Math.sqrt(getArgTrainingDataset().size());
            boolean b = this.argBinCount.isReadOnly();
            this.argBinCount.setReadOnly(false);
            this.argBinCount.setValue(new Integer(prBins));
            this.argBinCount.setReadOnly(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}