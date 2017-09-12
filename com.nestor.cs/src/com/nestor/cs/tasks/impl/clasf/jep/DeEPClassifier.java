//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DeEPClassifier.java (sed.data.clasf.jep)
// Utworzono 2005-08-09 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.jep;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.Dataset;
import com.nestor.cs.data.def.IncompatibleAttributeset;
import com.nestor.cs.data.def.InvalidAttributeValue;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.def.Rule;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.attributes.BinaryAttribute;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;
import com.nestor.cs.data.impl.datasets.VectorItemset;
import com.nestor.cs.tasks.def.Classifier;
import com.nestor.cs.tasks.impl.clasf.AbstractClassifier;
import com.nestor.cs.tasks.impl.dataset.ArrayListDataset;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.ArrayUtils;
import com.nestor.cs.util.util.param.Argument;

public class DeEPClassifier extends AbstractClassifier {

    private final Argument argAlfa = new Argument(new Double(10));

    private Double[] attributeWidth = null;

    private JEPClassifier jep = null;

    private DefaultAttributeSet newAs = null;

    public DeEPClassifier() {
        super();

        this.argAlfa.setName(Messages.getString("DeEPClassifier.argAlfa")); //$NON-NLS-1$
        this.argAlfa
                .setDescription(Messages
                        .getString("DeEPClassifier.argDescAlfa")); //$NON-NLS-1$
        this.argAlfa.setLowBound(new Double(0));
        this.argAlfa.setHighBound(new Double(100));
        getConfigurationArguments().addArgument(this.argAlfa);
    }

    public Object classify(ReadOnlyDataset dsTrain, ReadOnlyItemset it)
            throws IncompatibleAttributeset {
        if (this.newAs == null)
            return null;

        Dataset newDs;
        System.out.println(Messages.getString("DeEPClassifier.2")); //$NON-NLS-1$
        if (Attributes.areAllAttributesOfType(dsTrain, BinaryAttribute.class,
                true)) {
            newDs = createBinaryBaseDataset(dsTrain, it);
        } else {
            newDs = createMixedBaseDataset(dsTrain, it);
        }

        // zbi�r ucz�cy dla klasyfikatora JEP utworzony
        System.out.println(Messages.getString("DeEPClassifier.3")); //$NON-NLS-1$
        if (this.jep == null)
            this.jep = new JEPClassifier();
        try {
            this.jep.learn(newDs);
        } catch (Exception e) {
            throw new Error();
        }

        // zliczam wsparcia regu� kt�re znalaz� JEP
        // i wybieram klas�
        int clAttrIndex = dsTrain.getClassLabelIndex();
        Attribute clAttr = dsTrain.getAttribute(clAttrIndex);

        double[] classVotes = new double[clAttr.getAllowedValuesCount()];
        for (int cl = 0; cl < clAttr.getAllowedValuesCount(); cl++) {
            for (int i = 0; i < this.jep.getRuleCount(cl); i++) {
                Rule r = this.jep.getRule(cl, i);
                // regu� musi wspiera� klasyfikowany przyk�ad
                // poniewa� wyindukowana zosta�a ze zbioru, w
                // kt�rym ka�dy rekord jest podzbiorem rekordu
                // klasyfikowanego
                classVotes[cl] += r.getSupport();
                //* (double) newDs.size();
            }
        }
        int maxVotesIndex = ArrayUtils.max(classVotes);
        int maxCount = ArrayUtils
                .countOf(classVotes, classVotes[maxVotesIndex]);
        if (maxCount != 1)
            return null;
        else
            return clAttr.getAllowedValue(maxVotesIndex);
    }

    private ArrayListDataset createBinaryBaseDataset(ReadOnlyDataset dsTrain,
            ReadOnlyItemset it) {
        ArrayListDataset newDs = new ArrayListDataset(this.newAs);

        Boolean bTrue = new Boolean(true);
        Boolean bFalse = new Boolean(false);

        // utworzenie cz�ci wsp�lnej
        for (int i = 0; i < dsTrain.size(); i++) {
            dsTrain.setCursorAt(i);
            Object[] values = new Object[dsTrain.getAttributeCount()];

            boolean anyValueSet = false;
            for (int j = 0; j < dsTrain.getAttributeCount(); j++) {
                Object v = dsTrain.getValue(j);
                Object clasfVal = it.getValue(j);
                Object toSet = null;

                if (j == dsTrain.getClassLabelIndex()) {
                    toSet = v;

                } else if (clasfVal == null) {
                    toSet = bFalse;

                } else if (v == null) {
                    toSet = null;

                } else {
                    if (v.equals(clasfVal)) {
                        toSet = v;
                        if (((Boolean) v).booleanValue())
                            anyValueSet = true;
                    } else
                        toSet = bFalse;
                }
                values[j] = toSet;
            }
            try {
                if (anyValueSet) {
                    newDs.addItemset(values);
                }
            } catch (Exception e) {
                throw new Error(e);
            }
        }
        return newDs;
    }

