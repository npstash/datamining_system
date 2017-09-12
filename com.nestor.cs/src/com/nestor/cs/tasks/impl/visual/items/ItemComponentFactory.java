//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ItemComponents.java (sed.data.ui)
// Utworzono 2005-07-21 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual.items;

import java.util.Hashtable;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.impl.attributes.BinaryAttribute;
import com.nestor.cs.data.impl.attributes.ContinuousAttribute;
import com.nestor.cs.data.impl.attributes.DiscreteAttribute;
import com.nestor.cs.data.impl.attributes.FuzzyAttribute;
import com.nestor.cs.data.impl.attributes.NominalAttribute;

public abstract class ItemComponentFactory {

    private static Hashtable sharedItemComponents;

    public static ItemComponent createBinaryItemComponent(BinaryAttribute attr,
            Boolean value) {
        return new BinaryItemComponent(value);
    }

    public static ItemComponent createContinuousItemComponent(
            ContinuousAttribute attr, Double value) {
        return new StandardItemComponent(attr, value);
    }

    public static ItemComponent createDiscreteItemComponent(
            DiscreteAttribute attr, Integer value) {
        return new StandardItemComponent(attr, value);
    }

    public static ItemComponent createFuzzyItemComponent(FuzzyAttribute attr,
            FuzzyAttribute.FuzzyValue value) {
        return new FuzzyItemComponent(attr, value);
    }

    public static ItemComponent createItemComponent(Attribute attr, Object value) {
        if (attr instanceof ContinuousAttribute) {
            return createContinuousItemComponent((ContinuousAttribute) attr,
                    (Double) value);

        } else if (attr instanceof BinaryAttribute) {
            return createBinaryItemComponent((BinaryAttribute) attr,
                    (Boolean) value);

        } else if (attr instanceof FuzzyAttribute) {
            return createFuzzyItemComponent((FuzzyAttribute) attr,
                    (FuzzyAttribute.FuzzyValue) value);

        } else if (attr instanceof NominalAttribute) {
            return createNominalItemComponent((NominalAttribute) attr,
                    (Integer) value);

        } else if (attr instanceof DiscreteAttribute) {
            return createDiscreteItemComponent((DiscreteAttribute) attr,
                    (Integer) value);
        }
        return null;
    }

    public static ItemComponent createNominalItemComponent(
            NominalAttribute attr, Integer value) {
        return new NominalItemComponent(attr, value);
    }

    public static ItemComponent getSharedItemComponent(Attribute attr,
            Object value) {
        if (sharedItemComponents == null) {
            sharedItemComponents = new Hashtable();
        }
        ItemComponent ic = (ItemComponent) sharedItemComponents.get(attr
                .getClass());
        if (ic == null) {
            ic = createItemComponent(attr, value);
            sharedItemComponents.put(attr.getClass(), ic);
        }
        ic.reinit(attr, value);
        return ic;
    }

}