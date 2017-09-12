//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// CBA.java (sed.data.clasf)
// Utworzono 2005-08-07 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.cba;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.BinaryReadOnlyDataset;
import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.def.Rule;
import com.nestor.cs.data.def.RuleContainer;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSubset;
import com.nestor.cs.data.impl.datasets.DatasetView;
import com.nestor.cs.data.impl.datasets.binary.BinaryArrayItemset;
import com.nestor.cs.data.impl.datasets.binary.BinaryArrayReadOnlyDataset;
import com.nestor.cs.data.impl.datasets.binary.Operators;
import com.nestor.cs.tasks.def.LearningContext;
import com.nestor.cs.tasks.impl.clasf.AbstractRuleSetClassifier;
import com.nestor.cs.tasks.impl.clasf.RankedRuleContainer;
import com.nestor.cs.tasks.impl.clasf.jep.BinaryRule;
import com.nestor.cs.tasks.impl.preprocess.RemoveMissingValues;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.Timer;
import com.nestor.cs.util.util.param.Argument;

public class CBA extends AbstractRuleSetClassifier {

    private static class CRuleRecord {

        private int defClass;

        private Rule r;

        private int totalErrors;

        private CRuleRecord(Rule r, int defClass, int totalErrors) {
            super();
            this.r = r;
            this.defClass = defClass;
            this.totalErrors = totalErrors;
        }
    }

    private static class QClDistr {

        private int[] clDistr;

        private QClDistr(int clCnt) {
            super();
            this.clDistr = new int[clCnt];
        }

    }

    private static class RepalceInfo {

        Rule cRule;

        int dId;

        int y;

        public RepalceInfo(Rule rule, int id, int y) {
            super();
            this.cRule = rule;
            this.dId = id;
            this.y = y;
        }

    }

    private static class WRuleRec {

        private Rule cRule;

        private int dClIndex;

        private int dId;

        private Rule wRule;

        public WRuleRec(int id, int clIndex, Rule crule, Rule wrule) {
            super();
            this.dId = id;
            this.dClIndex = clIndex;
            this.cRule = crule;
            this.wRule = wrule;
        }
    }

    private static boolean areRulesEqual(RuleItemset r1, RuleItemset r2) {
        for (int i = 0; i < r1.condSet.length; i++) {
            if (r1.condSet[i] != r2.condSet[i])
                return false;
        }
        return true;
    }

    private static void calculateSupports(ArrayList Ck,
            BinaryArrayReadOnlyDataset binDs, DatasetView dsNonNull,
            int clAttrIndex, Attribute clAttr) {
        for (int i = 0; i < binDs.size(); i++) {
            dsNonNull.setCursorAt(i);
            Object classLabel = dsNonNull.getValue(clAttrIndex);
            int classLabelIndex = clAttr.getAllowedValueIndex(classLabel);

            binDs.setCursorAt(i);
            boolean[] binValues = binDs.getBinaryValues();

            ArrayList Cd = new ArrayList();
            for (int j = 0; j < Ck.size(); j++) {
                RuleItemset Ck_j = (RuleItemset) Ck.get(j);
                if (Operators.isSubsetOrEqualTo(Ck_j.condSet, binValues))
                    Cd.add(Ck_j);
            }

            for (int j = 0; j < Cd.size(); j++) {
                RuleItemset Cd_j = (RuleItemset) Cd.get(j);
                Cd_j.condSuppCount++;
                if (classLabelIndex == Cd_j.y) {
                    Cd_j.ruleSuppCount++;
                }
            }
        }
    }

    private static RuleItemset findPattern(RuleItemset base, boolean[] values,
            int level, int items) {
        int maxPrefix = 0;
        int index = -1;
        for (int i = 0; i < base.supersets.size(); i++) {
            RuleItemset sup = (RuleItemset) base.supersets.get(i);
            int p = Operators.getCommonItemPrefixLen(sup.condSet, values);
            if (p > maxPrefix) {
                maxPrefix = p;
                index = i;
            }
        }
        if (index == -1)
            return null;
        if (level == items) {
            if (maxPrefix == items) {
                return (RuleItemset) base.supersets.get(index);
            } else {
                return null;
            }
        } else {
            for (int i = 0; i < base.supersets.size(); i++) {
                RuleItemset sup = (RuleItemset) base.supersets.get(i);
                RuleItemset check = findPattern(sup, values, level + 1, items);
                if (check != null)
                    return check;
            }
            return null;
        }
    }

