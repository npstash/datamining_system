//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ImageTools.java (javan.util)
// Utworzono 2004-09-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

import javax.swing.ImageIcon;

public abstract class GraphicsTools {

    public static void drawArrow(Graphics g, Point2D from, Point2D to,
            int armLength, int armAngle) {
        int dx = (int) (to.getX() - from.getX());
        int dy = (int) (to.getY() - from.getY());
        if ((dx == 0) && (dy == 0))
            return;
        Point2D.Double toRotate = new Point2D.Double();

        int sgn = (dy >= -dx) ? -1 : 1;
        if ((dy < 0) && (dx > 0) && (dy == -dx))
            sgn = 1;

        if (Math.abs(dx) > Math.abs(dy)) {
            double a = (double) dy / (double) dx;
            double ddx = Math.sqrt(armLength * armLength / (1 + (a * a)));
            double ddy = a * ddx;
            toRotate.x = to.getX() + sgn * ddx;
            toRotate.y = to.getY() + sgn * ddy;
        } else {
            double a = (double) dx / (double) dy;
            double ddy = Math.sqrt(armLength * armLength / (1 + (a * a)));
            double ddx = a * ddy;
            toRotate.x = to.getX() + sgn * ddx;
            toRotate.y = to.getY() + sgn * ddy;
        }
        Point2D rotatedA = GraphicsTools.rotatePoint(to, toRotate, armAngle);
        Point2D rotatedB = GraphicsTools.rotatePoint(to, toRotate, -armAngle);

        g.drawLine((int) from.getX(), (int) from.getY(), (int) to.getX(),
                (int) to.getY());
        //        g.drawOval((int) from.getX() - 3, (int) from.getY() - 3, 6, 6);
        g.drawLine((int) to.getX(), (int) to.getY(), (int) rotatedA.getX(),
                (int) rotatedA.getY());
        g.drawLine((int) to.getX(), (int) to.getY(), (int) rotatedB.getX(),
                (int) rotatedB.getY());
    }

    /**
     * @param c -
     *            komponent na kt�rego obrze�u chcemy znale�� punkt przeci�cia z
     *            lini� (from - to)
     * @param from -
     *            punkt wewn�trz komponentu
     * @param to -
     *            punkt na zewn�trz komponentu
     * @return
     */
    public static Point getComponentBorderingPointOnLine(Component c,
            Point from, Point to) {
        Dimension dim = c.getPreferredSize();
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double ca = (double) dim.height / (double) dim.width;
        int part;
        if (dx == 0) {
            part = 1;
        } else {
            double a = dy / dx;
            if (Math.abs(ca) > Math.abs(a))
                part = 2;
            else
                part = 1;
        }
        int x, y;
        if (part == 1) {
            int ddy = dim.height / 2;
            y = from.y + (int) Math.signum(dy) * ddy;
            if (dy != 0) {
                int ddx = (int) Math.abs(ddy / dy * dx);
                x = from.x + (int) Math.signum(dx) * ddx;
            } else
                x = from.x;
        } else {
            int ddx = dim.width / 2;
            x = from.x + (int) Math.signum(dx) * ddx;
            if (dy != 0) {
                int ddy = (int) Math.abs(ddx / dx * dy);
                y = from.y + (int) Math.signum(dy) * ddy;
            } else
                y = from.y;
        }
        return new Point(x, y);
    }

    public static ImageIcon makeTransparentIcon(ImageIcon icon,
            final Color color) {
        ImageFilter filter = new RGBImageFilter() {
            // zapamietanie koloru transparencji wraz z ustawionymi alfa-bitami
            private int transparencyColor = (color.getRGB() | 0xFF000000);

            @Override
            public int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == this.transparencyColor) {
                    // to ten kolor - zerowanie alfa-bitow
                    return 0x00FFFFFF & rgb;
                } else {
                    // przepuszczenie innych kolorow
                    return rgb;
                }
            }
        };
        ImageProducer ip = new FilteredImageSource(icon.getImage().getSource(),
                filter);
        return new ImageIcon(Toolkit.getDefaultToolkit().createImage(ip));
    }

    public static Point2D rotatePoint(Point2D around, Point2D rotate,
            double degAngle) {
        double dxa = rotate.getX() - around.getX();
        double dya = rotate.getY() - around.getY();

        double radAngle = degAngle / 180d * Math.PI;

        double rx = (Math.cos(radAngle) * dxa - Math.sin(radAngle) * dya);
        double ry = (Math.sin(radAngle) * dxa + Math.cos(radAngle) * dya);

        Point2D.Double rotated = new Point2D.Double(rx + around.getX(), ry
                + around.getY());
        return rotated;
    }

}