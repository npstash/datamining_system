//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// StepEvent.java (nes.event)
// Utworzono 2005-07-27 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.util.EventObject;

import com.nestor.cs.util.proc.Task;

public class StepEvent extends EventObject {

    private int currentStep;

    public StepEvent(Task proc, int currentStep) {
        super(proc);
        this.currentStep = currentStep;
    }

    public int getCurrentStep() {
        return this.currentStep;
    }

    public Task getIterativeProcess() {
        return (Task) getSource();
    }
}