    private static ArrayList getAncestors(RuleItemset zero, boolean[] values) {

        ArrayList ancestors = new ArrayList();
        int len = Operators.getCommonItemPrefixLen(values, values);

        for (int i = 0; i < values.length; i++) {
            if (!values[i])
                continue;
            boolean[] copy = Operators.combine(values, values);
            copy[i] = false;

            RuleItemset ancestor = findPattern(zero, copy, 1, len - 1);
            if (ancestor == null)
                return null;
            ancestors.add(ancestor);
        }
        return ancestors;
    }

    private static ArrayList candidateGEN(RuleItemset zero, ArrayList Fk_1,
            boolean prePrune) {
        ArrayList Fk = new ArrayList();
        for (int i = 0; i < Fk_1.size() - 1; i++) {
            RuleItemset Fk_1i = (RuleItemset) Fk_1.get(i);

            for (int j = i + 1; j < Fk_1.size(); j++) {
                RuleItemset Fk_1j = (RuleItemset) Fk_1.get(j);
                if (Fk_1i.y != Fk_1j.y)
                    continue;

                // sklejam rekordy tylko r�ni�ce si� ostatni� warto�ci�
                if (Operators.areDiffOnLast(Fk_1i.condSet, Fk_1j.condSet)) {
                    boolean[] bFkn = Operators.combine(Fk_1i.condSet,
                            Fk_1j.condSet);

                    ArrayList ancestors = getAncestors(zero, bFkn);
                    if (ancestors != null) {
                        RuleItemset Fkn = new RuleItemset(bFkn, 0, Fk_1i.y, 0);
                        Fk_1i.supersets.add(Fkn);
                        Fkn.ancestors = ancestors;
                        Fk.add(Fkn);
                    }
                } else {
                    // dalsze elementy tak�e nie b�d� si� r�ni�y
                    // tylko sam� ko�c�wk�
                    break;
                }
            }
        }
        return Fk;
    }

    private static int defaultErrors(Distribution classDistr, int defClass,
            Attribute clAttr) {
        int errors = 0;
        for (Iterator iter = classDistr.getLabels().iterator(); iter.hasNext();) {
            Object clLabel = iter.next();
            int clLabelIndex = clAttr.getAllowedValueIndex(clLabel);
            if (defClass != clLabelIndex)
                errors += classDistr.getCount(clLabel);
        }
        return errors;
    }

    private static int errorsOfRule(Rule r, QClDistr clDistr) {
        //        double supp = ds.size() * r.getSupport();
        //        double csupp = supp * r.getConfidence();
        //        return (int) (supp - csupp);
        int res = 0;
        for (int i = 0; i < clDistr.clDistr.length; i++) {
            if (i != r.getClassValueIndex())
                res += clDistr.clDistr[i];
        }
        return res;
    }

    private static ArrayList genRules(ReadOnlyDataset ds, ArrayList Fk,
            double confTH) {
        ArrayList res = new ArrayList();
        for (int i = 0; i < Fk.size(); i++) {
            RuleItemset Fk_i = (RuleItemset) Fk.get(i);
            double fconf = (double) Fk_i.ruleSuppCount
                    / (double) Fk_i.condSuppCount;
            if (fconf >= confTH) {
                res.add(Fk_i);
            }
        }
        return res;
    }

    private static HashSet getCoverRules(HashSet U, ReadOnlyItemset it,
            Rule cRule, int clAttrIndex) {
        RankedRuleContainer.RuleRank cmp = RankedRuleContainer.RULE_RANK_COMPARATOR;
        HashSet wSet = new HashSet();
        Object clLabel = it.getValue(clAttrIndex);
        int clLabelIndex = it.getAttribute(clAttrIndex).getAllowedValueIndex(
                clLabel);

        for (Iterator iter = U.iterator(); iter.hasNext();) {
            Rule r = (Rule) iter.next();
            if (r.getClassValueIndex() == clLabelIndex)
                continue;
            if (!r.classifies(it))
                continue;
            if (cmp.compare(r, cRule) < 0)
                wSet.add(r);
        }
        return wSet;
    }

