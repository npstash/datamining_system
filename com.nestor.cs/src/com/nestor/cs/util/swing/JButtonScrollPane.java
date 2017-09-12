//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// JButtonScrollPane.java (sed.ui)
// Utworzono 2005-07-26 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.swing;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class JButtonScrollPane extends JScrollPane {

    protected ArrayList horizontalComponents = new ArrayList();

    protected int horizontalHeight = 0;

    protected ArrayList horizontalWidths = new ArrayList();

    protected int minimumBarSize = 20;

    protected ArrayList verticalComponents = new ArrayList();

    protected ArrayList verticalHeights = new ArrayList();

    protected int verticalWidth = 0;

    public JButtonScrollPane() {
        super();
    }

    public JButtonScrollPane(int horizontalHeight, int verticalWidth) {
        super();
        this.horizontalHeight = horizontalHeight;
        this.verticalWidth = verticalWidth;
    }

    public void addHorizontalComponent(Component cmp, int width) {
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(cmp);
        this.horizontalComponents.add(cmp);
        this.horizontalWidths.add(new Integer(width));
    }

    public void addVerticalComponent(Component cmp, int height) {
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(cmp);
        this.verticalComponents.add(cmp);
        this.verticalHeights.add(new Integer(height));
    }

    @Override
    public void doLayout() {
        super.doLayout();
        Rectangle bounds = getBounds();
        Rectangle vport = this.viewport.getBounds();
        vport.x = bounds.x;
        vport.y = bounds.y;

        if (!this.verticalComponents.isEmpty()) {
            Rectangle vrect = this.verticalScrollBar.getBounds();
            if (this.verticalWidth == 0)
                this.verticalWidth = vrect.width;
            vrect.x = bounds.x + bounds.width - this.verticalWidth;
            vrect.y = bounds.y;
            vrect.width = this.verticalWidth;
            vrect.height = bounds.height;

            int totalHeight = 0;
            for (int i = 0; i < this.verticalComponents.size(); i++) {
                Component cmp = (Component) this.verticalComponents.get(i);
                int height = ((Integer) this.verticalHeights.get(i)).intValue();
                if (vrect.height - totalHeight - height <= this.minimumBarSize)
                    break;
                Rectangle r = new Rectangle();
                r.x = vrect.x;
                r.y = vrect.y + totalHeight;
                r.width = vrect.width;
                r.height = height;
                cmp.setBounds(r);
                totalHeight += height;
            }
            vrect.y += totalHeight;
            vrect.height -= totalHeight;
            this.verticalScrollBar.setBounds(vrect);
            vport.width = bounds.width - this.verticalWidth - 1;
        }

        if (!this.horizontalComponents.isEmpty()) {
            Rectangle hrect = this.horizontalScrollBar.getBounds();
            if (this.horizontalHeight == 0)
                this.horizontalHeight = hrect.height;
            hrect.x = bounds.x;
            hrect.y = bounds.y + bounds.height - this.horizontalHeight;
            hrect.width = bounds.width;
            hrect.height = this.horizontalHeight;

            int totalWidth = 0;
            for (int i = 0; i < this.horizontalComponents.size(); i++) {
                Component cmp = (Component) this.horizontalComponents.get(i);
                int width = ((Integer) this.horizontalWidths.get(i)).intValue();
                if (hrect.width - totalWidth - width <= this.minimumBarSize)
                    break;
                Rectangle r = new Rectangle();
                r.x = hrect.x + totalWidth;
                r.y = hrect.y;
                r.height = hrect.height;
                r.width = width;
                cmp.setBounds(r);
                totalWidth += width;
            }
            hrect.x += totalWidth;
            hrect.width -= totalWidth;
            this.horizontalScrollBar.setBounds(hrect);
            vport.height = bounds.height - this.horizontalHeight - 1;
        }

        this.viewport.setBounds(vport);
    }

    public void setHorizontalHeight(int horizontalHeight) {
        this.horizontalHeight = horizontalHeight;
    }

    public void setMinimumBarSize(int minimumBarSize) {
        this.minimumBarSize = minimumBarSize;
    }

    public void setVerticalWidth(int verticalWidth) {
        this.verticalWidth = verticalWidth;
    }
}