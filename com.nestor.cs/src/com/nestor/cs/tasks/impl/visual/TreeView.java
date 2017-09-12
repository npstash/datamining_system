//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// TreeView.java (sed.ui.elements)
// Utworzono 2005-08-04 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.nestor.cs.Messages;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.swing.DescribedTree;
import com.nestor.cs.util.swing.DescribedTreeNodes;
import com.nestor.cs.util.swing.DescribedTreePanel;
import com.nestor.cs.util.util.param.Argument;

public class TreeView extends AbstractDiagramComponent {

    public final Argument argTree = new Argument();

    public final Argument argViewNodeDescr = new Argument(new Boolean(true));

    public final Argument argNote = new Argument();

    public TreeView() {
        super();
        this.argTree.setName(Messages.getString("TreeView.argTree")); //$NON-NLS-1$
        this.argTree.setDescription(Messages.getString("TreeView.argDescTree")); //$NON-NLS-1$
        this.argTree.setValueType(DescribedTree.class);
        getConfigurationArguments().addArgument(this.argTree);

        this.argViewNodeDescr.setName(Messages
                .getString("TreeView.argViewNodeDescr")); //$NON-NLS-1$
        this.argViewNodeDescr.setDescription(Messages
                .getString("TreeView.argDescViewNodeDescr")); //$NON-NLS-1$
        this.argViewNodeDescr.setRequired(false);
        getConfigurationArguments().addArgument(this.argViewNodeDescr);

        this.argNote.setName(Messages.getString("TreeView.argNote")); //$NON-NLS-1$
        this.argNote.setDescription(Messages.getString("TreeView.argNote")); //$NON-NLS-1$
        this.argNote.setRequired(false);
        this.argNote.setValueType(String.class);
        getConfigurationArguments().addArgument(this.argNote);
    }

    public boolean getArgViewNodeDescr() {
        Boolean b = (Boolean) this.argViewNodeDescr.getValue();
        if (b == null)
            return true;
        else
            return b.booleanValue();
    }

    public String getName() {
        String s = "";
        if (this.argNote.getValue() != null)
            s = this.argNote.getValue().toString() + " ";
        s += Messages.getString("TreeView.treeView"); //$NON-NLS-1$
        s += " [" + this.argTree.getValue().getClass().getSimpleName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
        return s;
    }

    public void initProcess() throws ProcessingException {
        if (!getConfigurationArguments().areRequiredArgumentsFilled())
            throw new MissingArgumentValue(getConfigurationArguments());

        DescribedTree dt = (DescribedTree) this.argTree.getValue();
        boolean showNodeText = getArgViewNodeDescr();
        DescribedTreePanel panel = new DescribedTreePanel(dt, showNodeText);
        JPanel jp = new JPanel(new BorderLayout());
        jp.add(new JScrollPane(panel), BorderLayout.CENTER);
        JPanel infos = new JPanel(new GridLayout(1, 4));
        infos.add(new JLabel("Rozmiar drzewa: "
                + DescribedTreeNodes.getAllNodeCount(dt.getRootNode())));
        infos.add(new JLabel("Liczba li�ci: "
                + DescribedTreeNodes.getLeafCount(dt.getRootNode())));
        infos.add(new JLabel("Wysoko��: "
                + DescribedTreeNodes.getTreeDepth(dt.getRootNode())));
        infos.add(new JLabel("Szeroko��: "
                + DescribedTreeNodes.getTreeWidth(dt.getRootNode())));
        jp.add(infos, BorderLayout.SOUTH);
        setPreparedComponent(jp);
    }

}