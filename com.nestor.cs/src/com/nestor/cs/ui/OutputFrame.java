//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// OutputFrame.java (sed.ui)
// Utworzono 2005-08-10 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui;

import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.nestor.cs.Messages;
import com.nestor.cs.util.event.EventDispatcher;
import com.nestor.cs.util.event.NotifyEvent;
import com.nestor.cs.util.event.NotifyListener;
import com.nestor.cs.util.event.SimpleEventDispatcher;

public class OutputFrame extends JFrame implements WindowListener {

    private class Docwriter extends OutputStream {

        @Override
        public void write(byte[] c) throws IOException {
            this.write(c, 0, c.length);
        }

        @Override
        public void write(byte[] c, int off, int len) throws IOException {
            if (getStaticFrame() != null)
                if (!getStaticFrame().isVisible())
                    getStaticFrame().setVisible(true);
            String s = new String(c, off, len);
            try {
                OutputFrame.this.doc.insertString(OutputFrame.this.doc.getLength(), s, OutputFrame.this.attrs);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void write(int i) throws IOException {
            if (getStaticFrame() != null)
                if (!getStaticFrame().isVisible())
                    getStaticFrame().setVisible(true);
            String s = "" + (char) i; //$NON-NLS-1$
            try {
                OutputFrame.this.doc.insertString(OutputFrame.this.doc.getLength(), s, OutputFrame.this.attrs);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

    }

    private static EventDispatcher eventDispatcher;

    private static OutputFrame frame;

    private static Object lock = new Object();

    /**
     * Powiadomienie o zamkni�ciu okna
     * 
     * @param l
     */
    public static void addNotifyListener(NotifyListener l) {
        getEventDispatcher().addListener(NotifyListener.class, l);
    }

    public static void closeFrame() {
        synchronized (lock) {
            if (frame == null)
                return;
            OutputFrame temp = frame;
            frame.windowClosing(null);
            temp.dispose();
            temp = null;
        }
    }

    protected static void fireNotify(Object source) {
        if (eventDispatcher != null) {
            NotifyEvent ne = new NotifyEvent(source);
            getEventDispatcher().notifyListeners(NotifyListener.class, ne);
        }
    }

    protected static EventDispatcher getEventDispatcher() {
        if (eventDispatcher == null) {
            eventDispatcher = new SimpleEventDispatcher();
        }
        return eventDispatcher;
    }

    public static OutputFrame getStaticFrame() {
        synchronized (lock) {
            return frame;
        }
    }

    public static void removeNotifyListener(NotifyListener l) {
        getEventDispatcher().removeListener(NotifyListener.class, l);
    }

    public static void startFrame() {
        synchronized (lock) {
            if (frame != null)
                return;
            frame = new OutputFrame();
        }
    }

    private AttributeSet attrs;

    private Document doc;

    private PrintStream oldOut;

    private OutputFrame() throws HeadlessException {
        super(Messages.getString("OutputFrame.procInfo")); //$NON-NLS-1$
        JTextArea jtaOutput = new JTextArea();
        setContentPane(new JScrollPane(jtaOutput));
        this.doc = jtaOutput.getDocument();
        this.attrs = this.doc.getDefaultRootElement().getAttributes();
        addWindowListener(this);
        setSize(500, 300);
        this.oldOut = System.out;
        PrintStream ps = new PrintStream(new Docwriter());
        System.setOut(ps);
    }

    public void windowActivated(WindowEvent arg0) {
    }

    public void windowClosed(WindowEvent arg0) {
    }

    public void windowClosing(WindowEvent arg0) {
        synchronized (lock) {
            System.setOut(this.oldOut);
            frame = null;
            fireNotify(this);
        }
    }

    public void windowDeactivated(WindowEvent arg0) {
    }

    public void windowDeiconified(WindowEvent arg0) {
    }

    public void windowIconified(WindowEvent arg0) {
    }

    public void windowOpened(WindowEvent arg0) {
    }

}