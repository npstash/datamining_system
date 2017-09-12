//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DeEP_Cpp.java (sed.data.clasf)
// Utworzono 2005-08-09 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.cpp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.Dataset;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;
import com.nestor.cs.tasks.impl.clasf.AbstractClassifier;
import com.nestor.cs.tasks.impl.dataset.ArrayListDataset;
import com.nestor.cs.tasks.impl.datasources.OldDatasets;
import com.nestor.cs.util.proc.ProcessingException;

public class CppClassifier extends AbstractClassifier {

    public static class PrintInput extends Thread {

        private Process proc;

        public boolean run = true;

        public PrintInput(Process p) {
            super();
            this.proc = p;
            start();
        }

        @Override
        public void run() {
            InputStream input = this.proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(isr);
            try {
                while (this.run) {
                    String s = br.readLine();
                    if (s != null)
                        System.out.println(s);
                    else
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static final String TEMP_DIR = "sed_temp"; //$NON-NLS-1$

    protected String[] additionalParams;

    private ArrayListDataset cachedDataset = null;

    protected String exeFile;

    public CppClassifier(String exeFile) {
        super();
        this.exeFile = exeFile;
        this.additionalParams = new String[0];
    }

    public CppClassifier(String exeFile, String[] additionalParams) {
        super();
        this.exeFile = exeFile;
        this.additionalParams = additionalParams;
    }

    public Object classify(ReadOnlyDataset dsTrain, ReadOnlyItemset it)
            throws IncompatibleAttributeset {
        if (this.cachedDataset == null) {
            throw new IncompatibleAttributeset(Messages
                    .getString("CppClassifier.learnRequired")); //$NON-NLS-1$
        }
        int clAttrIndex = it.getClassLabelIndex();
        for (int i = 0; i < this.cachedDataset.size(); i++) {
            this.cachedDataset.setCursorAt(i);
            //            if (Attributes.compareItemsetsByValueIndexes(it, cachedDataset))
            // {
            if (Attributes.compareItemsets(it, this.cachedDataset)) {
                return this.cachedDataset.getValue(clAttrIndex);
            }
        }
        throw new IncompatibleAttributeset(Messages
                .getString("CppClassifier.learnError")); //$NON-NLS-1$
    }

    @Override
    public void learn(ReadOnlyDataset dsTrain) throws ProcessingException {
        this.cachedDataset = new ArrayListDataset(getArgConvertedDataset(), true);
        try {
            learnAndClassify(dsTrain, this.cachedDataset);
        } catch (IncompatibleAttributeset e) {
            throw new ProcessingException(e);
        }
    }

    @Override
    public void learnAndClassify(ReadOnlyDataset dsTrain, Dataset toClasf)
            throws IncompatibleAttributeset, ProcessingException {
        super.learn(dsTrain);

        try {
            Attributes.checkForBinaryAttributes(dsTrain, true);
        } catch (IncompatibleAttributeset e) {
            throw new ProcessingException(e);
        }

        AttributeSet asCopy = getLearningContext().getAttributeSet();

        File dir = new File(CppClassifier.TEMP_DIR);
        dir.mkdir();
        if (!dir.exists())
            throw new ProcessingException(Messages
                    .getString("CppClassifier.cannotCreateDir")); //$NON-NLS-1$
        dir.deleteOnExit();

        try {
            File fTrainDs = File.createTempFile("cpp-train", ".dataset", dir); //$NON-NLS-1$ //$NON-NLS-2$
            File fTestDs = File.createTempFile("cpp-test", ".dataset", dir); //$NON-NLS-1$ //$NON-NLS-2$
            File fClasfDs = File.createTempFile("cpp-clasf", ".dataset", dir); //$NON-NLS-1$ //$NON-NLS-2$

            OldDatasets.saveOldDataset(fTrainDs, dsTrain);
            OldDatasets.saveOldDataset(fTestDs, toClasf);

            String[] cmdArray = new String[4 + this.additionalParams.length];
            cmdArray[0] = this.exeFile;
            cmdArray[1] = fTrainDs.getPath();
            cmdArray[2] = fTestDs.getPath();
            cmdArray[3] = fClasfDs.getPath();
            for (int i = 4; i < cmdArray.length; i++) {
                cmdArray[i] = this.additionalParams[i - 4];
            }
            System.out
                    .println(Messages.getString("CppClassifier.Starting") + this.exeFile); //$NON-NLS-1$
            Process p = Runtime.getRuntime().exec(cmdArray);
            PrintInput print = new PrintInput(p);

            int res = p.waitFor();

            print.run = false;
            if (res != 0) {
                throw new ProcessingException(this.exeFile
                        + Messages.getString("CppClassifier.terminated") + res); //$NON-NLS-1$
            } else {
                System.out.println(this.exeFile
                        + Messages.getString("CppClassifier.ended")); //$NON-NLS-1$
            }

            DefaultAttributeSet as = new DefaultAttributeSet();
            ReadOnlyDataset classifiedDataset = OldDatasets.loadOldDataset(as,
                    fClasfDs, asCopy.getClassLabelIndex());

            int clAttrIndex1 = toClasf.getClassLabelIndex();
            int clAttrIndex2 = classifiedDataset.getClassLabelIndex();

            Attribute clAttr1 = toClasf.getAttribute(clAttrIndex1);
            Attribute clAttr2 = classifiedDataset.getAttribute(clAttrIndex2);

            for (int i = 0; i < classifiedDataset.size(); i++) {
                toClasf.setCursorAt(i);
                classifiedDataset.setCursorAt(i);
                Object clasfValue = classifiedDataset.getValue(clAttrIndex2);

                int clLabelIndex = clAttr2.getAllowedValueIndex(clasfValue);
                Object clasfValue2 = clAttr1.getAllowedValue(clLabelIndex);

                toClasf.setValue(clAttrIndex1, clasfValue2);
            }

        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }

    public boolean requiresLearning() {
        return true;
    }
}