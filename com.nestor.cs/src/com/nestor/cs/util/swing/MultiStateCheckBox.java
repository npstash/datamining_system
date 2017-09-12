//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ExtendedJCheckBox.java (sed.data.ui)
// Utworzono 2005-07-21 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

import com.nestor.cs.util.event.EventDispatcher;
import com.nestor.cs.util.event.SimpleEventDispatcher;
import com.nestor.cs.util.event.StateEvent;
import com.nestor.cs.util.event.StateListener;

public class MultiStateCheckBox extends JCheckBox implements ActionListener {

    private static ImageIcon[] icons = null;

    public static final int STATE_FALSE = 0;

    public static final int STATE_TRUE = 2;

    public static final int STATE_UNKNOWN = 1;

    public static final int STATES = 3;

    private EventDispatcher eventDispatcher = null;

    private int state = 0;

    public MultiStateCheckBox() {
        super();
        if (icons == null) {
            icons = new ImageIcon[STATES];
            for (int i = 0; i < icons.length; i++) {
                URL u = MultiStateCheckBox.class
                        .getResource("images/checkstate" + i + ".gif"); //$NON-NLS-1$ //$NON-NLS-2$
                icons[i] = new ImageIcon(u);
            }
        }
        addActionListener(this);
        setState(0);
    }

    public void actionPerformed(ActionEvent e) {
        if (getState() < STATES - 1) {
            setState(getState() + 1);
        } else {
            setState(0);
        }
    }

    public void addStateListener(StateListener l) {
        getEventDispatcher().addListener(StateListener.class, l);
    }

    protected void fireState(int newState, int oldState) {
        if (this.eventDispatcher != null) {
            StateEvent se = new StateEvent(this, newState, oldState);
            getEventDispatcher().notifyListeners(StateListener.class, se);
        }
    }

    protected EventDispatcher getEventDispatcher() {
        if (this.eventDispatcher == null) {
            this.eventDispatcher = new SimpleEventDispatcher();
        }
        return this.eventDispatcher;
    }

    public int getState() {
        return this.state;
    }

    public void removeStateListener(StateListener l) {
        getEventDispatcher().removeListener(StateListener.class, l);
    }

    public void setState(int state) {
        if (state > STATES - 1)
            return;
        int oldState = state;
        this.state = state;
        setIcon(icons[state]);
        fireState(state, oldState);
    }
}