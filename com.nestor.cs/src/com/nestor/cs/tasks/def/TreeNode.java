//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// TreeNode.java (sed.data.clasf.tree)
// Utworzono 2005-08-02 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.def;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.util.swing.DescribedTreeNode;

public class TreeNode implements DescribedTreeNode {

    private LinkedHashMap childNodes;

    protected Distribution classDistribution;

    private int maxBranchNum = -1;

    protected TreeNodeTest test;

    private TreeClassifier tree;

    /**
     * Tworzy li��
     * 
     * @param classDistribution
     */
    public TreeNode(TreeClassifier tree, Distribution classDistribution) {
        this.test = null;
        this.classDistribution = classDistribution;
        this.childNodes = null;
        this.tree = tree;
    }

    /**
     * Tworzy w�ze� z testem
     * 
     * @param test
     * @param classDistribution
     */
    public TreeNode(TreeClassifier tree, TreeNodeTest test,
            Distribution classDistribution) {
        this.test = test;
        this.childNodes = new LinkedHashMap();
        this.classDistribution = classDistribution;
        this.tree = tree;
    }

    public void addBranch(int branchNum, TreeNode tn) {
        if (!isLeaf()) {
            if (tn != null) {
                this.childNodes.put(new Integer(branchNum), tn);
                if (branchNum > this.maxBranchNum)
                    this.maxBranchNum = branchNum;
            }
        }
    }

    public void changeIntoLeaf() {
        if (!isLeaf()) {
            this.childNodes.clear();
            this.childNodes = null;
            this.test = null;
            this.maxBranchNum = -1;
        }
    }

    public void changeIntoNode(TreeNodeTest test) {
        if (isLeaf()) {
            this.test = test;
            this.childNodes = new LinkedHashMap();
            this.maxBranchNum = -1;
        }
    }

    public Object classify(ReadOnlyItemset it) {
        if (!isLeaf()) {
            int b = this.test.selectBranch(it);
            if (b != TreeNodeTest.SELECT_NODE_CLASS) {
                TreeNode tn = getChildNodeForBranch(b);
                if (tn != null) {
                    return tn.classify(it);
                }
            }
        }
        return getDefaultClassLabel();
    }

    public TreeNode getChildNode(int childNum) {
        if (isLeaf())
            return null;

        ArrayList keys = new ArrayList(this.childNodes.keySet());
        Collections.sort(keys);
        int index = 0;
        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            Integer key = (Integer) iter.next();
            if (childNum == index++)
                return (TreeNode) this.childNodes.get(key);
        }
        return null;
    }

    public int getChildNodeCount() {
        if (isLeaf())
            return 0;
        else
            return this.childNodes.size();
    }

    public TreeNode getChildNodeForBranch(int branchNum) {
        if (!isLeaf()) {
            return (TreeNode) this.childNodes.get(new Integer(branchNum));
        } else
            return null;
    }

    public Distribution getClassDistribution() {
        return this.classDistribution;
    }

    public Object getDefaultClassLabel() {
        Object majorLabel = this.classDistribution.getMajorLabel();
        if (majorLabel == null)
            majorLabel = this.tree.getTreeRoot().getClassDistribution()
                    .getFirstMajorLabel();
        return majorLabel;
    }

    public String getNodeDescription() {
        AttributeSet as = this.tree.getLearningContext().getAttributeSet();
        Attribute clAttr = as.getAttribute(as.getClassLabelIndex());
        if (clAttr == null)
            return null;
        Object clLabel = getDefaultClassLabel();
        return clAttr.getName() + "=" + clAttr.getNameForValue(clLabel); //$NON-NLS-1$
    }

    public String getSubDescription(int i) {
        Attribute attr = this.tree.getLearningContext().getAttributeSet()
                .getAttribute(this.test.getTestedAttributeIndex());
        if (attr == null)
            return null;
        Test t = this.test.getTestForBranch(i);
        return t.toString(attr);
    }

    public DescribedTreeNode getSubNode(int i) {
        return getChildNodeForBranch(i);
    }

    public int getSubNodeCount() {
        return getTestBranchCount();
    }

    public TreeNodeTest getTest() {
        return this.test;
    }

    public int getTestBranchCount() {
        return this.maxBranchNum + 1;
    }

    public TreeClassifier getTree() {
        return this.tree;
    }

    public boolean isLeaf() {
        return (this.test == null);
    }

    public void removeBranch(int branchNum) {
        if (!isLeaf()) {
            this.childNodes.remove(new Integer(branchNum));
            if (branchNum == this.maxBranchNum) {
                this.maxBranchNum = -1;
                ArrayList keys = new ArrayList(this.childNodes.keySet());
                for (Iterator iter = keys.iterator(); iter.hasNext();) {
                    Integer key = (Integer) iter.next();
                    if (key.intValue() > this.maxBranchNum)
                        this.maxBranchNum = key.intValue();
                }
            }
        } // isLeaf
    }

    @Override
    public String toString() {
        return getNodeDescription();
    }

}