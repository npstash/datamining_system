//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// IncompatibleAttributeset.java (sed.data.attributes)
// Utworzono 2005-05-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

import com.nestor.cs.Messages;

public class IncompatibleAttributeset extends Exception {

    public static void checkEquality(AttributeSet as1, AttributeSet as2)
            throws IncompatibleAttributeset {
        if (!as1.equalsToAttributeSet(as2))
            throw new IncompatibleAttributeset(as1, as2);
    }

    public static void checkLength(AttributeSet as1, AttributeSet as2)
            throws IncompatibleAttributeset {
        if (as1.getAttributeCount() != as2.getAttributeCount())
            throw new IncompatibleAttributeset(as1, as2);
    }

    private static String prepareMessage(AttributeSet as1, AttributeSet as2) {
        String s;
        s = Messages.getString("IncompatibleAttributeset.inconsistentAS"); //$NON-NLS-1$
        s += as1 + ", " + as2; //$NON-NLS-1$
        return s;
    }

    public IncompatibleAttributeset(AttributeSet as1) {
        super(
                Messages.getString("IncompatibleAttributeset.attrSet") + as1 + Messages.getString("IncompatibleAttributeset.isInvalid")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public IncompatibleAttributeset(AttributeSet as1, AttributeSet as2) {
        super(prepareMessage(as1, as2));
    }

    public IncompatibleAttributeset(String s) {
        super(s);
    }
}