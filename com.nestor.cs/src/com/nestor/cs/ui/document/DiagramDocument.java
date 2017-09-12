//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiagramDocument.java (sed.ui)
// Utworzono 2005-07-16 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nestor.cs.Messages;
import com.nestor.cs.diagram.Diagram;
import com.nestor.cs.diagram.TaskGraph;
import com.nestor.cs.tasks.def.DiagramComponent;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.ui.document.elements.DocumentElement;
import com.nestor.cs.ui.document.elements.DocumentElementFactory;
import com.nestor.cs.ui.document.elements.DocumentElementFactoryRegistry;
import com.nestor.cs.ui.event.DiagramDocumentChangeEvent;
import com.nestor.cs.ui.event.DiagramDocumentChangeListener;
import com.nestor.cs.util.proc.ProcessingException;

public class DiagramDocument extends Diagram {

    private class DiagramElementEnumerator implements Enumeration {

        private Iterator iter;

        private DiagramElementEnumerator(Iterator it) {
            this.iter = it;
        }

        public boolean hasMoreElements() {
            return this.iter.hasNext();
        }

        public Object nextElement() {
            return this.iter.next();
        }
    }

    public class DiagramElementNode extends TaskGraph.TaskNode {

        public DiagramElementNode(TaskNode enode) {
            super(enode.getElement());
            setNodeId(enode.getNodeId());
        }

        public DiagramElementNode(Object element) {
            super(element);
        }

        @Override
        protected boolean doElementProcessing() throws ProcessingException {
            DocumentElement de = getDiagramElementForElement(getElement());
            if (de != null)
                de.getDiagramComponent().setIsProcessed(true);
            try {
                boolean res = super.doElementProcessing();
                if (getElement() instanceof DiagramComponent) {
                    DiagramComponent dc = (DiagramComponent) getElement();
                    if (dc.doModalDialog()) {
                        SystemFrame.getInstance().createComponentDialog(dc);
                    } else if (dc.doInternalFrame()) {
                        SystemFrame.getInstance().mainDesktop
                                .createComponentInternalFrame(dc);
                    } else {
                        SystemFrame.getInstance().createComponentFrame(dc);
                    }
                    return true;
                }
                return res;
            } catch (ProcessingException e) {
                throw e;
            } catch (RuntimeException e) {
                throw new ProcessingException(e);
            } finally {
                if (de != null)
                    de.getDiagramComponent().setIsProcessed(false);
            }
        }
    }

    public static final String PROPERTY_TITLE = "Title"; //$NON-NLS-1$

    protected static final String XMLATTR_ELEM_LOC = "location"; //$NON-NLS-1$

    private static Dimension maxDimension(Dimension d1, Dimension d2) {
        Dimension res = new Dimension((d1.width > d2.width) ? d1.width
                : d2.width, (d1.height > d2.height) ? d1.height : d2.height);
        return res;
    }

    private Dimension currSize = null;

    private String documentName = ""; //$NON-NLS-1$

    /**
     * Kluczem w tej mapie jest element dokumentu, natomiast warto�ci�
     * odpowiadaj�cy mu element diagramu (Object -> DiagramElement)
     */
    private HashMap elementMap;

    private String title = Messages.getString("DiagramDocument.newDocument"); //$NON-NLS-1$

    public DiagramDocument() {
        super();
    }

    public void addDiagramDocumentChangeListener(
            DiagramDocumentChangeListener listener) {
        getEventDispatcher().addListener(DiagramDocumentChangeListener.class,
                listener);
    }

    public synchronized void addDiagramElement(DocumentElement de)
            throws CannotEdit {
        super.addElement(new DiagramElementNode(de.getTask()));
        getElementMap().put(de.getTask(), de);
        fireDiagramDocumentChanged(de,
                DiagramDocumentChangeEvent.CHANGE_ELEMENT_ADDED);
    }

    @Override
    public synchronized void addElement(TaskNode en) throws CannotEdit {
        DocumentElementFactory factory = (DocumentElementFactory) DocumentElementFactoryRegistry.registeredFactories
                .get(en.getElement().getClass());
        if (factory != null) {
            super.addElement(new DiagramElementNode(en));
            DocumentElement de = factory.createDiagramElement(en.getElement());
            getElementMap().put(de.getTask(), de);
            fireDiagramDocumentChanged(de,
                    DiagramDocumentChangeEvent.CHANGE_ELEMENT_ADDED);
        }
    }

    @Override
    public synchronized void addElement(Object element) throws CannotEdit {
        DocumentElementFactory factory = (DocumentElementFactory) DocumentElementFactoryRegistry.registeredFactories
                .get(element.getClass());
        if (factory != null) {
            super.addElement(new DiagramElementNode(element));
            DocumentElement de = factory.createDiagramElement(element);
            getElementMap().put(element, de);
            fireDiagramDocumentChanged(de,
                    DiagramDocumentChangeEvent.CHANGE_ELEMENT_ADDED);
        }
    }

