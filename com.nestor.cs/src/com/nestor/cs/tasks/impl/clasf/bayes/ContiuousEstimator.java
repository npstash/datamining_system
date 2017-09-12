//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ContiuousEstimator.java (sed.data.clasf.bayes)
// Utworzono 2005-08-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.bayes;

import com.nestor.cs.data.def.ProbDistribution;

class ContiuousEstimator implements ConditionalEstimator {

    // warto�ci (�rednia, wariancja) dla poszczeg�lnych
    // etykiet klasy
    private ProbDistribution dMean;

    private ProbDistribution dStdDev;

    public ContiuousEstimator(ProbDistribution mean, ProbDistribution stdDev) {
        super();
        this.dMean = mean;
        this.dStdDev = stdDev;
    }

    public double getProbability(Object cLabel, Object xLabel) {
        double mean = this.dMean.getProbability(cLabel);
        double var = this.dStdDev.getProbability(cLabel);
        double x = ((Double) xLabel).doubleValue();

        double p = (x - mean) / var;
        p = -0.5 * p * p;
        p = Math.exp(p);
        p = p / (var * Math.sqrt(Math.PI * 2));
        return p;
    }

}