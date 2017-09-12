//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiagramElementFactoryRegistry.java (sed.ui.element)
// Utworzono 2005-07-16 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document.elements;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import com.nestor.cs.util.util.TreeLoader;

public abstract class DocumentElementFactoryRegistry {

    public static HashMap registeredFactories = loadElementFactories();

    public static DocumentElementTreeNode rootContainer;

    private static boolean hasElementClass(DocumentElementTreeNode detn) {
        if (detn.getFactory() == null)
            return false;
        return (detn.getFactory().getElementClass() != null);
    }

    private static HashMap loadElementFactories() {
        try {
            rootContainer = new DocumentElementTreeNode(TreeLoader
                    .load(DocumentElementFactoryRegistry.class
                            .getResourceAsStream("/com/nestor/cs/config/tasks.xml"))); //$NON-NLS-1$
        } catch (Exception e) {
            rootContainer = new DocumentElementTreeNode();
        }
        HashMap hm = new HashMap();
        Stack stack = new Stack();
        Iterator iter = rootContainer.getSubNodes().iterator();
        if (hasElementClass(rootContainer))
            hm.put(rootContainer.getFactory().getElementClass(), rootContainer
                    .getFactory());
        for (;;) {
            do {
                DocumentElementTreeNode ec = (DocumentElementTreeNode) iter
                        .next();
                if (hasElementClass(ec))
                    hm.put(ec.getFactory().getElementClass(), ec.getFactory());
                if (!ec.getSubNodes().isEmpty()) {
                    stack.push(iter);
                    iter = ec.getSubNodes().iterator();
                }
            } while (iter.hasNext());
            while (!iter.hasNext()) {
                if (stack.isEmpty())
                    return hm;
                iter = (Iterator) stack.pop();
            }
        }
    }

}