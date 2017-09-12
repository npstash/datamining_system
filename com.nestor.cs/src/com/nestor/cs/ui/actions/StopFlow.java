//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// StartFlow.java (sed.ui.actions)
// Utworzono 2005-07-22 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.actions;

import java.awt.event.ActionEvent;

import com.nestor.cs.Messages;
import com.nestor.cs.diagram.Diagram;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.util.event.StateEvent;
import com.nestor.cs.util.proc.Controller;

public class StopFlow extends AbstractProcessAction {

    public StopFlow() {
        super();
        putValue(NAME, Messages.getString("StopFlow.stop")); //$NON-NLS-1$
        putValue(ICON, getButtonIcon(getClass().getResource(
                "images/stop16button.gif"))); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString(getClass().getName()));
    }

    public void actionPerformed(ActionEvent arg0) {
        (new Thread() {
            @Override
            public void run() {
                Diagram doc = SystemFrame.getInstance().mainDesktop
                        .getActiveDocument();
                if (doc == null)
                    return;
                Controller.stop(doc);
            }
        }).start();
    }

    public void stateChanged(StateEvent e) {
        setEnabled(Controller.canStop(e.getIterativeProcess()));
    }

}