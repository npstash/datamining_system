//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// InformationGain.java (sed.data.conversion.discretizers.splits)
// Utworzono 2005-05-23 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.criteria;

import java.util.Collection;
import java.util.Iterator;

import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.DistributionCriteria;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.util.util.MathUtils;

public class InformationGain extends AbstractCriteria implements
        DistributionCriteria {

    public static double getEntropy(Distribution distr) {
        double p = distr.getTotalExamples();
        if (p == 0)
            return 0;
        double e = 0;
        double logBase = distr.getTotalLabels();
        if (logBase == 1)
            return 0;
        Collection labels = distr.getLabels();
        for (Iterator it = labels.iterator(); it.hasNext();) {
            Object label = it.next();
            double pd = distr.getCount(label);
            e += (pd / p) * MathUtils.plog(pd / p, logBase);
        }
        return -e;
    }

    public static double getInformationGain(Distribution[] distr) {
        Distribution totalDistribution = Distribution.createDistribution(distr);
        double n = totalDistribution.getTotalExamples();
        if (n == 0)
            return 0;

        double e = InformationGain.getEntropy(totalDistribution);

        double eI = 0;
        for (int i = 0; i < distr.length; i++) {
            double ei = InformationGain.getEntropy(distr[i]);
            double ni = distr[i].getTotalExamples();
            eI += ni / n * ei;
        }

        return e - eI;
    }

    public Double getMergeCriteriaValue(Distribution[] distributions) {
        return new Double(-getSplitCriteriaValue(distributions).doubleValue());
    }

    public Double getSplitCriteriaValue(Distribution[] distributions) {
        return new Double(getInformationGain(distributions));

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