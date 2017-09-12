//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ShowProcInfo.java (sed.ui.actions)
// Utworzono 2005-08-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.actions;

import java.awt.event.ActionEvent;

import com.nestor.cs.Messages;
import com.nestor.cs.ui.OutputFrame;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.util.event.NotifyEvent;
import com.nestor.cs.util.event.NotifyListener;
import com.nestor.cs.util.swing.ExtAbstractAction;

public class ShowProcInfo extends ExtAbstractAction {

    public ShowProcInfo() {
        super();
        putValue(NAME, Messages.getString("ShowProcInfo.processInfo")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString(getClass().getName()));
        actionPerformed(null);
        OutputFrame.addNotifyListener(new NotifyListener() {
            public void notify(NotifyEvent ev) {
                SystemFrame.getInstance().setShowOutputFrame(false);
                setSelected(false);
            }
        });
    }

    public synchronized void actionPerformed(ActionEvent arg0) {
        if (SystemFrame.getInstance().getShowOutputFrame()) {
            SystemFrame.getInstance().setShowOutputFrame(false);
            setSelected(false);
        } else {
            SystemFrame.getInstance().setShowOutputFrame(true);
            setSelected(true);
        }
    }

}