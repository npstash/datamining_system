//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Attribute.java (sed.data.interfaces)
// Utworzono 2005-04-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

import java.io.Serializable;

public interface Attribute extends Serializable {

    public static final int INFINITE_VALUES = -1;

    public static final String MISSING_VALUE_STR = "?"; //$NON-NLS-1$

    public Object cloneValue(Object value);

    public Test createTest(Object constraint, int testType);

    public boolean equals(Attribute at);

    public Object getAllowedValue(int index);

    public int getAllowedValueIndex(Object value);

    public int getAllowedValuesCount();

    public String[] getAllowedValuesNames();

    public String getName();

    public String getNameForValue(Object obj);

    public Object getValueFromName(String valueName);

    public Class getValueType();

    public boolean isSortable();

    public boolean isValueValid(Object obj);

    public void setName(String n);
}