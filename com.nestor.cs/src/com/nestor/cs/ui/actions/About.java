//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// About.java (sed.ui.actions)
// Utworzono 2005-08-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.nestor.cs.Messages;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.util.swing.ExtAbstractAction;

public class About extends ExtAbstractAction {

    public About() {
        super();
        putValue(NAME, Messages.getString("About.about")); //$NON-NLS-1$
        putValue(ICON, getButtonIcon(getClass().getResource(
                "images/about16button.gif"))); //$NON-NLS-1$
    }

    public void actionPerformed(ActionEvent arg0) {
        String s1 = Messages.getString("About.sedInfo"); //$NON-NLS-1$
        JOptionPane.showMessageDialog(SystemFrame.getInstance(), s1, Messages
                .getString("About.about"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
    }

}