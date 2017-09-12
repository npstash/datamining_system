//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractTopBottomDiscretizer.java (sed.data.processing)
// Utworzono 2005-05-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess.discr;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Criteria;
import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.ItemsetIndexesSelector;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.attributes.AbstractAttribute;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.attributes.DiscreteAttribute;
import com.nestor.cs.data.impl.criteria.InformationGain;
import com.nestor.cs.data.impl.datasets.SortedItemsets;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class SplittingDiscretizer extends AbstractDisjointDiscretizer {

    public final Argument argMaxDepth = new Argument();

    public final Argument argMinSize = new Argument();

    public final Argument argSplitCrit = new Argument();

    public SplittingDiscretizer() {
        super();

        this.argMinSize.setValueType(Double.class);
        this.argMinSize.setName(Messages
                .getString("SplittingDiscretizer.argMinIntervalSize")); //$NON-NLS-1$
        this.argMinSize.setDescription(Messages
                .getString("SplittingDiscretizer.argDescMinIntervalSize")); //$NON-NLS-1$
        this.argMinSize.setLowBound(new Double(0));
        this.argMinSize.setHighBound(new Double(100));
        getConfigurationArguments().addArgument(this.argMinSize);

        this.argMaxDepth.setValueType(Integer.class);
        this.argMaxDepth.setName(Messages
                .getString("SplittingDiscretizer.argMaxSplitDepth")); //$NON-NLS-1$
        this.argMaxDepth.setDescription(Messages
                .getString("SplittingDiscretizer.argDescMaxSplitDepth")); //$NON-NLS-1$
        this.argMaxDepth.setLowBound(new Integer(0));
        getConfigurationArguments().addArgument(this.argMaxDepth);

        this.argSplitCrit.setValueType(Criteria.class);
        this.argSplitCrit.setName(Messages
                .getString("SplittingDiscretizer.argSplitCritteria")); //$NON-NLS-1$
        this.argSplitCrit.setDescription(Messages
                .getString("SplittingDiscretizer.argDescSplitCritteria")); //$NON-NLS-1$
        getConfigurationArguments().addArgument(this.argSplitCrit);

        try {
            this.argMinSize.setValue(new Double(5));
            this.argMaxDepth.setValue(new Integer(0));
            this.argSplitCrit.setValue(new InformationGain());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Attribute discretizeAttribute(ReadOnlyDataset trainDs, int attrIndex)
            throws ProcessingException {
        if (!(trainDs.getAttribute(attrIndex).isSortable())) {
            String key = "SplittingDiscretizer.attributeMustBeSortable"; //$NON-NLS-1$
            throw new ProcessingException(Messages.getString(key));
        }

        ItemsetIndexesSelector iis = new SortedItemsets(attrIndex, true);
        int[] sortedIndexes = iis.selectItemsetIndexes(trainDs);

        try {
            produceSplitPoints(trainDs, attrIndex, sortedIndexes, 0,
                    sortedIndexes.length - 1, 1);
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
        if (getSplitPoints(attrIndex) == null) {
            // nie utworzono �adnego podzia�u,
            return null;
        }
        return new DiscreteAttribute(createDefaultLabels(attrIndex));
    }

    public int getArgMaximumDepth() {
        return ((Integer) this.argMaxDepth.getValue()).intValue();
    }

    public double getArgMinimumSize() {
        return ((Double) this.argMinSize.getValue()).doubleValue();
    }

    public Criteria getArgSplitCriteria() {
        return (Criteria) this.argSplitCrit.getValue();
    }

    private int getSplitPointIndex(ReadOnlyDataset ds, int attrIndex,
            int[] sortedIndexes, int startIndex, int endIndex) throws Exception {
        double maximumCriteria = -Double.MAX_VALUE;
        int bestSplitPoint = -1;

        if ((startIndex == 6) && (endIndex == 7)) {
            int x = 1;
        }

        Criteria splitC = (Criteria) this.argSplitCrit.getValue();

        int candidateSplitPoint = startIndex;

        int classIndex = 0;
        if (splitC.requiresClassDistribution())
            classIndex = ds.getClassLabelIndex();

        Distribution distribRight = null, distribLeft = null;
        if (splitC.requiresClassDistribution()) {
            distribRight = Distribution.createDistribution(ds, ds
                    .getClassLabelIndex(), sortedIndexes, startIndex, endIndex);
            distribLeft = Distribution.createDistribution(distribRight
                    .getLabels());
        }

        boolean pset = false;
        Object prevValue = null;
        Object label = null;
        for (;;) {
            while (candidateSplitPoint <= endIndex) {
                boolean next = false;
                ds.setCursorAt(sortedIndexes[candidateSplitPoint]);
                Object value = ds.getValue(attrIndex);
                if (splitC.requiresClassDistribution()) {
                    label = ds.getValue(classIndex);
                }
                if (!pset) {
                    prevValue = value;
                    pset = true;
                    next = true;
                } else {
                    if (Attributes.areLabelsEqual(value, prevValue))
                        next = true;
                }
                if (next) {
                    candidateSplitPoint++;
                    if (splitC.requiresClassDistribution()) {
                        distribRight.modifyCount(label, -1);
                        distribLeft.modifyCount(label, 1);
                    }
                } else {
                    prevValue = value;
                    break;
                }
            }

            if (candidateSplitPoint > endIndex)
                break;

            double nLeft = candidateSplitPoint - startIndex;
            double nRight = endIndex - candidateSplitPoint + 1;
            //            int minS = (int) (ds.size()
            //                    * ((Double) argMinSize.getValue()).doubleValue() / 100);
            int minS = (int) Math.ceil(sortedIndexes.length
                    * ((Double) this.argMinSize.getValue()).doubleValue() / 100);

            if ((nLeft < minS) || (nRight < minS))
                continue;

            Double criteria = null;
            criteria = splitC.getSplitCriteriaValue(ds, sortedIndexes,
                    startIndex, endIndex, new int[] { candidateSplitPoint },
                    new Distribution[] { distribLeft, distribRight });
            if (criteria != null)
                if (criteria.doubleValue() > maximumCriteria) {
                    maximumCriteria = criteria.doubleValue();
                    bestSplitPoint = candidateSplitPoint;
                }

            // zeby punktu 'split point' nie wczytywac drugi raz
            candidateSplitPoint++;
            if (splitC.requiresClassDistribution()) {
                distribRight.modifyCount(label, -1);
                distribLeft.modifyCount(label, 1);
            }
        }
        if (bestSplitPoint == -1)
            return -1;

        return bestSplitPoint;
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        if (getArgSplitCriteria().requiresClassDistribution()) {
            if (getArgTrainingDataset().getClassLabelIndex() < 0)
                throw new ProcessingException(
                        Messages
                                .getString("SplittingDiscretizer.supervisedDiscretizationRequiresClassLabel")); //$NON-NLS-1$

            if (getArgTrainingDataset().getAttribute(
                    getArgTrainingDataset().getClassLabelIndex())
                    .getAllowedValuesCount() == Attribute.INFINITE_VALUES) {
                String key = "SplittingDiscretizer.classAttributeMustHaveFiniteLabels"; //$NON-NLS-1$
                throw new ProcessingException(Messages.getString(key));
            }
        }
    }

    public boolean requiresLearning() {
        return true;
    }

    private void produceSplitPoints(ReadOnlyDataset ds, int attrIndex,
            int[] sortedIndexes, int startIndex, int endIndex, int depth)
            throws Exception {
        if (endIndex - startIndex <= 0)
            return;

        int maxD = ((Integer) this.argMaxDepth.getValue()).intValue();
        //        int minS = (int) (ds.size()
        //                * ((Double) argMinSize.getValue()).doubleValue() / 100);
        //        int minS = (int) Math.ceil((double) sortedIndexes.length
        //                * ((Double) argMinSize.getValue()).doubleValue() / 100);

        // sprawdzenie glebokosci
        if ((maxD > 0) && (depth > maxD))
            return;

        int splitIndex = getSplitPointIndex(ds, attrIndex, sortedIndexes,
                startIndex, endIndex);

        // brak podzia�u
        if ((splitIndex < startIndex) || (splitIndex > endIndex))
            return;

        // zdognie z regula ze lewy strona przedzialu jest zamknieta
        // splitIndex == startIndex implikuje ze prawy przedzial jest
        // pusty - brak podzialu
        if (splitIndex == startIndex)
            return;

        ds.setCursorAt(sortedIndexes[splitIndex - 1]);
        Comparable vLeft = (Comparable) ds.getValue(attrIndex);
        ds.setCursorAt(sortedIndexes[splitIndex]);
        Comparable vRight = (Comparable) ds.getValue(attrIndex);
        super.addSplitPoint(vLeft, vRight, attrIndex);

        produceSplitPoints(ds, attrIndex, sortedIndexes, startIndex,
                splitIndex - 1, depth + 1);
        produceSplitPoints(ds, attrIndex, sortedIndexes, splitIndex, endIndex,
                depth + 1);
    }

}