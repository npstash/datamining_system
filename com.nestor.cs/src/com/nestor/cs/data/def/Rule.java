//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Rule.java (sed.data.clasf)
// Utworzono 2005-07-30 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public class Rule extends TestSet {

    protected int classValueIndex = -1;

    // 0 - 1
    protected double confidence = 0;

    public Rule(AttributeSet attributeSet, int classValueIndex) {
        super(attributeSet);
        this.classValueIndex = classValueIndex;
    }

    public int getClassValueIndex() {
        return this.classValueIndex;
    }

    public double getConfidence() {
        return this.confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public void setSupport(ReadOnlyDataset ds) {
        if (ds.getClassLabelIndex() != this.attributeSet.getClassLabelIndex())
            return;
        if (ds.size() == 0) {
            this.support = 0;
            return;
        }
        int clAttrIndex = this.attributeSet.getClassLabelIndex();
        Attribute clAttr = this.attributeSet.getAttribute(clAttrIndex);
        int condCount = 0;
        int positiveCount = 0;
        for (int i = 0; i < ds.size(); i++) {
            ds.setCursorAt(i);
            if (classifies(ds)) {
                condCount++;
                int clValueIndex = clAttr.getAllowedValueIndex(ds
                        .getValue(clAttrIndex));
                if (this.classValueIndex == clValueIndex)
                    positiveCount++;
            }
        }
        this.support = (double) positiveCount / (double) ds.size();
    }

    public void setSupportAndConfidence(ReadOnlyDataset ds) {
        if (ds.getClassLabelIndex() != this.attributeSet.getClassLabelIndex())
            return;
        if (ds.size() == 0) {
            this.support = 0;
            this.confidence = 0;
            return;
        }
        int clAttrIndex = this.attributeSet.getClassLabelIndex();
        Attribute clAttr = this.attributeSet.getAttribute(clAttrIndex);
        int condCount = 0;
        int positiveCount = 0;
        for (int i = 0; i < ds.size(); i++) {
            ds.setCursorAt(i);
            if (classifies(ds)) {
                condCount++;
                int clValueIndex = clAttr.getAllowedValueIndex(ds
                        .getValue(clAttrIndex));
                if (this.classValueIndex == clValueIndex)
                    positiveCount++;
            }
        }
        this.support = (double) positiveCount / (double) ds.size();
        if (condCount == 0) {
            this.confidence = 0;
        } else {
            this.confidence = (double) positiveCount / (double) condCount;
        }
    }

    @Override
    public String toString() {
        String res = super.toString();
        Attribute clAttr = this.attributeSet.getAttribute(this.attributeSet
                .getClassLabelIndex());
        res += " => (" + clAttr.getName() + " = "; //$NON-NLS-1$ //$NON-NLS-2$
        res += clAttr.getNameForValue(clAttr.getAllowedValue(this.classValueIndex))
                + ")"; //$NON-NLS-1$
        return res;
    }
}