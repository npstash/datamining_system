//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Operators.java (sed.data.binary)
// Utworzono 2005-07-29 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets.binary;

import java.util.ArrayList;
import java.util.List;

import com.nestor.cs.data.def.BinaryReadOnlyDataset;
import com.nestor.cs.data.def.BinaryReadOnlyItemset;

public abstract class Operators {

    public static final int EQUAL = 1;

    public static final int EQUAL_OR_SUBSET = 3;

    public static final int EQUAL_OR_SUPERSET = 5;

    public static final int NONE = 0;

    public static final int SUBSET = 2;

    public static final int SUPERSET = 4;

    // przeprowadza operacje binarn�
    // L += R
    // = suma element�w z L i R
    // Przyk�ad:
    // L = 0101 0110 1100
    // R = 0111 0110 0001
    // W = 0111 0110 1101
    public static void add(boolean[] L, boolean[] R) {
        for (int i = 0; i < L.length; i++) {
            if (R[i])
                L[i] = true;
        }
    }

    public static BinaryReadOnlyDataset allSingletonsOf(BinaryReadOnlyItemset it) {
        int singletons = 0;
        int asLen = it.getAttributeCount();
        for (int i = 0; i < asLen; i++)
            if (it.getBinaryValue(i))
                singletons++;

        boolean values[][] = new boolean[singletons][it.getAttributeCount()];

        int currentIndex = 0;
        for (int i = 0; i < asLen; i++)
            if (it.getBinaryValue(i))
                values[currentIndex++][i] = true;

        return new BinaryArrayReadOnlyDataset(it, values);
    }

    public static boolean areDiffOnLast(boolean[] L, boolean[] R) {
        int commonLen = 0;
        for (int i = 0; i < R.length; i++)
            if (L[i] == R[i])
                commonLen++;
            else
                break;

        if (commonLen == L.length)
            return false;

        int diffs = 0;
        for (int i = commonLen; i < L.length; i++) {
            if (!L[i] && !R[i])
                continue;
            if (L[i] && !R[i])
                diffs++;
            if (!L[i] && R[i])
                diffs++;
            if (L[i] && R[i])
                return false;
            if (diffs > 2)
                return false;
        }
        return diffs == 2;
    }

    public static int getCommonItemPrefixLen(boolean[] L, boolean[] R) {
        int commonLen = 0;
        for (int i = 0; i < R.length; i++)
            if (L[i] == R[i]) {
                if (L[i])
                    commonLen++;
            } else
                break;
        return commonLen;
    }

    public static BinaryReadOnlyDataset cartesian(BinaryReadOnlyDataset ds1,
            BinaryReadOnlyDataset ds2) {
        int asSize = ds1.getAttributeCount();
        boolean[][] values = new boolean[ds1.size() * ds2.size()][asSize];
        int index = 0;
        for (int i = 0; i < ds1.size(); i++) {
            ds1.setCursorAt(i);
            for (int j = 0; j < ds2.size(); j++) {
                ds2.setCursorAt(j);
                add(values[index], ds1.getBinaryValues());
                add(values[index], ds2.getBinaryValues());
                index++;
            }
        }
        return new BinaryArrayReadOnlyDataset(ds1, values);
    }

    public static boolean[] combine(boolean[] L, boolean[] R) {
        boolean[] res = new boolean[L.length];
        for (int i = 0; i < L.length; i++)
            res[i] = L[i] || R[i];
        return res;
    }

    // przeprowadza operacje binarn�
    // L &= R
    // = suma element�w z L i R
    // Przyk�ad:
    // L = 0101 0110 1100
    // R = 0111 0110 0001
    // W = 0101 0110 0000
    public static void commonPart(boolean[] L, boolean[] R) {
        for (int i = 0; i < L.length; i++) {
            L[i] = R[i] && L[i];
        }
    }

