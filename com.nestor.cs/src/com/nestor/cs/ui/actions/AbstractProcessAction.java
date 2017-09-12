//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractProcessAction.java (sed.ui.actions)
// Utworzono 2005-07-27 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.actions;

import com.nestor.cs.diagram.Diagram;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.ui.event.ActiveDocumentChangedEvent;
import com.nestor.cs.ui.event.ActiveDocumentChangedListener;
import com.nestor.cs.util.event.StateEvent;
import com.nestor.cs.util.event.StateListener;
import com.nestor.cs.util.proc.Task;
import com.nestor.cs.util.swing.ExtAbstractAction;

public abstract class AbstractProcessAction extends ExtAbstractAction implements
        ActiveDocumentChangedListener, StateListener {

    public AbstractProcessAction() {
        super();
        SystemFrame.getInstance().mainDesktop
                .addActiveDocumentChangedListener(this);
        setEnabled(SystemFrame.getInstance().mainDesktop.getActiveDocument() != null);
    }

    public void activeDocumentChanged(ActiveDocumentChangedEvent event) {
        Diagram doc = event.getNewDocument();
        setEnabled(doc != null);
        if (event.getOldDocument() != null) {
            event.getOldDocument().removeStateListener(this);
        }
        if (doc != null) {
            doc.addStateListener(this);
            stateChanged(new StateEvent(doc, doc.getProcessState(),
                    Task.NONE));
        }
    }

}