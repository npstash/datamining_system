//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ARFFDataSink.java (sed.data.datasources)
// Utworzono 2005-07-22 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.datasources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.ItemsetIterator;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.impl.attributes.AbstractAttribute;
import com.nestor.cs.tasks.impl.ConfigurableTaskHelper;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class ARFFDataSink extends ConfigurableTaskHelper {

    public static String attributeToARFFString(Attribute attr) {
        String s = ARFFConsts.STR_DEFINES + ARFFConsts.DEF_ATTRIBUTE + " " //$NON-NLS-1$
                + attr.getName() + " "; //$NON-NLS-1$
        if (attr.getAllowedValuesCount() != Attribute.INFINITE_VALUES) {
            s += "{"; //$NON-NLS-1$
            String[] etts = attr.getAllowedValuesNames();
            for (int j = 0; j < etts.length; j++) {
                s += etts[j];
                if (j < etts.length - 1)
                    s += ", "; //$NON-NLS-1$
            }
            s += "}"; //$NON-NLS-1$
        } else {
            s += "real"; //$NON-NLS-1$
        }
        return s;
    }

    public static String itemsetToARFFString(ReadOnlyItemset it) {
        String s = ""; //$NON-NLS-1$
        for (int i = 0; i < it.getAttributeCount(); i++) {
            s += it.getAttribute(i).getNameForValue(it.getValue(i));
            if (i < it.getAttributeCount() - 1) {
                s += ", "; //$NON-NLS-1$
            }
        }
        return s;
    }

    public final Argument argDs = new Argument();

    public final Argument argFile = new Argument();

    public ARFFDataSink() {
        super();

        this.argFile.setName(Messages.getString("ARFFDataSink.argFile")); //$NON-NLS-1$
        this.argFile.setDescription(Messages.getString("ARFFDataSink.argDescFile")); //$NON-NLS-1$
        this.argFile.setValueType(File.class);
        getConfigurationArguments().addArgument(this.argFile);

        this.argDs.setName(Messages.getString("ARFFDataSink.argSavedDataset")); //$NON-NLS-1$
        this.argDs.setDescription(Messages
                .getString("ARFFDataSink.argDescSavedDataset")); //$NON-NLS-1$
        this.argDs.setValueType(ReadOnlyDataset.class);
        getConfigurationArguments().addArgument(this.argDs);
    }

    private ReadOnlyDataset getDataset() {
        return (ReadOnlyDataset) this.argDs.getValue();
    }

    private File getFile() {
        return (File) this.argFile.getValue();
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        PrintWriter pw;
        try {
            pw = new PrintWriter(new FileOutputStream(getFile()));
            pw.println(ARFFConsts.STR_COMMENT + getDataset().getDescription());

            pw.println(ARFFConsts.STR_DEFINES + ARFFConsts.DEF_RELATION + " " //$NON-NLS-1$
                    + getDataset().getName());

            for (int i = 0; i < getDataset().getAttributeCount(); i++) {
                Attribute attr = getDataset().getAttribute(i);
                pw.println(attributeToARFFString(attr));
            }

            pw.println(ARFFConsts.STR_DEFINES + ARFFConsts.DEF_DATA);

            ItemsetIterator it = getDataset().iterator();
            while (it.hasMoreItemsets()) {
                pw.println(itemsetToARFFString(it.nextItemset()));
            }
            pw.close();
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }

}