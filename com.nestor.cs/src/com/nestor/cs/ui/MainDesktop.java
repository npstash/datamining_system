//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// MainDesktop.java (sed.ui)
// Utworzono 2005-07-04 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui;

import java.awt.Color;
import java.beans.PropertyVetoException;
import java.util.HashMap;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import com.nestor.cs.diagram.Diagram;
import com.nestor.cs.tasks.def.DiagramComponent;
import com.nestor.cs.ui.document.DiagramDocument;
import com.nestor.cs.ui.document.DiagramFrame;
import com.nestor.cs.ui.document.TaskInternalFrame;
import com.nestor.cs.ui.event.ActiveDocumentChangedEvent;
import com.nestor.cs.ui.event.ActiveDocumentChangedListener;

public class MainDesktop extends JDesktopPane implements InternalFrameListener {

    private DiagramDocument activeDocument = null;

    private HashMap diagramComponentIFrames = new HashMap();

    public MainDesktop() {
        super();
        setBackground(Color.BLACK);
    }

    public void addActiveDocumentChangedListener(ActiveDocumentChangedListener l) {
        super.listenerList.add(ActiveDocumentChangedListener.class, l);
    }

    public JInternalFrame createComponentInternalFrame(DiagramComponent dc) {
        TaskInternalFrame f = null;
        if (this.diagramComponentIFrames.containsKey(dc)) {
            Object v = this.diagramComponentIFrames.get(dc);
            if (v instanceof TaskInternalFrame) {
                f = (TaskInternalFrame) v;
                f.reinitViewer(dc);
            }
        }
        if (f == null) {
            f = new TaskInternalFrame(dc);
            this.diagramComponentIFrames.put(dc, f);
            f.onCloseRemoveDCFromMap(this.diagramComponentIFrames);
            int windowCount = getAllFrames().length;
            add(f);
            f.setLocation(20 * (windowCount % 10), 20 * (windowCount % 10));
            f.setVisible(true);
        }
        return f;
    }

    protected void addDocumentFrame(DiagramFrame df) {
        df.addInternalFrameListener(this);
        add(df);
        int windowCount = getAllFrames().length;
        df.setLocation(20 * (windowCount % 10), 20 * (windowCount % 10));
        df.setVisible(true);
        try {
            df.setMaximum(true);
        } catch (PropertyVetoException e) {
        }
    }

    protected void fireActiveDocumentChanged(Diagram oldDoc, Diagram newDoc) {
        ActiveDocumentChangedListener[] ls = super.listenerList
                .getListeners(ActiveDocumentChangedListener.class);
        ActiveDocumentChangedEvent event = new ActiveDocumentChangedEvent(
                oldDoc, newDoc);
        for (int i = 0; i < ls.length; i++) {
            ls[i].activeDocumentChanged(event);
        }
    }

    public DiagramDocument getActiveDocument() {
        return this.activeDocument;
    }

    public void internalFrameActivated(InternalFrameEvent event) {
        DiagramDocument doc = ((DiagramFrame) event.getInternalFrame())
                .getDocument();
        setActiveDocument(doc);
    }

    public void internalFrameClosed(InternalFrameEvent arg0) {
        // TODO Auto-generated method stub
    }

    public void internalFrameClosing(InternalFrameEvent event) {
        DiagramDocument doc = ((DiagramFrame) event.getInternalFrame())
                .getDocument();
        doc.closeDocument();
        if (event.getInternalFrame().isSelected()) {
            setActiveDocument(null);
        }
        event.getInternalFrame().removeInternalFrameListener(this);
    }

    public void internalFrameDeactivated(InternalFrameEvent arg0) {
        // TODO Auto-generated method stub
    }

    public void internalFrameDeiconified(InternalFrameEvent arg0) {
        // TODO Auto-generated method stub
    }

    public void internalFrameIconified(InternalFrameEvent arg0) {
        // TODO Auto-generated method stub
    }

    public void internalFrameOpened(InternalFrameEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void removeActiveDocumentChangedListener(
            ActiveDocumentChangedListener l) {
        super.listenerList.remove(ActiveDocumentChangedListener.class, l);
    }

    private void setActiveDocument(DiagramDocument doc) {
        Diagram oldDoc = this.activeDocument;
        this.activeDocument = doc;
        fireActiveDocumentChanged(oldDoc, this.activeDocument);
    }
}