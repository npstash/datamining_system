//
//Praca Dyplomowa Magisterska: System Exploracji Danych
//Autor: Nestor Paw�owski
//
//ShowElements.java (sed.ui.actions)
//Utworzono 2005-07-04 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.actions;

import java.awt.event.ActionEvent;

import com.nestor.cs.Messages;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.util.swing.ExtAbstractAction;

public class ShowElements extends ExtAbstractAction {

    public ShowElements() {
        super();
        putValue(NAME, Messages.getString("ShowElements.diagramElements")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString(getClass().getName()));
        actionPerformed(null);
    }

    public synchronized void actionPerformed(ActionEvent arg0) {
        if (SystemFrame.getInstance().elementTree.isVisible()) {
            SystemFrame.getInstance().elementTree.setVisible(false);
            if (!SystemFrame.getInstance().parameterEdit.isVisible()) {
                SystemFrame.getInstance().hideCenterSplitPane();
            } else {
                SystemFrame.getInstance().invalidateLeftSplitPane();
            }
            setSelected(false);
        } else {
            SystemFrame.getInstance().elementTree.setVisible(true);
            if (!SystemFrame.getInstance().parameterEdit.isVisible()) {
                SystemFrame.getInstance().restoreCenterSplitPane();
            }
            SystemFrame.getInstance().restoreLeftSplitPane();
            setSelected(true);
        }
    }

}