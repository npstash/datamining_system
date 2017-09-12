//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// MED_mEstimate.java (sed.data.clasf.tree.prune)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree.prune;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.util.util.param.Argument;

public class MED_mEstimate extends MinimalErrorPruning {

    public final Argument argM = new Argument(new Double(10));

    public MED_mEstimate() {
        super();

        this.argM.setName(Messages.getString("MED_mEstimate.argM")); //$NON-NLS-1$
        this.argM.setDescription(Messages.getString("MED_mEstimate.argDescM")); //$NON-NLS-1$
        this.argM.setLowBound(new Double(0.1));
        getConfigurationArguments().addArgument(this.argM);
    }

    protected double getArgM() {
        if (this.argM.getValue() == null)
            return 0;
        return ((Double) this.argM.getValue()).doubleValue();
    }

    @Override
    public double getClassProbabilityEstimate(Distribution dTrain,
            Distribution dNode, Object clLabel) {
        double n = dTrain.getTotalExamples();
        double pca = (dTrain.getCount(clLabel)) / n;
        double m = getArgM();
        double nc = dNode.getCount(clLabel);
        double N = dNode.getTotalExamples();

        return (pca * m + nc) / (N + m);
    }
}