//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// TreeComponent.java (sed.ui.diagram)
// Utworzono 2005-07-10 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document.elements;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class DocumentElementTreeComponent extends SelectionBorderedPanel {

    protected DocumentElementTreeComponent(DocumentElementFactory element) {
        super();
        setBackground(Color.WHITE);
        if (element.getElementIcon() != null) {
            ImageIcon ic2 = new ImageIcon(element.getElementIcon().getImage()
                    .getScaledInstance(32, 32, Image.SCALE_SMOOTH));
            setLayout(new BorderLayout());
            add(new JLabel(ic2), BorderLayout.WEST);
            add(new JLabel(element.getElementName()), BorderLayout.CENTER);
        } else {
            add(new JLabel(element.getElementName()));
        }
    }

}