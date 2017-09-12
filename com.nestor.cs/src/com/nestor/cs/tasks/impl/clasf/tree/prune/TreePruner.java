//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// TreePruner.java (sed.data.clasf.tree)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree.prune;

import com.nestor.cs.tasks.def.TreeNode;

public interface TreePruner {

    public static final int NO_PRUNING = 0;

    public static final int REMOVE_NODE = 1;

    public static final int REMOVE_SUBNODES = 2;

    public int pruneNode(TreeNode node);

    public void pruneTree(TreeNode node);

}