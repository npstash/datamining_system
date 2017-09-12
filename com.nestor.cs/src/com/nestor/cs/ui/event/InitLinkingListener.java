//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// CreateLinkListener.java (sed.ui.diagram)
// Utworzono 2005-07-19 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.event;

import java.util.EventListener;

public interface InitLinkingListener extends EventListener {

    public void initLink(InitLinkingEvent event);

}