//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DatasetView.java (sed.data.ui)
// Utworzono 2005-07-19 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Dataset;
import com.nestor.cs.data.impl.attributes.FuzzyAttribute;
import com.nestor.cs.data.impl.datasets.ReadOnlyDatasetWrapper;
import com.nestor.cs.tasks.def.ConfigurableTask;
import com.nestor.cs.tasks.def.DiagramComponent;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.tasks.impl.visual.items.ItemComponent;
import com.nestor.cs.tasks.impl.visual.items.ItemComponentFactory;
import com.nestor.cs.util.event.UpdateEvent;
import com.nestor.cs.util.event.UpdateListener;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;
import com.nestor.cs.util.util.param.ArgumentList;

public class DatasetEdit extends ReadOnlyDatasetWrapper implements
        DiagramComponent, UpdateListener, ConfigurableTask {

    private ArgumentList argList = new ArgumentList();

    private Component preparedComponent = null;

    public boolean doInternalFrame() {
        return false;
    }

    public Component getComponent() {
        return this.preparedComponent;
    }

    public ArgumentList getConfigurationArguments() {
        return this.argList;
    }

    protected void setPreparedComponent(Component c) {
        this.preparedComponent = c;
    }


    private class DataEditor extends AbstractCellEditor implements
            TableCellEditor {

        private Dataset ds;

        private ItemComponent ic = null;

        private DataEditor(Dataset ds) {
            this.ds = ds;
        }

        public synchronized Object getCellEditorValue() {
            if (this.ic != null) {
                Object val = this.ic.getValue();
                this.ic = null;
                return val;
            } else
                return null;
        }

        public synchronized Component getTableCellEditorComponent(JTable table,
                Object value, boolean arg2, int row, int col) {
            Attribute attr = this.ds.getAttribute(col);
            this.ic = ItemComponentFactory.createItemComponent(attr, value);
            this.ic.addUpdateListener(DatasetEdit.this);
            return this.ic.getComponent();
        }
    }

    private class DataKey extends KeyAdapter {

        private Dataset ds;

        private DataKey(Dataset ds) {
            this.ds = ds;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (!(e.getSource() instanceof JTable))
                return;
            JTable table = (JTable) e.getSource();
            if (e.getKeyCode() == KeyEvent.VK_INSERT) {
                Object[] v = new Object[this.ds.getAttributeCount()];
                for (int i = 0; i < v.length; i++)
                    v[i] = null;
                try {
                    this.ds.addItemset(v);
                } catch (Exception ex) {
                }
                table.tableChanged(new TableModelEvent(table.getModel()));
            } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                if (table.getSelectedRow() >= 0) {
                    this.ds.removeItemset(table.getSelectedRow());
                    table.tableChanged(new TableModelEvent(table.getModel()));
                }
            }
        }
    }

    private class DataModel extends AbstractTableModel {

        private Dataset ds;

        private DataModel(Dataset ds) {
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
            return this.ds.getValue(col);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            Attribute attr = this.ds.getAttribute(col);
            if (attr instanceof FuzzyAttribute)
                return false;
            return true;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            this.ds.setCursorAt(row);
            try {
                this.ds.setValue(col, value);
            } catch (Exception e) {
            }
        }
    }

    private class DataRenderer implements TableCellRenderer {

        private Dataset ds;

        private DataRenderer(Dataset ds) {
            this.ds = ds;
        }

        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean selected, boolean hasFocus, int row,
                int col) {
            Attribute attr = this.ds.getAttribute(col);
            JComponent c = ItemComponentFactory.getSharedItemComponent(attr,
                    value).getComponent();
            return c;
        }
    }

    public final Argument argDataset = new Argument();

    private JTable table;

    public DatasetEdit() {
        super(null);
        this.argDataset.setName(Messages.getString("DatasetEdit.argEditDataset")); //$NON-NLS-1$
        this.argDataset.setDescription(Messages
                .getString("DatasetEdit.argDescEditDataset")); //$NON-NLS-1$
        this.argDataset.setValueType(Dataset.class);
        getConfigurationArguments().addArgument(this.argDataset);
    }

    public boolean doModalDialog() {
        return true;
    }

    @Override
    public String getName() {
        Dataset ds = (Dataset) this.argDataset.getValue();
        return ds.getName();
    }

    public void initProcess() throws ProcessingException {
        if (!getConfigurationArguments().areRequiredArgumentsFilled())
            throw new MissingArgumentValue(getConfigurationArguments());
        Dataset ds = (Dataset) this.argDataset.getValue();
        super.setWrappedDataset(ds);
        this.table = new JTable(new DataModel(ds));
        this.table.setDefaultRenderer(Object.class, new DataRenderer(ds));
        this.table.setDefaultEditor(Object.class, new DataEditor(ds));
        this.table.getTableHeader().setReorderingAllowed(false);
        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.table.addKeyListener(new DataKey(ds));
        setPreparedComponent(new JScrollPane(this.table));
    }

    public void update(UpdateEvent e) {
        if (this.table == null)
            return;
        if (!this.table.isEditing())
            return;
        this.table.setValueAt(e.getNewValue(), this.table.getEditingRow(), this.table
                .getEditingColumn());
    }
}