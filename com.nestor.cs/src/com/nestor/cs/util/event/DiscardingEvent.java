//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiscardingEvent.java (javan.util)
// Utworzono 2005-04-15 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

public interface DiscardingEvent {

    public boolean isDiscardedBy(DiscardingEvent e);

}