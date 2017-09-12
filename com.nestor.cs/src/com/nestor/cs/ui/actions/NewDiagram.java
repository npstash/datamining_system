//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// NewDiagram.java (sed.ui.actions)
// Utworzono 2005-07-04 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.actions;

import java.awt.event.ActionEvent;

import com.nestor.cs.Messages;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.ui.document.DiagramDocument;
import com.nestor.cs.ui.document.DiagramFrame;
import com.nestor.cs.util.swing.ExtAbstractAction;

public class NewDiagram extends ExtAbstractAction {

    public NewDiagram() {
        super();
        putValue(NAME, Messages.getString("NewDiagram.new")); //$NON-NLS-1$
        putValue(ICON, getButtonIcon(getClass().getResource(
                "images/new16button.gif"))); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString(getClass().getName()));
    }

    public void actionPerformed(ActionEvent arg0) {
        DiagramDocument doc = new DiagramDocument();
        DiagramFrame df = SystemFrame.getInstance().showDocument(doc);
    }
}