//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// RuleContainer.java (sed.data.clasf)
// Utworzono 2005-08-09 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.data.def;

public interface RuleContainer {

    public Rule getRule(int n);

    public Rule getRule(int classValueIndex, int n);

    public int getRuleCount();

    public int getRuleCount(int classValueIndex);

}