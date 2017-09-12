//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ConfigurableObjectSelectionListener.java (sed.ui.event)
// Utworzono 2005-08-02 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.event;

import java.util.EventListener;

public interface ObjectSelectionListener extends EventListener {

    public void objectSelected(ObjectSelectionEvent e);

}