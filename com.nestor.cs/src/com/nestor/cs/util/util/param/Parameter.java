//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Parameter.java (javan.util)
// Utworzono 2005-04-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util.param;

import java.io.InvalidClassException;

import com.nestor.cs.util.event.EventDispatcher;
import com.nestor.cs.util.event.SimpleEventDispatcher;
import com.nestor.cs.util.event.UpdateEvent;
import com.nestor.cs.util.event.UpdateListener;

public class Parameter extends Argument {

    protected EventDispatcher dispatcher = null;

    public Parameter() {
        super();
    }

    public Parameter(Object value, EventDispatcher dispatcher) {
        super(value);
        this.dispatcher = dispatcher;
    }

    public Parameter(String name, Object value, ParameterList pl) {
        super(value);
        setName(name);
        pl.addArgument(this);
    }

    public void addUpdateListener(UpdateListener l) {
        getDispatcher().addListener(UpdateListener.class, l);
    }

    protected void fireUpdate(Object oldValue, Object newValue) {
        if ((this.dispatcher != null) && (oldValue != getValue())) {
            UpdateEvent ue = new UpdateEvent(this, oldValue, newValue);
            getDispatcher().notifyListeners(UpdateListener.class, ue);
        }
    }

    protected EventDispatcher getDispatcher() {
        if (this.dispatcher == null) {
            this.dispatcher = new SimpleEventDispatcher();
        }
        return this.dispatcher;
    }

    public void removeUpdateListener(UpdateListener l) {
        getDispatcher().removeListener(UpdateListener.class, l);
    }

    @Override
    public void setValue(Object newValue) throws InvalidClassException,
            ArgumentValueOutOfRange {
        Object oldValue = getValue();
        super.setValue(newValue);
        fireUpdate(oldValue, newValue);
    }

}