//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// IntegerCellEditor.java (sed.ui.param)
// Utworzono 2005-07-23 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.paramedit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;

public class IntegerTableCellEditor extends AbstractCellEditor implements
        TableCellEditor, AdjustmentListener, DocumentListener {

    private Integer currentValue;

    private JPanel jpMain;

    private JScrollBar jsbBar;

    private JTextField jtfInt;

    private int lastCol = 0;

    private int lastRow = 0;

    private JTable lastTable = null;

    private int lastValue;

    private Integer maxValue;

    private Integer minValue;

    public IntegerTableCellEditor(Integer init, Integer min, Integer max) {
        super();
        this.jpMain = new JPanel(new BorderLayout());
        this.currentValue = init;
        this.minValue = min;
        this.maxValue = max;

        this.jtfInt = new JTextField();
        if (init != null)
            this.jtfInt.setText("" + init); //$NON-NLS-1$
        this.jtfInt.getDocument().addDocumentListener(this);

        this.jsbBar = new JScrollBar();
        this.jsbBar.setPreferredSize(new Dimension(20, 0));
        this.jsbBar.setVisibleAmount(0);
        this.jsbBar.setMinimum(Integer.MIN_VALUE);
        this.jsbBar.setMaximum(Integer.MAX_VALUE);
        this.lastValue = createValue();
        this.jsbBar.setValue(this.lastValue);
        this.jsbBar.addAdjustmentListener(this);

        this.jpMain.add(this.jtfInt, BorderLayout.CENTER);
        this.jpMain.add(this.jsbBar, BorderLayout.EAST);
    }

    public void adjustmentValueChanged(AdjustmentEvent event) {
        if (this.lastValue != event.getValue()) {
            int newValue = event.getValue();
            if (newValue < this.lastValue)
                increaseValue();
            if (newValue > this.lastValue)
                decreaseValue();
            this.lastValue = newValue;
            this.jtfInt.setText(this.currentValue + ""); //$NON-NLS-1$
        }
    }

    public void changedUpdate(DocumentEvent e) {
        updateTable();
    }

    public int createValue() {
        if (this.currentValue != null)
            return this.currentValue.intValue();
        else if (this.minValue != null)
            return this.minValue.intValue();
        else if (this.maxValue != null)
            return this.maxValue.intValue();
        else
            return 0;
    }

    public void decreaseValue() {
        if (this.currentValue == null) {
            this.currentValue = new Integer(createValue());
            return;
        }
        int c = this.currentValue.intValue() - 1;
        if (this.minValue != null) {
            int minC = this.minValue.intValue();
            if (c < minC)
                c = minC;
        }
        this.currentValue = new Integer(c);
    }

    public Object getCellEditorValue() {
        if (this.jtfInt.getText().equals("")) //$NON-NLS-1$
            return null;
        return this.jtfInt.getText();
    }

    public Component getTableCellEditorComponent(JTable table, Object unsued,
            boolean sel, int row, int col) {
        this.lastTable = table;
        this.lastRow = row;
        this.lastCol = col;
        return this.jpMain;
    }

    public void increaseValue() {
        if (this.currentValue == null) {
            this.currentValue = new Integer(createValue());
            return;
        }
        int c = this.currentValue.intValue() + 1;
        if (this.maxValue != null) {
            int maxC = this.maxValue.intValue();
            if (c > maxC)
                c = maxC;
        }
        this.currentValue = new Integer(c);
    }

    public void insertUpdate(DocumentEvent e) {
        updateTable();
    }

    public void removeUpdate(DocumentEvent arg0) {
        updateTable();
    }

    public void setValue(Integer val) {
        this.jtfInt.setText((val == null) ? "" : val + ""); //$NON-NLS-1$ //$NON-NLS-2$
        this.currentValue = val;
    }

    private void updateTable() {
        if (this.lastTable != null) {
            this.lastTable.setValueAt(getCellEditorValue(), this.lastRow, this.lastCol);
        }
    }

}