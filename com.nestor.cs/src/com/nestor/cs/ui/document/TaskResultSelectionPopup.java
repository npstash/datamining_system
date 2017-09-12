//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// FaceSelectionPopup.java (sed.ui.diagram)
// Utworzono 2005-07-27 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.nestor.cs.tasks.def.MultiResultTask;
import com.nestor.cs.ui.document.elements.DocumentElement;
import com.nestor.cs.ui.event.EndLinkingEvent;
import com.nestor.cs.ui.event.EndLinkingListener;
import com.nestor.cs.util.event.EventDispatcher;
import com.nestor.cs.util.event.SimpleEventDispatcher;

public class TaskResultSelectionPopup extends JPopupMenu {

    private class ElementFacePopupItem extends JMenuItem implements
            ActionListener {

        private MultiResultTask.TaskResult face = null;

        private ElementFacePopupItem(MultiResultTask.TaskResult face) {
            super(face.getElementFaceName());
            this.face = face;
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent event) {
            TaskResultSelectionPopup fsp = TaskResultSelectionPopup.this;
            fsp.getDispatcher().notifyListeners(EndLinkingListener.class,
                    new EndLinkingEvent(fsp, this.face));
        }
    }

    private EventDispatcher dispatcher = null;

    private DocumentElement element;

    private Component origComp;

    private int origX, origY;

    private JMenuItem showInvisible;

    public TaskResultSelectionPopup(MultiResultTask.TaskResult[] faces) {
        super();
        for (int i = 0; i < faces.length; i++) {
            ElementFacePopupItem efpi = new ElementFacePopupItem(faces[i]);
            add(efpi);
        }
    }

    public void addEndLinkingListener(EndLinkingListener listener) {
        getDispatcher().addListener(EndLinkingListener.class, listener);
    }

    private EventDispatcher getDispatcher() {
        if (this.dispatcher == null)
            this.dispatcher = new SimpleEventDispatcher();
        return this.dispatcher;
    }

    public void removeEndLinkingListener(EndLinkingListener listener) {
        getDispatcher().removeListener(EndLinkingListener.class, listener);
    }

    @Override
    public void show(Component c, int x, int y) {
        this.origX = x;
        this.origY = y;
        this.origComp = c;
        super.show(c, x, y);
    }

}