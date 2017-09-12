//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// CountMatrix.java (sed.data.clasf)
// Utworzono 2005-08-04 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.nestor.cs.Messages;
import com.nestor.cs.data.impl.datasets.RangeItemsets;
import com.nestor.cs.util.util.ArrayUtils;

public class CountMatrix {

    /**
     * @param labels -
     *            warto�ci badanego atrybutu
     * @return Czysty rozk�ad o etykietach warto�ci <code>labels</code>
     */
    public static CountMatrix createCountMatrix(Collection labelsX,
            Collection labelsY) {
        CountMatrix cld = new CountMatrix();
        cld.labelsX = new ArrayList(labelsX);
        cld.labelsY = new ArrayList(labelsY);
        cld.counts = new int[cld.labelsX.size()][cld.labelsY.size()];
        return cld;
    }

    public static CountMatrix createCountMatrix(CountMatrix[] matricesToSum) {
        CountMatrix cld = new CountMatrix();
        cld.labelsX = new ArrayList();
        cld.labelsY = new ArrayList();
        for (int i = 0; i < matricesToSum.length; i++) {
            for (int x = 0; x < matricesToSum[i].labelsX.size(); x++) {
                Object labelX = matricesToSum[i].labelsX.get(x);
                if (!cld.labelsX.contains(labelX))
                    cld.labelsX.add(labelX);
            }
            for (int y = 0; y < matricesToSum[i].labelsY.size(); y++) {
                Object labelY = matricesToSum[i].labelsY.get(y);
                if (!cld.labelsY.contains(labelY))
                    cld.labelsY.add(labelY);
            }
        }
        cld.counts = new int[cld.labelsX.size()][cld.labelsY.size()];
        for (int i = 0; i < matricesToSum.length; i++) {
            for (int x = 0; x < matricesToSum[i].labelsX.size(); x++) {
                Object labelX = matricesToSum[i].labelsX.get(x);
                int ix = cld.labelsX.indexOf(labelX);
                for (int y = 0; y < matricesToSum[i].labelsY.size(); y++) {
                    Object labelY = matricesToSum[i].labelsY.get(x);
                    int iy = cld.labelsY.indexOf(labelY);
                    cld.counts[ix][iy] += matricesToSum[i].counts[x][y];
                }
            }
        }
        return cld;
    }

    public static CountMatrix createCountMatrix(ReadOnlyDataset ds, int attrX,
            int attrY) {
        return createCountMatrix(ds, attrX, attrY, (new RangeItemsets())
                .selectItemsetIndexes(ds), 0, ds.size() - 1);
    }

    public static CountMatrix createCountMatrix(ReadOnlyDataset ds, int attrX,
            int attrY, int[] indexes, int startIndex, int endIndex) {
        CountMatrix cld = new CountMatrix();

        cld.labelsX = new ArrayList();
        cld.labelsY = new ArrayList();
        for (int i = startIndex; i <= endIndex; i++) {
            ds.setCursorAt(indexes[i]);
            Object labelX = ds.getValue(attrX);
            if (!cld.labelsX.contains(labelX))
                cld.labelsX.add(labelX);
            Object labelY = ds.getValue(attrY);
            if (!cld.labelsY.contains(labelY))
                cld.labelsY.add(labelY);
        }

        cld.counts = new int[cld.labelsX.size()][cld.labelsY.size()];
        for (int i = startIndex; i <= endIndex; i++) {
            ds.setCursorAt(indexes[i]);
            Object labelX = ds.getValue(attrX);
            Object labelY = ds.getValue(attrY);
            cld.counts[cld.labelsX.indexOf(labelX)][cld.labelsY.indexOf(labelY)]++;
        }
        return cld;
    }

    /**
     * Zawiera liczb� powt�rze� danej warto�ci w rozk�adzie
     */
    protected int[][] counts;

    /**
     * Lista warto�ci danego atrybutu zawarta w rozk�adzie
     */
    protected ArrayList labelsX;

    protected ArrayList labelsY;

    protected CountMatrix() {
    }

    public int getCount(Object labelX, Object labelY) {
        if (this.labelsX.indexOf(labelX) == -1)
            return 0;
        if (this.labelsY.indexOf(labelY) == -1)
            return 0;

        return this.counts[this.labelsX.indexOf(labelX)][this.labelsY.indexOf(labelY)];
    }

    public Distribution getVerticalDistribution(Object labelX) {
        Distribution d = Distribution.createDistribution(this.labelsY);
        for (Iterator i = this.labelsY.iterator(); i.hasNext();) {
            Object labelY = i.next();
            d.setCount(labelY, getCount(labelX, labelY));
        }
        return d;
    }

    public Distribution getHorizontalDistribution(Object labelY) {
        Distribution d = Distribution.createDistribution(this.labelsX);
        for (Iterator i = this.labelsX.iterator(); i.hasNext();) {
            Object labelX = i.next();
            d.setCount(labelX, getCount(labelX, labelY));
        }
        return d;
    }

    public Distribution getHorizontalDistribution() {
        Distribution[] ds = new Distribution[this.labelsY.size()];
        int index = 0;
        for (Iterator iy = this.labelsY.iterator(); iy.hasNext();) {
            Object labelY = iy.next();
            ds[index++] = getHorizontalDistribution(labelY);
        }
        return Distribution.createDistribution(ds);
    }

    public Distribution getVerticalDistribution() {
        Distribution[] ds = new Distribution[this.labelsX.size()];
        int index = 0;
        for (Iterator ix = this.labelsX.iterator(); ix.hasNext();) {
            Object labelX = ix.next();
            ds[index++] = getVerticalDistribution(labelX);
        }
        return Distribution.createDistribution(ds);
    }

    public Collection getLabelsX() {
        return Collections.unmodifiableCollection(this.labelsX);
    }

    public Collection getLabelsY() {
        return Collections.unmodifiableCollection(this.labelsY);
    }

    public int getTotalExamples() {
        int res = 0;
        for (int i = 0; i < this.counts.length; i++) {
            res += ArrayUtils.sum(this.counts[i]);
        }
        return res;
    }

    public int getTotalLabelsX() {
        return this.labelsX.size();
    }

    public int getTotalLabelsY() {
        return this.labelsY.size();
    }

    public void modifyCount(Object labelX, Object labelY, int delta) {
        if (!this.labelsX.contains(labelX) || !this.labelsY.contains(labelY)) {
            throw new Error(Messages.getString("CountMatrix.noValuesInDistr")); //$NON-NLS-1$
        }
        this.counts[this.labelsX.indexOf(labelX)][this.labelsY.indexOf(labelY)] += delta;
    }

    public void setCount(Object labelX, Object labelY, int count) {
        if (!this.labelsX.contains(labelX) || !this.labelsY.contains(labelY)) {
            throw new Error(Messages.getString("CountMatrix.noValuesInDistr")); //$NON-NLS-1$
        }
        this.counts[this.labelsX.indexOf(labelX)][this.labelsY.indexOf(labelY)] = count;
    }
}