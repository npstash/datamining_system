//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// TextView.java (sed.ui.elements)
// Utworzono 2005-08-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

import com.nestor.cs.Messages;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class TextView extends AbstractDiagramComponent {

    public final Argument argTextDocument = new Argument();

    public final Argument argNote = new Argument();

    public TextView() {
        super();
        this.argTextDocument.setName(Messages.getString("TextView.argTextDocument")); //$NON-NLS-1$
        this.argTextDocument.setDescription(Messages
                .getString("TextView.argDescTextDocument")); //$NON-NLS-1$
        this.argTextDocument.setValueType(Document.class);
        getConfigurationArguments().addArgument(this.argTextDocument);

        this.argNote.setName(Messages.getString("TextView.argNote")); //$NON-NLS-1$
        this.argNote.setDescription(Messages.getString("TextView.argDescNote")); //$NON-NLS-1$
        this.argNote.setRequired(false);
        this.argNote.setValueType(String.class);
        getConfigurationArguments().addArgument(this.argNote);
    }

    public Document getArgDocument() {
        return (Document) this.argTextDocument.getValue();
    }

    public String getName() {
        String s = (this.argNote.getValue() != null) ? (String) this.argNote.getValue()
                : ""; //$NON-NLS-1$
        return Messages.getString("TextView.textView") + " [" + s + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void initProcess() throws ProcessingException {
        if (!getConfigurationArguments().areRequiredArgumentsFilled())
            throw new MissingArgumentValue(getConfigurationArguments());

        JTextArea jta = new JTextArea(getArgDocument());
        jta.setEditable(false);

        setPreparedComponent(new JScrollPane(jta));
    }

}