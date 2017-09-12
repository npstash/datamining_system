//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SaveDocument.java (sed.ui.actions)
// Utworzono 2005-07-20 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.w3c.dom.Document;

import com.nestor.cs.Messages;
import com.nestor.cs.ui.DocFileFilter;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.ui.document.DiagramDocument;
import com.nestor.cs.ui.event.ActiveDocumentChangedEvent;
import com.nestor.cs.ui.event.ActiveDocumentChangedListener;
import com.nestor.cs.util.swing.ExtAbstractAction;
import com.nestor.cs.util.util.XMLTools;

public class SaveDocument extends ExtAbstractAction implements
        ActiveDocumentChangedListener {

    public SaveDocument() {
        super();
        super.putValue(NAME, Messages.getString("SaveDocument.save")); //$NON-NLS-1$
        SystemFrame.getInstance().mainDesktop
                .addActiveDocumentChangedListener(this);
        setEnabled(SystemFrame.getInstance().mainDesktop.getActiveDocument() != null);
        putValue(ICON, getButtonIcon(getClass().getResource(
                "images/save16button.gif"))); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString(getClass().getName()));
    }

    public void actionPerformed(ActionEvent event) {
        DiagramDocument doc = SystemFrame.getInstance().mainDesktop
                .getActiveDocument();
        if (doc == null)
            return;
        try {
            Document xmlDoc = doc.toXML();
            PrintWriter pw = new PrintWriter(new FileOutputStream(new File(doc
                    .getTitle()
                    + "." + DocFileFilter.DOC_FILENAME_EXT))); //$NON-NLS-1$
            XMLTools.printNode(pw, xmlDoc, ""); //$NON-NLS-1$
            pw.close();
        } catch (Exception e) {
            SystemFrame.getInstance().messageArea.showException(e);
        }
    }

    public void activeDocumentChanged(ActiveDocumentChangedEvent event) {
        setEnabled(event.getNewDocument() != null);
    }

}