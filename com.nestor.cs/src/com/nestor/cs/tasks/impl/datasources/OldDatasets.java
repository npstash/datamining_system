//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// OldDatasets.java (sed.data.datasources)
// Utworzono 2005-08-03 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.datasources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.attributes.BinaryAttribute;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;
import com.nestor.cs.data.impl.attributes.DiscreteAttribute;
import com.nestor.cs.data.impl.attributes.NominalAttribute;
import com.nestor.cs.data.impl.datasets.VectorItemset;
import com.nestor.cs.tasks.def.MultiResultTask;
import com.nestor.cs.tasks.impl.ConfigurableTaskHelper;
import com.nestor.cs.tasks.impl.dataset.ArrayListDataset;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class OldDatasets extends ConfigurableTaskHelper implements
        MultiResultTask {

    /**
     * @param as
     * @param f
     * @param clLabelPos -
     *            domy�lna warto�� -1 wskazuje, �e atrybut klasy ma by� ostatnim
     *            atrybutem
     * @return @throws
     *         Exception
     */
    public static ReadOnlyDataset loadOldDataset(DefaultAttributeSet as,
            File f, int clLabelPos) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(f));

        int clCnt = Integer.parseInt(br.readLine());
        int attrCnt = Integer.parseInt(br.readLine());
        int instCnt = Integer.parseInt(br.readLine());

        if (clLabelPos < 0)
            clLabelPos = attrCnt;
        as.removeAll();
        int binAttrNum = 0;
        for (int i = 0; i < attrCnt + 1; i++) {
            if (clLabelPos == i) {
                DiscreteAttribute temp = new DiscreteAttribute(clCnt);
                Attribute clAttr = new NominalAttribute(temp
                        .getAllowedValuesNames());
                clAttr.setName("class"); //$NON-NLS-1$
                as.addAttribute(clAttr);
            } else {
                Attribute binAttr = new BinaryAttribute(
                        new String[] { "0", "1" }); //$NON-NLS-1$ //$NON-NLS-2$
                binAttr.setName("binAttr" + binAttrNum++); //$NON-NLS-1$
                as.addAttribute(binAttr);
            }
        }

        as.setClassLabelIndex(clLabelPos);
        ArrayListDataset bds = new ArrayListDataset(as);

        for (int i = 0; i < instCnt; i++) {
            String line = br.readLine();
            StringTokenizer st = new StringTokenizer(line, " "); //$NON-NLS-1$
            int clIndex = Integer.parseInt(st.nextToken()) - 1;

            //unused
            st.nextToken();
            st.nextToken();
            st.nextToken();

            String values = st.nextToken();
            VectorItemset ai = new VectorItemset(as);
            int binAttrIndex = 0;
            for (int j = 0; j < values.length(); j++) {
                if (j == clLabelPos)
                    binAttrIndex++;
                char c = values.charAt(j);
                Object v = bds.getAttribute(binAttrIndex).getValueFromName(
                        c + ""); //$NON-NLS-1$
                ai.setValue(binAttrIndex++, v);
            }
            ai.setValue(clLabelPos, as.getAttribute(clLabelPos)
                    .getAllowedValue(clIndex));
            bds.addItemset(ai);
        }
        br.close();
        return bds;
    }

    public static void saveOldDataset(File f, ReadOnlyDataset bds)
            throws Exception {
        Attributes.checkForBinaryAttributes(bds, true);

        PrintWriter pw = new PrintWriter(new FileOutputStream(f));

        int clIndex = bds.getClassLabelIndex();
        Attribute clAttr = bds.getAttribute(clIndex);
        pw.println(clAttr.getAllowedValuesCount());
        int attrCnt = bds.getAttributeCount() - 1;
        pw.println(attrCnt);
        pw.println(bds.size());

        for (int i = 0; i < bds.size(); i++) {
            bds.setCursorAt(i);
            Object clValue = bds.getValue(clIndex);
            int clI = clAttr.getAllowedValueIndex(clValue);

            pw.print((clI + 1) + " " + attrCnt + " 0 0 "); //$NON-NLS-1$ //$NON-NLS-2$

            for (int j = 0; j < attrCnt; j++) {
                if (j != clIndex) {
                    boolean toset = false;
                    Object v = bds.getValue(j);
                    if (v != null) {
                        toset = ((Boolean) v).booleanValue();
                    }
                    char c = toset ? '1' : '0';
                    pw.print(c);
                }
            }
            pw.println();
        }
        pw.println("0"); //$NON-NLS-1$
        pw.close();
    }

    public Argument argBds = new Argument();

    public Argument argFile = new Argument();

    private TaskResult[] faces;

    private DefaultAttributeSet rAs;

    private ArrayListDataset rBds;

    public OldDatasets() {
        super();
        this.rAs = new DefaultAttributeSet();
        try {
            this.rBds = new ArrayListDataset(this.rAs);
        } catch (Exception e) {
            throw new Error(e);
        }
        this.faces = new TaskResult[] { new TaskResult(Messages
                .getString("OldDatasets.loadedDs"), //$NON-NLS-1$
                this, this.rBds) };

        this.argFile.setName(Messages.getString("OldDatasets.file")); //$NON-NLS-1$
        this.argFile.setValueType(File.class);
        this.argBds.setName(Messages.getString("OldDatasets.dsToWrite")); //$NON-NLS-1$
        this.argBds.setValueType(ReadOnlyDataset.class);
        this.argBds.setRequired(false);
        getConfigurationArguments().addArgument(this.argFile);
        getConfigurationArguments().addArgument(this.argBds);
    }

    public TaskResult[] getTaskResults() {
        return this.faces;
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        ReadOnlyDataset ds = (ReadOnlyDataset) this.argBds.getValue();
        File f = (File) this.argFile.getValue();
        if (ds != null) {
            try {
                saveOldDataset(f, ds);
            } catch (Exception e) {
                throw new ProcessingException(e);
            }
        } else {
            try {
                this.rBds.append(loadOldDataset(this.rAs, f, -1));
            } catch (Exception e) {
                throw new ProcessingException(e);
            }
        }
    }
}