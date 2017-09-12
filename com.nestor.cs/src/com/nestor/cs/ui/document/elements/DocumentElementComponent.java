//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiagramRenderer.java (sed.ui.diagram)
// Utworzono 2005-07-10 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document.elements;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class DocumentElementComponent extends SelectionBorderedPanel {

    public static int BORDER_SIZE = 2;

    private Border blueBorder = BorderFactory.createLineBorder(Color.BLUE,
            BORDER_SIZE);

    private DocumentElement diagramElement;

    private Border greenBorder = BorderFactory.createLineBorder(Color.GREEN,
            BORDER_SIZE);

    private JPanel innerPanel = new JPanel();

    private Border noneBorder = BorderFactory.createLineBorder(Color.WHITE,
            BORDER_SIZE);

    private Border redBorder = BorderFactory.createLineBorder(Color.RED,
            BORDER_SIZE);

    protected DocumentElementComponent(DocumentElement de,
            DocumentElementFactory factory) {
        super();
        add(this.innerPanel);
        setBackground(Color.WHITE);
        this.innerPanel.setBackground(Color.WHITE);
        this.innerPanel.setBorder(this.noneBorder);
        this.diagramElement = de;
        if (factory.getElementIcon() != null) {
            this.innerPanel.add(new JLabel(factory.getElementIcon()));
            setToolTipText(factory.getElementName());
        } else {
            this.innerPanel.add(new JLabel(factory.getElementName()));
        }
    }

    protected DocumentElementComponent(DocumentElement de, Object unnamedElement) {
        super();
        this.diagramElement = de;
        setLayout(new BorderLayout());
        add(new JLabel(unnamedElement.getClass().getSimpleName()),
                BorderLayout.CENTER);
    }

    public void clearCanAcceptLink() {
        Border newBorder = this.noneBorder;
        setInnerBorder(newBorder);
    }

    public DocumentElement getDiagramElement() {
        return this.diagramElement;
    }

    public void setCanAcceptLink(boolean b) {
        Border newBorder = (b) ? this.greenBorder : this.redBorder;
        setInnerBorder(newBorder);
    }

    private void setInnerBorder(Border newBorder) {
        if (this.innerPanel.getBorder() != newBorder)
            this.innerPanel.setBorder(newBorder);
    }

    public void setIsProcessed(boolean b) {
        Border newBorder = (b) ? this.blueBorder : this.noneBorder;
        setInnerBorder(newBorder);
    }

}