//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Semaphore.java (javan.proc)
// Utworzono 2005-04-13 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.proc;

public class Semaphore {

    private int value = 0;

    public Semaphore() {

    }

    private int maxRaises = 0;

    public Semaphore(int maxRaises) {
        this.maxRaises = maxRaises;
    }

    public synchronized void lower() {
        try {
            this.value++;
            if (this.value > 0) {
                super.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean timedLower(long milis) {
        try {
            this.value++;
            if (this.value > 0) {
                super.wait(milis);
                if (this.value > 0) {
                    // cancel operation 'lower'
                    this.value--;
                    return false;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public synchronized void raise() {
        this.value--;
        if (this.maxRaises > 0) {
            if (this.value < -this.maxRaises)
                this.value = -this.maxRaises;
        }
        if (this.value >= 0) {
            super.notify();
        }
    }
}