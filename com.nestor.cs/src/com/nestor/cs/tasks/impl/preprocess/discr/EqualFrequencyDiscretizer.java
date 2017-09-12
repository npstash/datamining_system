//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiscretizeEqualFrequency.java (sed.data.processing)
// Utworzono 2005-05-10 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess.discr;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.ItemsetIndexesSelector;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.attributes.DiscreteAttribute;
import com.nestor.cs.data.impl.datasets.Datasets;
import com.nestor.cs.data.impl.datasets.SortedItemsets;
import com.nestor.cs.util.proc.ProcessingException;

public class EqualFrequencyDiscretizer extends AbstractBinningDiscretizer {

    public EqualFrequencyDiscretizer() {
        super();
    }

    public Attribute discretizeAttribute(ReadOnlyDataset trainDs, int attrIndex)
            throws ProcessingException {
        if (!(trainDs.getAttribute(attrIndex).isSortable())) {
            String key = "EqualFrequencyDiscretizer.attributeMustBeSortable"; //$NON-NLS-1$
            throw new ProcessingException(Messages.getString(key));
        }

        ItemsetIndexesSelector iis = new SortedItemsets(attrIndex, true);
        int[] sortedIndexes = iis.selectItemsetIndexes(trainDs);

        double width = ((double) sortedIndexes.length) / (double) getArgBins();
        System.out.println(Messages.getString("EqualFrequencyDiscretizer.0") //$NON-NLS-1$
                + width);
        int currentBin = 1;
        for (int i = 0; i < sortedIndexes.length;) {
            int equalInRow = Datasets.numOfEqualValuesInARow(trainDs,
                    attrIndex, sortedIndexes, i, sortedIndexes.length - 1);
            if (equalInRow == 0)
                throw new Error();
            i += equalInRow;
            if (i == sortedIndexes.length)
                break;
            double shouldBeProcessed = (currentBin) * width;
            if (i >= shouldBeProcessed) {
                trainDs.setCursorAt(sortedIndexes[i - 1]);
                Comparable vLeft = (Comparable) trainDs.getValue(attrIndex);
                trainDs.setCursorAt(sortedIndexes[i]);
                Comparable vRight = (Comparable) trainDs.getValue(attrIndex);
                super.addSplitPoint(vLeft, vRight, attrIndex);
                currentBin++;
            }
        }
        if (currentBin < getArgBins()) {
            try {
                this.argBinCount.setValue(new Integer(currentBin));
            } catch (Exception e) {
            }
        }
        if (getSplitPoints(attrIndex) == null) {
            return null;
        }
        return new DiscreteAttribute(createDefaultLabels(attrIndex));
    }

    public boolean requiresLearning() {
        return true;
    }

}