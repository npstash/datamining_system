//
//Praca Dyplomowa Magisterska: System Exploracji Danych
//Autor: Nestor Paw�owski
//
//AbstractDatasetConverter.java (sed.data.conversion)
//Utworzono 2005-06-09 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.ItemsetIterator;
import com.nestor.cs.data.def.ItemsetSelector;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.impl.datasets.ArrayItemset;
import com.nestor.cs.data.impl.datasets.ReadOnlyDatasetWrapper;
import com.nestor.cs.tasks.def.ConfigurableTask;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.util.proc.AbstractTask;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;
import com.nestor.cs.util.util.param.ArgumentList;

public abstract class AbstractDatasetConverter extends AbstractTask
        implements ConfigurableTask, ReadOnlyDataset {

    public final Argument argConvertedDataset = new Argument();

    private ReadOnlyDataset delegatedDs;

    private ArgumentList inputArgumentList = new ArgumentList();

    private ReadOnlyDataset wrappingDs;

    public AbstractDatasetConverter() {
        super();
        this.argConvertedDataset.setValueType(ReadOnlyDataset.class);
        this.argConvertedDataset.setName(Messages
                .getString("AbstractDatasetConverter.argConvertedDataset")); //$NON-NLS-1$
        this.argConvertedDataset.setDescription(Messages
                .getString("AbstractDatasetConverter.argDescConvertedDataset")); //$NON-NLS-1$
        getConfigurationArguments().addArgument(this.argConvertedDataset);
    }

    public ReadOnlyDataset getArgConvertedDataset() {
        return (ReadOnlyDataset) this.argConvertedDataset.getValue();
    }

    public ArgumentList getConfigurationArguments() {
        return this.inputArgumentList;
    }

    public int getCursorPos() {
        return this.delegatedDs.getCursorPos();
    }

    public String getDescription() {
        return this.delegatedDs.getDescription();
    }

    public ReadOnlyItemset getItemset(int i) {
        int k = getCursorPos();
        setCursorAt(i);
        ReadOnlyItemset it = new ArrayItemset(this, getValues());
        setCursorAt(k);
        return it;
    }

    public String getName() {
        return this.delegatedDs.getName();
    }

    public Object[] getValues() {
        Object[] values = new Object[getAttributeCount()];
        for (int i = 0; i < values.length; i++)
            values[i] = getValue(i);
        return values;
    }

    public void initProcess() throws ProcessingException {
        if (!this.inputArgumentList.areRequiredArgumentsFilled())
            throw new MissingArgumentValue(this.inputArgumentList);

        this.delegatedDs = getArgConvertedDataset();
        this.wrappingDs = new ReadOnlyDatasetWrapper(this);
    }

    public ItemsetIterator iterator() {
        return this.wrappingDs.iterator();
    }

    public ItemsetIterator iterator(ItemsetSelector is) {
        return this.wrappingDs.iterator(is);
    }

    public void setCursorAt(int i) {
        this.delegatedDs.setCursorAt(i);
    }

    public void setDescription(String d) {
        this.delegatedDs.setDescription(d);
    }

    public void setName(String n) {
        this.delegatedDs.setName(n);
    }

    public int size() {
        return this.delegatedDs.size();
    }

}