//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Classifier.java (sed.data.clasf)
// Utworzono 2005-07-31 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.def;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Dataset;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.util.proc.ProcessingException;

public interface Classifier extends LearningMethod {

    public static class InvalidDataset extends ProcessingException {

        public InvalidDataset(Exception e) {
            super(e);
        }

        public InvalidDataset() {
            super(Messages.getString("Classifier.dsImproper")); //$NON-NLS-1$
        }

        public InvalidDataset(String s) {
            super(s);
        }
    }

    public static class MissingClassLabelAttribute extends ProcessingException {

        public MissingClassLabelAttribute() {
            super(Messages.getString("Classifier.classLabelRequired")); //$NON-NLS-1$
        }

        public MissingClassLabelAttribute(String s) {
            super(s);
        }
    }

    public Object classify(ReadOnlyDataset dsTrain, ReadOnlyItemset it)
            throws IncompatibleAttributeset;

    public void learnAndClassify(ReadOnlyDataset dsTrain, Dataset classified)
            throws IncompatibleAttributeset, ProcessingException;

}