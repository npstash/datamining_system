//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SelectionBorderedPanel.java (sed.ui.element)
// Utworzono 2005-07-14 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document.elements;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

public class SelectionBorderedPanel extends JPanel {

    private boolean selected;

    private Border selectedBorder;

    private Border unselectedBorder;

    public SelectionBorderedPanel() {
        super();
        this.selectedBorder = new EtchedBorder();
        this.unselectedBorder = new EmptyBorder(this.selectedBorder.getBorderInsets(this));
        this.selected = false;
        setBorder(this.unselectedBorder);
    }

    public void setSelected(boolean sel) {
        if (sel == this.selected)
            return;
        if (sel)
            setBorder(this.selectedBorder);
        else
            setBorder(this.unselectedBorder);
        this.selected = sel;
    }

    public void setSelectedBorder(Border selectedBorder) {
        this.selectedBorder = selectedBorder;
    }

    public void setUnselectedBorder(Border unselectedBorder) {
        this.unselectedBorder = unselectedBorder;
    }
}