//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ProbDistribution.java (sed.data.clasf)
// Utworzono 2005-08-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.nestor.cs.Messages;
import com.nestor.cs.util.util.ArrayUtils;

public class ProbDistribution {

    /**
     * @param labels -
     *            warto�ci badanego atrybutu
     * @return Czysty rozk�ad o etykietach warto�ci <code>labels</code>
     */
    public static ProbDistribution createProbDistribution(Collection labels) {
        ProbDistribution cld = new ProbDistribution();
        cld.labels = new ArrayList(labels);
        cld.probabilities = new double[cld.labels.size()];
        return cld;
    }

    public static ProbDistribution createProbDistribution(
            ProbDistribution[] distributionsToSum) {
        ProbDistribution cld = new ProbDistribution();
        cld.labels = new ArrayList();
        for (int i = 0; i < distributionsToSum.length; i++) {
            for (int k = 0; k < distributionsToSum[i].labels.size(); k++) {
                Object label = distributionsToSum[i].labels.get(k);
                if (!cld.labels.contains(label))
                    cld.labels.add(label);
            }
        }
        cld.probabilities = new double[cld.labels.size()];
        for (int i = 0; i < distributionsToSum.length; i++) {
            for (int k = 0; k < distributionsToSum[i].labels.size(); k++) {
                Object label = distributionsToSum[i].labels.get(k);
                cld.probabilities[cld.labels.indexOf(label)] += distributionsToSum[i].probabilities[k];
            }
        }
        return cld;
    }

    /**
     * Lista warto�ci danego atrybutu zawarta w rozk�adzie
     */
    protected ArrayList labels;

    /**
     * Zawiera wielko�� prawdopodobie�stwa dla danej etykiety
     */
    protected double[] probabilities;

    protected ProbDistribution() {
    }

    public void addProbabilities(ProbDistribution[] ps) {
        for (int i = 0; i < ps.length; i++) {
            if (ps[i] == null)
                continue;
            for (int j = 0; j < ps[i].labels.size(); j++) {
                Object clLabel = ps[i].labels.get(j);
                int myIndex = this.labels.indexOf(clLabel);
                if (myIndex < 0)
                    continue;
                this.probabilities[myIndex] += ps[i].probabilities[j];
            }
        }
    }

    public void clearProbabilities() {
        for (int i = 0; i < this.probabilities.length; i++) {
            this.probabilities[i] = 0;
        }
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
        double max = 0;
        int maxCnt = 0;
        Object maxObj = null;
        for (Iterator it = getLabels().iterator(); it.hasNext();) {
            Object label = it.next();
            double cnt = getProbability(label);
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

    public double getProbabilitiesSum() {
        return ArrayUtils.sum(this.probabilities);
    }

    public double getProbability(Object label) {
        return (this.labels.indexOf(label) == -1) ? 0 : this.probabilities[this.labels
                .indexOf(label)];
    }

    public int getTotalLabels() {
        return this.labels.size();
    }

    public void initProbabilities(Distribution dClass) {
        double n = dClass.getTotalExamples();
        if (n == 0)
            return;
        for (Iterator iter = dClass.getLabels().iterator(); iter.hasNext();) {
            Object clLabel = iter.next();
            int myIndex = this.labels.indexOf(clLabel);
            if (myIndex < 0)
                continue;
            double ni = dClass.getCount(clLabel);
            this.probabilities[myIndex] = ni / n;
        }
    }

    public void modifyProbability(Object label, double delta) {
        if (!this.labels.contains(label)) {
            throw new Error(
                    Messages.getString("ProbDistribution.missingValuesInDistr") + label + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        this.probabilities[this.labels.indexOf(label)] += delta;
    }

    public void multiplyProbabilities(double multiplier) {
        for (int i = 0; i < this.probabilities.length; i++) {
            this.probabilities[i] *= multiplier;
        }

    }

    public void setProbability(Object label, double prob) {
        if (!this.labels.contains(label)) {
            throw new Error(
                    Messages.getString("ProbDistribution.missingValuesInDistr") + label + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        this.probabilities[this.labels.indexOf(label)] = prob;
    }

    @Override
    public String toString() {
        String s = ""; //$NON-NLS-1$
        for (int i = 0; i < this.labels.size(); i++) {
            Object l = this.labels.get(i);
            s += getProbability(l) + "[" + l + "]"; //$NON-NLS-1$ //$NON-NLS-2$
            if (i < this.labels.size() - 1)
                s += " "; //$NON-NLS-1$
        }
        return s;
    }

}