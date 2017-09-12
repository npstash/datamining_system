//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiagramPanel.java (sed.ui)
// Utworzono 2005-07-16 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.nestor.cs.Messages;
import com.nestor.cs.diagram.TaskGraph;
import com.nestor.cs.tasks.def.MultiResultTask;
import com.nestor.cs.ui.SystemFrame;
import com.nestor.cs.ui.document.elements.DocumentElement;
import com.nestor.cs.ui.document.elements.DocumentElementComponent;
import com.nestor.cs.ui.document.elements.DocumentElementTransferHandler;
import com.nestor.cs.ui.event.DiagramDocumentChangeEvent;
import com.nestor.cs.ui.event.DiagramDocumentChangeListener;
import com.nestor.cs.ui.event.EndLinkingEvent;
import com.nestor.cs.ui.event.EndLinkingListener;
import com.nestor.cs.ui.event.InitLinkingEvent;
import com.nestor.cs.ui.event.InitLinkingListener;
import com.nestor.cs.ui.event.ObjectSelectionEvent;
import com.nestor.cs.ui.event.ObjectSelectionListener;
import com.nestor.cs.util.event.EventDispatcher;
import com.nestor.cs.util.event.SimpleEventDispatcher;
import com.nestor.cs.util.util.GraphicsTools;
import com.nestor.cs.util.util.grav.Grav;
import com.nestor.cs.util.util.grav.GravRectangle;
import com.nestor.cs.util.util.param.Argument;

