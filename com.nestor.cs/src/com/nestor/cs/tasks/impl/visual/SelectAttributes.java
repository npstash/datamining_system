//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SelectAttributes.java (sed.data.ui)
// Utworzono 2005-07-23 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSubset;
import com.nestor.cs.tasks.def.ConfigurableTask;
import com.nestor.cs.tasks.def.DiagramComponent;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;
import com.nestor.cs.util.util.param.ArgumentList;

public class SelectAttributes extends DefaultAttributeSubset implements
        DiagramComponent, ConfigurableTask {

    private class DataModel extends AbstractTableModel {

        private AttributeSet as;

        private DataModel(AttributeSet as) {
            this.as = as;
        }

        @Override
        public Class getColumnClass(int col) {
            switch (col) {
            case 0:
                return Boolean.class;
            case 1:
                return String.class;
            default:
                return Object.class;
            }
        }

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return this.as.getAttributeCount();
        }

        public Object getValueAt(int row, int col) {
            Attribute attr = this.as.getAttribute(row);
            boolean isClass = this.as.getClassLabelIndex() == row;
            if (attr == null)
                return null;
            switch (col) {
            case 0:
                return new Boolean(containsAttribute(row));
            case 1:
                String s = attr.toString();
                if (isClass)
                    s += Messages.getString("SelectAttributes.classLabel"); //$NON-NLS-1$
                return s;
            default:
                return null;
            }
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 0;
        }

        @Override
        public void setValueAt(Object val, int row, int col) {
            if (col != 0)
                return;
            Attribute attr = this.as.getAttribute(row);
            if (attr == null)
                return;
            if (!(val instanceof Boolean))
                return;
            boolean b = ((Boolean) val).booleanValue();
            if (b) {
                setAttributeContained(row, true);
            } else {
                setAttributeContained(row, false);
            }
        }
    }

    public final Argument argAttributeSet = new Argument();

    private ArgumentList argList = new ArgumentList();

    private Component preparedComponent = null;

    public SelectAttributes() {
        super();

        this.argAttributeSet.setName(Messages
                .getString("SelectAttributes.argAttributeset")); //$NON-NLS-1$
        this.argAttributeSet.setDescription(Messages
                .getString("SelectAttributes.argDescAttributeset")); //$NON-NLS-1$
        this.argAttributeSet.setValueType(AttributeSet.class);
        this.argList.addArgument(this.argAttributeSet);
    }

    public boolean doInternalFrame() {
        return false;
    }

    public boolean doModalDialog() {
        return true;
    }

    protected AttributeSet getArgAttributeSet() {
        return (AttributeSet) this.argAttributeSet.getValue();
    }

    public Component getComponent() {
        return this.preparedComponent;
    }

    public ArgumentList getConfigurationArguments() {
        return this.argList;
    }

    public String getName() {
        String s = Messages.getString("SelectAttributes.selectAttibutes"); //$NON-NLS-1$
        if (getArgAttributeSet() instanceof ReadOnlyDataset) {
            ReadOnlyDataset ds = (ReadOnlyDataset) getArgAttributeSet();
            s += Messages.getString("SelectAttributes.fromDataset") + ds.getName(); //$NON-NLS-1$
        }
        return s;
    }

    @Override
    public AttributeSet getRelatedAttributeSet() {
        return getArgAttributeSet();
    }

    public void initProcess() throws ProcessingException {
        if (!this.argList.areRequiredArgumentsFilled())
            throw new MissingArgumentValue(this.argList);

        super.contains = new boolean[getArgAttributeSet().getAttributeCount()];
        super.relatedAttributeSet = getArgAttributeSet();
        JTable table = new JTable(new DataModel(getArgAttributeSet()));
        table.getTableHeader().setReorderingAllowed(false);
        this.preparedComponent = new JScrollPane(table);
    }
}