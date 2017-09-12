//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SPRINT.java (sed.data.clasf.tree)
// Utworzono 2005-08-03 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.CountMatrix;
import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.DistributionCriteria;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.SplitPoint;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.criteria.GiniIndex;
import com.nestor.cs.data.impl.tests.SplitPointTest;
import com.nestor.cs.data.impl.tests.ValueSetTest;
import com.nestor.cs.tasks.def.TreeNode;
import com.nestor.cs.tasks.def.TreeNodeTest;
import com.nestor.cs.util.util.ArrayUtils;
import com.nestor.cs.util.util.param.Argument;

public class SPRINT extends AbstractTree {

    public static int MAX_SUBSETTING = 20;
    
    private class ARComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            if (!(o1 instanceof AttributeRecord))
                return 0;
            if (!(o2 instanceof AttributeRecord))
                return 0;
            AttributeRecord ar1 = (AttributeRecord) o1;
            AttributeRecord ar2 = (AttributeRecord) o2;
            if (!(ar1.attrValue instanceof Comparable))
                return 0;
            if (!(ar2.attrValue instanceof Comparable))
                return 0;
            return ((Comparable) ar1.attrValue).compareTo(ar2.attrValue);
        }
    }

    private class AttributeList {

        public class SplitInfo {

            static final int CATEGORICAL = 2;

            static final int CATEGORICAL_BINARY = 1;

            static final int CONTINUOUS = 3;

            static final int FAILED = 0;

            Attributes.ValueSubset binarySplitValueSubset = null;

            SplitPoint continuousSplitPoint = null;

            Double criteriaValue = null;

            AttributeList list = null;

            int splitType = FAILED;

        }

        public ArrayList attributeRecords = new ArrayList();

        public int attrIndex;

        public Distribution cAbove = null;

        public Distribution cBelow = null;

        public boolean isContinuous = false;

        public CountMatrix matrix = null;

        public AttributeList(AttributeList from, int[] rids) {
            this.attrIndex = from.attrIndex;
            this.isContinuous = from.isContinuous;

            // inicjalizacja histogram�w
            if (!this.isContinuous) {
                this.matrix = CountMatrix.createCountMatrix(
                        from.matrix.getLabelsX(), from.matrix.getLabelsY());
            } else {
                this.cBelow = Distribution.createDistribution(from.cBelow
                        .getLabels());
                this.cAbove = Distribution.createDistribution(from.cAbove
                        .getLabels());
            }

            // kopiowanie rekord�w
            for (int i = 0; i < from.attributeRecords.size(); i++) {
                AttributeRecord ar = (AttributeRecord) from.attributeRecords
                        .get(i);

                if (!ArrayUtils.isContained(ar.rid, rids))
                    continue;

                if (!this.isContinuous) {
                    this.matrix.modifyCount(ar.classValue, ar.attrValue, 1);
                } else {
                    this.cAbove.modifyCount(ar.classValue, 1);
                }
                this.attributeRecords.add(ar);
            }
            // wt�rne sortowanie rekord�w nie jest potrzebne!
        }

        public AttributeList(AttributeSet as, int attrIndex) {
            this.attrIndex = attrIndex;
            Attribute attr = as.getAttribute(attrIndex);
            Attribute clAttr = as.getAttribute(as.getClassLabelIndex());
            int attrValuesCnt = attr.getAllowedValuesCount();

            if (attrValuesCnt == Attribute.INFINITE_VALUES) {
                if (attr.isSortable()) {
                    this.isContinuous = true;
                } else
                    throw new Error(Messages.getString("SPRINT.0")); //$NON-NLS-1$
            }

            // kolekcja etykiet klasy
            int clValuesCnt = clAttr.getAllowedValuesCount();
            ArrayList clValues = new ArrayList();
            for (int i = 0; i < clValuesCnt; i++) {
                clValues.add(clAttr.getAllowedValue(i));
            }
            // inicjalizacja histogram�w
            if (!this.isContinuous) {
                ArrayList attrValues = new ArrayList();
                for (int i = 0; i < attrValuesCnt; i++) {
                    attrValues.add(attr.getAllowedValue(i));
                }
                this.matrix = CountMatrix.createCountMatrix(clValues, attrValues);
            } else {
                this.cBelow = Distribution.createDistribution(clValues);
                this.cAbove = Distribution.createDistribution(clValues);
            }
        }

        public Distribution getClassLabelDistribution() {
            Distribution cd = null;
            if (!this.isContinuous) {
                cd = this.matrix.getHorizontalDistribution();
            } else {
                cd = Distribution
                        .createDistribution(new Distribution[] { this.cAbove });
            }
            return cd;
        }

        public int[] getRidsFulfillingTest(Test t) {
            ArrayList rids = new ArrayList();
            for (int i = 0; i < this.attributeRecords.size(); i++) {
                AttributeRecord ar = (AttributeRecord) this.attributeRecords.get(i);
                if (t.classifies(ar.attrValue))
                    rids.add(new Integer(ar.rid));
            }
            int[] rs = new int[rids.size()];
            for (int i = 0; i < rs.length; i++) {
                rs[i] = ((Integer) rids.get(i)).intValue();
            }
            return rs;
        }

        public SplitInfo getSplit(DistributionCriteria crit, boolean binarySplit) {

            SplitInfo info = new SplitInfo();
            info.list = this;

            if (!this.isContinuous) {
                Collection clLabels = this.matrix.getLabelsX();
                Collection atLabels = this.matrix.getLabelsY();

                if (binarySplit && (atLabels.size() < MAX_SUBSETTING)) {
                    Distribution[] distrs = new Distribution[2];

                    // wszystkie mozliwe zestawy wartosci atrybutu
                    Object[] values = this.matrix.getLabelsY().toArray();
                    Attributes.ValueSubset[] subs = Attributes
                            .getSubsetsWithoutNegations(values);

                    double maxCritValue = -Double.MAX_VALUE;
                    Attributes.ValueSubset bestSubset = null;

                    for (int i = 0; i < subs.length; i++) {
                        Distribution dOnSplit = Distribution
                                .createDistribution(clLabels);
                        Distribution dOffSplit = Distribution
                                .createDistribution(clLabels);

                        // iteruje etykiety klasy
                        for (Iterator ci = clLabels.iterator(); ci.hasNext();) {
                            Object clLabel = ci.next();

                            // etykiety atrybutu
                            for (Iterator ai = atLabels.iterator(); ai
                                    .hasNext();) {
                                Object atLabel = ai.next();
                                if (subs[i].containsValue(atLabel)) {
                                    dOnSplit.modifyCount(clLabel, this.matrix
                                            .getCount(clLabel, atLabel));
                                } else {
                                    dOffSplit.modifyCount(clLabel, this.matrix
                                            .getCount(clLabel, atLabel));
                                }
                            }
                        }

                        if (dOnSplit.getTotalExamples() == 0)
                            continue;
                        if (dOffSplit.getTotalExamples() == 0)
                            continue;

                        distrs[0] = dOnSplit;
                        distrs[1] = dOffSplit;
                        Double critValue = crit.getSplitCriteriaValue(distrs);
                        if (critValue != null) {
                            if (critValue.doubleValue() > maxCritValue) {
                                maxCritValue = critValue.doubleValue();
                                bestSubset = subs[i];
                            }
                        }
                    } // for subs

                    if (bestSubset != null) {
                        info.criteriaValue = new Double(maxCritValue);
                        info.binarySplitValueSubset = bestSubset;
                        info.splitType = SplitInfo.CATEGORICAL_BINARY;
                    }
                } else {
                    Distribution[] distrs = new Distribution[atLabels.size()];
                    int di = 0;

                    // etykiety atrybutu
                    for (Iterator ai = atLabels.iterator(); ai.hasNext();) {
                        Object atLabel = ai.next();
                        distrs[di++] = this.matrix
                                .getHorizontalDistribution(atLabel);
                    }

                    info.criteriaValue = crit.getSplitCriteriaValue(distrs);
                    if (info.criteriaValue != null) {
                        info.splitType = SplitInfo.CATEGORICAL;
                    }
                } // else (!binary)
            } else { //Continuous
                Distribution[] distrs = new Distribution[2];
                distrs[0] = this.cBelow;
                distrs[1] = this.cAbove;
                double maxCritValue = -Double.MAX_VALUE;
                int bestSplit = -1;
                Object previousValue = null;
                for (int i = 0; i < this.attributeRecords.size(); i++) {
                    AttributeRecord ar = (AttributeRecord) this.attributeRecords
                            .get(i);

                    if (previousValue != null) {
                        // je�li warto�� atrybutu si� powtarza,
                        // nie mo�na utworzy� podzia�u
                        if (!ar.attrValue.equals(previousValue)) {
                            // warto�� atrybutu zmienia si�, sprawdzam
                            // warto�� kryterium w tym miejscu
                            Double critValue = crit
                                    .getSplitCriteriaValue(distrs);
                            if (critValue != null) {
                                if (critValue.doubleValue() > maxCritValue) {
                                    maxCritValue = critValue.doubleValue();
                                    bestSplit = i;
                                }
                            }
                        }
                    }
                    this.cAbove.modifyCount(ar.classValue, -1);
                    this.cBelow.modifyCount(ar.classValue, 1);

                    // maj�c na wzgl�dzie, �e warto�ci w
                    // ArrtibuteRecord's nie mog� by� null
                    // (nieznane), previousValue nigdy nie
                    // b�dzie powt�rnie null
                    previousValue = ar.attrValue;
                }
                if (bestSplit != -1) {
                    AttributeRecord exclAr = (AttributeRecord) this.attributeRecords
                            .get(bestSplit - 1);
                    AttributeRecord inclAr = (AttributeRecord) this.attributeRecords
                            .get(bestSplit);
                    info.continuousSplitPoint = new SplitPoint(
                            (Comparable) exclAr.attrValue,
                            (Comparable) inclAr.attrValue);
                    info.splitType = SplitInfo.CONTINUOUS;
                    info.criteriaValue = new Double(maxCritValue);
                }
                this.cAbove = Distribution.createDistribution(distrs);
                this.cBelow = Distribution.createDistribution(this.cAbove.getLabels());
            }
            return info;
        }

        public boolean hasDistinctClassLabels() {
            Distribution cd = null;
            int res = 0;
            if (!this.isContinuous) {
                cd = this.matrix.getHorizontalDistribution();
            } else {
                cd = this.cAbove;
            }

            for (Iterator it = cd.getLabels().iterator(); it.hasNext();) {
                Object label = it.next();
                if (cd.getCount(label) > 0) {
                    res++;
                    if (res > 1)
                        return true;
                }
            }
            return false;
        }

        public boolean hasDistinctValues() {
            int res = 0;
            if (!this.isContinuous) {
                Distribution ad = this.matrix.getVerticalDistribution();
                for (Iterator ai = ad.getLabels().iterator(); ai.hasNext();) {
                    Object atLabel = ai.next();
                    if (ad.getCount(atLabel) > 0) {
                        res++;
                        if (res > 1)
                            return true;
                    }
                }
            } else {
                Object previousValue = null;
                for (int i = 0; i < this.attributeRecords.size(); i++) {
                    AttributeRecord ar = (AttributeRecord) this.attributeRecords
                            .get(i);
                    if (previousValue != null) {
                        if (previousValue.equals(ar.attrValue))
                            continue;
                    }
                    res++;
                    if (res > 1)
                        return true;
                    previousValue = ar.attrValue;
                }
            }
            return false;
        }
    }

    private class AttributeRecord implements Serializable {

        public Object attrValue;

        public Object classValue;

        public int rid;

        public AttributeRecord(int rid, Object classValue, Object attrValue) {
            super();
            this.rid = rid;
            this.classValue = classValue;
            this.attrValue = attrValue;
        }

        @Override
        public String toString() {
            return this.rid + ". " + this.attrValue + " " + this.classValue; //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public final Argument argBinaryTree = new Argument(new Boolean(true));

    public final Argument argSplitCrit = new Argument();

    public SPRINT() {
        super();
        this.argBinaryTree.setName(Messages.getString("SPRINT.argBinaryTree")); //$NON-NLS-1$
        this.argBinaryTree.setDescription(Messages
                .getString("SPRINT.argDescBinaryTree")); //$NON-NLS-1$
        getConfigurationArguments().addArgument(this.argBinaryTree);

        this.argSplitCrit.setValueType(DistributionCriteria.class);
        this.argSplitCrit.setName(Messages.getString("SPRINT.argSplitCrit")); //$NON-NLS-1$
        this.argSplitCrit.setDescription(Messages
                .getString("SPRINT.argDescSplitCrit")); //$NON-NLS-1$
        try {
            this.argSplitCrit.setValue(new GiniIndex());
        } catch (Exception e) {
            throw new Error(e);
        }
        getConfigurationArguments().addArgument(this.argSplitCrit);
    }

    private AttributeList[] buildAttributeLists(ReadOnlyDataset ds) {
        ArrayList als = new ArrayList();
        int clIndex = ds.getClassLabelIndex();
        for (int a = 0; a < ds.getAttributeCount(); a++) {
            if (a == clIndex)
                continue;

            AttributeList al = new AttributeList(ds, a);

            // tworzenie attribute-records
            for (int i = 0; i < ds.size(); i++) {
                ds.setCursorAt(i);
                Object classLabel = ds.getValue(clIndex);
                if (classLabel == null)
                    continue;

                Object value = ds.getValue(a);
                if (value == null)
                    continue;
                AttributeRecord ar = new AttributeRecord(i, classLabel, value);
                al.attributeRecords.add(ar);

                // uaktualnienie wartosci histogramu
                if (al.isContinuous) {
                    al.cAbove.modifyCount(classLabel, 1);
                } else {
                    if (!al.matrix.getLabelsY().contains(value)) {
                        int x = 1;
                    }
                    if (!al.matrix.getLabelsX().contains(classLabel)) {
                        int x = 1;
                    }
                    al.matrix.modifyCount(classLabel, value, 1);
                }
            }
            // sortowanie rekord�w
            if (al.isContinuous) {
                Collections.sort(al.attributeRecords, new ARComparator());
            }
            als.add(al);
        }
        AttributeList[] attrLsts = new AttributeList[als.size()];
        return (AttributeList[]) als.toArray(attrLsts);
    }

    @Override
    protected TreeNode createRootNode(ReadOnlyDataset ds) {
        boolean bin = ((Boolean) this.argBinaryTree.getValue()).booleanValue();
        DistributionCriteria dc = (DistributionCriteria) this.argSplitCrit
                .getValue();
        return produceTreeNode(ds, dc, bin, buildAttributeLists(ds));
    }

    private TreeNode produceTreeNode(AttributeSet as,
            DistributionCriteria crit, boolean binarySplits,
            AttributeList[] lists) {

        // wybra� najlepszy punkt podzia�u
        double maxCriteria = -Double.MAX_VALUE;
        AttributeList.SplitInfo bestSplit = null;
        Distribution classDistribution = null;
        for (int i = 0; i < lists.length; i++) {
            if (classDistribution == null)
                classDistribution = lists[i].getClassLabelDistribution();
            else if (classDistribution.getTotalExamples() < lists[i]
                    .getClassLabelDistribution().getTotalExamples()) {
                classDistribution = lists[i].getClassLabelDistribution();
            }

            // do podzia�u potrzbne s� r�ne warto�ci klasy
            // po�r�d przyk�ad�w ucz�cych
            // listy mog� mie� r�ne rozk�ady klasy, z powodu
            // mo�liwo�ci r�nych wielko�ci tych list (a to
            // za spraw� warto�ci nieznanych)
            if (!lists[i].hasDistinctClassLabels())
                continue;

            // potrzebne s� te� r�ne warto�ci atrybutu
            if (!lists[i].hasDistinctValues())
                continue;
            AttributeList.SplitInfo si = lists[i].getSplit(crit, binarySplits);
            if (si.criteriaValue != null) {
                if (si.criteriaValue.doubleValue() > maxCriteria) {
                    maxCriteria = si.criteriaValue.doubleValue();
                    bestSplit = si;
                }
            }
        } //for

        if (classDistribution == null)
            return null;

        if (bestSplit == null) {
            System.out.println(Messages.getString("SPRINT.7") //$NON-NLS-1$
                    + classDistribution);
            // nie mo�na dalej dzieli�, tworzymy li��
            return new TreeNode(this, classDistribution);
        }

        // utworzenie testu
        TreeNodeTest test = null;
        int[][] branchRIDs = null;
        int attrIndex = bestSplit.list.attrIndex;
        Attribute attr = as.getAttribute(attrIndex);
        switch (bestSplit.splitType) {
        case AttributeList.SplitInfo.CATEGORICAL:
            ArrayList alTests = new ArrayList();
            ArrayList alRIDs = new ArrayList();
            for (int i = 0; i < attr.getAllowedValuesCount(); i++) {
                Test ti = attr.createTest(attr.getAllowedValue(i), Test.EQUAL);
                int[] rids = bestSplit.list.getRidsFulfillingTest(ti);
                if (rids.length > 0) {
                    alTests.add(ti);
                    alRIDs.add(rids);
                }
            }
            Test[] tests = (Test[]) alTests.toArray(new Test[alTests.size()]);
            branchRIDs = (int[][]) alRIDs.toArray(new int[tests.length][]);
            test = new MultiBranchNodeTest(attrIndex, tests);
            break;

        case AttributeList.SplitInfo.CATEGORICAL_BINARY:
            Attributes.ValueSubset vs = bestSplit.binarySplitValueSubset;
            Test t1;
            if (vs.getSubsetSize() == 1) {
                Object value = vs.getSubset()[0];
                t1 = attr.createTest(value, Test.EQUAL);
            } else {
                t1 = new ValueSetTest(vs.getSubset(), Test.EQUAL);
            }
            test = new BinaryNodeTest(attrIndex, t1);
            branchRIDs = new int[2][];
            branchRIDs[TreeNodeTest.BINARY_BRANCH_YES] = bestSplit.list
                    .getRidsFulfillingTest(t1);
            branchRIDs[TreeNodeTest.BINARY_BRANCH_NO] = bestSplit.list
                    .getRidsFulfillingTest(t1.createOppositeTest());
            break;

        case AttributeList.SplitInfo.CONTINUOUS:
            Test t2 = new SplitPointTest(bestSplit.continuousSplitPoint);
            test = new BinaryNodeTest(attrIndex, t2);
            branchRIDs = new int[2][];
            branchRIDs[TreeNodeTest.BINARY_BRANCH_YES] = bestSplit.list
                    .getRidsFulfillingTest(t2);
            branchRIDs[TreeNodeTest.BINARY_BRANCH_NO] = bestSplit.list
                    .getRidsFulfillingTest(t2.createOppositeTest());
            break;

        default:
            return null;
        }

        TreeNode node = new TreeNode(this, test, bestSplit.list
                .getClassLabelDistribution());
        System.out.println(Messages.getString("SPRINT.8")); //$NON-NLS-1$
        for (int i = 0; i < test.getBranchCount(); i++) {
            Test _t = test.getTestForBranch(i);
            if (_t != null)
                System.out.println(_t.toString(attr));
        }

        // utworzenie w�z��w potomnych
        for (int i = 0; i < branchRIDs.length; i++) {
            // mo�e si� zdarzy�, �e do kt�rej� ga��zi nie
            // p�jdzie �aden przyk�ad. mo�e to mie� miejsce w
            // przypadku test�w wielowarto�ciowych (wiele ga��zi)
            if (branchRIDs[i].length == 0)
                continue;
            AttributeList[] newLists = new AttributeList[lists.length];
            for (int j = 0; j < newLists.length; j++) {
                newLists[j] = new AttributeList(lists[j], branchRIDs[i]);
            }
            TreeNode newNode = produceTreeNode(as, crit, binarySplits, newLists);
            if (newNode != null) {
                node.addBranch(i, newNode);
            }
        }

        return node;
    }
}