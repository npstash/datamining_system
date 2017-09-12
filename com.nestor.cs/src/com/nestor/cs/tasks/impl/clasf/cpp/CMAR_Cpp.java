//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// CMAR_Cpp.java (sed.data.clasf.cppexe)
// Utworzono 2005-08-10 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.cpp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Dataset;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class CMAR_Cpp extends CppClassifier {

    public static final String CMAR_EXE_FILE = "cpp/cCMAR.exe"; //$NON-NLS-1$

    public final Argument argChi2TH = new Argument(new Double(3.84));

    public final Argument argConfDiffTH = new Argument(new Double(20));

    public final Argument argCovPrune = new Argument(new Boolean(true));

    public final Argument argMinConfidence = new Argument(new Double(50));

    public final Argument argMinCoverage = new Argument(new Integer(5));

    public final Argument argMinSupport = new Argument(new Double(5));

    public final Argument argRuleReduction = new Argument(new Boolean(false));

    private File fSets;

    public CMAR_Cpp() {
        super(CMAR_EXE_FILE);

        this.argMinSupport.setName(Messages.getString("CMAR_Cpp.argSuppTH")); //$NON-NLS-1$
        this.argMinSupport.setDescription(Messages
                .getString("CMAR_Cpp.argDescSuppTH")); //$NON-NLS-1$
        this.argMinSupport.setLowBound(new Double(0));
        this.argMinSupport.setHighBound(new Double(100));
        getConfigurationArguments().addArgument(this.argMinSupport);

        this.argMinConfidence.setName(Messages.getString("CMAR_Cpp.argConfTH")); //$NON-NLS-1$
        this.argMinConfidence.setDescription(Messages
                .getString("CMAR_Cpp.argDescConfTH")); //$NON-NLS-1$
        this.argMinConfidence.setLowBound(new Double(0));
        this.argMinConfidence.setHighBound(new Double(100));
        getConfigurationArguments().addArgument(this.argMinConfidence);

        this.argMinCoverage.setName(Messages.getString("CMAR_Cpp.argCovTH")); //$NON-NLS-1$
        this.argMinCoverage.setDescription(Messages
                .getString("CMAR_Cpp.argDescCovTH")); //$NON-NLS-1$
        this.argMinCoverage.setLowBound(new Integer(1));
        getConfigurationArguments().addArgument(this.argMinCoverage);

        this.argChi2TH.setName(Messages.getString("CMAR_Cpp.argChi2TH")); //$NON-NLS-1$
        this.argChi2TH.setDescription(Messages.getString("CMAR_Cpp.argDescChi2TH")); //$NON-NLS-1$
        this.argChi2TH.setLowBound(new Double(0));
        getConfigurationArguments().addArgument(this.argChi2TH);

        this.argConfDiffTH.setName(Messages.getString("CMAR_Cpp.argConfDiffTH")); //$NON-NLS-1$
        this.argConfDiffTH.setDescription(Messages
                .getString("CMAR_Cpp.argDescConfDiffTH")); //$NON-NLS-1$
        this.argConfDiffTH.setLowBound(new Double(0));
        this.argConfDiffTH.setHighBound(new Double(100));
        getConfigurationArguments().addArgument(this.argConfDiffTH);

        this.argRuleReduction.setName(Messages.getString("CMAR_Cpp.argRuleRed")); //$NON-NLS-1$
        this.argRuleReduction.setDescription(Messages
                .getString("CMAR_Cpp.argDescRuleRed")); //$NON-NLS-1$
        getConfigurationArguments().addArgument(this.argRuleReduction);

        this.argCovPrune.setName(Messages.getString("CMAR_Cpp.argPruneCov")); //$NON-NLS-1$
        this.argCovPrune.setDescription(Messages
                .getString("CMAR_Cpp.argDescPrunCov")); //$NON-NLS-1$
        getConfigurationArguments().addArgument(this.argCovPrune);

        try {
            this.fSets = File.createTempFile("sets", ".cmar"); //$NON-NLS-1$ //$NON-NLS-2$
            this.fSets.deleteOnExit();
            super.additionalParams = new String[] { this.fSets.getPath() };
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void learn(ReadOnlyDataset dsTrain) throws ProcessingException {
        printOptions();

        super.learn(dsTrain);
    }

    @Override
    public void learnAndClassify(ReadOnlyDataset dsTrain, Dataset toClasf)
            throws IncompatibleAttributeset, ProcessingException {
        printOptions();

        super.learnAndClassify(dsTrain, toClasf);
    }

    private void printOptions() throws ProcessingException {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(this.fSets));

            double supp = ((Double) this.argMinSupport.getValue()).doubleValue();
            double conf = ((Double) this.argMinConfidence.getValue()).doubleValue();
            int cov = ((Integer) this.argMinCoverage.getValue()).intValue();
            double chi2 = ((Double) this.argChi2TH.getValue()).doubleValue();
            double confDiffTH = ((Double) this.argConfDiffTH.getValue())
                    .doubleValue() / 100d;
            double suppDiff = 0;
            double confDiff = 0;
            int rulered = ((Boolean) this.argRuleReduction.getValue())
                    .booleanValue() ? 1 : 0;
            int covprun = ((Boolean) this.argCovPrune.getValue()).booleanValue() ? 1
                    : 0;
            int abssupp = 1;

            pw.println(supp + " :support"); //$NON-NLS-1$
            pw.println(conf + " :confidence"); //$NON-NLS-1$
            pw.println(cov + " :coverage"); //$NON-NLS-1$
            pw.println(chi2 + " :chi2"); //$NON-NLS-1$
            pw.println(suppDiff + " :suppdiff"); //$NON-NLS-1$
            pw.println(confDiff + " :confdiff"); //$NON-NLS-1$
            pw.println(rulered + " :rulereduction"); //$NON-NLS-1$
            pw.println(covprun + " :covprune"); //$NON-NLS-1$
            pw.println(abssupp + " :minsupp"); //$NON-NLS-1$
            pw.println(confDiffTH + " :minConfTH"); //$NON-NLS-1$

            pw.close();
        } catch (IOException e) {
            throw new ProcessingException(e);
        }
    }

}