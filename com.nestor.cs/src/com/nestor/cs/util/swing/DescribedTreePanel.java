//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// TreePanel.java (sed.ui.elements)
// Utworzono 2005-08-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import com.nestor.cs.util.util.GraphicsTools;

public class DescribedTreePanel extends JPanel implements Scrollable {

    private class BranchComponent extends JLabel {

        public BranchComponent(DescribedTreeNode node, int b) {
            super(node.getSubDescription(b));
            setBorder(BorderFactory.createRaisedBevelBorder());
            setBackground(Color.CYAN);
            setOpaque(true);
        }

    }

    private class LayoutRes {

        Component nCmp;

        Point p;

        public LayoutRes(Point p, Component cmp) {
            super();
            this.p = p;
            this.nCmp = cmp;
        }
    }

    private class NodeComponent extends JLabel {

        public NodeComponent(DescribedTreeNode node) {
            super();
            if (DescribedTreePanel.this.showNodeText || node.isLeaf()) {
                super.setText(node.getNodeDescription());
            } else {
                super.setText("       ");
            }
            setBorder(BorderFactory.createLoweredBevelBorder());
            setBackground(Color.YELLOW);
            setOpaque(true);
        }

    }

    private AbsoluteXYLayout layout;

    private ArrayList lines = new ArrayList();

    private int minHeightPerNode = 100;

    private int minWidthPerNode = 250;

    private boolean showNodeText;

    private int tdepth;

    private int[] tlevelFill;

    private int[] tlevels;

    private DescribedTree tree;

    private int twidth;

    public DescribedTreePanel(DescribedTree tree) {
        this(tree, true);
    }

    public DescribedTreePanel(DescribedTree tree, boolean showNodeText) {
        super();
        this.showNodeText = showNodeText;
        this.layout = new AbsoluteXYLayout(0.5f, 0.5f);
        setLayout(this.layout);
        this.tree = tree;

        this.tdepth = DescribedTreeNodes.getTreeDepth(tree.getRootNode());
        this.twidth = DescribedTreeNodes.getTreeWidth(tree.getRootNode());
        this.tlevels = DescribedTreeNodes.getTreeLevelWidths(tree.getRootNode());

        this.tlevelFill = new int[this.tlevels.length];
        layoutNode(tree.getRootNode(), 0);
    }

    public int getMinHeightPerNode() {
        return this.minHeightPerNode;
    }

    public int getMinWidthPerNode() {
        return this.minWidthPerNode;
    }

    public Dimension getPreferredScrollableViewportSize() {
        return this.layout.preferredLayoutSize(this);
    }

    public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
        if (o == SwingConstants.VERTICAL)
            return this.minHeightPerNode >> 1;
        else
            return this.minWidthPerNode >> 1;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
        if (o == SwingConstants.VERTICAL)
            return this.minHeightPerNode >> 1;
        else
            return this.minWidthPerNode >> 1;
    }

    public boolean getShowNodeText() {
        return this.showNodeText;
    }

    public LayoutRes layoutNode(DescribedTreeNode node, int level) {
        if (node == null)
            return null;

        int lw = (this.twidth * this.minWidthPerNode) / this.tlevels[level];
        int x = (this.tlevelFill[level] * lw) + (lw >> 1);
        int y = (level * this.minHeightPerNode) + (this.minHeightPerNode >> 1);
        Point p = new Point(x, y);
        Component nCmp = new NodeComponent(node);
        add(nCmp, p);
        this.tlevelFill[level]++;
        for (int i = 0; i < node.getSubNodeCount(); i++) {
            LayoutRes lr = layoutNode(node.getSubNode(i), level + 1);
            if (lr == null)
                continue;

            Point pBranch = new Point((p.x + lr.p.x) >> 1, (p.y + lr.p.y) >> 1);
            add(new BranchComponent(node, i), pBranch);

            Point pFrom = GraphicsTools.getComponentBorderingPointOnLine(nCmp,
                    p, lr.p);
            Point pTo = GraphicsTools.getComponentBorderingPointOnLine(lr.nCmp,
                    lr.p, p);
            Line2D.Float line = new Line2D.Float(pFrom, pTo);
            this.lines.add(line);
        }
        return new LayoutRes(p, nCmp);

    }

    @Override
    protected void paintChildren(Graphics g) {
        for (int i = 0; i < this.lines.size(); i++) {
            Line2D.Float line = (Line2D.Float) this.lines.get(i);
            GraphicsTools.drawArrow(g, line.getP1(), line.getP2(), 15, 30);
        }

        super.paintChildren(g);
    }

    public void setMinHeightPerNode(int minHeightPerNode) {
        this.minHeightPerNode = minHeightPerNode;
    }

    public void setMinWidthPerNode(int minWidthPerNode) {
        this.minWidthPerNode = minWidthPerNode;
    }

    public void setShowNodeText(boolean showNodeText) {
        this.showNodeText = showNodeText;
        removeAll();
        this.tlevelFill = new int[this.tlevels.length];
        layoutNode(this.tree.getRootNode(), 0);
    }
}