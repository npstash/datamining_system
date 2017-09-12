//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// BinaryRule.java (sed.data.clasf.jep)
// Utworzono 2005-08-08 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.jep;

import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.BinaryReadOnlyItemset;
import com.nestor.cs.data.def.Rule;
import com.nestor.cs.data.def.Test;

public class BinaryRule extends Rule {

    public BinaryRule(AttributeSet attributeSet, int classValueIndex,
            BinaryReadOnlyItemset bits) {
        super(attributeSet, classValueIndex);

        // zak�ada si� �e w 'bits' nie ma atrybutu klasy
        int binIndex = 0;
        for (int b = 0; b < attributeSet.getAttributeCount(); b++) {
            if (b == attributeSet.getClassLabelIndex())
                continue;
            if (bits.getBinaryValue(binIndex)) {
                Test t = bits.getAttribute(binIndex).createTest(
                        new Boolean(true), Test.EQUAL);
                setTest(b, t);
            }
            binIndex++;
        }
    }

    public BinaryRule(AttributeSet attributeSet, int classValueIndex,
            boolean[] bits) {
        super(attributeSet, classValueIndex);

        int binIndex = 0;
        for (int b = 0; b < attributeSet.getAttributeCount(); b++) {
            if (b == attributeSet.getClassLabelIndex())
                continue;
            if (bits[binIndex]) {
                Test t = attributeSet.getAttribute(b).createTest(
                        new Boolean(true), Test.EQUAL);
                setTest(b, t);
            }
            binIndex++;
        }
    }

}