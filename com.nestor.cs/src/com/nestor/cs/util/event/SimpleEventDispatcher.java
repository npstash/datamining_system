//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SimpleEventDispatcher.java (javan.event)
// Utworzono 2005-04-13 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

import com.nestor.cs.util.Messages;

public class SimpleEventDispatcher implements EventDispatcher {

    private EventListenerList listeners = new EventListenerList();

    public SimpleEventDispatcher() {
        super();
    }

    public void addListener(Class type, EventListener l) {
        this.listeners.add(type, l);
    }

    public void addListeners(Class type, EventListener[] l) {
        for (int i = 0; i < l.length; i++) {
            this.listeners.add(type, l[i]);
        }
    }

    protected EventListenerList getEventListnerList() {
        return this.listeners;
    }

    public EventListener[] getListeners() {
        Object[] fullList = this.listeners.getListenerList();
        EventListener[] resList = new EventListener[fullList.length / 2];
        for (int i = 0; i < fullList.length - 1; i += 2) {
            resList[i / 2] = (EventListener) fullList[i + 1];
        }
        return resList;
    }

    public EventListener[] getListeners(Class type) {
        return this.listeners.getListeners(type);
    }

    public void notifyListeners(Class type, EventObject e) {
        // no inner event queue implies lack of support for discarding or
        // combinable events
        EventListener[] l = getListeners(type);
        for (int i = 0; i < l.length; i++) {
            try {
                tryNotifyListener(type, l[i], e);
            } catch (Exception e1) {
                throw new Error(
                        Messages
                                .getString("SimpleEventDispatcher.notifyFailed") + e1.getMessage()); //$NON-NLS-1$
            }
        }
    }

    public void removeAllListeners() {
        Object[] fullList = this.listeners.getListenerList();
        for (int i = 0; i < fullList.length - 1; i += 2) {
            this.listeners.remove((Class) fullList[i],
                    (EventListener) fullList[i + 1]);
        }
    }

    public void removeAllListeners(Class type) {
        Object[] fullList = this.listeners.getListenerList();
        for (int i = 0; i < fullList.length - 1; i += 2) {
            if (fullList[i].equals(type)) {
                this.listeners.remove((Class) fullList[i],
                        (EventListener) fullList[i + 1]);
            }
        }
    }

    public void removeListener(Class type, EventListener l) {
        this.listeners.remove(type, l);
    }

    protected void tryNotifyListener(Class type, EventListener l, EventObject ev)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Method[] methods = type.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Class[] parameters = methods[i].getParameterTypes();
            if (parameters.length != 1)
                continue;
            if (EventObject.class.isAssignableFrom(parameters[0])) {
                methods[i].invoke(l, new Object[] { ev });
            }
        }
    }
}