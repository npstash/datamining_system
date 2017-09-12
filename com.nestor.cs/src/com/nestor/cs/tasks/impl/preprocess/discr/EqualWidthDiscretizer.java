//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// NominalizeEqualWidth.java (sed.data.processing)
// Utworzono 2005-05-08 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess.discr;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.attributes.ContinuousAttribute;
import com.nestor.cs.data.impl.attributes.DiscreteAttribute;
import com.nestor.cs.data.impl.datasets.Datasets;
import com.nestor.cs.util.proc.ProcessingException;

public class EqualWidthDiscretizer extends AbstractBinningDiscretizer {

    public EqualWidthDiscretizer() {
        super();
    }

    public Attribute discretizeAttribute(ReadOnlyDataset trainDs, int attrIndex)
            throws ProcessingException {
        if (!(trainDs.getAttribute(attrIndex) instanceof ContinuousAttribute))
            throw new ProcessingException(
                    Messages
                            .getString("EqualWidthDiscretizer.attributeMustBeContinuous")); //$NON-NLS-1$

        Comparable[] extrs = Datasets.getExtremumValues(trainDs, attrIndex);
        double low = ((Double) extrs[0]).doubleValue();
        double high = ((Double) extrs[1]).doubleValue();

        double width = (high - low) / getArgBins();
        System.out
                .println(Messages.getString("EqualWidthDiscretizer.0") + width); //$NON-NLS-1$

        for (int i = 1; i < getArgBins(); i++) {
            Double bound = new Double(i * width + low);
            addSplitPoint(bound, bound, attrIndex);
        }
        return new DiscreteAttribute(createDefaultLabels(attrIndex));
    }

    public boolean requiresLearning() {
        return true;
    }

}