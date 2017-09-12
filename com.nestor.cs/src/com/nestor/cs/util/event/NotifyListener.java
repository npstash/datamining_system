//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// NotifyListener.java (javan.event)
// Utworzono 2005-05-08 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.util.EventListener;

public interface NotifyListener extends EventListener {

    public void notify(NotifyEvent ev);

}