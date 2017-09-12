//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ZeroRule.java (com.nestor.cs.tasks.impl.clasf)
// Utworzono 2005-09-12 w �rodowisku Eclipse 3.0 
//

package com.nestor.cs.tasks.impl.clasf;

import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.util.proc.ProcessingException;


public class ZeroRule extends AbstractClassifier {

    private Object majorClassLabel = null;
    
    public Object classify(ReadOnlyDataset dsTrain, ReadOnlyItemset it)
            throws IncompatibleAttributeset {
        return this.majorClassLabel;
    }

    @Override
    public void learn(ReadOnlyDataset dsTrain) throws ProcessingException {
        super.learn(dsTrain);
        
        Distribution d = Distribution.createDistribution(dsTrain, dsTrain.getClassLabelIndex());
        this.majorClassLabel = d.getFirstMajorLabel();
    }

    public boolean requiresLearning() {
        return true;
    }

}
