//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SharedActionRegistry.java (sed.ui.actions)
// Utworzono 2005-07-22 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.actions;

import java.util.Hashtable;

import javax.swing.Action;

import com.nestor.cs.Messages;

public abstract class SharedActionRegistry {

    private static Hashtable actions = new Hashtable();

    public static Action getActionSharedInstance(Class c)
            throws ClassCastException {
        if (actions.containsKey(c)) {
            return (Action) actions.get(c);
        }
        if (!Action.class.isAssignableFrom(c)) {
            throw new ClassCastException(c
                    + Messages.getString("SharedActionRegistry.isNotA") //$NON-NLS-1$
                    + Action.class.getName());
        }
        Object obj = null;
        try {
            obj = c.newInstance();
        } catch (Exception e) {
            throw new ClassCastException(
                    Messages.getString("SharedActionRegistry.cannotInitialize") + c + "(" + e.getLocalizedMessage() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        actions.put(c, obj);
        return (Action) obj;
    }
}