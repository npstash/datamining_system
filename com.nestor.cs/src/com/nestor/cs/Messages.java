//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Messages.java (sed)
// Utworzono 2005-07-27 w �rodowisku Eclipse 3.0
//

package com.nestor.cs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private static final String BUNDLE_NAME = "com.nestor.cs.messages";//$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    private Messages() {
    }

    private static File fMR = new File("missingResources.txt"); //$NON-NLS-1$

    private static PrintWriter pwMR = null;

    private static HashSet saved = new HashSet();

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            if (pwMR == null) {
                try {
                    pwMR = new PrintWriter(new FileOutputStream(fMR));
                } catch (Exception e1) {
                    pwMR = null;
                }
            }
            if (pwMR != null) {
                if (!saved.contains(key)) {
                    pwMR.println(key);
                    saved.add(key);
                }
                pwMR.flush();
            }
            return '!' + key + '!';
        }
    }

    @Override
    public void finalize() {
        if (pwMR != null) {
            pwMR.close();
            pwMR = null;
        }
    }
}