//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiagramDocumentLayout.java (sed.ui)
// Utworzono 2005-07-16 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.util.Random;

import com.nestor.cs.ui.document.elements.DocumentElementComponent;

public class DiagramDocumentLayout implements LayoutManager2 {

    private DiagramDocument document;

    private Dimension prefSize = null;

    public DiagramDocumentLayout(DiagramDocument doc) {
        super();
        this.document = doc;
    }

    public void addLayoutComponent(Component cmp, Object element) {
    }

    public void addLayoutComponent(String s, Component cmp) {
    }

    protected Point createCoordinates(Component cmp) {
        Random r = new Random();
        Point p = new Point(r.nextInt(preferredLayoutSize(null).width
                - cmp.getPreferredSize().width), r
                .nextInt(preferredLayoutSize(null).height
                        - cmp.getPreferredSize().height));
        return p;
    }

    public float getLayoutAlignmentX(Container c) {
        return 0;
    }

    public float getLayoutAlignmentY(Container c) {
        return 0;
    }

    public void invalidateLayout(Container c) {
        this.prefSize = null;
    }

    public void layoutContainer(Container c) {
        int n = c.getComponentCount();
        for (int i = 0; i < n; i++) {
            DocumentElementComponent dec = (DocumentElementComponent) c
                    .getComponent(i);
            Point coords = dec.getDiagramElement().getComponentLocation();
            if (coords == null) {
                coords = createCoordinates(dec);
                dec.getDiagramElement().setComponentLocation(coords);
            }
            Dimension dim = dec.getPreferredSize();
            dec.setBounds(coords.x, coords.y, dim.width, dim.height);
        }
    }

    public Dimension maximumLayoutSize(Container c) {
        return preferredLayoutSize(c);
    }

    public Dimension minimumLayoutSize(Container c) {
        return new Dimension(0, 0);
    }

    public Dimension preferredLayoutSize(Container c) {
        if (this.prefSize == null)
            this.prefSize = this.document.getDiagramSize();
        return this.prefSize;
    }

    public void removeLayoutComponent(Component ecomp) {
    }

}