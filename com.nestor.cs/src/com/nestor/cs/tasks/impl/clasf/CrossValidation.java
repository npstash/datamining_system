//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// CrossValidation.java (sed.data.clasf)
// Utworzono 2005-08-07 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.attributes.BinaryAttribute;
import com.nestor.cs.data.impl.attributes.ContinuousAttribute;
import com.nestor.cs.data.impl.attributes.NominalAttribute;
import com.nestor.cs.data.impl.datasets.DatasetView;
import com.nestor.cs.tasks.def.Classifier;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.tasks.def.MultiResultTask;
import com.nestor.cs.tasks.impl.ConfigurableTaskHelper;
import com.nestor.cs.tasks.impl.dataset.ArrayListDataset;
import com.nestor.cs.util.event.StateEvent;
import com.nestor.cs.util.event.StateListener;
import com.nestor.cs.util.event.UpdateEvent;
import com.nestor.cs.util.event.UpdateListener;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.proc.Task;
import com.nestor.cs.util.util.ArrayUtils;
import com.nestor.cs.util.util.Timer;
import com.nestor.cs.util.util.param.Argument;
import com.nestor.cs.util.util.param.Parameter;

public class CrossValidation extends ConfigurableTaskHelper implements
        MultiResultTask, UpdateListener, StateListener {

    public final Parameter argClassifier = new Parameter();

    public final Argument argDataset = new Argument();

    public final Argument argFolds = new Argument(new Integer(10));

    public final Argument argBlockClassifier = new Argument(new Boolean(false));

    private PlainDocument document;

    private TaskResult[] faces;

    public CrossValidation() {
        super();

        this.argDataset.setName(Messages.getString("CrossValidation.argTestDs")); //$NON-NLS-1$
        this.argDataset.setDescription(Messages
                .getString("CrossValidation.argDescTestDs")); //$NON-NLS-1$
        this.argDataset.setValueType(ReadOnlyDataset.class);
        this.argDataset.setRequired(false);
        getConfigurationArguments().addArgument(this.argDataset);

        this.argClassifier.setName(Messages
                .getString("CrossValidation.argClassifier")); //$NON-NLS-1$
        this.argClassifier.setDescription(Messages
                .getString("CrossValidation.argDescClassifier")); //$NON-NLS-1$
        this.argClassifier.setValueType(Classifier.class);
        this.argClassifier.addUpdateListener(this);
        getConfigurationArguments().addArgument(this.argClassifier);

        this.argFolds.setName(Messages.getString("CrossValidation.argFolds")); //$NON-NLS-1$
        this.argFolds.setDescription(Messages
                .getString("CrossValidation.argDescFolds")); //$NON-NLS-1$
        this.argFolds.setLowBound(new Integer(1));
        this.argFolds.setHighBound(new Integer(15));
        getConfigurationArguments().addArgument(this.argFolds);

        this.argBlockClassifier.setName(Messages
                .getString("CrossValidation.argBlock")); //$NON-NLS-1$
        this.argBlockClassifier.setDescription(Messages
                .getString("CrossValidation.argDescBlock")); //$NON-NLS-1$
        this.argBlockClassifier.setRequired(false);
        getConfigurationArguments().addArgument(this.argBlockClassifier);

        this.faces = new TaskResult[1];
        this.document = new PlainDocument();
        this.faces[0] = new TaskResult(
                Messages.getString("CrossValidation.6"), this, this.document); //$NON-NLS-1$
    }

    public boolean isClassifierBlocking() {
        if (this.argBlockClassifier.getValue() == null)
            return true;
        else
            return ((Boolean) this.argBlockClassifier.getValue()).booleanValue();
    }

    private void addDsInfo(ReadOnlyDataset ds, boolean full) {
        appendText(Messages.getString("CrossValidation.7") + " " + ds.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        appendText(Messages.getString("CrossValidation.8") + ds.size()); //$NON-NLS-1$
        if (full) {
            appendText(Messages.getString("CrossValidation.9") + ds.getAttributeCount()); //$NON-NLS-1$
            appendText(Messages.getString("CrossValidation.10")); //$NON-NLS-1$
            appendText(Messages.getString("CrossValidation.11") //$NON-NLS-1$
                    + Attributes.getAttributeTypeCount(ds,
                            ContinuousAttribute.class));
            appendText(Messages.getString("CrossValidation.12") //$NON-NLS-1$
                    + Attributes.getAttributeTypeCount(ds,
                            NominalAttribute.class));
            appendText(Messages.getString("CrossValidation.13") //$NON-NLS-1$
                    + Attributes.getAttributeTypeCount(ds,
                            BinaryAttribute.class));
            appendText(Messages.getString("CrossValidation.14") //$NON-NLS-1$
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

    public Classifier getArgClassifier() {
        return (Classifier) this.argClassifier.getValue();
    }

    public ReadOnlyDataset getArgOrigTestDataset() {
        if (this.argDataset.getValue() == null) {
            AbstractClassifier ac = (AbstractClassifier) this.argClassifier
                    .getValue();
            return ac.getArgConvertedDataset();
        } else {
            return (ReadOnlyDataset) this.argDataset.getValue();
        }
    }

    public TaskResult[] getTaskResults() {
        return this.faces;
    }

    @Override
    public void initProcess() throws ProcessingException {
        // nale�y zagwarantowa� odblokowanie niezale�nie
        // od niepowodze�
        unblockClassifier(this.currentAC);

        super.initProcess();

        Classifier c = (Classifier) this.argClassifier.getValue();
        ReadOnlyDataset ds = (ReadOnlyDataset) this.argDataset.getValue();
        if (ds == null) {
            if (c instanceof AbstractClassifier) {
                AbstractClassifier ac = (AbstractClassifier) c;
                ds = ac.getArgConvertedDataset();
            }
        }
        if (ds == null)
            throw new MissingArgumentValue(this.argDataset);

        int folds = ((Integer) this.argFolds.getValue()).intValue();
        int n = ds.size();
        //        int foldSize = (int) Math.ceil(((double) n) / ((double) folds));
        double foldSize = ((double) n) / ((double) folds);

        ArrayList allIndexes = new ArrayList(n);
        for (int i = 0; i < n; i++) {
            allIndexes.add(new Integer(i));
        }

        Collections.shuffle(allIndexes);
        CalculateAccurancy.EvaluationResults[] res = new CalculateAccurancy.EvaluationResults[folds];

        appendText(Messages.getString("CrossValidation.5") //$NON-NLS-1$
                + getArgClassifier().getClass().getSimpleName());

        Timer t = new Timer();
        t.i(0).start();

        for (int f = 0; f < folds; f++) {
            ArrayList alTrain = new ArrayList();
            ArrayList alTest = new ArrayList();
            int foldStart = (int) (f * foldSize);
            int foldEnd = (int) ((f + 1) * foldSize);
            for (int i = 0; i < allIndexes.size(); i++) {
                int x = ((Integer) allIndexes.get(i)).intValue();
                if ((i >= foldStart) && (i < foldEnd))
                    alTest.add(new Integer(x));
                else
                    alTrain.add(new Integer(x));
            }
            int[] iTrain = ArrayUtils.createIntArray(alTrain);
            int[] iTest = ArrayUtils.createIntArray(alTest);
            System.out.println(Messages.getString("CrossValidation.2") + f); //$NON-NLS-1$
            System.out
                    .println(Messages.getString("CrossValidation.3") + iTrain.length); //$NON-NLS-1$
            System.out
                    .println(Messages.getString("CrossValidation.4") + iTest.length); //$NON-NLS-1$

            DatasetView dsTrain = new DatasetView(ds, iTrain);
            DatasetView dsTest = new DatasetView(ds, iTest);

            ArrayListDataset classified = new ArrayListDataset(dsTest, true);

            try {
                c.learnAndClassify(dsTrain, classified);
            } catch (IncompatibleAttributeset e1) {
                throw new ProcessingException(e1);
            }

            res[f] = CalculateAccurancy.evaluate(dsTest, classified);
        }

        t.i(0).end();

        CalculateAccurancy.EvaluationResults er = CalculateAccurancy.EvaluationResults
                .sumProbabilities(res);

        try {
            this.document.remove(0, this.document.getLength());
        } catch (BadLocationException e) {
            throw new Error(e);
        }

        addDsInfo(ds, true);
        appendText(Messages.getString("CrossValidation.17") + percentProb(er.pOk) + "% (" //$NON-NLS-1$ //$NON-NLS-2$
                + (er.nOk) + ")"); //$NON-NLS-1$
        appendText(Messages.getString("CrossValidation.1") + percentProb(er.pError) + "% (" //$NON-NLS-1$ //$NON-NLS-2$
                + er.nError + ")"); //$NON-NLS-1$
        appendText(Messages.getString("CrossValidation.0") + percentProb(er.pUnknown) + "% (" //$NON-NLS-1$ //$NON-NLS-2$
                + er.nUnknown + ")"); //$NON-NLS-1$
        long secs = t.i(0).getTotalTime() / 1000;
        long ms = (t.i(0).getTotalTime() % 1000) / 100;
        appendText("Czas ca�kowity[s]: " + secs + "." + ms);
    }

    private double percentProb(double p) {
        int pb = (int) (p * 10000d);
        return (pb) / 100d;
    }

    private void blockClassifier(AbstractClassifier ac) {
        if (!isClassifierBlocking())
            return;
        if (ac != null)
            ac.setProcessState(Task.USER1_RUN);
    }

    private void unblockClassifier(AbstractClassifier ac) {
        if (ac != null)
            if (ac.getProcessState() == Task.USER1_RUN) {
                ac.setProcessState(Task.NOTINITED);
            }
    }

    private AbstractClassifier currentAC = null;

    public void stateChanged(StateEvent e) {
        if (e.getIterativeProcess() != this.currentAC)
            return;
        if (e.getNewState() == Task.INITIALIZED) {
            blockClassifier(this.currentAC);
        }
    }

    public synchronized void update(UpdateEvent e) {
        if (e.getSource() == this.argClassifier) {
            Classifier cOld = (Classifier) e.getOldValue();
            Classifier cNew = (Classifier) e.getNewValue();

            if (cOld instanceof AbstractClassifier) {
                AbstractClassifier ac = (AbstractClassifier) cOld;
                ac.removeStateListener(this);
            }

            if (cNew instanceof AbstractClassifier) {
                AbstractClassifier ac = (AbstractClassifier) cNew;
                this.currentAC = ac;
                ac.addStateListener(this);
            }
        }
    }

}