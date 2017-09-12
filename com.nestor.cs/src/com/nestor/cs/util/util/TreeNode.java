//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ClassTreeNode.java (javan.util)
// Utworzono 2005-07-03 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Node;

import com.nestor.cs.util.Messages;

public class TreeNode {

    public HashMap attributes = new HashMap();

    public ArrayList nodes = new ArrayList();

    public TreeNode() {
    }

    protected TreeNode(Node n) {
        for (int i = 0; i < n.getAttributes().getLength(); i++) {
            this.attributes.put(n.getAttributes().item(i).getNodeName(), n
                    .getAttributes().item(i).getNodeValue());
        }
        if (n.getChildNodes().getLength() > 0) {
            for (int i = 0; i < n.getChildNodes().getLength(); i++) {
                Node n2 = n.getChildNodes().item(i);
                try {
                    if (n2.getNodeName().equalsIgnoreCase("node")) //$NON-NLS-1$
                        this.nodes.add(new TreeNode(n2));
                } catch (Exception e) {
                    System.out
                            .println(Messages
                                    .getString("TreeNode.couldNotLoadNode") + n2.toString()); //$NON-NLS-1$
                }
            }
        }
    }

}