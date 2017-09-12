//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// MenuBarLoader.java (sed.ui)
// Utworzono 2005-07-22 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui;

import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.nestor.cs.Messages;
import com.nestor.cs.ui.actions.SharedActionRegistry;
import com.nestor.cs.util.util.TreeLoader;
import com.nestor.cs.util.util.TreeNode;

public class CustomMenuBar extends JMenuBar {

    public static final String ATTR_ACTIONCLASS = "actionclass"; //$NON-NLS-1$

    public static final String ATTR_ITEMCLASS = "itemclass"; //$NON-NLS-1$

    public static final String ATTR_NAME = "name"; //$NON-NLS-1$

    public CustomMenuBar() {
        super();
        try {
            TreeNode ctnActions = TreeLoader.load(getClass()
                    .getResourceAsStream("/com/nestor/cs/config/menubaractions.xml")); //$NON-NLS-1$
            Iterator iter = ctnActions.nodes.iterator();
            JMenu parent = null, recent = null;
            Stack its = new Stack();
            do {
                while (iter.hasNext()) {
                    TreeNode node = (TreeNode) iter.next();
                    if (node.nodes.size() > 0) {
                        recent = new JMenu("" + node.attributes.get("name")); //$NON-NLS-1$ //$NON-NLS-2$
                        if (parent == null)
                            add(recent);
                        else
                            parent.add(recent);
                        its.push(iter);
                        iter = node.nodes.iterator();
                        its.push(parent);
                        parent = recent;
                    } else {
                        try {
                            JMenuItem jmi = createMenuItem(node);
                            if (jmi != null) {
                                if (parent == null)
                                    add(jmi);
                                else
                                    parent.add(jmi);
                            }
                        } catch (Exception e) {
                            SystemFrame.getInstance().messageArea
                                    .showException(new Exception(
                                            Messages
                                                    .getString("CustomMenuBar.errorLoadingMenuItem") + "(" //$NON-NLS-1$ //$NON-NLS-2$
                                                    + node + ")", e)); //$NON-NLS-1$
                        }
                    }
                } // while
                if (its.size() > 0) {
                    parent = (JMenu) its.pop();
                    iter = (Iterator) its.pop();
                } else
                    break;
            } while (true);

        } catch (Exception e) {
            SystemFrame.getInstance().messageArea.showException(new Exception(
                    Messages.getString("CustomMenuBar.errorLoadingMenu"), e)); //$NON-NLS-1$
        }
    }

    private JMenuItem createMenuItem(TreeNode node) throws Exception {
        if (node.attributes.containsKey(ATTR_ACTIONCLASS)) {
            JMenuItem item = null;
            String nodeName = null;
            if (node.attributes.containsKey(ATTR_NAME))
                nodeName = node.attributes.get(ATTR_NAME) + ""; //$NON-NLS-1$

            Class actionClass = Class.forName(node.attributes.get(
                    ATTR_ACTIONCLASS).toString());
            Object actionObj = SharedActionRegistry
                    .getActionSharedInstance(actionClass);

            if (node.attributes.containsKey(ATTR_ITEMCLASS)) {
                Class itemClass = Class.forName(node.attributes.get(
                        ATTR_ITEMCLASS).toString());
                item = (JMenuItem) itemClass.newInstance();
            } else {
                item = new JMenuItem();
            }

            if (actionObj instanceof Action) {
                item.setAction((Action) actionObj);
                if (nodeName != null)
                    item.setText(nodeName);
            } else if (actionObj instanceof ActionListener) {
                item.addActionListener((ActionListener) actionObj);
                if (nodeName != null)
                    item.setText(nodeName);
                else
                    item.setText(actionClass.getName());
            } else {
                return null;
            }
            return item;
        } else {
            return null;
        }
    }

}