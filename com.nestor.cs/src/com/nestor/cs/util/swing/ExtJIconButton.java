//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ExtJButton.java (nes.swing)
// Utworzono 2005-07-27 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class ExtJIconButton extends JButton implements PropertyChangeListener {

    public ExtJIconButton() {
        super();
    }

    public ExtJIconButton(Action a) {
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

            if (ea.getValue(ExtAbstractAction.ICON) != null)
                setExtProperties(ExtAbstractAction.ICON, ea
                        .getValue(ExtAbstractAction.ICON));

            if (ea.getValue(ExtAbstractAction.BUTTON_TOOLTIP) != null)
                setExtProperties(ExtAbstractAction.BUTTON_TOOLTIP, ea
                        .getValue(ExtAbstractAction.BUTTON_TOOLTIP));

            if (ea.getValue(Action.NAME) != null)
                setExtProperties(Action.NAME, ea
                        .getValue(Action.NAME));
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
        } else if (name.equals(ExtAbstractAction.ICON)) {
            if (value instanceof Icon) {
                setIcon((Icon) value);
            }
        } else if (name.equals(ExtAbstractAction.BUTTON_TOOLTIP)) {
            if (value instanceof String) {
                setToolTipText((String) value);
            }
        } else if (name.equals(Action.NAME)) {
            if (value instanceof String) {
                setText(""); //$NON-NLS-1$
            }
        }
    }

}