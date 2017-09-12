//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Nominalization.java (com.nestor.cs.tasks.impl.preprocess)
// Utworzono 2005-09-08 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.demo;

import com.nestor.cs.data.def.AttributeSubset;
import com.nestor.cs.data.def.ItemsetSelector;
import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.data.def.ReadOnlyItemset;
import com.nestor.cs.data.def.Test;
import com.nestor.cs.data.def.TestSet;
import com.nestor.cs.data.impl.attributes.ContinuousAttribute;
import com.nestor.cs.data.impl.tests.ComparableTest;
import com.nestor.cs.tasks.impl.preprocess.AbstractValueConverter;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.util.param.Argument;

public class Normalization extends AbstractValueConverter {

    private class AutoUpdateTest extends ComparableTest {

        public AutoUpdateTest(Comparable constraint, int testType) {
            super(constraint, testType);
        }

        @Override
        public boolean classifies(Object value) {
            if (super.classifies(value)) {
                updateConstraint(value);
                return true;
            } else
                return false;
        }
    }

    public final Argument argAttributeset = new Argument();

    public final Argument argMaximum = new Argument(new Double(1));

    public final Argument argMinimum = new Argument(new Double(0));

    private Double newMax;

    private Double newMin;

    private Double[] originalMaximum = null;

    private Double[] originalMinimum = null;

    private TestSet testForMin;

    private TestSet testForMax;

    public Normalization() {
        super();
        this.argMinimum.setName("Wartość minimalna normalizacji");
        super.getConfigurationArguments().addArgument(this.argMinimum);

        this.argMaximum.setName("Wartość maksymalna normalizacji");
        super.getConfigurationArguments().addArgument(this.argMaximum);

        this.argAttributeset.setName("Atrubuty do znormalizowania");
        this.argAttributeset.setValueType(AttributeSubset.class);
        this.argAttributeset.setRequired(false);
        super.getConfigurationArguments().addArgument(this.argAttributeset);
    }

    public Object getValue(int attrIndex) {
        Double Vmin = this.originalMinimum[attrIndex];
        Double Vmax = this.originalMaximum[attrIndex];
        if ((Vmin == null) || (Vmax == null))
            return getArgConvertedDataset().getValue(attrIndex);
        if (Vmax.doubleValue() - Vmin.doubleValue() == 0)
            return new Double(0);
        Double v = (Double) getArgConvertedDataset().getValue(attrIndex);
        if (v == null)
            return null;
        return new Double((v.doubleValue() - Vmin.doubleValue())
                / (Vmax.doubleValue() - Vmin.doubleValue())
                * (this.newMax.doubleValue() - this.newMin.doubleValue())
                + this.newMin.doubleValue());
    }

    @Override
    public void initProcess() throws ProcessingException {
        super.initProcess();

        this.originalMinimum = new Double[getArgConvertedDataset()
                .getAttributeCount()];
        this.originalMaximum = new Double[getArgConvertedDataset()
                .getAttributeCount()];

        this.newMin = (Double) this.argMinimum.getValue();
        this.newMax = (Double) this.argMaximum.getValue();
    }

    @Override
    public synchronized boolean stepProcess() throws ProcessingException {
        ReadOnlyDataset ds = getArgConvertedDataset();
        if (ds.size() == 0)
            return false;
        AttributeSubset as = (AttributeSubset) this.argAttributeset.getValue();
        if (as == null)
            as = ds;
        this.testForMin = new TestSet(ds);
        this.testForMax = new TestSet(ds);
        for (int i = 0; i < ds.getAttributeCount(); i++) {
            if (!as.containsAttribute(i))
                continue;
            if (ds.getAttribute(i) instanceof ContinuousAttribute) {
                AutoUpdateTest minTest = new AutoUpdateTest(new Double(
                        Double.MAX_VALUE), Test.LESS);
                AutoUpdateTest maxTest = new AutoUpdateTest(new Double(
                        Double.MIN_VALUE), Test.GREATER);
                this.testForMin.setTest(i, minTest);
                this.testForMax.setTest(i, maxTest);
            }
        }
        ItemsetSelector selector = new ItemsetSelector() {
            public boolean selectItemset(ReadOnlyItemset it) {
                Normalization.this.testForMin.classifies(it);
                Normalization.this.testForMax.classifies(it);
                return false;
            }
        };
        ds.iterator(selector).hasMoreItemsets();
        for (int i = 0; i < ds.getAttributeCount(); i++) {
            if (this.testForMax.getTest(i) != null) {
                this.originalMinimum[i] = (Double) this.testForMin.getTest(i)
                        .getConstraint();
                this.originalMaximum[i] = (Double) this.testForMax.getTest(i)
                        .getConstraint();
            }
        }
        return false;
    }

}