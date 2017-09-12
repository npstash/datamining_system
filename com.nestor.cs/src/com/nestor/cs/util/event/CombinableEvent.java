//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// CombinableEvent.java (javan.event)
// Utworzono 2005-04-15 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

public interface CombinableEvent {

    public boolean combineEvents(CombinableEvent otherEvent);

}