//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ObjectSelectionEvent.java (sed.ui.event)
// Utworzono 2005-08-02 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.event;

import java.util.EventObject;

public class ObjectSelectionEvent extends EventObject {

    private Object selectedObject;

    public ObjectSelectionEvent(Object source, Object selectedObject) {
        super(source);
        this.selectedObject = selectedObject;
    }

    public Object getSelectedObject() {
        return this.selectedObject;
    }
}