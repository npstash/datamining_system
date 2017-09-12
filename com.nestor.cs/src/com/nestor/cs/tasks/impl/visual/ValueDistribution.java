//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ValueDistribution.java (sed.ui.elements)
// Utworzono 2005-08-15 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ListDataListener;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSubset;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.tasks.def.MultiResultTask;
import com.nestor.cs.ui.document.GravString;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.grav.Grav;
import com.nestor.cs.util.util.grav.GravRectangle;
import com.nestor.cs.util.util.grav.GravShape;
import com.nestor.cs.util.util.param.Argument;

public class ValueDistribution extends AbstractDiagramComponent implements
        MultiResultTask {

    private static class AttributeComboBox extends JComboBox {

        private class Model implements ComboBoxModel {

            int containedSelected = -1;

            public void addListDataListener(ListDataListener l) {
            }

            public Object getElementAt(int index) {
                int i = AttributeComboBox.this.viewed.getIndexOfContained(index);
                return AttributeComboBox.this.viewed.getRelatedAttributeSet().getAttribute(i);
            }

            public Object getSelectedItem() {
                if (this.containedSelected >= 0) {
                    return getElementAt(this.containedSelected);
                } else
                    return null;
            }

            public int getSize() {
                return AttributeComboBox.this.viewed.getContainedCount();
            }

            public void removeListDataListener(ListDataListener l) {
            }

            public void setSelectedItem(Object anItem) {
                if (this.containedSelected >= 0) {
                    int ai = AttributeComboBox.this.viewed.getIndexOfContained(this.containedSelected);
                    AttributeComboBox.this.selected.setAttributeContained(ai,
                            false);
                }
                for (int i = 0; i < AttributeComboBox.this.viewed.getContainedCount(); i++) {
                    int orig = AttributeComboBox.this.viewed.getIndexOfContained(i);
                    Attribute a = AttributeComboBox.this.viewed.getRelatedAttributeSet().getAttribute(
                            orig);
                    if (a == anItem)
                        this.containedSelected = i;
                }
                if (this.containedSelected >= 0) {
                    int ai = AttributeComboBox.this.viewed.getIndexOfContained(this.containedSelected);
                    AttributeComboBox.this.selected.setAttributeContained(ai,
                            true);
                }
            }
        }

        private DefaultAttributeSubset selected;

        private DefaultAttributeSubset viewed;

        public AttributeComboBox(DefaultAttributeSubset viewed,
                DefaultAttributeSubset selected) {
            super();
            this.viewed = viewed;
            this.selected = selected;
            super.setModel(new Model());
        }

        public DefaultAttributeSubset getSelected() {
            return this.selected;
        }
    }

    private static class DistributionPanel extends JPanel implements
            ItemListener {

        private Attribute attr = null;

        private Distribution distr = null;

        private HashMap distrs = new HashMap();

        private ReadOnlyDataset ds;

        public DistributionPanel(ReadOnlyDataset ds) {
            super();
            setBackground(Color.WHITE);
            this.ds = ds;
        }

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                this.distr = null;
                this.attr = null;
                repaint();
                return;
            }
            if (e.getItem() instanceof Attribute) {
                Attribute a = (Attribute) e.getItem();
                if (this.distrs.containsKey(a)) {
                    this.distr = (Distribution) this.distrs.get(a);
                    this.attr = a;
                } else {
                    int ai = -1;
                    for (int i = 0; i < this.ds.getAttributeCount(); i++) {
                        if (a == this.ds.getAttribute(i))
                            ai = i;
                    }
                    if (ai >= 0) {
                        this.distr = Distribution.createDistribution(this.ds, ai);
                        this.distrs.put(a, this.distr);
                        this.attr = a;
                    } else {
                        this.distr = null;
                        this.attr = null;
                    }
                }
                repaint();
                return;
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            if (this.distr == null)
                return;

            int maxCnt = 0;
            int labelCnt = this.distr.getLabels().size();
            for (Iterator iter = this.distr.getLabels().iterator(); iter.hasNext();) {
                Object label = iter.next();
                if (this.distr.getCount(label) > maxCnt)
                    maxCnt = this.distr.getCount(label);
            }

            int fontH = g.getFontMetrics().getHeight();
            Dimension dim = getSize();
            Insets ins = getInsets();
            dim.width -= (ins.left + ins.right);
            dim.height -= (ins.bottom + ins.top + fontH);
            int W = dim.width / labelCnt;
            double H = (double) (dim.height) / (double) maxCnt;
            int maxH = (int) (H * maxCnt);

            ArrayList shapes = new ArrayList();

            int i = 0;
            for (Iterator iter = this.distr.getLabels().iterator(); iter.hasNext();) {
                Object label = iter.next();
                double n = this.distr.getCount(label);
                int x1 = i * W + 5 + ins.left;
                int x2 = (i + 1) * W - 5;
                int y1 = (maxH - (int) (H * n)) + fontH + ins.top;
                int y2 = dim.height + ins.top + fontH;
                if (x2 - x1 <= 0)
                    continue;
                GravRectangle gr = new GravRectangle(x1, y1, x2 - x1, y2 - y1);
                gr.setImmobile(true, true);
                shapes.add(gr);

                String s = this.attr.getName() + "=" + this.attr.getNameForValue(label); //$NON-NLS-1$
                s += " (" + this.distr.getCount(label) + ")"; //$NON-NLS-1$ //$NON-NLS-2$

                int centerx = x1 + (x2 - x1) / 2;
                int centery1 = y1;
                GravString gs = new GravString(s);
                gs.setImmobile(true, false);
                gs.initCoordinates(g.getFontMetrics(), centerx, centery1);
                shapes.add(gs);
                i++;
            }

            Grav grav = new Grav(shapes, new Rectangle(getSize()), 25);
            grav.setMaxIterations(100);
            grav.setTillZero(true);
            grav.startPushAlgorithm();
            i = 0;
            for (Iterator iter = shapes.iterator(); iter.hasNext();) {
                GravShape s = (GravShape) iter.next();
                int R = (i / 9) % 3 * 50 + 150;
                int G = (i / 3) % 3 * 50 + 150;
                int B = i % 3 * 50 + 150;
                Color c = new Color(R, G, B);
                if (s instanceof GravString) {
                    GravString gs = (GravString) s;
                    gs.setLineColor(c);
                    gs.paint(g);
                    i++;
                } else {
                    GravRectangle gr = (GravRectangle) s;
                    int x1 = (int) gr.getX();
                    int y1 = (int) gr.getY();
                    int w = (int) gr.getWidth();
                    int h = (int) gr.getHeight();
                    g.setColor(c);
                    g.fillRect(x1, y1, w, h);
                    g.setColor(Color.BLACK);
                    g.drawRect(x1, y1, w, h);
                }
            }

        }

    }

    public final Argument argDataset = new Argument();

    private MultiResultTask.TaskResult[] faces;

    private DefaultAttributeSubset selectedAttr;

    private DefaultAttributeSubset viewedAttrs;

    public ValueDistribution() {
        super();
        this.argDataset.setName(Messages.getString("DatasetView.argShowDataset")); //$NON-NLS-1$
        this.argDataset.setDescription(Messages
                .getString("DatasetView.argDescShowDataset")); //$NON-NLS-1$
        this.argDataset.setValueType(ReadOnlyDataset.class);
        getConfigurationArguments().addArgument(this.argDataset);

        this.selectedAttr = new DefaultAttributeSubset();
        this.faces = new MultiResultTask.TaskResult[1];
        this.faces[0] = new MultiResultTask.TaskResult(Messages
                .getString("ValueDistribution.selectedAttribute"), this, //$NON-NLS-1$
                this.selectedAttr);
    }

    @Override
    public boolean doModalDialog() {
        return true;
    }

    public ReadOnlyDataset getArgDataset() {
        return (ReadOnlyDataset) this.argDataset.getValue();
    }

    public TaskResult[] getTaskResults() {
        return this.faces;
    }

    public String getName() {
        return getArgDataset().getName();
    }

    public void initProcess() throws ProcessingException {
        if (!getConfigurationArguments().areRequiredArgumentsFilled())
            throw new MissingArgumentValue(getConfigurationArguments());

        ReadOnlyDataset ds = getArgDataset();
        this.viewedAttrs = new DefaultAttributeSubset(ds);
        this.selectedAttr.setRelatedAttributeSet(ds);
        for (int i = 0; i < ds.getAttributeCount(); i++) {
            if (ds.getAttribute(i).getAllowedValuesCount() != Attribute.INFINITE_VALUES) {
                this.viewedAttrs.setAttributeContained(i, true);
            }
        }

        JPanel jp = new JPanel(new BorderLayout());
        AttributeComboBox box = new AttributeComboBox(this.viewedAttrs, this.selectedAttr);
        jp.add(box, BorderLayout.NORTH);

        DistributionPanel panel = new DistributionPanel(ds);
        box.addItemListener(panel);

        jp.add(panel, BorderLayout.CENTER);

        if (this.viewedAttrs.getContainedCount() > 0) {
            box.setSelectedIndex(0);
        }
        setPreparedComponent(jp);
    }

}