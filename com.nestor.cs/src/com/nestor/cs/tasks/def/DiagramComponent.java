//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiagramComponent.java (sed.ui.diagram)
// Utworzono 2005-07-22 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.def;

import java.awt.Component;

import com.nestor.cs.util.proc.Initiable;

public interface DiagramComponent extends Initiable {

    public boolean doInternalFrame();

    public boolean doModalDialog();

    public Component getComponent();

    public String getName();
}