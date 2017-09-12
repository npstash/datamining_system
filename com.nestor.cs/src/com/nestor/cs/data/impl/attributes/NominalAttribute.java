//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// NominalAttribute.java (sed.data.attributes)
// Utworzono 2005-05-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.attributes;

import com.nestor.cs.data.def.Test;

public class NominalAttribute extends DiscreteAttribute {

    public NominalAttribute(String labels[]) {
        super(labels);
    }

    // mo�e tworzy� test r�wno�ci lub nier�wno�ci
    // je�li podano inny rodzaj testu, test nie zostanie
    // utworzony
    @Override
    public Test createTest(Object constraint, int testType) {
        if ((testType != Test.EQUAL) && (testType != Test.INEQUAL))
            return null;
        return super.createTest(constraint, testType);
    }

    @Override
    public boolean isSortable() {
        return false;
    }
}