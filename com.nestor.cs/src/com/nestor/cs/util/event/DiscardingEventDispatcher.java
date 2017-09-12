//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiscardingEventDispatcher.java (javan.event)
// Utworzono 2005-04-13 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.util.EventListener;
import java.util.EventObject;

import com.nestor.cs.util.Messages;
import com.nestor.cs.util.proc.Semaphore;

public class DiscardingEventDispatcher extends SimpleEventDispatcher implements
        Runnable {

    public static long THREAD_EXPIRATION_TIME = 2000;

    private EventObject lastEvent = null;

    private Class listenerType = null;

    private Semaphore mutex = new Semaphore(1);

    private boolean threadRunning = false;

    public DiscardingEventDispatcher(Class listenerType) {
        super();
        // this is invariant across calls, as its not advisable to
        // use this dispatcher for various listner types
        this.listenerType = listenerType;
    }

    @Override
    public void notifyListeners(Class type, EventObject e) {
        if (getEventListnerList().getListenerCount() == 0)
            return;
        synchronized (this.mutex) {
            // possibly discarding previous lastEvent
            // this guarantees to fire most-recent event.

            // respect to combinable events:
            if ((this.lastEvent instanceof CombinableEvent)
                    && (e instanceof CombinableEvent)) {
                if (((CombinableEvent) this.lastEvent)
                        .combineEvents((CombinableEvent) e)) {
                    // only if the combination succeded; otherwise
                    // proceed to discard
                    return;
                }
            } else
            // respect to discarding events: iff older event
            // discards new one
            if ((this.lastEvent instanceof DiscardingEvent)
                    && (e instanceof DiscardingEvent)) {
                if (((DiscardingEvent) e)
                        .isDiscardedBy((DiscardingEvent) this.lastEvent)) {
                    // in this case, discard incoming event
                    return;
                }
            }
            this.lastEvent = e;

            // allow thread to pass locked semaphore
            this.mutex.raise();

            if (!this.threadRunning) {
                try {
                    (new Thread(this)).start();
                    this.threadRunning = true;
                } catch (RuntimeException e1) {
                    this.threadRunning = false;
                    e1.printStackTrace();
                }
            }
        } // synch
    }

    public void run() {
        while (true) {
            EventObject event = null;
            synchronized (this.mutex) {
                if (!this.mutex.timedLower(THREAD_EXPIRATION_TIME)) {
                    // no event within given time = terminate thread
                    this.threadRunning = false;
                    break;
                }
                event = this.lastEvent;
                this.lastEvent = null;
            }

            if (event != null) {
                EventListener[] list = getListeners(this.listenerType);
                for (int i = 0; i < list.length; i++) {
                    EventListener l = list[i];
                    try {
                        tryNotifyListener(this.listenerType, l, event);
                    } catch (Exception e1) {
                        throw new Error(
                                Messages
                                        .getString("DiscardingEventDispatcher.notifyFailed") + e1.getMessage()); //$NON-NLS-1$
                    }
                } // for
            } // if
        } // while
    }

}