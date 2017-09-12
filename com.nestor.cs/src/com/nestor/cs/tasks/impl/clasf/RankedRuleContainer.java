//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// RuleContainer.java (sed.data.clasf)
// Utworzono 2005-08-07 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.Rule;
import com.nestor.cs.data.def.RuleContainer;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;
import com.nestor.cs.tasks.def.Classifier.MissingClassLabelAttribute;

public class RankedRuleContainer implements Serializable, RuleContainer {

    public static class RuleRank implements Comparator {

        public RuleRank() {
            super();
        }

        // zwraca -1 je�li pierwsza regu�a jest wy�ej w rankingu
        // ni� regu�a druga, 0 je�li ich ranking jest r�wny
        // i 1 je�li R2 jest wy�ej w rankingu:
        // R1 jest wy�ej w rankingu ni� R2 gdy:
        // 1) R1 ma wi�ksze zaufanie, lub
        // 2) zaufania regu� s� r�wne, lecz R1 na wi�ksze wsparcie
        // 3) zaufania i wsparcia s� r�wnem lecz R1 jest kr�tsza
        public int compare(Object arg0, Object arg1) {
            int res = 0;
            if ((arg0 instanceof Rule) && (arg1 instanceof Rule)) {
                Rule r0 = (Rule) arg0;
                Rule r1 = (Rule) arg1;
                double conf0 = r0.getConfidence();
                double conf1 = r1.getConfidence();
                if (conf0 > conf1)
                    return -1;
                if (conf0 < conf1)
                    return 1;

                double supp0 = r0.getSupport();
                double supp1 = r1.getSupport();
                if (supp0 > supp1)
                    return -1;
                if (supp0 < supp1)
                    return 1;

                int len0 = r0.getTestSetLength();
                int len1 = r1.getTestSetLength();
                if (len0 < len1)
                    return -1;
                if (len0 > len1)
                    return 1;

                return 0;
            }
            return res;
        }

    }

    public static final RuleRank RULE_RANK_COMPARATOR = new RuleRank();

    private static void addRule(Rule r, List list) {
        int ins = Collections.binarySearch(list, r, RULE_RANK_COMPARATOR);
        if (ins < 0)
            ins = -ins - 1;
        list.add(ins, r);
    }

    private ArrayList allContainedRules = new ArrayList();

    private AttributeSet attributeSet;

    private int defaultClassLabelIndex = -1;

    private ArrayList[] rules = null;

    public RankedRuleContainer(AttributeSet as) {
        super();
        this.attributeSet = new DefaultAttributeSet(as);
        int clAttrIndex = as.getClassLabelIndex();
        Attribute clAttr = as.getAttribute(clAttrIndex);
        this.rules = new ArrayList[clAttr.getAllowedValuesCount()];
        for (int i = 0; i < this.rules.length; i++) {
            this.rules[i] = new ArrayList();
        }
    }

    public void addRule(Rule r) throws IncompatibleAttributeset {
        r.setAttributeSet(this.attributeSet);
        addRule(r, this.rules[r.getClassValueIndex()]);
        addRule(r, this.allContainedRules);
    }

    public AttributeSet getAttributeSet() {
        return this.attributeSet;
    }

    public RankedRuleContainer getDatabaseCoverageRules(ReadOnlyDataset ds,
            int coverageTH) throws IncompatibleAttributeset,
            MissingClassLabelAttribute {
        IncompatibleAttributeset.checkEquality(this.attributeSet, ds);
        if (ds.getClassLabelIndex() < 0)
            throw new MissingClassLabelAttribute();

        int totalRules = getRuleCount();
        ArrayList allRules = this.allContainedRules;

        ArrayList dsIndexes = new ArrayList(ds.size());
        for (int i = 0; i < ds.size(); i++) {
            dsIndexes.add(new Integer(i));
        }
        int[] coverages = new int[ds.size()];

        int clAttrIndex = ds.getClassLabelIndex();
        Attribute clAttr = ds.getAttribute(clAttrIndex);

        boolean[] marks = new boolean[allRules.size()];

        double totalErrors = 0;
        double prevDefaultErrors = 0;
        Distribution classDistr = Distribution.createDistribution(ds,
                clAttrIndex);

        for (int x = 0; x < allRules.size(); x++) {
            if (dsIndexes.isEmpty())
                break;
            Rule r = (Rule) allRules.get(x);

            ArrayList dsIndexesToCover = new ArrayList();

            for (int i = 0; i < dsIndexes.size(); i++) {
                int index = ((Integer) dsIndexes.get(i)).intValue();
                ds.setCursorAt(index);
                Object clLabel = ds.getValue(clAttrIndex);
                int clLabelIndex = clAttr.getAllowedValueIndex(clLabel);

                if (r.classifies(ds)) {
                    dsIndexesToCover.add(new Integer(index));

                    if (r.getClassValueIndex() == clLabelIndex)
                        marks[x] = true;
                }
            }

            // je�li regu�a zosta�a wybrana
            if (marks[x]) {
                ArrayList dsIndexesToRemove = new ArrayList(dsIndexesToCover
                        .size());
                // ustawiam pokrycie odpowiednich przyk�ad�w
                for (int i = 0; i < dsIndexesToCover.size(); i++) {
                    int index = ((Integer) dsIndexesToCover.get(i)).intValue();
                    coverages[index]++;
                    if (coverages[index] >= coverageTH)
                        dsIndexesToRemove.add(new Integer(i));
                }

                // indeksy te s� od najmniejszego do najwi�kszego
                // wi�c usuwanie nale�y przeprowadzi� od ko�ca
                for (int i = dsIndexesToRemove.size() - 1; i >= 0; i--) {
                    int indexToRemove = ((Integer) dsIndexesToRemove.get(i))
                            .intValue();
                    dsIndexes.remove(indexToRemove);
                }
            }
        }

        RankedRuleContainer rc = new RankedRuleContainer(this.attributeSet);
        for (int i = 0; i < marks.length; i++) {
            if (marks[i])
                rc.addRule((Rule) allRules.get(i));
        }
        Object majorClassLabel = classDistr.getFirstMajorLabel();
        rc.setDefaultClassLabelIndex(clAttr
                .getAllowedValueIndex(majorClassLabel));

        return rc;
    }

    public int getDefaultClassLabelIndex() {
        return this.defaultClassLabelIndex;
    }

    public Rule getRule(int n) {
        return (Rule) this.allContainedRules.get(n);
    }

    public Rule getRule(int classValueIndex, int n) {
        return (Rule) this.rules[classValueIndex].get(n);
    }

    public int getRuleCount() {
        return this.allContainedRules.size();
    }

    public int getRuleCount(int classValueIndex) {
        return this.rules[classValueIndex].size();
    }

    public void resortRules() {
        for (int i = 0; i < this.rules.length; i++) {
            Collections.sort(this.rules[i], RULE_RANK_COMPARATOR);
        }
    }

    public void setDefaultClassLabelIndex(int defaultClassLabelIndex) {
        this.defaultClassLabelIndex = defaultClassLabelIndex;
    }

}