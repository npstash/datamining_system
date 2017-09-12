//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ExitSystem.java (sed.ui)
// Utworzono 2005-07-03 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.actions;

import java.awt.event.ActionEvent;

import com.nestor.cs.Messages;
import com.nestor.cs.util.swing.ExtAbstractAction;

public class ExitSystem extends ExtAbstractAction {

    public ExitSystem() {
        super();
        putValue(NAME, Messages.getString("ExitSystem.exit")); //$NON-NLS-1$
        putValue(ICON, getButtonIcon(getClass().getResource(
                "images/exit16button.gif"))); //$NON-NLS-1$
    }

    public void actionPerformed(ActionEvent arg0) {
        System.exit(0);
    }

}