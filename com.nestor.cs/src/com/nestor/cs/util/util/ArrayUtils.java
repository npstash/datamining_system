//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ArrayUtils.java (javan.util)
// Utworzono 2005-04-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util;

import java.util.ArrayList;

public abstract class ArrayUtils {

    public static int countOf(double[] in, double value) {
        int res = 0;
        for (int i = 0; i < in.length; i++) {
            if (in[i] == value) {
                res++;
            }
        }
        return res;
    }

    public static int countOf(int[] in, int value) {
        int res = 0;
        for (int i = 0; i < in.length; i++) {
            if (in[i] == value) {
                res++;
            }
        }
        return res;
    }

    public static boolean isContained(int a, int[] in) {
        for (int i = 0; i < in.length; i++) {
            if (a == in[i])
                return true;
        }
        return false;
    }

    public static int max(double[] in) {
        int res = -1;
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < in.length; i++) {
            if (in[i] > max) {
                max = in[i];
                res = i;
            }
        }
        return res;
    }

    public static int max(int[] in) {
        int res = -1;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < in.length; i++) {
            if (in[i] > max) {
                max = in[i];
                res = i;
            }
        }
        return res;
    }

    public static double sum(double[] in) {
        double res = 0;
        for (int i = 0; i < in.length; i++) {
            res += in[i];
        }
        return res;
    }

    public static int sum(int[] in) {
        int res = 0;
        for (int i = 0; i < in.length; i++) {
            res += in[i];
        }
        return res;
    }

    public static int[] createIntArray(ArrayList al) {
        int[] res = new int[al.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = ((Integer) al.get(i)).intValue();
        }
        return res;
    }
}