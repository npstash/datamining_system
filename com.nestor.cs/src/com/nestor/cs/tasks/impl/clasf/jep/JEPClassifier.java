//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// MDB_LLBorder.java (sed.data.clasf.jep)
// Utworzono 2005-07-29 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.jep;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.AttributeSubset;
import com.nestor.cs.data.def.BinaryDataset;
import com.nestor.cs.data.def.BinaryReadOnlyDataset;
import com.nestor.cs.data.def.BinaryReadOnlyItemset;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ItemsetIndexesSelector;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.def.Rule;
import com.nestor.cs.data.def.RuleContainer;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSubset;
import com.nestor.cs.data.impl.datasets.DatasetView;
import com.nestor.cs.data.impl.datasets.Datasets;
import com.nestor.cs.data.impl.datasets.SelectedItemsets;
import com.nestor.cs.data.impl.datasets.binary.BinaryArrayItemset;
import com.nestor.cs.data.impl.datasets.binary.BinaryArrayListDataset;
import com.nestor.cs.data.impl.datasets.binary.BinaryArrayReadOnlyDataset;
import com.nestor.cs.data.impl.datasets.binary.FastMinItemsetsMiner;
import com.nestor.cs.data.impl.datasets.binary.Operators;
import com.nestor.cs.data.impl.tests.TestSelector;
import com.nestor.cs.tasks.def.Classifier;
import com.nestor.cs.tasks.impl.clasf.AbstractRuleSetClassifier;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.ArrayUtils;
import com.nestor.cs.util.util.param.Argument;

public class JEPClassifier extends AbstractRuleSetClassifier {

    public static RootedBorder borderDiff(BinaryReadOnlyItemset U,
            BinaryReadOnlyDataset S, FastMinItemsetsMiner miner) {
        RootedBorder rb = new RootedBorder();
        rb.rootBound = U;
        BinaryArrayListDataset bds = null;
        try {
            bds = new BinaryArrayListDataset(S);
        } catch (IncompatibleAttributeset e) {
            throw new Error(e);
        }
        rb.setBound = bds;

        if (S.size() == 0)
            return rb;

        BinaryReadOnlyItemset U_S1 = new BinaryArrayItemset(U);
        Operators.deduct(U_S1.getBinaryValues(), S.getBinaryItemset(0)
                .getBinaryValues());
        try {
            bds.appendBinary(Operators.allSingletonsOf(U_S1));
        } catch (IncompatibleAttributeset e1) {
            throw new Error(e1);
        }

        for (int i = 1; i < S.size(); i++) {
            BinaryReadOnlyItemset U_Sj = new BinaryArrayItemset(U);
            S.setCursorAt(i);
            boolean[] Si = S.getBinaryValues();
            Operators.deduct(U_Sj.getBinaryValues(), Si);

            BinaryReadOnlyDataset singletons = Operators.allSingletonsOf(U_Sj);
            BinaryReadOnlyDataset cartes = Operators.cartesian(bds, singletons);

            bds.removeAll();
            miner.setSearchMax(false);
            int[] minItemsets = miner.selectItemsetIndexes(cartes);

            try {
                bds.appendBinary(cartes, minItemsets);
            } catch (IncompatibleAttributeset e2) {
                throw new Error(e2);
            }
        }
        return rb;
    }

    public static Object classifty(ReadOnlyDataset dsTrain, ReadOnlyItemset it,
            RuleContainer rules) throws IncompatibleAttributeset {
        IncompatibleAttributeset.checkLength(dsTrain, it);

        int clAttrIndex = dsTrain.getClassLabelIndex();
        Attribute clAttr = dsTrain.getAttribute(clAttrIndex);

        double[] classVotes = new double[clAttr.getAllowedValuesCount()];
        for (int cl = 0; cl < clAttr.getAllowedValuesCount(); cl++) {
            for (int i = 0; i < rules.getRuleCount(cl); i++) {
                Rule r = rules.getRule(cl, i);
                if (r.classifies(it)) {
                    classVotes[cl] += r.getSupport();
                }
            }
        }
        int maxVotesIndex = ArrayUtils.max(classVotes);
        int maxCount = ArrayUtils
                .countOf(classVotes, classVotes[maxVotesIndex]);
        if (maxCount != 1)
            return null;
        else
            return clAttr.getAllowedValue(maxVotesIndex);
    }

