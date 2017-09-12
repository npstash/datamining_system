//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AbstractBinningDiscretizer.java (sed.data.processing)
// Utworzono 2005-05-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess.discr;

import com.nestor.cs.Messages;
import com.nestor.cs.util.util.param.Argument;

public abstract class AbstractBinningDiscretizer extends
        AbstractDisjointDiscretizer {

    public final Argument argBinCount = new Argument();

    public AbstractBinningDiscretizer() {
        super();

        this.argBinCount.setValueType(Integer.class);
        this.argBinCount.setName(Messages
                .getString("AbstractBinningDiscretizer.argBinCount")); //$NON-NLS-1$
        this.argBinCount.setDescription(Messages
                .getString("AbstractBinningDiscretizer.argDescBinCount")); //$NON-NLS-1$
        this.argBinCount.setLowBound(new Integer(1));
        getConfigurationArguments().addArgument(this.argBinCount);

        try {
            this.argBinCount.setValue(new Integer(10));
        } catch (Exception e) {
        }
    }

    public int getArgBins() {
        return ((Integer) this.argBinCount.getValue()).intValue();
    }

}