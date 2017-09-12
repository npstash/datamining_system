//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ReusableDCViewer.java (sed.ui.diagram)
// Utworzono 2005-08-01 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui.document;

import java.util.Map;

import com.nestor.cs.tasks.def.DiagramComponent;

public interface ReusableTaskViewer {

    public DiagramComponent getDiagramComponent();

    public void reinitViewer(DiagramComponent dc);

    public void reinitViewer();

    public void onCloseRemoveDCFromMap(Map map);

}