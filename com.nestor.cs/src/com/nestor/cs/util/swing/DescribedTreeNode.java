//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DescribedTreeNode.java (nes.swing)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.swing;

public interface DescribedTreeNode {

    public String getNodeDescription();

    public String getSubDescription(int i);

    public DescribedTreeNode getSubNode(int i);

    public int getSubNodeCount();

    public boolean isLeaf();
}