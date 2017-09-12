//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// FuzzyAttribute.java (sed.data.attributes)
// Utworzono 2005-06-09 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.attributes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.impl.tests.FuzzyTest;

public class FuzzyAttribute extends DiscreteAttribute {

    public static class FuzzyValue implements Comparable {

        private HashMap membershipValues = null;

        public FuzzyValue() {
            super();
            this.membershipValues = new LinkedHashMap(2, 2);
        }

        protected FuzzyValue(FuzzyValue clone) {
            this();
            Iterator iter = clone.membershipValues.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry me = (Map.Entry) iter.next();
                Integer nInt = new Integer(((Integer) me.getKey()).intValue());
                Double nDouble = new Double(((Double) me.getValue())
                        .doubleValue());
                this.membershipValues.put(nInt, nDouble);
            }
        }

        public FuzzyValue(Integer discrete) {
            super();
            this.membershipValues = new LinkedHashMap(2, 2);
            addValueMembership(discrete, 1);
        }

        public void addValueMembership(Integer value, double membershipValue) {
            this.membershipValues.put(value, new Double(membershipValue));
        }

        public int compareTo(Object arg0) {
            if (arg0 instanceof FuzzyValue)
                return getDiscreteValueByMajority().compareTo(
                        ((FuzzyValue) arg0).getDiscreteValueByMajority());
            return 0;
        }

        public Integer getDiscreteValueByMajority() {
            double max = 0;
            Integer retVal = null;
            Iterator it = this.membershipValues.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                double mv = ((Double) me.getValue()).doubleValue();
                if (mv > max) {
                    max = mv;
                    retVal = (Integer) me.getKey();
                }
            }
            return retVal;
        }

        public double getValueMembership(Integer value) {
            Double m = (Double) this.membershipValues.get(value);
            if (m == null)
                return 0;
            return m.doubleValue();
        }

        public Iterator memberValuesIterator() {
            return this.membershipValues.keySet().iterator();
        }
    }

    public FuzzyAttribute(int labelCount) {
        super(labelCount);
    }

    public FuzzyAttribute(String[] labels) {
        super(labels);
    }

    @Override
    public Object cloneValue(Object value) {
        if (value instanceof FuzzyValue)
            return new FuzzyValue((FuzzyValue) value);
        else
            return null;
    }

    @Override
    public Test createTest(Object constraint, int testType) {
        return new FuzzyTest.TestWrapper(super.createTest(constraint, testType));
    }

    @Override
    public Object getAllowedValue(int index) {
        Integer i = (Integer) super.getAllowedValue(index);
        if (i == null)
            return null;
        return new FuzzyValue(i);
    }

    @Override
    public int getAllowedValueIndex(Object value) {
        if (!(value instanceof FuzzyValue))
            return -1;
        return super.getAllowedValueIndex(((FuzzyValue) value)
                .getDiscreteValueByMajority());
    }

    @Override
    public String[] getAllowedValuesNames() {
//        String[] s = new String[getAllowedValuesCount()];
//        for (int i = 0; i < s.length; i++) {
//            s[i] = super.getAllowedValue(i).toString();
//        }
//        return s;
        return super.getAllowedValuesNames();
    }

    private static double cutDouble(double d) {
        d *= 100;
        return ((int) d) / 100d;
    }

    @Override
    public String getNameForValue(Object obj) {
        if (obj instanceof FuzzyValue) {
            FuzzyValue fv = (FuzzyValue) obj;
            Iterator it = fv.memberValuesIterator();
            String res = "{"; //$NON-NLS-1$
            while (it.hasNext()) {
                Integer val = (Integer) it.next();
                res += "p(" + super.getNameForValue(val) + ")=" //$NON-NLS-1$ //$NON-NLS-2$
                        + cutDouble(fv.getValueMembership(val));
                if (it.hasNext())
                    res += ";"; //$NON-NLS-1$
            }
            res += "}"; //$NON-NLS-1$
            return res;
        } else if (obj instanceof Integer) {
            return super.getNameForValue(obj);
        }
        return null;
    }

    @Override
    public Object getValueFromName(String valueName) {
        if (valueName == null)
            return null;
        if (valueName.startsWith("{") && valueName.startsWith("}")) { //$NON-NLS-1$ //$NON-NLS-2$
            FuzzyValue fv = new FuzzyValue();
            for (int currentF = valueName.indexOf("f("); currentF >= 0; currentF = valueName.indexOf("p(", currentF)) { //$NON-NLS-1$ //$NON-NLS-2$ 
                try {
                    String discVal = valueName.substring(currentF + 2,
                            valueName.indexOf(")", currentF)); //$NON-NLS-1$
                    Integer val = new Integer(Integer.parseInt(discVal));
                    String membVal = valueName.substring(valueName.indexOf(
                            ")=", currentF) + 2, valueName.indexOf(";", //$NON-NLS-1$ //$NON-NLS-2$
                            currentF));
                    double memb = Double.parseDouble(membVal);
                    fv.addValueMembership(val, memb);
                } catch (Exception e) {
                    return null;
                }
            }
            return fv;
        } else {
            return super.getValueFromName(valueName);
        }
    }

    @Override
    public Class getValueType() {
        return FuzzyValue.class;
    }

    @Override
    public boolean isSortable() {
        return true;
    }

    @Override
    public boolean isValueValid(Object obj) {
        if (obj == null)
            return true;
        if (obj instanceof FuzzyValue)
            return super.isValueValid(((FuzzyValue) obj)
                    .getDiscreteValueByMajority());
        if (obj instanceof Integer)
            return super.isValueValid(obj);
        return false;
    }

    @Override
    public String toString() {
        return getName()
                + " [" + Messages.getString("FuzzyAttribute.fuzzyAttribute") + ", 0 - " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + (getAllowedValuesCount() - 1) + "]"; //$NON-NLS-1$
    }

}