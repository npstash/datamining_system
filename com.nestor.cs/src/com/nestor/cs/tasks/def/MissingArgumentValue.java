//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// MissingArgumentValue.java (sed.data.processing)
// Utworzono 2005-07-23 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.def;

import com.nestor.cs.Messages;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;
import com.nestor.cs.util.util.param.ArgumentList;

public class MissingArgumentValue extends ProcessingException {

    private static String prepareMessage(Argument[] as) {
        String s = Messages.getString("MissingArgumentValue.argumentValuesFor"); //$NON-NLS-1$
        for (int i = 0; i < as.length; i++) {
            if (as[i].getValue() == null) {
                s += "\"" + as[i].getName() + "\", "; //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        s += Messages.getString("MissingArgumentValue.areMissing"); //$NON-NLS-1$
        return s;
    }

    public MissingArgumentValue(Argument a) {
        super(prepareMessage(new Argument[] { a }));
    }

    public MissingArgumentValue(Argument[] as) {
        super(prepareMessage(as));
    }

    public MissingArgumentValue(ArgumentList al) {
        super(prepareMessage(al.getRequiredArguments()));
    }
}