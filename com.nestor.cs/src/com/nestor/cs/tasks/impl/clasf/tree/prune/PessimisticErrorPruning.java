//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// PessimisticErrorPruning.java (sed.data.clasf.tree.prune)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree.prune;

import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.tasks.def.TreeNode;

public class PessimisticErrorPruning extends AbstractTDPruner {

    public static double get_q_t(TreeNode tn) {
        Distribution dNode = tn.getClassDistribution();
        Object majorClass = dNode.getMajorLabel();
        if (majorClass == null)
            majorClass = tn.getDefaultClassLabel();
        double n = dNode.getTotalExamples();
        double ni = 0;
        if (majorClass != null)
            ni = dNode.getCount(majorClass);
        double q_t = n - ni + 0.5;
        return q_t;
    }

    public static double get_Q_t(TreeNode tn) {
        if (tn.isLeaf()) {
            return get_q_t(tn);
        } else {
            double Q_t = 0;
            for (int i = 0; i < tn.getTestBranchCount(); i++) {
                TreeNode subNode = tn.getChildNodeForBranch(i);
                if (subNode == null)
                    continue;
                double Q_ti = get_Q_t(subNode);
                Q_t += Q_ti;
            }
            return Q_t;
        }
    }

    public static int countLeafs(TreeNode tn) {
        if (tn.isLeaf())
            return 1;
        int leafs = 0;
        for (int i = 0; i < tn.getTestBranchCount(); i++) {
            TreeNode subNode = tn.getChildNodeForBranch(i);
            if (subNode == null)
                continue;
            leafs += countLeafs(subNode);
        }
        return leafs;
    }

    public int pruneNode(TreeNode tn) {
        if (tn.isLeaf())
            return NO_PRUNING;

        double n = tn.getClassDistribution().getTotalExamples();
        double leafs = countLeafs(tn);

        double q_t = get_q_t(tn);
        double Q_t = get_Q_t(tn) + leafs / 2;
        double SE_Q_t = Math.sqrt(Q_t * (n - Q_t) / n);

        if (q_t <= Q_t + SE_Q_t)
            return REMOVE_SUBNODES;
        else
            return NO_PRUNING;
    }
}