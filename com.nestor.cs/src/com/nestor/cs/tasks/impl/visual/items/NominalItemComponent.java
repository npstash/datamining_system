//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// NominatItemComponent.java (sed.data.ui)
// Utworzono 2005-07-21 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual.items;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.impl.attributes.AbstractAttribute;
import com.nestor.cs.data.impl.attributes.NominalAttribute;
import com.nestor.cs.util.event.EventDispatcher;
import com.nestor.cs.util.event.SimpleEventDispatcher;
import com.nestor.cs.util.event.UpdateEvent;
import com.nestor.cs.util.event.UpdateListener;

public class NominalItemComponent extends JComboBox implements ItemComponent,
        ItemListener {

    private class DataModel extends DefaultComboBoxModel implements
            ComboBoxModel {

        private int selected = 0;

        private void attrChanged() {
            fireContentsChanged(this, 0, NominalItemComponent.this.attr
                    .getAllowedValuesCount());
        }

        @Override
        public Object getElementAt(int i) {
            if (i < NominalItemComponent.this.attr.getAllowedValuesCount())
                return NominalItemComponent.this.attr
                        .getNameForValue(new Integer(i));
            else
                return Attribute.MISSING_VALUE_STR;
        }

        @Override
        public Object getSelectedItem() {
            return getElementAt(this.selected);
        }

        @Override
        public int getSize() {
            return NominalItemComponent.this.attr.getAllowedValuesCount() + 1;
        }

        public void setSelected(int selected) {
            this.selected = selected;
        }

        @Override
        public void setSelectedItem(Object o) {
            int index;
            if (o.equals(Attribute.MISSING_VALUE_STR))
                index = NominalItemComponent.this.attr.getAllowedValuesCount();
            else
                index = NominalItemComponent.this.attr
                        .getAllowedValueIndex(NominalItemComponent.this.attr
                                .getValueFromName(o.toString()));
            setSelected(index);
        }
    }

    private NominalAttribute attr;

    private EventDispatcher dispatcher = null;

    public NominalItemComponent(NominalAttribute attr, Integer value) {
        super();
        this.attr = attr;
        setModel(new DataModel());
        setValue(value);
        addItemListener(this);
    }

    public void addUpdateListener(UpdateListener listener) {
        getEventDispatcher().addListener(UpdateListener.class, listener);
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
        if (getSelectedIndex() < this.attr.getAllowedValuesCount())
            return new Integer(getSelectedIndex());
        else
            return null;
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED)
            return;
        fireUpdate(null, getValue());
    }

    public void reinit(Attribute attr, Object value) {
        this.attr = (NominalAttribute) attr;
        ((DataModel) getModel()).attrChanged();
        setValue(value);
    }

    public void removeUpdateListener(UpdateListener listener) {
        getEventDispatcher().removeListener(UpdateListener.class, listener);
    }

    public void setValue(Object value) {
        int index = 0;

        if (value == null)
            index = this.attr.getAllowedValuesCount();
        else
            index = ((Integer) value).intValue();
        setSelectedIndex(index);
    }

}