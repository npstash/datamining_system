//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// TableCellRendererFactory.java (sed.ui.param)
// Utworzono 2005-07-23 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.paramedit;

import java.awt.Component;
import java.awt.Font;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import com.nestor.cs.util.util.param.Argument;

public abstract class TableCellRendererFactory {

    private static JCheckBox booleanRenderer = null;

    private static FileTableCellEditor fileRenderer = null;

    private static IntegerTableCellEditor intRenderer = null;

    private static Component emptyUneditable = null;

    public synchronized static Component getSharedTableCellRenderer(Argument arg) {
        if (arg.getValueType().equals(Boolean.class)) {
            if (booleanRenderer == null)
                booleanRenderer = new JCheckBox();
            boolean b = false;
            if (arg.getValue() != null)
                b = ((Boolean) arg.getValue()).booleanValue();
            booleanRenderer.setSelected(b);
            return booleanRenderer;

        } else if (arg.getValueType().equals(Integer.class)) {
            if (intRenderer == null)
                intRenderer = new IntegerTableCellEditor((Integer) arg
                        .getValue(), (Integer) arg.getLowBound(),
                        (Integer) arg.getHighBound());
            else
                intRenderer.setValue((Integer) arg.getValue());
            return intRenderer.getTableCellEditorComponent(null, null, false,
                    0, 0);

        } else if (arg.getValueType().equals(File.class)) {
            if (fileRenderer == null)
                fileRenderer = new FileTableCellEditor((File) arg.getValue());
            else
                fileRenderer.setValue((File) arg.getValue());
            return fileRenderer.getTableCellEditorComponent(null, null, false,
                    0, 0);

        } else {
            if (!arg.isTextEditable()) {
                if (arg.getValue() == null) {
                    if (emptyUneditable == null)
                        emptyUneditable = new JLabel(" - "); //$NON-NLS-1$
                    return emptyUneditable;
                } else {
                    String s = arg.getValue().getClass().getSimpleName();
                    JLabel ret = new JLabel(s);
                    ret.setFont(Font.getFont("plain")); //$NON-NLS-1$
                    return ret;
                }
            }
        }
        return null;
    }

}