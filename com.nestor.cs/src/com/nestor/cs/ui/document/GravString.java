//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// GravString.java (sed.ui.diagram)
// Utworzono 2005-07-26 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import com.nestor.cs.util.util.grav.GravRectangle;

public class GravString extends GravRectangle {

    private Color lineColor = Color.GREEN;

    private int pointX = 0;

    private int pointY = 0;

    private String str;

    private int strh = 0;

    private int strw = 0;

    public GravString(String s) {
        super();
        this.str = s;
    }

    public int getOrigPointX() {
        return this.pointX;
    }

    public int getOrigPointY() {
        return this.pointY;
    }

    public void initCoordinates(FontMetrics fm, int centerx, int centery) {
        this.pointX = centerx;
        this.pointY = centery;
        this.strw = fm.stringWidth(this.str);
        int startx = centerx - this.strw / 2;
        this.strh = fm.getHeight();
        int starty = centery - this.strh / 2;
        setRect(startx, starty, this.strw, this.strh);
    }

    public void paint(Graphics g) {
        Color prevColor = g.getColor();
        if (this.lineColor != null) {
            g.setColor(this.lineColor);
            g.drawLine(this.pointX, this.pointY, (int) getCenterX(), (int) getCenterY());
        }
        g.setColor(Color.WHITE);
        g.fillRect((int) this.x, (int) this.y, (int) this.width, (int) this.height);
        g.setColor(Color.BLACK);
        g.drawRect((int) this.x, (int) this.y, (int) this.width, (int) this.height);
        g.drawString(this.str, (int) getCenterX() - this.strw / 2, (int) getCenterY()
                + this.strh / 3);
        g.setColor(prevColor);
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }
}