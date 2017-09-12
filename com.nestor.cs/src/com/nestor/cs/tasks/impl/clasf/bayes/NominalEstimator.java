//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// StandardEstimator.java (sed.data.clasf.bayes)
// Utworzono 2005-08-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.bayes;

import com.nestor.cs.data.def.CountMatrix;

class NominalEstimator implements ConditionalEstimator {

    public static final int ESTIMATION_STANDARD = 0;

    public static final int ESTIMATION_LAPLACE = 1;

    public static final int ESTIMATION_M = 2;

    // rozk�ad poziomy (horizontal, X) to rozk�ad etykiet klas
    // rozk�ad pionowy (vertical, Y) to rozk�ad warto�ci cechy
    private CountMatrix matrix;

    private int estimateType;

    private double M = 10;

    public NominalEstimator(CountMatrix matrix, int et) {
        super();
        this.matrix = matrix;
        this.estimateType = et;
    }

    public NominalEstimator(CountMatrix matrix, int et, double m) {
        this(matrix, et);
        this.M = m;
    }

    public double getProbability(Object cLabel, Object xLabel) {
        if (this.estimateType == ESTIMATION_STANDARD) {
            double nc = this.matrix.getVerticalDistribution(cLabel)
                    .getTotalExamples();
            double ncx = this.matrix.getCount(cLabel, xLabel);
            return ncx / nc;
        }
        if (this.estimateType == ESTIMATION_LAPLACE) {
            double nc = this.matrix.getVerticalDistribution(cLabel)
                    .getTotalExamples();
            double c = this.matrix.getTotalLabelsX();
            double ncx = this.matrix.getCount(cLabel, xLabel);
            return (ncx + 1) / (nc + c);
        }
        if (this.estimateType == ESTIMATION_M) {
            double nc = this.matrix.getVerticalDistribution(cLabel)
                    .getTotalExamples();
            double n = this.matrix.getTotalExamples();
            double pc = nc / n;
            double ncx = this.matrix.getCount(cLabel, xLabel);
            return (ncx + this.M * pc) / (nc + this.M);
        }
        return 0;
    }

}