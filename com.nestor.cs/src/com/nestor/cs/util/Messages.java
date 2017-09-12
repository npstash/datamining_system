//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Messages.java (nes)
// Utworzono 2005-07-27 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private static final String BUNDLE_NAME = "com.nestor.cs.util.messages";//$NON-NLS-1$

    private static ResourceBundle RESOURCE_BUNDLE = null;

    private Messages() {
    }

    public static String getString(String key) {
        try {
        	synchronized (Messages.class) {
        		if (RESOURCE_BUNDLE == null) 
        			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
        	}
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}