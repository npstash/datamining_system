//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiscreteAttribute.java (sed.data.attributes)
// Utworzono 2005-05-09 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.attributes;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.impl.tests.ComparableTest;

public class DiscreteAttribute extends AbstractAttribute {

    private String[] labels;

    public DiscreteAttribute(int labelCount) {
        this.labels = new String[labelCount];
        for (int i = 0; i < this.labels.length; i++) {
            this.labels[i] = "" + i; //$NON-NLS-1$
        }
    }

    public DiscreteAttribute(String[] labels) {
        if (labels == null)
            throw new NullPointerException();
        this.labels = labels;
    }

    public Object cloneValue(Object value) {
        if (!isValueValid(value))
            return null;
        if (value == null)
            return null;
        return new Integer(((Integer) value).intValue());
    }

    public Test createTest(Object constraint, int testType) {
        return new ComparableTest((Integer) constraint, testType);
    }

    public Object getAllowedValue(int index) {
        Integer i = new Integer(index);
        if (!isValueValid(i))
            return null;
        return i;
    }

    public int getAllowedValueIndex(Object value) {
        if (value == null)
            return -1;
        if (!isValueValid(value))
            return -1;
        return ((Integer) value).intValue();
    }

    public int getAllowedValuesCount() {
        return this.labels.length;
    }

    public String[] getAllowedValuesNames() {
        return this.labels;
    }

    public String getNameForValue(Object obj) {
        if (!isValueValid(obj))
            return null;
        if (obj == null)
            return Attribute.MISSING_VALUE_STR;
        return this.labels[((Integer) obj).intValue()];
    }

    public Object getValueFromName(String valueName) {
        for (int i = 0; i < this.labels.length; i++) {
            if (valueName.equals(this.labels[i]))
                return new Integer(i);
        }
        return null;
    }

    public Class getValueType() {
        return Integer.class;
    }

    public boolean isSortable() {
        return true;
    }

    public boolean isValueValid(Object obj) {
        if (obj == null)
            return true;
        if (!(obj instanceof Integer))
            return false;
        int i = ((Integer) obj).intValue();
        return ((i >= 0) && (i < this.labels.length));
    }

    @Override
    public String toString() {
        String s = " ["; //$NON-NLS-1$
        String ls[] = getAllowedValuesNames();
        for (int i = 0; i < ls.length; i++) {
            s += ls[i];
            if (i < ls.length - 1)
                s += ", "; //$NON-NLS-1$
        }
        s += "]"; //$NON-NLS-1$
        return getName() + s;
    }

}