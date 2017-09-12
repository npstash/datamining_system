//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// LoadDocument.java (sed.ui.actions)
// Utworzono 2005-07-20 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.nestor.cs.Messages;
import com.nestor.cs.ui.DocFileFilter;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.ui.document.DiagramDocument;
import com.nestor.cs.ui.document.DiagramFrame;
import com.nestor.cs.util.swing.ExtAbstractAction;

public class LoadDocument extends ExtAbstractAction {

    public LoadDocument() {
        super();
        putValue(NAME, Messages.getString("LoadDocument.load")); //$NON-NLS-1$
        putValue(ICON, getButtonIcon(getClass().getResource(
                "images/load16button.gif"))); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString(getClass().getName()));
    }

    public void actionPerformed(ActionEvent event) {
        JFileChooser jfc = new JFileChooser("."); //$NON-NLS-1$
        jfc.setFileFilter(new DocFileFilter());
        int ret = jfc.showOpenDialog(SystemFrame.getInstance());
        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                String fname = jfc.getSelectedFile().getAbsolutePath();
                if (!fname.endsWith("." + DocFileFilter.DOC_FILENAME_EXT)) { //$NON-NLS-1$
                    fname += "." + DocFileFilter.DOC_FILENAME_EXT; //$NON-NLS-1$
                }
                String noExt = fname.substring(0, fname.lastIndexOf('.'));
                DocumentBuilderFactory factory = DocumentBuilderFactory
                        .newInstance();
                factory.setNamespaceAware(false);
                factory.setValidating(false);
                DocumentBuilder builder = factory.newDocumentBuilder();
                File f = new File(fname);
                Document xmlDoc = builder.parse(f);

                DiagramDocument doc = new DiagramDocument();
                DiagramFrame df = SystemFrame.getInstance().showDocument(doc);
                doc.fromXML(xmlDoc);
                doc.setTitle(noExt);

            } catch (Exception e) {
                SystemFrame.getInstance().messageArea.showException(e);
            }
        }
    }

}