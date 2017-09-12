//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// MED_Laplace.java (sed.data.clasf.tree.prune)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree.prune;

import com.nestor.cs.data.def.Distribution;

public class MED_Laplace extends MinimalErrorPruning {

    @Override
    public double getClassProbabilityEstimate(Distribution dsTrain,
            Distribution dNode, Object clLabel) {
        double nc = dNode.getCount(clLabel);
        double N = dNode.getTotalExamples();
        double k = dsTrain.getTotalLabels();
        return (nc + 1) / (N + k);
    }

}