    public static Rule getCWRule(RuleContainer sortedR, ReadOnlyItemset it,
            int clAttrInd, boolean c) {
        Rule res = null;
        int clValI = it.getAttribute(clAttrInd).getAllowedValueIndex(
                it.getValue(clAttrInd));
        for (int i = 0; i < sortedR.getRuleCount(); i++) {
            Rule next = sortedR.getRule(i);
            if ((next.getClassValueIndex() == clValI) == c)
                if (next.classifies(it)) {
                    res = next;
                    break;
                }
        }
        return res;
    }

    private static RankedRuleContainer getSortedRules(ReadOnlyDataset ds,
            ArrayList rules) {
        RankedRuleContainer sr = new RankedRuleContainer(ds);
        for (int i = 0; i < rules.size(); i++) {
            RuleItemset ri = (RuleItemset) rules.get(i);
            BinaryRule rule = new BinaryRule(ds, ri.y, ri.condSet);
            rule.setSupport((double) ri.ruleSuppCount / (double) ds.size());
            rule.setConfidence((double) ri.ruleSuppCount
                    / (double) ri.condSuppCount);

            try {
                sr.addRule(rule);
            } catch (IncompatibleAttributeset e) {
                e.printStackTrace();
            }
        }
        return sr;
    }

    private static void modCasesCovered(HashMap qClDistrs, Rule r,
            int clLabelIndex, int mod, Attribute clAttr) {
        QClDistr clDist = (QClDistr) qClDistrs.get(r);
        if (clDist == null) {
            clDist = new QClDistr(clAttr.getAllowedValuesCount());
            qClDistrs.put(r, clDist);
        }
        clDist.clDistr[clLabelIndex] += mod;
    }

    private static void pruneCARs(ArrayList rules) {
        boolean[] prune = new boolean[rules.size()];
        for (int i = 0; i < rules.size(); i++) {
            RuleItemset ri = (RuleItemset) rules.get(i);

            if (ri.ancestors == null)
                continue;

            for (int j = 0; j < ri.ancestors.size(); j++) {
                RuleItemset anc = (RuleItemset) ri.ancestors.get(j);

                // b��d dla kr�tszej regu�y:
                double nt = anc.condSuppCount;
                double nOKt = anc.ruleSuppCount;
                double et = nt - nOKt;
                double e_t = et + 0.5d;

                // b��d dla regu�y d�u�szej:
                double nt1 = ri.condSuppCount;
                double nOKt1 = ri.ruleSuppCount;
                double et1 = nt1 - nOKt1;
                double e_T = et1 + 0.5d;
                double SE = Math.sqrt(e_T * (nt1 - e_T) / nt1);

                if (e_T + SE > et) {
                    prune[i] = true;
                    break;
                }
            }
        }
        for (int p = prune.length - 1; p >= 0; p--) {
            if (prune[p])
                rules.remove(p);
        }
    }

