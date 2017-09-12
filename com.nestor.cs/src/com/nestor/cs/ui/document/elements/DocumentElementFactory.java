//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiagramElement.java (sed.ui.diagram)
// Utworzono 2005-05-05 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document.elements;

import java.net.URL;

import javax.swing.ImageIcon;

import com.nestor.cs.Messages;
import com.nestor.cs.util.util.TreeNode;
import com.nestor.cs.util.util.param.InvalidArgumentValue;

public class DocumentElementFactory {

    private ImageIcon icon = null;

    private String name = null;

    private DocumentElementTreeComponent treeComponent = null;

    private Class type = null;

    public DocumentElementFactory(TreeNode descriptor)
            throws InvalidArgumentValue {
        super();
        String nameKey = (String) descriptor.attributes.get("nameKey"); //$NON-NLS-1$
        if (nameKey != null)
            this.name = Messages.getString(nameKey);
        try {
            this.type = Class.forName((String) descriptor.attributes.get("class")); //$NON-NLS-1$
            this.type.getConstructor(new Class[] {});
            if (this.name == null)
                this.name = this.type.getName();
        } catch (Exception e) {
            throw new InvalidArgumentValue(e.getMessage());
        }
        String iconString = (String) descriptor.attributes.get("icon"); //$NON-NLS-1$
        if (iconString != null) {
            URL iconURL = this.getClass().getResource(iconString);
            this.icon = new ImageIcon(iconURL);
        }
    }

    public DocumentElement createDiagramElement() {
        if (getElementClass() == null)
            return null;
        try {
            DocumentElement de = new DocumentElement(this, getElementClass()
                    .newInstance());
            return de;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DocumentElement createDiagramElement(Object withElement) {
        if (getElementClass() == null)
            return null;
        if (!getElementClass().equals(withElement.getClass()))
            return null;
        DocumentElement de = new DocumentElement(this, withElement);
        return de;
    }

    public Class getElementClass() {
        return this.type;
    }

    public ImageIcon getElementIcon() {
        return this.icon;
    }

    public String getElementName() {
        return this.name;
    }

    public DocumentElementTreeComponent getTreeComponent() {
        if (this.treeComponent == null)
            this.treeComponent = new DocumentElementTreeComponent(this);
        return this.treeComponent;
    }

}