//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// UpdateEvent.java (javan.event)
// Utworzono 2005-04-14 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.event;

import java.util.EventObject;

public class UpdateEvent extends EventObject implements CombinableEvent {

    private Object newValue = null;

    private Object oldValue = null;

    private long updateID;

    public UpdateEvent(Object source, Object oldValue, Object newValue) {
        super(source);
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.updateID = System.currentTimeMillis();
    }

    public UpdateEvent(Object source, Object oldValue, Object newValue,
            long updateID) {
        super(source);
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.updateID = updateID;
    }

    public boolean combineEvents(CombinableEvent otherEvent) {
        if (!(otherEvent instanceof UpdateEvent))
            return false;

        UpdateEvent e = (UpdateEvent) otherEvent;
        if (e.updateID < this.updateID) {
            if (!this.oldValue.equals(e.oldValue)) {
                this.oldValue = e.oldValue;
                return true;
            } else
                return false;
        } else if (e.updateID > this.updateID) {
            if (!this.newValue.equals(e.newValue)) {
                this.newValue = e.newValue;
                return true;
            } else
                return false;

        }
        // assume IDs are the same
        return false;
    }

    public Object getNewValue() {
        return this.newValue;
    }

    public Object getOldValue() {
        return this.oldValue;
    }
}