    // przeprowadza operacje binarn�
    // L -= R
    // = te elementy z L kt�rych nie ma w R
    // Przyk�ad:
    // L = 0101 0110 1100
    // R = 0111 0110 0001
    // W = 0000 0000 1100
    public static void deduct(boolean[] L, boolean[] R) {
        for (int i = 0; i < L.length; i++) {
            if (R[i] && L[i])
                L[i] = false;
        }
    }

    public static int diffNum(boolean[] L, boolean[] R) {
        int res = 0;
        for (int i = 0; i < L.length; i++)
            if (L[i] != R[i])
                res++;
        return res;
    }

    public static int getRelation(boolean[] L, boolean[] R) {
        boolean lIsSuper = true;
        boolean lIsSub = true;
        boolean equal = true;
        for (int i = 0; i < L.length; i++) {
            if (L[i] && !R[i]) {
                equal = false;
                lIsSub = false;
            }
            if (!L[i] && R[i]) {
                equal = false;
                lIsSuper = false;
            }
        }
        int res = 0;
        if (equal)
            res |= EQUAL;
        if (lIsSub)
            res |= SUBSET;
        if (lIsSuper)
            res |= SUPERSET;
        return res;
    }

    // zwraca wynik operacji binarnej
    // L < R
    // <=> ka�dy z element�w L zawiera si� w R,
    // oraz R ma wi�cej element�w
    // Przyk�ad:
    // L = 0101 0110 1100
    // R = 0111 0110 0001
    // W = prawda fa�sz fa�sz
    public static boolean isSubsetOf(boolean[] L, boolean[] R) {
        int x = 0;
        for (int i = 0; i < L.length; i++) {
            if (R[i]) {
                if (!L[i])
                    x++;
            } else if (L[i])
                return false;
        }
        return x > 0;
    }

    // zwraca wynik operacji binarnej
    // L <= R
    // <=> ka�dy z element�w R zawiera si� w L
    // Przyk�ad:
    // L = 0101 0110 1100
    // R = 0111 0110 0001
    // W = prawda prawda fa�sz
    public static boolean isSubsetOrEqualTo(boolean[] L, boolean[] R) {
        for (int i = 0; i < L.length; i++) {
            if (L[i] && !R[i])
                return false;
        }
        return true;
    }

    public static BinaryReadOnlyDataset minCartesian(BinaryReadOnlyDataset ds1,
            BinaryReadOnlyDataset ds2) {
        int asSize = ds1.getAttributeCount();
        ArrayList minValues = new ArrayList();
        int index = 0;
        for (int i = 0; i < ds1.size(); i++) {
            ds1.setCursorAt(i);
            for (int j = 0; j < ds2.size(); j++) {
                ds2.setCursorAt(j);
                boolean[] tempValues = new boolean[asSize];
                add(tempValues, ds1.getBinaryValues());
                add(tempValues, ds2.getBinaryValues());
                minValues.add(tempValues);
                index++;
            }
        }
        minCartesianReducer(minValues);
        boolean[][] values = new boolean[minValues.size()][];
        for (int i = 0; i < minValues.size(); i++) {
            values[i] = (boolean[]) minValues.get(i);
        }
        return new BinaryArrayReadOnlyDataset(ds1, values);
    }

    private static void minCartesianReducer(List l) {
        boolean[] toDelete = new boolean[l.size()];
        for (int i = 0; i < toDelete.length; i++)
            if (!toDelete[i]) {
                for (int j = i + 1; j < toDelete.length; j++)
                    if (!toDelete[j]) {
                        int rel = getRelation((boolean[]) l.get(i),
                                (boolean[]) l.get(j));
                        if (rel == EQUAL_OR_SUBSET) {
                            toDelete[j] = true;
                            continue;
                        } else if (rel == SUPERSET) {
                            toDelete[i] = true;
                            break;
                        }
                    }
            }
        for (int i = toDelete.length - 1; i >= 0; i--) {
            if (toDelete[i])
                l.remove(i);
        }
    }

    public static int positiveCount(boolean[] b) {
        int res = 0;
        for (int i = 0; i < b.length; i++) {
            if (b[i])
                res++;
        }
        return res;
    }

}