    public static RankedRuleContainer selectRulesM2(Distribution classDistr,
            RuleContainer sortedR, ReadOnlyDataset ds, int clAttrIndex,
            Attribute clAttr, boolean minError) {
        RankedRuleContainer.RuleRank cmp = RankedRuleContainer.RULE_RANK_COMPARATOR;

        // dzia�a jako 'mark'
        HashSet marks = new HashSet();

        //zawiera rozk�ady klas dla danej regu�y
        HashMap qClDistrs = new HashMap();
        LinkedHashSet Q = new LinkedHashSet();

        HashSet U = new HashSet();
        ArrayList A = new ArrayList();

        Rule zeroRule = new Rule(ds, -1);
        // STAGE 1.
        for (int i = 0; i < ds.size(); i++) {
            ds.setCursorAt(i);
            Object clLabel = ds.getValue(clAttrIndex);
            int clLabelIndex = clAttr.getAllowedValueIndex(clLabel);
            if (clLabelIndex == -1)
                continue;

            Rule cRule = getCWRule(sortedR, ds, clAttrIndex, true);
            Rule wRule = getCWRule(sortedR, ds, clAttrIndex, false);

            if (cRule == null)
                //                continue;
                cRule = zeroRule;

            U.add(cRule);
            modCasesCovered(qClDistrs, cRule, clLabelIndex, 1, clAttr);

            // tzn. cRule > wRule
            boolean cBetterThanW;
            if (wRule == null)
                cBetterThanW = true;
            else
                cBetterThanW = (cmp.compare(cRule, wRule) < 0);

            if (cBetterThanW) {
                Q.add(cRule);
                marks.add(cRule);
            } else {
                WRuleRec rec = new WRuleRec(i, clLabelIndex, cRule, wRule);
                A.add(rec);
            }
        }

        // STAGE 2.

        // kolekcja list ArrayList. kazda z nich zawiera
        // obiekty ReplaceInfo. Kluczem jest regu�a
        HashMap replaces = new HashMap();

        for (int i = 0; i < A.size(); i++) {
            WRuleRec rec = (WRuleRec) A.get(i);

            if (marks.contains(rec.wRule)) {
                modCasesCovered(qClDistrs, rec.cRule, rec.dClIndex, -1, clAttr);
                modCasesCovered(qClDistrs, rec.wRule, rec.dClIndex, 1, clAttr);
            } else {
                ds.setCursorAt(rec.dId);
                HashSet wSet = getCoverRules(U, ds, rec.cRule, clAttrIndex);
                for (Iterator iter = wSet.iterator(); iter.hasNext();) {
                    Rule w = (Rule) iter.next();
                    ArrayList replaceList = (ArrayList) replaces.get(w);
                    if (replaceList == null) {
                        replaceList = new ArrayList();
                        replaces.put(w, replaceList);
                    }
                    replaceList.add(new RepalceInfo(rec.cRule, rec.dId,
                            rec.dClIndex));
                    modCasesCovered(qClDistrs, w, rec.dClIndex, 1, clAttr);
                }
                Q.addAll(wSet);
            }
        }

        // STAGE 3.
        int ruleErrors = 0;
        RankedRuleContainer sortQ = new RankedRuleContainer(ds);
        for (Iterator iter = Q.iterator(); iter.hasNext();) {
            Rule r = (Rule) iter.next();
            if (r != zeroRule)
                try {
                    sortQ.addRule(r);
                } catch (IncompatibleAttributeset e) {
                    e.printStackTrace();
                }
        }

        if (minError) {
            boolean[] alreadyCoveredIds = new boolean[ds.size()];
            ArrayList C = new ArrayList();
            for (int rNum = 0; rNum < sortQ.getRuleCount(); rNum++) {
                Rule r = sortQ.getRule(rNum);
                QClDistr clDistr = (QClDistr) qClDistrs.get(r);
                if (clDistr == null)
                    continue;

                if (clDistr.clDistr[r.getClassValueIndex()] > 0) {
                    ArrayList replaceList = (ArrayList) replaces.get(r);
                    if (replaceList != null) {
                        for (Iterator i2 = replaceList.iterator(); i2.hasNext();) {
                            RepalceInfo ri = (RepalceInfo) i2.next();
                            if (alreadyCoveredIds[ri.dId]) {
                                modCasesCovered(qClDistrs, r, ri.y, -1, clAttr);
                            } else {
                                modCasesCovered(qClDistrs, ri.cRule, ri.y, -1,
                                        clAttr);
                                alreadyCoveredIds[ri.dId] = true;
                            }
                        }
                    }
                    ruleErrors += errorsOfRule(r, clDistr);
                    //                    updateClassDistr(ds, classDistr, r, clAttr);
                    Object labelToModify = clAttr.getAllowedValue(r
                            .getClassValueIndex());
                    classDistr.modifyCount(labelToModify, -clDistr.clDistr[r
                            .getClassValueIndex()]);
                    int defClass = clAttr.getAllowedValueIndex(classDistr
                            .getFirstMajorLabel());
                    int defErrors = defaultErrors(classDistr, defClass, clAttr);
                    int totalErrors = ruleErrors + defErrors;
                    C.add(new CRuleRecord(r, defClass, totalErrors));
                    if (classDistr.getTotalExamples() == 0)
                        break;
                }
            }

            int minTotalError = Integer.MAX_VALUE;
            int minErrorIndex = -1;
            for (int i = 0; i < C.size(); i++) {
                CRuleRecord rr = (CRuleRecord) C.get(i);
                if (rr.totalErrors < minTotalError) {
                    minTotalError = rr.totalErrors;
                    minErrorIndex = i;
                }
            }

            RankedRuleContainer resRules = new RankedRuleContainer(ds);
            for (int i = 0; i <= minErrorIndex; i++) {
                CRuleRecord rr = (CRuleRecord) C.get(i);
                try {
                    resRules.addRule(rr.r);
                } catch (IncompatibleAttributeset e) {
                    e.printStackTrace();
                }
                if (i == minErrorIndex)
                    resRules.setDefaultClassLabelIndex(rr.defClass);
            }

            return resRules;
        } else
            return sortQ;
    }

