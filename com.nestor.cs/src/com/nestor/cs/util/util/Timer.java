//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Timer.java (sed.data.clasf.jep)
// Utworzono 2005-08-02 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import com.nestor.cs.util.Messages;

public class Timer {

    public class Interval implements Comparable {

        long end = 0;

        int id;

        long start = 0;

        long totalTime = 0;

        public Interval(int id) {
            super();
            this.id = id;
        }

        public int compareTo(Object arg0) {
            Integer i1 = new Integer(this.id);
            Integer i2 = new Integer(((Interval) arg0).id);
            return i1.compareTo(i2);
        }

        public void end() {
            this.end = System.currentTimeMillis();
            this.totalTime += getTime();
//            start = 0;
//            end = 0;
        }

        public long getTime() {
            return this.end - this.start;
        }

        public long getTotalTime() {
            return this.totalTime;
        }

        public void print() {
            System.out
                    .println(Messages.getString("Timer.0") + this.id + Messages.getString("Timer.1") + this.totalTime + "ms (" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            + this.totalTime / 1000 + "s)"); //$NON-NLS-1$
        }

        public void print(String s) {
            System.out.println(this.totalTime + "ms (" + this.totalTime / 1000 + "s) : " //$NON-NLS-1$ //$NON-NLS-2$
                    + s);
        }

        public void start() {
            this.start = System.currentTimeMillis();
        }
    }

    private HashMap intervals = new HashMap();

    public Interval i(int i) {
        Integer in = new Integer(i);
        Interval iv = null;
        if (this.intervals.containsKey(in)) {
            iv = (Interval) this.intervals.get(in);
        } else {
            iv = new Interval(i);
            this.intervals.put(in, iv);
        }
        return iv;
    }

    public void printAll() {
        ArrayList al = new ArrayList(this.intervals.values());
        Collections.sort(al);
        int totalTime = 0;
        for (Iterator iter = al.iterator(); iter.hasNext();) {
            Interval iv = (Interval) iter.next();
            iv.print();
            totalTime += iv.totalTime;
        }
        System.out.println(Messages.getString("Timer.6") + totalTime + "ms (" //$NON-NLS-1$ //$NON-NLS-2$
                + (totalTime / 1000) + "s)"); //$NON-NLS-1$
    }

    public void printAll(String[] strs) {
        ArrayList al = new ArrayList(this.intervals.values());
        Collections.sort(al);
        int totalTime = 0;
        int i = 0;
        for (Iterator iter = al.iterator(); iter.hasNext();) {
            Interval iv = (Interval) iter.next();
            iv.print(strs[i++]);
            totalTime += iv.totalTime;
        }
        System.out.println(Messages.getString("Timer.9") + totalTime + "ms (" //$NON-NLS-1$ //$NON-NLS-2$
                + (totalTime / 1000) + "s)"); //$NON-NLS-1$
    }

}