    @Override
    public void closeDocument() {
        fireDiagramDocumentChanged(null,
                DiagramDocumentChangeEvent.CHANGE_DOCUMENT_CLOSED);
        super.closeDocument();
    }

    private void fireDiagramDocumentChanged(DocumentElement de, int change) {
        DiagramDocumentChangeEvent event = new DiagramDocumentChangeEvent(this,
                de, change);
        getEventDispatcher().notifyListeners(
                DiagramDocumentChangeListener.class, event);
    }

    @Override
    public void fromXML(Document xmlDoc) throws Exception {
        super.fromXML(xmlDoc);

        NodeList nl = xmlDoc.getElementsByTagName(XMLNODE_ELEMENT);
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);

            Node nLoc = n.getAttributes().getNamedItem(XMLATTR_ELEM_LOC);
            if (nLoc == null)
                continue;
            StringTokenizer st = new StringTokenizer(nLoc.getNodeValue(), ":"); //$NON-NLS-1$
            Point p = new Point(Integer.parseInt(st.nextToken()), Integer
                    .parseInt(st.nextToken()));

            int id = Integer.parseInt(n.getAttributes().getNamedItem(
                    XMLATTR_ELEM_ID).getNodeValue());
            Object element = getElementById(id);
            if (element == null)
                continue;
            DocumentElement de = getDiagramElementForElement(element);
            de.setComponentLocation(p);
        }
        invalidateNet();
    }

    public DocumentElement getDiagramElementForElement(Object element) {
        return (DocumentElement) getElementMap().get(element);
    }

    public synchronized Enumeration getDiagramElements() {
        return new DiagramElementEnumerator(getElementMap().values().iterator());
    }

    public Dimension getDiagramSize() {
        Dimension dim = getPreferredDiagramSize();
        if (this.currSize == null) {
            this.currSize = dim;
        } else {
            this.currSize = maxDimension(this.currSize, dim);
        }
        return this.currSize;
    }

    public String getDocumentName() {
        return this.documentName;
    }

    private Map getElementMap() {
        if (this.elementMap == null)
            this.elementMap = new HashMap();
        return this.elementMap;
    }

    public Dimension getPreferredDiagramSize() {
        Dimension prefSize = new Dimension(300, 300);
        Enumeration enumer = getDiagramElements();
        while (enumer.hasMoreElements()) {
            DocumentElement de = (DocumentElement) enumer.nextElement();
            Point p = de.getComponentLocation();

            // mo�e si� zdarzy�, �e wsp�rz�dne nie
            // zosta�y jeszcze wczytane
            if (p == null)
                continue;
            Dimension ded = de.getDiagramComponent().getSize();
            if (ded.width + p.x > prefSize.width)
                prefSize.width = ded.width + p.x;
            if (ded.height + p.y > prefSize.height)
                prefSize.height = ded.height + p.y;
        }
        return prefSize;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public void invalidateNet() throws CannotEdit {
        super.invalidateNet();
        fireDiagramDocumentChanged(null,
                DiagramDocumentChangeEvent.CHANGE_DOCUMENT_INVALIDATION);
    }

    public void removeDiagramDocumentChangeListener(
            DiagramDocumentChangeListener listener) {
        getEventDispatcher().removeListener(
                DiagramDocumentChangeListener.class, listener);
    }

    public synchronized void removeDiagramElement(DocumentElement de)
            throws CannotEdit {
        super.removeElement(de.getTask());
        getElementMap().remove(de.getTask());
        fireDiagramDocumentChanged(de,
                DiagramDocumentChangeEvent.CHANGE_ELEMENT_REMOVED);
    }

    @Override
    public synchronized void removeElement(Object element) throws CannotEdit {
        removeDiagramElement((DocumentElement) getElementMap().get(element));
    }

    // rozmiar diagramu zostanie powiekszony jesli dim jest wiekszy od
    // dotychczasowej wielkosci
    public void setDiagramSize(Dimension dim) {
        Dimension prefSize = getPreferredDiagramSize();
        this.currSize = maxDimension(dim, prefSize);
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public void setTitle(String title) {
        String oldTitle = this.title;
        this.title = title;
        firePropertyChanged(PROPERTY_TITLE, oldTitle, this.title);
    }

    @Override
    public Document toXML() throws Exception {
        Document doc = super.toXML();

        NodeList nl = doc.getElementsByTagName(XMLNODE_ELEMENT);
        for (int i = 0; i < nl.getLength(); i++) {
            if (!(nl.item(i) instanceof Element))
                continue;
            Element n = (Element) nl.item(i);
            int id = Integer.parseInt(n.getAttributes().getNamedItem(
                    XMLATTR_ELEM_ID).getNodeValue());

            Object element = getElementById(id);
            if (element == null)
                continue;
            DocumentElement de = getDiagramElementForElement(element);
            Attr elemLoc = doc.createAttribute(XMLATTR_ELEM_LOC);
            Point cmpLoc = de.getComponentLocation();
            elemLoc.setValue(cmpLoc.x + ":" + cmpLoc.y); //$NON-NLS-1$
            n.setAttributeNode(elemLoc);
        }
        return doc;
    }
}