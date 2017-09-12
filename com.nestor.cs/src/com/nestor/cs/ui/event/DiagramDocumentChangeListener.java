//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiagramDocumentChangeListener.java (sed.ui)
// Utworzono 2005-07-16 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.event;

import java.util.EventListener;

public interface DiagramDocumentChangeListener extends EventListener {

    public void documentChanged(DiagramDocumentChangeEvent event);

}