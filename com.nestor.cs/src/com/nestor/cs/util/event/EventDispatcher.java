//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// EventDispatcher.java (javan.event)
// Utworzono 2005-04-12 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.util.EventListener;
import java.util.EventObject;

public interface EventDispatcher {

    public void addListener(Class type, EventListener l);

    public void addListeners(Class type, EventListener[] l);

    public EventListener[] getListeners(Class type);

    public void notifyListeners(Class type, EventObject e);

    public void removeAllListeners();

    public void removeAllListeners(Class type);

    public void removeListener(Class type, EventListener l);

}