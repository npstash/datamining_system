//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiagramComponentDialog.java (sed.ui.diagram)
// Utworzono 2005-07-23 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.nestor.cs.Messages;
import com.nestor.cs.tasks.def.DiagramComponent;
import com.nestor.cs.ui.SystemFrame;

public class TaskDialog extends JDialog implements ReusableTaskViewer,
        ActionListener, WindowListener {

    private Map DCMap = null;

    private DiagramComponent diagramComponent;

    private JButton jbClose;

    private JPanel jpButtons;

    public TaskDialog(DiagramComponent dc) {
        super(SystemFrame.getInstance());
        this.jpButtons = new JPanel();
        this.jbClose = new JButton(Messages
                .getString("DiagramComponentDialog.acceptAndClose")); //$NON-NLS-1$
        this.jbClose.addActionListener(this);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(this);
        this.jpButtons.add(this.jbClose);
        getContentPane().setLayout(new BorderLayout());
        setSize(new Dimension(500, 400));
        setLocationRelativeTo(SystemFrame.getInstance());
        reinitViewer(dc);
    }

    public void actionPerformed(ActionEvent arg0) {
        dispose();
        if (this.DCMap != null) {
            this.DCMap.remove(this.diagramComponent);
        }
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
        getContentPane().add(this.jpButtons, BorderLayout.SOUTH);
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