//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// BinaryItemComponent.java (sed.data.ui)
// Utworzono 2005-07-21 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual.items;

import javax.swing.JComponent;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.util.event.StateEvent;
import com.nestor.cs.util.event.StateListener;
import com.nestor.cs.util.event.UpdateEvent;
import com.nestor.cs.util.event.UpdateListener;
import com.nestor.cs.util.swing.MultiStateCheckBox;

public class BinaryItemComponent extends MultiStateCheckBox implements
        ItemComponent, StateListener {

    private Boolean bf = new Boolean(false);

    private Boolean bt = new Boolean(true);

    public BinaryItemComponent(Boolean value) {
        super();
        setValue(value);
        addStateListener(this);
    }

    public void addUpdateListener(UpdateListener listener) {
        getEventDispatcher().addListener(UpdateListener.class, listener);
    }

    protected void fireUpdate(Object oldValue, Object newValue) {
        UpdateEvent ue = new UpdateEvent(this, oldValue, newValue);
        getEventDispatcher().notifyListeners(UpdateListener.class, ue);
    }

    public JComponent getComponent() {
        return this;
    }

    public Object getValue() {
        if (getState() == STATE_UNKNOWN)
            return null;
        return new Boolean(getState() == STATE_TRUE);
    }

    public void reinit(Attribute attr, Object value) {
        setValue(value);
    }

    public void removeUpdateListener(UpdateListener listener) {
        getEventDispatcher().removeListener(UpdateListener.class, listener);
    }

    public void setValue(Object value) {
        if (value == null)
            setState(STATE_UNKNOWN);
        else if (value.equals(this.bt))
            setState(STATE_TRUE);
        else if (value.equals(this.bf))
            setState(STATE_FALSE);
        else
            setState(STATE_UNKNOWN);
    }

    public void stateChanged(StateEvent e) {
        fireUpdate(null, getValue());
    }
}