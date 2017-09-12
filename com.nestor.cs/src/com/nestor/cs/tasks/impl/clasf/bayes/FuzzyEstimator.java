//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// FuzzyEstimator.java (sed.data.clasf.bayes)
// Utworzono 2005-08-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.bayes;

import java.util.Iterator;

import com.nestor.cs.data.def.ProbMatrix;
import com.nestor.cs.data.impl.attributes.FuzzyAttribute;

public class FuzzyEstimator implements ConditionalEstimator {

    private int estimateType;

    private double M = 10;

    private ProbMatrix matrix;

    public FuzzyEstimator(ProbMatrix matrix, int et) {
        this.matrix = matrix;
        this.estimateType = et;
    }

    public FuzzyEstimator(ProbMatrix matrix, int et, double m) {
        this(matrix, et);
        this.M = m;
    }

    public double getProbability(Object cLabel, Object xLabel) {
        if (!(xLabel instanceof FuzzyAttribute.FuzzyValue))
            return 0;
        FuzzyAttribute.FuzzyValue fv = (FuzzyAttribute.FuzzyValue) xLabel;

        double sum = 0;
        for (Iterator iter = this.matrix.getLabelsY().iterator(); iter.hasNext();) {
            Integer iPart = (Integer) iter.next();
            double vm = fv.getValueMembership(iPart);
            sum += getProbabilityPart(cLabel, iPart) * vm;
        }
        return sum;
    }

    private double getProbabilityPart(Object cLabel, Integer part) {
        if (this.estimateType == NominalEstimator.ESTIMATION_STANDARD) {
            double nc = this.matrix.getVerticalProbDistribution(cLabel)
                    .getProbabilitiesSum();
            double ncx = this.matrix.getProbability(cLabel, part);
            return ncx / nc;
        }
        if (this.estimateType == NominalEstimator.ESTIMATION_LAPLACE) {
            double nc = this.matrix.getVerticalProbDistribution(cLabel)
                    .getProbabilitiesSum();
            double c = this.matrix.getTotalLabelsX();
            double ncx = this.matrix.getProbability(cLabel, part);
            return (ncx + 1) / (nc + c);
        }
        if (this.estimateType == NominalEstimator.ESTIMATION_M) {
            double nc = this.matrix.getVerticalProbDistribution(cLabel)
                    .getProbabilitiesSum();
            double n = this.matrix.getProbabilitiesSum();
            double pc = nc / n;
            double ncx = this.matrix.getProbability(cLabel, part);
            return (ncx + this.M * pc) / (nc + this.M);
        }
        return 0;
    }

}