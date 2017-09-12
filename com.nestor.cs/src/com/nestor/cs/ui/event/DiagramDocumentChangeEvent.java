//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiagramDocumentChangeEvent.java (sed.ui)
// Utworzono 2005-07-16 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.event;

import java.util.EventObject;

import com.nestor.cs.ui.document.DiagramDocument;
import com.nestor.cs.ui.document.elements.DocumentElement;

public class DiagramDocumentChangeEvent extends EventObject {

    public static final int CHANGE_DOCUMENT_CLOSED = 3;

    public static final int CHANGE_DOCUMENT_INVALIDATION = 4;

    public static final int CHANGE_ELEMENT_ADDED = 1;

    public static final int CHANGE_ELEMENT_REMOVED = 2;

    private int change;

    private DocumentElement de;

    private DiagramDocument document;

    public DiagramDocumentChangeEvent(DiagramDocument document,
            DocumentElement de, int change) {
        super(document);
        this.document = document;
        this.de = de;
        this.change = change;
    }

    public int getChange() {
        return this.change;
    }

    public DocumentElement getDiagramElement() {
        return this.de;
    }

    public DiagramDocument getDocument() {
        return this.document;
    }

}