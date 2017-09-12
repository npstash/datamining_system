//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SEDDocument.java (sed.ui)
// Utworzono 2005-07-10 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.diagram;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.nestor.cs.Messages;
import com.nestor.cs.tasks.def.ConfigurableTask;
import com.nestor.cs.tasks.def.MultiResultTask;
import com.nestor.cs.util.util.PropEdit;
import com.nestor.cs.util.util.param.Argument;
import com.nestor.cs.util.util.param.ArgumentList;

public class Diagram extends TaskGraph {

    protected static final String DOC_VERSION = "0.2"; //$NON-NLS-1$

    protected static final String XMLATTR_ARG_DISCARD = "discarded"; //$NON-NLS-1$

    protected static final String XMLATTR_ARG_FACE = "face"; //$NON-NLS-1$

    protected static final String XMLATTR_ARG_LINKID = "linkID"; //$NON-NLS-1$

    protected static final String XMLATTR_DOC_VER = "version"; //$NON-NLS-1$

    protected static final String XMLATTR_ELEM_CLASS = "class"; //$NON-NLS-1$

    protected static final String XMLATTR_ELEM_ID = "id"; //$NON-NLS-1$

    protected static final String XMLNODE_ARGUMENT = "argument"; //$NON-NLS-1$

    protected static final String XMLNODE_DOCUMENT = "sedDocument"; //$NON-NLS-1$

    protected static final String XMLNODE_ELEM_SERIALIZED = "serialized"; //$NON-NLS-1$

    protected static final String XMLNODE_ELEMENT = "element"; //$NON-NLS-1$

    public Diagram() {
        super();
    }

    public void closeDocument() {
    }

    public void fromXML(Document xmlDoc) throws Exception {
        super.removeAll();

        NodeList docNode = xmlDoc.getElementsByTagName(XMLNODE_DOCUMENT);
        boolean okVer = false;
        if (docNode.getLength() == 1) {
            Attr vAttr = (Attr) docNode.item(0).getAttributes().getNamedItem(
                    XMLATTR_DOC_VER);
            if (vAttr != null) {
                double v = Double.parseDouble(vAttr.getNodeValue());
                double isv = Double.parseDouble(DOC_VERSION);
                if (v == isv)
                    okVer = true;
            }
        }
        if (!okVer)
            throw new Exception(Messages
                    .getString("SEDDocument.invalidDocumentVersion")); //$NON-NLS-1$

        // najpierw tworzone sa wszystkie elementy
        // i umieszczane w tablicy haszuj�cej aby mo�na je by�o odnale��
        // po 'linkID'
        // utworzona zostanie takze talica z obliczami 'faces'
        HashMap elementMap = new HashMap();
        HashMap faceListMap = new HashMap();
        NodeList elemList = xmlDoc.getElementsByTagName(XMLNODE_ELEMENT);
        for (int i = 0; i < elemList.getLength(); i++) {
            Node elemNode = elemList.item(i);
            String className = elemNode.getAttributes().getNamedItem(
                    XMLATTR_ELEM_CLASS).getNodeValue();
            Class elemClass;
            elemClass = Class.forName(className);
            Object element = null;
            String idStr = elemNode.getAttributes().getNamedItem(
                    XMLATTR_ELEM_ID).getNodeValue();

            if (Serializable.class.isAssignableFrom(elemClass)) {
                NodeList nl = elemNode.getChildNodes();
                for (int j = 0; j < nl.getLength(); j++) {
                    Node arg = nl.item(j);
                    if (arg.getNodeName().equals(XMLNODE_ELEM_SERIALIZED)) {
                        String text = arg.getTextContent();
                        ByteArrayInputStream bin = new ByteArrayInputStream(
                                text.getBytes());
                        ObjectInputStream in;
                        in = new ObjectInputStream(bin);
                        element = in.readObject();
                    }
                    break;
                }
            }
            if (element == null)
                element = elemClass.newInstance();
            elementMap.put(new Integer(idStr), element);

            // wpisywanie do tablicy obliczy
            // kazdy z elementow tablicy haszujacej to lista 'obliczy'
            // kluczem jest referencja do elementu
            if (element instanceof MultiResultTask) {
                MultiResultTask mfe = (MultiResultTask) element;
                ArrayList faces = new ArrayList();
                for (int j = 0; j < mfe.getTaskResults().length; j++) {
                    MultiResultTask.TaskResult ef = mfe.getTaskResults()[j];
                    faces.add(ef.getElementFaceObject());
                }
                faceListMap.put(element, faces);
            }
        }// for elems

        for (int i = 0; i < elemList.getLength(); i++) {
            Node elemNode = elemList.item(i);
            String idStr = elemNode.getAttributes().getNamedItem(
                    XMLATTR_ELEM_ID).getNodeValue();
            Object element = elementMap.get(new Integer(idStr));
            NodeList argList = elemNode.getChildNodes();

            int currentArg = 0;
            for (int j = 0; j < argList.getLength(); j++) {
                Node arg = argList.item(j);
                if (arg.getNodeName().equals(XMLNODE_ARGUMENT)) {
                    if (!(element instanceof ConfigurableTask))
                        break;
                    ConfigurableTask pe = (ConfigurableTask) element;
                    Argument a = pe.getConfigurationArguments().getArgument(
                            currentArg);
                    if (a == null)
                        break;
                    currentArg++;

                    boolean discard = arg.getAttributes().getNamedItem(
                            XMLATTR_ARG_DISCARD) != null;

                    if (discard)
                        continue;

                    Node argLink = arg.getAttributes().getNamedItem(
                            XMLATTR_ARG_LINKID);
                    Node faceLink = arg.getAttributes().getNamedItem(
                            XMLATTR_ARG_FACE);

                    if (argLink != null) {
                        Object targetElement = elementMap.get(new Integer(
                                argLink.getNodeValue()));
                        if (faceLink != null) {
                            if (!(targetElement instanceof MultiResultTask))
                                continue;
                            ArrayList al = (ArrayList) faceListMap
                                    .get(targetElement);
                            int faceNum = Integer.parseInt(faceLink
                                    .getNodeValue());
                            a.setValue(al.get(faceNum));
                        } else {
                            a.setValue(targetElement);
                        }
                    } else {
                        if (a.isTextEditable()) {
                            a.initValue(PropEdit.decodeString(arg
                                    .getTextContent().trim()));
                        }
                    }
                } // if name argument
            } // for args
            TaskNode enode = new TaskNode(element);
            enode.setNodeId(Integer.parseInt(idStr));
            addElement(enode);
        }// for elems
        super.invalidateNet();
    }

