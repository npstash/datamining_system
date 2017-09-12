//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// StartFlow.java (sed.ui.actions)
// Utworzono 2005-07-22 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;

import com.nestor.cs.Messages;
import com.nestor.cs.diagram.Diagram;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.util.event.StateEvent;
import com.nestor.cs.util.proc.Controller;
import com.nestor.cs.util.proc.ProcessingException;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class StartFlow extends AbstractProcessAction {

    public StartFlow() {
        super();
        super.putValue(NAME, Messages.getString("StartFlow.start")); //$NON-NLS-1$
        putValue(ICON, getButtonIcon(getClass().getResource(
                "images/run16button.gif"))); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString(getClass().getName()));
    }

    public void actionPerformed(ActionEvent arg0) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Diagram doc = SystemFrame.getInstance().mainDesktop
                            .getActiveDocument();
                    if (doc == null)
                        return;
                    Controller.run(doc);
                    playAudioNotice("/org/cs/ui/document/sound/complete.wav");
                } catch (ProcessingException e) {
                    playAudioNotice("/org/cs/ui/document/sound/error.wav");
                    SystemFrame.getInstance().messageArea.showException(e);
                }
            }
        };
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    private void playAudioNotice(String resource) {
        InputStream in = getClass().getResourceAsStream(resource);
        if (in != null)
            try {
                AudioStream as = new AudioStream(in);
                AudioPlayer.player.start(as);
            } catch (IOException e) {
            }
    }

    public void stateChanged(StateEvent e) {
        setEnabled(Controller.canRun(e.getIterativeProcess()));
    }

}