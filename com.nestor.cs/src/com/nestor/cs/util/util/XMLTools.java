//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// XMLTools.java (javan.util)
// Utworzono 2005-07-20 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util;

import java.io.PrintWriter;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class XMLTools {

    public static void printNode(PrintWriter pw, Node node, String indent) {

        switch (node.getNodeType()) {

        case Node.DOCUMENT_NODE:
            Document doc = (Document) node;
            pw.print("<?xml version=\"" + doc.getXmlVersion() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            if (doc.getXmlEncoding() != null)
                pw.print(" encoding=\"" + doc.getXmlEncoding() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            pw.println("?>\n"); //$NON-NLS-1$

            // recurse on each child
            NodeList nodes = node.getChildNodes();
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    printNode(pw, nodes.item(i), ""); //$NON-NLS-1$
                }
            }
            break;

        case Node.ELEMENT_NODE:
            String name = node.getNodeName();
            pw.print(indent + "<" + name); //$NON-NLS-1$
            NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node current = attributes.item(i);
                pw.print(" " + current.getNodeName() + "=\"" //$NON-NLS-1$ //$NON-NLS-2$
                        + current.getNodeValue() + "\""); //$NON-NLS-1$
            }

            // recurse on each child
            NodeList children = node.getChildNodes();
            if (children != null) {
                if (children.getLength() > 0) {
                    pw.println(">"); //$NON-NLS-1$
                    for (int i = 0; i < children.getLength(); i++) {
                        printNode(pw, children.item(i), indent + "  "); //$NON-NLS-1$
                    }
                    pw.println(indent + "</" + name + ">"); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    pw.println("/>"); //$NON-NLS-1$
                }
            } else {
                pw.println("/>"); //$NON-NLS-1$
            }
            break;

        case Node.TEXT_NODE:
            pw.println(node.getNodeValue());
            break;
        }
    }

}