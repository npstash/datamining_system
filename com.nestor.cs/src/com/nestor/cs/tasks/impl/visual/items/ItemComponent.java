//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ItemComponent.java (sed.data.ui)
// Utworzono 2005-07-21 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual.items;

import javax.swing.JComponent;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.util.event.UpdateListener;

public interface ItemComponent {

    public void addUpdateListener(UpdateListener listener);

    public JComponent getComponent();

    public Object getValue();

    public void reinit(Attribute attr, Object value);

    public void removeUpdateListener(UpdateListener listener);

    public void setValue(Object value);
}