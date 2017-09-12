//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractRuleset.java (sed.data.clasf)
// Utworzono 2005-08-02 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.Rule;
import com.nestor.cs.tasks.def.RuleSet;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public abstract class AbstractRuleSetClassifier extends AbstractClassifier
        implements RuleSet {

    public final Argument argRuleFile = new Argument();

    private RankedRuleContainer rules;

    protected boolean rulesRestored = false;

    public AbstractRuleSetClassifier() {
        super();
        this.argRuleFile.setName(Messages
                .getString("AbstractRuleSetClassifier.argFile")); //$NON-NLS-1$
        this.argRuleFile.setDescription(Messages
                .getString("AbstractRuleSetClassifier.argDescFile")); //$NON-NLS-1$
        this.argRuleFile.setRequired(false);
        this.argRuleFile.setValueType(File.class);
        getConfigurationArguments().addArgument(this.argRuleFile);
    }

    protected void addRule(Rule r) throws IncompatibleAttributeset {
        this.rules.addRule(r);
    }

    public Rule getRule(int n) {
        return this.rules.getRule(n);
    }

    public Rule getRule(int classValueIndex, int n) {
        return this.rules.getRule(classValueIndex, n);
    }

    public int getRuleCount() {
        return this.rules.getRuleCount();
    }

    public int getRuleCount(int classValueIndex) {
        return this.rules.getRuleCount(classValueIndex);
    }

    protected RankedRuleContainer getRules() {
        return this.rules;
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        if (getArgTrainingDataset().getClassLabelIndex() < 0)
            throw new MissingClassLabelAttribute();

        if (this.argRuleFile.getValue() != null) {
            try {
                File f = (File) this.argRuleFile.getValue();
                ObjectInputStream oos = new ObjectInputStream(
                        new FileInputStream(f));
                this.rules = (RankedRuleContainer) oos.readObject();
                oos.close();
                AttributeSet as1 = this.rules.getAttributeSet();
                AttributeSet as2 = getArgTrainingDataset();
                if (!as1.equalsToAttributeSet(as2))
                    throw new IncompatibleAttributeset(as1, as2);
                this.rulesRestored = true;
            } catch (Exception e) {
                throw new ProcessingException(e);
            }
        } else {
            this.rules = new RankedRuleContainer(getArgTrainingDataset());
            this.rulesRestored = false;
        }
    }

    @Override
    public void learn(ReadOnlyDataset dsTrain) throws ProcessingException {
        super.learn(dsTrain);

        this.rules = new RankedRuleContainer(dsTrain);
        this.rulesRestored = false;
    }

    protected void setRules(RankedRuleContainer rules) {
        this.rules = rules;
    }
}