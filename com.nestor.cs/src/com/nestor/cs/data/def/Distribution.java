//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ClassLabelDistribution.java (sed.data.processing)
// Utworzono 2005-05-23 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.nestor.cs.Messages;
import com.nestor.cs.data.impl.attributes.DiscreteAttribute;
import com.nestor.cs.data.impl.datasets.RangeItemsets;
import com.nestor.cs.util.util.ArrayUtils;

public class Distribution {

    /**
     * @param labels -
     *            warto�ci badanego atrybutu
     * @return Czysty rozk�ad o etykietach warto�ci <code>labels</code>
     */
    public static Distribution createDistribution(Collection labels) {
        Distribution cld = new Distribution();
        cld.labels = new ArrayList(labels);
        cld.counts = new int[cld.labels.size()];
        return cld;
    }

    public static Distribution createDistribution(DiscreteAttribute dAttr) {
        Distribution cld = new Distribution();
        cld.labels = new ArrayList();
        for (int i = 0; i < dAttr.getAllowedValuesCount(); i++)
            cld.labels.add(dAttr.getAllowedValue(i));
        cld.counts = new int[cld.labels.size()];
        return cld;
    }

    public static Distribution createDistribution(
            Distribution[] distributionsToSum) {
        Distribution cld = new Distribution();
        cld.labels = new ArrayList();
        for (int i = 0; i < distributionsToSum.length; i++) {
            for (int k = 0; k < distributionsToSum[i].labels.size(); k++) {
                Object label = distributionsToSum[i].labels.get(k);
                if (!cld.labels.contains(label))
                    cld.labels.add(label);
            }
        }
        cld.counts = new int[cld.labels.size()];
        for (int i = 0; i < distributionsToSum.length; i++) {
            for (int k = 0; k < distributionsToSum[i].labels.size(); k++) {
                Object label = distributionsToSum[i].labels.get(k);
                cld.counts[cld.labels.indexOf(label)] += distributionsToSum[i].counts[k];
            }
        }
        return cld;
    }

    public static Distribution createDistribution(ReadOnlyDataset ds,
            int attrIndex) {
        return createDistribution(ds, attrIndex, (new RangeItemsets())
                .selectItemsetIndexes(ds), 0, ds.size() - 1);
    }

    public static Distribution createDistribution(ReadOnlyDataset ds,
            int attrIndex, int[] indexes, int startIndex, int endIndex) {
        Distribution cld = new Distribution();

        cld.labels = new ArrayList();
        for (int i = startIndex; i <= endIndex; i++) {
            ds.setCursorAt(indexes[i]);
            Object label = ds.getValue(attrIndex);
            if (!cld.labels.contains(label))
                cld.labels.add(label);
        }

        cld.counts = new int[cld.labels.size()];
        for (int i = startIndex; i <= endIndex; i++) {
            ds.setCursorAt(indexes[i]);
            Object label = ds.getValue(attrIndex);
            cld.counts[cld.labels.indexOf(label)]++;
        }
        return cld;
    }

    /**
     * Zawiera liczb� powt�rze� danej warto�ci w rozk�adzie
     */
    protected int[] counts;

    /**
     * Lista warto�ci danego atrybutu zawarta w rozk�adzie
     */
    protected ArrayList labels;

    protected Distribution() {
    }

    public int getCount(Object label) {
        return (this.labels.indexOf(label) == -1) ? 0
                : this.counts[this.labels.indexOf(label)];
    }

    public Object getFirstMajorLabel() {
        int max = 0;
        Object maxObj = null;
        for (Iterator it = getLabels().iterator(); it.hasNext();) {
            Object label = it.next();
            int cnt = getCount(label);
            if (cnt > max) {
                max = cnt;
                maxObj = label;
            }
        }
        return maxObj;
    }

    public Collection getLabels() {
        return Collections.unmodifiableCollection(this.labels);
    }

    /**
     * Zwraca warto�� pojawiaj�c� si� najcz�siej w rozk�adzie. Je�li rozk�ad
     * jest pusty lub jest kilka najcz�ciej pojawiaj�cych si� warto�ci,
     * wynikiem jest null
     */
    public Object getMajorLabel() {
        int max = 0;
        int maxCnt = 0;
        Object maxObj = null;
        for (Iterator it = getLabels().iterator(); it.hasNext();) {
            Object label = it.next();
            int cnt = getCount(label);
            if (cnt > max) {
                max = cnt;
                maxObj = label;
                maxCnt = 1;
            } else if (cnt == max) {
                maxCnt++;
            }
        }
        if (maxCnt > 1)
            maxObj = null;
        return maxObj;
    }

    public int getTotalExamples() {
        return ArrayUtils.sum(this.counts);
    }

    public int getTotalLabels() {
        return this.labels.size();
    }

    public void modifyCount(Object label, int delta) {
        if (!this.labels.contains(label)) {
            throw new Error(
                    Messages.getString("Distribution.missingValuesInDist") + label + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        this.counts[this.labels.indexOf(label)] += delta;
    }

    public void setCount(Object label, int count) {
        if (!this.labels.contains(label)) {
            throw new Error(
                    Messages.getString("Distribution.missingValuesInDist") + label + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        this.counts[this.labels.indexOf(label)] = count;
    }

    @Override
    public String toString() {
        String s = ""; //$NON-NLS-1$
        for (int i = 0; i < this.labels.size(); i++) {
            Object l = this.labels.get(i);
            s += getCount(l) + "[" + l + "]"; //$NON-NLS-1$ //$NON-NLS-2$
            if (i < this.labels.size() - 1)
                s += " "; //$NON-NLS-1$
        }
        return s;
    }

}