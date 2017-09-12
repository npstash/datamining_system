//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// EventObjectWrapper.java (javan.event)
// Utworzono 2005-04-13 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.util.EventObject;

public class EventObjectWrapper {

    private EventObject event = null;

    private Class listenerType = null;

    public EventObjectWrapper(EventObject event, Class listenerType) {
        super();
        this.event = event;
        this.listenerType = listenerType;
    }

    public EventObject getEvent() {
        return this.event;
    }

    public Class getListenerType() {
        return this.listenerType;
    }
}