//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// InvalidAttributeValue.java (sed.data.attributes)
// Utworzono 2005-05-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

import com.nestor.cs.Messages;

public class InvalidAttributeValue extends Exception {

    public static void checkValidity(Attribute a, Object o)
            throws InvalidAttributeValue {
        if (!a.isValueValid(o))
            throw new InvalidAttributeValue(a, o);
    }

    public static void checkValidity(AttributeSet as, Object[] values)
            throws InvalidAttributeValue {
        for (int i = 0; i < as.getAttributeCount(); i++) {
            if (!as.getAttribute(i).isValueValid(values[i]))
                throw new InvalidAttributeValue(as.getAttribute(i), values[i]);
        }
    }

    public static void checkValidity(AttributeSet as, ReadOnlyItemset it)
            throws InvalidAttributeValue {
        for (int i = 0; i < as.getAttributeCount(); i++) {
            if (!as.getAttribute(i).isValueValid(it.getValue(i)))
                throw new InvalidAttributeValue(as.getAttribute(i), it
                        .getValue(i));
        }
    }

    public static String prepareMessage(Attribute a, Object v) {
        String s = Messages.getString("InvalidAttributeValue.invalidValue"); //$NON-NLS-1$
        s += " '" + v + "' "; //$NON-NLS-1$ //$NON-NLS-2$ 
        s += Messages.getString("InvalidAttributeValue.forAttribute"); //$NON-NLS-1$
        s += " '" + a + "'"; //$NON-NLS-1$ //$NON-NLS-2$
        return s;
    }

    public InvalidAttributeValue(Attribute at, Object value) {
        super(prepareMessage(at, value));
    }

    public InvalidAttributeValue(String s) {
        super(s);
    }

}