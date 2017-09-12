//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// FastMinBinaryItemsets.java (sed.data.binary)
// Utworzono 2005-08-03 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.impl.datasets.binary;

import com.nestor.cs.data.def.BinaryReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.datasets.AbstractItemsetIndexesSelector;

public class FastMinItemsetsMiner extends AbstractItemsetIndexesSelector {

    private long[][] bits = null;

    private boolean searchMax = false;

    public FastMinItemsetsMiner() {
        super();
    }

    public FastMinItemsetsMiner(boolean searchMax) {
        super();
        this.searchMax = searchMax;
    }

    public int[] maxMiner(ReadOnlyDataset ds, int startIndex, int endIndex) {
        if (!(ds instanceof BinaryReadOnlyDataset))
            return null;

        BinaryReadOnlyDataset bds = (BinaryReadOnlyDataset) ds;
        int attrCnt = bds.getAttributeCount();
        int dsSize = endIndex - startIndex + 1;

        int segments = (dsSize - 1) / 32 + 1;
        int oldSegments = 0;
        if (this.bits != null)
            oldSegments = this.bits[0].length;
        if ((segments > oldSegments) || (attrCnt != this.bits.length)) {
            this.bits = null;
            System.gc();
            this.bits = new long[attrCnt][segments];
        } else {
            for (int i = 0; i < segments; i++) {
                for (int j = 0; j < attrCnt; j++)
                    this.bits[j][i] = 0;
            }
        }

        boolean[] possibleMax = new boolean[dsSize];

        for (int i = 0; i < dsSize; i++) {
            int segment = i / 32;
            long bit = 1 << (i % 32);

            bds.setCursorAt(i + startIndex);
            boolean[] vs = bds.getBinaryValues();

            for (int j = 0; j < vs.length; j++)
                if (vs[j])
                    this.bits[j][segment] |= bit;
            possibleMax[i] = true;
        }

        for (int i = 0; i < dsSize; i++) {
            int segment = i / 32;
            long bit = 1 << (i % 32);

            long[] temp = new long[segments];
            boolean first = true;
            for (int j = 0; j < this.bits.length; j++) {
                if ((this.bits[j][segment] & bit) != 0) {
                    if (first) {
                        for (int k = 0; k < temp.length; k++) {
                            temp[k] = this.bits[j][k];
                        }
                        first = false;
                    } else {
                        for (int k = 0; k < temp.length; k++) {
                            temp[k] &= this.bits[j][k];
                        }
                    }
                }
            }
            temp[segment] &= ~bit;

            for (int s = 0; s < temp.length; s++)
                if (temp[s] != 0) {
                    possibleMax[i] = false;
                    for (int j = 0; j < this.bits.length; j++)
                        this.bits[j][segment] &= ~bit;
                    break;
                }
        }

        int maxCount = 0;
        for (int i = 0; i < possibleMax.length; i++)
            if (possibleMax[i])
                maxCount++;

        int[] res = new int[maxCount];
        int currentIndex = 0;
        for (int i = 0; i < possibleMax.length; i++)
            if (possibleMax[i])
                res[currentIndex++] = i + startIndex;

        return res;
    }

    public int[] minMiner(ReadOnlyDataset ds, int startIndex, int endIndex) {
        if (!(ds instanceof BinaryReadOnlyDataset))
            return null;

        BinaryReadOnlyDataset bds = (BinaryReadOnlyDataset) ds;
        int attrCnt = bds.getAttributeCount();
        int dsSize = endIndex - startIndex + 1;

        int segments = (dsSize - 1) / 32 + 1;
        int oldSegments = 0;
        if (this.bits != null)
            oldSegments = this.bits[0].length;
        if ((segments > oldSegments) || (attrCnt != this.bits.length)) {
            this.bits = null;
            System.gc();
            this.bits = new long[attrCnt][segments];
        } else {
            for (int i = 0; i < segments; i++) {
                for (int j = 0; j < attrCnt; j++)
                    this.bits[j][i] = 0;
            }
        }

        boolean[] possibleMin = new boolean[dsSize];

        for (int i = 0; i < dsSize; i++) {
            int segment = i / 32;
            long bit = 1 << (i % 32);

            bds.setCursorAt(i + startIndex);
            boolean[] vs = bds.getBinaryValues();

            for (int j = 0; j < vs.length; j++)
                if (vs[j])
                    this.bits[j][segment] |= bit;
            possibleMin[i] = true;
        }

        for (int i = 0; i < dsSize; i++) {
            if (!possibleMin[i])
                continue;

            int segment = i / 32;
            long bit = 1 << (i % 32);

            long[] temp = new long[segments];
            boolean first = true;
            for (int j = 0; j < this.bits.length; j++) {
                if ((this.bits[j][segment] & bit) != 0) {
                    if (first) {
                        for (int k = 0; k < temp.length; k++) {
                            temp[k] = this.bits[j][k];
                        }
                        first = false;
                    } else {
                        for (int k = 0; k < temp.length; k++) {
                            temp[k] &= this.bits[j][k];
                        }
                    }
                }
            }
            temp[segment] &= ~bit;

            for (int s = 0; s < temp.length; s++) {
                if (temp[s] != 0) {
                    long test = 1;
                    for (int j = 0; j < 32; j++) {
                        if ((temp[s] & test) == test) {
                            possibleMin[s * 32 + j] = false;
                            temp[s] &= ~test;
                            if (temp[s] == 0)
                                break;
                        }
                        test <<= 1;
                    }
                } // != 0
            } // temp s
        }

        int minCount = 0;
        for (int i = 0; i < possibleMin.length; i++)
            if (possibleMin[i])
                minCount++;

        int[] res = new int[minCount];
        int currentIndex = 0;
        for (int i = 0; i < possibleMin.length; i++)
            if (possibleMin[i])
                res[currentIndex++] = i + startIndex;

        return res;
    }

    public int[] selectItemsetIndexes(ReadOnlyDataset ds, int startIndex,
            int endIndex) {
        if (this.searchMax)
            return maxMiner(ds, startIndex, endIndex);
        else
            return minMiner(ds, startIndex, endIndex);
    }

    public void setSearchMax(boolean searchMax) {
        this.searchMax = searchMax;
    }
}