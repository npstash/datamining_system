//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ComponentFrame.java (sed.ui.diagram)
// Utworzono 2005-07-30 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.nestor.cs.tasks.def.DiagramComponent;
import com.nestor.cs.ui.SystemFrame;

public class TaskFrame extends JFrame implements ReusableTaskViewer,
        WindowListener {

    private Map DCMap = null;

    private DiagramComponent diagramComponent;

    public TaskFrame(DiagramComponent dc) {
        super();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        setSize(new Dimension(600, 500));
        setLocationRelativeTo(SystemFrame.getInstance());
        addWindowListener(this);
        reinitViewer(dc);
    }

    public DiagramComponent getDiagramComponent() {
        return this.diagramComponent;
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
        getContentPane().add(dc.getComponent(), BorderLayout.CENTER);
        getContentPane().validate();
        this.diagramComponent = dc;
    }

    public void windowActivated(WindowEvent arg0) {
    }

    public void windowClosed(WindowEvent arg0) {
    }

    public void windowClosing(WindowEvent arg0) {
        if (this.DCMap != null) {
            this.DCMap.remove(this.diagramComponent);
        }
    }

    public void windowDeactivated(WindowEvent arg0) {
    }

    public void windowDeiconified(WindowEvent arg0) {
    }

    public void windowIconified(WindowEvent arg0) {
    }

    public void windowOpened(WindowEvent arg0) {
    }

}