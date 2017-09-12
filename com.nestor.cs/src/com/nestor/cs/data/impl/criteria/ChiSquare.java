//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ChiSquare.java (sed.data.conversion.discretizers.splits)
// Utworzono 2005-05-24 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.criteria;

import java.util.Iterator;

import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.DistributionCriteria;
import com.nestor.cs.data.def.ReadOnlyDataset;

public class ChiSquare extends AbstractCriteria implements DistributionCriteria {

    public static double getChi2(Distribution[] distrs) {
        Distribution totalDistribution = Distribution
                .createDistribution(distrs);

        double n = totalDistribution.getTotalExamples();
        if (n == 0)
            return 0;

        double chi2 = 0;
        for (Iterator iter = totalDistribution.getLabels().iterator(); iter
                .hasNext();) {
            Object label = iter.next();

            double m = 0;

            for (int i = 0; i < distrs.length; i++) {
                double aid = distrs[i].getCount(label);
                m += aid;
            }
            m /= n;

            for (int i = 0; i < distrs.length; i++) {
                double aid = distrs[i].getCount(label);
                double ni = distrs[i].getTotalExamples();
                double alfa = ni * m;
                if (alfa != 0) {
                    chi2 += (aid - alfa) * (aid - alfa) / alfa;
                }
            }
        }
        return chi2;
    }

    public Double getMergeCriteriaValue(Distribution[] distributions) {
        return new Double(-getChi2(distributions));
    }

    @Override
    public Double getMergeCriteriaValue(ReadOnlyDataset ds, int[] indexes,
            int intervalStartIndex, int intervalEndIndex,
            int[] candidateSplitPoints, Distribution[] classDistributions) {
        return getMergeCriteriaValue(classDistributions);
    }

    public Double getSplitCriteriaValue(Distribution[] distributions) {
        return new Double(-getMergeCriteriaValue(distributions).doubleValue());
    }

    public boolean requiresClassDistribution() {
        return true;
    }

}