//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DatasetEdit.java (sed.data.ui)
// Utworzono 2005-07-19 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.attributes.AbstractAttribute;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class DatasetView extends AbstractDiagramComponent {

    private class DataModel extends AbstractTableModel {

        private ReadOnlyDataset ds;

        private DataModel(ReadOnlyDataset ds) {
            this.ds = ds;
        }

        @Override
        public Class getColumnClass(int col) {
            return Object.class;
        }

        public int getColumnCount() {
            return this.ds.getAttributeCount();
        }

        @Override
        public String getColumnName(int col) {
            return this.ds.getAttribute(col).getName();
        }

        public int getRowCount() {
            return this.ds.size();
        }

        public Object getValueAt(int row, int col) {
            this.ds.setCursorAt(row);
            Object value = this.ds.getValue(col);
            if (value == null)
                return Attribute.MISSING_VALUE_STR;
            Attribute attr = this.ds.getAttribute(col);
            return attr.getNameForValue(value);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
        }
    }

    public final Argument argDataset = new Argument();

    public DatasetView() {
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
        JTable table = new JTable(new DataModel(ds));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setPreparedComponent(new JScrollPane(table));
    }

}