//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// OldCppClassifier.java (sed.data.clasf)
// Utworzono 2005-08-08 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.cpp;

import java.io.File;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.Rule;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;
import com.nestor.cs.tasks.impl.clasf.cpp.CppClassifier.PrintInput;
import com.nestor.cs.tasks.impl.clasf.jep.JEPClassifier;
import com.nestor.cs.tasks.impl.dataset.ArrayListDataset;
import com.nestor.cs.tasks.impl.datasources.OldDatasets;
import com.nestor.cs.util.proc.ProcessingException;

public class JEP_Cpp extends JEPClassifier {

    public static final String JEP_EXE_FILE = "cpp/cJEP.exe"; //$NON-NLS-1$

    @Override
    public void learn(ReadOnlyDataset dsTrain)
            throws MissingClassLabelAttribute, InvalidDataset,
            ProcessingException {
        // nie wykonuje uczenia faktycznie, lecz sprawdza
        // wszystkie warunki
        super.learn(new ArrayListDataset(dsTrain));
        AttributeSet asCopy = getLearningContext().getAttributeSet();

        File dir = new File(CppClassifier.TEMP_DIR);
        dir.mkdir();
        if (!dir.exists())
            throw new ProcessingException(Messages
                    .getString("JEP_Cpp.cannotCreateDir")); //$NON-NLS-1$
        dir.deleteOnExit();

        try {
            File tempDs = File.createTempFile("jep-ds", ".dataset", dir); //$NON-NLS-1$ //$NON-NLS-2$
            tempDs.deleteOnExit();
            File tempMejeps = File.createTempFile("jep-mejep", ".dataset", dir); //$NON-NLS-1$ //$NON-NLS-2$
            tempMejeps.deleteOnExit();

            OldDatasets.saveOldDataset(tempDs, dsTrain);
            String[] cmdArray = new String[3];
            cmdArray[0] = JEP_EXE_FILE;
            cmdArray[1] = tempDs.getPath();
            cmdArray[2] = tempMejeps.getPath();

            System.out
                    .println(Messages.getString("JEP_Cpp.starting") + JEP_EXE_FILE); //$NON-NLS-1$
            Process p = Runtime.getRuntime().exec(cmdArray);
            PrintInput print = new PrintInput(p);
            int res = p.waitFor();
            print.run = false;
            if (res != 0) {
                throw new ProcessingException(JEP_EXE_FILE
                        + Messages.getString("JEP_Cpp.terminated") + res); //$NON-NLS-1$
            } else {
                System.out.println(JEP_EXE_FILE
                        + Messages.getString("JEP_Cpp.ended")); //$NON-NLS-1$
            }

            DefaultAttributeSet as = new DefaultAttributeSet();
            ReadOnlyDataset mejeps = OldDatasets.loadOldDataset(as, tempMejeps,
                    asCopy.getClassLabelIndex());
            int clAttrIndex = mejeps.getClassLabelIndex();
            Attribute clAttr = mejeps.getAttribute(clAttrIndex);

            for (int i = 0; i < mejeps.size(); i++) {
                mejeps.setCursorAt(i);
                Object clLabel = mejeps.getValue(clAttrIndex);
                int cl = clAttr.getAllowedValueIndex(clLabel);

                Rule rule = new Rule(asCopy, cl);
                for (int b = 0; b < mejeps.getAttributeCount(); b++) {
                    if (b == mejeps.getClassLabelIndex())
                        continue;
                    Boolean bool = (Boolean) mejeps.getValue(b);
                    if (bool.booleanValue()) {
                        Test t = mejeps.getAttribute(b).createTest(
                                new Boolean(true), Test.EQUAL);
                        rule.setTest(b, t);
                    }
                }
                rule.setSupportAndConfidence(dsTrain);
                if (rule.getSupport() > getArgMinSupport()) {
                    addRule(rule);
                }
            }

        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }
}