    private static void updateClassDistr(ReadOnlyDataset ds,
            Distribution classDistr, Rule r, Attribute clAttr) {
        Object clLabel = clAttr.getAllowedValue(r.getClassValueIndex());
        double supp = ds.size() * r.getSupport();
        int csupp = (int) (supp * r.getConfidence());
        int curr = classDistr.getCount(clLabel);
        int ncnt = curr - csupp;
        if (ncnt < 0)
            ncnt = 0;
        classDistr.setCount(clLabel, ncnt);
    }

    public final Argument argMaxRules = new Argument(new Integer(50000));

    public final Argument argMinClasfError = new Argument(new Boolean(false));

    public final Argument argMinConf = new Argument(new Double(50));

    public final Argument argMinSupport = new Argument(new Double(5));

    public final Argument argPrePrune = new Argument(new Boolean(false));

    public CBA() {
        super();

        this.argMinSupport.setName(Messages.getString("CBA.argMinSupp")); //$NON-NLS-1$
        this.argMinSupport.setDescription(Messages.getString("CBA.argDescMinSupp")); //$NON-NLS-1$
        this.argMinSupport.setLowBound(new Double(0));
        this.argMinSupport.setHighBound(new Double(100));
        getConfigurationArguments().addArgument(this.argMinSupport);

        this.argMinConf.setName(Messages.getString("CBA.argMinConf")); //$NON-NLS-1$
        this.argMinConf.setDescription(Messages.getString("CBA.argDescMinConf")); //$NON-NLS-1$
        this.argMinConf.setLowBound(new Double(0));
        this.argMinConf.setHighBound(new Double(100));
        getConfigurationArguments().addArgument(this.argMinConf);

        this.argPrePrune.setName(Messages.getString("CBA.argPrunePessimistic")); //$NON-NLS-1$
        this.argPrePrune.setDescription(Messages
                .getString("CBA.argDescPrunePessimistic")); //$NON-NLS-1$
        getConfigurationArguments().addArgument(this.argPrePrune);

        this.argMinClasfError.setName(Messages.getString("CBA.argMinError")); //$NON-NLS-1$
        this.argMinClasfError.setDescription(Messages
                .getString("CBA.argDescMinError")); //$NON-NLS-1$
        getConfigurationArguments().addArgument(this.argMinClasfError);

        this.argMaxRules.setName(Messages.getString("CBA.argMaxRules")); //$NON-NLS-1$
        this.argMaxRules.setDescription(Messages.getString("CBA.argDescMaxRules")); //$NON-NLS-1$
        this.argMaxRules.setLowBound(new Integer(0));
        this.argMaxRules.setHighBound(new Integer(250000));
        getConfigurationArguments().addArgument(this.argMaxRules);
    }

    public Object classify(ReadOnlyDataset dsTrain, ReadOnlyItemset it)
            throws IncompatibleAttributeset {
        LearningContext lc = getLearningContext();
        IncompatibleAttributeset.checkLength(lc.getAttributeSet(), it);

        for (int i = 0; i < getRuleCount(); i++) {
            Rule r = getRule(i);
            if (r.classifies(it))
                return lc.getClassAttribute().getAllowedValue(
                        r.getClassValueIndex());
        }

        return lc.getClassAttribute().getAllowedValue(
                getRules().getDefaultClassLabelIndex());
    }

