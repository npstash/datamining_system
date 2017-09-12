//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractAttribute.java (sed.data.attributes)
// Utworzono 2005-05-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.attributes;

import com.nestor.cs.data.def.Attribute;

public abstract class AbstractAttribute implements Attribute {

    private String name = null;

    public boolean equals(Attribute at) {
        if (at == null)
            return false;
        if (!at.getClass().equals(this.getClass()))
            return false;
        if (!at.getValueType().equals(this.getValueType()))
            return false;
        if (!at.getName().equals(this.getName()))
            return false;
        if (at.getAllowedValuesCount() != this.getAllowedValuesCount())
            return false;
        if (this.getAllowedValuesCount() > 0) {
            String[] names1 = this.getAllowedValuesNames();
            String[] names2 = at.getAllowedValuesNames();
            if ((names1 != null) && (names2 == null))
                return false;
            if ((names2 != null) && (names1 == null))
                return false;
            if (names1 != null) {
                for (int i = 0; i < names2.length; i++) {
                    if (!names1[i].equals(names2[i]))
                        return false;
                }
            }
        }
        return true;
    }

    public String getName() {
        if (this.name == null)
            return null;
        if ((this.name.indexOf("=") >= 0) || (this.name.indexOf("<") >= 0) //$NON-NLS-1$ //$NON-NLS-2$
                || (this.name.indexOf(">") >= 0)) //$NON-NLS-1$
            return "'" + this.name + "'"; //$NON-NLS-1$ //$NON-NLS-2$
        else
            return this.name;
    }

    public void setName(String n) {
        this.name = n;
    }

    @Override
    public String toString() {
        return getName() + " [" + getClass().getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

}