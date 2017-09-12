//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// AttributeBinarizer.java (sed.data.processing)
// Utworzono 2005-05-22 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess;

import java.util.ArrayList;

import com.nestor.cs.Messages;
import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.AttributeSet;
import com.nestor.cs.data.def.AttributeSubset;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.impl.attributes.AbstractAttribute;
import com.nestor.cs.data.impl.attributes.BinaryAttribute;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSet;
import com.nestor.cs.data.impl.attributes.DefaultAttributeSubset;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class AttributeBinarizer extends AbstractDatasetConverter {

    public static BinaryAttribute[] binarizeAttribute(Attribute attr) {
        BinaryAttribute[] res = new BinaryAttribute[attr
                .getAllowedValuesCount()];
        for (int i = 0; i < res.length; i++) {
            res[i] = new BinaryAttribute();
            res[i].setName(attr.getName() + "=" //$NON-NLS-1$
                    + attr.getAllowedValuesNames()[i]);
        }
        return res;
    }

    public static Boolean[] binarizeValue(Attribute attr, Object value) {
        Boolean[] res = new Boolean[attr.getAllowedValuesCount()];
        String[] labels = attr.getAllowedValuesNames();
        String label = attr.getNameForValue(value);
        for (int j = 0; j < labels.length; j++) {
            res[j] = new Boolean(label.equals(labels[j]));
        }
        return res;
    }

    public final Argument argAttributeSubset = new Argument();

    // nowy zbi�r atrybut�w, b�dzie zawiera� zar�no atrybuty z konwertowanego
    // zbioru
    // jak i nowe,zbinaryzowane
    private DefaultAttributeSet convertedAttributeset = null;

    // lista flag oznaczaj�ca czy atrybut o danym indeksie (w nowym zbiorze
    // atrybut�w)
    // by� binaryzowany
    private ArrayList isAttrBinarized;

    // lista indeks�w do starego zbioru atrybut�w. potrzebna do translacji
    // nowych indeks�w
    // atrybut�w na stare:
    // stary AS: A, B, C
    //              | | |________
    //              | |________ |
    //              |_________ | |_____
    // nowy AS: A0, A1, A2, B, C0, C1
    private ArrayList linkToOriginalAttr;

    // indeksy faktycznie konwertowanych atrybut�w
    // mog� pochodzi� a warto�ci argumentu
    // lub w przypadku jego braku, wybierane s�
    // wszystkie atrybuty dyskretne (opr�cz klasy)
    private AttributeSubset usedAttributeSubset = null;

    public AttributeBinarizer() {
        super();
        this.argAttributeSubset.setValueType(AttributeSubset.class);
        this.argAttributeSubset.setName(Messages
                .getString("AttributeBinarizer.argBinarizedAttributes")); //$NON-NLS-1$
        this.argAttributeSubset.setDescription(Messages
                .getString("AttributeBinarizer.argDescBinarizedAttributes")); //$NON-NLS-1$
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
        int relatedTo = ((Integer) this.linkToOriginalAttr.get(attrIndex))
                .intValue();
        boolean isBinarized = ((Boolean) this.isAttrBinarized.get(attrIndex))
                .booleanValue();

        if (!isBinarized)
            return getArgConvertedDataset().getValue(relatedTo);

        Attribute relatedAttribute = getArgConvertedDataset().getAttribute(
                relatedTo);
        int vi = relatedAttribute.getAllowedValueIndex(getArgConvertedDataset()
                .getValue(relatedTo));
        if (vi < 0)
            return null;
        return new Boolean(this.linkToOriginalAttr.indexOf(new Integer(relatedTo))
                + vi == attrIndex);
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();
        this.usedAttributeSubset = getArgAttributeSubset();

        // jesli nie ustawiono parametru okreslajacego ktore
        // atrybuty maja byc zbinaryzowane, zbinaryzowane zostana
        // wszystkie o skonczonej liczbie wartosci (i nie binarne)
        if (this.usedAttributeSubset == null) {
            ReadOnlyDataset ds = getArgConvertedDataset();
            this.usedAttributeSubset = new DefaultAttributeSubset(ds);

            for (int i = 0; i < ds.getAttributeCount(); i++) {
                Attribute attr = ds.getAttribute(i);

                // binarnych atrybutow sie nie binaryzuje, oczywiscie
                if (attr instanceof BinaryAttribute)
                    continue;

                // nie jest mozliwa binaryzacja atrybutow o nieokreslonej
                // liczbie wartosci
                if (attr.getAllowedValuesCount() == Attribute.INFINITE_VALUES)
                    continue;

                // pomijam atrybut klasy w domyslnej binaryzacji
                if (i == ds.getClassLabelIndex())
                    continue;
                ((DefaultAttributeSubset) this.usedAttributeSubset)
                        .setAttributeContained(i, true);
            }
        }

        this.linkToOriginalAttr = new ArrayList();
        this.isAttrBinarized = new ArrayList();
    }

    public void setClassLabelIndex(int attrIndex) {
        this.convertedAttributeset.setClassLabelIndex(attrIndex);
    }

    public boolean stepProcess() throws ProcessingException {

        this.convertedAttributeset = new DefaultAttributeSet();

        for (int a = 0; a < getArgConvertedDataset().getAttributeCount(); a++) {
            if (this.usedAttributeSubset.containsAttribute(a)) {
                Attribute attr = getArgConvertedDataset().getAttribute(a);
                if (attr.getAllowedValuesCount() == Attribute.INFINITE_VALUES) {
                    String key = "AttributeBinarizer.attributeMustHaveFiniteValues"; //$NON-NLS-1$ 
                    throw new ProcessingException(Messages.getString(key));
                }

                // przy zbinaryzowaniu atrybutu klasy, gubiony jest indeks
                // etykiety klasy (nie jest mozliwe ustawienie etykiety klasy na
                // kilku atrybutach)
                Attribute[] attrs = binarizeAttribute(attr);

                for (int i = 0; i < attrs.length; i++) {
                    this.convertedAttributeset.addAttribute(attrs[i]);
                    this.linkToOriginalAttr.add(new Integer(a));
                    this.isAttrBinarized.add(new Boolean(true));
                }

            } else {
                this.convertedAttributeset.addAttribute(getArgConvertedDataset()
                        .getAttribute(a));

                // jesli dany atybut jest etykiet� klasy,
                // nale�y zachowa� ustawienie i ustawic odpowiedni indeks w
                // convertedAttributeSet
                // (indeks ostatnio dodanego atrybutu, po dodaniu etykiety
                // klasy)
                if (getArgConvertedDataset().getClassLabelIndex() == a)
                    this.convertedAttributeset
                            .setClassLabelIndex(this.convertedAttributeset
                                    .getAttributeCount() - 1);
                this.linkToOriginalAttr.add(new Integer(a));
                this.isAttrBinarized.add(new Boolean(false));
            }
        }
        stepComplete();
        return false;
    }

}