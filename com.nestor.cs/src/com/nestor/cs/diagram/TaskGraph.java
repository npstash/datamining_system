//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ProcessorNet.java (sed.data.processing)
// Utworzono 2005-07-08 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.diagram;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Stack;

import com.nestor.cs.Messages;
import com.nestor.cs.tasks.def.ConfigurableTask;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.tasks.def.MultiResultTask;
import com.nestor.cs.util.event.EventDispatcher;
import com.nestor.cs.util.event.SimpleEventDispatcher;
import com.nestor.cs.util.proc.AbstractTask;
import com.nestor.cs.util.proc.Controller;
import com.nestor.cs.util.proc.Initiable;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.proc.Task;
import com.nestor.cs.util.util.MapOverList;
import com.nestor.cs.util.util.param.Argument;
import com.nestor.cs.util.util.param.ArgumentList;

public class TaskGraph extends AbstractTask {

    public class CannotEdit extends Exception {

        public CannotEdit() {
            super(Messages.getString("ElementNet.editImpossible")); //$NON-NLS-1$
        }
    }

    public class TaskEnumerator implements Enumeration {

        private Iterator iter = null;

        public TaskEnumerator(Iterator iter) {
            this.iter = iter;
        }

        public boolean hasMoreElements() {
            return this.iter.hasNext();
        }

        public Object nextElement() {
            if (!hasMoreElements())
                return null;
            return ((TaskNode) this.iter.next()).element;
        }
    }

    public class TaskNode {

        private Object element;

        private int nodeId = TaskGraph.this.getNextNodeId();

        private boolean tag = false;

        private ArrayList usedByElements = new ArrayList();

        private ArrayList usingElements = new ArrayList();

        public TaskNode(Object element) {
            this.element = element;
        }

        protected boolean doElementProcessing() throws ProcessingException {
            boolean result = false;
            if (this.element instanceof Initiable) {
                Initiable initiable = (Initiable) this.element;
                Controller.init(initiable);
                result = true;
            }
            if (this.element instanceof Task) {
                Task ip = (Task) this.element;
                Controller.run(ip);
                result = true;
            }
            return result;
        }

        public Object getElement() {
            return this.element;
        }

        public int getNodeId() {
            return this.nodeId;
        }

        public boolean ignoreStartup() {
            // przetwarzanie w tym w�le jest igonorowane
            // jesli element ten nie u�ywa innych element�w,
            // nie jest u�ywany przez inne elementy
            // oraz posiada nieuzupe�nione warto�ci argument�w
            if (!this.usedByElements.isEmpty())
                return false;
            if (!this.usingElements.isEmpty())
                return false;
            if (getElement() instanceof ConfigurableTask) {
                ConfigurableTask ce = (ConfigurableTask) getElement();
                if (!ce.getConfigurationArguments()
                        .areRequiredArgumentsFilled())
                    return true;
            }
            return false;
        }

        public void setNodeId(int nodeId) {
            if (nodeId >= TaskGraph.this.nextNodeId) {
                TaskGraph.this.nextNodeId = nodeId + 1;
            }
            this.nodeId = nodeId;
        }
    }

    public class TaskNodeEnumerator implements Enumeration {

        private Iterator iter;

        public TaskNodeEnumerator(Iterator iter) {
            super();
            this.iter = iter;
        }

        public boolean hasMoreElements() {
            return this.iter.hasNext();
        }

        public Object nextElement() {
            return this.iter.next();
        }
    }

    public class TaskNodePair {

        private MultiResultTask.TaskResult face;

        private Argument linkByArgument;

        private TaskNode usedBy;

        private TaskNode using;

        private TaskNodePair(TaskNode usedBy, TaskNode using, Argument arg) {
            this(usedBy, using, arg, null);
        }

        private TaskNodePair(TaskNode usedBy, TaskNode using, Argument arg,
                MultiResultTask.TaskResult face) {
            this.usedBy = usedBy;
            this.using = using;
            this.linkByArgument = arg;
            this.face = face;
        }

        public Argument getLinkByArgument() {
            return this.linkByArgument;
        }

