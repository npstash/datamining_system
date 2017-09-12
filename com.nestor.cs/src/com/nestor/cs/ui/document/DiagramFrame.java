//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiagramFrame.java (sed.ui)
// Utworzono 2005-07-04 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.nestor.cs.diagram.TaskGraph;

public class DiagramFrame extends JInternalFrame implements
        PropertyChangeListener {

    private DiagramPanel dPanel;

    public DiagramFrame(DiagramDocument doc) {
        super();
        this.dPanel = new DiagramPanel(doc);
        setTitle(doc.getTitle());
        JScrollPane jsp = new JScrollPane(this.dPanel);
        setContentPane(jsp);
        setMaximizable(true);
        setMaximizable(true);
        setClosable(true);
        setIconifiable(true);
        setResizable(true);
        setSize(new Dimension(450, 350));
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent arg0) {
                getDocument().removePropertyChangeListener(DiagramFrame.this);
            }
        });
        doc.addPropertyChangeListener(this);
    }

    public DiagramPanel getDiagramPanel() {
        return this.dPanel;
    }

    public DiagramDocument getDocument() {
        return this.dPanel.getDocument();
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(DiagramDocument.PROPERTY_TITLE)) {
            DiagramDocument dd = (DiagramDocument) e.getSource();
            setTitle(dd.getTitle());
        }
        if (e.getPropertyName().equals(TaskGraph.EDITABLE)) {
            if (e.getNewValue() instanceof Boolean) {
                boolean b = ((Boolean) e.getNewValue()).booleanValue();
                setClosable(b);
            }
        }
    }

    @Override
    public void reshape(int x, int y, int w, int h) {
        Dimension cpSize = getContentPane().getSize();
        Dimension fSizez = getSize();
        Dimension dim = new Dimension(cpSize.width + w - fSizez.width,
                cpSize.height + h - fSizez.height);
        getDocument().setDiagramSize(dim);
        this.dPanel.revalidate();
        super.reshape(x, y, w, h);
    }

}