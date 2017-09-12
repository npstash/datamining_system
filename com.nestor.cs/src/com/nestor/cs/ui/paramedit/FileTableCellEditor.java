//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// FileCellEditor.java (sed.ui.param)
// Utworzono 2005-07-23 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.paramedit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;

import com.nestor.cs.Messages;

public class FileTableCellEditor extends AbstractCellEditor implements
        TableCellEditor, ActionListener, DocumentListener {

    public static String getFileString(File f) {
        if (f == null)
            return ""; //$NON-NLS-1$
        try {
            File currentDir = new File("."); //$NON-NLS-1$
            String sCDir = currentDir.getCanonicalPath() + "\\"; //$NON-NLS-1$
            String fPath = f.getCanonicalPath();
            if (fPath.startsWith(sCDir))
                return fPath.substring(sCDir.length());
            else
                return fPath;
        } catch (IOException e) {
            return null;
        }
    }

    private JButton jbFindFile;

    private JPanel jpMain;

    private JTextField jtfFileField;

    private int lastCol = 0;

    private int lastRow = 0;

    private JTable lastTable = null;

    public FileTableCellEditor(File initialFile) {
        super();
        this.jpMain = new JPanel(new BorderLayout());
        this.jtfFileField = new JTextField(getFileString(initialFile));
        this.jtfFileField.getDocument().addDocumentListener(this);
        this.jbFindFile = new JButton("..."); //$NON-NLS-1$
        this.jbFindFile.setPreferredSize(new Dimension(20, 0));
        this.jbFindFile.addActionListener(this);
        this.jpMain.add(this.jtfFileField, BorderLayout.CENTER);
        this.jpMain.add(this.jbFindFile, BorderLayout.EAST);
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.jbFindFile) {
            JFileChooser jfcFile = new JFileChooser("."); //$NON-NLS-1$
            int res = jfcFile.showDialog(null, Messages
                    .getString("FileTableCellEditor.selectAFile")); //$NON-NLS-1$
            if (res == JFileChooser.APPROVE_OPTION) {
                this.jtfFileField.setText(getFileString(jfcFile.getSelectedFile()));
            }
        }
    }

    public void changedUpdate(DocumentEvent e) {
        updateTable();
    }

    public Object getCellEditorValue() {
        if (this.jtfFileField.getText().trim().equals("")) //$NON-NLS-1$
            return null;
        return this.jtfFileField.getText();
    }

    public Component getTableCellEditorComponent(JTable table, Object unused,
            boolean sel, int row, int col) {
        this.lastTable = table;
        this.lastRow = row;
        this.lastCol = col;
        return this.jpMain;
    }

    public void insertUpdate(DocumentEvent e) {
        updateTable();
    }

    public void removeUpdate(DocumentEvent arg0) {
        updateTable();
    }

    public void setValue(File f) {
        this.jtfFileField.setText(getFileString(f));
    }

    private void updateTable() {
        if (this.lastTable != null) {
            this.lastTable.setValueAt(getCellEditorValue(), this.lastRow, this.lastCol);
        }
    }

}