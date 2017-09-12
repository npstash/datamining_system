//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// MessageArea.java (sed.ui)
// Utworzono 2005-07-26 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.nestor.cs.Messages;
import com.nestor.cs.util.swing.ExtAbstractAction;
import com.nestor.cs.util.swing.JButtonScrollPane;

public class MessageArea extends JPanel {

    private class MessageExpirationThread implements Runnable {
        public void run() {
            while (true) {
                while ((MessageArea.this.secondsToClear > 0) || (MessageArea.this.isSaving)) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    MessageArea.this.secondsToClear--;
                }
                synchronized (MessageArea.this.threadLock) {
                    MessageArea.this.jtaText.setText(""); //$NON-NLS-1$
                    setDetails(""); //$NON-NLS-1$
                    try {
                        if (MessageArea.this.secondsToClear <= 0) {
                            MessageArea.this.secondsToClear = 0;
                            MessageArea.this.threadLock.wait();
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    private Thread clearingThread = null;

    private String details;

    private String message;

    private boolean isSaving = false;

    private JButton jbClear;

    private JButton jbDetails;

    private JButton jbSave;

    private JButtonScrollPane jbspScroll;

    private JTextArea jtaText;

    private int secondsToClear = 0;

    private Object threadLock = new Object();

    public MessageArea() {
        super(new BorderLayout());
        this.jtaText = new JTextArea();
        this.jtaText.setLineWrap(true);
        this.jtaText.setWrapStyleWord(true);
        this.jtaText.setEditable(false);

        Icon clearIcon = ExtAbstractAction.getButtonIcon(getClass()
                .getResource("images/clear16button.gif")); //$NON-NLS-1$
        this.jbClear = new JButton(clearIcon);
        this.jbClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                MessageArea.this.jtaText.setText(""); //$NON-NLS-1$
            }
        });
        this.jbClear.setToolTipText(Messages.getString("MessageArea.clearMessage")); //$NON-NLS-1$

        Icon saveIcon = ExtAbstractAction.getButtonIcon(getClass().getResource(
                "images/save16button.gif")); //$NON-NLS-1$
        this.jbSave = new JButton(saveIcon);
        this.jbSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                MessageArea.this.isSaving = true;
                if (MessageArea.this.jtaText.getText().equals("")) //$NON-NLS-1$
                    return;
                JFileChooser jfc = new JFileChooser("."); //$NON-NLS-1$
                int res = jfc.showSaveDialog(null);
                if (res == JFileChooser.APPROVE_OPTION) {
                    try {
                        PrintWriter pw = new PrintWriter(new FileOutputStream(
                                jfc.getSelectedFile()));
                        pw.write(MessageArea.this.jtaText.getText());
                        pw.close();
                    } catch (Exception e) {
                    }
                }
                MessageArea.this.isSaving = false;
            }
        });
        this.jbSave.setToolTipText(Messages.getString("MessageArea.saveMessage")); //$NON-NLS-1$

        Icon detailsIcon = ExtAbstractAction.getButtonIcon(getClass()
                .getResource("images/details16button.gif")); //$NON-NLS-1$
        this.jbDetails = new JButton(detailsIcon);
        this.jbDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MessageArea.this.jtaText.setText(MessageArea.this.message + "\n" + MessageArea.this.details); //$NON-NLS-1$
            }
        });
        this.jbDetails.setToolTipText(Messages.getString("MessageArea.showDetails")); //$NON-NLS-1$
        setDetails(""); //$NON-NLS-1$

        this.jbspScroll = new JButtonScrollPane();
        this.jbspScroll.addVerticalComponent(this.jbClear, 20);
        this.jbspScroll.addVerticalComponent(this.jbSave, 20);
        this.jbspScroll.addVerticalComponent(this.jbDetails, 20);
        this.jbspScroll.setVerticalWidth(20);

        this.jbspScroll.setViewportView(this.jtaText);
        add(this.jbspScroll, BorderLayout.CENTER);
    }

    public void appendDetails(String s) {
        this.details += "\n\n" + s; //$NON-NLS-1$
        setDetails(this.details);
    }

    public void appendInfo(String s, int timeoutSeconds) {
        this.message += "\n\n"; //$NON-NLS-1$
        showMessage(this.message + s, timeoutSeconds); 
    }

    public void setDetails(String s) {
        this.details = s;
        if (s.equals("")) { //$NON-NLS-1$
            this.jbDetails.setEnabled(false);
        } else {
            this.jbDetails.setEnabled(true);
        }
    }

    public void showException(Exception e) {
        String s = ""; //$NON-NLS-1$
        Throwable t = e;
        int totalLines = 1;
        do {
            StackTraceElement[] stack = t.getStackTrace();
            for (int i = 0; i < stack.length; i++) {
                s += stack[i] + "\r\n"; //$NON-NLS-1$
                totalLines++;
            }
            t = t.getCause();
            s += "\r\n"; //$NON-NLS-1$
            if (t != null) {
                s += Messages.getString("MessageArea.causedBy") + t.getLocalizedMessage() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
                totalLines++;
            }
        } while (t != null);
        this.message = e.getLocalizedMessage() + "\n"; //$NON-NLS-1$
        showMessage(this.message, totalLines * 5); 
        setDetails(s);
    }

    public void showMessage(String s) {
        this.jtaText.setText(s);
        setDetails(""); //$NON-NLS-1$
    }

    public void showMessage(String s, int timeoutSeconds) {
        this.jtaText.setText(s);
        setDetails(""); //$NON-NLS-1$
        if (this.clearingThread == null) {
            this.clearingThread = new Thread(new MessageExpirationThread());
            this.clearingThread.start();
        }
        synchronized (this.threadLock) {
            this.secondsToClear = timeoutSeconds;
            this.threadLock.notify();
        }
    }

    public void showTemporaryLegend(String s) {
        showMessage(s, s.length() / 10);
    }

}