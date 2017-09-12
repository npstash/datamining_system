//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// QueueingEventDispatcher.java (javan.event)
// Utworzono 2005-04-13 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.util.Collections;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.nestor.cs.util.Messages;
import com.nestor.cs.util.proc.Semaphore;

public class QueueingEventDispatcher extends SimpleEventDispatcher implements
        Runnable {

    public static long THREAD_EXPIRATION_TIME = 2000;

    private List eventQueue = Collections.synchronizedList(new LinkedList());

    private Semaphore mutex = new Semaphore(1);

    private boolean threadRunning = false;

    private int queueLimit = -1;

    public QueueingEventDispatcher() {
        super();
    }

    public QueueingEventDispatcher(int queueLimit) {
        super();
        this.queueLimit = queueLimit;
    }

    @Override
    public void notifyListeners(Class type, EventObject e) {
        if (getEventListnerList().getListenerCount() == 0)
            return;
        synchronized (this.mutex) {
            // combinable events:
            if (e instanceof CombinableEvent) {
                for (Iterator iter = this.eventQueue.iterator(); iter.hasNext();) {
                    EventObjectWrapper ew = (EventObjectWrapper) iter.next();
                    if (ew.getEvent() instanceof CombinableEvent)
                        if (((CombinableEvent) ew.getEvent())
                                .combineEvents((CombinableEvent) e))
                            return;
                    // if events are combined, this can be now discarded
                }
            }

            // discardable events:
            if (e instanceof DiscardingEvent) {
                for (Iterator iter = this.eventQueue.iterator(); iter.hasNext();) {
                    EventObjectWrapper ew = (EventObjectWrapper) iter.next();
                    if (ew.getEvent() instanceof DiscardingEvent) {
                        DiscardingEvent dev1 = (DiscardingEvent) e;
                        DiscardingEvent dev2 = (DiscardingEvent) ew.getEvent();
                        if (dev1.isDiscardedBy(dev2))
                            return;
                        if (dev2.isDiscardedBy(dev1)) {
                            this.eventQueue.set(this.eventQueue.indexOf(ew),
                                    new EventObjectWrapper(e, type));
                            return;
                        }
                    }
                }
            }

            // check the queue limit, if present.
            // Note: it doesnt work like discarding dispatcher, since
            // it discards incoming events instead of queued
            if (this.queueLimit != -1) {
                if (this.eventQueue.size() == this.queueLimit)
                    return;
            }

            this.eventQueue.add(new EventObjectWrapper(e, type));

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
        }
    }

    public void run() {
        while (true) {
            synchronized (this.mutex) {
                if (!this.mutex.timedLower(THREAD_EXPIRATION_TIME)) {
                    // no event within given time = terminate thread
                    this.threadRunning = false;
                    break;
                }
            }

            while (!this.eventQueue.isEmpty()) {

                // FIFO
                EventObjectWrapper ev = (EventObjectWrapper) this.eventQueue
                        .remove(0);
                EventListener[] list = getListeners(ev.getListenerType());
                for (int i = 0; i < list.length; i++) {
                    EventListener l = list[i];
                    try {
                        tryNotifyListener(ev.getListenerType(), l, ev
                                .getEvent());
                    } catch (Exception e) {
                        throw new Error(
                                Messages
                                        .getString("QueueingEventDispatcher.notifyFailed") + e.getMessage()); //$NON-NLS-1$
                    }
                }
            }
        } // while
    } // run

}