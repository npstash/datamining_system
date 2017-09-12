//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// InformationGainRatio.java (sed.data.conversion.discretizers.splits)
// Utworzono 2005-05-23 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.criteria;

import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.util.util.MathUtils;

public class InformationGainRatio extends InformationGain {

    public double getInformationGainRatio(Distribution[] distrs) {
        double IG = getInformationGain(distrs);
        Distribution totalDistribution = Distribution
                .createDistribution(distrs);
        double n = totalDistribution.getTotalExamples();
        if (n == 0)
            return 0;

        double ii = 0;
        for (int i = 0; i < distrs.length; i++) {
            double ni = distrs[i].getTotalExamples();
            ii += -(ni / n) * MathUtils.plog(ni / n, 2);

        }
        return IG / ii;
    }

    @Override
    public Double getSplitCriteriaValue(Distribution[] distributions) {
        return new Double(getInformationGainRatio(distributions));
    }

}