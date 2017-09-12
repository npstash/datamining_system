//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ActiveDocumentChangedListener.java (sed.ui)
// Utworzono 2005-07-22 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.event;

import java.util.EventListener;

public interface ActiveDocumentChangedListener extends EventListener {

    public void activeDocumentChanged(ActiveDocumentChangedEvent event);

}