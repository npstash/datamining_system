//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// JSelectedCheckBoxMenuItem.java (sed.ui)
// Utworzono 2005-07-22 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;

public class ExtJCheckBoxMenuItem extends JCheckBoxMenuItem implements
        PropertyChangeListener {

    public ExtJCheckBoxMenuItem() {
        super();
    }

    public ExtJCheckBoxMenuItem(Action a) {
        super(a);
    }

    public void propertyChange(PropertyChangeEvent event) {
        setExtProperties(event.getPropertyName(), event.getNewValue());
    }

    @Override
    public void setAction(Action a) {
        if (getAction() != null) {
            getAction().removePropertyChangeListener(this);
        }
        super.setAction(a);
        if (getAction() != null) {
            getAction().addPropertyChangeListener(this);
        }
        if (a instanceof ExtAbstractAction) {
            ExtAbstractAction ea = (ExtAbstractAction) a;
            if (ea.getValue(ExtAbstractAction.SELECTED) != null)
                setExtProperties(ExtAbstractAction.SELECTED, ea
                        .getValue(ExtAbstractAction.SELECTED));
            if (ea.getValue(ExtAbstractAction.ENABLED) != null)
                setExtProperties(ExtAbstractAction.ENABLED, ea
                        .getValue(ExtAbstractAction.ENABLED));
        }
    }

    private void setExtProperties(String name, Object value) {
        if (name.equals(ExtAbstractAction.ENABLED)) {
            if (value instanceof Boolean) {
                setEnabled(((Boolean) value).booleanValue());
            }
        } else if (name.equals(ExtAbstractAction.SELECTED)) {
            if (value instanceof Boolean) {
                setSelected(((Boolean) value).booleanValue());
            }
        }
    }
}