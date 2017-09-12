//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// FuzzyItemComponent.java (sed.data.ui)
// Utworzono 2005-07-21 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual.items;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.impl.attributes.FuzzyAttribute;
import com.nestor.cs.util.event.EventDispatcher;
import com.nestor.cs.util.event.SimpleEventDispatcher;
import com.nestor.cs.util.event.UpdateEvent;
import com.nestor.cs.util.event.UpdateListener;

public class FuzzyItemComponent extends JLabel implements ItemComponent {

    private FuzzyAttribute attr;

    private EventDispatcher dispatcher = null;

    public FuzzyItemComponent(FuzzyAttribute attr,
            FuzzyAttribute.FuzzyValue value) {
        super();
        this.attr = attr;
        setValue(value);
    }

    public void addUpdateListener(UpdateListener listener) {
        getEventDispatcher().addListener(UpdateListener.class, listener);
    }

    protected void fireUpdate(Object oldValue, Object newValue) {
        // w chwili obenej bez efektu, warto�ci rozmytych
        // nie mo�na edytowa�
        if (this.dispatcher != null) {
            UpdateEvent ue = new UpdateEvent(this, oldValue, newValue);
            getEventDispatcher().notifyListeners(UpdateListener.class, ue);
        }
    }

    public JComponent getComponent() {
        return this;
    }

    protected EventDispatcher getEventDispatcher() {
        if (this.dispatcher == null) {
            this.dispatcher = new SimpleEventDispatcher();
        }
        return this.dispatcher;
    }

    public Object getValue() {
        return this.attr.getValueFromName(getText());
    }

    public void reinit(Attribute attr, Object value) {
        this.attr = (FuzzyAttribute) attr;
        setValue(value);
    }

    public void removeUpdateListener(UpdateListener listener) {
        getEventDispatcher().removeListener(UpdateListener.class, listener);
    }

    public void setValue(Object value) {
        setText(this.attr.getNameForValue(value));
    }

}