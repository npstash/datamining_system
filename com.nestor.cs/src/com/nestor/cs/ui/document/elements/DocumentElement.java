//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Element.java (sed.ui.element)
// Utworzono 2005-07-16 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document.elements;

import java.awt.Point;

public class DocumentElement {

    private Point componentLocation = null;

    private DocumentElementComponent diagramComponent;

    private Object task;

    private DocumentElementFactory factory = null;

    protected DocumentElement(DocumentElementFactory factory, Object element) {
        this.factory = factory;
        this.task = element;
        this.diagramComponent = new DocumentElementComponent(this, factory);
    }

    protected DocumentElement(Object element) {
        this.task = element;
        this.diagramComponent = new DocumentElementComponent(this, this.factory);
    }

    public Point getComponentLocation() {
        return this.componentLocation;
    }

    public Point getComponentMiddleLocation() {
        return new Point(this.componentLocation.x
                + getDiagramComponent().getPreferredSize().width / 2,
                this.componentLocation.y
                        + getDiagramComponent().getPreferredSize().height / 2);
    }

    public DocumentElementComponent getDiagramComponent() {
        if (this.diagramComponent == null)
            this.diagramComponent = new DocumentElementComponent(this, getTask());
        return this.diagramComponent;
    }

    public Object getTask() {
        return this.task;
    }

    public DocumentElementFactory getFactory() {
        return this.factory;
    }

    public void setComponentLocation(Point componentLocation) {
        this.componentLocation = componentLocation;
    }
}