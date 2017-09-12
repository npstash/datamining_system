//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DiagramComponentAdapter.java (sed.ui.diagram)
// Utworzono 2005-07-22 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual;

import java.awt.Component;

import com.nestor.cs.tasks.def.ConfigurableTask;
import com.nestor.cs.tasks.def.DiagramComponent;
import com.nestor.cs.util.util.param.ArgumentList;

public abstract class AbstractDiagramComponent implements DiagramComponent,
        ConfigurableTask {

    private ArgumentList argList = new ArgumentList();

    private Component preparedComponent = null;

    public boolean doInternalFrame() {
        return false;
    }

    public boolean doModalDialog() {
        return false;
    }

    public Component getComponent() {
        return this.preparedComponent;
    }

    public ArgumentList getConfigurationArguments() {
        return this.argList;
    }

    protected void setPreparedComponent(Component c) {
        this.preparedComponent = c;
    }

}