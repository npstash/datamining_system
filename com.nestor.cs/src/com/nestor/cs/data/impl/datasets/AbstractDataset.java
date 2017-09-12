//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractDataset.java (sed.data.datasets)
// Utworzono 2005-05-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets;

import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.Dataset;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.InvalidAttributeValue;
import com.nestor.cs.data.def.ItemsetIterator;
import com.nestor.cs.data.def.ReadOnlyDataset;

public abstract class AbstractDataset extends AbstractReadOnlyDataset implements
        Dataset {

    public AbstractDataset(AttributeSet as) {
        super(as);
    }

    public void append(ReadOnlyDataset ds) throws IncompatibleAttributeset {
        ItemsetIterator it = ds.iterator();
        while (it.hasMoreItemsets()) {
            try {
                addItemset(it.nextItemset());
            } catch (InvalidAttributeValue e) {
                throw new Error();
            }
        }
    }

    public void append(ReadOnlyDataset ds, int[] indexes)
            throws IncompatibleAttributeset {
        for (int i = 0; i < indexes.length; i++) {
            try {
                ds.setCursorAt(indexes[i]);
                addItemset(ds);
            } catch (InvalidAttributeValue e) {
                throw new Error();
            }
        }
    }

}