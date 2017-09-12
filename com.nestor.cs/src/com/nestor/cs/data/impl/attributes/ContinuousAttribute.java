//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ContinuousAttribute.java (sed.data.attributes)
// Utworzono 2005-05-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.attributes;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.impl.tests.ComparableTest;

public class ContinuousAttribute extends AbstractAttribute {

    public Object cloneValue(Object value) {
        if (!isValueValid(value))
            return null;
        if (value == null)
            return null;
        return new Double(((Double) value).doubleValue());
    }

    public Test createTest(Object constraint, int testType) {
        return new ComparableTest((Double) constraint, testType);
    }

    public Object getAllowedValue(int index) {
        return null;
    }

    public int getAllowedValueIndex(Object value) {
        return -1;
    }

    public int getAllowedValuesCount() {
        return Attribute.INFINITE_VALUES;
    }

    public String[] getAllowedValuesNames() {
        return null;
    }

    public String getNameForValue(Object obj) {
        if (!isValueValid(obj))
            return null;
        if (obj == null)
            return Attribute.MISSING_VALUE_STR;
        if (((Double) obj).doubleValue() == ((Double) obj).intValue())
            return "" + ((Double) obj).intValue(); //$NON-NLS-1$
        else
            return obj.toString();
    }

    public Object getValueFromName(String valueName) {
        try {
            double res = Double.parseDouble(valueName);
            return new Double(res);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Class getValueType() {
        return Double.class;
    }

    public boolean isSortable() {
        return true;
    }

    public boolean isValueValid(Object obj) {
        if (obj == null)
            return true;
        return obj instanceof Double;
    }

    @Override
    public String toString() {
        return getName()
                + " [" + Messages.getString("ContinuousAttribute.contiuousAttribute") + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

}