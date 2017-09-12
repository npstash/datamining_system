//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Argument.java (javan.util)
// Utworzono 2005-04-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util.param;

import java.io.InvalidClassException;
import java.lang.reflect.Constructor;

import com.nestor.cs.util.Messages;

public class Argument {

    private String description = null;

    private Comparable highBound = null;

    private boolean isReadOnly = false;

    private boolean isRequired = true;

    private Comparable lowBound = null;

    private String name = null;

    private Object value = null;

    private Class valueType = Object.class;

    public Argument() {
    }

    public Argument(Object value) {
        this.value = value;
        if (value != null) {
            this.valueType = value.getClass();
        }
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof Argument) {
            Argument arg = (Argument) arg0;
            if (!arg.getName().equals(getName()))
                return false;
            if (!arg.getLowBound().equals(getLowBound()))
                return false;
            if (!arg.getHighBound().equals(getHighBound()))
                return false;
            return true;
        } else {
            return false;
        }
    }

    public String getDescription() {
        return this.description;
    }

    public Object getHighBound() {
        return this.highBound;
    }

    public Object getLowBound() {
        return this.lowBound;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public Class getValueType() {
        return this.valueType;
    }

    public void initValue(String txtValue) throws InvalidClassException,
            InvalidArgumentValue, ArgumentValueOutOfRange {
        Object newValue;
        if (txtValue == null) {
            newValue = null;
        } else {
            if (!isTextEditable())
                throw new InvalidClassException(Messages
                        .getString("Argument.cannotInitializeWithString")); //$NON-NLS-1$
            Constructor c;
            try {
                c = this.valueType.getConstructor(new Class[] { String.class });
            } catch (Exception e) {
                throw new InvalidClassException(Messages
                        .getString("Argument.cannotFindAppropirateConstructor") //$NON-NLS-1$
                        + getName() + "."); //$NON-NLS-1$
            }
            try {
                newValue = c.newInstance(new Object[] { txtValue });
            } catch (Exception e1) {
                throw new InvalidArgumentValue(this, txtValue);
            }
        }
        setValue(newValue);
    }

    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    public boolean isRequired() {
        return this.isRequired;
    }

    public boolean isTextEditable() {
        try {
            Constructor c = this.valueType
                    .getConstructor(new Class[] { String.class });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHighBound(Comparable highBound) {
        this.highBound = highBound;
    }

    public void setLowBound(Comparable lowBound) {
        this.lowBound = lowBound;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public void setValue(Object newValue) throws InvalidClassException,
            ArgumentValueOutOfRange {
        if (this.isReadOnly)
            return;
        if (newValue == null) {
            this.value = null;
        } else {
            if (!this.valueType.isAssignableFrom(newValue.getClass())) {
                throw new InvalidClassException(Messages
                        .getString("Argument.invalidClass")); //$NON-NLS-1$
            }
            if (this.lowBound != null) {
                if (this.lowBound.compareTo(newValue) > 0) {
                    throw new ArgumentValueOutOfRange(this, newValue);
                }
            }
            if (this.highBound != null) {
                if (this.highBound.compareTo(newValue) < 0) {
                    throw new ArgumentValueOutOfRange(this, newValue);
                }
            }
            this.value = newValue;
        }
    }

    public void setValueType(Class valueType) {
        this.valueType = valueType;
        try {
            initValue(this.value.toString());
        } catch (Exception e) {
            try {
                setValue(null);
            } catch (Exception e1) {
            }
        }
    }

    @Override
    public String toString() {
        return getName() + " = " + getValue(); //$NON-NLS-1$
    }
}