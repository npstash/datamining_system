//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiagramElementFactoryTreeNode.java (sed.ui.element)
// Utworzono 2005-07-16 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document.elements;

import java.util.ArrayList;
import java.util.Iterator;

import com.nestor.cs.Messages;
import com.nestor.cs.util.util.TreeNode;

public class DocumentElementTreeNode {

    private String description = null;

    private DocumentElementFactory factory = null;

    private String name = null;

    private ArrayList subNodes = new ArrayList();

    protected DocumentElementTreeNode() {
    }

    protected DocumentElementTreeNode(TreeNode tn) {
        String nameKey = (String) tn.attributes.get("nameKey"); //$NON-NLS-1$
        if (nameKey != null) {
            String descKey = nameKey + ".desc"; //$NON-NLS-1$
            this.description = Messages.getString(descKey);
        }
        try {
            this.factory = new DocumentElementFactory(tn);
        } catch (Exception e) {
            if (nameKey != null)
                this.name = Messages.getString(nameKey);
        }
        for (Iterator iter = tn.nodes.iterator(); iter.hasNext();) {
            TreeNode element = (TreeNode) iter.next();
            this.subNodes.add(new DocumentElementTreeNode(element));
        }
    }

    public String getDescription() {
        return this.description;
    }

    public DocumentElementFactory getFactory() {
        return this.factory;
    }

    public String getName() {
        return (getFactory() != null) ? getFactory().getElementName() : this.name;
    }

    public ArrayList getSubNodes() {
        return this.subNodes;
    }

    public DocumentElementTreeComponent getTreeComponent() {
        return (getFactory() != null) ? getFactory().getTreeComponent() : null;
    }
}