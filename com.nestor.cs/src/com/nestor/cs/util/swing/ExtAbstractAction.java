//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SEDAbstractAction.java (sed.ui.actions)
// Utworzono 2005-07-22 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.swing;

import java.awt.Color;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.nestor.cs.util.util.GraphicsTools;

public abstract class ExtAbstractAction extends AbstractAction {

    public static final String BUTTON_TOOLTIP = "Button tool tip"; //$NON-NLS-1$

    public static final String ENABLED = "Enabled"; //$NON-NLS-1$

    public static final String ICON = "Icon"; //$NON-NLS-1$

    public static final String SELECTED = "Selected"; //$NON-NLS-1$

    private static Color transpColor = new Color(0, 255, 255);

    public static Icon getButtonIcon(URL url) {
        ImageIcon ic = new ImageIcon(url);
        return GraphicsTools.makeTransparentIcon(ic, transpColor);
    }

    private String buttonToolTip = null;

    private boolean enabled = true;

    private Icon icon = null;

    private boolean selected = false;

    public ExtAbstractAction() {
        super();
    }

    public ExtAbstractAction(String arg0) {
        super(arg0);
    }

    public ExtAbstractAction(String arg0, Icon arg1) {
        super(arg0, arg1);
    }

    public String getButtonToolTip() {
        return this.buttonToolTip;
    }

    public Icon getIcon() {
        return this.icon;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setButtonToolTip(String buttonToolTip) {
        this.buttonToolTip = buttonToolTip;
    }

    @Override
    public void setEnabled(boolean en) {
        putValue(ENABLED, new Boolean(en));
        firePropertyChange(ENABLED, new Boolean(this.enabled), new Boolean(en));
        this.enabled = en;
    }

    public void setIcon(Icon ic) {
        putValue(ICON, ic);
        firePropertyChange(ICON, this.icon, ic);
        this.icon = ic;
    }

    public void setSelected(boolean sel) {
        putValue(SELECTED, new Boolean(sel));
        firePropertyChange(SELECTED, new Boolean(this.selected), new Boolean(sel));
        this.selected = sel;
    }
}