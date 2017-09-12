//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DefaultAttributeSubset.java (sed.data.attributes)
// Utworzono 2005-07-30 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.attributes;

import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.AttributeSubset;

public class DefaultAttributeSubset implements AttributeSubset {

    protected boolean[] contains = null;

    protected AttributeSet relatedAttributeSet = null;

    public DefaultAttributeSubset() {
    }

    public DefaultAttributeSubset(AttributeSet as) {
        this.contains = new boolean[as.getAttributeCount()];
        this.relatedAttributeSet = as;
    }

    public DefaultAttributeSubset(AttributeSet as, boolean[] contains) {
        super();
        this.contains = contains;
        this.relatedAttributeSet = as;
    }

    public DefaultAttributeSubset(AttributeSet as, int onlyExclude) {
        this.contains = new boolean[as.getAttributeCount()];
        for (int i = 0; i < this.contains.length; i++)
            if (i != onlyExclude)
                this.contains[i] = true;
        this.relatedAttributeSet = as;
    }

    public DefaultAttributeSubset(AttributeSet as, int[] contains) {
        super();
        this.contains = new boolean[as.getAttributeCount()];
        for (int i = 0; i < contains.length; i++)
            this.contains[contains[i]] = true;
        this.relatedAttributeSet = as;
    }

    public boolean containsAttribute(int attrIndex) {
        if ((attrIndex < 0) || (attrIndex >= this.contains.length))
            return false;
        return this.contains[attrIndex];
    }

    public int getContainedCount() {
        int res = 0;
        for (int i = 0; i < this.contains.length; i++) {
            if (this.contains[i])
                res++;
        }
        return res;
    }

    public int getIndexOfContained(int contained) {
        int res = 0;
        for (int i = 0; i < this.contains.length; i++) {
            if (this.contains[i]) {
                if (contained == res)
                    return i;
                res++;
            }
        }
        return -1;
    }

    public AttributeSet getRelatedAttributeSet() {
        return this.relatedAttributeSet;
    }

    public void setAttributeContained(int attrIndex, boolean contained) {
        if ((attrIndex < 0) || (attrIndex >= this.contains.length))
            return;
        this.contains[attrIndex] = contained;
    }

    public void setRelatedAttributeSet(AttributeSet relatedAttributeSet) {
        this.relatedAttributeSet = relatedAttributeSet;
        this.contains = new boolean[relatedAttributeSet.getAttributeCount()];
    }

}