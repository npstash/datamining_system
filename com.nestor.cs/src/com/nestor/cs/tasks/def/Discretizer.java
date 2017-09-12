//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Discretizer.java (sed.data.conversion.discretizers)
// Utworzono 2005-08-01 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.def;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.util.proc.ProcessingException;

public interface Discretizer extends LearningMethod {

    public Object getDiscreteValue(Object value, int attrIndex);

    public Attribute discretizeAttribute(ReadOnlyDataset trainDs, int attrIndex)
            throws ProcessingException;

}