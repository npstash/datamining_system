//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SelectAttributesFromText.java (sed.ui.elements)
// Utworzono 2005-08-15 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.visual;

import java.util.StringTokenizer;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSubset;
import com.nestor.cs.tasks.def.ConfigurableTask;
import com.nestor.cs.tasks.def.MissingArgumentValue;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;
import com.nestor.cs.util.util.param.ArgumentList;

public class SelectAttributesFromText extends DefaultAttributeSubset implements
        ConfigurableTask {

    public final Argument argAttributes = new Argument();

    public final Argument argAttributeSet = new Argument();

    private ArgumentList argList = new ArgumentList();

    public SelectAttributesFromText() {
        super();

        this.argAttributeSet.setName(Messages
                .getString("SelectAttributes.argAttributeset")); //$NON-NLS-1$
        this.argAttributeSet.setDescription(Messages
                .getString("SelectAttributes.argDescAttributeset")); //$NON-NLS-1$
        this.argAttributeSet.setValueType(AttributeSet.class);
        this.argList.addArgument(this.argAttributeSet);

        this.argAttributes.setName(Messages
                .getString("SelectAttributesFromText.argNums")); //$NON-NLS-1$
        this.argAttributes.setValueType(String.class);
        this.argAttributes.setDescription(Messages
                .getString("SelectAttributesFromText.argDescNums")); //$NON-NLS-1$
        this.argList.addArgument(this.argAttributes);
    }

    public ArgumentList getConfigurationArguments() {
        return this.argList;
    }

    public void initProcess() throws ProcessingException {
        if (!this.argList.areRequiredArgumentsFilled())
            throw new MissingArgumentValue(this.argList);

        AttributeSet as = ((AttributeSet) this.argAttributeSet.getValue());
        super.setRelatedAttributeSet(as);

        String s = ((String) this.argAttributes.getValue());
        StringTokenizer st = new StringTokenizer(s, ",; \t"); //$NON-NLS-1$
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int i = -1;
            try {
                i = Integer.parseInt(token);
            } catch (NumberFormatException e) {
                continue;
            }
            if (i < 0)
                continue;
            if (i >= as.getAttributeCount())
                continue;
            super.setAttributeContained(i, true);
        }
    }
}