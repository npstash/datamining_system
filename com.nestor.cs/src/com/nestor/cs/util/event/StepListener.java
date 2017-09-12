//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// StepListener.java (nes.event)
// Utworzono 2005-07-27 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.util.EventListener;

public interface StepListener extends EventListener {

    public void stepIncreased(StepEvent e);

}