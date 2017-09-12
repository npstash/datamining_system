//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiagramElementTransferHandler.java (sed.ui.element)
// Utworzono 2005-07-17 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document.elements;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;

import com.nestor.cs.diagram.TaskGraph.CannotEdit;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.ui.document.DiagramPanel;

public class DocumentElementTransferHandler extends TransferHandler {

    public static DocumentElementTransferHandler sharedInstance = new DocumentElementTransferHandler();

    @Override
    public boolean canImport(JComponent importTo, DataFlavor[] flavors) {
        if (importTo instanceof DiagramPanel) {
            DiagramPanel dp = (DiagramPanel) importTo;
            if (!dp.getDocument().isEditable())
                return false;
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i]
                        .equals(DocumentElementTransferable.diagramElementDataFlavor))
                    return true;
            }
        }
        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent from) {
        if (from instanceof JTree) {
            JTree tree = (JTree) from;
            DocumentElementFactory factory = ((DocumentElementTreeNode) tree
                    .getSelectionPath().getLastPathComponent()).getFactory();
            if (factory == null)
                return null;
            return new DocumentElementTransferable(factory
                    .createDiagramElement());
        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent arg0) {
        return TransferHandler.COPY;
    }

    @Override
    public Icon getVisualRepresentation(Transferable t) {
        DocumentElement de;
        try {
            de = (DocumentElement) t
                    .getTransferData(DocumentElementTransferable.diagramElementDataFlavor);
        } catch (Exception e) {
            return null;
        }
        return de.getFactory().getElementIcon();
    }

    @Override
    public boolean importData(JComponent cmp, Transferable t) {
        if (cmp instanceof DiagramPanel) {
            DiagramPanel dp = (DiagramPanel) cmp;
            DocumentElement de;
            try {
                de = (DocumentElement) t
                        .getTransferData(DocumentElementTransferable.diagramElementDataFlavor);
            } catch (Exception e) {
                return false;
            }
            Point location = dp.getMousePosition();
            location.x -= de.getDiagramComponent().getPreferredSize().width / 2;
            location.y -= de.getDiagramComponent().getPreferredSize().height / 2;
            de.setComponentLocation(location);
            try {
                dp.getDocument().addDiagramElement(de);
            } catch (CannotEdit e1) {
                SystemFrame.getInstance().messageArea.showException(e1);
            }
            return true;
        }
        return false;
    }
}