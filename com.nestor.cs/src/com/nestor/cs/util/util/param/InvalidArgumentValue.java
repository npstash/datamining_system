//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// InvalidArgumentValue.java (javan.util)
// Utworzono 2005-04-12 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util.param;

import com.nestor.cs.util.Messages;

public class InvalidArgumentValue extends Exception {

    public InvalidArgumentValue(Argument arg, String cause) {
        super(
                Messages.getString("InvalidArgumentValue.newValueForArgument") + "'" + arg.getName() + "'" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + Messages.getString("InvalidArgumentValue.isInvalid") + "[" + cause + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public InvalidArgumentValue(String s) {
        super(s);
    }

}