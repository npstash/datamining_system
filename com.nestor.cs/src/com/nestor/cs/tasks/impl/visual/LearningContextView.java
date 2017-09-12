//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// LearningContextView.java (sed.ui.elements)
// Utworzono 2005-08-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

import com.nestor.cs.Messages;
import com.nestor.cs.tasks.def.LearningMethod;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class LearningContextView extends AbstractDiagramComponent {

    public final Argument argLearningMethod = new Argument();

    public LearningContextView() {
        super();
        this.argLearningMethod.setName(Messages
                .getString("LearningContextView.argLearningMethod")); //$NON-NLS-1$
        this.argLearningMethod.setDescription(Messages
                .getString("LearningContextView.argDescLearningMethod")); //$NON-NLS-1$
        this.argLearningMethod.setValueType(LearningMethod.class);
        getConfigurationArguments().addArgument(this.argLearningMethod);
    }

    public LearningMethod getArgLearningMethod() {
        return (LearningMethod) this.argLearningMethod.getValue();
    }

    public String getName() {
        return Messages.getString("LearningContextView.0") //$NON-NLS-1$
                + getArgLearningMethod().getClass().getSimpleName() + "]"; //$NON-NLS-1$
    }

    public void initProcess() throws ProcessingException {
        if (!getConfigurationArguments().areRequiredArgumentsFilled())
            throw new MissingArgumentValue(getConfigurationArguments());

        Document doc = getArgLearningMethod().getLearningContext()
                .getDocument();
        JTextArea jta = new JTextArea(doc);
        jta.setEditable(false);
        jta.setAutoscrolls(true);

        setPreparedComponent(new JScrollPane(jta));
    }

}