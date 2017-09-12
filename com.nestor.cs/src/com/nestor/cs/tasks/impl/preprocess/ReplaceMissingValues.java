//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// ReplaceMissingValues.java (sed.data.conversion)
// Utworzono 2005-08-07 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.AttributeSubset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.attributes.AbstractAttribute;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.data.impl.attributes.BinaryAttribute;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSubset;
import com.nestor.cs.data.impl.attributes.DiscreteAttribute;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class ReplaceMissingValues extends AbstractDatasetConverter {

    public static String MISSING_VALUE_NEW_NAME = "(X)"; //$NON-NLS-1$

    public final Argument argAttributeSubset = new Argument();

    /**
     * Nowy zbi�r atrybut�w, b�dzie zawiera� zar�no atrybuty z konwertowanego
     * zbioru jak i nowe, z dodatkow� warto�ci� dla warto�ci nieznanych
     */
    private DefaultAttributeSet convertedAttributeset = null;

    /**
     * Lista flag oznaczaj�ca czy atrybut o danym indeksie (w nowym zbiorze
     * atrybut�w) by� konwertowany
     */
    private ArrayList isAttrConverted = null;

    private ArrayList isBinToDiscr = null;

    // indeksy faktycznie konwertowanych atrybut�w
    // mog� pochodzi� a warto�ci argumentu
    // lub w przypadku jego braku, wybierane s�
    // wszystkie atrybuty dyskretne (opr�cz klasy)
    private AttributeSubset usedAttributeSubset = null;

    public ReplaceMissingValues() {
        super();
        this.argAttributeSubset.setValueType(AttributeSubset.class);
        this.argAttributeSubset.setName(Messages
                .getString("ReplaceMissingValues.argAttrs")); //$NON-NLS-1$
        this.argAttributeSubset.setDescription(Messages
                .getString("ReplaceMissingValues.argDescAttrs")); //$NON-NLS-1$
        this.argAttributeSubset.setRequired(false);
        getConfigurationArguments().addArgument(this.argAttributeSubset);
    }

    public boolean containsAttribute(int attrIndex) {
        return this.convertedAttributeset.containsAttribute(attrIndex);
    }

    public boolean equalsToAttributeSet(AttributeSet as) {
        return this.convertedAttributeset.equalsToAttributeSet(as);
    }

    public AttributeSubset getArgAttributeSubset() {
        return (AttributeSubset) this.argAttributeSubset.getValue();
    }

    public Attribute getAttribute(int attributeIndex) {
        return this.convertedAttributeset.getAttribute(attributeIndex);
    }

    public int getAttributeCount() {
        return this.convertedAttributeset.getAttributeCount();
    }

    public int getClassLabelIndex() {
        return this.convertedAttributeset.getClassLabelIndex();
    }

    public AttributeSet getRelatedAttributeSet() {
        return this.convertedAttributeset.getRelatedAttributeSet();
    }

    public Object getValue(int attrIndex) {
        Object origValue = getArgConvertedDataset().getValue(attrIndex);

        if (!this.isAttrConverted.contains(new Integer(attrIndex)))
            return origValue;

        Attribute attr = this.convertedAttributeset.getAttribute(attrIndex);
        if (origValue == null)
            return attr.getValueFromName(MISSING_VALUE_NEW_NAME);

        if (this.isBinToDiscr.contains(new Integer(attrIndex))) {
            // warto�� origValue nie jest null
            int index = getArgConvertedDataset().getAttribute(attrIndex)
                    .getAllowedValueIndex(origValue);
            return attr.getAllowedValue(index);
        } else
            return origValue;
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();
        this.usedAttributeSubset = getArgAttributeSubset();

        // jesli nie ustawiono parametru okreslajacego dla ktorych
        // atrybut�w warto�ci nieznane maja byc zamienione nowymi warto�ciami,
        // algorytm wybierze wszystkie kt�re maj� warto�ci nieznane i nie s�
        // numeryczne
        if (this.usedAttributeSubset == null) {
            ReadOnlyDataset ds = getArgConvertedDataset();
            this.usedAttributeSubset = new DefaultAttributeSubset(ds);

            for (int i = 0; i < ds.getAttributeCount(); i++) {
                Attribute attr = ds.getAttribute(i);

                // nie jest mozliwe dodanie warto�ci do atrybutow o
                // nieokreslonej liczbie wartosci
                if (attr.getAllowedValuesCount() == Attribute.INFINITE_VALUES)
                    continue;

                if (!Attributes.hasMissingValues(ds, i))
                    continue;

                ((DefaultAttributeSubset) this.usedAttributeSubset)
                        .setAttributeContained(i, true);
            }
        }
        this.isAttrConverted = new ArrayList();
        this.isBinToDiscr = new ArrayList();
    }

    public void setClassLabelIndex(int attrIndex) {
        this.convertedAttributeset.setClassLabelIndex(attrIndex);
    }

    public boolean stepProcess() throws ProcessingException {

        this.convertedAttributeset = new DefaultAttributeSet();

        for (int a = 0; a < getArgConvertedDataset().getAttributeCount(); a++) {
            Attribute attr = getArgConvertedDataset().getAttribute(a);

            if (this.usedAttributeSubset.containsAttribute(a)) {

                if (attr.getAllowedValuesCount() == Attribute.INFINITE_VALUES) {
                    String key = "AttributeBinarizer.attributeMustHaveFiniteValues"; //$NON-NLS-1$ 
                    throw new ProcessingException(Messages.getString(key));
                }

                DiscreteAttribute newAttribute = null;
                // atrybuty binarne zast�powane s� dyskretnymi
                if (attr instanceof BinaryAttribute) {
                    BinaryAttribute ba = (BinaryAttribute) attr;
                    String[] labels = new String[3];
                    labels[0] = ba.getAllowedValuesNames()[0];
                    labels[1] = ba.getAllowedValuesNames()[1];
                    labels[2] = MISSING_VALUE_NEW_NAME;
                    newAttribute = new DiscreteAttribute(labels);
                    this.isBinToDiscr.add(new Integer(a));

                } else if (attr instanceof DiscreteAttribute) {
                    DiscreteAttribute da = (DiscreteAttribute) attr;
                    String[] labels = new String[da.getAllowedValuesCount() + 1];
                    for (int i = 0; i < da.getAllowedValuesCount(); i++) {
                        labels[i] = da.getAllowedValuesNames()[i];
                    }
                    labels[da.getAllowedValuesCount()] = MISSING_VALUE_NEW_NAME;

                    // dynamiczne tworzenie nowego obiektu atrubytu jest
                    // konieczne,
                    // poniewa� nie jest znany rodzaj atrybutu dyskretnego.
                    // Przyjmuje si� jednak, �e posiada on konstruktor
                    // <init>(String[] labels)
                    Class attrClass = attr.getClass();
                    try {
                        Constructor c = attrClass
                                .getConstructor(new Class[] { String[].class });
                        newAttribute = (DiscreteAttribute) c
                                .newInstance(new Object[] { labels });
                    } catch (Exception e) {
                        // inicjalizacja nie powiod�a si�
                        String s = Messages
                                .getString("ReplaceMissingValues.cannotInitialize"); //$NON-NLS-1$
                        throw new ProcessingException(s + attr);
                    }
                }

                if (newAttribute != null) {
                    newAttribute.setName(attr.getName());
                    this.convertedAttributeset.addAttribute(newAttribute);
                    this.isAttrConverted.add(new Integer(a));
                } else {
                    this.convertedAttributeset.addAttribute(attr);
                }

            } else {
                this.convertedAttributeset.addAttribute(attr);
            }
        }

        int clIndex = getArgConvertedDataset().getClassLabelIndex();
        if (clIndex >= 0)
            this.convertedAttributeset.setClassLabelIndex(clIndex);

        stepComplete();
        return false;
    }

}