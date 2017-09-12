//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ReadOnlyDataset.java (sed.data.datasets)
// Utworzono 2005-05-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public interface ReadOnlyDataset extends ReadOnlyItemset {

    public int getCursorPos();

    public String getDescription();

    public ReadOnlyItemset getItemset(int i);

    public String getName();

    public ItemsetIterator iterator();

    public ItemsetIterator iterator(ItemsetSelector is);

    public void setCursorAt(int i);

    public void setDescription(String d);

    public void setName(String n);

    public int size();

}