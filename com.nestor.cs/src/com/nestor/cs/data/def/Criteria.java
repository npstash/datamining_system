//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SplitCreationCriteria.java (sed.data.conversion.discretizers.splits)
// Utworzono 2005-05-23 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public interface Criteria {

    public Double getMergeCriteriaValue(ReadOnlyDataset ds, int[] indexes,
            int intervalStartIndex, int intervalEndIndex,
            int[] candidateSplitPoints, Distribution[] classDistributions);

    public Double getSplitCriteriaValue(ReadOnlyDataset ds, int[] indexes,
            int intervalStartIndex, int intervalEndIndex,
            int[] candidateSplitPoints, Distribution[] classDistributions);

    public boolean requiresClassDistribution();

}