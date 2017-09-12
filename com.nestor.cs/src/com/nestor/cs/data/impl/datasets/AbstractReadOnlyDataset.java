//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractReadOnlyDataset.java (sed.data.datasets)
// Utworzono 2005-05-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets;

import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.ItemsetIterator;
import com.nestor.cs.data.def.ItemsetSelector;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;

public abstract class AbstractReadOnlyDataset extends AbstractReadOnlyItemset
        implements ReadOnlyDataset {

    private class ReadOnlyItemsetIterator implements ItemsetIterator {

        private int currentPos = 0;

        private ItemsetSelector is = null;

        private ReadOnlyItemsetIterator() {
        }

        private ReadOnlyItemsetIterator(ItemsetSelector is) {
            this.is = is;
        }

        private boolean findNext() {
            do {
                setCursorAt(this.currentPos);
                if (this.is.selectItemset(AbstractReadOnlyDataset.this)) {
                    return true;
                } else {
                    if (this.currentPos < size() - 1)
                        this.currentPos++;
                    else
                        return false;
                }
            } while (true);
        }

        public boolean hasMoreItemsets() {
            if (this.currentPos >= size())
                return false;
            if (this.is == null)
                return true;
            else
                return findNext();

        }

        public ReadOnlyItemset nextItemset() {
            if (!hasMoreItemsets())
                return null;
            setCursorAt(this.currentPos++);
            return AbstractReadOnlyDataset.this;
        }
    }

    private String description = null;

    private String name = null;

    public AbstractReadOnlyDataset(AttributeSet as) {
        super(as);
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }

    public ItemsetIterator iterator() {
        return new ReadOnlyItemsetIterator();
    }

    public ItemsetIterator iterator(ItemsetSelector is) {
        return new ReadOnlyItemsetIterator(is);
    }

    public void setDescription(String d) {
        this.description = d;
    }

    public void setName(String n) {
        this.name = n;
    }
}