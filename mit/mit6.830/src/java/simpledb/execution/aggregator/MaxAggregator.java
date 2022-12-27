package simpledb.execution.aggregator;

import simpledb.storage.Field;
import simpledb.storage.IntField;

public class MaxAggregator implements CustomAggregator {
    private int max;

    public MaxAggregator() {
        this.max = Integer.MIN_VALUE;
    }

    @Override
    public void add(Field value) {
        this.max = Math.max(this.max, ((IntField)value).getValue());
    }

    @Override
    public Field compute() {
        return new IntField(this.max);
    }
}