        public TaskNode getUsedBy() {
            return this.usedBy;
        }

        public TaskNode getUsing() {
            return this.using;
        }

    }

    public static final String EDITABLE = "Editable"; //$NON-NLS-1$

    private EventDispatcher dispatcher = null;

    private boolean editable = true;

    private MapOverList faceMap = new MapOverList();

    private ArrayList links = new ArrayList();

    private Stack loopStack = new Stack();

    private int nextNodeId = 0;

    private MapOverList nodeMap = new MapOverList();

    private boolean validNet = false;

    public TaskGraph() {
        super();
        setEditable(true);
    }

    public synchronized void addElement(TaskNode en) throws CannotEdit {
        if (!isEditable())
            throw new CannotEdit();
        this.nodeMap.asMap().put(en.getElement(), en);
        if (en.getElement() instanceof MultiResultTask) {
            MultiResultTask.TaskResult[] faces = ((MultiResultTask) en
                    .getElement()).getTaskResults();
            for (int i = 0; i < faces.length; i++) {
                this.faceMap.asMap().put(faces[i].getElementFaceObject(), faces[i]);
            }
        }
        invalidateNet();
    }

    public synchronized void addElement(Object element) throws CannotEdit {
        if (!isEditable())
            throw new CannotEdit();
        TaskNode en = new TaskNode(element);
        this.nodeMap.asMap().put(element, en);
        if (element instanceof MultiResultTask) {
            MultiResultTask.TaskResult[] faces = ((MultiResultTask) element)
                    .getTaskResults();
            for (int i = 0; i < faces.length; i++) {
                this.faceMap.asMap().put(faces[i].getElementFaceObject(), faces[i]);
            }
        }
        invalidateNet();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getEventDispatcher()
                .addListener(PropertyChangeListener.class, listener);
    }

    private void buildElementNet() {
        this.links.clear();
        for (Iterator iter = this.nodeMap.asList().iterator(); iter.hasNext();) {
            TaskNode n = (TaskNode) iter.next();
            n.usedByElements.clear();
            n.usingElements.clear();
            n.tag = false;
        }

        for (Iterator iter = this.nodeMap.asList().iterator(); iter.hasNext();) {
            TaskNode n = (TaskNode) iter.next();

            if (n.element instanceof ConfigurableTask) {
                ConfigurableTask dp = (ConfigurableTask) n.element;
                ArgumentList al = dp.getConfigurationArguments();
                for (int i = 0; i < al.getArgumentCount(); i++) {
                    Argument arg = al.getArgument(i);
                    if (arg.getValue() == null)
                        continue;

                    TaskNode nodeTo = null;
                    MultiResultTask.TaskResult face = null;

                    if (this.nodeMap.asMap().containsKey(arg.getValue())) {
                        nodeTo = (TaskNode) this.nodeMap.asMap().get(arg.getValue());
                    } else if (this.faceMap.asMap().containsKey(arg.getValue())) {

                        // else - 'face' moze byc samym elementem, wiec
                        // nalezy uniknac dwukrotnego dodania po��czenia
                        face = (MultiResultTask.TaskResult) this.faceMap.asMap()
                                .get(arg.getValue());
                        nodeTo = (TaskNode) this.nodeMap.asMap().get(
                                face.getOwnerElement());
                    }
                    if (nodeTo != null) {
                        n.usingElements.add(nodeTo);
                        nodeTo.usedByElements.add(n);
                        this.links.add(new TaskNodePair(n, nodeTo, arg));
                    }
                }
            } // if
        }
        this.validNet = true;
    }

    private boolean checkLoop(TaskNode n) {
        if (this.loopStack.contains(n))
            return true;
        if (n.tag)
            return false;
        n.tag = true;
        if (!n.usedByElements.isEmpty()) {
            this.loopStack.push(n);
            for (Iterator iter = n.usedByElements.iterator(); iter.hasNext();) {
                TaskNode next = (TaskNode) iter.next();
                if (checkLoop(next))
                    return true;
            }
            this.loopStack.pop();
        }
        return false;
    }

