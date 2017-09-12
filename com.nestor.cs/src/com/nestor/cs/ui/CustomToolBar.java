//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// CustomToolBar.java (sed.ui)
// Utworzono 2005-07-27 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui;

import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.Action;
import javax.swing.JToolBar;

import com.nestor.cs.Messages;
import com.nestor.cs.ui.actions.SharedActionRegistry;
import com.nestor.cs.util.swing.ExtJIconButton;
import com.nestor.cs.util.util.TreeLoader;
import com.nestor.cs.util.util.TreeNode;

public class CustomToolBar extends JToolBar {

    public static final String ATTR_ACTIONCLASS = "actionclass"; //$NON-NLS-1$

    public CustomToolBar() {
        super();
        TreeNode ctnActions = null;
        try {
            ctnActions = TreeLoader.load(getClass().getResourceAsStream(
                    "/com/nestor/cs/config/toolbaractions.xml")); //$NON-NLS-1$
        } catch (Exception e) {
            SystemFrame.getInstance().messageArea
                    .showException(new Exception(Messages
                            .getString("CustomToolBar.errorLoadingToolbar"), e)); //$NON-NLS-1$
        }

        Iterator iter = ctnActions.nodes.iterator();
        Stack its = new Stack();
        do {
            while (iter.hasNext()) {
                TreeNode node = (TreeNode) iter.next();
                if (node.nodes.size() > 0) {
                    its.push(iter);
                    iter = node.nodes.iterator();
                } else {
                    try {
                        ExtJIconButton jib = createMenuItem(node);
                        if (jib != null)
                            add(jib);
                    } catch (Exception e) {
                        Exception e1 = new Exception(
                                Messages
                                        .getString("CustomToolBar.errorLoadingToolbarItem") + " (" + node //$NON-NLS-1$ //$NON-NLS-2$
                                        + ")", e); //$NON-NLS-1$
                        SystemFrame.getInstance().messageArea.showException(e1);
                    }
                }
            } // while
            if (its.size() > 0) {
                iter = (Iterator) its.pop();
                addSeparator();
            } else
                break;
        } while (true);

        setOrientation(HORIZONTAL);
    }

    private ExtJIconButton createMenuItem(TreeNode node) throws Exception {
        if (!node.attributes.containsKey(ATTR_ACTIONCLASS))
            return null;
        Class actionClass = Class.forName(node.attributes.get(ATTR_ACTIONCLASS)
                .toString());
        Object actionObj = SharedActionRegistry
                .getActionSharedInstance(actionClass);

        if (actionObj instanceof Action)
            return new ExtJIconButton((Action) actionObj);
        else if (actionObj instanceof ActionListener) {
            ExtJIconButton item = new ExtJIconButton();
            item.addActionListener((ActionListener) actionObj);
            return item;
        } else
            return null;
    }

}