    public Document toXML() throws Exception {
        HashMap elementMap = new HashMap();
        HashMap faceOwners = new HashMap();
        HashMap faceNums = new HashMap();
        for (Enumeration enumer = super.getElementNodes(); enumer.hasMoreElements();) {
            TaskGraph.TaskNode en = (TaskGraph.TaskNode) enumer.nextElement();
            elementMap.put(en.getElement(), new Integer(en.getNodeId()));

            // mapa obliczy elementu
            if (en.getElement() instanceof MultiResultTask) {
                MultiResultTask mfe = (MultiResultTask) en.getElement();
                for (int i = 0; i < mfe.getTaskResults().length; i++) {
                    MultiResultTask.TaskResult ef = mfe.getTaskResults()[i];
                    faceOwners.put(ef.getElementFaceObject(), en.getElement());
                    faceNums.put(ef.getElementFaceObject(), new Integer(i));
                }
            }
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement(XMLNODE_DOCUMENT);
        doc.appendChild(root);

        Attr verAttr = doc.createAttribute(XMLATTR_DOC_VER);
        verAttr.setValue(DOC_VERSION);
        root.setAttributeNode(verAttr);

        for (Iterator iter = elementMap.keySet().iterator(); iter.hasNext();) {
            Object element = iter.next();
            Element elementNode = doc.createElement(XMLNODE_ELEMENT);

            Attr idAttr = doc.createAttribute(XMLATTR_ELEM_ID);
            Integer id = (Integer) elementMap.get(element);
            idAttr.setValue(id.toString());
            elementNode.setAttributeNode(idAttr);

            Attr classAttr = doc.createAttribute(XMLATTR_ELEM_CLASS);
            Class cls = element.getClass();
            classAttr.setValue(cls.getName());
            elementNode.setAttributeNode(classAttr);

            if (element instanceof Serializable) {
                try {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bout);
                    out.writeObject(element);
                    Element ser = doc.createElement(XMLNODE_ELEM_SERIALIZED);
                    ser.setTextContent(bout.toString());
                    elementNode.appendChild(ser);
                } catch (Exception e) {
                }
            }

            if (element instanceof ConfigurableTask) {
                ConfigurableTask pe = (ConfigurableTask) element;
                ArgumentList al = pe.getConfigurationArguments();
                for (int i = 0; i < al.getArgumentCount(); i++) {
                    Argument arg = al.getArgument(i);
                    Object value = arg.getValue();
                    Element argumentNode = doc.createElement(XMLNODE_ARGUMENT);
                    boolean discard = true;

                    if (value != null) {
                        if (elementMap.containsKey(value)) {
                            Attr linkAttr = doc
                                    .createAttribute(XMLATTR_ARG_LINKID);
                            Integer linkId = (Integer) elementMap.get(value);
                            linkAttr.setValue(linkId.toString());
                            argumentNode.setAttributeNode(linkAttr);
                            discard = false;
                        } else if (faceOwners.containsKey(value)) {
                            Object elem = faceOwners.get(value);
                            if (elementMap.containsKey(elem)) {
                                Attr linkAttr = doc
                                        .createAttribute(XMLATTR_ARG_LINKID);
                                Integer linkId = (Integer) elementMap.get(elem);
                                linkAttr.setValue(linkId.toString());
                                argumentNode.setAttributeNode(linkAttr);
                                Attr faceAttr = doc
                                        .createAttribute(XMLATTR_ARG_FACE);
                                Integer face = (Integer) faceNums.get(value);
                                faceAttr.setValue(face.toString());
                                argumentNode.setAttributeNode(faceAttr);
                                discard = false;
                            }
                        } else if (arg.isTextEditable()) {
                            Text valueText = doc.createTextNode(PropEdit
                                    .codeString(value.toString()));
                            argumentNode.appendChild(valueText);
                            discard = false;
                        }
                    }
                    // wartosci argumentu brak lub nigdzie nie odnaleziono
                    // i nie mozna jej zapisac tekstowo. nie bedzie zapisana
                    // w pliku
                    if (discard) {
                        Attr discardAttr = doc
                                .createAttribute(XMLATTR_ARG_DISCARD);
                        argumentNode.setAttributeNode(discardAttr);
                    }
                    elementNode.appendChild(argumentNode);
                } // for arguments
            } // if pe
            root.appendChild(elementNode);
        } // for elements
        return doc;
    }

}

