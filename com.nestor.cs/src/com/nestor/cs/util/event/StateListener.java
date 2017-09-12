//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// StateListener.java (javan.event)
// Utworzono 2005-04-15 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.util.EventListener;

public interface StateListener extends EventListener {

    public void stateChanged(StateEvent e);

}