    @Override
    public void learn(ReadOnlyDataset dsTrain) throws ProcessingException {
        super.learn(dsTrain);

        AttributeSet asCopy = getLearningContext().getAttributeSet();

        try {
            Attributes.checkForBinaryAttributes(asCopy, true);
        } catch (IncompatibleAttributeset e1) {
            throw new ProcessingException(e1);
        }

        int clAttrIndex = asCopy.getClassLabelIndex();
        Attribute clAttr = asCopy.getAttribute(clAttrIndex);
        int clLabels = clAttr.getAllowedValuesCount();

        int minSupp = (int) (((Double) this.argMinSupport.getValue()).doubleValue() / 100d * dsTrain
                .size());
        if (minSupp < 1)
            minSupp = 1;
        double minConf = ((Double) this.argMinConf.getValue()).doubleValue() / 100d;
        boolean prePrune = ((Boolean) this.argPrePrune.getValue()).booleanValue();
        boolean minError = ((Boolean) this.argMinClasfError.getValue())
                .booleanValue();
        int maxRule = ((Integer) this.argMaxRules.getValue()).intValue();

        try {
            System.out.println(Messages.getString("CBA.9")); //$NON-NLS-1$
            // pozbywam si� warto�ci nieznanych
            DatasetView dsNonNull = RemoveMissingValues.getNonNullView(dsTrain);
            System.out.println(Messages.getString("CBA.10")); //$NON-NLS-1$

            // ca�y zbi�r przerabiam na binarny, potrzebny b�dzie
            // do obliczania wspar�
            BinaryArrayReadOnlyDataset binDs = new BinaryArrayReadOnlyDataset(
                    new DatasetView(dsNonNull, new DefaultAttributeSubset(
                            dsNonNull, clAttrIndex)));
            int binAttCnt = binDs.getAttributeCount();

            Distribution classDistr = Distribution.createDistribution(
                    dsNonNull, clAttrIndex);

            // regu�a domy�lna
            ArrayList zeroRuleList = null;

            RuleItemset defRule;
            Object defClassLabel = classDistr.getFirstMajorLabel();
            int defClassLabelIndex = clAttr.getAllowedValueIndex(defClassLabel);

            defRule = new RuleItemset(new boolean[binAttCnt], classDistr
                    .getTotalExamples(), defClassLabelIndex, classDistr
                    .getCount(defClassLabel));
            zeroRuleList = new ArrayList();
            zeroRuleList.add(defRule);

            // trzeba wygenerowa� 1-regu�y cz�ste
            ArrayList Fk_1 = new ArrayList();
            {
                boolean[] fullItemset = new boolean[binAttCnt];
                for (int i = 0; i < fullItemset.length; i++)
                    fullItemset[i] = true;
                BinaryArrayItemset itFullItemset = new BinaryArrayItemset(
                        binDs, fullItemset);
                BinaryReadOnlyDataset singlets = Operators
                        .allSingletonsOf(itFullItemset);
                RuleItemset[] oneRuleItemsets = new RuleItemset[singlets.size()];
                int[][] oneRuleDistrs = new int[singlets.size()][clLabels];

                for (int i = 0; i < binDs.size(); i++) {
                    binDs.setCursorAt(i);
                    dsNonNull.setCursorAt(i);
                    Object clLabel = dsNonNull.getValue(clAttrIndex);
                    int clLabelIndex = clAttr.getAllowedValueIndex(clLabel);

                    for (int j = 0; j < oneRuleItemsets.length; j++) {
                        if (oneRuleItemsets[j] == null) {
                            singlets.setCursorAt(j);
                            oneRuleItemsets[j] = new RuleItemset(singlets
                                    .getBinaryValues(), 0, -1, 0);
                        }
                        if (Operators.isSubsetOrEqualTo(
                                oneRuleItemsets[j].condSet, binDs
                                        .getBinaryValues())) {
                            oneRuleItemsets[j].condSuppCount++;
                            oneRuleDistrs[j][clLabelIndex]++;
                        }
                    }
                }
                for (int i = 0; i < oneRuleItemsets.length; i++) {
                    RuleItemset ri = oneRuleItemsets[i];
                    for (int j = 0; j < clLabels; j++) {
                        if (oneRuleDistrs[i][j] >= minSupp) {
                            int ruleCnt = oneRuleDistrs[i][j];
                            int condCnt = ri.condSuppCount;
                            RuleItemset nri = new RuleItemset(ri.condSet,
                                    condCnt, j, ruleCnt);
                            nri.ancestors = zeroRuleList;
                            defRule.supersets.add(nri);
                            Fk_1.add(nri);
                        }
                    }
                }
            }
            System.out.println(Messages.getString("CBA.11") //$NON-NLS-1$
                    + Fk_1.size());
            int k = 0;

            ArrayList rules = new ArrayList();
            ArrayList nrules = genRules(dsTrain, Fk_1, minConf);
            ArrayList nxrules = new ArrayList();
            nxrules.addAll(nrules);
            System.out.println(k
                    + Messages.getString("CBA.16") + nxrules.size()); //$NON-NLS-1$

            if (prePrune) {
                pruneCARs(nxrules);
                System.out.println(k + Messages.getString("CBA.17")//$NON-NLS-1$
                        + nxrules.size());
            }
            rules.addAll(nxrules);
            System.out.println(k + Messages.getString("CBA.19") + rules.size()); //$NON-NLS-1$

            Timer t = new Timer();

            int totalCnadidates = Fk_1.size() + rules.size();
            for (k = 1; !Fk_1.isEmpty(); k++) {
                // TODO
                t.i(0).start();

                ArrayList Ck = candidateGEN(defRule, Fk_1, prePrune);

                // TODO
                t.i(0).end();

                totalCnadidates += Ck.size();
                if (totalCnadidates > maxRule) {
                    System.out.println(Messages.getString("CBA.14")); //$NON-NLS-1$
                    break;
                }
                System.out.println(k + Messages.getString("CBA.15") //$NON-NLS-1$
                        + Ck.size());

                // TODO
                t.i(1).start();
                calculateSupports(Ck, binDs, dsNonNull, clAttrIndex, clAttr);

                // usuwanie niecz�stych
                ArrayList Fk = new ArrayList();
                for (int i = 0; i < Ck.size(); i++) {
                    RuleItemset Ck_i = (RuleItemset) Ck.get(i);
                    if (Ck_i.ruleSuppCount >= minSupp) {
                        Fk.add(Ck_i);
                    } else {
                        // usuwanie odno�nik�w do zbioru niecz�stego
                        for (int j = 0; j < Ck_i.ancestors.size(); j++) {
                            RuleItemset ancestor = (RuleItemset) Ck_i.ancestors
                                    .get(j);
                            ancestor.supersets.remove(Ck_i);
                        }
                    }
                }

                // TODO
                t.i(1).end();
                t.i(2).start();

                nrules = genRules(dsTrain, Fk, minConf);
                nxrules.clear();
                nxrules.addAll(nrules);

                Fk_1 = Fk;

                System.out.println(k
                        + Messages.getString("CBA.16") + nxrules.size()); //$NON-NLS-1$
                if (prePrune) {
                    pruneCARs(nxrules);
                    System.out.println(k + Messages.getString("CBA.17")//$NON-NLS-1$
                            + nxrules.size());
                    if (nxrules.size() == 0)
                        break;
                }
                rules.addAll(nxrules);

                // TODO
                t.i(2).end();

                System.out.println(k
                        + Messages.getString("CBA.19") + rules.size()); //$NON-NLS-1$

                totalCnadidates += nxrules.size();
                if (totalCnadidates >= maxRule) {
                    System.out.println(Messages.getString("CBA.18"));//$NON-NLS-1$
                    break;
                }
            }

            // TODO
            t.i(3).start();

            System.out.println(Messages.getString("CBA.20")); //$NON-NLS-1$
            RankedRuleContainer sortedRules = getSortedRules(dsTrain, rules);
            RankedRuleContainer coverRules = selectRulesM2(classDistr,
                    sortedRules, dsNonNull, clAttrIndex, clAttr, minError);
            super.setRules(coverRules);

            // TODO
            t.i(3).end();

            t.printAll(new String[] { Messages.getString("CBA.1"), //$NON-NLS-1$
                    Messages.getString("CBA.2"), //$NON-NLS-1$
                    Messages.getString("CBA.3"), Messages.getString("CBA.4") }); //$NON-NLS-1$ //$NON-NLS-2$

            System.out.println(Messages.getString("CBA.0") //$NON-NLS-1$
                    + getRules().getRuleCount());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public boolean requiresLearning() {
        return true;
    }
}