//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DescribedTreeNodes.java (nes.swing)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.swing;

import com.nestor.cs.util.util.ArrayUtils;

public abstract class DescribedTreeNodes {

    public static int getAllNodeCount(DescribedTreeNode tree) {
        if (tree == null)
            return 0;
        if (tree.isLeaf())
            return 1;
        int d = 0;
        for (int i = 0; i < tree.getSubNodeCount(); i++) {
            DescribedTreeNode tn = tree.getSubNode(i);
            if (tn != null) {
                int n = getAllNodeCount(tn);
                d += n;
            }
        }
        return d + 1;
    }

    public static int getLeafCount(DescribedTreeNode tree) {
        if (tree == null)
            return 0;
        if (tree.isLeaf())
            return 1;
        int d = 0;
        for (int i = 0; i < tree.getSubNodeCount(); i++) {
            DescribedTreeNode tn = tree.getSubNode(i);
            if (tn != null) {
                int n = getLeafCount(tn);
                d += n;
            }
        }
        return d;
    }

    public static int getNonLeafNodeCount(DescribedTreeNode tree) {
        if (tree == null)
            return 0;
        if (tree.isLeaf())
            return 0;
        int d = 0;
        for (int i = 0; i < tree.getSubNodeCount(); i++) {
            DescribedTreeNode tn = tree.getSubNode(i);
            if (tn != null) {
                int n = getNonLeafNodeCount(tn);
                d += n;
            }
        }
        return d + 1;
    }

    public static int getTreeDepth(DescribedTreeNode tree) {
        if (tree == null)
            return 0;
        int d = 0;
        for (int i = 0; i < tree.getSubNodeCount(); i++) {
            DescribedTreeNode tn = tree.getSubNode(i);
            if (tn != null) {
                int n = getTreeDepth(tn);
                if (n > d)
                    d = n;
            }
        }
        return d + 1;
    }

    public static void getTreeLevelWidth(DescribedTreeNode tree, int[] levels,
            int level) {
        if (tree == null)
            return;
        int mySubWidth = 0;
        for (int i = 0; i < tree.getSubNodeCount(); i++) {
            DescribedTreeNode tn = tree.getSubNode(i);
            if (tn != null)
                mySubWidth++;
        }
        if (mySubWidth > 0) {
            levels[level] += mySubWidth;
            for (int i = 0; i < tree.getSubNodeCount(); i++) {
                DescribedTreeNode tn = tree.getSubNode(i);
                if (tn != null)
                    getTreeLevelWidth(tn, levels, level + 1);
            }
        }
    }

    public static int[] getTreeLevelWidths(DescribedTreeNode tree) {
        if (tree == null)
            return new int[0];
        int[] levelWidth = new int[getTreeDepth(tree)];
        levelWidth[0] = 1;
        getTreeLevelWidth(tree, levelWidth, 1);
        return levelWidth;
    }

    public static int getTreeWidth(DescribedTreeNode tree) {
        if (tree == null)
            return 0;
        int[] levelWidth = getTreeLevelWidths(tree);
        return levelWidth[ArrayUtils.max(levelWidth)];
    }

}