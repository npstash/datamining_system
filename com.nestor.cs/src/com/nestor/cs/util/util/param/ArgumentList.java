//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ArgumentList.java (javan.util)
// Utworzono 2005-04-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util.param;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.nestor.cs.util.util.MapOverList;

public class ArgumentList {

    private int nextUnnamed = 0;

    private String getNextUnnamed() {
        return "" + this.nextUnnamed++; //$NON-NLS-1$
    }

    public static boolean areArgumentsFilled(Argument[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].getValue() == null)
                return false;
        }
        return true;
    }

    private MapOverList arguments = null;

    public ArgumentList() {
        this.arguments = new MapOverList(new ArrayList(), new LinkedHashMap());
    }

    public void addArgument(Argument arg) {
        if (arg.getName() == null)
            arg.setName(getNextUnnamed());
        this.arguments.asMap().put(arg.getName(), arg);
    }

    public void addArgumentsFrom(Object o) {
        Field[] flds = o.getClass().getFields();
        for (int i = 0; i < flds.length; i++) {
            if (flds[i].getType().isAssignableFrom(Argument.class)) {
                try {
                    Object obj = flds[i].get(o);
                    if (obj instanceof Argument) {
                        addArgument((Argument) obj);
                    }
                } catch (Exception e) {
                    throw new Error();
                }
            }
        }
    }

    public boolean areRequiredArgumentsFilled() {
        return areArgumentsFilled(getRequiredArguments());
    }

    public Argument getArgument(int i) {
        return (Argument) this.arguments.asList().get(i);
    }

    public Argument getArgument(String name) {
        return (Argument) this.arguments.asMap().get(name);
    }

    public int getArgumentCount() {
        return this.arguments.asList().size();
    }

    public int getArgumentIndex(Argument arg) {
        return this.arguments.asList().indexOf(arg);
    }

    public Argument[] getArguments() {
        return (Argument[]) this.arguments.asList().toArray(new Argument[] {});
    }

    public Argument[] getRequiredArguments() {
        Collection required = new ArrayList();
        for (Iterator iter = this.arguments.asList().iterator(); iter.hasNext();) {
            Argument arg = (Argument) iter.next();
            if (arg.isRequired()) {
                required.add(arg);
            }
        }
        return (Argument[]) required.toArray(new Argument[] {});
    }

    public void setArgumentsReadOnly(boolean readOnly) {
        for (Iterator iter = this.arguments.asList().iterator(); iter.hasNext();) {
            Argument arg = (Argument) iter.next();
            arg.setReadOnly(readOnly);
        }
    }
}