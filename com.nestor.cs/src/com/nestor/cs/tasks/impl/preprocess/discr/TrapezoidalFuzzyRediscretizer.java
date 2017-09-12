//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// FuzzyDiscretizer.java (sed.data.conversion.discretizers)
// Utworzono 2005-06-10 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess.discr;

import java.util.Collection;
import java.util.Iterator;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.ItemsetIterator;
import com.nestor.cs.data.def.ItemsetSelector;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.def.SplitPoint;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;
import com.nestor.cs.data.impl.attributes.FuzzyAttribute;
import com.nestor.cs.data.impl.datasets.ArrayItemset;
import com.nestor.cs.data.impl.datasets.ReadOnlyDatasetWrapper;
import com.nestor.cs.tasks.impl.ConfigurableTaskHelper;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class TrapezoidalFuzzyRediscretizer extends ConfigurableTaskHelper
        implements ReadOnlyDataset {

    public static double getLeftMembershipLevel(SplitPoint split,
            Comparable value) {
        if (split.getSplitPoint() instanceof Double) {
            double l = ((Double) split.getLeftSplitBound()).doubleValue();
            double r = ((Double) split.getRightSplitBound()).doubleValue();
            double v = ((Double) value).doubleValue();
            if (v <= l)
                return 1;
            if (v >= r)
                return 0;
            return 1 - (v - l) / (r - l);
        } else
            return (value.compareTo(split.getSplitPoint()) < 0) ? 1 : 0;
    }

    public static double getRightMembershipLevel(SplitPoint split,
            Comparable value) {
        return 1 - getLeftMembershipLevel(split, value);
    }

    private DefaultAttributeSet convertedAttributeset;

    private ReadOnlyDataset delegatedDs;

    public final Argument argDisjointDiscretizer = new Argument();

    private ReadOnlyDataset wrappingDs;

    public TrapezoidalFuzzyRediscretizer() {
        super();
        String key = "TrapezoidalFuzzyRediscretizer.argDisjointDiscretization"; //$NON-NLS-1$
        this.argDisjointDiscretizer.setName(Messages.getString(key));
        this.argDisjointDiscretizer
                .setDescription(Messages
                        .getString("TrapezoidalFuzzyRediscretizer.argDescDisjointDiscretization")); //$NON-NLS-1$
        this.argDisjointDiscretizer.setValueType(AbstractDisjointDiscretizer.class);
        getConfigurationArguments().addArgument(this.argDisjointDiscretizer);
    }

    public boolean containsAttribute(int attrIndex) {
        return this.convertedAttributeset.containsAttribute(attrIndex);
    }

    public boolean equalsToAttributeSet(AttributeSet as) {
        return this.convertedAttributeset.equalsToAttributeSet(as);
    }

    public AbstractDisjointDiscretizer getArgDisjointDiscretizer() {
        return (AbstractDisjointDiscretizer) this.argDisjointDiscretizer.getValue();
    }

    public Attribute getAttribute(int attributeIndex) {
        return this.convertedAttributeset.getAttribute(attributeIndex);
    }

    public int getAttributeCount() {
        return this.convertedAttributeset.getAttributeCount();
    }

    public int getClassLabelIndex() {
        return this.convertedAttributeset.getClassLabelIndex();
    }

    public int getCursorPos() {
        return this.delegatedDs.getCursorPos();
    }

    public String getDescription() {
        return this.delegatedDs.getDescription();
    }

    protected Object getFuzzyValue(Object value, int attrIndex,
            Collection splitPoints) {
        if (value == null)
            return null;

        Integer dvalue = (Integer) getArgDisjointDiscretizer()
                .getDiscreteValue(value, attrIndex);

        SplitPoint inboundSplit = null;
        SplitPoint normalSplit = null;
        SplitPoint split = null;
        Comparable toFit = (Comparable) value;
        for (Iterator i = splitPoints.iterator(); i.hasNext();) {
            split = (SplitPoint) i.next();
            if (toFit.compareTo(split.getSplitPoint()) < 0)
                normalSplit = split;
            if ((toFit.compareTo(split.getLeftSplitBound()) > 0)
                    && (toFit.compareTo(split.getRightSplitBound()) < 0)) {
                inboundSplit = split;
                break;
            }
        }
        if (normalSplit == null)
            normalSplit = split;
        if (inboundSplit == null)
            inboundSplit = normalSplit;

        FuzzyAttribute.FuzzyValue fv = new FuzzyAttribute.FuzzyValue();
        Integer lvalue, rvalue;
        if (toFit.compareTo(inboundSplit.getSplitPoint()) < 0) {
            lvalue = dvalue;
            rvalue = new Integer(dvalue.intValue() + 1);
        } else {
            rvalue = dvalue;
            lvalue = new Integer(dvalue.intValue() - 1);
        }
        double lmemb = getLeftMembershipLevel(inboundSplit, toFit);
        double rmemb = getRightMembershipLevel(inboundSplit, toFit);

        if (lmemb != 0)
            fv.addValueMembership(lvalue, lmemb);
        if (rmemb != 0)
            fv.addValueMembership(rvalue, rmemb);
        return fv;
    }

    public ReadOnlyItemset getItemset(int i) {
        int k = getCursorPos();
        setCursorAt(i);
        ReadOnlyItemset it = new ArrayItemset(this, getValues());
        setCursorAt(k);
        return it;
    }

    public String getName() {
        return this.delegatedDs.getName();
    }

    public AttributeSet getRelatedAttributeSet() {
        return this.convertedAttributeset.getRelatedAttributeSet();
    }

    public Object getValue(int attrIndex) {
        Collection splitPoints = getArgDisjointDiscretizer().getSplitPoints(
                attrIndex);
        if (splitPoints == null)
            return getArgDisjointDiscretizer().getValue(attrIndex);
        else
            return getFuzzyValue(getArgDisjointDiscretizer()
                    .getArgConvertedDataset().getValue(attrIndex), attrIndex,
                    splitPoints);
    }

    public Object[] getValues() {
        Object[] values = new Object[getAttributeCount()];
        for (int i = 0; i < values.length; i++)
            values[i] = getValue(i);
        return values;
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        this.convertedAttributeset = new DefaultAttributeSet();
        this.delegatedDs = getArgDisjointDiscretizer();
        this.wrappingDs = new ReadOnlyDatasetWrapper(this);

        for (int a = 0; a < getArgDisjointDiscretizer().getAttributeCount(); a++) {
            Attribute attr = getArgDisjointDiscretizer().getAttribute(a);

            if (getArgDisjointDiscretizer().getSplitPoints(a) != null) {
                Attribute newAttr = new FuzzyAttribute(attr
                        .getAllowedValuesNames());
                if (newAttr.getName() == null)
                    newAttr.setName(attr.getName());
                this.convertedAttributeset.addAttribute(newAttr);
            } else {
                this.convertedAttributeset.addAttribute(attr);
            }
        }

        this.convertedAttributeset.setClassLabelIndex(getArgDisjointDiscretizer()
                .getClassLabelIndex());
    }

    public ItemsetIterator iterator() {
        return this.wrappingDs.iterator();
    }

    public ItemsetIterator iterator(ItemsetSelector is) {
        return this.wrappingDs.iterator(is);
    }

    public void setClassLabelIndex(int attrIndex) {
        this.convertedAttributeset.setClassLabelIndex(attrIndex);
    }

    public void setCursorAt(int i) {
        this.delegatedDs.setCursorAt(i);
    }

    public void setDescription(String d) {
        this.delegatedDs.setDescription(d);
    }

    public void setName(String n) {
        this.delegatedDs.setName(n);
    }

    public int size() {
        return this.delegatedDs.size();
    }

}