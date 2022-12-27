package simpledb.execution.aggregator;

import simpledb.storage.Field;
import simpledb.storage.IntField;

public class MinAggregator implements CustomAggregator {
    private int min;

    public MinAggregator() {
        this.min = Integer.MAX_VALUE;
    }

    @Override
    public void add(Field value) {
        this.min = Math.min(this.min, ((IntField)value).getValue());
    }

    @Override
    public Field compute() {
        return new IntField(this.min);
    }
}
