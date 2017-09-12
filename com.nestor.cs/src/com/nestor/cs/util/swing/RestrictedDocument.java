//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DoubleValueDocument.java (sed.data.ui)
// Utworzono 2005-07-21 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.swing;

import java.lang.reflect.Constructor;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.impl.attributes.AbstractAttribute;

public class RestrictedDocument extends PlainDocument {

    private Class restriction;

    public RestrictedDocument(Class c) {
        this.restriction = c;
    }

    public static int MAX_STR_LEN = 20;

    @Override
    public void insertString(int pos, String s, AttributeSet as)
            throws BadLocationException {
        if (super.getLength() + s.length() > MAX_STR_LEN)
            return;
        super.insertString(pos, "", as); //$NON-NLS-1$
        String current = super.getText(0, super.getLength());
        String s1 = current.substring(0, pos);
        String s2 = current.substring(pos, current.length());
        String wouldBe = s1 + s + s2;
        if (!wouldBe.equals(Attribute.MISSING_VALUE_STR)) {
            try {
                Constructor c = this.restriction
                        .getConstructor(new Class[] { String.class });
                c.newInstance(new Object[] { wouldBe });
                super.insertString(pos, s, as);
            } catch (Exception e) {
            }
        } else {
            super.insertString(pos, s, as);
        }
    }
}