    private void cleanup() {
        setEditable(true);
        for (Enumeration enumer = getElements(); enumer.hasMoreElements();) {
            Object element = enumer.nextElement();
            if (element instanceof ConfigurableTask) {
                ConfigurableTask ce = (ConfigurableTask) element;
                ce.getConfigurationArguments().setArgumentsReadOnly(false);
            }
        }
        buildElementNet();
    }

    private void clearTags() {
        for (Iterator iter = this.nodeMap.asList().iterator(); iter.hasNext();) {
            TaskNode next = (TaskNode) iter.next();
            next.tag = false;
        }
    }

    private TaskNode findNextStarting() {
        ArrayList startingList = new ArrayList();

        // wyszukanie wszystkich w�z��w, kt�rych przetwarzania
        // mo�e by� rozpocz�te
        for (Iterator iter = this.nodeMap.asList().iterator(); iter.hasNext();) {
            TaskNode next = (TaskNode) iter.next();
            if (next.usingElements.isEmpty() && !next.tag
                    && !next.ignoreStartup()) {
                startingList.add(next);
            }
        }

        // wybranie spo�r�d nich tego, kt�ry jest u�ywany przez
        // najmniejsz� liczb� innych element�w.
        TaskNode minUsed = null;

        for (int i = 0; i < startingList.size(); i++) {
            TaskNode next = (TaskNode) startingList.get(i);
            if (i == 0) {
                minUsed = next;
                continue;
            }
            if (next.usedByElements.size() < minUsed.usedByElements.size()) {
                minUsed = next;
            }
        }
        return minUsed;
    }

    private TaskNode findNextUntagged() {
        for (Iterator iter = this.nodeMap.asList().iterator(); iter.hasNext();) {
            TaskNode next = (TaskNode) iter.next();
            if (!next.tag)
                return next;
        }
        return null;
    }

    protected void firePropertyChanged(String propertyName, Object oldValue,
            Object newValue) {
        PropertyChangeEvent e = new PropertyChangeEvent(this, propertyName,
                oldValue, newValue);
        getEventDispatcher().notifyListeners(PropertyChangeListener.class, e);
    }

    protected synchronized Object getElementById(int id) {
        for (Enumeration enumer = getElementNodes(); enumer.hasMoreElements();) {
            TaskNode en = (TaskNode) enumer.nextElement();
            if (id == en.getNodeId())
                return en.getElement();
        }
        return null;
    }

    public Enumeration getElementNodes() {
        return new TaskNodeEnumerator(this.nodeMap.asList().iterator());
    }

    public Enumeration getElements() {
        return new TaskEnumerator(this.nodeMap.asList().iterator());
    }

    protected EventDispatcher getEventDispatcher() {
        if (this.dispatcher == null) {
            this.dispatcher = new SimpleEventDispatcher();

        }
        return this.dispatcher;
    }

    public TaskNodePair[] getLinks() {
        if (!this.validNet) {
            buildElementNet();
        }
        return (TaskNodePair[]) this.links.toArray(new TaskNodePair[this.links.size()]);
    }

    private int getNextNodeId() {
        return this.nextNodeId++;
    }

    private boolean hasLoops() {
        if (!this.validNet) {
            buildElementNet();
        }
        if (this.nodeMap.asList().isEmpty())
            return false;
        clearTags();
        this.loopStack.clear();
        for (TaskNode check = findNextUntagged(); check != null; check = findNextUntagged())
            if (checkLoop(check))
                return true;
        return false;
    }

    public void initProcess() throws ProcessingException {
        buildElementNet();

        for (Enumeration enumer = getElementNodes(); enumer.hasMoreElements();) {
            TaskNode node = (TaskNode) enumer.nextElement();
            if (node.ignoreStartup())
                continue;

            Object element = node.getElement();
            if (element instanceof ConfigurableTask) {
                ArgumentList al = ((ConfigurableTask) element)
                        .getConfigurationArguments();
                if (!al.areRequiredArgumentsFilled())
                    throw new MissingArgumentValue(al);
                al.setArgumentsReadOnly(true);
            }
        }

        if (hasLoops()) {
            String key = "ElementNet.startImpossibleDueToCycles"; //$NON-NLS-1$
            throw new ProcessingException(Messages.getString(key));
        }
        clearTags();

        setEditable(false);
    }

