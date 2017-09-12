//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ArgumentValueOutOfRange.java (javan.util)
// Utworzono 2005-04-15 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util.param;

import com.nestor.cs.util.Messages;

public class ArgumentValueOutOfRange extends Exception {

    public ArgumentValueOutOfRange(Argument arg, Object newValue) {
        super(
                Messages.getString("ArgumentValueOutOfRange.newValue") + "'" + newValue + "'" + Messages.getString("ArgumentValueOutOfRange.forArgument") + "'" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                        + arg.getName()
                        + "'" + Messages.getString("ArgumentValueOutOfRange.isOutOfBounds") + "(" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + arg.getLowBound() + "; " + arg.getHighBound() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
    }

}