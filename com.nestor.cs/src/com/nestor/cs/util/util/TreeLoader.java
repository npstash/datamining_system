//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ClassTreeLoader.java (javan.util)
// Utworzono 2005-07-03 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TreeLoader {

    public static TreeNode load(InputStream inputStream) throws Exception {
        //      Get Document Builder Factory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Turn off validation, and turn off namespaces
        factory.setValidating(false);
        factory.setNamespaceAware(false);

        // Obtain a document builder object
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Parse the document
        Document doc = builder.parse(inputStream);

        NodeList mapps = doc.getElementsByTagName("node"); //$NON-NLS-1$
        for (int i = 0; i < mapps.getLength(); i++) {
            Node node = mapps.item(i);
            return new TreeNode(node);
        }
        return null;
    }

    public static TreeNode load(String xmlFile) throws Exception {
        FileInputStream fis = new FileInputStream(xmlFile);
        TreeNode tn = load(fis);
        fis.close();
        return tn;
    }

}

