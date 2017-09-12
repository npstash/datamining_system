//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// CalcutaleAccurancy.java (sed.data.clasf)
// Utworzono 2005-08-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Distribution;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.attributes.BinaryAttribute;
import com.nestor.cs.data.impl.attributes.ContinuousAttribute;
import com.nestor.cs.data.impl.attributes.DiscreteAttribute;
import com.nestor.cs.data.impl.attributes.NominalAttribute;
import com.nestor.cs.tasks.def.MultiResultTask;
import com.nestor.cs.tasks.impl.ConfigurableTaskHelper;
import com.nestor.cs.tasks.impl.datasources.ARFFDataSink;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class CalculateAccurancy extends ConfigurableTaskHelper implements
        MultiResultTask {

    public static class EvaluationResults {

        public static EvaluationResults sumProbabilities(EvaluationResults[] ers) {
            EvaluationResults res = new EvaluationResults();
            double ersLen = ers.length;
            for (int i = 0; i < ers.length; i++) {
                res.pOk += ers[i].pOk;
                res.pError += ers[i].pError;
                res.pUnknown += ers[i].pUnknown;
            }
            res.pOk /= ersLen;
            res.pError /= ersLen;
            res.pUnknown /= ersLen;
            return res;
        }

        public Distribution classDistribution = null;

        public Distribution errorDistribution = null;

        public ArrayList errorIndexes = null;

        public int n = 0;

        public int nError = 0;

        public int nOk = 0;

        public int nUnknown = 0;

        public double pError = 0;

        public double pOk = 0;

        public double pUnknown = 0;

        public Distribution unknownDistribution = null;

        public ArrayList unknownIndexes = null;

    }

    public static EvaluationResults evaluate(ReadOnlyDataset origDs,
            ReadOnlyDataset clasfDs) {
        EvaluationResults er = new EvaluationResults();

        er.n = origDs.size();
        er.nError = 0;
        er.nUnknown = 0;

        er.errorIndexes = new ArrayList();
        er.unknownIndexes = new ArrayList();

        int origClIndex = origDs.getClassLabelIndex();
        int clasfClIndex = clasfDs.getClassLabelIndex();
        Attribute origClAttr = origDs.getAttribute(origClIndex);
        Attribute clasfClAttr = clasfDs.getAttribute(clasfClIndex);

        if (DiscreteAttribute.class.isAssignableFrom(origClAttr.getClass())) {
            er.classDistribution = Distribution
                    .createDistribution((DiscreteAttribute) origClAttr);
            er.errorDistribution = Distribution
                    .createDistribution(er.classDistribution.getLabels());
            er.unknownDistribution = Distribution
                    .createDistribution(er.classDistribution.getLabels());
        }

        for (int i = 0; i < er.n; i++) {
            clasfDs.setCursorAt(i);
            Object clasfClLabel = clasfDs.getValue(clasfClIndex);
            origDs.setCursorAt(i);
            Object origClLabel = origDs.getValue(origClIndex);

            boolean equal = Attributes
                    .areLabelsEqual(origClLabel, clasfClLabel);

            if (!equal) {
                er.nError++;
                er.errorIndexes.add(new Integer(i));
                if (clasfClLabel == null) {
                    er.nUnknown++;
                    er.unknownIndexes.add(new Integer(i));
                }
            } else {
                er.nOk++;
            }

            if ((origClLabel != null) && (er.classDistribution != null)) {
                if (!equal)
                    er.errorDistribution.modifyCount(origClLabel, 1);
                if (clasfClLabel == null)
                    er.unknownDistribution.modifyCount(origClLabel, 1);
                er.classDistribution.modifyCount(origClLabel, 1);
            }
        }

        // b��dy
        er.pOk = (double) er.nOk / (double) er.n;
        er.pError = (double) er.nError / (double) er.n;
        er.pUnknown = (double) er.nUnknown / (double) er.n;

        return er;
    }

    public final Argument argClassifier = new Argument();

    public final Argument argOrigTestingDs = new Argument();

    private PlainDocument document;

    private TaskResult[] faces;

    public CalculateAccurancy() {
        super();

        this.argOrigTestingDs.setName(Messages
                .getString("CalculateAccurancy.argOrigDs")); //$NON-NLS-1$
        this.argOrigTestingDs.setDescription(Messages
                .getString("CalculateAccurancy.argDescOrigDs")); //$NON-NLS-1$
        this.argOrigTestingDs.setValueType(ReadOnlyDataset.class);
        this.argOrigTestingDs.setRequired(false);
        getConfigurationArguments().addArgument(this.argOrigTestingDs);

        this.argClassifier.setName(Messages
                .getString("CalculateAccurancy.argClassifier")); //$NON-NLS-1$
        this.argClassifier.setDescription(Messages
                .getString("CalculateAccurancy.argDescClassifier")); //$NON-NLS-1$
        this.argClassifier.setValueType(AbstractClassifier.class);
        getConfigurationArguments().addArgument(this.argClassifier);

        this.faces = new TaskResult[1];
        this.document = new PlainDocument();
        this.faces[0] = new TaskResult(
                Messages.getString("CalculateAccurancy.4"), this, this.document); //$NON-NLS-1$
    }

    private void addDsInfo(String s, ReadOnlyDataset ds, boolean full) {
        appendText(s
                + Messages.getString("CalculateAccurancy.5") + " " + ds.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        appendText(Messages.getString("CalculateAccurancy.6") + ds.size()); //$NON-NLS-1$
        if (full) {
            appendText(Messages.getString("CalculateAccurancy.7") + ds.getAttributeCount()); //$NON-NLS-1$
            appendText(Messages.getString("CalculateAccurancy.8")); //$NON-NLS-1$
            appendText("\t" //$NON-NLS-1$
                    + Messages.getString("CalculateAccurancy.10") //$NON-NLS-1$
                    + Attributes.getAttributeTypeCount(ds,
                            ContinuousAttribute.class));
            appendText("\t" //$NON-NLS-1$
                    + Messages.getString("CalculateAccurancy.12") //$NON-NLS-1$
                    + Attributes.getAttributeTypeCount(ds,
                            NominalAttribute.class));
            appendText("\t" //$NON-NLS-1$
                    + Messages.getString("CalculateAccurancy.14") //$NON-NLS-1$
                    + Attributes.getAttributeTypeCount(ds,
                            BinaryAttribute.class));
            appendText(Messages.getString("CalculateAccurancy.15") //$NON-NLS-1$
                    + ds.getAttribute(ds.getClassLabelIndex()));
            appendText(""); //$NON-NLS-1$
        }
    }

    private void appendText(String s) {
        AttributeSet as = this.document.getDefaultRootElement().getAttributes();
        try {
            this.document.insertString(this.document.getLength(), s + "\n", as); //$NON-NLS-1$
        } catch (BadLocationException e) {
        }
    }

    public AbstractClassifier getArgClassifier() {
        return (AbstractClassifier) this.argClassifier.getValue();
    }

    public ReadOnlyDataset getArgOrigTestDataset() {
        if (this.argOrigTestingDs.getValue() == null) {
            AbstractClassifier ac = (AbstractClassifier) this.argClassifier
                    .getValue();
            return ac.getArgConvertedDataset();
        } else {
            return (ReadOnlyDataset) this.argOrigTestingDs.getValue();
        }
    }

    public TaskResult[] getTaskResults() {
        return this.faces;
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();
        try {
            this.document.remove(0, this.document.getLength());
        } catch (BadLocationException e1) {
        }

        ReadOnlyDataset origDs = getArgOrigTestDataset();
        ReadOnlyDataset clasfDs = getArgClassifier();
        try {
            IncompatibleAttributeset.checkLength(origDs, clasfDs);
        } catch (IncompatibleAttributeset e) {
            new ProcessingException(e);
        }

        if (origDs.size() != clasfDs.size()) {
            appendText(Messages.getString("CalculateAccurancy.18")); //$NON-NLS-1$
            appendText(Messages.getString("CalculateAccurancy.0") //$NON-NLS-1$
                    + origDs.size());
            appendText(Messages.getString("CalculateAccurancy.20") //$NON-NLS-1$
                    + clasfDs.size());
            return;
        }

        int origClIndex = origDs.getClassLabelIndex();
        int clasfClIndex = clasfDs.getClassLabelIndex();
        Attribute origClAttr = origDs.getAttribute(origClIndex);
        Attribute clasfClAttr = clasfDs.getAttribute(clasfClIndex);

        if (!origClAttr.equals(clasfClAttr)) {
            appendText(Messages.getString("CalculateAccurancy.21")); //$NON-NLS-1$
            appendText(Messages.getString("CalculateAccurancy.22") + origClAttr); //$NON-NLS-1$
            appendText(Messages.getString("CalculateAccurancy.23") + clasfClAttr); //$NON-NLS-1$
            return;
        }

        EvaluationResults er = evaluate(origDs, clasfDs);

        appendText(Messages.getString("CalculateAccurancy.1") //$NON-NLS-1$
                + getArgClassifier().getClass().getSimpleName());
        addDsInfo(
                Messages.getString("CalculateAccurancy.24"), getArgClassifier().getArgTrainingDataset(), true); //$NON-NLS-1$
        addDsInfo(Messages.getString("CalculateAccurancy.25"), origDs, false); //$NON-NLS-1$

        appendText(Messages.getString("CalculateAccurancy.26") + percentProb(er.pOk) + "% (" //$NON-NLS-1$ //$NON-NLS-2$
                + (er.nOk) + ")"); //$NON-NLS-1$
        appendText("\t" + Messages.getString("CalculateAccurancy.30") + "\t\t\t: " + percentProb(er.pError) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + "% (" + er.nError + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        appendText("\t" + Messages.getString("CalculateAccurancy.35") + percentProb(er.pUnknown) //$NON-NLS-1$ //$NON-NLS-2$
                + "% (" + er.nUnknown + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        appendText(""); //$NON-NLS-1$

        if (er.classDistribution != null) {
            appendText(Messages.getString("CalculateAccurancy.39")); //$NON-NLS-1$
            for (Iterator iter = er.classDistribution.getLabels().iterator(); iter
                    .hasNext();) {
                Object clLabel = iter.next();
                appendText(Messages.getString("CalculateAccurancy.40") + origClAttr.getName() + "=" //$NON-NLS-1$ //$NON-NLS-2$
                        + origClAttr.getNameForValue(clLabel));
                int cn = er.classDistribution.getCount(clLabel);
                if (cn != 0) {
                    int cnerr = er.errorDistribution.getCount(clLabel);
                    int cnunk = er.unknownDistribution.getCount(clLabel);
                    double cpOk = (double) (cn - cnerr) / (double) cn;
                    double cpErr = (double) cnerr / (double) cn;
                    double cpUnk = (double) cnunk / (double) cn;

                    appendText("\t" + Messages.getString("CalculateAccurancy.43") + "\t\t\t: " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            + percentProb(cpOk) + "% (" + (cn - cnerr) + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                    appendText("\t" + Messages.getString("CalculateAccurancy.48") + "\t\t\t: " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            + percentProb(cpErr) + "% (" + cnerr + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                    appendText("\t" + Messages.getString("CalculateAccurancy.53") //$NON-NLS-1$ //$NON-NLS-2$
                            + percentProb(cpUnk) + "% (" + cnunk + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                appendText(""); //$NON-NLS-1$
            }
        }

        appendText(""); //$NON-NLS-1$
        if (er.errorIndexes.size() > 0) {
            appendText(Messages.getString("CalculateAccurancy.58")); //$NON-NLS-1$
            for (int e = 0; e < er.errorIndexes.size(); e++) {
                Integer in = (Integer) er.errorIndexes.get(e);
                if (er.unknownIndexes.contains(in))
                    continue;
                int i = in.intValue();
                clasfDs.setCursorAt(i);
                origDs.setCursorAt(i);
                Object clasfClLabel = clasfDs.getValue(clasfClIndex);
                String s = i + ". " + ARFFDataSink.itemsetToARFFString(origDs); //$NON-NLS-1$
                s += "  " + Messages.getString("CalculateAccurancy.61") //$NON-NLS-1$ //$NON-NLS-2$
                        + origClAttr.getNameForValue(clasfClLabel);
                appendText(s);
            }
        }

        appendText(""); //$NON-NLS-1$
        if (er.unknownIndexes.size() > 0) {
            appendText(Messages.getString("CalculateAccurancy.63")); //$NON-NLS-1$
            for (int e = 0; e < er.unknownIndexes.size(); e++) {
                Integer in = (Integer) er.unknownIndexes.get(e);
                int i = in.intValue();
                clasfDs.setCursorAt(i);
                origDs.setCursorAt(i);
                Object clasfClLabel = clasfDs.getValue(clasfClIndex);
                String s = i + ". " + ARFFDataSink.itemsetToARFFString(origDs); //$NON-NLS-1$
                appendText(s);
            }
        }
    }

    private double percentProb(double p) {
        int pb = (int) (p * 10000d);
        return (pb) / 100d;
    }

}