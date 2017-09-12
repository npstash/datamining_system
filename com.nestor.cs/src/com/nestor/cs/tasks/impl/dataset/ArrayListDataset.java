//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// VectorDataset.java (sed.data.datasets)
// Utworzono 2005-05-08 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.dataset;

import java.util.ArrayList;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.InvalidAttributeValue;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;
import com.nestor.cs.data.impl.datasets.AbstractDataset;
import com.nestor.cs.data.impl.datasets.ArrayItemset;
import com.nestor.cs.tasks.def.ConfigurableTask;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.ArrayUtils;
import com.nestor.cs.util.util.param.Argument;
import com.nestor.cs.util.util.param.ArgumentList;

public class ArrayListDataset extends AbstractDataset implements
        ConfigurableTask {

    public final Argument argCopiedDataset;

    private final ArgumentList argList = new ArgumentList();

    // u�ywany w przypadku wywo�ania konstruktora
    // bez parametr�w
    private DefaultAttributeSet as = null;

    private int cursor = -1;

    private ArrayList rows = null;

    public ArrayListDataset() {
        super(new DefaultAttributeSet());
        this.as = (DefaultAttributeSet) super.attributeSet;

        this.argCopiedDataset = new Argument();
        this.argCopiedDataset.setName(Messages
                .getString("ArrayReadOnlyDataset.argCopiedDataset")); //$NON-NLS-1$
        this.argCopiedDataset.setDescription(Messages
                .getString("ArrayListDataset.argDescCopiedDataset")); //$NON-NLS-1$
        this.argCopiedDataset.setValueType(ReadOnlyDataset.class);

        this.argList.addArgument(this.argCopiedDataset);
    }

    public ArrayListDataset(AttributeSet as) {
        super(as);
        this.rows = new ArrayList();
        this.argCopiedDataset = null;
    }

    public ArrayListDataset(ReadOnlyDataset dsToCopy, boolean copy) {
        super(dsToCopy);
        if (copy) {
            initFromDS(dsToCopy);
        }
        this.argCopiedDataset = null;
    }

    public void addItemset(Object[] values) throws InvalidAttributeValue {
        InvalidAttributeValue.checkValidity(this, values);
        this.rows.add(values);
    }

    public void addItemset(ReadOnlyItemset it) throws IncompatibleAttributeset,
            InvalidAttributeValue {
        IncompatibleAttributeset.checkLength(this, it);
        InvalidAttributeValue.checkValidity(this, it);
        this.rows.add(it.getValues());
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
        return new ArrayItemset(this, (Object[]) this.rows.get(i));
    }

    public final Object getValue(int attrIndex) {
        return ((Object[]) this.rows.get(this.cursor))[attrIndex];
    }

    public Object[] getValues() {
        return (Object[]) this.rows.get(this.cursor);
    }

    private void initFromDS(ReadOnlyDataset dsToCopy) {
        this.rows = new ArrayList(dsToCopy.size());
        for (int i = 0; i < dsToCopy.size(); i++) {
            dsToCopy.setCursorAt(i);
            Object[] valuesToCopy = dsToCopy.getValues();
            Object[] valuesCopy = new Object[dsToCopy.getAttributeCount()];
            for (int j = 0; j < valuesCopy.length; j++) {
                valuesCopy[j] = valuesToCopy[j];
            }
            this.rows.add(valuesCopy);
        }
        setName(dsToCopy.getName());
        setDescription(dsToCopy.getDescription());
    }

    public void initProcess() throws ProcessingException {
        if (!getConfigurationArguments().areRequiredArgumentsFilled())
            throw new MissingArgumentValue(getConfigurationArguments());

        // je�li ten argument jest zainicjalizowany, to
        // 'as' (AttributeSet) jest zainicjalizowany i mo�e by� niepusty
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

    public void removeAll() {
        this.rows.clear();
    }

    public void removeItemset(int i) {
        this.rows.remove(i);
    }

    public void removeItemsets(int[] indexes) {
        for (int i = size() - 1; i >= 0; i--) {
            if (ArrayUtils.isContained(i, indexes))
                removeItemset(i);
        }
    }

    public void retainItemsets(int[] indexes) {
        for (int i = size() - 1; i >= 0; i--) {
            if (!ArrayUtils.isContained(i, indexes))
                removeItemset(i);
        }
    }

    public void setCursorAt(int i) {
        this.cursor = i;
    }

    public void setValue(int attrIndex, Object value)
            throws InvalidAttributeValue, IndexOutOfBoundsException {
        if ((this.cursor >= size()) || (this.cursor < 0))
            throw new IndexOutOfBoundsException();
        InvalidAttributeValue.checkValidity(getAttribute(attrIndex), value);
        ((Object[]) this.rows.get(this.cursor))[attrIndex] = value;
    }

    public int size() {
        return this.rows.size();
    }

}