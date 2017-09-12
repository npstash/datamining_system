//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ParameterList.java (javan.util)
// Utworzono 2005-04-13 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util.param;

import com.nestor.cs.util.event.EventDispatcher;
import com.nestor.cs.util.event.SimpleEventDispatcher;
import com.nestor.cs.util.event.UpdateListener;

public class ParameterList extends ArgumentList {

    private EventDispatcher sharedDispatcher = null;

    public ParameterList() {
    }

    public ParameterList(ArgumentList aList) {
        for (int i = 0; i < aList.getArgumentCount(); i++)
            addArgument(aList.getArgument(i));
    }

    public ParameterList(ArgumentList aList, EventDispatcher dispatcher) {
        this(aList);
        this.sharedDispatcher = dispatcher;
    }

    public ParameterList(EventDispatcher dispatcher) {
        this.sharedDispatcher = dispatcher;
    }

    @Override
    public void addArgument(Argument arg) {
        super.addArgument(arg);
        if (arg instanceof Parameter) {
            Parameter p = (Parameter) arg;
            if (p.dispatcher == null)
                p.dispatcher = this.sharedDispatcher;
        }
    }

    public void addUpdateListener(UpdateListener l) {
        getEventDispatcher().addListener(UpdateListener.class, l);
    }

    protected EventDispatcher getEventDispatcher() {
        if (this.sharedDispatcher == null) {
            this.sharedDispatcher = new SimpleEventDispatcher();
        }
        return this.sharedDispatcher;
    }

    public void removeUpdateListener(UpdateListener l) {
        getEventDispatcher().removeListener(UpdateListener.class, l);
    }

}