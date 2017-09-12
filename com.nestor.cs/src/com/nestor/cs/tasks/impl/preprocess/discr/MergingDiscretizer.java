//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// MergingDiscretizer.java (sed.data.conversion.discretizers)
// Utworzono 2005-05-24 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess.discr;

import java.util.ArrayList;
import java.util.Iterator;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Criteria;
import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.ItemsetIndexesSelector;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.attributes.AbstractAttribute;
import com.nestor.cs.data.impl.attributes.DiscreteAttribute;
import com.nestor.cs.data.impl.criteria.ChiSquare;
import com.nestor.cs.data.impl.datasets.Datasets;
import com.nestor.cs.data.impl.datasets.SortedItemsets;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class MergingDiscretizer extends AbstractDisjointDiscretizer {

    public final Argument argMaxSize = new Argument();

    public final Argument argMergeCrit = new Argument();

    public MergingDiscretizer() {
        super();

        this.argMaxSize.setValueType(Double.class);
        this.argMaxSize.setName(Messages
                .getString("MergingDiscretizer.argMaxIntervalSize")); //$NON-NLS-1$
        this.argMaxSize.setDescription(Messages
                .getString("MergingDiscretizer.argDescMaxIntervalSize")); //$NON-NLS-1$
        this.argMaxSize.setLowBound(new Double(0));
        this.argMaxSize.setHighBound(new Double(100));
        getConfigurationArguments().addArgument(this.argMaxSize);

        this.argMergeCrit.setValueType(Criteria.class);
        this.argMergeCrit.setName(Messages
                .getString("MergingDiscretizer.argSplitCritteria")); //$NON-NLS-1$
        this.argMergeCrit.setDescription(Messages
                .getString("MergingDiscretizer.argDescSplitCriteria")); //$NON-NLS-1$
        getConfigurationArguments().addArgument(this.argMergeCrit);

        try {
            this.argMaxSize.setValue(new Double(5));
            this.argMergeCrit.setValue(new ChiSquare());
        } catch (Exception e) {
        }
    }

    public Attribute discretizeAttribute(ReadOnlyDataset trainDs, int attrIndex)
            throws ProcessingException {
        if (!(trainDs.getAttribute(attrIndex).isSortable())) {
            String key = "MergingDiscretizer.arrtibuteMustBeSortable"; //$NON-NLS-1$
            throw new ProcessingException(Messages.getString(key));
        }

        ArrayList splits = new ArrayList(trainDs.size());
        Criteria splitC = (Criteria) this.argMergeCrit.getValue();

        ItemsetIndexesSelector iis = new SortedItemsets(attrIndex, true);
        int[] sortedIndexes = iis.selectItemsetIndexes(trainDs);

        try {
            int currentIndex = 0;
            int endIndex = sortedIndexes.length - 1;

            for (;;) {
                int eqn = Datasets.numOfEqualValuesInARow(trainDs, attrIndex,
                        sortedIndexes, currentIndex, sortedIndexes.length - 1);
                currentIndex += eqn;
                if (currentIndex > endIndex)
                    break;
                splits.add(new Integer(currentIndex));
            }

//            int maxS = (int) (trainDs.size()
//                    * ((Double) argMaxSize.getValue()).doubleValue() / 100);
          int maxS = (int) Math.ceil(sortedIndexes.length
          * ((Double) this.argMaxSize.getValue()).doubleValue() / 100);
            boolean stopMerging = false;
            do {
                double maxCriterion = -Double.MAX_VALUE;
                int removeSplit = -1;
                int intv1S = 0;
                for (int i = 0; i < splits.size(); i++) {
                    int intv2S = ((Integer) splits.get(i)).intValue();
                    int intv1E = intv2S - 1;
                    int intv2E = endIndex;
                    if (i + 1 < splits.size()) {
                        intv2E = ((Integer) splits.get(i + 1)).intValue() - 1;
                    }

                    if (intv2E - intv1S + 1 <= maxS) {
                        Distribution ldLeft = null, ldRight = null;
                        if (splitC.requiresClassDistribution()) {
                            ldLeft = Distribution.createDistribution(trainDs,
                                    trainDs.getClassLabelIndex(),
                                    sortedIndexes, intv1S, intv1E);
                            ldRight = Distribution.createDistribution(trainDs,
                                    trainDs.getClassLabelIndex(),
                                    sortedIndexes, intv2S, intv2E);
                        }
                        Double criteria = null;
                        criteria = splitC.getMergeCriteriaValue(trainDs,
                                sortedIndexes, intv1S, intv2E,
                                new int[] { intv2S }, new Distribution[] {
                                        ldLeft, ldRight });
                        if (criteria != null)
                            if (criteria.doubleValue() > maxCriterion) {
                                maxCriterion = criteria.doubleValue();
                                removeSplit = i;
                            }
                    }
                    intv1S = intv2S;
                }
                if (removeSplit == -1) {
                    stopMerging = true;
                } else {
                    splits.remove(removeSplit);
                }
            } while (!stopMerging);

            for (Iterator iter = splits.iterator(); iter.hasNext();) {
                Integer split = (Integer) iter.next();
                trainDs.setCursorAt(sortedIndexes[split.intValue() - 1]);
                Comparable cLeft = (Comparable) trainDs.getValue(attrIndex);
                trainDs.setCursorAt(sortedIndexes[split.intValue()]);
                Comparable cRight = (Comparable) trainDs.getValue(attrIndex);
                addSplitPoint(cLeft, cRight, attrIndex);
            }
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
        if (getSplitPoints(attrIndex) == null) {
            return null;
        }
        return new DiscreteAttribute(createDefaultLabels(attrIndex));
    }

    public Criteria getArgMergeCriteria() {
        return (Criteria) this.argMergeCrit.getValue();
    }

    public double getArgMinimumSize() {
        return ((Double) this.argMaxSize.getValue()).doubleValue();
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        if (getArgMergeCriteria().requiresClassDistribution()) {
            if (getArgTrainingDataset().getClassLabelIndex() < 0) {
                String key = "MergingDiscretizer.supervisedDiscretizationRequiresClassLabel"; //$NON-NLS-1$
                throw new ProcessingException(Messages.getString(key));
            }

            if (getArgTrainingDataset().getAttribute(
                    getArgTrainingDataset().getClassLabelIndex())
                    .getAllowedValuesCount() == Attribute.INFINITE_VALUES) {
                String key = "MergingDiscretizer.classAttributeMustHaveFiniteLables"; //$NON-NLS-1$
                throw new ProcessingException(Messages.getString(key));
            }
        }
    }

    public boolean requiresLearning() {
        return true;
    }

}