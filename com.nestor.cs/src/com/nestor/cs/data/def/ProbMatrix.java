//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ProbMatrix.java (sed.data.clasf)
// Utworzono 2005-08-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.nestor.cs.Messages;
import com.nestor.cs.util.util.ArrayUtils;

public class ProbMatrix {

    /**
     * @param labels -
     *            warto�ci badanego atrybutu
     * @return Czysty rozk�ad o etykietach warto�ci <code>labels</code>
     */
    public static ProbMatrix createProbMatrix(Collection labelsX,
            Collection labelsY) {
        ProbMatrix cld = new ProbMatrix();
        cld.labelsX = new ArrayList(labelsX);
        cld.labelsY = new ArrayList(labelsY);
        cld.probabilities = new double[cld.labelsX.size()][cld.labelsY.size()];
        return cld;
    }

    public static ProbMatrix createProbMatrix(CountMatrix cm) {
        ProbMatrix cld = new ProbMatrix();
        cld.labelsX = new ArrayList(cm.getLabelsX());
        cld.labelsY = new ArrayList(cm.getLabelsY());
        cld.probabilities = new double[cld.labelsX.size()][cld.labelsY.size()];
        for (Iterator iterX = cld.labelsX.iterator(); iterX.hasNext();) {
            Object labelX = iterX.next();

            for (Iterator iterY = cld.labelsY.iterator(); iterY.hasNext();) {
                Object labelY = iterY.next();
                cld.probabilities[cld.labelsX.indexOf(labelX)][cld.labelsY
                        .indexOf(labelY)] = cm.getCount(labelX, labelY);
            }

        }
        return cld;
    }

    public static ProbMatrix createProbMatrix(ProbMatrix[] matricesToSum) {
        ProbMatrix cld = new ProbMatrix();
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
        cld.probabilities = new double[cld.labelsX.size()][cld.labelsY.size()];
        for (int i = 0; i < matricesToSum.length; i++) {
            for (int x = 0; x < matricesToSum[i].labelsX.size(); x++) {
                Object labelX = matricesToSum[i].labelsX.get(x);
                int ix = cld.labelsX.indexOf(labelX);
                for (int y = 0; y < matricesToSum[i].labelsY.size(); y++) {
                    Object labelY = matricesToSum[i].labelsY.get(x);
                    int iy = cld.labelsY.indexOf(labelY);
                    cld.probabilities[ix][iy] += matricesToSum[i].probabilities[x][y];
                }
            }
        }
        return cld;
    }

    /**
     * Lista warto�ci danego atrybutu zawarta w rozk�adzie
     */
    protected ArrayList labelsX;

    protected ArrayList labelsY;

    /**
     * Zawiera prawdopodobie�stwa dla kombinacji etykiet
     */
    protected double[][] probabilities;

    protected ProbMatrix() {
    }

    public ProbDistribution getHorizontalProbDistribution() {
        ProbDistribution[] ds = new ProbDistribution[this.labelsY.size()];
        int index = 0;
        for (Iterator iy = this.labelsY.iterator(); iy.hasNext();) {
            Object labelY = iy.next();
            ds[index++] = getHorizontalProbDistribution(labelY);
        }
        return ProbDistribution.createProbDistribution(ds);
    }

    public ProbDistribution getHorizontalProbDistribution(Object labelY) {
        ProbDistribution d = ProbDistribution.createProbDistribution(this.labelsX);
        for (Iterator i = this.labelsX.iterator(); i.hasNext();) {
            Object labelX = i.next();
            d.setProbability(labelX, getProbability(labelX, labelY));
        }
        return d;
    }

    public Collection getLabelsX() {
        return Collections.unmodifiableCollection(this.labelsX);
    }

    public Collection getLabelsY() {
        return Collections.unmodifiableCollection(this.labelsY);
    }

    public double getProbability(Object labelX, Object labelY) {
        if (this.labelsX.indexOf(labelX) == -1)
            return 0;
        if (this.labelsY.indexOf(labelY) == -1)
            return 0;

        return this.probabilities[this.labelsX.indexOf(labelX)][this.labelsY.indexOf(labelY)];
    }

    public int getTotalLabelsX() {
        return this.labelsX.size();
    }

    public int getTotalLabelsY() {
        return this.labelsY.size();
    }

    public double getProbabilitiesSum() {
        double res = 0;
        for (int i = 0; i < this.probabilities.length; i++) {
            res += ArrayUtils.sum(this.probabilities[i]);
        }
        return res;
    }

    public ProbDistribution getVerticalProbDistribution() {
        ProbDistribution[] ds = new ProbDistribution[this.labelsX.size()];
        int index = 0;
        for (Iterator ix = this.labelsX.iterator(); ix.hasNext();) {
            Object labelX = ix.next();
            ds[index++] = getVerticalProbDistribution(labelX);
        }
        return ProbDistribution.createProbDistribution(ds);
    }

    public ProbDistribution getVerticalProbDistribution(Object labelX) {
        ProbDistribution d = ProbDistribution.createProbDistribution(this.labelsY);
        for (Iterator i = this.labelsY.iterator(); i.hasNext();) {
            Object labelY = i.next();
            d.setProbability(labelY, getProbability(labelX, labelY));
        }
        return d;
    }

    public void modifyProbability(Object labelX, Object labelY, double delta) {
        if (!this.labelsX.contains(labelX) || !this.labelsY.contains(labelY)) {
            throw new Error(Messages.getString("ProbMatrix.noValuesInDistr")); //$NON-NLS-1$
        }
        this.probabilities[this.labelsX.indexOf(labelX)][this.labelsY.indexOf(labelY)] += delta;
    }

    public void setProbability(Object labelX, Object labelY, double prob) {
        if (!this.labelsX.contains(labelX) || !this.labelsY.contains(labelY)) {
            throw new Error(Messages.getString("ProbMatrix.noValuesInDistr")); //$NON-NLS-1$
        }
        this.probabilities[this.labelsX.indexOf(labelX)][this.labelsY.indexOf(labelY)] = prob;
    }
}