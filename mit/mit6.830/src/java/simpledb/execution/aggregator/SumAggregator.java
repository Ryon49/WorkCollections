package simpledb.execution.aggregator;

import simpledb.storage.Field;
import simpledb.storage.IntField;

/**
 * Sum Aggregator will only be used for IntField
 */
public class SumAggregator implements CustomAggregator {
    private int sum;

    public SumAggregator() {
        this.sum = 0;
    }

    @Override
    public void add(Field value) {
        this.sum += ((IntField)value).getValue();
    }

    @Override
    public Field compute() {
        return new IntField(this.sum);
    }
}
