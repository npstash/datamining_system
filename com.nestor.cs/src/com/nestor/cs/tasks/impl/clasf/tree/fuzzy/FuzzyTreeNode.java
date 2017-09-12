//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// FuzzyTreeNode.java (sed.data.clasf.tree.fuzzy)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree.fuzzy;

import java.util.Collection;
import java.util.Iterator;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.ProbDistribution;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.tasks.def.TreeClassifier;
import com.nestor.cs.tasks.def.TreeNode;

public class FuzzyTreeNode extends TreeNode {

    public FuzzyTreeNode(TreeClassifier tree, Distribution classDistribution) {
        super(tree, classDistribution);
    }

    public FuzzyTreeNode(TreeClassifier tree, FuzzyTreeNodeTest test,
            Distribution classDistribution) {
        super(tree, test, classDistribution);
    }

    public FuzzyTreeNode(TreeNode tn) {
        super(tn.getTree(), tn.getClassDistribution());
        if (!tn.isLeaf()) {
            changeIntoNode(new FuzzyTreeNodeTest(tn.getTest()));
            for (int i = 0; i < tn.getTestBranchCount(); i++) {
                TreeNode subNode = tn.getChildNodeForBranch(i);
                if (subNode == null)
                    continue;

                TreeNode repl;
                if (subNode.isLeaf()) {
                    repl = new FuzzyTreeNode(subNode.getTree(), subNode
                            .getClassDistribution());
                } else {
                    repl = new FuzzyTreeNode(subNode);
                }
                addBranch(i, repl);
            } // for
        }
    }

    @Override
    public Object classify(ReadOnlyItemset it) {
        Object majorLabel = null;
        if (!isLeaf()) {
            Object value = it.getValue(this.test.getTestedAttributeIndex());
            ProbDistribution pd = fuzzyClassify(getClassDistribution()
                    .getLabels(), it);
            majorLabel = pd.getMajorLabel();
        }
        if (majorLabel == null)
            majorLabel = getDefaultClassLabel();
        return majorLabel;
    }

    public ProbDistribution fuzzyClassify(Collection clLabels,
            ReadOnlyItemset it) {
        FuzzyTreeNodeTest ftnt = (FuzzyTreeNodeTest) this.test;
        ProbDistribution pd = ProbDistribution.createProbDistribution(clLabels);
        if (isLeaf()) {
            pd.initProbabilities(getClassDistribution());
        } else {
            double probs[] = ftnt.getBranchProbabilities(it);
            ProbDistribution[] subCps = new ProbDistribution[probs.length];
            for (int i = 0; i < probs.length; i++) {
                if (probs[i] == 0)
                    continue;
                FuzzyTreeNode subNode = (FuzzyTreeNode) getChildNodeForBranch(i);
                if (subNode == null)
                    continue;
                subCps[i] = subNode.fuzzyClassify(clLabels, it);
                subCps[i].multiplyProbabilities(probs[i]);
            }
            pd.addProbabilities(subCps);
        }
        return pd;
    }

    @Override
    public String getNodeDescription() {
        AttributeSet as = getTree().getLearningContext().getAttributeSet();
        Attribute clAttr = as.getAttribute(as.getClassLabelIndex());
        if (clAttr == null)
            return null;

        ProbDistribution pd = ProbDistribution
                .createProbDistribution(this.classDistribution.getLabels());
        pd.initProbabilities(this.classDistribution);

        String s = ""; //$NON-NLS-1$
        boolean first = true;
        for (Iterator i = pd.getLabels().iterator(); i.hasNext();) {
            Object label = i.next();

            if (pd.getProbability(label) == 0)
                continue;
            if (!first) {
                s += "; "; //$NON-NLS-1$
            }
            int ip = (int) (pd.getProbability(label) * 1000d);
            double p = ip / 1000d;
            s += "P(" + clAttr.getName() + "="; //$NON-NLS-1$ //$NON-NLS-2$
            s += clAttr.getNameForValue(label) + ")=" + p; //$NON-NLS-1$
            first = false;
        }
        return s;
    }
}