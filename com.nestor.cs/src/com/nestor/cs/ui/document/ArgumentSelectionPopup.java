//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ArgumentSelectMenu.java (sed.ui.diagram)
// Utworzono 2005-07-19 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.nestor.cs.Messages;
import com.nestor.cs.diagram.TaskGraph.CannotEdit;
import com.nestor.cs.tasks.def.ConfigurableTask;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.ui.document.elements.DocumentElement;
import com.nestor.cs.ui.event.InitLinkingEvent;
import com.nestor.cs.ui.event.InitLinkingListener;
import com.nestor.cs.util.event.EventDispatcher;
import com.nestor.cs.util.event.SimpleEventDispatcher;
import com.nestor.cs.util.util.param.Argument;
import com.nestor.cs.util.util.param.ArgumentList;

public class ArgumentSelectionPopup extends JPopupMenu {

    private class ArgumentPopupItem extends JCheckBoxMenuItem implements
            ActionListener {

        private Argument arg = null;

        private ArgumentPopupItem(Argument arg) {
            super(arg.getName());
            setToolTipText(arg.getDescription());
            if (arg.isRequired() && (arg.getValue() == null))
                setForeground(Color.RED);
            if (arg.isReadOnly())
                setForeground(Color.GRAY);
            this.arg = arg;
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent event) {
            ArgumentSelectionPopup asm = ArgumentSelectionPopup.this;
            asm.getDispatcher().notifyListeners(InitLinkingListener.class,
                    new InitLinkingEvent(asm, asm.element, this.arg));
        }
    }

    private class RemoveElement implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            int res = JOptionPane.showConfirmDialog(ArgumentSelectionPopup.this.origComp, Messages
                    .getString("ArgumentSelectionPopup.reallyDelete") + " \"" //$NON-NLS-1$ //$NON-NLS-2$
                    + ArgumentSelectionPopup.this.element.getFactory().getElementName() + "\" ?", //$NON-NLS-1$
                    "", JOptionPane.YES_NO_OPTION); //$NON-NLS-1$
            if (res == JOptionPane.YES_OPTION) {
                try {
                    SystemFrame.getInstance().mainDesktop.getActiveDocument()
                            .removeDiagramElement(ArgumentSelectionPopup.this.element);
                } catch (CannotEdit e) {
                    SystemFrame.getInstance().messageArea.showException(e);
                }
            }
            setVisible(false);
        }
    }

    private EventDispatcher dispatcher = null;

    private DocumentElement element;

    private ArrayList invisibleItems = new ArrayList();

    private Component origComp;

    private int origX, origY;

    private JMenuItem showInvisible;

    public ArgumentSelectionPopup(DocumentElement de) {
        super(de.getFactory().getElementName());
        this.element = de;

        if (SystemFrame.getInstance().mainDesktop.getActiveDocument()
                .isEditable()) {
            JMenuItem deleteDE = new JMenuItem(Messages
                    .getString("ArgumentSelectionPopup.deleteElement")); //$NON-NLS-1$
            deleteDE.addActionListener(new RemoveElement());
            add(deleteDE);
            addSeparator();
        }

        int totalItems = 0;
        if (de.getTask() instanceof ConfigurableTask) {
            ConfigurableTask pe = (ConfigurableTask) de.getTask();
            ArgumentList al = pe.getConfigurationArguments();
            for (int i = 0; i < al.getArgumentCount(); i++) {
                Argument arg = al.getArgument(i);
                ArgumentPopupItem ami = new ArgumentPopupItem(arg);
                totalItems++;
                if (arg.getValue() != null) {
                    ami.setSelected(true);
                }
                if (!arg.isTextEditable()) {
                    add(ami);
                } else {
                    this.invisibleItems.add(ami);
                }
            }
        }
        if (totalItems == 0) {
            add(new JMenuItem(Messages
                    .getString("ArgumentSelectionPopup.noArguments"))); //$NON-NLS-1$
        }
        if (this.invisibleItems.size() > 0) {
            addSeparator();
            this.showInvisible = new JMenuItem(Messages
                    .getString("ArgumentSelectionPopup.showAll")); //$NON-NLS-1$
            this.showInvisible.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    remove(ArgumentSelectionPopup.this.showInvisible);
                    for (Iterator iter = ArgumentSelectionPopup.this.invisibleItems.iterator(); iter
                            .hasNext();) {
                        ArgumentPopupItem ami = (ArgumentPopupItem) iter.next();
                        add(ami);
                    }
                    show(ArgumentSelectionPopup.this.origComp, ArgumentSelectionPopup.this.origX, ArgumentSelectionPopup.this.origY);
                }
            });
            add(this.showInvisible);
        }
    }

    public void addInitLinkingListener(InitLinkingListener listener) {
        getDispatcher().addListener(InitLinkingListener.class, listener);
    }

    private EventDispatcher getDispatcher() {
        if (this.dispatcher == null)
            this.dispatcher = new SimpleEventDispatcher();
        return this.dispatcher;
    }

    public void removeInitLinkingListener(InitLinkingListener listener) {
        getDispatcher().removeListener(InitLinkingListener.class, listener);
    }

    @Override
    public void show(Component c, int x, int y) {
        this.origX = x;
        this.origY = y;
        this.origComp = c;
        super.show(c, x, y);
    }

}