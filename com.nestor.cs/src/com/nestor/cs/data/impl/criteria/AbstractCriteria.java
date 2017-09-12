//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractCriteria.java (sed.data.conversion.discretizers.criteria)
// Utworzono 2005-07-30 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.criteria;

import com.nestor.cs.data.def.Criteria;
import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.ReadOnlyDataset;

public abstract class AbstractCriteria implements Criteria {

    public Double getMergeCriteriaValue(ReadOnlyDataset ds, int[] indexes,
            int intervalStartIndex, int intervalEndIndex,
            int[] candidateSplitPoints, Distribution[] classDistributions) {

        Double splitCrit = getSplitCriteriaValue(ds, indexes,
                intervalStartIndex, intervalEndIndex, candidateSplitPoints,
                classDistributions);

        if (splitCrit == null)
            return null;

        return new Double(-splitCrit.doubleValue());
    }

    public Double getSplitCriteriaValue(ReadOnlyDataset ds, int[] indexes,
            int intervalStartIndex, int intervalEndIndex,
            int[] candidateSplitPoints, Distribution[] classDistributions) {

        Double mergeCrit = getMergeCriteriaValue(ds, indexes,
                intervalStartIndex, intervalEndIndex, candidateSplitPoints,
                classDistributions);

        if (mergeCrit == null)
            return null;

        return new Double(-mergeCrit.doubleValue());
    }

}