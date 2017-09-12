//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ParameterEdit.java (javan.ui)
// Utworzono 2005-07-03 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.paramedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.util.EventListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.nestor.cs.Messages;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.util.event.UpdateEvent;
import com.nestor.cs.util.event.UpdateListener;
import com.nestor.cs.util.util.param.Argument;
import com.nestor.cs.util.util.param.ParameterList;

public class ParameterEdit extends JPanel implements UpdateListener,
        ListSelectionListener {

    private class DataEditor extends AbstractCellEditor implements
            TableCellEditor {

        public Object getCellEditorValue() {
            if (ParameterEdit.this.editor != null) {
                Object res = ParameterEdit.this.editor.getCellEditorValue();
                ParameterEdit.this.editor = null;
                return res;
            }
            return invalidValue;
        }

        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean sel, int row, int col) {
            Argument arg = ParameterEdit.this.pList.getArgument(row);
            if (arg == null)
                return null;
            if (col == 1) {
                Component cmp = null;
                ParameterEdit.this.editor = TableCellEditorFactory.getTableCellEdit(table, row,
                        col, arg);
                if (ParameterEdit.this.editor != null) {
                    cmp = ParameterEdit.this.editor.getTableCellEditorComponent(table, value, sel,
                            row, col);
                }
                return cmp;
            }
            return null;
        }
    }

    private class DataModel implements TableModel {
        public void addTableModelListener(TableModelListener listener) {
            if (ParameterEdit.this.listeners.getListenerCount(TableModelListener.class) == 0) {
                ParameterEdit.this.pList.addUpdateListener(ParameterEdit.this);
            }
            ParameterEdit.this.listeners.add(TableModelListener.class, listener);
        }

        public Class getColumnClass(int col) {
            return String.class;
        }

        public int getColumnCount() {
            return 2;
        }

        public String getColumnName(int column) {
            switch (column) {
            case 0:
                return Messages.getString("ParameterEdit.parameterName"); //$NON-NLS-1$
            case 1:
                return Messages.getString("ParameterEdit.value"); //$NON-NLS-1$
            default:
                return null;
            }
        }

        public int getRowCount() {
            return ParameterEdit.this.pList.getArgumentCount();
        }

        public Object getValueAt(int row, int column) {
            switch (column) {
            case 0:
                String name = ParameterEdit.this.pList.getArgument(row).getName();
                return name;
            case 1:
                return ParameterEdit.this.pList.getArgument(row).getValue();
            default:
                return null;
            }
        }

        public boolean isCellEditable(int row, int column) {
            switch (column) {
            case 1:
                return ParameterEdit.this.pList.getArgument(row).isTextEditable()
                        && (!ParameterEdit.this.pList.getArgument(row).isReadOnly());
            default:
                return false;
            }
        }

        public void removeTableModelListener(TableModelListener arg0) {
            ParameterEdit.this.listeners.remove(TableModelListener.class, arg0);
            if (ParameterEdit.this.listeners.getListenerCount(TableModelListener.class) == 0) {
                ParameterEdit.this.pList.removeUpdateListener(ParameterEdit.this);
            }
        }

        public void setValueAt(Object obj, int row, int column) {
            switch (column) {
            case 1:
                try {
                    if (obj == null)
                        ParameterEdit.this.pList.getArgument(row).setValue(null);
                    else if (obj.toString().equals("")) //$NON-NLS-1$
                        ParameterEdit.this.pList.getArgument(row).setValue(null);
                    else if (obj != invalidValue)
                        ParameterEdit.this.pList.getArgument(row).initValue(obj.toString());
                } catch (Exception e) {
                    showError(e.getMessage());
                }
                break;
            }
        }

    }

    private class DataRenderer implements TableCellRenderer {

        private JCheckBox jcbRenderer = null;

        private JLabel jlRenderer = null;

        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean sel, boolean foc, int row, int col) {
            Argument arg = ParameterEdit.this.pList.getArgument(row);
            if (arg == null)
                return null;
            Component c = null;
            if (col == 1) {
                c = TableCellRendererFactory.getSharedTableCellRenderer(arg);
            }

            if (c == null)
                c = ParameterEdit.this.defaultRenderer.getTableCellRendererComponent(table, value,
                        sel, foc, row, col);

            if ((arg.isRequired()) && (col == 0))
                c.setForeground(Color.RED);
            else
                c.setForeground(Color.BLACK);

            return c;
        }
    }

    private static final String invalidValue = Messages
            .getString("ParameterEdit.invalidValue"); //$NON-NLS-1$

    private Thread currentThread = null;

    private Object currentThreadMutex = new Object();

    private TableCellRenderer defaultRenderer;

    private TableCellEditor editor = null;

    private ParameterList emptyList = new ParameterList();

    private JTextArea errorArea;

    private EventListenerList listeners = new EventListenerList();

    private String nextErrorMsg = null;

    private Object nextErrorMsgMutex = new Object();

    private JTable parameterTable;

    private ParameterList pList;

    public ParameterEdit() {
        super(new GridLayout(1, 0));
        this.errorArea = new JTextArea();
        this.errorArea.setEditable(false);
        this.errorArea.setLineWrap(true);

        this.pList = this.emptyList;

        this.parameterTable = new JTable(new DataModel()) {

            @Override
            public String getToolTipText(MouseEvent event) {
                ParameterList pl = ParameterEdit.this.pList;
                int row = rowAtPoint(event.getPoint());
                if ((row < 0) || (row >= pl.getArgumentCount()))
                    return null;
                Argument arg = pl.getArgument(row);
                String s = arg.getName() + " [" //$NON-NLS-1$
                        + arg.getValueType().getSimpleName() + "]"; //$NON-NLS-1$
                s += " = " + arg.getValue(); //$NON-NLS-1$
                return s;
            }
        };

        this.parameterTable.setDefaultEditor(Object.class, new DataEditor());
        this.defaultRenderer = this.parameterTable.getDefaultRenderer(Object.class);
        this.parameterTable.setDefaultRenderer(Object.class, new DataRenderer());
        this.parameterTable.setRowSelectionAllowed(false);
        this.parameterTable.setColumnSelectionAllowed(false);
        this.parameterTable.getSelectionModel().addListSelectionListener(this);

        JPanel jp = new JPanel(new BorderLayout());
        jp.add(new JScrollPane(this.parameterTable), BorderLayout.CENTER);
        jp.add(this.errorArea, BorderLayout.SOUTH);
        jp.setPreferredSize(new Dimension(250, 100));

        JScrollPane jsp = new JScrollPane(jp);
        add(jsp);

        showParameterList(null);
    }

    protected synchronized void showError(String s) {
        if (this.currentThread != null) {
            synchronized (this.currentThreadMutex) {
                this.currentThreadMutex.notify();
            }
            try {
                this.currentThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.currentThread = null;
            }
        }
        synchronized (this.nextErrorMsgMutex) {
            this.nextErrorMsg = s;
            this.currentThread = new Thread() {
                @Override
                public void run() {
                    synchronized (ParameterEdit.this.currentThreadMutex) {
                        ParameterEdit.this.errorArea.setText(ParameterEdit.this.nextErrorMsg);
                        try {
                            synchronized (ParameterEdit.this.nextErrorMsgMutex) {
                                ParameterEdit.this.nextErrorMsgMutex.notify();
                            }
                            ParameterEdit.this.currentThreadMutex.wait(5000);
                        } catch (InterruptedException e) {
                        } finally {
                            ParameterEdit.this.errorArea.setText(""); //$NON-NLS-1$
                        }
                    }
                }
            };
            try {
                this.currentThread.start();
                this.nextErrorMsgMutex.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showParameterList(ParameterList pl) {
        this.parameterTable.editingStopped(null);
        this.parameterTable.removeEditor();

        if (this.pList != null)
            this.pList.removeUpdateListener(this);

        if (pl == null)
            this.pList = this.emptyList;
        else
            this.pList = pl;

        this.parameterTable.tableChanged(new TableModelEvent(this.parameterTable
                .getModel()));
    }

    public void update(UpdateEvent e) {
        if (!(e.getSource() instanceof Argument))
            return;
        int idx = this.pList.getArgumentIndex((Argument) e.getSource());
        TableModelEvent event = new TableModelEvent(this.parameterTable.getModel(),
                idx, idx);
        EventListener[] tbls = this.listeners.getListeners(TableModelListener.class);
        for (int i = 0; i < tbls.length; i++) {
            TableModelListener listener = (TableModelListener) tbls[i];
            listener.tableChanged(event);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting())
            return;
        int i = this.parameterTable.getSelectedRow();
        if (i < 0)
            return;
        String d = this.pList.getArgument(i).getDescription();
        if (d != null) {
            SystemFrame.getInstance().messageArea.showTemporaryLegend(d);
        }
    }
}