//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// MinimalErrorPruning.java (sed.data.clasf.tree.prune)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree.prune;

import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.tasks.def.TreeNode;

public abstract class MinimalErrorPruning extends AbstractBUPruner {

    public double get_e_t(TreeNode tn, Distribution dTrain) {
        Distribution dNode = tn.getClassDistribution();
        Object majorClass = tn.getDefaultClassLabel();
        double e_t;
        if (majorClass == null)
            e_t = 1;
        else
            e_t = 1 - getClassProbabilityEstimate(dTrain, dNode, majorClass);
        return e_t;
    }

    public double get_E_t(TreeNode tn, Distribution dTrain) {
        if (tn.isLeaf()) {
            return get_e_t(tn, dTrain);
        } else {
            Distribution dNode = tn.getClassDistribution();
            double n = dNode.getTotalExamples();
            double E_t = 0;
            for (int i = 0; i < tn.getTestBranchCount(); i++) {
                TreeNode subNode = tn.getChildNodeForBranch(i);
                if (subNode == null)
                    continue;
                Distribution di = subNode.getClassDistribution();
                double ni = di.getTotalExamples();
                double pi = ni / n;
                double E_ti = get_E_t(subNode, dTrain);
                E_t += pi * E_ti;
            }
            return E_t;
        }
    }

    public abstract double getClassProbabilityEstimate(Distribution dTrain,
            Distribution dNode, Object clLabel);

    public int pruneNode(TreeNode tn) {
        if (tn.isLeaf())
            return NO_PRUNING;

        Distribution dTrain = tn.getTree().getTreeRoot().getClassDistribution();

        double e_t = get_e_t(tn, dTrain);
        double E_t = get_E_t(tn, dTrain);

        if (e_t <= E_t)
            return REMOVE_SUBNODES;
        return NO_PRUNING;
    }

}