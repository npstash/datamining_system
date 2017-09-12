//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// GiniIndex.java (sed.data.conversion.discretizers.criteria)
// Utworzono 2005-08-03 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.criteria;

import java.util.Iterator;

import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.DistributionCriteria;
import com.nestor.cs.data.def.ReadOnlyDataset;

public class GiniIndex extends AbstractCriteria implements DistributionCriteria {

    public static double getGiniIndex(Distribution distr) {
        double n = distr.getTotalExamples();
        if (n == 0)
            return 0;

        double gini = 1;
        for (Iterator it = distr.getLabels().iterator(); it.hasNext();) {
            Object label = it.next();
            double pd = (distr.getCount(label)) / n;
            gini -= pd * pd;
        }
        return gini;
    }

    public static double getGiniIndex(Distribution[] distrs) {
        Distribution totalDistribution = Distribution
                .createDistribution(distrs);
        double n = totalDistribution.getTotalExamples();
        if (n == 0)
            return 0;

        double gini = getGiniIndex(totalDistribution);
        for (int i = 0; i < distrs.length; i++) {
            double ni = distrs[i].getTotalExamples();
            double gi = getGiniIndex(distrs[i]);
            gini -= ni / n * gi;
        }
        return gini;
    }

    public Double getMergeCriteriaValue(Distribution[] distributions) {
        return new Double(-getSplitCriteriaValue(distributions).doubleValue());
    }

    public Double getSplitCriteriaValue(Distribution[] distributions) {
        return new Double(getGiniIndex(distributions));
    }

    @Override
    public Double getSplitCriteriaValue(ReadOnlyDataset ds, int[] indexes,
            int intervalStartIndex, int intervalEndIndex,
            int[] candidateSplitPoints, Distribution[] classDistributions) {
        return getSplitCriteriaValue(classDistributions);
    }

    public boolean requiresClassDistribution() {
        return true;
    }
}