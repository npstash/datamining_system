//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Filter.java (sed.data.filters)
// Utworzono 2005-05-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.def;

import com.nestor.cs.util.proc.Initiable;
import com.nestor.cs.util.util.param.ArgumentList;

public interface ConfigurableTask extends Initiable {

    public ArgumentList getConfigurationArguments();

}