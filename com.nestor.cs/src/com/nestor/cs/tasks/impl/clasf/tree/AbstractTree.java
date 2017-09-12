//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Tree.java (sed.data.clasf.tree)
// Utworzono 2005-08-02 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.tasks.def.TreeClassifier;
import com.nestor.cs.tasks.def.TreeNode;
import com.nestor.cs.tasks.impl.clasf.AbstractClassifier;
import com.nestor.cs.tasks.impl.clasf.tree.prune.TreePruner;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.swing.DescribedTree;
import com.nestor.cs.util.swing.DescribedTreeNode;
import com.nestor.cs.util.util.param.Argument;

public abstract class AbstractTree extends AbstractClassifier implements
        TreeClassifier, DescribedTree {

    public final Argument argPruner = new Argument();

    protected TreeNode root = null;

    public AbstractTree() {
        super();

        this.argPruner.setName(Messages.getString("AbstractTree.argPruning")); //$NON-NLS-1$
        this.argPruner.setDescription(Messages
                .getString("AbstractTree.argDescPruning")); //$NON-NLS-1$
        this.argPruner.setValueType(TreePruner.class);
        this.argPruner.setRequired(false);
        getConfigurationArguments().addArgument(this.argPruner);
    }

    public Object classify(ReadOnlyDataset dsTrain, ReadOnlyItemset it)
            throws IncompatibleAttributeset {
        IncompatibleAttributeset.checkLength(dsTrain, it);
        return this.root.classify(it);
    }

    protected abstract TreeNode createRootNode(ReadOnlyDataset ds);

    public DescribedTreeNode getRootNode() {
        return this.root;
    }

    public TreeNode getTreeRoot() {
        return this.root;
    }

    @Override
    public void learn(ReadOnlyDataset dsTrain) throws ProcessingException {
        super.learn(dsTrain);

        this.root = createRootNode(dsTrain);
        TreePruner tp = (TreePruner) this.argPruner.getValue();
        if (tp != null) {
            tp.pruneTree(this.root);
        }
    }

    public boolean requiresLearning() {
        return true;
    }

}