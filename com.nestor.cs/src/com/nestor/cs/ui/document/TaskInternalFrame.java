//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ComponentFrame.java (sed.ui.diagram)
// Utworzono 2005-07-22 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import com.nestor.cs.tasks.def.DiagramComponent;

public class TaskInternalFrame extends JInternalFrame implements
        InternalFrameListener {

    private Map DCMap = null;

    private DiagramComponent diagramComponent;

    public TaskInternalFrame(DiagramComponent dc) {
        super();
        setMaximizable(true);
        setMaximizable(true);
        setClosable(true);
        setIconifiable(true);
        setResizable(true);
        setSize(new Dimension(350, 350));
        addInternalFrameListener(this);
        reinitViewer(dc);
    }

    public DiagramComponent getDiagramComponent() {
        return this.diagramComponent;
    }

    public void internalFrameActivated(InternalFrameEvent arg0) {
    }

    public void internalFrameClosed(InternalFrameEvent arg0) {
    }

    public void internalFrameClosing(InternalFrameEvent arg0) {
        if (this.DCMap != null)
            this.DCMap.remove(this.diagramComponent);
    }

    public void internalFrameDeactivated(InternalFrameEvent arg0) {
    }

    public void internalFrameDeiconified(InternalFrameEvent arg0) {
    }

    public void internalFrameIconified(InternalFrameEvent arg0) {
    }

    public void internalFrameOpened(InternalFrameEvent arg0) {
    }

    public void onCloseRemoveDCFromMap(Map map) {
        this.DCMap = map;
    }

    public void reinitViewer() {
        reinitViewer(getDiagramComponent());
    }

    public void reinitViewer(DiagramComponent dc) {
        setTitle(dc.getName());
        getContentPane().removeAll();
        getContentPane().add(dc.getComponent());
        getContentPane().validate();
        this.diagramComponent = dc;
    }

}