    public void invalidateNet() throws CannotEdit {
        if (!isEditable())
            throw new CannotEdit();
        //        setProcessState(IterativeProcess.NOTINITED);
        this.validNet = false;
    }

    public boolean isEditable() {
        return this.editable;
    }

    public synchronized void removeAll() throws CannotEdit {
        if (!isEditable())
            throw new CannotEdit();
        while (!this.nodeMap.asList().isEmpty()) {
            TaskNode en = (TaskNode) this.nodeMap.asList().get(0);
            removeElement(en.getElement());
        }
    }

    public synchronized void removeElement(Object element) throws CannotEdit {
        if (!isEditable())
            throw new CannotEdit();

        if (element instanceof ConfigurableTask) {
            ConfigurableTask ce = (ConfigurableTask) element;
            ArgumentList al = ce.getConfigurationArguments();
            al.setArgumentsReadOnly(false);
            for (int i = 0; i < al.getArgumentCount(); i++) {
                try {
                    al.getArgument(i).setValue(null);
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }

        if (this.nodeMap.asMap().remove(element) != null) {

            // nalezy usun�� wska�niki do oblicz elementu. w tym
            // celu tworze tablice z elementami ktorej
            // bede porownywal wartosci argumentow innych elementow
            MultiResultTask.TaskResult[] faces;
            if (element instanceof MultiResultTask)
                faces = ((MultiResultTask) element).getTaskResults();
            else
                faces = new MultiResultTask.TaskResult[] {};

            // usuwanie obliczy z tablicy haszuj�cej
            for (int i = 0; i < faces.length; i++) {
                this.faceMap.asMap().remove(faces[i].getElementFaceObject());
            }

            // w innych elementach typu ConfigurableElement
            // nale�y wyczyci� warto�ci argument�w wskazuj�cych
            // na usuwany element
            for (Enumeration enumer = getElements(); enumer.hasMoreElements();) {
                Object el = enumer.nextElement();
                if (el instanceof ConfigurableTask) {
                    ConfigurableTask ce = (ConfigurableTask) el;

                    // sprawdzanie kolejno argument�w
                    for (int i = 0; i < ce.getConfigurationArguments()
                            .getArgumentCount(); i++) {
                        Argument arg = ce.getConfigurationArguments()
                                .getArgument(i);
                        boolean clearArgument = false;
                        if (arg.getValue() == element)
                            clearArgument = true;
                        else {
                            // sprawdzanie obliczy elementu
                            for (int j = 0; j < faces.length; j++) {
                                if (arg.getValue() == faces[j]
                                        .getElementFaceObject()) {
                                    clearArgument = true;
                                    break;
                                }
                            }
                        }

                        if (clearArgument) {
                            try {
                                ce.getConfigurationArguments().getArgument(i)
                                        .setValue(null);
                            } catch (Exception e) {
                            }
                        }
                    } // for
                }
            } // for

            invalidateNet();
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getEventDispatcher().removeListener(PropertyChangeListener.class,
                listener);
    }

    private void setEditable(boolean b) {
        boolean old = this.editable;
        this.editable = b;
        firePropertyChanged(EDITABLE, new Boolean(old), new Boolean(this.editable));
    }

    public boolean stepProcess() throws ProcessingException {
        TaskNode starting = findNextStarting();

        if (starting != null) {
            starting.doElementProcessing();
            starting.tag = true;
            for (Iterator iter = starting.usedByElements.iterator(); iter
                    .hasNext();) {
                TaskNode next = (TaskNode) iter.next();
                next.usingElements.remove(starting);
            }
            starting.usedByElements.clear();
            stepComplete();
            return true;
        }
        cleanup();
        stepComplete();
        return false;
    }

    @Override
    public boolean stopProcess() {
        boolean res = super.stopProcess();
        if (res)
            cleanup();
        return res;
    }

}