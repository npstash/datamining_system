//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// BinaryAttribute.java (sed.data.attributes)
// Utworzono 2005-05-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.attributes;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.impl.tests.BooleanTest;

public class BinaryAttribute extends NominalAttribute {

    public static String[] defaultLabels = { "n", "y" }; //$NON-NLS-1$ //$NON-NLS-2$

    public static int isLabelANO(String l) {
        if (l == null)
            return -2;
        if (l.equalsIgnoreCase("no")) //$NON-NLS-1$
            return 0;
        if (l.equalsIgnoreCase("n")) //$NON-NLS-1$
            return 1;
        if (l.equalsIgnoreCase("false")) //$NON-NLS-1$
            return 2;
        if (l.equalsIgnoreCase("f")) //$NON-NLS-1$
            return 3;
        if (l.equalsIgnoreCase("0")) //$NON-NLS-1$
            return 4;
        if (l.equalsIgnoreCase("fa�sz")) //$NON-NLS-1$
            return 5;
        if (l.equalsIgnoreCase("falsz")) //$NON-NLS-1$
            return 5;
        if (l.equalsIgnoreCase("nie")) //$NON-NLS-1$
            return 6;
        return -2;
    }

    public static int isLabelAYES(String l) {
        if (l == null)
            return -1;
        if (l.equalsIgnoreCase("yes")) //$NON-NLS-1$
            return 0;
        if (l.equalsIgnoreCase("y")) //$NON-NLS-1$
            return 1;
        if (l.equalsIgnoreCase("true")) //$NON-NLS-1$
            return 2;
        if (l.equalsIgnoreCase("t")) //$NON-NLS-1$
            return 3;
        if (l.equalsIgnoreCase("1")) //$NON-NLS-1$
            return 4;
        if (l.equalsIgnoreCase("prawda")) //$NON-NLS-1$
            return 5;
        if (l.equalsIgnoreCase("tak")) //$NON-NLS-1$
            return 6;
        return -1;
    }

    public static boolean isValueListABinary(String[] values) {
        if (values.length != 2)
            return false;
        if ((values[0] == null) || (values[1] == null))
            return false;
        for (int i = 0; i < 2; i++) {
            if ((isLabelANO(values[i]) < 0) && (isLabelANO(values[1 - i]) < 0))
                return false;
            if ((isLabelAYES(values[i]) < 0)
                    && (isLabelAYES(values[1 - i]) < 0))
                return false;
            if (isLabelANO(values[i]) == isLabelAYES(values[1 - i]))
                return true;
        }
        return false;
    }

    private int indexOfTrue = 1;

    private int indexOfFalse = 0;

    public BinaryAttribute() {
        super(defaultLabels);
    }

    public BinaryAttribute(String[] labels) {
        super(labels);
        if (isLabelANO(labels[0]) == isLabelAYES(labels[1])) {
            this.indexOfFalse = 0;
            this.indexOfTrue = 1;
        } else if (isLabelANO(labels[1]) == isLabelAYES(labels[0])) {
            this.indexOfFalse = 1;
            this.indexOfTrue = 0;
        }
    }

    @Override
    public Object cloneValue(Object value) {
        if (!isValueValid(value))
            return null;
        if (value == null)
            return null;
        return new Boolean(((Boolean) value).booleanValue());
    }

    @Override
    public Test createTest(Object constraint, int testType) {
        return new BooleanTest((Boolean) constraint, testType);
    }

    @Override
    public Object getAllowedValue(int index) {
        if (index == this.indexOfFalse)
            return new Boolean(false);
        if (index == this.indexOfTrue)
            return new Boolean(true);
        return null;
    }

    @Override
    public int getAllowedValueIndex(Object value) {
        if (value instanceof Boolean) {
            return (((Boolean) value).booleanValue()) ? this.indexOfTrue
                    : this.indexOfFalse;
        }
        return -1;
    }

    @Override
    public int getAllowedValuesCount() {
        return 2;
    }

    @Override
    public String getNameForValue(Object obj) {
        if (!isValueValid(obj))
            return null;
        if (obj == null)
            return Attribute.MISSING_VALUE_STR;
        boolean v = ((Boolean) obj).booleanValue();
        return v ? getAllowedValuesNames()[this.indexOfTrue]
                : getAllowedValuesNames()[this.indexOfFalse];
    }

    @Override
    public Object getValueFromName(String valueName) {
        Integer i = (Integer) super.getValueFromName(valueName);
        if (i == null)
            return null;
        if (i.intValue() == this.indexOfFalse)
            return new Boolean(false);
        else
            return new Boolean(true);
    }

    @Override
    public Class getValueType() {
        return Boolean.class;
    }

    @Override
    public boolean isSortable() {
        return false;
    }

    @Override
    public boolean isValueValid(Object obj) {
        if (obj == null)
            return true;
        return obj instanceof Boolean;
    }

    @Override
    public String toString() {
        String s = getName() + " ["; //$NON-NLS-1$
        s += Messages.getString("BinaryAttribute.binaryAttribute"); //$NON-NLS-1$ 
        s += " "; //$NON-NLS-1$
        String ls[] = getAllowedValuesNames();
        for (int i = 0; i < ls.length; i++) {
            s += ls[i];
            if (i < ls.length - 1)
                s += ", "; //$NON-NLS-1$
        }
        s += "]"; //$NON-NLS-1$
        return s;
    }
}