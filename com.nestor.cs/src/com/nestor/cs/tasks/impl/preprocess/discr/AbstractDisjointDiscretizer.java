//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractBinningDiscretizer.java (sed.data.processing)
// Utworzono 2005-05-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess.discr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.SplitPoint;
import com.nestor.cs.util.proc.ProcessingException;

public abstract class AbstractDisjointDiscretizer extends AbstractDiscretizer {

    private HashMap splitPointLists;

    public AbstractDisjointDiscretizer() {
        super();
    }

    protected void addSplitPoint(Comparable onLeft, Comparable onRight,
            int attrIndex) {
        ArrayList splitPoints = (ArrayList) this.splitPointLists.get(new Integer(
                attrIndex));
        if (splitPoints == null) {
            splitPoints = new ArrayList();
            this.splitPointLists.put(new Integer(attrIndex), splitPoints);
        }
        SplitPoint sp = new SplitPoint(onLeft, onRight);
        splitPoints.add(sp);
        Collections.sort(splitPoints);
        System.out
                .println(Messages.getString("AbstractDisjointDiscretizer.0") + sp); //$NON-NLS-1$
    }

    protected String[] createDefaultLabels(int attrIndex) {
        ArrayList splitPoints = (ArrayList) this.splitPointLists.get(new Integer(
                attrIndex));
        if (splitPoints == null)
            return null;

        String[] labels = new String[splitPoints.size() + 1];
        for (int i = 0; i < labels.length; i++) {
            if (i > 0)
                labels[i] = "<" + splitPoints.get(i - 1).toString() + ":"; //$NON-NLS-1$ //$NON-NLS-2$
            else
                labels[i] = "(-Inf:"; //$NON-NLS-1$
            if (i < labels.length - 1)
                labels[i] += splitPoints.get(i).toString() + ")"; //$NON-NLS-1$
            else
                labels[i] += "+Inf)"; //$NON-NLS-1$
        }
        return labels;
    }

    public Object getDiscreteValue(Object value, int attrIndex) {
        if (value == null)
            return null;
        ArrayList splitPoints = (ArrayList) this.splitPointLists.get(new Integer(
                attrIndex));
        if (splitPoints == null)
            return null;

        Comparable toFit = (Comparable) value;
        for (int i = 0; i < splitPoints.size(); i++) {
            Comparable bound = ((SplitPoint) splitPoints.get(i))
                    .getSplitPoint();
            if (toFit.compareTo(bound) < 0) {
                return new Integer(i);
            }
        }
        return new Integer(splitPoints.size());
    }

    public Collection getSplitPoints(int attrIndex) {
        return (ArrayList) this.splitPointLists.get(new Integer(attrIndex));
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();
        this.splitPointLists = new HashMap();
    }

}