//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ElementTrasferable.java (sed.ui.element)
// Utworzono 2005-07-16 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document.elements;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class DocumentElementTransferable implements Transferable {

    public static DataFlavor diagramElementDataFlavor = new DataFlavor(
            DocumentElement.class, DocumentElement.class.getSimpleName());

    private DocumentElement diagramElement;

    private DataFlavor[] flavors;

    public DocumentElementTransferable(DocumentElement element) {
        this.diagramElement = element;
        Class c = DocumentElement.class;
        this.flavors = new DataFlavor[] { diagramElementDataFlavor };
    }

    public DocumentElement getDiagramElement() {
        return this.diagramElement;
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return this.diagramElement;
        }
        return null;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return this.flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (int i = 0; i < this.flavors.length; i++)
            if (flavor.equals(this.flavors[i]))
                return true;
        return false;
    }
}