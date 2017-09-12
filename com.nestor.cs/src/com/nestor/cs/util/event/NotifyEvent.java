//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// NotifyEvent.java (javan.event)
// Utworzono 2005-04-14 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.util.EventObject;

public class NotifyEvent extends EventObject implements DiscardingEvent {

    public NotifyEvent(Object source) {
        super(source);
    }

    public boolean isDiscardedBy(DiscardingEvent e) {
        if (!(e instanceof NotifyEvent))
            return false;
        // any two NotifyEvents are discardible
        return true;
    }

}