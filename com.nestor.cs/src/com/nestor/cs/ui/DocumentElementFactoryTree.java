//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ElementTree.java (sed.ui)
// Utworzono 2005-07-04 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.nestor.cs.Messages;
import com.nestor.cs.ui.document.elements.DocumentElementFactoryRegistry;
import com.nestor.cs.ui.document.elements.DocumentElementTransferHandler;
import com.nestor.cs.ui.document.elements.DocumentElementTreeNode;

public class DocumentElementFactoryTree extends JScrollPane implements
        TreeModel, TreeCellRenderer, TreeSelectionListener {

    private DefaultTreeCellRenderer defCellRenderer = new DefaultTreeCellRenderer();

    private EventListenerList listeners = new EventListenerList();

    private JTree tree;

    public DocumentElementFactoryTree() {
        super();
        this.tree = new JTree(this);
        this.tree.setRootVisible(false);
        this.tree.setShowsRootHandles(true);
        this.tree.setEditable(false);
        this.tree.setDragEnabled(true);
        this.tree.setTransferHandler(DocumentElementTransferHandler.sharedInstance);

        DefaultTreeSelectionModel defTSM = new DefaultTreeSelectionModel();
        defTSM.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.tree.setSelectionModel(defTSM);

        this.tree.setCellRenderer(this);
        this.tree.addTreeSelectionListener(this);

        setViewportView(this.tree);
    }

    public void addTreeModelListener(TreeModelListener listener) {
        this.listeners.add(TreeModelListener.class, listener);
    }

    public Object getChild(Object node, int i) {
        return ((DocumentElementTreeNode) node).getSubNodes().get(i);
    }

    public int getChildCount(Object node) {
        return ((DocumentElementTreeNode) node).getSubNodes().size();
    }

    public int getIndexOfChild(Object parent, Object child) {
        return ((DocumentElementTreeNode) parent).getSubNodes().indexOf(child);
    }

    public Object getRoot() {
        return DocumentElementFactoryRegistry.rootContainer;
    }

    public Component getTreeCellRendererComponent(JTree t, Object obj,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        DocumentElementTreeNode detn = (DocumentElementTreeNode) obj;
        if (detn.getTreeComponent() != null) {
            detn.getTreeComponent().setSelected(selected);
            return detn.getTreeComponent();
        } else
            return this.defCellRenderer.getTreeCellRendererComponent(t, detn
                    .getName(), selected, expanded, leaf, row, hasFocus);
    }

    public boolean isLeaf(Object node) {
        return ((DocumentElementTreeNode) node).getSubNodes().isEmpty();
    }

    public void removeTreeModelListener(TreeModelListener listener) {
        this.listeners.remove(TreeModelListener.class, listener);
    }

    public void valueChanged(TreeSelectionEvent sel) {
        if (sel == null)
            return;

        if (sel.getNewLeadSelectionPath() == null)
            return;

        Object lc = sel.getNewLeadSelectionPath().getLastPathComponent();
        if (lc instanceof DocumentElementTreeNode) {
            DocumentElementTreeNode node = (DocumentElementTreeNode) lc;
            String help = ""; //$NON-NLS-1$
            if (node.getFactory() != null) {
                help = "\n\n" //$NON-NLS-1$
                        + Messages.getString("DiagramElementFactoryTree.2"); //$NON-NLS-1$
            }
            if (node.getDescription() != null) {
                SystemFrame.getInstance().messageArea.showTemporaryLegend(node
                        .getDescription()
                        + help);
            }
        }
    }

    public void valueForPathChanged(TreePath path, Object value) {
    }
}