public class DiagramPanel extends JPanel implements MouseListener,
        MouseMotionListener, DiagramDocumentChangeListener,
        InitLinkingListener, EndLinkingListener, PopupMenuListener, Scrollable,
        PropertyChangeListener {

    private class DiagramElementMouseListener extends MouseAdapter implements
            MouseListener, MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent event) {
            DocumentElementComponent dec = (DocumentElementComponent) event
                    .getComponent();
            Point nc = event.getPoint();
            nc.x += dec.getLocation().x
                    - ((DiagramPanel.this.clickedAt != null) ? DiagramPanel.this.clickedAt.x : 0);
            nc.y += dec.getLocation().y
                    - ((DiagramPanel.this.clickedAt != null) ? DiagramPanel.this.clickedAt.y : 0);
            dec.getDiagramElement().setComponentLocation(nc);
            DiagramPanel.this.linksToGravs.clear();
            DiagramPanel.this.revalidate();
            DiagramPanel.this.repaint();
        }

        @Override
        public void mouseEntered(MouseEvent event) {
            DocumentElementComponent dec = (DocumentElementComponent) event
                    .getComponent();
            dec.setSelected(true);
        }

        @Override
        public void mouseExited(MouseEvent event) {
            DocumentElementComponent dec = (DocumentElementComponent) event
                    .getComponent();
            dec.setSelected(false);
        }

        @Override
        public void mouseMoved(MouseEvent event) {
        }

        @Override
        public void mousePressed(MouseEvent event) {
            DiagramPanel.this.clickedAt = event.getPoint();
            Point panelPoint = DiagramPanel.this.getMousePosition();
            DocumentElementComponent dec = (DocumentElementComponent) event
                    .getComponent();

            if (event.getButton() == MouseEvent.BUTTON1) {
                synchronized (DiagramPanel.this.linkLock) {
                    if (DiagramPanel.this.linkContext == null) {
                        fireObjectSelected(dec.getDiagramElement().getTask());
                    } else {
                        MultiResultTask.TaskResult[] faces = DiagramPanel.this.linkContext
                                .getPossibleLinkFaces(dec.getDiagramElement());
                        if (faces.length > 0) {
                            if (faces.length == 1) {
                                DiagramPanel.this.linkContext.setLinkEnd(faces[0]
                                        .getElementFaceObject());
                                linkingEnded();
                            } else {
                                tryStartFacePopup(panelPoint, faces);
                            } // else
                        } // if
                    } // else
                } // sync

            } else if (event.getButton() == MouseEvent.BUTTON3) {
                synchronized (DiagramPanel.this.linkLock) {
                    if (DiagramPanel.this.linkContext == null) {
                        tryStartArgumentPopup(panelPoint, dec);
                    }
                } // synchronized
            } // else if
        }

        private void tryStartArgumentPopup(Point at,
                DocumentElementComponent dec) {
            if (!DiagramPanel.this.getDocument().isEditable())
                return;
            if (at != null) {
                ArgumentSelectionPopup asm = new ArgumentSelectionPopup(dec
                        .getDiagramElement());
                asm.addInitLinkingListener(DiagramPanel.this);
                asm.addPopupMenuListener(DiagramPanel.this);
                asm.show(DiagramPanel.this, at.x, at.y);
            }
        }

        private void tryStartFacePopup(Point at,
                MultiResultTask.TaskResult[] faces) {
            if (at != null) {
                TaskResultSelectionPopup fsp = new TaskResultSelectionPopup(
                        faces);
                fsp.addEndLinkingListener(DiagramPanel.this);
                fsp.addPopupMenuListener(DiagramPanel.this);
                fsp.show(DiagramPanel.this, at.x, at.y);
            } // if
        }
    }

    private class LinkContext {

        private Argument linkArg = null;

        private DocumentElement linkDE = null;

        public LinkContext(Argument link, DocumentElement linkedFrom) {
            super();
            this.linkArg = link;
            this.linkDE = linkedFrom;
        }

        private boolean canAcceptLink(DocumentElement de) {
            synchronized (DiagramPanel.this.linkLock) {
                if (de == this.linkDE)
                    return false;
                if (de == null)
                    return false;
                boolean directLink = isProperLinkEndObject(de.getTask());
                if ((!directLink) && (de.getTask() instanceof MultiResultTask)) {
                    MultiResultTask.TaskResult[] faces = ((MultiResultTask) de
                            .getTask()).getTaskResults();
                    for (int i = 0; i < faces.length; i++) {
                        if (isProperLinkEndObject(faces[i]
                                .getElementFaceObject()))
                            return true;
                    }
                    return false;
                } else
                    return directLink;
            }
        }

        private MultiResultTask.TaskResult[] getPossibleLinkFaces(
                DocumentElement de) {
            synchronized (DiagramPanel.this.linkLock) {
                ArrayList al = new ArrayList();

                if (isProperLinkEndObject(de.getTask())) {
                    MultiResultTask.TaskResult self = new MultiResultTask.TaskResult(
                            de.getFactory().getElementName(), de.getTask(), de
                                    .getTask());
                    al.add(self);
                }
                if (de.getTask() instanceof MultiResultTask) {
                    MultiResultTask.TaskResult[] faces = ((MultiResultTask) de
                            .getTask()).getTaskResults();
                    for (int i = 0; i < faces.length; i++) {
                        if (isProperLinkEndObject(faces[i]
                                .getElementFaceObject()))
                            al.add(faces[i]);
                    }
                }
                MultiResultTask.TaskResult[] result = new MultiResultTask.TaskResult[al
                        .size()];
                return (MultiResultTask.TaskResult[]) al.toArray(result);
            }
        }

        private boolean isProperLinkEndObject(Object obj) {
            synchronized (DiagramPanel.this.linkLock) {
                if (obj == null)
                    return false;
                if (obj == this.linkDE.getTask())
                    return false;
                Class toClass = obj.getClass();
                Class argClass = this.linkArg.getValueType();
                return argClass.isAssignableFrom(toClass);
            }
        }

        private void setLinkEnd(Object obj) {
            synchronized (DiagramPanel.this.linkLock) {
                try {
                    if (obj != null) {
                        if (isProperLinkEndObject(obj)) {
                            this.linkArg.setValue(obj);
                            DiagramPanel.this.document.invalidateNet();
                        }
                    }
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }
    }

    private static final int ARROW_ARM_ANGLE = 30;

    private static final int ARROW_ARM_LENGHT = 15;

    private Point clickedAt = null;

    private DiagramElementMouseListener commonListener = new DiagramElementMouseListener();

    private DiagramDocument document;

    private EventDispatcher eventDispatcher = null;

    private Grav grav;

    private DiagramDocumentLayout layout;

    private LinkContext linkContext = null;

    private Object linkLock = new Object();

    private HashMap linksToGravs = new HashMap();

    public DiagramPanel(DiagramDocument doc) {
        super();
        this.document = doc;
        this.layout = new DiagramDocumentLayout(this.document);
        setLayout(this.layout);
        this.document.addDiagramDocumentChangeListener(this);
        this.document.addPropertyChangeListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        setTransferHandler(DocumentElementTransferHandler.sharedInstance);
        setProperBackgroud();

        this.grav = new Grav(null, null, 10);
        this.grav.setDelay(0);
        this.grav.setStep(5);
        this.grav.setMaxIterations(10);
        this.grav.setMoveAll(false);
        this.grav.setTillZero(true);
        this.grav.setNotify(false);
    }

    public void addObjectSelectionListener(ObjectSelectionListener listener) {
        getEventDispatcher().addListener(ObjectSelectionListener.class,
                listener);
    }

    public void documentChanged(DiagramDocumentChangeEvent event) {
        Component cmp;
        switch (event.getChange()) {
        case DiagramDocumentChangeEvent.CHANGE_DOCUMENT_CLOSED:
            this.document.removeDiagramDocumentChangeListener(this);
            return;
        case DiagramDocumentChangeEvent.CHANGE_ELEMENT_ADDED:
            cmp = event.getDiagramElement().getDiagramComponent();
            add(cmp);
            cmp.addMouseListener(this.commonListener);
            cmp.addMouseMotionListener(this.commonListener);
            revalidate();
            break;
        case DiagramDocumentChangeEvent.CHANGE_ELEMENT_REMOVED:
            cmp = event.getDiagramElement().getDiagramComponent();
            remove(cmp);
            cmp.removeMouseListener(this.commonListener);
            cmp.removeMouseMotionListener(this.commonListener);
            revalidate();
            break;
        case DiagramDocumentChangeEvent.CHANGE_DOCUMENT_INVALIDATION:
            this.linksToGravs.clear();
            revalidate();
            repaint();
            break;
        }
    }

    public void endLink(EndLinkingEvent event) {
        synchronized (this.linkLock) {
            if (this.linkContext != null) {
                this.linkContext.setLinkEnd(event.getElementFace()
                        .getElementFaceObject());
                if (event.getSource() instanceof TaskResultSelectionPopup) {
                    TaskResultSelectionPopup fsp = (TaskResultSelectionPopup) event
                            .getSource();
                    fsp.removeEndLinkingListener(this);
                    fsp.removePopupMenuListener(this);
                }
                //                linkContext = null;
                linkingEnded();
            }
        }
    }

    public void fireObjectSelected(Object obj) {
        ObjectSelectionEvent event = new ObjectSelectionEvent(this, obj);
        getEventDispatcher().notifyListeners(ObjectSelectionListener.class,
                event);
    }

    public DiagramDocument getDocument() {
        return this.document;
    }

    public EventDispatcher getEventDispatcher() {
        if (this.eventDispatcher == null) {
            this.eventDispatcher = new SimpleEventDispatcher();
        }
        return this.eventDispatcher;
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableBlockIncrement(Rectangle r, int orientation,
            int direction) {
        return 20;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public int getScrollableUnitIncrement(Rectangle r, int orientation,
            int direction) {
        return 20;
    }

    public void initLink(InitLinkingEvent event) {
        synchronized (this.linkLock) {
            if (event.getArgument().getValue() == null) {
                SystemFrame.getInstance().messageArea
                        .showTemporaryLegend(Messages
                                .getString("DiagramPanel.0")); //$NON-NLS-1$
                // rozpocz�to ��czenie element�w
                this.linkContext = new LinkContext(event.getArgument(), event
                        .getDiagramElement());

                // pod�wietlenie element�w mog�cych by�
                // celem ��czenia
                Enumeration enumer = getDocument().getDiagramElements();
                while (enumer.hasMoreElements()) {
                    DocumentElement de = (DocumentElement) enumer.nextElement();
                    de.getDiagramComponent().setCanAcceptLink(
                            this.linkContext.canAcceptLink(de));
                }

            } else {
                // usuni�cie ��czenia
                try {
                    event.getArgument().setValue(null);
                    this.document.invalidateNet();
                    revalidate();
                } catch (Exception e) {
                }
            }
        }
        if (event.getSource() instanceof ArgumentSelectionPopup) {
            ArgumentSelectionPopup asm = (ArgumentSelectionPopup) event
                    .getSource();
            asm.removeInitLinkingListener(this);
            asm.removePopupMenuListener(this);
        }
    }

    private void linkingEnded() {
        synchronized (this.linkLock) {
            this.linkContext = null;

            Enumeration enumer = getDocument().getDiagramElements();
            while (enumer.hasMoreElements()) {
                DocumentElement de = (DocumentElement) enumer.nextElement();
                de.getDiagramComponent().clearCanAcceptLink();
            }

            revalidate();
            repaint();
        }
    }

    public void mouseClicked(MouseEvent arg0) {
        linkingEnded();
    }

    public void mouseDragged(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mouseMoved(MouseEvent arg0) {
        synchronized (this.linkLock) {
            if (this.linkContext != null) {
                repaint();
            }
        }
    }

    public void mousePressed(MouseEvent arg0) {
    }

    public void mouseReleased(MouseEvent arg0) {
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);

        boolean addGravStrings = false;
        if (this.linksToGravs.isEmpty()) {
            addGravStrings = true;
            for (Enumeration enumer = getDocument().getDiagramElements(); enumer
                    .hasMoreElements();) {
                DocumentElement de = (DocumentElement) enumer.nextElement();
                GravRectangle gr = new GravRectangle(de.getDiagramComponent()
                        .getBounds());
                gr.setImmobile(true);
                this.linksToGravs.put(de, gr);
            }
        }

        TaskGraph.TaskNodePair[] links = getDocument().getLinks();
        for (int i = 0; i < links.length; i++) {
            Object elementTo = links[i].getUsedBy().getElement();
            Object elementFrom = links[i].getUsing().getElement();
            DocumentElement deFrom = getDocument().getDiagramElementForElement(
                    elementFrom);
            DocumentElement deTo = getDocument().getDiagramElementForElement(
                    elementTo);
            if (deFrom.getComponentLocation() == null)
                continue;
            if (deTo.getComponentLocation() == null)
                continue;

            Point from = deFrom.getComponentMiddleLocation();
            Point to = deTo.getComponentMiddleLocation();

            if (addGravStrings) {
                GravString gs;
                if (this.linksToGravs.containsKey(links[i])) {
                    gs = (GravString) this.linksToGravs.get(links[i]);
                } else {
                    String desc = links[i].getLinkByArgument().getName();
                    gs = new GravString(desc);
                    gs.initCoordinates(g.getFontMetrics(), (to.x + from.x) / 2,
                            (to.y + from.y) / 2);
                    this.linksToGravs.put(links[i], gs);
                }
            }

            Point modFrom = GraphicsTools.getComponentBorderingPointOnLine(
                    deFrom.getDiagramComponent(), from, to);

            Point modTo = GraphicsTools.getComponentBorderingPointOnLine(deTo
                    .getDiagramComponent(), to, from);
            GraphicsTools.drawArrow(g, modFrom, modTo, ARROW_ARM_LENGHT,
                    ARROW_ARM_ANGLE);
        }
        paintGravStrings(g);
        paintLinking(g);

    }

    private void paintGravStrings(Graphics g) {
        this.grav.setGravShapes(this.linksToGravs.values());
        Rectangle fit = new Rectangle(getPreferredSize());
        this.grav.setFit(fit);
        this.grav.startPushAlgorithm();

        for (Iterator iter = this.linksToGravs.values().iterator(); iter.hasNext();) {
            Object next = iter.next();
            if (next instanceof GravString) {
                GravString gs = (GravString) next;
                gs.paint(g);
            }
        }
    }

    private void paintLinking(Graphics g) {
        synchronized (this.linkLock) {
            if (this.linkContext != null) {
                Point from = this.linkContext.linkDE.getComponentMiddleLocation();
                Point to = getMousePosition();
                if (to != null) {
                    g.drawLine(from.x, from.y, to.x, to.y);
                }
            }
        }
    }

    public void popupMenuCanceled(PopupMenuEvent event) {
        if (event.getSource() instanceof ArgumentSelectionPopup) {
            ArgumentSelectionPopup asm = (ArgumentSelectionPopup) event
                    .getSource();
            asm.removeInitLinkingListener(this);
            asm.removePopupMenuListener(this);
        }
        if (event.getSource() instanceof TaskResultSelectionPopup) {
            TaskResultSelectionPopup fsp = (TaskResultSelectionPopup) event
                    .getSource();
            fsp.removeEndLinkingListener(this);
            fsp.removePopupMenuListener(this);
        }
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
    }

    public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getSource() == getDocument()) {
            if (e.getPropertyName().equals(TaskGraph.EDITABLE)) {
                setProperBackgroud();
            }
        }
    }

    public void removeObjectSelectionListener(ObjectSelectionListener listener) {
        getEventDispatcher().removeListener(ObjectSelectionListener.class,
                listener);
    }

    private void setProperBackgroud() {
        if (getDocument().isEditable()) {
            setBackground(Color.WHITE);
        } else {
            setBackground(new Color(235, 235, 235));
        }
    }

}