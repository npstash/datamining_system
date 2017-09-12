//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// PCX_Estimator.java (sed.data.clasf.bayes)
// Utworzono 2005-08-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.bayes;

interface ConditionalEstimator {

    // zwraca estymacj� prawdopodonie�sta P(A|C)
    public double getProbability(Object cLabel, Object xLabel);

}