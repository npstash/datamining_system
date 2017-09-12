//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ZinglerFuzzy.java (sed.data.clasf.tree)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree.fuzzy;

import com.nestor.cs.Messages;
import com.nestor.cs.tasks.def.MultiResultTask;
import com.nestor.cs.tasks.impl.ConfigurableTaskHelper;
import com.nestor.cs.tasks.impl.clasf.tree.AbstractTree;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class ZeidlerFuzzy extends ConfigurableTaskHelper implements
        MultiResultTask {

    public final Argument argFuzzedTree = new Argument();

    private TaskResult[] faces;

    private FuzzyTree fuzzyTree;

    public ZeidlerFuzzy() {
        super();

        this.argFuzzedTree
                .setName(Messages.getString("ZeidlerFuzzy.argTreeToFuzzy")); //$NON-NLS-1$
        this.argFuzzedTree.setDescription(Messages
                .getString("ZeidlerFuzzy.argDescTreeToFuzzy")); //$NON-NLS-1$
        this.argFuzzedTree.setValueType(AbstractTree.class);
        getConfigurationArguments().addArgument(this.argFuzzedTree);

        this.fuzzyTree = new FuzzyTree();
        this.faces = new TaskResult[1];
        this.faces[0] = new TaskResult(
                Messages.getString("ZeidlerFuzzy.2"), this, this.fuzzyTree); //$NON-NLS-1$
    }

    public AbstractTree getArgFuzzedTree() {
        return (AbstractTree) this.argFuzzedTree.getValue();
    }

    public TaskResult[] getTaskResults() {
        return this.faces;
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        this.fuzzyTree.initFuzzyTree(getArgFuzzedTree());
    }
}