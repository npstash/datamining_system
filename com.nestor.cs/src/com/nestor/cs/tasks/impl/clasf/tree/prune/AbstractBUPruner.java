//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractPruner.java (sed.data.clasf.tree.prune)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree.prune;

import com.nestor.cs.Messages;
import com.nestor.cs.tasks.def.TreeNode;
import com.nestor.cs.tasks.impl.ConfigurableTaskHelper;
import com.nestor.cs.util.util.param.Argument;

public abstract class AbstractBUPruner extends ConfigurableTaskHelper implements
        TreePruner {

    public static int pruneTreeBottomUp(TreeNode tn, TreePruner tp) {
        if (tn == null)
            return NO_PRUNING;
        if (!tn.isLeaf()) {
            for (int i = 0; i < tn.getTestBranchCount(); i++) {
                TreeNode branch = tn.getChildNodeForBranch(i);
                int res = pruneTreeBottomUp(branch, tp);

                if (res == REMOVE_SUBNODES)
                    branch.changeIntoLeaf();
                else if (res == REMOVE_NODE)
                    tn.removeBranch(i);
            }
        }
        return tp.pruneNode(tn);
    }

    public final Argument argFollowingPruning = new Argument();

    public AbstractBUPruner() {
        super();

        this.argFollowingPruning.setName(Messages
                .getString("AbstractBUPruner.argPrune")); //$NON-NLS-1$
        this.argFollowingPruning.setDescription(Messages
                .getString("AbstractBUPruner.argDescPrune")); //$NON-NLS-1$
        this.argFollowingPruning.setValueType(TreePruner.class);
        this.argFollowingPruning.setRequired(false);
        getConfigurationArguments().addArgument(this.argFollowingPruning);
    }

    public void pruneTree(TreeNode tn) {
        int res = pruneTreeBottomUp(tn, this);
        if (res == TreePruner.REMOVE_SUBNODES)
            tn.changeIntoLeaf();
        TreePruner tp = (TreePruner) this.argFollowingPruning.getValue();
        if (tp != null) {
            tp.pruneTree(tn);
        }
    }

}