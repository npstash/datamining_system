//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// MinimumSupportPruner.java (sed.data.clasf.tree.prune)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree.prune;

import com.nestor.cs.Messages;
import com.nestor.cs.tasks.def.TreeNode;
import com.nestor.cs.util.util.param.Argument;

public class MinimumSupportPruning extends AbstractBUPruner {

    public final Argument argMinSupport = new Argument(new Double(10));

    public MinimumSupportPruning() {
        super();
        this.argMinSupport.setName(Messages
                .getString("MinimumSupportPruning.argMinSupp")); //$NON-NLS-1$
        this.argMinSupport.setDescription(Messages
                .getString("MinimumSupportPruning.argDescMinSupp")); //$NON-NLS-1$
        this.argMinSupport.setLowBound(new Double(0));
        this.argMinSupport.setHighBound(new Double(100));
        getConfigurationArguments().addArgument(this.argMinSupport);
    }

    protected double getArgMinSupport() {
        if (this.argMinSupport.getValue() == null)
            return 0;
        return ((Double) this.argMinSupport.getValue()).doubleValue();
    }

    public int pruneNode(TreeNode tn) {
        double n = tn.getTree().getTreeRoot().getClassDistribution()
                .getTotalExamples();
        double ni = tn.getClassDistribution().getTotalExamples();
        boolean b = (ni / n) < (getArgMinSupport() / 100d);
        if (b)
            return REMOVE_NODE;
        else
            return NO_PRUNING;
    }

}