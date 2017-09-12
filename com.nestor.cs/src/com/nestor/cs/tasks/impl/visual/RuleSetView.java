//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// RulesetView.java (sed.ui.elements)
// Utworzono 2005-08-01 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Rule;
import com.nestor.cs.tasks.def.LearningContext;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.tasks.def.RuleSet;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class RuleSetView extends AbstractDiagramComponent {

    public final Argument argRuleset = new Argument();

    public RuleSetView() {
        super();
        this.argRuleset.setName(Messages.getString("RuleSetView.argRuleset")); //$NON-NLS-1$
        this.argRuleset.setDescription(Messages
                .getString("RuleSetView.argDescRuleset")); //$NON-NLS-1$
        this.argRuleset.setValueType(RuleSet.class);
        getConfigurationArguments().addArgument(this.argRuleset);
    }

    private JTextArea createTextArea(RuleSet rs, int v) {
        PlainDocument pDoc = new PlainDocument();
        AttributeSet textAs = pDoc.getDefaultRootElement().getAttributes();

        if (v >= 0) {
            for (int r = 0; r < rs.getRuleCount(v); r++) {
                Rule rule = rs.getRule(v, r);
                try {
                    int supp100 = (int) (rule.getSupport() * 10000d);
                    double supp = supp100 / 100d;
                    int conf = (int) (rule.getConfidence() * 100d);
                    String s = r
                            + Messages.getString("RuleSetView.conf") + conf + Messages.getString("RuleSetView.supp") + supp + "% "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    s += rule.toString() + "\n"; //$NON-NLS-1$
                    pDoc.insertString(pDoc.getLength(), s, textAs);
                } catch (BadLocationException e) {
                }
            }
        } else {
            for (int r = 0; r < rs.getRuleCount(); r++) {
                Rule rule = rs.getRule(r);
                try {
                    int supp100 = (int) (rule.getSupport() * 10000d);
                    double supp = supp100 / 100d;
                    int conf = (int) (rule.getConfidence() * 100d);
                    String s = r
                            + Messages.getString("RuleSetView.conf") + conf + Messages.getString("RuleSetView.supp") + supp + "% "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    s += rule.toString() + "\n"; //$NON-NLS-1$
                    pDoc.insertString(pDoc.getLength(), s, textAs);
                } catch (BadLocationException e) {
                }
            }
        }

        JTextArea jta = new JTextArea(pDoc);
        jta.setEditable(false);
        return jta;
    }

    public String getName() {
        RuleSet rs = (RuleSet) this.argRuleset.getValue();
        String s = (rs != null) ? Messages.getString("RuleSetView.0") + rs.getClass().getSimpleName() + "]" //$NON-NLS-1$ //$NON-NLS-2$
                : ""; //$NON-NLS-1$
        return Messages.getString("RuleSetView.ruleList") + s; //$NON-NLS-1$
    }

    public void initProcess() throws ProcessingException {
        if (!getConfigurationArguments().areRequiredArgumentsFilled())
            throw new MissingArgumentValue(getConfigurationArguments());

        RuleSet rs = (RuleSet) this.argRuleset.getValue();
        LearningContext lc = rs.getLearningContext();
        Attribute clAttr = lc.getAttributeSet().getAttribute(
                lc.getAttributeSet().getClassLabelIndex());

        JTabbedPane tabs = new JTabbedPane();
        JTextArea jta0 = createTextArea(rs, -1);
        tabs
                .addTab(
                        Messages.getString("RuleSetView.allRules"), new JScrollPane(jta0)); //$NON-NLS-1$
        for (int v = 0; v < clAttr.getAllowedValuesCount(); v++) {
            JTextArea jta = createTextArea(rs, v);
            Object clValue = clAttr.getAllowedValuesNames()[v];
            tabs.addTab(clAttr.getName() + "=" + clValue, new JScrollPane(jta)); //$NON-NLS-1$
        }

        setPreparedComponent(tabs);
    }

}