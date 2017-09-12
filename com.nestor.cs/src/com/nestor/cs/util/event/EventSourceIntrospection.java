//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// EventSourceIntrospection.java (javan.event)
// Utworzono 2005-04-14 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.io.InvalidClassException;
import java.lang.reflect.Method;
import java.util.EventListener;

import com.nestor.cs.util.Messages;

public abstract class EventSourceIntrospection {

    public static Method getListenerAddMethod(Class eventSourceClass,
            Class listenerType) throws InvalidClassException {
        return getListenerMethod("add", eventSourceClass, listenerType); //$NON-NLS-1$
    }

    public static Method getListenerMethod(String methodNameCore,
            Class eventSourceClass, Class listenerType)
            throws InvalidClassException {
        if (!EventListener.class.isAssignableFrom(listenerType)) {
            throw new InvalidClassException(Messages
                    .getString("EventSourceIntrospection.classNotAListener")); //$NON-NLS-1$
        }
        String listenerName = listenerType.getName().substring(
                listenerType.getName().lastIndexOf('.') + 1);
        if (!listenerName.endsWith("Listener")) { //$NON-NLS-1$
            throw new InvalidClassException(
                    Messages
                            .getString("EventSourceIntrospection.listenerHasInvalidName")); //$NON-NLS-1$
        }
        String lookedUpEvent = listenerName.substring(0, listenerName
                .lastIndexOf("Listener")); //$NON-NLS-1$
        Method[] methods = eventSourceClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            String methodName = methods[i].getName();
            if (!methodName.startsWith(methodNameCore))
                continue;

            if (!methodName.endsWith("Listener")) //$NON-NLS-1$
                continue;

            if (!methodName.substring(methodNameCore.length(),
                    methodName.lastIndexOf("Listener")).equals(lookedUpEvent)) //$NON-NLS-1$
                continue;

            Class[] params = methods[i].getParameterTypes();
            if (params.length != 1)
                continue;
            if (params[0].equals(listenerType)) {
                return methods[i];
            }
        }
        return null;
    }

    public static Method getListenerRemoveMethod(Class eventSourceClass,
            Class listenerType) throws InvalidClassException {
        return getListenerMethod("remove", eventSourceClass, listenerType); //$NON-NLS-1$
    }

    public static boolean isAcceptingListeners(Class eventSourceClass,
            Class listenerType) throws InvalidClassException {
        return (getListenerAddMethod(eventSourceClass, listenerType) != null);
    }

    public static boolean addListenerTo(Object addTo, Class listenerType,
            EventListener l) throws InvalidClassException {
        Method m = getListenerAddMethod(addTo.getClass(), listenerType);
        if (m == null)
            return false;
        try {
            m.invoke(addTo, new Object[] { l });
        } catch (Exception e) {
            // this should not happen since most of requirements
            // are already checked (except of security, which shouldnt matter)
            throw new Error(e);
        }
        return true;
    }

    public static boolean removeListenerFrom(Object removeFrom,
            Class listenerType, EventListener l) throws InvalidClassException {
        Method m = getListenerAddMethod(removeFrom.getClass(), listenerType);
        if (m == null)
            return false;
        try {
            m.invoke(removeFrom, new Object[] { l });
        } catch (Exception e) {
            throw new Error(e);
        }
        return true;
    }

}