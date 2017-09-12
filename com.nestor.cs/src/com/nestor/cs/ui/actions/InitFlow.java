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
import com.nestor.cs.util.proc.ProcessingException;

public class InitFlow extends AbstractProcessAction {

    public InitFlow() {
        super();
        super.putValue(NAME, Messages.getString("InitFlow.init")); //$NON-NLS-1$
        putValue(ICON, getButtonIcon(getClass().getResource(
                "images/init16button.gif"))); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString(getClass().getName()));
    }

    public void actionPerformed(ActionEvent arg0) {
        (new Thread() {
            @Override
            public void run() {
                try {
                    Diagram doc = SystemFrame.getInstance().mainDesktop
                            .getActiveDocument();
                    if (doc == null)
                        return;
                    Controller.init(doc);
                } catch (ProcessingException e) {
                    SystemFrame.getInstance().messageArea.showException(e);
                }
            }
        }).start();
    }

    public void stateChanged(StateEvent e) {
        setEnabled(Controller.canInit(e.getIterativeProcess()));
    }
}