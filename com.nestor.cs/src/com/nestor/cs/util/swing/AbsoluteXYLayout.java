//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbsoluteXYLayout.java (nes.swing)
// Utworzono 2005-08-04 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.util.HashMap;

public class AbsoluteXYLayout implements LayoutManager2 {

    private float offsetX = 0;

    private float offsetY = 0;

    public AbsoluteXYLayout() {
        super();
    }

    /**
     * @param offsetX -
     *            warto�� od 0 do 1. je�li 0, komponenty b�d� uk�adane tak, �e
     *            ich lewa strona b�dzie mia�a wsp�rz�dn� X zadanego punktu.
     *            je�li 1, na wsp�rz�dnej X zadanego punktu znajdzie si� prawa
     *            strona komponentu.
     * @param offsetY
     */
    public AbsoluteXYLayout(float offsetX, float offsetY) {
        super();
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    private HashMap coordinates = new HashMap();

    public void addLayoutComponent(Component cmp, Object element) {
        if (element instanceof Point)
            this.coordinates.put(cmp, element);
    }

    public void addLayoutComponent(String s, Component cmp) {
        int x = 1;
    }

    public float getLayoutAlignmentX(Container c) {
        return 0;
    }

    public float getLayoutAlignmentY(Container c) {
        return 0;
    }

    public void invalidateLayout(Container c) {
    }

    public void layoutContainer(Container c) {
        int n = c.getComponentCount();
        for (int i = 0; i < n; i++) {
            Component cmp = c.getComponent(i);
            Point coords = (Point) this.coordinates.get(cmp);
            if (coords == null)
                continue;

            Dimension dim = cmp.getPreferredSize();
            int ox = (int) (this.offsetX * (dim.width));
            int oy = (int) (this.offsetY * (dim.height));
            cmp.setBounds(coords.x - ox, coords.y - oy, dim.width, dim.height);
        }
    }

    public Dimension maximumLayoutSize(Container c) {
        return preferredLayoutSize(c);
    }

    public Dimension minimumLayoutSize(Container c) {
        return new Dimension(0, 0);
    }

    public Dimension preferredLayoutSize(Container c) {
        int maxx = 0;
        int maxy = 0;
        int n = c.getComponentCount();
        for (int i = 0; i < n; i++) {
            Component cmp = c.getComponent(i);
            Point p = (Point) this.coordinates.get(cmp);
            if (p == null)
                continue;
            Dimension dim = cmp.getMaximumSize();
            int ox = (int) ((1 - this.offsetX) * (dim.width));
            int oy = (int) ((1 - this.offsetY) * (dim.height));
            int nx = p.x + ox + 1;
            int ny = p.y + oy + 1;
            if (nx > maxx)
                maxx = nx;
            if (ny > maxy)
                maxy = ny;
        }
        return new Dimension(maxx, maxy);
    }

    public void removeLayoutComponent(Component ecomp) {
        this.coordinates.remove(ecomp);
    }

}