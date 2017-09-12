//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// LearningMethod.java (sed.data.clasf)
// Utworzono 2005-08-01 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.def;

import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.util.proc.ProcessingException;

public interface LearningMethod {

    public LearningContext getLearningContext();

    public boolean requiresLearning();

    public void learn(ReadOnlyDataset dsTrain) throws ProcessingException;

}