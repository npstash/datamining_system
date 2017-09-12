//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// WrappingParameter.java (javan.util)
// Utworzono 2005-07-23 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util.param;

import java.io.InvalidClassException;

import com.nestor.cs.util.event.EventDispatcher;

public class ParameterWrapper extends Parameter {

    private Argument wrappedArg;

    public ParameterWrapper(Argument arg, EventDispatcher dispatcher) {
        super(arg.getValue(), dispatcher);
        this.wrappedArg = arg;
    }

    public ParameterWrapper(Argument arg, ParameterList pl) {
        super(arg.getName(), arg.getValue(), pl);
        this.wrappedArg = arg;
    }

    @Override
    public boolean equals(Object arg0) {
        return this.wrappedArg.equals(arg0);
    }

    @Override
    public String getDescription() {
        return this.wrappedArg.getDescription();
    }

    @Override
    public Object getHighBound() {
        return this.wrappedArg.getHighBound();
    }

    @Override
    public Object getLowBound() {
        return this.wrappedArg.getLowBound();
    }

    @Override
    public String getName() {
        return this.wrappedArg.getName();
    }

    @Override
    public Object getValue() {
        return this.wrappedArg.getValue();
    }

    @Override
    public Class getValueType() {
        return this.wrappedArg.getValueType();
    }

    @Override
    public int hashCode() {
        return this.wrappedArg.hashCode();
    }

    @Override
    public void initValue(String txtValue) throws InvalidClassException,
            InvalidArgumentValue, ArgumentValueOutOfRange {
        this.wrappedArg.initValue(txtValue);
    }

    @Override
    public boolean isReadOnly() {
        return this.wrappedArg.isReadOnly();
    }

    @Override
    public boolean isRequired() {
        return this.wrappedArg.isRequired();
    }

    @Override
    public boolean isTextEditable() {
        return this.wrappedArg.isTextEditable();
    }

    @Override
    public void setDescription(String description) {
        this.wrappedArg.setDescription(description);
    }

    @Override
    public void setHighBound(Comparable highBound) {
        this.wrappedArg.setHighBound(highBound);
    }

    @Override
    public void setLowBound(Comparable lowBound) {
        this.wrappedArg.setLowBound(lowBound);
    }

    @Override
    public void setName(String name) {
        this.wrappedArg.setName(name);
    }

    @Override
    public void setValue(Object newValue) throws InvalidClassException,
            ArgumentValueOutOfRange {
        this.wrappedArg.setValue(newValue);
    }

    @Override
    public void setReadOnly(boolean isReadOnly) {
        this.wrappedArg.setReadOnly(isReadOnly);
    }

    @Override
    public void setRequired(boolean isRequired) {
        this.wrappedArg.setRequired(isRequired);
    }

    @Override
    public void setValueType(Class valueType) {
        this.wrappedArg.setValueType(valueType);
    }

    @Override
    public String toString() {
        return this.wrappedArg.toString();
    }
}