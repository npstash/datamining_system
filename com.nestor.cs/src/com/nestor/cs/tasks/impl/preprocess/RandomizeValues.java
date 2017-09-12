//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// RandomizeMissingValues.java (com.nestor.cs.tasks.impl.preprocess)
// Utworzono 2005-09-13 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.preprocess;

import java.util.Random;

import com.nestor.cs.data.def.Attribute;
import com.nestor.cs.data.def.InvalidAttributeValue;
import com.nestor.cs.data.impl.attributes.Attributes;
import com.nestor.cs.tasks.impl.dataset.ArrayListDataset;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class RandomizeValues extends AbstractValueConverter {

    public final Argument argRndProb = new Argument(new Double(100));

    public final Argument argDeviation = new Argument(new Double(33));

    private ArrayListDataset dsCopy = null;

    private Random rnd = new Random();

    public RandomizeValues() {
        super();

        this.argRndProb.setName("Procent szumu w danych [%]");
        this.argRndProb.setLowBound(new Double(0));
        this.argRndProb.setHighBound(new Double(100));
        this.argRndProb
                .setDescription("Okre�la prawdopodobie�stwo dla ka�dej warto�ci w zbiorze danych, �e zostanie ona zmieniona na losow�.");
        getConfigurationArguments().addArgument(this.argRndProb);

        this.argDeviation.setName("Odchylenie standardowe [%]");
        this.argDeviation.setLowBound(new Double(0));
        this.argDeviation
                .setDescription("Procent szeroko�ci przedzia�u warto�ci atrybutu uznawany za odchylenie standardowe szumu.");
        getConfigurationArguments().addArgument(this.argDeviation);
    }

    private boolean changeValue() {
        double prob = ((Double) this.argRndProb.getValue()).doubleValue() / 100d;
        double r = this.rnd.nextDouble();
        return r < prob;
    }

    private double getNormalVariable() {
        double x1 = this.rnd.nextDouble();
        double x2 = this.rnd.nextDouble();
        return Math.sqrt(-2 * Math.log(x1)) * Math.cos(2 * Math.PI * x2);
    }

    private double getNormalVariable(double mean, double deviation) {
        double n = getNormalVariable();
        return deviation * n + mean;
    }

    private double getNoisedValue(double max, double min, double value,
            double dev) {
        double n = getNormalVariable(value, dev);
        if (n < min)
            n = min;
        if (n > max)
            n = max;
        return n;
    }

    public Object getValue(int attrIndex) {
        this.dsCopy.setCursorAt(getArgConvertedDataset().getCursorPos());
        return this.dsCopy.getValue(attrIndex);
    }

    @Override
    public boolean stepProcess() throws ProcessingException {
        this.rnd = new Random();

        double relDev = ((Double) this.argDeviation.getValue()).doubleValue() / 100d;

        this.dsCopy = new ArrayListDataset(getArgConvertedDataset(), true);
        for (int i = 0; i < this.dsCopy.getAttributeCount(); i++) {
            if (i == this.dsCopy.getClassLabelIndex())
                continue;
            Attribute a = this.dsCopy.getAttribute(i);
            if (a.getValueType().equals(Integer.class)) {
                int min = ((Integer) a.getAllowedValue(0)).intValue();
                int max = ((Integer) a.getAllowedValue(a
                        .getAllowedValuesCount() - 1)).intValue();
                double dev = relDev * (max - min);

                for (int j = 0; j < this.dsCopy.size(); j++) {
                    if (!changeValue())
                        continue;
                    this.dsCopy.setCursorAt(j);
                    Integer value = (Integer) this.dsCopy.getValue(i);
                    if (value == null)
                        continue;
                    //                    int r = rnd.nextInt(max - min + 1) + min;
                    double rd = getNoisedValue(max, min,
                            value.intValue(), dev);
                    int r = (int) Math.floor(rd + 0.5d);
                    if (r < min)
                        r = min;
                    if (r > max)
                        r = max;
                    try {
                        this.dsCopy.setValue(i, new Integer(r));
                    } catch (IndexOutOfBoundsException e) {
                    } catch (InvalidAttributeValue e) {
                    }
                }
            } else if (a.getValueType().equals(Double.class)) {
                double min = Attributes.getExtremumValue(this.dsCopy, i, false);
                double max = Attributes.getExtremumValue(this.dsCopy, i, true);
                double dev = relDev * (max - min);

                for (int j = 0; j < this.dsCopy.size(); j++) {
                    if (!changeValue())
                        continue;
                    this.dsCopy.setCursorAt(j);
                    Double value = (Double) this.dsCopy.getValue(i);
                    if (value == null)
                        continue;
                    //                    double r = rnd.nextDouble() * (max - min) + min;
                    double r = getNormalVariable(value.doubleValue(), dev);

                    try {
                        this.dsCopy.setValue(i, new Double(r));
                    } catch (IndexOutOfBoundsException e) {
                    } catch (InvalidAttributeValue e) {
                    }
                }
            }
        }

        super.stepProcess();
        return false;
    }

}