//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Test.java (sed.data.clasf)
// Utworzono 2005-07-30 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

import java.io.Serializable;

public interface Test extends Serializable {

    public static final int EQUAL = 1;

    public static final int GREATER = 4;

    public static final int GREATER_OR_EQUAL = 5;

    public static final int INEQUAL = 0;

    public static final int LESS = 2;

    public static final int LESS_OR_EQUAL = 3;

    public boolean classifies(Object value);

    public Test createOppositeTest();

    public Object getConstraint();

    public int getTestType();

    public String toString(Attribute attr);
}