    private ArrayListDataset createMixedBaseDataset(ReadOnlyDataset dsTrain,
            ReadOnlyItemset it) {
        ArrayListDataset newDs = new ArrayListDataset(this.newAs);
        VectorItemset newIt = new VectorItemset(this.newAs);
        Boolean bTrue = new Boolean(true);
        Boolean bFalse = new Boolean(false);

        // utworzenie cz�ci wsp�lnej
        for (int i = 0; i < dsTrain.size(); i++) {
            dsTrain.setCursorAt(i);

            boolean anyValueSet = false;
            for (int j = 0; j < dsTrain.getAttributeCount(); j++) {
                Object v = dsTrain.getValue(j);
                Object clasfVal = it.getValue(j);
                Object toSet = null;

                if (j == dsTrain.getClassLabelIndex()) {
                    toSet = v;

                } else if (clasfVal == null) {
                    toSet = bFalse;

                } else if (v == null) {
                    toSet = null;

                } else if ((this.attributeWidth[j] == null)
                        && (v instanceof Boolean)) {
                    if (v.equals(clasfVal)) {
                        toSet = v;
                        if (((Boolean) v).booleanValue())
                            anyValueSet = true;
                    } else
                        toSet = bFalse;

                } else if (this.attributeWidth[j] != null) {
                    double spread = this.attributeWidth[j].doubleValue();
                    double maxDiff = spread * getArgAlfa() / 100d;
                    Double d = doubleVal(v);
                    Double origD = doubleVal(clasfVal);
                    if ((d == null) || (origD == null)) {
                        toSet = null;
                    } else {
                        double dd = d.doubleValue();
                        double origDD = origD.doubleValue();
                        if (Math.abs(dd - origDD) <= maxDiff) {
                            toSet = bTrue;
                            anyValueSet = true;
                        } else
                            toSet = bFalse;
                    }
                }
                try {
                    newIt.setValue(j, toSet);
                } catch (InvalidAttributeValue e) {
                    throw new Error(e);
                }
            }
            try {
                if (anyValueSet) {
                    newDs.addItemset(newIt);
                }
            } catch (Exception e) {
                throw new Error(e);
            }
        }
        return newDs;
    }

    private Double doubleVal(Object v) {
        if (v instanceof Integer) {
            return new Double(((Integer) v).intValue());
        } else if (v instanceof Double) {
            return (Double) v;
        } else {
            return null;
        }
    }

    public double getArgAlfa() {
        return ((Double) this.argAlfa.getValue()).doubleValue();
    }

    @Override
    public void learn(ReadOnlyDataset dsTrain)
            throws Classifier.MissingClassLabelAttribute, Classifier.InvalidDataset,
            ProcessingException {
        super.learn(dsTrain);

        this.newAs = new DefaultAttributeSet();
        this.attributeWidth = new Double[dsTrain.getAttributeCount()];

        for (int i = 0; i < dsTrain.getAttributeCount(); i++) {
            Attribute attr = dsTrain.getAttribute(i);

            if (i == dsTrain.getClassLabelIndex()) {
                this.newAs.addAttribute(attr);
                continue;
            }
            if (attr instanceof BinaryAttribute) {
                this.newAs.addAttribute(attr);
                continue;
            }
            if (attr.isSortable()) {
                double low = Double.MAX_VALUE;
                double high = -Double.MAX_VALUE;
                boolean set = false;
                for (int j = 0; j < dsTrain.size(); j++) {
                    dsTrain.setCursorAt(j);
                    Object v = dsTrain.getValue(i);
                    if (v == null)
                        continue;
                    Double dbv = doubleVal(v);
                    if (dbv == null) {
                        throw new ProcessingException(Messages
                                .getString("DeEPClassifier.4") + v.getClass()); //$NON-NLS-1$
                    }
                    double dv = dbv.doubleValue();
                    if (dv < low) {
                        low = dv;
                        set = true;
                    }
                    if (dv > high) {
                        high = dv;
                    }
                }
                if (!set) {
                    throw new ProcessingException(Messages
                            .getString("DeEPClassifier.5") //$NON-NLS-1$
                            + attr);
                }
                this.attributeWidth[i] = new Double(high - low);

                BinaryAttribute substitueAttr = new BinaryAttribute();
                substitueAttr.setName(attr.getName());
                this.newAs.addAttribute(substitueAttr);
                continue;
            }

            // �aden z powy�szych rodzaj�w atrybut�w
            throw new ProcessingException(Messages
                    .getString("DeEPClassifier.6")); //$NON-NLS-1$
        }
        this.newAs.setClassLabelIndex(dsTrain.getClassLabelIndex());
    }

    public boolean requiresLearning() {
        return true;
    }
}