//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ActiveDocumentChangedEvent.java (sed.ui)
// Utworzono 2005-07-22 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.event;

import java.util.EventObject;

import com.nestor.cs.diagram.Diagram;
import com.nestor.cs.ui.SystemFrame;

public class ActiveDocumentChangedEvent extends EventObject {

    private Diagram newDoc;

    private Diagram oldDoc;

    public ActiveDocumentChangedEvent(Diagram oldDoc, Diagram newDoc) {
        super(SystemFrame.getInstance().mainDesktop);
        this.oldDoc = oldDoc;
        this.newDoc = newDoc;
    }

    public Diagram getNewDocument() {
        return this.newDoc;
    }

    public Diagram getOldDocument() {
        return this.oldDoc;
    }
}