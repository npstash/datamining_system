//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// LearningContext.java (sed.data.clasf)
// Utworzono 2005-08-01 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.def;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;

public class LearningContext {

    private AttributeSet attributeSet;

    private PlainDocument document;

    private javax.swing.text.AttributeSet textAs;

    private int trainingDatasetSize = -1;

    public LearningContext(AttributeSet as) {
        super();
        this.attributeSet = new DefaultAttributeSet(as);
        this.document = new PlainDocument();
        this.textAs = this.document.getDefaultRootElement().getAttributes();
        if (as.getClassLabelIndex() >= 0) {
            Attribute clAttr = as.getAttribute(as.getClassLabelIndex());
        }
    }

    public void addNote(String note) {
        try {
            this.document.insertString(this.document.getLength(), note + "\n", this.textAs); //$NON-NLS-1$
        } catch (BadLocationException e) {
        }
    }

    public AttributeSet getAttributeSet() {
        return this.attributeSet;
    }

    public Attribute getClassAttribute() {
        return this.attributeSet.getAttribute(this.attributeSet.getClassLabelIndex());
    }

    public Document getDocument() {
        return this.document;
    }

}