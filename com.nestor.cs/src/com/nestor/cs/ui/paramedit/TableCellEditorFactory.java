//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// CellEditorFactory.java (sed.ui.param)
// Utworzono 2005-07-23 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.paramedit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;

import com.nestor.cs.util.util.param.Argument;

public abstract class TableCellEditorFactory {

    private static class CellEditorListener implements ActionListener,
            DocumentListener {

        private CellEditor ce;

        private int col;

        private int row;

        private JTable table;

        public CellEditorListener(JTable table, int row, int col, CellEditor ce) {
            super();
            this.table = table;
            this.row = row;
            this.col = col;
            this.ce = ce;
        }

        public void actionPerformed(ActionEvent arg0) {
            updateTable();
        }

        public void changedUpdate(DocumentEvent arg0) {
            updateTable();
        }

        public void insertUpdate(DocumentEvent arg0) {
            updateTable();
        }

        public void removeUpdate(DocumentEvent arg0) {
            updateTable();
        }

        private void updateTable() {
            if (this.table != null) {
                this.table.setValueAt(this.ce.getCellEditorValue(), this.row, this.col);

            }
        }
    }

    public synchronized static TableCellEditor getTableCellEdit(JTable table,
            int row, int col, Argument arg) {
        if (arg.getValueType().equals(Boolean.class)) {
            JCheckBox jcb = new JCheckBox();
            TableCellEditor tce = new DefaultCellEditor(jcb);
            jcb.addActionListener(new CellEditorListener(table, row, col, tce));
            return tce;

        } else if (arg.getValueType().equals(File.class)) {
            return new FileTableCellEditor((File) arg.getValue());

        } else if (arg.getValueType().equals(Integer.class)) {
            return new IntegerTableCellEditor((Integer) arg.getValue(),
                    (Integer) arg.getLowBound(), (Integer) arg.getHighBound());
        }

        JTextField jtf = new JTextField();
        TableCellEditor tce = new DefaultCellEditor(jtf);
        jtf.getDocument().addDocumentListener(
                new CellEditorListener(table, row, col, tce));
        return tce;
    }

}