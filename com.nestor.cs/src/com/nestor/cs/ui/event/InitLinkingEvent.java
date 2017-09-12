//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// CreateLinkEvent.java (sed.ui.diagram)
// Utworzono 2005-07-19 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.event;

import java.util.EventObject;

import com.nestor.cs.ui.document.elements.DocumentElement;
import com.nestor.cs.util.util.param.Argument;

public class InitLinkingEvent extends EventObject {

    private Argument arg;

    private DocumentElement element;

    public InitLinkingEvent(Object source, DocumentElement de, Argument arg) {
        super(source);
        this.element = de;
        this.arg = arg;
    }

    public Argument getArgument() {
        return this.arg;
    }

    public DocumentElement getDiagramElement() {
        return this.element;
    }
}