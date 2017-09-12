//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// StateEvent.java (nes.event)
// Utworzono 2005-07-27 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.util.EventObject;

import com.nestor.cs.util.proc.Task;

public class StateEvent extends EventObject {

    private int newState, oldState;

    private Task proc = null;

    public StateEvent(Task proc, int newState, int oldState) {
        super(proc);
        this.proc = proc;
        this.newState = newState;
        this.oldState = oldState;
    }

    public StateEvent(Object source, int newState, int oldState) {
        super(source);
        this.newState = newState;
        this.oldState = oldState;
    }

    public Task getIterativeProcess() {
        return this.proc;
    }

    public int getNewState() {
        return this.newState;
    }

    public int getOldState() {
        return this.oldState;
    }

}