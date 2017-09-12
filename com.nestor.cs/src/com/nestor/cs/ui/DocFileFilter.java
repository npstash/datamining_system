//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DocFileFilter.java (sed.ui)
// Utworzono 2005-07-20 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.nestor.cs.Messages;

public class DocFileFilter extends FileFilter {

    public static final String DOC_FILENAME_EXT = "xml"; //$NON-NLS-1$

    public DocFileFilter() {
        super();
    }

    @Override
    public boolean accept(File f) {
        if (f.getName().toLowerCase().endsWith("." + DOC_FILENAME_EXT)) //$NON-NLS-1$
            return true;
        if (f.isDirectory())
            return true;
        return false;
    }

    @Override
    public String getDescription() {
        return Messages.getString("DocFileFilter.diagramFiles"); //$NON-NLS-1$
    }

}