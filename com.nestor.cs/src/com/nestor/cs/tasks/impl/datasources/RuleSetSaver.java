//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// RuleSetSaver.java (sed.data.clasf)
// Utworzono 2005-08-02 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.datasources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.Rule;
import com.nestor.cs.tasks.def.RuleSet;
import com.nestor.cs.tasks.impl.ConfigurableTaskHelper;
import com.nestor.cs.tasks.impl.clasf.RankedRuleContainer;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class RuleSetSaver extends ConfigurableTaskHelper {

    public final Argument argFile = new Argument();

    public final Argument argRuleSet = new Argument();

    public RuleSetSaver() {
        super();
        this.argFile.setValueType(File.class);
        this.argFile.setName("Plik");
        this.argFile
                .setDescription("Nazwa pliku, w kt�rym maj� zosta� zapisane regu�y");
        getConfigurationArguments().addArgument(this.argFile);

        this.argRuleSet.setName("Zbi�r regu�");
        this.argRuleSet
                .setDescription("Wskazany zbi�r regu� zostanie zapisany do pliku");
        this.argRuleSet.setValueType(RuleSet.class);
        getConfigurationArguments().addArgument(this.argRuleSet);
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();
        RuleSet rs = (RuleSet) this.argRuleSet.getValue();
        File f = (File) this.argFile.getValue();

        AttributeSet as = rs.getLearningContext().getAttributeSet();
        int clIndex = as.getClassLabelIndex();
        Attribute clAttr = as.getAttribute(clIndex);
        RankedRuleContainer rc = new RankedRuleContainer(as);
        for (int cl = 0; cl < clAttr.getAllowedValuesCount(); cl++) {
            for (int r = 0; r < rs.getRuleCount(cl); r++) {
                Rule rul = rs.getRule(cl, r);
                try {
                    rc.addRule(rul);
                } catch (IncompatibleAttributeset e1) {
                }
            }
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(f));
            oos.writeObject(rc);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // init
}