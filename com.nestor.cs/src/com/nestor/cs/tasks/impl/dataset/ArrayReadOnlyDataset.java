//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ArrayReadOnlyDataset.java (sed.data.datasets)
// Utworzono 2005-07-28 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.dataset;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;
import com.nestor.cs.data.impl.datasets.AbstractReadOnlyDataset;
import com.nestor.cs.data.impl.datasets.ArrayItemset;
import com.nestor.cs.tasks.def.ConfigurableTask;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;
import com.nestor.cs.util.util.param.ArgumentList;

public class ArrayReadOnlyDataset extends AbstractReadOnlyDataset implements
        ConfigurableTask {

    public final Argument argCopiedDataset;

    private final ArgumentList argList = new ArgumentList();

    // u�ywany w przypadku wywo�ania konstruktora
    // bez parametr�w
    private DefaultAttributeSet as = null;

    private int cursor = -1;

    private Object[][] values = null;

    public ArrayReadOnlyDataset() {
        super(new DefaultAttributeSet());
        this.as = (DefaultAttributeSet) super.attributeSet;

        this.argCopiedDataset = new Argument();
        this.argCopiedDataset.setName(Messages
                .getString("ArrayReadOnlyDataset.argCopiedDataset")); //$NON-NLS-1$
        this.argCopiedDataset.setDescription(Messages
                .getString("ArrayReadOnlyDataset.argDescCopiedDataset")); //$NON-NLS-1$
        this.argCopiedDataset.setValueType(ReadOnlyDataset.class);

        this.argList.addArgument(this.argCopiedDataset);
    }

    public ArrayReadOnlyDataset(ReadOnlyDataset dsToCopy) {
        super(dsToCopy);
        initFromDS(dsToCopy);
        this.argCopiedDataset = null;
    }

    public ReadOnlyDataset getArgCopiedDataset() {
        return ((ReadOnlyDataset) this.argCopiedDataset.getValue());
    }

    public ArgumentList getConfigurationArguments() {
        return this.argList;
    }

    public int getCursorPos() {
        return this.cursor;
    }

    public ReadOnlyItemset getItemset(int i) {
        return new ArrayItemset(this, this.values[i]);
    }

    public Object getValue(int attrIndex) {
        return this.values[this.cursor][attrIndex];
    }

    public Object[] getValues() {
        return this.values[this.cursor];
    }

    private void initFromDS(ReadOnlyDataset dsToCopy) {
        this.values = new Object[dsToCopy.size()][];
        for (int i = 0; i < this.values.length; i++) {
            dsToCopy.setCursorAt(i);
            this.values[i] = dsToCopy.getValues();
        }
        setName(dsToCopy.getName());
        setDescription(dsToCopy.getDescription());
    }

    public void initProcess() throws ProcessingException {
        if (!getConfigurationArguments().areRequiredArgumentsFilled())
            throw new MissingArgumentValue(getConfigurationArguments());

        if (this.argCopiedDataset != null) {
            this.as.removeAll();
            ReadOnlyDataset ds = getArgCopiedDataset();
            for (int i = 0; i < ds.getAttributeCount(); i++) {
                this.as.addAttribute(ds.getAttribute(i));
            }
            this.as.setClassLabelIndex(ds.getClassLabelIndex());
            initFromDS(getArgCopiedDataset());
        }
    }

    public void setCursorAt(int i) {
        this.cursor = i;
    }

    public int size() {
        return this.values.length;
    }
}