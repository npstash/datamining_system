//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Attributes.java (sed.data.attributes)
// Utworzono 2005-07-30 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.attributes;

import java.util.Iterator;
import java.util.LinkedList;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.ItemsetIterator;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.impl.tests.TestSelector;
import com.nestor.cs.util.util.ArrayUtils;

public abstract class Attributes {

    public static class ValueSubset {

        private int[] values;

        private Object[] valueSet;

        public ValueSubset(Object[] valueSet, int val) {
            this.values = new int[1];
            this.values[0] = val;
            this.valueSet = valueSet;
        }

        public ValueSubset(ValueSubset copy, int val) {
            if (!ArrayUtils.isContained(val, copy.values) && (val >= 0)
                    && (val < copy.valueSet.length)) {
                this.values = new int[copy.values.length + 1];
                for (int i = 0; i < copy.values.length; i++) {
                    this.values[i] = copy.values[i];
                }
                this.values[this.values.length - 1] = val;
            } else {
                this.values = new int[copy.values.length];
                for (int i = 0; i < copy.values.length; i++) {
                    this.values[i] = copy.values[i];
                }
            }
            this.valueSet = copy.valueSet;
        }

        public boolean containsValue(int index) {
            return ArrayUtils.isContained(index, this.values);
        }

        public boolean containsValue(Object value) {
            for (int i = 0; i < this.values.length; i++) {
                if (value.equals(this.valueSet[this.values[i]]))
                    return true;
            }
            return false;
        }

        public Object[] getSubset() {
            Object[] sub = new Object[this.values.length];
            for (int i = 0; i < sub.length; i++) {
                sub[i] = this.valueSet[this.values[i]];
            }
            return sub;
        }

        public int getSubsetSize() {
            return this.values.length;
        }

        @Override
        public String toString() {
            String s = "("; //$NON-NLS-1$
            for (int i = 0; i < this.values.length; i++) {
                s += this.valueSet[this.values[i]];
                if (i < this.values.length - 1)
                    s += ","; //$NON-NLS-1$
            }
            s += ")"; //$NON-NLS-1$
            return s;
        }
    }

    public static boolean areAllAttributesOfType(AttributeSet as,
            Class attrType, boolean exceptClassAttr) {
        for (int i = 0; i < as.getAttributeCount(); i++) {
            if ((i == as.getClassLabelIndex()) && (exceptClassAttr))
                continue;
            if (!as.getAttribute(i).getClass().equals(attrType))
                return false;
        }
        return true;
    }

    public static boolean areLabelsEqual(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else {
            return o1.equals(o2);
        }
    }

    public static void checkForBinaryAttributes(AttributeSet as,
            boolean exceptClass) throws IncompatibleAttributeset {
        for (int i = 0; i < as.getAttributeCount(); i++) {
            if (i == as.getClassLabelIndex() && exceptClass)
                continue;
            Attribute a = as.getAttribute(i);
            if (!(a instanceof BinaryAttribute))
                throw new IncompatibleAttributeset(Messages
                        .getString("Attributes.onlyBinaryAllowed")); //$NON-NLS-1$
        }
    }

    public static boolean compareItemsets(ReadOnlyItemset it1,
            ReadOnlyItemset it2) {
        if (it1.getAttributeCount() != it2.getAttributeCount())
            return false;
        for (int i = 0; i < it1.getAttributeCount(); i++) {
            if (!areLabelsEqual(it1.getValue(i), it2.getValue(i)))
                return false;
        }
        return true;
    }

    public static boolean compareItemsetsByValueIndexes(ReadOnlyItemset it1,
            ReadOnlyItemset it2) {
        if (it1.getAttributeCount() != it2.getAttributeCount())
            return false;
        for (int i = 0; i < it1.getAttributeCount(); i++) {
            Attribute a1 = it1.getAttribute(i);
            Attribute a2 = it2.getAttribute(i);
            if (a1.getAllowedValuesCount() != a2.getAllowedValuesCount())
                return false;

            int vi1 = a1.getAllowedValueIndex(it1.getValue(i));
            int vi2 = a2.getAllowedValueIndex(it2.getValue(i));

            if (vi1 != vi2)
                return false;
        }
        return true;
    }

    public static int findNextContiuousAttribute(AttributeSet as, int after) {
        // bedzie rowne 0 jesli after == -1
        int index = after + 1;
        while (index < as.getAttributeCount()) {
            Attribute temp = as.getAttribute(index);
            if (temp.getAllowedValuesCount() == Attribute.INFINITE_VALUES)
                return index;
            index++;
        }
        return -1;
    }

    public static int findNextDiscreteAttribute(AttributeSet as, int after) {
        int index = after + 1;
        while (index < as.getAttributeCount()) {
            Attribute temp = as.getAttribute(index++);
            if (temp.getAllowedValuesCount() != Attribute.INFINITE_VALUES)
                return index;
            index++;
        }
        return -1;
    }

