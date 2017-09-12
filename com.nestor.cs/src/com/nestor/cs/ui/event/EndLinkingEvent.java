//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// EndLinkingEvent.java (sed.ui.event)
// Utworzono 2005-07-27 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.event;

import java.util.EventObject;

import com.nestor.cs.tasks.def.MultiResultTask;

public class EndLinkingEvent extends EventObject {

    private MultiResultTask.TaskResult face;

    public EndLinkingEvent(Object source, MultiResultTask.TaskResult face) {
        super(source);
        this.face = face;
    }

    public MultiResultTask.TaskResult getElementFace() {
        return this.face;
    }

}