    public static BinaryDataset getMEJEPs(RootedBorder[] borders,
            FastMinItemsetsMiner miner) {
        int totalMEJEPs = 0;
        BinaryReadOnlyDataset firstDS = null;
        for (int i = 0; i < borders.length; i++) {
            totalMEJEPs += borders[i].setBound.size();
            if (firstDS == null)
                firstDS = borders[i].setBound;
        }
        if (firstDS == null)
            return null;
        BinaryArrayListDataset mejeps;
        try {
            mejeps = new BinaryArrayListDataset(firstDS);
        } catch (IncompatibleAttributeset e) {
            throw new Error(e);
        }
        for (int i = 0; i < borders.length; i++) {
            BinaryReadOnlyDataset binDs = borders[i].setBound;
            for (int j = 0; j < binDs.size(); j++) {
                binDs.setCursorAt(j);
                mejeps.addBinaryItemset(binDs.getBinaryValues());
            }
        }
        miner.setSearchMax(false);
        int[] minIndexes = miner.selectItemsetIndexes(mejeps);
        mejeps.retainItemsets(minIndexes);

        return mejeps;
    }

    // jako paramerty brane sa granice horyzontalne danej klasy (D2) oraz
    // pozostalych danych (D1)
    public static BinaryDataset MDB_LLBorder(
            BinaryReadOnlyDataset horizBoundD2,
            BinaryReadOnlyDataset horizBoundD1, FastMinItemsetsMiner miner) {
        BinaryArrayListDataset lowBounds;
        try {
            lowBounds = new BinaryArrayListDataset(horizBoundD2);
        } catch (IncompatibleAttributeset e1) {
            throw new Error(e1);
        }

        int attrCnt = horizBoundD1.getAttributeCount();
        for (int i = 0; i < horizBoundD2.size(); i++) {
            BinaryReadOnlyItemset Ei = horizBoundD2.getBinaryItemset(i);
            System.out.print("."); //$NON-NLS-1$
            if (((i % 70) == 0) && (i / 70 > 0))
                System.out.println();

            // jesli ktorys z Cj jest supersetm Ei to
            // znaczy �e Ei nie mo�e zawiera� JEP'a
            // -> wszystkie jego podzbiory istniej� w D1
            // (a dok�adnie w Cj)
            boolean isCjSuper = false;
            for (int j = 0; j < horizBoundD1.size(); j++) {
                horizBoundD1.setCursorAt(j);
                if (Operators.isSubsetOrEqualTo(Ei.getBinaryValues(),
                        horizBoundD1.getBinaryValues())) {
                    isCjSuper = true;
                    break;
                }
            }
            if (isCjSuper)
                continue;
            // {C'1, ..., C'm} <- {C1 x Ej, ..., Cm x Ej}
            BinaryArrayListDataset C_ = null;
            try {
                C_ = new BinaryArrayListDataset(horizBoundD1);
            } catch (IncompatibleAttributeset e) {
                throw new Error(e);
            }
            for (int j = 0; j < horizBoundD1.size(); j++) {
                horizBoundD1.setCursorAt(j);
                boolean[] Cj = horizBoundD1.getBinaryValues();
                boolean[] C_j = new boolean[Cj.length];
                for (int k = 0; k < C_j.length; k++)
                    C_j[k] = Cj[k];
                Operators.commonPart(C_j, Ei.getBinaryValues());
                C_.addBinaryItemset(C_j);
            }
            // w C_ pozostawiam tylko maksymalne elementy
            miner.setSearchMax(true);
            int[] maxItemsets = miner.selectItemsetIndexes(C_);
            C_.retainItemsets(maxItemsets);

            // dodanie do wyniku granicy utworzonej
            // przez BORDER-DIFF
            RootedBorder rb = borderDiff(Ei, C_, miner);
            try {
                lowBounds.appendBinary(rb.setBound);
            } catch (IncompatibleAttributeset e2) {
                throw new Error(e2);
            }
            if (((i % 25) == 0) || (i == horizBoundD2.size() - 1)) {
                miner.setSearchMax(false);
                int[] minIndexes = miner.selectItemsetIndexes(lowBounds);
                lowBounds.retainItemsets(minIndexes);
            }
        }
        System.out.println(""); //$NON-NLS-1$
        return lowBounds;
    }

    public final Argument argMinSupport = new Argument(new Double(0));

    private FastMinItemsetsMiner sharedMiner = new FastMinItemsetsMiner();

    public JEPClassifier() {
        super();
        this.argMinSupport.setName(Messages.getString("JEPClassifier.2")); //$NON-NLS-1$
        this.argMinSupport.setDescription(Messages.getString("JEPClassifier.3")); //$NON-NLS-1$
        this.argMinSupport.setRequired(false);
        this.argMinSupport.setHighBound(new Double(1));
        this.argMinSupport.setLowBound(new Double(0));
        getConfigurationArguments().addArgument(this.argMinSupport);
    }

    public Object classify(ReadOnlyDataset dsTrain, ReadOnlyItemset it)
            throws IncompatibleAttributeset {
        return classifty(dsTrain, it, this);
    }

    public double getArgMinSupport() {
        if (this.argMinSupport.getValue() != null)
            return ((Double) this.argMinSupport.getValue()).doubleValue() / 100d;
        return 0;
    }

    private FastMinItemsetsMiner getSharedMiner() {
        if (this.sharedMiner == null) {
            this.sharedMiner = new FastMinItemsetsMiner();
        }
        return this.sharedMiner;
    }

