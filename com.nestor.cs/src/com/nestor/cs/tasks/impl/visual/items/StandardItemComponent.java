//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ContinuousItemComponent.java (sed.data.ui)
// Utworzono 2005-07-21 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual.items;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.util.event.EventDispatcher;
import com.nestor.cs.util.event.SimpleEventDispatcher;
import com.nestor.cs.util.event.UpdateEvent;
import com.nestor.cs.util.event.UpdateListener;
import com.nestor.cs.util.swing.RestrictedDocument;

public class StandardItemComponent extends JTextField implements ItemComponent,
        DocumentListener {

    private Attribute attr;

    private EventDispatcher dispatcher = null;

    public StandardItemComponent(Attribute attr, Object value) {
        super();
        this.attr = attr;
        setDocument(new RestrictedDocument(attr.getValueType()));
        setValue(value);
        super.getDocument().addDocumentListener(this);
    }

    public void addUpdateListener(UpdateListener listener) {
        getEventDispatcher().addListener(UpdateListener.class, listener);
    }

    public void changedUpdate(DocumentEvent arg0) {
        fireUpdate(null, getValue());
    }

    protected void fireUpdate(Object oldValue, Object newValue) {
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

    public void insertUpdate(DocumentEvent arg0) {
        fireUpdate(null, getValue());
    }

    public void reinit(Attribute attr, Object value) {
        setValue(value);
    }

    public void removeUpdate(DocumentEvent arg0) {
        fireUpdate(null, getValue());
    }

    public void removeUpdateListener(UpdateListener listener) {
        getEventDispatcher().removeListener(UpdateListener.class, listener);
    }

    public void setValue(Object value) {
        setText(this.attr.getNameForValue(value));
    }
}