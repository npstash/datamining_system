//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// UpdateListener.java (javan.event)
// Utworzono 2005-04-13 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.util.EventListener;

public interface UpdateListener extends EventListener {

    public void update(UpdateEvent e);

}