    @Override
    public void learn(ReadOnlyDataset dsTrain)
            throws Classifier.MissingClassLabelAttribute,
            Classifier.InvalidDataset, ProcessingException {
        super.learn(dsTrain);

        createLearningContext(dsTrain);

        // tworz� kopi� zestawu atrybut�w na wszelki wypadek
        // gdy oryginalny p�niej si� zmieni� (referencje do
        // zestawu atrubuty b�d� w regu�ach!)
        AttributeSet asCopy = getLearningContext().getAttributeSet();

        try {
            Attributes.checkForBinaryAttributes(asCopy, true);
        } catch (IncompatibleAttributeset e) {
            throw new InvalidDataset(e);
        }

        if (dsTrain.size() == 0)
            return;

        int clAttrIndex = asCopy.getClassLabelIndex();
        Attribute clAttr = asCopy.getAttribute(clAttrIndex);

        // w dalszych obliczeniach trzeba wykluczyc atrybut klasy
        AttributeSubset woClass = new DefaultAttributeSubset(asCopy,
                clAttrIndex);

        System.out.println(Messages.getString("JEPClassifier.JEPlearning")); //$NON-NLS-1$
        System.out.println("Liczba przyk�ad�w: " + (dsTrain.size()));
        System.out.println("Liczba atrybut�w binarnych: "
                + (dsTrain.getAttributeCount() - 1));
        for (int cl = 0; cl < clAttr.getAllowedValuesCount(); cl++) {
            Object clValue = clAttr.getAllowedValue(cl);
            System.out
                    .println(Messages
                            .getString("JEPClassifier.learningForClass") + clAttr.getName() + "=" //$NON-NLS-1$ //$NON-NLS-2$
                            + clAttr.getNameForValue(clValue));

            // nalezy wybrac indeksy tych itemset�w kt�re
            // maj� klas� cl
            Test tClass = clAttr.createTest(clValue, Test.EQUAL);
            TestSelector tsClass = new TestSelector(clAttrIndex, tClass);
            ItemsetIndexesSelector iisClass = new SelectedItemsets(tsClass);
            int[] iClass = iisClass.selectItemsetIndexes(dsTrain);

            // pozosta�e indeksy
            int[] iOther = Datasets.remainingIndexes(dsTrain.size(), iClass);

            // trzeba utworzyc zbiory danych odpowiadaj�ce
            // utworzonym indeksom
            // widoki na odpowiadaj�ce cz�ci zbioru
            DatasetView dvClass = new DatasetView(dsTrain, woClass, iClass);
            DatasetView dvOther = new DatasetView(dsTrain, woClass, iOther);

            // kopiowanie warto�ci do pami�ci i utworzenie
            // osobnych zbior�w
            BinaryArrayReadOnlyDataset dsClass;
            BinaryArrayReadOnlyDataset dsOther;
            try {
                dsClass = new BinaryArrayReadOnlyDataset(dvClass);
                dsOther = new BinaryArrayReadOnlyDataset(dvOther);
            } catch (Exception e) {
                throw new Error(e);
            }
            // wyszukanie element�w maksymalnych
            getSharedMiner().setSearchMax(true);
            int[] iMaxClass = getSharedMiner().selectItemsetIndexes(dsClass);
            int[] iMaxOther = getSharedMiner().selectItemsetIndexes(dsOther);
            System.out
                    .println(Messages
                            .getString("JEPClassifier.horizBordersSize") //$NON-NLS-1$
                            + iMaxClass.length
                            + Messages.getString("JEPClassifier.and") + iMaxOther.length); //$NON-NLS-1$

            // pozostawienie wartosci maksymalnych
            try {
                dsClass = new BinaryArrayReadOnlyDataset(dsClass, iMaxClass);
                dsOther = new BinaryArrayReadOnlyDataset(dsOther, iMaxOther);
            } catch (Exception e1) {
                throw new Error(e1);
            }

            System.out.println(Messages.getString("JEPClassifier.JEPgen")); //$NON-NLS-1$
            BinaryDataset mejeps = MDB_LLBorder(dsClass, dsOther,
                    getSharedMiner());
            System.gc();

            System.out.println(Messages.getString("JEPClassifier.mejepsFound") //$NON-NLS-1$
                    + mejeps.size());
            for (int i = 0; i < mejeps.size(); i++) {
                mejeps.setCursorAt(i);
                BinaryRule rule = new BinaryRule(asCopy, cl, mejeps);

//                rule.setSupportAndConfidence(new DatasetView(dsTrain, iClass));
                rule.setSupportAndConfidence(dsTrain);
                if (rule.getSupport() > getArgMinSupport()) {
                    try {
                        addRule(rule);
                    } catch (IncompatibleAttributeset e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
        this.sharedMiner = null;
        System.gc();
    }

    public boolean requiresLearning() {
        return !this.rulesRestored;
    }

}