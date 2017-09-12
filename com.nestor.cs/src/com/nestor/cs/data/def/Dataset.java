//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Dataset.java (sed.data.interfaces)
// Utworzono 2005-04-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public interface Dataset extends ReadOnlyDataset, Itemset {

    public void addItemset(ReadOnlyItemset it) throws IncompatibleAttributeset,
            InvalidAttributeValue;

    public void addItemset(Object[] values) throws InvalidAttributeValue;

    public void append(ReadOnlyDataset ds) throws IncompatibleAttributeset;

    public void append(ReadOnlyDataset ds, int[] indexes)
            throws IncompatibleAttributeset;

    public void removeAll();

    public void removeItemset(int i);

    public void removeItemsets(int[] indexes);

    public void retainItemsets(int[] indexes);

}