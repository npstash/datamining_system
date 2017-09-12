//
//Praca Dyplomowa Magisterska: System Exploracji Danych
//Autor: Nestor Paw�owski
//
//SaveDocument.java (sed.ui.actions)
//Utworzono 2005-07-20 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.swing.JFileChooser;

import org.w3c.dom.Document;

import com.nestor.cs.Messages;
import com.nestor.cs.ui.DocFileFilter;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.ui.document.DiagramDocument;
import com.nestor.cs.ui.event.ActiveDocumentChangedEvent;
import com.nestor.cs.ui.event.ActiveDocumentChangedListener;
import com.nestor.cs.util.swing.ExtAbstractAction;
import com.nestor.cs.util.util.XMLTools;

public class SaveAsDocument extends ExtAbstractAction implements
        ActiveDocumentChangedListener {

    public SaveAsDocument() {
        super();
        super.putValue(NAME, Messages.getString("SaveAsDocument.saveAs")); //$NON-NLS-1$
        SystemFrame.getInstance().mainDesktop
                .addActiveDocumentChangedListener(this);
        setEnabled(SystemFrame.getInstance().mainDesktop.getActiveDocument() != null);
        putValue(ICON, getButtonIcon(getClass().getResource(
                "images/saveas16button.gif"))); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString(getClass().getName()));
    }

    public void actionPerformed(ActionEvent event) {
        DiagramDocument doc = SystemFrame.getInstance().mainDesktop
                .getActiveDocument();
        if (doc == null)
            return;
        JFileChooser jfc = new JFileChooser("."); //$NON-NLS-1$
        jfc.setFileFilter(new DocFileFilter());
        int ret = jfc.showSaveDialog(SystemFrame.getInstance());
        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                String fname = jfc.getSelectedFile().getAbsolutePath();
                if (!fname.endsWith("." + DocFileFilter.DOC_FILENAME_EXT)) { //$NON-NLS-1$
                    fname += "." + DocFileFilter.DOC_FILENAME_EXT; //$NON-NLS-1$
                }
                String noExt = fname.substring(0, fname.lastIndexOf('.'));
                Document xmlDoc = doc.toXML();
                File f = new File(fname);
                PrintWriter pw = new PrintWriter(new FileOutputStream(f));
                XMLTools.printNode(pw, xmlDoc, ""); //$NON-NLS-1$
                pw.close();
                doc.setTitle(noExt);
            } catch (Exception e) {
                SystemFrame.getInstance().messageArea.showException(e);
            }
        }
    }

    public void activeDocumentChanged(ActiveDocumentChangedEvent event) {
        setEnabled(event.getNewDocument() != null);
    }

}