    public static int getAttributeTypeCount(AttributeSet as, Class attrType) {
        int res = 0;
        for (int i = 0; i < as.getAttributeCount(); i++) {
            if (as.getAttribute(i).getClass().equals(attrType))
                res++;
        }
        return res;
    }

    public static double getExtremumValue(ReadOnlyDataset ds, int attrIndex,
            boolean max) {
        Attribute attr = ds.getAttribute(attrIndex);
        double extr = (max) ? Double.MIN_VALUE : Double.MAX_VALUE;
        if (!(attr instanceof ContinuousAttribute))
            return extr;

        for (int i = 0; i < ds.size(); i++) {
            ds.setCursorAt(i);
            Double d = (Double) ds.getValue(attrIndex);
            if (d != null) {
                if (max) {
                    if (d.doubleValue() > extr)
                        extr = d.doubleValue();
                } else {
                    if (d.doubleValue() < extr)
                        extr = d.doubleValue();
                }
            }
        }
        return extr;
    }

    /**
     * Oblicza warto�� �redni� dla danego atrybutu. Dzia�a tylko dla atrybut�w
     * ci�g�ych (rzeczywistych)
     * 
     * @param ds
     * @param attrIndex
     * @return
     */
    public static double getMeanValue(ReadOnlyDataset ds, int attrIndex) {
        Attribute attr = ds.getAttribute(attrIndex);
        if (!(attr instanceof ContinuousAttribute))
            return 0;

        double sum = 0;
        double n = ds.size();
        for (int i = 0; i < ds.size(); i++) {
            ds.setCursorAt(i);
            Double d = (Double) ds.getValue(attrIndex);
            if (d != null)
                sum += d.doubleValue();
        }
        return sum / n;
    }

    public static double getStandardDeviation(ReadOnlyDataset ds, int attrIndex) {
        Attribute attr = ds.getAttribute(attrIndex);
        if (!(attr instanceof ContinuousAttribute))
            return 0;

        double sumX2 = 0;
        double sumX = 0;
        double n = 0;
        for (int i = 0; i < ds.size(); i++) {
            ds.setCursorAt(i);
            Double d = (Double) ds.getValue(attrIndex);
            if (d != null) {
                double dv = d.doubleValue();
                sumX2 += dv * dv;
                sumX += dv;
                n++;
            }
        }
        if (n <= 1)
            return 0;
        sumX2 *= n;
        return Math.sqrt((sumX2 - sumX * sumX) / (n * (n - 1)));
    }

    /**
     * Wyszukuje w�a�ciwe podzbiory danego zbioru warto�ci w ten spos�b, aby
     * w�r�d wynikowych podzbior�w nie by�o ich negacji, np. dla zbioru
     * warto�ci: (a,b,c,d) wyszukane zostan�: (a), (b), (c), (d), (a,b), (a,c),
     * (a,d) Ka�dy inny od powy�szych zbior�w podzbi�r (a,b,c,d) posiada swoj�
     * negacj� w wynikowych podziorach, np. podzbi�r (a,b,c) ma negacj� (d)
     * 
     * @param valueSet -
     *            warto�ci, kt�rych podzbiory maj� by� odszukane
     * @return tablica wynikowych podzbior�w
     */
    public static ValueSubset[] getSubsetsWithoutNegations(Object[] valueSet) {
        // przetestuj kazdy niepusty podzbior zestawu wartosci
        LinkedList zestawy = new LinkedList();
        for (int i = 0; i < valueSet.length; i++) {
            ValueSubset nowy = new ValueSubset(valueSet, i);

            LinkedList dopisz = new LinkedList();
            for (Iterator iter = zestawy.iterator(); iter.hasNext();) {
                ValueSubset z = (ValueSubset) iter.next();
                // dopisuje sie tylko jesli nowy zestaw bedzie mial
                // dlugosc mniejsza niz polowa wszystkich wartosci
                if (z.getSubsetSize() + 1 <= (valueSet.length >> 1)) {
                    ValueSubset dopisany = new ValueSubset(z, i);
                    dopisz.add(dopisany);
                }
            }

            zestawy.add(nowy);

            for (Iterator iter = dopisz.iterator(); iter.hasNext();) {
                ValueSubset z = (ValueSubset) iter.next();
                zestawy.add(z);
            }
        }
        ValueSubset[] zw = new ValueSubset[zestawy.size()];
        return (ValueSubset[]) zestawy.toArray(zw);
    }

    public static boolean hasMissingValues(ReadOnlyDataset ds, int attrIndex) {
        Test test = ds.getAttribute(attrIndex).createTest(null, Test.EQUAL);
        TestSelector selector = new TestSelector(attrIndex, test);
        ItemsetIterator ii = ds.iterator(selector);
        return (ii.hasMoreItemsets());
    }
}