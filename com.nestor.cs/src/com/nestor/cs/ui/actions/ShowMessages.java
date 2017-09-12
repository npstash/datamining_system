//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ShowMessages.java (sed.ui.actions)
// Utworzono 2005-08-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.actions;

import java.awt.event.ActionEvent;

import com.nestor.cs.Messages;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.util.swing.ExtAbstractAction;

public class ShowMessages extends ExtAbstractAction {

    public ShowMessages() {
        super();
        putValue(NAME, Messages.getString("ShowMessages.messagePanel")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString(getClass().getName()));
        actionPerformed(null);
    }

    public synchronized void actionPerformed(ActionEvent arg0) {
        if (SystemFrame.getInstance().messageArea.isVisible()) {
            SystemFrame.getInstance().messageArea.setVisible(false);
            setSelected(false);
        } else {
            SystemFrame.getInstance().messageArea.setVisible(true);
            SystemFrame.getInstance().restoreRightSplitPane();
            setSelected(true);
        }
    }

}