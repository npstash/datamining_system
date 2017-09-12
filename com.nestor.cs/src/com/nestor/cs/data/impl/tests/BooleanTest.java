//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// BooleanTest.java (sed.data.clasf)
// Utworzono 2005-07-30 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.tests;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.impl.attributes.BinaryAttribute;

public class BooleanTest extends AbstractTest {

    public BooleanTest(Boolean constraint, int testType) {
        super(constraint, testType);
        if ((testType != Test.EQUAL) && (testType != Test.INEQUAL))
            throw new IllegalArgumentException();
    }

    public boolean classifies(Object value) {
        if (getConstraint() == null) {
            boolean b = (value == null);
            if (getTestType() == Test.EQUAL)
                return b;
            else
                return !b;
        } else {
            if (getTestType() == Test.EQUAL)
                return getConstraint().equals(value);
            else
                return !getConstraint().equals(value);
        }
    }

    public Test createOppositeTest() {
        return new BooleanTest(getBoolean(), getOppositeTestType());
    }

    public Boolean getBoolean() {
        return (Boolean) getConstraint();
    }

    @Override
    public String toString(Attribute attr) {
        if (attr instanceof BinaryAttribute) {
            BinaryAttribute ba = (BinaryAttribute) attr;
            if (ba.getAllowedValuesNames() == BinaryAttribute.defaultLabels) {
                if ((getTestType() == EQUAL) == getBoolean().booleanValue())
                    return attr.getName();
                else
                    return "!" + attr.getName(); //$NON-NLS-1$
            }
        }
        String res = attr.getName() + getTestTypeString();
        res += attr.getNameForValue(getConstraint());
        return res;
    }
}