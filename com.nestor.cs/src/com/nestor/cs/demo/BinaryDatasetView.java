//
//Praca Dyplomowa Magisterska: System Exploracji Danych
//Autor: Nestor Paw�owski
//
//BinaryDatasetEdit.java (sed.data.ui)
//Utworzono 2005-07-19 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.demo;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.attributes.BinaryAttribute;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.tasks.impl.visual.AbstractDiagramComponent;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class BinaryDatasetView extends AbstractDiagramComponent {

    private class DataModel extends AbstractTableModel {

        private ReadOnlyDataset ds;

        private DataModel(ReadOnlyDataset ds) {
            this.ds = ds;
        }

        @Override
        public Class getColumnClass(int col) {
            return String.class;
        }

        public int getColumnCount() {
            return (this.ds.getClassLabelIndex() >= 0) ? 2 : 1;
        }

        @Override
        public String getColumnName(int col) {
            return (col == 0) ? "Transakcja" : "Etykieta klasy: "
                    + this.ds.getAttribute(this.ds.getClassLabelIndex()).getName();
        }

        public int getRowCount() {
            return this.ds.size();
        }

        public Object getValueAt(int row, int col) {
            if ((row < 0) || (row >= this.ds.size())) 
                return null;
            this.ds.setCursorAt(row);
            if (col == 0) {
                return getTransactionString(this.ds);
            } else {
                int clIndex = this.ds.getClassLabelIndex();
                Attribute clAttr = this.ds.getAttribute(clIndex);
                Object value = this.ds.getValue(clIndex);
                return clAttr.getNameForValue(value);
            }
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
        }
    }

    // atrybut klasy b�dzie pomini�ty
    public static String getTransactionString(ReadOnlyItemset it) {
        String res = "{";
        boolean itemAdded = false;
        for (int i = 0; i < it.getAttributeCount(); i++) {
            if (i == it.getClassLabelIndex())
                continue;
            Attribute a = it.getAttribute(i);
            if (!(a instanceof BinaryAttribute))
                return null;
            Boolean bl = (Boolean) it.getValue(i);
            if (bl == null)
                continue;
            boolean b = bl.booleanValue();
            if (b) {
                if (itemAdded)
                    res += ", ";
                res += a.getName();
                itemAdded = true;
            }
        }
        return res + "}";
    }

    public final Argument argDataset = new Argument();

    public BinaryDatasetView() {
        super();
        this.argDataset.setName(Messages.getString("DatasetView.argShowDataset")); //$NON-NLS-1$
        this.argDataset.setDescription(Messages
                .getString("DatasetView.argDescShowDataset")); //$NON-NLS-1$
        this.argDataset.setValueType(ReadOnlyDataset.class);
        getConfigurationArguments().addArgument(this.argDataset);
    }

    public String getName() {
        ReadOnlyDataset ds = (ReadOnlyDataset) this.argDataset.getValue();
        return ds.getName();
    }

    public void initProcess() throws ProcessingException {
        if (!getConfigurationArguments().areRequiredArgumentsFilled())
            throw new MissingArgumentValue(getConfigurationArguments());

        ReadOnlyDataset ds = (ReadOnlyDataset) this.argDataset.getValue();
        if (!Attributes.areAllAttributesOfType(ds, BinaryAttribute.class, true))
            throw new ProcessingException("Dozwolone tylko atrybuty binarne!");

        JTable table = new JTable(new DataModel(ds));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setPreparedComponent(new JScrollPane(table));
    }

}