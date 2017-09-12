//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DefaultAttributeset.java (sed.data.attributes)
// Utworzono 2005-05-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.attributes;

import java.util.ArrayList;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.AttributeSubset;

public class DefaultAttributeSet implements AttributeSet {

    private ArrayList attributes = new ArrayList();

    private int currentClassLabel = -1;

    public DefaultAttributeSet() {
    }

    public DefaultAttributeSet(Attribute[] as) {
        for (int i = 0; i < as.length; i++) {
            Attribute attr = as[i];
            this.attributes.add(attr);
        }
    }

    public DefaultAttributeSet(AttributeSet as) {
        for (int i = 0; i < as.getAttributeCount(); i++) {
            Attribute attr = as.getAttribute(i);
            this.addAttribute(as.getAttribute(i));
        }
        setClassLabelIndex(as.getClassLabelIndex());
    }

    public DefaultAttributeSet(AttributeSubset as) {
        int clIndex = -1;
        for (int i = 0; i < as.getRelatedAttributeSet().getAttributeCount(); i++) {
            if (as.containsAttribute(i)) {
                this.attributes.add(as.getRelatedAttributeSet().getAttribute(i));
                if (as.getRelatedAttributeSet().getClassLabelIndex() == i) {
                    clIndex = this.attributes.size() - 1;
                }
            }
        }
        if (clIndex != -1) {
            setClassLabelIndex(clIndex);
        }
    }

    public void addAll(Attribute[] attibutes) {
        for (int i = 0; i < attibutes.length; i++) {
            addAttribute(attibutes[i]);
        }
    }

    public void addAll(AttributeSet as, boolean copyClass) {
        for (int i = 0; i < as.getAttributeCount(); i++) {
            addAttribute(as.getAttribute(i));
        }
        if (copyClass) {
            setClassLabelIndex(as.getClassLabelIndex());
        }
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    public boolean containsAttribute(int attrIndex) {
        return (attrIndex >= 0) && (attrIndex < getAttributeCount());
    }

    public boolean equalsToAttributeSet(AttributeSet as) {
        if (as.getAttributeCount() != this.getAttributeCount())
            return false;
        for (int i = 0; i < this.getAttributeCount(); i++) {
            if (!as.getAttribute(i).equals(this.getAttribute(i)))
                return false;
        }
        return true;
    }

    public Attribute getAttribute(int attributeIndex) {
        if ((attributeIndex < 0) || (attributeIndex >= this.attributes.size()))
            return null;
        return (Attribute) this.attributes.get(attributeIndex);
    }

    public int getAttributeCount() {
        return this.attributes.size();
    }

    public AttributeSet getAttributeSet() {
        return this;
    }

    public int getClassLabelIndex() {
        return this.currentClassLabel;
    }

    public AttributeSet getRelatedAttributeSet() {
        return this;
    }

    public void removeAll() {
        this.attributes.clear();
        this.currentClassLabel = -1;
    }

    public void removeAttribute(Attribute attribute) {
        this.attributes.remove(attribute);
    }

    public void setClassLabelIndex(int attrIndex) {
        if ((attrIndex < 0) || (attrIndex >= getAttributeCount()))
            attrIndex = -1;
        else
            this.currentClassLabel = attrIndex;
    }
}