//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ConcurrentEventDispatcher.java (javan.event)
// Utworzono 2005-04-15 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.util.EventObject;

public class ConcurrentEventDispatcher extends SimpleEventDispatcher {

    private EventDispatcher dispatcher = null;

    private EventObject event = null;

    private Class listenerType = null;

    public ConcurrentEventDispatcher() {
        this.dispatcher = this;
    }

    private void callSuperclassNotify(Class type, EventObject e) {
        super.notifyListeners(type, e);
    }

    @Override
    public synchronized void notifyListeners(Class type, EventObject e) {
        if (getEventListnerList().getListenerCount() == 0)
            return;
        // no inner event queue implies lack of support for discarding or
        // combinable events
        this.event = e;
        this.listenerType = type;
        (new Thread() {
            @Override
            public void run() {
                EventObject ev = null;
                Class ltype = null;
                synchronized (ConcurrentEventDispatcher.this.dispatcher) {
                    if ((ConcurrentEventDispatcher.this.event == null) || (ConcurrentEventDispatcher.this.listenerType == null))
                        return;
                    ev = ConcurrentEventDispatcher.this.event;
                    ltype = ConcurrentEventDispatcher.this.listenerType;
                    ConcurrentEventDispatcher.this.event = null;
                    ConcurrentEventDispatcher.this.listenerType = null;
                    ConcurrentEventDispatcher.this.dispatcher.notify();
                }
                callSuperclassNotify(ltype, ev);
            }
        }).start();
        try {
            wait();
        } catch (InterruptedException e1) {
            this.event = null;
            this.listenerType = null;
        }
    }
}