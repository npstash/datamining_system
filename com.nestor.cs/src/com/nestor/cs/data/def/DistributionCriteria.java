//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DistributionCriteria.java (sed.data.conversion.discretizers.criteria)
// Utworzono 2005-08-04 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;


public interface DistributionCriteria {

    public Double getMergeCriteriaValue(Distribution[] distributions);

    public Double getSplitCriteriaValue(Distribution[] distributions);

}