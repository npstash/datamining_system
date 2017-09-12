//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// NaiveBayes.java (sed.data.clasf)
// Utworzono 2005-08-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.bayes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.CountMatrix;
import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ItemsetIndexesSelector;
import com.nestor.cs.data.def.ProbDistribution;
import com.nestor.cs.data.def.ProbMatrix;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.def.TestSet;
import com.nestor.cs.data.impl.attributes.AbstractAttribute;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.attributes.ContinuousAttribute;
import com.nestor.cs.data.impl.attributes.DiscreteAttribute;
import com.nestor.cs.data.impl.attributes.FuzzyAttribute;
import com.nestor.cs.data.impl.datasets.DatasetView;
import com.nestor.cs.data.impl.datasets.SelectedItemsets;
import com.nestor.cs.data.impl.tests.TestSelector;
import com.nestor.cs.data.impl.tests.TestSetSelector;
import com.nestor.cs.tasks.def.LearningContext;
import com.nestor.cs.tasks.impl.clasf.AbstractClassifier;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class NaiveBayes extends AbstractClassifier {

    public static void appendText(Document doc, String s) {
        try {
            doc.insertString(doc.getLength(), s + "\n", //$NON-NLS-1$
                    doc.getDefaultRootElement().getAttributes());
            System.out.println(s);
        } catch (BadLocationException e) {
        }
    }

    public static void printCountMatrix(CountMatrix cm, Attribute aX,
            Attribute aY, Document doc) {
        String h = "\t"; //$NON-NLS-1$
        for (Iterator iterX = cm.getLabelsX().iterator(); iterX.hasNext();) {
            Object labelX = iterX.next();
            h += aX.getNameForValue(labelX) + "\t"; //$NON-NLS-1$
        }
        appendText(doc, h);

        for (Iterator iterY = cm.getLabelsY().iterator(); iterY.hasNext();) {
            Object labelY = iterY.next();

            String l = aY.getNameForValue(labelY) + "\t"; //$NON-NLS-1$
            for (Iterator iterX = cm.getLabelsX().iterator(); iterX.hasNext();) {
                Object labelX = iterX.next();
                l += cm.getCount(labelX, labelY) + "\t"; //$NON-NLS-1$
            }
            appendText(doc, l);
        }
        appendText(doc, ""); //$NON-NLS-1$
    }

    public static void printDistribution(Distribution d, Attribute a,
            Document doc) {
        appendText(doc, Messages.getString("NaiveBayes.label") + //$NON-NLS-1$ 
                "\t\t" + //$NON-NLS-1$
                Messages.getString("NaiveBayes.repeats")); //$NON-NLS-1$

        for (Iterator iter = d.getLabels().iterator(); iter.hasNext();) {
            Object label = iter.next();
            appendText(doc, a.getNameForValue(label) + "\t\t" //$NON-NLS-1$
                    + d.getCount(label));
        }
        appendText(doc, ""); //$NON-NLS-1$
    }

    public static void printProbDistribution(ProbDistribution d, Attribute a,
            Document doc) {
        appendText(
                doc,
                Messages.getString("NaiveBayes.label") + "\t\t" + Messages.getString("NaiveBayes.value")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        for (Iterator iter = d.getLabels().iterator(); iter.hasNext();) {
            Object label = iter.next();
            appendText(doc, a.getNameForValue(label) + "\t\t" //$NON-NLS-1$
                    + d.getProbability(label));
        }
        appendText(doc, ""); //$NON-NLS-1$
    }

    public static void printProbMatrix(ProbMatrix cm, Attribute aX,
            Attribute aY, Document doc) {
        String h = "\t"; //$NON-NLS-1$
        for (Iterator iterX = cm.getLabelsX().iterator(); iterX.hasNext();) {
            Object labelX = iterX.next();
            h += aX.getNameForValue(labelX) + "\t"; //$NON-NLS-1$
        }
        appendText(doc, h);

        for (Iterator iterY = cm.getLabelsY().iterator(); iterY.hasNext();) {
            Object labelY = iterY.next();

            String l = aY.getNameForValue(labelY) + "\t"; //$NON-NLS-1$
            for (Iterator iterX = cm.getLabelsX().iterator(); iterX.hasNext();) {
                Object labelX = iterX.next();
                l += cm.getProbability(labelX, labelY) + "\t"; //$NON-NLS-1$
            }
            appendText(doc, l);
        }
        appendText(doc, ""); //$NON-NLS-1$
    }

    private Distribution aprioriProbs = null;

    public final Argument argEstimation = new Argument(new Integer(0));

    public final Argument argM = new Argument(new Double(10));

    private ArrayList classLabels = null;

    private ConditionalEstimator[] estimators = null;

    public NaiveBayes() {
        super();

        this.argEstimation.setName(Messages.getString("NaiveBayes.argEstimation")); //$NON-NLS-1$
        this.argEstimation.setLowBound(new Integer(0));
        this.argEstimation.setHighBound(new Integer(2));
        this.argEstimation.setDescription(Messages
                .getString("NaiveBayes.argDescEstimation")); //$NON-NLS-1$
        getConfigurationArguments().addArgument(this.argEstimation);

        this.argM.setName(Messages.getString("NaiveBayes.argM")); //$NON-NLS-1$
        this.argM.setDescription(Messages.getString("NaiveBayes.argDescM")); //$NON-NLS-1$
        this.argM.setLowBound(new Double(0.01));
        this.argM.setRequired(false);
        getConfigurationArguments().addArgument(this.argM);
    }

    public Object classify(ReadOnlyDataset dsTrain, ReadOnlyItemset it)
            throws IncompatibleAttributeset {
        int clAttrIndex = dsTrain.getClassLabelIndex();
        double n = this.aprioriProbs.getTotalExamples();

        Object maxClassLabel = null;
        double maxProb = 0;
        double maxAprioriProb = 0;
//        int maxCnt = 0;
        for (int c = 0; c < this.classLabels.size(); c++) {
            Object clLabel = this.classLabels.get(c);
            double nc = this.aprioriProbs.getCount(clLabel);
            double pca = nc / n;
            for (int i = 0; i < this.estimators.length; i++) {
                if (i == clAttrIndex)
                    continue;
                Object value = it.getValue(i);
                if (value == null)
                    continue;
                double pca_i = this.estimators[i].getProbability(clLabel, value);
                pca *= pca_i;
                if (pca == 0)
                    break;
            }
            if ((pca > maxProb) || ((pca == maxProb) && (nc >= maxAprioriProb))){
                maxClassLabel = clLabel;
                maxProb = pca;
                maxAprioriProb = nc;
//                maxCnt = 1;
            } 
//            else if (pca == maxProb) {
//                maxCnt++;
//            }
        }
//        if (maxCnt > 1)
//            maxClassLabel = null;
        return maxClassLabel;
    }

    @Override
    public void learn(ReadOnlyDataset dsTrain) throws ProcessingException {
        super.learn(dsTrain);
        LearningContext lc = getLearningContext();

        // indeks atrybutu klasy, atrybut klasy oraz
        // etykiety atrybutu klasy b�d� potrzebne dalej
        int clAttrIndex = dsTrain.getClassLabelIndex();
        Attribute clAttr = dsTrain.getAttribute(clAttrIndex);

        if (clAttr.getAllowedValuesCount() == Attribute.INFINITE_VALUES) {
            String key = "SplittingDiscretizer.classAttributeMustHaveFiniteLabels"; //$NON-NLS-1$
            throw new ProcessingException(Messages.getString(key));
        }

        this.classLabels = new ArrayList(clAttr.getAllowedValuesCount());
        for (int i = 0; i < clAttr.getAllowedValuesCount(); i++) {
            this.classLabels.add(clAttr.getAllowedValue(i));
        }

        // pozbywam si� rekord�w o warto�ciach nieznanych
        TestSet nonNullTS = new TestSet(dsTrain);
        for (int a = 0; a < dsTrain.getAttributeCount(); a++) {
            Attribute attr = dsTrain.getAttribute(a);
            nonNullTS.setTest(a, attr.createTest(null, Test.INEQUAL));
        }
        TestSetSelector nonNullTSS = new TestSetSelector(nonNullTS);
        ItemsetIndexesSelector nonNullIIS = new SelectedItemsets(nonNullTSS);
        int[] nonNullI = nonNullIIS.selectItemsetIndexes(dsTrain);
        DatasetView dsNonNull = new DatasetView(dsTrain, nonNullI);

        // rodzaj estymacji
        int estType = ((Integer) this.argEstimation.getValue()).intValue();
        Double M = (Double) this.argM.getValue();

        // obliczenie prawdopodobie�stw P(C)
        this.aprioriProbs = Distribution.createDistribution(dsNonNull, clAttrIndex);
        appendText(lc.getDocument(), Messages.getString("NaiveBayes.25")); //$NON-NLS-1$
        printDistribution(this.aprioriProbs, clAttr, lc.getDocument());

        this.estimators = new ConditionalEstimator[dsNonNull.getAttributeCount()];
        for (int a = 0; a < this.estimators.length; a++) {
            // atrybut klasy pomijam
            if (a == clAttrIndex)
                continue;
            Attribute attr = dsNonNull.getAttribute(a);
            appendText(lc.getDocument(), Messages.getString("NaiveBayes.26") //$NON-NLS-1$
                    + attr.getName());

            if (attr instanceof ContinuousAttribute) {
                ProbDistribution dMean = ProbDistribution
                        .createProbDistribution(this.classLabels);
                ProbDistribution dDev = ProbDistribution
                        .createProbDistribution(this.classLabels);
                for (Iterator iter = this.classLabels.iterator(); iter.hasNext();) {
                    Object clLabel = iter.next();

                    // do utworzenia estymatora nale�y wybra�
                    // tylko te przyk�ady ze zbioru ucz�cego,
                    // kt�re maj� etykiet� klasy 'clLabel'.
                    // Utworzenie testu wybieraj�cego rekord:
                    Test t = clAttr.createTest(clLabel, Test.EQUAL);

                    // utworzenie selektora do rekord�w:
                    TestSelector selector = new TestSelector(clAttrIndex, t);

                    // utworzenie selektora indeks�w:
                    ItemsetIndexesSelector onlyThisClass = new SelectedItemsets(
                            selector);

                    // wybranie indeks�w rekord�w dla danej etykiety klasy
                    int[] indexes = onlyThisClass
                            .selectItemsetIndexes(dsNonNull);

                    // utworzenie widoku na ucz�cy zbi�r danych
                    DatasetView classView = new DatasetView(dsNonNull, indexes);

                    double mean = Attributes.getMeanValue(classView, a);
                    double dev = Attributes.getStandardDeviation(classView, a);
                    dMean.setProbability(clLabel, mean);
                    dDev.setProbability(clLabel, dev);
                }
                this.estimators[a] = new ContiuousEstimator(dMean, dDev);

                appendText(lc.getDocument(), Messages
                        .getString("NaiveBayes.27")); //$NON-NLS-1$
                appendText(lc.getDocument(), Messages
                        .getString("NaiveBayes.28")); //$NON-NLS-1$
                printProbDistribution(dMean, clAttr, lc.getDocument());
                appendText(lc.getDocument(), Messages
                        .getString("NaiveBayes.29")); //$NON-NLS-1$
                printProbDistribution(dDev, clAttr, lc.getDocument());

            } else if (attr instanceof FuzzyAttribute) {
                int nv = attr.getAllowedValuesCount();
                FuzzyAttribute fattr = (FuzzyAttribute) attr;
                FuzzyAttribute.FuzzyValue[] fvs = new FuzzyAttribute.FuzzyValue[nv];
                Integer[] intLabels = new Integer[nv];
                for (int v = 0; v < nv; v++) {
                    fvs[v] = (FuzzyAttribute.FuzzyValue) fattr
                            .getAllowedValue(v);
                    intLabels[v] = fvs[v].getDiscreteValueByMajority();
                }

                ProbMatrix pm = ProbMatrix.createProbMatrix(this.classLabels, Arrays
                        .asList(intLabels));

                // zebranie sumy prawdopodobie�stw
                for (int k = 0; k < dsNonNull.size(); k++) {
                    dsNonNull.setCursorAt(k);
                    FuzzyAttribute.FuzzyValue fv = (FuzzyAttribute.FuzzyValue) dsNonNull
                            .getValue(a);
                    Object clLabel = dsNonNull.getValue(clAttrIndex);

                    for (int v = 0; v < nv; v++) {
                        double memb = fv.getValueMembership(intLabels[v]);
                        pm.modifyProbability(clLabel, intLabels[v], memb);
                    }
                }
                if (M != null) {
                    this.estimators[a] = new FuzzyEstimator(pm, estType, M
                            .doubleValue());
                } else {
                    this.estimators[a] = new FuzzyEstimator(pm, estType);
                }

                appendText(lc.getDocument(), Messages
                        .getString("NaiveBayes.30")); //$NON-NLS-1$
                printProbMatrix(pm, clAttr, attr, lc.getDocument());

            } else if (attr instanceof DiscreteAttribute) {
                CountMatrix cm = CountMatrix.createCountMatrix(dsNonNull,
                        clAttrIndex, a);
                if (M != null) {
                    this.estimators[a] = new NominalEstimator(cm, estType, M
                            .doubleValue());
                } else {
                    this.estimators[a] = new NominalEstimator(cm, estType);
                }

                appendText(lc.getDocument(), Messages
                        .getString("NaiveBayes.31")); //$NON-NLS-1$
                printCountMatrix(cm, clAttr, attr, lc.getDocument());

            } // else
        } /// for attribute
    } // learn

    public boolean requiresLearning() {
        return true;
    }
}