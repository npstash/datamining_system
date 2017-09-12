//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ARFFDataSource.java (sed.data.datasets)
// Utworzono 2005-05-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.datasources;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.impl.attributes.BinaryAttribute;
import com.nestor.cs.data.impl.attributes.ContinuousAttribute;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;
import com.nestor.cs.data.impl.attributes.NominalAttribute;
import com.nestor.cs.data.impl.datasets.AbstractReadOnlyDataset;
import com.nestor.cs.data.impl.datasets.ArrayItemset;
import com.nestor.cs.tasks.def.ConfigurableTask;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;
import com.nestor.cs.util.util.param.ArgumentList;

public class ARFFDataSource extends AbstractReadOnlyDataset implements
        ConfigurableTask {

    private class ARFFParseException extends Exception {
        private ARFFParseException() {
            super(
                    Messages.getString("ARFFDataSource.errorInFile") + getFile().getName() + Messages.getString("ARFFDataSource.line") //$NON-NLS-1$ //$NON-NLS-2$
                            + ARFFDataSource.this.reader.getLineNumber());
        }
    }

    public static Attribute parseARFFAttribute(String line, boolean loadBinary) {
        String defLine = removePrefix(line, ARFFConsts.STR_DEFINES).trim();
        String rawAttr = removePrefix(defLine, ARFFConsts.DEF_ATTRIBUTE).trim();
        StringTokenizer st = new StringTokenizer(rawAttr, " {\t"); //$NON-NLS-1$
        if (!st.hasMoreTokens())
            return null;
        String attrName = st.nextToken();
        if (!st.hasMoreTokens())
            return null;
        String attrDef = removePrefix(rawAttr, attrName).trim();
        attrName = removeApostrophes(attrName).trim();

        Attribute nAtr = null;
        if (attrDef.equalsIgnoreCase("real") //$NON-NLS-1$
                || attrDef.equalsIgnoreCase("double") //$NON-NLS-1$
                || attrDef.equalsIgnoreCase("integer") //$NON-NLS-1$
                || attrDef.equalsIgnoreCase("int")) { //$NON-NLS-1$
            nAtr = new ContinuousAttribute();
        } else {
            if (!attrDef.startsWith("{") || !attrDef.endsWith("}")) //$NON-NLS-1$ //$NON-NLS-2$
                return null;
            StringTokenizer attrValues = new StringTokenizer(attrDef, "{},\t"); //$NON-NLS-1$
            ArrayList alist = new ArrayList();
            while (attrValues.hasMoreTokens()) {
                String atr = removeApostrophes(attrValues.nextToken());
                alist.add(atr);
            }
            String[] values = (String[]) alist.toArray(new String[] {});
            if ((loadBinary) && (values.length == 2)) {
                nAtr = new BinaryAttribute(values);
            } else {
                nAtr = new NominalAttribute(values);
            }
        }
        nAtr.setName(attrName);
        return nAtr;
    }

    public static ReadOnlyItemset parseARFFItemset(String line, AttributeSet as) {
        StringTokenizer st = new StringTokenizer(line.trim(), ","); //$NON-NLS-1$
        int currentAttr = 0;
        Object[] initValues = new Object[as.getAttributeCount()];
        while (st.hasMoreTokens()) {
            if (currentAttr >= initValues.length)
                return null;
            String value = removeApostrophes(st.nextToken());
            Attribute attr = as.getAttribute(currentAttr);
            initValues[currentAttr++] = attr.getValueFromName(value);
        }
        if (currentAttr < as.getAttributeCount())
            return null;
        ReadOnlyItemset it = null;
        it = new ArrayItemset(as, initValues);
        return it;
    }

    private static String removeApostrophes(String str) {
        String s = str.trim();
        while ((s.startsWith("'") && s.endsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
                || (s.startsWith("\"") && s.endsWith("\""))) { //$NON-NLS-1$ //$NON-NLS-2$
            if (s.length() < 3)
                return ""; //$NON-NLS-1$
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    private static String removePrefix(String s, String prefix) {
        return s.substring(s.toLowerCase().indexOf(prefix.toLowerCase())
                + prefix.length(), s.length());
    }

    public final Argument argFile = new Argument();

    private ArgumentList argList = new ArgumentList();

    public final Argument argLoadBinary = new Argument(new Boolean(false));

    private DefaultAttributeSet attributes = null;

    private int currentCursorPosition;

    private int dataStartLine;

    private LineNumberReader reader = null;

    private int recentlyCreatedItemset;

    private String recentlyLoadedRecord;

    private ReadOnlyItemset tempItemset;

    private int totalLines;

    public ARFFDataSource() {
        super(new DefaultAttributeSet());
        this.attributes = (DefaultAttributeSet) super.attributeSet;

        this.argFile.setName(Messages.getString("ARFFDataSource.argFile")); //$NON-NLS-1$
        this.argFile
                .setDescription(Messages
                        .getString("ARFFDataSource.argDescFile")); //$NON-NLS-1$        
        this.argFile.setValueType(File.class);
        this.argList.addArgument(this.argFile);

        this.argLoadBinary.setName(Messages
                .getString("ARFFDataSource.argLoadBinary")); //$NON-NLS-1$
        this.argLoadBinary.setDescription(Messages
                .getString("ARFFDataSource.argDescLoadBinary")); //$NON-NLS-1$
        this.argList.addArgument(this.argLoadBinary);
    }

    @Override
    protected void finalize() {
        try {
            this.reader.close();
        } catch (IOException e) {
        } finally {
            this.reader = null;
        }
    }

    public boolean getArgLoadBinary() {
        return ((Boolean) this.argLoadBinary.getValue()).booleanValue();
    }

    public ArgumentList getConfigurationArguments() {
        return this.argList;
    }

    public int getCursorPos() {
        return this.currentCursorPosition;
    }

    private File getFile() {
        return (File) this.argFile.getValue();
    }

    public synchronized ReadOnlyItemset getItemset(int i) {
        if (this.recentlyCreatedItemset == i) {
            return this.tempItemset;
        }
        try {
            ReadOnlyItemset it = parseARFFItemset(loadLine(i), this);
            if (it != null) {
                this.recentlyCreatedItemset = i;
                this.tempItemset = it;
            }
            return it;
        } catch (IOException e) {
            return null;
        }
    }

    public synchronized Object getValue(int attrIndex) {
        ReadOnlyItemset it = getItemset(this.currentCursorPosition);
        if (it != null)
            return it.getValue(attrIndex);
        return null;
    }

    public synchronized Object[] getValues() {
        ReadOnlyItemset it = getItemset(this.currentCursorPosition);
        if (it != null)
            return it.getValues();
        return null;
    }

    public void initProcess() throws ProcessingException {
        if (!getConfigurationArguments().areRequiredArgumentsFilled())
            throw new MissingArgumentValue(getConfigurationArguments());

        try {
            if (this.reader != null)
                this.reader.close();
            this.reader = new LineNumberReader(new FileReader(getFile()));
            loadARFFHeader();
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }

    private void loadARFFHeader() throws Exception {
        setName(getFile().getName());
        setDescription(""); //$NON-NLS-1$
        this.dataStartLine = -1;
        this.totalLines = 0;
        this.currentCursorPosition = -1;
        this.recentlyLoadedRecord = null;
        this.recentlyCreatedItemset = -1;
        this.tempItemset = null;
        this.attributes.removeAll();

        while (this.reader.ready()) {
            String line = this.reader.readLine();
            String lowerCase = line.toLowerCase();

            if (line.trim().equals("")) //$NON-NLS-1$
                continue;

            if (lowerCase.trim().startsWith(
                    ARFFConsts.STR_COMMENT.toLowerCase().trim())) {
                setDescription(getDescription()
                        + removePrefix(line, ARFFConsts.STR_COMMENT));
                continue;
            }

            if (lowerCase.trim().startsWith(
                    ARFFConsts.STR_DEFINES.toLowerCase().trim())) {
                String defLine = removePrefix(line, ARFFConsts.STR_DEFINES)
                        .toLowerCase().trim();

                if (defLine.startsWith(ARFFConsts.DEF_RELATION.toLowerCase())) {
                    String rawName = removePrefix(defLine,
                            ARFFConsts.DEF_RELATION).trim();
                    setName(removeApostrophes(rawName));
                    continue;
                }

                if (defLine.startsWith(ARFFConsts.DEF_ATTRIBUTE.toLowerCase())) {
                    Attribute attr = parseARFFAttribute(line,
                            getArgLoadBinary());
                    if (attr == null) {
                        throw new ARFFParseException();
                    }
                    this.attributes.addAttribute(attr);
                    int aindex = this.attributes.getAttributeCount() - 1;
                    if (attr.getName().toLowerCase().equals(
                            ARFFConsts.DEF_ATTR_CLASS.toLowerCase()))
                        this.attributes.setClassLabelIndex(aindex);
                    continue;
                }

                if (defLine.startsWith(ARFFConsts.DEF_DATA.toLowerCase())) {
                    String dataLine = null;
                    while (this.reader.ready()) {
                        dataLine = this.reader.readLine();
                        if (dataLine.trim().equals("")) //$NON-NLS-1$
                            continue;
                        if (dataLine.trim().toLowerCase().startsWith(
                                ARFFConsts.STR_COMMENT.toLowerCase()))
                            continue;
                        if (dataLine.trim().toLowerCase().startsWith(
                                ARFFConsts.STR_DEFINES.toLowerCase()))
                            throw new ARFFParseException();
                        this.dataStartLine = this.reader.getLineNumber() - 1;
                        break;
                    }
                    while (dataLine != null) {
                        if (dataLine.trim().equals("")) //$NON-NLS-1$
                            break;
                        if (dataLine.trim().toLowerCase().startsWith(
                                ARFFConsts.STR_COMMENT.toLowerCase()))
                            break;
                        if (dataLine.trim().toLowerCase().startsWith(
                                ARFFConsts.STR_DEFINES.toLowerCase()))
                            break;
                        ReadOnlyItemset it = parseARFFItemset(dataLine, this);
                        if (it == null)
                            throw new ARFFParseException();
                        it = null;
                        this.totalLines++;
                        if (this.reader.ready()) {
                            dataLine = this.reader.readLine();
                        } else {
                            dataLine = null;
                        }
                    }
                    break;
                } // data
            } // definition
        } // while ready
    }

    private synchronized String loadLine(int i) throws IOException {
        if ((this.recentlyLoadedRecord != null) && (i == recordToBeRead() - 1)) {
            return this.recentlyLoadedRecord;
        }
        if (i != recordToBeRead()) {
            if (recordToBeRead() > i) {
                this.reader.close();
                this.reader = new LineNumberReader(new FileReader(getFile()));
                while (this.reader.getLineNumber() < this.dataStartLine) {
                    if (!this.reader.ready()) {
                        throw new IOException();
                    }
                    this.reader.readLine();
                }
            }
            while (recordToBeRead() < i) {
                if (!this.reader.ready()) {
                    throw new IOException();
                }
                this.reader.readLine();
            }
        }
        this.recentlyLoadedRecord = this.reader.readLine();
        return this.recentlyLoadedRecord;
    }

    private int recordToBeRead() {
        int r = this.reader.getLineNumber() - this.dataStartLine;
        return r;
    }

    public synchronized void setCursorAt(int i) {
        this.currentCursorPosition = i;
    }

    public int size() {
        return this.totalLines;
    }
}